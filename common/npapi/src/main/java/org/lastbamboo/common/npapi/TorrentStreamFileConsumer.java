package org.lastbamboo.common.npapi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that consumes torrent file streams from the plugin.
 */
public class TorrentStreamFileConsumer implements NpapiStreamFileConsumer,
    UrlTorrentData
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final NpapiStreamListener listener;
    
    private final Map<String, NpapiStreamData> m_torrentDataMap = 
        new ConcurrentHashMap<String, NpapiStreamData>();
    
    /**
     * Creates a new consumer for newly downloaded torrents.
     */
    public TorrentStreamFileConsumer(final NpapiStreamListener listener)
        {
        this.listener = listener;
        }

    public void consume(final String url, final Map<String, String> httpHeaders,
        final String streamName, final String streamPath, 
        final String streamTempPath)
        {
        final String contentType = httpHeaders.get("content-type");
        if (StringUtils.isBlank(contentType))
            {
            m_log.warn("No content type in headers: "+ httpHeaders+
                ", with url "+url+", streamName "+streamName+
                ", streamPath "+streamPath+
                ", and streamTempPath "+streamTempPath);
            //return;
            }
        if (!"application/x-bittorrent".equals(contentType))
            {
            m_log.warn("Got non-bittorrent content type: {}", contentType);
            //return;
            }
        
        final NpapiStreamData data = 
            new NpapiStreamDataImpl(url, httpHeaders, streamName, streamPath, 
                streamTempPath);
        
        m_log.info("Notifying listeners...");
        //m_torrentDataMap.put(url, data);
        listener.onStream(data);
        }

    public NpapiStreamData getTorrentData(final String url)
        {
        return this.m_torrentDataMap.get(url);
        }
    }
