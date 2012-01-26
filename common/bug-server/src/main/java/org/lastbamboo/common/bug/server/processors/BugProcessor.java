package org.lastbamboo.common.bug.server.processors;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for classes that can process bugs.
 */
public interface BugProcessor
    {

    /**
     * Processes the specified map of bug name/value pairs.
     * 
     * @param is The bug post body of name/value pairs.
     * @param request 
     */
    void processBug(InputStream is, HttpServletRequest request);

    }
