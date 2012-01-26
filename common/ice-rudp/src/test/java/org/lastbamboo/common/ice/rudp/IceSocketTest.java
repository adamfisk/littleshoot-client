package org.lastbamboo.common.ice.rudp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lastbamboo.common.ice.IceMediaStreamDesc;
import org.lastbamboo.common.ice.IceMediaStreamFactoryImpl;
import org.lastbamboo.common.ice.IceOfferAnswerFactory;
import org.lastbamboo.common.ice.MappedTcpAnswererServer;
import org.lastbamboo.common.ice.NatPmpServiceStub;
import org.lastbamboo.common.ice.UdpSocketFactory;
import org.lastbamboo.common.ice.UpnpServiceStub;
import org.lastbamboo.common.offer.answer.OfferAnswer;
import org.lastbamboo.common.offer.answer.OfferAnswerFactory;
import org.lastbamboo.common.offer.answer.OfferAnswerListener;
import org.lastbamboo.common.portmapping.NatPmpService;
import org.lastbamboo.common.portmapping.UpnpService;
import org.lastbamboo.common.rudp.RudpService;
import org.lastbamboo.common.rudp.RudpServiceImpl;
import org.lastbamboo.common.turn.client.TurnClientListener;
import org.lastbamboo.common.turn.http.server.ServerDataFeeder;
import org.littleshoot.util.CandidateProvider;
import org.littleshoot.util.DnsSrvCandidateProvider;
import org.littleshoot.util.NetworkUtils;
import org.littleshoot.util.RelayingSocketHandler;
import org.littleshoot.util.ShootConstants;
import org.littleshoot.util.SocketListener;
import org.littleshoot.mina.common.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test connections between ICE agents.
 */
