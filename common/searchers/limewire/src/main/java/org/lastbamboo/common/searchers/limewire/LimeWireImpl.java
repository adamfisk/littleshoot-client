package org.lastbamboo.common.searchers.limewire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.id.uuid.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.lastbamboo.common.http.client.CommonsHttpClient;
import org.lastbamboo.common.http.client.CommonsHttpClientImpl;
import org.lastbamboo.common.rest.SearchRequestBean;
import org.littleshoot.util.CommonUtils;
import org.littleshoot.util.DaemonThread;
import org.littleshoot.util.DeadlockSupport;
import org.littleshoot.util.ResettingMultiThreadedHttpConnectionManager;
import org.littleshoot.util.ResourceTypeTranslator;
import org.littleshoot.util.ResourceTypeTranslatorImpl;
import org.limewire.core.api.file.CategoryManager;
import org.limewire.core.api.search.SearchCategory;
import org.limewire.core.settings.ConnectionSettings;
import org.limewire.core.settings.SearchSettings;
import org.limewire.core.settings.iTunesSettings;
import org.limewire.inject.GuiceUtils;
import org.limewire.io.GUID;
import org.limewire.io.IpPort;
import org.limewire.net.FirewallService;
import org.limewire.nio.NIODispatcher;
import org.limewire.service.ErrorService;
import org.limewire.service.MessageService;
import org.limewire.util.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import com.limegroup.gnutella.ActivityCallback;
import com.limegroup.gnutella.ConnectionManager;
import com.limegroup.gnutella.DownloadServices;
import com.limegroup.gnutella.LifecycleManager;
import com.limegroup.gnutella.LimeCoreGlue;
import com.limegroup.gnutella.LimeCoreGlue.InstallFailedException;
import com.limegroup.gnutella.LimeWireCoreModule;
import com.limegroup.gnutella.RemoteFileDesc;
import com.limegroup.gnutella.SearchServices;
import com.limegroup.gnutella.downloader.RemoteFileDescFactory;
import com.limegroup.gnutella.util.LimeWireUtils;
import com.sun.jna.Native;


/**
 * Implementation for accessing the LimeWire backend.
 */
