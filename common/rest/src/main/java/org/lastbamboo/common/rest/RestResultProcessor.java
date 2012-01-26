package org.lastbamboo.common.rest;

import org.apache.commons.id.uuid.UUID;

/**
 * Class that processes generated REST search results.
 * 
 * @param <T> Class extending {@link RestResult}.
 */
public interface RestResultProcessor<T extends RestResult>
    {

    /**
     * Processes the given search results.
     * 
     * @param uuid The unique <code>UUID</code> for the search.
     * @param results The search results to process.
     */
    void processResults(final UUID uuid, final RestResults<T> results);
    }
