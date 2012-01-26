package org.lastbamboo.common.stun.server;

import java.io.IOException;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.SimpleByteBufferAllocator;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launches the STUN server.
 */
public class StunServerLauncher
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(StunServerLauncher.class);
    
    /**
     * Launches the STUN server.
     * 
     * @param args The command line arguments.
     */
    public static void main(final String[] args)
        {
        LOG.debug("Launching SIP and TURN servers...");
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        final StunServerLauncher launcher = new StunServerLauncher();
        LOG.debug("Created launcher");
        try
            {
            launcher.launch();
            LOG.debug("Started launcher");
            }
        catch (final IOException e)
            {
            LOG.error("Could not start!!!",e);
            }
        }

    /**
     * Launches the server.
     * @throws IOException If we cannot bind the server port. 
     */
    public void launch() throws IOException
        {
        final StunMessageVisitorFactory messageVisitorFactory = 
            new StunServerMessageVisitorFactory();
        final StunServer server = 
            new UdpStunServer(messageVisitorFactory, "UDP-STUN-Server");
        server.start();
        
        // Just keep the thread open.
        try
            {
            synchronized (this)
                {
                wait();
                }
            }
        catch (final InterruptedException e)
            {
            LOG.debug("Got interrupt -- CTR-Ced?", e);
            }
        }

    }
