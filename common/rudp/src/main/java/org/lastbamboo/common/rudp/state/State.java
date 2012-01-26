package org.lastbamboo.common.rudp.state;

import org.lastbamboo.common.rudp.TimerId;
import org.lastbamboo.common.rudp.segment.Segment;
import org.littleshoot.util.Pair;

/**
 * A state for a reliable UDP connection.
 */
public interface State
    {
    /**
     * Accepts a visitor to this state.
     * 
     * @param <T>
     *      The type of the result of visitation.
     *      
     * @param visitor
     *      The visitor.
     *      
     * @return
     *      The result of the visitation.
     */
    <T> T accept
            (StateVisitor<T> visitor);
    
    /**
     * Attempts to close the connection and returns the next state.
     * 
     * @return
     *      The next state.
     */
    State close
            ();
    
    /**
     * Notifies this state that it has been entered by the state machine.
     */
    void entered
            ();
    
    /**
     * Notifies this state that it has been exited by the state machine.
     */
    void exited
            ();
    
    /**
     * Returns the next state as a result of processing a given segment.
     * 
     * @param segment
     *      The segment.
     *      
     * @return
     *      The next state.
     */
    State getNext
            (Segment segment);
    
    /**
     * Returns the next state as a result of an attempt to open the connection
     * while in this state.
     * 
     * @return
     *      The next state as a result of an attempt to open the connection
     *      while in this state.
     */
    State open
            ();
    
    /**
     * Returns the next state as a result of trying to send a message on the
     * connection while in this state.
     * 
     * @param data
     *      The data of the message to send.
     *      
     * @return
     *      A pair whose first element is the status of the send attempt and
     *      whose second element is the next state as a result of an attempt to
     *      send a message on the connection while in this state.
     */
    Pair<SendStatus,State> send
            (byte[] data);
    
    /**
     * Returns the next state as a result of trying to send a message on the
     * connection while in this state.
     * 
     * @param tryTime
     *      The time to wait if our send buffer is currently full.  In the
     *      default <code>send</code>, an exception will immediately be thrown
     *      if our send buffer is currently full.  In this case, we will try
     *      for <code>waitTime</code> milliseconds before throwing the exception
     *      indicating that our send buffers are full.
     * @param data
     *      The data of the message to send.
     *      
     * @return
     *      The next state as a result of an attempt to send a message on the
     *      connection while in this state.
     */
    State send
            (long tryTime,
             byte[] data);
    
    /**
     * Returns the next state as a result of a timer expiration.
     * 
     * @param timerId
     *      The identifier of the timer that expired.
     *      
     * @return
     *      The next state as a result of the timer expiration.
     */
    State timerExpired
            (TimerId timerId);
    }
