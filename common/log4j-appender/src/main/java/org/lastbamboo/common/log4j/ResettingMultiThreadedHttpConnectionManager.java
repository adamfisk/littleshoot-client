package org.lastbamboo.common.log4j;

import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a connection manager that just resets HTTP and HTTPS protocol
 * handling back to the default.  This is an odd case, but useful when 
 * your code includes other libraries that set custom HTTP protocol
 * handlers.  You often don't want to use those custom handlers, but
 * you don't want to affect the integrity of the third-party library
 * through resetting the protocol handlers globally.  This successfully
 * overrides those settings without external effects.
 */
public class ResettingMultiThreadedHttpConnectionManager
    extends MultiThreadedHttpConnectionManager
    {
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private static final Protocol HTTP_PROT = 
        new Protocol("http", new DefaultProtocolSocketFactory(), 80);
    
    private static final Protocol HTTPS_PROT = 
        new Protocol("https", 
            (ProtocolSocketFactory)new SSLProtocolSocketFactory(), 443);
    
    @Override
    public HttpConnection getConnection(final HostConfiguration hc)
        {
        m_log.debug("Getting connection...");
        useDefaultProtocols(hc);
        final HttpConnection conn = super.getConnection(hc);
        m_log.debug("Returning connection...");
        return conn;
        }
    
    @Override
    public HttpConnection getConnectionWithTimeout(
        final HostConfiguration hc, final long timeout) 
        throws ConnectionPoolTimeoutException 
        {
        m_log.debug("Getting connection...");
        useDefaultProtocols(hc);
        final HttpConnection conn = super.getConnectionWithTimeout(hc, timeout);
        m_log.debug("Returning connection...");
        return conn;
        }

    private void useDefaultProtocols(final HostConfiguration hc)
        {
        final Protocol p = hc.getProtocol();
        if (p == HTTP_PROT || p == HTTPS_PROT) 
            {
            m_log.debug("Protocol already set...");
            return;
            }
        final String scheme = p == null ? "" : p.getScheme();
        
        if (scheme.equalsIgnoreCase("http"))
            {
            final HttpHost host = 
                new HttpHost(hc.getHost(), hc.getPort(), HTTP_PROT);
            hc.setHost(host);
            }
        else if (scheme.equalsIgnoreCase("https"))
            {
            final HttpHost host = 
                new HttpHost(hc.getHost(), hc.getPort(), HTTPS_PROT);
            hc.setHost(host);
            }
        }
    }