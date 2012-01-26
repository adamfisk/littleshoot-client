package org.lastbamboo.jni;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.lastbamboo.common.portmapping.NatPmpService;
import org.lastbamboo.common.portmapping.PortMapListener;
import org.lastbamboo.common.portmapping.PortMappingProtocol;
import org.lastbamboo.common.portmapping.UpnpService;
import org.littleshoot.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Java wrapper class for calls to native lib torrent.
 */
public class JLibTorrent implements NatPmpService, UpnpService {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private long m_totalUploadBytes;
    private long m_totalDownloadBytes;
    private long m_totalPayloadUploadBytes;
    private long m_totalPayloadDownloadBytes;
    private int m_uploadRate;
    private int m_downloadRate;
    private int m_payloadUploadRate;
    private int m_payloadDownloadRate;
    private int m_numPeers;
    private final boolean m_isPro;
    private final File m_dataDir = CommonUtils.getLittleShootDir();
    private boolean m_libLoaded;
    
    private final Map<Integer, PortMapListener> m_upnpMappingIdsToListeners = 
        Collections.synchronizedMap(new LinkedHashMap<Integer, PortMapListener>() {
            private static final long serialVersionUID = 6999097893740294302L;

            @Override
            protected boolean removeEldestEntry(
                final Map.Entry<Integer, PortMapListener> eldest) {
                // This makes the map automatically purge the least used
                // entry.
                final boolean remove = size() > 200;
                return remove;
            }
        });
    
    private final Map<Integer, PortMapListener> m_natPmpMappingIdsToListeners = 
        Collections.synchronizedMap(new LinkedHashMap<Integer, PortMapListener>() {

            private static final long serialVersionUID = 8582401885547276580L;

            @Override
            protected boolean removeEldestEntry(
                final Map.Entry<Integer, PortMapListener> eldest) {
                // This makes the map automatically purge the least used
                // entry.
                final boolean remove = size() > 200;
                return remove;
            }
        });

    private volatile boolean m_stopped;

    public JLibTorrent(final Collection<File> libFiles, final boolean isPro) {
        this.m_isPro = isPro;
        this.m_libLoaded = false;
        try {
            for (final File lib : libFiles) {
                if (lib.isFile()) {
                    m_log.info("Loading: " + lib);
                    System.load(lib.getAbsolutePath());
                    m_libLoaded = true;
                    m_log.info("Loaded: " + lib);
                    break;
                }
            }
            if (!m_libLoaded) {
                System.loadLibrary("jnltorrent");
                m_libLoaded = true;
            }
            init();
        } catch (final Throwable t) {
            // We might be running LittleShoot in embedded mode without all
            // the bells and whistles. Just log this and ignore.
            m_log.warn("Could not load LibTorrent library!", t);
            m_libLoaded = false;
        }
        final Runnable runner = new Runnable() {

            public void run() {
                final Set<Integer> upnpIndeces = 
                    m_upnpMappingIdsToListeners.keySet();
                for (final int index : upnpIndeces) {
                    System.out.println("Removing UPnP mapping at index: "+index);
                    removeUpnpMapping(index);
                }
                final Set<Integer> natPmpIndeces = 
                    m_natPmpMappingIdsToListeners.keySet();
                for (final int index : natPmpIndeces) {
                    System.out.println("Removing NAT-PMP mapping at index: "+index);
                    removeNatPmpMapping(index);
                }
            }
        };
        final Thread shutdown = 
            new Thread(runner, "Remove-UPNP-NATPMP-Mappings");
        Runtime.getRuntime().addShutdownHook(shutdown);
    }

    public JLibTorrent(final boolean isPro) {
        this.m_isPro = isPro;
        System.loadLibrary("jnltorrent");
        init();
    }

    public JLibTorrent(final String libraryPath, final boolean isPro) {
        this.m_isPro = isPro;
        System.load(libraryPath);
        init();
    }