public class LimeWireImpl implements LimeWire, LimeWireQueryResultListener
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final Map<UUID, LimeWireSearchListener> m_uuidsToListeners =
        new ConcurrentHashMap<UUID, LimeWireSearchListener>();
    
    private final Map<URI, LimeWireJsonResult> m_resultMap = 
        new ConcurrentHashMap<URI, LimeWireJsonResult>();
    
    private final ResourceTypeTranslator m_translator = 
        new ResourceTypeTranslatorImpl ();
    
    // This is not really used in practice.
    private static final String URI_PREFIX =
        "http://www.littleshoot.org/images/icons/";
    
    private final Map<String,String> m_typesToUris = 
        new HashMap<String,String> ();
    private volatile boolean m_enabled;

    // Stuff for initialization.
    @Inject private volatile Provider<LifecycleManager> lifecycleManager;
    @Inject private Provider<LimeCoreGlue> limeCoreGlue;
    @Inject private Provider<NIODispatcher> nioDispatcher; 
    @Inject private Provider<FirewallService> firewallServices;
    @Inject private Provider<LittleShootCallback> littleShootCallback;
    
    // Stuff for searching/result-handling/downloading/connecting
    @Inject private Provider<SearchServices> searchServices;
    @Inject private Provider<CategoryManager> categoryManager;
    @Inject private Provider<ConnectionManager> connectionManager;
    @Inject private Provider<DownloadServices> downloadServices;
    @Inject private Provider<RemoteFileDescFactory> remoteFileDescFactory; 

    /**
     * Creates a new LimeWire interface to the backend.
     */
    public LimeWireImpl() {
        final Preferences prefs = Preferences.userRoot();
        this.m_enabled = prefs.getBoolean(CommonUtils.LIMEWIRE_ENABLED_KEY, true);
        // startLimeWire();
        final Runnable shutdownRunner = new Runnable() {
            public void run() {
                // This writes the gnutella.net file, amongst many other tasks.
                // Note on OSX the gnutella.net file is in:
                // ~/.littleshoot/gnutella.net

                if (lifecycleManager != null) {
                    final LifecycleManager manager = lifecycleManager.get();
                    if (manager != null) {
                        manager.shutdown();
                    }
                }
            }
        };

        // This is not a daemon, as it's not in LimeWire. See Finalizer.
        final Thread limeWireShutdownThread = 
            new Thread(shutdownRunner, "LimeWire-Shutdown-Thread");
        Runtime.getRuntime().addShutdownHook(limeWireShutdownThread);
    }

    public void start() {
        preinit();
        setupCallbacksAndListeners();
        createLimeWire();
        glueCore();
        validateEarlyCore();        
        DeadlockSupport.startDeadlockMonitoring();
        installProperties();
        startEarlyCore();
        
        // LS specific hook
        Preferences prefs = Preferences.userRoot();
        boolean connect = prefs.getBoolean(CommonUtils.LIMEWIRE_ENABLED_KEY, true);        
        ConnectionSettings.CONNECT_ON_STARTUP.setValue(connect);        
        iTunesSettings.ITUNES_SUPPORT_ENABLED.setValue(false);
        littleShootCallback.get().addSearchListener(this);
        m_log.debug("Added listener to: " + littleShootCallback.get());
        
        final Runnable runner = new Runnable() {
            public void run() {
                startCore();
            }
        };
        final Thread thread = new DaemonThread(runner, "LimeWire-Start-Thread");
        thread.start();
        
        m_typesToUris.put ("document", "document.png");
        m_typesToUris.put ("audio", "audio.png");
        m_typesToUris.put ("video", "video.png");
        m_typesToUris.put ("image", "image.png");
        m_typesToUris.put ("application/mac", "application.png");
        m_typesToUris.put ("application/linux", "application.png");
        m_typesToUris.put ("application/win", "application.png");
    }

    /** Initializes the very early things. */
    /*
     * DO NOT CHANGE THIS WITHOUT KNOWING WHAT YOU'RE DOING.
     * PREINSTALL MUST BE DONE BEFORE ANYTHING ELSE IS REFERENCED.
     * (Because it sets the preference directory in CommonUtils.)
     */
    private void preinit() {
        // Make sure the settings directory is set.
        final File configDir = new File(SystemUtils.USER_HOME, ".littleshoot");
        final File gnutellaDotNet = new File(configDir, "gnutella.net");
        if (!gnutellaDotNet.isFile()) {
            downloadGnutellaDotNet(gnutellaDotNet);
        }
        else {
            final long size = gnutellaDotNet.length();
            final long lm = gnutellaDotNet.lastModified();
            final long elapsed = System.currentTimeMillis() - lm;
            final boolean download = elapsed > 1000 * 60 * 60 * 24 * 7;
            if (size == 0L || download) {
                downloadGnutellaDotNet(gnutellaDotNet);
            }
        }

        
        try {
            LimeCoreGlue.preinstall(configDir);
        } catch (InstallFailedException ife) {
            m_log.warn("Install failed!!");
        }
    }
    
    private void downloadGnutellaDotNet(final File gnutellaDotNet) {
        final String url = "http://www.littleshoot.org/gnutellaDotNet";
        final MultiThreadedHttpConnectionManager cm =
            new ResettingMultiThreadedHttpConnectionManager();
        
        final CommonsHttpClient client = new CommonsHttpClientImpl(cm);
        
        final GetMethod get = new GetMethod(url);
        InputStream is = null;
        final OutputStream os;
        try {
            os = new FileOutputStream(gnutellaDotNet);
        } catch (final FileNotFoundException e) {
            m_log.error("Could not open stream to gnutella.net?", e);
            return;
        }
        try {
            m_log.info("About to download gnutella.net");
            final int status = client.executeMethod(get);
            if (status >= 200 && status < 300) {
                is = get.getResponseBodyAsStream();
                IOUtils.copy(is, os);
            }
            else {
                m_log.error("Non 200 response from gnutella.net!! "+status);
            }
        } catch (final IOException e) {
            m_log.error("Could not download gnutella.net!");
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
            get.releaseConnection();
            
            final long size = gnutellaDotNet.length();
            m_log.info("Downloaded file. Size is: {}", size);
            if (size == 0L) {
                m_log.error("Gnutella.net still empty!!");
            }
        }
    }

    private void setupCallbacksAndListeners() {
        LittleShootErrorHandler handler = new LittleShootErrorHandler();
        // Set the error handler so we can receive core errors.
        ErrorService.setErrorCallback(handler);
        
        // set error handler for uncaught exceptions originating from non-LW
        Thread.setDefaultUncaughtExceptionHandler(handler);
        Native.setCallbackExceptionHandler(handler);
        
        // Set the messaging handler so we can receive core messages
        MessageService.setCallback(new LittleShootMessageHandler());
        
        if (OSUtils.isMacOSX()) {
            // Raise the number of allowed concurrent open files to 1024.
            org.limewire.util.SystemUtils.setOpenFileLimit(1024);     
        
            // TODO: Install this if you want to listen to events on OS X.
//            MacEventHandler.instance();
        }    
    }
    
    /** Wires together LimeWire. */
    private Injector createLimeWire() {
        Module core = Modules
            .override(new LimeWireCoreModule())
            .with(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(FirewallService.class).to(LittleShootFirewallServiceImpl.class);
                }
            });
                
        Injector injector = Guice.createInjector(Stage.DEVELOPMENT,
                core,
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ActivityCallback.class).to(LittleShootCallback.class);
                        requestInjection(LimeWireImpl.this);
                    }
                });
        GuiceUtils.loadEagerSingletons(injector);
        return injector;
    }
    
    /** Installs any system properties. */
    private void installProperties() {        
        System.setProperty("http.agent", LimeWireUtils.getHttpServer());
    }    
    
    /** Starts any early core-related functionality. */
    private void startEarlyCore() {        
        // Add this running program to the Windows Firewall Exceptions list
        boolean inFirewallException = firewallServices.get().addToFirewall();
        
        if(!inFirewallException) {
            lifecycleManager.get().loadBackgroundTasks();
        }
    }

    /** Starts the core. */
    private void startCore() {        
        // Start the backend threads.  Note that the GUI is not yet visible,
        // but it needs to be constructed at this point  
        lifecycleManager.get().start();
    }    
    
    /** Wires together remaining non-Guiced pieces. */
    private void glueCore() {
        limeCoreGlue.get().install();
    }
    
    /** Tasks that can be done after core is created, before it's started. */
    private void validateEarlyCore() {
        // See if our NIODispatcher clunked out.
        if (!nioDispatcher.get().isRunning()) {
            m_log.warn("Internet blocked!!");
        }
    }
    
    public void search(final UUID uuid,
            final String keywords,
            final LimeWireSearchListener listener,
            final SearchRequestBean searchRequestBean) {
        if (!isEnabled()) {
            m_log.debug("Not searching since not enabled");
            return;
        }
        this.m_uuidsToListeners.put(uuid, listener);
        SearchCategory searchCategory;
        if (searchRequestBean.isAllTypes()) {
            m_log.debug("Searching for all media types.");
            searchCategory = SearchCategory.ALL;
        } else if (searchRequestBean.isAnySingleType()) {
            if (searchRequestBean.isApplications()) {
                searchCategory = SearchCategory.PROGRAM;
            } else if (searchRequestBean.isAudio()) {;
                searchCategory = SearchCategory.AUDIO;
            } else if (searchRequestBean.isDocuments()) {
                searchCategory = SearchCategory.DOCUMENT;
            } else if (searchRequestBean.isImages()) {
                searchCategory = SearchCategory.IMAGE;
            } else if (searchRequestBean.isVideo()) {
                searchCategory = SearchCategory.IMAGE;
            } else {
                m_log.warn("Could not find specific type for: "
                        + searchRequestBean);
                searchCategory = SearchCategory.OTHER;
            }
        } else {
            searchCategory = SearchCategory.ALL;
        }
        m_log.debug("Search category: {}", searchCategory);
        final byte[] guid = uuid.getRawBytes();

        final String searchTerms;
        if (keywords.length() > SearchSettings.MAX_QUERY_LENGTH.getValue()) {
            searchTerms = shortenKeywords(keywords,
                    SearchSettings.MAX_QUERY_LENGTH.getValue());
            m_log.debug("Shortened keywords to: {}", searchTerms);
        } else {
            searchTerms = keywords;
        }
        searchServices.get().query(guid, searchTerms, searchCategory);

    }

    private String shortenKeywords(final String keywords, final int maxLength) {
        final StringBuilder sb = new StringBuilder();
        final Scanner sc = new Scanner(keywords);
        while (sc.hasNext()) {
            final String next = sc.next();
            if (sb.length() + next.length() + 1 > maxLength) {
                // Check if the current string makes up all of the query
                // we've scanned so far -- could be one long word or gibberish.
                if (sb.length() == 0) {
                    return next.substring(0, maxLength - 1);
                } else {
                    // Just return the existing string.
                    return sb.toString();
                }
            } else {
                sb.append(" ");
                sb.append(next);
            }
        }
        return sb.toString();
    }

    public void onSearchResult(final RemoteFileDesc rfd,
        final Set<? extends IpPort> altLocs, final GUID queryGUID) {
        m_log.debug("Got result with name: {}", rfd.getFileName());
        if (isSpam(rfd)) {
            m_log.debug("Got spammy result: {}", rfd);
            return;
        }
        final UUID id = new UUID(queryGUID.bytes());

        final URI uri;
        try {
            uri = new URI(rfd.getSHA1Urn().httpStringValue());
        } catch (final URISyntaxException e) {
            m_log.warn("Could not create URI", e);
            return;
        }

        final LimeWireJsonResult result;

        final LimeWireSearchListener listener = this.m_uuidsToListeners.get(id);
        synchronized (this.m_resultMap) {
            if (this.m_resultMap.containsKey(uri)) {
                m_log.debug("Found matching SHA-1");
                result = this.m_resultMap.get(uri);
                result.addData(rfd, altLocs);
                if (listener != null) {
                    m_log.debug("Notifying listener of change.");
                    listener.onResultsChange();
                } else {
                    m_log.warn("Could not find listener for result!!");
                }
            } else {
                if (!CommonUtils.isPro()
                        && this.m_resultMap.size() >= CommonUtils.FREE_RESULT_LIMIT) {
                    m_log.debug("Not adding result for free version");
                    return;
                }
                m_log.debug("Adding result for new SHA-1");
                final String title = rfd.getFileName();
                final String mediaType = m_translator.getType(title);
                try {
                    result = new LimeWireJsonResult(uri, rfd, queryGUID, altLocs,
                            createThumbnailUrl(mediaType), mediaType);
                    this.m_resultMap.put(uri, result);
                } catch (final URISyntaxException e) {
                    m_log.warn("URI syntax error", e);
                    return;
                }

                if (listener != null) {
                    m_log.debug("Sending to listener: {}", listener);
                    listener.onSearchResult(result);
                } else {
                    m_log.warn("Could not find listener for result!!");
                }
            }
        }
    }
    
    private URI createThumbnailUrl(final String mediaType)
            throws URISyntaxException {

        if (m_typesToUris.containsKey(mediaType)) {
            final String suffix = m_typesToUris.get(mediaType);
            return new URI(URI_PREFIX + suffix);
        } else {
            return new URI(URI_PREFIX + "document.png");
        }
    }

    private boolean isSpam(final RemoteFileDesc rfd) {
        final long ct = rfd.getCreationTime();
        if (ct == -1) {
            return true;
        }
        return false;
    }

    public UUID newUuid() {
        if (!isEnabled()) {
            return UUID.randomUUID();
        }
        final byte[] guid = searchServices.get().newQueryGUID();
        return new UUID(guid);
    }

    public LimeWireJsonResult getResults(final URI sha1) {
        return this.m_resultMap.get(sha1);
    }

    public boolean isEnabled() {
        return this.m_enabled;
    }

    public void setEnabled(final boolean enabled) {
        if (this.connectionManager == null) {
            m_log.warn("connectionManager still null!!");
            this.m_enabled = enabled;
            return;
        }
        if (this.m_enabled == enabled) {
            m_log.warn("Not changing enabled state still: " + enabled);
            if (!enabled) {
                connectionManager.get().disconnect(false);
            } else {
                connectionManager.get().connect();
            }
        }
        if (!this.m_enabled && enabled) {
            connectionManager.get().connect();
        } else if (this.m_enabled && !enabled) {
            connectionManager.get().disconnect(false);
        }
        this.m_enabled = enabled;
    }
    
    public ConnectionManager getConnectionManager() {
        return connectionManager != null ? connectionManager.get() : null;
    }
    
    public DownloadServices getDownloadServices() {
        return downloadServices != null ? downloadServices.get() : null;
    }
    
    public RemoteFileDescFactory getRemoteFileDescFactory() {
        return remoteFileDescFactory != null ? remoteFileDescFactory.get() : null;
    }
    
    
}
