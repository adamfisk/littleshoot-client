package org.lastbamboo.common.ice;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidatePair;
import org.lastbamboo.common.ice.candidate.IceCandidatePairState;
import org.lastbamboo.common.ice.candidate.IceCandidateType;
import org.lastbamboo.common.ice.candidate.IceCandidateVisitor;
import org.lastbamboo.common.ice.candidate.IceCandidateVisitorAdapter;
import org.lastbamboo.common.ice.candidate.IceTcpHostPassiveCandidate;
import org.lastbamboo.common.ice.candidate.IceTcpPeerReflexiveCandidate;
import org.lastbamboo.common.ice.candidate.IceTcpRelayPassiveCandidate;
import org.lastbamboo.common.ice.candidate.IceTcpServerReflexiveSoCandidate;
import org.lastbamboo.common.ice.candidate.IceUdpHostCandidate;
import org.lastbamboo.common.ice.candidate.IceUdpPeerReflexiveCandidate;
import org.lastbamboo.common.ice.candidate.IceUdpRelayCandidate;
import org.lastbamboo.common.ice.candidate.IceUdpServerReflexiveCandidate;
import org.lastbamboo.common.stun.stack.message.BindingErrorResponse;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.BindingSuccessResponse;
import org.lastbamboo.common.stun.stack.message.CanceledStunMessage;
import org.lastbamboo.common.stun.stack.message.ConnectErrorStunMessage;
import org.lastbamboo.common.stun.stack.message.NullStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorAdapter;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControlledAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControllingAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IcePriorityAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceUseCandidateAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that performs connectivity checks for a single pair. This 
 * implements ICE section 7.1 from:<p>
 * 
 * http://tools.ietf.org/html/draft-ietf-mmusic-ice-17#section-7.1
 */
