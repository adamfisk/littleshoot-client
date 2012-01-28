package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for JSON controllers.
 */
public class JsonControllerUtils {

    private static final Logger LOG = 
        LoggerFactory.getLogger(JsonControllerUtils.class);

    /**
     * Writes a JSON response.
     * 
     * @param request The request.
     * @param response The response.
     * @param data The JSON data to write.
     * @throws IOException If there's any error writing the response.
     */
    public static void writeResponse(final HttpServletRequest request,
        final HttpServletResponse response, final String data)
        throws IOException {
        final String finalData;
        if (StringUtils.isBlank(data)) {
            finalData = new JSONObject().toString();
        } else {
            finalData = data;
        }
        final String responseString;
        final String functionName = request.getParameter("callback");
        if (StringUtils.isBlank(functionName)) {
            responseString = finalData;
            response.setContentType("application/json");
        } else {
            responseString = functionName + "(" + finalData + ");";
            response.setContentType("text/javascript");
        }

        response.setStatus(HttpServletResponse.SC_OK);
        
        final byte[] content = responseString.getBytes("UTF-8");
        response.setContentLength(content.length);

        final OutputStream os = response.getOutputStream();

        LOG.debug("Writing javascript callback func "+functionName);
        os.write(content);
        os.flush();
    }

    /**
     * Writes a JSON response.
     * 
     * @param request The request.
     * @param response The response.
     * @param json The JSON data to write.
     * @throws IOException If there's any error writing the response.
     */
    public static void writeResponse(final HttpServletRequest request,
            final HttpServletResponse response, final JSONObject json)
            throws IOException {
        writeResponse(request, response, json.toString());
    }


    /**
     * Writes a JSON response.
     * 
     * @param request The request.
     * @param response The response.
     * @param json The JSON data to write.
     * @throws IOException If there's any error writing the response.
     */
    public static void writeResponse(final HttpServletRequest request,
            final HttpServletResponse response, final JSONArray json)
            throws IOException {
        writeResponse(request, response, json.toString());
    }

    /**
     * Writes an empty response for calls that are purely one-way RPC.
     * 
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @throws IOException If any unexpected error occurs.
     */
    public static void writeResponse(final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        writeResponse(request, response, "");
    }

    public static void writeResponse(final HttpServletRequest request,
        final HttpServletResponse response, 
        final org.json.simple.JSONObject json) throws IOException {
        writeResponse(request, response, json.toJSONString());
    }
}
