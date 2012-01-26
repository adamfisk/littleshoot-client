package org.lastbamboo.common.rudp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.lastbamboo.common.rudp.segment.AbstractSegmentVisitor;
import org.lastbamboo.common.rudp.segment.AckSegment;
import org.lastbamboo.common.rudp.segment.EackSegment;
import org.lastbamboo.common.rudp.segment.Segment;
import org.lastbamboo.common.rudp.segment.SegmentVisitor;
import org.littleshoot.util.F1;
import org.littleshoot.util.UInt;
import org.littleshoot.util.UIntImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the retransmitter interface.
 */
public final class RetransmitterImpl implements Retransmitter
    {
    
    private volatile int m_averageRtt = 0;
    private volatile int m_numRttMeasurements = 0;
    private volatile long m_totalRtt = 0L;
    private volatile int m_maxRtt = 100;
    
    /**
     * The class of object used to track segments for retransmission.
     */
    private static final class Tracker
        {
        /**
         * The segment being tracked.
         */
        private final Segment m_segment;
        
        /**
         * The task used for retransmitting the associated segment.  We maintain
         * this reference so that we may cancel the task when the segment is
         * acknowledged.
         */
        private final TimerTask m_task;

        private final long m_startTime;

        /**
         * Constructs a new tracker.
         * 
         * @param segment
         *      The segment being tracked.
         * @param task
         *      The task used for retransmitting the associated segment.
         */
        private Tracker (final Segment segment, final TimerTask task)
            {
            m_segment = segment;
            m_task = task;
            this.m_startTime = System.currentTimeMillis();
            }
        
        /**
         * Returns the segment being tracked.
         * 
         * @return
         *      The segment being tracked.
         */
        public Segment getSegment ()
            {
            return m_segment;
            }

        /**
         * Returns the task used for retransmitting the associated segment.
         * 
         * @return
         *      The task used for retransmitting the associated segment.
         */
        public TimerTask getTask ()
            {
            return m_task;
            }

        /**
         * Returns the start time for the tracker.
         * 
         * @return The start time for the tracker.
         */
        public long getStartTime()
            {
            return m_startTime;
            }
        }
    /**
     * Returns whether a given segment has data.  Only segments carrying data
     * need to be retransmitted if they are not acknowledged.
     * 
     * @param segment The segment.
     *      
     * @return True if the segment has data, false otherwise.
     */
    private static boolean hasData (final Segment segment)
        {
        final SegmentVisitor<Boolean> visitor =
            new AbstractSegmentVisitor<Boolean> (false)
            {
            @Override
            public Boolean visitAck (final AckSegment ack)
                {
                return ack.getData ().length > 0;
                }
            
            @Override
            public Boolean visitEack (final EackSegment eack)
                {
                return eack.getData ().length > 0;
                }
            };
            
        return segment.accept (visitor);
        }

    /**
     * The logger.
     */
    private final Logger m_logger;
    
    /**
     * The delay in milliseconds before retransmitting.
     */
    private volatile long m_retransmissionDelay;
    
    /**
     * The timer used to manage timing of retransmissions.
     */
    private final Timer m_timer;
    
    /**
     * The map that maintains information about transmitted segments.
     */
    private final SortedMap<UInt,Tracker> m_transmitted;
    
    /**
     * The function used to write UDP messages.
     */
    private final F1<Segment,Void> m_writeF;
    
    /**
     * Constructs a new retransmitter.
     * 
     * @param writeF The function used to write UDP messages.
     */
    public RetransmitterImpl (final F1<Segment,Void> writeF)
        {
        m_logger = LoggerFactory.getLogger (RetransmitterImpl.class);
        
        m_writeF = writeF;
        
        m_timer = new Timer ("RUDP-Retransmitter-Timer", true);
        
        m_retransmissionDelay = 4000;
        m_transmitted = new TreeMap<UInt,Tracker> ();
        }
   
    /**
     * Transmits a segment with a given delay for retransmission.
     * 
     * @param segment The segment.
     * @param delay
     *      The delay.  This may be ignored if the segment is not eligible for
     *      retransmission.
     */
    private void transmit (final Segment segment, final long delay)
        {
        m_logger.debug ("Transmitting segment...");
        
        // Only segments with data are retransmitted.
        if (hasData (segment))
            {
            final TimerTask retransmissionTask = new TimerTask ()
                {
                @Override
                public void run ()
                    {
                    try
                        {
                        synchronized (RetransmitterImpl.this)
                            {
                            //System.out.println("Retransmitting segment....");
                            m_logger.debug ("Retransmitting segment..");
                            m_transmitted.remove (segment.getSeqNum ());
                            
                            final long maxDelay = 8000;
                            final long backedOff = (long) (delay * 1.5);
                            
                            final long newDelay = Math.min (backedOff, maxDelay);
                            transmit (segment, newDelay);
                            }
                        }
                    catch (final RuntimeException e)
                        {
                        m_logger.warn("Unexpected throwable!!!", e);
                        }
                    }
                };
            
            synchronized (this)
                {
                m_writeF.run (segment);
                m_timer.schedule (retransmissionTask, delay);
                final Tracker tracker = 
                    new Tracker (segment, retransmissionTask);
                m_transmitted.put (segment.getSeqNum (), tracker);
                }
            }
        else
            {
            m_writeF.run (segment);
            }
        } 
    
    /**
     * {@inheritDoc}
     */
    public void acknowledged (final UInt seqNum)
        {
        final long endTime = System.currentTimeMillis();
        synchronized (this)
            {
            // We grab all of the segments with sequence numbers <= the given
            // sequence number.  We do this by using the headMap method which is
            // exclusive with respect to its argument.  That is why we have to
            // add one.
            final Map<UInt,Tracker> acked =
                m_transmitted.headMap (new UIntImpl (seqNum.toInt () + 1));
            
            // We track the keys to remove separately to avoid concurrent
            // modification.  The map returned by headMap is actually backed by
            // the original map.
            final Collection<UInt> keysToRemove =
                new ArrayList<UInt> (acked.size ());
            
            for (final Tracker tracker : acked.values ())
                {
                final Segment segment = tracker.getSegment ();
                
                //m_logger.debug ("Acknowledging: " +
                 //                   segment.getSeqNum ().toLong ());
                
                tracker.getTask ().cancel ();
                keysToRemove.add (segment.getSeqNum ());
                }
            
            for (final UInt keyToRemove : keysToRemove)
                {
                final Tracker removed = m_transmitted.remove (keyToRemove);
                
                m_numRttMeasurements++;
                final int curRtt = (int) (endTime - removed.getStartTime());
                if (curRtt > m_maxRtt)
                    {
                    m_maxRtt = curRtt;
                    }
                m_totalRtt += curRtt;
                
                }
            }
        
        if (m_numRttMeasurements > 4)
            {
            m_averageRtt = (int) (m_totalRtt / m_numRttMeasurements);
            //System.out.println("RTT measurements "+m_numRttMeasurements);
            //System.out.println("Average RTT: "+m_averageRtt);
            //System.out.println("Max RTT: "+m_maxRtt);
            //m_logger.debug("RTT measurements "+m_numRttMeasurements);
            //m_logger.debug("Average RTT: "+m_averageRtt);
            //m_logger.debug("Max RTT: "+m_maxRtt);
            m_retransmissionDelay = Math.max(1000, m_averageRtt * 4);
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public void cancelActive ()
        {
        synchronized (this)
            {
            for (final Tracker tracker : m_transmitted.values ())
                {
                tracker.getTask ().cancel ();
                }
            
            m_transmitted.clear ();
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public void oneAcknowledged (final UInt seqNum)
        {
        synchronized (this)
            {
            if (m_transmitted.containsKey (seqNum))
                {
                final Tracker tracker = m_transmitted.get (seqNum);
            
                m_logger.debug ("Acknowledging: " + seqNum.toLong ());
            
                tracker.getTask ().cancel ();
                m_transmitted.remove (seqNum);
                }
            else
                {
                // The sequence number has already been acknowledged or is
                // otherwise unknown to this retransmitter.
                }
            }
        }

    /**
     * {@inheritDoc}
     */
    public void transmit (final Segment segment)
        {
        transmit (segment, m_retransmissionDelay);
        }
    }
