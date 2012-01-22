package org.lastbamboo.client.services;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.prefs.Prefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks a directory for torrents.
 */
public class TorrentDirectoryTracker {

    private final Logger m_log = LoggerFactory.getLogger(getClass());

    /**
     * Creates a new tracker for the torrent directory.
     * 
     * @param downloadFactory The class for creating new downloaders.
     */
    public TorrentDirectoryTracker() {
        startTracking();
    }

    private void startTracking() {
        final File torrentDir = Prefs.getTorrentDir();
        final File processedTorrentDir = new File(torrentDir.getParentFile(),
                "processedTorrents");
        if (!processedTorrentDir.isDirectory()) {
            if (!processedTorrentDir.mkdirs()) {
                m_log.error("Could not create torrent dir");
                return;
            }
        }

        final Timer timer = new Timer("Torrent-Directory-Timer", true);

        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                final File[] files = torrentDir.listFiles();
                if (!(files.length == 0)) {
                    processFiles(Arrays.asList(files), processedTorrentDir);
                }
            }
        };

        timer.schedule(task, 0, 2000);
    }

    private void processFiles(final Collection<File> files,
            final File processedDir) {
        for (final File file : files) {
            try {
                final File movedFile = new File(processedDir, file.getName());
                if (movedFile.isFile()) {
                    if (!movedFile.delete()) {
                        m_log.warn("Could not delete file that's in the way!");

                        // Ignore the new one -- not much else to do.
                        if (!file.delete()) {
                            m_log.warn("Could not delete new file!!");
                        }
                        continue;
                    }
                }

                FileUtils.moveFileToDirectory(file, processedDir, true);
                final String uri = movedFile.toURI().toASCIIString();

                // Creating torrent downloaders also starts them.
                LittleShootModule.getDownloadFactory()
                        .createBitTorrentDownloader(uri, movedFile);
            } catch (final IOException e) {
                m_log.error("Could not handle torrent from file: " + file, e);
            }
        }
    }
}
