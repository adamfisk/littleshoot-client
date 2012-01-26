package org.lastbamboo.client.handlers;

/**
 * Service interface that simply returns a boolean in response to a generic command
 * object.
 */
public interface BooleanService
    {

    /**
     * Processes the command data in the given bean.
     * 
     * @param ob The bean containing request data.
     * @return <code>true</code> or <code>false</code> depending on the result
     * of the business logic action.
     */
    boolean service(Object ob);
    }
