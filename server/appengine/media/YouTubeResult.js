dojo.provide("littleshoot.YouTubeResult");

dojo.declare("littleshoot.YouTubeResult", null, 
    {
    
    constructor : function(resultData)
        {
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
        return result.title.$t;
        },
        
    getIconParams : function (result)
        {
        var pfx = Constants.IMAGES + "sources/";
        var params = {};
        
        var sfx = "youtube_small.jpg";
        params.width = 63;
        params.height = 27;

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

        var nameText = document.createTextNode(this.getTitle(result));
        textSpan.appendChild (nameText);

        linkDiv.appendChild (textSpan);
        
        var details = document.createElement("div");
        details.className = "searchResultDetails";
        
        if (result.yt$statistics !== undefined) {
            if (result.yt$statistics.viewCount !== undefined) {
                dojo.require("dojo.number");
                dojo.addOnLoad(function() {
                    var viewCount = 
                        dojo.number.format(result.yt$statistics.viewCount, {pattern: "###,###,###,###"});
                    SearchResultUtils.createSearchDetail("Views: "+viewCount, details);
                });
            }
        }
        
        if (result.gd$rating !== undefined) {
            if (result.gd$rating.average !== undefined) {
                dojo.require("dojo.number");
                dojo.addOnLoad(function() {
                    var rating = dojo.number.format(result.gd$rating.average, {pattern: "#.##"});
                    SearchResultUtils.createSearchDetail("Rating: "+rating, details);
                });
            }
        }
        
        if (result.media$group !== undefined) {
            if (result.media$group.yt$duration !== undefined) {
                var rawSecs = result.media$group.yt$duration.seconds;
                var mins = Math.floor((rawSecs / 60) % 60);
                var secs = rawSecs % 60;
                SearchResultUtils.createSearchDetail("Duration: "+mins+" mins, "+ secs+" secs", details);
            }
        }
        
        linkDiv.appendChild(details);

        resultLink.appendChild(linkDiv);
        linkDiv.appendChild(CommonUtils.clearBoth());
        linkDiv = null;
        },
        
    getViewCount : function (result)
        {
        if (result.yt$statistics)
            {
            return result.yt$statistics.viewCount;
            }
        return "";
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

        tn = result.media$group.media$thumbnail[0];
        img.height = tn.height;
        img.width = tn.width;
        img.src = tn.url;
        img.alt = "YouTube thumbnail...";
        CommonUtils.showElement(img);

        resultDiv.appendChild(img);
        }
    });

