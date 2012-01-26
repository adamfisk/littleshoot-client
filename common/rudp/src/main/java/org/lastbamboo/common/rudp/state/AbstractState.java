package org.lastbamboo.common.rudp.state;

import java.util.Random;

/**
 * An abstract base class to ease implementation of reliable UDP connection
 * states.
 */
public abstract class AbstractState implements State
    {
    /**
     * The output interface for this state.
     */
    protected final StateOutput m_output;
    
    /**
     * The random number generator that may be used by this state.
     */
    protected final Random m_random;
    
    /**
     * The connection record of this connection.
     */
    protected final ConnectionRecord m_record;
    
    /**
     * Constructs this abstract base class.
     * 
     * @param random
     *      The random number generator that may be used by this state.
     * @param output
     * 		The output interface for this state.
     * @param record
     * 		The connection record of this connection.
     */
    protected AbstractState
            (final Random random,
             final StateOutput output,
             final ConnectionRecord record)
        {
        m_random = random;
        m_output = output;
        m_record = record;
        }
    }
