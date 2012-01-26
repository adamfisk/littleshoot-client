package org.lastbamboo.common.offer.answer;


/**
 * Interface for "transaction user" (TU) classes wishing to listen for 
 * transaction events.
 */
public interface OfferAnswerTransactionListener
    {

    /**
     * Called when the transaction completed normally with a 200 OK response.
     * 
     * @param message The message that transitioned the transaction to a
     * successful state.
     */
    void onTransactionSucceeded(OfferAnswerMessage message);

    /**
     * Called when the transaction failed with an error response, a timeout,
     * or for any other reason.  This is called really when we receive
     * anything but a 200 OK response.
     * 
     * @param message The message that transitioned the transaction to a
     * failed state.
     */
    void onTransactionFailed(OfferAnswerMessage message);

    }
