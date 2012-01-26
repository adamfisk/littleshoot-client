package org.lastbamboo.common.stun.stack.message;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.attributes.MappedAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Binding response message.
 */
public class BindingSuccessResponse extends AbstractStunMessage {

    private final Logger LOG = LoggerFactory
            .getLogger(BindingSuccessResponse.class);
    private final InetSocketAddress m_mappedAddress;

    /**
     * Creates a new binding response message.
     * 
     * @param transactionId
     *            The ID of the transaction.
     * @param address
     *            The mapped address.
     */
    public BindingSuccessResponse(final byte[] transactionId,
            final InetSocketAddress address) {
        super(new UUID(transactionId),
                StunMessageType.BINDING_SUCCESS_RESPONSE,
                createAttributes(address));
        m_mappedAddress = address;
    }

    /**
     * Creates a new binding response message.
     * 
     * @param transactionId
     *            The transaction ID of the response.
     * @param attributes
     *            The response attributes.
     */
    public BindingSuccessResponse(final UUID transactionId,
            final Map<StunAttributeType, StunAttribute> attributes) {
        super(transactionId, StunMessageType.BINDING_SUCCESS_RESPONSE,
                attributes);
        m_mappedAddress = getAddress(attributes);
    }

    private InetSocketAddress getAddress(
            final Map<StunAttributeType, StunAttribute> attributes) {
        final MappedAddressAttribute mappedAddress = (MappedAddressAttribute) attributes
                .get(StunAttributeType.MAPPED_ADDRESS);
        if (mappedAddress == null) {
            LOG.error("No mapped address in: " + attributes.values());
            return null;
        }
        return mappedAddress.getInetSocketAddress();
    }

    private static Map<StunAttributeType, StunAttribute> createAttributes(
            final InetSocketAddress address) {
        final Map<StunAttributeType, StunAttribute> attributes = new HashMap<StunAttributeType, StunAttribute>();

        final StunAttribute attribute = new MappedAddressAttribute(address);
        attributes.put(StunAttributeType.MAPPED_ADDRESS, attribute);

        return attributes;
    }

    /**
     * Accessor for the mapped address received in the binding response.
     * 
     * @return The client's mapped address.
     */
    public InetSocketAddress getMappedAddress() {
        return m_mappedAddress;
    }

    public <T> T accept(final StunMessageVisitor<T> visitor) {
        return visitor.visitBindingSuccessResponse(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " with MAPPED ADDRESS: "
                + getMappedAddress();
    }

}
