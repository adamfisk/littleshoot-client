package org.lastbamboo.common.offer.answer;

import java.io.IOException;

/**
 * Exception for when there's not answer to an offer/answer exchange.
 */
public class NoAnswerException extends IOException {

    private static final long serialVersionUID = 8243953563871685981L;

    /**
     * Creates a new exception.
     * 
     * @param msg The message associated with the exception.
     */
    public NoAnswerException(final String msg) {
        super (msg);
    }

    
    /**
     * Creates a new exception.
     * 
     * @param msg The message associated with the exception.
     * @param cause What caused this exception.
     */
    public NoAnswerException(final String msg, final Throwable cause) {
        super (msg, cause);
    }
}
