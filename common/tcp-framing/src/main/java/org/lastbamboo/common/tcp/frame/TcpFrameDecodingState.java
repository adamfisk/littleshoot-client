package org.lastbamboo.common.tcp.frame;

import java.util.List;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.littleshoot.util.mina.DecodingState;
import org.littleshoot.util.mina.DecodingStateMachine;
import org.littleshoot.util.mina.FixedLengthDecodingState;
import org.littleshoot.util.mina.decode.binary.UnsignedShortDecodingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State machine for decoding framed TCP messages following RFC 4571.
 */
public class TcpFrameDecodingState extends DecodingStateMachine 
    {

    private final static Logger m_log = 
        LoggerFactory.getLogger(TcpFrameDecodingState.class);
    
    @Override
    protected DecodingState init() throws Exception
        {
        m_log.debug("Initing...");
        return new ReadMessageLength();
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
    
    private static class ReadMessageLength extends UnsignedShortDecodingState
        {

        @Override
        protected DecodingState finishDecode(final int length, 
            final ProtocolDecoderOutput out) throws Exception
            {
            m_log.debug("Read message length: "+length);
            return new ReadBody(length);
            }
    
        }
    
    private static class ReadBody extends FixedLengthDecodingState
        {

        private ReadBody(final int length)
            {
            super(length);
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
            
            final TcpFrame message = new TcpFrame(readData);
            m_log.debug("Writing TCP Frame message to IoHandler...");
            out.write(message);
            return null;
            }
        }
    }

