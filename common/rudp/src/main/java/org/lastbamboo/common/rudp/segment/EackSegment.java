package org.lastbamboo.common.rudp.segment;

import java.util.Collection;

import org.littleshoot.util.UInt;

/**
 * An EACK segment.
 */
public interface EackSegment extends Segment
    {
    /**
     * Returns the data in this ACK segment.
     * 
     * @return
     *      The data in this ACK segment.
     */
    byte[] getData
            ();
    
    /**
     * Returns the collection of received sequence numbers.
     * 
     * @return
     *      The collection of received sequence numbers.
     */
    Collection<UInt> getReceivedSeqNums
            ();
    }
