package org.lastbamboo.client.services.http;

/**
 * HTTP server.
 */
public interface HttpServer {

    /**
     * "Joins" the server -- keeps it up and blocking.
     */
    void joinServer();

    /**
     * Stops the server.
     */
    void stopServer();

}
