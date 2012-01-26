package org.lastbamboo.server.resource;

/**
 * Exception thrown when a group already exists.
 */
public class GroupExistsException extends Exception
    {

    /**
     * Creates a new exception.
     * 
     * @param msg The message.
     */
    public GroupExistsException(final String msg)
        {
        super(msg);
        }

    /**
     * Generated serial version ID.
     */
    private static final long serialVersionUID = -1490130203266453340L;

    }
