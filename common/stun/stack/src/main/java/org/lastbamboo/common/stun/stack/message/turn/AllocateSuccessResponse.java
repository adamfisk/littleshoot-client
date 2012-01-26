package org.lastbamboo.common.stun.stack.message.turn;

import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.AbstractStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.attributes.MappedAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RelayAddressAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a new response to an allocate request.  This includes the mapped
 * address the server has allocated to proxy data to the TURN client.
 */
public final class AllocateSuccessResponse extends AbstractStunMessage
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final InetSocketAddress m_mappedAddress;

    private final InetSocketAddress m_relayAddress;

    /**
     * Creates a new successful response to an allocate request.
     * 
     * @param transactionId The ID of the transaction, matching the ID of the
     * request.
     * @param attributes The message attributes.
     */
    public AllocateSuccessResponse(final UUID transactionId, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        super(transactionId, StunMessageType.ALLOCATE_SUCCESS_RESPONSE,
            attributes);
        final MappedAddressAttribute ma = (MappedAddressAttribute) attributes.get(
            StunAttributeType.MAPPED_ADDRESS);
        
        final RelayAddressAttribute ra = (RelayAddressAttribute) attributes.get(
            StunAttributeType.RELAY_ADDRESS);
        m_mappedAddress = ma.getInetSocketAddress(); 
        m_relayAddress = ra.getInetSocketAddress();
        }

    /**
     * Creates a new successful response to an allocate request.
     * 
     * @param transactionId The ID of the transaction, matching the ID of the
     * request.
     * @param relayAddress The allocated RELAY ADDRESS on the server.
     * @param mappedAddress The MAPPED ADDRESS, or the "server reflexive"
     * address.
     */
    public AllocateSuccessResponse(final UUID transactionId,
        final InetSocketAddress relayAddress,
        final InetSocketAddress mappedAddress)
        {
        super(transactionId, StunMessageType.ALLOCATE_SUCCESS_RESPONSE,
            createAttributes(relayAddress, mappedAddress));
        this.m_mappedAddress = mappedAddress;
        this.m_relayAddress = relayAddress;
        }

    private static Map<StunAttributeType, StunAttribute> createAttributes(
        final InetSocketAddress relayAddress,
        final InetSocketAddress mappedAddress)
        {
        final RelayAddressAttribute ra = 
            new RelayAddressAttribute(relayAddress);
        final MappedAddressAttribute ma = 
            new MappedAddressAttribute(mappedAddress);
        return createAttributes(ra, ma);
        }

    /**
     * Accessor for the mapped address in the response.
     * 
     * @return The mapped address.
     */
    public InetSocketAddress getMappedAddress()
        {
        return m_mappedAddress;
        }

    /**
     * Accessor for the RELAY ADDRESS in the response.
     * 
     * @return The RELAY ADDRESS.
     */
    public InetSocketAddress getRelayAddress()
        {
        return this.m_relayAddress;
        }
    
    public <T> T accept(final StunMessageVisitor<T> visitor)
        {
        return visitor.visitAllocateSuccessResponse(this);
        }

    }
