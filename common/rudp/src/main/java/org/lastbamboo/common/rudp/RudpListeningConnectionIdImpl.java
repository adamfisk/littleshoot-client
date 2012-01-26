package org.lastbamboo.common.rudp;

import java.net.InetSocketAddress;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * An implementation of the reliable UDP listening connection identifier
 * interface.
 */
public final class RudpListeningConnectionIdImpl
    implements RudpListeningConnectionId
    {
    /**
     * The local address on which the connection is listening.
     */
    private final InetSocketAddress m_localAddress;
    
    /**
     * Constructs a new listening connection identifier.
     * 
     * @param localAddress
     *      The local address on which the connection is listening.
     */
    public RudpListeningConnectionIdImpl (final InetSocketAddress localAddress)
        {
        m_localAddress = localAddress;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals (final Object other)
        {
        if (other instanceof RudpListeningConnectionId)
            {
            final RudpListeningConnectionId otherId =
                (RudpListeningConnectionId) other;
            
            return m_localAddress.equals (otherId.getLocalAddress ());
            }
        else
            {
            return false;
            }
        }

    /**
     * {@inheritDoc}
     */
    public InetSocketAddress getLocalAddress ()
        {
        return m_localAddress;
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode ()
        {
        return new HashCodeBuilder (800159, 801289).
            append (m_localAddress.hashCode ()).toHashCode ();
        }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
        {
        return getClass().getSimpleName() + " on: " + m_localAddress;
        }
    }
