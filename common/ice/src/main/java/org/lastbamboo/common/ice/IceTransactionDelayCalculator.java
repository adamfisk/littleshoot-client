package org.lastbamboo.common.ice;

/**
 * Calculates the value for ICE Ta.  The given formula for Ta is:
 * 
 *  For each media stream i:
 *  
 *  Ta_i = (stun_packet_size / rtp_packet_size) * rtp_ptime
 *  
 *  
 *                          1
 *    Ta = MAX (20ms, ------------------- )
 *                          k
 *                        ----
 *                        \        1
 *                         >    ------
 *                        /       Ta_i
 *                        ----
 *                         i=1
 */
public class IceTransactionDelayCalculator
    {

    private IceTransactionDelayCalculator()
        {
        // Should never be constructed.
        }

    /**
     * Calculates the transaction delay for a single media stream. 
     * 
     * @param Ta_i The calculated transaction delay for the media stream in
     * question.
     * @param k The number of media streams.
     * @return The delay to use.
     */
    public static int calculateDelay(final int Ta_i, final int k)
        {
        return Math.max(20, streamSpecificDelay(Ta_i, k));
        }
    
    private static int streamSpecificDelay(final double Ta_i, final int k)
        {
        double sum = 0L;
        for (int i = 1; i <= k; i++)
            {
            sum += (1/Ta_i);
            }
        
        double finalValue = 1/sum;
        return (int) Math.ceil(finalValue);
        }
    }
