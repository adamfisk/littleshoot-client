package org.lastbamboo.common.stun.stack.decoder;

import org.littleshoot.util.mina.DemuxingStateMachineProtocolDecoder;

/**
 * Decoder for STUN messages that uses a state machine and is capable of being
 * demultiplexed with other protocols on the same port. 
 */
public class StunDemuxingStateMachineProtocolDecoder extends
    DemuxingStateMachineProtocolDecoder 
    {

    /**
     * Creates a new state machine decoder.
     */
    public StunDemuxingStateMachineProtocolDecoder()
        {
        super(new StunMessageDecodingState());
        }

    }
