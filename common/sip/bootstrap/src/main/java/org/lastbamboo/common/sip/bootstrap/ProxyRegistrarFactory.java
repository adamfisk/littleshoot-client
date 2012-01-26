package org.lastbamboo.common.sip.bootstrap;

import java.net.URI;

import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;

/**
 * A factory for creating a proxy registrar.
 */
public interface ProxyRegistrarFactory
    {
    /**
     * Returns a proxy registrar. The returned registrar will handle
     * registration for a given client and proxy. A given listener will be
     * notified of registration events.
     * 
     * @param client The client for which the registrar will handle 
     * registration.
     * @param proxy The proxy with which the registrar will register.
     * @param listener The listener to notify of registration events.
     * @return A proxy registrar.
     */
    ProxyRegistrar getRegistrar(URI client, URI proxy,
        ProxyRegistrationListener listener);
    }
