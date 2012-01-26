package org.lastbamboo.common.nio;

import java.nio.channels.SelectionKey;

/**
 * Interface for classes that can handle different events from the selector.
 */
public interface SelectorHandler 
    {
    
    /**
     * Notifies the handler that it should handle the specified key.
     * @param sk The key to handle.
     */
    void handleKey(final SelectionKey sk);
    }
