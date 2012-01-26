package org.lastbamboo.common.nio;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;

import org.lastbamboo.common.nio.WriteHandlerImpl;

import junit.framework.TestCase;

/**
 * Test for the write handler for NIO writing.
 */
public final class WriteHandlerImplTest extends TestCase
    {

    /**
     * Tests to make sure we catch nulls in writes.
     */
    public void testWriteCollectionWithNulls() throws Exception
        {
        final WriteHandlerImpl writer = new WriteHandlerImpl(null, null);
        
        final Collection buffers = new LinkedList();
        buffers.add(null);
        
        try 
            {
            writer.write(buffers);
            fail("Should have thrown null pointer");
            }
        catch (final NullPointerException e)
            {
            
            }
        
        try 
            {
            writer.writeLater(buffers);
            fail("Should have thrown null pointer");
            }
        catch (final NullPointerException e)
            {
            
            }
        
        try 
            {
            writer.write((ByteBuffer)null);
            fail("Should have thrown null pointer");
            }
        catch (final NullPointerException e)
            {
            
            }
        }

    }
