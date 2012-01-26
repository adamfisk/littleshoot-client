package org.lastbamboo.common.stun.stack.decoder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.lastbamboo.common.stun.stack.message.BindingErrorResponse;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.BindingSuccessResponse;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributesFactory;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributesFactoryImpl;
import org.lastbamboo.common.stun.stack.message.turn.AllocateErrorResponse;
import org.lastbamboo.common.stun.stack.message.turn.AllocateRequest;
import org.lastbamboo.common.stun.stack.message.turn.AllocateSuccessResponse;
import org.lastbamboo.common.stun.stack.message.turn.ConnectRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectionStatusIndication;
import org.lastbamboo.common.stun.stack.message.turn.DataIndication;
import org.lastbamboo.common.stun.stack.message.turn.SendIndication;
import org.littleshoot.util.mina.DecodingState;
import org.littleshoot.util.mina.DecodingStateMachine;
import org.littleshoot.util.mina.FixedLengthDecodingState;
import org.littleshoot.util.mina.MinaUtils;
import org.littleshoot.util.mina.decode.binary.UnsignedShortDecodingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State machine for decoding STUN messages.
 */
public class StunMessageDecodingState extends DecodingStateMachine 
    {

    private final static Logger m_log = 
        LoggerFactory.getLogger(StunMessageDecodingState.class);
    
    private static final Map<StunAttributeType, StunAttribute> 
        EMPTY_ATTRIBUTES = Collections.emptyMap();
    
    @Override
    protected DecodingState init() throws Exception
        {
        m_log.debug("Initing...");
        return new ReadMessageType();
        }

    @Override
    protected void destroy() throws Exception
        {
        }
    
    @Override
    protected DecodingState finishDecode(final List<Object> childProducts, 
        final ProtocolDecoderOutput out) throws Exception
        {
        m_log.error("Got finish decode for full message");
        return null;
        }
    
    private static class ReadMessageType extends UnsignedShortDecodingState
        {
    
        @Override
        protected DecodingState finishDecode(final int decoded, 
            final ProtocolDecoderOutput out) throws Exception
            {
            return new ReadMessageLength(decoded);
            }
        }
    
    private static class ReadMessageLength extends UnsignedShortDecodingState
        {

        private final int m_messageType;

        private ReadMessageLength(final int messageType)
            {
            m_messageType = messageType;
            }

        @Override
        protected DecodingState finishDecode(final int decoded, 
            final ProtocolDecoderOutput out) throws Exception
            {
            m_log.debug("Read message length: "+decoded);
            return new ReadTransactionId(this.m_messageType, decoded);
            }
    
        }
    
    private static class ReadTransactionId extends FixedLengthDecodingState
        {

        private final int m_messageType;
        private final int m_messageLength;

        private ReadTransactionId(final int messageType, 
            final int messageLength)
            {
            super(16);
            m_messageType = messageType;
            m_messageLength = messageLength;
            }

        @Override
        protected DecodingState finishDecode(final ByteBuffer readData, 
            final ProtocolDecoderOutput out) throws Exception
            {
            // This copy is not ideal, but passing around ByteBuffers was 
            // causing issues.
            final byte[] transactionId = MinaUtils.toByteArray(readData);
            m_log.debug("Read transaction id...");
            if (this.m_messageLength > 0)
                {
                return new ReadBody(this.m_messageType, this.m_messageLength, 
                    transactionId);                
                }
            else
                {
                m_log.debug("Handling empty body");
                final StunMessage message = 
                    createMessage(this.m_messageType, transactionId, 
                        EMPTY_ATTRIBUTES);
                out.write(message);
                return null;
                }
            }
        }
    
    private static class ReadBody extends FixedLengthDecodingState
        {

        private final int m_type;
        private final byte[] m_transactionId;

        private ReadBody(final int type, final int length, 
            final byte[] transactionId)
            {
            super(length);
            m_type = type;
            m_transactionId = transactionId;
            }

        @Override
        protected DecodingState finishDecode(final ByteBuffer readData, 
            final ProtocolDecoderOutput out) throws Exception
            {
            if (readData.remaining() != m_length)
                {
                m_log.error("Read body of unexpected length." +
                    "\nExpected length:  "+m_length+
                    "\nRemaining length: "+readData.remaining());
                }
            final StunAttributesFactory factory = 
                new StunAttributesFactoryImpl();
            
            // This decodes the entire body into an attributes map.
            final Map<StunAttributeType, StunAttribute> attributes = 
                factory.createAttributes(readData);
            
            final StunMessage message = 
                createMessage(this.m_type, this.m_transactionId, attributes);
            
            out.write(message);
            return null;
            }
        }
    
    private static StunMessage createMessage(final int type,
        final byte[] transactionId, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        final UUID id = new UUID(transactionId);
        final StunMessageType messageType = StunMessageType.toType(type);
        if (messageType == null)
            {
            m_log.warn("Unrecognized type: "+type);
            throw new IllegalArgumentException("Unrecognized type: "+type);
            }
        m_log.debug("Decoded STUN message type: {}", messageType);
        switch (messageType)
            {
            case BINDING_REQUEST:
                return new BindingRequest(id, attributes);
            case BINDING_SUCCESS_RESPONSE:
                return new BindingSuccessResponse(id, attributes);
            case BINDING_ERROR_RESPONSE:
                return new BindingErrorResponse(id, attributes);
            case ALLOCATE_REQUEST:
                return new AllocateRequest(id);
            case ALLOCATE_SUCCESS_RESPONSE:
                return new AllocateSuccessResponse(id, attributes);
            case ALLOCATE_ERROR_RESPONSE:
                return new AllocateErrorResponse(id, attributes);
            case DATA_INDICATION:
                return new DataIndication(id, attributes);
            case SEND_INDICATION:
                return new SendIndication(id, attributes);
            case CONNECT_REQUEST:
                return new ConnectRequest(id, attributes);
            case CONNECTION_STATUS_INDICATION:
                return new ConnectionStatusIndication(id, attributes);
            }
        m_log.error("Could not understand message type: "+type);
        return null;
        }

    }

