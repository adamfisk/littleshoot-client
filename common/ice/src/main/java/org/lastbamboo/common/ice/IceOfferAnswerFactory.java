package org.lastbamboo.common.ice;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;

import javax.net.SocketFactory;

import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.sdp.IceCandidateSdpEncoder;
import org.lastbamboo.common.offer.answer.OfferAnswer;
import org.lastbamboo.common.offer.answer.OfferAnswerConnectException;
import org.lastbamboo.common.offer.answer.OfferAnswerFactory;
import org.lastbamboo.common.offer.answer.OfferAnswerListener;
import org.lastbamboo.common.portmapping.NatPmpService;
import org.lastbamboo.common.portmapping.UpnpService;
import org.lastbamboo.common.stun.client.StunClient;
import org.lastbamboo.common.stun.client.UdpStunClient;
import org.lastbamboo.common.turn.client.TurnClientListener;
import org.littleshoot.util.CandidateProvider;
import org.littleshoot.mina.common.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for creating ICE agents that process ICE offers and answers.
 */
public class IceOfferAnswerFactory implements OfferAnswerFactory {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final IceMediaStreamFactory m_mediaStreamFactory;
    private final UdpSocketFactory m_udpSocketFactory;
    private final IceMediaStreamDesc m_streamDesc;
    private final CandidateProvider<InetSocketAddress> m_turnCandidateProvider;
    private final NatPmpService m_natPmpService;
    private final UpnpService m_upnpService;
    private final MappedTcpAnswererServer m_answererServer;

    private final TurnClientListener m_turnClientListener;

    private final CandidateProvider<InetSocketAddress> m_stunCandidateProvider;

    private InetAddress m_publicAddress;

    private final MappedTcpOffererServerPool m_offererServer;

    private final SocketFactory m_socketFactory;

    /**
     * Creates a new ICE agent factory. The factory maintains a reference to
     * the TCP TURN client because the client holds a persistent connection
     * to the TURN server and is used across all ICE sessions.
     * 
     * @param mediaStreamFactory The factory for creating ICE media streams.
     * @param udpSocketFactory Factory for creating reliable UDP sockets.
     * @param streamDesc Data for the type of stream to create, such as TCP,
     * UPD, or either.
     * @param answererServer The single router port-mapped server socket for
     * when we're the answerer.
     * @param stunCandidateProvider Provider for STUN servers.
     * @param offererServer The pool of mapped servers to send from the
     * offering side.
     */
    public IceOfferAnswerFactory(
            final IceMediaStreamFactory mediaStreamFactory,
            final UdpSocketFactory udpSocketFactory,
            final IceMediaStreamDesc streamDesc,
            final CandidateProvider<InetSocketAddress> turnCandidateProvider,
            final NatPmpService natPmpService, final UpnpService upnpService,
            final MappedTcpAnswererServer answererServer,
            final TurnClientListener turnClientListener, 
            final CandidateProvider<InetSocketAddress> stunCandidateProvider, 
            final MappedTcpOffererServerPool offererServer,
            final SocketFactory socketFactory) {
        this.m_mediaStreamFactory = mediaStreamFactory;
        this.m_udpSocketFactory = udpSocketFactory;
        this.m_streamDesc = streamDesc;
        this.m_turnCandidateProvider = turnCandidateProvider;
        this.m_natPmpService = natPmpService;
        this.m_upnpService = upnpService;
        this.m_answererServer = answererServer;
        this.m_turnClientListener = turnClientListener;
        this.m_stunCandidateProvider = stunCandidateProvider;
        this.m_offererServer = offererServer;
        this.m_socketFactory = socketFactory;
        this.m_publicAddress = determinePublicAddress(stunCandidateProvider);
    }

    public OfferAnswer createAnswerer(
            final OfferAnswerListener offerAnswerListener)
            throws OfferAnswerConnectException {
        return createOfferAnswer(false, offerAnswerListener);
    }

    public OfferAnswer createOfferer(
            final OfferAnswerListener offerAnswerListener)
            throws OfferAnswerConnectException {
        return createOfferAnswer(true, offerAnswerListener);
    }

