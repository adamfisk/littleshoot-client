package org.lastbamboo.common.sip.bootstrap;

import org.littleshoot.util.Optional;

/**
 * A interface to an object robustly handles registrations by maintaining
 * registrations to several registrars.
 */
public interface RobustProxyRegistrar extends ProxyRegistrar
    {
    /**
     * Returns the URI of the registrar that was most recently active.  If no
     * registrar has been active yet, a <code>None</code> object is returned.
     * Otherwise, a <code>Some</code> object is returned whose contents is the
     * URI of the registrar.
     *
     * @return The URI that was most recently active.
     */
    Optional mostRecentlyActive();
    }
