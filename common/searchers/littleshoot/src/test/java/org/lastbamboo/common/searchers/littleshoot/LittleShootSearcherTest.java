package org.lastbamboo.common.searchers.littleshoot;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.SystemUtils;
import org.junit.Test;
import org.lastbamboo.common.rest.RestResultProcessor;
import org.lastbamboo.common.rest.RestResults;
import org.littleshoot.util.HttpParamKeys;
import org.littleshoot.util.ShootConstants;

/**
 * Tests the LittleShoot searcher.
 */
public class LittleShootSearcherTest
    {

    @Test public void testSearching() throws Exception
        {
    
        final RestResultProcessor<JsonLittleShootResult> processor =
            new RestResultProcessor<JsonLittleShootResult>()
            {
            public void processResults(final UUID uuid, 
                final RestResults<JsonLittleShootResult> results)
                {
                System.out.println("Got results: " + results);
                }
            };
            
        final Map<String, String> params = new HashMap<String, String>();
        params.put(HttpParamKeys.KEYWORDS, "test");
        params.put(HttpParamKeys.APPLICATIONS, "true");
        params.put(HttpParamKeys.GROUP_NAME, ShootConstants.WORLD_GROUP);
        params.put(HttpParamKeys.START_PAGE, "0");
        params.put(HttpParamKeys.USER_ID , "2974829");
        params.put(HttpParamKeys.ITEMS_PER_PAGE, "20");
        params.put(HttpParamKeys.OS, SystemUtils.OS_NAME);
        params.put("audio", "true");
        params.put("documents", "true");
        params.put("images", "true");
        params.put("video", "true");
        
        
        // We just make sure no exceptions are thrown.
        final LittleShootSearcher searcher = 
            new LittleShootSearcher(processor, UUID.randomUUID(), params, 
                "test");
        
        searcher.search();
        }
    }
