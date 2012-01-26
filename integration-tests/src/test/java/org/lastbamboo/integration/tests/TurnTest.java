package org.lastbamboo.integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lastbamboo.common.amazon.ec2.AmazonEc2Utils;
import org.lastbamboo.common.ice.IceAgent;
import org.lastbamboo.common.ice.IceStunConnectivityCheckerFactoryImpl;
import org.littleshoot.stun.stack.message.StunMessage;
import org.littleshoot.stun.stack.message.StunMessageVisitorFactory;
import org.littleshoot.stun.stack.transaction.StunTransactionTracker;
import org.littleshoot.stun.stack.transaction.StunTransactionTrackerImpl;
import org.lastbamboo.common.tcp.frame.TcpFrame;
import org.lastbamboo.common.tcp.frame.TcpFrameCodecFactory;
import org.lastbamboo.common.tcp.frame.TcpFrameEncoder;
import org.lastbamboo.common.turn.client.StunTcpFrameTurnClientListener;
import org.lastbamboo.common.turn.client.TcpTurnClient;
import org.lastbamboo.common.turn.client.TurnClient;
import org.lastbamboo.common.turn.client.TurnClientListener;
import org.lastbamboo.common.turn.client.TurnStunDemuxableProtocolCodecFactory;
import org.lastbamboo.common.turn.server.TcpTurnServer;
import org.littleshoot.util.CandidateProvider;
import org.littleshoot.util.NetworkUtils;
import org.littleshoot.util.RuntimeIoException;
import org.littleshoot.util.mina.ByteBufferUtils;
import org.littleshoot.util.mina.DemuxableProtocolCodecFactory;
import org.littleshoot.util.mina.DemuxingProtocolCodecFactory;
import org.littleshoot.util.mina.MinaUtils;
import org.lastbamboo.integration.tests.stubs.IceAgentStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration test for TURN.  This test works by setting up a TURN server,
 * a TURN client, and a remote host.  The TURN client forwards data to a local
 * "data" server that just returns back the same thing it gets.
 */
