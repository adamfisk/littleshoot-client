package org.lastbamboo.common.nio;

import java.nio.channels.SocketChannel;
import java.util.EventListener;

/**
 * Listener for newly accepted connections.
 */
public interface AcceptorListener extends EventListener
    {

    /**
     * Called when we've accepted a new socket channel.
     * @param sc The newly accepted client channel.
     */
    void onAccept(final SocketChannel sc);

    }
