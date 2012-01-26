package org.lastbamboo.common.ice;

import java.util.Collection;
import java.util.Queue;

import org.lastbamboo.common.ice.candidate.IceCandidatePair;

/**
 * Interface for ICE agents. 
 */
public interface IceAgent extends IceOfferAnswer {

    /**
     * Accessor for the overall state of ICE processing.
     * 
     * @return The overall state of ICE processing for all media streams.
     */
    IceState getIceState();
    
    /**
     * Sets whether or not this agent is the controlling agent.
     * 
     * @param controlling Whether or not this agent is the controlling agent.
     */
    void setControlling(boolean controlling);
    
    /**
     * Returns whether or not this agent is the controlling agent.
     * 
     * @return <code>true</code> if this agent is the controlling agent,
     * otherwise <code>false</code>.
     */
    boolean isControlling();
    
    /**
     * Accessor for the role conflict tie-breaker for this agent.
     * 
     * @return The role conflict tie-breaker for this agent.
     */
    IceTieBreaker getTieBreaker();

    /**
     * Calculates the delay in milliseconds to use before initiating a new
     * transaction for a given media stream.  The agent handles this because
     * the number of outstanding media streams is taken into account when
     * calculating the delay, and only the agent has that information.
     * 
     * @param Ta_i The transaction delay for the specific media stream.
     * @return The transaction delay to use based on a formula taking into
     * account the number of active media streams.
     */
    long calculateDelay(int Ta_i);
    
    /**
     * Notifies the listener that the media stream may have valid pairs for
     * all components of a given media stream, possibly requiring changing 
     * states of other streams, as specified in part 2 of ICE section 
     * 7.1.2.2.3. "Updating Pair States".
     * 
     * @param mediaStream The media stream.
     */
    void checkValidPairsForAllComponents(IceMediaStream mediaStream);

    /**
     * Tells the listener to unfreeze any other check lists.
     * 
     * @param mediaStream The media stream initiating the unfreeze operation.
     */
    void onUnfreezeCheckLists(IceMediaStream mediaStream);

    /**
     * Recomputes the priorities of pairs in checklists.  This can happen,
     * for example, if our role has changed from controlling to controlled or
     * vice versa.
     */
    void recomputePairPriorities();

    /**
     * Accessor for all the media streams for the agent.
     * 
     * @return The media streams for the agent.
     */
    Collection<IceMediaStream> getMediaStreams();

    /**
     * Indicates a pair has been nominated.  The agent needs to update checks
     * and pair states accordingly and likely to begin transmitting media.
     * 
     * @param pair The nominated pair.
     * @param iceMediaStream The ICE media stream the pair is a part of.
     */
    void onNominatedPair(IceCandidatePair pair, IceMediaStream iceMediaStream);

    /**
     * Accessor for the nominated pairs.
     * 
     * TODO: We only currently support the single media stream.  This method
     * would have to change for multiple streams.
     * @return The {@link Queue} of nominated {@link IceCandidatePair}s.
     */
    Queue<IceCandidatePair> getNominatedPairs();

    /**
     * Tells the agent to consider the valid pairs for this media stream for 
     * nomination.
     * 
     * @param mediaStream The media stream the pair is valid for.
     */
    void onValidPairs(IceMediaStream mediaStream);

    /**
     * Called when there are no more pairs to process.
     */
    void onNoMorePairs();

    /**
     * Whether or not the check list is closed.
     * 
     * @return Whether or not the check list is closed.
     */
    boolean isClosed();

}
