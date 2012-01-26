package org.lastbamboo.client.search;

import java.io.File;
import java.net.URI;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.prefs.Preferences;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.client.prefs.PrefKeys;
import org.lastbamboo.client.services.download.DownloadTracker;
import org.lastbamboo.client.services.download.MoverDStateStatusVisitor;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.SearchRequestBean;
import org.littleshoot.util.IoUtils;
import org.littleshoot.util.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor for representing resources in the JSON data format.
 * 
 * @param <T> Class extending {@link RestResult}.
 */
public class JsonSearchResultVisitor<T extends RestResult>
    {

    private final Logger m_log = 
        LoggerFactory.getLogger(JsonSearchResultVisitor.class);
    private final Collection<JSONObject> m_results = 
        new LinkedList<JSONObject>();
    private final String m_escapedJson;
    private final MimeType m_mimeType;
    
    /**
     * Creates a new visitor for converting search results to the JSON data 
     * format.
     * 
     * @param sessionResults The collection of search results to visit.
     * @param resultsPerPage The number of results per page.
     * @param pageIndex The page index to build JSON data for.
     * @param mt The class for determining MIME types.
     * @param downloadTracker The class that keeps track of downloads.
     * @param guid The unique ID for the search.
     */
    public JsonSearchResultVisitor(final SessionResults<T> sessionResults, 
        final int resultsPerPage, final int pageIndex, final MimeType mt,
        final DownloadTracker<MoverDState<Sha1DState<MsDState>>, 
            Downloader<MoverDState<Sha1DState<MsDState>>>> downloadTracker, 
        final UUID guid)
        {
        this.m_mimeType = mt;
        final JSONObject json = new JSONObject();
        
        // Insert commas in the number.
        final int totalResults = sessionResults.getTotalResults(guid);
        final NumberFormat format = NumberFormat.getInstance();
        final String formatted = format.format(totalResults);
        
        final Preferences prefs = Preferences.userRoot ();
        final File path = new File(prefs.get (PrefKeys.DOWNLOAD_DIR, ""));
        if (!path.isDirectory())
            {
            m_log.error("Downloads dir does not exist at: {}", path);
            }
        
        JsonUtils.put(json, "totalResults", totalResults);
        JsonUtils.put(json, "totalResultsFormatted", formatted);
        JsonUtils.put(json, "complete", sessionResults.isComplete(guid));
        JsonUtils.put(json, "downloadPath", path);
        
        addSearchData(json, sessionResults);
        
        final int index = pageIndex * resultsPerPage;
        final Collection<T> results;
        final UUID jsonGuid;
        if (guid == null)
            {
            jsonGuid = sessionResults.getLatestGuid();
            results = sessionResults.getLatest(resultsPerPage, index);
            }
        else
            {
            jsonGuid = new UUID(guid);
            results = sessionResults.getResults(jsonGuid,
                resultsPerPage, index);
            }
        
        JsonUtils.put(json, "guid", jsonGuid.toUrn());
        for (final RestResult result : results)
            {
            addResult(result, downloadTracker);
            }

        try
            {
            json.put("results", this.m_results);
            }
        catch (final JSONException e)
            {
            m_log.warn("Exception inserting resources", e);
            }
        
        final String jsonString = json.toString();
        
        // We need to escape single quotes from the titles because they're
        // interpreted in JavaScript as close quotes.  We do it here because
        // the JSON code will do its own escaping if we do it when inserting
        // into the map.
        this.m_escapedJson = jsonString.replaceAll("'", "\\\\'");
        }
    
    /**
     * Creates an empty JSON response with an error message.
     * 
     * @param errorMessage The error message to include.
     */
    public JsonSearchResultVisitor(final String errorMessage)
        {
        final JSONObject json = new JSONObject();
        JsonUtils.put(json, "totalResults", 0);
        JsonUtils.put(json, "totalResultsFormatted", 0);
        JsonUtils.put(json, "complete", true);
        JsonUtils.put(json, "error", errorMessage);
        try
            {
            json.put("results", this.m_results);
            }
        catch (final JSONException e)
            {
            m_log.warn("Exception inserting resources", e);
            }
        final String jsonString = json.toString();
        
        // We need to escape single quotes from the titles because they're
        // interpreted in JavaScript as close quotes.  We do it here because
        // the JSON code will do its own escaping if we do it when inserting
        // into the map.
        this.m_escapedJson = jsonString.replaceAll("'", "\\\\'");
        this.m_mimeType = null;
        }
    
    private void addSearchData(final JSONObject json, 
        final SessionResults<T> sessionResults)
        {
        final Collection<SearchResults<T>> searches = 
            sessionResults.getAllResults();
        
        final JSONArray array = new JSONArray();
        for (final SearchResults<T> results : searches)
            {
            final SearchRequestBean bean = results.getRequestBean();
            final JSONObject searchJson = new JSONObject();
            
            JsonUtils.put(searchJson, "guid", results.getGuid().toString());
            JsonUtils.put(searchJson, "totalResults", results.getTotalResults());
            JsonUtils.put(searchJson, "keywords", bean.getKeywords());
            
            array.put(searchJson);
            }
        
        try
            {
            json.put("searchData", array);
            }
        catch (final JSONException e)
            {
            m_log.warn("Could not append JSON!!", e);
            }
        }


    private void addResult(final RestResult result, 
        final DownloadTracker<MoverDState<Sha1DState<MsDState>>, 
            Downloader<MoverDState<Sha1DState<MsDState>>>> downloadTracker)
        {
        if (result.getJson() != null)
            {
            addJsonResult(result, downloadTracker);
            }
        else
            {
            addXmlResult(result, downloadTracker);
            }
        }
    
    private void addJsonResult(final RestResult result,
        final DownloadTracker<MoverDState<Sha1DState<MsDState>>, 
            Downloader<MoverDState<Sha1DState<MsDState>>>> downloadTracker)
        {
        final String jsonString = IoUtils.inflateString(result.getJson());
        final JSONObject json;
        try
            {
            json = new JSONObject(jsonString);
            }
        catch (final JSONException e)
            {
            m_log.error("Could not create JSON!!");
            return;
            }
        
        addMimeType(result, json);
        
        final String source = result.getSource().toLowerCase();
        if (source.equals("limewire"))
            {
            addLimeWireData(result, json);
            addDownloadData(result.getUrl(), json, downloadTracker);
            }
        else if (source.equals("littleshoot"))
            {
            addDownloadData(result.getUrl(), json, downloadTracker);
            }
        else if (source.equals("isohunt"))
            {
            if (!hasMimeType(json))
                {
                addIsoHuntMimeType(result, json);
                }
            addDownloadData(result.getUrl(), json, downloadTracker);
            }
        this.m_results.add(json);
        }

    private void addIsoHuntMimeType(final RestResult result, 
        final JSONObject json)
        {
        // Hack for IsoHunt because many titles include the file extension 
        // towards the end.
        final String title = result.getTitle();
        m_log.debug("Testing title: {}", title);
        final String extension = StringUtils.substringAfterLast(title, " ");
        
        m_log.debug("Testing extension: {}", extension);
        final String mimeType = this.m_mimeType.getMimeType("tmp."+extension);
        if (StringUtils.isNotBlank(mimeType))
            {
            m_log.debug("Adding MIME type: {}", mimeType);
            JsonUtils.put(json, "mimeType", mimeType);
            }
        }

    private boolean hasMimeType(final JSONObject json)
        {
        return json.has("mimeType");
        }

    private void addMimeType(final RestResult result, final JSONObject json)
        {
        if (hasMimeType(json))
            {
            return;
            }
        final String mimeType;
        if (StringUtils.isNotBlank(result.getMimeType()))
            {
            mimeType = result.getMimeType();
            }
        else if (this.m_mimeType != null && 
            StringUtils.isNotBlank(result.getTitle()))
            {
            mimeType = this.m_mimeType.getMimeType(result.getTitle());
            }
        else
            {
            mimeType = StringUtils.EMPTY;
            }
        
        if (StringUtils.isNotBlank(mimeType))
            {
            JsonUtils.put(json, "mimeType", mimeType);
            }
        }

    private void addLimeWireData(final RestResult result, final JSONObject json)
        {
        if (result.getNumSources() != -1)
            {
            JsonUtils.put(json, "numSources", result.getNumSources());
            }
        }

    private void addXmlResult(final RestResult result, 
        final DownloadTracker<MoverDState<Sha1DState<MsDState>>, 
            Downloader<MoverDState<Sha1DState<MsDState>>>> downloadTracker)
        {
        final JSONObject json = new JSONObject();
        final String mimeType;
        if (StringUtils.isNotBlank(result.getMimeType()))
            {
            mimeType = result.getMimeType();
            }
        else if (this.m_mimeType != null)
            {
            mimeType = this.m_mimeType.getMimeType(result.getTitle());
            }
        else
            {
            mimeType = StringUtils.EMPTY;
            }
            
        JsonUtils.put(json, "title", result.getTitle());
        JsonUtils.put(json, "size", result.getFileSize());
        JsonUtils.put(json, "uri", result.getUrl());
        JsonUtils.put(json, "thumbnailUrl", result.getThumbnailUrl());
        JsonUtils.put(json, "sha1", result.getSha1Urn());
        JsonUtils.put(json, "source", result.getSource());
        JsonUtils.put(json, "mimeType", mimeType);
        JsonUtils.put(json, "mediaType", result.getMediaType());
        
        if (result.getLengthSeconds() != -1)
            {
            JsonUtils.put(json, "lengthSeconds", result.getLengthSeconds());
            }
        if (StringUtils.isNotBlank(result.getDescription()))
            {
            JsonUtils.put(json, "description", result.getDescription());
            }
        if (StringUtils.isNotBlank(result.getAuthor()))
            {
            JsonUtils.put(json, "author", result.getAuthor());
            }
        if (result.getRating() != -1)
            {
            JsonUtils.put(json, "rating", result.getRating());
            }
        if (result.getNumSources() != -1)
            {
            JsonUtils.put(json, "numSources", result.getNumSources());
            }
        if (result.getThumbnailWidth() != -1)
            {
            JsonUtils.put(json, "thumbWidth", result.getThumbnailWidth());
            }
        if (result.getThumbnailHeight() != -1)
            {
            JsonUtils.put(json, "thumbHeight", result.getThumbnailHeight());
            }
        
        addDownloadData(result.getUrl(), json, downloadTracker);
        this.m_results.add(json);
        }

    private void addDownloadData(final URI uri, final JSONObject json,
        final DownloadTracker<MoverDState<Sha1DState<MsDState>>, 
            Downloader<MoverDState<Sha1DState<MsDState>>>> downloadTracker)
        {
        final Downloader<MoverDState<Sha1DState<MsDState>>> dl;
        if (downloadTracker.hasActiveDownloader(uri))
            {
            dl = downloadTracker.getActiveDownloader(uri);
            }
        else if (downloadTracker.hasCompletedDownloader(uri))
            {
            dl = downloadTracker.getCompletedDownloader(uri);
            }
        else
            {
            return;
            }
        
        //JsonUtils.put(json, "downloadSize", dl.getSize());
        JsonUtils.put(json, "downloadPath", dl.getCompleteFile().getParentFile());
        final MoverDState<Sha1DState<MsDState>> state = dl.getState();
        state.accept (new MoverDStateStatusVisitor (json));
        }
    
    /**
     * Accessor for the JSON string.
     * 
     * @return The string for the visited resources in the JSON data format.
     */
    public String getJson()
        {
        return this.m_escapedJson;
        }

    }
