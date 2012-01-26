package org.lastbamboo.common.stun.stack.message;

import org.lastbamboo.common.stun.stack.message.turn.AllocateErrorResponse;
import org.lastbamboo.common.stun.stack.message.turn.AllocateRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectionStatusIndication;
import org.lastbamboo.common.stun.stack.message.turn.AllocateSuccessResponse;
import org.lastbamboo.common.stun.stack.message.turn.DataIndication;
import org.lastbamboo.common.stun.stack.message.turn.SendIndication;

/**
 * Visitor for various STUN messages.
 * 
 * @param <T> The return type for this visitor.
 */
public interface StunMessageVisitor<T>
    {

    /**
     * Visits a binding message.
     * 
     * @param binding The binding message.
     * @return The return type for this visitor.
     */
    T visitBindingRequest(BindingRequest binding);

    /**
     * Visits a binding success response.
     * 
     * @param response The binding success response.
     * @return The return type for this visitor.
     */
    T visitBindingSuccessResponse(BindingSuccessResponse response);
    
    /**
     * Visits a binding error response.
     * 
     * @param response Binding error response.
     * @return The return type for this visitor.
     */
    T visitBindingErrorResponse(BindingErrorResponse response);

    /**
     * Visits the TURN usage allocate request message.
     * 
     * @param request The TURN usage allocate request.
     * @return The return type for this visitor.
     */
    T visitAllocateRequest(AllocateRequest request);

    /**
     * Visits a response for a successful Allocate Request.
     * 
     * @param response The response to a successful Allocate Request.
     * @return The return type for this visitor.
     */
    T visitAllocateSuccessResponse(AllocateSuccessResponse response);
 
    /**
     * Visits a response for a failed Allocate Request.
     * 
     * @param response The response to a failed Allocate Request.
     * @return The return type for this visitor.
     */
    T visitAllocateErrorResponse(AllocateErrorResponse response);

    T visitDataIndication(DataIndication data);

    T visitSendIndication(SendIndication request);
    
    /**
     * Visits a connection request from a client.  Only STUN servers will
     * respond to this request.  This request indicates the client wishes
     * to allow connections from the host specified in the REMOTE ADDRESS
     * attribute.
     * 
     * @param request The connect request.
     * @return The return type for this visitor.
     */
    T visitConnectRequest(ConnectRequest request);
    
    /**
     * Visits a connection status indication message informing clients of the
     * connection status of remote hosts. The connection status is sent in the
     * CONNECT STAT attribute.
     * 
     * @param indication The connection status indication message.
     * @return The return type for this visitor.
     */
    T visitConnectionStatusIndication(ConnectionStatusIndication indication);

    /**
     * Visits the absence of a message.  This can occur, for example, when a
     * request receives no response whatsoever.
     * 
     * @param message The message to visit.
     * @return The return type for this visitor.
     */
    T visitNullMessage(NullStunMessage message);

    /**
     * Visits a STUN message indicating the STUN transaction was canceled.  
     * This allows visitors to differentiate between canceled messages and 
     * messages where the server never responded.
     * 
     * @param message The canceled message.
     * @return The type the visitor returns.
     */
    T visitCanceledMessage(CanceledStunMessage message);

    /**
     * Visits a STUN "message" indicating there was a connection error, such
     * as an ICMP error or a failure to create a TCP connection.
     * 
     * @param message The connection error message.
     * @return The type the visitor returns.
     */
    T visitConnectErrorMesssage(ConnectErrorStunMessage message);

    }
