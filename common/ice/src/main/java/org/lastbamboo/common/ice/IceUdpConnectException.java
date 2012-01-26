package org.lastbamboo.common.ice;

import java.io.IOException;

/**
 * Exception for when we can't establish a UDP peer for ICE.
 */
public class IceUdpConnectException extends Exception
    {

    /**
     * Generated server version ID.
     */
    private static final long serialVersionUID = 9217866898986615657L;
    
    /**
     * Creates a new UDP connection exception.
     * 
     * @param message The error message.
     * @param cause The cause.
     */
    public IceUdpConnectException(final String message, final IOException cause)
        {
        super(message, cause);
        }

    }
