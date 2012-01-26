package org.lastbamboo.integration.tests.stubs;

import org.littleshoot.stun.stack.message.BindingErrorResponse;
import org.littleshoot.stun.stack.message.BindingRequest;
import org.littleshoot.stun.stack.message.BindingSuccessResponse;
import org.littleshoot.stun.stack.message.CanceledStunMessage;
import org.littleshoot.stun.stack.message.ConnectErrorStunMessage;
import org.littleshoot.stun.stack.message.NullStunMessage;
import org.littleshoot.stun.stack.message.StunMessage;
import org.littleshoot.stun.stack.message.StunMessageVisitor;
import org.littleshoot.stun.stack.message.turn.AllocateErrorResponse;
import org.littleshoot.stun.stack.message.turn.AllocateRequest;
import org.littleshoot.stun.stack.message.turn.AllocateSuccessResponse;
import org.littleshoot.stun.stack.message.turn.ConnectRequest;
import org.littleshoot.stun.stack.message.turn.ConnectionStatusIndication;
import org.littleshoot.stun.stack.message.turn.DataIndication;
import org.littleshoot.stun.stack.message.turn.SendIndication;

public class StunMessageVisitorStub implements StunMessageVisitor<StunMessage>
    {

    public StunMessage visitAllocateErrorResponse(AllocateErrorResponse response)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitAllocateRequest(AllocateRequest request)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitAllocateSuccessResponse(
            AllocateSuccessResponse response)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitBindingErrorResponse(BindingErrorResponse response)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitBindingRequest(BindingRequest binding)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitBindingSuccessResponse(
            BindingSuccessResponse response)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitCanceledMessage(CanceledStunMessage message)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitConnectRequest(ConnectRequest request)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitConnectionStatusIndication(
            ConnectionStatusIndication indication)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitDataIndication(DataIndication data)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitConnectErrorMesssage(ConnectErrorStunMessage message)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitNullMessage(NullStunMessage message)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public StunMessage visitSendIndication(SendIndication request)
        {
        // TODO Auto-generated method stub
        return null;
        }

    }
