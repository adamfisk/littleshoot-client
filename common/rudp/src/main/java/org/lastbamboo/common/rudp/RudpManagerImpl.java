package org.lastbamboo.common.rudp;

import static org.lastbamboo.common.rudp.ConnectionImpl.InitialState.CLOSED;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

import org.lastbamboo.common.rudp.segment.Segment;
import org.lastbamboo.common.rudp.state.ConnectionRecord;
import org.lastbamboo.common.rudp.state.ConnectionRecordImpl;
import org.lastbamboo.common.rudp.state.OutOfOrderData;
import org.littleshoot.util.Either;
import org.littleshoot.util.F1;
import org.littleshoot.util.LeftImpl;
import org.littleshoot.util.Optional;
import org.littleshoot.util.Pair;
import org.littleshoot.util.RuntimeSocketException;
import org.littleshoot.util.RuntimeSocketTimeoutException;
import org.littleshoot.util.ThreadUtils;
import org.littleshoot.util.UIntImpl;
import org.littleshoot.util.UShortImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the reliable UDP manager implementation.
 * 
 * TODO: We need to remove connections when they die to avoid OOME.
 */
public final class RudpManagerImpl implements RudpManager
    {
    
    /**
     * The logger.
     */
    private final Logger m_log = 
        LoggerFactory.getLogger (RudpManagerImpl.class);
    
    /**
     * The mapping from connection identifiers to actual connections.
     */
    private final Map<RudpConnectionId,Connection> m_connections;
    
    /**
     * The mapping from listening connection identifiers to actual listening
     * connections.
     */
    private final Map<RudpListeningConnectionId,ListeningConnection>
        m_listeningConnections;
    
    /**
     * The random number generator used to generate initial sequence numbers.
     */
    private final Random m_random;
    
    /**
     * The timer used to schedule events.
     */
    private final Timer m_timer;
    
    /**
     * Constructs a new reliable UDP manager.
     */
    public RudpManagerImpl ()
        {
        m_timer = new Timer ("RUDP-Manager-Timer", true);
        m_random = new Random();//SecureRandom.getInstance ("SHA1PRNG");
        
        // We perform this apparently superfluous call to force the RNG to
        // seed itself.  Otherwise, there may be a really long delay the
        // first time it is called, at which point we will probably be in a
        // time-sensitive section.
        m_log.info ("Seeding random number generator");
        m_random.nextInt ();
        m_log.info ("Random number generator seeded");
        
        m_listeningConnections =
            new HashMap<RudpListeningConnectionId,ListeningConnection>();
    
        m_connections = new HashMap<RudpConnectionId,Connection> ();
        }
    
    /**
     * Returns the result of applying a given function to the value associated
     * with a given key in a given map.
     * 
     * @param <T> The type of the value.
     * @param <KeyT> The type of the key in the map.
     * @param <ReturnT> The type of the function's return value.
     * @param map The map.
     * @param key The key.
     * @param f The function to apply to the value.
     * @return The result of the function application.
     */
    private <T,KeyT,ReturnT> ReturnT withByKey (final Map<KeyT,T> map,
        final KeyT key, final F1<T,ReturnT> f)
        {
        final T object;
        
        // We only synchronize the retrieval of the object on which to act so
        // that the lock is not held when the function is run.
        synchronized (map)
            {
            if (map.containsKey (key))
                {
                //m_logger.debug ("Running on: " + key +" with instance: "+this);
                object = map.get (key);
                }
            else
                {
                //assert false : "Unknown key";
                m_log.error("Unknown key: "+key+" with trace\n"+
                    ThreadUtils.dumpStack());
                // TODO: Make this a more specific exception so that it may be
                // handled properly.
                throw new RuntimeException ("Key "+key+" not in map: " + map + 
                    " "+this);
                }
            }
        
        return f.run (object);
        }
    
    /**
     * Returns the result of applying a function to a connection given by
     * identifier.
     * 
     * @param <T> The type of the function's return value.
     * @param id The identifier of the connection.
     * @param f The function.
     * @return The result of applying the given function to the connection 
     * given by the given identifier.
     */
    private <T> T withConnection (final RudpConnectionId id,
        final F1<Connection,T> f)
        {
        return withByKey (m_connections, id, f);
        }
    
    /**
     * Returns the result of applying a function to a listening connection given
     * by identifier.
     * 
     * @param <T> The type of the function's return value.
     *      
     * @param id The identifier of the listening connection.
     * @param f The function.
     *      
     * @return The result of applying the given function to the listening
     *  connection given by the given identifier.
     */
    private <T> T withListeningConnection(final RudpListeningConnectionId id,
        final F1<ListeningConnection,T> f)
        {
        return withByKey (m_listeningConnections, id, f);
        }
    
    /**
     * {@inheritDoc}
     */
    public void accept (final RudpListeningConnectionId id,
        final F1<Either<RudpConnectionId,RuntimeException>,Void> openCallback)
        {
        final F1<InetSocketAddress,Void> internalOpenCallback =
            new F1<InetSocketAddress,Void> ()
            {
            public Void run (final InetSocketAddress remoteAddress)
                {
                final RudpConnectionId connectionId =
                    new RudpConnectionIdImpl (id.getLocalAddress (), 
                        remoteAddress);
                
                final Either<RudpConnectionId,RuntimeException> value =
                        new LeftImpl<RudpConnectionId,RuntimeException>
                            (connectionId);
                
                openCallback.run (value);
                
                return null;
                }
            };
            
        final RaceRunnable closedRunner = new RaceRunnableImpl ();
            
        final F1<ListeningConnection,Pair<InetSocketAddress,Connection>> f =
            new F1<ListeningConnection, Pair<InetSocketAddress,Connection>> ()
            {
            public Pair<InetSocketAddress,Connection> run
                    (final ListeningConnection connection)
                {
                final Pair<InetSocketAddress,Connection> pair =
                        connection.accept (internalOpenCallback, closedRunner);
                
                return pair;
                }
            };
            
        final Pair<InetSocketAddress,Connection> pair =
                withListeningConnection (id, f);
        final RudpConnectionId connectionId =
            new RudpConnectionIdImpl (id.getLocalAddress (), pair.getFirst ());
        
        synchronized (m_connections)
            {
            m_connections.put (connectionId, pair.getSecond ());
            }
        
        final Runnable closedDelegate =
            new ClosedRunner (m_connections, connectionId);
        closedRunner.setDelegate (closedDelegate);
        }
    
    /**
     * {@inheritDoc}
     */
    public void close (final RudpConnectionId id)
        {
        final F1<Connection,Void> f = new F1<Connection,Void> ()
            {
            public Void run (final Connection connection)
                {
                connection.close ();
                return null;
                }
            };
            
        withConnection (id, f);
        }
    
    /**
     * {@inheritDoc}
     */
    public void handle (final RudpConnectionId id, final Segment segment)
        {
        final F1<Connection,Void> f = new F1<Connection,Void> ()
            {
            public Void run (final Connection connection)
                {
                connection.handle (segment);
                return null;
                }
            };
            
        withConnection (id, f);
        }

    /**
     * {@inheritDoc}
     */
    public void handle (final RudpListeningConnectionId id,
        final InetSocketAddress address, final Segment segment,
        final F1<Segment,Void> writeF)
        {
        final F1<ListeningConnection,Void> f =
            new F1<ListeningConnection,Void> ()
            {
            public Void run (final ListeningConnection connection)
                {
                connection.handle (address, segment, writeF);
                return null;
                }
            };
            
        m_log.debug ("Handling segment: {}", segment.getClass());
        
        final RudpConnectionId connectionId =
            new RudpConnectionIdImpl (id.getLocalAddress (), address);
        
        final Connection connection;
        
        synchronized (m_connections)
            {
            connection = m_connections.get (connectionId);
            }
        
        if (connection == null)
            {
            withListeningConnection (id, f);
            }
        else
            {
            m_log.debug ("Found connection");
            connection.handle (segment);
            }
        }

    /**
     * {@inheritDoc}
     */
    public void listen (final RudpListeningConnectionId id, final int backlog)
        {
        if (m_listeningConnections.containsKey (id))
            {
            // TODO: Error.
            }
        else
            {
            final ListeningConnection connection =
                new ListeningConnectionImpl (m_random, m_timer, 
                    id.getLocalAddress (), backlog);
            m_listeningConnections.put (id, connection);
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public void notifyClosed (final RudpConnectionId id)
        {
        synchronized (m_connections)
            {
            if (m_log.isDebugEnabled())
                {
                m_log.debug ("Removing connection.  Trace is: {}", 
                    ThreadUtils.dumpStack());
                m_log.debug ("Got closed connection: {}", id);
                m_log.debug ("Connections are: {}", m_connections);
                }
            m_connections.remove (id);
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public void notifyClosed (final RudpListeningConnectionId id,
        final InetSocketAddress address)
        {
        m_log.debug("Notified of closed listening connection: {}", id);
        final RudpConnectionId connectionId =
            new RudpConnectionIdImpl (id.getLocalAddress (), address);
        
        synchronized (m_connections)
            {
            if (m_connections.containsKey (connectionId))
                {
                m_log.debug ("Closing listening connection: {}",  connectionId);
                m_connections.remove (connectionId);
                }
            else
                {
                // We do not know of such a connection.
                m_log.warn (id + " unknown");
                }
            }
        }

    /**
     * {@inheritDoc}
     */
    public void open (final RudpConnectionId id,
        final F1<Segment,Void> writeF,
        final F1<Either<RudpConnectionId,RuntimeException>,Void> openCallback)
        {
        final ConnectionRecord record =
                new ConnectionRecordImpl
                    (new UIntImpl (0),
                     new UIntImpl (0),
                     new UIntImpl (0),
                     new UShortImpl (200), // TODO: This should be configurable.
                     new UShortImpl (200),
                     new UShortImpl (0),
                     new UShortImpl (0),
                     new UIntImpl (0),
                     new UIntImpl (0),
                     new LinkedList<OutOfOrderData> ());
        
        final Runnable openRunner = new Runnable ()
            {
            public void run ()
                {
                try
                    {
                    final Either<RudpConnectionId,RuntimeException> value =
                        new LeftImpl<RudpConnectionId,RuntimeException> (id);
                    openCallback.run (value);
                    }
                catch (final Throwable t)
                    {
                    m_log.warn("Unexpected throwable.", t);
                    }
                }
            };
            
        final Runnable closedRunner = new ClosedRunner (m_connections, id);
        
        final Connection connection =
            new ConnectionImpl (m_random, m_timer, CLOSED, record, writeF,
                openRunner, closedRunner);
        
        synchronized (m_connections)
            {
            m_connections.put (id, connection);
            }
        
        connection.open ();
        }
    
    /**
     * {@inheritDoc}
     */
    public byte[] receive (final RudpConnectionId id)
        {
        final F1<Connection,byte[]> f = new F1<Connection,byte[]> ()
            {
            public byte[] run (final Connection connection)
                {
                try
                    {
                    return connection.receive ();
                    }
                catch (final SocketException e)
                    {
                    m_log.debug("Socket exception!", e);
                    throw new RuntimeSocketException(e.getMessage(), e);
                    }
                catch (final SocketTimeoutException e)
                    {
                    m_log.debug("Socket timed out!", e);
                    throw new RuntimeSocketTimeoutException(e.getMessage(), e);
                    }
                catch (final Throwable t)
                    {
                    m_log.warn("Unexpected throwable", t);
                    throw new RuntimeException(t.getMessage(), t);
                    }
                }
            };
        return withConnection (id, f);
        }
    
    /**
     * {@inheritDoc}
     */
    public void send (final RudpConnectionId id, final byte[] data)
        {
        send (id, data, 60000);
        }

    /**
     * {@inheritDoc}
     */
    public void send (final RudpConnectionId id, final byte[] data,
        final long timeout)
        {
        //final byte[] dataCopy = new byte[data.length];
        
        //System.arraycopy (data, 0, dataCopy, 0, data.length);
        
        final F1<Connection,Void> f = new F1<Connection,Void> ()
            {
            public Void run (final Connection connection)
                {
                m_log.debug ("Sending with custom timeout: {}", timeout);
                connection.send (data, timeout);
                return null;
                }
            };
            
        if (data.length > 0)
            {
            m_log.debug ("Trying to send on connection: " + id);
            withConnection (id, f);
            }
        else
            {
            // No data to send.  Do nothing.
            
            // Perhaps we should send a NUL in this case? 2007_07_03_jjc
            }
        }

    /**
     * {@inheritDoc}
     */
    public Optional<byte[]> tryReceive (final RudpConnectionId id)
        {
        final F1<Connection,Optional<byte[]>> f =
            new F1<Connection,Optional<byte[]>> ()
            {
            public Optional<byte[]> run (final Connection connection)
                {
                return connection.tryReceive ();
                }
            };
            
        return withConnection (id, f);
        }

    /**
     * {@inheritDoc}
     */
    public void setSoTimeout (final RudpConnectionId id, final int timeout)
        {
        final F1<Connection, Void> f = new F1<Connection, Void> ()
            {
            public Void run (final Connection connection)
                {
                connection.setSoTimeout (timeout);
                return null;
                }
            };
        withConnection (id, f);
        }

    /**
     * {@inheritDoc}
     */
    public void remove(final RudpListeningConnectionId id)
        {
        m_log.debug("Removing listening connection: {}", id);
        this.m_listeningConnections.remove(id);
        }
    
    /**
     * The callback used to handle closed connections.
     */
    private static class ClosedRunner implements Runnable
        {
        
        private final Logger m_log = LoggerFactory.getLogger(getClass());
        
        /**
         * The map of connections from which to remove the closed connection.
         */
        private final Map<RudpConnectionId,Connection> m_connections;
        
        /**
         * The identifier of the closed connection.
         */
        private final RudpConnectionId m_id;
        
        /**
         * Constructs a new callback used to handle closed connections.
         * 
         * @param connections The map of connections.
         * @param id The identifier of the closed connection.
         */
        private ClosedRunner (
            final Map<RudpConnectionId,Connection> connections,
            final RudpConnectionId id)
            {
            m_connections = connections;
            m_id = id;
            }
        
        /**
         * {@inheritDoc}
         */
        public void run ()
            {
            try
                {
                synchronized (m_connections)
                    {
                    m_connections.remove (m_id);
                    }
                }
            catch (final Throwable t)
                {
                m_log.error("Unexpected throwable", t);
                }
            }
        }
    }
