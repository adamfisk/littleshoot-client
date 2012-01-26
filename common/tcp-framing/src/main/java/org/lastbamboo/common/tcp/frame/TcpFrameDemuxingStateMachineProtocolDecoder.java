package org.lastbamboo.common.tcp.frame;

import org.littleshoot.util.mina.DemuxingStateMachineProtocolDecoder;

/**
 * Decoder for framed TCP messages that uses a state machine and is capable of 
 * being demultiplexed with other protocols on the same port. 
 */
public class TcpFrameDemuxingStateMachineProtocolDecoder extends
    DemuxingStateMachineProtocolDecoder 
    {

    /**
     * Creates a new state machine decoder.
     */
    public TcpFrameDemuxingStateMachineProtocolDecoder()
        {
        super(new TcpFrameDecodingState());
        }

    }
