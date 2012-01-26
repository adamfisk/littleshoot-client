package org.lastbamboo.common.ice;

import junit.framework.TestCase;

/**
 * Test for the delay calculator.
 */
public class IceTransactionDelayCalculatorTest extends TestCase
    {

    public void testCalculateDelay() throws Exception
        {
        final int delay = IceTransactionDelayCalculator.calculateDelay(20, 1);
        
        assertEquals(20, delay);
        }
    
    }
