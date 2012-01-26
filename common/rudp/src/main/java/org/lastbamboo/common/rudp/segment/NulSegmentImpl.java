package org.lastbamboo.common.rudp.segment;

import org.littleshoot.util.UInt;

/**
 * A NUL segment.
 */
public final class NulSegmentImpl extends AbstractSegment implements NulSegment
    {
    /**
     * Constructs a new NUL segment.
     * 
     * @param seqNum
     *      The sequence number.
     * @param ackNum
     *      The acknowledgment number.
     */
    public NulSegmentImpl
            (final UInt seqNum,
             final UInt ackNum)
        {
        super (seqNum, ackNum);
        }

    /**
     * {@inheritDoc}
     */
    public <T> T accept
            (final SegmentVisitor<T> visitor)
        {
        return visitor.visitNul (this);
        }
    }