public class IceStunClientCandidateProcessor 
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final IceCandidatePair m_pair;

    private final IceAgent m_iceAgent;

    private final IceMediaStream m_mediaStream;

    private final ExistingSessionIceCandidatePairFactory m_existingSessionPairFactory;

    /**
     * Creates a new connectivity checker for a single UDP pair.
     * 
     * @param iceAgent The top-level ICE agent.
     * @param iceMediaStream The media stream this check is trying to establish.
     * @param udpPair The candidate pair.
     * @param existingSessionPairFactory Pair factory to use when there's a 
     * session that's already been created prior to construction of the pair.
     */
    public IceStunClientCandidateProcessor(final IceAgent iceAgent, 
        final IceMediaStream iceMediaStream, final IceCandidatePair udpPair,
        final ExistingSessionIceCandidatePairFactory existingSessionPairFactory)
        {
        m_iceAgent = iceAgent;
        m_mediaStream = iceMediaStream;
        m_pair = udpPair;
        m_existingSessionPairFactory = existingSessionPairFactory;
        }
    
    /**
     * Processes the specified local candidate.
     * 
     * @param localCandidate The local candidate.
     */
    public void processLocalCandidate(final IceCandidate localCandidate)
        {
        // See ICE section 7 "Performing Connectivity Checks".
        final IceCandidate remoteCandidate = this.m_pair.getRemoteCandidate();
        
        // Now send a BindingRequest with PRIORITY, USE-CANDIDATE, 
        // ICE-CONTROLLING etc.
        final long requestPriority = 
            IcePriorityCalculator.calculatePriority(
                IceCandidateType.PEER_REFLEXIVE, IceTransportProtocol.UDP);

        final IcePriorityAttribute priorityAttribute = 
            new IcePriorityAttribute(requestPriority);
        
        final StunAttribute controlling;
        
        // The agent uses the same tie-breaker throughout the session.
        final IceTieBreaker tieBreaker = m_iceAgent.getTieBreaker();
        
        // We use a separate variable here because we need to know what
        // we sent in the case of error responses, and the data in the
        // ICE agent can change.
        final boolean isControlling = m_iceAgent.isControlling();
        if (isControlling)
            {
            controlling = new IceControllingAttribute(tieBreaker.toByteArray());
            }
        else
            {
            controlling = new IceControlledAttribute(tieBreaker.toByteArray());
            }
        
        // TODO: Add CREDENTIALS attribute.
        final BindingRequest request;
        
        // This could be for either regular or aggressive nomination.
        final boolean includedUseCandidate;
        
        // We only include the USE-CANDIDATE attribute if it's set AND we're
        // the controlling agent. The controlled agent never sends 
        // USE-CANDIDATE.
        if (this.m_pair.useCandidateSet() && isControlling)
            {
            m_log.info("Creating Binding Request with USE CANDIDATE");
            request = new BindingRequest(priorityAttribute, controlling, 
                new IceUseCandidateAttribute());
            includedUseCandidate = true;
            }
        else
            {
            request = new BindingRequest(priorityAttribute, controlling);
            includedUseCandidate = false;
            }
        
        // TODO: Obtain RTO properly.
        final long rto = 20L;
        
        m_log.debug("Writing Binding Request: {}", request);
        
        final StunMessage response = this.m_pair.check(request, rto);
        
        final StunMessageVisitor<IceCandidate> visitor = 
            new StunMessageVisitorAdapter<IceCandidate>()
            {
            
            @Override
            public IceCandidate visitBindingSuccessResponse(
                final BindingSuccessResponse bsr)
                {
                m_log.info("Visiting binding success response...");
                
                if (m_pair.isNominateOnSuccess()) 
                    {
                    /*
                      As stated in section 7.2.1.5. Updating the Nominated Flag:
                     
                     
                     "If the state of this pair is In-Progress, if its check produces a
                      successful result, the resulting valid pair has its nominated flag
                      set when the response arrives.  This may end ICE processing for
                      this media stream when it arrives; see Section 8."
                     */
                    m_pair.nominate();
                    m_iceAgent.onNominatedPair(m_pair, m_mediaStream);
                    return null;
                    }
                
                // Now check the mapped address and see if it matches
                // any of the local candidates we know about. If it 
                // does not, it's a new peer reflexive candidate.
                //
                // We use the mapped address of the response as the local
                // candidate address for the new pair, as specified in 
                // 7.1.2.2.2.
                final InetSocketAddress mappedAddress = 
                    bsr.getMappedAddress();
                final IceCandidate matchingCandidate = 
                    m_mediaStream.getLocalCandidate(mappedAddress, 
                        localCandidate.isUdp());
                
                if (matchingCandidate != null)
                    {
                    // This will have the priority signaled in the original
                    // SDP, as specified in 7.1.2.2.2.
                    return matchingCandidate;
                    }
                else
                    {
                    // This basically indicates the NAT bound to a new port for
                    // the outgoing request to the new host, meaning the NAT
                    // is using at least address-dependent mapping and possibly
                    // address and port dependent mapping.
                    
                    // Note the base candidate here is the local candidate
                    // from the pair, i.e. the candidate we're visiting.
                    
                    // We use the PRIORITY from the Binding Request, as
                    // specified in section 7.1.2.2.1. and 7.1.2.2.2.  
                    final IceCandidate peerReflexiveLocal = 
                        new IceUdpPeerReflexiveCandidate(mappedAddress, 
                            localCandidate, m_iceAgent.isControlling(), 
                            requestPriority);
                    
                    m_mediaStream.addLocalCandidate(peerReflexiveLocal);
                    return peerReflexiveLocal;
                    }
                }

            @Override
            public IceCandidate visitBindingErrorResponse(
                final BindingErrorResponse bindingErrorResponse)
                {
                // This is likely a role-conflict error.  We need to 
                // handle it as specified in section 7.1.2.1.
                final int errorCode = bindingErrorResponse.getErrorCode();
                
                if (errorCode != 487)
                    {
                    // It's an error response we don't know how to handle.
                    m_log.warn("Unexpected error response: " + 
                        bindingErrorResponse.getAttributes());
                    m_pair.setState(IceCandidatePairState.FAILED);
                    return null;
                    }
                if (!isControlling)
                    {
                    m_iceAgent.setControlling(true);
                    }
                else
                    {
                    m_iceAgent.setControlling(false);
                    }
                
                // As stated in ICE 17 section 7.1.2.1. Failure Cases:
                // "the agent MUST enqueue the candidate pair whose check
                // generated the 487 into the triggered check queue.  The 
                // state of that pair is set to Waiting."
                
                // This has the effect of sending a new Binding Request to
                // the remote host reflecting the new role.
                
                // Note that we queue up a triggered check always here,
                // assuming that the role change was actually correct.
                m_pair.setState(IceCandidatePairState.WAITING);
                m_mediaStream.addTriggeredPair(m_pair);
                
                return null;
                }

            @Override
            public IceCandidate visitNullMessage(final NullStunMessage message)
                {
                m_log.debug("Visiting null message...");
                // See section 7.1.2.1. Failure Cases
                // This means we never received any response to our request,
                // interpreted as a failure.
                m_pair.setState(IceCandidatePairState.FAILED);
                return null;
                }

            @Override
            public IceCandidate visitCanceledMessage(
                final CanceledStunMessage message)
                {
                m_log.debug("Transaction was canceled...");
                // The outgoing message was canceled.  This can happen when
                // we received a pair that generated a triggered check on 
                // a STUN server.  In this case, we don't treat it as a failure
                // but instead simply stop processing the pair.  It will be
                // added to the triggered check queue and will be re-checked.
                return null;
                }

            @Override
            public IceCandidate visitConnectErrorMesssage(
                final ConnectErrorStunMessage message)
                {
                // See section 7.1.2.1. Failure Cases.
                m_log.debug("Got connect error -- setting pair state to " +
                    "failed.");
                m_pair.setState(IceCandidatePairState.FAILED);
                return null;
                }
            };
            
        if (response == null)
            {
            m_log.debug("No response -- could not connect?");
            
            // See ICE 7.1.2.3.  Check List and Timer State Updates --
            // we need to update the check list and timer states in a 
            // failure case as well.
            m_mediaStream.updateCheckListAndTimerStates();
            return;
            }
            
        final IceCandidate newLocalCandidate = response.accept(visitor);
        if (newLocalCandidate == null)
            {
            m_log.debug("Check failed or was canceled -- should happen " +
                "quite often");
            
            // See ICE 7.1.2.3.  Check List and Timer State Updates --
            // we need to update the check list and timer states in a 
            // failure case as well.
            m_mediaStream.updateCheckListAndTimerStates();
            return;
            }
        else
            {
            processSuccess(newLocalCandidate, remoteCandidate, 
                includedUseCandidate, requestPriority);
            }
        }
    
    /**
     * Processes a successful response to a check. This is specified in 
     * ICE section 7.1.2.2 at:
     * 
     * <p>
     * http://tools.ietf.org/html/draft-ietf-mmusic-ice-18#section-7.1.2.2
     * <p>
     * 
     * Note we always know the source IP and port and destination IP and port
     * are correct here because we're using a MINA UDP socket that's 
     * "connected", meaning it only accepts data from that single IP address
     * and port.
     * 
     * @param localCandidate The calculated local candidate. This can be 
     * peer reflexive, but it also could just be the candidate of the original
     * pair we just issued a check for.
     * @param remoteCandidate The remote candidate from the original pair.
     * @param useCandidate Whether the Binding Request included the 
     * USE-CANDIDATE attribute.
     * @param bindingRequestPriority The priority of the Binding Request.
     */
    private void processSuccess(final IceCandidate localCandidate, 
        final IceCandidate remoteCandidate, final boolean useCandidate, 
        final long bindingRequestPriority)
        {
        m_log.debug("Processing success...");
        if (this.m_iceAgent.isClosed())
            {
            m_log.info("Agent already closed...ignoring success.");
            return;
            }
        final InetSocketAddress remoteAddress = 
            remoteCandidate.getSocketAddress();
        
        final InetSocketAddress newLocalAddress = 
            localCandidate.getSocketAddress();
        
        final IceCandidatePair pairToAddToValidList;
        if (equalsOriginalPair(this.m_pair, newLocalAddress, remoteAddress))
            {
            // Just add the original pair;
            m_log.debug("Using original pair...");
            pairToAddToValidList = this.m_pair;
            }
        else
            {
            m_log.debug("Original pair not equal");
            m_log.debug("Original pair was: {}", m_pair);
            m_log.debug(
                "New pair addresses: {}"+newLocalAddress, remoteAddress);
            final IceCandidatePair existingPair = 
                m_mediaStream.getPair(newLocalAddress, remoteAddress,
                    localCandidate.isUdp());
            if (existingPair != null)
                {
                m_log.debug("Using existing pair...");
                pairToAddToValidList = existingPair;
                }
            else
                {
                // The pair is a completely new pair.  
                // We've already calculated the priority of the local candidate,
                // but we still need the priority of the remote candidate.
                
                // Here's the description of calculating the remote priority:
                //
                // The priority of the remote candidate is taken from the 
                // SDP of the peer.  If the candidate does not appear there, 
                // then the check must have been a triggered check to a new 
                // remote candidate.  In that case, the priority is taken as the
                // value of the PRIORITY attribute in the Binding Request which
                // triggered the check that just completed.
                final long remotePriority;
                if (this.m_mediaStream.hasRemoteCandidateInSdp(remoteAddress, 
                    remoteCandidate.isUdp()))
                    {
                    // The check was not a triggered check, so use the original 
                    // candidate's priority.
                    remotePriority = this.m_mediaStream.getRemoteCandidate(
                        remoteAddress, remoteCandidate.isUdp()).getPriority();
                    }
                else
                    {
                    // The check was a triggered check, so we use the priority
                    // from the Binding Request we just sent, as specified in
                    // section 7.1.2.2.2.
                    remotePriority = bindingRequestPriority;
                    }
                
                final IceCandidate newRemoteCandidate =
                    createRemoteCandidate(remoteCandidate, remotePriority);
                
                // This is key here.  Even if the local candidate is a
                // new peer reflexive candidate, its base is still the
                // local candidate for the pair that started this check.
                // If it's not peer reflexive, the local candidate *is* the 
                // local candidate of the pair that started the check.  
                // The remote candidate is the remote candidate from the pair 
                // in any case.
                //
                // Either way, the point is that the underlying transport
                // connection is the same so we need to reuse it, so we 
                // construct a new pair with the same existing transport.
                m_log.debug("Creating new pair...");
                pairToAddToValidList = m_existingSessionPairFactory.newUdpPair(
                    localCandidate, newRemoteCandidate, 
                    this.m_pair.getIoSession());
                m_log.debug("Created pair:\n{}", pairToAddToValidList);
                }
            }
        
        m_log.debug("Adding valid pair...");
        m_mediaStream.addValidPair(pairToAddToValidList);
        
        // 7.1.2.2.3.  Updating Pair States
        
        // Tell the media stream to update pair states as a result of 
        // a valid pair.  
        m_log.debug("Updating pair states...");
        this.m_mediaStream.updatePairStates(this.m_pair);
    
        this.m_iceAgent.checkValidPairsForAllComponents(m_mediaStream);
        
        // Tell the ICE agent to consider this valid pair if it was not just
        // nominated. Nominated pairs have already been considered as valid
        // pairs -- that's how they had their nominated flag set.
        if (!updateNominatedFlag(pairToAddToValidList, useCandidate))
            {
            m_log.debug("Not Nominated.  Notifying agent of valid pairs...");
            this.m_iceAgent.onValidPairs(m_mediaStream);
            }
        
        // 7.1.2.3. Check List and Timer State Updates
        m_log.debug("Updating check list and timer states.");
        m_mediaStream.updateCheckListAndTimerStates();
        }
    
    /**
     * This implements:<p> 
     * 
     * 7.1.2.2.4.  Updating the Nominated Flag<p>
     * 
     * and part of:<p>
     * 
     * 7.2.1.5.  Updating the Nominated Flag<p>
     * 
     * @param validPair The valid pair.
     * @param sentUseCandidateInRequest Whether or not the USE-CANDIDATE 
     * attribute appeared in the original Binding Request.
     * @return <code>true</code> if the valid pair was nominated, otherwise
     * <code>false</code>.
     */
    private boolean updateNominatedFlag(final IceCandidatePair validPair, 
        final boolean sentUseCandidateInRequest)
        {
        if (this.m_iceAgent.isControlling() && sentUseCandidateInRequest)
            {
            m_log.debug("Nominating pair on controlling agent.");
            validPair.nominate();
            this.m_iceAgent.onNominatedPair(validPair, this.m_mediaStream);
            return true;
            }
        
        // If the check was the result of a triggered check from a request
        // that included USE-CANDIDATE (a request from the controlling agent), 
        // we nominate.
        else if (!this.m_iceAgent.isControlling() && m_pair.useCandidateSet())
            {
            m_log.debug("Nominating new pair on controlled agent!!");
            // We just put the pair in the successful state, so we know
            // that's the state it's in (it has to be in the successful
            // state for us to nominate it).
            
            // NOTE: We nominate the *original pair* here, not the valid pair.
            // That was the pair the originally had USE-CANDIDATE set.
            m_pair.nominate();
            this.m_iceAgent.onNominatedPair(m_pair, this.m_mediaStream);
            return true;
            }
        
        m_log.debug("Not nominating pair.");
        return false;
        }

    /**
     * Checks if the new pair equals the original pair that generated
     * the check.
     * 
     * @param pair The original pair that generated the check.
     * @param newLocalAddress The new local candidate.
     * @param newRemoteAddress The new remote candidate.
     * @return <code>true</code> if the pairs match, otherwise 
     * <code>false</code>.
     */
    private boolean equalsOriginalPair(final IceCandidatePair pair, 
        final InetSocketAddress newLocalAddress, 
        final InetSocketAddress newRemoteAddress)
        {
        final InetSocketAddress oldLocalAddress =
            pair.getLocalCandidate().getSocketAddress();
        final InetSocketAddress oldRemoteAddress = 
            pair.getRemoteCandidate().getSocketAddress();
        return 
            newLocalAddress.equals(oldLocalAddress) &&
            newRemoteAddress.equals(oldRemoteAddress);
        }
    
    /**
     * This creates a new remote candidate with all the same characteristics of
     * the original remote candidate, except with a different priority.  This
     * is a pain, but we don't want to just set the priority of an existing
     * candidate.
     * 
     * @param remoteCandidate The remote candidate to essentially copy.
     * @param remotePriority The new priority.
     * @return The new candidate.
     */
    private static IceCandidate createRemoteCandidate(
        final IceCandidate remoteCandidate, final long remotePriority)
        {
        final InetSocketAddress remoteAddress = 
            remoteCandidate.getSocketAddress();
        final String foundation = remoteCandidate.getFoundation();
        final boolean controlling = remoteCandidate.isControlling();
        final int componentId = remoteCandidate.getComponentId();
        final InetAddress relatedAddress = remoteCandidate.getRelatedAddress();
        final int relatedPort = remoteCandidate.getRelatedPort();
        final IceCandidateVisitor<IceCandidate> visitor =
            new IceCandidateVisitorAdapter<IceCandidate>()
            {

            @Override
            public IceCandidate visitTcpHostPassiveCandidate(
                final IceTcpHostPassiveCandidate candidate)
                {
                return new IceTcpHostPassiveCandidate(remoteAddress, 
                    foundation, controlling, remotePriority, componentId);
                }
            
            @Override
            public IceCandidate visitTcpPeerReflexiveCandidate(
                final IceTcpPeerReflexiveCandidate candidate)
                {
                return new IceTcpPeerReflexiveCandidate(remoteAddress, 
                    foundation, componentId, controlling, remotePriority);
                }

            @Override
            public IceCandidate visitTcpRelayPassiveCandidate(
                final IceTcpRelayPassiveCandidate candidate)
                {
                return new IceTcpRelayPassiveCandidate(remoteAddress, 
                    foundation, relatedAddress, relatedPort, 
                    controlling, remotePriority, 
                    componentId);
                }

            @Override
            public IceCandidate visitTcpServerReflexiveSoCandidate(
                final IceTcpServerReflexiveSoCandidate candidate)
                {
                return new IceTcpServerReflexiveSoCandidate(remoteAddress, 
                    foundation, relatedAddress, relatedPort, 
                    controlling, remotePriority, componentId);
                }

            @Override
            public IceCandidate visitUdpHostCandidate(
                final IceUdpHostCandidate candidate)
                {
                return new IceUdpHostCandidate(remoteAddress, foundation, 
                    remotePriority, controlling, componentId);
                }

            @Override
            public IceCandidate visitUdpPeerReflexiveCandidate(
                final IceUdpPeerReflexiveCandidate candidate)
                {
                return new IceUdpPeerReflexiveCandidate(remoteAddress, 
                    foundation, componentId, controlling, remotePriority);
                }

            @Override
            public IceCandidate visitUdpRelayCandidate(
                final IceUdpRelayCandidate candidate)
                {
                return new IceUdpRelayCandidate(remoteAddress, foundation, 
                    remotePriority, controlling, componentId, 
                    relatedAddress, relatedPort);
                }

            @Override
            public IceCandidate visitUdpServerReflexiveCandidate(
                final IceUdpServerReflexiveCandidate candidate)
                {
                return new IceUdpServerReflexiveCandidate(remoteAddress, 
                    foundation, relatedAddress, relatedPort, 
                    controlling, remotePriority, 
                    componentId);
                }
            };
        return remoteCandidate.accept(visitor);
        }
    }
