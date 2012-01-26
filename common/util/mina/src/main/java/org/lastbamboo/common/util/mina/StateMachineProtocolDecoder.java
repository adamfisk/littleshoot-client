package org.lastbamboo.common.util.mina;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolDecoder;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top level decoder for state machine decoders.
 */
public class StateMachineProtocolDecoder implements ProtocolDecoder
    {
    private final Logger LOG = 
        LoggerFactory.getLogger(StateMachineProtocolDecoder.class);

    private final DecodingStateMachine m_stateMachine;

    private DecodingState m_currentState;

    /**
     * Creates a new top-level state machine decoder.
     * 
     * @param stateMachine The state machine.
     */
    public StateMachineProtocolDecoder(final DecodingStateMachine stateMachine)
        {
        if (stateMachine == null)
            {
            throw new NullPointerException("stateMachine");
            }
        this.m_stateMachine = stateMachine;
        }

    public void decode(final IoSession session, final ByteBuffer in,
        final ProtocolDecoderOutput out) throws Exception
        {
        DecodingState state = this.m_currentState;
        try
            {
            while (in.hasRemaining())
                {
                if (state == null)
                    {
                    state = m_stateMachine.init();
                    }
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
                    LOG.debug("Got null state...");
                    if (in.hasRemaining())
                        {
                        LOG.debug("State machine ended but hasn't read all " +
                            "data!!");
                        continue;
                        }
                    else
                        {
                        // Wait for more data.
                        break;
                        }
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
    }
