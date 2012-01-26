package org.lastbamboo.common.util.mina.decode.binary;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.littleshoot.util.mina.DecodingState;

/**
 * Decoding state for reading a single unsigned int.
 */
public abstract class UnsignedIntDecodingState implements DecodingState
    {

    public DecodingState decode(final ByteBuffer in, 
        final ProtocolDecoderOutput out) throws Exception
        {
        if (in.remaining() > 3)
            {
            final long decoded = in.getUnsignedInt();
            return finishDecode(decoded, out);
            }
        else
            {
            return this;
            }
        }

    /**
     * Called on the subclass when the unsigned int has been successfully 
     * decoded.
     * 
     * @param decodedShort The decoded unsigned int.
     * @param out The decoder output.
     * @return The next state.
     * @throws Exception If any unexpected error occurs.
     */
    protected abstract DecodingState finishDecode(final long decodedShort, 
        final ProtocolDecoderOutput out) throws Exception;
    }