    private void init() {
        cacheMethodIds();
        start(this.m_isPro, normalizePath(this.m_dataDir));
        checkAlerts();
    }

    /**
     * Shuts down the libtorrent core.
     */
    public void stopLibTorrent() {
        if (!this.m_libLoaded || this.m_stopped) return;
        m_stopped = true;
        stop();
    }

    public void updateSessionStatus() {
        if (!this.m_libLoaded || this.m_stopped) return;
        update_session_status();
    }

    public void download(final File incompleteDir, final File torrentFile,
        final boolean sequential, final int torrentState)
        throws IOException {
        if (!this.m_libLoaded || this.m_stopped) return;
        add_torrent(incompleteDir.getCanonicalPath(),
            torrentFile.getCanonicalPath(), (int) torrentFile.length(),
            sequential, torrentState);
    }

    public void rename(final File torrentFile, final String newName) {
        if (!this.m_libLoaded || this.m_stopped) return;
        final String path = normalizePath(torrentFile);
        rename(path, newName);
    }

    public void moveToDownloadsDir(final File torrentFile,
        final File downloadsDir) {
        if (!this.m_libLoaded || this.m_stopped) return;
        final String path = normalizePath(torrentFile);
        final String downloadsDirPath = normalizePath(downloadsDir);
        move_to_downloads_dir(path, downloadsDirPath);
    }

    public long getMaxByteForTorrent(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return -1L;
        final String path = normalizePath(torrentFile);
        return get_max_byte_for_torrent(path);
    }

    public void pauseTorrent(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return;
        final String path = normalizePath(torrentFile);
        pause_torrent(path);
    }

    public void resumeTorrent(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return;
        final String path = normalizePath(torrentFile);
        resume_torrent(path);
    }

    /**
     * Performs a "hard" resume of a torrent. This is necessary when starting
     * torrents from previous sessions that were paused in the previous session.
     * The usual resume scenario doesn't appear to work in this case because
     * simply setting a torrent to auto_managed that was never started out of
     * the pause state in the first place appears to have no effect.
     * 
     * @param torrentFile The torrent file.
     */
    public void hardResumeTorrent(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return;
        m_log.info("Performing hard resume");
        final String path = normalizePath(torrentFile);
        hard_resume_torrent(path);
    }

    public long getSizeForTorrent(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return 0L;
        final String path = normalizePath(torrentFile);
        return get_size_for_torrent(path);
    }

    public void removeTorrent(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return;
        final String path = normalizePath(torrentFile);
        remove_torrent(path);
    }

    public void removeTorrentAndFiles(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return;
        final String path = normalizePath(torrentFile);
        remove_torrent_and_files(path);
    }

    public int getStateForTorrent(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return -1;
        final String path = normalizePath(torrentFile);
        return get_state_for_torrent(path);
    }

    public String getName(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return torrentFile.getName();
        final String path = normalizePath(torrentFile);
        return get_name_for_torrent(path);
    }

    public int getNumFiles(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return 0;
        final String path = normalizePath(torrentFile);
        return get_num_files_for_torrent(path);
    }

    public int getDownloadSpeed(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return 0;
        final String path = normalizePath(torrentFile);
        return get_speed_for_torrent(path);
    }

    public int getNumHosts(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return 0;
        final String path = normalizePath(torrentFile);
        return get_num_peers_for_torrent(path);
    }

    public long getBytesRead(final File torrentFile) {
        if (!this.m_libLoaded || this.m_stopped) return 0L;
        final String path = normalizePath(torrentFile);
        return get_bytes_read_for_torrent(path);
    }

    public void setMaxUploadSpeed(final int bytesPerSecond) {
        if (!this.m_libLoaded || this.m_stopped) return;
        set_max_upload_speed(bytesPerSecond);
    }
    
