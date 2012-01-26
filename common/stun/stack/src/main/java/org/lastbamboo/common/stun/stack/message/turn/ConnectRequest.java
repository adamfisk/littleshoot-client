package org.lastbamboo.common.stun.stack.message.turn;

import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.AbstractStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RemoteAddressAttribute;

/**
 * A TURN connect request message.
 */
public class ConnectRequest extends AbstractStunMessage
    {

    private final InetSocketAddress m_remoteAddress;

    /**
     * Creates a new connection request.
     * 
     * @param transactionId The ID of the transaction.
     * @param attributes
     */
    public ConnectRequest(final UUID transactionId, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        super(transactionId, StunMessageType.CONNECT_REQUEST, attributes);
        m_remoteAddress = 
            ((RemoteAddressAttribute) attributes.get(
                StunAttributeType.REMOTE_ADDRESS)).getInetSocketAddress();
        }

    /**
     * Creates a new connect request for the specified remote address.
     * 
     * @param remoteAddress The remote host to connect to. 
     */
    public ConnectRequest(final InetSocketAddress remoteAddress)
        {
        super(UUID.randomUUID(), StunMessageType.CONNECT_REQUEST, 
            createRemoteAddress(remoteAddress));
        this.m_remoteAddress = remoteAddress;
        }

    /**
     * Accessor for the remote address attribute.
     * 
     * @return The remote address attribute.
     */
    public InetSocketAddress getRemoteAddress()
        {
        return m_remoteAddress;
        }
    
    public <T> T accept(final StunMessageVisitor<T> visitor)
        {
        return visitor.visitConnectRequest(this);
        }

    }
