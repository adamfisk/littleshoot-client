package org.lastbamboo.common.rudp.state;

import java.util.Random;

import org.lastbamboo.common.rudp.RudpAlreadyOpenException;
import org.lastbamboo.common.rudp.RudpNotOpenException;
import org.lastbamboo.common.rudp.TimerId;
import org.lastbamboo.common.rudp.TimerIdImpl;
import org.lastbamboo.common.rudp.segment.AbstractSegmentVisitor;
import org.lastbamboo.common.rudp.segment.AckSegment;
import org.lastbamboo.common.rudp.segment.AckSegmentImpl;
import org.lastbamboo.common.rudp.segment.EackSegment;
import org.lastbamboo.common.rudp.segment.NulSegment;
import org.lastbamboo.common.rudp.segment.RstSegment;
import org.lastbamboo.common.rudp.segment.RstSegmentImpl;
import org.lastbamboo.common.rudp.segment.Segment;
import org.lastbamboo.common.rudp.segment.SegmentVisitor;
import org.lastbamboo.common.rudp.segment.SynSegment;
import org.lastbamboo.common.rudp.segment.SynSegmentImpl;
import org.littleshoot.util.Pair;
import org.littleshoot.util.UInt;
import org.littleshoot.util.UIntImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SYN-SENT state.
 */
public final class SynSent extends AbstractState implements State
    {
    /**
     * The logger.
     */
    private final Logger m_logger;
    
    /**
     * The identifier of the timer used to time out if we spend too much time in
     * this state.
     */
    private final TimerId m_timerId;
    
    /**
     * Constructs a SYN-SENT state.
     * 
     * @param random
     *      The random number generator that may be used by this state.
     * @param output
     * 		The output interface for this state.
     * @param record
     * 		The connection record of this connection.
     */
    public SynSent
            (final Random random,
             final StateOutput output,
             final ConnectionRecord record)
        {
        super (random, output, record);
        
        m_logger = LoggerFactory.getLogger (SynSent.class);
        m_timerId = new TimerIdImpl ();
        }
    
    /**
     * {@inheritDoc}
     */
    public <T> T accept
            (final StateVisitor<T> visitor)
        {
        return visitor.visitSynSent (this);
        }
    
    /**
     * {@inheritDoc}
     */
    public State close
            ()
        {
        final Segment rst =
                new RstSegmentImpl (m_record.getNextSeqNum (),
                                    m_record.getLastReceivedSeqNum ());
        
        m_output.transmit (rst);
        
        return new Closed (m_random, m_output, m_record);
        }
    
    /**
     * {@inheritDoc}
     */
    public void entered
            ()
        {
        m_logger.debug ("Setting SYN-SENT timer");
        m_output.setTimer (m_timerId, 20000);
        }
    
    /**
     * {@inheritDoc}
     */
    public void exited
            ()
        {
        m_logger.debug ("Canceling SYN-SENT timer");
        m_output.cancelTimer (m_timerId);
        }
    
    /**
     * {@inheritDoc}
     */
    public State getNext
            (final Segment segment)
        {
        final SegmentVisitor<State> visitor =
                new AbstractSegmentVisitor<State> (this)
            {
            private void handleAck
                    (final Segment segment)
                {
                final UInt ackNum = segment.getAckNum ();
                
                if (m_record.getInitialSeqNum ().equals (ackNum))
                    {
                    // This is okay.
                    m_output.acknowledge (ackNum);
                    }
                else
                    {
                    // The sequence number is wrong.  Reset the connection.
                    final UInt seqNum = new UIntImpl (ackNum.toInt () + 1);
                    
                    m_logger.debug ("Bad sequence number");
                    
                    final Segment rst =
                            new RstSegmentImpl (seqNum, new UIntImpl (0));
                    
                    m_output.transmit (rst);
                    
                    // TODO: Close this half-open connection.
                    }
                }
            
            @Override
            public State visitAck
                    (final AckSegment ack)
                {
                handleAck (ack);
                return super.visitAck (ack);
                }
        
            @Override
            public State visitEack
                    (final EackSegment eack)
                {
                handleAck (eack);
                return super.visitEack (eack);
                }
        
            @Override
            public State visitNul
                    (final NulSegment nul)
                {
                return super.visitNul (nul);
                }
        
            @Override
            public State visitRst
                    (final RstSegment rst)
                {
                if (rst.isAck ())
                    {
                    // TODO: Signal connection refused.
                    return new Closed (m_random, m_output, m_record);
                    }
                else
                    {
                    return super.visitRst (rst);
                    }
                }
        
            @Override
            public State visitSyn
                    (final SynSegment syn)
                {
                if (syn.isAck ())
                    {
                    m_output.acknowledge (syn.getAckNum ());
                    
                    final ConnectionRecord newRecord =
                            new ConnectionRecordImpl
                                (syn.getSeqNum (),
                                 m_record.getInitialSeqNum (),
                                 syn.getSeqNum (),
                                 m_record.getMaxBuffered (),
                                 syn.getMaxOutstanding (),
                                 syn.getMaxSegmentSize (),
                                 m_record.getMaxSendSegmentSize (),
                                 m_record.getNextSeqNum (),
                                 new UIntImpl (syn.getAckNum ().toInt () + 1),
                                 m_record.getOutOfSequenceAcks ());
                    
                    final Segment ack =
                            new AckSegmentImpl
                                (newRecord.getNextSeqNum (),
                                 newRecord.getLastReceivedSeqNum (),
                                 new byte[] {});
                    
                    m_output.transmit (ack);
                    
                    return new Open (m_random, m_output, newRecord);
                    }
                else
                    {
                    final ConnectionRecord newRecord =
                            new ConnectionRecordImpl
                                (syn.getSeqNum (),
                                 m_record.getInitialSeqNum (),
                                 syn.getSeqNum (),
                                 m_record.getMaxBuffered (),
                                 syn.getMaxOutstanding (),
                                 syn.getMaxSegmentSize (),
                                 m_record.getMaxSendSegmentSize (),
                                 m_record.getNextSeqNum (),
                                 m_record.getOldestUnackedSeqNum (),
                                 m_record.getOutOfSequenceAcks ());
                    
                    final Segment synAck =
                            new SynSegmentImpl
                                (newRecord.getInitialSeqNum (),
                                 newRecord.getLastReceivedSeqNum (),
                                 true,
                                 newRecord.getMaxReceiveSegmentSize (),
                                 newRecord.getMaxBuffered ());
                    
                    m_output.transmit (synAck);
                    
                    return super.visitSyn (syn);
                    }
                }
            };
        
        return segment.accept (visitor);
        }

    /**
     * {@inheritDoc}
     */
    public State open
            ()
        {
        throw new RudpAlreadyOpenException ();
        }
    
    /**
     * {@inheritDoc}
     */
    public Pair<SendStatus,State> send
            (final byte[] data)
        {
        throw new RudpNotOpenException ();
        }
    
    /**
     * {@inheritDoc}
     */
    public State send
            (final long tryTime,
             final byte[] data)
        {
        throw new RudpNotOpenException ();
        }
    
    /**
     * {@inheritDoc}
     */
    public State timerExpired
            (final TimerId timerId)
        {
        return this;
        }
    }
