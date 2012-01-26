package org.lastbamboo.common.rudp;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.lastbamboo.common.rudp.segment.RudpDecoder;
import org.lastbamboo.common.rudp.segment.RudpEncoder;
import org.lastbamboo.common.rudp.segment.Segment;
import org.littleshoot.util.mina.DemuxableProtocolCodecFactory;
import org.littleshoot.util.mina.DemuxableProtocolDecoder;

/**
 * {@link DemuxableProtocolCodecFactory} for RUDP.
 */
public class RudpDemuxableProtocolCodecFactory
    implements DemuxableProtocolCodecFactory<Segment>
    {
    /**
     * {@inheritDoc}
     */
    public boolean canDecode(ByteBuffer in)
        {
        // We could refine this in the future.  For now, though, it's just
        // demultiplexed with STUN traffic, and STUN is designed for easy
        // demultiplexing.  This allows us to the just check if messages are 
        // STUN.
        return true;
        }
    
    public boolean enoughData(final ByteBuffer in)
        {
        // We don't use RUDP messages to differentiate protocols, so we always
        // have "enough" data.
        return true;
        }

    /**
     * {@inheritDoc}
     */
    public Class<Segment> getClassToEncode()
        {
        return Segment.class;
        }

    /**
     * {@inheritDoc}
     */
    public DemuxableProtocolDecoder newDecoder()
        {
        return new RudpDecoder();
        }

    /**
     * {@inheritDoc}
     */
    public ProtocolEncoder newEncoder()
        {
        return new RudpEncoder();
        }
    }
