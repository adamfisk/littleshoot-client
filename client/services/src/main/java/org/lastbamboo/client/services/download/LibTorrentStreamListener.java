package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.common.npapi.NpapiStreamData;
import org.lastbamboo.common.npapi.NpapiStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that manages reading torrent files downloaded from the plugin.
 */
public class LibTorrentStreamListener implements NpapiStreamListener {

    private final Logger m_log = LoggerFactory.getLogger(getClass());

    public void onStream(final NpapiStreamData data) {
        m_log.debug("Got stream!!");
        final Map<String, String> headers = data.getHttpHeaders();
        m_log.debug("With headers: {}", headers);

        // This URI often will contain illegal characters, so we keep it as
        // a string.
        final String uri = data.getUrl();
        final File torrentFile = data.getStreamFile();

        if (torrentFile.length() == 0L) {
            m_log.error("No data in torrent file. Ignoring: {}",
                    torrentFile.getAbsolutePath());
            return;
        }
        try {
            // Creating torrent downloaders also starts them.
            LittleShootModule.getDownloadFactory().createBitTorrentDownloader(
                uri, torrentFile);
        } catch (final IOException e) {
            m_log.error("Could not handle torrent from URI: " + uri, e);
        }
    }
}
