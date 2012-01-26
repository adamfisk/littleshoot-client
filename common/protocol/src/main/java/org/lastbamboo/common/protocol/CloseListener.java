package org.lastbamboo.common.protocol;

import java.util.EventListener;

/**
 * Listener for close events.
 */
public interface CloseListener extends EventListener
    {

    /**
     * Notifies the listener that the specified reader/writer has closed.
     * @param readerWriter The reader/writer that has closed.
     */
    void onClose(final ReaderWriter readerWriter);

    }
