package org.lastbamboo.common.rudp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.littleshoot.mina.common.ConnectFuture;
import org.littleshoot.mina.common.ExecutorThreadModel;
import org.littleshoot.mina.common.ExpiringSessionRecycler;
import org.littleshoot.mina.common.IoAcceptor;
import org.littleshoot.mina.common.IoConnector;
import org.littleshoot.mina.common.IoFuture;
import org.littleshoot.mina.common.IoFutureListener;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoHandlerAdapter;
import org.littleshoot.mina.common.IoService;
import org.littleshoot.mina.common.IoServiceConfig;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.common.ThreadModel;
import org.littleshoot.mina.filter.codec.ProtocolCodecFilter;
import org.littleshoot.mina.filter.codec.ProtocolDecoder;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.littleshoot.mina.filter.executor.ExecutorFilter;
import org.littleshoot.mina.transport.socket.nio.DatagramAcceptor;
import org.littleshoot.mina.transport.socket.nio.DatagramAcceptorConfig;
import org.littleshoot.mina.transport.socket.nio.DatagramConnector;
import org.littleshoot.mina.transport.socket.nio.DatagramConnectorConfig;
import org.lastbamboo.common.rudp.segment.RudpDecoder;
import org.lastbamboo.common.rudp.segment.RudpEncoder;
import org.lastbamboo.common.rudp.segment.Segment;
import org.littleshoot.util.DaemonThread;
import org.littleshoot.util.DaemonThreadFactory;
import org.littleshoot.util.Either;
import org.littleshoot.util.F1;
import org.littleshoot.util.Future;
import org.littleshoot.util.FutureBuilder;
import org.littleshoot.util.FutureBuilderImpl;
import org.littleshoot.util.Optional;
import org.littleshoot.util.RightImpl;
import org.littleshoot.util.UShort;
import org.littleshoot.util.UShortImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the reliable UDP service interface.
 */
