package org.lastbamboo.client.services.http;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.handlers.AppCheckController;
import org.lastbamboo.client.handlers.ClearFailedDownloadsController;
import org.lastbamboo.client.handlers.ClearInactiveDownloadsController;
import org.lastbamboo.client.handlers.CrossDomainController;
import org.lastbamboo.client.handlers.DownloadController;
import org.lastbamboo.client.handlers.DownloadFolderController;
import org.lastbamboo.client.handlers.DownloadInfoController;
import org.lastbamboo.client.handlers.DownloadStreamController;
import org.lastbamboo.client.handlers.DownloadTorrentController;
import org.lastbamboo.client.handlers.DownloadsController;
import org.lastbamboo.client.handlers.HiiPublishController;
import org.lastbamboo.client.handlers.LoginController;
import org.lastbamboo.client.handlers.PauseDownloadController;
import org.lastbamboo.client.handlers.PublishController;
import org.lastbamboo.client.handlers.PublishSmartFileController;
import org.lastbamboo.client.handlers.PublishedFilesController;
import org.lastbamboo.client.handlers.ReadCallDataController;
import org.lastbamboo.client.handlers.RemoveFileController;
import org.lastbamboo.client.handlers.ResumeDownloadController;
import org.lastbamboo.client.handlers.SearchController;
import org.lastbamboo.client.handlers.SearchResultsController;
import org.lastbamboo.client.handlers.SignedRelayController;
import org.lastbamboo.client.handlers.StartTorrentDownloadController;
import org.lastbamboo.client.handlers.StopDownloadController;
import org.lastbamboo.client.handlers.StartCallController;
import org.lastbamboo.client.handlers.AllCallsController;
import org.lastbamboo.client.handlers.WriteCallDataController;
import org.lastbamboo.client.services.Configurator;
import org.lastbamboo.client.services.FileRefresher;
import org.lastbamboo.client.services.FileRefresherImpl;
import org.lastbamboo.client.services.LimeWireMonitor;
import org.lastbamboo.client.services.SystemTray;
import org.lastbamboo.client.services.SystemTrayImpl;
import org.lastbamboo.client.services.TorrentDirectoryTracker;
import org.lastbamboo.client.services.download.LibTorrentStreamListener;
import org.lastbamboo.common.npapi.NpapiIpcHandler;
import org.lastbamboo.common.npapi.TorrentStreamFileConsumer;
import org.littleshoot.util.CommonUtils;
import org.littleshoot.util.RuntimeIoException;
import org.littleshoot.util.ShootConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of the HTTP server.
 */
public class HttpServerImpl implements HttpServer {

