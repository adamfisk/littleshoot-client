package org.lastbamboo.common.ice;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidatePair;
import org.lastbamboo.common.ice.sdp.IceCandidateSdpDecoder;
import org.lastbamboo.common.ice.sdp.IceCandidateSdpDecoderImpl;
import org.lastbamboo.common.offer.answer.OfferAnswerListener;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of an ICE agent. An agent can contain multiple media 
 * streams and manages the top level of an ICE exchange. 
 */
public class IceAgentImpl implements IceAgent {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private volatile boolean m_controlling;
    
    /**
     * The state of overall ICE processing across all media streams.
     */
    private AtomicReference<IceState> m_iceState =
        new AtomicReference<IceState>(IceState.RUNNING);

    /**
     * TODO: This is just a place holder for now for the most part, as we only
     * currently support a single media stream.
     */
    private final Collection<IceMediaStream> m_mediaStreams =
        new LinkedList<IceMediaStream>();

    /**
     * The tie breaker to use when both agents think they're controlling.
     */
    private final IceTieBreaker m_tieBreaker;

    private final IceMediaStream m_mediaStream;

    private final OfferAnswerListener m_offerAnswerListener;

    private final AtomicBoolean m_closed = new AtomicBoolean(false);

    private final UdpSocketFactory m_udpSocketFactory;

    private final IceStunUdpPeer m_stunUdpPeer;

    /**
     * Creates a new ICE agent for an answerer. Passes the offer in the
     * constructor.
     * 
     * @param mediaStreamFactory Factory for creating the media streams 
     * we're using ICE to establish.
     * @param controlling Whether or not agent will start out as controlling.
     * @throws IceUdpConnectException If there's an error connecting the 
     * ICE UDP peer.
     */
    public IceAgentImpl(final IceMediaStreamFactory mediaStreamFactory,
            final boolean controlling,
            final OfferAnswerListener offerAnswerListener,
            final UdpSocketFactory udpSocketFactory)
            throws IceUdpConnectException {
        this.m_controlling = controlling;
        this.m_offerAnswerListener = offerAnswerListener;
        this.m_udpSocketFactory = udpSocketFactory;
        this.m_tieBreaker = new IceTieBreaker();

        // TODO: We only currently support a single media stream!!

        // Much of the action takes place as a result of the following call.
        // When this call completes, the TCP and UDP clients and servers
        // are both started, the candidates are gathered, etc.
        this.m_mediaStream = mediaStreamFactory.newStream(this);
        this.m_stunUdpPeer = this.m_mediaStream.getStunUdpPeer();
        this.m_mediaStreams.add(this.m_mediaStream);
    }

    private void setIceState(final IceState state) {
        this.m_iceState.set(state);
        if (state == IceState.COMPLETED) {
            final IceCandidatePair pair = getNominatedPair();
            final IoSession session = pair.getIoSession();
            m_udpSocketFactory.newSocket(session, isControlling(),
                    this.m_offerAnswerListener, this.m_stunUdpPeer);
        } else if (state == IceState.FAILED) {
            m_log.debug("Got ICE failed.  Closing.");
            close();
            this.m_offerAnswerListener.onOfferAnswerFailed(this);
        }
    }

    public void checkValidPairsForAllComponents(
            final IceMediaStream mediaStream) {
        // See ICE section 7.1.2.2.3. This indicates the media stream has a
        // valid pair for all it's components. That event can potentially
        // unfreeze checks for other media streams.

        // TODO: We only currently handle a single media stream, so we don't
        // perform these checks for now!!!
    }

    public void onUnfreezeCheckLists(final IceMediaStream mediaStream) {
        // Specified in ICE section 7.1.2.3.
        // TODO: We only currently handle a single media stream, so we don't
        // unfreeze any other streams for now!!

        // We need to check if all pairs for the check list for the media
        // stream are in either the Failed or Succeeded state. If they are,
        // then we need to go grouping etc of other check lists.
    }

    public long calculateDelay(final int Ta_i) {
        return IceTransactionDelayCalculator.calculateDelay(Ta_i,
                this.m_mediaStreams.size());
    }

    public boolean isControlling() {
        return this.m_controlling;
    }

    public void setControlling(final boolean controlling) {
        Thread.dumpStack();
        m_log.warn("Setting controlling to: " + controlling);
        // this.m_controlling = controlling;
    }

    public void recomputePairPriorities() {
        this.m_mediaStream.recomputePairPriorities(this.m_controlling);
    }

    public IceTieBreaker getTieBreaker() {
        return m_tieBreaker;
    }

    public byte[] generateAnswer() {
        return m_mediaStream.encodeCandidates();
    }

    public byte[] generateOffer() {
        return m_mediaStream.encodeCandidates();
    }

    public void processOffer(final ByteBuffer offer) {
        processRemoteCandidates(offer);
    }

    public void processAnswer(final ByteBuffer answer) {
        if (this.m_closed.get()) {
            m_log.info("UDP ICE agent is already closed! Ignoring answer.");
            return;
        }
        processRemoteCandidates(answer);
    }

    private void processRemoteCandidates(final ByteBuffer encodedCandidates) {
        // TODO: We should process all possible media streams.
        if (this.m_closed.get()) {
            m_log.info("Already closed -- not processing remote candidates");
            return;
        }

        // Note we set the controlling status of remote candidates to
        // whatever we are not!!
        final IceCandidateSdpDecoder decoder = new IceCandidateSdpDecoderImpl();
        final Collection<IceCandidate> remoteCandidates;
        try {
            remoteCandidates = decoder.decode(encodedCandidates,
                    !this.m_controlling);
        } catch (final IOException e) {
            m_log.warn("Could not process remote candidates", e);
            setIceState(IceState.FAILED);
            return;
        }

        // This should result in the stream entering either the Completed or
        // the Failed state.
        try {
            this.m_mediaStream.establishStream(remoteCandidates);
        } catch (final RuntimeException e) {
            m_log.error("Error establishing stream", e);
            setIceState(IceState.FAILED);
        }
    }

