package org.lastbamboo.common.rudp.segment;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.littleshoot.util.UInt;

/**
 * An EACK segment.
 */
public final class EackSegmentImpl extends AbstractSegment
        implements EackSegment
    {
    /**
     * The data of this EACK segment.
     */
    private final byte[] m_data;
    
    /**
     * The collection of received sequence numbers.
     */
    private final Collection<UInt> m_receivedSeqNums;

    /**
     * Constructs a new EACK segment.
     * 
     * @param seqNum
     *      The sequence number.
     * @param ackNum
     *      The acknowledgment number.
     * @param receivedSeqNums
     *      The collection of received sequence numbers.
     * @param data
     *      The data of this ACK segment.
     */
    public EackSegmentImpl
            (final UInt seqNum,
             final UInt ackNum,
             final Collection<UInt> receivedSeqNums,
             final byte[] data)
        {
        super (seqNum, ackNum);
        
        m_receivedSeqNums = receivedSeqNums;
        m_data = data;
        }
    
    /**
     * {@inheritDoc}
     */
    public <T> T accept
            (final SegmentVisitor<T> visitor)
        {
        return visitor.visitEack (this);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals
            (final Object other)
        {
        if (other instanceof EackSegment)
            {
            final EackSegment otherEack = (EackSegment) other;
            
            return super.equals (other) &&
                    Arrays.equals (otherEack.getData (), m_data) &&
                    otherEack.getReceivedSeqNums ().equals (m_receivedSeqNums);
            }
        else
            {
            return false;
            }
        }

    /**
     * {@inheritDoc}
     */
    public byte[] getData
            ()
        {
        return m_data;
        }
    
    /**
     * {@inheritDoc}
     */
    public Collection<UInt> getReceivedSeqNums
            ()
        {
        return m_receivedSeqNums;
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode
            ()
        {
        return new HashCodeBuilder (104003, 104801).
                append (super.hashCode ()).
                append (m_data).
                append (m_receivedSeqNums.hashCode ()).toHashCode ();
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString
            ()
        {
        final StringBuilder builder = new StringBuilder ();
        
        int i = 0;
        
        for (final UInt receivedSeqNum : m_receivedSeqNums)
            {
            builder.append (receivedSeqNum.toLong ());
            
            if (++i != m_receivedSeqNums.size ())
                {
                builder.append (", ");
                }
            }
        
        return "EACK [seqNum: " + super.getSeqNum ().toLong () + ", " +
                    "ackNum: " + super.getAckNum ().toLong () + ", " +
                    "eackSeqNums: " + builder.toString () + "]";
        }
    }
