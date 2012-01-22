package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.lastbamboo.common.jlibtorrent.LibTorrentStreamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for "downloading" a directory to the user. This happens when a 
 * torrent download has multiple files, for example. This is effectively a 
 * no-op.
 */
public class DirectoryStreamer implements LibTorrentStreamer
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * Creates a new streamer for the specified directory.
     */
    public DirectoryStreamer()
        {
        }

    public void stop()
        {
        }

    public void write(final OutputStream os, final boolean cancelOnStreamClose)
        {
        
        try
            {
            os.close();
            }
        catch (final IOException e)
            {
            m_log.debug("Exception closing stream", e);
            }
        }

    public File getIncompleteFile()
        {
        return null;
        }

    }
