package org.lastbamboo.common.rudp;

import java.net.InetSocketAddress;

import org.lastbamboo.common.rudp.segment.Segment;
import org.lastbamboo.common.rudp.segment.SynSegment;
import org.littleshoot.util.F1;

/**
 * A queued connection on a listening connection.  These are used to track
 * connections that have not yet been accepted.
 */
public interface QueuedConnection
    {
    /**
     * Returns the local address of this connection.
     * 
     * @return
     *      The local address of this connection.
     */
    InetSocketAddress getLocalAddress
            ();
    
    /**
     * Returns the remote address of this connection.
     * 
     * @return
     *      The remote address of this connection.
     */
    InetSocketAddress getRemoteAddress
            ();
    
    /**
     * Returns the SYN segment used to initiate the connection.
     * 
     * @return
     *      The SYN segment used to initiate the connection.
     */
    SynSegment getSyn
            ();
    
    /**
     * Returns the function used to write UDP messages for this connection.
     * 
     * @return
     *      The function used to write UDP messages for this connection.
     */
    F1<Segment,Void> getWriteF
            ();
    }
