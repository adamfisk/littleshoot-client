package org.lastbamboo.common.rudp.segment;

import org.littleshoot.util.UInt;

/**
 * A reliable UDP segment.
 */
public interface Segment
    {
    /**
     * Accepts a visitor to this segment.
     * 
     * @param <T>
     *      The return type of this visitor.
     *      
     * @param visitor
     *      The visitor.
     *      
     * @return
     *      The result of the visitation.
     */
    <T> T accept
            (SegmentVisitor<T> visitor);
    
    /**
     * Returns the acknowledgment number of this segment.
     * 
     * @return
     *      The acknowledgment number of this segment.
     */
    UInt getAckNum
            ();
    
    /**
     * Returns the sequence number of this segment.
     * 
     * @return
     *      The sequence number of this segment.
     */
    UInt getSeqNum
            ();
    }
