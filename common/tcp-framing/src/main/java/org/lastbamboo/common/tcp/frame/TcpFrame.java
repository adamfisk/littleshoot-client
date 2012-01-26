package org.lastbamboo.common.tcp.frame;

import org.apache.commons.lang.ArrayUtils;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.util.mina.MinaUtils;

/**
 * Class for a single TCP frame. 
 */
public final class TcpFrame
    {

    private final byte[] m_data;
    
    /**
     * Creates a new TCP frame for the specified framed data.
     * 
     * @param data The data to frame.
     */
    public TcpFrame(final ByteBuffer data)
        {
        this(MinaUtils.toByteArray(data));
        }
    
    /**
     * Creates a new {@link TcpFrame}.
     * 
     * @param data The data.
     */
    public TcpFrame(final byte[] data)
        {
        if (data.length > 0xffff)
            {
            throw new IllegalArgumentException(
                "Data length must be smaller than: "+0xffff+" but is:"+
                data.length);
            }
        m_data = data.clone();
        }

    public TcpFrame(final byte[] data, final  int off, final int len)
        {
        // TODO: Figure out a more efficient way of doing this?  Possibly
        // using ByteBuffers internally, something like that?
        this.m_data = ArrayUtils.subarray(data, off, len);
        }

    /**
     * Accessor for the length of the framed data.
     * 
     * @return The length of the framed data.
     */
    public int getLength()
        {
        return this.m_data.length;
        }

    /**
     * Accessor for the framed data buffer.
     * 
     * @return The framed data buffer.
     */
    public byte[] getData()
        {
        return this.m_data.clone();
        }
    
    @Override
    public String toString()
        {
        return getClass().getSimpleName();
        }
    }
