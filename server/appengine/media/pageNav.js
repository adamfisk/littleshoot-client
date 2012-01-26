dojo.provide("littleshoot.PageNav");
//dojo.require("littleshoot.CommonUtils");

/*jslint */

dojo.declare("PageNav", null, 
    {
    constructor : function(resultsPerPage, resultPageDiv, pageSwitcher)
        {
        //console.info("Creating page nav");
        this.RESULTS_PER_PAGE = resultsPerPage;
        this.resultPageDiv = resultPageDiv;
        this.pageSwitcher = pageSwitcher;
        this.pageIndex = -1;
        },
    
    updatePageLinks : function (pageIndex, totalResults)
        {
        //console.info("Updating page links...");
        if (isNaN(pageIndex))
            {
            console.error("PageNav.prototype.updatePageLinks index NaN");
            return;
            }
        if (isNaN(totalResults))
            {
            console.error("PageNav.prototype.updatePageLinks totalResults NaN");
            return;
            }
        if (totalResults <= this.RESULTS_PER_PAGE)
            {
            //console.i.info("Not enough results for paging: "+totalResults);
            CommonUtils.hideElement(this.resultPageDiv);
            return;
            }
        else
            {
            CommonUtils.showElement(this.resultPageDiv);
            }

        if (this.pageIndex == pageIndex)
            {
            //console.info("Page hasn't changed -- not updating links.");
            return;
            }
        this.pageIndex = pageIndex;
        dojo.query(".pageLinksDiv").forEach(dojo.hitch(this, function(pageLinkDiv)
            {
            this.updateLinksForDiv(pageLinkDiv, pageIndex, totalResults);
            pageLinkDiv = null;
            }));
        this.updatePreviousLinks(pageIndex, this.resultPageDiv);
        this.updateNextLinks(pageIndex, this.resultPageDiv, totalResults);
        },    

    updateLinksForDiv : function (pageLinksDiv, pageIndex, totalResults)
        {
        var totalPages = Math.ceil(totalResults/this.RESULTS_PER_PAGE);
        var upperLimit = pageIndex + 6;
        if (upperLimit > totalPages)
            {
            upperLimit = totalPages;
            }
        var lowerLimit = pageIndex - 5;
        if (lowerLimit < 0)
            {
            lowerLimit = 0;
            }
        
        pageIndex++;
        var newPageLinksDiv = document.createElement("div");
        newPageLinksDiv.id = pageLinksDiv.id;
        newPageLinksDiv.className = pageLinksDiv.className;
        
        for (var i = lowerLimit; i < upperLimit; i++)
            {
            this.appendPageLink(newPageLinksDiv, pageIndex, i+1);
            }
        // More efficient to build the new div and then swap it.
        pageLinksDiv.parentNode.replaceChild(newPageLinksDiv, pageLinksDiv);
        },
    
    updatePreviousLinks : function (pageIndex, resultPageDiv)
        {
        if (isNaN(pageIndex))
            {
            //console.error("updatePreviousLinks index NaN");
            return;
            }
        
        dojo.query(".previousLink", resultPageDiv).forEach(dojo.hitch(this, function (link)
            {
            this.updatePreviousLink(link, pageIndex);  
            }));
        },

    updatePreviousLink : function (previousLink, pageIndex)
        {
        var previousIndex = pageIndex - 1;
        if (previousIndex < 0)
            {
            previousIndex = 0;
            }
        
        if (isNaN(previousIndex))
            {
            //console.error("PageNav.prototype.updatePreviousLink index NaN");
            CommonUtils.showError("We couldn't locate the previous link");
            return;
            }
        var linkFunc = this.createFuncForIndex(previousIndex);
        
        // We create a new node here to avoid complications with multiple 
        // events.
        var link = document.createElement("a");
        link.appendChild(document.createTextNode("Previous"));
        dojo.addClass(link, "previousLink");
        dojo.connect(link, "onclick", null, linkFunc);
        
        previousLink.parentNode.replaceChild(link, previousLink);
        link = null;
        },
    
    updateNextLinks : function (pageIndex, resultPageDiv, totalResults)
        {
        dojo.query(".nextLink", resultPageDiv).forEach(dojo.hitch(this, function (link)
            {
            this.updateNextLink(link, pageIndex, totalResults);
            }));
        },

    updateNextLink : function (nextLink, pageIndex, totalResults)
        {
        var nextIndex = pageIndex + 1;
        //console.info("Updating next link to "+nextIndex);
        if (nextIndex * this.RESULTS_PER_PAGE >= totalResults)
            {
            nextIndex = pageIndex;
            //console.info("Reset next link to "+nextIndex);
            }
        var linkFunc = this.createFuncForIndex(nextIndex);
        
        // We create a new node here to avoid complications with multiple 
        // events.
        var link = document.createElement("a");
        link.appendChild(document.createTextNode("Next"));
        dojo.addClass(link, "nextLink");
        dojo.connect(link, "onclick", null, linkFunc);
        
        nextLink.parentNode.replaceChild(link, nextLink);
        link = null;
        },
    
    appendPageLink : function (pageLinksDiv, pageIndex, pageNum)
        {
        var link = document.createElement("span");
        if (pageIndex == pageNum)
            {
            link.className = "pageLinkSelected";
            }
        else
            {
            link.className = "pageLink";
            }
        link.appendChild(document.createTextNode(pageNum));
        var numToLoad = pageNum - 1;
        
        // I don't believe we need to worry about event disconnections here 
        // because we're creating an entirely new div each time.
        dojo.connect(link, "onclick", null, this.createFuncForIndex(numToLoad));
        pageLinksDiv.appendChild(link);
        link = null;
        },
    
    createFuncForIndex : function (index)
        {
        //console.info("Creating link for index: "+index);
        var func = dojo.hitch(this, function()
            {
            //console.info("About to switch pages using: "+this.pageSwitcher);
            this.pageSwitcher.switchResultsPage(index);
            });
        return func;
        }
    });
    
