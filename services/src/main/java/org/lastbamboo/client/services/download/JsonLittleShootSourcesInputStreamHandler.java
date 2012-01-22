package org.lastbamboo.client.services.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.littleshoot.util.IoExceptionWithCause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes an input stream containing LittleShoot download sources in JSON
 * format.
 */
public class JsonLittleShootSourcesInputStreamHandler {

    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private URI m_sha1;

    public Collection<URI> handleInputStream(final InputStream is)
            throws IOException {
        final String jsonString = IOUtils.toString(is);
        return handleString(jsonString);
    }

    public Collection<URI> handleString(final String jsonString)
        throws IOException {
        m_log.debug("Got JSON: {}", jsonString);
        final JSONObject json;
        try {
            json = new JSONObject(jsonString);
        } catch (final JSONException e) {
            m_log.warn("Could not read JSON from: " + jsonString, e);
            throw new IoExceptionWithCause("Could not read JSON", e);
        }
        final JSONArray jsonResults;
        try {
            jsonResults = json.getJSONArray("urls");
        } catch (final JSONException e) {
            m_log.warn("Could not get results array from: " + jsonString, e);
            throw new IoExceptionWithCause("Could not get results array", e);
        }

        try {
            this.m_sha1 = new URI(json.getString("sha1"));
        } catch (final JSONException e) {
            // The SHA-1 will just be null in this case.
            m_log.warn("Did not get SHA-1 in download data: " + jsonString, e);
        } catch (final URISyntaxException e) {
            m_log.warn("Could not parse SHA-1 in download data: " + jsonString,
                    e);
        }
        final Collection<URI> uris = new LinkedHashSet<URI>();
        for (int i = 0; i < jsonResults.length(); i++) {
            try {
                final String urlString = jsonResults.getString(i);
                m_log.info("Adding URI: {}", urlString);
                try {
                    uris.add(new URI(urlString));
                } catch (final URISyntaxException e) {
                    m_log.warn("Could not parse URI: " + urlString, e);
                }
            } catch (final JSONException e) {
                m_log.warn("Could not parse JSON", e);
            }
        }
        return uris;
    }

    public URI getSha1() {
        return this.m_sha1;
    }

}
