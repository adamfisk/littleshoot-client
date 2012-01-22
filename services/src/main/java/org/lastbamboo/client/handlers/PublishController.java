package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.services.PublishFileService;
import org.lastbamboo.common.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for publishing files.
 */
public class PublishController extends HttpServlet {
    
    private static final long serialVersionUID = -883034247034247944L;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.trace("Handling JSON request: {}", request.getQueryString());
        
        ControllerUtils.preventCaching(response);
        
        ControllerUtils.printCookies(request);
        final Map<String, String> cookieMap = 
            ControllerUtils.createCookieMap(request);
        final String key = cookieMap.get("siteKey");
        final String json;
        if (!ControllerUtils.signatureMatches(request, key)) {
            final JSONObject obj = new JSONObject();
            JsonUtils.put(obj, "success", false);
            JsonUtils.put(obj, "message", "Could not publish. Security error.");
            json = obj.toString();
        }
        else {
            final Map<String, String> paramMap = 
                ControllerUtils.toParamMap(request);
            
            paramMap.remove("signature");
            paramMap.remove("callback");
            final PublishFileService service = 
                new PublishFileService(LittleShootModule.getFileMapper(), 
                    LittleShootModule.getRemoteResourceRepository(), 
                    LittleShootModule.getPublishedFilesTracker(),
                    paramMap, cookieMap, ControllerUtils.toHeaderMap(request));
            json = service.getJson(request);
        }
        
        JsonControllerUtils.writeResponse(request, response, json);
    }
    
}
