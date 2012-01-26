package org.lastbamboo.common.searchers.yahoo;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.rest.ElementProcessingException;
import org.lastbamboo.common.rest.RestResultFactory;
import org.lastbamboo.common.rest.RestResultSources;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestResultsMetadataImpl;
import org.lastbamboo.common.rest.RestSearcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Factory class for creating Yahoo video results.
 */
public final class YahooVideoFactory implements RestResultFactory<YahooVideo>
    {

    private static final Logger LOG = LoggerFactory.getLogger(YahooVideoFactory.class);
    private final RestSearcher<YahooVideo> m_searcher;
    
    /**
     * Creats a new result factory.
     * 
     * @param searcher The searcher that initiated the search.  This is needed
     * in case we need to make future calls to the server for more results.
     */
    public YahooVideoFactory(final RestSearcher<YahooVideo> searcher)
        {
        this.m_searcher = searcher;
        }
    
    public YahooVideo createResult(final Document doc, final Element element, 
        final int index) throws ElementProcessingException
        {
        LOG.debug("Creating new video from: "+element);
        try
            {
            return new YahooVideo(element, index);
            }
        catch (final URISyntaxException e)
            {
            LOG.error("Could not read uris", e);
            throw new ElementProcessingException("Could not create photo", e);
            }
        }

    public RestResultsMetadata<YahooVideo> createResultsMetadata(
        final Document document)
        {
        return new RestResultsMetadataImpl<YahooVideo>(document, "ResultSet", 
            "totalResultsAvailable", RestResultSources.YAHOO_VIDEOS, 
            this.m_searcher);
        }

    }
