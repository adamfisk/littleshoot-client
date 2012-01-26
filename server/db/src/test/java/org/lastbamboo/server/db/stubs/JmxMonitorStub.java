package org.lastbamboo.server.db.stubs;

import java.net.InetSocketAddress;
import java.util.Map;

import org.lastbamboo.common.jmx.client.JmxDomainHandler;
import org.lastbamboo.common.jmx.client.JmxMonitor;
import org.lastbamboo.common.jmx.client.ServerStatusListener;

public class JmxMonitorStub implements JmxMonitor
    {

    public void addJmxDomainHandler(JmxDomainHandler domainHandler)
        {
        // TODO Auto-generated method stub

        }

    public void addListener(ServerStatusListener listener)
        {
        // TODO Auto-generated method stub

        }

    public Map<InetSocketAddress, Map<String, String>> getServerData()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void monitor()
        {
        // TODO Auto-generated method stub
        
        }

    }
