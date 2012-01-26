package org.lastbamboo.common.ice.candidate;

/**
 * Creates a new class for calculating pair priorities.
 */
public class IceCandidatePairPriorityCalculator
    {
    private IceCandidatePairPriorityCalculator()
        {
        // Should never be constructed.
        }

    /**
     * Calculates the priority for the candidates pair.
     * 
     * @param localCandidate The local candidate in the pair.
     * @param remoteCandidate The remote candidate in the pair.
     * @return The priority for the pair.
     */
    public static long calculatePriority(final IceCandidate localCandidate, 
        final IceCandidate remoteCandidate)
        {
        // See ICE section 5.7.2. 
        // Here's the formula for calculating pair priorities:
        // G = the priority of the controlling candidate.
        // D = the priority of the controlled candidate.
        // pair priority = 2^32*MIN(G,D) + 2*MAX(G,D) + (G>D?1:0)
        // 
        // Below we use:
        // pair priority = A + B + C
        final long G;
        final long D;
        if (localCandidate.isControlling())
            {
            G = localCandidate.getPriority();
            D = remoteCandidate.getPriority();
            }
        else
            {
            G = remoteCandidate.getPriority();
            D = localCandidate.getPriority();
            }
        final long A = (long) (Math.pow(2, 32) * Math.min(G, D));
        final long B = 2 * Math.max(G, D);
        final int C = G > D ? 1 : 0;
        
        final long pairPriority = A + B + C;
        return pairPriority;
        }
    }