    /**
     * Constant for the Commons logging instance.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final Server m_server = new Server();

    /**
     * Creates a new HTTP server.
     */
    public HttpServerImpl() {
        final String apiName = "API";
        final String fsName = "FileServer";
        final ContextHandlerCollection contexts = 
            new ContextHandlerCollection();
        
        final ServletContextHandler api = newContext("/", apiName);
        contexts.addHandler(api);

        final MimeTypes mt = api.getMimeTypes();
        mt.addMimeMapping("mp4", "video/mp4");
        mt.addMimeMapping("rar", "application/x-rar");
        
        // Not totally clear what this should be.  Ubuntu uses text/plain.  
        // Fedora uses application/x-iso9660-image
        mt.addMimeMapping("iso", "application/x-iso9660");
        mt.addMimeMapping("mkv", "video/x-matroska");
        mt.addMimeMapping("mka", "audio/x-matroska");
        mt.addMimeMapping("m4v", "video/x-m4v");
        mt.addMimeMapping("dmg", "application/x-apple-diskimage");
        

        final ServletContextHandler files = newContext("/uri-res", fsName);
        files.setMimeTypes(mt);
        
        contexts.addHandler(files);
        
        m_server.setHandler(contexts);
        m_server.setStopAtShutdown(true);
        
        final SelectChannelConnector apiConnector = 
            new SelectChannelConnector();
        apiConnector.setPort(ShootConstants.API_PORT);
        
        // TODO: Make sure this works on Linux!!
        apiConnector.setHost("127.0.0.1");
        apiConnector.setName(apiName);
        
        final SelectChannelConnector filesConnector = 
            new SelectChannelConnector();
        final InetSocketAddress filesAddress;
        try {
            filesAddress = LittleShootModule.getFilesAddress();
        } catch (final UnknownHostException e) {
            m_log.error("Could not get host?", e);
            throw new RuntimeIoException("Could not get host", e);
        }
        filesConnector.setPort(filesAddress.getPort());
        
        filesConnector.setHost(filesAddress.getAddress().getHostAddress());
        filesConnector.setName(fsName);
        
        this.m_server.setConnectors(
            new Connector[]{apiConnector, filesConnector});
 
        final CrossDomainController cdc = new CrossDomainController();
        api.addServlet(new ServletHolder(new AppCheckController()),"/api/client/appCheck");
        api.addServlet(new ServletHolder(cdc),"/crossdomain.xml");
        api.addServlet(new ServletHolder(cdc),"/api/client/crossdomain.xml");
        
        api.addServlet(new ServletHolder(new DownloadController()),"/api/client/download/*");
        api.addServlet(new ServletHolder(new DownloadStreamController()),"/api/client/streamDownload/*");
        api.addServlet(new ServletHolder(new DownloadTorrentController()),"/api/client/downloadTorrent/*");
        api.addServlet(new ServletHolder(new StartTorrentDownloadController()),"/api/client/startTorrentDownload/*");
        api.addServlet(new ServletHolder(new DownloadsController()),"/api/client/downloads");
        api.addServlet(new ServletHolder(new DownloadInfoController()),"/api/client/downloadInfo");
        
        api.addServlet(new ServletHolder(new SearchController()),"/api/client/search");
        api.addServlet(new ServletHolder(new SearchResultsController()),"/api/client/searchResults");
        
        api.addServlet(new ServletHolder(new SignedRelayController()),"/api/client/secure/signedRelay");
        
        api.addServlet(new ServletHolder(new StopDownloadController()),"/api/client/stopDownload/*");
        api.addServlet(new ServletHolder(new PauseDownloadController()),"/api/client/pauseDownload/*");
        api.addServlet(new ServletHolder(new ResumeDownloadController()),"/api/client/resumeDownload/*");
        api.addServlet(new ServletHolder(new RemoveFileController()),"/api/client/secure/removeFile");
        api.addServlet(new ServletHolder(new DownloadFolderController()),"/api/client/openDownloadFolder/*");

        api.addServlet(new ServletHolder(new ClearInactiveDownloadsController()), "/api/client/clearInactiveDownloads");
        api.addServlet(new ServletHolder(new ClearFailedDownloadsController()),"/api/client/clearFailedDownloads");

        api.addServlet(new ServletHolder(new PublishSmartFileController()),"/api/client/publishSmartFile");
        if (CommonUtils.isTrue(ShootConstants.HII_KEY)) {
            api.addServlet(new ServletHolder(new HiiPublishController()),"/api/client/hiipublish");
        }
        api.addServlet(new ServletHolder(new PublishController()),"/api/client/secure/publishFile");
        api.addServlet(new ServletHolder(new PublishedFilesController()),"/api/client/publishedFiles");
        
        api.addServlet(new ServletHolder(new StartCallController()),"/api/client/startCall");
        api.addServlet(new ServletHolder(new WriteCallDataController()),"/api/client/writeCallData");
        api.addServlet(new ServletHolder(new ReadCallDataController()),"/api/client/readCallData");
        api.addServlet(new ServletHolder(new AllCallsController()),"/api/client/allCalls");
        api.addServlet(new ServletHolder(new LoginController()),"/api/client/login");
        
        final DefaultServlet ds = new FileServingServlet();
        //api.addServlet(new ServletHolder(ds),"/favicon.ico");
        files.addServlet(new ServletHolder(ds),"/*");
        
        startServer();
        
        //context.addServlet(new ServletHolder(),"");
        
        //api.addServlet(new ServletHolder(new DirectoryRedirectController()),"/*");
        
        launchServices();
    }
    
