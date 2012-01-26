package org.lastbamboo.common.rudp.state;

import java.util.Collection;

import org.littleshoot.util.UInt;
import org.littleshoot.util.UShort;

/**
 * An implementation of the connection record interface.
 */
public class ConnectionRecordImpl implements ConnectionRecord
    {
    /**
     * The initial sequence number received from the remote end.
     */
    private final UInt m_initialReceiveSeqNum;
    
    /**
     * The initial sequence number used by the local end.
     */
    private final UInt m_initialSeqNum;
    
    /**
     * The last received sequence number from the remote end.
     */
    private final UInt m_lastReceivedSeqNum;
    
    /**
     * The maximum number of received segments to buffer.
     */
    private final UShort m_maxBuffered;
    
    /**
     * The maximum number of outstanding segments to send that have not yet been
     * acknowledged.
     */
    private final UShort m_maxOutstanding;
    
    /**
     * The maximum size of segments that can be received.
     */
    private final UShort m_maxReceiveSegmentSize;
    
    /**
     * The maximum size of segments that can be sent in bytes.
     */
    private final UShort m_maxSendSegmentSize;
    
    /**
     * The next sequence number to use.
     */
    private final UInt m_nextSeqNum;
    
    /**
     * The oldest sequence number that has not yet been acknowledged.
     */
    private final UInt m_oldestUnackedSeqNum;
    
    /**
     * The acknowledgments that have been received out of order.
     */
    private final Collection<OutOfOrderData> m_outOfSequenceAcks;
    
    /**
     * Constructs a new connection record.
     * 
     * @param initialReceiveSeqNum
     *      The initial sequence number received from the remote end.
     * @param initialSeqNum
     *      The initial sequence number used by the local end.
     * @param lastReceivedSeqNum
     *      The last received sequence number from the remote end.
     * @param maxBuffered
     *      The maximum number of received segments to buffer.
     * @param maxOutstanding
     *      The maximum number of outstanding segments to send that have not yet
     *      been acknowledged.
     * @param maxReceiveSegmentSize
     *      The maximum size of segments that can be received.
     * @param maxSendSegmentSize
     *      The maximum size of segments that can be sent in bytes.
     * @param nextSeqNum
     *      The next sequence number to use.
     * @param oldestUnackedSeqNum
     *      The oldest sequence number that has not yet been acknowledged.
     * @param outOfSequenceAcks
     *      The acknowledgments that have been received out of order.
     */
    public ConnectionRecordImpl
            (final UInt initialReceiveSeqNum,
             final UInt initialSeqNum,
             final UInt lastReceivedSeqNum,
             final UShort maxBuffered,
             final UShort maxOutstanding,
             final UShort maxReceiveSegmentSize,
             final UShort maxSendSegmentSize,
             final UInt nextSeqNum,
             final UInt oldestUnackedSeqNum,
             final Collection<OutOfOrderData> outOfSequenceAcks)
        {
        m_initialReceiveSeqNum = initialReceiveSeqNum;
        m_initialSeqNum = initialSeqNum;
        m_lastReceivedSeqNum = lastReceivedSeqNum;
        m_maxBuffered = maxBuffered;
        m_maxOutstanding = maxOutstanding;
        m_maxReceiveSegmentSize = maxReceiveSegmentSize;
        m_maxSendSegmentSize = maxSendSegmentSize;
        m_nextSeqNum = nextSeqNum;
        m_oldestUnackedSeqNum = oldestUnackedSeqNum;
        m_outOfSequenceAcks = outOfSequenceAcks;
        }

    /**
     * {@inheritDoc}
     */
    public UInt getInitialReceiveSeqNum
            ()
        {
        return m_initialReceiveSeqNum;
        }

    /**
     * {@inheritDoc}
     */
    public UInt getInitialSeqNum
            ()
        {
        return m_initialSeqNum;
        }

    /**
     * {@inheritDoc}
     */
    public UInt getLastReceivedSeqNum
            ()
        {
        return m_lastReceivedSeqNum;
        }

    /**
     * {@inheritDoc}
     */
    public UShort getMaxBuffered
            ()
        {
        return m_maxBuffered;
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
    public UShort getMaxReceiveSegmentSize
            ()
        {
        return m_maxReceiveSegmentSize;
        }

    /**
     * {@inheritDoc}
     */
    public UShort getMaxSendSegmentSize
            ()
        {
        return m_maxSendSegmentSize;
        }

    /**
     * {@inheritDoc}
     */
    public UInt getNextSeqNum
            ()
        {
        return m_nextSeqNum;
        }

    /**
     * {@inheritDoc}
     */
    public UInt getOldestUnackedSeqNum
            ()
        {
        return m_oldestUnackedSeqNum;
        }

    /**
     * {@inheritDoc}
     */
    public Collection<OutOfOrderData> getOutOfSequenceAcks
            ()
        {
        return m_outOfSequenceAcks;
        }
    }
