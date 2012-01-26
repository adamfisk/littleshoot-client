package org.lastbamboo.common.ice.transport;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.LinkedList;

import org.littleshoot.mina.common.ConnectFuture;
import org.littleshoot.mina.common.ExecutorThreadModel;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoService;
import org.littleshoot.mina.common.IoServiceConfig;
import org.littleshoot.mina.common.IoServiceListener;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.common.RuntimeIOException;
import org.littleshoot.mina.common.ThreadModel;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.littleshoot.mina.filter.codec.ProtocolCodecFilter;
import org.littleshoot.mina.transport.socket.nio.DatagramConnector;
import org.littleshoot.mina.transport.socket.nio.DatagramConnectorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for creating a UDP "connection" for ICE.  This really just sets up
 * the UDP transport for a UDP connectivity check. 
 */
public class IceUdpConnector implements IceConnector, IoServiceListener
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final ProtocolCodecFactory m_demuxingCodecFactory;
    private final IoHandler m_demuxingIoHandler;
    private final boolean m_controlling;
    
    private final Collection<IoServiceListener> m_serviceListeners =
        new LinkedList<IoServiceListener>();
    private final DatagramConnector m_connector;

    /**
     * Creates a new UDP connector.
     * 
     * @param protocolCodecFactory The class for interpreting the protocol
     * for the connection.
     * @param demuxingIoHandler The class for processing read and written
     * messages.
     * @param controlling Whether or not we're the controlling agent.
     */
    public IceUdpConnector(
        final ProtocolCodecFactory protocolCodecFactory,
        final IoHandler demuxingIoHandler, final boolean controlling)
        {
        m_demuxingCodecFactory = protocolCodecFactory;
        m_demuxingIoHandler = demuxingIoHandler;
        m_controlling = controlling;
        this.m_connector = new DatagramConnector();
        this.m_connector.addListener(this);
        }

    public IoSession connect(final InetSocketAddress localAddress,
        final InetSocketAddress remoteAddress)
        {
        synchronized (this.m_serviceListeners)
            {
            for (final IoServiceListener listener : this.m_serviceListeners)
                {
                this.m_connector.addListener(listener);
                }
            }
        final DatagramConnectorConfig cfg = this.m_connector.getDefaultConfig();
        cfg.getSessionConfig().setReuseAddress(true);
        
        final ThreadModel threadModel = ExecutorThreadModel.getInstance(
            getClass().getSimpleName() + 
            (this.m_controlling ? "-Controlling" : "-Not-Controlling"));
        
        final ProtocolCodecFilter demuxingFilter = 
            new ProtocolCodecFilter(this.m_demuxingCodecFactory);
        cfg.setThreadModel(threadModel);
        
        this.m_connector.getFilterChain().addLast("demuxFilter", demuxingFilter);

        m_log.debug("Connecting from "+localAddress+" to "+
            remoteAddress);
        
        final ConnectFuture cf = 
            this.m_connector.connect(remoteAddress, localAddress, 
                this.m_demuxingIoHandler);
        
        cf.join();
        try
            {
            final IoSession session = cf.getSession();
            if (session == null)
                {
                m_log.error("Could not create session from "+
                    localAddress +" to "+remoteAddress);
                throw new RuntimeIOException("Could not create session");
                }
            if (!session.isConnected())
                {
                throw new RuntimeIOException("Not connected");
                }
            return session;
            }
        catch (final RuntimeIOException e)
            {
            // I've seen this happen when the local address is already bound
            // for some reason (clearly without SO_REUSEADDRESS somehow).
            m_log.error("Could not create session from "+ localAddress +" to "+
                remoteAddress+" -- look at the CAUSE!!!", e);
            throw e;
            }
        }

    public void addIoServiceListener(final IoServiceListener serviceListener)
        {
        this.m_serviceListeners.add(serviceListener);
        }

    public void serviceActivated(IoService arg0, SocketAddress arg1,
            IoHandler arg2, IoServiceConfig arg3) {
        // TODO Auto-generated method stub
        
    }

    public void serviceDeactivated(IoService arg0, SocketAddress arg1,
            IoHandler arg2, IoServiceConfig arg3) {
        // TODO Auto-generated method stub
        
    }

    public void sessionCreated(IoSession arg0) {
        // TODO Auto-generated method stub
        
    }

    public void sessionDestroyed(final IoSession session) {
        m_log.info("Got session closed: {}", session);
        try {
            this.m_connector.getFilterChain().clear();
        } catch (final Exception e) {
            m_log.warn("Exception clearing filter chaing!!",e);
        }
        //this.m_connector.getFilterChain().clear();
    }
    }
