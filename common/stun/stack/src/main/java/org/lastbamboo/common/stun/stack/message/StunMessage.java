package org.lastbamboo.common.stun.stack.message;

import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;

/**
 * Interface for STUN messages.
 */
public interface StunMessage extends VisitableStunMessage
    {

    /**
     * Accessor for the ID of the message's transaction.
     * 
     * @return The transaction ID.
     */
    UUID getTransactionId();

    /**
     * Accessor for the total length of the message, including headers.
     * 
     * @return The total length of the message.
     */
    int getTotalLength();
    
    /**
     * Accessor for the message attributes.
     * 
     * @return The message attributes {@link Map}.
     */
    Map<StunAttributeType, StunAttribute> getAttributes();

    /**
     * Accessor for the length of the message body.
     * 
     * @return The length of the message body.
     */
    int getBodyLength();

    /**
     * Accessor for the type of the message.
     * 
     * @return The type of the message.
     */
    StunMessageType getType();

    }
