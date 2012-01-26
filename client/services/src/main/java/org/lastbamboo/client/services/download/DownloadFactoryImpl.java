package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.prefs.PrefKeys;
import org.lastbamboo.client.services.FileMapper;
import org.lastbamboo.client.services.RemoteResourceRepository;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.DummySha1Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.MultiSourceDownloader;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.download.Sha1Downloader;
import org.lastbamboo.common.download.UriResolver;
import org.littleshoot.util.DaemonThread;
import org.littleshoot.util.ResourceTypeTranslator;
import org.littleshoot.util.ResourceTypeTranslatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating new downloads.
 */
public class DownloadFactoryImpl implements DownloadFactory
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    /**
     * Creates a new factory for downloads.
     */
    public DownloadFactoryImpl()
        {
        final Runnable torrentLoader = new Runnable()
            {
            public void run()
                {
                try
                    {
                    loadTorrents();
                    } 
                catch (final Exception e)
                    {
                    m_log.error("Error loading torrents", e);
                    }
                }
            };
        final Thread torrentLoaderThread = new DaemonThread(torrentLoader);
        torrentLoaderThread.start();
        }

    public Downloader<MoverDState<Sha1DState<MsDState>>> createBitTorrentDownloader(
        final String uri, final File torrentFile) throws IOException
        {
        return createBitTorrentDownloader(uri, torrentFile, false,
            new HashMap<String, String>(), new HashMap<String, String>(), null,
            -1);
        }
    
    public Downloader<MoverDState<Sha1DState<MsDState>>> createBitTorrentResumeDownloader(
        final String uri, final File torrentFile, final File fullIncompleteDir, 
        final int torrentState) throws IOException
        {
        return createBitTorrentDownloader(uri, torrentFile, true,
            new HashMap<String, String>(), new HashMap<String, String>(),
            fullIncompleteDir, torrentState);
        }
    
    public Downloader<MoverDState<Sha1DState<MsDState>>> createBitTorrentDownloader(
        final String uri, final Map<String, String> paramMap, 
        final Map<String, String> cookieMap)
        {
        try
            {
            final File torrentFile = DownloadUtils.downloadTorrentFile(uri);
            return createBitTorrentDownloader(uri, torrentFile, false,
                paramMap, cookieMap, null, -1);
            }
        catch (final IOException e)
            {
            m_log.error("Could not create BitTorrent download", e);
            return null;
            }
        }
    
    
    private Downloader<MoverDState<Sha1DState<MsDState>>> createBitTorrentDownloader(
        final String uri, final File torrentFile, final boolean resume,
        final Map<String, String> paramMap, 
        final Map<String, String> cookieMap, final File fullIncompleteDir,
        final int torrentState) throws IOException
        {
        if (LittleShootModule.getDownloadTracker().hasActiveOrSucceededDownloader(uri))
            {
            return LittleShootModule.getDownloadTracker().getActiveOrSucceededDownloader(uri);
            }
        else
            {
            LittleShootModule.getDownloadTracker().deleteFailedDownloader(uri);
            final TorrentDecoder decoder = new TorrentDecoderImpl(torrentFile);
            final String name = decoder.getName();
            final File completeFile = 
                DownloadUtils.determineFile(name, 
                    LittleShootModule.getDownloadTracker());
            final int numFiles = decoder.getNumFiles();
            
            final ResourceTypeTranslator trans = new ResourceTypeTranslatorImpl();
            final String type = trans.getType(name);
            final boolean stream = trans.isAudioOrVideo(type);
            
            final Preferences prefs = Preferences.userRoot ();
            final File incompleteDir;
            if (fullIncompleteDir == null)
                {
                incompleteDir = new File(prefs.get (PrefKeys.INCOMPLETE_DIR, ""));
                }
            else
                {
                incompleteDir = fullIncompleteDir;
                }

            if (!incompleteDir.isDirectory())
                {
                m_log.error("Incomplete dir does not exist at: {}",incompleteDir);
                if (!incompleteDir.mkdirs())
                    {
                    m_log.error("Could not make dirs either!");
                    }
                }
            final LibTorrentDownloader torrentDownloader;
            try
                {
                torrentDownloader = 
                    new LibTorrentDownloader(uri, completeFile, 
                        LittleShootModule.getTorrentManager().getLibTorrent(),
                        incompleteDir, torrentFile, numFiles, stream, resume,
                        torrentState);
                }
            catch (final IOException e)
                {
                m_log.error("Could not create BitTorrent download", e);
                return null;
                }

            
            final long expectedSize = torrentDownloader.getSize();
            
            // The BitTorrent downloader verifies as it downloads, so we
            // don't verify at the end.
            final Downloader<Sha1DState<MsDState>> sha1Downloader =
                new DummySha1Downloader<MsDState> (torrentDownloader, 
                    expectedSize);
            
            final Downloader<MoverDState<Sha1DState<MsDState>>> downloader =
                new TempFileMoverDownloader<Sha1DState<MsDState>>(
                    sha1Downloader, torrentDownloader);
                
            LittleShootModule.getDownloadTracker().trackDownloader (uri, 
                downloader, torrentDownloader);
            return downloader;
            }
        }

    /*
    public Downloader<MoverDState<Sha1DState<MsDState>>> createLimeWireDownloader(
        final File completeFile, final URI expectedSha1, final long size, 
        final Map<String, String> paramMap, 
        final Map<String, String> cookieMap)
        {
        if (LittleShootModule.getDownloadTracker().hasActiveOrSucceededDownloader(expectedSha1))
            {
            return LittleShootModule.getDownloadTracker().getActiveOrSucceededDownloader(expectedSha1);
            }
        else
            {
            LittleShootModule.getDownloadTracker().deleteFailedDownloader(expectedSha1);
            final ResourceTypeTranslator trans = new ResourceTypeTranslatorImpl();
            final String type = trans.getType(completeFile.getName());
            final boolean streamable = trans.isAudioOrVideo(type);
            final LimeWireDownloader lwDownloader =  
                new LimeWireDownloader(completeFile, expectedSha1, size, 
                    LittleShootModule.getLimeWire(), 
                    LittleShootModule.getFileMapper(), 
                    LittleShootModule.getRemoteResourceRepository(), streamable);
            
            
            // We wrap the multi-source downloader with a SHA-1 verifying
            // downloader to ensure integrity.
            final Downloader<Sha1DState<MsDState>> sha1VerifyingDownloader =
                new Sha1Downloader<MsDState> (lwDownloader, expectedSha1, 
                    size);
            
            final Downloader<MoverDState<Sha1DState<MsDState>>> downloader =
                new TempFileMoverDownloader<Sha1DState<MsDState>>(
                    sha1VerifyingDownloader);
            
            LittleShootModule.getDownloadTracker().trackDownloader(
                expectedSha1, downloader, lwDownloader);
            return downloader;
            }
        }
        */
            
    public Downloader<MoverDState<Sha1DState<MsDState>>> createDownloader(
        final File incompleteFile, final URI uri, 
        final long size, final URI expectedUrn, 
        final UriResolver uriResolver, 
        final RemoteResourceRepository remoteRepository,
        final FileMapper fileMapper, final Map<String, String> paramMap,
        final Map<String, String> cookieMap)
        {
        if (LittleShootModule.getDownloadTracker().hasActiveOrSucceededDownloader(uri))
            {
            return LittleShootModule.getDownloadTracker().getActiveOrSucceededDownloader(uri);
            }
        else
            {
            m_log.debug("Creating new downloader...");
            LittleShootModule.getDownloadTracker().deleteFailedDownloader(uri);
            final ResourceTypeTranslator trans = new ResourceTypeTranslatorImpl();
            final String type = trans.getType(incompleteFile.getName());
            final boolean streamable = trans.isAudioOrVideo(type);
            
            final Preferences prefs = Preferences.userRoot ();
            final File downloadsDir = 
                new File(prefs.get (PrefKeys.DOWNLOAD_DIR, ""));
            
            if (!downloadsDir.isDirectory())
                {
                m_log.error("Downloads dir does not exist at: {}", downloadsDir);
                }
            final MultiSourceDownloader multiSourceDownloader = 
                new MultiSourceDownloader(incompleteFile, uri,  
                    size, uriResolver, 2, expectedUrn, 
                    downloadsDir, streamable);
            
            final Downloader<Sha1DState<MsDState>> sha1VerifyingDownloader;
            if (expectedUrn != null)
                {
                // We wrap the multi-source downloader with a SHA-1 
                // verifying downloader to ensure integrity.
                sha1VerifyingDownloader =
                    new Sha1Downloader<MsDState> (multiSourceDownloader, 
                        expectedUrn, size);
                }
            else
                {
                // If we don't have a SHA-1, we just add a dummy wrapper
                // that does the best it can, verifying things like the
                // size.
                sha1VerifyingDownloader = 
                    new DummySha1Downloader<MsDState> (
                        multiSourceDownloader, size);
                }
            
            // Finally, we wrap everything in a downloader that manages
            // downloading to a temporary directory.  We do this to avoid
            // making partially downloaded files available outside of the
            // system.
            final FilePublisher publisher = 
                new LittleShootFilePublisher(fileMapper, remoteRepository, 
                    uri, expectedUrn);
            final Downloader<MoverDState<Sha1DState<MsDState>>> downloader =
                new TempFileMoverDownloader<Sha1DState<MsDState>>(
                    sha1VerifyingDownloader, paramMap, cookieMap, true,
                    publisher);
            
            LittleShootModule.getDownloadTracker().trackDownloader (uri, 
                downloader, multiSourceDownloader);
            
            return downloader;
            }
        }
    
    private void loadTorrents()
        {
        m_log.info("Loading pre-existing torrents");
        final DownloadPersister persister = new DownloadPersister();
        final TorrentDataCallback callback = new TorrentDataCallback()
            {
            public void onData(final String uri, final File torrentFile,
                final File fullIncompleteDir, final int torrentState)
                {
                m_log.info("Loading torrent with incomplete dir: {}", 
                    fullIncompleteDir);
                try
                    {
                    createBitTorrentResumeDownloader(uri, torrentFile, 
                        fullIncompleteDir, torrentState);
                    }
                catch (final IOException e)
                    {
                    m_log.error("Could not create resume torrent downloader",e);
                    }
                }
            
            };
        persister.loadTorrents(callback);
        }

    }
