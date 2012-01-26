package org.lastbamboo.client.services.download;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Map.Entry;

import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.DownloaderState;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.download.VisitableDownloader;
import org.littleshoot.util.Pair;

/**
 * The interface for classes that keep track of current downloads.
 * 
 * @param <StateT> The state type of the downloaders that are used by this 
 * download tracker.
 * @param <DownloaderT> The type of downloaders that are used by this 
 * download tracker. 
 */
public interface DownloadTracker<StateT extends DownloaderState,
    DownloaderT extends Downloader<StateT>>
    {
    /**
     * Tracks a downloader associated with a given download.
     * 
     * @param id The identifier of the download.
     * @param downloader The downloader.
     * @param visitableDownloader The base visitable downloader. 
     */
    void trackDownloader(URI id, DownloaderT downloader, 
        VisitableDownloader<MsDState> visitableDownloader);
    

    /**
     * Tracks a downloader associated with a given download.
     * 
     * @param id The identifier of the download.
     * @param downloader The downloader.
     * @param visitableDownloader The base visitable downloader. 
     */
    void trackDownloader(String id, DownloaderT downloader, 
        VisitableDownloader<MsDState> visitableDownloader);
    
    /**
     * Returns whether a downloader exists for a given download.
     * 
     * @param id The identifier of the download.
     * @return True if a downloader exists, false otherwise.
     */
    boolean hasActiveDownloader(URI id);
    
    /**
     * Returns whether a downloader exists for a given download.
     * 
     * @param id The identifier of the download.
     * @return True if a downloader exists, false otherwise.
     */
    boolean hasActiveOrSucceededDownloader(URI id);
    
    
    /**
     * Returns whether a downloader exists for a given download.
     * 
     * @param id The identifier of the download.
     * @return True if a downloader exists, false otherwise.
     */
    boolean hasActiveOrSucceededDownloader(String id);
    
    /**
     * Returns whether a downloader has recently completed for a given file,
     * whether it succeeded or failed.
     * 
     * @param id The identifier of the download.
     * @return True if a downloader exists, false otherwise.
     */
    boolean hasCompletedDownloader(URI id);
    
    /**
     * Returns a downloader, if it exists, for a given download.
     * 
     * @param id The identifier of the download.
     * @return A downloader, if it exists, <code>None</code> otherwise.
     */
    DownloaderT getActiveDownloader (URI id);
    
    /**
     * Returns a downloader, if it exists, for a given download.
     * 
     * @param id The identifier of the download.
     * @return A downloader, if it exists, <code>None</code> otherwise.
     */
    DownloaderT getActiveOrSucceededDownloader (URI id);
    
    /**
     * Returns a downloader, if it exists, for a given download.
     * 
     * @param uri The identifier of the download.
     * @return A downloader, if it exists, <code>None</code> otherwise.
     */
    DownloaderT getActiveOrSucceededDownloader(String uri);
    
    /**
     * Returns a completed downloader, if it exists, for a given download.
     * 
     * @param id The identifier of the download.
     * @return A downloader, if it exists, <code>None</code> otherwise.
     */
    DownloaderT getCompletedDownloader(URI id);

    /**
     * Removes the specified downloader.
     * 
     * @param id The ID of the downloader.
     */
    void deleteDownloader(URI id);
    
    /**
     * Accesses all of the downloads.
     * 
     * @return All the downloads.
     */
    Collection<Entry<URI,Pair<DownloaderT, VisitableDownloader<MsDState>>>> getAll();

    /**
     * Accessor for all active downloads.
     * 
     * @return All active downloads.
     */
    Collection<Entry<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>>> getActive();
    
    /**
     * Accesses a single download.
     * 
     * @param uri The URI for the download.
     * @return The single download data.
     */
    Entry<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>> getSingle(URI uri);
    
    /**
     * Checks if there's an existing downloader that's ultimately going to
     * save it's downloaded file to the specified location.
     * 
     * @param file The file path in question.
     * @return <code>true</code> if there's an existing downloader planning 
     * on saving it's file to the specified location, otherwise false.
     */
    boolean saveLocationTaken(File file);

    /**
     * Clears inactive downloads.
     */
    void clearInactive();

    /**
     * Clears failed and canceled downloads, but not successfully completed.
     */
    void clearFailedAndCanceled();

    /**
     * Deletes all downloaders that are not active or succeeded.
     * 
     * @param uri The {@link URI} of the downloader.
     */
    void deleteFailedDownloader(URI uri);

    /**
     * Deletes all downloaders that are not active or succeeded.
     * 
     * @param uri The URI of the downloader.
     */
    void deleteFailedDownloader(String uri);

}
