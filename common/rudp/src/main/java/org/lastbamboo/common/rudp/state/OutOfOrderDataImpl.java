package org.lastbamboo.common.rudp.state;

import org.littleshoot.util.UInt;

/**
 * An implementation of the out of order data interface.
 */
public class OutOfOrderDataImpl implements OutOfOrderData
    {
    /**
     * The data.
     */
    private final byte[] m_data;
    
    /**
     * The sequence number that was acknowledged out of order.
     */
    private final UInt m_seqNum;
    
    /**
     * Constructs a new out of order data object.
     * 
     * @param seqNum
     *      The sequence number that was acknowledged out of order.
     * @param data
     *      The data.
     */
    public OutOfOrderDataImpl
            (final UInt seqNum,
             final byte[] data)
        {
        super ();
        
        m_data = data;
        m_seqNum = seqNum;
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
    public UInt getSeqNum
            ()
        {
        return m_seqNum;
        }
    }
