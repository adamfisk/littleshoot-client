package org.lastbamboo.common.searchers.youtube;

import java.net.URI;

/**
 * Interface for data about individual YouTube videos.
 */
public interface YouTubeVideo
    {

    /**
     * Accessor for the title of the video.
     * 
     * @return The title of the video.
     */
    String getTitle();

    /**
     * Accessor for the URL for the video.
     * 
     * @return The URL for the video.
     */
    URI getUrl();

    /**
     * Accessor for the description of the video.
     * 
     * @return The description of the video.
     */
    String getDescription();

    /**
     * Accessor for the author of the video.
     * 
     * @return The author of the video.
     */
    String getAuthor();
    
    /**
     * Accessor for the length of the video.
     * 
     * @return The length of the video.
     */
    int getLengthSeconds();
    }
