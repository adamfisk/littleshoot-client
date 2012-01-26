package org.lastbamboo.common.stun.stack.message.attributes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.littleshoot.mina.common.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for reading a STUN address and port attribute body.  All 
 * STUN attributes that contain an address and port share a common syntax.
 */
public class AddressAttributeReader
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(AddressAttributeReader.class);
    
    private static final short IPv4 = 0x01;
    private static final short IPv6 = 0x02;
    
    private AddressAttributeReader()
        {
        // Should never be constructed.
        }

    /**
     * Reads the the address and port from the STUN address attribute body.
     * 
     * @param body The body of the STUN address attribute.
     * @return The address and port in the body.
     * @throws IOException If the body does not match the STUN protocol
     * requirements for address and port encoding. 
     */
    public static InetSocketAddress readAddress(final ByteBuffer body) 
        throws IOException
        {
        // The first byte is empty zeros.  Ignore it.
        body.get();
        final byte family = body.get();
        final int port = body.getUnsignedShort();
        
        final int length;
        
        if (family == IPv4)
            {
            length = 4;
            }
        else if (family == IPv6)
            {
            length = 16;
            }
        else
            {
            LOG.error("Could not understand address family: "+family);
            throw new IOException("Could not understand address family: " +
                family);
            }

        final byte[] addressBytes = new byte[length];
        body.get(addressBytes);
        final InetAddress inetAddress = InetAddress.getByAddress(addressBytes);
        final InetSocketAddress socketAddress = 
            new InetSocketAddress(inetAddress, port);
        
        return socketAddress;
        }
    }
