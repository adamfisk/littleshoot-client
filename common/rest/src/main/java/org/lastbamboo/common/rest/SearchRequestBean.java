package org.lastbamboo.common.rest;


/**
 * General interface for search requests.
 */
public interface SearchRequestBean
    {

    boolean isSafeSearch();
    
    void setSafeSearch(boolean safeSearch);
    
    String getKeywords();

    void setKeywords(final String keyword);

    boolean isApplications();

    void setApplications(boolean applications);

    boolean isAudio();

    void setAudio(boolean audios);

    boolean isDocuments();

    void setDocuments(boolean documents);

    boolean isImages();

    void setImages(boolean images);

    boolean isVideo();

    void setVideo(boolean videos);
    
    
    /**
     * Accessor for the search terms for this search.
     * 
     * @return The terms the user entered for the search.
     */
    String getSearchString();
    
    /**
     * Sets the search terms entered by the user.
     * @param terms The search terms entered by the user.
     */
    void setSearchString(final String terms);

    /**
     * Sets the ID of the user performing the search.
     * 
     * @param userId The ID of the user performing the search.
     */
    void setUserId(long userId);
    
    /**
     * Gets the ID of the user performing the search.
     * 
     * @return The ID of the user performing the search.
     */
    long getUserId();
    
    void setGroupName(String groupName);

    String getGroupName();

    void setInstanceId(long instanceId);
    
    long getInstanceId();
    
    void setLittleShoot(boolean littleShoot);
    
    boolean isLittleShoot();

    void setYahoo(boolean yahoo);
    
    boolean isYahoo();
    
    void setYouTube(boolean youTube);
    
    boolean isYouTube();
    
    void setFlickr(boolean flickr);
    
    boolean isFlickr();

    boolean isLimeWire();
    
    void setLimeWire(boolean limeWire);
    
    void setIsoHunt(boolean isoHunt);

    boolean isIsoHunt();

    boolean isAllTypes();

    boolean isAnySingleType();

    void setIMeem(boolean imeem);
    
    boolean isIMeem();    
    }
