package org.lastbamboo.common.nio;

import java.io.IOException;

/**
 * Interface for non-blocking socket connections.
 */
public interface Connector extends SelectorHandler
    {
    
    /**
     * Starts a non-blocking connection attempt.
     * 
     * @throws IOException If we could not open a channel and register it.
     */
    void connect() throws IOException;

    /**
     * Closes the connection.
     */
    void close();

    }