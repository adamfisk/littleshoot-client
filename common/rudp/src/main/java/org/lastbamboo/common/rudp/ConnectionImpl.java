package org.lastbamboo.common.rudp;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.lastbamboo.common.rudp.segment.Segment;
import org.lastbamboo.common.rudp.state.AbstractStateVisitor;
import org.lastbamboo.common.rudp.state.Closed;
import org.lastbamboo.common.rudp.state.ConnectionRecord;
import org.lastbamboo.common.rudp.state.Listen;
import org.lastbamboo.common.rudp.state.SendStatus;
import org.lastbamboo.common.rudp.state.State;
import org.lastbamboo.common.rudp.state.StateOutput;
import org.lastbamboo.common.rudp.state.StateVisitor;
import org.littleshoot.util.F1;
import org.littleshoot.util.NoneImpl;
import org.littleshoot.util.OnceRunnable;
import org.littleshoot.util.Optional;
import org.littleshoot.util.Pair;
import org.littleshoot.util.SomeImpl;
import org.littleshoot.util.UInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the connection interface.
 */
public class ConnectionImpl implements Connection
    {
    
    /**
     * The logger for this class.
     */
    private final Logger m_logger;
    
    /**
     * The list of messages sent to this connection.
     */
    private final List<byte[]> m_messages;
    
    /**
     * The callback to call when we enter the OPEN state.
     */
    private final Runnable m_onOpen;
    
    /**
     * The callback to call when we enter the CLOSED state.
     */
    private final Runnable m_onClosed;
    
    /**
     * The current state of this connection.
     */
    private State m_state;
    
    /**
     * The timer used to schedule future events.
     */
    private final Timer m_timer;
    
    /**
     * The map of timer identifiers to the actual tasks that handle them.  Our
     * "timers" correspond to timer tasks, not <code>Timer</code>s.
     */
    private final Map<TimerId,TimerTask> m_timerTasks;

    /**
     * The time to wait for incoming reads.  The default of 0 indicates an
     * infinite wait time.
     */
    private int m_soTimeout = 0;

    /**
     * Constructs a new connection.
     * 
     * @param random
     *      The random number generator used to create sequence numbers.
     * @param timer
     *      The timer used to schedule future events.
     * @param initialState
     *      The initial state for this connection.
     * @param record
     *      The initial connection record.
     * @param writeF
     *      The function used to write UDP messages.
     * @param onOpen
     *      The callback to call when we enter the OPEN state.
     * @param onClosed
     *      The callback to call when we enter the CLOSED state.
     */
    public ConnectionImpl
            (final Random random,
             final Timer timer,
             final InitialState initialState,
             final ConnectionRecord record,
             final F1<Segment,Void> writeF,
             final Runnable onOpen,
             final Runnable onClosed)
        {
        m_logger = LoggerFactory.getLogger (ConnectionImpl.class);
        
        m_messages = new LinkedList<byte[]> ();
        
        m_timer = timer;
        m_timerTasks = new HashMap<TimerId,TimerTask> ();
        
        // We use a OnceRunnable so that we make sure to only call the callback
        // once.
        m_onOpen = new OnceRunnable (onOpen);
        m_onClosed = new OnceRunnable (onClosed);
        
        final Retransmitter retransmitter = new RetransmitterImpl (writeF);
        
        final StateOutput output = new StateOutput ()
            {

            public void acknowledge (final UInt seqNum)
                {
                m_logger.debug("Got ACK!!");
                retransmitter.acknowledged (seqNum);
                
                synchronized (ConnectionImpl.this)
                    {
                    ConnectionImpl.this.notifyAll ();
                    }
                
                m_logger.debug ("Notified all");
                }
            
            public void acknowledgeOne (final UInt seqNum)
                {
                retransmitter.oneAcknowledged (seqNum);
                }
            
            public void cancelTimer (final TimerId timerId)
                {
                synchronized (ConnectionImpl.this)
                    {
                    if (m_timerTasks.containsKey (timerId))
                        {
                        final TimerTask task = m_timerTasks.remove (timerId);
                        task.cancel ();
                        }
                    }
                }
            
            public void setTimer (final TimerId timerId, final long delay)
                {
                final TimerTask task = new TimerTask ()
                    {
                    @Override
                    public void run ()
                        {
                        try
                            {
                            synchronized (ConnectionImpl.this)
                                {
                                m_timerTasks.remove (timerId);
                                setState (m_state.timerExpired (timerId));
                                }
                            }
                        catch (final Throwable t)
                            {
                            m_logger.warn("Unexpected throwable!!", t);
                            }
                        }
                    };
                    
                synchronized (ConnectionImpl.this)
                    {
                    m_timerTasks.put (timerId, task);
                    m_timer.schedule (task, delay);
                    }
                }
            
            public void stopRetransmissions ()
                {
                retransmitter.cancelActive ();
                }
            
            public void queue (final byte[] message)
                {
                /*
                final StringBuilder builder = new StringBuilder ();
                
                builder.append ('[');
                
                for (int i = 0; i < message.length - 1; ++i)
                    {
                    builder.append (message[i]);
                    builder.append (", ");
                    }
                
                builder.append (message[message.length - 1]);
                builder.append (']');
                
                */
                //m_logger.debug (builder.toString());
                m_logger.debug ("Adding message to queue");
                synchronized (m_messages)
                    {
                    m_messages.add (message);
                    m_messages.notifyAll ();
                    }
                }

            public void transmit (final Segment segment)
                {
                m_logger.info ("Transmitting segment...");
                
                retransmitter.transmit (segment);
                }
            };
            
        switch (initialState)
            {
            case CLOSED:
                m_state = new Closed (random, output, record);
                break;
                
            case LISTEN:
                m_state = new Listen (random, output, record);
                break;
                
            default:
                assert false : "Unknown initial state: " + initialState;
            }
        }
    
    /**
     * Sets the current state of this connection.
     * 
     * @param state The new state.
     */
    private void setState (final State state)
        {
        synchronized (this)
            {
            if (m_state == state)
                {
                m_logger.debug ("Setting state to same state");
                }
            else
                {
                m_logger.debug ("Setting state to: " + state);
                
                m_state.exited ();
                m_state = state;
                m_state.entered ();
                
                if (isOpen (state))
                    {
                    m_onOpen.run ();
                    }
                else if (isClosed (state))
                    {
                    m_onClosed.run ();
                    
                    m_logger.debug("Notifying messages due to close...");
                    synchronized (m_messages)
                        {
                        m_messages.notifyAll ();
                        }
                    }
                else
                    {
                    // We do not have to do anything special.
                    }
                }
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public void close ()
        {
        synchronized (this)
            {
            setState (m_state.close ());
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public void handle (final Segment segment)
        {
        synchronized (this)
            {
            setState (m_state.getNext (segment));
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public void open ()
        {
        synchronized (this)
            {
            setState (m_state.open ());
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public byte[] receive () throws SocketTimeoutException, SocketException 
        {
        synchronized (m_messages)
            {
            while (m_messages.isEmpty ())
                {
                try
                    {
                    m_logger.debug ("Waiting for more messages");
                    m_messages.wait (this.m_soTimeout);
                    
                    if (isClosed(this.m_state))
                        {
                        throw new SocketException("Socket closed!!");
                        }
                    if (m_messages.isEmpty())
                        {
                        throw new SocketTimeoutException(
                            "No messages received after waiting "+
                            this.m_soTimeout);
                        }
                    m_logger.debug ("Done waiting");
                    }
                catch (final InterruptedException e)
                    {
                    m_logger.warn ("Interrupted while waiting for message", e);
                    throw new RuntimeException ("Interrupted", e);
                    }
                }
        
            return m_messages.remove (0);
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public void send (final byte[] data)
        {
        synchronized (this)
            {
            final Pair<SendStatus,State> pair = m_state.send (data);
            
            switch (pair.getFirst ())
                {
                case SUCCESS:
                    setState (pair.getSecond ());
                    break;
                    
                case SEND_BUFFER_FULL:
                    setState (pair.getSecond ());
                    throw new RudpException ("Too many outstanding segments");
                }
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public void send (final byte[] data, final long timeout)
        {
        final long timeoutTime = System.currentTimeMillis () + timeout;
        synchronized (this)
            {
            final Pair<SendStatus,State> pair = m_state.send (data);
            switch (pair.getFirst ())
                {
                case SUCCESS:
                    m_logger.debug ("Successful sending");
                    setState (pair.getSecond ());
                    break;
                    
                case SEND_BUFFER_FULL:
                    {
                    m_logger.debug("Send buffer full...");
                    setState (pair.getSecond ());
                    
                    SendStatus status = SendStatus.SEND_BUFFER_FULL;
                    long currentTime = System.currentTimeMillis ();
                    
                    while (status == SendStatus.SEND_BUFFER_FULL &&
                                currentTime < timeoutTime)
                        {
                        try
                            {
                            final long waitTime = timeoutTime - currentTime;
                            m_logger.debug("Waiting for: {}", waitTime);
                            
                            if (waitTime < 1)
                                {
                                m_logger.warn("Bad wait time: "+waitTime);
                                break;
                                }
                            this.wait (waitTime);
                            m_logger.debug("Out of wait...sending on: {}", 
                                m_state);
                            
                            final Pair<SendStatus,State> resendPair =
                                    m_state.send (data);
                            
                            setState (resendPair.getSecond ());
                            
                            status = resendPair.getFirst ();
                            currentTime = System.currentTimeMillis ();
                            }
                        catch (final InterruptedException interruptedException)
                            {
                            m_logger.warn("Send interrupted??");
                            throw new RudpException
                                ("Interrupted while waiting to send message");
                            }
                        }
                    
                    switch (status)
                        {
                        case SUCCESS:
                            break;
                            
                        case SEND_BUFFER_FULL:
                            {
                            m_logger.debug("Send buffer still full!!!");
                            throw new RudpException
                                    ("Too many outstanding segments");
                            }
                        default:
                            {
                            m_logger.error("Unknown send status: " + status);
                            throw new RudpException
                                    ("Unknown send status: " + status);
                            }
                        }
                    
                    break;
                    }
                    
                default:
                    {
                    m_logger.error("Unknown send status: " + pair.getFirst ());
                    throw new RudpException
                            ("Unknown send status: " + pair.getFirst ());
                    }
                }
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public Optional<byte[]> tryReceive ()
        {
        synchronized (m_messages)
            {
            if (m_messages.isEmpty ())
                {
                return new NoneImpl<byte[]> ();
                }
            else
                {
                return new SomeImpl<byte[]> (m_messages.remove (0));
                }
            }
        }

    /**
     * {@inheritDoc}
     */
    public void setSoTimeout (final int timeout)
        {
        this.m_soTimeout = timeout;
        }
    
    /**
     * The possible initial states for this connection.
     */
    public enum InitialState
        {
        /**
         * The closed initial state.  This is the initial state for actively
         * opened connections.
         */
        CLOSED,
        
        /**
         * The listen initial state.  This is the initial state for passively
         * opened connections.
         */
        LISTEN
        }
    
    /**
     * Returns whether a given state is the OPEN state.
     * 
     * @param state The state.
     *      
     * @return True if the given state is the OPEN state, false otherwise.
     */
    private static boolean isOpen (final State state)
        {
        final StateVisitor<Boolean> visitor =
                new AbstractStateVisitor<Boolean> (false)
            {
            @Override
            public Boolean visitOpen (final State state)
                {
                return true;
                }
            };
            
        return state.accept (visitor);
        }
    
    /**
     * Returns whether a given state is the CLOSED state.
     * 
     * @param state The state.
     *      
     * @return True if the given state is the CLOSED state, false otherwise.
     */
    private static boolean isClosed (final State state)
        {
        final StateVisitor<Boolean> visitor =
                new AbstractStateVisitor<Boolean> (false)
            {
            @Override
            public Boolean visitClosed (final State state)
                {
                return true;
                }
            };
            
        return state.accept (visitor);
        }
    }
