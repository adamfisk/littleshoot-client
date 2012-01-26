package org.lastbamboo.common.stun.stack.encoder;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolEncoderOutput;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.littleshoot.util.mina.DemuxableProtocolEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes bytes into STUN messages.
 */
public class StunProtocolEncoder implements DemuxableProtocolEncoder {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public void dispose(final IoSession session) throws Exception {

    }

    public void encode(final IoSession session, final Object message,
            final ProtocolEncoderOutput out) throws Exception {
        LOG.debug("{} encoding message: {}", this, message);
        final StunMessageEncoder encoder = new StunMessageEncoder();

        final StunMessage stunMessage = (StunMessage) message;
        final ByteBuffer buf = encoder.encode(stunMessage);
        out.write(buf);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
