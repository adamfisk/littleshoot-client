package org.lastbamboo.common.jmx.client;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.amazon.ec2.AmazonEc2CandidateProvider;

/**
 * Launcher class for launching a SIP and a TURN server.
 */
public class Launcher
    {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);
    
    /**
     * Launches the SIP and TURN servers.
     * 
     * @param args The command line arguments.
     */
    public static void main(final String[] args)
        {
        LOG.debug("Starting JMX Client...");
        final InetSocketAddress localHost = 
            new InetSocketAddress("127.0.0.1", 9999);
        
        /*
        final Collection<InetAddress> servers =
            new AmazonEc2UtilsImpl().getInstanceAddresses("sip-turn");
          //  new LinkedList<InetSocketAddress>();
        //servers.add(localHost);
        final SipTurnMonitor monitor = new SipTurnMonitor(servers);
        monitor.monitor();
        */
        }

    }
