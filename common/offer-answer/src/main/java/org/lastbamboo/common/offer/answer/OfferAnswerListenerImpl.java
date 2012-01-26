package org.lastbamboo.common.offer.answer;

import java.io.IOException;
import java.net.Socket;

import org.littleshoot.util.SocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that listens for creation of RUDP sockets on the server side.
 */
public class OfferAnswerListenerImpl implements OfferAnswerListener
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final SocketListener m_socketListener;

    /**
     * Creates a new listener for RUDP server sockets.  These get past along
     * to a socket processing class.
     */
    public OfferAnswerListenerImpl(final SocketListener socketListener)
        {
        this.m_socketListener = socketListener;
        }

    public void onOfferAnswerFailed(final OfferAnswer offerAnswer)
        {
        m_log.warn("Offer/Answer failed.  Not starting media for: {}",
            offerAnswer);
        }

    public void onTcpSocket(final Socket sock)
        {
        m_log.info("Received TCP socket");
        onSocket(sock);
        }
    
    public void onUdpSocket(final Socket sock)
        {
        m_log.info("Received UDP socket");
        onSocket(sock);
        }
    
    private void onSocket(final Socket sock)
        {
        m_log.info("Got socket on listener!!");
        // Set a timeout cap. We set this really high
        // because clients can request large content 
        // lengths. In that case, we won't read anything
        // from them for a long time since they're doing
        // all the reading. Jetty uses 200 seconds by
        // default.
        try
            {
            sock.setSoTimeout(40 * 60 * 1000);
            m_socketListener.onSocket(sock);
            }
        catch (final IOException e)
            {
            m_log.warn("Exception processing socket", e);
            try
                {
                sock.close();
                }
            catch (final IOException e1)
                {
                m_log.warn("Could not close socket", e1);
                }
            }
        }
    }
