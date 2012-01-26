package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.prefs.Prefs;
import org.lastbamboo.client.search.MetaSearcher;
import org.lastbamboo.client.search.SearchResultManager;
import org.lastbamboo.client.search.Searcher;
import org.lastbamboo.client.services.SessionAttributeKeys;
import org.lastbamboo.client.services.command.SearchCommand;
import org.lastbamboo.common.rest.RestSearcher;
import org.lastbamboo.common.rest.SearchRequestBean;
import org.lastbamboo.common.searchers.limewire.LimeWire;
import org.lastbamboo.common.searchers.limewire.LimeWireJsonResult;
import org.lastbamboo.common.searchers.limewire.LimeWireSearcher;
import org.lastbamboo.common.searchers.littleshoot.JsonLittleShootResult;
import org.lastbamboo.common.searchers.littleshoot.LittleShootSearcher;
import org.littleshoot.util.HttpParamKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initiates a search request.
 */
public final class SearchController extends HttpServlet {
    
    private static final long serialVersionUID = -8225831541722242408L;
    
    /**
     * Logger for this class.
     */
    private final Logger log = LoggerFactory.getLogger (getClass());

    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        log.trace ("Initiating search with query: {}",
            request.getQueryString());
        ControllerUtils.preventCaching(response);
        
        final SearchRequestBean bean = new SearchCommand();
        ControllerUtils.populate(bean, request);
        processSearch(request, response, bean);
    }

    private void processSearch(final HttpServletRequest request,
        final HttpServletResponse response, final SearchRequestBean bean)
        throws IOException {
        log.debug("Starting search for {}", bean.getKeywords());

        final HttpSession session = request.getSession();

        // LimeWire crams information into the GUID, so we need to use it's
        // version.
        final LimeWire limewire = LittleShootModule.getLimeWire();
        final SearchResultManager srm = 
            LittleShootModule.getSearchResultManager();
        final UUID uuid = limewire.newUuid();
        srm.addMapping(session.getId(), uuid, bean);

        final Map<String, String> paramMap = 
            ControllerUtils.toParamMap(request);

        final Map<String, String> cookieMap = 
            ControllerUtils.createCookieMap(request);
        final String userId = cookieMap.get(SessionAttributeKeys.USER_ID);
        if (!StringUtils.isBlank(userId)) {
            // You don't have to be logged in to search.
            paramMap.put(HttpParamKeys.USER_ID, userId);
        }

        paramMap.remove("callback");
        paramMap.remove("signature");

        final Map<String, String> shootCopy = new HashMap<String, String>();
        shootCopy.putAll(paramMap);
        shootCopy.put(HttpParamKeys.INSTANCE_ID, String.valueOf(Prefs.getId()));
        final RestSearcher<JsonLittleShootResult> littleShoot = 
            new LittleShootSearcher(srm, uuid, shootCopy, bean.getKeywords());

        final RestSearcher<LimeWireJsonResult> limeWireSearcher = 
            new LimeWireSearcher(limewire, srm, uuid, bean);
        final Searcher searcher = new MetaSearcher(srm, uuid, littleShoot,
                limeWireSearcher, paramMap);
        searcher.search(bean);

        final JSONObject json = new JSONObject();

        try {
            json.put("query", bean.getSearchString());
            json.put("guid", uuid.toString());
        } catch (final JSONException e) {
            log.error("Could not insert query", e);
        }

        JsonControllerUtils.writeResponse(request, response, json);
    }
}
