package org.lastbamboo.common.ice.rudp;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.junit.Test;
import org.littleshoot.util.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quick test for InetAddress reachability.
 */
public class WindowsReachableTest
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    

    @Test public void testReachable() throws Exception
        {
        final InetAddress local = NetworkUtils.getLocalHost();
        m_log.debug("Local host is: "+local);
        
        // If you reduce the following to, say, 600 milliseconds, Windows
        // times out!  That's on Vista.
        assertTrue(local.isReachable(1200));
        }
    
    }