    private OfferAnswer createOfferAnswer(final boolean controlling,
            final OfferAnswerListener offerAnswerListener)
            throws OfferAnswerConnectException {
        final IceOfferAnswer turnOfferAnswer = newTurnOfferAnswer(controlling,
                offerAnswerListener);
        final IceOfferAnswer udp = newUdpOfferAnswer(controlling,
                offerAnswerListener);

        final IceOfferAnswer tcp = newTcpOfferAnswer(this.m_publicAddress,
                offerAnswerListener, controlling);

        // We create a high-level class that starts a race between the TCP
        // and UDP connections. The TCP approach does not use ICE, instead
        // simplifying things significantly through using straight sockets,
        // either via UPnP, directly over an internal network, or when one of
        // the peers is on the public Internet.
        return new OfferAnswer() {
            public byte[] generateOffer() {
                return encodeCandidates(controlling, tcp, udp, turnOfferAnswer);
            }

            public byte[] generateAnswer() {
                return encodeCandidates(controlling, tcp, udp, turnOfferAnswer);
            }

            public void close() {
                if (tcp != null)
                    tcp.close();
                if (turnOfferAnswer != null)
                    turnOfferAnswer.close();
                if (udp != null)
                    udp.close();
            }

            public void processAnswer(final ByteBuffer answer) {
                m_log.info("Processing answer...");
                m_log.info("Turn offer answer: {}", turnOfferAnswer);
                if (m_streamDesc.isUseRelay() && turnOfferAnswer != null) {
                    turnOfferAnswer.processAnswer(answer.duplicate());
                }
                if (m_streamDesc.isTcp() && tcp != null) {
                    tcp.processAnswer(answer.duplicate());
                }
                if (m_streamDesc.isUdp() && udp != null) {
                    udp.processAnswer(answer.duplicate());
                }
            }

            public void processOffer(final ByteBuffer offer) {
                m_log.info("Processing offer...");
                if (m_streamDesc.isTcp() && tcp != null) {
                    tcp.processOffer(offer);
                }
                if (m_streamDesc.isUdp() && udp != null) {
                    udp.processOffer(offer);
                }
                if (m_streamDesc.isUseRelay() && turnOfferAnswer != null) {
                    turnOfferAnswer.processOffer(offer);
                }
                m_log.info("Done processing offer...");
            }

            public void closeTcp() {
                if (tcp != null)
                    tcp.close();
                if (turnOfferAnswer != null)
                    turnOfferAnswer.close();
            }

            public void closeUdp() {
                if (udp != null)
                    udp.close();
            }

            public void useRelay() {
                m_log.info("Sending use relay notification.");
                if (tcp != null)
                    tcp.useRelay();
                if (turnOfferAnswer != null)
                    turnOfferAnswer.useRelay();
            }
        };
    }
    
    private InetAddress determinePublicAddress(
        final CandidateProvider<InetSocketAddress> provider) {
        try {
            final StunClient stun = new UdpStunClient(provider);
            stun.connect();
            return stun.getServerReflexiveAddress().getAddress();
        } catch (final IOException e) {
            m_log.warn("Could not get server reflexive address", e);
            return null;
        }
    }
    
    private IceOfferAnswer newTcpOfferAnswer(final InetAddress publicAddress,
            final OfferAnswerListener offerAnswerListener,
            final boolean controlling) {
        if (this.m_streamDesc.isTcp()) {
            m_log.info("Creating new TCP offer answer");
            return new TcpOfferAnswer(publicAddress, offerAnswerListener,
                controlling, m_natPmpService, m_upnpService,
                m_answererServer, this.m_stunCandidateProvider, 
                this.m_offererServer, m_socketFactory);
        } else {
            return null;
        }
    }

    private IceOfferAnswer newUdpOfferAnswer(final boolean controlling,
            final OfferAnswerListener offerAnswerListener)
            throws OfferAnswerConnectException {
        if (this.m_streamDesc.isUdp()) {
            try {
                return new IceAgentImpl(this.m_mediaStreamFactory, controlling,
                        offerAnswerListener, this.m_udpSocketFactory);
            } catch (final IceUdpConnectException e) {
                throw new OfferAnswerConnectException(
                        "Could not create UDP connection", e);
            }
        }
        return null;
    }

    private byte[] encodeCandidates(final boolean controlling,
            final IceOfferAnswer tcp, final IceOfferAnswer udp,
            final IceOfferAnswer tcpTurn) {
        final IceCandidateSdpEncoder encoder = new IceCandidateSdpEncoder(
                m_streamDesc.getMimeContentType(),
                m_streamDesc.getMimeContentSubtype());

        final Collection<IceCandidate> localCandidates = 
            new HashSet<IceCandidate>();
        if (tcp != null) {
            localCandidates.addAll(tcp.gatherCandidates());
        }
        if (udp != null) {
            localCandidates.addAll(udp.gatherCandidates());
        }
        if (!controlling && m_streamDesc.isUseRelay() && tcpTurn != null) {
            localCandidates.addAll(tcpTurn.gatherCandidates());
        }
        encoder.visitCandidates(localCandidates);
        return encoder.getSdp();
    }

    /**
     * Creates a new TURN offer/answer.
     * 
     * @param controlling Whether or not this is the controlling ICE agent.
     * @param offerAnswerListener The listener for socket resolution.
     * @return The offer/answer for TURN.
     */
    private IceOfferAnswer newTurnOfferAnswer(final boolean controlling,
            final OfferAnswerListener offerAnswerListener) {
        if (!this.m_streamDesc.isUseRelay()) {
            return null;
        }

        try {
            final TcpTurnOfferAnswer turn = new TcpTurnOfferAnswer(
                m_turnCandidateProvider, controlling, offerAnswerListener,
                m_turnClientListener);

            // We only actually connect to the TURN server on the answerer/
            // non-controlling client.
            if (!controlling) {
                turn.connect();
            }
            return turn;
        } catch (final IOException e) {
            m_log.error("Could not connect to TURN server!!", e);
            return null;
        }
    }
}
