package org.lastbamboo.common.ice;

import org.lastbamboo.common.ice.candidate.IceCandidateType;

/**
 * Class for calculating ICE priorities.  This includes the transport 
 * preferences as discussed in draft-ietf-mmusic-ice-tcp-04.  Since this 
 * implementation is initially intended for file exchange and not media 
 * sessions, we reverse the recommended TCP and UDP preferences in favor of
 * TCP. 
 */
public class IcePriorityCalculator
    {
    
    /**
     * The component ID is 1 unless otherwise specified.
     */
    private final static int DEFAULT_COMPONENT_ID = 1;
    
    /**
     * The is the local interface preference for calculating ICE priorities.
     * This is set to the highest possible value because we currently
     * only use one interface.
     */
    private static final int LOCAL_PREFERENCE = 65535;
    
    private static final int TCP_TRANSPORT_PREF = 15;
    
    private static final int UDP_TRANSPORT_PREF = 6;

    /**
     * Calculates teh priority for the specified type using the default 
     * component ID of 1 and the default local preference.
     * 
     * @param type The type of the candidate.
     * @param transport 
     * @return The priority.
     */
    public static long calculatePriority(final IceCandidateType type, 
        final IceTransportProtocol transport)
        {
        // See draft-ietf-mmusic-ice-17.txt section 4.1.2.1.
        return
            (long) (Math.pow(2, 24) * type.getTypePreference()) +
            (long) (Math.pow(2, 8) * calculateLocalPreference(transport))+
            (int) (Math.pow(2, 0) * (256 - DEFAULT_COMPONENT_ID));
        }

    /**
     * Taken from draft-ietf-mmusic-ice-tcp-04 for establishing preferences
     * for transports.
     * 
     * @param transport The transport protocol in use.
     * @return The local preference.
     */
    private static long calculateLocalPreference(
        final IceTransportProtocol transport)
        {
        long transportPref = 0;
        long directionPref = 0;
        switch (transport)
            {
            case TCP_ACT:
                transportPref = TCP_TRANSPORT_PREF;
                directionPref = 5;
                break;
            case TCP_PASS:
                transportPref = TCP_TRANSPORT_PREF;
                directionPref = 2;
                break;
            case TCP_SO:
                transportPref = TCP_TRANSPORT_PREF;
                directionPref = 7;
                break;
            case UDP:
                transportPref = UDP_TRANSPORT_PREF;
                // We just choose a neutral direction pref for UDP.
                directionPref = 4;
                break;
            }
        
        // According to draft-ietf-mmusic-ice-tcp-04 the other-pref should
        // be different if two candidates have the same type-pref,
        // transport-pref, and direction-pref.  We have no way of telling what
        // the other candidates have at this point, though, so it's difficult 
        // to tell.  
        //
        // Theoretically each candidate could store each value, and the 
        // priority could be calculated on the fly in compareTo.  This only
        // applies to multi-homed hosts.
        final int otherPref = 511;
        
        return
            (long) (Math.pow(2, 12) * transportPref) +
            (long) (Math.pow(2, 9) * directionPref) +
            (int) (Math.pow(2, 0) * otherPref);
        }
    }
