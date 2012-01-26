package org.lastbamboo.common.util.mina.decode.binary;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.littleshoot.util.mina.DecodingState;

/**
 * Decoding state for reading a single unsigned short.
 */
public abstract class UnsignedShortDecodingState implements DecodingState
    {

    public DecodingState decode(final ByteBuffer in, 
        final ProtocolDecoderOutput out) throws Exception
        {
        if (in.remaining() > 1)
            {
            final int decoded = in.getUnsignedShort();
            return finishDecode(decoded, out);
            }
        else
            {
            return this;
            }
        }

    /**
     * Called on the subclass when the unsigned short has been successfully 
     * decoded.
     * 
     * @param decoded The decoded unsigned short.
     * @param out The decoder output.
     * @return The next state.
     * @throws Exception If any unexpected error occurs.
     */
    protected abstract DecodingState finishDecode(final int decoded, 
        final ProtocolDecoderOutput out) throws Exception;
    }
