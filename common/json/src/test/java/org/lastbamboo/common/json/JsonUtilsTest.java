package org.lastbamboo.common.json;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.Collection;

import org.junit.Test;


public class JsonUtilsTest
    {

    @Test public void testServers() throws Exception
        {
        final String json = "{\"servers\":[" +
        	"{\"port\":1,\"address\":\"localhost\"}," +
        	"{\"port\":2,\"address\":\"localhost\"}," +
        	"{\"port\":3,\"address\":\"ec2-67-202-41-97.compute-1.amazonaws.com\"}," +
        	"{\"port\":4,\"address\":\"localhost\"}," +
        	"{\"port\":5,\"address\":\"localhost\"}]}";
        
        final Collection<InetSocketAddress> servers = JsonUtils.getInetAddresses(json);
        assertEquals("Unexpected number of servers.", 5, servers.size());
        
        int port = 1;
        for (final InetSocketAddress server : servers)
            {
            assertEquals(port, server.getPort());
            port++;
            }
        }
    }
