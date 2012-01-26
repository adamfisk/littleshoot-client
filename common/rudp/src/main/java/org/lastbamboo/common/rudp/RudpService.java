package org.lastbamboo.common.rudp;

import java.net.InetSocketAddress;
import java.net.Socket;

import org.littleshoot.mina.common.IoServiceListener;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.util.F1;
import org.littleshoot.util.Future;
import org.littleshoot.util.Optional;

/**
 * A reliable UDP service.  This service provides the interface to our relaible
 * UDP implementation.  There should only be one of these per application.
 */
public interface RudpService extends IoServiceListener
    {
    /**
     * Accepts a connection on a given listening connection.
     * 
     * @param id The identifier of the listening connection.
     * @param listener The listener to be notified of events pertaining to the 
     * accepted connection.
     *      
     * @return A future result that becomes ready when the connection becomes 
     * open.
     */
    Future<RudpConnectionId> accept (RudpListeningConnectionId id,
        RudpListener listener);
    
    /**
     * Accepts a connection on a given {@link IoSession}.
     * 
     * @param session The session identifying the connection to listen on.
     * @param listener The listener to be notified of events pertaining to the 
     * accepted connection.
     *      
     * @return A future result that becomes ready when the connection becomes 
     * open.
     */
    Future<RudpConnectionId> accept (IoSession session, RudpListener listener);
    
    /**
     * Closes a reliable UDP connection.
     * 
     * @param id The identifier that specifies the connection.
     */
    void close (RudpConnectionId id);
    
    /**
     * Starts listening for reliable UDP connections on a given port on
     * localhost.
     *
     * @param session The connection {@link IoSession} to listen on.
     * 
     * @return The identifier for the listening connection that was created to
     *  listen for reliable UDP connections.
     */
    public RudpListeningConnectionId listen (IoSession session);
    
    /**
     * Listens for reliable UDP connections on a given port.
     * 
     * @param port The port.
     * @param backlog The maximum number of connections to queue that have not 
     *  yet been accepted.
     * @return The identifier for the listening connection.
     */
    RudpListeningConnectionId listen (int port, int backlog);
    
    /**
     * Opens the service with the specified connected {@link IoSession}.
     * 
     * @param session The connected {@link IoSession}.
     * @return Returns the identifier of the opened connection.
     */
    Future<RudpConnectionId> open (IoSession session);
    
    /**
     * Opens a reliable UDP connection.
     * 
     * @param address The address to which to open a connection.
     * @param listener The listener to be notified of events pertaining to the 
     * opened connection.
     * @return Returns the identifier of the opened connection.
     */
    Future<RudpConnectionId> open (InetSocketAddress address, 
        RudpListener listener);
    
    /**
     * Receives a message.  This method is blocking.  If there is no message to
     * receive, this method blocks until a message is available.
     * 
     * @param id The identifier that specifies the connection.
     * @return The received message.
     */
    byte[] receive (RudpConnectionId id);
    
    /**
     * Sends data on a given connection.
     * 
     * @param id The identifier that specifies the connection.
     * @param data The data to send.
     */
    void send (RudpConnectionId id, byte[] data);
    
    /**
     * Sends data on a given connection.  If the send buffer is currently full,
     * this method will block until it is not full to send the message or until
     * the timeout (in milliseconds) has elapsed, whichever comes first.
     * 
     * @param id The identifier of the connection.
     * @param data The data to send.
     * @param timeout The maximum amount of time for which to block.
     */
    void send (RudpConnectionId id, byte[] data, long timeout);
    
    /**
     * Receives a message.  This method is non-blocking.  If there is no message
     * to receive, <code>None</code> is returned.
     * 
     * @param id The identifier that specifies the connection.
     * 
     * @return The received message.
     */
    Optional<byte[]> tryReceive (RudpConnectionId id);

    /**
     * Accessor for the RUDP manager.
     * 
     * @return The RUDP manager.
     */
    RudpManager getManager();

    /**
     * Creates a new RUDP socket.
     * 
     * @param future The future that will supply the connection ID.
     * @param session The MINA session.
     * @return The new socket.
     */
    Socket newSocket(Future<RudpConnectionId> future, IoSession session);

    /**
     * Notifies the service the specified socket has closed.
     * 
     * @param rudpSocket The socket that closed.
     */
    void socketClosed(RudpSocket rudpSocket);

    }
