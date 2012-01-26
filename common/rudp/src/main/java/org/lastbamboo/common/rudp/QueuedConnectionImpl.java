package org.lastbamboo.common.rudp;

import java.net.InetSocketAddress;

import org.lastbamboo.common.rudp.segment.Segment;
import org.lastbamboo.common.rudp.segment.SynSegment;
import org.littleshoot.util.F1;

/**
 * An implementation of the queue connection interface.
 */
public final class QueuedConnectionImpl implements QueuedConnection
    {
    /**
     * The local address of this queued connection.
     */
    private final InetSocketAddress m_localAddress;
    
    /**
     * The remote address of this queued connection.
     */
    private final InetSocketAddress m_remoteAddress;
    
    /**
     * The SYN segment used to initiate this connection.
     */
    private final SynSegment m_syn;
    
    /**
     * The function used to write UDP messages for this connection.
     */
    private final F1<Segment,Void> m_writeF;
    
    /**
     * Constructs a new queued connection.
     * 
     * @param localAddress
     *      The local address of this queued connection.
     * @param remoteAddress
     *      The remote address of this queued connection.
     * @param syn
     *      The SYN segment used to initiate this connection.
     * @param writeF
     *      The function used to write UDP messages for this connection.
     */
    public QueuedConnectionImpl
            (final InetSocketAddress localAddress,
             final InetSocketAddress remoteAddress,
             final SynSegment syn,
             final F1<Segment,Void> writeF)
        {
        m_localAddress = localAddress;
        m_remoteAddress = remoteAddress;
        m_syn = syn;
        m_writeF = writeF;
        }

    /**
     * {@inheritDoc}
     */
    public InetSocketAddress getLocalAddress
            ()
        {
        return m_localAddress;
        }

    /**
     * {@inheritDoc}
     */
    public InetSocketAddress getRemoteAddress
            ()
        {
        return m_remoteAddress;
        }

    /**
     * {@inheritDoc}
     */
    public SynSegment getSyn
            ()
        {
        return m_syn;
        }

    /**
     * {@inheritDoc}
     */
    public F1<Segment,Void> getWriteF
            ()
        {
        return m_writeF;
        }
    }
