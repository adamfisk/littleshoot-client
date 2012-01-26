package org.lastbamboo.common.ice;

/**
 * Interface for classes that schedule checks. 
 */
public interface IceCheckScheduler
    {

    /**
     * Schedules checks.
     */
    void scheduleChecks();

    /**
     * Notifies the scheduler that a pair has been added.  This is useful in
     * certain cases where checks may have stopped, and the scheduler needs to
     * know to go back to work.
     */
    void onPair();

    }
