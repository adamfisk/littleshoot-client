package org.lastbamboo.server.services;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.jmx.client.JmxMonitor;
import org.lastbamboo.common.services.JsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that monitors server status. 
 */
public class ServerMonitorService implements JsonProvider
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final JmxMonitor m_jmxMonitor;

    /**
     * Creates a new monitor service.
     * 
     * @param jmxMonitor The class that does the monitoring.
     */
    public ServerMonitorService(final JmxMonitor jmxMonitor)
        {
        this.m_jmxMonitor = jmxMonitor;
        }

    public String getJson()
        {
        final Map<InetSocketAddress, Map<String, String>> data = 
            this.m_jmxMonitor.getServerData();
        final Collection<JSONObject> jsonData = toJson(data);
        
        final JSONObject json = new JSONObject();
        try
            {
            json.put("servers", jsonData);
            }
        catch (final JSONException e)
            {
            m_log.error("Could not create json data", e);
            }
        
        return json.toString();
        }

    private Collection<JSONObject> toJson(
        final Map<InetSocketAddress, Map<String, String>> data)
        {
        final Collection<JSONObject> jsonData =
            new LinkedList<JSONObject>();
        for (final Map.Entry<InetSocketAddress, Map<String, String>> entry : 
            data.entrySet())
            {
            final JSONObject obj = new JSONObject();
            try
                {
                obj.put("address", entry.getKey().toString());
                final Map<String, String> value = entry.getValue();
                for (final Map.Entry<String, String> mapEntry : value.entrySet())
                    {
                    obj.put(mapEntry.getKey(), mapEntry.getValue());
                    }
                jsonData.add(obj);
                }
            catch (final JSONException e)
                {
                m_log.warn("JSON error on: "+entry, e);
                }
            }
        return jsonData;
        }
    }
