package org.lastbamboo.common.util.mina;

import org.littleshoot.mina.filter.codec.ProtocolEncoder;


public interface DemuxableEncoderFactory
    {

    /**
     * Creates a new encoder.
     * 
     * @return A new {@link ProtocolEncoder}.
     */
    ProtocolEncoder newEncoder();

    /**
     * Gets the class this factory is designed to encode.
     * 
     * @return The {@link Class} this factory is designed to encode.
     */
    Class getClassToEncode();

    }
