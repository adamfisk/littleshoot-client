package org.lastbamboo.common.rest;

import java.util.Collection;
import java.util.LinkedList;

import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.RestResultSources;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.RestResultsImpl;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestResultsMetadataImpl;
import org.lastbamboo.common.rest.stubs.RestResultStub;

import junit.framework.TestCase;

/**
 * Test for rest results class.
 */
public class RestResultsImplTest extends TestCase
    {

    /**
     * Tests adding results method.
     * 
     * @throws Exception If there's an unexpected error.
     */
    public void testAddResults() throws Exception
        {
        final Collection<RestResult> resultsList = new LinkedList<RestResult>();
        final RestResultsMetadata<RestResult> metadata = 
            new RestResultsMetadataImpl<RestResult>(0, 
                RestResultSources.YOU_TUBE, null);
        final RestResults results = 
            new RestResultsImpl<RestResult>(metadata, resultsList);
        
        assertEquals(0, results.getCurrentResults().size());
        
        final Collection<RestResult> resultsList2 = 
            new LinkedList<RestResult>();
        resultsList2.add(new RestResultStub());
        
        results.addResults(resultsList2);
        assertEquals(1, results.getCurrentResults().size());
        }
    }