    public int addUpnpMapping(final PortMappingProtocol protocol, 
        final int internalPort, final int externalPort, 
        final PortMapListener portMapListener) {
        if (!this.m_libLoaded || this.m_stopped) return -1;
        final int mappingId;
        switch (protocol) {
            case TCP:
                mappingId = add_tcp_upnp_mapping(internalPort, externalPort);
                break;
            case UDP:
                mappingId = add_udp_upnp_mapping(internalPort, externalPort);
                break;
            default:
                m_log.error("Bad protocol");
                throw new IllegalArgumentException("Bad protocol");
        }
        m_log.info("Adding port mapping ID: {}", mappingId);
        if (mappingId == -1) {
            portMapListener.onPortMapError();
        } else {
            m_upnpMappingIdsToListeners.put(mappingId, portMapListener);
        }
        return mappingId;
    }

    public int addNatPmpMapping(final PortMappingProtocol protocol, 
        final int internalPort, final int externalPort, 
        final PortMapListener portMapListener) {
        if (!this.m_libLoaded || this.m_stopped) return -1;
        final int mappingId;
        switch (protocol) {
            case TCP:
                mappingId = add_tcp_natpmp_mapping(internalPort, externalPort);
                break;
            case UDP:
                mappingId = add_udp_natpmp_mapping(internalPort, externalPort);
                break;
            default:
                m_log.error("Bad protocol");
                throw new IllegalArgumentException("Bad protocol");
        }
        m_log.info("Adding port mapping ID: {}", mappingId);
        if (mappingId == -1) {
            portMapListener.onPortMapError();
        } else {
            m_upnpMappingIdsToListeners.put(mappingId, portMapListener);
        }
        return mappingId;
    }
    
    public void removeUpnpMapping(final int mappingIndex) {
        delete_upnp_mapping(mappingIndex);
    }

    public void removeNatPmpMapping(final int mappingIndex) {
        delete_natpmp_mapping(mappingIndex);
    }
    
    public void checkAlerts() {
        if (!this.m_libLoaded || this.m_stopped) return;
        // TODO: Use a global Timer instead?
        final Runnable runner = new Runnable() {

            public void run() {
                while (!m_stopped) {
                    // We sleep first so we don't immediately start checking
                    // before LibTorrent's initialized.
                    try {
                        Thread.sleep(2000);
                    } catch (final InterruptedException e) {
                        m_log.error("Interrupted?", e);
                    }
                    check_alerts();
                }
            }
        };
        final Thread t = new Thread(runner);
        t.setDaemon(true);
        t.start();
    }
    
    private final String normalizePath(final File torrentFile) {
        try {
            return torrentFile.getCanonicalPath();
        } catch (final IOException e) {
            return torrentFile.getAbsolutePath();
        }
    }
    
    private native void set_max_upload_speed(final int bytesPerSecond);
    private native int add_tcp_upnp_mapping(final int internalPort, final int externalPort);
    private native int add_udp_upnp_mapping(final int internalPort, final int externalPort);
    private native int add_tcp_natpmp_mapping(final int internalPort, final int externalPort);
    private native int add_udp_natpmp_mapping(final int internalPort, final int externalPort);
    
    private native void delete_natpmp_mapping(final int mappingIndex);
    private native void delete_upnp_mapping(final int mappingIndex);
    
    private native void check_alerts();
    
    private native void cacheMethodIds();
    
    private native void update_session_status();
    
    private native void move_to_downloads_dir(final String path, 
        final String downloadsDir);
    
    private native void rename(final String path, final String newName);
    
    private native long get_bytes_read_for_torrent(final String path);
    
    private native int get_num_peers_for_torrent(final String path);
    
    private native int get_speed_for_torrent(final String path);

    private native int get_num_files_for_torrent(final String path);
    
    private native int get_state_for_torrent(final String path);
    
    private native long get_size_for_torrent(final String path);

    private native String get_name_for_torrent(final String path);
    
    private native void remove_torrent(final String path);
    
