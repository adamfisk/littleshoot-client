package org.lastbamboo.common.rudp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import org.littleshoot.mina.common.IoSession;
import org.littleshoot.util.F0;
import org.littleshoot.util.F1;
import org.littleshoot.util.NotYetImplementedException;
import org.littleshoot.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A socket implementation backed by our reliable UDP implementation.
 */
public final class RudpSocket extends Socket
    {
    private enum State
        {
        BOUND,
        CONNECTED,
        CLOSED
        }
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * The identifier of the connection represented by this socket.
     */
    private final RudpConnectionId m_id;
    
    /**
     * The reliable UDP service used to implement this socket.
     */
    private final RudpService m_service;

    private final OutputStream m_outputStream;

    private final InputStream m_inputStream;
    
    private boolean m_shutIn = false;
    private boolean m_shutOut = false;
    private volatile State m_state;

    private final Object m_closeLock = new Object();

    private final IoSession m_session;
    
    /**
     * Creates a new {@link RudpSocket} with the specified underlying service
     * class and the specified connection ID.
     * 
     * @param service The RUDP service class.
     * @param id The unique ID for this connection.
     * @param session The {@link IoSession} for the socket.
     */
    public RudpSocket (final RudpService service, final RudpConnectionId id, 
        final IoSession session)
        {
        if (service == null)
            {
            m_log.error("Null service");
            throw new NullPointerException("Null service");
            }
        if (id == null)
            {
            m_log.error("Null ID...");
            throw new NullPointerException("Null ID");
            }
        m_service = service;
        m_id = id;
        this.m_outputStream = newOutputStream(service, id);
        this.m_inputStream = newInputStream(service, id);
        m_state = State.CONNECTED;
        this.m_session = session;
        }

    private InputStream newInputStream (final RudpService service, 
        final RudpConnectionId connectionId)
        {
        final F1<RudpConnectionId,InputStream> f =
                new F1<RudpConnectionId,InputStream> ()
            {
            public InputStream run (final RudpConnectionId id)
                {
                return new RudpInputStream (service, connectionId,
                    RudpSocket.this);
                }
            };
            
        return withId (f);
        }

    private OutputStream newOutputStream (final RudpService service, 
        final RudpConnectionId connectionId)
        {
        final F1<RudpConnectionId,OutputStream> f =
            new F1<RudpConnectionId,OutputStream> ()
            {
            public OutputStream run (final RudpConnectionId id)
                {
                return new RudpOutputStream (service, connectionId, 
                    RudpSocket.this);
                }
            };
            
        return withId (f);
        }

    /**
     * Returns the result of the execution of some functions depending on
     * whether we have a non-null connection identifier.
     * 
     * @param <T> The return type of the operation.
     *      
     * @param fNoId The function to run if the connection identifier is null.
     * @param f  The function to run if the connection identifier is non-null.  
     *  The function is passed the identifier.
     *      
     * @return The result of the function that was run.
     */
    private <T> T withId (final F0<T> fNoId, final F1<RudpConnectionId,T> f)
        {
        if (m_id == null)
            {
            return fNoId.run ();
            }
        else
            {
            return f.run (m_id);
            }
        }

    /**
     * Returns the result of the execution of a function on a non-null
     * connection identifier.  If the connection identifier is null, a runtime
     * exception is thrown.
     * 
     * @param <T> The return type of the operation.
     * @param f The function to run.  It is passed the identifier.
     * @return The return value of the function.
     * @throws RuntimeException If the connection identifier is null.
     */
    private <T> T withId(final F1<RudpConnectionId,T> f) throws RuntimeException
        {
        final F0<T> fNoId = new F0<T> ()
            {
            public T run ()
                {
                m_log.warn("Not connected!!");
                throw new RuntimeException ("Not connected");
                }
            };
            
        return withId (fNoId, f);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind (final SocketAddress address) throws IOException
        {
        // For now, we do not allow binding of the local address.  The ephemeral
        // port will just be chosen.
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close () throws IOException
        {
        m_log.debug("Closing socket from call stack: "+ThreadUtils.dumpStack());
        synchronized(m_closeLock) 
            {
            if (isClosed())
                {
                m_log.debug("Already closed...");
                return;
                }
        
            // We set up the functions used to close our connection in the RUDP
            // service.
            final F0<Void> fNoId = new F0<Void> ()
                {
                public Void run ()
                    {
                    // There is no connection.  Do nothing.
                    return null;
                    }
                };
                
            final F1<RudpConnectionId,Void> f = new F1<RudpConnectionId,Void> ()
                {
                public Void run (final RudpConnectionId id)
                    {
                    m_service.close (id);
                    return null;
                    }
                };
                    
            withId (fNoId, f);
            m_state = State.CLOSED;
            
            this.m_service.socketClosed(this);
            
            // We also close the session.  MINA typically detects the session
            // has closed, but we shouldn't rely on that behavior, particularly
            // given it's the type of area that tends to vary by operating 
            // system.  No harm closing the session again.
            // Note: session can be null for testing.
            if (m_session != null)
                {
                m_log.debug("Closing session");
                this.m_session.close();
                }
            }
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect (final SocketAddress address) throws IOException
        {
        connect (address, 60000);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect (final SocketAddress address, final int timeout) 
        throws IOException
        {
        m_log.debug("RudpSockets are always connected...ignoring");
        /*
        if (address == null)
            throw new IllegalArgumentException("The address can't be null");
        if (timeout < 0)
            throw new IllegalArgumentException("The timeout can't be negative");
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (isConnected())
            throw new SocketException("already connected");
        if (!(address instanceof InetSocketAddress))
            throw new IllegalArgumentException("Unsupported address type");
        
        if (m_id == null)
            {
            final RudpListener listener = new RudpListener ()
                {
                };
                
            final Future<RudpConnectionId> future =
                    m_service.open ((InetSocketAddress) address, listener);
            
            // TODO: For now, just wait synchronously.
            future.join ();
            
            m_id = future.get ();
            }
        else
            {
            throw new RuntimeException ("Socket already connected");
            }
            */
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public SocketChannel getChannel ()
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getInetAddress ()
        {
        final F1<RudpConnectionId,InetAddress> f =
                new F1<RudpConnectionId,InetAddress> ()
            {
            public InetAddress run (final RudpConnectionId id)
                {
                return id.getRemoteAddress ().getAddress ();
                }
            };
            
        return withId (f);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream () throws IOException
        {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isConnected())
            throw new SocketException("Socket is not connected");
        if (isInputShutdown())
            throw new SocketException("Socket input is shutdown");
        return this.m_inputStream;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getKeepAlive () throws SocketException
        {
        return false;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getLocalAddress ()
        {
        final F1<RudpConnectionId,InetAddress> f =
                new F1<RudpConnectionId,InetAddress> ()
            {
            public InetAddress run (final RudpConnectionId id)
                {
                return id.getLocalAddress ().getAddress ();
                }
            };
            
        return withId (f);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLocalPort ()
        {
        final F1<RudpConnectionId,Integer> f =
                new F1<RudpConnectionId,Integer> ()
            {
            public Integer run (final RudpConnectionId id)
                {
                return id.getLocalAddress ().getPort ();
                }
            };
            
        return withId (f);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public SocketAddress getLocalSocketAddress ()
        {
        final F1<RudpConnectionId,SocketAddress> f =
                new F1<RudpConnectionId,SocketAddress> ()
            {
            public SocketAddress run (final RudpConnectionId id)
                {
                return id.getLocalAddress ();
                }
            };
            
        return withId (f);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getOOBInline () throws SocketException
        {
        return false;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getOutputStream () throws IOException
        {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isConnected())
            throw new SocketException("Socket is not connected");
        if (isOutputShutdown())
            throw new SocketException("Socket output is shutdown");
        return this.m_outputStream;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort ()
        {
        final F1<RudpConnectionId,Integer> f =
            new F1<RudpConnectionId,Integer> ()
            {
            public Integer run (final RudpConnectionId id)
                {
                return id.getRemoteAddress ().getPort ();
                }
            };
            
        return withId (f);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getReceiveBufferSize () throws SocketException
        {
        //throw new NotYetImplementedException ();
        // This is just the default size we've seen on OSX.
        return 81660;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public SocketAddress getRemoteSocketAddress ()
        {
        final F1<RudpConnectionId,SocketAddress> f =
                new F1<RudpConnectionId,SocketAddress> ()
            {
            public SocketAddress run (final RudpConnectionId id)
                {
                return id.getRemoteAddress ();
                }
            };
            
        return withId (f);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getReuseAddress () throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSendBufferSize () throws SocketException
        {
        //throw new NotYetImplementedException ();
        // This is just the default size we've seen on OSX.
        return 81660; 
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSoLinger () throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSoTimeout () throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getTcpNoDelay () throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTrafficClass () throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBound ()
        {
        return m_state == State.BOUND || m_state == State.CONNECTED;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed ()
        {
        return m_state == State.CLOSED;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected ()
        {
        return m_state == State.CONNECTED;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInputShutdown ()
        {
        return this.m_shutIn;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOutputShutdown ()
        {
        return this.m_shutOut;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendUrgentData (final int data) throws IOException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setKeepAlive (final boolean on) throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOOBInline (final boolean on) throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPerformancePreferences (final int connectionTime,
        final int latency, final int bandwidth)
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReceiveBufferSize (final int size) throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReuseAddress (final boolean on) throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSendBufferSize (final int size) throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSoLinger (final boolean on, final int linger) 
        throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSoTimeout (final int timeout) throws SocketException
        {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (timeout < 0)
            throw new IllegalArgumentException("timeout can't be negative");

        this.m_service.getManager().setSoTimeout(this.m_id, timeout);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTcpNoDelay (final boolean on) throws SocketException
        {
        // We have not way of implementing this over RUDP for now.
        //throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTrafficClass (final int tc) throws SocketException
        {
        m_log.error("Not yet implemented");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdownInput () throws IOException
        {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isConnected())
            throw new SocketException("Socket is not connected");
        if (isInputShutdown())
            throw new SocketException("Socket input is already shutdown");
        m_log.debug("Closing input stream...");
        this.m_inputStream.close();
        this.m_shutIn = true;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdownOutput () throws IOException
        {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isConnected())
            throw new SocketException("Socket is not connected");
        if (isOutputShutdown())
            throw new SocketException("Socket output is already shutdown");
        m_log.debug("Closing output stream...");
        this.m_outputStream.close();
        this.m_shutOut = true;
        }
    }
