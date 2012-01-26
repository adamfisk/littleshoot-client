package org.lastbamboo.server.resource;

/**
 * Exception for when a user already exists in the database.
 */
public class UserExistsException extends Exception
    {

    /**
     * Generated serial version ID.
     */
    private static final long serialVersionUID = -6551905936025100401L;

    /**
     * Creates a new exception indicating the user exists.
     * 
     * @param msg The detail message.
     */
    public UserExistsException(final String msg)
        {
        super(msg);
        }
    }
