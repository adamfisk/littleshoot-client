package org.lastbamboo.server.services;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Test monitor service utilities. 
 */
public class MonitorServiceUtilsTest
    {

    @Test public void testService() throws Exception
        {
        final Map<InetSocketAddress, Map<String, String>> data = 
            new HashMap<InetSocketAddress, Map<String,String>>();
        
        final String portKey = "sipPort";
        final Map<String, String> dataMap = new HashMap<String, String>();
        final int port = 792;
        dataMap.put("sipPort", String.valueOf(port));
        InetSocketAddress key = new InetSocketAddress("23.87.5.2", 391);
        data.put(key, dataMap);
        
        final JSONObject json = MoniterServiceUtils.toJson(data, portKey);
        
        final JSONArray servers = json.getJSONArray("servers");
        assertEquals(1, servers.length());
        final JSONObject server = servers.getJSONObject(0);
        assertEquals(port, server.getInt("port"));
        assertEquals(key.getAddress().getHostAddress(), server.get("address"));
        }
    }
