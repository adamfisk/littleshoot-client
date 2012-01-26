package org.lastbamboo.server.resource;

/**
 * Exception for when the user is not found.
 */
public class UserNotFoundException extends Exception
    {

    /**
     * Generated serial version ID.
     */
    private static final long serialVersionUID = -6230068204983714591L;
    
    /**
     * Creates a new {@link UserNotFoundException}.
     * 
     * @param msg The message for the exception.
     */
    public UserNotFoundException(String msg)
        {
        super(msg);
        }

    }
