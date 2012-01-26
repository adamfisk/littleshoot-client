package org.lastbamboo.client.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.services.download.FilePublisher;
import org.lastbamboo.client.services.download.LittleShootFilePublisher;
import org.lastbamboo.common.json.JsonUtils;
import org.littleshoot.util.CommonUtils;
import org.littleshoot.util.Sha1Hasher;
import org.littleshoot.util.ShootConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for publishing files.
 */
public class PublishSmartFileController extends HttpServlet {
    
    private static final long serialVersionUID = 7271894932355410772L;

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.info("Handling JSON request: {}", request.getQueryString());
        ControllerUtils.preventCaching(response);
        final Properties props = CommonUtils.getProps();
        final String localKey = props.getProperty(ShootConstants.HII_KEY);
        if (StringUtils.isEmpty(localKey)) {
            invalidRequest(request, response, "No key configured.");
            return;
        }
        
        ControllerUtils.printCookies(request);
        final Map<String, String> paramMap = 
            ControllerUtils.toParamMap(request);

        //final PublishFileCommand cmd = (PublishFileCommand) command;
        //final File file = cmd.getFile();
        final String path = request.getParameter("file");
        if (StringUtils.isBlank(path)) {
            invalidRequest(request, response, "File parameter invalid.");
            return;
        }
        final File file = new File(path);
        if (!file.isFile()) {
            invalidRequest(request, response, 
                "File not valid. Path given: " + path);
            return;
        }
        try {
            final URI sha1 = Sha1Hasher.createSha1Urn(file);
            m_log.info("SHA-1 is: {}", sha1);
            final FilePublisher publisher = 
                new LittleShootFilePublisher(
                    LittleShootModule.getFileMapper(), 
                    LittleShootModule.getRemoteResourceRepository(), 
                    sha1, sha1);
            
            final String jsonString = publisher.publish(file);
            m_log.info("Got response from server: {}", jsonString);
            writeResponse(request, response, jsonString);
        } 
        catch (final IOException e) {
            invalidRequest(request, response, e.getMessage());
        }
    }

    private void invalidRequest(final HttpServletRequest request, 
        final HttpServletResponse response, final String msg) {
        final JSONObject obj = new JSONObject();
        JsonUtils.put(obj, "success", false);
        JsonUtils.put(obj, "message", msg);
        writeResponse(request, response, obj.toString());
    }
    
    private void writeResponse(final HttpServletRequest request,
        final HttpServletResponse response, final String jsonString) {
        try  {
            JsonControllerUtils.writeResponse(request, response, jsonString);
        } 
        catch (final IOException e) {
            m_log.info("Could not write response", e);
        }
    }
}
