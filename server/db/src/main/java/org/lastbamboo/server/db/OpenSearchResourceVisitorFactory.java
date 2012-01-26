package org.lastbamboo.server.db;

import java.util.Collection;

import org.lastbamboo.server.resource.FileAndInstances;
import org.lastbamboo.server.resource.MetaFileResource;

/**
 * Interface for factories for creating Amazon OpenSearch XML resource visitors.
 */
public interface OpenSearchResourceVisitorFactory
    {

    /**
     * Creates a new resource visitor.
     * 
     * @param keywords The keywords to search for.
     * @param startIndex The start index of results to return.
     * @param itemsPerPage The number of items to return.
     * @param resources The resource to create a visitor for.
     * @param totalResults The total number of results available.
     * @return The new visitor.
     */
    OpenSearchResourceVisitor createVisitor(String keywords, int startIndex, 
        int itemsPerPage, Collection<MetaFileResource> resources, 
        int totalResults);

    /**
     * Creates a new resource visitor that returns the resources in OpenSearch
     * format with only the URI data for the source of the resource.
     * 
     * @param fileAndUsers The resources to create an OpenSearch response for.
     * @return The new visitor.
     */
    OpenSearchResourceVisitor createSourceOnlyVisitor(
        FileAndInstances fileAndUsers);

    }
