package org.lastbamboo.common.stun.stack.message.attributes.ice;

import org.lastbamboo.common.stun.stack.message.attributes.AbstractStunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;

/**
 * The USE-CANDIDATE attribute indicates that the candidate pair resulting 
 * from this check should be used for transmission of media.  The attribute 
 * has no content (the Length field of the attribute is zero); it serves 
 * as a flag.
 */
public final class IceUseCandidateAttribute extends AbstractStunAttribute 
    {

    /**
     * Creates a new USE-CANDIDATE attribute.
     */
    public IceUseCandidateAttribute()
        {
        super(StunAttributeType.ICE_USE_CANDIDATE, 0);
        }
    
    public void accept(final StunAttributeVisitor visitor)
        {
        visitor.visitIceUseCandidate(this);
        }
    
    @Override
    public String toString()
        {
        return getClass().getSimpleName();
        }
    }
