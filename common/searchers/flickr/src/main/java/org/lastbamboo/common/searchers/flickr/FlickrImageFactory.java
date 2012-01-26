package org.lastbamboo.common.searchers.flickr;

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
 * Class for creating Flickr images.
 */
public class FlickrImageFactory implements RestResultFactory<FlickrImage>
    {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FlickrImageFactory.class);
    private final RestSearcher<FlickrImage> m_searcher;
    //private final FlickrSearcher m_searcher;
    
    /**
     * Creates a new result factory.
     * 
     * @param searcher The searcher that initiated the search.  This is needed
     * in case we need to make future calls to the server for more results.
     */
    public FlickrImageFactory(final RestSearcher<FlickrImage> searcher)
        {
        this.m_searcher = searcher;
        }
    
    public FlickrImage createResult(final Document doc, final Element element, 
        final int index) throws ElementProcessingException
        {
        LOG.debug("Creating new photo from: "+element);
        try
            {
            return new FlickrImage(element);
            }
        catch (final URISyntaxException e)
            {
            LOG.error("Could not read uris", e);
            throw new ElementProcessingException("Could not create photo", e);
            }
        }

    public RestResultsMetadata<FlickrImage> createResultsMetadata(
        final Document document)
        {
        return new RestResultsMetadataImpl<FlickrImage>(document, "photos", 
            "total", RestResultSources.FLICKR, this.m_searcher);
        }

    }
