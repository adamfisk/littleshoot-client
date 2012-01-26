package org.lastbamboo.common.rudp.segment;

import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.littleshoot.mina.filter.codec.ProtocolDecoder;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;

/**
 * A codec factory with one decoder and one encoder.
 */
public final class BasicCodecFactory implements ProtocolCodecFactory
    {
    /**
     * The decoder.
     */
    private final ProtocolDecoder m_decoder;
    
    /**
     * The encoder.
     */
    private final ProtocolEncoder m_encoder;
    
    /**
     * Constructs a new codec factory.
     * 
     * @param decoder
     *      The decoder to use.
     * @param encoder
     *      The encoder to use.
     */
    public BasicCodecFactory
            (final ProtocolDecoder decoder,
             final ProtocolEncoder encoder)
        {
        m_decoder = decoder;
        m_encoder = encoder;
        }
    
    /**
     * {@inheritDoc}
     */
    public ProtocolDecoder getDecoder
            () throws Exception
        {
        return m_decoder;
        }

    /**
     * {@inheritDoc}
     */
    public ProtocolEncoder getEncoder
            () throws Exception
        {
        return m_encoder;
        }
    }
