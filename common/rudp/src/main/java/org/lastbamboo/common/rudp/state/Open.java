package org.lastbamboo.common.rudp.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;
import org.lastbamboo.common.rudp.RudpAlreadyOpenException;
import org.lastbamboo.common.rudp.TimerId;
import org.lastbamboo.common.rudp.segment.AbstractSegmentVisitor;
import org.lastbamboo.common.rudp.segment.AckSegment;
import org.lastbamboo.common.rudp.segment.AckSegmentImpl;
import org.lastbamboo.common.rudp.segment.EackSegment;
import org.lastbamboo.common.rudp.segment.EackSegmentImpl;
import org.lastbamboo.common.rudp.segment.NulSegment;
import org.lastbamboo.common.rudp.segment.RstSegment;
import org.lastbamboo.common.rudp.segment.RstSegmentImpl;
import org.lastbamboo.common.rudp.segment.Segment;
import org.lastbamboo.common.rudp.segment.SegmentVisitor;
import org.lastbamboo.common.rudp.segment.SynSegment;
import org.littleshoot.util.F1;
import org.littleshoot.util.Pair;
import org.littleshoot.util.PairImpl;
import org.littleshoot.util.UInt;
import org.littleshoot.util.UIntImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The OPEN state.
 */
public final class Open extends AbstractState implements State
    {
    /**
     * The logger.
     */
    private final Logger m_logger;
    
    /**
     * Constructs an OPEN state.
     * 
     * @param random The random number generator that may be used by this state.
     * @param output The output interface for this state.
     * @param record The connection record of this connection.
     */
    public Open (final Random random, final StateOutput output,
        final ConnectionRecord record)
        {
        super (random, output, record);
        m_logger = LoggerFactory.getLogger (Open.class);
        }
    
    /**
     * {@inheritDoc}
     */
    public <T> T accept (final StateVisitor<T> visitor)
        {
        return visitor.visitOpen (this);
        }
    
    /**
     * {@inheritDoc}
     */
    public State close ()
        {
        m_logger.debug("Closing!!");
        final Segment rst =
            new RstSegmentImpl (m_record.getNextSeqNum (),
                                m_record.getLastReceivedSeqNum ());
        
        m_output.transmit (rst);
        
        return new CloseWait (m_random, m_output, m_record);
        }
    
    /**
     * {@inheritDoc}
     */
    public void entered ()
        {
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
            private ConnectionRecord getNewRecord
                    (final UInt seqNum)
                {
                return new ConnectionRecordImpl
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
                }
            
            private boolean isSeqNumOk (final UInt seqNum)
                {
                final boolean seqNumAfterCurrent =
                        m_record.getLastReceivedSeqNum ().toLong () <
                            seqNum.toLong ();
                
                final int maxInt =
                        m_record.getLastReceivedSeqNum ().toInt () +
                            (m_record.getMaxBuffered ().toInt () * 2);
                
                final UInt max = new UIntImpl (maxInt);
                
                final boolean underMax = seqNum.toLong () <= max.toLong ();
                
                /*
                synchronized (m_logger)
                    {
                    m_logger.debug
                            ("Last Received: " +
                                 m_record.getLastReceivedSeqNum ().toLong ());
                    
                    m_logger.debug ("Sequence: " + seqNum.toLong ());
                
                    m_logger.debug ("max: " + max.toLong ());
                    m_logger.debug ("seqNumAfterCurrent: " +
                                        seqNumAfterCurrent);
                    
                    m_logger.debug ("underMax: " + underMax);
                    }
                    */
                
                return seqNumAfterCurrent && underMax;
                }
            
            private ConnectionRecord handleAck (final UInt ackNum)
                {
                /*
                m_logger.debug ("Handling ACK number {}", ackNum.toLong ());
                m_logger.debug ("next: {}",
                                m_record.getNextSeqNum ().toLong ());
                */
                
                if (m_record.getOldestUnackedSeqNum ().toLong () <=
                            ackNum.toLong () &&
                        ackNum.toLong () < m_record.getNextSeqNum ().toLong ())
                    {
                    // We construct a new connection record that records the
                    // new last acknowledged segment.
                    final ConnectionRecord newRecord =
                            new ConnectionRecordImpl
                                (m_record.getInitialReceiveSeqNum (),
                                 m_record.getInitialSeqNum (),
                                 m_record.getLastReceivedSeqNum (),
                                 m_record.getMaxBuffered (),
                                 m_record.getMaxOutstanding (),
                                 m_record.getMaxReceiveSegmentSize (),
                                 m_record.getMaxSendSegmentSize (),
                                 m_record.getNextSeqNum (),
                                 new UIntImpl (ackNum.toInt () + 1),
                                 m_record.getOutOfSequenceAcks ());
                    
                    // TODO: Flush acknowledged segments.
                    return newRecord;
                    }
                else
                    {
                    return m_record;
                    }
                }
            
            private ConnectionRecord handleData (final ConnectionRecord record,
                final UInt seqNum, final byte[] data)
                {
                if (data.length > 0)
                    {
                    final int seqNumInt = seqNum.toInt ();
                    
                    final int lastReceivedSeqNumInt =
                            record.getLastReceivedSeqNum ().toInt ();
                    
                    if (seqNumInt == lastReceivedSeqNumInt + 1)
                        {
                        //m_logger.debug ("next: " +
                          //                  record.getNextSeqNum ().toLong ());
                        
                        m_logger.debug ("About to queue in-sequence");
                        m_logger.debug ("data.length: " + data.length);
                        m_output.queue (data);
                        
                        // Queue all of the data that may have been held up by
                        // this segment.  If data arrived out of order, we could
                        // not deliver the out-of-order data until the proper
                        // in-order data arrived.
                        final Pair<UInt,Collection<OutOfOrderData>>
                                pair = queueBlocked
                                        (record.getOutOfSequenceAcks (),
                                         seqNumInt);
                        
                        final ConnectionRecord newRecord =
                                new ConnectionRecordImpl
                                    (record.getInitialReceiveSeqNum (),
                                     record.getInitialSeqNum (),
                                     pair.getFirst (),
                                     record.getMaxBuffered (),
                                     record.getMaxOutstanding (),
                                     record.getMaxReceiveSegmentSize (),
                                     record.getMaxSendSegmentSize (),
                                     record.getNextSeqNum (),
                                     record.getOldestUnackedSeqNum (),
                                     pair.getSecond ());
                        
                        final Segment ack =
                                new AckSegmentImpl
                                    (newRecord.getNextSeqNum (),
                                     newRecord.getLastReceivedSeqNum (),
                                     ArrayUtils.EMPTY_BYTE_ARRAY);
                        
                        m_output.transmit (ack);
                        
                        return newRecord;
                        }
                    else
                        {
                        // TODO: The data was out of order.  Do something
                        // useful.
                        
                        m_logger.debug ("Out of order data received");
                        m_logger.debug ("data.length: " + data.length);
                        
                        // We save the data that was out of order so that when
                        // the missing data arrives to fill the gap, we can
                        // deliver all of the data to the user.
                        final OutOfOrderData oooData =
                                new OutOfOrderDataImpl (seqNum, data);
                        
                        final Collection<OutOfOrderData> newOutOfSequenceAcks =
                                new LinkedList<OutOfOrderData>
                                    (m_record.getOutOfSequenceAcks ());
                        
                        newOutOfSequenceAcks.add (oooData);
                        
                        final ConnectionRecord newRecord =
                                new ConnectionRecordImpl
                                    (record.getInitialReceiveSeqNum (),
                                     record.getInitialSeqNum (),
                                     record.getLastReceivedSeqNum (),
                                     record.getMaxBuffered (),
                                     record.getMaxOutstanding (),
                                     record.getMaxReceiveSegmentSize (),
                                     record.getMaxSendSegmentSize (),
                                     record.getNextSeqNum (),
                                     record.getOldestUnackedSeqNum (),
                                     newOutOfSequenceAcks);
                        
                        final Collection<UInt> seqNums =
                                new ArrayList<UInt>
                                    (newOutOfSequenceAcks.size ());
                        
                        for (final OutOfOrderData oneOooData :
                                newOutOfSequenceAcks)
                            {
                            seqNums.add (oneOooData.getSeqNum ());
                            }
                        
                        final Segment eack =
                                new EackSegmentImpl
                                    (newRecord.getNextSeqNum (),
                                     newRecord.getLastReceivedSeqNum (),
                                     seqNums,
                                     ArrayUtils.EMPTY_BYTE_ARRAY);
                        
                        m_output.transmit (eack);
                        
                        return newRecord;
                        }
                    }
                else
                    {
                    m_logger.debug("No data to handle.");
                    return record;
                    }
                }
            
            private Pair<UInt,Collection<OutOfOrderData>> queueBlocked
                    (final Collection<OutOfOrderData> outOfSequenceAcks,
                     final int seqNumInt)
                {
                m_logger.debug ("size: " + outOfSequenceAcks.size ());
                
                // We convert the collection of out of order data into a map
                // keyed by sequence number so that we may quickly find the
                // messages that were blocked by the given sequence number.
                final Map<UInt,OutOfOrderData> newAcks =
                        new HashMap<UInt,OutOfOrderData> ();
                
                for (final OutOfOrderData data : outOfSequenceAcks)
                    {
                    m_logger.debug ("key: " + data.getSeqNum ().toLong ());
                    newAcks.put (data.getSeqNum (), data);
                    }
                
                // Walk up from the current sequence number until we reach a gap
                // in the out of order data.  For example, suppose we have
                // gotten sequence numbers [5, 6, 7, 9] out of order.  When we
                // get the acknowledgment for sequence number for, we will walk
                // up [5, 6, 7] and find that we have no message for sequence
                // number 8.
                UInt front = new UIntImpl (seqNumInt + 1);
                
                while (newAcks.containsKey (front))
                    {
                    m_output.queue (newAcks.remove (front).getData ());
                    
                    front = new UIntImpl (front.toInt () + 1);
                    }
                
                return new PairImpl<UInt,Collection<OutOfOrderData>>
                        (new UIntImpl (front.toInt () - 1),
                         newAcks.values ());
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
                    m_logger.debug ("Bad sequence number");
                    
                    final Segment ack =
                            new AckSegmentImpl
                                (m_record.getNextSeqNum (),
                                 m_record.getLastReceivedSeqNum (),
                                 new byte[] {});
                    
                    m_output.transmit (ack);
                    
                    return Open.this;
                    }
                }
            
            @Override
            public State visitAck (final AckSegment ack)
                {
                final F1<AckSegment,State> f = new F1<AckSegment,State> ()
                    {
                    public State run (final AckSegment ack)
                        {
                        // We create a new connection record that records the
                        // acknowledgment.
                        final ConnectionRecord newRecord =
                            handleAck (ack.getAckNum ());
                        
                        // We notify the outside world of the acknowledgment.
                        // This prevents retransmission of the acknowledged
                        // segment.
                        m_output.acknowledge (ack.getAckNum ());
                        
                        final ConnectionRecord dataRecord = 
                            handleData (newRecord, ack.getSeqNum (),
                                ack.getData ());
                        return new Open (m_random, m_output, dataRecord);
                        }
                    };
                    
                return ifSeqNumOk (f, ack);
                }
            
            @Override
            public State visitEack (final EackSegment eack)
                {
                final F1<EackSegment,State> f = new F1<EackSegment,State> ()
                    {
                    public State run (final EackSegment eack)
                        {
                        final ConnectionRecord newRecord =
                            handleAck (eack.getAckNum ());
                        
                        for (final UInt seqNum : eack.getReceivedSeqNums ())
                            {
                            m_output.acknowledgeOne (seqNum);
                            }
                        
                        return new Open (m_random,
                                         m_output,
                                         handleData (newRecord,
                                                     eack.getSeqNum (),
                                                     eack.getData ()));
                        }
                    };
                    
                return ifSeqNumOk (f, eack);
                }

            @Override
            public State visitNul (final NulSegment nul)
                {
                final F1<NulSegment,State> f = new F1<NulSegment,State> ()
                    {
                    public State run
                            (final NulSegment nul)
                        {
                        final ConnectionRecord newRecord =
                                getNewRecord (nul.getSeqNum ());
                        
                        final Segment ack =
                                new AckSegmentImpl (m_record.getNextSeqNum (),
                                                    nul.getSeqNum (),
                                                    new byte[] {});
                        
                        m_output.transmit (ack);
                        
                        return new Open (m_random, m_output, newRecord);
                        }
                    };
                    
                return ifSeqNumOk (f, nul);
                }
            
            @Override
            public State visitRst (final RstSegment rst)
                {
                // TODO: Signal connection reset.
                return new CloseWait (m_random, m_output, m_record);
                }
            
            @Override
            public State visitSyn (final SynSegment syn)
                {
                final UInt seqNum =
                        new UIntImpl (syn.getAckNum ().toInt () + 1);
                
                final Segment rst =
                        new RstSegmentImpl (seqNum, new UIntImpl (0));
                
                m_output.transmit (rst);
                
                // TODO: Signal connection reset.
                // TDOO: Deallocate connection record.
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
        final long next = m_record.getNextSeqNum ().toLong ();
        final long unacked = m_record.getOldestUnackedSeqNum ().toLong ();
        final int max = m_record.getMaxOutstanding ().toInt ();
        
        /*
        m_logger.debug ("next: " + next);
        m_logger.debug ("unacked: " + unacked);
        m_logger.debug ("max: " + max);
        */
        
        if (next < unacked + max)
            {
            final Segment ack =
                    new AckSegmentImpl (m_record.getNextSeqNum (),
                                        m_record.getLastReceivedSeqNum (),
                                        data);
            
            m_output.transmit (ack);
            
            final UInt nextNext =
                    new UIntImpl (m_record.getNextSeqNum ().toInt () + 1);
            
            final ConnectionRecord newRecord =
                    new ConnectionRecordImpl
                        (m_record.getInitialReceiveSeqNum (),
                         m_record.getInitialSeqNum (),
                         m_record.getLastReceivedSeqNum (),
                         m_record.getMaxBuffered (),
                         m_record.getMaxOutstanding (),
                         m_record.getMaxReceiveSegmentSize (),
                         m_record.getMaxSendSegmentSize (),
                         nextNext,
                         m_record.getOldestUnackedSeqNum (),
                         m_record.getOutOfSequenceAcks ());
            
            final State nextState = new Open (m_random, m_output, newRecord);
            
            return new PairImpl<SendStatus,State>
                            (SendStatus.SUCCESS, nextState);
            }
        else
            {
            m_logger.debug ("Did not send, since we have too many " +
                                "outstanding segments");
            
            return new PairImpl<SendStatus,State>
                            (SendStatus.SEND_BUFFER_FULL, this);
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public State send (final long tryTime, final byte[] data)
        {
        // TODO
        return this;
        }
    
    /**
     * {@inheritDoc}
     */
    public State timerExpired (final TimerId timerId)
        {
        return this;
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString ()
        {
        return "OPEN";// + m_record.getNextSeqNum ().toLong ();
        }
    }
