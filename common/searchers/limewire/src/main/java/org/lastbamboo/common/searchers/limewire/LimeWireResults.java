package org.lastbamboo.common.searchers.limewire;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.lastbamboo.common.rest.RestResultSources;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestResultsMetadataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Results from LimeWire.
 */
public class LimeWireResults implements RestResults<LimeWireJsonResult>
    {

    private final Logger m_log = 
        LoggerFactory.getLogger(LimeWireResults.class);
    private volatile List<LimeWireJsonResult> m_results =
        new LinkedList<LimeWireJsonResult>();
    private final LimeWireSearcher m_limeWireSearcher;
    
    private Timer m_timer = new Timer("LimeWire-Results-Timer", true);
    
    private volatile boolean m_complete = false;
    
    private TimerTask m_timerTask = new TimerTask()
        {
        @Override
        public void run()
            {
            m_complete = true;
            }
        };
    private final long m_startTime = System.currentTimeMillis();
    
    private static final long SEARCH_TIME = 1000 * 35L;
    private static final long EXTENSION_INTERVAL = 1000 * 14L;
    private static final long HARD_CAP = 1000 * 50L;
    private final boolean m_active;;
    
    /**
     * Creates a new set of results.
     * 
     * @param limeWireSearcher The search class.
     * @param active Whether or not LimeWire search is active. 
     */
    public LimeWireResults(final LimeWireSearcher limeWireSearcher, 
        final boolean active)
        {
        this.m_limeWireSearcher = limeWireSearcher;
        this.m_active = active;
            
        this.m_timer.schedule(m_timerTask, SEARCH_TIME);
        }

    public RestResultsMetadata<LimeWireJsonResult> getMetadata()
        {
        return new RestResultsMetadataImpl<LimeWireJsonResult>(
            this.m_results.size(), RestResultSources.LIMEWIRE, 
            this.m_limeWireSearcher);
        }

    public void addResult(final LimeWireJsonResult result)
        {
        m_log.debug("Start size: {}", this.m_results.size());
        final long totalTime = System.currentTimeMillis() - this.m_startTime;
        if (totalTime > HARD_CAP)
            {
            m_complete = true;
            this.m_timer.cancel();
            this.m_timerTask.cancel();
            }
        else if (totalTime > (SEARCH_TIME-EXTENSION_INTERVAL))
            {
            this.m_complete = false;
            this.m_timer.cancel();
            this.m_timerTask.cancel();
            this.m_timer = new Timer("LimeWire-Results-Timer", true);
            this.m_timerTask = new TimerTask()
                {
                @Override
                public void run()
                    {
                    m_complete = true;
                    }
                };
            this.m_timer.schedule(m_timerTask, EXTENSION_INTERVAL);
            }
        
        this.m_results.add(result);
        m_log.debug("End size: "+this.m_results.size());
        sort();
        }

    /**
     * Notifies this class results should be sorted again.
     */
    public void sort()
        {
        synchronized (this.m_results)
            {
            Collections.sort(this.m_results);
            }
        }

    public void addResults(final Collection<LimeWireJsonResult> results)
        {
        // We just ignore this for LimeWire results as they're all stored in
        // the same collection.
        }

    public Collection<LimeWireJsonResult> getCurrentResults()
        {
        return this.m_results;
        }

    public boolean hasMoreResults()
        {
        // We'll process results as they come in naturally -- not what is
        // meant by "more" here that would indicate we should send another
        // request to a more traditional REST API.
        return false;
        }   

    public boolean isComplete()
        {
        if (!this.m_limeWireSearcher.isEnabled() || !this.m_active)
            {
            m_log.debug("LimeWire disabled -- returning complete true");
            return true;
            }
        m_log.debug("Using normal completion check");
        return this.m_complete;
        }
    
    @Override 
    public String toString()
        {
        return getClass().getSimpleName() + " with results: " + this.m_results;
        }
    }
