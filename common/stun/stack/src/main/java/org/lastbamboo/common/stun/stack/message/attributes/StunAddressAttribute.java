package org.lastbamboo.common.stun.stack.message.attributes;

import java.net.InetSocketAddress;

/**
 * Interface for STUN address attributes.
 */
public interface StunAddressAttribute extends StunAttribute
    {
    
    /**
     * Accessor for the address and port in the address.
     * 
     * @return The address and port.
     */
    InetSocketAddress getInetSocketAddress();

    /**
     * Accessor for the family of the address.
     * 
     * @return The family of the address, either IPv4 or IPv6.
     */
    int getAddressFamily();

    }
