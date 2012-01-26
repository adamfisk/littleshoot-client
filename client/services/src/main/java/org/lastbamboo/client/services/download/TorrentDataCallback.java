package org.lastbamboo.client.services.download;

import java.io.File;

/**
 * Callback for loaded torrent data.
 */
public interface TorrentDataCallback
    {

    void onData(String decodedUri, File torrentFile, File incompleteDir, 
        int torrentState);

    }