    private native void remove_torrent_and_files(final String path);
    
    private native void pause_torrent(final String path);
    
    private native void resume_torrent(final String path);
    
    private native void hard_resume_torrent(final String path);
    
    private native long get_max_byte_for_torrent(final String path);
    
    /**
     * Initialize the libtorrent core.
     * 
     * @param dataDir The path to the data directory for storing certain kinds
     * of files.
     */
    private native void start(final boolean isPro, final String dataDir);
    //private native void start(final boolean isPro);
    
    private native void stop();
    
    private native long add_torrent(String incompletePath, String torrentPath, 
        int size, boolean sequential, int torrentState);

    public void setTotalUploadBytes(final long totalUploadBytes) {
        m_totalUploadBytes = totalUploadBytes;
    }

    public long getTotalUploadBytes() {
        return m_totalUploadBytes;
    }

    public void setTotalDownloadBytes(final long totalDownloadBytes) {
        // m_log.info("Setting total download bytes to: {}",
        // totalDownloadBytes);
        m_totalDownloadBytes = totalDownloadBytes;
    }

    public long getTotalDownloadBytes() {
        return m_totalDownloadBytes;
    }

    public void setDownloadRate(final int downloadRate) {
        // m_log.info("Setting download rate to: {}", downloadRate);
        m_downloadRate = downloadRate;
    }

    public int getDownloadRate() {
        return m_downloadRate;
    }

    public void setUploadRate(final int uploadRate) {
        // m_log.info("Setting upload rate to: {}", uploadRate);
        m_uploadRate = uploadRate;
    }

    public int getUploadRate() {
        return m_uploadRate;
    }

    public void setNumPeers(int numPeers) {
        m_numPeers = numPeers;
    }

    public int getNumPeers() {
        return m_numPeers;
    }

    public void setPayloadUploadRate(final int payloadUploadRate) {
        m_payloadUploadRate = payloadUploadRate;
    }

    public int getPayloadUploadRate() {
        return m_payloadUploadRate;
    }

    public void setPayloadDownloadRate(final int payloadDownloadRate) {
        m_payloadDownloadRate = payloadDownloadRate;
    }

    public int getPayloadDownloadRate() {
        return m_payloadDownloadRate;
    }

    public void setTotalPayloadUploadBytes(final long totalPayloadUploadBytes) {
        m_totalPayloadUploadBytes = totalPayloadUploadBytes;
    }

    public long getTotalPayloadUploadBytes() {
        return m_totalPayloadUploadBytes;
    }

    public void setTotalPayloadDownloadBytes(
            final long totalPayloadDownloadBytes) {
        m_totalPayloadDownloadBytes = totalPayloadDownloadBytes;
    }

    public long getTotalPayloadDownloadBytes() {
        return m_totalPayloadDownloadBytes;
    }

    public void portMapAlert(final int mappingId, final int externalPort,
        final int type) {
        m_log.info("GOT PORT MAPPED!! ID: " + mappingId + " EXTERNAL PORT: "
                + externalPort);

        final PortMapListener listener;
        
        // From LT docs: type is 0 for NAT-PMP and 1 for UPnP
        if (type == 0) {
            listener = this.m_natPmpMappingIdsToListeners.get(mappingId);
        }
        else {
            listener = this.m_upnpMappingIdsToListeners.get(mappingId);
        }

        if (listener == null) {
            // This can often happen because LibTorrent itself maps ports, and
            // we don't have listeners registered for those while we still
            // get alerts.
            m_log.info("No listener for ID!! " + mappingId);
            return;
        }
        listener.onPortMap(externalPort);
    }

    public void portMapLogAlert(final int type, final String message) {
        m_log.info("Port map log for type {}: " + message, type);
    }

    public void log(final String msg) {
        m_log.info("From native code: {}", msg);
    }

    public void logError(final String msg) {
        m_log.error("From native code: {}", msg);
    }
}
