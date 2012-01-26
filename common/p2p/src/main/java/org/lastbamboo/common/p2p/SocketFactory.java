package org.lastbamboo.common.p2p;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;

import org.lastbamboo.common.offer.answer.NoAnswerException;

/**
 * General factory interface for sockets.
 */
public interface SocketFactory {

    /**
     * Creates a new socket.
     * 
     * @param uri The URI to generate a socket from.
     * @return The socket.
     * @throws IOException If there's an error connecting.
     * @throws NoAnswerException If there's no response from the answerer. 
     */
    Socket newSocket(URI uri) throws IOException, NoAnswerException;
}
