package org.lastbamboo.common.jmx.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.lastbamboo.common.amazon.ec2.AmazonEc2CandidateProvider;
import org.littleshoot.util.CandidateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests JMXserver monitoring.
 */
public class JmxMonitorTest
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    @Test public void testMonitoring() throws Exception
        {
        final Properties props = new Properties();
        final File file = new File(System.getProperty("user.home"), 
            "/.ec2/ec2.properties");
        if (!file.exists())
            {
            m_log.debug("No EC2 props file at: "+file.getAbsolutePath());
            return;
            }
        final InputStream is = new FileInputStream(file);
        props.load(is);
        final String keyId = props.getProperty("accessKeyId");
        final String key = props.getProperty("accessKey");
        if (StringUtils.isBlank(keyId) || StringUtils.isBlank(key))
            {
            m_log.debug("EC2 props aren't set!!");
            return;
            }
        
        final AmazonEc2CandidateProvider ec2 = 
            new AmazonEc2CandidateProvider(keyId, key);
        
        final CandidateProvider<InetAddress> provider = 
            new CandidateProvider<InetAddress>()
            {

            public InetAddress getCandidate()
                {
                try
                    {
                    return InetAddress.getByName(
                        "ec2-67-202-6-199.z-1.compute-1.amazonaws.com");
                    }
                catch (UnknownHostException e)
                    {
                    Assert.fail("Could not get host");
                    return null;
                    }
                }

            public Collection<InetAddress> getCandidates()
                {
                final Collection<InetAddress> candidates = 
                    new LinkedList<InetAddress>();
                candidates.add(getCandidate());
                return candidates;
                }
            
            };
        // TODO: Switch this back to using EC2.
            /*
        final JmxMonitorImpl monitor = new JmxMonitorImpl(ec2);
        
        final Runnable runner = new Runnable()
            {
            public void run()
                {
                monitor.monitor();
                }
            };
        final Thread thread = new Thread(runner, "Server-Monitor");
        thread.setDaemon(true);
        thread.start();
        
        
        Thread.sleep(6000);
        final Collection<InetSocketAddress> servers = monitor.getCandidates();
        m_log.debug("Got candidates: {}", servers);
        assertEquals("Unexpected servers: "+servers, 1, servers.size());
        */
        }
    }
