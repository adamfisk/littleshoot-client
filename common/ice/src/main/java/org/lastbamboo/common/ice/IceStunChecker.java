package org.lastbamboo.common.ice;

import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.StunMessage;

/**
 * Interface for classes that perform ICE connectivity checks using STUN. 
 */
public interface IceStunChecker
    {

    /**
     * Writes a STUN binding request with the RTO value used for 
     * retransmissions explicitly set.
     * 
     * @param request The STUN binding request.
     * @param rto The value to use for RTO when calculating retransmission 
     * times.  Note this only applies to UDP.
     * @return The response message.
     */
    StunMessage write(BindingRequest request, long rto);

    /**
     * Cancels the existing STUN transaction.  The behavior for this is 
     * described in ICE section 7.2.1.4. on triggered checks.  From that 
     * section, cancellation:<p> 
     * 
     * "means that the agent will not retransmit the 
     * request, will not treat the lack of response to be a failure, but will 
     * wait the duration of the transaction timeout for a response."
     */
    void cancelTransaction();

    /**
     * Close any connections associated with the checker.
     */
    void close();

    }
