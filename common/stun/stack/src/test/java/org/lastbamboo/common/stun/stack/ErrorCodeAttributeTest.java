package org.lastbamboo.common.stun.stack;

import org.lastbamboo.common.stun.stack.message.attributes.ErrorCodeAttribute;

import junit.framework.TestCase;

/**
 * Tests the error code attribute.
 */
public class ErrorCodeAttributeTest extends TestCase
    {

    public void testAttribute() throws Exception
        {
        final ErrorCodeAttribute error = 
            new ErrorCodeAttribute(487, "Role Conflict");
        
        assertEquals(4, error.getErrorClass());
        assertEquals(87, error.getErrorNumber());
        assertEquals("Role Conflict", error.getReasonPhrase());
        
        final int bodyLength = "Role Conflict".getBytes("UTF-8").length + 4;
        assertEquals (bodyLength, error.getBodyLength());
        }
    }
