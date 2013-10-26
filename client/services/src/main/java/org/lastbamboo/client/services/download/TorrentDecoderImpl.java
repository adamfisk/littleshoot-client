package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.lastbamboo.common.bencode.BDecoderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Meta-decoder that creates a delegate decoder using redundant decoder 
 * implementations. This is helpful because some torrents don't accurately 
 * follow the spec, and some decoders can handle files that don't conform while
 * others can't.
 */
public class TorrentDecoderImpl implements TorrentDecoder
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final TorrentDecoder m_delegate;

    /**
     * Creates a new meta-decoder.
     * 
     * @param torrentFile The torrent file to decode.
     * @throws IOException If there's an IO error decoding the torrent.
     */
    public TorrentDecoderImpl(final File torrentFile) throws IOException
        {
        Map<String, Object> torrentMap = null;
        try
            {
            torrentMap = BDecoderUtils.map(torrentFile);
            }
        catch (final IOException e)
            {
            m_log.warn("BDecoder could not parse torrent at: "+torrentFile, e);
            }
        catch (final RuntimeException e)
            {
            m_log.error("BDecoder parsing error for file at: "+torrentFile, e);
            }
        
        /*
        if (torrentMap == null)
            {
            try 
                {
                final FileInputStream is = new FileInputStream(torrentFile);
                final Object metaInfo = Token.parse(is.getChannel());
                if(!(metaInfo instanceof Map))
                    {
                    m_log.error("Not a map: {}", metaInfo);
                    throw new IOException("metaInfo not a Map!");
                    }
                torrentMap = (Map<String, Object>) metaInfo;
                copyToErrorDir(torrentFile, "bdecoder-only");
                }
            catch (final IOException e)
                {
                m_log.error("Could not create LimeWire torrent decoder", e);
                copyToErrorDir(torrentFile, "bdecoder-and-limewire");
                }
            }
            */
        if (torrentMap != null)
            {
            this.m_delegate = new MapTorrentDecoder(torrentMap);
            }
        else
            {
            throw new IOException("Could not create torrent decoder");
            }
        }

    public String getName()
        {
        return this.m_delegate.getName();
        }

    public int getNumFiles()
        {
        return this.m_delegate.getNumFiles();
        }
    
    private void copyToErrorDir(final File streamFile, final String subDirName)
        {
        final File lsDir = new File(SystemUtils.USER_HOME, ".littleshoot");
        lsDir.mkdirs();
        final File parentDir = new File(lsDir, subDirName);
        parentDir.mkdirs();
        final File errorDir = new File(parentDir, "torrent-error-files");
        errorDir.mkdirs();
        try
            {
            FileUtils.copyFileToDirectory(streamFile, errorDir);
            }
        catch (final IOException e)
            {
            m_log.warn("Could not copy torrent file to diretory", e);
            }
        }


    }
