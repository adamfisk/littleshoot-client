package org.lastbamboo.common.util.mina;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.SimpleByteBufferAllocator;
import org.junit.Before;
import org.littleshoot.util.mina.ByteBufferUtils;

/**
 * Tests the utility class for manipulating <code>ByteBuffer</code>s.
 */
public final class ByteBufferUtilTest extends TestCase
    {

    private static final Logger LOG = LoggerFactory.getLogger(ByteBufferUtilTest.class);
    
    /**
     * MINA does some funky things if we don't do this first.
     */
    @Before
    public void setUp()
        {
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        }
    
    /**
     * Tests the utility method for determining the number of remaining bytes 
     * in a group of <code>ByteBuffer</code>s.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testRemaining() throws Exception
        {
        final ByteBuffer buffer0 = createBuffer(5);
        final ByteBuffer buffer1 = createBuffer(15);
        final ByteBuffer buffer2 = createBuffer(25);
        final ByteBuffer buffer3 = createBuffer(35);
        final Collection<ByteBuffer> buffers = new LinkedList<ByteBuffer>();
        buffers.add(buffer0);
        buffers.add(buffer1);
        buffers.add(buffer2);
        buffers.add(buffer3);
        
        assertEquals(80, ByteBufferUtils.remaining(buffers));
        }
    
    /**
     * Tests the method for combining multiple buffers into one buffer.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testCombine() throws Exception
        {
        final ByteBuffer buffer0 = createBuffer(5);
        final ByteBuffer buffer1 = createBuffer(15);
        final ByteBuffer buffer2 = createBuffer(25);
        final ByteBuffer buffer3 = createBuffer(35);
        final Collection<ByteBuffer> buffers = new LinkedList<ByteBuffer>();
        buffers.add(buffer0);
        buffers.add(buffer1);
        buffers.add(buffer2);
        buffers.add(buffer3);
        
        final ByteBuffer combined = ByteBufferUtils.combine(buffers);
        
        assertEquals(80, combined.remaining());
        
        for (int i = 0; i < 5; i++)
            {
            assertEquals(i, combined.get());
            }
        for (int i = 0; i < 15; i++)
            {
            assertEquals(i, combined.get());
            }
        for (int i = 0; i < 25; i++)
            {
            assertEquals(i, combined.get());
            }
        for (int i = 0; i < 35; i++)
            {
            assertEquals(i, combined.get());
            }
        }
    
    private ByteBuffer createBuffer(final int length)
        {
        final ByteBuffer buf = ByteBuffer.allocate(length);
        for (int i = 0; i < length; i++)
            {
            buf.put((byte) i);
            }
        buf.rewind();
        return buf;
        }
    
    /**
     * Tests the method for splitting a large buffer into multiple smaller 
     * buffers.
     * @throws Exception If any unexpected error occurs.
     */
    public void testSplit() throws Exception
        {
        final int chunkSize = 1000;
        final int numChunks = 10;
        
        // We add the 5 at the end to give us an extra set of tricky bytes
        // to handle correctly.
        final int limit = chunkSize * numChunks + 5;
        final ByteBuffer bigBuffer = ByteBuffer.allocate(limit);
        for (int i = 0; i < limit; i++)
            {
            bigBuffer.put((byte) (i % 255));
            }
        
        bigBuffer.flip();
        
        final ByteBuffer testBuffer = bigBuffer.duplicate();

        final Collection buffers = ByteBufferUtils.split(bigBuffer, chunkSize);
        assertEquals(numChunks+1, buffers.size());
        
        int totalLength = 0;
        for (final Iterator iter = buffers.iterator(); iter.hasNext();)
            {
            LOG.trace("Testing next buffer...");
            final ByteBuffer curBuffer = (ByteBuffer) iter.next();
            
            while (curBuffer.hasRemaining())
                {
                final byte testByte = testBuffer.get();
                assertEquals(testByte, curBuffer.get());
                totalLength ++;
                }
            }
        
        // Make sure the total size of the buffers is the size we expect.
        assertEquals(limit, totalLength);
        }
    }
