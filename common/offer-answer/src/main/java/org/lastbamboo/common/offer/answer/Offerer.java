package org.lastbamboo.common.offer.answer;

import java.io.IOException;
import java.net.URI;

/**
 * Interface for classes that send offers to other peers.
 */
public interface Offerer 
    {

    /**
     * Send an offer to the specified URI. Implementations of this method
     * SHOULD NOT BLOCK.
     * 
     * @param uri The URI indicating the user/machine to send the offer to.
     * @param offer The raw offer data itself.
     * @param transactionListener The listener for transaction success or
     * failure.
     * @throws IOException If there's an IO error sending the offer.
     */
    void offer(URI uri, byte[] offer, 
        OfferAnswerTransactionListener transactionListener) throws IOException;

    }
