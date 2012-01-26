package org.lastbamboo.common.util.mina;

import org.littleshoot.mina.common.ByteBuffer;

/**
 * Protocol decoder with additional methods making it capable of being 
 * demultiplexed between multiple protocols. 
 */
public interface DemuxableDecoderFactory 
    {

    /**
     * Creates a new decoder.
     * 
     * @return A new {@link DemuxableProtocolDecoder}.
     */
    DemuxableProtocolDecoder newDecoder();

    /**
     * Returns whether or not this factory can create decoders that can 
     * understand the specified data.
     * 
     * @param in The data to decode.
     * @return <code>true</code> if this factory can create decoders for the
     * given data, otherwise <code>false</code>.
     */
    boolean canDecode(ByteBuffer in);

    }
