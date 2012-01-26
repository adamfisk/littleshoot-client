package org.lastbamboo.common.util.mina;

import org.littleshoot.mina.filter.codec.ProtocolDecoder;

/**
 * Protocol decoder with additional methods making it capable of being 
 * demultiplexed between multiple protocols. 
 */
public interface DemuxableProtocolDecoder extends ProtocolDecoder
    {

    boolean atMessageBoundary();
    
    }
