package org.lastbamboo.server.services;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for handling monitor service data. 
 */
public class MoniterServiceUtils
    {
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(MoniterServiceUtils.class);

    /**
     * Extracts server addresses and ports from server monitor data.
     * @param servers The collection of server addresses.
     * @param port The port to use.
     * 
     * @return A {@link Collection} of {@link JSONObject}s with each object
     * containing an address and a port.
     */
    public static JSONObject toJson(final Collection<InetAddress> servers, final int port)
        {
        
        final List<JSONObject> jsonData = new LinkedList<JSONObject>();
        for (final InetAddress server : servers)
            {
            final JSONObject obj = new JSONObject();
            try
                {
                obj.put("address", server.getHostName());
                obj.put("port", port);
                jsonData.add(obj);
                }
            catch (final JSONException e)
                {
                LOG.warn("JSON error on: "+server, e);
                }
            }
        
        // Make sure we randomize the servers.
        Collections.shuffle(jsonData);
        final JSONObject json = new JSONObject();
        try
            {
            json.put("servers", jsonData);
            }
        catch (final JSONException e)
            {
            LOG.error("Could not create json data", e);
            }
        return json;
        }
    
    
    /**
     * Extracts server addresses and ports from server monitor data.
     * 
     * @param data The data to extract addresses and ports from.
     * @param portKey The key for looking up the port.
     * @return A {@link Collection} of {@link JSONObject}s with each object
     * containing an address and a port.
     */
    public static JSONObject toJson(
        final Map<InetSocketAddress, Map<String, String>> data, 
        final String portKey)
        {
        
        final List<JSONObject> jsonData = new LinkedList<JSONObject>();
        for (final Map.Entry<InetSocketAddress, Map<String, String>> entry : 
            data.entrySet())
            {
            final InetSocketAddress address = entry.getKey();
            final Map<String, String> value = entry.getValue();
            final String portString = value.get(portKey);
            
            // First check if it's running our service at all.
            if (StringUtils.isEmpty(portString))
                {
                LOG.debug("Server at {} not running SIP", address);
                continue;
                }
            if (!NumberUtils.isNumber(portString))
                {
                LOG.warn("Port is not a number: {}", portString);
                continue;
                }
            final JSONObject obj = new JSONObject();
            try
                {
                obj.put("address", address.getAddress().getHostAddress());
                obj.put("port", Integer.parseInt(portString));
                jsonData.add(obj);
                }
            catch (final JSONException e)
                {
                LOG.warn("JSON error on: "+entry, e);
                }
            }
        
        // Make sure we randomize the servers.
        Collections.shuffle(jsonData);
        final JSONObject json = new JSONObject();
        try
            {
            json.put("servers", jsonData);
            }
        catch (final JSONException e)
            {
            LOG.error("Could not create json data", e);
            }
        return json;
        }
    }
