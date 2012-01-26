package org.lastbamboo.common.rudp.state;

import java.util.Random;

import org.lastbamboo.common.rudp.RudpAlreadyOpenException;
import org.lastbamboo.common.rudp.RudpNotOpenException;
import org.lastbamboo.common.rudp.TimerId;
import org.lastbamboo.common.rudp.segment.AbstractSegmentVisitor;
import org.lastbamboo.common.rudp.segment.AckSegment;
import org.lastbamboo.common.rudp.segment.AckSegmentImpl;
import org.lastbamboo.common.rudp.segment.NulSegment;
import org.lastbamboo.common.rudp.segment.RstSegmentImpl;
import org.lastbamboo.common.rudp.segment.Segment;
import org.lastbamboo.common.rudp.segment.SegmentVisitor;
import org.lastbamboo.common.rudp.segment.SynSegment;
import org.lastbamboo.common.rudp.segment.SynSegmentImpl;
import org.littleshoot.util.Pair;
import org.littleshoot.util.UInt;
import org.littleshoot.util.UIntImpl;

/**
 * The LISTEN state.
 */
public final class Listen extends AbstractState implements State
    {
    /**
     * Constructs a LISTEN state.
     * 
     * @param random
     *      The random number generator that may be used by this state.
     * @param output
     * 		The output interface for this state.
     * @param record
     * 		The connection record of this connection.
     */
    public Listen
            (final Random random,
             final StateOutput output,
             final ConnectionRecord record)
        {
        super (random, output, record);
        }
    
    /**
     * {@inheritDoc}
     */
    public <T> T accept
            (final StateVisitor<T> visitor)
        {
        return visitor.visitListen (this);
        }
    
    /**
     * {@inheritDoc}
     */
    public State close
            ()
        {
        return new Closed (m_random, m_output, m_record);
        }
    
    /**
     * {@inheritDoc}
     */
    public void entered
            ()
        {
        }
    
    /**
     * {@inheritDoc}
     */
    public void exited
            ()
        {
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
            private void sendRst
                    (final UInt segAck)
                {
                final UInt seq = new UIntImpl (segAck.toInt () + 1);
                final Segment rst = new RstSegmentImpl (seq, new UIntImpl (0));
                
                m_output.transmit (rst);
                }
            
            private ConnectionRecord getNewRecord
                    (final SynSegment syn,
                     final UInt oldestUnackedSeqNum)
                {
                return new ConnectionRecordImpl
                        (syn.getSeqNum (),
                         m_record.getInitialSeqNum (),
                         syn.getSeqNum (),
                         m_record.getMaxBuffered (),
                         syn.getMaxOutstanding (),
                         syn.getMaxSegmentSize (),
                         m_record.getMaxReceiveSegmentSize (),
                         m_record.getNextSeqNum (),
                         oldestUnackedSeqNum,
                         m_record.getOutOfSequenceAcks ());
                }
            
            public State visitAck
                    (final AckSegment ack)
                {
                sendRst (ack.getAckNum ());
                return Listen.this;
                }

            public State visitNul
                    (final NulSegment nul)
                {
                sendRst (nul.getAckNum ());
                return Listen.this;
                }
            
            public State visitSyn
                    (final SynSegment syn)
                {
                if (syn.isAck ())
                    {
                    final Segment ack =
                            new AckSegmentImpl (m_record.getNextSeqNum (),
                                                syn.getSeqNum (),
                                                new byte[] {});
                    
                    m_output.transmit (ack);
                    
                    return new Open (m_random,
                                     m_output,
                                     getNewRecord (syn, syn.getAckNum ()));
                    }
                else
                    {
                    final Segment outSyn =
                            new SynSegmentImpl
                                (m_record.getInitialSeqNum (),
                                 syn.getSeqNum (),
                                 true,
                                 m_record.getMaxBuffered (),
                                 m_record.getMaxReceiveSegmentSize ());
                    
                    m_output.transmit (outSyn);
                                                        
                    final ConnectionRecord newRecord =
                            getNewRecord (syn,
                                          m_record.getOldestUnackedSeqNum ());
                    
                    return new SynRcvd (m_random, m_output, newRecord);
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
