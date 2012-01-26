package org.lastbamboo.common.stun.stack.message.attributes.ice;

import java.math.BigInteger;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.support.ByteBufferHexDumper;
import org.lastbamboo.common.stun.stack.message.attributes.AbstractStunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ICE-CONTROLLING attribute is present in a Binding Request, and 
 * indicates that the client believes it is currently in the controlling
 * role.  The content of the attribute is a 64 bit unsigned integer in 
 * network byte ordering, which contains a random number used for 
 * tie-breaking of role conflicts.
 */
public final class IceControllingAttribute extends AbstractStunAttribute 
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final byte[] m_tieBreaker;

    /**
     * Creates a new ICE-CONTROLLING attribute.
     * 
     * @param tieBreaker The tie-breaker for control conflicts.
     */
    public IceControllingAttribute(final byte[] tieBreaker)
        {
        super(StunAttributeType.ICE_CONTROLLING, 
            tieBreaker.length);
        m_tieBreaker = tieBreaker;
        }

    /**
     * Accessor for the random 64 bit positive integer that serves as the 
     * tie-breaker.
     *  
     * @return The 64 bit positive integer for this attribute.
     */
    public byte[] getTieBreaker()
        {
        return m_tieBreaker;
        }
    
    public void accept(final StunAttributeVisitor visitor)
        {
        visitor.visitIceControlling(this);
        }

    public String toString()
        {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" with tie breaker: ");
        //final byte[] bytes = this.m_tieBreaker;
        final ByteBuffer buf = ByteBuffer.wrap(this.m_tieBreaker);
        sb.append(ByteBufferHexDumper.getHexdump(buf));
        //return getClass().getSimpleName() + " with tie breaker: " +
          //  this.m_tieBreaker;
        return sb.toString();
        }
    }
