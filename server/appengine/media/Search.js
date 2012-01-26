dojo.provide("littleshoot.Search");
//dojo.require("littleshoot.CommonUtils");
//dojo.require("littleshoot.SearchResults");

dojo.declare("littleshoot.Search", null, 
    {
    constructor : function()
        {
        },

    search : function () {
        /**
         * This indicates the search has started, i.e. we've received the
         * callback from the client indicating the client started the search.  
         * We now need to load the results.
         */
        var onSearchStarted = function (guid)
            {
            //console.info("Search...getting ready to process results");
            if (CommonUtils.onIndexPage())
                {
                //console.info("Navigating to search page from somewhere else...");
                
                window.location.href = "search";
                }
            else
                {
                // The problem here is we don't actually know if we're really
                // on the search page here...
                if (window.searchResults)
                    {
                    //console.info("Stopping old results...");
                    window.searchResults.stop();
                    }
                //console.info("Creating new search results and loading");
                window.searchResults = 
                    new littleshoot.SearchResults(guid, false);
                window.searchResults.loadResultsPage(0, true);
                }
            };
        
        var loadHandler = function(data, ioArgs)
            {
            //console.info("Received response to search.  GUID is: "+data.guid);
            var guid = data.guid;
            dojo.cookie("lastSearchGuid", guid);
            onSearchStarted(guid);
            };
            
        var errorHandler = function(data, ioArgs)
            {
            console.error("Dojo returned: "+data);
            CommonUtils.showMessage("LittleShoot Not Found", 
                "We're sorry, but we could not access your search results.  "+
                "Are you sure LittleShoot is successfully installed?");
            };
        
        var values = dijit.byId("searchForm").attr("value");
        CommonUtils.setCookie("searchForm", dojo.toJson(values));
        
        if (CommonUtils.isBlank(values.keywords))
            {
            //console.info("No search term");
            CommonUtils.showMessage("No Search Terms", 
                "Please enter words to search for.");
            return null;
            }
        
        var getConfig = { 
            url: Constants.CLIENT_URL + "search",
            callbackParamName: "callback",
            load: loadHandler,
            error: errorHandler,
            content: values,
            timeout: 40000,
            noCache: (new Date()).getTime()
        };
        
        var getWhenLoaded = function () {
            return dojo.io.script.get(getConfig);
        };
        
        dojoLoader.scriptGet(getWhenLoaded);
    }
});

Search =
    {
    lightBoxEnabled : false,
    
    onOnLoad : function()
        {
        //console.info("Handling on load call for search window...");
        // We go through all of this because the options flicker otherwise
        // before dojo is loaded.  We can't just set the raw opacity either
        // because different browsers handle it differently.
        $("#grayNavControlDivSearch").show();
        //CommonUtils.showElement(node);

        Search.activate();
        SearchResultUtils.forceReload();
        
        /*
        var state = {
            back: function() { console.info("Initial state...not sure what to do..."); },
            forward: function() { console.info("Initial state...not sure what to do..."); }
        };
        dojo.back.setInitialState(state);
        */
        },
        
    activate : function()
        {
        //console.info("Activating search...");
        Search.setSearchCheckBoxes();
        Search.enableSearchButtons(); 
        },
        
    enableLightBox : function ()
        {
        Search.lightBoxEnabled = true;
        },
        
    isLighBoxEnabled : function ()
        {
        return Search.lightBoxEnabled;
        },
        
    startSearch : function()
        {
        if (!window.gotLittleShoot)
            {
            LittleShoot.downloadPrompt();
            return;
            }
        //console.info("Starting search...");
        var keywordValue = dojo.byId("searchForm").keywords.value;
        Search.setKeyword(keywordValue);
        //console.info("Sending search: "+keywordValue);
        var searcher = new littleshoot.Search();
        searcher.search();
        if (pageTracker) {
            pageTracker._trackPageview("search");
        }
        },
        
    enableSearchButtons : function()
        {
        //console.info("Enabling search buttons...");
        
        if (document.searchButtonConnectId)
            {
            dojo.disconnect(document.searchButtonConnectId);
            }
        
        var button = dojo.byId("searchButton");
        document.searchButtonConnectId = 
            dojo.connect(button, "onclick", null, function () {
                //console.info("Starting search from buttom click...");
                Search.startSearch();
            }); 
        },
        
    setSearchCheckBoxes : function ()
        {
        var cookie = dojo.cookie("searchForm");
        
        if (cookie)
            {
            //console.info("Got cookie: "+cookie);
            var params = dojo.fromJson(cookie);
            Search.setSearchCheckBoxesWithData(params);
            }
        else
            {
            console.warn("no search cookie data!!!");
            }
        },
        
    setSearchCheckBoxesWithData : function (formParams)
        {
        //console.info("Setting form data with: "+formParams);
        if (!formParams)
            {
            console.warn("No form params??");
            return;
            }
        // Store them in case we're on the site page.
        var keywords = formParams.keywords;
        
        // We set the keywords to undefined for privacy reasons -- we
        // don't want to display what you were searching for to the next
        // person who comes along.
        if (CommonUtils.onIndexPage())
            {
            formParams.keywords = undefined;
            }
        
        var form = dijit.byId("searchForm");
        form.attr("value", formParams);
        
        //console.info("Form now has JSON: "+form.attr("value"));
        
        // We only make this call on the site page because if we're there
        // it's almost always because the user has just initiated a search,
        // and we want to initialize all the keyword fields in that case.
        if (!CommonUtils.onIndexPage())
            {
            Search.setKeyword(keywords);
            }
        },
        
    setKeyword : function (keyword)
        {
        //console.info("Using keyword: "+keyword);
        if (!keyword) 
            {
            return;
            }
        var keywordNode = document.createTextNode(keyword);
        var searchTermDiv = dojo.byId("searchTermDiv");
        if (!searchTermDiv)
            {
            return;
            }
        if (searchTermDiv.firstChild)
            {
            searchTermDiv.replaceChild(keywordNode, searchTermDiv.firstChild);
            }
        else
            {
            searchTermDiv.appendChild(keywordNode);
            }
        }
    };
    
