package org.lastbamboo.common.stun.stack.message.attributes.turn;

import java.net.InetSocketAddress;

import org.lastbamboo.common.stun.stack.message.attributes.AbstractStunAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;

/**
 * Relay address attribute.
 */
public class RemoteAddressAttribute extends AbstractStunAddressAttribute
    {

    /**
     * Creates a new relay address attribute.
     * 
     * @param socketAddress The IP and port to put in the attribute.
     */
    public RemoteAddressAttribute(final InetSocketAddress socketAddress)
        {
        super(StunAttributeType.REMOTE_ADDRESS, socketAddress);
        }

    public void accept(final StunAttributeVisitor visitor)
        {
        visitor.visitRemoteAddress(this);
        }

    }
