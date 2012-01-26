package org.lastbamboo.common.rudp;

import java.net.InetSocketAddress;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.util.UInt;
import org.littleshoot.util.UIntImpl;
import org.littleshoot.util.mina.ByteBufferExt;
import org.littleshoot.util.mina.ByteBufferExtImpl;

/**
 * An implementation of reliable UDP utilities.
 */
public final class RudpUtils
    {
    
    private RudpUtils() {}
    
    /**
     * Returns the next byte in a byte buffer.  If the byte buffer is empty, 0
     * is returned. 
     * 
     * @param bb The byte buffer.
     * @return The next byte in the byte buffer or 0 if the buffer is empty.
     */
    private static final byte safeGet (final ByteBufferExt bb)
        {
        if (bb.hasRemaining ())
            {
            return bb.get ();
            }
        else
            {
            return 0;
            }
        }
    
    /**
     * Returns the checksum computed for a given byte buffer.
     * 
     * @param bb The byte buffer.
     *      
     * @return The checksum for a given byte buffer.
     */
    public static UInt getChecksum (final ByteBuffer bb)
        {
        return getChecksum (new ByteBufferExtImpl (bb));
        }
    
    /**
     * Returns the checksum computed for a given byte buffer.
     * 
     * @param bb The byte buffer.
     *      
     * @return The checksum for a given byte buffer.
     */
    public static UInt getChecksum (final ByteBufferExt bb)
        {
        final int originalPosition = bb.position ();
        final int originalLimit = bb.limit ();
        
        int checksum = 0;
        
        while (bb.hasRemaining ())
            {
            final byte b0 = safeGet (bb);
            final byte b1 = safeGet (bb);
            final byte b2 = safeGet (bb);
            final byte b3 = safeGet (bb);
            
            final int i = (b0 << 24) + (b1 << 16) + (b2 << 8) + b3;
            
            final UInt ui = new UIntImpl (i);
            
            checksum += ui.toLong ();
            }
        
        bb.position (originalPosition);
        bb.limit (originalLimit);
        
        return new UIntImpl (checksum);
        }

    /**
     * Returns whether the checksum held in a byte buffer holding a RUDP segment
     * is correct.
     * 
     * @param bb The extended byte buffer that holds the RUDP segment.
     *      
     * @return True if the checksum is correct, false otherwise.
     */
    public static boolean isChecksumOk (final ByteBufferExt bb)
        {
        final int checkSumIndex = bb.position() + RudpConstants.CHECKSUM_INDEX;
        final UInt checksum = bb.getUInt (checkSumIndex);
        final UInt zero = new UIntImpl (0);
        
        bb.putUInt (checkSumIndex, zero);

        final UInt expectedChecksum = getChecksum (bb);
        return checksum.equals (expectedChecksum);
        }

    
    /**
     * Converts an <code>IoSession</code> into a listening connection
     * identifier.
     * 
     * @param session The session.
     *      
     * @return The listening connection identifier constructed from the given
     *  session.
     */
    public static RudpListeningConnectionId toListeningId (
        final IoSession session)
        {
        final InetSocketAddress localAddress = 
            (InetSocketAddress) session.getLocalAddress ();
        
        return new RudpListeningConnectionIdImpl (localAddress);
        }
    
    
    /**
     * Returns the connection identifier associated with a session.
     * 
     * @param session The session.
     * @return The connection identifier associated with the session.
     */
    public static RudpConnectionId toId (final IoSession session)
        {
        final InetSocketAddress localAddress =
            (InetSocketAddress) session.getLocalAddress ();
        
        final InetSocketAddress remoteAddress =
            (InetSocketAddress) session.getRemoteAddress ();
                
        return new RudpConnectionIdImpl (localAddress, remoteAddress);
        }
    }
