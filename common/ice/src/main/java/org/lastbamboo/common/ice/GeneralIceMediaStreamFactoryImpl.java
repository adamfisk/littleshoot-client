package org.lastbamboo.common.ice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;

import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidateGatherer;
import org.lastbamboo.common.ice.candidate.IceCandidatePairFactory;
import org.lastbamboo.common.ice.candidate.IceCandidatePairFactoryImpl;
import org.lastbamboo.common.ice.candidate.UdpIceCandidateGatherer;
import org.lastbamboo.common.ice.transport.IceUdpConnector;
import org.lastbamboo.common.stun.stack.StunIoHandler;
import org.lastbamboo.common.stun.stack.StunProtocolCodecFactory;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorFactory;
import org.lastbamboo.common.stun.stack.transaction.StunTransactionTracker;
import org.lastbamboo.common.stun.stack.transaction.StunTransactionTrackerImpl;
import org.lastbamboo.common.turn.client.TurnClientListener;
import org.littleshoot.util.CandidateProvider;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating media streams.  This factory offers a more complex
 * API intended for specialized ICE implementations of the simpler
 * {@link IceMediaStreamFactory} interface to use behind the scenes.
 */
public class GeneralIceMediaStreamFactoryImpl 
    implements GeneralIceMediaStreamFactory
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final CandidateProvider<InetSocketAddress> m_stunServerCandidateProvider;
    
    /**
     * Creates a new ICE media stream factory with the specified candidate 
     * providers for connecting to TURN and STUN servers.
     * 
     * @param stunServerCandidateProvider The STUN server address provider.
     */
    public GeneralIceMediaStreamFactoryImpl(
        final CandidateProvider<InetSocketAddress> stunServerCandidateProvider) 
        {
        this.m_stunServerCandidateProvider = stunServerCandidateProvider;
        }
    
    public <T> IceMediaStream newIceMediaStream(
        final IceMediaStreamDesc streamDesc, final IceAgent iceAgent, 
        final TurnClientListener delegateTurnClientListener) 
        throws IceUdpConnectException
        {
        final ProtocolCodecFactory codecFactory =
            new StunProtocolCodecFactory();
        
        //final DemuxableProtocolCodecFactory stunCodecFactory =
        //    new StunDemuxableProtocolCodecFactory();
        //final ProtocolCodecFactory demuxingCodecFactory = 
        //    new DemuxingProtocolCodecFactory(
        //        stunCodecFactory, protocolCodecFactory);
        final StunTransactionTracker<StunMessage> transactionTracker =
            new StunTransactionTrackerImpl();

        final IceStunCheckerFactory checkerFactory =
            new IceStunCheckerFactoryImpl(transactionTracker);
        
        final StunMessageVisitorFactory messageVisitorFactory =
            new IceStunConnectivityCheckerFactoryImpl<StunMessage>(iceAgent, 
                transactionTracker, checkerFactory);
        final IoHandler udpIoHandler = 
            new StunIoHandler<StunMessage>(messageVisitorFactory);
        /*
        final IoHandler udpIoHandler = 
            new DemuxingIoHandler<StunMessage, T>(
                StunMessage.class, stunIoHandler, protocolMessageClass, 
                udpProtocolIoHandler);
                */

        final IceStunUdpPeer udpStunPeer;
        if (streamDesc.isUdp())
            {
            try
                {
                udpStunPeer = 
                    new IceStunUdpPeer(codecFactory, udpIoHandler,
                        iceAgent.isControlling(), transactionTracker, 
                        this.m_stunServerCandidateProvider);
                }
            catch (final IOException e)
                {
                // Note the constructor of the peer attempts a connection, so
                // the exception is named correctly.
                m_log.warn("Error connecting UDP peer", e);
                throw new IceUdpConnectException("Could not create UDP peer", e);
                }
            //udpStunPeer.addIoServiceListener(udpServiceListener);
            }
        else
            {
            udpStunPeer = null;
            }
        
        final IceCandidateGatherer gatherer =
            new UdpIceCandidateGatherer(udpStunPeer, 
                iceAgent.isControlling(), streamDesc);
        
        final IceMediaStreamImpl stream = new IceMediaStreamImpl(iceAgent, 
            streamDesc, gatherer, udpStunPeer);

        if (udpStunPeer != null)
            {
            udpStunPeer.addIoServiceListener(stream);
            }
        
        m_log.debug("Added media stream as listener...connecting...");
        
        if (udpStunPeer != null)
            {
            try
                {
                udpStunPeer.connect();
                }
            catch (final IOException e)
                {
                // Note this will not occur because the connect at this 
                // point is effectively a no-op -- the connection takes place
                // immediately in the constructor.
                m_log.warn("Error connecting UDP peer", e);
                // We've got to make sure to close TCP too!!
                udpStunPeer.close();
                throw new IceUdpConnectException("Could not create UDP peer", e);
                }
            }
        
        final Collection<IceCandidate> localCandidates = 
            gatherer.gatherCandidates();
        
        final IceUdpConnector udpConnector = 
            new IceUdpConnector(codecFactory,
                udpIoHandler, iceAgent.isControlling());
        udpConnector.addIoServiceListener(stream);
        //udpConnector.addIoServiceListener(udpServiceListener);

        final IceCandidatePairFactory candidatePairFactory = 
            new IceCandidatePairFactoryImpl(checkerFactory, udpConnector);
        
        final IceCheckList checkList = 
            new IceCheckListImpl(candidatePairFactory, 
                localCandidates);
        final ExistingSessionIceCandidatePairFactory existingSessionPairFactory =
            new ExistingSessionIceCandidatePairFactoryImpl(checkerFactory);
        final IceCheckScheduler scheduler = 
            new IceCheckSchedulerImpl(iceAgent, stream, checkList,
                existingSessionPairFactory);
        stream.start(checkList, localCandidates, scheduler);
        return stream;
        }
    }
