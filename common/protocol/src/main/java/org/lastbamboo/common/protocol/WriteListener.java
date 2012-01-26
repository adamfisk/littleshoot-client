package org.lastbamboo.common.protocol;


/**
 * Listener for write events.  This is called when a buffer or set of buffers
 * are successfully written to the network.
 */
public interface WriteListener
    {

    /**
     * Called when the write has been performed.
     * 
     * @param data The bean containing more data about the write.
     */
    void onWrite(final WriteData data);

    }
