package org.lastbamboo.common.rudp.state;

import org.lastbamboo.common.rudp.TimerId;
import org.lastbamboo.common.rudp.segment.Segment;
import org.littleshoot.util.UInt;

/**
 * The facade interface that handles the stateful output of connection state
 * handling.
 */
public interface StateOutput
    {
    /**
     * Acknowledges a sequence number of a segment that was previously
     * transmitted.  This acknowledgment is considered cumulative.  All segments
     * with lesser sequence numbers are also acknowledged.
     * 
     * @param seqNum
     *      The sequence number.
     */
    void acknowledge
            (UInt seqNum);
    
    /**
     * Acknowledges a sequence number of a segment that was previously
     * transmitted.
     * 
     * @param seqNum
     *      The sequence number.
     */
    void acknowledgeOne
            (UInt seqNum);
    
    /**
     * Cancels a given timer.
     * 
     * @param timerId
     *      The identifier of the timer.
     */
    void cancelTimer
            (TimerId timerId);
    
    /**
     * Sets a timer.
     * 
     * @param timerId
     *      The identifier of the timer to set.
     * @param delay
     *      The delay before the timer expires in milliseconds.
     */
    void setTimer
            (TimerId timerId,
             long delay);
    
    /**
     * Stops all retransmissions.
     */
    void stopRetransmissions
            ();
    
    /**
     * Transmits a segment.
     * 
     * @param segment
     *      The segment.
     */
    void transmit
            (Segment segment);
    
    /**
     * Queues a received message for the client.
     * 
     * @param message
     *      The message.
     */
    void queue
            (byte[] message);
    }
