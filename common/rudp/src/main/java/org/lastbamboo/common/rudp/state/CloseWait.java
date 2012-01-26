package org.lastbamboo.common.rudp.state;

import java.util.Random;

import org.lastbamboo.common.rudp.RudpAlreadyOpenException;
import org.lastbamboo.common.rudp.RudpNotOpenException;
import org.lastbamboo.common.rudp.TimerId;
import org.lastbamboo.common.rudp.TimerIdImpl;
import org.lastbamboo.common.rudp.segment.AbstractSegmentVisitor;
import org.lastbamboo.common.rudp.segment.RstSegment;
import org.lastbamboo.common.rudp.segment.Segment;
import org.lastbamboo.common.rudp.segment.SegmentVisitor;
import org.littleshoot.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The CLOSE-WAIT state.
 */
public final class CloseWait extends AbstractState implements State
    {
    /**
     * The logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger (getClass ());
    
    /**
     * Our timer identifier for the timer that determines when we go into the
     * CLOSED state.
     */
    private final TimerId m_closeTimerId;
    
    /**
     * Constructs a CLOSE-WAIT state.
     * 
     * @param random The random number generator that may be used by this state.
     * @param output The output interface for this state.
     * @param record The connection record of this connection.
     */
    public CloseWait (final Random random, final StateOutput output,
        final ConnectionRecord record)
        {
        super (random, output, record);
        m_closeTimerId = new TimerIdImpl ();
        }
    
    /**
     * {@inheritDoc}
     */
    public <T> T accept (final StateVisitor<T> visitor)
        {
        return visitor.visitCloseWait (this);
        }
    
    /**
     * {@inheritDoc}
     */
    public State close ()
        {
        m_log.debug ("Close called while in CLOSE-WAIT");
        return this;
        }
    
    /**
     * {@inheritDoc}
     */
    public void entered ()
        {
        m_log.debug ("Setting close timer");
        
        m_output.stopRetransmissions ();
        m_output.setTimer (m_closeTimerId, 8000);
        }
    
    /**
     * {@inheritDoc}
     */
    public void exited ()
        {
        }

    /**
     * {@inheritDoc}
     */
    public State getNext (final Segment segment)
        {
        final SegmentVisitor<State> visitor =
                new AbstractSegmentVisitor<State> (this)
            {
            @Override
            public State visitRst (final RstSegment rst)
                {
                // TODO: Cancel TIMWAIT timer.
                // Signal closed connection record.
                return new Closed (m_random, m_output, m_record);
                }
            };
        
        return segment.accept (visitor);
        }

    /**
     * {@inheritDoc}
     */
    public State open ()
        {
        throw new RudpAlreadyOpenException ();
        }

    /**
     * {@inheritDoc}
     */
    public Pair<SendStatus,State> send (final byte[] data)
        {
        throw new RudpNotOpenException ();
        }
    
    /**
     * {@inheritDoc}
     */
    public State send (final long tryTime, final byte[] data)
        {
        throw new RudpNotOpenException ();
        }
    
    /**
     * {@inheritDoc}
     */
    public State timerExpired (final TimerId timerId)
        {
        if (timerId.equals (m_closeTimerId))
            {
            m_log.debug ("Close timer expired");
            
            return new Closed (m_random, m_output, m_record);
            }
        else
            {
            return this;
            }
        }
    }
