package org.lastbamboo.common.searchers.isohunt;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.rest.AbstractRestSearcher;
import org.lastbamboo.common.rest.JsonRestResultBodyExtraProcessor;
import org.lastbamboo.common.rest.JsonRestResultBodyProcessor;
import org.lastbamboo.common.rest.JsonRestResultFactory;
import org.lastbamboo.common.rest.RestResultProcessor;
import org.lastbamboo.common.rest.RestResultSources;
import org.littleshoot.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for searching IsoHunt's REST API.
 */
public class IsoHuntSearcher extends AbstractRestSearcher<JsonIsoHuntResult> 
    implements JsonRestResultBodyExtraProcessor
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final Map<String, String> m_paramMap;

    public IsoHuntSearcher(
        final RestResultProcessor<JsonIsoHuntResult> resultProcessor, 
        final UUID uuid, final String searchString)
        {
        super(resultProcessor, uuid, searchString, 1, 
            RestResultSources.ISO_HUNT);
        
        this.m_paramMap = new HashMap<String, String>();
        final JsonRestResultFactory<JsonIsoHuntResult> resultFactory =
            new JsonIsoHuntResultFactory(this);
        this.m_resultBodyProcessor = 
            new JsonRestResultBodyProcessor<JsonIsoHuntResult>("items", "list", 
                "", resultFactory, this);
        }

    public String createUrlString(final String searchTerms)
        {
        final String baseUrl = "http://isohunt.com/js/json.php";
        
        // IsoHunt's start param is the actual index, not the page.  So we need
        // to multiply by the page size and add 1, since it starts at 1.
        final int start = ((this.m_pageIndex - 1) * 100) + 1;
        this.m_paramMap.put("ihq", searchTerms);
        this.m_paramMap.put("start", String.valueOf(start));
        this.m_paramMap.put("sort", "seeds");
        final String url = UriUtils.newUrl(baseUrl, this.m_paramMap);
        m_log.debug("Sending search to URL: "+url);
        return url;
        }

    @Override
    public String toString()
        {
        return getClass().getSimpleName();
        }

    public void noBaseNode(final JSONObject json)
        {
        // This will happen when there are no results.  Just make sure the
        // total results is zero.
        try
            {
            final int totalResults = json.getInt("total_results");
            if (totalResults != 0)
                {
                m_log.error("No base node with non-zero total: "+json);
                }
            }
        catch (final JSONException e)
            {
            m_log.error("Could not access total results in: " + json, e);
            }
        
        }
    }
