dojo.provide("littleshoot.Constants");
dojo.provide("littleshoot.littleshootlib");

Constants =
    {
    
    CLIENT_URL : "http://p2p2o.littleshoot.org:8107/api/client/",
    
    CLIENT_SECURE_URL : "http://p2p2o.littleshoot.org:8107/api/client/secure/",
    
    DOWNLOAD_URL : "http://p2p2o.littleshoot.org:8107/api/client/download/",
    
    DOWNLOADS_URL : "http://p2p2o.littleshoot.org:8107/api/client/downloads",
    
    DOWNLOAD_TORRENT_URL : "http://p2p2o.littleshoot.org:8107/api/client/downloadTorrent/",
    
    START_TORRENT_DOWNLOAD_URL : "http://p2p2o.littleshoot.org:8107/api/client/startTorrentDownload/",
    
    STREAM_DOWNLOAD_URL : "http://p2p2o.littleshoot.org:8107/api/client/streamDownload/",
    
    IMAGES : "http://littleshootimages.appspot.com/images/",
    
    AUDIO_IMAGE: "http://littleshootimages.appspot.com/images/icons/audio_75x75.gif",
    
    VIDEOS_IMAGE: "http://littleshootimages.appspot.com/images/icons/video_75x75.gif",
    
    IMAGES_IMAGE: "http://littleshootimages.appspot.com/images/icons/image_75x75.gif",
    
    DOCS_IMAGE: "http://littleshootimages.appspot.com/images/icons/document_75x75.gif",
    
    APPS_IMAGE: "http://littleshootimages.appspot.com/images/icons/application_75x75.gif",
    
    //SERVER_URL : "lastbamboo-server-site/api/",
    SERVER_URL : "api/",
    
    API_URL : "api/",
    
    // This is important because the platform code needs to call it from other sites!!
    FULL_API_URL : "http://www.littleshoot.org/api/",
    
    // This one's for the cookie set from JavaScript
    CLIENT_COOKIE_KEY : "littleShootData",
    
    // This one's for the cookie the local LittleShoot server sets -- this one
    // has issues on IE and is not always reliable.  
    // See AppCheckController.java.
    SET_FROM_CLIENT_COOKIE_KEY : "littleShootClientCookie",
    
    VERSION_MESSAGE : "LittleShoot 0.61 is here! This version improves downloading and searching "+
        " and includes over 40 bug fixes. Would you like to download the new version now?",
    
    /**
     * The most recent version of the app.
     */
    CURRENT_VERSION : 0.61,
    
    MAIN_LINE_VERSION: 0.00,
    
    PUBLIC_PERMISSION : 0,
    GROUP_PERMISSION : 1,
    PRIVATE_PERMISSION : 2,
    
    API_VERSION : 1,
    
    DOWNLOADED_COOKIE_KEY: "downloadedCookie",
    
    RESULTS_PER_PAGE : 20
    };
