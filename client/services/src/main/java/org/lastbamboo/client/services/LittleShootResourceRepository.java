package org.lastbamboo.client.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.lastbamboo.client.prefs.Prefs;
import org.lastbamboo.common.http.client.ForbiddenException;
import org.littleshoot.util.DaemonThread;
import org.littleshoot.util.DefaultHttpClient;
import org.littleshoot.util.DefaultHttpClientImpl;
import org.littleshoot.util.SecurityUtils;
import org.littleshoot.util.Sha1Hasher;
import org.littleshoot.util.ShootConstants;
import org.littleshoot.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for updating the remote LittleShoot resource repository.
 */
public class LittleShootResourceRepository implements RemoteResourceRepository {
    /**
     * The log for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass()); 
    
    private final DefaultHttpClient m_clientManager = 
        new DefaultHttpClientImpl();
    
    public String insertResource(final File file, final URI uri, final URI sha1)
            throws IOException, ForbiddenException {
        m_log.debug("Inserting resource '" + file.getPath() + "' with server: "
                + ShootConstants.SERVER_URL);
        return insertResource(file, false, uri, sha1);
    }

    public String insertResource(final File file, final boolean downloaded,
            final URI uri, final URI sha1) throws IOException,
            ForbiddenException {
        m_log.debug("Inserting resource '" + file.getPath() + "' with server: "
                + ShootConstants.SERVER_URL);

        final Map<String, String> params = createPublishParams(file, uri, sha1);
        addParam(params, "downloaded", Boolean.toString(downloaded));
        return publishRaw(params);
    }
    
    public void insertResource(final File file,
            final Map<String, String> paramMap,
            final Map<String, String> cookieMap,
            final PublishedFilesTracker pendingFilesTracker,
            final FileMapper fileMapper, final PublishedCallback callback,
            final Map<String, String> headerMap) throws IOException,
            ForbiddenException {
        insertResource(file, paramMap, cookieMap, pendingFilesTracker, 
            fileMapper, callback, headerMap, "");
    }

    public void insertResource(final File file,
            final Map<String, String> paramMap,
            final Map<String, String> cookieMap,
            final PublishedFilesTracker pendingFilesTracker,
            final FileMapper fileMapper, final PublishedCallback callback,
            final Map<String, String> headerMap, final String namespace) 
            throws IOException, ForbiddenException {
        m_log.debug("Inserting resource '" + file.getPath() + "' with server: "
                + ShootConstants.SERVER_URL);

        if (fileMapper.hasFile(file)) {
            m_log.debug("File already published");
            return;
        }
        final CountingInputStream cis = new CountingInputStream(
                new FileInputStream(file));
        pendingFilesTracker.addFile(file, cis, paramMap);
        final Runnable sha1Runner = new Runnable() {
            public void run() {
                try {
                    final URI sha1 = Sha1Hasher.createSha1Urn(cis);
                    final Map<String, String> params = createPublishParams(
                            file, sha1, sha1);
                    paramMap.putAll(params);
                    signedRequest(paramMap, cookieMap, headerMap,
                            "/api/publishFile");
                    fileMapper.map(sha1, file);
                    m_log.debug("Mapped...issuing callback...");
                    callback.onFilePublished(file, sha1);
                } catch (final IOException e) {
                    m_log.warn("Could not publish file", e);
                } catch (final ForbiddenException e) {
                    m_log.warn("Forbidden to publish file", e);
                } finally {
                    pendingFilesTracker.removeFile(file);
                }
            }
        };
        final Thread sha1Thread = new DaemonThread(sha1Runner,
                "SHA-1-Publishing-Thread");
        sha1Thread.start();
    }

    public String deleteResource(final Map<String, String> paramMap,
            final Map<String, String> cookieMap) throws IOException,
            ForbiddenException {
        m_log.debug("Deleting resource using params: {}", paramMap);
        paramMap.putAll(createDeleteParams());
        return signedRequest(paramMap, cookieMap,
                new HashMap<String, String>(), "/api/deleteFile");
    }

    public String deleteResource(final File file, final URI uri)
            throws IOException, ForbiddenException {
        m_log.debug("Deleting resource '" + file.getPath() + "' with server: "
                + ShootConstants.SERVER_URL);
        final Map<String, String> paramMap = createDeleteParams();
        paramMap.put("uri", uri.toASCIIString());
        final String baseUrl = createBaseUrl("/api/deleteFile");
        return post(baseUrl, paramMap);
    }

    private String signedRequest(final Map<String, String> paramMap,
            final Map<String, String> cookieMap,
            final Map<String, String> headerMap, final String apiPath)
            throws IOException, ForbiddenException {
        final String sessionId = cookieMap.get(SessionAttributeKeys.SESSION_ID);
        if (StringUtils.isBlank(sessionId)) {
            m_log.error(SessionAttributeKeys.SESSION_ID + " not in cookies: "
                    + cookieMap);
            throw new IOException("Missing session ID");
        }

        final String userId = cookieMap.get(SessionAttributeKeys.USER_ID);
        if (StringUtils.isBlank(userId)) {
            m_log.debug("No user ID!!");
            // throw new IOException("Blank user ID");
        } else {
            paramMap.put("userId", userId);
        }

        m_log.debug("Sending session ID as cookie: " + sessionId);

        final String baseUrl = createBaseUrl(apiPath);
        final String finalUrl = sign(baseUrl, paramMap, cookieMap);

        final HttpMethod method = new PostMethod(finalUrl);
        method.setRequestHeader("Cookie", SessionAttributeKeys.SESSION_ID + "="
                + sessionId);
        if (headerMap.containsKey("Referer")) {
            method.setRequestHeader("Referer", headerMap.get("Referer"));
        }

        m_log.debug("Sending request to: {}", finalUrl);
        return post(method);
    }

    private String publishRaw(final Map<String, String> params)
            throws IOException, ForbiddenException {
        final String baseUrl = createBaseUrl("/api/publishRawFile");
        return post(baseUrl, params);
    }

    /**
     * Returns a URL used to delete a resource from the repository.
     * 
     * @return A URL used to delete the given resource from the repository.
     * @throws IOException
     *             If we can't create the SHA-1.
     */
    private Map<String, String> createDeleteParams() throws IOException {
        final Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("instanceId", String.valueOf(Prefs.getId()));
        return paramMap;
    }

