package org.lastbamboo.common.protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.EventListener;


/**
 * Listens for the creation of a read/write handler for a new connection.
 */
public interface ReadWriteConnectorListener extends EventListener
    {

    /**
     * Called when we've successfully connected to a host and have created 
     * the associated read/write handler.
     * @param readerWriter The read/write handler for the newly connected host.
     * @throws IOException If we could not enable writing on this channel.
     */
    void onConnect(final ReaderWriter readerWriter) throws IOException;

    /**
     * Called when the connection attempt to the remote host failed for any
     * reason.
     * @param socketAddress The IP address and port of the remote host that we
     * could not connect to.
     */
    void onConnectFailed(final InetSocketAddress socketAddress);

    }
