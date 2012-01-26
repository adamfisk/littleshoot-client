package org.lastbamboo.common.rudp.segment;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.littleshoot.mina.filter.codec.ProtocolEncoderOutput;

/**
 * The reliable UDP encoder for MINA.
 */
public final class RudpEncoder implements ProtocolEncoder
    {
        
    /**
     * {@inheritDoc}
     */
    public void dispose
            (final IoSession session) throws Exception
        {
        // Do nothing for now.
        }

    /**
     * {@inheritDoc}
     */
    public void encode
            (final IoSession session,
             final Object object,
             final ProtocolEncoderOutput output) throws Exception
        {
        final Segment segment = (Segment) object;
        final RudpSegmentEncoder encoder = new RudpSegmentEncoder();
        final ByteBuffer buffer = encoder.encode(segment);
        output.write (buffer);
        }
    }
