package org.lastbamboo.common.rudp;

/**
 * An implementation of the RaceRunnable interface.
 */
public final class RaceRunnableImpl implements RaceRunnable
    {
    /**
     * The number of times this runnable object was run before the delegate was
     * set.
     */
    private int m_runCount;
    
    /**
     * The delegate.  This can only be set once.
     */
    private Runnable m_delegate;
    
    /**
     * Constructs a new RaceRunnable.
     */
    public RaceRunnableImpl
            ()
        {
        m_runCount = 0;
        m_delegate = null;
        }
    
    /**
     * {@inheritDoc}
     */
    public void run
            ()
        {
        if (m_delegate == null)
            {
            synchronized (this)
                {
                ++m_runCount;
                }
            }
        else
            {
            m_delegate.run ();
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public void setDelegate
            (final Runnable delegate)
        {
        if (m_delegate == null)
            {
            m_delegate = delegate;
        
            synchronized (this)
                {
                for (int i = 0; i < m_runCount; ++i)
            		{
            		m_delegate.run ();
            		}
        	
        		m_runCount = 0;
                }
            }
        else
            {
            throw new RuntimeException ("Delegate should only be set once");
            }
        }
    }
