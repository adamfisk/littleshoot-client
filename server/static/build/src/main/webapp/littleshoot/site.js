dojo.provide("littleshoot.Site");
dojo.require("littleshoot.CommonUtils");
dojo.require("littleshoot.SiteNav");

var stateIndex = 0;
var ApplicationState = function(contentId, subSectionId, bookmarkValue)
    {
    //console.debug("Content ID: "+contentId);
    if (!contentId) 
        {
        //console.error("No content ID!");
        throw new Error("Must have a content ID");
        }
        
    this.contentId = contentId;
    this.subSectionId = subSectionId;
    if (bookmarkValue)
        {
        this.changeUrl = bookmarkValue + stateIndex++;
        }
    else
        {
        this.changeUrl = false;                    
        }
    };
    

dojo.extend(ApplicationState, 
    {
    back : function()
        {
        //console.info("BACK BUTTON CONTENT ID: "+this.contentId);
        //console.info("BACK BUTTON CALLING SUBNAV: "+this.subSectionId);
        this.goToMe();
        },
    forward : function()
        {
        //console.info("FORWARD BUTTON CONTENT ID: "+this.contentId);
        //console.info("FORWARD BUTTON CALLING SUBNAV: "+this.subSectionId);
        this.goToMe();
        },
        
    goToMe : function()
        {
        SiteNav.contentNavNoHistory(this.contentId);
        if (this.subSectionId) 
            {
            //console.info("History navigating to subcontent: " + this.subSectionId);
            SiteNav.subContentNavNoHistory(this.contentId, this.subSectionId);
            }
        }
    });
    
var Tabs = 
    {
    leftTabOnClick : null,
    rightTabOnClick : null,
    
    connectLeft : function (func)
        {
        this.leftTabOnClick = 
            this.connect(this.leftTabOnClick, "leftTab", func);
        },  
    
    connectRight : function (func)
        {
        this.rightTabOnClick = 
            this.connect(this.rightTabOnClick, "rightTab", func);
        },  
          
    connect : function (connectHandle, tabId, func)
        {
        //console.info("Connect handle: "+connectHandle);
        if (connectHandle)
            {
            dojo.disconnect(connectHandle);
            }
        return dojo.connect(dojo.byId(tabId), "onclick", null, func); 
        }
    };

