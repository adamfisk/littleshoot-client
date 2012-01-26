package org.lastbamboo.common.ice;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Queue;

import org.littleshoot.mina.common.IoServiceListener;
import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidatePair;
import org.lastbamboo.common.stun.stack.message.BindingRequest;

/**
 * A media stream for an ICE agent.
 */
public interface IceMediaStream extends IoServiceListener
    {

    /**
     * Accessor for all valid pairs for this stream.
     * 
     * @return The {@link Queue} of all valid pairs for this stream.
     */
    Queue<IceCandidatePair> getValidPairs();
    
    /**
     * Adds a new valid pair.
     * 
     * @param pair The pair to add.
     */
    void addValidPair(IceCandidatePair pair);


    /**
     * Accessor for the remote candidate associate with the specified remote
     * address.
     * 
     * @param remoteAddress The remote address to look for.
     * @param isUdp Whether to search for UDP candidates.
     * @return The candidate associated with the specified remote address,
     * or <code>null</code> if there's no associated candidate.
     */
    IceCandidate getRemoteCandidate(InetSocketAddress remoteAddress, 
        boolean isUdp);
 
    /**
     * Accessor for the local candidate associate with the specified local
     * address.
     * 
     * @param localAddress The local address to look for.
     * @return The candidate associated with the specified local address,
     * or <code>null</code> if there's no associated candidate.
     */
    IceCandidate getLocalCandidate(InetSocketAddress localAddress, 
        boolean isUdp);

    /**
     * Adds a local candidate.
     * 
     * @param localCandidate The local candidate to add.
     */
    void addLocalCandidate(IceCandidate localCandidate);
    
    /**
     * Accesses the pair matching the specified local and remote addresses,
     * if any.
     * 
     * @param localAddress The address for the local candidate.
     * @param remoteAddress The address for the remote candidate.
     * @param udp Whether or not the pair is UDP.
     * @return The pair matching both addresses, or <code>null</code> if no
     * such pair exists.
     */
    IceCandidatePair getPair(InetSocketAddress localAddress, 
        InetSocketAddress remoteAddress, boolean udp);

    /**
     * Called when connectivity checks have created a new valid pair and the
     * media stream needs to update the states of other pairs.  
     * 
     * @param generatingPair The pair that generated the valid pair.
     */
    void updatePairStates(IceCandidatePair generatingPair);

    /**
     * Adds a pair to the triggered check queue.
     * 
     * @param pair The pair to add.
     */
    void addTriggeredPair(IceCandidatePair pair);

    /**
     * Recomputes the priorities of pairs in checklists.  This can happen,
     * for example, if our role has changed from controlling to controlled or
     * vice versa.
     * @param controlling The current controlling status of the agent.
     */
    void recomputePairPriorities(boolean controlling);

    /**
     * Establishes a media stream using the answer data from the remote host.
     * 
     * @param remoteCandidates The answer from the remote host.
     */
    void establishStream(Collection<IceCandidate> remoteCandidates);

    /**
     * Checks whether or not the specified remote address matches any of
     * the addresses of remote candidates.  This is typically used when
     * checking for peer reflexive candidates.  If it's an address we don't 
     * know about, it's typically a new peer reflexive candidate.
     * 
     * @param remoteAddress The remote address to check.
     * @param isUdp Whether or not the candidate is UDP.
     * @return <code>true</code> if the address matches the address of a 
     * remote candidate we already know about, otherwise <code>false</code>.
     */
    boolean hasRemoteCandidate(InetSocketAddress remoteAddress, boolean isUdp);
   
    /**
     * Checks whether or not the specified remote address matches any of
     * the addresses of remote candidates.  This is typically used when
     * checking for peer reflexive candidates.  If it's an address we don't 
     * know about, it's typically a new peer reflexive candidate.
     * 
     * @param remoteAddress The remote address to check.
     * @param isUdp Whether or not the candidate is UDP.
     * @return <code>true</code> if the address matches the address of a 
     * remote candidate we already know about, otherwise <code>false</code>.
     */
    boolean hasRemoteCandidateInSdp(InetSocketAddress remoteAddress, 
        boolean isUdp);

    /**
     * Adds a peer reflexive candidate to the list of remote candidates.
     * 
     * @param request The {@link BindingRequest} that initiated the 
     * establishment of a new peer reflexive candidate.
     * @param localAddress The local address the request was sent to.  This
     * allows us to match the local address with the local candidate it was
     * sent to.  We use that to determine the component ID of the new peer
     * reflexive candidate. 
     * @param remoteAddress The remote address of the peer that sent the 
     * Binding Request.
     * @param isUdp Whether the candidate should be a UDP candidate or not.
     * @return The new peer reflexive remote candidate.
     */
    IceCandidate addRemotePeerReflexive(BindingRequest request, 
        InetSocketAddress localAddress, InetSocketAddress remoteAddress, 
        boolean isUdp);
    
    /**
     * Encodes this media stream in SDP.
     * 
     * @return The media stream encoded in SDP.
     */
    byte[] encodeCandidates();

    /**
     * Adds the specified ICE candidate pair to the check list.
     * 
     * @param pair The pair to add.
     */
    void addPair(IceCandidatePair pair);

    /**
     * Gets the state of the check list.
     * 
     * @return The state of the check list.
     */
    IceCheckListState getCheckListState();

    /**
     * Implements ICE section 7.1.2.3. Check List and Timer State Updates.
     */
    void updateCheckListAndTimerStates();

    /**
     * Checks whether or not there are existing pairs on either the triggered
     * check list or the normal check list.  For the normal check list, the
     * pair must be in the FROZEN, WAITING, or IN PROGRESS states.
     * 
     * @param pair The pair to check.
     * @return <code>true</code> if there's a higher priority pair that could
     * still complete its check, otherwise <code>false</code>.
     */
    boolean hasHigherPriorityPendingPair(IceCandidatePair pair);

    /**
     * Notifies the media stream that there's been a nominated pair.  The 
     * media stream follows the process in section 8.1.2, removing all 
     * Waiting and Frozen pairs in the check list and the triggered check queue
     * and ceasing retransmissions for pairs that are In-Progress if their
     * priorities are lower than the nominated pair.
     * 
     * @param pair The nominated pair.
     */
    void onNominated(IceCandidatePair pair);

    /**
     * Sets the state of the check list.
     * 
     * @param state The state of the check list.
     */
    void setCheckListState(IceCheckListState state);

    /**
     * Accessor for all nominated pairs for this stream.
     * 
     * @return The {@link Queue} of all nominated pairs for this stream.
     */
    Queue<IceCandidatePair> getNominatedPairs();
    
    /**
     * Starts gathering candidates and executing ICE.
     * 
     * @param checkList The candidate pair check list.
     * @param localCandidates The local candidates.
     * @param scheduler The check scheduler.
     */
    void start(IceCheckList checkList, Collection<IceCandidate> localCandidates,
        IceCheckScheduler scheduler);

    /**
     * Perform any necessary close operations for the media stream.
     */
    void close();
    
    /**
     * Accessor for the local candidates for this stream.
     * 
     * @return The local candidates for this stream.
     */
    Collection<IceCandidate> getLocalCandidates();

    InetAddress getPublicAddress();

    IceStunUdpPeer getStunUdpPeer(); 

    }
