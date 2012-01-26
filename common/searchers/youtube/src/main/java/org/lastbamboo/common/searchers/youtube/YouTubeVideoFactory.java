package org.lastbamboo.common.searchers.youtube;

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
import org.w3c.dom.NodeList;

/**
 * Factory class for creating YouTube video results.
 */
public final class YouTubeVideoFactory 
    implements RestResultFactory<YouTubeVideoImpl>
    {

    private static final Logger LOG = LoggerFactory.getLogger(YouTubeVideoFactory.class);
    private final RestSearcher<YouTubeVideoImpl> m_searcher;
    
    /**
     * Creates a new result factory.
     * 
     * @param searcher The searcher that initiated the search.  This is needed
     * in case we need to make future calls to the server for more results.
     */
    public YouTubeVideoFactory(final RestSearcher<YouTubeVideoImpl> searcher)
        {
        this.m_searcher = searcher;
        }

    public YouTubeVideoImpl createResult(final Document doc, 
        final Element element, final int index) 
        throws ElementProcessingException
        {
        LOG.debug("Creating new video from: "+element);
        try
            {
            return new YouTubeVideoImpl(element);
            }
        catch (final URISyntaxException e)
            {
            LOG.error("Could not read uris", e);
            throw new ElementProcessingException("Could not create photo", e);
            }
        }

    public RestResultsMetadata<YouTubeVideoImpl> createResultsMetadata(
        final Document document)
        {
        final NodeList children = 
            document.getElementsByTagName("video");
        return new RestResultsMetadataImpl<YouTubeVideoImpl>(
            children.getLength(), RestResultSources.YOU_TUBE, this.m_searcher);
        }

    }
