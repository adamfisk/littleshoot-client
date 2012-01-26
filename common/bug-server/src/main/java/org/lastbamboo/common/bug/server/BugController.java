package org.lastbamboo.common.bug.server;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.bug.server.processors.BugProcessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller that accepts bug submissions in the form of REST requests.
 */
public class BugController extends AbstractController
    {

    private final Logger LOG = LoggerFactory.getLogger(BugController.class);
    
    private final BugProcessor m_bugProcessor;

    /**
     * Creates a new controller for handling bug submissions.
     * 
     * @param processor The class that processes the bug data.
     */
    public BugController(final BugProcessor processor)
        {
        this.m_bugProcessor = processor;
        }
        
    @Override
    protected ModelAndView handleRequestInternal(
        final HttpServletRequest request, final HttpServletResponse response) 
        throws Exception
        {
        LOG.debug("Received bug...");
        
        final ServletInputStream sis = request.getInputStream();
        this.m_bugProcessor.processBug(sis, request);
        return null;
        }

    }
