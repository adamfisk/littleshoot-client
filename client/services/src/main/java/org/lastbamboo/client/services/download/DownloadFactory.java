package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.lastbamboo.client.services.FileMapper;
import org.lastbamboo.client.services.RemoteResourceRepository;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.download.UriResolver;

/**
 * Interface for classes that can create new download instances.
 */
public interface DownloadFactory
    {

    /**
     * Creates a new downloader and adds it to any tracking classes.
     * @param sessionId The ID of the user's session.
     * @param file The file to download to.
     * @param uri The URI to download.
     * @param size The size of the file in bytes.
     * @param expectedUrn The expected SHA-1 URN for the downloaded file.
     * @param paramMap The {@link Map} of HTTP request parameters for the
     * request that initiated the download.
     * @param cookieMap The map of cookies from the original HTTP request.
     * @return The new downloader.
     */
    Downloader<MoverDState<Sha1DState<MsDState>>> createDownloader(File file,
        URI uri, long size, URI expectedUrn, UriResolver uriResolver,
        RemoteResourceRepository remoteRepository, FileMapper mapper, 
        Map<String, String> paramMap, Map<String, String> cookieMap);
    
    /*
    Downloader<MoverDState<Sha1DState<MsDState>>> createLimeWireDownloader(
        File completeFile, URI expectedSha1, long size, 
        Map<String, String> paramMap, Map<String, String> cookieMap);
    */
    
    Downloader<MoverDState<Sha1DState<MsDState>>> createBitTorrentDownloader(
        String uri, Map<String, String> paramMap, 
        Map<String, String> cookieMap);

    Downloader<MoverDState<Sha1DState<MsDState>>> createBitTorrentDownloader(
        String uri, File torrentFile) throws IOException;

    Downloader<MoverDState<Sha1DState<MsDState>>> createBitTorrentResumeDownloader(
        String uri, File torrentFile, File fullIncompleteDir, int torrentState) 
        throws IOException;
    }
