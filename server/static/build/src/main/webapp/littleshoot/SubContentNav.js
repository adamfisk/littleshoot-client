dojo.provide("littleshoot.SubContentNav");
dojo.require("littleshoot.CommonUtils");

SubContentNav =
    {
    
    subContentNavNoHistory : function (contentId, subSectionId)
        {
        if (!subSectionId) 
            {
            //console.error("No subcontent ID!");
            throw new Error("Must have a subcontent ID for navigating!!");
            }
        //console.info("Navigating to subcontent: "+subSectionId);
        SubContentNav.subContentNavBase(contentId, subSectionId);  
        },
        
    subContentNav : function(contentId, subSectionId)
        {
        if (!contentId) 
            {
            //console.error("No content ID!");
            throw new Error("Must have a content ID for navigating!!");
            }
        var appState = new ApplicationState(contentId, subSectionId, 
            contentId+subSectionId);
        dojo.back.addToHistory(appState);
        SubContentNav.subContentNavBase(contentId, subSectionId);
        },
        
    subContentNavBase : function(contentId, subSectionId)
        {
        var subContentId = contentId + subSectionId;
        //console.info("Navigating to: "+subContentId);
        var toggleableClass = contentId + "Section";
        // We first just hide everything.
        CommonUtils.hideAll("."+toggleableClass);
        
        // Then show the one we want.
        //console.info("Searching for: "+subContentId);
        var subContent = dojo.byId(subContentId);
        //console.info("Showing: "+subContent);
        CommonUtils.showElement(subContent);
        
        var subNavTitleText = dojo.attr(subContent, "title");//subContent.getAttribute("title");
        //console.info("Setting sub-title text to: "+subNavTitleText);
        dojo.query(".contentTitleSub", contentId).forEach(function(each)
            {
            each.innerHTML = subNavTitleText;
            });
        
        dojo.query(".subNavLinkSelected").forEach(function(toggleable) 
            {
            toggleable.className="subNavLink";
            });
            
        var subNavName = subContentId + "SubNav";
        //console.info("Setting class name for: "+subNavName);
        var subContentNavElement = dojo.byId(subNavName);
        
        //subContentNavElement.className="normalBoldBlack subNavLink";
        subContentNavElement.className="subNavLinkSelected";
        }
    };
