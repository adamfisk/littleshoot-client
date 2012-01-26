package org.lastbamboo.client.services.download;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.lang.math.RandomUtils;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.prefs.PrefKeys;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.download.UriResolver;
import org.littleshoot.util.DaemonThread;
import org.littleshoot.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager class for initiating new downloads, applying the appropriate 
 * download listeners, etc.
 */
public class StartDownloadServiceImpl implements StartDownloadService
    {
    /**
     * The log for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    

    public void bitTorrentDownload(final String uri,
        final Map<String, String> paramMap,
        final Map<String, String> cookieMap) {
        final Downloader<MoverDState<Sha1DState<MsDState>>> downloader = 
            LittleShootModule.getDownloadFactory().createBitTorrentDownloader(
                uri, paramMap, cookieMap);

        if (downloader != null) {
            download(downloader);
        }
    }

    public void download(final URI uri, final URI expectedSha1,
        final String name, final long size,
        final Map<String, String> paramMap,
        final Map<String, String> cookieMap) {
        if (expectedSha1 != null && resourceExists(expectedSha1)) {
            m_log.debug("We already have the file.");
            return;
        }
        final String fileName = FileUtils.removeIllegalCharsFromFileName(name)
                + "_" + RandomUtils.nextInt();

        final Preferences prefs = Preferences.userRoot();
        final File incompleteDir = new File(prefs.get(PrefKeys.INCOMPLETE_DIR,
                ""));
        if (!incompleteDir.isDirectory()) {
            m_log.warn("Incomplete dir does not exist at: {}", incompleteDir);
            incompleteDir.mkdirs();
        }
        final File incompleteFile = new File(incompleteDir, fileName);

        m_log.debug("Creating downloader...");

        // Note we create the URI resolver here to allow other implementations
        // to resolve URIs differently.
        final UriResolver resolver = new LittleShootUriResolver();
        final Downloader<MoverDState<Sha1DState<MsDState>>> downloader = 
            LittleShootModule.getDownloadFactory().createDownloader(
                incompleteFile, uri, size, expectedSha1, resolver, 
                LittleShootModule.getRemoteResourceRepository(), 
                LittleShootModule.getFileMapper(), paramMap, cookieMap);

        download(downloader);
    }

    public void limeWireDownload(final File completeFile, final URI sha1,
        final long size, final Map<String, String> paramMap,
        final Map<String, String> cookieMap) {
        if (resourceExists(sha1)) {
            m_log.debug("We already have the file.");
            return;
        }

        final Downloader<MoverDState<Sha1DState<MsDState>>> downloader = 
            LittleShootModule.getDownloadFactory().createLimeWireDownloader(
                completeFile, sha1, size, paramMap, cookieMap);

        download(downloader);
    }

    private void download(
        final Downloader<MoverDState<Sha1DState<MsDState>>> downloader) {
        if (downloader.isStarted()) {
            m_log.debug("Downloader already started.");
            return;
        }
        final Runnable downloadRunner = new Runnable() {
            public void run() {
                try {
                    downloader.start();
                } catch (final Throwable t) {
                    m_log.error("Error starting the download", t);
                }
            }
        };

        final Thread downloadThread = new DaemonThread(downloadRunner,
                "Download-Thread-" + downloader);
        downloadThread.start();
    }

    private boolean resourceExists(final URI sha1) {
        return LittleShootModule.getFileMapper().hasFile(sha1);
    }
}
