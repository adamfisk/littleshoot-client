package org.lastbamboo.server.resource;

/**
 * Exception for when there's an invalid password reset ID.
 */
public class BadPasswordResetIdException extends Exception
    {

    /**
     * Generated serial version ID.
     */
    private static final long serialVersionUID = 183514356131882595L;

    /**
     * Creates a new {@link BadPasswordResetIdException}.
     * 
     * @param msg The message for the exception.
     */
    public BadPasswordResetIdException(final String msg)
        {
        super(msg);
        }

    /**
     * Creates a new {@link BadPasswordResetIdException}.
     */
    public BadPasswordResetIdException()
        {
        super();
        }

    }
