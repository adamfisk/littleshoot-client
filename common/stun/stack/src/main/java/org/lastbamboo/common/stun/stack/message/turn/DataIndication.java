package org.lastbamboo.common.stun.stack.message.turn;

import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;

/**
 * TURN message that encapsulates data coming from a remote host for sending
 * on to the TURN client.
 */
public final class DataIndication extends AbstractStunDataMessage 
    {
    
    /**
     * Creates a new Data Indication message.
     * 
     * @param transactionId The transaction ID.
     * @param attributes The message attributes.
     */
    public DataIndication(final UUID transactionId, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        super(transactionId, StunMessageType.DATA_INDICATION, attributes);
        }

    /**
     * Creates a new Data Indication message.
     * 
     * @param remoteAddress The remote address the data arrived from.
     * @param data The data.
     */
    public DataIndication(final InetSocketAddress remoteAddress, 
        final byte[] data)
        {
        super(StunMessageType.DATA_INDICATION, data, remoteAddress);
        }

    public <T> T accept(final StunMessageVisitor<T> visitor)
        {
        return visitor.visitDataIndication(this);
        }
    }
