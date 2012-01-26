package org.lastbamboo.common.jmx.client;

import java.net.InetAddress;

/**
 * Interface for classes that wish to listen for server status events. 
 */
public interface ServerStatusListener
    {
    
    /**
     * Notifies the listener about the online status of a server.
     * 
     * @param server The address of the server.
     * @param online The server's online status.
     */
    void onOnline(InetAddress server, boolean online);

    }
