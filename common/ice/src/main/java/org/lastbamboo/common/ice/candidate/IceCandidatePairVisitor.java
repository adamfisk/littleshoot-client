package org.lastbamboo.common.ice.candidate;

/**
 * Interface for classes that visit ICE candidate pairs.
 *  
 * @param <T> The class to return from visit methods.
 */
public interface IceCandidatePairVisitor<T>
    {

    /**
     * Visits a UDP candidate pair.
     * 
     * @param pair The candidate pair.
     * @return A specific class for this visitor.
     * 
     */
    T visitUdpIceCandidatePair(IceUdpCandidatePair pair);

    }
