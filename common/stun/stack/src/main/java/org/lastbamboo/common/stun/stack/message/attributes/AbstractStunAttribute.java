package org.lastbamboo.common.stun.stack.message.attributes;

/**
 * Abstracts out common STUN attribute functionality.
 */
public abstract class AbstractStunAttribute implements StunAttribute
    {

    private final int m_bodyLength;
    private final StunAttributeType m_attributeType;

    /**
     * Creates a new attribute.
     * 
     * @param attributeType The type of the attribute.
     * @param bodyLength The length of the attribute body in bytes.
     */
    public AbstractStunAttribute(final StunAttributeType attributeType, 
        final int bodyLength)
        {
        m_attributeType = attributeType;
        m_bodyLength = bodyLength;
        }

    public int getBodyLength()
        {
        return m_bodyLength;
        }
    
    public int getTotalLength()
        {
        return m_bodyLength + 4;
        }

    public StunAttributeType getAttributeType()
        {
        return m_attributeType;
        }

    @Override
    public String toString()
        {
        return getClass().getSimpleName();
        }
    }
