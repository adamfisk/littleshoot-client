package org.lastbamboo.common.stun.stack.transaction;

import org.lastbamboo.common.stun.stack.message.StunMessage;

/**
 * Interface for classes wishing to listen for STUN transtaction events.
 * 
 * @param <T> The return type of the event methods.
 */
public interface StunTransactionListener<T>
    {

    /**
     * Called when the transaction completed normally with a successful
     * binding response.
     * 
     * @param message The binding request.
     * @param response The binding response.
     * @return The return type of the event methods.
     */
    T onTransactionSucceeded(StunMessage message, StunMessage response);

    /**
     * Called when the transaction failed with an error response, a timeout,
     * or for any other reason. 
     * 
     * @param request The original request.
     * @param response The binding response.
     * @return The return type of the event methods.
     */
    T onTransactionFailed(StunMessage request, StunMessage response);

    }
