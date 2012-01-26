package org.lastbamboo.common.ice.rudp;

import java.net.Socket;

import org.lastbamboo.common.ice.IceStunUdpPeer;
import org.lastbamboo.common.ice.UdpSocketFactory;
import org.lastbamboo.common.offer.answer.OfferAnswerListener;
import org.lastbamboo.common.rudp.RudpConnectionId;
import org.lastbamboo.common.rudp.RudpListener;
import org.lastbamboo.common.rudp.RudpService;
import org.littleshoot.util.Future;
import org.littleshoot.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUdpSocketFactory implements UdpSocketFactory
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final RudpService m_rudpService;
    
    public DefaultUdpSocketFactory(final RudpService rudpService)
        {
        this.m_rudpService = rudpService;
        }
    
    public void newSocket(final IoSession session, final boolean controlling,
        final OfferAnswerListener socketListener, 
        final IceStunUdpPeer stunUdpPeer)
        {
        if (session == null)
            {
            m_log.error("Null session: {}", session);
            return;
            }
        
        // We make the controlling ICE agent the RUDP "client" because
        // it acts on the nominated pair only after sending the
        // controlled agent the nomination message, so the controlled
        // agent is able to start RUDP before the controlling
        // agent, and we want the server side to start first.
        if (!controlling)
            {
            m_log.debug(
                "Creating RUDP client socket on CONTROLLED agent.");
            final Runnable clientRunner = new Runnable()
                {
                public void run()
                    {
                    try
                        {
                        openClientSocket(m_rudpService, session, 
                            socketListener);
                        }
                    catch (final Throwable t)
                        {
                        m_log.error("Client socket exception", t);
                        }
                    }
                };

            final Thread rudpClientThread = 
                new Thread(clientRunner, "RUDP Client Thread");
            rudpClientThread.setDaemon(true);
            rudpClientThread.start();
            }
        else
            {
            m_log.debug(
                "Creating RUDP server socket on CONTROLLING agent.");
            m_log.debug("Listening on: {}", session);
            
            // If we call "accept" right away here, we'll kill the
            // IoSession thread and won't receive messages, so we 
            // need to start a new thread.
            final Runnable serverRunner = new Runnable ()
                {
                public void run()
                    {
                    try
                        {
                        openServerSocket(m_rudpService, session,
                            socketListener);
                        }
                    catch (final Throwable t)
                        {
                        m_log.error("Server socket exception", t);
                        }
                    }
                };
            final Thread serverThread = 
                new Thread (serverRunner, "RUDP Accepting Thread");
            serverThread.setDaemon(true);
            serverThread.start();
            }
        }
    

    private void openClientSocket(final RudpService rudpService, 
        final IoSession session, final OfferAnswerListener socketListener)
        {
        m_log.debug("Opening client socket...");
        // We sleep for a bit to give the server side a 
        // chance to come up.  If the client comes up 
        // before the server, the connection will fail.
        final long sleepTime = 1200;
        m_log.debug("About to sleep for: {}", sleepTime);
        try
            {
            Thread.sleep(sleepTime);
            }
        catch (final InterruptedException e)
            {
            m_log.error("Sleep interrupted??", e);
            }
        m_log.debug("Opening connection...");
        final Future<RudpConnectionId> future = rudpService.open(session);
        m_log.debug("About to join...");
        future.join (20000);
        m_log.debug("Finished join...");
        openSocket(rudpService, future, session, socketListener);
        }

    private void openServerSocket(final RudpService rudpService, 
        final IoSession session, final OfferAnswerListener socketListener)
        {
        m_log.debug("Opening server socket...");
        final RudpListener listener = new RudpListener ()
            {
            };
        final Future<RudpConnectionId> future =
            rudpService.accept (session, listener);
        
        m_log.debug("Acceptor about to join...");
        future.join (20000);
        m_log.debug("Completed acceptor join...");
        openSocket(rudpService, future, session, socketListener);
        }


    private void openSocket(final RudpService rudpService, 
        final Future<RudpConnectionId> future, final IoSession session, 
        final OfferAnswerListener socketListener)
        {
        final Socket sock =  rudpService.newSocket(future, session);
        socketListener.onUdpSocket(sock);
        }

    }
