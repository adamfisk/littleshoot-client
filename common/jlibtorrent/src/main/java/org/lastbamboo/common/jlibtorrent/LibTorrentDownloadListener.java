package org.lastbamboo.common.jlibtorrent;

import java.net.URI;

/**
 * Listener for LibTorrent events.
 */
public interface LibTorrentDownloadListener
    {

    void onState(int state);

    void onFailure();

    void onComplete();

    /**
     * Called to verify the bytes returned by the stream equaled the bytes
     * saved to disk. Simply catches any bugs that might be present in the
     * streaming code.
     * 
     * @param sha1 The SHA-1 streamed.
     */
    void onVerifyStream(URI sha1);

    }
