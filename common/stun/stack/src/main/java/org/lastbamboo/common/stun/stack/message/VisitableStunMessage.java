package org.lastbamboo.common.stun.stack.message;

/**
 * Interface for STUN messages that are visitable by visitors.
 */
public interface VisitableStunMessage
    {

    /**
     * Accepts the specified visitor class.
     * 
     * @param <T> The type the visitor will return.
     * @param visitor The visitor to accept.
     * @return The return value of the visitor. 
     */
    <T> T accept(StunMessageVisitor<T> visitor);
    
    }
