package org.lastbamboo.common.protocol;

import java.util.Collection;


/**
 * Interface for connecting to servers.
 */
public interface ServerConnector
    {

    /**
     * Connects to the specified group of server addresses.
     * 
     * @param servers The collection of InetSocketAddress instances to 
     * connect to.
     * @param listener The listener for connections.
     */
    void connect(final Collection servers, 
        final ReadWriteConnectorListener listener);

    }
