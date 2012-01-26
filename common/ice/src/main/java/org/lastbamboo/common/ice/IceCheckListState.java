package org.lastbamboo.common.ice;

/**
 * Enumeration of states for ICE check lists. 
 */
public enum IceCheckListState
    {

    /**
     * We're still running through the pairs in the check list.
     */
    RUNNING,
    
    /**
     * The check list has completed all checks.
     */
    COMPLETED,
    
    /**
     * The check list failed to create a successful pair.
     */
    FAILED;
    
    }
