package org.lastbamboo.common.ice.candidate;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.lastbamboo.common.ice.IcePriorityCalculator;
import org.lastbamboo.common.ice.IceTransportProtocol;

/**
 * Class that abstracts out general attributes of all ICE session candidates.
 */
public abstract class AbstractIceCandidate implements IceCandidate, 
    Comparable<AbstractIceCandidate>
    {

    private final InetSocketAddress m_address;
    
    private final IceTransportProtocol m_transport;

    private final IceCandidateType m_candidateType;
    
    private final long m_priority;

    private final String m_foundation;
    
    private boolean m_controlling;

    private final IceCandidate m_baseCandidate;
    
    private final int m_componentId;
    
    private final InetAddress m_relatedAddress;
    private final int m_relatedPort;
    
    /**
     * The component ID is 1 unless otherwise specified.
     */
    protected final static int DEFAULT_COMPONENT_ID = 1;
    
    private final boolean m_isUdp;
    
    /**
     * Creates a new ICE candidate.
     * 
     * @param socketAddress The candidate address and port.
     * @param baseAddress The base address.
     * @param type The type of candidate.
     * @param transport The transport protocol.
     * @param controlling Whether or not this candidate is the controlling
     * candidate.
     */
    public AbstractIceCandidate(final InetSocketAddress socketAddress, 
        final InetAddress baseAddress, final IceCandidateType type, 
        final IceTransportProtocol transport, final boolean controlling)
        {
        this(socketAddress, 
            IceFoundationCalculator.calculateFoundation(type, baseAddress, 
                transport), 
            type, transport, 
            IcePriorityCalculator.calculatePriority(type, transport), controlling, 
            DEFAULT_COMPONENT_ID, null, null, -1);
        }

    protected AbstractIceCandidate(final InetSocketAddress socketAddress, 
        final String foundation, final IceCandidateType type, 
        final IceTransportProtocol transport, final long priority,
        final boolean controlling, final int componentId,
        final IceCandidate baseCandidate, final InetAddress relatedAddress, 
        final int relatedPort)
        {
        if (socketAddress == null)
            {
            throw new NullPointerException("Null socket address");
            }
        if (type == null)
            {
            throw new NullPointerException("Null type");
            }
        if (transport == null)
            {
            throw new NullPointerException("Null transport");
            }
        this.m_address = socketAddress;
        this.m_candidateType = type;
        this.m_transport = transport;
        this.m_priority = priority;
        this.m_foundation = foundation;
        this.m_controlling = controlling;
        this.m_componentId = componentId;
        if (baseCandidate == null)
            {
            this.m_baseCandidate = this;
            }
        else
            {
            this.m_baseCandidate = baseCandidate;
            }
        
        m_relatedAddress = relatedAddress;
        m_relatedPort = relatedPort;
        
        boolean udp = true;
        switch (transport)
            {
            case TCP_ACT:
            case TCP_PASS:
            case TCP_SO:
                udp = false;
                break;
            case UDP:
                udp = true;
                break;
            }
        this.m_isUdp = udp;
        }
    
    public void setControlling(final boolean controlling)
        {
        this.m_controlling = controlling;
        }

    public IceTransportProtocol getTransport()
        {
        return this.m_transport;
        }

    public IceCandidateType getType()
        {
        return this.m_candidateType;
        }

    public final InetSocketAddress getSocketAddress()
        {
        return m_address;
        }

    public final long getPriority()
        {
        return m_priority;
        }

    public int getComponentId()
        {
        return m_componentId;
        }

    public String getFoundation()
        {
        return m_foundation;
        }
    
    public boolean isControlling()
        {
        return m_controlling;
        }
    
    public IceCandidate getBaseCandidate()
        {
        return m_baseCandidate;
        }
    
    public InetAddress getRelatedAddress()
        {
        return m_relatedAddress;
        }

    public int getRelatedPort()
        {
        return m_relatedPort;
        }

    public boolean isUdp()
        {
        return m_isUdp;
        }

    @Override
    public int hashCode()
        {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((m_address == null) ? 0 : m_address.hashCode());
        result = PRIME * result + ((m_candidateType == null) ? 0 : m_candidateType.hashCode());
        result = PRIME * result + m_componentId;
        result = PRIME * result + ((m_foundation == null) ? 0 : m_foundation.hashCode());
        result = PRIME * result + (int) (m_priority ^ (m_priority >>> 32));
        result = PRIME * result + ((m_transport == null) ? 0 : m_transport.hashCode());
        return result;
        }

    @Override
    public boolean equals(Object obj)
        {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbstractIceCandidate other = (AbstractIceCandidate) obj;
        if (m_address == null)
            {
            if (other.m_address != null)
                return false;
            }
        else if (!m_address.equals(other.m_address))
            return false;
        if (m_candidateType == null)
            {
            if (other.m_candidateType != null)
                return false;
            }
        else if (!m_candidateType.equals(other.m_candidateType))
            return false;
        if (m_componentId != other.m_componentId)
            return false;
        if (m_foundation == null)
            {
            if (other.m_foundation != null)
                return false;
            }
        else if (!m_foundation.equals(other.m_foundation))
            return false;
        if (m_priority != other.m_priority)
            return false;
        if (m_transport == null)
            {
            if (other.m_transport != null)
                return false;
            }
        else if (!m_transport.equals(other.m_transport))
            return false;
        return true;
        }

    public int compareTo(final AbstractIceCandidate other)
        {
        final Long priority1 = Long.valueOf(m_priority);
        final Long priority2 = Long.valueOf(other.getPriority());
        final int priorityComparison = priority1.compareTo(priority2);
        if (priorityComparison != 0)
            {
            // We reverse this because we want to go from highest to lowest.
            return -priorityComparison;
            }
        
        // Otherwise, the two candidates have the same priority, but we now
        // need to check the other equality attributes for consistency with
        // equals.  

        if (!m_address.equals(other.m_address))
            return -1;
        if (!m_candidateType.equals(other.m_candidateType))
            return -1;
        if (m_foundation != other.m_foundation)
            return -1;
        if (m_controlling != other.m_controlling)
            return -1;
        if (!m_transport.equals(other.m_transport))
            return -1;
        
        // In this case, they really are the same.
        return 0;
        }
    
    @Override
    public String toString()
        {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" ");
        sb.append(m_controlling ? "controlling" : "controlled");
        sb.append(" ");
        sb.append(m_address);
        sb.append(this.m_isUdp ? " UDP" : " TCP");
        return sb.toString();
        }
    }
