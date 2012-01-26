package org.lastbamboo.common.util.mina;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link StateMachineProtocolDecoder} that can be used with a protocol
 * that should be dumultiplexed with other protocols.
 */
public class DemuxingStateMachineProtocolDecoder 
    implements DemuxableProtocolDecoder
    {
    private final Logger LOG = 
        LoggerFactory.getLogger(StateMachineProtocolDecoder.class);
    
    private final DecodingStateMachine m_stateMachine;

    private DecodingState m_currentState;

    /**
     * Creates a new {@link DemuxingStateMachineProtocolDecoder}.
     * 
     * @param stateMachine The state machine that will transition between
     * states.
     */
    public DemuxingStateMachineProtocolDecoder(
        final DecodingStateMachine stateMachine)
        {
        if (stateMachine == null)
            {
            throw new NullPointerException("null state machine");
            }
        this.m_stateMachine = stateMachine;
        }

    public void decode(final IoSession session, final ByteBuffer in,
        final ProtocolDecoderOutput out) throws Exception
        {
        //LOG.debug("Decoding: ", MinaUtils.toAsciiString(in));
        DecodingState state = this.m_currentState;
        if (state == null)
            {
            state = m_stateMachine.init();
            }
        try
            {
            for (;;)
                {
                int remaining = in.remaining();

                // Wait for more data if all data is consumed.
                if (remaining == 0)
                    {
                    LOG.debug("Breaking -- no remaining bytes...");
                    break;
                    }
                
                DecodingState oldState = state;
                
                if (LOG.isDebugEnabled())
                    {
                    LOG.debug("Calling decode on state: {}", 
                        state.getClass().getSimpleName());
                    }
                state = state.decode(in, out);

                if (state == null)
                    {
                    LOG.debug("Got null state...breaking...");
                    break;
                    }

                // Wait for more data if nothing is consumed and state didn't
                // change.
                if (in.remaining() == remaining && oldState == state)
                    {
                    LOG.debug("Nothing consumed...breaking");
                    break;
                    }
                }
            }
        catch (final Exception e)
            {
            state = null;
            throw e;
            }
        finally
            {
            this.m_currentState = state;
            }
        }

    public void dispose(final IoSession session) throws Exception
        {
        }

    public void finishDecode(final IoSession session, 
        final ProtocolDecoderOutput out) throws Exception
        {
        LOG.debug("Finish decode called in top-level state machine " +
            "protocol decoder");
        }
    
    public boolean atMessageBoundary()
        {
        // The underlying state machine MUST always return null when at a 
        // message boundary for this to work.
        return this.m_currentState == null;
        }

    }
