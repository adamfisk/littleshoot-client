package org.lastbamboo.common.rudp;

import java.net.InetSocketAddress;

/**
 * An identifier for a reliable UDP listening connection.
 */
public interface RudpListeningConnectionId
    {
    /**
     * Returns the local address on which the connection is listening.
     * 
     * @return
     *      The local address on which the connection is listening.
     */
    InetSocketAddress getLocalAddress ();
    }
