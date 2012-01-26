package org.lastbamboo.common.rudp.segment;

import org.littleshoot.util.UInt;

/**
 * A RST segment.
 */
public final class RstSegmentImpl extends AbstractSegment implements RstSegment
    {
    /**
     * Whether this RST segment is also an ack.
     */
    private final boolean m_ack;
    
    /**
     * Constructs a new RST segment.
     * 
     * @param seqNum
     *      The sequence number.
     * @param ackNum
     *      The acknowledgment number.
     * @param ack
     *      Whether this RST segment is also an ack.
     */
    public RstSegmentImpl
            (final UInt seqNum,
             final UInt ackNum,
             final boolean ack)
        {
        super (seqNum, ackNum);
        
        m_ack = ack;
        }

    /**
     * Constructs a new RST segment.
     * 
     * @param seqNum
     *      The sequence number.
     * @param ackNum
     *      The acknowledgment number.
     */
    public RstSegmentImpl
            (final UInt seqNum,
             final UInt ackNum)
        {
        this (seqNum, ackNum, false);
        }

    /**
     * {@inheritDoc}
     */
    public <T> T accept
            (final SegmentVisitor<T> visitor)
        {
        return visitor.visitRst (this);
        }

    /**
     * {@inheritDoc}
     */
    public boolean isAck
            ()
        {
        return m_ack;
        }
    }
