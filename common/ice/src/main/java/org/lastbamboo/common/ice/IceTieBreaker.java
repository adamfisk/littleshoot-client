package org.lastbamboo.common.ice;

import java.util.Random;

/**
 * Class that handled ICE tie breakers. 
 */
public class IceTieBreaker
    {

    private static final Random s_random = new Random();
    private final byte[] m_byteArray = new byte[8];
    
    /**
     * Creates a new tie breaker.
     */
    public IceTieBreaker()
        {
        s_random.nextBytes(m_byteArray);
        }

    /**
     * Accessor for the tie breaker bytes.
     * 
     * @return The tie breaker as a byte array.
     */
    public byte[] toByteArray()
        {
        return m_byteArray;
        }
    }
