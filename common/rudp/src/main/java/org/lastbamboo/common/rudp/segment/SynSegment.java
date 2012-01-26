package org.lastbamboo.common.rudp.segment;

import org.littleshoot.util.UShort;

/**
 * A SYN segment.
 */
public interface SynSegment extends Segment
    {
    /**
     * Returns the maximum number of outstanding segments.
     * 
     * @return
     *      The maximum number of outstanding segments.
     */
    UShort getMaxOutstanding
            ();
    
    /**
     * Returns the maximum segment size.
     * 
     * @return
     *      The maximum segment size.
     */
    UShort getMaxSegmentSize
            ();
    
    /**
     * Returns whether this SYN segment is also an ACK, since ACKs can be
     * piggybacked.
     * 
     * @return
     *      Whether this SYN segment is also an ACK.
     */
    boolean isAck
            ();
    }