public class IceSocketTest
    {

    private static final Logger m_log = 
        LoggerFactory.getLogger(IceSocketTest.class);
    private static final AtomicReference<ServerSocket> m_localServer =
        new AtomicReference<ServerSocket>();
    private static volatile boolean m_dataServerClosed = false;
    private OfferAnswer m_offerer;
    private OfferAnswer m_answerer;
    private byte[] m_offer;
    
    @BeforeClass public static void startTestServer() throws Exception
        {
        m_log.info("About to start local server");
        startLocalServer();
        m_log.info("Started local server!!");
        
        synchronized (m_localServer)
            {
            if (m_localServer.get() == null)
                {
                m_localServer.wait(12000);
                }
            }
        m_log.info("Local server definitely started!!");
        
        Assert.assertFalse("Local server is null.", m_localServer.get() == null);
        }
    
    public static void closeServer()
        {
        m_dataServerClosed = true;
        if (m_localServer.get() != null)
            {
            try
                {
                m_localServer.get().close();
                }
            catch (IOException e)
                {
                m_log.warn("Could not close server");
                }
            }
        }
    
    public void closeIce() throws Exception 
        {
        if (this.m_offerer != null) m_offerer.close();
        if (this.m_answerer != null) m_answerer.close();
        m_offerer = null;
        m_answerer = null;
        }
    
    @Test public void testLocalTcpConnection() throws Exception
        {
        final IceMediaStreamDesc desc = 
            new IceMediaStreamDesc(true, false, "message", "http", 1, false);
        
        final AtomicReference<Socket> offererSocket =
            new AtomicReference<Socket>(null);
        final AtomicReference<Socket> answererSocket =
            new AtomicReference<Socket>(null);
        final OfferAnswerListener offererListener = new OfferAnswerListener()
            {

            public void onOfferAnswerFailed(final OfferAnswer mediaOfferAnswer)
                {
                m_log.debug("Offerer failed!!");
                synchronized (offererSocket)
                    {
                    offererSocket.notifyAll();
                    }
                }

            public void onTcpSocket(final Socket sock)
                {
                m_log.debug("Offerer completed!!");
                synchronized (offererSocket)
                    {
                    final Socket existing = offererSocket.get();
                    if (existing == null)
                        {
                        m_log.info("Setting socket to: {}", sock);
                        offererSocket.set(sock);
                        offererSocket.notifyAll();
                        }
                    else
                        {
                        m_log.info("Ignoring/closing socket. Existing: {} New "+sock, 
                            existing);
                        try
                            {
                            sock.close();
                            }
                        catch (IOException e)
                            {
                            }
                        }
                    }
                }

            public void onUdpSocket(final Socket sock)
                {
                throw new RuntimeException("Received UDP SOCKET -- BADD!!");
                }
            };
            
        final OfferAnswerListener answererListener = new OfferAnswerListener()
            {
            
            public void onOfferAnswerFailed(final OfferAnswer mediaOfferAnswer)
                {
                m_log.debug("Answerer failed!!");
                synchronized (answererSocket)
                    {
                    answererSocket.notifyAll();
                    }
                }

            public void onTcpSocket(final Socket sock)
                {
                m_log.debug("Answerer completed!!!");
                answererSocket.set(sock);
                synchronized (answererSocket)
                    {
                    echo(sock);
                    answererSocket.notifyAll();
                    }
                }

            public void onUdpSocket(final Socket sock)
                {
                throw new RuntimeException("Received UDP SOCKET -- BADD!!");
                }
            };
        
        establishTest(desc, offererListener, answererListener);
        
        final Thread answerThread = new Thread(new Runnable()
            {
            public void run()
                {
                m_log.debug("Telling m_answerer to process offer: {}", 
                    new String(m_offer));
                m_answerer.processOffer(ByteBuffer.wrap(m_offer));
                }
            }, "AnswerThread");
        
        answerThread.setDaemon(true);
        answerThread.start();
        
        final byte[] answer = m_answerer.generateAnswer();
        
        m_log.debug("Generated answer: {}", new String(answer));
        
        m_log.debug("Telling offerer to process answer: {}",new String(answer));
        m_offerer.processAnswer(ByteBuffer.wrap(answer));
        
        synchronized (offererSocket)
            {
            if (offererSocket.get() == null)
                {
                offererSocket.wait(6000);
                }
            }
        
        synchronized (answererSocket)
            {
            if (answererSocket.get() == null)
                {
                answererSocket.wait(6000);
                }
            }

        final Socket sock1 = offererSocket.get();
        Assert.assertNotNull("Offerer socket is null", sock1);
        
        m_log.debug("Running quick socket test...");
        quickSocketTest(sock1);
        }
    
    /**
     * Tests creating a local UDP connection using ICE.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    @Test public void testLocalRudpUdpConnection() throws Exception
        {
        
        final IceMediaStreamDesc desc = 
            new IceMediaStreamDesc(false, true, "message", "http", 1, false);
        
        final AtomicReference<Socket> offererSocket =
            new AtomicReference<Socket>();
        final AtomicReference<Socket> answererSocket =
            new AtomicReference<Socket>();
        
        final OfferAnswerListener offererListener = new OfferAnswerListener()
            {
            public void onOfferAnswerFailed(final OfferAnswer mediaOfferAnswer)
                {
                m_log.debug("Offerer failed!!");
                synchronized (offererSocket)
                    {
                    offererSocket.notifyAll();
                    }
                }
            public void onTcpSocket(Socket sock)
                {
                throw new RuntimeException("TCP socket??");
                }
            
            public void onUdpSocket(Socket sock)
                {
                m_log.debug("Offerer completed!!");
                synchronized (offererSocket)
                    {
                    final Socket existing = offererSocket.get();
                    if (existing == null)
                        {
                        m_log.info("Setting socket to: {}", sock);
                        offererSocket.set(sock);
                        offererSocket.notifyAll();
                        }
                    else
                        {
                        throw new RuntimeException("Received second socket??");
                        }
                    }
                }
            };
            
        final OfferAnswerListener answererListener = new OfferAnswerListener()
            {
            
            public void onOfferAnswerFailed(final OfferAnswer mediaOfferAnswer)
                {
                m_log.debug("Answerer failed!!");
                synchronized (answererSocket)
                    {
                    answererSocket.notifyAll();
                    }
                }

            public void onTcpSocket(final Socket sock)
                {
                throw new RuntimeException("Received TCP SOCKET -- BADD!!");
                }

            public void onUdpSocket(final Socket sock)
                {
                m_log.debug("Answerer completed!!!");
                answererSocket.set(sock);
                synchronized (answererSocket)
                    {
                    echo(sock);
                    answererSocket.notifyAll();
                    }
                }
            };
        
        establishTest(desc, offererListener, answererListener);
        m_log.info("Established test!!");
        
        //m_log.debug("Generated answer: {}", new String(answer));
        
        //final AtomicBoolean threadFailed = new AtomicBoolean(false);
        final Thread answerThread = new Thread(new Runnable()
            {
            public void run()
                {
                m_log.debug("Telling m_answerer to process offer: {}", 
                    new String(m_offer));
                m_answerer.processOffer(ByteBuffer.wrap(m_offer));
                }
            });
        
        answerThread.setDaemon(true);
        answerThread.start();
        
        final byte[] answer = m_answerer.generateAnswer();
        m_log.debug("Telling offerer to process answer: {}",new String(answer));
        
        
        // Sleep for a second to simulate network latency and to avoid the 
        // degenerate case where both agents continually get checks on 
        // pairs in the in progress state, forcing them to cancel those checks
        // and to add triggered checks.
        Thread.sleep(200);
        m_offerer.processAnswer(ByteBuffer.wrap(answer));
        
        m_log.debug("About to wait for sockets...");
        synchronized (offererSocket)
            {
            if (offererSocket.get() == null)
                {
                offererSocket.wait(12000);
                }
            }
        
        synchronized (answererSocket)
            {
            if (answererSocket.get() == null)
                {
                answererSocket.wait(12000);
                }
            }
        
        Assert.assertNotNull("Offerer socket is null", offererSocket.get());
        Assert.assertNotNull("Answerer socket is null", answererSocket.get());

        final Socket sock1 = offererSocket.get();
        quickSocketTest(sock1);
        }
    
    private static final byte[] outputBytes = "TEST THIS BABY OUT".getBytes();
    
    private void quickSocketTest(final Socket offerer) throws IOException
        {
        final InputStream is1 = offerer.getInputStream();
        final OutputStream os1 = offerer.getOutputStream();
        
        os1.write(outputBytes);
        os1.flush();

        m_log.info("About to read on CLIENT SOCKET...");
        final byte[] inputBytes = new byte[outputBytes.length];
        is1.read(inputBytes);
        m_log.info("Read: "+new String(inputBytes));
        assertTrue("Socket did not transmit data", 
            Arrays.equals(outputBytes, inputBytes));
        
        m_log.info("Closing offerer");
        offerer.close();
        }
    
    private static void echo(final Socket answerer)
        {
        m_log.info("Echoing...");
        final Runnable runner = new Runnable()
            {
            public void run()
                {
                try
                    {
                    final InputStream answererIs = answerer.getInputStream();
                    final OutputStream answererOs = answerer.getOutputStream();
                    final byte[] inputBytes = new byte[outputBytes.length];
                    m_log.info("About to READ ON ECHO SERVER");
                    answererIs.read(inputBytes);
                    m_log.info("FINISHED READ ON ECHO SERVER. READ: "+
                        new String(inputBytes));
                    
                    //checkArrays(outputBytes, inputBytes);
                    //assertTrue("Socket did not transmit data", 
                    //    Arrays.equals(outputBytes, inputBytes));
                    //Arrays.fill(inputBytes, (byte)0);
                    answererOs.write(outputBytes);
                    m_log.info("FINISHED WRITE ON ECHO SERVER");
                    }
                catch (final IOException e)
                    {
                    e.printStackTrace();
                    }
                }
            };
        
        final Thread thread = new Thread(runner, "AnswerEchoThread");
        thread.setDaemon(true);
        thread.start();
        }
    
    private void checkArrays(final byte[] array1, final byte[] array2)
        {
        if (!Arrays.equals(array1, array2))
            {
            throw new RuntimeException("Arrays not equal." +
                "\nArray 1:\n"+arrayToString(array1)+
                "\nArray 2:\n"+arrayToString(array2));
            }
        }

    private String arrayToString(final byte[] array)
        {
        final StringBuilder sb = new StringBuilder();
        for (final byte b : array)
            {
            sb.append(b);
            }
        return sb.toString();
        }

    private void establishTest(final IceMediaStreamDesc desc, 
        final OfferAnswerListener offererListener, 
        final OfferAnswerListener answererListener) throws Exception
        {
        final RudpService rudpService1 = new RudpServiceImpl();
        final RudpService rudpService2 = new RudpServiceImpl();
    
        final CandidateProvider<InetSocketAddress> stunCandidateProvider =
            new DnsSrvCandidateProvider("_stun._udp.littleshoot.org");
        
        final CandidateProvider<InetSocketAddress> turnCandidateProvider =
            new DnsSrvCandidateProvider("_turn._tcp.littleshoot.org");
        
        final IceMediaStreamFactoryImpl mediaStreamFactory1 =
            new IceMediaStreamFactoryImpl(desc,
                stunCandidateProvider);
        final IceMediaStreamFactoryImpl mediaStreamFactory2 =
            new IceMediaStreamFactoryImpl(desc,
                stunCandidateProvider);
        
        final UdpSocketFactory socketFactory1 =
            new DefaultUdpSocketFactory(rudpService1);
        final UdpSocketFactory socketFactory2 =
            new DefaultUdpSocketFactory(rudpService2);
        
        final NatPmpService natPmpService = new NatPmpServiceStub();
        final UpnpService upnpService = new UpnpServiceStub();
        
        final SocketListener socketListener = new SocketListener() {
            
            public void onSocket(final Socket sock) throws IOException {
                final SocketListener sl = 
                    new RelayingSocketHandler(NetworkUtils.getLocalHost(), 
                        ShootConstants.HTTP_PORT);
                sl.onSocket(sock);
            }
        };
        final MappedTcpAnswererServer answererServer =
            new MappedTcpAnswererServer(natPmpService, upnpService, 
                socketListener);
        
        final InetAddress localServerAddress;
        try
            {
            localServerAddress = NetworkUtils.getLocalHost();
            }
        catch (final UnknownHostException e)
            {
            m_log.warn("Could not get local host!!", e);
            throw new RuntimeException("Could not get local host?!?", e);
            }
        final InetSocketAddress httpServerAddress =
            new InetSocketAddress(localServerAddress, ShootConstants.HTTP_PORT);
        
        final TurnClientListener clientListener =
            new ServerDataFeeder(httpServerAddress);
            
        final OfferAnswerFactory factory1 = 
            new IceOfferAnswerFactory(mediaStreamFactory1, socketFactory1, desc,
                turnCandidateProvider, natPmpService, upnpService, 
                    answererServer, clientListener);
        
        final OfferAnswerFactory factory2 = 
            new IceOfferAnswerFactory(mediaStreamFactory2, socketFactory2, desc,
                turnCandidateProvider, natPmpService, upnpService, 
                    answererServer, clientListener);
            
        m_offerer = factory1.createOfferer(offererListener);
        m_offer = m_offerer.generateOffer();
        m_log.info("OFFER: "+new String(m_offer));
    
        m_answerer = factory2.createAnswerer(answererListener);
        
        m_log.info("About to tell answerer to process the initial offer");
        //m_answerer.processOffer(ByteBuffer.wrap(m_offer));
        //m_log.info("Done processing the initial offer");
        assertFalse(m_answerer == null);
        }

    private static void startLocalServer()
        {
        final Runnable runner = new Runnable()
            {
            public void run()
                {
                try
                    {
                    runDataServer();
                    }
                catch (final Throwable t)
                    {
                    m_log.info("Caught throwable while running data server", t);
                    }
                }
            };
        final Thread turnThread = 
            new Thread(runner, "TURN-Test-Data-Server-Thread");
        turnThread.setDaemon(true);
        turnThread.start();
        }
    
    private static void runDataServer() throws Exception
        {
        final ServerSocket server = new ServerSocket(ShootConstants.HTTP_PORT); 
        m_localServer.set(server);
        synchronized (m_localServer)
            {
            m_localServer.notifyAll();
            }
        while (!m_dataServerClosed)
            {
            final Socket sock = m_localServer.get().accept();
            m_log.debug("Accepted one socket");
            echo(sock);
            }
        m_log.info("Closing data sserver");
        m_localServer.get().close();
        }
    }
