package org.lastbamboo.common.rudp;

/**
 * The interface to a runnable object that delegates to a delegate runner.  This
 * runnable object remembers calls made before the delegate was set and replays
 * them to the delegate when it is set.  This is handy for dealing with certain
 * race conditions.
 */
public interface RaceRunnable extends Runnable
    {
    /**
     * Sets the delegate.
     * 
     * @param delegate
     *      The delegate.
     */
    void setDelegate
            (Runnable delegate);
    }
