dojo.provide("littleshoot.FlickrResult");

/**
 * Here's what the JSON for each result looks like:
 * 
 * {"thumbnailUrl":"http://farm1.static.flickr.com/31/97951579_d7e765d679",
 * "isfamily":0,"isfriend":0,
 * "title":"Hurricane Katrina from Space (hurricanekatrina)",
 * "farm":1,
 * "uri":"http://farm1.static.flickr.com/31/97951579_d7e765d679",
 * "owner":"48135670@N00",
 * "source":"flickr",
 * "server":"31",
 * "ispublic":1,
 * "id":"97951579",
 * "secret":"d7e765d679"}
 */
dojo.declare("littleshoot.FlickrResult", null, 
    {
    
    constructor : function(resultData)
        {
        //this.resultDiv = this.newResultDiv(resultData);
        this.resultDiv = SearchResultUtils.newResultDiv(resultData, this);
        },

    getDiv : function ()
        {
        return this.resultDiv;
        },

    createHrefUrl : function (result)
        {
        return result.uri;
        },
        
    getSearchResultId : function (result)
        {
        return this.getTitle(result) + result.source;
        },
        
    getTitle : function (result)
        {
        return result.title + ".jpg";
        },
        
    getIconParams : function (result)
        {
        var pfx = Constants.IMAGES + "sources/";
        var params = {};
        
        sfx = "flickr_small.gif";
        params.width = 50;
        params.height = 20;
        
        params.url = pfx + sfx;

        return params;
        },

    /**
     * Appends the link text to the result.
     */
    appendLink : function(result, href, resultLink, baseId, resultDiv)
        {
        //console.info("About to append link...");
        //console.dir(result);
        var linkDiv = document.createElement("div");
        linkDiv.className = "searchResultLinkDiv";
        linkDiv.id = baseId + "LinkDiv";
        
        var iconImg = SearchResultUtils.getIconImg (result, this);
        linkDiv.appendChild (iconImg);

        var textSpan = document.createElement ("div");
        textSpan.className = "searchResultLinkText";

        var title = this.getTitle(result);
        var nameText = document.createTextNode(title);
        textSpan.appendChild (nameText);

        linkDiv.appendChild (textSpan);
        
        var details = document.createElement("div");
        details.className = "searchResultDetails";
        
        linkDiv.appendChild(details);

        resultLink.setAttribute("href", href + ".jpg");
        dojo.attr(resultLink, "rel", "lightbox");
        dojo.attr(resultLink, "title", result.title);
        
        /*
        if (Search.isLighBoxEnabled()) {
            $(resultLink).lightBox({fixedNavigation:true, 
                extraText : function (href) {
                    return "   <a href='"+href+"' target='blank'>Open in New Window</a>";
                }
            });
        }
        */
        resultLink.appendChild(linkDiv);
        linkDiv.appendChild(CommonUtils.clearBoth());
        linkDiv = null;
        },
        
    /**
     * Appends the image to the result, often a thumbnail.
     */    
    appendImage : function (result, resultDiv, linkUrl, baseId)
        {
        var img = document.createElement("img");
        CommonUtils.hideElement(img);
        
        img.setAttribute("border", 0);
        img.className = "searchResultThumbnail";
        img.id = baseId + "Image";

        img.src = linkUrl + "_s.jpg";
        img.width = 75;
        img.alt ="Loading Flickr Image Thumbnail...";
        
        CommonUtils.showElement(img);

        resultDiv.appendChild(img);
        }
        
    });

