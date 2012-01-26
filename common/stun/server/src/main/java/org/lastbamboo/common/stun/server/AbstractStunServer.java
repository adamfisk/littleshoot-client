package org.lastbamboo.common.stun.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoService;
import org.littleshoot.mina.common.IoServiceConfig;
import org.littleshoot.mina.common.IoServiceListener;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.common.SimpleByteBufferAllocator;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.littleshoot.util.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a STUN server.
 */
public abstract class AbstractStunServer implements StunServer, 
    IoServiceListener
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(UdpStunServer.class);
    
    /**
     * Use the default STUN port.
     */
    private static final int STUN_PORT = 3478;

    private InetSocketAddress m_boundAddress;

    protected final String m_threadName;

    protected final ProtocolCodecFactory m_codecFactory;

    protected final IoHandler m_ioHandler;
    
    /**
     * Creates a new STUN server.
     * 
     * @param codecFactory The factory for creating STUN codecs.
     * @param ioHandler The IO handler, often a demuxing handler that
     * demultiplexes STUN with the media protocol.
     * @param threadName The name of the thread to use.
     */
    public AbstractStunServer(final ProtocolCodecFactory codecFactory, 
        final IoHandler ioHandler, final String threadName)
        {
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        m_codecFactory = codecFactory;
        m_ioHandler = ioHandler;
        m_threadName = threadName;
        }

    public void start() throws IOException
        {
        start(new InetSocketAddress(NetworkUtils.getLocalHost(),STUN_PORT));
        }
    
    public void start(final InetSocketAddress bindAddress) throws IOException
        {
        final InetSocketAddress bindAddressToUse = 
            createBindAddress(bindAddress);

        bind(bindAddressToUse);
        }
    
    protected abstract void bind(final InetSocketAddress bindAddress) 
        throws IOException;

    private static InetSocketAddress createBindAddress(
        final InetSocketAddress bindAddress)
        {
        if (bindAddress == null)
            {
            try
                {
                return new InetSocketAddress(NetworkUtils.getLocalHost(), 0);
                }
            catch (UnknownHostException e)
                {
                LOG.warn("Could not get local host address", e);
                return null;
                }
            }
        else
            {
            return bindAddress;
            }
        }

    public InetSocketAddress getBoundAddress()
        {
        return this.m_boundAddress;
        }

    public void serviceActivated(final IoService service, 
        final SocketAddress serviceAddress, final IoHandler handler, 
        final IoServiceConfig config)
        {
        // Note this is called immediately after the call to bind when
        // starting the server, so the bound address will always be set
        // if start has been called, at least with MINA 1.1.1.
        LOG.debug("Setting bound address to: {}", serviceAddress);
        this.m_boundAddress = (InetSocketAddress) serviceAddress;
        }

    public void serviceDeactivated(final IoService service, 
        final SocketAddress serviceAddress, final IoHandler handler, 
        final IoServiceConfig config)
        {
        LOG.debug("Session deactivated on service address: {}",
            serviceAddress);
        }

    public void sessionCreated(final IoSession session)
        {
        LOG.debug("Session created: {}", session);
        }

    public void sessionDestroyed(final IoSession session)
        {
        LOG.debug("Session destroyed: {}", session);
        }

    }
