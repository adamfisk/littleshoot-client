package org.lastbamboo.common.rudp;

import java.io.InputStream;

import org.littleshoot.util.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A test server for running reliable UDP tests on separate hosts.
 */
public final class RudpTestServer
    {
    /**
     * Runs this test server.
     * 
     * @param argv
     *      The command line arguments.  This server expects none.
     */
    public static void main
            (final String[] argv)
        {
        /*
        final Logger logger = LoggerFactory.getLogger (RudpTestServer.class);
        
        final RudpService service = new RudpServiceImpl ();
        
        final RudpListeningConnectionId listeningId =
                service.listen (RudpTestConfig.PORT, 5);
        
        final RudpListener listener = new RudpListener ()
            {
            };
            
        logger.debug ("Before accept");
        
        final Future<RudpConnectionId> openFuture =
                service.accept (listeningId, listener);
        
        logger.debug ("After accept");
        
        openFuture.join ();
        
        logger.debug ("Connection opened");
        
        final RudpConnectionId id = openFuture.get ();
        
        final InputStream is = new RudpInputStream (service, id, 
            new RudpSocket(service, id));
        
        logger.debug ("Checking data");
        
        RudpTestUtils.check (logger, RudpTestConfig.NUM_TEST_BYTES, is);
        
        logger.debug ("Data checked");
        
        System.exit (0);
        */
        }
    }
