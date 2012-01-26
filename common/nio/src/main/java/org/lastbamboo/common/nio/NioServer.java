package org.lastbamboo.common.nio;

import java.io.IOException;

/**
 * Interface for NIO servers.
 */
public interface NioServer
    {
    
    /**
     * Starts the server.
     * 
     * @throws IOException If there's an IO error starting the server.
     */
    void startServer() throws IOException;
    
    /**
     * Closes the server.
     */
    void close();
    }
