package org.lastbamboo.common.ice;

import java.net.Socket;

/**
 * Listener for UDP socket events.
 */
public interface UdpSocketListener
    {

    /**
     * Called when an UDP socket has successfully connected.
     * 
     * @param sock The connected UDP socket.
     */
    void onUdpConnect(Socket sock);

    }
