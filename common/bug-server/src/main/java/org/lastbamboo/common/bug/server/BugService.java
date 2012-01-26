package org.lastbamboo.common.bug.server;

/**
 * Service interface for handling bug database access requests. 
 */
public interface BugService
    {

    /**
     * Accesses the top bugs and returns them in JSON format.
     * 
     * @return The top bugs in JSON format.
     */
    String getTopBugsJson();

    }
