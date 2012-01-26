package org.lastbamboo.common.stun.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.littleshoot.mina.common.IoServiceListener;

/**
 * Interface for starting a STUN server.
 */
public interface StunServer
    {

    /**
     * Starts the server on the default STUN port.
     * 
     * @throws IOException If we cannot bind to the port. 
     */
    void start() throws IOException;
    
    /**
     * Starts the server, binding to the specified address.  If the argument
     * is <code>null</code>, this will choose an available port to bind to.
     * 
     * @param bindAddress The address to bind to.
     * @throws IOException If we cannot bind to the specified address, even
     * when this is <code>null</code> and we choose it randomly. 
     */
    void start(InetSocketAddress bindAddress) throws IOException;

    /**
     * Gets the address the server is bound to.
     * 
     * @return The address the server is bound to.
     */
    InetSocketAddress getBoundAddress();

    void addIoServiceListener(IoServiceListener serviceListener);

    void close();

    }
