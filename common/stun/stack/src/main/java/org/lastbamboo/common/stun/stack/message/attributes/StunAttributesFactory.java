package org.lastbamboo.common.stun.stack.message.attributes;

import java.util.Map;

import org.littleshoot.mina.common.ByteBuffer;

/**
 * Factory for creating STUN attributes.
 */
public interface StunAttributesFactory
    {

    /**
     * Creates a new {@link Map} of STUN attributes from the specified message
     * body.
     * 
     * @param body The body of the STUN message.
     * @return The {@link Map} of attribute types to STUN attributes.
     */
    Map<StunAttributeType, StunAttribute> createAttributes(ByteBuffer body);

    }