var Site =
    {
    
    init: function()
        {
        //CommonUtils.addGroupLogoNoFade();
        
        function createSingleLink(name, text, linksDiv, skipAppend)
            {
            var link = document.createElement("a");
            var linkBottom = document.createElement("a");
            var navFunc = function()
                {
                SiteNav.contentNav(name);
                };
            dojo.connect(link, "onclick", null, navFunc);
            //link.setAttribute("onclick", "SiteNav.contentNav('" + name + "')");
            //link.href = "SiteNav.contentNav('" + name + "')";
            dojo.addClass(link, "navLink");
            dojo.addClass(link, name + "Nav");
            link.appendChild(document.createTextNode(text));
            
            linksDiv.appendChild(link);
            
            if (!skipAppend) 
                {
                linksDiv.appendChild(document.createTextNode(" | "));
                }
            }
        function createLink(name, text, linksDiv, bottomLinksDiv, skipAppend)
            {
            createSingleLink(name, text, linksDiv, skipAppend);
            createSingleLink(name, text, bottomLinksDiv, skipAppend);
            }
        
        // This just turns the current URL query params into an object.
        var params = CommonUtils.queryToObject();
        //console.info("Params are: "+dojo.toJson(params));
        
        var linksOuterDiv = dojo.byId("grayNavControlDivLinks");
        var linksBottomOuterDiv = dojo.byId("grayNavControlDivLinksBottom");
        
        var linksDiv = document.createElement("div");
        var bottomLinksDiv = document.createElement("div");
        createLink("whatIs", "What is LittleShoot?", linksDiv, bottomLinksDiv);
        createLink("help", "Help", linksDiv, bottomLinksDiv);
        createLink("acknowledgements", "Thanks", linksDiv, bottomLinksDiv);
        createLink("technology", "Technology", linksDiv, bottomLinksDiv);
        createLink("contact", "Contact", linksDiv, bottomLinksDiv);
        createLink("aboutUs", "About Us", linksDiv, bottomLinksDiv, true);
        //createLink("donate", "Donate", linksDiv, true);
        
        var contentDataDiv = dojo.byId("contentDataDiv");
        if (params.contentId == "download") 
            {
            linksDiv.appendChild(document.createTextNode(" | "));
            bottomLinksDiv.appendChild(document.createTextNode(" | "));
            createLink("download", "Download", linksDiv, bottomLinksDiv, true);
            contentDataDiv.appendChild(dojo.byId("download"));
            }
        
        //console.info("Created links");
        
        // This technique allows us to code these divs in HTML outside
        // of their specific placement in their associated DIVs.
        var grayNavControlDiv = dojo.byId("grayNavControlDiv");
        grayNavControlDiv.insertBefore(linksOuterDiv, grayNavControlDiv.firstChild);
        grayNavControlDiv.appendChild(dojo.byId("grayNavControlDivSearch"));
        grayNavControlDiv.appendChild(dojo.byId("grayNavControlDivPublish"));
        
        
        contentDataDiv.appendChild(dojo.byId("whatIs"));
        contentDataDiv.appendChild(dojo.byId("help"));
        contentDataDiv.appendChild(dojo.byId("aboutUs"));
        contentDataDiv.appendChild(dojo.byId("contact"));
        contentDataDiv.appendChild(dojo.byId("acknowledgements"));
        //contentDataDiv.appendChild(dojo.byId("donate"));
        contentDataDiv.appendChild(dojo.byId("technology"));
        contentDataDiv.appendChild(dojo.byId("contentDataDivSearch"));
        contentDataDiv.appendChild(dojo.byId("contentDataDivPublish"));
        
        dojo.byId("contentDataDivBottom").appendChild(dojo.byId("contentDataDivBottomSearch"));
        dojo.byId("contentDataDivBottom").appendChild(dojo.byId("contentDataDivBottomPublish"));
        
        Site.appendOsSpecificDownloadDiv();
        
        // This is used in several places below, so don't delete it!!.
        var emailSuccess = params.emailSuccess;
        if (emailSuccess) 
            {
            var message = "Great.  We've got your e-mail address.  We'll notify " +
            "you as soon as you can join the network!  Feel free to read " +
            "more about LittleShoot.";
            
            CommonUtils.showMessage("Thank You", message);
            }
        
        // We do this way down here to make sure both nav menus have the
        // download link if they should.
        linksOuterDiv.appendChild(linksDiv);
        linksBottomOuterDiv.appendChild(bottomLinksDiv);
        
        var contentId = params.contentId;
        
        if (!contentId) 
            {
            contentId = "whatIs";
            }
        
        // We can't navigate anywhere within the page before this call!
        // For whatever reason, the back stuff freaks out with a
        // painfully cryptic error.
        var state = new ApplicationState(contentId);
        dojo.back.setInitialState(state);
        
        if (contentId === "search") 
            {
            SiteNav.toSearch();
            }
        else if (contentId === "publish") 
            {
            SiteNav.toPublish();
            }
        else 
            {
            //console.debug("Navigating to: " + contentId);
            SiteNav.contentNav(contentId);
            }

        //console.info("Checking for download...");
        //var params = CommonUtils.queryToObject();
        if (params.contentId == "download")                
            {
            if (params.noAutoDownload !== true)
                {
                var delayedDownload = function()
                    {
                    setTimeout(function()
                        {
                        CommonUtils.downloadInstaller();
                        }, 
                        2000);   
                    };
                dojo.addOnLoad(delayedDownload);
                }
            }
        
        },
        
    appendOsSpecificDownloadDiv : function()
        {
        if(/Windows/.test(navigator.userAgent))
            {
            //console.info("On windows...");
            dojo.byId("download").appendChild(dojo.byId("windowsDownloadDiv"));
            }
        else if (/Mac/.test(navigator.userAgent))
            {
            dojo.byId("download").appendChild(dojo.byId("macDownloadDiv"));
            }
        else
            {
            dojo.byId("download").appendChild(dojo.byId("otherDownloadDiv"));
            }
        },
        
    appPresent : function()
        {
        //console.debug("Found app!!");
        /*
        dojo.byId("searchTooltip").innerHTML =
            "Search the LittleShoot Network";
        dojo.byId("publishTooltip").innerHTML =
            "Publish Your Files to the World";
        */
           
        Tabs.connectLeft(function(event)
            {
            SiteNav.toSearch();
            
            /*
            var loadSearchResultsTest = function()
                {
                //console.info("Loading files...");
                window.searchResults = new SearchResults();
                window.searchResults.pageIndex = 0;
                    
                var json = 
                    {
                    "totalResults" : 18,
                    "totalResultsFormatted" : 18,
                    "complete" : false,
                    "results": 
                        [
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","name":"name", "source":"littleshoot","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"}
                        
                        ]
                    }
                window.searchResults.processResultSet(json);
                }
            
            loadSearchResultsTest();
            */
            });
        
        Tabs.connectRight(function(event)
            {
            SiteNav.toPublish();
            //CommonUtils.showMessage("Publishing Disabled", "We're sorry, but publishing is temporarily "+
                //"unavailable as we add more security to the publishing infrastructure.");
            
            /*
            var loadFilesTest = function()
                {
                //console.info("Loading files...");
                var resourceCreator = new littleshoot.FileResource();
                window.resourceLoader = 
                    new ResourceLoader("files", resourceCreator, true);
                
                window.resourceLoader.pageIndex = 0;
                    
                var json = 
                    {
                    "totalResults" : 8,
                    "totalResultsFormatted" : 8,
                    "complete" : false,
                    "results": 
                        [
                        {"mimeType":"application/octet-stream","title":"title","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","title":"title","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","title":"title","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","title":"title","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","title":"title","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","title":"title","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","title":"title","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"},
                        {"mimeType":"application/octet-stream","title":"title","uri":"urn:sha1:testtesttest","tags":"test tags","urn":"urn:sha1:testtesttest","size":10,"path":"/usr/local/path.file","mediaType":"document"}
                        
                        ]
                    }
                window.resourceLoader.processResources(json);
                //window.resourceLoader.loadResultsPage(0);
                }
            
            loadFilesTest();
            */
            });
        },
    
    appNotPresent : function()
        {
        var tabFunc = function(event)
            {
            dijit.byId("emailRestrictedDialog").show();
            };
        
        //console.info("Connecting tabs");
        Tabs.connectLeft(tabFunc);
        Tabs.connectRight(tabFunc);
    
        /*
        //console.debug("app not present...");
        dojo.byId("publishTooltip").innerHTML =
            "When LittleShoot is installed, this will enable you to instantly publish "+ 
            "your files to the world.";
        dojo.byId("searchTooltip").innerHTML = 
            "When LittleShoot is installed, this will enable you to search "+ 
            "the network.";
            */ 
        },
           
    oldVersion : function()
        {
        //console.debug("Found old version!!");
        Site.appPresent();
        }
    };

