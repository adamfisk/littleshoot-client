package org.lastbamboo.common.http.client;

/**
 * Exception for when there's no content range header and we need one.
 */
public class NoContentRangeException extends RuntimeException
    {

    /**
     * Generated serial ID.
     */
    private static final long serialVersionUID = -6295840044105284432L;

    /**
     * Creates a new exception.
     * 
     * @param msg The message.
     */
    public NoContentRangeException(final String msg)
        {
        super(msg);
        }
    }