    private ServletContextHandler newContext(final String path,
        final String name) {
        final ServletContextHandler context = 
            new ServletContextHandler(ServletContextHandler.SESSIONS);
        
        final Map<String, String> params = context.getInitParams();
        params.put("org.eclipse.jetty.servlet.Default.gzip", "false");
        params.put("org.eclipse.jetty.servlet.Default.welcomeServlets", "false");
        params.put("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        params.put("org.eclipse.jetty.servlet.Default.aliases", "false");
        //params.put("javax.servlet.include.request_uri", "true");
        context.setContextPath(path);
        context.setConnectorNames(new String[] {name});
        return context;
    }

    /**
     * Specialized servlet for file serving that simply uses the Jetty default
     * servlet with overriding the method for accessing resources based on the
     * request path.
     */
    private class FileServingServlet extends DefaultServlet {

        private static final long serialVersionUID = -6428591292336746514L;

        @Override
        public Resource getResource(final String pathInContext) {
            m_log.trace("Received request uri: {}", pathInContext);
            final String urn = StringUtils.substringAfter(pathInContext,"N2R-");

            if (StringUtils.isBlank(urn)) {
                m_log.error("No request URN in: {}", pathInContext);
                // No URN -- client error.
                throw new HttpRequestClientErrorException(
                    HttpServletResponse.SC_BAD_REQUEST, "", pathInContext);
            }
            final URI uri;
            try {
                uri = new URI(urn);
            } catch (final URISyntaxException e) {
                // Bad client request.
                m_log.warn("Invalid URI", e);
                throw new HttpRequestClientErrorException(
                    HttpServletResponse.SC_BAD_REQUEST, "", pathInContext);
            }
            final File file = LittleShootModule.getFileMapper().getFile(uri);
            
            try {
                return new FileResource(file.toURI().toURL());
            } catch (final MalformedURLException e) {
                m_log.error("Could not create file resource", e);
            } catch (final IOException e) {
                m_log.error("Could not create file resource", e);
            } catch (final URISyntaxException e) {
                m_log.error("Could not create file resource", e);
            }
            return null;
        }
    }

    private void launchServices() {
        m_log.debug("Launching services...");

        final Runnable runner = new Runnable() {
            
            public void run() {

                final LibTorrentStreamListener libTorrentStreamListener =
                    new LibTorrentStreamListener();
                final TorrentStreamFileConsumer torrentStreamFileConsumer =
                    new TorrentStreamFileConsumer(libTorrentStreamListener);
                final NpapiIpcHandler npapIpcHandler =
                    new NpapiIpcHandler(torrentStreamFileConsumer);
                npapIpcHandler.start();
                
                // This is automatically started.
                final TorrentDirectoryTracker torrentDirectoryTracker =
                    new TorrentDirectoryTracker();
                
                final LimeWireMonitor limeWire = 
                    new LimeWireMonitor(LittleShootModule.getLimeWire());
                System.out.println("Starting LimeWire!!");
                limeWire.start();
                final SystemTray tray = new SystemTrayImpl();
                
                // This starts automatically.
                final Configurator conf = new Configurator();
                
                try {
                    Thread.sleep(3000);
                } catch (final InterruptedException e) {
                    m_log.error("Interrupted?");
                }
                
                // This is automatically started.
                final FileRefresher localResourceRefresher =
                    new FileRefresherImpl(LittleShootModule.getFileMapper(), 
                        LittleShootModule.getRemoteResourceRepository(), 
                        LittleShootModule.getDownloadTracker(), true, 
                        LittleShootModule.getHeartbeat());
            }
        };
        
        final Thread thread = new Thread(runner, "Launch-Services-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    private void startServer() {
        m_log.debug("Starting server on port " + ShootConstants.API_PORT);
        try {
            m_server.start();
        } catch (final Throwable t) {
            m_log.error("Error starting server...exiting!!!", t);
            System.exit(0);
        }
    }
    
    public void joinServer() {
        m_log.debug("Starting server on port " + ShootConstants.API_PORT);
        try {
            m_server.join();
        } catch (final Throwable t) {
            m_log.error("Error joining serve...exiting!!!", t);
            System.exit(0);
        }
    }

    public void stopServer() {
        m_log.debug("Stopping the server...");
        try {
            this.m_server.stop();
        } catch (final Exception e) {
            m_log.warn("Exception while stopping", e);
        }
    }
}