/*
if (CommonUtils.onSitePage())
    {
    console.info("Loading site page!!");
    //dojo.addOnLoad(Site.init);
    if (!CommonUtils.isBrowserSupported())
        {
        CommonUtils.showIncompatibleBrowserDialog();
        //console.info("Bad browser or os...");
        }
    
    // This is necessary if we've come straight from the home page, for example -- we 
    // need to set the check boxes in that case.
    //dojo.addOnLoad(Search.setSearchCheckBoxes);
    //dojo.addOnLoad(Button.buildButtons);
    //dojo.addOnLoad(Search.enableSearchButtons);  
    //dojo.addOnLoad(CommonUtils.loadLittleShootApplet);
    dojo.addOnLoad(CommonUtils.hideSpinner);
    
    littleShootConfig = 
        {
        littleShootLoading: "loading",
        
        disableAutoLoad : true,
        
        littleShootPresent : function (apiVersion) 
            {
            console.info("LittleShoot present");
            Site.appPresent();
            },
        littleShootNotPresent : function () 
            {
            console.info("LittleShoot not present");
            Site.appNotPresent();
            },
        oldLittleShootVersion : function ()
            {
            console.info("LittleShoot old version");
            Site.oldVersion();
            }
        };
    dojo.addOnLoad(LittleShoot.hasLittleShoot);
    
    // This provides configuration options to the audio player.
    ympparams = 
        {
        scrape: false
        };  
    dojo.addOnLoad(CommonUtils.configureYmp);
    
    CommonUtils.loadUrchin();
    }
*/