package org.lastbamboo.integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.id.uuid.UUID;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lastbamboo.common.http.client.HttpClientGetRequester;
import org.lastbamboo.common.ice.IceAgent;
import org.lastbamboo.common.ice.IceStunConnectivityCheckerFactoryImpl;
import org.littleshoot.stun.stack.encoder.StunMessageEncoder;
import org.littleshoot.stun.stack.message.StunMessage;
import org.littleshoot.stun.stack.message.StunMessageType;
import org.littleshoot.stun.stack.message.StunMessageVisitorFactory;
import org.littleshoot.stun.stack.message.attributes.StunAttributeType;
import org.littleshoot.stun.stack.message.turn.AllocateSuccessResponse;
import org.littleshoot.stun.stack.message.turn.DataIndication;
import org.littleshoot.stun.stack.transaction.StunTransactionTracker;
import org.littleshoot.stun.stack.transaction.StunTransactionTrackerImpl;
import org.lastbamboo.common.tcp.frame.TcpFrame;
import org.lastbamboo.common.tcp.frame.TcpFrameCodecFactory;
import org.lastbamboo.common.tcp.frame.TcpFrameEncoder;
import org.lastbamboo.common.turn.client.StunTcpFrameTurnClientListener;
import org.lastbamboo.common.turn.client.TcpTurnClient;
import org.lastbamboo.common.turn.client.TurnClientListener;
import org.lastbamboo.common.turn.client.TurnStunDemuxableProtocolCodecFactory;
import org.lastbamboo.common.turn.http.server.ServerDataFeeder;
import org.littleshoot.util.CandidateProvider;
import org.littleshoot.util.ShootConstants;
import org.littleshoot.util.mina.ByteBufferUtils;
import org.littleshoot.util.mina.DemuxableProtocolCodecFactory;
import org.littleshoot.util.mina.DemuxingProtocolCodecFactory;
import org.littleshoot.util.mina.MinaUtils;
import org.lastbamboo.integration.tests.stubs.IceAgentStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test for the TURN client.  This creates a sort of fake TURN server that
 * sends lots of data to the TURN client.
 */
