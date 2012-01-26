package org.lastbamboo.common.offer.answer;

import org.littleshoot.mina.common.ByteBuffer;

/**
 * Interface for classes that generate offers and process answers for 
 * offer/answer protocols. 
 */
public interface OfferAnswer 
    {

    /**
     * Generates an offer.
     * 
     * @return The offer.
     */
    byte[] generateOffer();

    /**
     * Generates an answer.
     * 
     * @return The answer.
     */
    byte[] generateAnswer();
    
    /**
     * Tells an offerer to process its answer.
     * 
     * @param answer The answer.
     * @param offerAnswerListener Listener for offer/answer events.
     */
    void processAnswer(ByteBuffer answer);

    /**
     * Tells an answerer to process its offer.
     * 
     * @param offer The offer.
     */
    void processOffer(ByteBuffer offer);

    /**
     * Perform any necessary close operations for the media.
     */
    void close();

    void closeUdp();

    void closeTcp();

    void useRelay();
    
    }