public final class RudpServiceImpl implements RudpService
    {

    /**
     * The number of seconds before considering a connection idle and closing
     * it to recover resources.
     */
    private final int m_idleTime = 60;
    
    /**
     * The logger.
     */
    private final Logger m_logger = 
        LoggerFactory.getLogger (RudpServiceImpl.class);
    
    /**
     * The reliable UDP manager that does most of the non-MINA heavy lifting.
     */
    private final RudpManager m_manager = new RudpManagerImpl ();
    
    private final Collection<RudpSocket> m_sockets = new HashSet<RudpSocket>();
    
    /**
     * Constructs a new reliable UDP service.
     */
    public RudpServiceImpl ()
        {
        addShutdownHook();
        }
    
    private void addShutdownHook()
        {
        final Runnable shutDownRunner = new Runnable()
            {
            public void run()
                {
                synchronized (m_sockets)
                    {
                    System.out.println("Closing RUDP sockets");
                    for (final RudpSocket sock : m_sockets)
                        {
                        m_logger.debug("Closing all sockets...");
                        try
                            {
                            sock.close();
                            }
                        catch (final Exception e)
                            {
                            m_logger.debug("Exception closing socket", e);
                            }
                        }
                    System.out.println("Closed RUDP sockets...");
                    }
                }
            };
        final Thread hook = 
            new DaemonThread(shutDownRunner, 
                "LittleShoot-RUDP-Shutdown-Thread");
        Runtime.getRuntime().addShutdownHook(hook);
        }

    /**
     * Returns a write function that is used to write UDP messages over a given
     * session.
     * 
     * @param session The session.
     *      
     * @return The write function.
     */
    private static F1<Segment,Void> getWriteF (final IoSession session)
        {
        final F1<Segment,Void> f = new F1<Segment,Void> ()
            {
            public Void run (final Segment segment)
                {
                session.write (segment);
                return null;
                }
            };
            
        return f;
        }
    
    
    /**
     * Starts listening for reliable UDP connections on a given port on
     * localhost.
     *
     * @param port The port.
     * @param backlog The maximum number of connections to queue that have not 
     * yet been accepted.
     * 
     * @return The identifier for the listening connection that was created to
     * listen for reliable UDP connections.
     */
    private RudpListeningConnectionId listen (final UShort port,
        final int backlog)
        {
        final ExecutorService executor = Executors.newCachedThreadPool (
            new DaemonThreadFactory("RUDP-Listening-Thread-Pool"));
        final DatagramAcceptorConfig config = new DatagramAcceptorConfig ();
        final ThreadModel threadModel = 
            ExecutorThreadModel.getInstance("RUDP-Acceptor");
        
        config.setSessionRecycler (new ExpiringSessionRecycler (m_idleTime));
        config.setThreadModel(threadModel);
        config.getFilterChain ().addLast ("executor",
                                          new ExecutorFilter (executor));
      
        final ProtocolEncoder encoder = new RudpEncoder ();
        final ProtocolDecoder decoder = new RudpDecoder ();
        
        final ProtocolCodecFilter codecFilter = 
                new ProtocolCodecFilter(encoder, decoder);
        
        config.getFilterChain ().addLast ("rudp", codecFilter);
      
        final InetSocketAddress address =
            new InetSocketAddress (port.toInt ());
        
        final RudpListeningConnectionId id =
            new RudpListeningConnectionIdImpl (address);
        
        // This handler is used by all connections on this port.  Each
        // connection from a different endpoint will have a different
        // session.
        final IoHandler handler = new IoHandlerAdapter ()
            {
            @Override
            public void messageReceived (final IoSession session,
                final Object msg)
                {
                // m_logger.debug ("Session: " + session.toString ());
                //m_logger.debug (msg.toString ());
                
                final InetSocketAddress remoteAddress =
                        (InetSocketAddress) session.getRemoteAddress ();
                
                synchronized (RudpServiceImpl.this)
                    {
                    m_manager.handle (id, remoteAddress, (Segment) msg,
                        getWriteF (session));
                    }
                }
            
            @Override
            public void sessionClosed (final IoSession session) throws Exception
                {
                final InetSocketAddress remoteAddress =
                    (InetSocketAddress) session.getRemoteAddress ();
                
                synchronized (RudpServiceImpl.this)
                    {
                    m_manager.notifyClosed (id, remoteAddress);
                    }
                super.sessionClosed (session);
                }
            
            @Override
            public void exceptionCaught(final IoSession session, 
                final Throwable t)
                {
                m_logger.warn("Caught exception!", t);
                }
            };
        
        final IoAcceptor acceptor = new DatagramAcceptor ();

        try
            {
            synchronized (this)
                {
                acceptor.bind (address, handler, config);
                m_logger.debug ("MINA acceptor bound to '" + address + "'");
                
                m_manager.listen (id, backlog);
                }
            
                return id;
            }
        catch (final IOException e)
            {
            throw new RuntimeException ("Could not bind to port " +
                                            port.toInt ());
            }
        }

    /**
     * {@inheritDoc}
     */
    public Future<RudpConnectionId> accept
            (final RudpListeningConnectionId id,
             final RudpListener listener)
        {
        final FutureBuilder<RudpConnectionId> openFuture =
                new FutureBuilderImpl<RudpConnectionId> ();
        
        final F1<Either<RudpConnectionId,RuntimeException>,Void> openCallback =
                new F1<Either<RudpConnectionId,RuntimeException>,Void> ()
            {
            public Void run
                    (final Either<RudpConnectionId,RuntimeException> value)
                {
                openFuture.set (value);
                return null;
                }
            };
        
        m_manager.accept (id, openCallback);
        
        return openFuture;
        }
    
    /**
     * {@inheritDoc}
     */
    public Future<RudpConnectionId> accept (final IoSession session,
        final RudpListener listener)
        {
        return accept(RudpUtils.toListeningId(session), listener);
        }

    /**
     * {@inheritDoc}
     */
    public void close (final RudpConnectionId id)
        {
        m_manager.close (id);
        }

    /**
     * {@inheritDoc}
     */
    public RudpManager getManager ()
        {
        return this.m_manager;
        }
    
    /**
     * {@inheritDoc}
     */
    public RudpListeningConnectionId listen (final int port, final int backlog)
        {
        final UShort shortPort = new UShortImpl (port);
        return listen (shortPort, backlog);
        }
    
    /**
     * {@inheritDoc}
     */
    public RudpListeningConnectionId listen (final IoSession session)
        {
        final RudpListeningConnectionId id = RudpUtils.toListeningId(session);
        
        m_manager.listen (id, 5);
        
        return id;
        }

    /**
     * {@inheritDoc}
     */
    public Future<RudpConnectionId> open (final InetSocketAddress address,
        final RudpListener listener)
        {
        final FutureBuilder<RudpConnectionId> openFuture =
                new FutureBuilderImpl<RudpConnectionId> ();
        
        final IoHandler handler = new IoHandlerAdapter ()
            {
            private Either<RudpConnectionId,RuntimeException> getFutureValue
                    (final Throwable cause)
                {
                return new RightImpl<RudpConnectionId,RuntimeException>
                        (new RuntimeException (cause));
                }
            
            @Override
            public void exceptionCaught
                    (final IoSession session,
                     final Throwable cause) throws Exception
                {
                if (cause instanceof PortUnreachableException)
                    {
                    openFuture.set (getFutureValue (cause));
                    }
                else
                    {
                    m_logger.warn("Exception caught", cause);
                    super.exceptionCaught (session, cause);
                    }
                }

            @Override
            public void messageReceived (final IoSession session, 
                final Object msg)
                {
                //m_logger.debug (msg.toString ());
                m_manager.handle (RudpUtils.toId (session), (Segment) msg);
                }
            
            @Override
            public void sessionClosed (final IoSession session)
                {
                m_logger.debug("Session closed");
                m_manager.notifyClosed (RudpUtils.toId (session));
                }
            };
        
        final ProtocolEncoder encoder = new RudpEncoder ();
        final ProtocolDecoder decoder = new RudpDecoder ();
        
        final ProtocolCodecFilter codecFilter = 
                new ProtocolCodecFilter (encoder, decoder);
            
        final DatagramConnectorConfig connectorConfig =
                new DatagramConnectorConfig ();
        final ThreadModel threadModel = 
            ExecutorThreadModel.getInstance("RUDP-Client");
        
        connectorConfig.setSessionRecycler
                (new ExpiringSessionRecycler (m_idleTime));
        
        connectorConfig.setThreadModel(threadModel);
        connectorConfig.getFilterChain ().addLast ("rudp", codecFilter);
        
        final IoConnector connector = new DatagramConnector ();
        
        final ConnectFuture connectFuture = 
                connector.connect (address, handler, connectorConfig);
        
        final F1<Either<RudpConnectionId,RuntimeException>,Void> openCallback =
                new F1<Either<RudpConnectionId,RuntimeException>,Void> ()
            {
            public Void run
                    (final Either<RudpConnectionId,RuntimeException> value)
                {
                openFuture.set (value);
                return null;
                }
            };
        
        final IoFutureListener connectListener = new IoFutureListener ()
            {
            public void operationComplete (final IoFuture future)
                {
                if (connectFuture.isConnected ())
                    {
                    final IoSession session = connectFuture.getSession ();
                    final RudpConnectionId id = RudpUtils.toId (session);
                    
                    m_manager.open (id, getWriteF (session), openCallback);
                    }
                else
                    {
                    final Either<RudpConnectionId,RuntimeException> value =
                        new RightImpl<RudpConnectionId,RuntimeException>
                            (new RudpException ("Attempt to connect failed"));
                    
                    openFuture.set (value);
                    }
                }
            };
            
        connectFuture.addListener (connectListener);
        
        return openFuture;
        }
    
    /**
     * {@inheritDoc}
     */
    public Future<RudpConnectionId> open (final IoSession session)
        {
        final FutureBuilder<RudpConnectionId> openFuture =
            new FutureBuilderImpl<RudpConnectionId> ();
    
        final F1<Either<RudpConnectionId,RuntimeException>,Void> openCallback =
            new F1<Either<RudpConnectionId,RuntimeException>,Void> ()
            {
            public Void run (
                final Either<RudpConnectionId,RuntimeException> value)
                {
                openFuture.set (value);
                return null;
                }
            };
            
        final RudpConnectionId id = RudpUtils.toId (session);
        
        m_manager.open (id, getWriteF (session), openCallback);
        
        return openFuture;
        }

    public byte[] receive (final RudpConnectionId id) 
        {
        return m_manager.receive (id);
        }
    
    public void send (final RudpConnectionId id, final byte[] data)
        {
        m_manager.send (id, data);
        }

    public void send (final RudpConnectionId id, final byte[] data,
        final long timeout)
        {
        m_manager.send (id, data, timeout);
        }

    public Optional<byte[]> tryReceive (final RudpConnectionId id)
        {
        return m_manager.tryReceive (id);
        }

    public void serviceActivated(final IoService service, 
        final SocketAddress serviceAddress, final IoHandler handler, 
        final IoServiceConfig config)
        {
        }

    public void serviceDeactivated(final IoService service, 
        final SocketAddress serviceAddress, final IoHandler handler, 
        final IoServiceConfig config)
        {
        }

    public void sessionCreated(final IoSession session)
        {
        listen(session);
        }

    public void sessionDestroyed(final IoSession session)
        {
        //m_manager.remove(toListeningId(session));
        }

    public Socket newSocket(final Future<RudpConnectionId> future, 
        final IoSession session)
        {
        final RudpSocket sock = new RudpSocket(this, future.get(), session);
        synchronized (m_sockets)
            {
            this.m_sockets.add(sock);
            }
        return sock;
        }

    public void socketClosed(final RudpSocket rudpSocket)
        {
        synchronized (m_sockets)
            {
            this.m_sockets.remove(rudpSocket);
            }
        }
    }
