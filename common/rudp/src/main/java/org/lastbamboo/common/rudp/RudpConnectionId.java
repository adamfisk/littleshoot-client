package org.lastbamboo.common.rudp;

import java.net.InetSocketAddress;

/**
 * The interface to an object that identifies a RUDP connection.
 */
public interface RudpConnectionId
    {
    /**
     * Returns the local address used for the connection specified by this
     * identifier.
     * 
     * @return
     *      The local address used for the connection specified by this
     *      identifier.
     */
    InetSocketAddress getLocalAddress
            ();
    
    /**
     * Returns the remote address used for the connection specified by this
     * identifier.
     * 
     * @return
     *      The remote address used for the connection specified by this
     *      identifier.
     */
    InetSocketAddress getRemoteAddress
            ();
    }
