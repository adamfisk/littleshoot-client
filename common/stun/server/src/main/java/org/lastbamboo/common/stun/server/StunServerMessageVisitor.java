package org.lastbamboo.common.stun.server;

import java.net.InetSocketAddress;

import org.apache.commons.id.uuid.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.BindingSuccessResponse;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorAdapter;

/**
 * Class that visits read messages on a STUN server.
 */
public class StunServerMessageVisitor extends StunMessageVisitorAdapter<Object>
    {

    private final Logger LOG = LoggerFactory.getLogger(StunServerMessageVisitor.class);
    
    private final IoSession m_session;

    /**
     * Creates a new visitor for visiting STUN messages on the server side.
     * 
     * @param session The MINA IO session.
     */
    public StunServerMessageVisitor(final IoSession session)
        {
        m_session = session;
        }

    public Object visitBindingRequest(final BindingRequest binding)
        {
        LOG.debug("STUN server visiting binding request...");
        final InetSocketAddress address = 
            (InetSocketAddress) m_session.getRemoteAddress();
        
        final UUID transactionId = binding.getTransactionId();
        final StunMessage response = 
            new BindingSuccessResponse(transactionId.getRawBytes(), address);
        
        this.m_session.write(response);
        return null;
        }
    }
