package org.lastbamboo.common.bug.server;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller that accepts bug submissions in the form of REST requests.
 */
public class TopBugsController extends AbstractController
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final BugService m_bugService;

    /**
     * Creates a new controller for handling bug submissions.
     * 
     * @param bugService The class for servicing bug access requests.
     */
    public TopBugsController(final BugService bugService)
        {
        m_bugService = bugService;
        }
        
    @Override
    protected ModelAndView handleRequestInternal(
        final HttpServletRequest request, final HttpServletResponse response) 
        throws Exception
        {
        m_log.trace("Handling app check request: "+request.getQueryString());
        final String functionName = request.getParameter("callback");
        if (StringUtils.isBlank(functionName))
            {
            m_log.warn("No function name in: "+request.getQueryString());
            return null;
            }
        m_log.trace("Function: "+functionName);
        
        final String json = this.m_bugService.getTopBugsJson();
        
        final String javaScript = functionName+"("+json.toString()+");";
        
        m_log.debug("Writing javascript: {}", javaScript);
        final OutputStream os = response.getOutputStream();
        os.write(javaScript.getBytes("UTF-8"));
        os.flush();
        
        // We have no model and view to show in this case.
        return null;
        }
    }
