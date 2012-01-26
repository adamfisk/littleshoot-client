package org.lastbamboo.client.services.download;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import org.apache.commons.lang.SystemUtils;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.prefs.PrefKeys;
import org.lastbamboo.common.download.DownloadVisitor;
import org.lastbamboo.common.download.GnutellaDownloader;
import org.lastbamboo.common.download.LittleShootDownloader;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.TorrentDownloader;
import org.lastbamboo.common.download.VisitableDownloader;
import org.littleshoot.util.CommonUtils;
import org.littleshoot.util.Pair;
import org.lastbamboo.jni.JLibTorrent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager for LibTorrent.
 */
public class LibTorrentManagerImpl implements LibTorrentManager
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final JLibTorrent m_libTorrent;

    /**
     * Creates a new LibTorrent manager.
     */
    public LibTorrentManagerImpl()
        {
        final String libName = System.mapLibraryName("jnltorrent");
        final Collection<File> libCandidates = new LinkedList<File>();
        
        libCandidates.add(new File (new File("../../lib"), libName));
        libCandidates.add(new File (new File("../lib"), libName));
        libCandidates.add(new File (libName));
        libCandidates.add(new File (
            new File(SystemUtils.USER_HOME, ".littleshoot"), libName));
        
        this.m_libTorrent = new JLibTorrent(libCandidates, CommonUtils.isPro());
        
        final Runnable hookRunner = new Runnable()
            {
            public void run()
                {
                System.out.println("Updating torrent stats...");
                final Preferences prefs = Preferences.userRoot();
                final long totalDownload = 
                    prefs.getLong(PrefKeys.TOTAL_BITTORRENT_DOWNLOAD_BYTES, 0L);
                final long totalUpload = 
                    prefs.getLong(PrefKeys.TOTAL_BITTORRENT_UPLOAD_BYTES, 0L);
                prefs.putLong(PrefKeys.TOTAL_BITTORRENT_DOWNLOAD_BYTES, 
                    totalDownload+m_libTorrent.getTotalPayloadDownloadBytes());
                prefs.putLong(PrefKeys.TOTAL_BITTORRENT_UPLOAD_BYTES, 
                    totalUpload+m_libTorrent.getTotalPayloadUploadBytes());
                
                
                saveDownloads();
                
                System.out.println("Stopping LibTorrent");
                m_libTorrent.stopLibTorrent();
                }
            };
        
        final Thread hook = new Thread(hookRunner, "LibTorrent-Shutdown-Thread");
        Runtime.getRuntime().addShutdownHook(hook);
        }
    
    private void saveDownloads()
        {
        m_log.debug("Saving downloads....");
        final Collection<Entry<URI,Pair<?, VisitableDownloader<MsDState>>>> downloads = 
            LittleShootModule.getDownloadTracker().getActive();
        final DownloadPersister visitor = new DownloadPersister();
        
        // Make sure we remove any stale data.
        visitor.clear();
        for (final Entry<URI,Pair<?, VisitableDownloader<MsDState>>> download : downloads)
            {
            final VisitableDownloader<MsDState> vd = 
                download.getValue().getSecond();
            vd.accept(visitor);
            }
        
        visitor.save();
        }
    
    public void setSeeding(final boolean seeding)
        {
        final Collection<Entry<URI,Pair<?, VisitableDownloader<MsDState>>>> downloads = 
            LittleShootModule.getDownloadTracker().getAll();
        final DownloadVisitor<Object> visitor = new DownloadVisitor<Object>()
            {

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
                libTorrentDownloader.setSeeding(seeding);
                return null;
                }
        
            };
        for (final Entry<URI,Pair<?, VisitableDownloader<MsDState>>> download : downloads)
            {
            final VisitableDownloader<MsDState> vd = 
                download.getValue().getSecond();
            vd.accept(visitor);
            }
        }

    public JLibTorrent getLibTorrent()
        {
        return m_libTorrent;
        }

    public void setMaxUploadSpeed(final int bytesPerSecond)
        {
        m_libTorrent.setMaxUploadSpeed(bytesPerSecond);
        }
    }
