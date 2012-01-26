package org.lastbamboo.common.stun.stack.encoder;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;
import org.littleshoot.util.mina.MinaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes bytes into STUN messages.  This is separate from the MINA
 * protocol encoder for easier testing.
 */
public class StunMessageEncoder
    {

    private final Logger m_log = 
        LoggerFactory.getLogger(StunMessageEncoder.class);
    
    /**
     * Encodes a {@link StunMessage} into a {@link ByteBuffer}.
     * 
     * @param stunMessage The STUN message to encode.
     * @return The message encoded in a {@link ByteBuffer} ready for writing
     * (flipped).
     */
    public ByteBuffer encode(final StunMessage stunMessage) 
        {
        final int length = stunMessage.getTotalLength();
        final ByteBuffer buf = ByteBuffer.allocate(length);
        
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Total message length: "+length+" for STUN message: "+
                stunMessage);
            }
        final StunMessageType type = stunMessage.getType();
        MinaUtils.putUnsignedShort(buf, type.toInt());
        MinaUtils.putUnsignedShort(buf, stunMessage.getBodyLength());
        
        final UUID transactionId = stunMessage.getTransactionId();

        buf.put(transactionId.getRawBytes());
        
        final Map<StunAttributeType, StunAttribute> attributes = 
            stunMessage.getAttributes();
        
        putAttributes(attributes, buf);
        
        buf.flip();
        m_log.debug("Encoded STUN message as buf: {}", buf);
        return buf;
        }

    private void putAttributes(
        final Map<StunAttributeType, StunAttribute> attributesMap, 
        final ByteBuffer buf)
        {
        final StunAttributeVisitor visitor = new StunAttributeEncoder(buf);
        final Collection<StunAttribute> attributes = attributesMap.values();
        for (final StunAttribute attribute : attributes)
            {
            attribute.accept(visitor);
            }
        }

    }
