package org.lastbamboo.common.stun.stack.message;

import org.littleshoot.mina.common.IoSession;

/**
 * Factory for creating STUN message visitors.  Implementing classes might
 * include a factory for the server and a factory for the client, for example.
 * 
 * @param <T> The type visitors return.
 */
public interface StunMessageVisitorFactory<T>
    {

    /**
     * Creates a new visitor.
     * 
     * @param session The {@link IoSession} for reading or writing any necessary
     * data.
     * @return The new visitor.
     */
    StunMessageVisitor<T> createVisitor(IoSession session);

    }
