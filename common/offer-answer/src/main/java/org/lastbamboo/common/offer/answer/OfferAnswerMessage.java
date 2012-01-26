package org.lastbamboo.common.offer.answer;

import org.littleshoot.mina.common.ByteBuffer;

/**
 * Interface for messages in an offer/answer exchange. These are typically a
 * part of a transaction and contain a message body.
 */
public interface OfferAnswerMessage 
    {

    /**
     * The key to use for transactions.
     * @return The unique key for this transaction.
     */
    String getTransactionKey();

    /**
     * Returns the body of the message.
     * 
     * @return The body of the message.
     */
    ByteBuffer getBody();
    
    }
