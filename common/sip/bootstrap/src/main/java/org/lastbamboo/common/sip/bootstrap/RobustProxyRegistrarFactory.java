package org.lastbamboo.common.sip.bootstrap;

import java.net.URI;

import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;


/**
 * A factory for creating a robust proxy registrar.
 */
public interface RobustProxyRegistrarFactory
    {
    /**
     * Returns a proxy registrar.  The returned registrar will handle
     * registration for a given client and proxy.  A given listener will be
     * notified of registration events.
     * 
     * @param client The client the registrar will handle registration for.
     * @param listener The listener to notify of registration events.
     * 
     * @return A proxy registrar.
     */
    RobustProxyRegistrar getRegistrar (URI client, 
        ProxyRegistrationListener listener);
    }
