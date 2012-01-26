package org.lastbamboo.common.npapi;

import java.util.Map;

/**
 * Interface for classes that consume processes stream file data.
 */
public interface NpapiStreamFileConsumer
    {

    void consume(String url, Map<String, String> httpHeaders,
        String streamName, String streamPath, String streamTempPath);
    
    }
