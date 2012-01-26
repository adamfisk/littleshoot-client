dojo.provide("littleshoot.SearchResults");

dojo.require("dojo.back");

/*jslint forin: true*/

var searchPageApplicationState = 0;

SearchPageApplicationState = function(resultsPage)
    {
    //console.info("Creating new search page history instance for: "+resultsPage);
    this.resultsPage = resultsPage;
    //this.changeUrl = 
      //  "searchPage"+resultsPage + searchPageApplicationState++;
    };
    
SearchHistory =
    {
    addHistory : function (pageIndex)
        {
        //console.info("Adding history");
        //var appState = new SearchPageApplicationState(pageIndex);
        
        var appState =
            {
            resultsPage : pageIndex,
            back: function(){ window.searchResults.switchResultsPageNoHistory(this.resultsPage); },
            forward: function(){ window.searchResults.switchResultsPageNoHistory(this.resultsPage); },
            changeUrl: true
            };
        //console.info("Adding to back..");
        
        dojo.back.addToHistory(appState);
        //console.info("Added to back...");
        }            
    };
    
dojo.declare("littleshoot.SearchResults", null, 
    {
    
    constructor : function(searchGuid, forceReload)
        {
        //CommonUtils.hideSpinner();
        this.wasComplete = false;
        
        // This is just useful for debug logs.
        this.resultSetIndex = 0;
        this.stopped = false;
        if (!searchGuid)
            {
            //console.error("No search guid!!");
            throw new Error("No search guid!!!!");
            }

        if (forceReload)
            {
            //console.info("Forcing reload!!!");
            this.forceReload = forceReload;
            }
        else
            {
            this.forceReload = false;
            }
        
        this.searchGuid = searchGuid;
        dojo.cookie("lastSearchGuid", searchGuid);
        
        var column1 = dojo.byId("contentColumnDiv1Search");
        var column2 = dojo.byId("contentColumnDiv2Search");
        
        CommonUtils.clearChildren(column1);
        CommonUtils.clearChildren(column2);
        //console.info("Created search results for guid: "+this.searchGuid);
        },

    loadResultsPage : function (pageIndex, useSpinner)
        {
        //console.info("Loading results page: "+pageIndex);
        if (this.stopped)
            {
            //console.info("Stopped...not loading results page for guid: "+this.searchGuid);
            return;
            }
        
        if (useSpinner && !this.wasComplete)
            {
            //console.info("Showing spinner");
            CommonUtils.showSpinner();
            }
        
        clearTimeout(this.timeoutId);
        this.pageIndex = pageIndex;

        var callingInstance = this;
        var getWhenLoaded = function() {
            var params = 
                {
                resultsPerPage: Constants.RESULTS_PER_PAGE,
                pageIndex: callingInstance.pageIndex,
                images: true,
                video: true,
                audio: true,
                documents: true,
                applications: true,
                guid: callingInstance.searchGuid,
                preventCache: true,
                noCache: (new Date()).getTime()
                };
                
            var deferred = dojo.io.script.get(
                { 
                url: Constants.CLIENT_URL + "searchResults",
                callbackParamName: "callback",
                load: function (response, args)
                    {
                    //console.info("Got search result data: "+dojo.toJson(response));
                    return response;
                    },
                
                error : function (response, args)
                    {
                    console.warn("Error accessing search results: "+response+args);
                    //console.dir(args);
                    //CommonUtils.showError("Error accessing search results: "+response+args);
                    //console.warn("One call did not complete...");
                    return response;
                    },
                content: params,
                timeout : 10000
                });
            
            var success = function (response) {
                //console.info("Got deferred response: "+response);
                callingInstance.processResultSet(response);
                return response;
            };
            
            var error = function (response) {
                //console.error("Got error response: "+response);
                CommonUtils.hideSpinner();
                return response;
            };
            
            deferred.addCallback(success);
            deferred.addErrback(error);
            
            if (useSpinner)
                {
                var hideSpinner = function()
                    {
                    //console.info("Hiding spinner from deferred...");
                    //CommonUtils.hideSpinner("searchStatusDiv");
                    //CommonUtils.hideSpinner();
                    };
                deferred.addBoth(hideSpinner);
                }
            //return deferred;
        
        };
        dojoLoader.scriptGet(getWhenLoaded);
    },
        
    processResultSet : function (jsonData)
        {
        //console.info("Got JSON search data: "+dojo.toJson(jsonData.searchData));
        if (!jsonData.searchData)
            {
            console.warn("No search data...returning");
            return;
            }
        //console.info("Received "+jsonData.results.length+" results...");
        if (this.stopped)
            {
            //console.info("Stopped...not processing result set...");
            return;
            }
        
        if (jsonData.searchData)
            {
            this.updateForwardBackButtons(jsonData);
            }
        
        //console.info("Updating total results...");
        this.updateTotalResults(jsonData);
        
        if (this.wasComplete && jsonData.complete)
            {
            //console.info("Data complete, ignoring...");
            CommonUtils.hideSpinner();
            this.updateDownloads(jsonData);
            jsonData = null;
            this.scheduleCall();
            return;
            }
        
        //CommonUtils.showSpinner("searchStatusDiv");
        //CommonUtils.showSpinner();
        
        this.wasComplete = jsonData.complete;
        //console.info("Complete: "+jsonData.complete);
        
        if ((this.forceReload === false) && this.sameResults(jsonData))
            {
            //console.info("No change in results, returning...");
            this.updateDownloads(jsonData);
            jsonData = null;
            this.scheduleCall();
            return;
            }
            
        window.lastJsonSearchData = jsonData;
        var column1 = dojo.byId("contentColumnDiv1Search");
        var column2 = dojo.byId("contentColumnDiv2Search");
        var resultsColumnDiv1 = document.createElement("div");
        var resultsColumnDiv2 = document.createElement("div");
        resultsColumnDiv1.id = column1.id;
        resultsColumnDiv2.id = column2.id;
        resultsColumnDiv1.className = column1.className;
        resultsColumnDiv2.className = column2.className;
        
        //console.info("Looping through results.  We've got: "+jsonData.results.length);
        var half = Math.ceil(jsonData.results.length/2);
        for (var i = 0; i < half; i++) {
            try {
                var c1Result = new littleshoot.SearchResult(jsonData.results[i], jsonData.downloadPath);
                var rd1 = c1Result.getDiv();
                if (rd1)
                    {
                    //console.info("Adding result");
                    resultsColumnDiv1.appendChild(rd1);
                    }
                else {
                    //console.info("No div");
                    }
            } catch (err) {
                console.error("Caught error: "+err);
            }
        }
        for (var j = half; j < jsonData.results.length; j++) {
            var c2Result = new littleshoot.SearchResult(jsonData.results[j], jsonData.downloadPath);
            var rd2 = c2Result.getDiv();
            if (rd2) {
                //console.info("Adding result 2");
                resultsColumnDiv2.appendChild(rd2);
            }
            else {
                //console.info("No div 2");
            }
        }
        
        column1.parentNode.replaceChild(resultsColumnDiv1, column1);
        column2.parentNode.replaceChild(resultsColumnDiv2, column2);      

        if (Search.isLighBoxEnabled())
            {
            //console.info("Setting light boxes...");
            $('#searchResultsDiv a[rel*=lightbox]').lightBox({fixedNavigation:true, 
                extraText : function (href) {
                    return "   <a href='"+href+"' target='blank'>Open in New Window</a>";
                }
            }); 
            }
        
        //console.info("Updating nav links.  Result set number: "+this.resultSetIndex);
        //console.info("Updating nav links using index: "+this.pageIndex);
        
        this.updateDownloads(jsonData);
        
        var topNav = dojo.byId("resultPageDivSearchTop");
        var bottomNav = dojo.byId("resultPageDivSearchBottom");
        var pageNavTop = new PageNav(Constants.RESULTS_PER_PAGE, topNav, this);
        var pageNavBottom = new PageNav(Constants.RESULTS_PER_PAGE, bottomNav, this);
        pageNavTop.updatePageLinks(this.pageIndex, jsonData.totalResults);
        pageNavBottom.updatePageLinks(this.pageIndex, jsonData.totalResults);
        
        // Memory leak paranoia.
        column1 = null;
        column2 = null;
        topNav = null;
        bottomNav = null;
        pageNavTop = null;
        pageNavBottom = null;
        resultsColumnDiv1 = null;
        resultsColumnDiv2 = null;
        jsonData = null;

        this.resultSetIndex++;
        
        //console.info("Scheduling next result call");
        this.scheduleCall();
        
        },    
    
    updateForwardBackButtons : function (jsonData)
        {
        var lastSearchIndex;
        var backIndex;
        var forwardIndex;
        var foundMatch = false;
        for (var i = 0; i < jsonData.searchData.length; i++)
            {
            var curGuid = jsonData.searchData[i].guid;
            
            //console.info("Comparing "+curGuid +" to "+ this.searchGuid);
            if (curGuid == this.searchGuid)
                {
                //console.info("Got match!!");
                lastSearchIndex = i;
                backIndex = i - 1;
                forwardIndex = i + 1;
                foundMatch = true;
                //dojo.cookie("lastSearchIndex", lastSearchIndex);
                break;
                }
            }
        //console.info("Last search index: "+lastSearchIndex);
        if (!foundMatch)
            {
            //console.error("No matching GUID!!!");
            throw new Error("No matching GUID!!");
            }
        
        
        if (jsonData.searchData.length === 1)
            {
            //console.info("Only one search!");
            CommonUtils.hideElement(dojo.byId("searchBackDiv"));
            CommonUtils.hideElement(dojo.byId("searchForwardDiv"));
            return;
            }
        
        CommonUtils.showElement(dojo.byId("searchBackDiv"));
        CommonUtils.showElement(dojo.byId("searchForwardDiv"));
        
        //console.info("last search index: "+lastSearchIndex);
        var backButton = dojo.byId("searchBackDiv");
        if (lastSearchIndex > 0)
            {
            //CommonUtils.showElement(backButton);
            dojo.removeClass(backButton, "backDisabled");
            dojo.addClass(backButton, "backEnabled");
            if (window.backButtonEventId)
                {
                dojo.disconnect(window.backButtonEventId);
                }
            var backGuid = jsonData.searchData[backIndex].guid;
            
            window.backButtonEventId = dojo.connect(backButton, "onclick", 
                function (evt) {
                    //console.info("Moving back to guid: "+backGuid);
                    SearchResultUtils.reload(backGuid);
                });
            }
        else
            {
            dojo.disconnect(window.backButtonEventId);
            dojo.removeClass(backButton, "backEnabled");
            dojo.addClass(backButton, "backDisabled");
            }
        
        var forwardButton = dojo.byId("searchForwardDiv");
        if (forwardIndex < 5 && forwardIndex < jsonData.searchData.length)
            {
            //CommonUtils.showElement(forwardButton);
            dojo.removeClass(forwardButton, "forwardDisabled");
            dojo.addClass(forwardButton, "forwardEnabled");
            if (window.forwardButtonEventId)
                {
                dojo.disconnect(window.forwardButtonEventId);
                }
            var forwardGuid = jsonData.searchData[forwardIndex].guid;
            
            window.forwardButtonEventId = dojo.connect(forwardButton, "onclick", 
                function (evt) {
                    //console.info("Moving forward to guid: "+forwardGuid);
                    SearchResultUtils.reload(forwardGuid);
                });
            }
        else
            {
            //CommonUtils.hideElement(forwardButton);
            dojo.disconnect(window.forwardButtonEventId);
            dojo.removeClass(forwardButton, "forwardEnabled");
            dojo.addClass(forwardButton, "forwardDisabled");
            }
        },
        
    updateDownloads : function (jsonData)
        {
        for (var i = 0; i < jsonData.results.length; i++)
            {
            //console.info("Appending result to 1 with i: "+i);
            var result = jsonData.results[i];
            if (result.downloadStatus)
                {
                SearchResultUtils.updateDownload(result, jsonData.downloadPath);
                }
            else
                {
                //console.info("Not updating result with no download status");
                }
            }
        },
            
    updateTotalResults : function(jsonData)
        {
        //console.info("Updating total...");
        var totalResultsText;
        if (jsonData.totalResults === 0 && !jsonData.complete)
            {
            totalResultsText = document.createTextNode("Searching...");
            }
        else if (jsonData.totalResults === 0 && jsonData.complete)
            {
            totalResultsText = document.createTextNode("No Results");
            }
        else
            {
            var firstResultNum = (this.pageIndex * Constants.RESULTS_PER_PAGE) + 1;
            var lastResultNum = firstResultNum + Constants.RESULTS_PER_PAGE - 1;
            if (jsonData.totalResults < Constants.RESULTS_PER_PAGE)
                {
                lastResultNum = jsonData.totalResults;
                }
            else if (lastResultNum > jsonData.totalResults)
                {
                lastResultNum = jsonData.totalResults;
                }
            var rawText = "Results "+firstResultNum+" - "+
                lastResultNum + " out of "+jsonData.totalResultsFormatted;
            
            //console.info("Raw text: "+rawText);
            totalResultsText = document.createTextNode(rawText);
            }
        var totalResults = dojo.byId("totalSearchResultsSpan");
        if (totalResults.firstChild)
            {
            totalResults.replaceChild(totalResultsText, totalResults.firstChild);
            }
        else
            {
            totalResults.appendChild(totalResultsText);
            }  
        totalResults = null;
        //console.info("Updated total results");
        },

    sameResults : function(jsonData)
        {
        //console.info("Processing results: "+dojo.toJson(jsonData))
        if (window.lastJsonSearchData && window.lastJsonSearchData.results[0])
            {
            if (window.lastJsonSearchData.pageIndex == jsonData.pageIndex)
                {
                var oldIndex = window.lastJsonSearchData.results.length - 1;
                var newIndex = jsonData.results.length - 1;
                //console.info("Same page index");
                if (oldIndex == newIndex)
                    {
                    //console.info("Same results length");
                    var oldUrl = window.lastJsonSearchData.results[oldIndex].uri;
                    if (jsonData.results[newIndex])
                        {
                        var newUrl = jsonData.results[newIndex].uri;
                        if (oldUrl == newUrl)
                            {
                            //console.info("Got same results");
                            return true;
                            }
                        }
                    else
                        {
                        //console.info("URL changed: "+oldUrl+" versus "+newUrl);
                        }
                    }
                else
                    {
                    //console.info("Length changed: "+oldIndex+" versus "+newIndex);
                    }
                }
            else
                {
                //console.info("Index changed: "+window.lastJsonSearchData.pageIndex+" versus " + jsonData.pageIndex);
                }
            //console.info("Results changed!!");
            }
        return false;
        },
    
    scheduleCall : function ()
        {
        if (this.stopped)
            {
            //console.info("Not scheduling call because we're stopped...");
            return;
            }
        var refreshTime = 1400;
        //var refreshTime = 16000;
        var reload = dojo.hitch(this, function () {
            try {
                this.loadResultsPage(this.pageIndex, true);
            } catch (err) {
                console.error("Caught error: "+err);
            }
        });
        this.timeoutId = setTimeout(reload, refreshTime);
        },    
        
    /**
     * Switches the result page without adding anything to the history.  This
     * is useful, for example, when the history itself is setting the page
     * as the result of a back or forward button press.
     * 
     * @param {Object} pageIndex The index of the page to switch to.
     */
    switchResultsPageNoHistory : function (pageIndex)
        {
        //console.info("Switching results page without history to: "+pageIndex);
        this.switchResultsPageBase(pageIndex);
        },
    
    /**
     * Switches the result page, adding the page to the history.
     * 
     * @param {Object} pageIndex The index of the page to switch to.
     */
    switchResultsPage : function (pageIndex)
        {
        //console.info("Switching results page with history to: "+pageIndex);
        
        // First, add the history.
        SearchHistory.addHistory(pageIndex);
        
        // Then go to the page.
        this.switchResultsPageBase(pageIndex);
        },
        
    switchResultsPageBase : function (pageIndex) {
        //console.info("Switching results page to: "+pageIndex);
        // OK, we want to add back button handling here.
        clearTimeout(this.timeoutId);
        this.wasComplete = false;
        
        try {
            this.loadResultsPage(pageIndex, true);
        } catch (err) {
            console.error("Caught error: "+err);
        }
    },
        
    /**
     * Stops this instance from requesting more results or processing more
     * results.
     */
    stop : function ()
        {
        //console.info("Stopping for guid: "+this.searchGuid)
        this.stopped = true;
        CommonUtils.hideSpinner();
        }
    }
);

dojo.extend(SearchPageApplicationState, 
    {
    back : function()
        {
        this.goToMe();
        },
    forward : function()
        {
        this.goToMe();
        },
        
    goToMe : function()
        {
        window.searchResults.switchResultsPageNoHistory(this.resultsPage);
        }
    });

