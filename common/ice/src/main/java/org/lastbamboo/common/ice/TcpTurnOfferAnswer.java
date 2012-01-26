package org.lastbamboo.common.ice;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;

import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidateVisitor;
import org.lastbamboo.common.ice.candidate.IceCandidateVisitorAdapter;
import org.lastbamboo.common.ice.candidate.IceTcpRelayPassiveCandidate;
import org.lastbamboo.common.ice.sdp.IceCandidateSdpDecoder;
import org.lastbamboo.common.ice.sdp.IceCandidateSdpDecoderImpl;
import org.lastbamboo.common.offer.answer.OfferAnswerListener;
import org.lastbamboo.common.stun.stack.StunProtocolCodecFactory;
import org.lastbamboo.common.turn.client.TcpTurnClient;
import org.lastbamboo.common.turn.client.TurnClientListener;
import org.littleshoot.util.CandidateProvider;
import org.littleshoot.util.ThreadUtils;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes offers and answers for a TURN server connection.
 */
public class TcpTurnOfferAnswer implements IceOfferAnswer 
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final TcpTurnClient m_turnClient;
    private final boolean m_controlling;
    private ByteBuffer m_encodedCandidates;
    private final OfferAnswerListener m_offerAnswerListener;

    /**
     * Creates a new TURN offer/answer.
     * 
     * @param turnCandidateProvider The class that provides candidate TURN
     * servers to connect to.
     * @param localServerAddress The address of the local HTTP server to relay
     * traffic to.
     * @param controlling Whether or not this is the controlling ICE agent.
     * @param offerAnswerListener The class to notify of sockets.
     */
    public TcpTurnOfferAnswer(
        final CandidateProvider<InetSocketAddress> turnCandidateProvider,
        final boolean controlling,
        final OfferAnswerListener offerAnswerListener,
        final TurnClientListener clientListener) 
        {
        this.m_controlling = controlling;
        this.m_offerAnswerListener = offerAnswerListener;
        final ProtocolCodecFactory codecFactory = 
            new StunProtocolCodecFactory();
        this.m_turnClient = 
            new TcpTurnClient(clientListener, turnCandidateProvider, 
                codecFactory);
        }
    
    /**
     * Connects to the TURN server
     * 
     * @throws IOException If we can't connect.
     */
    public void connect() throws IOException 
        {
        this.m_turnClient.connect();
        }
    
    public void close() 
        {
        this.m_turnClient.close();
        }

    public void closeTcp() 
        {
        this.m_turnClient.close();
        }

    public void closeUdp() 
        {
        }

    public byte[] generateAnswer() 
        {
        return null;
        }

    public byte[] generateOffer() 
        {
        return null;
        }

    public void processAnswer(final ByteBuffer answer) 
        {
        this.m_encodedCandidates = answer;
        }

    public void processOffer(final ByteBuffer offer) 
        {
        this.m_encodedCandidates = offer;
        }

    public Collection<? extends IceCandidate> gatherCandidates() 
        {
        m_log.info("Gathering TURN candidates");
        // For relayed candidates, the related address is the mapped 
        // address.
        final InetSocketAddress relatedAddress = 
            m_turnClient.getServerReflexiveAddress();
        
        final InetAddress stunServerAddress = 
            m_turnClient.getStunServerAddress();
        
        final InetSocketAddress relayAddress = 
            this.m_turnClient.getRelayAddress();
        final IceCandidate relayCandidate = 
            new IceTcpRelayPassiveCandidate(relayAddress, 
                stunServerAddress, relatedAddress.getAddress(), 
                relatedAddress.getPort(), this.m_controlling);
        
        return Arrays.asList(relayCandidate);
        }

    public InetAddress getPublicAdress() 
        {
        return this.m_turnClient.getMappedAddress().getAddress();
        }

    private void processRemoteCandidates(final ByteBuffer encodedCandidates) 
        {
        m_log.info("Decoding TURN relay candidates");
        final IceCandidateSdpDecoder decoder = new IceCandidateSdpDecoderImpl();
        final Collection<IceCandidate> remoteCandidates;
        try
            {
            // Note the second argument doesn't matter at all.
            remoteCandidates = decoder.decode(encodedCandidates, false);
            }
        catch (final IOException e)
            {
            m_log.warn("Could not process remote candidates", e);
            return;
            }

        m_log.info("Processing TURN relay candidates: {}", remoteCandidates);
        
        // OK, we've got the candidates. We'll now parallelize connection 
        // attempts to all of them, taking the first to succeed. Note there's 
        // typically a single local network candidate that will only succeed
        // if we're on same subnet and then a public candidate that's 
        // either there because the remote host is on the public Internet or
        // because the public address was mapped using UPnP.
        final IceCandidateVisitor<Object> visitor = 
            new IceCandidateVisitorAdapter<Object>()
                {
                
                @Override
                public Object visitTcpRelayPassiveCandidate(
                    final IceTcpRelayPassiveCandidate candidate)
                    {
                    m_log.info("Connecting to turn candidate");
                    connectToCandidate(candidate);
                    return null;
                    }
                };
        for (final IceCandidate candidate : remoteCandidates)
            {
            candidate.accept(visitor);
            }
        }
    
    public void useRelay() 
        {
        m_log.info("Using relay");
        // We wait until here to process the TURN candidates. If there is a
        // relay candidate, we'll connect to it.
        processRemoteCandidates(this.m_encodedCandidates);
        }
    
    private void connectToCandidate(final IceCandidate candidate) 
        {
        if (candidate == null) 
            {
            m_log.warn("Null candidate?? "+ThreadUtils.dumpStack());
            return;
            }
        final Runnable threadRunner = new Runnable()
            {
            public void run()
                {
                Socket sock = null;
                try
                    {
                    m_log.info("Connecting to: {}", candidate);
                    sock = new Socket();
                    sock.connect(candidate.getSocketAddress(), 20*1000);
                    m_log.info("Connected to: {}", candidate);
                    m_offerAnswerListener.onTcpSocket(sock);
                    }
                catch (final IOException e)
                    {
                    m_log.warn("Could not connect to relay?", e);
                    }
                }
            };
        final Thread connectorThread = 
            new Thread(threadRunner, "ICE-TCP-Connect-"+candidate);
        connectorThread.setDaemon(true);
        connectorThread.start();
        }
    }