    /**
     * Returns a URL used to publish a resource in the repository.
     * 
     * @param uri
     *            The URI for the file.
     * @param sha1
     *            The SHA1 URN for the file.
     * @param file
     *            The resource to publish.
     * 
     * @return A URL used to publish the given resource in the repository.
     * @throws IOException
     *             If we can't create the SHA-1;
     */
    private Map<String, String> createPublishParams(final File file,
            final URI uri, final URI sha1) throws IOException {
        final String sha1String;
        if (sha1 != null) {
            sha1String = sha1.toASCIIString();
        } else {
            sha1String = Sha1Hasher.createSha1Urn(file).toASCIIString();
        }

        final String uriString;
        if (uri != null) {
            uriString = uri.toASCIIString();
        } else {
            // We just use the SHA-1 if there's no URI specified.
            uriString = sha1String;
        }
        final Map<String, String> params = createBasePublishParams(file);

        addParam(params, "uri", uriString);
        addParam(params, "sha1", sha1String);

        return params;
    }
    
    /**
     * Returns a URL used to publish a resource in the repository.
     * @param uri 
     * 
     * @param resource The resource to publish.
     * @return A URL used to publish the given resource in the repository.
     * @throws IOException If we can't create the SHA-1;
     */
    private Map<String, String> createBasePublishParams(final File file)
            throws IOException {
        final Map<String, String> params = new HashMap<String, String>();

        addParam(params, "title", file.getName());
        addParam(params, "size", String.valueOf(file.length()));
        addParam(params, "instanceId", String.valueOf(Prefs.getId()));
        addParam(params, "timeZone", SystemUtils.USER_TIMEZONE);
        addParam(params, "country", SystemUtils.USER_COUNTRY);
        addParam(params, "language", SystemUtils.USER_LANGUAGE);

        return params;
    }

    private void addParam(final Map<String, String> params, final String key,
            final String value) {
        if (StringUtils.isBlank(key)) {
            m_log.info("Blank key for value: {}", value);
        }
        if (StringUtils.isBlank(value)) {
            m_log.info("Blank value for key: {}", key);
        }
        params.put(key, value);
    }

    private String post(final String baseUrl, final Map<String, String> params)
            throws IOException, ForbiddenException {
        final String url = UriUtils.newUrl(baseUrl, params);
        final HttpMethod method = new PostMethod(url);
        return post(method);
    }

    private String post(final HttpMethod method) throws IOException,
            ForbiddenException {
        method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        method.setRequestHeader("Accept-Encoding", "gzip");
        InputStream is = null;
        try {
            this.m_clientManager.executeMethod(method);
            final int statusCode = method.getStatusCode();
            final StatusLine statusLine = method.getStatusLine();
            final Header encoding = method
                    .getResponseHeader("Content-Encoding");
            if (encoding != null && encoding.getValue().equals("gzip")) {
                m_log.debug("Unzipping body...");
                is = new GZIPInputStream(method.getResponseBodyAsStream());
            } else {
                is = method.getResponseBodyAsStream();
            }
            final String body = IOUtils.toString(is);
            if (StringUtils.isBlank(body)) {
                // Could easily be a post request, which would not have a body.
                m_log.debug("No response body.  Post request?");
            }
            if (statusCode == HttpStatus.SC_FORBIDDEN) {
                final String msg = "NO 200 OK: " + method.getURI() + "\n"
                        + statusLine + "\n" + body;
                m_log.warn(msg);
                throw new ForbiddenException(msg);
            }

            if (statusCode != HttpStatus.SC_OK) {

                final String msg = "NO 200 OK: " + method.getURI() + "\n"
                        + statusLine + "\n" + body;
                m_log.warn(msg);
                throw new IOException(msg);
            } else {
                m_log.debug("Got 200 response...");
            }

            return body;
        } finally {
            IOUtils.closeQuietly(is);
            method.releaseConnection();
        }

    }

    private String sign(final String baseUrl, final Map<String, String> params,
            final Map<String, String> cookieMap) throws ForbiddenException {
        final String key = cookieMap.get(SessionAttributeKeys.KEY);
        if (StringUtils.isBlank(key)) {
            m_log.error("No key");
            throw new ForbiddenException("No key");
        }
        final String preSignatureUrl = UriUtils.newUrl(baseUrl, params);
        final String sig = SecurityUtils.signAndEncode(key, preSignatureUrl);

        return UriUtils.appendParam(preSignatureUrl, "signature", sig);
    }

    private String createBaseUrl(final String path) {
        // Quick check just to remind ourselves we're running against a locally
        // mapped address. This can be the case when we've customized
        // /etc/hosts for testing.
        try {
            final String baseAddress = InetAddress.getByName(
                    ShootConstants.BASE_DOMAIN).getHostAddress();
            if (baseAddress.equals("127.0.0.1")) {
                m_log.warn("Running against local testing machine.");
            }
        } catch (final UnknownHostException e) {
            m_log.warn("Could not resolve base domain -- JUST A CHECK", e);
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(ShootConstants.SERVER_URL);
        sb.append(path);
        return sb.toString();
    }
}
    