package org.lastbamboo.common.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.littleshoot.util.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Class for search result metadata from specific REST sources.  Results from
 * Flickr will have their own metadata, for example.
 * @param <T> Class extending {@link RestResult}
 */
public class RestResultsMetadataImpl<T extends RestResult> 
    implements RestResultsMetadata<T>
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(RestResultsMetadataImpl.class);
    private final int m_totalResults;
    private final RestResultSources m_sourceId;
    private final RestSearcher<T> m_searcher;

    /**
     * Creates a new metadata class for a single source.
     * 
     * @param totalResults The total number of results.
     * @param sourceId The ID of the source.
     * @param searcher The searcher that retrieved these results.
     */
    public RestResultsMetadataImpl(final int totalResults, 
        final RestResultSources sourceId,
        final RestSearcher<T> searcher)
        {
        this.m_totalResults = totalResults;
        this.m_sourceId = sourceId;
        this.m_searcher = searcher;
        }
    
    /**
     * Creates a new metadata class, extracting the metadata from the XML.
     * 
     * @param doc The XML document.
     * @param nodeName The name of the nodes containing individual results.
     * @param totalResultsAttributeName The name of the attribute for the
     * total number of results.
     * @param sourceId The ID of the search source.
     * @param searcher The searcher that retrieved these results.
     */
    public RestResultsMetadataImpl(final Document doc, final String nodeName, 
        final String totalResultsAttributeName, final RestResultSources sourceId,
        final RestSearcher<T> searcher)
        {
        this(extractTotalResults(doc, nodeName, totalResultsAttributeName, 
            searcher), sourceId, searcher);
        }

    /**
     * Creates metadata for the specified XML, simply counting the nodes of
     * the specified name to determine the total number of results.  This is
     * for sources where all of the results are returned in the first query.
     * 
     * @param doc The XML document.
     * @param nodeName The name of the node to count to determine the total
     * results.
     * @param sourceId The ID of the search source.
     * @param searcher The searcher for later use.
     */
    public RestResultsMetadataImpl(final Document doc, final String nodeName, 
        final RestResultSources sourceId, final RestSearcher<T> searcher)
        {
        this(extractTotalResults(doc, nodeName, searcher), sourceId, searcher);
        }

    /**
     * Extracts the total results from just counting the nodes of the 
     * specified name in the XML.
     * 
     * @param doc The XML document.
     * @param nodeName The name of the node.
     * @param searcher The class performing the search.
     * @param totalResultsAttributeName 
     * @return The number of total results.
     */
    private static int extractTotalResults(final Document doc, 
        final String nodeName, final RestSearcher searcher)
        {
        final NodeList resultsList = doc.getElementsByTagName(nodeName);
        final int length = resultsList.getLength();
        if (length == 0)
            {
            LOG.error("Did not get results!!");
            writeToFile(doc, searcher);
            return 0;
            }
        return length;
        }

    private static int extractTotalResults(final Document doc, 
        final String nodeName, final String totalResultsAttributeName,
        final RestSearcher searcher)
        {
        final NodeList resultsList = doc.getElementsByTagName(nodeName);
        final Node results = resultsList.item(0);
        if (results == null)
            {
            LOG.error("Did not get results in: "+XmlUtils.toString(doc));
            writeToFile(doc, searcher);
            return 0;
            }
        final NamedNodeMap attributes = results.getAttributes();
        final Node numResultsNode = 
            attributes.getNamedItem(totalResultsAttributeName);
        
        if (numResultsNode == null)
            {
            LOG.warn("Could not get total results from doc using: "+
                totalResultsAttributeName+"\n"+
                XmlUtils.toString(doc));
            return 0;            
            }
        final String numResultsString = numResultsNode.getNodeValue();
        if (!NumberUtils.isNumber(numResultsString))
            {
            LOG.warn("Could not get total results from doc: \n"+
                XmlUtils.toString(doc));
            return 0;
            }
        return Integer.parseInt(numResultsString);
        }

    public int getTotalResults()
        {
        return this.m_totalResults;
        }

    public RestResultSources getSource()
        {
        return this.m_sourceId;
        }
    
    public RestSearcher<T> getSearcher()
        {
        return this.m_searcher;
        }
    
    /**
     * Just for debugging XML feeds.
     * 
     * @param doc The input stream.
     * @param searcher The class the performed the search.
     * @throws IOException If there's an error reading or writing.
     */
    private static void writeToFile(final Document doc, 
        final RestSearcher searcher)
        {
        final File outFile = new File("restError.xml");
        OutputStream os = null;
        try
            {
            os = new FileOutputStream(outFile);
            final String search = searcher.getSearchString();
            os.write(search.getBytes());
            os.write("\n".getBytes());
            final Transformer t = 
                TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(doc), new StreamResult(os));
            os.close();
            }
        catch (TransformerConfigurationException e)
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
        catch (TransformerFactoryConfigurationError e)
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
        catch (TransformerException e)
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
        catch (final IOException e)
            {       
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
        finally
            {
            IOUtils.closeQuietly(os);
            }
        }

    }
