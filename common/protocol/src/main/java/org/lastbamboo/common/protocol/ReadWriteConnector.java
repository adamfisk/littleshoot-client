package org.lastbamboo.common.protocol;

import java.io.IOException;

/**
 * Interface for classes that connect to remote hosts and create handlers
 * for reading and writing for those connections.  
 */
public interface ReadWriteConnector 
    {

    /**
     * Connects to the remote host.
     * @throws IOException If there's a network error connecting.
     */
    void connect() throws IOException;

    }
