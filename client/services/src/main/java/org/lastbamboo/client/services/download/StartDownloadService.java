package org.lastbamboo.client.services.download;

import java.io.File;
import java.net.URI;
import java.util.Map;

/**
 * Service for managing a single download.
 */
public interface StartDownloadService
    {

    void download(URI uri, URI expectedSha1, String name, 
        long size, Map<String, String> paramMap, 
        Map<String, String> cookieMap);

    void limeWireDownload(File completeFile, URI sha1, long size, 
        Map<String, String> paramMap, Map<String, String> cookieMap);

    void bitTorrentDownload(String uri, 
        Map<String, String> paramMap, Map<String, String> cookieMap);

    }
