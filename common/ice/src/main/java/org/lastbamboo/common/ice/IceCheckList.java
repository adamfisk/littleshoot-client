package org.lastbamboo.common.ice;

import java.util.Collection;

import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidatePair;
import org.littleshoot.util.Closure;
import org.littleshoot.util.Predicate;

/**
 * Interface for ICE check lists. 
 */
public interface IceCheckList 
    {

    /**
     * Sets the state of the check list.
     * 
     * @param state The state of the check list.
     */
    void setState(IceCheckListState state);

    /**
     * Accessor for the state of the check list.
     * 
     * @return The state of the check list.
     */
    IceCheckListState getState();

    void check();
    
    /**
     * Returns whether or not this check list is considered "active" and should 
     * count towards the value of N in timer computation from section 5.8.
     * 
     * @return <code>true</code> if the check list is active, otherwise
     * <code>false</code>.
     */
    boolean isActive();

    /**
     * Adds a pair to the triggered check queue.
     * 
     * @param pair The pair to add.
     */
    void addTriggeredPair(IceCandidatePair pair);

    /**
     * Removes the top triggered pair.  Triggered pairs are maintained in a
     * FIFO queue.
     * 
     * @return The top triggered pair, or <code>null</code> if there is no
     * such pair.
     */
    IceCandidatePair removeTopTriggeredPair();

    /**
     * Recomputes the priorities of pairs in checklists.  This can happen,
     * for example, if our role has changed from controlling to controlled or
     * vice versa.
     * @param controlling The current controlling status of the agent.
     */
    void recomputePairPriorities(boolean controlling);

    /**
     * Adds the specified ICE candidate pair to the check list.
     * 
     * @param pair The pair to add.
     */
    void addPair(IceCandidatePair pair);

    /**
     * Forms the check list.  The check list is not created upon construction
     * because we can receive incoming checks before we've received the 
     * answer, requiring adding triggered pairs to the list before we can
     * create a proper check list.
     * 
     * @param remoteCandidates The remote candidates to use in forming a 
     * check list.
     */
    void formCheckList(Collection<IceCandidate> remoteCandidates);

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
    void removeWaitingAndFrozenPairs(IceCandidatePair pair);

    /**
     * Executes the specified {@link Closure} on candidate pairs in the 
     * check list.
     * 
     * @param closure The {@link Closure} to execute.
     */
    void executeOnPairs(Closure<IceCandidatePair> closure);

    /**
     * Selects the first pair matching the predicate from the normal check list.
     * 
     * @param pred The {@link Predicate} to check with.
     * @return The first matching pair, or <code>null</code> if no such pair
     * exists.
     */
    IceCandidatePair selectPair(Predicate<IceCandidatePair> pred);
   
    /**
     * Selects the first pair matching the predicate from any check list.
     * 
     * @param pred The {@link Predicate} to check with.
     * @return The first matching pair from any check list, including the 
     * triggered check queue, or <code>null</code> if no such pair exists.
     */
    IceCandidatePair selectAnyPair(Predicate<IceCandidatePair> pred);

    /**
     * Returns whether or not any pairs match the specified criteria.
     * 
     * @param pred The {@link Predicate} to match against.
     * @return <code>true</code> if any pairs match the specified criteria,
     * otherwise <code>false</code>.
     */
    boolean matchesAny(Predicate<IceCandidatePair> pred);

    /**
     * Returns whether all the pairs in the check list fit the given predicate.
     * 
     * @param pred The {@link Predicate} to match against.
     * @return <code>true</code> if all pairs match the specified criteria,
     * otherwise <code>false</code>.
     */
    boolean matchesAll(Predicate<IceCandidatePair> pred);
    
    /**
     * Close all candidates pair connections.
     */
    void close();

    

    }
