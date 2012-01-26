package org.lastbamboo.client.handlers;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface that provides data in JSON format in response to data from a 
 * servlet command bean.. 
 */
public interface JsonCommandProvider
    {

    /**
     * Returns date as JSON.
     * 
     * @param request The HTTP request object.
     */
    String getJson(HttpServletRequest request);
    }
