package org.lastbamboo.common.ice;

/**
 * Enumeration of ICE states across all media streams. 
 */
public enum IceState
    {

    /**
     * ICE is still running.
     */
    RUNNING,
    
    /**
     * ICE processing is completed.
     */
    COMPLETED,
    
    /**
     * ICE processing has failed.
     */
    FAILED;
    }
