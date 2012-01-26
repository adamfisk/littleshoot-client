package org.lastbamboo.common.rudp.segment;

import java.util.Arrays;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.littleshoot.util.UInt;

/**
 * An ACK segment.
 */
public final class AckSegmentImpl extends AbstractSegment implements AckSegment
    {
    /**
     * The data of this ACK segment.
     */
    private final byte[] m_data;
    
    /**
     * Constructs a new ACK segment.
     * 
     * @param seqNum
     *      The sequence number.
     * @param ackNum
     *      The acknowledgment number.
     * @param data
     *      The data of this ACK segment.
     */
    public AckSegmentImpl (final UInt seqNum, final UInt ackNum,
        final byte[] data)
        {
        super (seqNum, ackNum);
        
        // TODO: Check that data size is small enough.
        
        m_data = data;
        }

    /**
     * {@inheritDoc}
     */
    public <T> T accept (final SegmentVisitor<T> visitor)
        {
        return visitor.visitAck (this);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals (final Object other)
        {
        if (other instanceof AckSegment)
            {
            final AckSegment otherAck = (AckSegment) other;
            
            return super.equals (other) &&
                    Arrays.equals (otherAck.getData (), m_data);
            }
        else
            {
            return false;
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public byte[] getData ()
        {
        return m_data;
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode ()
        {
        return new HashCodeBuilder (602647, 603401).
                append (super.hashCode ()).
                append (m_data).
                toHashCode ();
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString ()
        {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" with date length: ");
        sb.append(this.m_data.length);
        sb.append(", sequence number: ");
        sb.append(getSeqNum ().toLong ());
        sb.append(", ack number: ");
        sb.append(getAckNum ().toLong ());
        return sb.toString();
        /*
        final String dataString;
        
        if (m_data.length > 0)
            {
            final StringBuilder builder = new StringBuilder ();
            
            builder.append ('[');
            
            for (int i = 0; i < m_data.length - 1; ++i)
                {
                builder.append (m_data[i]);
                builder.append (", ");
                }
            
            builder.append (m_data[m_data.length - 1]);
            builder.append (']');
            
            dataString = builder.toString ();
            }
        else
            {
            dataString = "[]";
            }
                            
        return "ACK [seqNum: " + super.getSeqNum ().toLong () + ", " +
                    "ackNum: " + super.getAckNum ().toLong ()  + ", " +
                    "data.length: " + m_data.length + ", " +
                    "data: " + m_data + ":" + dataString + "]";
                    */
        }
    }
