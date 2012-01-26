package org.lastbamboo.common.sip.httpclient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.lastbamboo.common.offer.answer.NoAnswerException;
import org.lastbamboo.common.p2p.SocketFactory;
import org.lastbamboo.common.sip.stack.SipUriFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Socket factory for creating SIP-negotiated sockets for HTTP clients.
 */
public final class SipProtocolSocketFactory implements ProtocolSocketFactory {

    /**
     * Logger for this class.
     */
    private final Logger m_log = 
        LoggerFactory.getLogger(SipProtocolSocketFactory.class);
    
    private final SocketFactory m_sipSocketFactory;

    private final SipUriFactory m_sipUriFactory;

    /**
     * Creates a new factory for creating sockets from SIP URIs.
     * 
     * @param sipSocketFactory The factory that actually creates the sockets.
     * @param sipUriFactory The class for creating SIP URIs from the host
     * names from HTTP client.
     */
    public SipProtocolSocketFactory(final SocketFactory sipSocketFactory,
            final SipUriFactory sipUriFactory) {
        this.m_sipSocketFactory = sipSocketFactory;
        this.m_sipUriFactory = sipUriFactory;
    }

    public Socket createSocket(final String host, final int port,
            final InetAddress clientHost, final int clientPort) {
        m_log.warn("Attempted unsupported socket call");
        throw new UnsupportedOperationException("not allowed");
    }

    public Socket createSocket(final String host, final int port,
            final InetAddress clientHost, final int clientPort,
            final HttpConnectionParams params) throws IOException,
            UnknownHostException, ConnectTimeoutException {
        m_log.trace("Creating a socket for user: {}", host);
        return createSocket(host, port);
    }
    
    public Socket createSocket(final String host, final int port)
            throws IOException, UnknownHostException {
        m_log.trace("Creating a socket for user: {}", host);
        final Preferences prefs = Preferences.userRoot();
        final long id = prefs.getLong("LITTLESHOOT_ID", -1);
        if (id == Long.parseLong(host)) {
            // This is an error because we should just stream it locally
            // if we have the file (we're trying to connect to ourselves!).
            m_log.error("Ignoring request to download from ourselves...");
            throw new IOException("Not downloading from ourselves...");
        }

        final URI sipUri = this.m_sipUriFactory.createSipUri(host);
        try {
            m_log.trace("About to create socket...");
            final Socket sock = this.m_sipSocketFactory.newSocket(sipUri);
            m_log.debug("Got socket!! Returning to HttpClient");

            // Note there can appear to be an odd delay after this point if
            // you're just looking at the raw logs, but it's due to HttpClient
            // actually making the HTTP request and getting a response.
            return sock;
        } catch (final NoAnswerException e) {
            m_log.warn("No answer!!", e);
            throw e;
        } catch (final IOException e) {
            m_log.warn("Exception creating SIP socket", e);
            throw e;
        }
    }
}
