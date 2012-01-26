package org.lastbamboo.common.stun.stack.message;

import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A STUN Binding message.
 */
public class BindingRequest extends AbstractStunMessage 
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(BindingRequest.class);
    
    /**
     * Creates a new STUN binding message.
     * 
     * @param id The message's transaction ID.
     * @param attributes Additional Binding Request attributes, typically 
     * attributes associated with a particular STUN usage.
     */
    public BindingRequest(final UUID id, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        super(id, StunMessageType.BINDING_REQUEST, attributes);
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Created Binding Request from network...");
            }
        }

    /**
     * Creates a new binding request from scratch.
     */
    public BindingRequest()
        {
        super(StunMessageType.BINDING_REQUEST);
        }

    /**
     * Creates a new Binding Request with the specified attributes.
     * 
     * @param attributes Additional Binding Request attributes, typically 
     * attributes associated with a particular STUN usage.
     */
    public BindingRequest(final StunAttribute... attributes)
        {
        super(StunMessageType.BINDING_REQUEST, createAttributes(attributes));
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Created Binding Request from scratch: {}", this);
            }
        }

    public <T> T accept(final StunMessageVisitor<T> visitor)
        {
        return visitor.visitBindingRequest(this);
        }

    }
