package org.lastbamboo.common.rudp;

import org.lastbamboo.common.rudp.segment.Segment;
import org.littleshoot.util.UInt;

/**
 * The interface to an object that may retransmit segments when they are not
 * acknowledged.
 */
public interface Retransmitter
    {
    /**
     * Notifies this retransmitter that a particular segment identified by
     * sequence number has been acknowledged.  This acknowledgment is considered
     * cumulative.  All segments with lesser sequence numbers are also
     * acknowledged.
     * 
     * @param seqNum
     *      The sequence number of the segment that has been acknowledged.
     */
    void acknowledged
            (UInt seqNum);
    
    /**
     * Cancels all of the currently active segments waiting for retransmission.
     * This is useful when we know that the receiving end is no longer
     * listening.
     */
    void cancelActive
            ();
    
    /**
     * Notifies this retransmitter that a particular segment identified by
     * sequence number has been acknowledged.  This notification is not
     * cumulative.  It acknowledges just one segment which may be out of order.
     * 
     * @param seqNum
     *      The sequence number of the segment that has been acknowledged.
     */
    void oneAcknowledged
            (UInt seqNum);
    
    /**
     * Transmits a segment.
     * 
     * @param segment
     *      The segment to transmit.
     */
    void transmit
            (Segment segment);
    }
