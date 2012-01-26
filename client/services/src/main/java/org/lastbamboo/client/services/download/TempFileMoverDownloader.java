package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.lastbamboo.common.download.AbstractDownloader;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.DownloaderListener;
import org.lastbamboo.common.download.DownloaderState;
import org.lastbamboo.common.download.DownloaderStateType;
import org.lastbamboo.common.download.MoverDState;
import org.littleshoot.util.ITunesUtils;
import org.littleshoot.util.RuntimeIoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A downloader that moves a file downloaded by a delegate downloader to a
 * permanent home.  It considers the file downloaded by the delegate downloader
 * to be a temporary file.
 * 
 * @param <DsT> The delegate downloader state type.
 */
public class TempFileMoverDownloader<DsT extends DownloaderState>
    extends AbstractDownloader<MoverDState<DsT>>
    implements Downloader<MoverDState<DsT>>
    {
    
    /**
     * The log for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * The delegate downloader whose file will be moved when its downloading is
     * complete.
     */
    private final Downloader<DsT> m_delegate;
    
    /**
     * The current state of this downloader.
     */
    private MoverDState<DsT> m_state;

    private final Map<String, String> m_paramMap;

    private final Map<String, String> m_cookieMap;

    private final boolean m_move;

    private final FilePublisher m_filePublisher;

    private final FileMover m_fileMover;

    /**
     * Simplified constructor or a mover downloader.
     * 
     * @param delegate The delegate downloader whose file will be moved when its 
     *  downloading is complete.
     */
    public TempFileMoverDownloader(final Downloader<DsT> delegate)
        {
        this (delegate, 
            new HashMap<String, String>(), new HashMap<String, String>(), 
            false, 
            new FilePublisher()
                {
                public String publish(final File file)
                    {
                    // Does nothing.
                    return "";
                    }
                }
            );
        }
    
    /**
     * Simplified constructor or a mover downloader.
     * 
     * @param delegate The delegate downloader whose file will be moved when its 
     *  downloading is complete.
     * @param fileMover Custom class for moving the file.
     */
    public TempFileMoverDownloader(final Downloader<DsT> delegate, 
        final FileMover fileMover)
        {
        this (delegate, 
            new HashMap<String, String>(), new HashMap<String, String>(), 
            true, 
            new FilePublisher()
                {
                public String publish(final File file)
                    {
                    // Does nothing.
                    return "";
                    }
                },
            fileMover
            );
        }
    
    /**
     * Constructs a new downloader.
     * 
     * @param delegate The delegate downloader whose file will be moved when its 
     *  downloading is complete.
     * @param paramMap The map of original HTTP request parameters. 
     * @param cookieMap The map of cookies from the initial browser request.
     * @param move Whether or not to actually perform a move.
     * @param filePublisher The class for publishing the file to some 
     * repository.
     */
    public TempFileMoverDownloader(
        final Downloader<DsT> delegate, 
        final Map<String, String> paramMap, final Map<String, String> cookieMap, 
        final boolean move, final FilePublisher filePublisher)
        {
        this(delegate, paramMap, cookieMap, move, filePublisher, 
            new FileMover() 
                {
                public boolean move(final File incomplete, final File complete)
                    {
                    return incomplete.renameTo (complete);
                    }
                });
        }
    
    /**
     * Constructs a new downloader.
     * 
     * @param delegate The delegate downloader whose file will be moved when its 
     *  downloading is complete.
     * @param paramMap The map of original HTTP request parameters. 
     * @param cookieMap The map of cookies from the initial browser request.
     * @param move Whether or not to actually perform a move.
     * @param filePublisher The class for publishing the file to some 
     * repository.
     * @param fileMover The class for moving files.
     */
    private TempFileMoverDownloader(
        final Downloader<DsT> delegate, 
        final Map<String, String> paramMap, final Map<String, String> cookieMap, 
        final boolean move, final FilePublisher filePublisher,
        final FileMover fileMover)
        {
        this.m_delegate = delegate;
        this.m_paramMap = paramMap;
        this.m_move = move;
        this.m_filePublisher = filePublisher;
        this.m_fileMover = fileMover;
        
        // We need to flag this as a downloaded file for when we publish it
        // to the LittleShoot servers.
        this.m_paramMap.put("downloaded", Boolean.toString(true));
        this.m_cookieMap = cookieMap;
        
        m_state = new MoverDState.DownloadingImpl<DsT> (m_delegate.getState ());
        m_delegate.addListener (new DelegateListener ());
        }

    /**
     * Sets the current state of this downloader.
     * 
     * @param state The new state.
     */
    private void setState (final MoverDState<DsT> state)
        {
        if (m_state.equals (state))
            {
            // Do nothing.  The state has not changed.
            }
        else
            {
            m_state = state;
            
            fireStateChanged (state);
            }
        }
    
    public File getIncompleteFile ()
        {
        return this.m_delegate.getIncompleteFile();
        }
    
    public long getSize ()
        {
        return m_delegate.getSize ();
        }

    public MoverDState<DsT> getState ()
        {
        return m_state;
        }

    public void start ()
        {
        m_delegate.start ();
        }
    
    public boolean isStarted ()
        {
        return m_delegate.isStarted();
        }
    
    public void write (final OutputStream os, final boolean cancelOnStreamClose)
        {
        if (DownloadUtils.isDownloading (m_state))
            {
            m_delegate.write (os, cancelOnStreamClose);
            }
        else if (DownloadUtils.isMoved (m_state))
            {
            InputStream is = null;
            try
                {
                is = new FileInputStream (getCompleteFile());
                
                try
                    {
                    IOUtils.copy (is, os);
                    }
                catch (final IOException e)
                    {
                    m_log.warn("Could not copy resource");
                    throw new RuntimeIoException (e);
                    }
                }
            catch (final FileNotFoundException e)
                {
                m_log.warn("Downloaded file could not be found", e);
                throw new RuntimeException
                    ("Downloaded file could not be found", e);
                }
            finally 
                {
                IOUtils.closeQuietly(is);
                }
            }
        else
            {
            m_log.error("Trying to write a failed download in state: {}", 
                m_state);
            throw new RuntimeException ("Trying to write a failed download");
            }
        }

    public String getFinalName()
        {
        return this.m_delegate.getFinalName();
        }

    public File getCompleteFile()
        {
        return m_delegate.getCompleteFile();
        }
    
    public void stop(final boolean removeFiles)
        {
        this.m_delegate.stop(removeFiles);
        }
    
    public void pause()
        {
        this.m_delegate.pause();
        }
    
    public void resume()
        {
        this.m_delegate.resume();
        }
    
    /**
     * The listener attached to the delegate downloader used to react to changes
     * in the downloader.
     */
    private class DelegateListener implements DownloaderListener<DsT>
        {

        public void stateChanged (final DsT state)
            {
            m_log.debug ("(state, type) == (" + state + ", " + state.getType () +
                           ")");
            
            if (state.getType () == DownloaderStateType.SUCCEEDED)
                {
                m_log.debug("Complete..");
                downloadComplete ();
                }
            else if (state.getType() == DownloaderStateType.FAILED)
                {
                m_log.debug("Failed...");
                setState(new MoverDState.FailedImpl<DsT> (state));
                }
            else
                {
                if (DownloadUtils.isDownloading (m_state))
                    {
                    m_log.debug("Downloading...");
                    setState (new MoverDState.DownloadingImpl<DsT> (state));
                    }
                else
                    {
                    // Ignore events from the delegate downloader if we do not
                    // think that we are in the downloading state.  Something is
                    // odd if this happens, though.
                    m_log.warn ("Got delegate downloader event despite being " +
                                  "done with the downloader: " + state);
                    }
                }
            }
        
        /**
         * Performs the necessary actions when the delegate downloader has
         * successfully completed.
         */
        private void downloadComplete ()
            {
            setState (new MoverDState.MovingImpl<DsT> ());
            
            m_log.debug ("Moving file...");
            
            final File downloadedFile = m_delegate.getIncompleteFile ();
            
            final File permanentFile = m_delegate.getCompleteFile();
            if (!permanentFile.exists () && m_move)
                {
                if (!m_fileMover.move(downloadedFile, permanentFile))
                    {
                    m_log.warn ("Could not rename file to: {}", permanentFile);
                    setState (new MoverDState.MoveFailedImpl<DsT> ());
                    }
                else
                    {
                    m_log.debug ("Successfully renamed file to: {}", 
                        permanentFile);
                    
                    m_filePublisher.publish(permanentFile);
                    
                    toMovedState(permanentFile);
                    
                    }
                }
            
            // Otherwise, set to failed if were were supposed to move it.
            else if (m_move)
                {
                m_log.debug ("Downloaded file already exists at: {}", 
                    permanentFile);
                if (downloadedFile.length() != permanentFile.length())
                    {
                    m_log.warn ("Different file already exists at: {}", 
                        permanentFile);
                    setState (new MoverDState.MoveFailedImpl<DsT> ());
                    }
                else
                    {
                    // We consider it successfully moved.
                    toMovedState(permanentFile);
                    }
                }
            // Otherwise, it's already moved, and we've succeeded.
            else
                {
                toMovedState(permanentFile);
                }
            }

        private void toMovedState(final File permanentFile)
            {
            setState (new MoverDState.MovedImpl<DsT> ());
            
            if (iTunes(permanentFile))
                {
                setState (new MoverDState.MovedToITunesImpl<DsT> ());
                }
            }

        private boolean iTunes(final File permanentFile)
            {
            // Try adding it to iTunes.
            try
                {
                return ITunesUtils.addAudioFile(permanentFile);
                }
            catch (final IOException e)
                {
                m_log.warn("Could not add to playlist", e);
                return false;
                }
            }
        }
    }
