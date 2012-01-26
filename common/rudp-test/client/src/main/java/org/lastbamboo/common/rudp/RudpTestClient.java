package org.lastbamboo.common.rudp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.littleshoot.util.Future;
import org.littleshoot.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A test client for running reliable UDP tests on separate hosts.
 */
public final class RudpTestClient
    {
    /**
     * Runs this test server.
     * 
     * @param argv
     *      The command line arguments.  This server expects none.
     */
    public static void main (final String[] argv)
        {
        /*
        final Logger logger = LoggerFactory.getLogger (RudpTestClient.class);
        
        final byte[] data = RudpTestUtils.getTestData (20000);
        
        final RudpService service = new RudpServiceImpl ();
        
        final InetSocketAddress address =
                new InetSocketAddress (argv[0], RudpTestConfig.PORT);
        
        final RudpListener listener = new RudpListener ()
            {
            };
        
        final Future<RudpConnectionId> openFuture =
                service.open (address, listener);
        
        logger.debug ("Joining open future");
        
        openFuture.join ();
        
        final RudpConnectionId id = openFuture.get ();
        
        final OutputStream os = 
            new RudpOutputStream (service, id, new RudpSocket(service, id));
        
        try
            {
            logger.debug ("Before: os.write");
            
            os.write (data);
            
            logger.debug ("After: os.write");
            
            ThreadUtils.safeSleep (5000);
            
            System.exit (0);
            }
        catch (final IOException e)
            {
            throw new RuntimeException (e);
            }
            */
        }
    }
