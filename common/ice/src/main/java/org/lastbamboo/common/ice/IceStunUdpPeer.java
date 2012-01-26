package org.lastbamboo.common.ice;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoServiceListener;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.lastbamboo.common.stun.client.StunClient;
import org.lastbamboo.common.stun.client.UdpStunClient;
import org.lastbamboo.common.stun.server.StunServer;
import org.lastbamboo.common.stun.server.UdpStunServer;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.transaction.StunTransactionTracker;
import org.littleshoot.util.CandidateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ICE STUN peer class for UDP.  This is basically just a STUN client for 
 * candidate gathering and a STUN server for processing incoming requests
 * from peer reflexive candidates.
 * 
 * NOTE: This class takes a little work to wrap one's head around as far as
 * the socket SO_REUSEADDRESS option goes.  Basically, we run both a client
 * and a server on the same port.  We're using MINA as the underlying IO
 * framework, though, and MINA has connectors and acceptors.  MINA calls 
 * connect on the underlying DatagramChannel for the connector.  When that call
 * is made, the channel will only accept incoming data from the host it's
 * connected to.  We also bind to that port with an accepting channel,
 * though, so incoming data from other hosts goes to the accepting channel.<p>
 * 
 * Note also that the behavior for what packets go where differs by operating 
 * system.  On Windows, for example, packets from a host that has had 
 * DatagramSocket.connect() called for that host will not necessarily go to 
 * the "connected" host when there's another server socket bound to that port 
 * (using SO_REUSEADDRESS).  If you followed that sentence, this means that 
 * both the connected "client" and the listening "server" message handling 
 * code needs to be prepared to be prepared to handle any message. 
 */
public class IceStunUdpPeer implements StunClient, StunServer
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final StunClient m_stunClient;
    private final StunServer m_stunServer;

    private InetSocketAddress m_serverReflexiveAddress;
    
    /**
     * Creates a new ICE STUN UDP peer.
     * @param demuxingCodecFactory The class for interpreting the multiple 
     * protocols on the wire -- STUN and whatever protocol it's negotiating
     * a connection for.
     * @param ioHandler The class for handling read and written 
     * messages. 
     * @param controlling Whether or not this agent is controlling.
     * @param transactionTracker The class for tracking STUN transactions.
     * @throws IOException If there's an error connecting the client or server.
     */
    public IceStunUdpPeer(final ProtocolCodecFactory demuxingCodecFactory, 
        final IoHandler ioHandler, final boolean controlling, 
        final StunTransactionTracker<StunMessage> transactionTracker,
        final CandidateProvider<InetSocketAddress> stunServerCandidateProvider) 
        throws IOException 
        {
        // We pass the IoHandler here because we need to be prepared to handle 
        // STUN and protocol specific messages on all code bound to the same 
        // local port.  This is because different OSes handle  
        // SO_REUSEADDRESS slightly differently, particularly with regards to 
        // sockets that are "connected" to specific external hosts.  We need 
        // to use the same IoHandler for all of them to make sure it's 
        // created here as well as the separate connectors created when we're 
        // making connectivity consistent, with "all of them" meaning the STUN 
        // client and server checks.
        this.m_stunClient = 
            new UdpStunClient(transactionTracker, ioHandler, 
                stunServerCandidateProvider);
        this.m_stunClient.connect();
        this.m_serverReflexiveAddress = 
            this.m_stunClient.getServerReflexiveAddress();
        if (this.m_serverReflexiveAddress == null)
            {
            final String msg = "Could not get server reflexive address.  " +
                "Did STUN server respond??";
            m_log.error(msg);
            throw new IOException(msg);
            }
        // We also add whether we're controlling for thread
        // naming here just to make log reading easier.
        final String controllingString;
        if (controlling)
            {
            controllingString = "-Controlling";
            }
        else
            {
            controllingString = "-Not-Controlling";
            }
        
        // NOTE: We're starting the server here before external code has
        // had the chance to add listeners.  In this case, it will be fine
        // because the caller cannot have sent the offer or answer until
        // the listeners are added (or SHOULD not have), so there's no way
        // of missing any relevant events.
        this.m_stunServer = 
            new UdpStunServer(demuxingCodecFactory, 
                ioHandler, controllingString);
        
        // Just bind to the same port as the client.
        // Note this only works because both the client and server are using 
        // the SO_REUSEADDRESS option.
        this.m_stunServer.start(this.m_stunClient.getHostAddress());
        
        m_log.debug("Started STUN client on local address: {}",
            this.m_stunClient.getHostAddress());
        m_log.debug("Started STUN server on local address: {}",
            this.m_stunServer.getBoundAddress());
        
        }

    public void connect() throws IOException
        {
        // We don't do anything here because the client is already connected
        // in the constructor!!
        //this.m_stunClient.connect();
        }

    public InetSocketAddress getHostAddress()
        {
        return this.m_stunClient.getHostAddress();
        }

    public InetSocketAddress getRelayAddress()
        {
        return this.m_stunClient.getRelayAddress();
        }

    public InetSocketAddress getServerReflexiveAddress()
        {
        m_log.info("Getting server reflexive address");
        // We return the cached server reflexive address because we need to
        // get it before the "server side" UDP handler binds to the same 
        // port, as it can "steal" incoming packets on Windows.
        return this.m_serverReflexiveAddress;
        }

    public InetAddress getStunServerAddress()
        {
        return this.m_stunClient.getStunServerAddress();
        }

    public StunMessage write(final BindingRequest request, 
        final InetSocketAddress remoteAddress)
        {
        //return this.m_stunClient.write(request, remoteAddress);
        m_log.error("Unsupported!!!!!!!");
        throw new IllegalStateException("Not implemented.");
        }
    
    public StunMessage write(final BindingRequest request, 
        final InetSocketAddress remoteAddress, final long rto)
        {
        //return this.m_stunClient.write(request, remoteAddress, rto);
        m_log.error("Unsupported!!!!!!!");
        throw new IllegalStateException("Not implemented.");
        }

    public void start()
        {
        // We've already started the server for ICE.
        }

    public void start(final InetSocketAddress bindAddress)
        {
        // We've already started the server for ICE.
        }

    public InetSocketAddress getBoundAddress()
        {
        return this.m_stunServer.getBoundAddress();
        }

    public void addIoServiceListener(final IoServiceListener serviceListener)
        {
        this.m_stunClient.addIoServiceListener(serviceListener);
        this.m_stunServer.addIoServiceListener(serviceListener);
        }
    

    public void close()
        {
        m_log.debug("Closing ICE UDP peer...");
        this.m_stunClient.close();
        this.m_stunServer.close();
        }

    public boolean hostPortMapped()
        {
        // We don't currently do any mapping for UDP.
        return false;
        }
    
    public StunServer getStunServer() 
        {
        return m_stunServer;
        }
    
    @Override
    public String toString()
        {
        return getClass().getSimpleName();
        }
    }
