package org.lastbamboo.common.ice;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.lastbamboo.common.offer.answer.OfferAnswerListener;
import org.lastbamboo.common.stun.server.StunServer;
import org.littleshoot.mina.common.IoAcceptor;
import org.littleshoot.mina.common.IoService;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.transport.socket.nio.support.DatagramSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import udt.UDTClient;
import udt.UDTReceiver;
import udt.UDTServerSocket;
import udt.UDTSocket;

/**
 * Factory for creating UDT sockets.
 */
public class UdtSocketFactory implements UdpSocketFactory
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final ExecutorService m_threadPool = 
        Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(final Runnable r) {
                final Thread t = new Thread(r, "UDT-Socket-Accept-Thread");
                t.setDaemon(true);
                return t;
            }
        });
    
    public void newSocket(final IoSession session, final boolean controlling,
        final OfferAnswerListener socketListener, 
        final IceStunUdpPeer stunUdpPeer) 
        {
        if (session == null)
            {
            m_log.error("Null session: {}", session);
            return;
            }
        
        UDTReceiver.connectionExpiryDisabled = true;
        clear(session, stunUdpPeer);
        if (!controlling)
            {
            // The CONTROLLED agent is notified to start the media stream first
            // in the ICE process, so this is called before the other side
            // starts sending media. We have to consider this in terms of
            // making sure we wait until the other side is ready.
            m_log.debug(
                "Creating UDT client socket on CONTROLLED agent.");
            final Runnable clientRunner = new Runnable()
                {
                public void run()
                    {
                    try
                        {
                        //openClientSocket(session, socketListener);
                        openServerSocket(session, socketListener);
                        }
                    catch (final Throwable t)
                        {
                        m_log.error("Client socket exception", t);
                        }
                    }
                };
    
            final Thread udtClientThread = 
                new Thread(clientRunner, "UDT Client Thread");
            udtClientThread.setDaemon(true);
            udtClientThread.start();
            }
        else
            {
            // This actually happens second in the ICE process -- the
            // controlled agent is notified to start sending media first!
            m_log.debug(
                "Creating UDT server socket on CONTROLLING agent.");
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
                        //openServerSocket(session, socketListener);
                        openClientSocket(session, socketListener);
                        }
                    catch (final Throwable t)
                        {
                        m_log.error("Server socket exception", t);
                        }
                    }
                };
            final Thread serverThread = 
                new Thread (serverRunner, "UDT Accepting Thread");
            serverThread.setDaemon(true);
            serverThread.start();
            }
        }

    protected void openClientSocket(final IoSession session,
        final OfferAnswerListener socketListener) 
        throws InterruptedException, IOException 
        {
        final InetSocketAddress local = 
            (InetSocketAddress) session.getLocalAddress();
        final InetSocketAddress remote = 
            (InetSocketAddress) session.getRemoteAddress();

        m_log.info("Session local was: {}", local);
        m_log.info("Binding to port: {}", local.getPort());
        
        final UDTClient client = 
            new UDTClient(local.getAddress(), local.getPort());

        // Wait for a bit to make sure the server side has a chance to come up.
        final long sleepTime = 700;
        m_log.info("Client side sleeping for {} milliseconds", sleepTime);
        Thread.sleep(sleepTime);
        m_log.info("About to connect...");
        client.connect(remote.getAddress(), remote.getPort());
        m_log.info("Connected!!!");
        
        final Socket sock = client.getSocket();
        m_log.info("Got socket...notifying listener");
        
        
        socketListener.onUdpSocket(sock);
        m_log.info("Exiting...");
        }
    
    protected void openServerSocket(final IoSession session,
        final OfferAnswerListener socketListener) 
        throws InterruptedException, IOException 
        {
        final InetSocketAddress local = 
            (InetSocketAddress) session.getLocalAddress();

        m_log.info("Session local was: {}", local);
        m_log.info("Binding to port: {}", local.getPort());
        final UDTServerSocket server = 
            new UDTServerSocket(local.getAddress(), local.getPort());
        
        final UDTSocket sock = server.accept();
        m_threadPool.execute(new RequestRunner(socketListener, sock));
        }
    
    public static class RequestRunner implements Runnable {

        private final Logger m_log = LoggerFactory.getLogger(getClass());
        private final UDTSocket sock;
        private final OfferAnswerListener socketListener;

        public RequestRunner(final OfferAnswerListener socketListener, 
            final UDTSocket sock) {
            this.socketListener = socketListener;
            this.sock = sock;
        }

        public void run() {
            m_log.info("NOTIFYING SOCKET LISTENER!!");
            socketListener.onUdpSocket(sock);
        }
    }
    
    private void clear(final IoSession session, 
        final IceStunUdpPeer stunUdpPeer) 
        {
        m_log.info("Clearing session!!");
        final DatagramSessionImpl dgSession = (DatagramSessionImpl)session;
        final DatagramChannel dgChannel = dgSession.getChannel();
        final DatagramSocket dgSock = dgChannel.socket();
        m_log.info("Closing socket on local address: {}", 
            dgSock.getLocalSocketAddress());
        session.close().join(10 * 1000);
        
        final StunServer stunServer = stunUdpPeer.getStunServer();
        stunServer.close();
        try
            {
            final IoService service = session.getService();
            m_log.info("Service is: {}", service);
            if (IoAcceptor.class.isAssignableFrom(service.getClass()))
                {
                m_log.info("Unbinding all!!");
                final IoAcceptor acceptor = (IoAcceptor) service;
                acceptor.unbindAll();
                }
            session.getService().getFilterChain().clear();
            dgChannel.disconnect();
            dgChannel.close();
            }
        catch (final Exception e)
            {
            m_log.error("Error clearing session!!", e);
            }
        }
    }
