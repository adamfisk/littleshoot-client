package org.lastbamboo.common.jmx.client;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Interface for classes that monitor the SIP and TURN servers. 
 */
public interface JmxMonitor
    {

    /**
     * Adds the specified listener.
     * 
     * @param listener The listener for server events.
     */
    void addListener(ServerStatusListener listener);

    /**
     * Accessor for data about all known servers.
     * 
     * @return Data about all known servers.
     */
    Map<InetSocketAddress, Map<String, String>> getServerData();
    
    /**
     * Adds a class for processing data from a specific domain, normally
     * the java package.
     * 
     * @param domainHandler The class for handling data from a specific domain.
     */
    void addJmxDomainHandler(JmxDomainHandler domainHandler);
    
    /**
     * Starts monitoring the servers.
     */
    void monitor();
    }
