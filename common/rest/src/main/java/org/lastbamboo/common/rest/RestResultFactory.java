package org.lastbamboo.common.rest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Interface for classes that create REST results from XML.
 * 
 * @param <R> A type extending a REST result.
 */
public interface RestResultFactory <R extends RestResult>
    {

    /**
     * Processes a single XML element.
     * 
     * @param doc The enclosing XML document.
     * @param element The node to use to create the REST result.
     * @param index The index of this node in the parent document
     * @return The result generated from the XML element.
     * @throws ElementProcessingException If it cannot be processed for any
     * reason.
     */
    R createResult(Document doc, Element element, int index) 
        throws ElementProcessingException;

    /**
     * Creates metadata about the set of results.
     * 
     * @param document The XML results.
     * @return The metadata about the results.
     */
    RestResultsMetadata<R> createResultsMetadata(Document document);

    }
