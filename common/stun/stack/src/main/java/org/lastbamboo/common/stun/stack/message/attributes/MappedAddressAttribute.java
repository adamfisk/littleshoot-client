package org.lastbamboo.common.stun.stack.message.attributes;

import java.net.InetSocketAddress;

/**
 * Mapped Address attribute.
 */
public class MappedAddressAttribute extends AbstractStunAddressAttribute
    {

    /**
     * Creates a new mapped address attribute.
     * 
     * @param socketAddress The IP and port to put in the attribute.
     */
    public MappedAddressAttribute(final InetSocketAddress socketAddress)
        {
        super(StunAttributeType.MAPPED_ADDRESS, socketAddress);
        }

    public void accept(final StunAttributeVisitor visitor)
        {
        visitor.visitMappedAddress(this);
        }

    }
