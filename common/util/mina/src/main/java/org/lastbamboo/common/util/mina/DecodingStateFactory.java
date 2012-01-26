package org.lastbamboo.common.util.mina;

/**
 * Factory for creating new {@link DecodingState}s. 
 */
public interface DecodingStateFactory
    {

    /**
     * Creates a new {@link DecodingState}.
     * 
     * @return The new {@link DecodingState}.
     */
    DecodingState newState();
    }
