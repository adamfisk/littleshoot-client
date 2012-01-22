package org.lastbamboo.client.resource;

import org.w3c.dom.Document;

/**
 * Resource visitor that outputs a DOM object.
 */
public interface DomResourceVisitor extends ResourceVisitor
    {

    /**
     * Accessor for the org.w3c.dom.Document instance.
     * 
     * @return The org.w3c.dom.Document instance.
     */
    Document getDocument();
    }