    public Collection<IceMediaStream> getMediaStreams() {
        return Collections.unmodifiableCollection(this.m_mediaStreams);
    }

    public void onNominatedPair(final IceCandidatePair pair,
            final IceMediaStream mediaStream) {
        if (m_log.isDebugEnabled()) {
            m_log.debug("Received nominated pair on agent.  "
                    + "Controlling: {} pair: {}", isControlling(), pair);
        }

        if (this.m_closed.get()) {
            m_log.info("Agent closed. Ignoring nomination.");
            return;
        }
        // We now need to set the state of the check list as specified in
        // 8.1.2. Updating States
        final IceCheckListState state = mediaStream.getCheckListState();
        if (state == IceCheckListState.RUNNING) {
            mediaStream.onNominated(pair);
            mediaStream.setCheckListState(IceCheckListState.COMPLETED);
            // Now handle the case where all check lists are completed.
            if (allCheckListsInState(IceCheckListState.COMPLETED)) {
                setIceState(IceState.COMPLETED);

                if (this.isControlling()) {
                    // TODO: We need to update the default candidate in the
                    // SDP if the one we're using differs!! We have to send
                    // the new offer.
                }
            }
        }

        else if (state == IceCheckListState.FAILED) {
            if (allCheckListsInState(IceCheckListState.FAILED)) {
                m_log.debug("All check lists are failed...agent is failed");
                setIceState(IceState.FAILED);
            } else if (anyCheckListInState(IceCheckListState.COMPLETED)) {
                // TODO: We SHOULD remove the failed media stream from the
                // session in our updated offer.
            }
            // Otherwise, none are COMPLETED and at least one of them must
            // be in the RUNNING state, so we just let ICE continue.
        }
    }

    private boolean anyCheckListInState(final IceCheckListState state) {
        synchronized (this.m_mediaStreams) {
            for (final IceMediaStream stream : this.m_mediaStreams) {
                final IceCheckListState curState = stream.getCheckListState();
                if (state == curState) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean allCheckListsInState(final IceCheckListState state) {
        synchronized (this.m_mediaStreams) {
            for (final IceMediaStream stream : this.m_mediaStreams) {
                final IceCheckListState curState = stream.getCheckListState();
                if (state != curState) {
                    return false;
                }
            }
        }
        return true;
    }

    public IceState getIceState() {
        return m_iceState.get();
    }

    public Queue<IceCandidatePair> getNominatedPairs() {
        return this.m_mediaStream.getNominatedPairs();
    }

    public void onValidPairs(final IceMediaStream mediaStream) {
        // 8.1.1.1. Regular Nomination -- we decide whether to continue our
        // checks or nominate now. We can nominate now through adding the
        // pair to the triggered check queue with the USE-CANDIDATE attribute.
        //
        // As stated in that section:
        // "The criteria for stopping the checks and for evaluating the
        // valid pairs is entirely a matter of local optimization."
        //
        // In this case, we have few enough candidates that we can nominate
        // pairs once we've completed checks for all high priority pairs.
        m_log.debug("Processing valid pair...");

        if (this.m_closed.get()) {
            m_log.info("Already closed...ingoring");
            return;
        }

        if (!isControlling()) {
            m_log.debug("Not the controlling agent, so not sending a message "
                    + "to select the final pair.");
        } else {
            final Queue<IceCandidatePair> validPairs = mediaStream
                    .getValidPairs();
            final IceCandidatePair pair = validPairs.peek();
            if (pair.isNominated()) {
                m_log.debug("Pair already nominated!!!");
                return;
            }

            /*
             * if (mediaStream.hasHigherPriorityPendingPair(pair)) {
             * m_log.debug("We have higher priority pairs that haven't " +
             * "completed their checks"); } else
             */
            {
                m_log.debug("Repeating check that produced the valid pair "
                        + "using USE-CANDIDATE");
                pair.useCandidate();
                mediaStream.addTriggeredPair(pair);
            }
        }
    }

    private IceCandidatePair getNominatedPair() {
        final Queue<IceCandidatePair> pairs = getNominatedPairs();

        final IceCandidatePair topPriorityPair = pairs.peek();
        if (topPriorityPair == null) {
            m_log.warn("No nominated pairs");
            return null;
        }
        return topPriorityPair;
    }

    public void close() {
        final boolean wasClosed = m_closed.getAndSet(true);
        if (wasClosed) {
            m_log.debug("Aleady closed.");
            return;
        }

        m_log.debug("Closing ICE agent.");
        // Close all the media streams.
        synchronized (this.m_mediaStreams) {
            for (final IceMediaStream stream : this.m_mediaStreams) {
                stream.close();
            }
        }
    }

    public void onNoMorePairs() {
        m_log.debug("No more pairs.");
        if (this.m_iceState.get() != IceState.COMPLETED
                && this.m_iceState.get() != IceState.FAILED) {
            m_log.debug("Setting ice state to failed -- no more pairs.");
            setIceState(IceState.FAILED);
        }
    }

    public void closeTcp() {
        // Ignored.
    }

    public void closeUdp() {
        close();
    }

    public Collection<? extends IceCandidate> gatherCandidates() {
        return this.m_mediaStream.getLocalCandidates();
    }

    public InetAddress getPublicAdress() {
        return this.m_mediaStream.getPublicAddress();
    }

    public void useRelay() {
        // We don't use UDP relays.
    }

    public boolean isClosed() {
        return this.m_closed.get();
    }
}
