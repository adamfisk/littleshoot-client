package org.lastbamboo.common.rudp;

/**
 * A reliable UDP exception.
 */
public class RudpException extends RuntimeException
    {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -4827167109867646834L;

    /**
     * Constructs a new exception.
     */
    public RudpException
            ()
        {
        super ();
        }

    /**
     * Constructs a new exception.
     * 
     * @param message
     *      The message of this exception.
     * @param cause
     *      The cause of this exception.
     */
    public RudpException
            (final String message,
             final Throwable cause)
        {
        super (message, cause);
        }

    /**
     * Constructs a new exception.
     * 
     * @param message
     *      The message of this exception.
     */
    public RudpException    
            (final String message)
        {
        super (message);
        }

    /**
     * Constructs a new exception.
     * 
     * @param cause
     *      The cause of this exception.
     */
    public RudpException
            (final Throwable cause)
        {
        super (cause);
        }
    }
