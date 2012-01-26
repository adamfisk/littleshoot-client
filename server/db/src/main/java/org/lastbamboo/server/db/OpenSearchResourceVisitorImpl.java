package org.lastbamboo.server.db;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.server.resource.AudioFileResource;
import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.MetaFileResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Creates a visitor for resources that outputs results in Amazon OpenSearch
 * format.
 */
public class OpenSearchResourceVisitorImpl 
    extends AbstractOpenSearchResourceVisitor
    {

    private final Logger LOG = LoggerFactory.getLogger(OpenSearchResourceVisitorImpl.class);

    /**
     * Creates a new visitor for generating results in Amazon OpenSearch format.
     * 
     * @param resources The resources to create results for.
     * @param doc The XML document for the results.
     */
    public OpenSearchResourceVisitorImpl(
        final Collection<MetaFileResource> resources, final Document doc)
        {
        super(doc);
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Creating feed from: "+resources.size()+" resources...");
            }
        
        for (final MetaFileResource fr : resources)
            {
            fr.accept(this);
            }
        }

    @Override
    public Element visitAudioFileResource(final AudioFileResource afr)
        {
        return visitFileResource(afr);
        }

    @Override
    public Element visitFileResource(final FileResource fr)
        {
        LOG.debug("Does nothing for now...");
        return null;
        }

    public Element visitMetaFileResource(final MetaFileResource resource)
        {
        LOG.debug("Visiting file resource...");
        final Element entry = super.createEntry(resource);
        this.m_feed.appendChild(entry);
        return entry;
        }

    }
