dojo.provide("littleshoot.Downloader");

dojo.declare("littleshoot.Downloader", null, 
    {
    constructor : function(uri, fileName, size, urn, mimeType, source)
        {
        this.uri = uri;    
        this.fileName = fileName;
        this.size = size;
        this.mimeType = mimeType;
        this.urn = urn;
        this.source = source;
        console.log("Downloading URL: "+this.url);
        },
    
    download : function ()
        {
        var loadHandler = function(data, ioArgs)
            {
            onDownloadRequested(data);
            };
            
        var errorHandler = function(data, ioArgs)
            {
            CommonUtils.showError(data);
            };
            
        var params = 
            {
            uri: this.uri, 
            name: this.fileName,
            urn: this.urn,
            size: this.size,
            mimeType: this.mimeType,
            noCache: (new Date()).getTime()
            };
        
        if (CommonUtils.inGroup())
            {
            params.groupName = CommonUtils.getGroupName();
            }
            
        console.log("Sending request for file: "+params.name);
            
        var getParams = 
            { 
            url: Constants.CLIENT_URL + "download", 
            callbackParamName: "callback",
            load: loadHandler,
            error: errorHandler,
            content: params,
            timeout: 10000
            };
        
        var deferred = CommonUtils.get(getParams);
        },
        
    setFrameSrc : function (name, uri)
        {
        console.info("Setting frame source...");
        updateDownloadStatus(uri);
        var viewerPage;
        var mt = this.mimeType;
        
        var isSafari = /Safari/.test(navigator.userAgent);
        var isLinux = /Linux/.test(navigator.userAgent);
        
        console.log("Is Safari? "+isSafari);
        
        var params = 
            {
            uri: this.uri, 
            name: this.fileName,
            urn: this.urn,
            size: this.size,
            mimeType: this.mimeType,
            source: this.source,
            noCache: (new Date()).getTime()
            };
        
        if (CommonUtils.inGroup())
            {
            params.groupName = CommonUtils.getGroupName();
            }
        
        var fileUrl = Constants.CLIENT_URL +"download?"+
            dojo.objectToQuery(params);
        
        console.info("Downloading file: "+fileUrl);
        //window.location.href = fileUrl;
        
        // Use the QuickTime plugin if it's supported.  Note that it doesn't
        // load correctly on at least some Safari versions.
        if ((mt == "video/mpeg" || mt == "video/quicktime") &&
            !isLinux)
            {
            console.log("Setting cookie URL to: "+fileUrl);
            viewerPage = "quickTimeViewer.html?url=" + 
                encodeURIComponent(fileUrl); 
            }
        else
            {
            console.log("Did not understand file type or browser doesn't " + 
                "support plugin.");
            console.log("Setting the frame to use the file directly.");
            viewerPage = fileUrl;
            }
        
        if (isSafari)
            {
            dojo.byId("downloadFrame").height = 500;
            }
            
        console.log("Setting src to: "+viewerPage);
        dojo.byId("downloadFrame").src = viewerPage;
        }
    });
