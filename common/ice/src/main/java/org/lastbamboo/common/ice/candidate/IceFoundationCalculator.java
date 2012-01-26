package org.lastbamboo.common.ice.candidate;

import java.net.InetAddress;

import org.lastbamboo.common.ice.IceTransportProtocol;

/**
 * Class for calculating ICE foundations. 
 */
public class IceFoundationCalculator 
    {
    
    private IceFoundationCalculator()
        {
        // Make sure it's not constructed.
        }
    
    /**
     * Returns the foundation using the type and the base address.
     * 
     * @param type The ICE candidate type.
     * @param baseAddress The base address.
     * @param transport The transport protocol.
     * @return The calculated foundation.
     */
    public static String calculateFoundation(final IceCandidateType type, 
        final InetAddress baseAddress, final IceTransportProtocol transport)
        {
        // The string here is arbitrary -- it just has to be unique for 
        // different foundations.  We make it pretty explicit -- just writing
        // out the transport, type, and base address.
        return 
            transport.getName() + "-" + 
            type.toSdp() + "-" + 
            baseAddress.getHostAddress();
        }

    /**
     * Returns the foundation using the type, base address, and STUN server
     * address.
     * 
     * @param type The ICE candidate type.
     * @param baseAddress The base address.
     * @param transport The transport protocol.
     * @param stunServerAddress The STUN server address.
     * @return The calculated foundation.
     */
    public static String calculateFoundation(final IceCandidateType type, 
        final InetAddress baseAddress, final IceTransportProtocol transport,
        final InetAddress stunServerAddress)
        {
        return 
            transport.getName() + "-" + 
            type.toSdp() + "-" + 
            baseAddress.getHostAddress() + "-" +
            stunServerAddress.getHostAddress();
        }

    }
