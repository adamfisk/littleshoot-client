package org.lastbamboo.common.stun.stack.message;

import org.lastbamboo.common.stun.stack.message.turn.AllocateErrorResponse;
import org.lastbamboo.common.stun.stack.message.turn.AllocateRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectionStatusIndication;
import org.lastbamboo.common.stun.stack.message.turn.DataIndication;
import org.lastbamboo.common.stun.stack.message.turn.SendIndication;
import org.lastbamboo.common.stun.stack.message.turn.AllocateSuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter class for convenient creation of message visitor subclasses.  This
 * will log errors whenever something is visited that's not overidden.  If
 * a subclass should handle a message, it therefore must override the 
 * appropriate visit method.
 * 
 * @param <T> The class the visitor methods return.
 */
public class StunMessageVisitorAdapter<T>
    implements StunMessageVisitor<T>
    {

    private Logger LOG = LoggerFactory.getLogger(
        StunMessageVisitorAdapter.class);
    
    public T visitAllocateRequest(final AllocateRequest request)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", request);
        return null;
        }

    public T visitBindingRequest(final BindingRequest request)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", request);
        return null;
        }

    public T visitConnectRequest(final ConnectRequest request)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", request);
        return null;
        }

    public T visitConnectionStatusIndication(
        final ConnectionStatusIndication indication)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", indication);
        return null;
        }

    public T visitDataIndication(final DataIndication data)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", data);
        return null;
        }

    public T visitSendIndication(final SendIndication request)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", request);
        return null;
        }

    public T visitAllocateSuccessResponse(
        final AllocateSuccessResponse response)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", response);
        return null;
        }

    public T visitAllocateErrorResponse(
        final AllocateErrorResponse response)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", response);
        return null;
        }

    public T visitBindingSuccessResponse(
        final BindingSuccessResponse response)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", response);
        return null;
        }
    
    public T visitBindingErrorResponse(
        final BindingErrorResponse response)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", response);
        return null;
        }
    
    public T visitNullMessage(final NullStunMessage message)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", message);
        return null;
        }

    public T visitCanceledMessage(final CanceledStunMessage message)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", message);
        return null;
        }
    
    public T visitConnectErrorMesssage(final ConnectErrorStunMessage message)
        {
        LOG.error(getClass().getSimpleName() + 
            " visiting unexpected message: {}", message);
        return null;
        }
    }
