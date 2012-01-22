package org.lastbamboo.client.services.http;

import java.io.File;


/**
 * Utility class that resolves an HTTP request for a <code>FileResource</code>
 * to the <code>FileResource</code> in the database.
 */
public interface ResourceRequestResolver
    {

    /**
     * Resolves the given HTTP request to a <code>File</code>.  The
     * request MUST be in the HUGE 0.94 format of: <p>
     * 
     * /uri-res/N2R?[URN] <p>
     * 
     * Other formats may be supported in the future.
     * 
     * @param request The HTTP request.
     * @return The <code>File</code> for the request, or 
     * <code>null</code> if no resource matches the request.
     */
    //File resolveRequest(final HttpServletRequest request);

    File resolveRequest(String pathInContext);
    }
