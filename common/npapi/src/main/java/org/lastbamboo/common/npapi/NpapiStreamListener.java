package org.lastbamboo.common.npapi;

/**
 * Class that listens for NPAPI streams.
 */
public interface NpapiStreamListener
    {

    /**
     * Notifies the listener we've received a stream.
     * @param data The stream data.
     */
    void onStream(NpapiStreamData data);

    }
