dojo.provide("littleshoot.ResourceLoader");

dojo.declare("littleshoot.ResourceLoader", null, 
    {
    
    constructor : function(columnDivId, resourceCreator, loaderForFiles)
        {
        this.columnDivId = columnDivId;
        
        this.pageNavTop = new PageNav(Constants.RESULTS_PER_PAGE, 
            dojo.byId("resultPageDivPublishTop"), this);
        this.pageNavBottom = new PageNav(Constants.RESULTS_PER_PAGE, 
            dojo.byId("resultPageDivPublishBottom"), this);
        
        this.resourceCreator = resourceCreator;
        this.loaderForFiles = loaderForFiles;
        },

    isFiles : function ()
        {
        return this.loaderForFiles;
        },
        
    processResources : function (jsonData)
        {
        //console.info("Received "+jsonData.results.length+" results...");
        
        //updateTotalResults(jsonData, this.pageIndex, Constants.RESULTS_PER_PAGE, true);
        
        if (!jsonData.results)
            {
            console.warn("Unexpected JSON: '"+jsonData+"'");
            }
        //console.info("Got JSON: ", dojo.fromJson(jsonData));
        this.lastJsonData = jsonData;
        var column1 = dojo.byId(this.columnDivId+"Column1");
        var column2 = dojo.byId(this.columnDivId+"Column2");
        var resultsColumnDiv1 = document.createElement("div");
        var resultsColumnDiv2 = document.createElement("div");
        resultsColumnDiv1.id = column1.id;
        resultsColumnDiv2.id = column2.id;
        resultsColumnDiv1.className = column1.className;
        resultsColumnDiv2.className = column2.className;
        //var style1 = dojo.style(column1, "display");
        //var style2 = dojo.style(column2, "display");
        //dojo.style(resultsColumnDiv1, "display", style1);
        //dojo.style(resultsColumnDiv2, "display", style2);
        var half = Math.ceil(jsonData.results.length/2);
        for (var i = 0; i < half; i++)
            {
            //console.info("Appending result to 1 with i: "+i);
            this.appendResult(jsonData.results[i], resultsColumnDiv1);
            }
        for (var j = half; j < jsonData.results.length; j++)
            {
            //console.info("Appending result to 2 with i: "+j);
            this.appendResult(jsonData.results[j], resultsColumnDiv2);
            }
        var column1ContainerDiv = dojo.byId("contentColumnDiv1Publish");
        var column2ContainerDiv = dojo.byId("contentColumnDiv2Publish");
        //CommonUtils.clearChildren(column1);
        //CommonUtils.clearChildren(column2);
        
        column1ContainerDiv.replaceChild(resultsColumnDiv1, column1);
        column2ContainerDiv.replaceChild(resultsColumnDiv2, column2);        
        
        //console.info("Updating links...");
        this.pageNavTop.updatePageLinks(this.pageIndex, jsonData.totalResults);
        this.pageNavBottom.updatePageLinks(this.pageIndex, jsonData.totalResults);
        CommonUtils.hideSpinner();
        },    
    
    switchResultsPage : function (pageIndex)
        {
        //console.info("Switching results page to: "+pageIndex);
        this.loadResultsPage(pageIndex);
        },

    loadResultsPage : function (pageIndex)
        {
        //console.info("Loading results page: "+pageIndex);
        if (!pageIndex && pageIndex !== 0)
            {
            throw new Error("No page index!!!");
            }
        this.pageIndex = pageIndex;
        
        if (this.pending)
            {
            this.pending.cancel();
            this.pending = null;
            }
        //if (CommonUtils.hasLittleShoot())
          //  {
            this.pending = new littleshoot.PendingFiles(false, false);
            this.pending.loadLocalPendingResources("pendingFilesContainerDiv", this);
            //}
        this.listResources();
        },
            
    appendResult : function (result, resultsDiv)
        {
        var resultDiv = this.resourceCreator.createResource(result);
        resultsDiv.appendChild(resultDiv);
        },
    
    listResources : function ()
        {
        // This can cause odd issues when the calling context creates a 
        // different "this"
        var pageIndex = this.pageIndex;
        
        if (!pageIndex && pageIndex !== 0)
            {
            console.dir(this);
            throw new Error("No page index!! Printed this...");
            }
        // Then we load published data on the central server.
        //console.info("Loading resources...");
        var cookieData = dojo.cookie(Constants.CLIENT_COOKIE_KEY);
        if (!cookieData)
            {
            console.warn("No instance data. We need an instance ID to list " +
                "resources.");
            //throw new Error("No instance data");
            return;
            }
        
        var id = dojo.fromJson(cookieData).instanceId;
        //console.info("Instance ID is: "+id);
        var listingData = 
            {
            instanceId: id,
            pageIndex: pageIndex,
            resultsPerPage: Constants.RESULTS_PER_PAGE
            };
            
        if (CommonUtils.inGroup())
            {
            listingData.groupName = CommonUtils.getGroupName();
            }
        
        //console.info("About to load published files...");
        var deferred = dojo.xhrPost(
            {
            url: "/api/fileListing",
            timeout: 20000,
            content: listingData,
            handleAs: "json",
            load: function(response, ioArgs)
                {
                //console.info("Final listing got the response: " + dojo.toJson(response));
                return response;
                },
            error: function(response, ioArgs)
                {
                console.warn("Error response...");
                return response;
                }
            });
        
        var success = dojo.hitch(this, function (response)
            {
            console.info("Got deferred response: ", response);
            this.processResources(response);
            return response;
            });
        
        var error = dojo.hitch(this, function (err)
            {
            console.error("Got response...");
            console.error("Got error response: ", err);
            CommonUtils.showMessage("File Access Error", 
                "LittleShoot's having trouble contacting it's servers.  "+
                "Please try again after giving the little guy a brief rest!");
            return err;
            });
        deferred.addCallback(success);
        deferred.addErrback(error);
        
        return deferred;
        }
    });