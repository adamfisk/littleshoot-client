package org.lastbamboo.common.ice;

import java.net.InetSocketAddress;

/**
 * Interface for server sockets that have their ports mapped.
 */
public interface MappedServerSocket {

    boolean isPortMapped();
    
    int getMappedPort();

    InetSocketAddress getHostAddress();
}
