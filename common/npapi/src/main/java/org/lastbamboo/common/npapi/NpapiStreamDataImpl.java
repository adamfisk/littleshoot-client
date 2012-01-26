package org.lastbamboo.common.npapi;

import java.io.File;
import java.util.Map;

/**
 * Struct for NPAPI stream file data.
 */
public class NpapiStreamDataImpl implements NpapiStreamData
    {

    private final String m_url;
    private final Map<String, String> m_httpHeaders;
    private final String m_streamName;
    private final String m_streamPath;
    private final String m_streamTempPath;

    /**
     * Creates a new class for holding stream data.
     * 
     * @param url The URL for the stream.
     * @param httpHeaders The HTTP headers the URL responded with.
     * @param streamName The name of the stream.
     * @param streamPath The stream path on disk.
     * @param streamTempPath The path for the temporary stream on disk.
     */
    public NpapiStreamDataImpl(final String url, 
        final Map<String, String> httpHeaders, final String streamName, 
        final String streamPath, final String streamTempPath)
        {
        this.m_url = url;
        this.m_httpHeaders = httpHeaders;
        this.m_streamName = streamName;
        this.m_streamPath = streamPath;
        this.m_streamTempPath = streamTempPath;
        }

    public String getUrl()
        {
        return this.m_url;
        }

    public Map<String, String> getHttpHeaders()
        {
        return this.m_httpHeaders;
        }

    public String getStreamName()
        {
        return this.m_streamName;
        }

    public String getStreamPath()
        {
        return this.m_streamPath;
        }

    public String getStreamTempPath()
        {
        return this.m_streamTempPath;
        }

    public File getStreamFile()
        {
        return new File (this.m_streamPath);//, this.m_streamName);
        }

    }
