package org.lastbamboo.common.ice.candidate;

/**
 * States for ICE candidate pairs. 
 */
public enum IceCandidatePairState
    {

    /**
     * The pair is waiting for execution.
     */
    WAITING,
    
    /**
     * The pair resolution is in progress.
     */
    IN_PROGRESS,
    
    /**
     * The pair succeeded.
     */
    SUCCEEDED,
    
    /**
     * The pair has permanently failed.
     */
    FAILED,
    
    /**
     * The pair is inactive.
     */
    FROZEN;
    
    }
