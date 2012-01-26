package org.lastbamboo.common.stun.stack.message.attributes;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Abstract class for all STUN attributes that include an address.
 */
public abstract class AbstractStunAddressAttribute extends AbstractStunAttribute
    implements StunAddressAttribute
    {

    private final InetSocketAddress m_inetSocketAddress;

    private final int m_addressFamily;

    /**
     * Creates a new mapped address attribute.
     * 
     * @param attributeType The type of the attribute.
     * @param socketAddress The IP and port to put in the attribute.
     */
    public AbstractStunAddressAttribute(final StunAttributeType attributeType,
        final InetSocketAddress socketAddress)
        {
        super(attributeType, getBodyLength(socketAddress));
        this.m_inetSocketAddress = socketAddress;
        
        final InetAddress address = socketAddress.getAddress();
        
        if (address instanceof Inet4Address)
            {
            this.m_addressFamily = 0x01;
            }
        else
            {
            this.m_addressFamily = 0x02;
            }
        }

    private static int getBodyLength(final InetSocketAddress address)
        {
        final InetAddress ia = address.getAddress();
        if (ia instanceof Inet4Address)
            {
            // 2 byte family + 2 byte port + 4 byte address
            return 8;
            }
        // 2 byte family + 2 byte port + 16 byte address
        return 20;
        }

    public InetSocketAddress getInetSocketAddress()
        {
        return m_inetSocketAddress;
        }

    public int getAddressFamily()
        {
        return this.m_addressFamily;
        }
    
    @Override
    public String toString()
        {
        return getClass().getSimpleName() + " for: " + m_inetSocketAddress;
        }
    }
