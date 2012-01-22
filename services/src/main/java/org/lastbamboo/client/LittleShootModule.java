package org.lastbamboo.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.lastbamboo.client.search.SearchResultManager;
import org.lastbamboo.client.services.FileMapper;
import org.lastbamboo.client.services.Heartbeat;
import org.lastbamboo.client.services.HeartbeatImpl;
import org.lastbamboo.client.services.LittleShootResourceRepository;
import org.lastbamboo.client.services.PreferencesFileMapper;
import org.lastbamboo.client.services.PublishedFilesTracker;
import org.lastbamboo.client.services.PublishedFilesTrackerImpl;
import org.lastbamboo.client.services.RemoteResourceRepository;
import org.lastbamboo.client.services.download.DownloadFactory;
import org.lastbamboo.client.services.download.DownloadFactoryImpl;
import org.lastbamboo.client.services.download.DownloadRequestHandler;
import org.lastbamboo.client.services.download.DownloadRequestHandlerImpl;
import org.lastbamboo.client.services.download.DownloadStreamHandler;
import org.lastbamboo.client.services.download.DownloadStreamHandlerImpl;
import org.lastbamboo.client.services.download.DownloadTracker;
import org.lastbamboo.client.services.download.DownloadTrackerImpl;
import org.lastbamboo.client.services.download.LibTorrentManager;
import org.lastbamboo.client.services.download.LibTorrentManagerImpl;
import org.lastbamboo.client.services.download.StartDownloadService;
import org.lastbamboo.client.services.download.StartDownloadServiceImpl;
import org.lastbamboo.common.p2p.DefaultRawUdpServerDepot;
import org.lastbamboo.common.p2p.P2PClient;
import org.lastbamboo.common.p2p.RawUdpServerDepot;
import org.lastbamboo.common.portmapping.NatPmpService;
import org.lastbamboo.common.portmapping.UpnpService;
import org.lastbamboo.common.searchers.limewire.LimeWire;
import org.lastbamboo.common.searchers.limewire.LimeWireImpl;
import org.lastbamboo.jni.JLibTorrent;
import org.littleshoot.p2p.P2P;
import org.littleshoot.util.NetworkUtils;
import org.littleshoot.util.ShootConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for lazy-initialization and dependency injection of LittleShoot
 * components without the overhead and extra size of Spring or Guice.
 */
public class LittleShootModule {

    private static final Logger LOG = 
        LoggerFactory.getLogger(LittleShootModule.class);
    
    private volatile static FileMapper fileMapper;
    
    private volatile static RemoteResourceRepository remoteResourceRepository;
    
    private volatile static PublishedFilesTracker publishedFilesTracker;
    
    private volatile static DownloadTracker downloadTracker;

    private volatile static LimeWire limeWire;
    
    /**
     * We start this here because it loads old torrents and resumes them.
     */
    private static final DownloadFactory downloadFactory =
        new DownloadFactoryImpl();
    
    private volatile static StartDownloadService startDownloadService;

    private volatile static DownloadStreamHandler downloadStreamHandler;
    
    private volatile static DownloadRequestHandler downloadRequestHandler;
    
    private volatile static SearchResultManager searchResultManager;
    
    private volatile static Heartbeat heartbeat;

    private volatile static DefaultRawUdpServerDepot rawUdpServerDepot;
    
    private volatile static P2PClient p2pClient;
    
    /**
     * We pre-initialize this because we want to make sure to start downloading
     * pending torrents right away.
     */
    private static final LibTorrentManager torrentManager = 
        new LibTorrentManagerImpl();
    
    public static P2PClient getP2PSipClient() {
        if (p2pClient == null) {
            try {
                final JLibTorrent lt =
                    LittleShootModule.getTorrentManager().getLibTorrent();
                final NatPmpService natPmp = lt;
                final UpnpService upnp = lt;
                p2pClient = 
                    P2P.newSipP2PClient("sip", natPmp, upnp,
                        LittleShootModule.getFilesAddress(), 
                        LittleShootModule.getRawUdpServerDepot());
            } catch (final IOException e) {
                LOG.error("Could not create LittleShoot P2P", e);
            }
        }
        return p2pClient;
    }
    
    public static FileMapper getFileMapper() {
        if (fileMapper == null) {
            fileMapper = new PreferencesFileMapper();
        }
        return fileMapper;
    }

    public static RemoteResourceRepository getRemoteResourceRepository() {
        if (remoteResourceRepository == null) {
            remoteResourceRepository = new LittleShootResourceRepository();
        }
        return remoteResourceRepository;
    }

    public static PublishedFilesTracker getPublishedFilesTracker() {
        if (publishedFilesTracker == null) {
            publishedFilesTracker = new PublishedFilesTrackerImpl();
        }
        return publishedFilesTracker;
    }

    public static DownloadTracker getDownloadTracker() {
        if (downloadTracker == null) {
            downloadTracker = new DownloadTrackerImpl();
        }
        return downloadTracker;
    }

    public static LimeWire getLimeWire() {
        if (limeWire == null) {
            limeWire = new LimeWireImpl();
            limeWire.start();
        }
        return limeWire;
    }

    public static LibTorrentManager getTorrentManager() {
        return torrentManager;
    }

    public static DownloadFactory getDownloadFactory() {
        return downloadFactory;
    }

    public static StartDownloadService getStartDownloadService() {
        if (startDownloadService == null) {
            startDownloadService = new StartDownloadServiceImpl();
        }
        return startDownloadService;
    }

    public static DownloadStreamHandler getDownloadStreamHandler() {
        if (downloadStreamHandler == null) {
            downloadStreamHandler = new DownloadStreamHandlerImpl();
        }
        return downloadStreamHandler;
    }

    public static DownloadRequestHandler getDownloadRequestHandler() {
        if (downloadRequestHandler == null) {
            downloadRequestHandler = new DownloadRequestHandlerImpl();
        }
        return downloadRequestHandler;
    }

    public static SearchResultManager getSearchResultManager() {
        if (searchResultManager == null) {
            searchResultManager = new SearchResultManager();
        }
        return searchResultManager;
    }

    public static Heartbeat getHeartbeat() {
        if (heartbeat == null) {
            heartbeat = new HeartbeatImpl();
        }
        return heartbeat;
    }

    public static RawUdpServerDepot getRawUdpServerDepot() {
        if (rawUdpServerDepot == null) {
            rawUdpServerDepot = new DefaultRawUdpServerDepot();
        }
        return rawUdpServerDepot;
    }

    public static InetSocketAddress getFilesAddress() 
        throws UnknownHostException {
        return new InetSocketAddress(NetworkUtils.getLocalHost(), 
            ShootConstants.FILES_PORT);
    }
}