public class TurnTest
    {

    private final static Logger LOG = LoggerFactory.getLogger(TurnTest.class);
    
    /**
     * Use the default STUN port.
     */
    private static final int STUN_PORT = 3478;

    private static final int NUM_MESSAGES_PER_REMOTE_HOST = 3;

    /**
     * The number of remote hosts we create for each TURN client.
     */
    private static final int NUM_REMOTE_HOSTS_PER_CLIENT = 3;

    /**
     * The number of TURN clients connected to the TURN server to create.
     */
    private static final int NUM_TURN_CLIENTS = 2;
    
    private final AtomicBoolean m_turnServerStarted = new AtomicBoolean(false);
    
    private final AtomicInteger m_totalBytesRead = new AtomicInteger(0);
    private volatile String m_lotsOfNumbers;
    private TcpTurnServer m_turnServer;
    private final Map<TurnClient, Map<InetSocketAddress, Collection<ByteBuffer>>> m_clientsToAddressedBufs =
        new ConcurrentHashMap<TurnClient, Map<InetSocketAddress,Collection<ByteBuffer>>>();

    
    private volatile int m_bytesWithFramingSent;
    private volatile int m_bytesWithoutFramingSent;

    private final Map<TurnClient, AtomicInteger> m_clientsToBytesRead =
        new ConcurrentHashMap<TurnClient, AtomicInteger>();
    
    @BeforeClass public static void establishTest() throws Exception
        {
        LOG.debug("Starting test...");
        }
    
    @AfterClass public static void afterTest() throws Exception
        {
        LOG.debug("Test complete!");
        }
    
    /**
     * Tests all TURN handling.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    @Test public void testTurn() throws Exception
        {
        startTurnThreadedServer();
        final StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 20000; i++)
            {
            sb.append(i);
            sb.append("-");
            if (sb.length() % 20 == 0)
                {
                sb.append("\n");
                }
            }
        final String temp = sb.toString().trim();
        
        this.m_lotsOfNumbers = temp + "\r\n";
        final int MESSAGE_SIZE = m_lotsOfNumbers.length();
        
        assertTrue("Numbers not big enough to test Data Indication size limits", 
            MESSAGE_SIZE > 0xffff);
        LOG.debug("Message size: {}", MESSAGE_SIZE);
        
        synchronized (this)
            {
            if (!this.m_turnServerStarted.get())
                {
                wait(6000);
                }
            }
        assertTrue(this.m_turnServerStarted.get());
        
        final Collection<TurnClient> turnClients = 
            new LinkedList<TurnClient>();
        for (int i = 0; i < NUM_TURN_CLIENTS; i++)
            {
            final TurnClient client = createTurnClient();
            turnClients.add(client);
            }
        
        final Collection<Socket> remoteHosts = new LinkedList<Socket>();
        for (final TurnClient client : turnClients)
            {
            final Collection<Socket> rhs = createRemoteHosts(client);
            remoteHosts.addAll(rhs);
            }
        
        assertEquals(NUM_TURN_CLIENTS*NUM_REMOTE_HOSTS_PER_CLIENT, 
             remoteHosts.size());
        
        // Test first sending from all remote hosts and then reading
        // from all of them.
        int host = 0;
        m_bytesWithFramingSent = 0;
        for (final Socket remoteHost : remoteHosts)
            {
            LOG.debug("Sending data from remote host...");
            final OutputStream os = remoteHost.getOutputStream();
            final byte[] numberBytes = this.m_lotsOfNumbers.getBytes("US-ASCII");

            for (int i = 0; i < NUM_MESSAGES_PER_REMOTE_HOST; i++)
                { 
                LOG.debug("Writing message number: "+i+" from host: "+host);
                final Collection<byte[]> split = split(numberBytes);
                for (final byte[] curData : split)
                    {
                    final TcpFrame frame = new TcpFrame(curData);
                    final TcpFrameEncoder encoder = new TcpFrameEncoder();
                    final ByteBuffer encodedFrame = encoder.encode(frame);
                    final byte[] encodedBytes = 
                        MinaUtils.toByteArray(encodedFrame); 
                    os.write(encodedBytes);
                    m_bytesWithFramingSent += encodedBytes.length; 
                    m_bytesWithoutFramingSent += curData.length;
                    }
                }
            os.flush();
            host++;
            }
        
        LOG.debug("Remote hosts sent byte total: {}", m_bytesWithFramingSent);
        
        synchronized (this)
            {
            if (m_totalBytesRead.get() < m_bytesWithoutFramingSent)
                {
                wait(12000);
                }
            }
        
        // OK, first check to make sure each client received the expected 
        // raw number of bytes. 
        final Collection<AtomicInteger> dataReadPerClient = 
            this.m_clientsToBytesRead.values();
        
        final int expectedUnframedBytesPerClient = 
            NUM_MESSAGES_PER_REMOTE_HOST * NUM_REMOTE_HOSTS_PER_CLIENT * MESSAGE_SIZE;
        
        int cumulativeDataRead = 0;
        for (final AtomicInteger read : dataReadPerClient)
            {
            assertEquals(expectedUnframedBytesPerClient, read.get());
            cumulativeDataRead += read.get();
            }
        
        assertEquals(cumulativeDataRead, m_totalBytesRead.get());
        
        // Now make sure the overall number is correct.  If this fails and
        // the above passes, there could be something wrong with one of our
        // ByteBuffer utility methods.
        assertEquals(m_bytesWithoutFramingSent, m_totalBytesRead.get());
        
        // Now check that each TURN client sent the correct data to its 
        // listener.
        final Collection<Map<InetSocketAddress, Collection<ByteBuffer>>> 
            allData = this.m_clientsToAddressedBufs.values();
        
        final byte[] original = this.m_lotsOfNumbers.getBytes("US-ASCII");
        for (final Map<InetSocketAddress, Collection<ByteBuffer>> addressedData : allData)
            {
            // Make sure we received data from the expected number of hosts.
            assertEquals(NUM_REMOTE_HOSTS_PER_CLIENT, addressedData.keySet().size());
            
            // Now make sure the data is what we expected.
            final Collection<Collection<ByteBuffer>> allBufs = 
                addressedData.values();
            for (final Collection<ByteBuffer> data : allBufs)
                {
                // This data should already have the TCP frame stripped.
                final ByteBuffer combined = ByteBufferUtils.combine(data);
                
                for (int i = 0; i < NUM_MESSAGES_PER_REMOTE_HOST; i++)
                    {
                    //m_log.debug("Original has "+original.length+" while buf has "+combined.capacity());
                    //m_log.debug("Original: \n"+this.m_lotsOfNumbers);
                    //m_log.debug("Read:\n", MinaUtils.toAsciiString(combined));
                    final byte[] sliver = new byte[original.length];
                    combined.get(sliver);
                    final String sliverString = new String(sliver, "US-ASCII");
                    assertEquals(this.m_lotsOfNumbers, sliverString);
                    }
                }
            LOG.debug("One success!!!");
            }
        }
    
    /**
     * Splits the main read buffer into smaller buffers that will 
     * fit in TURN messages.
     * 
     * @param remoteHost The host the data came from.
     * @param buffer The main read buffer to split.
     * @param session The session for reading and writing data.
     * @param nextFilter The next class for processing the message.
     */
    private Collection<byte[]> split(final byte[] buffer)
        {
        LOG.debug("Sending split buffers!!");
        // Break up the data into smaller chunks.
        return MinaUtils.splitToByteArrays(ByteBuffer.wrap(buffer), 0x0fff-1000);
        }

    private Collection<Socket> createRemoteHosts(final TurnClient client) 
        throws Exception
        {
        final Collection<Socket> remoteHosts = new LinkedList<Socket>();
        for (int i = 0; i < NUM_REMOTE_HOSTS_PER_CLIENT; i++)
            {
            final Socket remoteHost = createRemoteHost(client);
            remoteHosts.add(remoteHost);
            }
        return remoteHosts;
        }

    private Socket createRemoteHost(final TurnClient client) throws Exception
        {
        // Make sure we got a correct mapped address attribute -- the 
        // server reflexive address.  This should be a loopback address since
        // we're connecting from localhost.
        final InetSocketAddress mappedAddress = client.getMappedAddress();
        final InetAddress mia = mappedAddress.getAddress();
        assertTrue("Unexpected mapped address: "+mia, 
            mia.isSiteLocalAddress() || mia.isLoopbackAddress());
        
        // The relay address should be site local because the test TURN
        // server is just running locally.  If we're on EC2, things are weird though
        // because we get the real public address, but there's no hairpinning.
        final InetSocketAddress tempAddress = client.getRelayAddress();
        final InetSocketAddress relayAddress;
        if (AmazonEc2Utils.onEc2())
            {
            relayAddress = 
                new InetSocketAddress(NetworkUtils.getLocalHost(), tempAddress.getPort());
            }
        else
            {
            relayAddress = tempAddress;
            }
        
        final InetAddress ria = relayAddress.getAddress();
        
        // The relay address should be the local address since the server's
        // just running locally.
        assertTrue("Unexpected relay address: "+ria, 
            ria.equals(NetworkUtils.getLocalHost()));
        
        // Create a socket for the remote host.
        final Socket remoteSocket = new Socket();
        remoteSocket.setSoTimeout(10000);
        
        remoteSocket.connect(relayAddress, 10000);
        assertTrue(remoteSocket.isConnected());
        return remoteSocket;
        }

    private TurnClient createTurnClient() throws Exception
        {
        final InetSocketAddress localTurnServer = 
            new InetSocketAddress(NetworkUtils.getLocalHost(), STUN_PORT);
        
        final Map<InetSocketAddress, Collection<ByteBuffer>> addressesToMessages = 
            new HashMap<InetSocketAddress, Collection<ByteBuffer>>();

        final Set<InetSocketAddress> openedAddresses = 
            new HashSet<InetSocketAddress>();
        
        final AtomicInteger readBytes = new AtomicInteger(0);
        final TurnClientListener delegateListener =
            new TurnClientListener()
            {
            public void close() {}
            public void onData(final InetSocketAddress remoteAddress, 
                final IoSession session, final byte[] data)
                {
                LOG.debug("Processing data from address: {}", remoteAddress);
                readBytes.addAndGet(data.length);
                if (addressesToMessages.containsKey(remoteAddress))
                    {
                    final Collection<ByteBuffer> bufs = 
                        addressesToMessages.get(remoteAddress);
                    addRaw(bufs, data);
                    }
                else
                    {
                    final Collection<ByteBuffer> bufs = 
                        new LinkedList<ByteBuffer>();
                    addressesToMessages.put(remoteAddress, bufs);
                    addRaw(bufs, data);
                    }
                LOG.debug("Now TURN client received data from "+
                    addressesToMessages.keySet().size()+" addresses");
                }
            public void onRemoteAddressClosed(
                final InetSocketAddress remoteAddress) {}
            public IoSession onRemoteAddressOpened(
                final InetSocketAddress remoteAddress, final IoSession session)
                {
                openedAddresses.add(remoteAddress);
                LOG.debug("Now "+openedAddresses.size()+" opened addresses...");
                return null;
                }
            
            // This method is required to extract the data from the TCP frames.
            private void addRaw(final Collection<ByteBuffer> bufs,
                final byte[] data)
                {
                // The data is already unwrapped from the TCP frame here.
                bufs.add(ByteBuffer.wrap(data));
                m_totalBytesRead.addAndGet(data.length);// += data.length;
                LOG.debug("Total bytes read on TURN client: {}", 
                    m_totalBytesRead);
                synchronized (TurnTest.this)
                    {
                    if (m_totalBytesRead.get() == m_bytesWithoutFramingSent)
                        {
                        TurnTest.this.notify();
                        }
                    }
                }
            };
        final TurnClient client = createTurnClient(localTurnServer, delegateListener); 
        client.connect();
        m_clientsToAddressedBufs.put(client, addressesToMessages);
        m_clientsToBytesRead.put(client, readBytes);
        int count = 0;
        while (!client.isConnected() && count < 30)
            {
            Thread.sleep(100);
            count++;
            }
        
        assertTrue("Client could not connect!!", client.isConnected());
        return client;
        }
    
    private static TcpTurnClient createTurnClient(
        final InetSocketAddress turnServerAddress, 
        final TurnClientListener delegateListener)
        {
        final IceAgent iceAgent = new IceAgentStub();
        
        final StunTransactionTracker<StunMessage> transactionTracker =
            new StunTransactionTrackerImpl();
        final StunMessageVisitorFactory messageVisitorFactory =
            new IceStunConnectivityCheckerFactoryImpl<StunMessage>(iceAgent, 
                transactionTracker, null);
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

    private void startTurnThreadedServer()
        {
        final Runnable runner = new Runnable()
            {
            public void run()
                {
                try
                    {
                    startTurnServer();
                    }
                catch (final IOException e)
                    {
                    Assert.fail ("Could not start server");
                    throw new RuntimeIoException("Could not start server", e);
                    }
                }
            };
        final Thread turnThread = 
            new Thread(runner, "TURN-Test-TURN-Server-Thread");
        turnThread.setDaemon(true);
        turnThread.start();
        }

    private void startTurnServer() throws IOException 
        {
        this.m_turnServer = new TcpTurnServer();
        m_turnServer.start();
        
        // We don't know when it's really started.
        try
            {
            Thread.sleep(1000);
            }
        catch (InterruptedException e)
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
        synchronized (this)
            {
            m_turnServerStarted.set(true);
            notify();
            }
        }
    }
