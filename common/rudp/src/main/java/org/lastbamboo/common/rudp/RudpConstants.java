package org.lastbamboo.common.rudp;

import org.littleshoot.util.UByte;
import org.littleshoot.util.UByteImpl;

/**
 * Constants for our reliable UDP implementation.
 */
public final class RudpConstants
    {
    /**
     * The bitfield indicating a SYN segment.
     */
    public static final byte SYN = getBitfield (7);
    
    /**
     * The bitfield indicating an ACK segment.
     */
    public static final byte ACK = getBitfield (6);
    
    /**
     * The bitfield indicating an EACK segment.
     */
    public static final byte EACK = (byte) (getBitfield (5) | getBitfield (6));
    
    /**
     * The bitfield indicating a RST segment.
     */
    public static final byte RST = getBitfield (4);
    
    /**
     * The bitfield indicating a NUL segment.
     */
    public static final byte NUL = getBitfield (3);
    
    /**
     * The byte index of the checksum in an RUDP header.
     */
    public static final int CHECKSUM_INDEX = 12;
    
    /**
     * The ACK header length.  Note that this is in 2-byte units.
     */
    public static final UByte ACK_HEADER_LENGTH = new UByteImpl (8);
    
    /**
     * The EACK header length.  Note that this is in 2-byte units.
     */
    public static final UByte EACK_HEADER_LENGTH = new UByteImpl (8);
    
    /**
     * The NUL header length.  Note that this is in 2-byte units.
     */
    public static final UByte NUL_HEADER_LENGTH = new UByteImpl (8);
    
    /**
     * The RST header length.  Note that this is in 2-byte units.
     */
    public static final UByte RST_HEADER_LENGTH = new UByteImpl (8);
    
    /**
     * The SYN header length.  Note that this is in 2-byte units.
     */
    public static final UByte SYN_HEADER_LENGTH = new UByteImpl (11);
    
    /**
     * Returns a byte bitfield with a given bit set.
     * 
     * @param bit
     *      The bit to set.
     *      
     * @return
     *      A byte bitfield with the given bit set.
     */
    private static byte getBitfield
            (final int bit)
        {
        assert bit >= 0;
        assert bit < 8;
        
        return (byte) (1 << bit);
        }
    }
