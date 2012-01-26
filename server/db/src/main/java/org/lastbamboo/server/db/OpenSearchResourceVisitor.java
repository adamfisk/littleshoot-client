package org.lastbamboo.server.db;

import java.io.OutputStream;

import org.lastbamboo.server.resource.ResourceVisitor;
import org.w3c.dom.Element;

/**
 * Interface for {@link ResourceVisitor}s that format resources as Amazon
 * OpenSearch results.
 */
public interface OpenSearchResourceVisitor extends ResourceVisitor<Element>
    {

    /**
     * Writes the resources to the {@link OutputStream}.
     * 
     * @param os The stream to write to.
     */
    public void write(final OutputStream os);
    }
