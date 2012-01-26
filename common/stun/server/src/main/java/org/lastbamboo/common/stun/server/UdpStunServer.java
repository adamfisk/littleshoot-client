package org.lastbamboo.common.stun.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoServiceListener;
import org.littleshoot.mina.common.ThreadModel;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.littleshoot.mina.filter.codec.ProtocolCodecFilter;
import org.littleshoot.mina.filter.executor.ExecutorFilter;
import org.littleshoot.mina.transport.socket.nio.DatagramAcceptor;
import org.littleshoot.mina.transport.socket.nio.DatagramAcceptorConfig;
import org.lastbamboo.common.stun.stack.StunIoHandler;
import org.lastbamboo.common.stun.stack.StunProtocolCodecFactory;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorFactory;
import org.littleshoot.util.DaemonThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a STUN server.
 */
public class UdpStunServer extends AbstractStunServer
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(UdpStunServer.class);
    
    private final ExecutorService m_threadPool;
    
    private final DatagramAcceptor m_acceptor;
    
    /**
     * Creates a new STUN server.
     * 
     * @param codecFactory The class for creating codec handlers. 
     * @param ioHandler The class for processing STUN I/O events.
     * @param threadName Additional string for thread naming to make 
     * debugging easier.
     */
    public UdpStunServer(final ProtocolCodecFactory codecFactory, 
        final IoHandler ioHandler, final String threadName)
        {
        super(codecFactory, ioHandler, threadName);
        m_threadPool = Executors.newCachedThreadPool(
            new DaemonThreadFactory("UDP-STUN-Server-Thread-Pool-"+threadName));
        m_acceptor = new DatagramAcceptor(m_threadPool);
        }
    
    /**
     * Creates a new STUN server.
     * 
     * @param visitorFactory The factory for creating classes for visiting 
     * STUN messages and handling them appropriately as they're read.
     * @param threadName Additional string for thread naming to make 
     * debugging easier.
     */
    public UdpStunServer(final StunMessageVisitorFactory visitorFactory, 
        final String threadName)
        {
        this(new StunProtocolCodecFactory(), 
            new StunIoHandler(visitorFactory), threadName);
        }
    
    @Override
    protected void bind(final InetSocketAddress bindAddress)
        {
        m_acceptor.addListener(this);
        

        final DatagramAcceptorConfig config = m_acceptor.getDefaultConfig();
        config.setThreadModel(ThreadModel.MANUAL);
        config.getSessionConfig().setReuseAddress(true);
        
        final ProtocolCodecFilter codecFilter = 
            new ProtocolCodecFilter(this.m_codecFactory);
        config.getFilterChain().addLast("stunFilter", codecFilter);
        config.getFilterChain().addLast("executor", 
            new ExecutorFilter(m_threadPool));
        
        try
            {
            m_acceptor.bind(bindAddress, this.m_ioHandler, config);
            LOG.debug("Started STUN server!!");
            }
        catch (final IOException e)
            {
            LOG.error("Could not bind server", e);
            }
        }

    public void addIoServiceListener(final IoServiceListener serviceListener)
        {
        this.m_acceptor.addListener(serviceListener);
        }
    
    public void close()
        {
        this.m_acceptor.unbindAll();
        }

    }
