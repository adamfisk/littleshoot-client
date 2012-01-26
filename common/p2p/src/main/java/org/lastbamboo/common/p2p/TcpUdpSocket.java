package org.lastbamboo.common.p2p;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;

import org.lastbamboo.common.offer.answer.NoAnswerException;

/**
 * Interface for classes that can create P2P sockets.
 */
public interface TcpUdpSocket
    {

    /**
     * Creates a new socket.
     * 
     * @param uri The URI to create a socket from. This could be a SIP URI,
     * for example.
     * @return The new socket.
     * @throws IOException If there's an IO error creating the socket.
     * @throws NoAnswerException If the answerer doesn't reply.
     */
    Socket newSocket(URI uri) throws IOException, NoAnswerException;
    }
