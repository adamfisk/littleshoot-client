package org.lastbamboo.common.stun.stack.message.turn;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.AbstractStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.turn.ConnectionStatus;
import org.lastbamboo.common.stun.stack.message.attributes.turn.ConnectionStatusAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RemoteAddressAttribute;

/**
 * Message indicating the connection status of a specific remote host.
 */
public class ConnectionStatusIndication extends AbstractStunMessage
    {

    private final InetSocketAddress m_remoteAddress;
    private final ConnectionStatus m_connectionStatus;


    /**
     * Creates a new connection status indication message.
     * 
     * @param transactionId The ID of the transaction.
     * @param attributes The message attributes.
     */
    public ConnectionStatusIndication(final UUID transactionId, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        super(transactionId, StunMessageType.CONNECTION_STATUS_INDICATION,
            attributes);
        final RemoteAddressAttribute att = 
            (RemoteAddressAttribute)attributes.get(
                StunAttributeType.REMOTE_ADDRESS);
        this.m_remoteAddress = att.getInetSocketAddress();
        
        final ConnectionStatusAttribute csa =
            (ConnectionStatusAttribute)attributes.get(
                StunAttributeType.CONNECT_STAT);
        this.m_connectionStatus = csa.getConnectionStatus();
        }

    /**
     * Creates a new connection status indication message.
     * 
     * @param remoteAddress The remote address we're indicating the status of.
     * @param connectionStatus The connection status.
     */
    public ConnectionStatusIndication(final InetSocketAddress remoteAddress, 
        final ConnectionStatus connectionStatus)
        {
        super(UUID.randomUUID(), StunMessageType.CONNECTION_STATUS_INDICATION,
            createAttributes(remoteAddress, connectionStatus));
        this.m_remoteAddress = remoteAddress;
        this.m_connectionStatus = connectionStatus;
        }

    private static Map<StunAttributeType, StunAttribute> createAttributes(
        final InetSocketAddress remoteAddress, 
        final ConnectionStatus connectionStatus)
        {
        final Map<StunAttributeType, StunAttribute> attributes = 
            new ConcurrentHashMap<StunAttributeType, StunAttribute>();
        final StunAttribute remoteAddressAttribute =
            new RemoteAddressAttribute(remoteAddress);
        final StunAttribute status = 
            new ConnectionStatusAttribute(connectionStatus);
        attributes.put(remoteAddressAttribute.getAttributeType(), 
            remoteAddressAttribute);
        attributes.put(status.getAttributeType(), status);
        return attributes;
        }

    public <T> T accept(StunMessageVisitor<T> visitor)
        {
        return visitor.visitConnectionStatusIndication(this);
        }

    /**
     * Accessor for the connection status.
     * 
     * @return The connection status.
     */
    public ConnectionStatus getConnectionStatus()
        {
        return m_connectionStatus;
        }

    /**
     * Accessor for the remote address.
     * 
     * @return The remote address.
     */
    public InetSocketAddress getRemoteAddress()
        {
        return m_remoteAddress;
        }

    }
