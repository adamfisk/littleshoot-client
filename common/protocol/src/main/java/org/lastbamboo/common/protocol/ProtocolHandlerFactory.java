package org.lastbamboo.common.protocol;

/**
 * Factory class for creating the protocol handlers for specific protocols.  
 * Note this factory class is necessary because the protocol handlers keep track
 * of any previously read data from incomplete messages for the specific 
 * protocol.  
 */
public interface ProtocolHandlerFactory
    {

    /**
     * Creates a handler for the specific protocol this server processes.
     * @return The handler for the specific protocol implemented on this server.
     */
    ProtocolHandler createProtocolHandler();

    }
