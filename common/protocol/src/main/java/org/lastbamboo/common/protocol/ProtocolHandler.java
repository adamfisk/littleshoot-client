package org.lastbamboo.common.protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Defines an interface for different protocols to handle incoming data and
 * to process it.
 */
public interface ProtocolHandler 
    {
    
    /**
     * Reads incoming message data from the specified buffer.  The handler
     * MUST read all of the data in the buffer.  This is essential for correct
     * operation of any servers utilizing this handler.  It's also potentially
     * important for performance, as otherwise the buffers could become filled
     * and start only partially clearing the TCP receive buffers on subsequent
     * reads, at least in the NIO case.
     * 
     * @param buffer The buffers containing the newly received data. 
     * @param remoteHost The remote host that send the data in the buffer.
     * @throws IOException If there are any issues reading the message data.
     */
    void handleMessages(final ByteBuffer buffer, 
        final InetSocketAddress remoteHost) throws IOException;

    }