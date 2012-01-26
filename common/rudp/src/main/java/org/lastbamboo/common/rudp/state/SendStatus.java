package org.lastbamboo.common.rudp.state;

/**
 * The result of a send operation.
 */
public enum SendStatus
    {
    /**
     * Success.
     */
    SUCCESS,
    
    /**
     * Failure because the send buffer is full.
     */
    SEND_BUFFER_FULL
    }
