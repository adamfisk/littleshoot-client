package org.lastbamboo.client.search;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.rest.RestSearcher;
import org.lastbamboo.common.rest.SearchRequestBean;
import org.lastbamboo.common.searchers.flickr.FlickrSearcher;
import org.lastbamboo.common.searchers.isohunt.IsoHuntSearcher;
import org.lastbamboo.common.searchers.youtube.YouTubeGDataSearcher;
import org.littleshoot.util.DaemonThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.lastbamboo.common.searchers.yahoo.YahooImageSearcher;

/**
 * Class that aggregates searchers according to type.  All searchers search 
 * for generalized keyword searches, whereas image searchers also search for
 * images, video searchers also search for videos, etc.
 */
public class MetaSearcher implements Searcher {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MetaSearcher.class);

    private final SearchResultProcessor m_searchResultProcessor;

    private final UUID m_uuid;

    /*
    private final RestSearcher<JsonLittleShootResult> m_littleShootSearcher;

    private final RestSearcher<LimeWireJsonResult> m_limeWireSearcher;
    */

    private final Map<String, String> m_paramMap;
    
    /**
     * Creates a new visitor that aggregates multiple searchers for different
     * searcher types.
     * 
     * @param processor The class that processes search results.
     * @param uuid The ID for this search.
     * @param littleShootSearcher Searcher for searching LittleShoot.
     * @param limeWireSearcher Searcher for searching LimeWire.
     * @param paramMap Map of original request params. 
     */
    public MetaSearcher(final SearchResultProcessor processor, final UUID uuid,
            //final RestSearcher<JsonLittleShootResult> littleShootSearcher,
            //final RestSearcher<LimeWireJsonResult> limeWireSearcher,
            final Map<String, String> paramMap) {
        this.m_searchResultProcessor = processor;
        this.m_uuid = uuid;
        //this.m_littleShootSearcher = littleShootSearcher;
        //this.m_limeWireSearcher = limeWireSearcher;
        this.m_paramMap = paramMap;
    }

    private void search(final RestSearcher searcher) {
        LOG.debug("Sending search to " + searcher);
        final Runnable searchRunner = new Runnable() {
            public void run() {
                try {
                    sendSearch(searcher);
                } catch (final Throwable t) {
                    LOG.error("Throwable while searching with: " + searcher, t);
                }
            }
        };

        final Thread searchThread = new DaemonThread(searchRunner, searcher
                .getClass().getSimpleName() + "-thread-" + hashCode());
        searchThread.start();
    }

    private void sendSearch(final RestSearcher searcher) {
        try {
            LOG.debug("Sending search to: " + searcher);
            searcher.search();
        } catch (final IOException e) {
            // The user could have lost her Internet connection, for example.
            LOG.debug("Could not access search service", e);
            return;
        }
    }

    public UUID search(final SearchRequestBean request) {
        /*
        if (request.isLittleShoot()) {
            search(this.m_littleShootSearcher);
        } else {
            LOG.debug("Not searching LittleShoot");
        }

        if (request.isLimeWire()) {
            search(this.m_limeWireSearcher);
        }
        */
        if (request.isIsoHunt()) {
            search(new IsoHuntSearcher(this.m_searchResultProcessor,
                    this.m_uuid, request.getKeywords()));
        }
        if (request.isVideo()) {
            searchVideoSites(request, this.m_uuid);
        }
        if (request.isImages()) {
            searchImageSites(request, this.m_uuid);
        }
        return this.m_uuid;
    }

    private void searchImageSites(final SearchRequestBean request,
            final UUID uuid) {
        final String searchString = request.getKeywords();
        if (request.isFlickr()) {
            final RestSearcher flickr = new FlickrSearcher(
                    this.m_searchResultProcessor, uuid,
                    "d67bc572b8b129a7264d1780fd9ed084", searchString);
            search(flickr);
        }

        /*
        if (request.isYahoo()) {
            final RestSearcher yahoo = new YahooImageSearcher(
                    this.m_searchResultProcessor,
                    uuid,
                    "ajzgmvnV34GrMcdruY9h3vKb4GD5AZoqfiLXhWfazKWcIkuaWZYlyoCqxrEV",
                    searchString);
            search(yahoo);
        }
        */
    }

    private void searchVideoSites(final SearchRequestBean request,
            final UUID uuid) {
        final String searchString = request.getKeywords();
        if (request.isYouTube()) {
            final RestSearcher youTube = new YouTubeGDataSearcher(
                    this.m_searchResultProcessor, uuid, searchString);
            search(youTube);
        }

        // Yahoo disabled their video search API in March of 2009.
        /*
         * if (request.isYahoo()) { final RestSearcher yahoo = new
         * YahooVideoSearcher(this.m_searchResultProcessor, uuid, "littleshoot",
         * searchString); search(yahoo); }
         */
    }
}
