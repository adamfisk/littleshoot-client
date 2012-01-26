package org.lastbamboo.common.online;

import java.net.InetAddress;

import org.littleshoot.util.NetworkUtils;

import junit.framework.TestCase;

public class RobustOnlineStatusUpdaterTest extends TestCase
    {

    public void testUpdate() throws Exception
        {
        final RobustOnlineStatusUpdater updater = 
            new RobustOnlineStatusUpdater();
        
        final InetAddress serverAddress = NetworkUtils.getLocalHost();
        final String baseUri = "sip://294729";
        final long userId = 48298472L;
        
        // NOTE: You have to manually look at the server database to see
        // if this is working.  Uncomment below.
        /*
        updater.updateStatus(userId, baseUri, true, serverAddress);
        
        Thread.sleep(2000);
        
        updater.allOffline(serverAddress, 1, 10000);
        
        Thread.sleep(3000);
        */
        }
    }
