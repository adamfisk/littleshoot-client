package org.lastbamboo.common.ice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumeration of ICE transport protocols for encoding in SDP>
 */
public enum IceTransportProtocol
    {

    /**
     * Simultaneous open.
     */
    TCP_SO("tcp-so"),
    
    /**
     * Active.
     */
    TCP_ACT("tcp-act"),
    
    /**
     * Passive.
     */
    TCP_PASS("tcp-pass"), 
    
    /**
     * UDP protocol.
     */
    UDP("udp"), 

    ;
    
    private final String m_name;

    private IceTransportProtocol(final String name)
        {
        m_name = name;
        }

    /**
     * Accessor for the name of the protocol for encoding in SDP.
     * 
     * @return The name of the protocol.
     */
    public String getName()
        {
        return m_name;
        }
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(IceTransportProtocol.class);
    
    private static final Map<String, IceTransportProtocol> s_sdpToTransport = 
        new HashMap<String, IceTransportProtocol>();
    
    static
        {
        for (final IceTransportProtocol type : values())
            {
            s_sdpToTransport.put(type.getName(), type);
            }
        }
    
    /**
     * Gets the type for the associated SDP encoding.
     * 
     * @param sdp The SDP for the type.
     * @return The corresponding transport.
     */
    public static IceTransportProtocol toTransport(final String sdp)
        {
        final IceTransportProtocol type = 
            s_sdpToTransport.get(sdp.toLowerCase(Locale.US));
        if (type == null)
            {
            LOG.error("No matching type for: "+sdp);
            }
        return type;
        }
    }
