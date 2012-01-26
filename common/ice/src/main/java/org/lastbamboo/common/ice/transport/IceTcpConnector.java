package org.lastbamboo.common.ice.transport;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang.SystemUtils;
import org.littleshoot.mina.common.ConnectFuture;
import org.littleshoot.mina.common.ExecutorThreadModel;
import org.littleshoot.mina.common.IoFuture;
import org.littleshoot.mina.common.IoFutureListener;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoServiceListener;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.common.RuntimeIOException;
import org.littleshoot.mina.common.ThreadModel;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.littleshoot.mina.filter.codec.ProtocolCodecFilter;
import org.littleshoot.mina.transport.socket.nio.SocketConnector;
import org.littleshoot.mina.transport.socket.nio.SocketConnectorConfig;
import org.lastbamboo.common.stun.stack.StunDemuxableProtocolCodecFactory;
import org.lastbamboo.common.stun.stack.StunIoHandler;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorFactory;
import org.lastbamboo.common.tcp.frame.TcpFrame;
import org.lastbamboo.common.tcp.frame.TcpFrameCodecFactory;
import org.lastbamboo.common.tcp.frame.TcpFrameIoHandler;
import org.littleshoot.util.mina.DemuxableProtocolCodecFactory;
import org.littleshoot.util.mina.DemuxingIoHandler;
import org.littleshoot.util.mina.DemuxingProtocolCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for establishing TCP connections for ICE. 
 */
public class IceTcpConnector implements IceConnector
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final boolean m_controlling;
    private final DemuxingIoHandler<StunMessage, TcpFrame> m_demuxingIoHandler;
    
    private final Collection<IoServiceListener> m_serviceListeners =
        new LinkedList<IoServiceListener>();

    /**
     * Creates a new connector for connecting to the remote address.
     * 
     * @param messageVisitorFactory The class for visiting received STUN
     * messages.
     * @param controlling Whether or not this agent is controlling.
     */
    public IceTcpConnector(
        final StunMessageVisitorFactory messageVisitorFactory, 
        final boolean controlling)
        {
        m_controlling = controlling;
        // TODO: We don't currently support TCP-SO, so we don't bind to the 
        // local port.
        final IoHandler stunIoHandler = 
            new StunIoHandler<StunMessage>(messageVisitorFactory);

        final TcpFrameIoHandler streamIoHandler = 
            new TcpFrameIoHandler();
        this.m_demuxingIoHandler = 
            new DemuxingIoHandler<StunMessage, TcpFrame>(
                StunMessage.class, stunIoHandler, 
                TcpFrame.class, streamIoHandler);
        }

    public IoSession connect(final InetSocketAddress localAddress,
        final InetSocketAddress remoteAddress)
        {
        m_log.debug("Creating TCP connection from "+localAddress+" to "+
            remoteAddress);
        final SocketConnector connector = new SocketConnector();
        
        synchronized (this.m_serviceListeners)
            {
            for (final IoServiceListener listener : this.m_serviceListeners)
                {
                connector.addListener(listener);
                }
            }

        final SocketConnectorConfig cfg = connector.getDefaultConfig();
        
        // There's a bug with keep alive and reuse address in Java for Vista,
        // presumably related to Java's failure to properly use Vista's new
        // networking stack.
        // See: https://issues.apache.org/jira/browse/DIRMINA-379
        if (SystemUtils.IS_OS_WINDOWS_VISTA)
            {
            cfg.getSessionConfig().setKeepAlive(false);
            }
        cfg.getSessionConfig().setReuseAddress(true);
        
        final ThreadModel threadModel = ExecutorThreadModel.getInstance(
            getClass().getSimpleName() +
            (this.m_controlling ? "-Controlling-" : "-Not-Controlling-") +
            hashCode());
        cfg.setThreadModel(threadModel);
        connector.setDefaultConfig(cfg);
        
        
        final DemuxableProtocolCodecFactory stunCodecFactory =
            new StunDemuxableProtocolCodecFactory();
        final DemuxableProtocolCodecFactory tcpFramingCodecFactory =
            new TcpFrameCodecFactory();
        final ProtocolCodecFactory demuxingCodecFactory = 
            new DemuxingProtocolCodecFactory(stunCodecFactory, 
                tcpFramingCodecFactory);
        
        final ProtocolCodecFilter demuxingFilter = 
            new ProtocolCodecFilter(demuxingCodecFactory);
        
        connector.getFilterChain().addLast("demuxingFilter", demuxingFilter);

        m_log.debug("Establishing TCP connection to: {}", remoteAddress);
        final InetAddress address = remoteAddress.getAddress();
        final int connectTimeout;
        // If the address is on the local network, we should be able to 
        // connect more quickly.  If we can't, that likely indicates the 
        // address is just from a different local network.
        if (address.isSiteLocalAddress())
            {
            // We used to use this "optimization" to check if an address is
            // reachable prior to attempting a connection, but it ends up 
            // taking a long time in many cases and is therefore not much of 
            // an optimization.
            
            /*
            try
                {
                // Note, we used to put the timeout at 600 milliseconds.  You'd
                // think this would be way more than enough time to connect
                // to another host on the local network, but it turns out it's
                // not even enough time for Vista to check if the address is
                // reachable if the address is localhost!  Odd, but true.
                final int reachableTimeout;
                if (SystemUtils.IS_OS_WINDOWS_VISTA)
                    {
                    reachableTimeout = 5000;
                    }
                else
                    {
                    reachableTimeout = 3000;
                    }
                if (!address.isReachable(reachableTimeout))
                    {
                    m_log.debug("Address is not reachable: {}", remoteAddress);
                    return null;
                    }
                }
            catch (final IOException e)
                {
                m_log.debug("Exception checking reachability", e);
                return null;
                }
            m_log.debug("Address is reachable. Connecting to: {}", 
                 remoteAddress);
                 */

            // We should be able to connect to local, private addresses 
            // really quickly.  So don't wait around too long.
            connectTimeout = 6000;
            }
        else
            {
            connectTimeout = 10000;
            }
        
        m_log.debug("Connecting with timeout: {}", connectTimeout);
        final ConnectFuture cf = 
            connector.connect(remoteAddress, this.m_demuxingIoHandler);
        final IoFutureListener futureListener = new IoFutureListener()
            {
            public void operationComplete(final IoFuture future)
                {
                m_log.debug("Got future: {}", future);
                m_log.debug("Ready: {}", future.isReady());
                }
            };
        cf.addListener(futureListener);
        cf.join(connectTimeout);
        m_log.debug("Successfully joined...");
        try
            {
            final IoSession session = cf.getSession();
            if (session == null)
                {
                m_log.debug("Session is null!!");
                return null;
                }
            m_log.debug("TCP STUN checker connected on: {}",session);
            return session;
            }
        catch (final RuntimeIOException e)
            {
            // This happens when we can't connect.
            m_log.debug("Could not connect to host: {}", remoteAddress);
            m_log.debug("Reason for no connection: ", e);
            return null;
            }
        }
    
    public void addIoServiceListener(final IoServiceListener serviceListener)
        {
        this.m_serviceListeners.add(serviceListener);
        }
    }
