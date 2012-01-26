package org.lastbamboo.common.rudp.segment;

/**
 * An ACK segment.
 */
public interface AckSegment extends Segment
    {
    /**
     * Returns the data in this ACK segment.
     * 
     * @return
     *      The data in this ACK segment.
     */
    byte[] getData
            ();
    }
