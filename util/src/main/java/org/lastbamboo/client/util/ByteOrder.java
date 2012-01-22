package org.lastbamboo.client.util;

import java.io.*;

/**
 * Various static routines for solving endian problems.
 * 
 * TODO Look for open source versions of these methods.
 */
public final class ByteOrder 
    {

    /**
     * Big-endian bytes to short.
     * @param x the array of big-endian bytes.
     * @param offset any offset into the specified array.
     * @return the short representation of the big-endian bytes.
     */
    public static short beb2short(final byte[] x, final int offset) 
        {
        return (short)((x[offset    ]  <<  8) |
                       (x[offset + 1] & 0xFF));
        }
    
    /**
     * Interprets the value of x as an unsigned byte, and returns
     * it as integer.  For example, ubyte2int(0xFF) == 255, not -1.
     * 
     * @param x the unsigned byte to convert to an int.
     * @return the integer representation of the unsigned byte.
     */
    public static int ubyte2int(final byte x) 
        {
        return x & 0xFF;
        }
    
    /**
     * Int to little-endian bytes: writes x to given stream.
     * @param x
     * @param os
     * @throws IOException
     */
    public static void int2leb(final int x, final OutputStream os)
        throws IOException 
        {
        os.write((byte) x       );
        os.write((byte)(x >>  8));
        os.write((byte)(x >> 16));
        os.write((byte)(x >> 24));
        }
    
    /**
     * Int to little-endian bytes: writes x to buf[offset ..].
     * @param x the int to convert
     * @param buf the little-endian byte array to create
     * @param offset the offset into the byte array
     */
    public static void int2leb(final int x, final byte[] buf, 
        final int offset) 
        {
        buf[offset    ] = (byte) x       ;
        buf[offset + 1] = (byte)(x >>  8);
        buf[offset + 2] = (byte)(x >> 16);
        buf[offset + 3] = (byte)(x >> 24);
        }
    
    /**
     * Little-endian bytes to short.  Returns the value of 
     * x[offset .. offset + 2] as a short, assuming x is interpreted as a 
     * signed little-endian number (i.e., x[offset] is LSB).  If you want to 
     * interpret it as an unsigned number, call ubytes2int() on the result.
     * @param x the little-endian byte array to convert
     * @param offset the offset of the least significant bit in the byte array 
     * @return a new short converted from the byte array data
     */
    public static short leb2short(final byte[] x, final int offset) 
        {
        return (short)((x[offset    ] & 0xFF) |
                       (x[offset + 1]  <<  8));
        }


    /**
     * Little-endian bytes to int.  Returns the value of 
     * x[offset .. offset + 4] as an int, assuming x is interpreted as a 
     * signed little-endian number (i.e., x[offset] is LSB). If you want to 
     * interpret it as an unsigned number, call ubytes2long() on the result.
     * @param x the byte array to convert
     * @param offset the offset of the least significant bit in the byte array
     * @return a new int from the little-endian byte data
     */
    public static int leb2int(final byte[] x, final int offset) 
        {
        return ( x[offset    ] & 0xFF       ) |
               ((x[offset + 1] & 0xFF) <<  8) |
               ((x[offset + 2] & 0xFF) << 16) |
               ( x[offset + 3]         << 24);
        }


    /**
     * Short to little-endian bytes: writes x to given stream.
     * @param x the short to convert
     * @param os the stream to write the little-endian byte to
     * @throws IOException if any write error occurs while writing the bytes
     */
    public static void short2leb(final short x, final OutputStream os)
        throws IOException 
        {
        os.write((byte) x      );
        os.write((byte)(x >> 8));
        }
    }
