package org.lastbamboo.common.searchers.limewire;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.limewire.bittorrent.Torrent;
import org.limewire.core.api.download.DownloadAction;
import org.limewire.core.api.download.DownloadException;
import org.limewire.io.GUID;
import org.limewire.io.IpPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.limegroup.gnutella.ActivityCallback;
import com.limegroup.gnutella.Downloader;
import com.limegroup.gnutella.RemoteFileDesc;
import com.limegroup.gnutella.Uploader;
import com.limegroup.gnutella.browser.MagnetOptions;
import com.limegroup.gnutella.messages.QueryReply;
import com.limegroup.gnutella.messages.QueryRequest;

@Singleton
public class LittleShootCallback implements ActivityCallback {

    private final Logger m_log = LoggerFactory
            .getLogger(LittleShootCallback.class);

    private final Collection<LimeWireQueryResultListener> m_searchListeners =
        new LinkedList<LimeWireQueryResultListener>();
    
    public void handleQueryResult(RemoteFileDesc rfd, 
            QueryReply queryReply,
            Set<? extends IpPort> altLocs) {
        m_log.debug("Received search result...");
        m_log.debug("Listeners: {}", m_searchListeners);
        synchronized (this.m_searchListeners) {
            for (final LimeWireQueryResultListener listener : this.m_searchListeners) {
                m_log.debug("Notifying listener...");
                listener.onSearchResult(rfd, altLocs, new GUID(queryReply.getGUID()));
            }
        }
    }

    public void addSearchListener(final LimeWireQueryResultListener listener) {
        this.m_searchListeners.add(listener);
    }

    public void addUpload(Uploader arg0) {
        m_log.debug("Received callback...");
    }

    public void handleSharedFileUpdate(File arg0) {
        m_log.debug("Received callback...");
    }

    public void handleTorrent(File arg0) {
        m_log.debug("Received callback...");
    }

    public void installationCorrupted() {
        m_log.debug("Received callback...");
    }

    public void restoreApplication() {
        m_log.debug("Received callback...");
    }

    public void uploadsComplete() {
        m_log.debug("Received callback...");
    }

    public void addDownload(final Downloader dl) {
        m_log.debug("Received callback...");
    }

    public void downloadsComplete() {
        m_log.debug("Received callback...");
    }

    public void removeDownload(Downloader arg0) {
        m_log.debug("Received callback...");
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " with search listeners: "
                + this.m_searchListeners + " and hashCode: " + hashCode();
    }

    public void handleDownloadException(DownloadAction downLoadAction,
            DownloadException e, boolean supportsNewSaveDir) {
        m_log.debug("Received callback...");
    }

    public void handleMagnets(MagnetOptions[] magnets) {
        m_log.debug("Received callback...");
    }

    public void handleQuery(QueryRequest query, String address, int port) {
        m_log.debug("Received callback...");
    }

    public boolean isQueryAlive(GUID guid) {
        m_log.debug("Received callback...");
        return false;
    }

    public String translate(String s) {
        m_log.debug("Received callback...");
        return s;
    }

    public void uploadComplete(Uploader u) {
        m_log.debug("Received callback...");
    }

    public void promptAboutUnscannedPreview(Downloader dloader) {
        m_log.debug("Received callback...");    
    }

    public boolean promptTorrentFilePriorities(Torrent torrent) {
        m_log.debug("Received callback...");
        return false;
    }
}
