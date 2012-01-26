package org.lastbamboo.common.offer.answer;


/**
 * Exception for when we can't connect an offerer or an answerer.
 */
public class OfferAnswerConnectException extends Exception
    {
    /**
     * Generated ID.
     */
    private static final long serialVersionUID = -7556469722658592557L;
    
    /**
     * Creates a new exception.
     * 
     * @param message The exception message.
     * @param cause The cause.
     */
    public OfferAnswerConnectException(final String message, 
        final Exception cause)
        {
        super(message, cause);
        }
    }
