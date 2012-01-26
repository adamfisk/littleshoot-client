dojo.provide("littleshoot.DownloadStatus");


function refreshStatus()
    {
    console.log("Refreshing status...");
    this.downloadStatus.updateStatus();
    }

/**
 * Called from the server when it has obtained the download status.
 */
function onDownloadStatus(data)
    {
    var status = data.status;
    console.log("Received download status: "+status);
    var statusSpan = dojo.byId("downloadStatusSpan");
    var statusText = document.createTextNode(status);
    statusSpan.replaceChild(statusText, statusSpan.firstChild);
    this.timeoutId = setTimeout(refreshStatus, 2000);
    }

dojo.declare("littleshoot.DownloadStatus", null, 
    {
    constructor : function(uri)
        {
        this.uri = uri;    
        this.url = Constants.CLIENT_URL + "downloadStatus";
        console.log("Using download status URL: " + this.url);
        },
    
    updateStatus : function ()
        {
        var loadHandler = function(data, ioArgs)
            {
            onDownloadStatus(data);
            };
            
        var errorHandler = function(data, ioArgs)
            {
            CommonUtils.showError(dojo.toJson(data));
            };
            
        var params = 
            {
            uri: this.uri 
            };
    
        var deferred = dojo.io.script.get(
            { 
            url: this.url, 
            callbackParamName: "callback",
            load: loadHandler,
            error: errorHandler,
            content: params,
            timeout: 10000
            });
        }
    });
    
function updateDownloadStatus(uri)
    {
    console.log("Updating status...");
    this.downloadStatus = new littleshoot.DownloadStatus(uri);
    this.downloadStatus.updateStatus();
    } 
    
