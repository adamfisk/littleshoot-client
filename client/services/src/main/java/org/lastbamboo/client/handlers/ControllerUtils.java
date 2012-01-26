package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.client.services.SessionAttributeKeys;
import org.littleshoot.util.BeanUtils;
import org.littleshoot.util.HttpParamKeys;
import org.littleshoot.util.SecurityUtils;
import org.littleshoot.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Utility methods for controllers.
 */
public class ControllerUtils
    {
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(ControllerUtils.class);

    private static final String HEADER_PRAGMA = "Pragma";

    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    
    /**
     * Prevent the response from being cached.
     * See <code>http://www.mnot.net/cache_docs</code>.
     */
    public static void preventCaching(final HttpServletResponse response) {
        response.setHeader(HEADER_PRAGMA, "no-cache");
        
        // HTTP 1.1 header: "no-cache" is the standard value,
        // "no-store" is necessary to prevent caching on FireFox.
        response.setHeader(HEADER_CACHE_CONTROL, "no-cache");
        response.addHeader(HEADER_CACHE_CONTROL, "no-store");
    }
    
    
    public static boolean verifyMethod(final HttpServletRequest request,
        final HttpServletResponse response, final String... methods) {
        final String method = request.getMethod();
        for (final String m : methods) {
            if (method.equalsIgnoreCase(m)) {
                return true;
            }
        }
        try {
            LOG.warn("Sending error for method: {}", method);
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Invalid request method");
        } catch (final IOException e) {
            LOG.warn("Coud not send error", e);
        }
        return false;
    }
    
    /**
     * Checks if a signature matches.
     * 
     * @param request The signed request.
     * @param key The key it was signed with.
     * @return <code>true</code> if the signature matches, otherwise 
     * <code>false</code>.
     */
    public static boolean signatureMatches(final HttpServletRequest request,
        final String key)
        {
        LOG.debug("Checking for signature match...");
        final String sig = request.getParameter("signature");
        if (StringUtils.isBlank(sig))
            {
            LOG.warn("No signature.");
            return false;
            }
        if (StringUtils.isBlank(key))
            {
            LOG.warn("No key");
            return false;
            }
        
        LOG.debug("Sig from client: {}", sig);
        final StringBuffer buf = request.getRequestURL();
        buf.append("?");
        buf.append(request.getQueryString());
        final String url = buf.toString();
        LOG.debug("URL is: "+url);
        
        final String preSignatureUrl = 
            url.substring(0, url.indexOf("&signature"));
        
        LOG.debug("Computing with url: {}", preSignatureUrl);
        final String computedSig = 
            SecurityUtils.signAndEncode(key, preSignatureUrl);

        if (sig.equals(computedSig))
            {
            LOG.debug("Signatures matched!");
            return true;
            }
        else
            {
            LOG.warn("Signatures did not match!  Expected "+sig+" but was: "+
                    computedSig);
            return false;
            }
        }

    /**
     * Creates a {@link Map} of all the available cookies.
     * 
     * @param request The {@link HttpServletRequest} containing cookies.
     * @return A {@link Map} of the cookies for the request.
     */
    public static Map<String, String> createCookieMap(
        final HttpServletRequest request)
        {
        LOG.debug("Creating cookie map..");
        final Map<String, String> cookieMap = 
            new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        final Cookie[] cookies = request.getCookies();

        if (cookies != null)
            {
            for (final Cookie cookie : cookies)
                {
                cookieMap.put(cookie.getName(), cookie.getValue());
                }
            }
        return cookieMap;
        }
    

    /**
     * Prints the cookies for the request.
     * 
     * @param request The HTTP request.
     */
    public static void printCookies(final HttpServletRequest request)
        {
        final Cookie[] cookies = request.getCookies();

        if (cookies != null)
            {
            LOG.debug("Printing cookies..");
            final StringBuilder sb = new StringBuilder();
            sb.append("\n");
            for (final Cookie cookie : cookies)
                {
                sb.append(" Name/Value: "+cookie.getName()+"/"+cookie.getValue());
                sb.append(" Domain: "+cookie.getDomain());
                sb.append(" Max Age: "+cookie.getMaxAge());
                sb.append(" Path: "+cookie.getPath());
                sb.append("\n");
                }
            sb.append("\n");
            
            LOG.debug(sb.toString());
            }
        }
    
    /**
     * Prints request headers.
     * 
     * @param request The request.
     */
    public static void printRequestHeaders(final HttpServletRequest request)
        {
        LOG.debug(getRequestHeaders(request).toString());
        }
    
    /**
     * Gets request headers as a string.
     * 
     * @param request The request.
     * @return The request headers as a string.
     */
    public static String getRequestHeaders(final HttpServletRequest request)
        {
        final Enumeration<String> headers = request.getHeaderNames();
        final StringBuilder sb = new StringBuilder();
        sb.append("\n");
        while (headers.hasMoreElements())
            {
            final String headerName = headers.nextElement();
            sb.append(headerName);
            sb.append(": ");
            sb.append(request.getHeader(headerName));
            sb.append("\n");
            }
        return sb.toString();
        }
    
    /**
     * Converts the request arguments to a map of parameter keys to single
     * values, ignoring multiple values.
     * 
     * @param request The request.
     * @return The mapped argument names and values.
     */
    public static Map<String, String> toParamMap(
        final HttpServletRequest request)
        {
        final Map<String, String> map = 
            new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        final Map<String, String[]> paramMap = request.getParameterMap();
        final Set<String> keys = paramMap.keySet();
        for (final String key : keys)
            {
            final String[] values = paramMap.get(key);
            map.put(key, values[0]);
            }
        return map;
        }

    /**
     * Quick check if a paramater exists.
     * 
     * @param request The request to check. 
     * @param paramName The name of the param.
     * @return <code>true</code> if it exists, otherwise <code>false</code>.
     */
    public static boolean hasParam(final HttpServletRequest request, 
        final String paramName)
        {
        final String param = request.getParameter(paramName);
        return StringUtils.isNotBlank(param);
        }

    /**
     * Creates a map of header names to values from an HTTP request.
     * 
     * @param request The HTTP request.
     * @return The map of header names to values.
     */
    public static Map<String, String> toHeaderMap(
        final HttpServletRequest request)
        {
        final Map<String, String> map = 
            new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        final Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements())
            {
            final String name = headers.nextElement();
            final String value = request.getHeader(name);
            map.put(name, value);
            }
        return map;
        }

    /**
     * Signs a request using a key in a cookie.
     * 
     * @param request The request with the cookie data.
     * @param baseUrl The base URL.
     * @param paramMap The parameters to use in creating the new URL.  This is
     * passed in rather than just taking them from the request because in some
     * cases it may be necessary to filter certain parameters.
     * @return The URL with the signature added.
     * @throws IOException If any of the required cookies can't be found, such
     * as for the session ID.
     */
    public static String sign(final HttpServletRequest request, 
        final String baseUrl, final Map<String, String> paramMap) 
        throws IOException
        {
        final Map<String, String> cookieMap = 
            ControllerUtils.createCookieMap(request);
        final String sessionId = cookieMap.get(SessionAttributeKeys.SESSION_ID);
        if (StringUtils.isBlank(sessionId))
            {
            LOG.error("No session ID!!");
            throw new IOException("Missing session ID");
            }
        
        final String key = cookieMap.get(SessionAttributeKeys.KEY);
        if (StringUtils.isBlank(key))
            {
            LOG.error("No key");
            throw new IOException("No key");
            }
        final String preSignatureUrl = UriUtils.newUrl(baseUrl, paramMap);
        final String sig = SecurityUtils.signAndEncode(key, preSignatureUrl);
        return UriUtils.appendParam(preSignatureUrl, "signature", sig);
        }

    /**
     * Gets the key to use for looking up the key in session attributes.
     * 
     * @param request The request from the user.
     * @return The key ID to use.
     */
    public static String getKeyName(final HttpServletRequest request) {
        final String[] values = 
            (String[]) request.getParameterMap().get(HttpParamKeys.KEY_ID);
        final String keyId = values[0]; 
        if (StringUtils.isNotBlank(keyId)) return keyId;
        return SessionAttributeKeys.KEY;
    }

    public static void populate(final Object bean, 
        final Map<String, String> params, final Collection<String> required) {
        for (final String key : required) {
            if (!params.containsKey(key)) {
                LOG.warn("Key not found in request: {}", key);
                throw new IllegalArgumentException(key + " is required!!");
            }
        }
        
        BeanUtils.populateBean(bean, params);
    }

    public static void populate(final Object bean, 
        final Map<String, String> params) {
        populate(bean, params, new HashSet<String>());
    }
    
    public static void populate(final Object bean, 
        final Map<String, String> params, final String... required) {
        populate(bean, params, Sets.newHashSet(required));
    }
    
    public static void populate(final Object bean, 
        final HttpServletRequest request) {
        populate(bean, toParamMap(request), new HashSet<String>());
    }
    
    public static void populate(final Object bean, 
        final HttpServletRequest request, final String... required) {
        populate(bean, toParamMap(request), Sets.newHashSet(required));
    }
}
