package org.lastbamboo.common.rudp.segment;

/**
 * A RST segment.
 */
public interface RstSegment extends Segment
    {
    // No extra operations.
    /**
     * Returns whether this RST segment is also an ACK, since ACKs can be
     * piggybacked.
     * 
     * @return
     *      Whether this RST segment is also an ACK.
     */
    boolean isAck
            ();
    }
