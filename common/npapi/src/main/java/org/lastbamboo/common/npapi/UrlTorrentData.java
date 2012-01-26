package org.lastbamboo.common.npapi;


/**
 * Interface for classes that can retrieve torrent data for URLs.
 */
public interface UrlTorrentData
    {
    
    /**
     * Retrieves torrent data for the specified URL.
     * 
     * @param url The URL of the torrent data.
     * @return The torrent data.
     */
    NpapiStreamData getTorrentData(String url);
    }
