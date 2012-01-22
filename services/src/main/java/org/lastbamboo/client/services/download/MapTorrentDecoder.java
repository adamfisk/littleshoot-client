package org.lastbamboo.client.services.download;

import java.util.Map;

import org.lastbamboo.common.bencode.BDecoderUtils;

/**
 * Generic torrent decoder that will work with many decoding implementations. 
 * This just takes a {@link Map} of {@link String}s to {@link Object}s that 
 * many Java bdecoders will create and uses standard access calls on top of
 * the {@link Map} to access torrent data.
 */
public class MapTorrentDecoder implements TorrentDecoder
    {

    private final Map<String, Object> m_map;

    /**
     * Creates a new map decoder.
     * 
     * @param map The {@link Map} containing torrent data.
     */
    public MapTorrentDecoder(final Map<String, Object> map)
        {
        this.m_map = map;
        }

    public String getName()
        {
        return BDecoderUtils.name(this.m_map);
        }

    public int getNumFiles()
        {
        return BDecoderUtils.numFiles(this.m_map);
        }

    }
