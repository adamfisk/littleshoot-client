package org.lastbamboo.common.rudp.segment;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.littleshoot.util.UInt;
import org.littleshoot.util.UShort;

/**
 * A SYN segment.
 */
public class SynSegmentImpl extends AbstractSegment implements SynSegment
    {
    /**
     * Whether this SYN segment is also an ack.
     */
    private final boolean m_ack;
    
    /**
     * The maximum number of outstanding segments.
     */
    private final UShort m_maxOutstanding;
    
    /**
     * The maximum segment size.
     */
    private final UShort m_maxSegmentSize;
    
    /**
     * Constructs this SYN segment.
     * 
     * @param seqNum
     *      The sequence number.
     * @param ackNum
     *      The acknowledgment number.
     * @param ack
     *      Whether this SYN segment is also an ack.
     * @param maxOutstanding
     *      The maximum number of outstanding segments.
     * @param maxSegmentSize 
     *      The maximum segment size.
     */
    public SynSegmentImpl
            (final UInt seqNum,
             final UInt ackNum,
             final boolean ack,
             final UShort maxOutstanding,
             final UShort maxSegmentSize)
        {
        super (seqNum, ackNum);
        
        m_ack = ack;
        m_maxOutstanding = maxOutstanding;
        m_maxSegmentSize = maxSegmentSize;
        }
    
    /**
     * Constructs this SYN segment.
     * 
     * @param seqNum
     *      The sequence number.
     * @param ackNum
     *      The acknowledgment number.
     * @param maxOutstanding
     *      The maximum number of outstanding segments.
     * @param maxSegmentSize 
     *      The maximum segment size.
     */
    public SynSegmentImpl
            (final UInt seqNum,
             final UInt ackNum,
             final UShort maxOutstanding,
             final UShort maxSegmentSize)
        {
        this (seqNum, ackNum, false, maxOutstanding, maxSegmentSize);
        }
    
    /**
     * {@inheritDoc}
     */
    public <T> T accept
            (final SegmentVisitor<T> visitor)
        {
        return visitor.visitSyn (this);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals
            (final Object other)
        {
        if (other instanceof SynSegment)
            {
            final SynSegment otherSyn = (SynSegment) other;
            
            return super.equals (other) &&
                    otherSyn.getMaxOutstanding ().equals (m_maxOutstanding) &&
                    otherSyn.getMaxSegmentSize ().equals (m_maxSegmentSize);
            }
        else
            {
            return false;
            }
        }

    /**
     * {@inheritDoc}
     */
    public UShort getMaxOutstanding
            ()
        {
        return m_maxOutstanding;
        }
    
    /**
     * {@inheritDoc}
     */
    public UShort getMaxSegmentSize
            ()
        {
        return m_maxSegmentSize;
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode
            ()
        {
        return new HashCodeBuilder (203417, 203627).
                append (super.hashCode ()).
                append (m_maxOutstanding.hashCode ()).
                append (m_maxSegmentSize.hashCode ()).
                toHashCode ();
        }
    
    /**
     * {@inheritDoc}
     */
    public boolean isAck
            ()
        {
        return m_ack;
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString
            ()
        {
        final String ack = m_ack ? "/ACK" : "";
        
        return "SYN" + ack + " [seqNum: " + super.getSeqNum ().toLong () +
                    ", " + "ackNum: " + super.getAckNum ().toLong () + "]";
        }
    }
