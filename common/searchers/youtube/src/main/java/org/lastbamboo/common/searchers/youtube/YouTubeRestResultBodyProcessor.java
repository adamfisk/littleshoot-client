package org.lastbamboo.common.searchers.youtube;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.IOUtils;
import org.lastbamboo.common.rest.RestResultBodyProcessor;
import org.lastbamboo.common.rest.RestResultSources;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.RestResultsImpl;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestResultsMetadataImpl;
import org.lastbamboo.common.rest.RestSearcher;
import org.littleshoot.util.xml.XPathUtils;
import org.littleshoot.util.xml.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for processing GData results from YouTube.
 */
public class YouTubeRestResultBodyProcessor 
    implements RestResultBodyProcessor<YouTubeGDataVideo>
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final DocumentBuilder m_documentBuilder;

    private final RestSearcher<YouTubeGDataVideo> m_searcher;
    
    /**
     * Creates a new class for processing result bodies from YouTube.
     * 
     * @param searcher The class for searching YouTube.
     */
    public YouTubeRestResultBodyProcessor(
        final RestSearcher<YouTubeGDataVideo> searcher)
        {
        this.m_searcher = searcher;
        //this.m_resultFactory = searcher;
        final DocumentBuilderFactory factory = 
            DocumentBuilderFactory.newInstance();
        
        // NOTE: The "right" way to do this is to make it namespace aware and
        // to implement specialized namespace-interpreting interfaces.
        // See: http://blog.davber.com/2006/09/17/xpath-with-namespaces-in-java/
        //factory.setNamespaceAware(true);
        factory.setValidating(false);
        try
            {
            this.m_documentBuilder = factory.newDocumentBuilder();
            }
        catch (final ParserConfigurationException e)
            {
            m_log.error("Could not create doc builder", e);
            throw new IllegalArgumentException(
                "Could not create doc builder", e);
            } 
        }

    public RestResults<YouTubeGDataVideo> processResults(final InputStream is) 
        throws IOException
        {
        final Collection<YouTubeGDataVideo> results = 
            new LinkedList<YouTubeGDataVideo>();
        Document doc = null;
        try
            {
            doc = this.m_documentBuilder.parse(is);
            final NodeList children = doc.getElementsByTagName("entry");
            for (int i = 0; i < children.getLength(); i++)
                {
                final Node node = children.item(i);
                try
                    {
                    final YouTubeGDataVideo result = 
                        new YouTubeGDataVideo((Element) node);
                    m_log.debug("Adding result: {}", result);
                    results.add(result);
                    }
                catch (final URISyntaxException e)
                    {
                    m_log.error("Could not process element", e);
                    }
                }
            
            if (children.getLength() == 0)
                {
                m_log.debug("No results.  Received:\n"+XmlUtils.toString(doc));
                }
            
            final XPathUtils utils = XPathUtils.newXPath(doc);
            final String totalResultsXPath = "/feed/totalResults/text()";
            final String totalResultsString = utils.getString(totalResultsXPath);
            int totalResults = Integer.parseInt(totalResultsString);
            if (totalResults > 999)
                {
                // There's a 999 result limit on what you can actually 
                // page through.
                totalResults = 999;
                }
            final RestResultsMetadata<YouTubeGDataVideo> metadata =
                new RestResultsMetadataImpl<YouTubeGDataVideo>(totalResults, 
                    RestResultSources.YOU_TUBE, this.m_searcher);
            return new RestResultsImpl<YouTubeGDataVideo>(metadata, results);
            }
        catch (final SAXException e)
            {
            m_log.warn("Could not parse document.", e);
            //writeToFile(body);
            return null;
            }
        catch (final XPathExpressionException e)
            {
            m_log.warn("Error in XPath", e);
            return null;
            }
        catch (final RuntimeException e)
            {
            m_log.warn("Runtime error.", e);
            writeToFile(doc);
            return null;
            }
        }
    
    /*
    public RestResults<YouTubeGDataVideo> processResults(final InputStream is) 
        throws IOException
        {
        final String body = IOUtils.toString(is);
        //writeToFile(body);
        m_log.debug("Reading body...");
        final Collection<YouTubeGDataVideo> results = 
            new LinkedList<YouTubeGDataVideo>();
        try
            {
            final InputSource source = new InputSource(new StringReader(body));
            final Document doc = this.m_documentBuilder.parse(source);
            //final Document doc = this.m_documentBuilder.parse(is);
            final XPathUtils utils = XPathUtils.newXPath(doc);
            final String titlesXPath = "/feed/entry/title";
            final String linksXPath = "/feed/entry/link[@rel='alternate']/@href";
            final String thumbnailXPath = "/feed/entry/group/thumbnail[1]/@url";
            final String thumbnailWidthXPath = "/feed/entry/group/thumbnail[1]/@width";
            final String thumbnailHeightXPath = "/feed/entry/group/thumbnail[1]/@height";
            
            final String lengthXPath = "/feed/entry/group/duration/@seconds";
            final String authorXPath = "/feed/entry/author/name";
            final String descriptionXPath = "/feed/entry/group/description";
            
            final NodeList titles = utils.getNodes(titlesXPath);
            final NodeList links = utils.getNodes(linksXPath);
            final NodeList thumbs = utils.getNodes(thumbnailXPath);
            final NodeList thumbsWidth = utils.getNodes(thumbnailWidthXPath);
            final NodeList thumbsHeight = utils.getNodes(thumbnailHeightXPath);
            final NodeList lengths = utils.getNodes(lengthXPath);
            final NodeList authors = utils.getNodes(authorXPath);
            final NodeList descriptions = utils.getNodes(descriptionXPath);
            
            m_log.debug("Found thumbs: {}", thumbs.getLength());
            for (int i = 0; i < links.getLength(); i++)
                {
                final String title = titles.item(i).getTextContent()+".flv";
                
                final String fullLink = links.item(i).getTextContent();
                final String vid = StringUtils.substringAfter(fullLink, "v=");
                final String link = 
                    "http://www.youtube.com/v/"+vid+"&fmt=18";
                final String thumb = thumbs.item(i).getTextContent();
                final int thumbWidth = 
                    Integer.parseInt(thumbsWidth.item(i).getTextContent());
                final int thumbHeight = 
                    Integer.parseInt(thumbsHeight.item(i).getTextContent());
                final String length = lengths.item(i).getTextContent();
                final String author = authors.item(i).getTextContent();
                final String description = 
                    descriptions.item(i).getTextContent().trim();
                if (!NumberUtils.isNumber(length))
                    {
                    m_log.warn("Not a number: {}", length);
                    continue;
                    }
                try
                    {
                    final YouTubeGDataVideo video = new YouTubeGDataVideo(
                        title, link, thumb, thumbWidth, thumbHeight, 
                        Integer.parseInt(length), author, 
                        description);
                    results.add(video);
                    }
                catch (final URISyntaxException e)
                    {
                    m_log.warn("Invalid URI", e);
                    }
                }
            
            final String totalResultsXPath = "/feed/totalResults/text()";
            final String totalResultsString = utils.getString(totalResultsXPath);
            int totalResults = Integer.parseInt(totalResultsString);
            if (totalResults > 999)
                {
                // There's a 999 result limit on what you can actually 
                // page through.
                totalResults = 999;
                }
            final RestResultsMetadata<YouTubeGDataVideo> metadata =
                new RestResultsMetadataImpl<YouTubeGDataVideo>(totalResults, 
                    RestResultSources.YOU_TUBE, this.m_searcher);
            return new RestResultsImpl<YouTubeGDataVideo>(metadata, results);
            }
        catch (final SAXException e)
            {
            m_log.warn("Could not parse document.  Body:\n"+body, e);
            writeToFile(body);
            }
        catch (final XPathExpressionException e)
            {
            m_log.warn("Error in XPath", e);
            }
        return null;
        }
        */

    private void writeToFile(final Document doc) throws IOException
        {
        if (doc == null) return;
        final File outFile = new File("YouTubeErrorResults.xml");
        outFile.delete();
        final OutputStream os = new FileOutputStream(outFile);
        final Reader is = new StringReader(XmlUtils.toString(doc));
        IOUtils.copy(is, os);
        os.flush();
        os.close();
        is.close();
        }

    /**
     * Just for debugging XML feeds.
     * 
     * @param is The input stream.
     * @throws IOException If there's an error reading or writing.
     */
    private void writeToFile(final String str) throws IOException
        {
        final File outFile = new File("YouTubeErrorResults.xml");
        outFile.delete();
        final OutputStream os = new FileOutputStream(outFile);
        final Reader is = new StringReader(str);
        IOUtils.copy(is, os);
        os.flush();
        os.close();
        is.close();
        }
    }
