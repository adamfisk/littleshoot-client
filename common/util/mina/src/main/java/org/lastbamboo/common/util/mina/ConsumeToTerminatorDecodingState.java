package org.lastbamboo.common.util.mina;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Consumes until a fixed (ASCII) character is reached.  This also allows
 * several characters to act as terminators.  In this case, the first 
 * terminator that is read ends the reading, i.e. it doesn't look for 
 * multiple terminators strung together.
 * 
 * The terminator is skipped.
 */
public abstract class ConsumeToTerminatorDecodingState implements DecodingState 
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private ByteBuffer m_buffer;

    private final byte m_terminator1;

    private final byte m_terminator2;

    private byte m_foundTerminator;

    /**
     * Creates a new instance.
     * 
     * @param terminator The terminator.
     */
    protected ConsumeToTerminatorDecodingState(final byte terminator)
        {
        m_terminator1 = terminator;
        m_terminator2 = -1;
        }
    
    /**
     * Creates a new instance.
     * 
     * @param terminator1The first terminator.
     * @param terminator2 The second terminator.
     */
    protected ConsumeToTerminatorDecodingState(final byte terminator1,
        final byte terminator2)
        {
        m_terminator1 = terminator1;
        m_terminator2 = terminator2;
        }

    public DecodingState decode(final ByteBuffer in, 
        final ProtocolDecoderOutput out) throws Exception
        {
        int beginPos = in.position();
        int terminatorPos = -1;
        int limit = in.limit();

        for (int i = beginPos; i < limit; i++)
            {
            final byte b = in.get(i);
            if (b == this.m_terminator1 || b == this.m_terminator2)
                {
                this.m_foundTerminator = b;
                terminatorPos = i;
                break;
                }
            }

        if (terminatorPos >= 0)
            {
            final ByteBuffer product;

            if (beginPos < terminatorPos)
                {
                in.limit(terminatorPos);

                if (m_buffer == null)
                    {
                    product = in.slice();
                    }
                else
                    {
                    m_buffer.put(in);
                    product = m_buffer.flip();
                    m_buffer = null;
                    }

                in.limit(limit);
                }
            else
                {
                // When input contained only terminator rather than actual
                // data...
                if (m_buffer == null)
                    {
                    product = ByteBuffer.allocate(1);
                    product.limit(0);
                    }
                else
                    {
                    product = m_buffer.flip();
                    m_buffer = null;
                    }
                }
            
            in.position(terminatorPos + 1);
            //m_log.debug("Read: {}", MinaUtils.toAsciiString(product.duplicate()));
            return finishDecode(this.m_foundTerminator, product, out);
            }
        else
            {
            if (m_buffer == null)
                {
                m_buffer = ByteBuffer.allocate(in.remaining());
                m_buffer.setAutoExpand(true);
                }
            m_buffer.put(in);
            return this;
            }
        }

    protected abstract DecodingState finishDecode(byte foundTerminator,
        ByteBuffer product, ProtocolDecoderOutput out) throws Exception;
    }