public class TcpTurnClientTest
    {

    private final static Logger LOG = 
        LoggerFactory.getLogger(TcpTurnClientTest.class);
    
    private static final int HTTP_SERVER_PORT = 57892;
    
    // This is the number of messages the fake TURN server simulates sending
    // to the client in Data Indications.
    private static final int NUM_MESSAGES = 10;
    
    private static final String HTTP_REQUEST_LINE = createRequestLine();
    
    private static final String HTTP_RESPONSE_LINE = createResponseLine();
    
    private final AtomicInteger m_httpRequestsReceivedOnServer = 
        new AtomicInteger(0);
    
    private final AtomicInteger m_turnServerHttpResponses = new AtomicInteger(0);
    
    private volatile boolean m_httpFailed = true;

    private volatile boolean m_closeServers = false;

    private ServerSocket m_httpServerSocket;
    
    @BeforeClass public static void start()
        {
        LOG.debug("Starting test...");
        }
    
    @AfterClass public static void afterTest() throws Exception
        {
        LOG.debug("Test complete!");
        }
    
    @After public void closeServers()
        {
        m_closeServers = true;
        if (this.m_httpServerSocket != null)
            {
            try
                {
                this.m_httpServerSocket.close();
                }
            catch (final IOException e)
                {
                e.printStackTrace();
                }
            }
        }
    
    private static String createResponseLine()
        {
        // Not real HTTP.
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++)
            {
            sb.append("HTTP/1.1 200 OK");
            }
        // This is what the test uses for parsing.
        sb.append("\r\n");
        return sb.toString();
        }

    private static String createRequestLine()
        {
        // Not real HTTP.
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++)
            {
            sb.append("GET /test HTTP/1.1");
            }
        // This is what the test uses for parsing.
        sb.append("\r\n");
        return sb.toString();
        }
    
    /**
     * Quick test to see how quickly we can allocate TURN clients on the
     * server.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    @Test public void testPublicServerConnection() throws Exception
        {
        
        final HttpClientGetRequester requester = new HttpClientGetRequester();
        final String response = 
            requester.request(ShootConstants.SERVER_URL + "/api/turnServer");
        LOG.debug("Received response: {}", response);
        final JSONObject json = new JSONObject(response);
        
        final JSONArray servers = json.getJSONArray("servers");
        final JSONObject server = servers.getJSONObject(0);

        final InetAddress address = InetAddress.getByName(server.getString("address"));
        final int port = Integer.parseInt(server.getString("port"));
        
        final InetSocketAddress turnServerAddress =
            new InetSocketAddress(address, port);
        
        final TcpTurnClient client = createTurnClient(turnServerAddress);
        client.connect();
        assertTrue("Could not connect to: ", client.isConnected());
        }
    
    @Test public void testTurnServerToDataServer() throws Exception
        {
        startThreadedHttpServer();
        startThreadedTurnServer();
        Thread.sleep(200);
        
        final InetSocketAddress turnServerAddress = 
            new InetSocketAddress("127.0.0.1", 3478);

        final TcpTurnClient client = createTurnClient(turnServerAddress);
        client.connect();
        assertTrue("Client could not connect!!", client.isConnected());
        
        synchronized (m_httpRequestsReceivedOnServer)
            {
            if (m_httpRequestsReceivedOnServer.get() < NUM_MESSAGES)
                {
                LOG.debug("Waiting for requests");
                m_httpRequestsReceivedOnServer.wait(16000);
                }
            }
        
        assertEquals(NUM_MESSAGES, m_httpRequestsReceivedOnServer.get());
        
        // Now make sure the TURN server got the HTTP response wrapped in a
        // Send Indication message and TCP framed within the Send Indication.
        synchronized (m_turnServerHttpResponses)
            {
            if (!(this.m_turnServerHttpResponses.get() == NUM_MESSAGES))
                {
                m_turnServerHttpResponses.wait(6000);
                }
            }
        
        assertEquals(NUM_MESSAGES, this.m_turnServerHttpResponses.get());
        assertFalse(this.m_httpFailed);
        }
    
    
    private static TcpTurnClient createTurnClient(
        final InetSocketAddress turnServerAddress)
        {
        final IceAgent iceAgent = new IceAgentStub();
        
        final StunTransactionTracker<StunMessage> transactionTracker =
            new StunTransactionTrackerImpl();
        final StunMessageVisitorFactory messageVisitorFactory =
            new IceStunConnectivityCheckerFactoryImpl<StunMessage>(iceAgent, 
                transactionTracker, null);
        final InetSocketAddress httpServerAddress =
            new InetSocketAddress("127.0.0.1", HTTP_SERVER_PORT);
        final ServerDataFeeder delegateListener =
            new ServerDataFeeder(httpServerAddress);
        
        // This class just decodes the TCP frames.
        final TurnClientListener turnClientListener =
            new StunTcpFrameTurnClientListener(messageVisitorFactory, 
                delegateListener);
    
        final DemuxableProtocolCodecFactory tcpFramingCodecFactory =
            new TcpFrameCodecFactory();
        
        final TurnStunDemuxableProtocolCodecFactory mapper = 
            new TurnStunDemuxableProtocolCodecFactory();
        final ProtocolCodecFactory codecFactory = 
            new DemuxingProtocolCodecFactory(mapper, 
                tcpFramingCodecFactory);
        
        final CandidateProvider<InetSocketAddress> candidateProvider =
            new CandidateProvider<InetSocketAddress>()
            {
            public Collection<InetSocketAddress> getCandidates()
                {
                final Collection<InetSocketAddress> candidates =
                    new LinkedList<InetSocketAddress>();
                candidates.add(turnServerAddress);
                return candidates;
                }

            public InetSocketAddress getCandidate()
                {
                return turnServerAddress;
                }
            };
        final TcpTurnClient client = 
            new TcpTurnClient(turnClientListener, candidateProvider, 
                codecFactory);
        return client;
        }

    private void startThreadedHttpServer()
        {
        final Runnable serverRunner = new Runnable()
            {
            public void run()
                {
                try
                    {
                    startHttpServer();
                    }
                catch (final Exception e)
                    {
                    // Should generally be OK.
                    e.printStackTrace();
                    }
                }
            };
            
        final Thread serverThread = 
            new Thread(serverRunner, "Test-HTTP-Thread");
        serverThread.setDaemon(true);
        serverThread.start();
        }
    
    private void startThreadedTurnServer()
        {
        final Runnable serverRunner = new Runnable()
            {
            public void run()
                {
                try
                    {
                    startTurnServer();
                    }
                catch (final Exception e)
                    {
                    LOG.error("Could not start server", e);
                    fail("Server errror: "+e.getMessage());
                    }
                }
            };
            
        final Thread serverThread = 
            new Thread(serverRunner, "Test-TURN-Server-Thread");
        serverThread.setDaemon(true);
        serverThread.start();
        }
    
    private void startTurnServer() throws Exception
        {
        final ServerSocket server = new ServerSocket(3478);
        
        // We basically just accept the single TURN client socket and send
        // it a successful allocate response before sending lots of data
        // down the connection.
        LOG.debug("About to wait...");
        final Socket client = server.accept();
        LOG.debug("Got socket...");
        final InputStream is = client.getInputStream();
        final byte[] header = new byte[20];
        final int read = is.read(header);
        assertEquals(20, read);
        final ByteBuffer allocateRequestBuffer = ByteBuffer.allocate(20);
        allocateRequestBuffer.put(header);
        allocateRequestBuffer.flip();
        final int messageTypeInt = allocateRequestBuffer.getUnsignedShort();
        final StunMessageType messageType = 
            StunMessageType.toType(messageTypeInt);
        assertEquals(StunMessageType.ALLOCATE_REQUEST, messageType);
        final int messageLength = allocateRequestBuffer.getUnsignedShort();
        assertEquals(0, messageLength);
        final byte[] transactionId = new byte[16];
        allocateRequestBuffer.get(transactionId);
        LOG.debug("Got trans ID");
        
        final OutputStream os = client.getOutputStream();
        final InetSocketAddress randomRelayAddress = 
            new InetSocketAddress(42314);
        final AllocateSuccessResponse sar = 
            new AllocateSuccessResponse(new UUID(transactionId), 
                randomRelayAddress, 
                (InetSocketAddress)client.getRemoteSocketAddress());
        final StunMessageEncoder encoder = new StunMessageEncoder();
        final ByteBuffer encodedResponse = encoder.encode(sar);
        os.write(MinaUtils.toByteArray(encodedResponse));
        os.flush();
        
        // Now write lots of requests and read lots of responses.
        final Collection<InetSocketAddress> remoteAddresses = 
            createRemoteAddresses();
        for (final InetSocketAddress remoteAddress : remoteAddresses)
            {
            writeHttpRequest(os, remoteAddress);
            }
        
        final Map<InetSocketAddress, Collection<ByteBuffer>> addressesToBufs = 
            readHttpResponses(is);
        
        for (final InetSocketAddress remoteAddress : remoteAddresses)
            {
            final Collection<ByteBuffer> bufs = 
                addressesToBufs.get(remoteAddress);
            assertFalse("No data for address: "+remoteAddress, bufs == null);
            readHttpResponse(addressesToBufs.get(remoteAddress), remoteAddress);
            }
        server.close();
        }

    private Map<InetSocketAddress, Collection<ByteBuffer>> readHttpResponses(
        final InputStream is) throws IOException
        {
        int messagesRead = 0;
        int httpBytesRead = 0;
        final Map<InetSocketAddress, Collection<ByteBuffer>> addressesToBufs = 
            new HashMap<InetSocketAddress, Collection<ByteBuffer>>();
        while (httpBytesRead < (this.HTTP_RESPONSE_LINE.length() * NUM_MESSAGES))
            {
            final ByteBuffer lengthBuf = ByteBuffer.allocate(4);
            while(lengthBuf.hasRemaining())
                {
                MinaUtils.putUnsignedByte(lengthBuf, is.read());
                }
            lengthBuf.flip();
            final int siType = lengthBuf.getUnsignedShort();
            final StunMessageType messageType = StunMessageType.toType(siType);
            assertEquals(StunMessageType.SEND_INDICATION, messageType);
            
            // Add 16 for the transaction ID.
            final int siLength = lengthBuf.getUnsignedShort() + 16;
            final ByteBuffer siBuf = ByteBuffer.allocate(siLength);
            LOG.debug("Reading Send Indication with length: "+siLength);
            while (siBuf.hasRemaining())
                {
                MinaUtils.putUnsignedByte(siBuf, is.read());
                }
            siBuf.flip();
            
            // Skip the transaction id.
            siBuf.skip(16);
            
            InetSocketAddress remoteAddress = null;
            ByteBuffer data = null;
            while (siBuf.hasRemaining())
                {
                final int typeInt = siBuf.getUnsignedShort();
                final StunAttributeType type = StunAttributeType.toType(typeInt);
                final int dataLength = siBuf.getUnsignedShort();
                
                if (type == StunAttributeType.DATA)
                    {
                    LOG.debug("Reading data attribute");
                    // Add the bytes to the HTTP data.
                    final byte[] dataBytes = new byte[dataLength];
                    siBuf.get(dataBytes);
                    httpBytesRead += dataBytes.length;
                    data = ByteBuffer.wrap(dataBytes, 2, dataBytes.length-2);
                    if (remoteAddress != null)
                        {
                        addData(addressesToBufs, remoteAddress, data);
                        }
                    }
                else if (type == StunAttributeType.REMOTE_ADDRESS)
                    {
                    LOG.debug("Reading remote address attribute");
                    // Read the address family.
                    siBuf.getUnsignedShort();
                    // Read the port.
                    final int port = siBuf.getUnsignedShort();
                    final byte[] addressBytes = new byte[4];
                    siBuf.get(addressBytes);
                    
                    final InetAddress address = 
                        InetAddress.getByAddress(addressBytes);
                    remoteAddress = 
                        new InetSocketAddress(address, port);
                    if (data != null)
                        {
                        addData(addressesToBufs, remoteAddress, data);
                        }
                    }
                else
                    {
                    // Just skip it.
                    siBuf.skip(dataLength);
                    }
                }
            messagesRead++;
            }
        
        LOG.debug("Read HTTP bytes -- total: "+httpBytesRead);
        LOG.debug("Read total HTTP messages "+messagesRead);
        return addressesToBufs;
        }
    

    private void addData(
        final Map<InetSocketAddress, Collection<ByteBuffer>> addressesToBufs, 
        final InetSocketAddress remoteAddress, final ByteBuffer data)
        {
        LOG.debug("Adding data of length: "+data.remaining());
        if (addressesToBufs.containsKey(remoteAddress))
            {
            final Collection<ByteBuffer> bufs = 
                addressesToBufs.get(remoteAddress);
            bufs.add(data);
            }
        else
            {
            final Collection<ByteBuffer> bufs = 
                new LinkedList<ByteBuffer>();
            bufs.add(data);
            addressesToBufs.put(remoteAddress, bufs);
            }
        }

    private Collection<InetSocketAddress> createRemoteAddresses()
        {
        final Collection<InetSocketAddress> addresses = 
            new LinkedList<InetSocketAddress>();
        for (int i=0;i< NUM_MESSAGES; i++)
            {
            addresses.add(newAddress(i));
            }
        return addresses;
        }

    private InetSocketAddress newAddress(final int i)
        {
        // Just use some random addresses and port.
        final String addressBase = "47.2.97.";
        final int portBase = 2794;
        final String address = addressBase + (34 + i);
        final int port = portBase + i;
        final InetSocketAddress remoteAddress = 
            new InetSocketAddress(address, port);
        return remoteAddress;
        }

    private void writeHttpRequest(final OutputStream os, 
        final InetSocketAddress remoteAddress) throws Exception
        {
        // Now just write dummy data as if it were from a remote host.
        final TcpFrame frame = 
            new TcpFrame(HTTP_REQUEST_LINE.getBytes("US-ASCII"));
        final TcpFrameEncoder tcpFrameEncoder = new TcpFrameEncoder();
        
        final ByteBuffer encodedFrame = tcpFrameEncoder.encode(frame);
        
        // Now write a wrapped HTTP request.
        final DataIndication indication = 
            new DataIndication(remoteAddress, 
                MinaUtils.toByteArray(encodedFrame));
        final StunMessageEncoder stunEncoder = new StunMessageEncoder();
        final ByteBuffer indicationBuf = stunEncoder.encode(indication);
        os.write(MinaUtils.toByteArray(indicationBuf));
        os.flush();
        LOG.debug("Flushed HTTP request");
        }
    

    private void readHttpResponse(final Collection<ByteBuffer> bufs,
        final InetSocketAddress remoteAddress) throws Exception
        {
        final ByteBuffer combined = ByteBufferUtils.combine(bufs);
        LOG.debug("Combined "+bufs.size()+" into combined buffer with "+
              combined.remaining());
        while (combined.hasRemaining())
            {
            readHttpResponse(combined, remoteAddress);
            }
        }
    
    private void readHttpResponse(final ByteBuffer buf, 
        final InetSocketAddress remoteAddress) throws Exception
        {
        LOG.debug("Reading HTTP response...");
        int numMessages = 0;
        while (buf.hasRemaining())
            {
            final byte[] responseBody = 
                new byte[HTTP_RESPONSE_LINE.length()];
            buf.get(responseBody);
            m_httpFailed = checkHttpResponse(responseBody);
            numMessages++;
            m_turnServerHttpResponses.incrementAndGet();
            }
        
        synchronized (m_turnServerHttpResponses)
            {
            if (m_turnServerHttpResponses.get() == NUM_MESSAGES)
                {
                m_turnServerHttpResponses.notify();
                }
            }
        }

    private boolean checkRemoteAddress(final InetSocketAddress remoteAddress, 
        final byte[] body) throws Exception
        {
        final ByteBuffer buf = ByteBuffer.wrap(body);
        final int family = buf.getUnsignedShort();
        assertEquals("Expected IPv4", 0x01, family);
        final int port = buf.getUnsignedShort();
        assertEquals("Unexpected port in remote address", 
            remoteAddress.getPort(), port);
        
        final byte[] addressBytes = new byte[4];
        buf.get(addressBytes);
        final InetAddress address = InetAddress.getByAddress(addressBytes);
        assertEquals("Unexpected address in remote address", 
            remoteAddress.getAddress(), address);
        return !remoteAddress.getAddress().equals(address);
        }

    private boolean checkHttpResponse(final byte[] body) throws Exception
        {
        // The body will contain the TCP framing, so we have to look inside
        // of the frame to get the HTTP itself.
        
        // Make sure the frame specified the correct length.
        //final ByteBuffer buf = ByteBuffer.wrap(body);
        //final int length = buf.getUnsignedShort();
        
        //We need to unwrap everything from the TCP frames too!!
        //assertEquals(body.length - 2, length);
        
        //final byte[] httpBody = new byte[length]; 
        //System.arraycopy(body, 2, httpBody, 0, httpBody.length);
        final String httpResponse = new String(body, "US-ASCII");
        assertEquals("Unexpected HTTP response line", this.HTTP_RESPONSE_LINE, httpResponse);
        LOG.debug("Got HTTP response!!!");
        return !this.HTTP_RESPONSE_LINE.equals(httpResponse);
        }

    private void startHttpServer() throws IOException
        {
        this.m_httpServerSocket = new ServerSocket(HTTP_SERVER_PORT);
        while (!this.m_closeServers)
            {
            final Socket client = m_httpServerSocket.accept();
            serveHttpSocket(client);
            }
        m_httpServerSocket.close();
        }

    private void serveHttpSocket(final Socket client)
        {
        final Runnable runner = new Runnable()
            {
            public void run()
                {
                try
                    {
                    serve(client);
                    }
                catch (Exception e)
                    {
                    // We'll pick up this error elsewhere.
                    e.printStackTrace();
                    }
                }

            private void serve(final Socket clientSocket) throws Exception
                {
                final InputStream is = clientSocket.getInputStream();
                final Scanner scan = new Scanner(is);
                scan.useDelimiter("\r\n");
                
                while (scan.hasNext())
                    {
                    final String request = scan.next();
                    LOG.debug("Got request: "+request);
                    assertEquals(HTTP_REQUEST_LINE.trim(), request);
                    synchronized (m_httpRequestsReceivedOnServer)
                        {
                        m_httpRequestsReceivedOnServer.incrementAndGet();
                        if (m_httpRequestsReceivedOnServer.get() == NUM_MESSAGES)
                            {
                            m_httpRequestsReceivedOnServer.notify();
                            }
                        }
                    
                    // Now, write the HTTP response and make sure our fake TURN 
                    // server gets it in a Send Indication!!  Note it doesn't 
                    // matter at all that this is HTTP, hence the bare minimum 
                    // messages here.
                    final OutputStream os = clientSocket.getOutputStream();
                    
                    os.write(HTTP_RESPONSE_LINE.getBytes("US-ASCII"));
                    os.flush();
                    }
                }
            };
        final Thread thread = new Thread(runner, "HTTP-Client-Thread");
        thread.setDaemon(true);
        thread.start();
        }
    }
