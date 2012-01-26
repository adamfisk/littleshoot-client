package org.lastbamboo.common.ice.candidate;

import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.StunMessage;

/**
 * Interface for a pair of ICE candidates.
 */
public interface IceCandidatePair extends Comparable<IceCandidatePair>
    {

    /**
     * Accessor for the local candidate for the pair.
     * 
     * @return The local candidate for the pair.
     */
    IceCandidate getLocalCandidate();
    
    /**
     * Accessor for the remote candidate for the pair.
     * 
     * @return the remote candidate for the pair.
     */
    IceCandidate getRemoteCandidate();

    /**
     * Accessor for the priority for the pair.
     * 
     * @return The priority for the pair.
     */
    long getPriority();
    
    /**
     * Accesses the state of the pair.
     * 
     * @return The state of the pair.
     */
    IceCandidatePairState getState();
    
    /**
     * Accessor for the foundation for the pair.
     * 
     * @return The foundation for the candidate pair.  Note that this is a 
     * string because the foundation for the pair is the *concatenation* of
     * the foundations of the candidates.
     */
    String getFoundation();

    /**
     * Sets the state of the pair.
     * 
     * @param state The state of the pair.
     */
    void setState(IceCandidatePairState state);

    /**
     * Accessor for the component ID for the pair.  Note that both candidates
     * in the pair always have the same component ID.
     * 
     * @return The component ID for the pair.
     */
    int getComponentId();
    
    /**
     * Accepts the specified visitor to an ICE candidate pair.
     * 
     * @param <T> The class to return.
     * @param visitor The visitor to accept.
     * @return The class the visitor created. 
     */
    <T> T accept(IceCandidatePairVisitor<T> visitor);

    /**
     * Nominates this pair as potentially the final pair for exchanging media.  
     * The nominated pair with the highest priority is the pair that is 
     * ultimately used.
     * 
     * @param nominated Whether or not this pair is nominated as the final 
     * pair for exchanging media.
     */
    void nominate();

    /**
     * Recomputes the priority for the pair.
     */
    void recomputePriority();

    /**
     * Cancels the existing STUN transaction.  The behavior for this is 
     * described in ICE section 7.2.1.4. on triggered checks.  From that 
     * section, cancellation:<p> 
     * 
     * "means that the agent will not retransmit the 
     * request, will not treat the lack of response to be a failure, but will 
     * wait the duration of the transaction timeout for a response."
     */
    void cancelStunTransaction();

    /**
     * Sets a flag indicating checks on this pair should include the 
     * USE-CANDIDATE attribute in their Binding Requests.  See:
     *
     * 8.1.1.1. Regular Nomination.
     */
    void useCandidate();

    /**
     * Returns whether or not the USE-CANDIDATE attribute is set, i.e. whether
     * or not this pair includes the USE-CANDIDATE attribute in its outgoing
     * Binding Requests.
     * 
     * @return <code>true</code> if the USE-CANDIDATE attribute is in use. 
     */
    boolean useCandidateSet();

    /**
     * Returns whether or not this pair has already been nominated.
     * 
     * @return <code>true</code> if the pair has been nominated, otherwise
     * <code>false</code>.
     */
    boolean isNominated();

    StunMessage check(BindingRequest request, long rto);

    void close();

    IoSession getIoSession();
    
    /**
     * Sets the {@link IoSession} for the pair.
     * 
     * @param session The {@link IoSession}.
     */
    void setIoSession(final IoSession session);

    /**
     * Check for whether or not this pair is for a TURN connection.  If so,
     * we may handle it slightly differently upon nomination or at any other
     * time.
     * 
     * @return <code>true</code> if the local candidate for this pair is for
     * our connection to our TURN server (the connection over which we sent
     * an Allocate Request).
     */
    boolean isTurnPair();
    
    /**
     * Returns whether or not a pair is a TCP pair.
     * 
     * @return <code>true</code> if the pair is TCP, otherwise 
     * <code>false</code>.
     */
    boolean isTcp();

    void nominateOnSuccess();
    
    boolean isNominateOnSuccess();
    }
