package org.lastbamboo.common.ice.candidate;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumeration of ICE candidate types, such as host, relayed, server reflexive,
 * or peer reflexive. 
 */
public enum IceCandidateType
    {
    
    /**
     * Host candidates accessible on the local network.
     */
    HOST (126, "host"),
    
    /**
     * Candidates relayed through a STUN relay server.
     */
    RELAYED (0, "relay"),

    /**
     * Candidates with public addresses determined using a STUN server.
     */
    SERVER_REFLEXIVE (100, "srflx"),
    
    /**
     * Candidate discovered from exchanging STUN messages with peers.
     */
    PEER_REFLEXIVE (110, "prflx"),
    ;
    
    private final int m_typePreference;
    private final String m_sdp;

    private IceCandidateType(final int typePreference, final String sdp)
        {
        m_typePreference = typePreference;
        m_sdp = sdp;
        }

    /**
     * Accessor for the type preference used in the formula for calculating
     * candidate priorities.
     * 
     * @return The type preference for calculating candidate priorities.
     */
    public int getTypePreference()
        {
        return m_typePreference;
        }
    
    /**
     * Gets the SDP representation of this type, such as "relay", "host",
     * "srflx" or "prflx".
     * 
     * @return The SDP representation of this type.
     */
    public String toSdp()
        {
        return this.m_sdp;
        }
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(IceCandidateType.class);
    
    private static final Map<String, IceCandidateType> s_sdpToType = 
        new HashMap<String, IceCandidateType>();
    
    static
        {
        for (final IceCandidateType type : values())
            {
            s_sdpToType.put(type.toSdp(), type);
            }
        }
    
    /**
     * Gets the type for the associated SDP encoding.
     * 
     * @param sdp The SDP for the type.
     * @return The corresponding type.
     */
    public static IceCandidateType toType(final String sdp)
        {
        final IceCandidateType type = s_sdpToType.get(sdp);
        if (type == null)
            {
            LOG.error("No matching type for: "+sdp);
            }
        return type;
        }
    }
