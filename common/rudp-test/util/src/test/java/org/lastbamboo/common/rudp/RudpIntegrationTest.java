package org.lastbamboo.common.rudp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.littleshoot.util.Future;
import org.littleshoot.util.NetworkUtils;
import org.littleshoot.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An integration test for our reliable UDP implementation.
 */
public final class RudpIntegrationTest
    {
    /**
     * Asserts that a collection of messages were received in the given
     * collection order.
     * 
     * @param logger
     *      The logger for logging messages.
     * @param expected
     *      The expected messages, in order.
     * @param service
     *      The service used to receive messages.
     * @param id
     *      The identifier of the connection from which to receive messages.
     */
    private static void assertReceive
            (final Logger logger,
             final Collection<byte[]> expected,
             final RudpService service,
             final RudpConnectionId id)
        {
        final Collection<byte[]> actual =
                new ArrayList<byte[]> (expected.size ());
                
        final int expectedSize = expected.size ();
                
        for (int i = 0; i < expectedSize; ++i)
            {
            logger.debug ("Receiving {} of {}", i + 1, expectedSize);
            actual.add (service.receive (id));
            logger.debug ("Received {} of {}", i + 1, expectedSize);
            }
        
        Assert.assertTrue ("Received bad data", deepEquals (expected, actual));
        }
    
    /**
     * Returns whether two collections of byte arrays are deeply equal.  They
     * need to be the same size and each element must have the same byte values.
     * 
     * @param lhs
     *      The left hand side of the equality test.
     * @param rhs
     *      The right hand side of the equality test.
     *      
     * @return
     *      True if they are equal.
     */
    private static boolean deepEquals
            (final Collection<byte[]> lhs,
             final Collection<byte[]> rhs)
        {
        if (lhs.size () == rhs.size ())
            {
            final Iterator<byte[]> lhsIter = lhs.iterator ();
            final Iterator<byte[]> rhsIter = rhs.iterator ();
            
            boolean same = true;
            
            while (lhsIter.hasNext () && same)
                {
                final byte[] oneLhs = lhsIter.next ();
                final byte[] oneRhs = rhsIter.next ();
                
                same = Arrays.equals (oneLhs, oneRhs);
                }
            
            return same;
            }
        else
            {
            return false;
            }
        }
    
    /**
     * Returns random messages for testing.
     * 
     * @param random
     *      The random number generator to use.
     * @param numMessages
     *      The number of messages to create.
     *      
     * @return
     *      A collection of random messages.
     */
    private static Collection<byte[]> getRandomMessages
            (final SecureRandom random,
             final int numMessages)
        {
        final Collection<byte[]> messages = new ArrayList<byte[]> (numMessages);
        
        for (int i = 0; i < numMessages; ++i)
            {
            final byte[] message = new byte[random.nextInt (8) + 1];
            
            random.nextBytes (message);
            
            messages.add (message);
            }
        
        return messages;
        }
    
    /**
     * Sends a collection of messages using a given service.
     * 
     * @param logger
     *      The logger for logging messages.
     * @param messages
     *      The collection of messages to send.
     * @param service
     *      The service used to send the messages.
     * @param id
     *      The identifier of the connection over which to send the messages.
     */
    private static void send
            (final Logger logger,
             final Collection<byte[]> messages,
             final RudpService service,
             final RudpConnectionId id)
        {
        int i = 1;
        
        for (final byte[] message : messages)
            {
            logger.debug ("Sending {} of {}", i, messages.size ());
            
            service.send (id, message);
            
            logger.debug ("Sent {} of {}", i, messages.size ());
            
            ++i;
            
//            try
//                {
//                // TODO: We sleep because if we just spin in a loop, we exceed
//                // the default number of outstanding messages.
//                Thread.sleep (50);
//                }
//            catch (final InterruptedException e)
//                {
//                }
            }
        }
    
    /**
     * The logger.
     */
    private Logger m_logger;
    
    /**
     * The random number generator.
     */
    private SecureRandom m_random;
    
    /**
     * The assertion error that may be thrown by a non-main thread.  This is
     * captured and rethrown on the main thread by the
     * <code>checkThread</code> method to move assertion errors back onto the
     * main thread for JUnit.
     */
    private AssertionError m_threadThrown;
    
    /**
     * Checks whether a thread threw an assertion error.  If so, this method
     * will rethrow the error.  This should be called on the main JUnit thread.
     */
    private void checkThread
            ()
        {
        synchronized (this)
            {
            if (m_threadThrown == null)
                {
                // No exception was thrown.  Do nothing.
                }
            else
                {
                throw m_threadThrown;
                }
            }
        }
    
    /**
     * Wraps a thread runner so that assertion errors appear or the main JUnit
     * thread.
     * 
     * @param runner
     *      The runner to be wrapped.
     *      
     * @return
     *      A new runner that puts assertion errors on the main JUnit thread.
     */
    private Runnable wrapThreadRunner
            (final Runnable runner)
        {
        final Runnable threadRunner = new Runnable ()
            {
            public void run
                    ()
                {
                try
                    {
                    runner.run ();
                    }
                catch (final AssertionError e)
                    {
                    m_logger.debug ("Thrown: " + e);
                    
                    synchronized (RudpIntegrationTest.this)
                        {
                        if (m_threadThrown == null)
                            {
                            m_threadThrown = e;
                            }
                        else
                            {
                            // An exception has already been recorded, probably
                            // from another thread.  Do not overwrite, since we
                            // will report the first error.
                            }
                        }
                    }
                }
            };
            
        return threadRunner;
        }
    
    /**
     * Sets up the test fixtures.
     */
    @Before
    public void setUp
            ()
        {
        m_logger = LoggerFactory.getLogger (RudpIntegrationTest.class);
        
        try
            {
            m_random = SecureRandom.getInstance ("SHA1PRNG");
            }
        catch (final NoSuchAlgorithmException e)
            {
            throw new RuntimeException (e);
            }
        }
    
    /**
     * Tests simple bidirectional sending of messages.
     */
    @Test
    public void testSimple
            ()
        {
        m_logger.debug ("Running simple test");
        
        final Collection<byte[]> serverSent = getRandomMessages (m_random, 50);
        final Collection<byte[]> clientSent = getRandomMessages (m_random, 50);
        
        final int port = 1432;
        final RudpService service = new RudpServiceImpl ();
        
        final Runnable serverRunner = new Runnable ()
            {
            public void run
                    ()
                {
                final RudpListeningConnectionId listeningId =
                        service.listen (port, 5);
                
                final RudpListener listener = new RudpListener ()
                    {
                    };
                
                final Future<RudpConnectionId> openFuture =
                        service.accept (listeningId, listener);
                
                openFuture.join ();
                
                final RudpConnectionId id = openFuture.get ();
                
                m_logger.debug ("Sending from server");
                
                send (m_logger, serverSent, service, id);
                
                assertReceive (m_logger, clientSent, service, id);
                }
            };
            
        final Thread serverThread = new Thread (wrapThreadRunner (serverRunner),
                                                "ServerThread");
        
        serverThread.start ();
        
        ThreadUtils.safeSleep (1000);
        
        final Runnable clientRunner = new Runnable ()
            {
            public void run
                    ()
                {
                try
                    {
                    final InetSocketAddress address =
                            new InetSocketAddress (NetworkUtils.getLocalHost (),
                                                   port);
                    
                    final RudpListener listener = new RudpListener ()
                        {
                        };
                    
                    final Future<RudpConnectionId> openFuture =
                            service.open (address, listener);
                    
                    openFuture.join ();
                    
                    final RudpConnectionId id = openFuture.get ();
                    
                    send (m_logger, clientSent, service, id);
                    
                    assertReceive (m_logger, serverSent, service, id);
                    
                    // threadUtils.safeSleep (10000);
                    
                    service.close (id);
                    }
                catch (final UnknownHostException e)
                    {
                    m_logger.debug (e.toString ());
                    }
                }
            };
            
        final Thread clientThread = new Thread (wrapThreadRunner (clientRunner),
                                                "ClientThread");
        
        clientThread.start ();
        
        // final Thread clientThread2 = new Thread (clientRunner, "ClientThread2");
        
        // clientThread2.start ();
        
        // TODO: Use wait/notify instead to minimize test time.
        ThreadUtils.safeSleep (7000);
        
        try
            {
            clientThread.join ();
            // serviceThread.join ();
            
            clientThread.interrupt ();
            serverThread.interrupt ();
            
            m_logger.debug ("clientThread.isAlive (): " +
                                clientThread.isAlive ());
            
            m_logger.debug ("serverThread.isAlive (): " +
                                serverThread.isAlive ());
            }
        catch (final InterruptedException e)
            {
            }
        }
    
    /**
     * Tests our reliable UDP socket streams.
     */
    // @Test
    public void testStream
            ()
        {
        final int port = 1433;
        final RudpService service = new RudpServiceImpl ();
        
        final byte[] data = RudpTestUtils.getTestData (20000);
        
        final Runnable serverRunner = new Runnable ()
            {
            public void run
                    ()
                {
                final RudpListeningConnectionId listeningId =
                        service.listen (port, 5);
                
                final RudpListener listener = new RudpListener ()
                    {
                    };
                
                final Future<RudpConnectionId> openFuture =
                        service.accept (listeningId, listener);
                
                openFuture.join ();
                
                final RudpConnectionId id = openFuture.get ();
                
                final InputStream is = 
                    new RudpInputStream (service, id, 
                        new RudpSocket(service, id, null));
                
                for (int i = 0; i < data.length; ++i)
                    {
                    try
                        {
                        //m_logger.debug ("Checking data[" + i + "] of " +
                          //                  data.length);
                        
                        final byte b = (byte) is.read ();
                        
                        Assert.assertTrue ("Data mismatch (" + i + "): '" +
                                               b + "' != '" +
                                               data[i] + "'",
                                           b == data[i]);
                        }
                    catch (final IOException e)
                        {
                        throw new RuntimeException (e);
                        }
                    }
                
                m_logger.debug ("Data checked");
                }
            };
            
        final Thread serverThread = new Thread (wrapThreadRunner (serverRunner),
                                                "ServerThread");
        
        serverThread.start ();
        
        ThreadUtils.safeSleep (1000);
        
        final Runnable clientRunner = new Runnable ()
            {
            public void run
                    ()
                {
                try
                    {
                    final InetSocketAddress address =
                            new InetSocketAddress (NetworkUtils.getLocalHost (),
                                                   port);
                    
                    final RudpListener listener = new RudpListener ()
                        {
                        };
                    
                    final Future<RudpConnectionId> openFuture =
                            service.open (address, listener);
                    
                    openFuture.join ();
                    
                    final RudpConnectionId id = openFuture.get ();
                    
                    final OutputStream os = new RudpOutputStream (service, id, 
                        new RudpSocket(service, id, null));
                    
                    try
                        {
                        os.write (data);
                        os.flush();
                        }
                    catch (final IOException e)
                        {
                        throw new RuntimeException (e);
                        }
                    }
                catch (final UnknownHostException e)
                    {
                    m_logger.debug (e.toString ());
                    }
                }
            };
            
        final Thread clientThread = new Thread (wrapThreadRunner (clientRunner),
                                                "ClientThread");
        
        clientThread.start ();
        
        try
            {
            clientThread.join ();
            serverThread.join ();
            
            m_logger.debug ("clientThread.isAlive (): " +
                                clientThread.isAlive ());
            
            m_logger.debug ("serverThread.isAlive (): " +
                                serverThread.isAlive ());
            
            checkThread ();
            }
        catch (final InterruptedException e)
            {
            }
        }
    }
