package org.lastbamboo.common.jlibtorrent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.security.DigestOutputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.littleshoot.util.Base32;
import org.littleshoot.util.DaemonThread;
import org.littleshoot.util.Sha1;
import org.littleshoot.util.ThreadUtils;
import org.lastbamboo.jni.JLibTorrent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for streaming LibTorrent downloads to the browser.
 */
public class LibTorrentStreamerImpl implements LibTorrentStreamer
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final JLibTorrent m_libTorrent;
    private final File m_torrentFile;
    
    private final long m_completeSize;
    private volatile boolean m_stopped;
    private final LibTorrentDownloadListener m_stateListener;
    private final File m_saveDir;
    private File m_incompleteFile;

    public LibTorrentStreamerImpl(final JLibTorrent libTorrent, 
        final File torrentFile, final long completeSize, final File saveDir,
        final LibTorrentDownloadListener stateListener)
        {
        this.m_libTorrent = libTorrent;
        this.m_torrentFile = torrentFile;
        this.m_completeSize = completeSize;
        this.m_saveDir = saveDir;
        //this.m_downloadFile = saveFile;
        this.m_stateListener = stateListener;
        }
    
    public void write(final OutputStream originalOs,
        final boolean cancelOnStreamClose) {
        final DigestOutputStream os = 
            new DigestOutputStream(originalOs, new Sha1());

        try {
            stream(os);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    private boolean stream(final DigestOutputStream os) {
        long totalTransferred = 0;
        int fileNotFoundCount = 0;

        while (true && !this.m_stopped) {
            final int state;
            synchronized (this.m_torrentFile) {
                if (this.m_stopped)
                    break;
                state = 
                    this.m_libTorrent.getStateForTorrent(this.m_torrentFile);
            }
            // m_log.debug("In state: " + state);
            this.m_stateListener.onState(state);

            /*
             * Here's the enum of states as a reference. enum state_t {
             * queued_for_checking, checking_files, downloading_metadata,
             * downloading, finished, seeding, allocating };
             */

            if (state == LibTorrentUtils.FAILED
                    || state == LibTorrentUtils.INVALID_HANDLE) {
                m_log.debug("Torrent no longer valid!!");
                this.m_stateListener.onFailure();
                return false;
            }
            if (state == 0 || state == 1 || state == 2 || state == 6) {
                m_log.debug("Download hasn't started. In state: " + state);
                ThreadUtils.safeSleep(1000);
                continue;
            }

            if (state == 200) {
                m_log.debug("Download is paused");
                ThreadUtils.safeSleep(1000);
                continue;
            }

            final File saveFile;
            if (this.m_incompleteFile != null) {
                saveFile = this.m_incompleteFile;
            } else {
                try {
                    saveFile = determineDownloadFile();
                } catch (final IOException e2) {
                    m_log.debug("Unexpected error", e2);
                    IOUtils.closeQuietly(os);
                    return false;
                }
                if (saveFile == null) {
                    // LibTorrent likely just hasn't actually allocated the file
                    // on
                    // disk yet.
                    m_log.debug("Could not find download file! Not written yet?");
                    if (fileNotFoundCount > 1000) {
                        m_log.error("Still could not create file after way "
                                + "too many tries.  Aborting");
                        m_stateListener.onFailure();
                        break;
                    }
                    ThreadUtils.safeSleep(800);
                    fileNotFoundCount++;
                    continue;
                }
            }
            final FileInputStream is;
            try {
                is = new FileInputStream(saveFile);
            } catch (final FileNotFoundException e) {
                m_log.error("File still not found?", e);
                IOUtils.closeQuietly(os);
                return false;
            }

            final FileChannel fc = is.getChannel();
            try {
                fc.position(totalTransferred);
            } catch (final IOException e1) {
                m_log.error("Could not set position", e1);
            }
            final WritableByteChannel wbc = Channels.newChannel(os);
            
            //m_log.debug("Accessing max byte for torrent...");
            final long fileIndex;
            synchronized (this.m_torrentFile) {
                if (this.m_stopped)
                    break;
                fileIndex = 
                    this.m_libTorrent.getMaxByteForTorrent(this.m_torrentFile);
            }
            m_log.info("File index: {}", fileIndex);
            if (fileIndex > totalTransferred) {
                m_log.debug("Got new file index: " + fileIndex);
                // Stream to the index
                final long count = fileIndex - totalTransferred;

                m_log.debug("Transferring bytes: " + count);
                try {
                    // final long transferred = IoUtils.copy(is, os, count);
                    //
                    // final byte[] newData = new byte[count];
                    // is.read(newData, this.m_lastFileIndex, count);

                    final long transferred = fc.transferTo(totalTransferred,
                            count, wbc);
                    if (transferred < count) {
                        m_log.debug("Did not transfer all the bytes!! "
                                + "Browser closed?");
                    }
                    os.flush();
                    totalTransferred += transferred;
                    // this.m_lastFileIndex = fileIndex;
                } catch (final IOException e) {
                    // Could indicate the user closed the browser window??
                    m_log.debug("Exception transferring file", e);
                    break;
                }

                m_log.debug("Total bytes transferred: " + totalTransferred);
                if (totalTransferred == this.m_completeSize) {
                    m_log.debug("Download finished!");
                    break;
                }
            }
            m_log.info("About to get lock");
            synchronized (this) {
                try {
                    m_log.info("Waiting");
                    wait(1000);
                    m_log.info("Done waiting");
                } catch (final InterruptedException e) {
                    m_log.debug("Interrupted", e);
                    break;
                }
            }
        }

        try {
            os.flush();
        } catch (final IOException e) {
            m_log.debug("Exception flushing data", e);
        }
        
        // We do this here because we need to make sure we've written all the
        // bytes from the incomplete file to the browser.
        final int state;
        synchronized (this.m_torrentFile)
            {
            if (this.m_stopped) return false;
            
            state = this.m_libTorrent.getStateForTorrent(this.m_torrentFile);
            }
        if (LibTorrentUtils.isComplete(state))
            {
            // We thread this because it can otherwise create a deadlock!!
            final Runnable runner = new Runnable() 
                {
                public void run()
                    {
                    m_stateListener.onComplete();
                    
                    final byte[] sha1Bytes = os.getMessageDigest().digest();
                    
                    try
                        {
                        // preferred casing: lowercase "urn:sha1:", uppercase encoded value
                        // note that all URNs are case-insensitive for the "urn:<type>:" 
                        // part, but some MAY be case-sensitive thereafter (SHA1/Base32 is 
                        // case insensitive)
                        final URI sha1 = new URI("urn:sha1:"+Base32.encode(sha1Bytes));
                        m_stateListener.onVerifyStream(sha1);
                        }
                    catch (final URISyntaxException e)
                        {
                        // This should never happen.
                        m_log.error("Could not encode SHA-1", e);
                        } 
                    }
                };
            final Thread daemon = 
                new DaemonThread(runner, "Torrent-Complete-Runner");
            daemon.start();
            return true;
            }
        return false;
        }

    /**
     * This exists because translating file paths between JNI and Java is a
     * PITA, especially cross-platform.  We've basically created a unique 
     * directory for the download, so we just traverse it until we find the
     * only file there. This allows us to totally bypass native path issues.
     * 
     * @return The file, or null if it doesnt't exist yet.
     * @throws IOException If an unexpected file configuration is found.
     */
    private File determineDownloadFile() throws IOException {
        final File[] files = this.m_saveDir.listFiles();
        if (files == null) {
            // Looks like the directory was deleted externally
            m_log.debug("Save directory deleted.");
            throw new IOException("Save directory deleted");
        }
        if (files.length == 0) {
            m_log.debug("No files yet in temp dir");
            return null;
        }

        m_log.info("Filesin temp download dir: {}", Arrays.asList(files));

        if (files.length > 2) {
            m_log.warn("Unexpected number of files in download dir: {}",
                    files.length);
        }
        File file1 = null;
        for (final File f : files) {
            if (!f.getName().endsWith("fastresume")) {
                file1 = f;
            }
        }
        if (file1 == null) {
            m_log.error("Could not find downloading file!!");
            throw new IOException("Could not find downloading file!!");
        }
        if (file1.isFile()) {
            m_log.debug("Returning file: {}", file1);
            this.m_incompleteFile = file1;
            return file1;
        } else {
            final File[] subFiles = file1.listFiles();
            if (subFiles.length == 0) {
                m_log.debug("No files yet in download sub dir");
                return null;
            } else {
                final File subFile1 = subFiles[0];
                if (!subFile1.isFile()) {
                    m_log.warn("Sub dir file not a file in download");
                    throw new IOException("Sub dir file not a file: "
                            + subFile1);
                } else {
                    m_log.debug("Returning sub dir file: {}", subFile1);
                    this.m_incompleteFile = subFile1;
                    return subFile1;
                }
            }
        }
    }

    public void stop() {
        synchronized (this.m_torrentFile) {
            this.m_stopped = true;
        }
        synchronized (this) {
            this.notifyAll();
        }
    }

    public File getIncompleteFile() {
        return this.m_incompleteFile;
    }

}
