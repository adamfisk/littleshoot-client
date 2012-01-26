package org.lastbamboo.integration.tests.stubs;

import org.littleshoot.mina.common.IoSession;
import org.littleshoot.stun.stack.message.StunMessage;
import org.littleshoot.stun.stack.message.StunMessageVisitor;
import org.littleshoot.stun.stack.message.StunMessageVisitorFactory;

public class StunMessageVisitorFactoryStub implements
        StunMessageVisitorFactory<StunMessage>
    {

    public StunMessageVisitor<StunMessage> createVisitor(IoSession session)
        {
        return new StunMessageVisitorStub();
        }
    }
