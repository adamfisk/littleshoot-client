package org.lastbamboo.common.stun.stack.transaction;

import java.net.InetSocketAddress;

import org.lastbamboo.common.stun.stack.message.StunMessage;

/**
 * Interface for classes responsible for keeping track of SIP transactions.
 * 
 * @param <T> The type visitors for transactions return. 
 */
public interface StunTransactionTracker<T>
    {

    /**
     * Adds a transaction for the specified request with the specified 
     * listener.
     * 
     * @param request The request to add a transaction for.
     * @param listener The listener for transaction events.
     * @param localAddress The local address the request will be sent from.
     * This allows the verification of the address when we receive responses,
     * as required in some STUN usages such as ICE.
     * @param remoteAddress The remote address the request will be sent to.  
     * This allows the verification of the address when we receive responses,
     * as required in some STUN usages such as ICE.
     * @param remoteAddress2 
     */
    void addTransaction(StunMessage request, StunTransactionListener listener, 
        InetSocketAddress localAddress, InetSocketAddress remoteAddress);

    /**
     * Accessor for the client transcaction associated with the specified
     * message.
     * 
     * @param message The message containing a branch ID and SIP method to use
     * as a key for looking up the associated tranction.
     * @return The transaction associated with the specified message, or 
     * <code>null</code> if there is no associated transaction.  This can
     * happen if the transaction has timed out, for example.
     */
    StunClientTransaction<T> getClientTransaction(StunMessage message);

    }
