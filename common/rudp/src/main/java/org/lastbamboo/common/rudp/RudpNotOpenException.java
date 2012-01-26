package org.lastbamboo.common.rudp;

/**
 * An exception indicating that a reliable UDP connection is not open.  This
 * exception will be thrown when an operation that requires an OPEN connection
 * is called on a connection in some other state.
 */
public class RudpNotOpenException extends RudpException
    {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 4590912174090855263L;

    /**
     * Constructs a new exception.
     */
    public RudpNotOpenException ()
        {
        super ();
        }

    /**
     * Constructs a new exception.
     * 
     * @param message The message of this exception.
     * @param cause The cause of this exception.
     */
    public RudpNotOpenException (final String message, final Throwable cause)
        {
        super (message, cause);
        }

    /**
     * Constructs a new exception.
     * 
     * @param message The message of this exception.
     */
    public RudpNotOpenException (final String message)
        {
        super (message);
        }

    /**
     * Constructs a new exception.
     * 
     * @param cause The cause of this exception.
     */
    public RudpNotOpenException (final Throwable cause)
        {
        super (cause);
        }
    }
