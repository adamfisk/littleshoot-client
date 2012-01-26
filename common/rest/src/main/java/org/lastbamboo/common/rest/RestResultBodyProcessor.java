package org.lastbamboo.common.rest;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for processing REST results
 * 
 * @param <T> An instance of a REST result.
 */
public interface RestResultBodyProcessor<T extends RestResult>
    {

    /**
     * Processes rest results contained in the specified input stream.
     * 
     * @param is The input stream containing results.
     * @return The RestResult instances.
     * @throws IOException If there's an IO error reading the data.
     */
    RestResults<T> processResults(InputStream is) 
        throws IOException;
    }
