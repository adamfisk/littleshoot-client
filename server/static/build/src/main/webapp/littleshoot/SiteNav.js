dojo.provide("littleshoot.SiteNav");
dojo.require("littleshoot.CommonUtils");

/**
 * This is no longer used.
 */
SiteNav =
    {
    contentNavNoHistory : function(contentId)
        {
        if (contentId == "search") {
            SiteNav.toSearchNoHistory();
        }
        else 
            if (contentId == "publish") {
                SiteNav.toPublishNoHistory();
            }
            else {
                SiteNav.contentNavBase(contentId);
            }    
        },
        
    contentNav : function(contentId)
        {
        if (!contentId) 
            {
            //console.error("No content ID!");
            throw new Error("Must have a content ID for navigating!!");
            }
        var appState = new ApplicationState(contentId, null, contentId);
        dojo.back.addToHistory(appState);
        SiteNav.contentNavBase(contentId);
        },
        
    contentNavBase : function(contentId)
        {
        //console.info("Navigating to: "+contentId);
        
        var content = dojo.byId(contentId);
        //console.debug("Showing: "+content);
        
        
        // If we've already dynamically downloaded the page we're
        // navigating to, don't do it again -- just show it.
        if (dojo.hasClass(content, "downloaded")) 
            {
            //console.info("Using downloaded content");
            SiteNav.onContent(contentId);
            }
            
        // Otherwise, we have to download it.
        else 
            {
            //console.debug("Downloading content for "+content.className);
            var url = contentId + "Content.html";
            var deferredResponse = CommonUtils.getHtml(url);
            deferredResponse.addCallback(
                function(response) 
                    {
                    //console.debug("Got response");
                    var contentBodyDiv = dojo.byId(contentId+"ContentBody");
                    ////console.debug("Got html: "+response);
                    contentBodyDiv.innerHTML = response;
                    //console.info("Called getHtml");
                    dojo.addClass(content, "downloaded");
                    
                    //console.info("Calling onContent from deferred response");
                    SiteNav.onContent(contentId);   
                    }
                );
            }
        },
      
    /**
     * Handles the event that content is available for a certain 
     * section.  This could mean it was downloaded, or it could mean
     * it was already present in the DOM.
     * 
     * @param {Object} contentId The DOM ID of the div containing the
     * data to change.
     */
    onContent : function (contentId)
        {
        var content = dojo.byId(contentId);
        // We first just hide everything.
        CommonUtils.hideAll(".toggleable");
        CommonUtils.hideAll(".searchToggle");
        CommonUtils.hideAll(".publishToggle");
        CommonUtils.hideAll(".resultPageDivTop");
        CommonUtils.hideAll(".resultPageDivBottom");
        CommonUtils.showAll(".siteNavToggle");
        CommonUtils.showElement(content);
        
        // Unselect them all.  We'll select one shortly.
        SiteNav.unselectNavs();
        
        // Now make the nav element for the selected page a different 
        // color.
        dojo.query("."+contentId + "Nav").forEach(function(toggleable)
            {
            dojo.removeClass(toggleable, "normalLightBold");
            dojo.addClass(toggleable, "normalBoldBlack");
            });
        
        // Activate the default sub-navigation link if there is one.
        var subSectionIdDefault = dojo.attr(content, "default");//content.getAttribute("default");
        if (subSectionIdDefault)
            {
            //console.debug("Navigating to default subcontent");
            SiteNav.subContentNavNoHistory(contentId, subSectionIdDefault);  
            }
            
        dojo.addClass(dojo.byId("leftTab"),"unselectedTab");
        dojo.removeClass(dojo.byId("leftTab"),"selectedTab");
        dojo.addClass(dojo.byId("rightTab"),"unselectedTab");
        dojo.removeClass(dojo.byId("rightTab"),"selectedTab");
        
        dijit.scrollIntoView(dojo.byId("topDiv"));
        },
            
    unselectNavs : function ()
        {
        dojo.query(".navLink").forEach(function(toggleable) 
            {
            dojo.removeClass(toggleable, "normalBoldBlack");
            dojo.addClass(toggleable, "normalLightBold");
            });    
        },
    
    toSearchNoHistory : function()
        {
        SiteNav.toSearchBase();
        },
       
    toSearch : function()
        {
        var appState = new ApplicationState("search", null, "search");
        dojo.back.addToHistory(appState);
        SiteNav.toSearchBase();
        },
        
    toSearchBase : function()
        {
        dojo.addClass(dojo.byId("leftTab"),"selectedTab");
        dojo.removeClass(dojo.byId("leftTab"),"unselectedTab");
        dojo.addClass(dojo.byId("rightTab"),"unselectedTab");
        dojo.removeClass(dojo.byId("rightTab"),"selectedTab");
        CommonUtils.hideAll(".publishToggle");
        CommonUtils.hideAll(".siteToggle");
        CommonUtils.hideAll(".siteNavToggle");
        CommonUtils.hideAll(".resultPageDivTop");
        CommonUtils.hideAll(".resultPageDivBottom");
        CommonUtils.showAll(".searchToggle");
        
        //console.info("Loading results...");
        SearchResultUtils.forceReload();
        },
    
    toPublishNoHistory : function()
        {
        SiteNav.toPublishBase();
        },  
                   
    toPublish : function()
        {
        var appState = new ApplicationState("publish", null, "publish");
        dojo.back.addToHistory(appState);
        SiteNav.toPublishBase();
        },  
          
    toPublishBase : function()
        {
        console.info("Switching to publish page...");
        dojo.addClass(dojo.byId("leftTab"),"unselectedTab");
        dojo.removeClass(dojo.byId("leftTab"),"selectedTab");
        dojo.addClass(dojo.byId("rightTab"),"selectedTab");
        dojo.removeClass(dojo.byId("rightTab"),"unselectedTab");
        
        CommonUtils.hideAll(".searchToggle");
        CommonUtils.hideAll(".siteToggle");
        CommonUtils.hideAll(".siteNavToggle");
        CommonUtils.hideAll(".resultPageDivTop");
        CommonUtils.hideAll(".resultPageDivBottom");
        CommonUtils.showAll(".publishToggle");
        CommonUtils.showAll(".filesToggle");
        SiteNav.unselectNavs();
        
        Publisher.init();
        
        CommonUtils.loadFiles();
        if (window.searchResults)
            {
            window.searchResults.stop();
            }
        }
    };
