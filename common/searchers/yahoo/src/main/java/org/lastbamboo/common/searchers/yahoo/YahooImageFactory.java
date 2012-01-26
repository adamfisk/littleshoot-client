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
 * Class for processing XML Elements from the Yahoo image search service.
 */
public final class YahooImageFactory implements RestResultFactory<YahooImage>
    {
    
    private static final Logger LOG = LoggerFactory.getLogger(YahooImageFactory.class);
    private final RestSearcher<YahooImage> m_searcher;

    /**
     * Creats a new result factory.
     * 
     * @param searcher The searcher that initiated the search.  This is needed
     * in case we need to make future calls to the server for more results.
     */
    public YahooImageFactory(final RestSearcher<YahooImage> searcher)
        {
        this.m_searcher = searcher;
        }
    
    public YahooImage createResult(final Document doc, final Element element, 
        final int index) throws ElementProcessingException
        {
        LOG.debug("Creating new image from: "+element);
        try
            {
            return new YahooImage(element, index);
            }
        catch (final URISyntaxException e)
            {
            throw new ElementProcessingException("Could not create photo", e);
            }
        }

    public RestResultsMetadata<YahooImage> createResultsMetadata(
        final Document document)
        {
        return new RestResultsMetadataImpl<YahooImage>(document, "ResultSet", 
            "totalResultsAvailable", RestResultSources.YAHOO_IMAGES,  
            this.m_searcher);
        }

    }
