package org.lastbamboo.common.ice.candidate;

import java.util.Comparator;


/**
 * Comparator for ICE candidate pairs. 
 */
public class IceCandidatePairComparator implements Comparator<IceCandidatePair>
    {
    
    public int compare(final IceCandidatePair pair1, 
        final IceCandidatePair pair2)
        {
        final long pair1Priority = pair1.getPriority();
        final long pair2Priority = pair2.getPriority();
        
        if (pair1Priority > pair2Priority) return -1;
        if (pair1Priority < pair2Priority) return 1;
        return 0;
        }

    }
