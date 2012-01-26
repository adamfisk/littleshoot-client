package org.lastbamboo.common.npapi;

import java.io.File;
import java.util.Map;

/**
 * Interface for NPAPI stream data structs.
 */
public interface NpapiStreamData
    {

    String getUrl();

    Map<String, String> getHttpHeaders();

    String getStreamName();

    String getStreamPath();

    String getStreamTempPath();

    File getStreamFile();
    }
