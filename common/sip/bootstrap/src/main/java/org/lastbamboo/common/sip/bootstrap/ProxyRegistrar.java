package org.lastbamboo.common.sip.bootstrap;

/**
 * A interface to an object that handles registration of a client to a proxy.
 */
public interface ProxyRegistrar
    {
    /**
     * Registers a client with a proxy. This is expected to be a non-blocking
     * call. Generally, proxy registrars should be supplied with the
     * information necessary for registration during construction. This
     * includes the client to register, the proxy to register with, and a
     * listener to be notified of registration events.
     */
    void register ();
    }
