package org.lastbamboo.common.rudp;

/**
 * An exception indicating that a connection is already open.  This will be
 * thrown when a client attempts to open a connection that is already open.
 */
public class RudpAlreadyOpenException extends RudpException
    {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 5834528452552946759L;

    /**
     * Constructs a new exception.
     */
    public RudpAlreadyOpenException
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
    public RudpAlreadyOpenException
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
    public RudpAlreadyOpenException
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
    public RudpAlreadyOpenException
            (final Throwable cause)
        {
        super (cause);
        }
    }
