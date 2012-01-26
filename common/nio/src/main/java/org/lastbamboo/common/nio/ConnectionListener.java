package org.lastbamboo.common.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Listener for receiving connection events.
 */
public interface ConnectionListener 
    {
    /**
     * Called when the connection is fully established.  
     * @param sc The newly connected socket.
     */
    void onConnect(final SocketChannel sc);

    /**
     * Called when a connection to the specified host has failed for any reason.
     * @param socketAddress The address and port of the host we could not 
     * connect to.
     */
    void onConnectFailed(final InetSocketAddress socketAddress);
    }
