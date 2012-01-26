package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.Preferences;

import org.apache.commons.io.IOUtils;
import org.lastbamboo.client.prefs.PrefKeys;
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
public class DummyFileMoverDownloader<DsT extends DownloaderState>
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
     * The file path to which we move the file downloaded by the delegate 
     * downloader.
     */
    private final File m_permanentFile;
    
    /**
     * The current state of this downloader.
     */
    private MoverDState<DsT> m_state;

    
    /**
     * Constructs a new downloader.
     * 
     * @param delegate The delegate downloader whose file will be moved when its 
     *  downloading is complete.
     */
    public DummyFileMoverDownloader(final Downloader<DsT> delegate)
        {
        m_delegate = delegate;
        
        final Preferences prefs = Preferences.userRoot ();
        final File downloadsDir = 
            new File(prefs.get (PrefKeys.DOWNLOAD_DIR, ""));
        if (!downloadsDir.isDirectory())
            {
            m_log.error("Downloads dir does not exist at: {}", downloadsDir);
            }
        
        m_permanentFile = new File (downloadsDir, m_delegate.getFinalName());
        
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
        return m_permanentFile;
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
                is = new FileInputStream (m_permanentFile);
                
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
            throw new RuntimeException ("Trying to write a failed download");
            }
        }

    public String getFinalName()
        {
        return this.m_delegate.getFinalName();
        }

    public File getCompleteFile()
        {
        return this.m_delegate.getCompleteFile();
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
        
        /**
         * {@inheritDoc}
         */
        public void stateChanged (final DsT state)
            {
            //LOG.debug ("(state, type) == (" + state + ", " + state.getType () +
            //               ")");
            
            if (state.getType () == DownloaderStateType.SUCCEEDED)
                {
                setState (new MoverDState.MovedImpl<DsT> ());
                final File permanentFile = m_delegate.getCompleteFile();
                // Try adding it to iTunes.
                try
                    {
                    ITunesUtils.addAudioFile(permanentFile);
                    }
                catch (final IOException e)
                    {
                    m_log.warn("Could not add to playlist", e);
                    }
                }
            else
                {
                if (DownloadUtils.isDownloading (m_state))
                    {
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
        }
    }
