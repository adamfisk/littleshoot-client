package org.lastbamboo.common.stun.stack.message.attributes.ice;

import org.lastbamboo.common.stun.stack.message.attributes.AbstractStunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;

/**
 * The PRIORITY attribute indicates the priority that is to be associated 
 * with a peer reflexive candidate, should one be discovered by this check.  
 * It is a 32 bit unsigned integer.
 */
public final class IcePriorityAttribute extends AbstractStunAttribute {

    private final long m_priority;

    /**
     * Creates a new priority attribute.
     * 
     * @param priority
     *            The priority as an unsigned int.
     */
    public IcePriorityAttribute(final long priority) {
        super(StunAttributeType.ICE_PRIORITY, 4);
        m_priority = priority;
    }

    /**
     * Accessor for the priority as an unsigned int.
     * 
     * @return The priority as an unsigned int.
     */
    public long getPriority() {
        return m_priority;
    }

    public void accept(final StunAttributeVisitor visitor) {
        visitor.visitIcePriority(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " priority: " + m_priority;
    }
}
