package org.lastbamboo.common.rudp;

import static org.lastbamboo.common.rudp.ConnectionImpl.InitialState.LISTEN;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;

import org.lastbamboo.common.rudp.segment.AbstractSegmentVisitor;
import org.lastbamboo.common.rudp.segment.Segment;
import org.lastbamboo.common.rudp.segment.SegmentVisitor;
import org.lastbamboo.common.rudp.segment.SynSegment;
import org.lastbamboo.common.rudp.state.ConnectionRecord;
import org.lastbamboo.common.rudp.state.ConnectionRecordImpl;
import org.lastbamboo.common.rudp.state.OutOfOrderData;
import org.littleshoot.util.F1;
import org.littleshoot.util.Pair;
import org.littleshoot.util.PairImpl;
import org.littleshoot.util.UIntImpl;
import org.littleshoot.util.UShortImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the listening connection interface.
 */
public final class ListeningConnectionImpl implements ListeningConnection
    {
    /**
     * The local address on which this connection is listening.
     */
    private final InetSocketAddress m_localAddress;
    
    /**
     * The logger.
     */
    private final Logger m_logger;
    
    /**
     * The queue of incoming connections that have not yet been accepted.
     */
    private final ArrayBlockingQueue<QueuedConnection> m_queuedConnections;
    
    /**
     * The random number generator used to generate initial sequence numbers.
     */
    private final Random m_random;
    
    /**
     * The timer used to schedule future events.
     */
    private final Timer m_timer;
    
    /**
     * Constructs a new listening connection.
     * 
     * @param random
     *      The random number generator used to generate initial sequence
     *      numbers.
     * @param timer
     *      The timer used to schedule future events.
     * @param localAddress
     *      The local address on which this connection is listening.
     * @param backlog
     *      The maximum number of connections to queue that have not yet been
     *      accepted.
     */
    public ListeningConnectionImpl
            (final Random random,
             final Timer timer,
             final InetSocketAddress localAddress,
             final int backlog)
        {
        m_logger = LoggerFactory.getLogger (ListeningConnectionImpl.class);
        
        // TODO: Handle SND.MAX and RMAX.BUF
        
        m_random = random;
        m_timer = timer;
    
        m_localAddress = localAddress;
        m_queuedConnections =
                new ArrayBlockingQueue<QueuedConnection> (backlog);
        }

    /**
     * {@inheritDoc}
     */
    public Pair<InetSocketAddress,Connection> accept
            (final F1<InetSocketAddress,Void> openCallback,
             final Runnable closedCallback)
        {
        try
            {
            m_logger.debug ("m_queuedConnection.size () == " +
                                m_queuedConnections.size ());
            
            // We use a blocking queue to block until a connection is actually
            // available for accepting.
            final QueuedConnection queuedConnection =
                    m_queuedConnections.take ();
            
            m_logger.debug ("Queued connection acquired");
            
            // Once we have a connection to accept, we create a new full
            // connection object to handle it.
            // final int seqNum = m_random.nextInt ();
            final int seqNum = 17;
            
            m_logger.debug ("About to construct connection record");
            
            final ConnectionRecord record =
                    new ConnectionRecordImpl
                        (new UIntImpl (0),
                         new UIntImpl (seqNum),
                         new UIntImpl (0),
                         new UShortImpl (200), // TODO: Should be configurable.
                         new UShortImpl (200),
                         new UShortImpl (0),
                         new UShortImpl (0),
                         new UIntImpl (seqNum + 1),
                         new UIntImpl (seqNum),
                         new LinkedList<OutOfOrderData> ());
            
            final Runnable onOpen = new Runnable ()
                {
                public void run ()
                    {
                    try
                        {
                        openCallback.run (queuedConnection.getRemoteAddress ());
                        }
                    catch (final Throwable t)
                        {
                        m_logger.warn("Throwable opening.", t);
                        }
                    }
                };            
                
            m_logger.debug ("About to create new connection");
                
            final Connection connection =
                    new ConnectionImpl (m_random,
                                        m_timer,
                                        LISTEN,
                                        record,
                                        queuedConnection.getWriteF (),
                                        onOpen,
                                        closedCallback);
            
            m_logger.debug ("About to handle SYN");
            
            // The handling of the initial SYN is done by the new connection.
            connection.handle (queuedConnection.getSyn ());
            
            m_logger.debug ("SYN handled");
            
            return new PairImpl<InetSocketAddress,Connection>
                    (queuedConnection.getRemoteAddress (), connection);
            }
        catch (final InterruptedException e)
            {
            throw new RuntimeException (e);
            }
        }

    /**
     * {@inheritDoc}
     */
    public void handle
            (final InetSocketAddress remoteAddress,
             final Segment segment,
             final F1<Segment,Void> writeF)
        {
        m_logger.debug ("Segment received: " + segment);
        
        final SegmentVisitor<Void> visitor =
            new AbstractSegmentVisitor<Void> (null)
            {
            @Override
            public Void visitSyn (final SynSegment syn)
                {
                m_logger.debug ("SYN received on listening connection");
                
                if (m_queuedConnections.remainingCapacity () > 0)
                    {
                    try
                        {
                        final QueuedConnection connection =
                                new QueuedConnectionImpl (m_localAddress,
                                                          remoteAddress,
                                                          syn,
                                                          writeF);
                        
                        m_logger.debug ("Queuing new connection");
                        m_queuedConnections.put (connection);
                        }
                    catch (final InterruptedException e)
                        {
                        m_logger.debug("Unexpected interrupt", e);
                        throw new RuntimeException ("Interrupted?", e);
                        }
                    }
                else
                    {
                    // We have reached our backlog limit.  Ignore this
                    // connection attempt.
                    m_logger.info ("Connection attempt dropped, since " +
                                        "backlog limit has been reached");
                    }
                
                return null;
                }
            };
            
        segment.accept (visitor);
        }
    }
