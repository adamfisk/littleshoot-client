package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.lastbamboo.common.download.AbstractDownloader;
import org.lastbamboo.common.download.DownloadVisitor;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.MsDState.Downloading;
import org.lastbamboo.common.download.TorrentDownloader;
import org.lastbamboo.common.download.VisitableDownloader;
import org.lastbamboo.common.jlibtorrent.LibTorrentDownloadListener;
import org.lastbamboo.common.jlibtorrent.LibTorrentStreamer;
import org.lastbamboo.common.jlibtorrent.LibTorrentStreamerImpl;
import org.lastbamboo.common.jlibtorrent.LibTorrentUtils;
import org.littleshoot.util.CommonUtils;
import org.littleshoot.util.DaemonThread;
import org.littleshoot.util.Sha1Hasher;
import org.littleshoot.util.ThreadUtils;
import org.lastbamboo.jni.JLibTorrent;
//import org.limewire.collection.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.limegroup.gnutella.Downloader.DownloadState;
//import com.limegroup.gnutella.downloader.DownloadListener;

/**
 * Downloader for downloading torrents using JNI bridge to LibTorrent.
 */
public class LibTorrentDownloader extends AbstractDownloader<MsDState>
    implements Downloader<MsDState>, LibTorrentDownloadListener,
    FileMover, VisitableDownloader<MsDState>, TorrentDownloader //DownloadListener
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private MsDState m_state;
    private final String m_finalName;
    private final File m_completeFileOrFolder;
    private final AtomicBoolean m_started = new AtomicBoolean(false);
    private final File m_torrentFile;
    private final JLibTorrent m_libTorrent;
    private long m_fileSize;
    private volatile boolean m_stopped;
    private final LibTorrentStreamer m_streamer;
    private final int m_numFiles;
    
    private volatile boolean m_updateStats = true;

    /**
     * Set to a placeholder value to indicate it's uninitialized.
     */
    private long m_maxByte = -10L;

    private volatile int m_curTorrentState;

    private final boolean m_streamable;

    private volatile int m_activeWrites;
    
    private final Object CLOSE_LOCK = new Object();
    
    private final Collection<URI> m_streamedSha1s = new LinkedList<URI>();

    private final File m_incompleteDir;

    private final String m_uri;

    private volatile boolean m_inOldSessionState;

    public LibTorrentDownloader(final String uri, 
        final File completeFileOrFolder,
        final JLibTorrent libTorrent, final File incompleteBaseDir,
        final File torrentFile, final int numFiles, final boolean stream, 
        final boolean resume, final int torrentState) throws IOException 
        {
        this.m_uri = uri;
        this.m_libTorrent = libTorrent;
        this.m_completeFileOrFolder = completeFileOrFolder;
        setState (new MsDState.GettingSourcesImpl());
        
        if (resume)
            {
            // For resumes, the incomplete directory passed in is the full, 
            // unique directory.
            this.m_inOldSessionState = true;
            this.m_incompleteDir = incompleteBaseDir;
            }
        else
            {
            this.m_inOldSessionState = false;
            this.m_incompleteDir = newUniqueIncompleteDir(incompleteBaseDir);
            }
        m_torrentFile = torrentFile;
        
        this.m_streamable = numFiles == 1 && stream;
        m_log.info("About to call download on torrent...");
        this.m_libTorrent.download(m_incompleteDir, torrentFile, m_streamable,
            torrentState);
        m_log.info("Finished download call...");
        
        try
            {
            Thread.sleep(300);
            }
        catch (final InterruptedException e)
            {
            m_log.warn("Unexpected interrupt", e);
            }
        
        // If we're resuming a torrent from a previous session, and that
        // torrent was paused, we need to manually pause it.
        if (torrentState == LibTorrentUtils.PAUSED)
            {
            m_log.info("Calling pause");
            pause();
            }
        m_log.info("Getting file size for torrent...");
        this.m_fileSize = 
            this.m_libTorrent.getSizeForTorrent(this.m_torrentFile);
        m_log.info("Finished getting size for torrent");
        
        if (this.m_fileSize < 0)
            {
            m_log.error("LibTorrent returned a negative size for the torrent. " +
                "Over 2GB?");
            }
        
        m_log.info("Downloading torrent using torrent file at path: {}", 
            this.m_torrentFile);
        m_log.debug("File size: " + this.m_fileSize);
        this.m_finalName =  this.m_libTorrent.getName(this.m_torrentFile);
        m_log.debug("Using final name: {}", this.m_finalName);
        
        this.m_numFiles = numFiles;
        
        //m_log.debug("Using incomplete path: {}", this.m_incompleteFile);
        if (m_numFiles > 1)
            {
            m_log.debug("Downloading multiple files to directory...");
            this.m_streamer = new DirectoryStreamer();
            }
        else 
            {
            m_log.debug("Downloading single file...");
            this.m_streamer = 
                new LibTorrentStreamerImpl(this.m_libTorrent, this.m_torrentFile, 
                    this.m_fileSize, m_incompleteDir, this);
            }
        
        updateDownloadStats();
        m_log.debug("Final name: {}", this.m_finalName);
        m_log.debug("Torrent state: "+torrentState);
        
        final Runnable hookRunner = new Runnable()
            {
            public void run() 
                {
                System.out.println("Stopping torrent: "+m_finalName);
                // We don't actually remove the torrent here because it can
                // cause invalid memory access errors.
                m_stopped = true;
                m_streamer.stop();
                }
            };
        final Thread hook = new Thread(hookRunner, 
            "LibTorrent-Download-Shutdown-"+this.m_finalName);
        Runtime.getRuntime().addShutdownHook(hook);
        }
    
    private File newUniqueIncompleteDir(final File incompleteBaseDir) 
        throws IOException
        {
        for (int i = 0; i < 20; i++)
            {
            final File dir = 
                new File(incompleteBaseDir, "torrent-"+RandomUtils.nextInt());
            if (!dir.isDirectory())
                {
                if (!dir.mkdirs())
                    {
                    m_log.error("Could not make directory: {}", dir);
                    }
                else
                    {
                    return dir;
                    }
                }
            }
        m_log.error("Could not make unique directory");
        throw new IOException("Could not create unique directory!!");
        }

    public File getCompleteFile()
        {
        return this.m_completeFileOrFolder;
        }

    public String getFinalName()
        {
        // http://developer.amazonwebservices.com/connect/thread.jspa?messageID=63944&#63944
        final String delim = "_files_";
        if (this.m_finalName.contains(delim))
            {
            final String newName = 
                StringUtils.substringAfterLast(this.m_finalName, delim);
            return newName;
            }
        return this.m_finalName;
        }

    public File getIncompleteFile()
        {
        if (this.m_numFiles == 1)
            {
            return this.m_streamer.getIncompleteFile();
            }
        else
            {
            return null;
            }
        }

    public long getSize()
        {
        return this.m_fileSize;
        }

    public MsDState getState()
        {
        return this.m_state;
        }

    public boolean isStarted()
        {
        return this.m_started.get();
        }

    public void start()
        {
        // The download is already occurring on a separate native thread, so
        // we ignore this call (and it's not necessary in this case due to
        // threading differences).
        this.m_started.set(true);
        }

    public void stop(final boolean removeFiles)
        {
        if (this.m_stopped)
            {
            m_log.debug("Already stopped!");
            return;
            }

        this.m_stopped = true;
        
        this.m_streamer.stop();
        setState(MsDState.CANCELED);

        // We delay the actual canceling in LibTorrent temporarily to make
        // sure the statistics updating thread doesn't attempt to access an
        // invalid torrent handle.
        final TimerTask tt = new TimerTask()
            {
            @Override
            public void run()
                {
                m_log.debug("Removing torrent handle...");
                final File resumeFile = 
                    new File(m_incompleteDir, "resume.fastresume");
                resumeFile.delete();
                if (removeFiles)
                    {
                    m_libTorrent.removeTorrentAndFiles(m_torrentFile);
                    
                    m_log.info("Deleting incomplete dir: {}", 
                        m_incompleteDir);
                    
                    try {
                        // Sleep to give LibTorrent time to clean up.
                        Thread.sleep(300);
                        
                        // This is the dir of the form: 
                        // incomplete/torrent-181562596
                        FileUtils.deleteDirectory(m_incompleteDir);
                    } catch (final IOException e) {
                        m_log.error("Could not delete: "+m_incompleteDir, e);
                    } catch (final InterruptedException e) {
                        m_log.error("Could not delete: "+m_incompleteDir, e);
                    }
                    }
                else
                    {
                    m_log.info("Not removing all files");
                    m_libTorrent.removeTorrent(m_torrentFile);
                    }
                }
            };
        final Timer timer = new Timer("Torrent-Download-Cancel-Thread", true);
        timer.schedule(tt, 3000);
        }

    public void write(final OutputStream os, final boolean cancelOnStreamClose)
        {
        m_log.debug("Starting stream");

        if (this.m_numFiles == 1)
            {
            // The streamer will take over stats updates.
            m_updateStats = false;
            this.m_activeWrites++;
            m_log.debug("Incremented writes to: "+this.m_activeWrites);
            try 
                {
                this.m_streamer.write(os, cancelOnStreamClose);
                }
            finally
                {
                this.m_activeWrites--;
                m_log.debug("Finished write, decremented active writes to: "+
                    this.m_activeWrites);
                if (this.m_activeWrites == 0)
                    {
                    if (!LibTorrentUtils.isComplete(this.m_curTorrentState))
                        {
                        // Go back to updating the stats!
                        m_updateStats = true;
                        updateDownloadStats();
                        }
                    synchronized (CLOSE_LOCK)
                        {
                        m_log.debug("Notifying close lock waiters");
                        CLOSE_LOCK.notifyAll();
                        }
                    }
                }
            }
        else
            {
            m_log.debug("Writing multiple file torrent with stats: "+
                this.m_updateStats);
            }
        }
    
    private void setState (final MsDState state)
        {
        if (m_state != null && m_state.equals (state))
            {
            // Do nothing.  The state has not changed.
            //m_log.debug("No state change.  State remaining: {}", state);
            }
        else
            {
            m_state = state;
            //m_log.debug ("Setting state of " +this.m_finalName +" to "+ state);
            fireStateChanged (state);
            }
        }

    public void onComplete()
        {
        m_log.debug("Got file complete!!");
        setState(MsDState.COMPLETE);
        }

    public void onData(final File file, final RandomAccessFile raf, int arg2)
        {
        // Ignored since we rely on JNI to LibTorrent for all data.
        }

    /*
    public void onPendingClose(final DownloadState downloadStatus)
        {
        // Ignored since we rely on JNI to LibTorrent for all data.
        }

    public void onRangeRead(final Range range)
        {
        // Ignored since we rely on JNI to LibTorrent for all data.
        }
    */

    public void onState(final int state) {
        if (this.m_stopped) {
            return;
        }
        synchronized (this.m_torrentFile) {
            if (state == LibTorrentUtils.DOWNLOADING) {
                // We need to synchronize on the torrent file because another
                // thread could render the torrent handle invalid, which would
                // crash the whole program.
                setState(newDownloadingState());
            } else if (state == LibTorrentUtils.PAUSED) {
                if (this.m_curTorrentState != state) {
                    setState(new MsDState.PausedImpl(newDownloadingState()));
                }
            } else {
                m_log.info("Nothing to do for state: {}", state);
            }

            this.m_curTorrentState = state;
        }
    }
    

    private Downloading newDownloadingState()
        {
        if (this.m_stopped)
            {
            m_log.error("In stopped state: "+ThreadUtils.dumpStack());
            // Returning null is better than crashing.
            return null;
            }
        synchronized (this.m_torrentFile)
            {
            final int bytesPerSecond = 
                this.m_libTorrent.getDownloadSpeed(this.m_torrentFile);
            return new MsDState.LibTorrentDownloadingState (
                bytesPerSecond,
                this.m_libTorrent.getNumHosts(this.m_torrentFile), 
                this.m_libTorrent.getBytesRead(this.m_torrentFile),
                this.m_numFiles,
                this.m_libTorrent.getMaxByteForTorrent(this.m_torrentFile),
                getSize());
            }
        }
    
    public void onFailure()
        {
        this.m_stopped = true;
        synchronized (this.m_torrentFile)
            {
            setState(MsDState.FAILED);
            this.m_libTorrent.removeTorrent(this.m_torrentFile);
            }
        }

    public boolean move(final File incomplete, final File complete)
        {
        m_log.info("Got call to move file...");
        
        // TODO: I don't think this thread is a daemon thread -- an issue when
        // we're closing and it blocks.
        synchronized (CLOSE_LOCK)
            {
            if (this.m_activeWrites > 0)
                {
                m_log.info("Waiting for active writes to complete");
                try
                    {
                    CLOSE_LOCK.wait(1000*60*60*24*14);
                    if (this.m_activeWrites > 0)
                        {
                        m_log.error("Still active writes:"+this.m_activeWrites);
                        }
                    }
                catch (final InterruptedException e)
                    {
                    m_log.debug("Interrupted", e);
                    }
                }
            }
        if (this.m_stopped)
            {
            m_log.warn("Moving a stopped torrent");
            return true;
            }

        if (this.m_completeFileOrFolder.exists()) 
            {
            m_log.debug("Already exists at: "+
                this.m_completeFileOrFolder);
            return true;
            }
        
        m_log.debug("No existing file or folder at: {}. Moving.", 
            this.m_completeFileOrFolder);
        
        final File downloadsDir = complete.getParentFile();
        
        if (!downloadsDir.equals(this.m_completeFileOrFolder.getParentFile()))
            {
            m_log.error("Downloads folder mismatch");
            }
        synchronized (this.m_torrentFile)
            {
            // This is correct to use the .torrent file instead of the
            // complete file here, as LibTorrent keeps track of all 
            // downloads by their corresponding torrent file.
            this.m_libTorrent.moveToDownloadsDir(this.m_torrentFile, downloadsDir);
            }

        int sleeps = 0;
        // Note the complete location can be a directory.
        while (!this.m_completeFileOrFolder.exists() && sleeps < 100)
            {
            ThreadUtils.safeSleep(1000);
            sleeps++;
            }
        
        // Note the complete location can be a directory.
        if (!this.m_completeFileOrFolder.exists())
            {
            m_log.error("Still no file or directory after " + 
                sleeps+" sleeps at: {}", this.m_completeFileOrFolder);
            return false;
            }
        if (this.m_completeFileOrFolder.isDirectory())
            {
            m_log.debug("Not taking the hash of a folder");
            return true;
            }
        try
            {
            final URI fileSha1 = 
                Sha1Hasher.createSha1Urn(this.m_completeFileOrFolder);
            for (final URI sha1 : this.m_streamedSha1s)
                {
                if (!sha1.equals(fileSha1))
                    {
                    m_log.error("Streamed SHA-1 not equal to file SHA-1!!");
                    }
                }
            }
        catch (final IOException e)
            {
            m_log.error("Could not create SHA-1!", e);
            }

        synchronized (this.m_torrentFile)
            {
            if (this.m_completeFileOrFolder.isFile()) 
                {
                // We need to get rid of Amazon S3's annoying naming if we find it.
                // See this thread:
                // http://developer.amazonwebservices.com/connect/thread.jspa?messageID=63944&#63944
                final String delim = "_files_";
                if (this.m_finalName.contains(delim))
                    {
                    final String newName = 
                        StringUtils.substringAfterLast(this.m_finalName, delim);
                    m_log.info("Renaming file to: {}", newName);
                    this.m_libTorrent.rename(this.m_torrentFile, newName);
                    }
                else
                    {
                    m_log.debug("Not renaming file");
                    }
                }
            }
        
        return true;
        }

    private void updateDownloadStats()
        {
        // We can't stream torrents with multiple files, but we still
        // have to update the state of the download.
        final Runnable runner = new Runnable() 
            {

            public void run()
                {
                while (true && !m_stopped && m_updateStats)
                    {
                    if (m_stopped) break;
                    final int state;
                    synchronized (m_torrentFile)
                        {
                        state = m_libTorrent.getStateForTorrent(m_torrentFile);
                        }
                    m_log.debug("In state: " + state);
                    onState(state);
                    
                    if (LibTorrentUtils.isComplete(state))
                        {
                        final Preferences prefs = Preferences.userRoot();
                        final boolean seedingEnabled = 
                            prefs.getBoolean(CommonUtils.SEEDING_ENABLED_KEY, true);
                        
                        if (!seedingEnabled) 
                            {
                            pause();
                            }
                        onComplete();
                        break;
                        }
                    ThreadUtils.safeSleep(1000);
                    }
                }
            };
        final DaemonThread thread = 
            new DaemonThread(runner, "Download-State-Updater-"+m_finalName);
        thread.start();
        }
    
    public void setSeeding(final boolean seeding)
        {
        if (LibTorrentUtils.isComplete(this.m_curTorrentState))
            {
            if (!seeding)
                {
                m_log.debug("Turning seeding off (pausing)");
                pause();
                }
            else if (seeding)
                {
                m_log.debug("Turning seeding on (resuming)");
                resume();
                }
            }
        }

    public long getMaxContiguousByte()
        {
        synchronized (m_torrentFile)
            {
            if (!this.m_stopped)
                {
                this.m_maxByte = 
                    this.m_libTorrent.getMaxByteForTorrent(this.m_torrentFile);
                }
            }
        return this.m_maxByte;
        }

    public int getNumFiles()
        {
        return this.m_numFiles;
        }

    public int getTorrentState()
        {
        return this.m_libTorrent.getStateForTorrent(this.m_torrentFile);
        }

    public void pause()
        {
        if (this.m_stopped)
            {
            m_log.error("Torrent is stopped!!");
            return;
            }
        synchronized (m_torrentFile)
            {
            setState(new MsDState.PausedImpl(newDownloadingState()));
            this.m_libTorrent.pauseTorrent(this.m_torrentFile);
            }
        }

    public void resume()
        {
        if (this.m_stopped)
            {
            m_log.error("Torrent is stopped!!");
            return;
            }
        synchronized (m_torrentFile)
            {
            setState(newDownloadingState());
            if (this.m_inOldSessionState)
                {
                this.m_libTorrent.hardResumeTorrent(this.m_torrentFile);
                }
            else
                {
                this.m_libTorrent.resumeTorrent(this.m_torrentFile);
                }
            this.m_inOldSessionState = false;
            }
        }
    
    public long getBytesRead()
        {
        return this.m_libTorrent.getBytesRead(this.m_torrentFile);
        }
    
    public void onVerifyStream(final URI sha1)
        {
        this.m_streamedSha1s.add(sha1);
        }

    public boolean isStreamable()
        {
        return this.m_streamable;
        }
    
    public File getTorrentFile()
        {
        return this.m_torrentFile;
        }

    public File getIncompleteDir()
        {
        return this.m_incompleteDir;
        }

    public String getUri()
        {
        return this.m_uri;
        }
    
    public <T> T accept(DownloadVisitor<T> visitor)
        {
        return visitor.visitTorrentDownloader(this);
        }
    }

