package org.lastbamboo.common.util.mina;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.transport.socket.nio.SocketSessionConfig;
import org.littleshoot.util.NotYetImplementedException;
import org.littleshoot.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A socket implementation that wraps a MINA {@link IoSession} in a 
 * {@link Socket} interface.
 */
public final class IoSessionSocket extends Socket
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final IoSession m_ioSession;
    private final InputStream m_in;
    private final OutputStream m_out;
    private final SocketSessionConfig m_socketSessionConfig;

    /**
     * Creates a new {@link Socket} subclass that works with MINA sessions.
     * 
     * @param ioSession The MINA {@link IoSession}.
     * @param in The {@link InputStream}.
     * @param out The {@link OutputStream}.
     */
    public IoSessionSocket(final IoSession ioSession, 
        final InputStream in, final OutputStream out)
        {
        this.m_ioSession = ioSession;
        this.m_in = in;
        this.m_out = out;
        this.m_socketSessionConfig = (SocketSessionConfig)ioSession.getConfig();
        this.m_socketSessionConfig.setKeepAlive(true);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(final SocketAddress address) throws IOException
        {
        m_log.warn("Attempting to bind to local address...");
        // For now, we do not allow binding of the local address.  The ephemeral
        // port will just be chosen.
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException
        {
        m_log.debug("Closing socket from: "+ThreadUtils.dumpStack());
        this.m_ioSession.close();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final SocketAddress address) throws IOException
        {
        connect (address, 60000);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final SocketAddress address, final int timeout) 
        throws IOException
        {
        m_log.warn("We should already be connected!!");
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public SocketChannel getChannel()
        {
        m_log.warn("Not implemented!!!");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getInetAddress()
        {
        return ((InetSocketAddress)this.m_ioSession.getRemoteAddress()).getAddress();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream() throws IOException
        {
        m_log.debug("Returning input stream...");
        return this.m_in;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getKeepAlive() throws SocketException
        {
        return false;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getLocalAddress()
        {
        return ((InetSocketAddress)this.m_ioSession.getLocalAddress()).getAddress();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLocalPort()
        {
        return ((InetSocketAddress)this.m_ioSession.getLocalAddress()).getPort();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public SocketAddress getLocalSocketAddress()
        {
        return this.m_ioSession.getLocalAddress();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getOOBInline() throws SocketException
        {
        return false;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getOutputStream() throws IOException
        {
        m_log.debug("Returning output stream...");
        return this.m_out;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort()
        {
        return ((InetSocketAddress)this.m_ioSession.getRemoteAddress()).getPort();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getReceiveBufferSize() throws SocketException
        {
        return this.m_socketSessionConfig.getReceiveBufferSize();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public SocketAddress getRemoteSocketAddress()
        {
        return this.m_ioSession.getRemoteAddress();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getReuseAddress () throws SocketException
        {
        return this.m_socketSessionConfig.isReuseAddress();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSendBufferSize() throws SocketException
        {
        return this.m_socketSessionConfig.getSendBufferSize();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSoLinger() throws SocketException
        {
        return this.m_socketSessionConfig.getSoLinger();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSoTimeout() throws SocketException
        {
        m_log.warn("Not implemented!!!");
        throw new NotYetImplementedException("SO_TIMEOUT not implemented");
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getTcpNoDelay() throws SocketException
        {
        return this.m_socketSessionConfig.isTcpNoDelay();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTrafficClass() throws SocketException
        {
        return this.m_socketSessionConfig.getTrafficClass();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBound()
        {
        return true;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed()
        {
        return !this.m_ioSession.isConnected();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected()
        {
        return this.m_ioSession.isConnected();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInputShutdown()
        {
        m_log.warn("Not implemented!!!");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOutputShutdown()
        {
        m_log.warn("Not implemented!!!");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendUrgentData(final int data) throws IOException
        {
        this.m_out.write(data);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setKeepAlive(final boolean on) throws SocketException
        {
        this.m_socketSessionConfig.setKeepAlive(on);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOOBInline(final boolean on) throws SocketException
        {
        this.m_socketSessionConfig.setOobInline(on);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPerformancePreferences(final int connectionTime,
        final int latency, final int bandwidth)
        {
        m_log.warn("Not implemented!!!");
        throw new NotYetImplementedException ();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setReceiveBufferSize(final int size) 
        throws SocketException
        {
        this.m_socketSessionConfig.setReceiveBufferSize(size);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReuseAddress(final boolean on) throws SocketException
        {
        this.m_socketSessionConfig.setReuseAddress(on);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSendBufferSize(final int size) throws SocketException
        {
        this.m_socketSessionConfig.setSendBufferSize(size);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSoLinger(final boolean on, final int linger) 
        throws SocketException
        {
        this.m_socketSessionConfig.setSoLinger(linger);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSoTimeout(final int timeout) throws SocketException
        {
        // Ignored since we don't know how to handle it for now.
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTcpNoDelay(final boolean on) throws SocketException
        {
        this.m_socketSessionConfig.setTcpNoDelay(on);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTrafficClass(final int tc) throws SocketException
        {
        this.m_socketSessionConfig.setTrafficClass(tc);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdownInput() throws IOException
        {
        m_log.debug("Closing input stream from: "+ThreadUtils.dumpStack());
        this.m_in.close();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdownOutput() throws IOException
        {
        m_log.debug("Closing output stream from: "+ThreadUtils.dumpStack());
        this.m_out.close();
        }
    }
