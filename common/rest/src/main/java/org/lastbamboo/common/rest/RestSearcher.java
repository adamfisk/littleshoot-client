package org.lastbamboo.common.rest;

import java.io.IOException;
import java.util.Collection;

/**
 * Interface for classes that search REST APIs.
 * 
 * @param <T> A class extending a REST result.
 */
public interface RestSearcher <T extends RestResult>
    {

    /**
     * Searches a REST API and returns a {@link Collection} of search results
     * instance for the specific REST interface.  
     * 
     * @param searchTerms The terms to search for.
     * @return The {@link Collection} of {@link RestResult} instances
     * @throws IOException If there's an IO error sending the search.
     */
    RestResults<T> search() throws IOException;
    
    /**
     * Create a URL string for the specific REST API using the specified
     * URL-encoded search terms.  
     * 
     * @param encodedSearchTerms The URL-encoded search terms.
     * @return The search URL String for the specific REST API.
     * @throws IOException If there's a read error generating the query.  Some
     * query factories need to do things like get information from the network,
     * for example.
     */
    String createUrlString(final String encodedSearchTerms);

    /**
     * Accessor for the string the searcher is using to search.
     * 
     * @return The string the searcher is using to search.
     */
    String getSearchString();

    }
