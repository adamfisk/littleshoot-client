package org.lastbamboo.common.searchers.littleshoot;

import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.rest.AbstractRestSearcher;
import org.lastbamboo.common.rest.JsonRestResultBodyProcessor;
import org.lastbamboo.common.rest.JsonRestResultFactory;
import org.lastbamboo.common.rest.RestResultProcessor;
import org.lastbamboo.common.rest.RestResultSources;
import org.littleshoot.util.HttpParamKeys;
import org.littleshoot.util.ShootConstants;
import org.littleshoot.util.UriUtils;

/**
 * Class for searching LittleShoot's REST API.
 */
public class LittleShootSearcher extends AbstractRestSearcher<JsonLittleShootResult>
    {
    
    private static final Logger LOG = LoggerFactory.getLogger(LittleShootSearcher.class);
    private final Map<String, String> m_paramMap;

    public LittleShootSearcher(
        final RestResultProcessor<JsonLittleShootResult> resultProcessor, 
        final UUID uuid, final Map<String, String> paramMap, 
        final String searchString)
        {
        super(resultProcessor, uuid, searchString, 0, 
            RestResultSources.LITTLE_SHOOT);
        
        paramMap.put("itemsPerPage", String.valueOf(100));
        paramMap.put(HttpParamKeys.OS, SystemUtils.OS_NAME);
        paramMap.put(HttpParamKeys.TIME_ZONE, SystemUtils.USER_TIMEZONE);
        this.m_paramMap = paramMap;
        final JsonRestResultFactory<JsonLittleShootResult> resultFactory =
            new JsonLittleShootResultFactory(this);
        this.m_resultBodyProcessor = 
            new JsonRestResultBodyProcessor<JsonLittleShootResult>("results", resultFactory);
        }

    public String createUrlString(final String searchTerms)
        {
        LOG.info("Creating LittleShoot search...");
        final String baseUrl = 
            ShootConstants.SERVER_URL + "/api/search";
        
        this.m_paramMap.put("startPage", String.valueOf(this.m_pageIndex));
        final String url = UriUtils.newUrl(baseUrl, this.m_paramMap);
        LOG.debug("Sending search to URL: "+url);
        return url;
        }

    @Override
    public String toString()
        {
        return getClass().getSimpleName();
        }
    }
