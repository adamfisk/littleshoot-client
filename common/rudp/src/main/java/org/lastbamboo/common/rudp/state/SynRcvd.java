package org.lastbamboo.common.rudp.state;

import java.util.Random;

import org.lastbamboo.common.rudp.RudpAlreadyOpenException;
import org.lastbamboo.common.rudp.RudpNotOpenException;
import org.lastbamboo.common.rudp.TimerId;
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
import org.littleshoot.util.F1;
import org.littleshoot.util.Pair;
import org.littleshoot.util.UInt;
import org.littleshoot.util.UIntImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SYN-RCVD state.
 */
public final class SynRcvd extends AbstractState implements State
    {
    /**
     * The logger.
     */
    private final Logger m_logger;
    
    /**
     * Constructs a SYN-RCVD state.
     * 
     * @param random
     *      The random number generator that may be used by this state.
     * @param output
     * 		The output interface for this state.
     * @param record
     * 		The connection record of this connection.
     */
    public SynRcvd
            (final Random random,
             final StateOutput output,
             final ConnectionRecord record)
        {
        super (random, output, record);
        
        m_logger = LoggerFactory.getLogger (SynRcvd.class);
        }
    
    /**
     * {@inheritDoc}
     */
    public <T> T accept
            (final StateVisitor<T> visitor)
        {
        return visitor.visitSynRcvd (this);
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
            private ConnectionRecord handleData
                    (final ConnectionRecord record,
                     final UInt seqNum,
                     final byte[] data)
                {
                final int seqNumInt = seqNum.toInt ();
                
                final int lastReceivedSeqNumInt =
                        record.getLastReceivedSeqNum ().toInt ();
                
                if (seqNumInt == lastReceivedSeqNumInt + 1 && data.length > 0)
                    {
                    final ConnectionRecord newRecord =
                            new ConnectionRecordImpl
                                (m_record.getInitialReceiveSeqNum (),
                                 m_record.getInitialSeqNum (),
                                 seqNum,
                                 m_record.getMaxBuffered (),
                                 m_record.getMaxOutstanding (),
                                 m_record.getMaxReceiveSegmentSize (),
                                 m_record.getMaxSendSegmentSize (),
                                 m_record.getNextSeqNum (),
                                 m_record.getOldestUnackedSeqNum (),
                                 m_record.getOutOfSequenceAcks ());
                    
                    final Segment ack =
                            new AckSegmentImpl
                                (newRecord.getNextSeqNum (),
                                 newRecord.getLastReceivedSeqNum (),
                                 new byte[] {});
                    
                    m_output.transmit (ack);
                    
                    return newRecord;
                    }
                else
                    {
                    // TODO: The data was out of order.  Do something useful.
                    return m_record;
                    }
                }
            
            private boolean isSeqNumOk
                    (final UInt seqNum)
                {
                final boolean seqNumAfterInitial =
                        m_record.getInitialReceiveSeqNum ().toLong () <
                            seqNum.toLong ();
                
                final int maxInt =
                        m_record.getLastReceivedSeqNum ().toInt () +
                            (m_record.getMaxBuffered ().toInt () * 2);
                
                final UInt max = new UIntImpl (maxInt);
                
                final boolean underMax = seqNum.toLong () <= max.toLong ();
                
                m_logger.debug ("seqNum: " + seqNum.toLong ());
                m_logger.debug ("max: " + max.toLong ());
                m_logger.debug ("seqNumAfterInitial: " + seqNumAfterInitial);
                m_logger.debug ("underMax: " + underMax);
                
                return seqNumAfterInitial && underMax;
                }
            
            private <T extends Segment> State ifSeqNumOk
                    (final F1<T,State> f,
                     final T segment)
                {
                if (isSeqNumOk (segment.getSeqNum ()))
                    {
                    return f.run (segment);
                    }
                else
                    {
                    final Segment ack =
                            new AckSegmentImpl
                                    (m_record.getNextSeqNum (),
                                     m_record.getLastReceivedSeqNum (),
                                     new byte[] {});
                    
                    m_output.transmit (ack);
                    
                    return SynRcvd.this;
                    }
                }
            
            private void sendRst
                    (final Segment segment)
                {
                final UInt segAck = segment.getAckNum ();
                final UInt seq = new UIntImpl (segAck.toInt () + 1);
                final Segment rst = new RstSegmentImpl (seq, new UIntImpl (0));
                
                m_output.transmit (rst);
                }
            
            @Override
            public State visitAck
                    (final AckSegment ack)
                {
                final F1<AckSegment,State> f = new F1<AckSegment,State> ()
                    {
                    public State run
                            (final AckSegment segment)
                        {
                        if (segment.getAckNum ().equals
                                (m_record.getInitialSeqNum ()))
                            {
                            m_output.acknowledge (segment.getAckNum ());
                            
                            final ConnectionRecord newRecord =
                                    handleData (m_record,
                                                segment.getSeqNum (),
                                                segment.getData ());
                            
                            return new Open (m_random, m_output, newRecord);
                            }
                        else
                            {
                            sendRst (segment);
                            return SynRcvd.this;
                            }
                        }
                    };
                  
                return ifSeqNumOk (f, ack);
                }
        
            @Override
            public State visitEack
                    (final EackSegment eack)
                {
                final F1<Segment,State> f = new F1<Segment,State> ()
                    {
                    public State run
                            (final Segment segment)
                        {
                        sendRst (segment);
                        return SynRcvd.this;
                        }
                    };
                  
                return ifSeqNumOk (f, eack);
                }
        
            @Override
            public State visitNul
                    (final NulSegment nul)
                {
                final UInt seqNum = nul.getSeqNum ();
                final int seqNumInt = seqNum.toInt ();
                
                final int lastReceivedSeqNumInt =
                        m_record.getLastReceivedSeqNum ().toInt ();
                
                if (seqNumInt == lastReceivedSeqNumInt + 1)
                    {
                    final ConnectionRecord newRecord =
                            new ConnectionRecordImpl
                                (m_record.getInitialReceiveSeqNum (),
                                 m_record.getInitialSeqNum (),
                                 seqNum,
                                 m_record.getMaxBuffered (),
                                 m_record.getMaxOutstanding (),
                                 m_record.getMaxReceiveSegmentSize (),
                                 m_record.getMaxSendSegmentSize (),
                                 m_record.getNextSeqNum (),
                                 m_record.getOldestUnackedSeqNum (),
                                 m_record.getOutOfSequenceAcks ());
                    
                    final Segment ack =
                            new AckSegmentImpl
                                (newRecord.getNextSeqNum (),
                                 newRecord.getLastReceivedSeqNum (),
                                 new byte[] {});
                    
                    m_output.transmit (ack);
                    
                    return SynRcvd.this;
                    }
                else
                    {
                    // TODO: The data was out of order.  Do something useful.
                    return SynRcvd.this;
                    }
                }
        
            @Override
            public State visitRst
                    (final RstSegment rst)
                {
                final F1<Segment,State> f = new F1<Segment,State> ()
                    {
                    public State run
                            (final Segment segment)
                        {
                        // TODO: Signal connection refused.
                        sendRst (segment);
                        return new Closed (m_random, m_output, m_record);
                        }
                    };
                  
                return ifSeqNumOk (f, rst);
                }
        
            @Override
            public State visitSyn
                    (final SynSegment syn)
                {
                final F1<Segment,State> f = new F1<Segment,State> ()
                    {
                    public State run
                            (final Segment segment)
                        {
                        // TODO: Signal connection reset.
                        sendRst (segment);
                        return new Closed (m_random, m_output, m_record);
                        }
                    };
                  
                return ifSeqNumOk (f, syn);
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
