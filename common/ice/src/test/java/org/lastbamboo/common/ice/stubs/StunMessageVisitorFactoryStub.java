package org.lastbamboo.common.ice.stubs;

import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorAdapter;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorFactory;

public class StunMessageVisitorFactoryStub implements StunMessageVisitorFactory
    {

    public StunMessageVisitor createVisitor(IoSession session)
        {
        return new StunMessageVisitorAdapter();
        }

    }
