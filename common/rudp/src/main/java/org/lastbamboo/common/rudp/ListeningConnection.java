package org.lastbamboo.common.rudp;

import java.net.InetSocketAddress;

import org.lastbamboo.common.rudp.segment.Segment;
import org.littleshoot.util.F1;
import org.littleshoot.util.Pair;

/**
 * A listening connection.  This is used to handle a passively opened
 * connection.
 */
public interface ListeningConnection
    {
    /**
     * Handles a segment delivered to this connection.
     * 
     * @param remoteAddress
     *      The remote address from which the segment came.
     * @param segment
     *      The segment.
     * @param writeF
     *      The function used to send UDP messages.
     */
    void handle
            (InetSocketAddress remoteAddress,
             Segment segment,
             F1<Segment,Void> writeF);
    
    /**
     * Accepts a connection made on this listening connection.  This call will
     * block until a connection is made.
     * 
     * @param openCallback
     *      The callback to call when the accepted connection goes into the
     *      OPEN state.  The callback is called with the socket address of the
     *      remote end of the accepted connection.
     * @param closedCallback
     *      The callback to call when the accepted connection goes into the
     *      CLOSED state.
     *      
     * @return
     *      The socket address of the remote end of the connection and the
     *      accepted connection itself.
     */
    Pair<InetSocketAddress,Connection> accept
            (F1<InetSocketAddress,Void> openCallback,
             Runnable closedCallback);
    }
