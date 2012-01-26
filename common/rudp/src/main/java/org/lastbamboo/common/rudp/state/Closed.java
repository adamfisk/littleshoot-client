package org.lastbamboo.common.rudp.state;

import java.security.SecureRandom;
import java.util.Random;

import org.lastbamboo.common.rudp.RudpException;
import org.lastbamboo.common.rudp.RudpNotOpenException;
import org.lastbamboo.common.rudp.TimerId;
import org.lastbamboo.common.rudp.segment.AbstractSegmentVisitor;
import org.lastbamboo.common.rudp.segment.AckSegment;
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
 * The CLOSED state.
 */
public final class Closed extends AbstractState implements State
    {
    /**
     * The logger.
     */
    private final Logger m_logger;
    
    /**
     * Constructs a CLOSED state.
     * 
     * @param random
     *      The random number generator that may be used by this state.
     * @param output
     * 		The output interface for this state.
     * @param record
     * 		The connection record of this connection.
     */
    public Closed
            (final Random random,
             final StateOutput output,
             final ConnectionRecord record)
        {
        super (random, output, record);
        
        m_logger = LoggerFactory.getLogger (Closed.class);
        }
    
    /**
     * {@inheritDoc}
     */
    public <T> T accept
            (final StateVisitor<T> visitor)
        {
        return visitor.visitClosed (this);
        }
    
    /**
     * {@inheritDoc}
     */
    public State close
            ()
        {
        throw new RudpException ("Connection not open");
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
            
            private void sendRstAck
                    ()
                {
                final Segment rst =
                        new RstSegmentImpl (new UIntImpl (0),
                                            m_record.getLastReceivedSeqNum ());
                
                m_output.transmit (rst);
                }

            @Override
            public State visitAck
                    (final AckSegment ack)
                {
                sendRst (ack.getAckNum ());
                return super.visitAck (ack);
                }

            @Override
            public State visitEack
                    (final EackSegment eack)
                {
                sendRstAck ();
                return super.visitEack (eack);
                }

            @Override
            public State visitNul
                    (final NulSegment nul)
                {
                sendRst (nul.getAckNum ());
                return super.visitNul (nul);
                }
            
            @Override
            public State visitRst
                    (final RstSegment rst)
                {
                return super.visitRst (rst);
                }

            @Override
            public State visitSyn
                    (final SynSegment syn)
                {
                sendRstAck ();
                return super.visitSyn (syn);
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
        final int seqNum = m_random.nextInt ();
        
        final ConnectionRecord newRecord =
                new ConnectionRecordImpl
                    (m_record.getInitialReceiveSeqNum (),
                     m_record.getInitialSeqNum (),
                     m_record.getLastReceivedSeqNum (),
                     m_record.getMaxBuffered (),
                     m_record.getMaxOutstanding (),
                     m_record.getMaxReceiveSegmentSize (),
                     m_record.getMaxSendSegmentSize (),
                     new UIntImpl (seqNum + 1),
                     new UIntImpl (seqNum),
                     m_record.getOutOfSequenceAcks ());
        
        // We open a connection by sending an initial SYN segment and waiting
        // for a response by entering the SYN-SENT state.
        final Segment syn =
                new SynSegmentImpl (new UIntImpl (seqNum),
                                    new UIntImpl (0),
                                    newRecord.getMaxOutstanding (),
                                    newRecord.getMaxReceiveSegmentSize ());
        
        m_logger.debug ("Writing " + syn);
        
        m_output.transmit (syn);
            
        return new SynSent (m_random, m_output, newRecord);
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
