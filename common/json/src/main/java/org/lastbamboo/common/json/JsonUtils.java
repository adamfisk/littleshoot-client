package org.lastbamboo.common.json;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON utilities methods.
 */
public class JsonUtils
    {
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(JsonUtils.class);
    
    private JsonUtils()
        {
        
        }

    /**
     * Puts the specified name/value pair in the specified JSON object.
     * 
     * @param json The JSON object to put data in.
     * @param name The name for the data.
     * @param value The value for the data.
     */
    public static void put(final JSONObject json, final String name, 
        final int value)
        {
        try
            {
            json.put(name, new Integer(value));
            }
        catch (final JSONException e)
            {
            LOG.warn("JSON error", e);
            }
        }
    
    /**
     * Puts the specified name/value pair in the specified JSON object.
     * 
     * @param json The JSON object to put data in.
     * @param name The name for the data.
     * @param value The value for the data.
     */
    public static void put(final JSONObject json, final String name, 
        final Long value)
        {
        try
            {
            json.put(name, value);
            }
        catch (final JSONException e)
            {
            LOG.warn("JSON error", e);
            }
        }

    /**
     * Puts the specified name/value pair in the specified JSON object.
     * 
     * @param json The JSON object to put data in.
     * @param name The name for the data.
     * @param value The value for the data.
     */
    public static void put(final JSONObject json, final String name, 
        final String value)
        {
        try
            {
            json.put(name, value);
            }
        catch (final JSONException e)
            {
            LOG.warn("JSON error", e);
            }
        }

    /**
     * Puts the specified name/value pair in the specified JSON object.
     * 
     * @param json The JSON object to put data in.
     * @param name The name for the data.
     * @param value The value for the data.
     */
    public static void put(final JSONObject json, final String name, 
        final boolean value)
        {
        try
            {
            json.put(name, value);
            }
        catch (final JSONException e)
            {
            LOG.warn("JSON error", e);
            }
        }

    /**
     * Puts the specified name/value pair in the specified JSON object.
     * 
     * @param json The JSON object to put data in.
     * @param name The name for the data.
     * @param value The value for the data.
     */
    public static void put(final JSONObject json, final String name, 
        final double value)
        {
        try
            {
            json.put(name, value);
            }
        catch (final JSONException e)
            {
            LOG.warn("JSON error", e);
            }
        }

    /**
     * Puts the specified name/value pair in the specified JSON object.
     * 
     * @param json The JSON object to put data in.
     * @param name The name for the data.
     * @param value The value for the data.
     */
    public static void put(final JSONObject json, final String name, 
        final Collection value)
        {
        try
            {
            json.put(name, value);
            }
        catch (final JSONException e)
            {
            LOG.warn("JSON error", e);
            }
        }
    
    /**
     * Puts the specified name/value pair in the specified JSON object.
     * 
     * @param json The JSON object to put data in.
     * @param name The name for the data.
     * @param value The value for the data.
     */
    public static void put(final JSONObject json, final String name, 
        final URI value)
        {
        if (value == null)
            {
            // Ignore nulls and check for them on the client side.
            return;
            }
        try
            {
            json.put(name, value);
            }
        catch (final JSONException e)
            {
            LOG.warn("JSON error", e);
            }
        }
    
    public static void put(final JSONObject json, final String name, 
        final File value)
        {
        if (value == null)
            {
            // Ignore nulls and check for them on the client side.
            return;
            }
        try
            {
            json.put(name, value);
            }
        catch (final JSONException e)
            {
            LOG.warn("JSON error", e);
            }
        }
    /**
     * Specialized method for accessing data of the form:
     * 
     * {"servers":[
     * {"port":3478,"address":"ec2-67-202-41-97.compute-1.amazonaws.com"},
     * {"port":3478,"address":"localhost"}
     * ]}
     * 
     * @param data The data to parse.
     * @return The data converted to {@link InetSocketAddress}es.
     */
    public static Collection<InetSocketAddress> getInetAddresses(
        final String data)
        {
        if (StringUtils.isBlank(data))
            {
            LOG.error("Bad data from server: " + data);
            return null;
            }
        
        final Collection<InetSocketAddress> addresses = 
            new LinkedList<InetSocketAddress>();
        try
            {
            final JSONObject json = new JSONObject(data);
            final JSONArray servers = json.getJSONArray("servers");
            final int length = servers.length();
            
            for (int i =0 ; i < length ; i++)
                {
                final JSONObject server = servers.getJSONObject(i);
                final String address = server.getString("address");
                if (StringUtils.isBlank(address))
                    {
                    LOG.warn("Got blank address");
                    continue;
                    }
                final int port = server.getInt("port");
                final InetSocketAddress isa =
                    new InetSocketAddress(address, port);
                addresses.add(isa);
                }
            }
        catch (final JSONException e)
            {
            LOG.error("Could not read JSON: "+data, e);
            }
        return addresses;
        }

    public static int extractInt(final JSONObject json, final String key)
        {
        try
            {
            return json.getInt(key);
            }
        catch (JSONException e)
            {
            return -1;
            } 
        }

    }

