package org.lastbamboo.common.stun.stack.message.attributes.turn;

import java.net.InetSocketAddress;

import org.lastbamboo.common.stun.stack.message.attributes.AbstractStunAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;

/**
 * Relay address attribute.
 */
public class RelayAddressAttribute extends AbstractStunAddressAttribute
    {

    /**
     * Creates a new relay address attribute.
     * 
     * @param socketAddress The IP and port to put in the attribute.
     */
    public RelayAddressAttribute(final InetSocketAddress socketAddress)
        {
        super(StunAttributeType.RELAY_ADDRESS, socketAddress);
        }

    public void accept(final StunAttributeVisitor visitor)
        {
        visitor.visitRelayAddress(this);
        }

    }
