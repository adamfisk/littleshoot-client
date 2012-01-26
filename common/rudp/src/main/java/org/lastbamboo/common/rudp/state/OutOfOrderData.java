package org.lastbamboo.common.rudp.state;

import org.littleshoot.util.UInt;

/**
 * The interface to an object that represents data that was received out of
 * order.
 */
public interface OutOfOrderData
    {
    /**
     * Returns the data.
     * 
     * @return
     *      The data.
     */
    byte[] getData
            ();
    
    /**
     * Returns the sequence number that was acknowledged out of order.
     * 
     * @return
     *      The sequence number that was acknowledged out of order.
     */
    UInt getSeqNum
            ();
    }
