dojo.provide("littleshoot.DownloadsLoader");

dojo.declare("littleshoot.DownloadsLoader", null, 
    {
    
    /*
    downloadTestData : {
        downloadDir : "/Users/afisk/test",
        total : 10,
        downloads : [
                     {"downloadStatus" : "Complete", 
                     "downloadNumSources" : 10,
                     "downloadSpeed" : 5.08,
                     "downloadBytesRead" : "200"}
                     ]
    },
    */
    
    constructor : function()
        {
        this.pageNavTop = new PageNav(Constants.RESULTS_PER_PAGE, 
            dojo.byId("resultPageDivDownloadsTop"), this);
        this.pageNavBottom = new PageNav(Constants.RESULTS_PER_PAGE, 
            dojo.byId("resultPageDivDownloadsBottom"), this);
        
        this.downloadBuilder = new littleshoot.DownloadBuilder();
        },
        
    processDownloads : function (jsonData)
        {
        console.info("Received "+jsonData.downloads.length+" downloads...");
        
        //updateTotalResults(jsonData, this.pageIndex, Constants.RESULTS_PER_PAGE, true);
        
        if (!jsonData.downloads)
            {
            console.warn("Unexpected JSON: '"+dojo.toJson(jsonData)+"'");
            }
        //console.info("Got JSON: ", dojo.fromJson(jsonData));
        this.lastJsonData = jsonData;
        var column1 = dojo.byId("downloadsColumn1");
        var column2 = dojo.byId("downloadsColumn2");
        var resultsColumnDiv1 = document.createElement("div");
        var resultsColumnDiv2 = document.createElement("div");
        resultsColumnDiv1.id = column1.id;
        resultsColumnDiv2.id = column2.id;
        resultsColumnDiv1.className = column1.className;
        resultsColumnDiv2.className = column2.className;
        var half = Math.ceil(jsonData.downloads.length/2);
        for (var i = 0; i < half; i++)
            {
            //console.info("Appending result to 1 with i: "+i);
            this.appendResult(jsonData.downloads[i], resultsColumnDiv1);
            }
        for (var j = half; j < jsonData.downloads.length; j++)
            {
            //console.info("Appending result to 2 with i: "+j);
            this.appendResult(jsonData.downloads[j], resultsColumnDiv2);
            }
        var column1ContainerDiv = dojo.byId("contentColumnDiv1Downloads");
        var column2ContainerDiv = dojo.byId("contentColumnDiv2Downloads");
        //CommonUtils.clearChildren(column1);
        //CommonUtils.clearChildren(column2);
        
        column1ContainerDiv.replaceChild(resultsColumnDiv1, column1);
        column2ContainerDiv.replaceChild(resultsColumnDiv2, column2);        
        
        //console.info("Updating links...");
        this.pageNavTop.updatePageLinks(this.pageIndex, jsonData.total);
        this.pageNavBottom.updatePageLinks(this.pageIndex, jsonData.total);
        CommonUtils.hideSpinner();
        },    
    
    /**
     * This effectively implements the "PageSwitcher" interface.
     */
    switchResultsPage : function (pageIndex)
        {
        //console.info("Switching results page to: "+pageIndex);
        this.loadDownloadsPage(pageIndex);
        },

    loadDownloadsPage : function (pageIndex)
        {
        //console.info("Loading results page: "+pageIndex);
        if (!pageIndex && pageIndex !== 0)
            {
            throw new Error("No page index!!!");
            }
        this.pageIndex = pageIndex;
        
        this.processDownloads(downloadTestData);
        //this.secureLoadDownloads();
        },
            
    appendResult : function (result, resultsDiv)
        {
        var resultDiv = this.downloadBuilder.createResource(result);
        resultsDiv.appendChild(resultDiv);
        },
    
    loadDownloadsLocal : function ()
        {
        if (!this.pageIndex && this.pageIndex !== 0)
            {
            console.dir(this);
            throw new Error("No page index!! Printed this...");
            }
        
        var params = {};
        params.pageIndex = this.pageIndex;

        var deferred = dojo.io.script.get(
            { 
            url: CLIENT_SECURE_URL + "downloads", 
            callbackParamName: "callback",
            load: function(response, ioArgs)
                {
                //console.info("Final listing got the response: " + dojo.toJson(response));
                return response;
                },
            error: function(response, ioArgs)
                {
                console.warn("Error response...");
                return response;
                },
            timeout: 20000,
            content: params,
            preventCache: true,
            noCache: (new Date()).getTime()
            });
        
        var success = dojo.hitch(this, function (response)
            {
            console.info("Got deferred response: ", response);
            this.processDownloads(response);
            return response;
            });
        
        var error = dojo.hitch(this, function (err)
            {
            console.warning("Could not load downloads data: ", err);
            return err;
            });
        deferred.addCallback(success);
        deferred.addErrback(error);
        
        return deferred;
        },
        
    secureLoadDownloads : function ()
        {
        var id = CommonUtils.keyId();
        var deferred = CommonUtils.requestKey(id);
        var success = dojo.hitch(this, function (data, ioArgs)
            {
            console.info("Got deferred response with data: "+data);
            //this.publishLocal(this.file, values, id);
            this.loadDownloadsLocal();
            });
        
        var error = dojo.hitch(this, function (data, ioArgs)
            {
            console.error("Got deferred response with data: "+data);
            });
        deferred.addCallback(success);
        deferred.addErrback(error);
        }
    });