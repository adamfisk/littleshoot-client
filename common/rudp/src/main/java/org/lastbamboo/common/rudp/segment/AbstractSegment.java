package org.lastbamboo.common.rudp.segment;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.littleshoot.util.UInt;

/**
 * An abstract base class to help implement segments.
 */
public abstract class AbstractSegment implements Segment
    {
    /**
     * The acknowledgment number.
     */
    private final UInt m_ackNum;
    
    /**
     * The sequence number.
     */
    private final UInt m_seqNum;
    
    /**
     * Constructs this abstract segment.
     * 
     * @param seqNum
     *      The sequence number.
     * @param ackNum
     *      The acknowledgment number.
     */
    protected AbstractSegment
            (final UInt seqNum,
             final UInt ackNum)
        {
        m_seqNum = seqNum;
        m_ackNum = ackNum;
        }
    
    /**
     * {@inheritDoc}
     */
    public UInt getAckNum
            ()
        {
        return m_ackNum;
        }
    
    /**
     * {@inheritDoc}
     */
    public UInt getSeqNum
            ()
        {
        return m_seqNum;
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals
            (final Object other)
        {
        if (other instanceof Segment)
            {
            final Segment otherSegment = (Segment) other;
            
            return otherSegment.getAckNum ().equals (m_ackNum) &&
                    otherSegment.getSeqNum ().equals (m_seqNum);
            }
        else
            {
            return false;
            }
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode
            ()
        {
        return new HashCodeBuilder (52009, 52067).
                append (m_ackNum.hashCode ()).
                append (m_seqNum.hashCode ()).
                toHashCode ();
        }
    }
