package org.lastbamboo.common.rudp;

import java.net.InetSocketAddress;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * An implementation of the RUDP connection identifier interface.
 */
public final class RudpConnectionIdImpl implements RudpConnectionId
    {
    /**
     * The local address of the connection identified by this identifier.
     */
    private final InetSocketAddress m_localAddress;
    
    /**
     * The remote address of the connection identified by this identifier.
     */
    private final InetSocketAddress m_remoteAddress;
    
    /**
     * Constructs a new connection identifier.
     * 
     * @param localAddress
     *      The local address of the connection identified by this identifier.
     * @param remoteAddress
     *      The remote address of the connection identified by this identifier.
     */
    public RudpConnectionIdImpl (final InetSocketAddress localAddress,
        final InetSocketAddress remoteAddress)
        {
        m_localAddress = localAddress;
        m_remoteAddress = remoteAddress;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals (final Object other)
        {
        if (other instanceof RudpConnectionId)
            {
            final RudpConnectionId otherId = (RudpConnectionId) other;
            
            return m_localAddress.equals (otherId.getLocalAddress ()) &&
                    m_remoteAddress.equals (otherId.getRemoteAddress ());
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
    public InetSocketAddress getRemoteAddress ()
        {
        return m_remoteAddress;
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode ()
        {
        return new HashCodeBuilder (800161, 800497).
                append (m_localAddress.hashCode ()).
                append (m_remoteAddress.hashCode ()).toHashCode ();
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString ()
        {
        return "[local: " + m_localAddress + ", " +
                "remote: " + m_remoteAddress + "]";
        }
    }
