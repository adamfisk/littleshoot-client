package org.lastbamboo.client.services.download;

/**
 * Interface for classes that decoder torrents.  These have specialized 
 * methods specific to torrents at a higher level than raw bdecoders.
 */
public interface TorrentDecoder
    {

    /**
     * The name for the torrent.
     * 
     * @return The name for the torrent.
     */
    String getName();

    /**
     * The number of files in the torrent.
     * 
     * @return The number of files in the torrent.
     */
    int getNumFiles();

    }
