package org.lastbamboo.common.stun.server;

import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorFactory;

/**
 * Class for creating STUN message visitors on the server side. 
 */
public class StunServerMessageVisitorFactory implements
    StunMessageVisitorFactory
    {

    public StunMessageVisitor createVisitor(final IoSession session)
        {
        return new StunServerMessageVisitor(session);
        }

    public StunMessageVisitor createVisitor(final IoSession session, 
        final Object attachment)
        {
        return new StunServerMessageVisitor(session);
        }

    }
