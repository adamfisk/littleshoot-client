package org.lastbamboo.common.offer.answer;


/**
 * Interface for factories that create classes the create offers and process
 * answers. 
 */
public interface OfferAnswerFactory
    {

    /**
     * Creates a new class for creating offers and processing answers.
     * 
     * @param listener Listener for events during the offer/answer exchange. 
     * @return A new class for creating offers and processing answers.
     * @throws OfferAnswerConnectException If there's an error connecting the
     * offerer.
     */
    OfferAnswer createOfferer(OfferAnswerListener listener) 
        throws OfferAnswerConnectException;
    
    /**
     * Creates a new class for processing offers and creating answers.
     * 
     * @param offer The offer.
     * @param listener Listener for events during the offer/answer exchange. 
     * @return A new class for processing offers and creating answers.
     * @throws OfferAnswerConnectException If there's an error connecting the
     * answerer.
     */
    OfferAnswer createAnswerer(OfferAnswerListener listener) 
        throws OfferAnswerConnectException;

    }
