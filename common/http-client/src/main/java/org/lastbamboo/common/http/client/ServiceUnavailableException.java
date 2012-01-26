package org.lastbamboo.common.http.client;

/**
 * Exception for 503 service unavailable errors.
 */
public class ServiceUnavailableException extends Exception
    {

    /**
     * Generated serial version ID.
     */
    private static final long serialVersionUID = -7224367332200306689L;

    /**
     * Creates a new exception.
     * 
     * @param msg The detail message.
     */
    public ServiceUnavailableException(final String msg)
        {
        super(msg);
        }

    }
