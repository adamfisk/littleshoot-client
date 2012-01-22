package org.lastbamboo.client.services.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.littleshoot.util.InputStreamHandler;
import org.littleshoot.util.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Processes an input stream containing LittleShoot download source XML in 
 * Amazon OpenSearch format.
 */
public class LittleShootSourcesInputStreamHandler implements InputStreamHandler
    {
    
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    private final XPathFactory m_xPathFactory = 
        XPathFactory.newInstance();
    private final XPath m_path = m_xPathFactory.newXPath();
    
    private final String m_xPathString = "/feed/entry/link";
    
    private final Collection<URI> m_uris = new HashSet<URI>();

    public void handleInputStream(final InputStream is) throws IOException
        {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db;
        try
            {
            db = dbf.newDocumentBuilder();
            }
        catch (final ParserConfigurationException e)
            {
            LOG.error("Misconfigured parser", e);
            throw new RuntimeException("Bad parser config", e);
            }
        
        try
            {
            final Document doc = db.parse(is);
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Got sources doc: "+XmlUtils.toString(doc));
                }
            
            final NodeList uris = 
                (NodeList) m_path.evaluate(m_xPathString, doc, 
                    XPathConstants.NODESET);
            
            LOG.debug("Found "+uris.getLength()+" nodes...");
            for (int i = 0; i < uris.getLength(); i++)
                {
                final NamedNodeMap attributeMap = uris.item(i).getAttributes();
                final Node href = attributeMap.getNamedItem("href");
                LOG.debug("Found node value: "+href.getNodeValue());
                final URI curUri = new URI(href.getNodeValue());
                this.m_uris.add(curUri);
                }
            }
        catch (final SAXException e)
            {
            LOG.warn("Could not parse XML", e);
            final IOException ioe = new IOException("Could not parse XML");
            ioe.initCause(e);
            throw ioe;
            }
        catch (final XPathExpressionException e)
            {
            LOG.warn("Bad XPath", e);
            final IOException ioe = new IOException("Bad XPath");
            ioe.initCause(e);
            throw ioe;
            }
        catch (final URISyntaxException e)
            {
            LOG.warn("Bad URI", e);
            final IOException ioe = new IOException("Bad URI");
            ioe.initCause(e);
            throw ioe;
            }
        }

    public Collection<URI> getUris()
        {
        return this.m_uris;
        }

    }
