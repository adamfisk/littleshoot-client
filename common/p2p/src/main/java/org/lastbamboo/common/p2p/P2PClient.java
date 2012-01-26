package org.lastbamboo.common.p2p;

import java.io.IOException;
import java.net.URI;

import org.lastbamboo.common.offer.answer.Offerer;

/**
 * General interface for P2P clients.
 */
public interface P2PClient extends Offerer, SocketFactory {

    /**
     * Registers a given user ID with P2P proxies so that other people can
     * connect to her.
     *
     * @param userId The identifier of the user to register.
     */
    void register(long userId);
    
    /**
     * Registers a given user ID with P2P proxies so that other people can
     * connect to her.
     *
     * @param userId The identifier of the user to register.
     */
    void register(URI sipUri);

    /**
     * Registers a given user ID with P2P proxies so that other people can
     * connect to her.
     *
     * @param userId The identifier of the user to register.
     */
    void register(String id);
    
    /**
     * Logs in to the server.
     * 
     * @param user The user name.
     * @param password The password.
     * @return The JID of the logged in user.
     * @throws IOException If we could not log in.
     */
    String login(String user, String password) throws IOException;
    
    /**
     * Logs in to the server.
     * 
     * @param user The user name.
     * @param password The password.
     * @param id An ID to use to identify this logged-in instance.
     * @return The JID of the logged in user.
     * @throws IOException If we could not log in.
     */
    String login(String user, String password, String id) throws IOException;
    
}
