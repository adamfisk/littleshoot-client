package org.lastbamboo.client.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.StringUtils;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.search.JsonSearchResultVisitor;
import org.lastbamboo.client.search.SearchResultManager;
import org.lastbamboo.client.search.SessionResults;
import org.lastbamboo.client.services.command.SearchResultsCommand;
import org.lastbamboo.common.rest.RestResult;
import org.littleshoot.util.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Controller for requests for search results.
 * 
 * @param <T> Class extending a {@link RestResult}.
 */
public class SearchResultsController<T extends RestResult> 
    extends HttpServlet {
    
    private static final long serialVersionUID = 358885899548732036L;
    
    /**
     * Logger for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        LOG.trace("Handling search results request: "+request.getQueryString());
        ControllerUtils.preventCaching(response);
        
        final SearchResultsCommand resultsParams = 
            new SearchResultsCommand();
        ControllerUtils.populate(resultsParams, request);
        
        final String sessionId = request.getSession().getId();
        final String searchIdString = resultsParams.getGuid();
        final SearchResultManager searchResultManager =
            LittleShootModule.getSearchResultManager();
        final String toReturn;
        if (searchResultManager.hasMapping(sessionId, searchIdString))
            {     
            if (StringUtils.isBlank(searchIdString))
                {
                LOG.warn("No mapping for session ID: "+sessionId+
                        " and search ID: "+searchIdString+"\n"+
                        ControllerUtils.getRequestHeaders(request));
                final JsonSearchResultVisitor<T> visitor = 
                    new JsonSearchResultVisitor<T>("Blank Search ID");
                toReturn = visitor.getJson();
                }
            else
                {
                final UUID searchId = new UUID(searchIdString);
                
                final SessionResults<T>  results = 
                    searchResultManager.getResults(sessionId, searchId);
                
                final MimeType mt = new MimeType()
                    {
                    public String getMimeType(final String fileName)
                        {
                        return getServletContext().getMimeType(fileName);
                        }
                    };
                final JsonSearchResultVisitor<T> visitor = 
                    new JsonSearchResultVisitor<T>(results, 
                        resultsParams.getResultsPerPage(),
                        resultsParams.getPageIndex(), mt, 
                        LittleShootModule.getDownloadTracker(),
                        new UUID(resultsParams.getGuid()));
                
                toReturn = visitor.getJson();
                }
            }
        else
            {
            // This should typically not happen.  It occurs if the user's 
            // browser disallows cookies from other sub-domains, has cookies
            // disabled entirely, etc.
            
            // It *will not* get called when the browser forces a reload when
            // the search page is first loaded with no previous searches 
            // because this is prevented in JavaScript when it finds no search
            // guid.
            LOG.warn("No mapping for session ID: "+sessionId+
                " and search ID: "+searchIdString+"\n"+
                ControllerUtils.getRequestHeaders(request));
            final JsonSearchResultVisitor<T> visitor = 
                new JsonSearchResultVisitor<T>("No Session ID");
            toReturn = visitor.getJson();
            }
        
        JsonControllerUtils.writeResponse(request, response, toReturn);
    }
}
