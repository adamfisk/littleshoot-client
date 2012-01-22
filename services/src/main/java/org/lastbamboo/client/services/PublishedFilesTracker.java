package org.lastbamboo.client.services;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.input.CountingInputStream;
import org.json.JSONObject;
import org.littleshoot.util.MimeType;

/**
 * Interface that tracks files that are awaiting full publishing -- typically
 * files that are still hashing.
 */
public interface PublishedFilesTracker
    {

    /**
     * Accesses pending file data.
     * 
     * @param mt Class for determining MIME types.
     * @return pending files data.
     */
    JSONObject getPublishedFiles(MimeType mt);

    void addFile(File file, CountingInputStream cis, 
        Map<String, String> paramMap);

    void removeFile(File file);

    }
