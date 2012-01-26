package org.lastbamboo.common.online;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.http.client.HttpClientPostRequester;
import org.lastbamboo.common.http.client.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updates the online status for a given user handling any potential errors
 * appropriately.
 */
public class RobustOnlineStatusUpdater implements OnlineStatusUpdater
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(RobustOnlineStatusUpdater.class);
    
    private final ExecutorService m_executor = 
        Executors.newCachedThreadPool();
    
    private final String m_apiUrl = 
        "http://littleshootapi.appspot.com/api/";

    private boolean m_updateActive = true;
    
    private final HttpClientPostRequester m_requester = 
        new HttpClientPostRequester();
    
    /**
     * We keep track of the what IDs are in the update queue.  If we get
     * a duplicate ID, we use the more recent status.  This is important 
     * because threading could otherwise alter the order updates are sent in,
     * potentially resulting in incorrect state.
     */
    private final Map<String, RobustHttpPostRequester> m_extantUpdaters =
        new ConcurrentHashMap<String, RobustHttpPostRequester>();

    private volatile boolean m_bulkUpdateActive;
    
    public void updateStatus(final String instanceId, final String baseUri, 
        final boolean online, final InetAddress serverAddress)
        {
        if (!this.m_updateActive)
            {
            LOG.warn("Update inactive.  Testing?");
            return;
            }
        
        if (this.m_bulkUpdateActive)
            {
            LOG.debug("Ignoring update with bulk update in progress");
            return;
            }
        final RobustHttpPostRequester runner = 
            updateStatusNoThread(instanceId, baseUri, online, serverAddress);
        m_executor.execute(runner);
        }

    private RobustHttpPostRequester updateStatusNoThread(final String instanceId,
        final String baseUri, final boolean online, 
        final InetAddress serverAddress)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Updating online status to: "+online+
                " for user: "+instanceId);
            }
        if (!this.m_updateActive)
            {
            LOG.warn("Update inactive.  Testing?");
            return null;
            }
        
        // First, check if there's an existing updater for that instance.  If
        // there is, cancel it and use the new update status.
        final RobustHttpPostRequester existingUpdater = 
            this.m_extantUpdaters.get(instanceId);
        if (existingUpdater != null)
            {
            LOG.debug("Canceling existing updater.");
            existingUpdater.cancel();
            m_extantUpdaters.remove(instanceId);
            }
        
        final Map<String, String> parameters = new HashMap<String, String>();
        
        parameters.put("instanceId", instanceId);
        parameters.put("baseUri", baseUri);
        parameters.put("online", Boolean.toString(online));
        parameters.put("serverAddress", serverAddress.getHostAddress());
        
        final String baseUrl = m_apiUrl + "instanceOnline";
        final RobustHttpPostRequester runner = 
            new RobustHttpPostRequester(instanceId, baseUrl, parameters, 6, 
                60 * 1000);
        this.m_extantUpdaters.put(instanceId, runner);
        return runner;
        }

    public void setUpdateActive(final boolean updateActive)
        {
        this.m_updateActive = updateActive;
        }
    
    public void allOffline(final InetAddress serverAddress)
        {
        LOG.debug("Handling all offline...");
        this.m_bulkUpdateActive = true;
        synchronized (this.m_extantUpdaters)
            {
            final Collection<RobustHttpPostRequester> updaters = 
                this.m_extantUpdaters.values();
            for (final RobustHttpPostRequester updater : updaters)
                {
                updater.cancel();
                }
            }
        
        // We first need to get the JSON data for online instances.
        final Map<String, String> params = new HashMap<String, String>();
        
        params.put("limit", String.valueOf(20));
        params.put("serverAddress", serverAddress.getHostAddress());
        
        final String baseUrl = m_apiUrl + "instancesForServer";
        
        final long sleepTime = 10000;
        int failures = 0;
        while (true)
            {
            try
                {
                final String jsonString = 
                    this.m_requester.request(baseUrl, params);
                try
                    {
                    final JSONObject json = new JSONObject(jsonString);
                    final boolean complete = json.getBoolean("complete");
                    if (complete)
                        {
                        LOG.debug("Got complete -- updated all instances!!");
                        break;
                        }
                    else
                        {
                        LOG.debug("Not complete -- updating more instances!!");
                        final JSONArray ids = json.getJSONArray("instanceIds");
                        final int length = ids.length();
                        
                        for (int i = 0; i < length; i++)
                            {
                            final String instanceId = ids.getString(i);
                            setInstanceOffline(instanceId, serverAddress);
                            }
                        final String nextInstanceId = 
                            json.getString("nextInstanceId");
                        params.put("instanceId", String.valueOf(nextInstanceId));
                        }
                    }
                catch (final JSONException e)
                    {
                    LOG.error("Could not parse JSON: "+jsonString, e);
                    break;
                    }
                }
            catch (final IOException e)
                {
                LOG.warn("IO error accessing server!", e);
                failures++;
                if (failures > 10)
                    {
                    break;
                    }
                try
                    {
                    Thread.sleep(sleepTime);
                    }
                catch (final InterruptedException e1)
                    {
                    LOG.error("Sleep error", e1);
                    }
                }
            catch (final ServiceUnavailableException e)
                {
                LOG.warn("ServiceUnavailableException accessing server!", e);
                failures++;
                if (failures > 10)
                    {
                    break;
                    }
                try
                    {
                    Thread.sleep(sleepTime);
                    }
                catch (final InterruptedException e1)
                    {
                    LOG.error("Sleep error", e1);
                    }
                }
            }
        m_bulkUpdateActive = false;
        }

    private void setInstanceOffline(final String instanceId, 
        final InetAddress serverAddress)
        {
        LOG.debug("Setting instance offline: {}", instanceId);
        // We don't thread this request because it's typically done in the
        // context of a shutdown hook.  Shutdown hooks exit with the thread
        // exits, so the program would exit prematurely if we passed this off
        // to another thread here.
        final String baseUri = "sip://" + instanceId;
        final RobustHttpPostRequester requester = 
            updateStatusNoThread(instanceId, baseUri, false, serverAddress);
        requester.run();
        }

    /**
     * Quick little interface to allow canceling.
     */
    private static interface Cancelable
        {
        void cancel();
        
        boolean isCanceled();
        };
    
    private final class RobustHttpPostRequester implements Runnable,
        Cancelable
        {

        private final String m_baseUrl;
        private final Map<String, String> m_parameters;
        private final int m_sleepInterval;
        private final int m_attempts;
        private volatile boolean m_cancelled = false;
        private final String m_instanceId;

        private RobustHttpPostRequester(final String instanceId, 
            final String baseUrl, final Map<String, String> parameters,
            final int attempts, final int sleepInterval)
            {
            this.m_instanceId = instanceId;
            m_baseUrl = baseUrl;
            m_parameters = parameters;
            m_attempts = attempts;
            m_sleepInterval = sleepInterval;
            }

        public void cancel()
            {
            this.m_cancelled = true;
            }
        
        public boolean isCanceled()
            {
            return this.m_cancelled;
            }

        public void run()
            {
            LOG.debug("Running threaded request...");
            request(this.m_instanceId, this.m_baseUrl, this.m_parameters, 
                this.m_attempts, this.m_sleepInterval, this);
            }
        }
    
    private void request(final String instanceId, final String baseUrl, 
        final Map<String, String> params,
        final int maxFailures, final long sleepInterval, 
        final Cancelable cancelable)
        {
        int failures = 0;
        while (failures < maxFailures)
            {
            if (cancelable.isCanceled())
                {
                LOG.debug("Request with params "+params+" is canceled.  " +
                    "Not requesting.");
                break;
                }
            try
                {
                LOG.debug("Sending update request");
                final String response = m_requester.request(baseUrl, params);
                try
                    {
                    final JSONObject json = new JSONObject(response);
                    final boolean complete = json.getBoolean("complete");
                    if (complete)
                        {
                        LOG.debug("Got complete!!");
                        break;
                        }
                    else
                        {
                        LOG.debug("Got a second start SHA-1...");
                        final String startSha1 = json.getString("nextSha1");
                        params.put("startSha1", startSha1);
                        continue;
                        }
                    }
                catch (final JSONException e)
                    {
                    LOG.error("Could not read JSON: "+response, e);
                    break;
                    }
                }
            catch (final HttpException e)
                {
                failures ++;
                LOG.warn("Could not update server", e);
                }
            catch (final IOException e)
                {
                failures ++;
                LOG.warn("Could not update server", e);
                }
            catch (final ServiceUnavailableException e)
                {
                failures ++;
                LOG.warn("Could not update server", e);
                }
            
            if (cancelable.isCanceled())
                {
                LOG.debug("Request with params "+params+" is canceled!!");
                break;
                }
            try
                {
                // TODO: This should ideally not hold up the thread here!!
                Thread.sleep(sleepInterval);
                }
            catch (final InterruptedException e)
                {
                LOG.error("Sleep interrupted?", e);
                }
            }
        
        m_extantUpdaters.remove(instanceId);
        LOG.debug("Updated server");
        }
    }
