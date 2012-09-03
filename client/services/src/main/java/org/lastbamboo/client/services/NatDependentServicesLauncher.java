package org.lastbamboo.client.services;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;

import javax.security.auth.login.CredentialException;

import org.lastbamboo.client.prefs.PrefKeys;
import org.lastbamboo.client.prefs.Prefs;
import org.lastbamboo.common.p2p.P2PClient;
import org.littleshoot.util.NetworkUtils;
import org.littleshoot.util.ShootConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class launches any services that depend upon the client's local NAT
 * configuration. If the client is not behind any firewall or NAT, for
 * example, it can act as a server for various network service protocols, such
 * as TURN and SIP. If the client is behind a firewall or NAT that blocks all
 * UDP traffic, it can start a TURN client.  For full-cone NATs and
 * port-restricted cone NATs, this might only start SIP client services.
 */
public final class NatDependentServicesLauncher {

    /**
     * Logger for this class.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final P2PClient p2pClient;

    /**
     * Creates a new class for launching any services that depend on the type
     * of NAT we're behind. If the client is not behind any NAT or firewall,
     * this launches services such as a TURN server and a SIP proxy server. If
     * the client is behind a symmetric NAT, this might start specialized
     * address/port resolution services designed to penetrate symmetric NATS,
     * including the simplest case of just using TURN. This will become a TURN
     * client in other cases where the local configuration does not allow
     * incoming UDP packets.
     * 
     * @param sipClientLauncher The class that launches a SIP client.
     */
    public NatDependentServicesLauncher(final P2PClient sipClientLauncher) {
        p2pClient = sipClientLauncher;
        startClients();
    }

    private void startSipClient() {
        log.debug("Starting SIP client...");
        try {
            this.p2pClient.login(Long.toString(Prefs.getId()), "");
        } catch (final IOException e) {
            log.error("Error logging in!", e);
        } catch (final CredentialException e) {
            log.error("Error logging in!", e);
        }
    }

    private void startClients() {
        // All clients are connected via SIP.
        startSipClient();
        final InetAddress localHost;
        try {
            localHost = NetworkUtils.getLocalHost();
        } catch (final UnknownHostException e) {
            log.error("Could not determine local host", e);
            return;
        }

        // If we're on the public Internet, we handle our address differently,
        // allowing other clients to connect directly rather than negotiate
        // connections using ICE.
        if (NetworkUtils.isPublicAddress(localHost)) {
            setOpenInternetBaseUri(localHost);
        }
    }

    private void setOpenInternetBaseUri(final InetAddress localHost) {
        final Preferences prefs = Preferences.userRoot();
        final String host = localHost.getHostAddress();
        final String baseUri = "http://" + host + ":" + ShootConstants.API_PORT;
        log.debug("Setting base URI to: " + baseUri);
        prefs.put(PrefKeys.BASE_URI, baseUri);
    }
}
