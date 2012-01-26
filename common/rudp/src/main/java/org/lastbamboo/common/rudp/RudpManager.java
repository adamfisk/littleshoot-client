package org.lastbamboo.common.rudp;

import java.net.InetSocketAddress;

import org.lastbamboo.common.rudp.segment.Segment;
import org.littleshoot.util.Either;
import org.littleshoot.util.F1;
import org.littleshoot.util.Optional;

/**
 * The reliable UDP manager that handles all of the logic of our reliable UDP
 * implementation and decouples the actual transport layer used.
 */
public interface RudpManager
    {
    /**
     * Accepts a connection on a given listening connection.
     * 
     * @param id
     *      The identifier of the listening connection.
     * @param openCallback
     *      The callback to call when the accept connection goes into the OPEN
     *      state.  The callback is called with either the connection identifier
     *      of the accepted connection or an exception indicating an error.
     */
    void accept
            (RudpListeningConnectionId id,
             F1<Either<RudpConnectionId,RuntimeException>,Void> openCallback);
    
    /**
     * Closes a connection.
     * 
     * @param id
     *      The identifier on the listening connection.
     */
    void close
            (RudpConnectionId id);
    
    /**
     * Handles a segment delivered to a connection.
     * 
     * @param id
     *      The identifier of the connection.
     * @param segment
     *      The segment to handle.
     */
    void handle
            (RudpConnectionId id,
             Segment segment);
    
    /**
     * Handles a segment delivered to a listening connection.
     * 
     * @param id
     *      The identifier of the listening connection.
     * @param remoteAddress
     *      The remote address from which the segment was sent.
     * @param segment
     *      The segment.
     * @param writeF
     *      The function used to write messages that may result from handling 
     *      the segment.
     */
    void handle
            (RudpListeningConnectionId id,
             InetSocketAddress remoteAddress,
             Segment segment,
             F1<Segment,Void> writeF);
    
    /**
     * Tells this manager that we are listening on a given listening connection.
     * Note that this is a bit of a lapse in the decoupling, since the
     * identifier needs to be created outside of the manager.  Ideally, we could
     * abstract the actual listening setup.
     * 
     * @param id
     *      The identifier of the listening connection.
     * @param backlog
     *      The maximum number of connections to queue that have not yet been
     *      accepted.
     */
    void listen
            (RudpListeningConnectionId id,
             int backlog);
    
    /**
     * Notifies this manager that a given connection has been externally
     * closed.
     * 
     * @param id
     *      The identifier of the connection that was closed.
     */
    void notifyClosed
            (RudpConnectionId id);
    
    /**
     * Notifies this manager that a given connection inititiated by accepting a
     * connection on a listening connection has been externally closed.
     * 
     * @param id
     *      The identifier of the listening connection.
     * @param remoteAddress
     *      The remote client's address.  Combined with the listening connection
     *      identifier, we can identify a unique connection.
     */
    void notifyClosed
            (RudpListeningConnectionId id,
             InetSocketAddress remoteAddress);
    
    /**
     * Tells this manager that we are opening a connection.  Note this suffers
     * from the similar coupling described for <code>listen</code>.
     * 
     * @param id
     *      The identifier of the connection.
     * @param writeF
     *      The function used to write messages on this connection.
     * @param openCallback
     *      The callback to call when the accepted connection goes into the
     *      OPEN state.  The callback is called with either the connection
     *      identifier of the accepted connection or an exception indicating an
     *      error.
     */
    void open
            (RudpConnectionId id,
             F1<Segment,Void> writeF,
             F1<Either<RudpConnectionId,RuntimeException>,Void> openCallback);
    
    /**
     * Receives a message.  This method is blocking.  If there is no message to
     * receive, this method blocks until a message is available.
     * 
     * @param id
     *      The identifier that specifies the connection.
     * 
     * @return
     *      The received message.
     */
    byte[] receive
            (RudpConnectionId id);
    
    /**
     * Sends a message on a given connection.
     * 
     * @param id
     *      The identifier of the connection.
     * @param data
     *      The data in the message to send.
     */
    void send
            (RudpConnectionId id,
             byte[] data);
    
    /**
     * Sends a message on a given connection.  If the send buffer is currently
     * full, this method will block until it is not full to send the message or
     * until the timeout (in milliseconds) has elapsed, whichever comes first.
     * 
     * @param id
     *      The identifier of the connection.
     * @param data
     *      The data to send.
     * @param timeout
     *      The maximum amount of time for which to block.
     */
    void send
            (RudpConnectionId id,
             byte[] data,
             long timeout);
    
    /**
     * Receives a message.  This method is non-blocking.  If there is no message
     * to receive, <code>None</code> is returned.
     * 
     * @param id
     *      The identifier that specifies the connection.
     * 
     * @return
     *      The received message.
     */
    Optional<byte[]> tryReceive
            (RudpConnectionId id);

    /**
     * Sets the SO_TIMEOUT option on the connection, or the number of 
     * milliseconds to wait on reads before throwing an exception.
     * 
     * @param connectionId 
     *      The ID of the connection to set the SO_TIMEOUT option on.
     * @param timeout 
     *      The timeout to use in milliseconds.
     */
    void setSoTimeout
            (RudpConnectionId connectionId, 
             int timeout);

    /**
     * Removes the specified {@link RudpListeningConnectionId} from the IDs
     * to track for incoming connection.
     * 
     * @param id The ID to remove.
     */
    void remove
            (RudpListeningConnectionId id);
    }
