package org.lastbamboo.common.util.mina;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.DefaultIoFilterChainBuilder;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoServiceListener;
import org.littleshoot.mina.common.SimpleByteBufferAllocator;
import org.littleshoot.mina.common.ThreadModel;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.littleshoot.mina.filter.codec.ProtocolCodecFilter;
import org.littleshoot.mina.filter.executor.ExecutorFilter;
import org.littleshoot.mina.transport.socket.nio.SocketAcceptor;
import org.littleshoot.mina.transport.socket.nio.SocketAcceptorConfig;
import org.littleshoot.util.DaemonThreadFactory;
import org.littleshoot.util.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A MINA TCP server.
 */
public class MinaTcpServer implements MinaServer
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final SocketAcceptor m_acceptor;
    private final IoHandler m_handler;
    
    /**
     * Creates a new MINA TCP server.
     * 
     * @param codecFactory The codec factory to use with the acceptor.
     * @param ioServiceListener The listener for IO service events.
     * @param handler The {@link IoHandler} for processing incoming data.
     */
    public MinaTcpServer(final ProtocolCodecFactory codecFactory, 
        final IoServiceListener ioServiceListener, final IoHandler handler)
        {
        this(codecFactory, ioServiceListener, handler, "MINA-Server-Thread");
        }

    /**
     * Creates a new MINA TCP server.
     * 
     * @param codecFactory The codec factory to use with the acceptor.
     * @param ioServiceListener The listener for IO service events.
     * @param handler The {@link IoHandler} for processing incoming data.
     * @param baseThreadName The base name that will be used for threads
     * processing data arriving on the server.
     */
    public MinaTcpServer(final ProtocolCodecFactory codecFactory, 
        final IoServiceListener ioServiceListener, final IoHandler handler,
        final String baseThreadName)
        {
        if (ioServiceListener == null)
            {
            m_log.error("No IO Service Listener");
            throw new NullPointerException("Null listener");
            }
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        m_handler = handler;
        final Executor executor = Executors.newCachedThreadPool(
            new DaemonThreadFactory(baseThreadName+"-Mina-TCP-Server"));
        m_acceptor = new SocketAcceptor(4, executor);

        final SocketAcceptorConfig cfg = m_acceptor.getDefaultConfig();
        
        cfg.setThreadModel(ThreadModel.MANUAL);

        // Just hoping this method does what it sounds like it does.
        cfg.setDisconnectOnUnbind(true);

        cfg.setReuseAddress(true);
        cfg.getSessionConfig().setReuseAddress(true);
        
        m_acceptor.addListener(ioServiceListener);

        final DefaultIoFilterChainBuilder filterChainBuilder = 
            cfg.getFilterChain();
        final ProtocolCodecFilter codecFilter = 
            new ProtocolCodecFilter(codecFactory);
        filterChainBuilder.addLast("codec", codecFilter);
        filterChainBuilder.addLast("threadPool", new ExecutorFilter(executor));
        m_log.debug("Started MINA TCP server.");
        }

    public void start(final int port) throws IOException
        {
        final InetSocketAddress address = 
            new InetSocketAddress(NetworkUtils.getLocalHost(), port);
        m_acceptor.bind(address, m_handler);
        }

    public void stop()
        {
        this.m_acceptor.unbindAll();
        }

    public void addIoServiceListener(final IoServiceListener serviceListener)
        {
        if (serviceListener == null)
            {
            throw new NullPointerException("Null listener");
            }
        this.m_acceptor.addListener(serviceListener);
        }

    }
