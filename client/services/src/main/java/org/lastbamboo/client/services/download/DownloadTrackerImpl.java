package org.lastbamboo.client.services.download;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.DownloaderListener;
import org.lastbamboo.common.download.DownloaderState;
import org.lastbamboo.common.download.DownloaderStateType;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.VisitableDownloader;
import org.littleshoot.util.Pair;
import org.littleshoot.util.PairImpl;
import org.littleshoot.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for tracking downloads.
 * 
 * @param <StateT> The state type of the downloaders that are used by this 
 * download tracker.
 * @param <DownloaderT> The type of downloaders that are used by this download 
 * tracker. 
 */
public class DownloadTrackerImpl<StateT extends DownloaderState,
    DownloaderT extends Downloader<StateT>>
    implements DownloadTracker<StateT,DownloaderT> {
   
    /**
     * The logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger (getClass());
    
    /**
     * The mapping of download identifiers to the downloaders that handle them.
     */
    private final Map<URI,Pair<DownloaderT, VisitableDownloader<MsDState>>> m_downloaders = 
        Collections.synchronizedMap(new LinkedHashMap<URI,Pair<DownloaderT, VisitableDownloader<MsDState>>>() {
            private static final long serialVersionUID = -1967838169453546383L;

            @Override
            protected boolean removeEldestEntry(
                final Map.Entry<URI,Pair<DownloaderT, VisitableDownloader<MsDState>>> eldest) {
                // This makes the map automatically lose the least used
                // entry.
                return size() > 200;
            }
        });

    /**
     * The mapping of download identifiers to the downloaders that handle them 
     * for downloads that have recently reached some sort of complete state,
     * whether they failed or succeeded.
     */
    private final Map<URI,Pair<DownloaderT, VisitableDownloader<MsDState>>> m_completedDownloaders = 
        Collections.synchronizedMap(new LinkedHashMap<URI,Pair<DownloaderT, VisitableDownloader<MsDState>>>() {
            private static final long serialVersionUID = -3377717374587429179L;

            @Override
            protected boolean removeEldestEntry(
                final Map.Entry<URI,Pair<DownloaderT, VisitableDownloader<MsDState>>> eldest) {
                // This makes the map automatically lose the least used
                // entry.
                return size() > 200;
            }
        });

    public DownloaderT getActiveOrSucceededDownloader(final String uriString) {
        final URI uri = normalizeUri(uriString);
        if (uri == null) {
            return null;
        }
        return getActiveOrSucceededDownloader(uri);
    }
    
    public DownloaderT getActiveDownloader(final URI id) {
        return optional(m_downloaders, id);
    }
    
    public DownloaderT getActiveOrSucceededDownloader(final URI id) {
        synchronized (m_downloaders) {
            if (hasActiveDownloader(id)) {
                return getActiveDownloader(id);
            }
        }

        return getSucceededDownloader(id);
    }

    public DownloaderT getCompletedDownloader(final URI id) {
        return optional(this.m_completedDownloaders, id);
    }
    
    private DownloaderT optional(
        final Map<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>> downloaders,
        final URI id) {
        synchronized (downloaders) {
            final Pair<DownloaderT, VisitableDownloader<MsDState>> pair = 
                downloaders.get(id);
            if (pair != null) {
                return pair.getFirst();
            }
        }
        return null;
    }

    public boolean hasActiveDownloader(final URI id) {
        synchronized (m_downloaders) {
            return m_downloaders.containsKey(id);
        }
    }
    
    public boolean hasActiveOrSucceededDownloader(final URI id) {
        return hasActiveDownloader(id) || hasSucceededDownloader(id);
    }

    private boolean hasSucceededDownloader(final URI id) {
        synchronized (this.m_completedDownloaders) {
            if (this.m_completedDownloaders.containsKey(id)) {
                final Pair<DownloaderT, VisitableDownloader<MsDState>> pair = 
                    this.m_completedDownloaders.get(id);
                final DownloaderT dlType = pair.getFirst();
                final DownloaderState state = dlType.getState();
                final DownloaderStateType type = state.getType();
                if (type == DownloaderStateType.SUCCEEDED) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private DownloaderT getSucceededDownloader(final URI id) {
        synchronized (this.m_completedDownloaders) {
            if (this.m_completedDownloaders.containsKey(id)) {
                final Pair<DownloaderT, VisitableDownloader<MsDState>> pair = 
                    this.m_completedDownloaders.get(id);
                final DownloaderT dlType = pair.getFirst();
                final DownloaderState state = dlType.getState();
                final DownloaderStateType type = state.getType();
                if (type == DownloaderStateType.SUCCEEDED) {
                    return dlType;
                }
            }
        }
        m_log.warn("Could not find succeeded downloader in: "
                + this.m_completedDownloaders + " with all downloaders as: "
                + getAll());
        return null;
    }

    public boolean hasCompletedDownloader(final URI id) {
        synchronized (m_completedDownloaders) {
            return m_completedDownloaders.containsKey(id);
        }
    }

    public void deleteDownloader(final URI id) {
        synchronized (this.m_downloaders) {
            this.m_downloaders.remove(id);
        }

        synchronized (this.m_completedDownloaders) {
            this.m_completedDownloaders.remove(id);
        }
    }

    public void trackDownloader(final URI id, final DownloaderT downloader,
            final VisitableDownloader<MsDState> visitableDownloader) {
        // We synchronize here to prevent multiple threads from tracking the
        // download. The test to see whether or not we are already tracking the
        // download must be atomic with the insertion of the downloader into the
        // map.
        synchronized (m_downloaders) {
            if (m_downloaders.containsKey(id)) {
                // We are already tracking the download.
            } else {

                final Pair<DownloaderT, VisitableDownloader<MsDState>> pair = 
                    new PairImpl<DownloaderT, VisitableDownloader<MsDState>>(
                        downloader, visitableDownloader);
                visitableDownloader.addListener(new TrackerDownloaderListener(
                        id, pair));
                m_downloaders.put(id, pair);
            }
        }
    }

    public Collection<Entry<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>>> getAll() {
        final Collection<Entry<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>>> copy = 
            getActive();

        synchronized (this.m_completedDownloaders) {
            final Collection<Entry<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>>> original = 
                this.m_completedDownloaders.entrySet();
            copy.addAll(original);
        }
        return copy;
    }
    
    public Entry<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>> getSingle(
        final URI uri) {
        final Pair<DownloaderT, VisitableDownloader<MsDState>> active = 
            this.m_downloaders.get(uri);
        if (active != null) {
            // A bit out of the ordinary, but should be fine.
            return new AbstractMap.SimpleImmutableEntry(uri, active);
        }
        final Pair<DownloaderT, VisitableDownloader<MsDState>> completed = 
            this.m_completedDownloaders.get(uri);
        
        if (completed == null) {
            m_log.warn("Could not find download for URI: {}", uri);
            return null;
        }
        // A bit out of the ordinary, but should be fine.
        return new AbstractMap.SimpleImmutableEntry(uri, completed);
    }

    public Collection<Entry<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>>> getActive() {
        final Collection<Entry<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>>> copy = 
            new ArrayList<Entry<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>>>();
        synchronized (this.m_downloaders) {
            final Collection<Entry<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>>> original = 
                this.m_downloaders.entrySet();
            copy.addAll(original);
        }

        return copy;
    }

    private class TrackerDownloaderListener 
        implements DownloaderListener<MsDState> {

        private final URI m_id;
        private final Pair<DownloaderT, VisitableDownloader<MsDState>> m_downloader;

        private TrackerDownloaderListener(
                final URI id,
                final Pair<DownloaderT, VisitableDownloader<MsDState>> downloader) {
            this.m_id = id;
            this.m_downloader = downloader;
        }

        public void stateChanged(final MsDState state) {
            // m_log.debug("Got state changed!!");
            final DownloaderStateType type = state.getType();
            switch (type) {
            case RUNNING:
                // m_log.debug("Got downloader running...");
                break;
            case SUCCEEDED:
                m_log.debug("Got download succeeded...moving to complete");
                moveToComplete();
                break;
            case FAILED:
                m_log.debug("Got download failed...moving to complete");
                moveToComplete();
                break;
            }
        }

        private void moveToComplete() {
            m_completedDownloaders.put(this.m_id, this.m_downloader);
            synchronized (m_downloaders) {
                m_downloaders.remove(this.m_id);
            }
        }
    }

    public boolean saveLocationTaken(final File file) {
        if (saveLocationTaken(file, m_downloaders)) {
            return false;
        }
        return saveLocationTaken(file, m_completedDownloaders);
    }

    private boolean saveLocationTaken(
            final File file,
            final Map<URI, Pair<DownloaderT, VisitableDownloader<MsDState>>> downloaders) {
        synchronized (downloaders) {
            for (final Pair<DownloaderT, VisitableDownloader<MsDState>> pair : downloaders.values()) {
                final DownloaderT downloader = pair.getFirst();
                final File current = downloader.getCompleteFile();
                if (current.equals(file)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void clearInactive() {
        synchronized (this.m_completedDownloaders) {
            this.m_completedDownloaders.clear();
        }
    }

    public void deleteFailedDownloader(final URI uri) {
        final DownloaderT dlType = getCompletedDownloader(uri);
        if (dlType == null)
            return;

        final DownloaderState state = dlType.getState();
        final DownloaderStateType type = state.getType();
        if (type == DownloaderStateType.FAILED) {
            this.m_completedDownloaders.remove(uri);
        }
    }

    public void deleteFailedDownloader(final String uriString) {
        final URI uri = normalizeUri(uriString);
        if (uri == null) {
            return;
        }
        deleteFailedDownloader(uri);
    }

    public void clearFailedAndCanceled() {
        synchronized (this.m_completedDownloaders) {
            final Iterator<Pair<DownloaderT, VisitableDownloader<MsDState>>> iter = 
                this.m_completedDownloaders.values().iterator();
            while (iter.hasNext()) {
                final Pair<DownloaderT, VisitableDownloader<MsDState>> pair = 
                    iter.next();
                final DownloaderT dlType = pair.getFirst();

                final DownloaderState state = dlType.getState();
                final DownloaderStateType type = state.getType();
                if (type == DownloaderStateType.FAILED) {
                    iter.remove();
                }
            }
        }
    }

    public boolean hasActiveOrSucceededDownloader(final String uriString) {
        final URI uri = normalizeUri(uriString);
        if (uri == null) {
            return false;
        }
        return hasActiveOrSucceededDownloader(uri);
    }

    public void trackDownloader(final String uriString,
            final DownloaderT downloader,
            final VisitableDownloader<MsDState> torrentDownloader) {
        final URI uri = normalizeUri(uriString);
        if (uri == null) {
            return;
        }
        trackDownloader(uri, downloader, torrentDownloader);
    }

    private URI normalizeUri(final String uriString) {
        try {
            return new URI(uriString);
        } catch (final URISyntaxException e) {
            final String beginning = StringUtils.substringBeforeLast(uriString,
                    "/");
            final String end = StringUtils.substringAfterLast(uriString, "/");
            final String encoded = UriUtils.urlNonFormEncode(end);

            final String normal = beginning + "/" + encoded;
            try {
                return new URI(normal);
            } catch (final URISyntaxException e1) {
                // OK, we've done our best!
                m_log.error("Still could not normalize: " + uriString, e1);
                return null;
            }
        }
    }
}