package org.lastbamboo.common.stun.stack.message.turn;

import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.AbstractStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.attributes.ErrorCodeAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a new response to an allocate request.  This includes the mapped
 * address the server has allocated to proxy data to the TURN client.
 */
public final class AllocateErrorResponse extends AbstractStunMessage
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * Creates a new successful response to an allocate request.
     * 
     * @param transactionId The ID of the transaction, matching the ID of the
     * request.
     * @param attributes The message attributes.
     */
    public AllocateErrorResponse(final UUID transactionId, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        super(transactionId, StunMessageType.ALLOCATE_ERROR_RESPONSE,
            attributes);
        }

    /**
     * Creates a new successful response to an allocate request.
     * 
     * @param transactionId The ID of the transaction, matching the ID of the
     * request.
     * @param errorCode The code for the error.
     * @param reasonPhrase The reason description.
     */
    public AllocateErrorResponse(final UUID transactionId,
        final int errorCode, final String reasonPhrase)
        {
        super(transactionId, StunMessageType.ALLOCATE_ERROR_RESPONSE,
            createAttributes(errorCode, reasonPhrase));
        }

    private static Map<StunAttributeType, StunAttribute> createAttributes(
        final int errorCode, final String reasonPhrase)
        {
        final StunAttribute error = 
            new ErrorCodeAttribute(errorCode, reasonPhrase);
        return createAttributes(error);
        }
    
    /**
     * Returns the full error code.
     * 
     * @return The full error code, including the class and the number.
     */
    public int getErrorCode()
        {
        final Map<StunAttributeType, StunAttribute> attributes = getAttributes();
        final ErrorCodeAttribute errorAttribute = 
            (ErrorCodeAttribute) attributes.get(StunAttributeType.ERROR_CODE);
        return errorAttribute.getErrorCode();
        }
    
    public <T> T accept(final StunMessageVisitor<T> visitor)
        {
        return visitor.visitAllocateErrorResponse(this);
        }

    }
