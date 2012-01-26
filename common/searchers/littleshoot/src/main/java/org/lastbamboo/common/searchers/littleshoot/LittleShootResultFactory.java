package org.lastbamboo.common.searchers.littleshoot;

import java.net.URISyntaxException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.rest.ElementProcessingException;
import org.lastbamboo.common.rest.RestResultFactory;
import org.lastbamboo.common.rest.RestResultSources;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestResultsMetadataImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class for creating LittleShoot results
 */
public class LittleShootResultFactory implements RestResultFactory<LittleShootResult>
    {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(LittleShootResultFactory.class);
    private final LittleShootSearcher m_searcher;
    
    private static final XPathFactory s_xPathFactory = 
        XPathFactory.newInstance();
    private static final XPath s_path = s_xPathFactory.newXPath();
    
    static
        {
        s_path.setNamespaceContext(new LittleShootNamespaceContext());
        }
    
    /**
     * Creates a new result factory.
     * 
     * @param searcher The searcher that initiated the search.  This is needed
     * in case we need to make future calls to the server for more results.
     */
    public LittleShootResultFactory(final LittleShootSearcher searcher)
        {
        this.m_searcher = searcher;
        }
    
    public LittleShootResult createResult(final Document doc, 
        final Element element, final int index) 
        throws ElementProcessingException
        {
        //LOG.debug("Creating new result from: "+element);
        try
            {
            return new LittleShootResult(doc, element, index);
            }
        catch (final URISyntaxException e)
            {
            LOG.error("Could not read uris", e);
            throw new ElementProcessingException("Could not create photo", e);
            }
        }

    public RestResultsMetadata<LittleShootResult> createResultsMetadata(
        final Document document)
        {
        /*
        final String sizePath = "/atom:feed/opensearch:totalResults";
        final int totalResults;
        try
            {
            final String totalResultsString = 
                (String) s_path.evaluate(sizePath, document, XPathConstants.STRING);
            
            LOG.debug("length: "+totalResultsString);
            totalResults = Integer.parseInt(totalResultsString);
            }
        catch (final XPathExpressionException e)
            {
            LOG.error("Bad XPath", e);
            throw new IllegalArgumentException("Bad document", e);
            }
        return new RestResultsMetadataImpl<LittleShootResult>(totalResults, 
            RestResultSources.LITTLE_SHOOT, this.m_searcher);
            */
        return null;
        }

    }
