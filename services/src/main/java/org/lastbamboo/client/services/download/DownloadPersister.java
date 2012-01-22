package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.download.DownloadVisitor;
import org.lastbamboo.common.download.GnutellaDownloader;
import org.lastbamboo.common.download.LittleShootDownloader;
import org.lastbamboo.common.download.TorrentDownloader;
import org.littleshoot.util.Sha1Hasher;
import org.littleshoot.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for persisting downloads.
 */
public class DownloadPersister implements DownloadVisitor<Object>
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final Preferences m_torrents;
    
    private static final String TORRENTS_KEY = "torrents";
    private static final String TORRENT_FILE = "torrentFile";
    private static final String TORRENT_URI = "torrentUri";
    private static final String INCOMPLETE_DIR = "incompleteDir";
    private static final String TORRENT_STATE = "torrentState";

    /**
     * Creates a new class for persisting downloads.
     */
    public DownloadPersister() 
        {
        final Preferences prefs = Preferences.userRoot();
        
        this.m_torrents = prefs.node(TORRENTS_KEY);
        }
    
    public Object visitGnutellaDownloader(
        final GnutellaDownloader gnutellaDownloader)
        {
        return null;
        }

    public Object visitLittleShootDownloader(
        final LittleShootDownloader littleShootDownloader)
        {
        return null;
        }

    public Object visitTorrentDownloader(
        final TorrentDownloader libTorrentDownloader)
        {
        saveTorrent(libTorrentDownloader.getUri(), 
            libTorrentDownloader.getTorrentFile(),
            libTorrentDownloader.getIncompleteDir(),
            libTorrentDownloader.getTorrentState());
        return null;
        }

    /**
     * Saves the specified torrent data. Public for testing.
     * 
     * @param uri The {@link URI} for the torrent.
     * @param torrentFile The torrent file.
     * @param incompleteDir The incomplete directory.
     * @param torrentState The state of the torrent.
     */
    public void saveTorrent(final String uri, final File torrentFile, 
        final File incompleteDir, final int torrentState)
        {
        if (StringUtils.isBlank(uri))
            {
            m_log.error("Blank URI?");
            return;
            }
        
        // We use the hash because the straight URI can be too long for
        // a node name.
        // We encode it because the hash can contain characters not accepted
        // in node names.
        final String key = UriUtils.urlFormEncode(Sha1Hasher.hash(uri));
        m_log.debug("Using key: {}", key);
        
        //final String key = String.valueOf(uri.);
        final Preferences torrentPrefs = this.m_torrents.node(key);
        
        m_log.debug("Persisting torrent with uri: {}", uri);
        try
            {
            torrentPrefs.put(TORRENT_URI, uri);
            torrentPrefs.put(TORRENT_FILE, torrentFile.getCanonicalPath());
            torrentPrefs.put(INCOMPLETE_DIR, incompleteDir.getCanonicalPath());
            torrentPrefs.putInt(TORRENT_STATE, torrentState);
            }
        catch (final IOException e)
            {
            m_log.error("Error saving torrent: "+torrentFile+
                " with resume at: "+incompleteDir, e);
            }
        try
            {
            torrentPrefs.flush();
            }
        catch (final BackingStoreException e)
            {
            m_log.error("Error flushing data", e);
            }
        }

    /**
     * Saves all torrent data.
     */
    public void save()
        {
        try
            {
            this.m_torrents.flush();
            }
        catch (final BackingStoreException e)
            {
            m_log.error("Error flushing torrent data", e);
            }
        }
    

    /**
     * Clears all torrent data.
     */
    public void clear()
        {
        final Preferences root = Preferences.userRoot();
        final Preferences prefs = root.node(TORRENTS_KEY);
        String[] torrentUris;
        try
            {
            torrentUris = prefs.childrenNames();
            }
        catch (final BackingStoreException e)
            {
            m_log.error("Could not load torrents", e);
            return;
            }
        for (final String key : torrentUris)
            {
            final Preferences torrentNode = prefs.node(key);
            try
                {
                torrentNode.clear();
                }
            catch (final BackingStoreException e)
                {
                m_log.error("Could not clear", e);
                }
            }
        try
            {
            prefs.clear();
            }
        catch (final BackingStoreException e)
            {
            m_log.error("Could not clear", e);
            }
        }

    /**
     * Loads torrents.
     * 
     * @param callback The callback class for when we've loaded the data.
     */
    public void loadTorrents(final TorrentDataCallback callback)
        {
        final Preferences root = Preferences.userRoot();
        final Preferences prefs = root.node(TORRENTS_KEY);
        String[] torrentUris;
        try
            {
            torrentUris = prefs.childrenNames();
            }
        catch (final BackingStoreException e)
            {
            m_log.error("Could not load torrents", e);
            return;
            }
        for (final String key : torrentUris)
            {
            final Preferences torrentNode = prefs.node(key);
            final String torrentUri = torrentNode.get(TORRENT_URI, "");
            if (StringUtils.isBlank(torrentUri))
                {
                m_log.error("No torrent URI for key: {}", key);
                removeNode(torrentNode);
                continue;
                }
            final String torrentFilePath = torrentNode.get(TORRENT_FILE, "");
            if (StringUtils.isBlank(torrentFilePath))
                {
                m_log.error("No torrent file path?");
                removeNode(torrentNode);
                continue;
                }
            final File torrentFile = new File(torrentFilePath);
            if (!torrentFile.isFile())
                {
                m_log.warn("No torrent file at: {}", torrentFile);
                removeNode(torrentNode);
                continue;
                }
            final String incompleteDirPath = torrentNode.get(INCOMPLETE_DIR, "");
            if (StringUtils.isBlank(incompleteDirPath))
                {
                m_log.error("No torrent file path?");
                removeNode(torrentNode);
                continue;
                }
            final File incompleteDir = new File(incompleteDirPath);
            if (!incompleteDir.isDirectory())
                {
                m_log.warn("No incomplete dir at: {}", incompleteDir);
                removeNode(torrentNode);
                continue;
                }
            
            /*
            final File resumeFile = new File(incompleteDir, "resume.fastresume");
            if (!resumeFile.isFile())
                {
                m_log.warn("No fast resume file at: {}", resumeFile);
                removeNode(torrentNode);
                continue;
                }
                */
            
            final int torrentState = torrentNode.getInt(TORRENT_STATE, -1);
            if (torrentState == -1)
                {
                m_log.warn("No torrent state!");
                }
            
            //final String decodedUri = UriUtils.urlFormDecode(key);
            callback.onData(torrentUri, torrentFile, incompleteDir,torrentState);
            }
        }

    private void removeNode(final Preferences torrentNode)
        {
        try
            {
            torrentNode.removeNode();
            }
        catch (BackingStoreException e)
            {
            m_log.debug("Could not remove node",e);
            }
        }
    }
