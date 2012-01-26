package org.lastbamboo.server.services;

import org.lastbamboo.common.jmx.client.JmxDomainHandlerImpl;
import org.lastbamboo.common.jmx.client.JmxMonitor;
import org.lastbamboo.common.sip.proxy.SipProxyImpl;
import org.lastbamboo.common.sip.proxy.SipProxyImplMBean;
import org.lastbamboo.common.sip.proxy.SipRegistrarImpl;
import org.lastbamboo.common.sip.proxy.SipRegistrarImplMBean;
import org.lastbamboo.common.turn.server.TcpTurnServer;
import org.lastbamboo.common.turn.server.TcpTurnServerMBean;
import org.lastbamboo.common.turn.server.TurnClientManagerImpl;
import org.lastbamboo.common.turn.server.TurnClientManagerImplMBean;

/**
 * Class that configures what JMX will monitor. 
 */
public class JmxMonitorConfig
    {

    /**
     * Creates a new monitor service.
     * 
     * @param jmxMonitor The class that does the monitoring.
     */
    public JmxMonitorConfig(final JmxMonitor jmxMonitor)
        {
        // Now add all the classes to monitor.
        jmxMonitor.addJmxDomainHandler(
            new JmxDomainHandlerImpl<TurnClientManagerImpl, TurnClientManagerImplMBean>(
                TurnClientManagerImpl.class, TurnClientManagerImplMBean.class));
        jmxMonitor.addJmxDomainHandler(
            new JmxDomainHandlerImpl<TcpTurnServer, TcpTurnServerMBean>(
                TcpTurnServer.class, TcpTurnServerMBean.class));
        
        jmxMonitor.addJmxDomainHandler(
            new JmxDomainHandlerImpl<SipRegistrarImpl, SipRegistrarImplMBean>(
                SipRegistrarImpl.class, SipRegistrarImplMBean.class));
        jmxMonitor.addJmxDomainHandler(
            new JmxDomainHandlerImpl<SipProxyImpl, SipProxyImplMBean>(
                SipProxyImpl.class, SipProxyImplMBean.class));
        jmxMonitor.monitor();
        }
    }
