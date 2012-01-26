package org.lastbamboo.common.searchers.limewire;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.SystemUtils;
import org.lastbamboo.common.rest.RestResultProcessor;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.RestSearcher;
import org.lastbamboo.common.rest.SafeFilter;
import org.lastbamboo.common.rest.SearchRequestBean;
import org.littleshoot.util.ResourceTypeTranslatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.limegroup.gnutella.util.QueryUtils;

/**
 * Class for searching LimeWire.
 */
public class LimeWireSearcher implements RestSearcher<LimeWireJsonResult>, 
    LimeWireSearchListener
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final RestResultProcessor<LimeWireJsonResult> m_resultProcessor;
    private final UUID m_uuid;
    private final String m_keywords;
    private final LimeWire m_limeWire;
    private final SearchRequestBean m_searchBean;
    private final LimeWireResults m_results;
    private final Set<String> m_typeSet = new HashSet<String>();
    private boolean m_enabled;

    private static final Set<String> s_unsafeTypes = new HashSet<String>();

    static 
        {
        s_unsafeTypes.add("video");
        s_unsafeTypes.add("image");
        };
        
    /**
     * Creates a new searcher for searching LimeWire.
     * 
     * @param limeWire The interface to the LimeWire backend.
     * @param resultProcessor The class that processes any results we receive.
     * @param uuid The ID of the search.
     * @param bean The search data.
     */
    public LimeWireSearcher(
        final LimeWire limeWire,
        final RestResultProcessor<LimeWireJsonResult> resultProcessor, 
        final UUID uuid, final SearchRequestBean bean)
        {
        this.m_limeWire = limeWire;
        this.m_enabled = limeWire.isEnabled();
        this.m_resultProcessor = resultProcessor;
        this.m_uuid = uuid;
        this.m_searchBean = bean;
        
        // We need to normalize the query before sending it out.
        this.m_keywords = QueryUtils.removeIllegalChars(bean.getKeywords());
        this.m_results = new LimeWireResults(this, bean.isLimeWire());

        if (bean.isApplications())
            {
            m_typeSet.add(ResourceTypeTranslatorImpl.APPLICATION_TYPE);
            if (SystemUtils.IS_OS_MAC)
                {
                m_typeSet.add(ResourceTypeTranslatorImpl.MAC_APPLICATION_TYPE);
                }
            else if (SystemUtils.IS_OS_WINDOWS)
                {
                m_typeSet.add(ResourceTypeTranslatorImpl.WINDOWS_APPLICATION_TYPE);
                }
            else if (SystemUtils.IS_OS_LINUX)
                {
                m_typeSet.add(ResourceTypeTranslatorImpl.LINUX_APPLICATION_TYPE);
                }
            }
        if (bean.isAudio())
            {
            m_typeSet.add(ResourceTypeTranslatorImpl.AUDIO_TYPE);
            }
        if (bean.isDocuments())
            {
            m_typeSet.add(ResourceTypeTranslatorImpl.DOCUMENT_TYPE);
            m_typeSet.add(ResourceTypeTranslatorImpl.ARCHIVE_TYPE);
            }
        if (bean.isImages())
            {
            m_typeSet.add(ResourceTypeTranslatorImpl.IMAGE_TYPE);
            }
        if (bean.isVideo())
            {
            m_typeSet.add(ResourceTypeTranslatorImpl.VIDEO_TYPE);
            }
        
        // This just makes sure we submit an entry to the result processor
        // so it can check out status.
        m_resultProcessor.processResults(m_uuid, m_results);
        }

    public RestResults<LimeWireJsonResult> search() throws IOException
        {
        this.m_limeWire.search(this.m_uuid, this.m_keywords, this, 
            this.m_searchBean);
        return this.m_results;
        }
    
    public String createUrlString(final String encodedSearchTerms) 
        {
        throw new UnsupportedOperationException("Not supported");
        }

    public String getSearchString()
        {
        return this.m_keywords;
        }

    public void onSearchResult(final LimeWireJsonResult result)
        {
        m_log.debug("Processing search result...");
        if (this.m_searchBean.isSafeSearch() && !isSafe(result))
            {
            m_log.debug("Filtering 'unsafe' result: {}", result.getTitle());
            return;
            }
         
        final String mediaType = result.getMediaType();
        if (this.m_typeSet.contains(mediaType.trim()))
            {
            this.m_results.addResult(result);
            
            // This call just "tricks" the result processor into re-processing
            // data for these results.
            m_resultProcessor.processResults(m_uuid, m_results);
            }
        }

    private boolean isSafe(final LimeWireJsonResult result)
        {
        final String type = result.getMediaType();
        if (!s_unsafeTypes.contains(type)) 
            {
            m_log.debug("Type is safe");
            return true;
            }
        final String title = result.getTitle();
        return SafeFilter.isSafe(title);
        }

    public void onResultsChange()
        {
        this.m_results.sort();
        }

    public boolean isEnabled()
        {
        return this.m_enabled;
        }

    }
