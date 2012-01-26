package org.lastbamboo.common.stun.stack.message;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;

/**
 * Placeholder class that forces callers to handle cases where the request
 * transaction was cancelled.
 */
public class CanceledStunMessage implements StunMessage
    {

    public Map<StunAttributeType, StunAttribute> getAttributes()
        {
        return Collections.emptyMap();
        }

    public int getBodyLength()
        {
        return 0;
        }

    public int getTotalLength()
        {
        return 0;
        }

    public UUID getTransactionId()
        {
        return UUID.randomUUID();
        }

    public StunMessageType getType()
        {
        return null;
        }

    public <T> T accept(final StunMessageVisitor<T> visitor)
        {
        return visitor.visitCanceledMessage(this);
        }

    }
