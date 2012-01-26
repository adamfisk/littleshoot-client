package org.lastbamboo.common.rudp.state;

import java.util.Collection;

import org.littleshoot.util.UInt;
import org.littleshoot.util.UShort;

/**
 * A connection record for a reliable UDP connection.
 */
public interface ConnectionRecord
    {
    /**
     * Returns the initial sequence number received from the remote end.
     * 
     * @return
     *      The initial sequence number received from the remote end.
     */
    UInt getInitialReceiveSeqNum
            ();
    
    /**
     * Returns the initial sequence number used by the local end.
     * 
     * @return
     *      The initial sequence number used by the local end.
     */
    UInt getInitialSeqNum
            ();
    
    /**
     * Returns the last received sequence number from the remote end.
     * 
     * @return
     *      The last received sequence number from the remote end.
     */
    UInt getLastReceivedSeqNum
            ();
    
    /**
     * Returns the maximum number of received segments to buffer.
     * 
     * @return
     *      The maximum number of received segments to buffer.
     */
    UShort getMaxBuffered
            ();
    
    /**
     * Returns the maximum number of outstanding segments to send that have not
     * yet been acknowledged.
     * 
     * @return
     *      The maximum number of outstanding segments to send that have not yet
     *      been acknowledged.
     */
    UShort getMaxOutstanding
            ();
    
    /**
     * Returns the maximum size of segments that can be received.
     * 
     * @return
     *      The maximum size of segments that can be received.
     */
    UShort getMaxReceiveSegmentSize
            ();
    
    /**
     * Returns the maximum size of segments that can be sent in bytes.
     * 
     * @return
     *      The maximum size of segments that can be sent in bytes.
     */
    UShort getMaxSendSegmentSize
            ();
    
    /**
     * Returns the next sequence number to use.
     * 
     * @return
     *      The next sequence number to use.
     */
    UInt getNextSeqNum
            ();
    
    /**
     * Returns the oldest sequence number that has not yet been acknowledged.
     * 
     * @return
     *      The oldest sequence number that has not yet been acknowledged.
     */
    UInt getOldestUnackedSeqNum
            ();
    
    /**
     * Returns the acknowledgments that have been received out of order.  We
     * maintain the data in these acknowledgements as well, since we will give
     * it to the user once the proper ordering is restored.
     * 
     * @return
     *      The acknowledgments that have been received out of order.
     */
    Collection<OutOfOrderData> getOutOfSequenceAcks
            ();
    }
