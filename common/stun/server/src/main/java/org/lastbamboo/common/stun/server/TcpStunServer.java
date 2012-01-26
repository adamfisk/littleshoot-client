package org.lastbamboo.common.stun.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoServiceListener;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.littleshoot.util.mina.MinaTcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a STUN server.
 */
public class TcpStunServer extends AbstractStunServer
    {

    private final Logger m_log = 
        LoggerFactory.getLogger(TcpStunServer.class);
    private final MinaTcpServer m_server;
    
    /**
     * Creates a new STUN server.
     * 
     * @param ioHandler The {@link IoHandler} to use for processing messages
     * and {@link IoSession}s. 
     * @param threadName Additional string for thread naming to make 
     * debugging easier.
     */
    public TcpStunServer(final ProtocolCodecFactory codecFactory,
        final IoHandler ioHandler, final String threadName)
        {
        super(codecFactory, ioHandler, threadName);
        
        this.m_server = new MinaTcpServer(codecFactory, this, 
            ioHandler, "TCP-STUN-Server-" + threadName);
        }

    @Override
    protected void bind(final InetSocketAddress bindAddress) throws IOException
        {
        m_log.debug("Running STUN TCP server on: {}", bindAddress);
        m_server.start(bindAddress.getPort());
        }

    public void addIoServiceListener(final IoServiceListener serviceListener)
        {
        this.m_server.addIoServiceListener(serviceListener);
        }

    
    public void close()
        {
        this.m_server.stop();
        }
    }
