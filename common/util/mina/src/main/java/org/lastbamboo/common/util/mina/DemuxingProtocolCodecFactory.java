package org.lastbamboo.common.util.mina;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.littleshoot.mina.filter.codec.ProtocolDecoder;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.littleshoot.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Codec factory that can demultiplex incoming data between multiple protocols.
 */
public class DemuxingProtocolCodecFactory implements ProtocolCodecFactory
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final List<DemuxableProtocolCodecFactory> m_codecFactories =
        new LinkedList<DemuxableProtocolCodecFactory>();
    
    /**
     * Creates a new {@link DemuxingProtocolCodecFactory} with the specified
     * encoders and decoders.
     * 
     * @param firstCodecFactory The codec factory for one protocol.
     * @param secondCodecFactory The codec factory for another protocol.
     */
    public DemuxingProtocolCodecFactory(
        final DemuxableProtocolCodecFactory firstCodecFactory,
        final DemuxableProtocolCodecFactory secondCodecFactory)
        {
        if (firstCodecFactory == null)
            {
            throw new NullPointerException("Null first factory");
            }
        if (secondCodecFactory == null)
            {
            throw new NullPointerException("Null second factory");
            }
        m_codecFactories.add(firstCodecFactory);
        m_codecFactories.add(secondCodecFactory);
        }

    public ProtocolDecoder getDecoder() 
        {
        m_log.debug("Returning decoder...");
        synchronized (this.m_codecFactories)
            {
            return new DemuxingProtocolDecoder(this.m_codecFactories);
            }
        }

    public ProtocolEncoder getEncoder() 
        {
        m_log.debug("Returning encoder...");
        synchronized (this.m_codecFactories)
            {
            return new DemuxingProtocolEncoder(this.m_codecFactories);
            }
        }
    
    private static final class DemuxingProtocolEncoder 
        implements ProtocolEncoder
        {

        private final Logger m_encoderLogger = 
            LoggerFactory.getLogger(getClass());
        
        private final Collection<DemuxableProtocolCodecFactory> 
            m_encoderFactories = new LinkedList<DemuxableProtocolCodecFactory>();

        private DemuxingProtocolEncoder(
            final List<DemuxableProtocolCodecFactory> factories)
            {
            synchronized (factories)
                {
                this.m_encoderFactories.addAll(factories);
                }
            }

        public void dispose(final IoSession session) throws Exception
            {
            // TODO Auto-generated method stub
            
            }

        public void encode(final IoSession session, final Object message, 
            final ProtocolEncoderOutput out) throws Exception
            {
            this.m_encoderLogger.debug("Encoding message...");
            final Class messageClass = message.getClass();
            synchronized (this.m_encoderFactories)
                {
                for (final DemuxableProtocolCodecFactory factory : this.m_encoderFactories)
                    {
                    final Class factoryClass = factory.getClassToEncode();
                    if (factoryClass.isAssignableFrom(messageClass))
                        {
                        final ProtocolEncoder encoder = factory.newEncoder();
                        encoder.encode(session, message, out);
                        
                        // Got it, so just return.
                        return;
                        }
                    }
                }
            
            m_encoderLogger.warn("Could not encode message: {}", message);
            m_encoderLogger.warn("Factories: {}", this.m_encoderFactories);
            }
        }

    private static final class DemuxingProtocolDecoder 
        implements ProtocolDecoder
        {
        private final Logger m_decoderLog = LoggerFactory.getLogger(getClass());
        private volatile DemuxableProtocolDecoder m_currentDecoder;
        private final List<DemuxableProtocolCodecFactory> m_codecFactories;

        private DemuxingProtocolDecoder(
            final List<DemuxableProtocolCodecFactory> factories)
            {
            this.m_codecFactories = 
                new LinkedList<DemuxableProtocolCodecFactory>();
            this.m_codecFactories.addAll(factories);
            }
        
        public void decode(final IoSession session, final ByteBuffer in, 
            final ProtocolDecoderOutput out) throws Exception
            {
            while (in.hasRemaining())
                {
                if (this.m_currentDecoder == null || 
                    this.m_currentDecoder.atMessageBoundary())
                    {
                    if (enoughData(in))
                        {
                        this.m_currentDecoder = selectDecoder(in);
                        }
                    else
                        {
                        // There's not enough data to determine which decoder 
                        // to use, so wait until we get more.
                        break;
                        }
                    }
                this.m_currentDecoder.decode(session, in, out);
                }
            }

        /**
         * Selects the first decoder that is capable of decoding the message.
         * 
         * @param in The {@link ByteBuffer} to decode.
         * @return The decoder capable of decoding the data.
         */
        private DemuxableProtocolDecoder selectDecoder(final ByteBuffer in)
            {
            int limit = in.limit();
            int pos = in.position();
            try
                {
                for (final DemuxableProtocolCodecFactory decoderFactory : 
                    this.m_codecFactories)
                    {
                    if (decoderFactory.canDecode(in))
                        {
                        m_decoderLog.debug("Returning decoder from factory: {}", 
                            decoderFactory);
                        return decoderFactory.newDecoder();
                        }
                    }
                }
            finally
                {
                in.position(pos);
                in.limit(limit);
                }
            m_decoderLog.warn("Did not understand buffer: {}", 
                MinaUtils.toAsciiString(in));
            throw new IllegalArgumentException("Could not read data: " + 
                MinaUtils.toAsciiString(in));
            }
        
        private boolean enoughData(final ByteBuffer in)
            {
            for (final DemuxableProtocolCodecFactory decoderFactory : 
                this.m_codecFactories)
                {
                if (!decoderFactory.enoughData(in))
                    {
                    return false;
                    }
                }
            return true;
            }

        public void dispose(final IoSession session) throws Exception
            {
            }

        public void finishDecode(final IoSession session, 
            final ProtocolDecoderOutput out) throws Exception
            {
            }
        
        }
    }
