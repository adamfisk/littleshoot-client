package org.lastbamboo.server.db;

import java.util.Collection;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.server.resource.FileAndInstances;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.OnlineInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Factory for creating Amazon OpenSearch resource visitors.
 */
public class OpenSearchResourceVisitorFactoryImpl implements
    OpenSearchResourceVisitorFactory
    {

    private final Logger LOG = LoggerFactory.getLogger(OpenSearchResourceVisitorFactoryImpl.class);
    private final DocumentBuilder m_builder;
    
    private static final String ATOM_NS = "http://www.w3.org/2005/Atom";

    private static final String OPENSEARCH_NS = 
        "http://a9.com/-/spec/opensearch/1.1/";
    
    /**
     * Creates a new factory for creating OpenSearch resource visitors.
     */
    public OpenSearchResourceVisitorFactoryImpl()
        {
        final DocumentBuilderFactory builderFactory = 
            DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        try
            {
            this.m_builder = builderFactory.newDocumentBuilder();
            }
        catch (final ParserConfigurationException e)
            {
            LOG.error("Could not create builder!!", e);
            throw new IllegalArgumentException("Could not create factory", e);
            }
        }
    
    public OpenSearchResourceVisitor createVisitor(
        final String keywords, final int startIndex, 
        final int itemsPerPage, final Collection<MetaFileResource> resources,
        final int totalResults)
        {
        final Document doc = m_builder.newDocument();
        final Element feed = doc.createElementNS(ATOM_NS, "feed");
        feed.setAttribute("xmlns", ATOM_NS);
        feed.setAttribute("xmlns:opensearch", OPENSEARCH_NS);
        doc.appendChild(feed);
        
        appendOpenSearchElement(doc, feed, "totalResults", totalResults);
        appendOpenSearchElement(doc, feed, "startIndex", startIndex);
        appendOpenSearchElement(doc, feed, "itemsPerPage", itemsPerPage);
        appendAtomElement(doc, feed, "title", "LittleShoot Search: "+keywords);
        return new OpenSearchResourceVisitorImpl(resources, doc);
        }
    
    public OpenSearchResourceVisitor createSourceOnlyVisitor(
        final FileAndInstances fileAndInstances)
        {
        final Set<OnlineInstance> instances = 
            fileAndInstances.getOnlineInstances();
        final Document doc = m_builder.newDocument();
        final Element feed = doc.createElementNS(ATOM_NS, "feed");
        feed.setAttribute("xmlns", ATOM_NS);
        feed.setAttribute("xmlns:opensearch", OPENSEARCH_NS);
        doc.appendChild(feed);
        appendOpenSearchElement(doc, feed, "totalResults", instances.size());
        appendAtomElement(doc, feed, "title", "LittleShoot URN Sources");
        return new OpenSearchSourceOnlyResourceVisitor(fileAndInstances, doc);
        }
    
    private void appendOpenSearchElement(final Document doc, final Element feed, 
        final String name, final int value)
        {
        appendOpenSearchElement(doc, feed, name, Integer.toString(value));
        }

    private void appendOpenSearchElement(final Document doc, final Element feed, 
        final String name, final String value)
        {
        final Element element = 
            doc.createElementNS(OPENSEARCH_NS, "opensearch:"+name);
        element.appendChild(doc.createTextNode(value));
        feed.appendChild(element);
        }
    
    private void appendAtomElement(final Document doc, final Element feed, 
        final String name, final String value)
        {
        final Element element = doc.createElementNS(ATOM_NS, name);
        element.appendChild(doc.createTextNode(value));
        feed.appendChild(element);
        }

    }
