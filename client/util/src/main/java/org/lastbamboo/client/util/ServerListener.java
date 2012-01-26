package org.lastbamboo.client.util;

import java.util.EventListener;

/**
 * Interface for listeners for events on the local server.
 */
public interface ServerListener extends EventListener
    {
    
    /**
     * Called when the server is running.
     */
    void serverRunning();
    }
