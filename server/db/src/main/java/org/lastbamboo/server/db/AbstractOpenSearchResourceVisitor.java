package org.lastbamboo.server.db;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.server.resource.AudioFileResource;
import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.OnlineInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Abstracts out common functionality for Amazon OpenSearch resource visitors.
 */
public abstract class AbstractOpenSearchResourceVisitor implements
    OpenSearchResourceVisitor
    {

    private final Logger LOG = LoggerFactory.getLogger(AbstractOpenSearchResourceVisitor.class);
    
    protected final Document m_document;

    protected final Node m_feed;
    
    protected static final String ATOM_NS = "http://www.w3.org/2005/Atom";
    
    /**
     * Creates a new visitor.  This immediately calls all the specified 
     * resources to accept the visitor.
     * 
     * @param doc The XML document containing the basic OpenSearch formatting.
     */
    public AbstractOpenSearchResourceVisitor(final Document doc)
        {
        this.m_document = doc;
        this.m_feed = doc.getFirstChild();
        }

    public abstract Element visitAudioFileResource(AudioFileResource afr);

    public abstract Element visitFileResource(FileResource resource);

    public void write(final OutputStream os)
        {
        final TransformerFactory tf = TransformerFactory.newInstance();
        try
            {
            final Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(this.m_document), 
                new StreamResult(os));
            }
        catch (final TransformerConfigurationException e)
            {
            LOG.error("Could not create transformer", e);
            }
        catch (final TransformerException e)
            {
            LOG.error("Could not transform", e);
            }
        }
    
    protected Element createEntry(final MetaFileResource mfr)
        {
        return createEntry(mfr, mfr.getSha1Urn());
        }
    
    protected Element createEntry(final MetaFileResource mfr, 
        final OnlineInstance ur)
        {
        return createEntry(mfr, createIri(mfr, ur));
        }        

    private Element createEntry(final MetaFileResource mfr, final String iri)
        {
        final Element entry = this.m_document.createElementNS(ATOM_NS, "entry");
        appendAtomElement(entry, "title", mfr.getTitle());
        appendAtomElement(entry, "type", mfr.getMimeType());
        appendAtomLinkElement(entry, mfr, iri);
        appendAtomElement(entry, "id", mfr.getSha1Urn());
        return entry;
        }

    private void appendAtomLinkElement(final Element entry, 
        final MetaFileResource resource, final String iri)
        {
        final Element link = 
            this.m_document.createElementNS(ATOM_NS, "link");
        link.setAttribute("rel", "enclosure");
        link.setAttribute("href", iri);
        link.setAttribute("length", Long.toString(resource.getSize()));
        entry.appendChild(link);
        }

    private String createIri(final MetaFileResource mfr, 
        final OnlineInstance ur)
        {
        final StringBuilder sb = new StringBuilder();
        
        // This could be a SIP URI or an HTTP URL, for example, if the user
        // is not behind a firewall.
        sb.append(ur.getBaseUri());
        sb.append("/uri-res/N2R?");
        sb.append(mfr.getSha1Urn());
        return sb.toString();
        }

    private void appendAtomElement(final Element parentElement, 
        final String name, final Date value)
        {
        final DateFormat format = 
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String date = format.format(value);
        appendAtomElement(parentElement, name, date);
        }
    
    private void appendAtomElement(final Element parentElement, 
        final String name, final String value)
        {
        final Element childElement = 
            this.m_document.createElementNS(ATOM_NS, name);
        childElement.appendChild(this.m_document.createTextNode(value));
        parentElement.appendChild(childElement);
        }
    }
