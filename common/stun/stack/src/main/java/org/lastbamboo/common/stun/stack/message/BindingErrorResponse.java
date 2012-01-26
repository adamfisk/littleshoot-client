package org.lastbamboo.common.stun.stack.message;

import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.attributes.ErrorCodeAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;

/**
 * Binding error response message.
 */
public class BindingErrorResponse extends AbstractStunMessage
    {

    /**
     * Creates a new binding response message.
     * 
     * @param transactionId The ID of the transaction.
     * @param errorCode The code for the error.
     * @param reasonPhrase The reason description.
     */
    public BindingErrorResponse(final UUID transactionId, 
        final int errorCode, final String reasonPhrase)
        {
        super(transactionId, StunMessageType.BINDING_ERROR_RESPONSE, 
            createAttributes(errorCode, reasonPhrase));
        }
    
    /**
     * Creates a new binding response message.
     * 
     * @param transactionId The transaction ID of the response.
     * @param attributes The response attributes.
     */
    public BindingErrorResponse(final UUID transactionId, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        super(transactionId, StunMessageType.BINDING_ERROR_RESPONSE, 
            attributes);
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
        return visitor.visitBindingErrorResponse(this);
        }
    
    public String toString()
        {
        return getClass().getSimpleName()+" with attributes: "+getAttributes();
        }
    }
