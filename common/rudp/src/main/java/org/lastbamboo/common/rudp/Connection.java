package org.lastbamboo.common.rudp;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.lastbamboo.common.rudp.segment.Segment;
import org.littleshoot.util.Optional;

/**
 * An interface to a reliable UDP connection.
 */
public interface Connection
    {
    /**
     * Closes this connection.
     */
    void close ();
    
    /**
     * Handles a segment delivered to this connection.
     * 
     * @param segment
     *      The segment.
     */
    void handle (Segment segment);
    
    /**
     * Attempts to open this connection.
     */
    void open ();
    
    /**
     * Receives a message.  This method is blocking.  If there is no message to
     * receive, this method blocks until a message is available.
     * 
     * @return The received message.
     * @throws SocketTimeoutException  If there's an SO_TIMEOUT set on the 
     * connection and it times out.
     * @throws SocketException A more generic socket exception thrown, 
     * for example, when the underlying data stream is closed.
     */
    byte[] receive () throws SocketTimeoutException, SocketException;
    
    /**
     * Attempts to send the given data as a message on this connection.
     * 
     * @param data The data to send.
     */
    void send (byte[] data);
    
    /**
     * Attempts to send the given data as a message on this connection.  If the
     * send buffer is currently full, this method will block until it is not
     * full to send the message or until the timeout (in milliseconds) has
     * elapsed, whichever comes first.
     * 
     * @param data The data to send.
     * @param timeout The maximum amount of time for which to block.
     */
    void send (byte[] data, long timeout);
    
    /**
     * Receives a message.  This method is non-blocking.  If there is no message
     * to receive, <code>None</code> is returned.
     * 
     * @return The received message.
     */
    Optional<byte[]> tryReceive ();

    /**
     * Sets the SO_TIMEOUT option on the connection, or the number of 
     * milliseconds to wait on reads before throwing an exception.
     * 
     * @param timeout  The timeout to use in milliseconds.
     */
    void setSoTimeout (int timeout);
    }
