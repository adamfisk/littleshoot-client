dojo.provide("littleshoot.YahooBossResult");

/**
 * Here's what the JSON for each result looks like:
 * 
 * {"refererurl":"http://www.cs.fiu.edu/~rvara001/hurricane-katrina",
 * "width":"2304","thumbnail_height":"127","title":"hurricane-katrina 008.jpg",
 * "thumbnail_width":"170",
 * "thumbnail_url":"http://sp1.yt-thm-a01.yimg.com/image/25/m8/4251123182",
 * "filename":"hurricane-katrina 008.jpg","date":"2005/09/02","height":"1728",
 * "abstract":"hurricane-katrina 00.. 02-Sep-2005 01:49 1.7M hurricane-katrina 
 * 00.. 02-Sep-2005 01:50 1.7M hurricane-katrina 00.. 02-Sep-2005 01:50 1.7M 
 * hurricane-katrina 00.. 02-Sep-2005 01:57 1.8M",
 * "clickurl":"http://www.cs.fiu.edu/~rvara001/hurricane-katrina/hurricane-katrina%20008.jpg",
 * "refererclickurl":"http://www.cs.fiu.edu/~rvara001/hurricane-katrina",
 * "url":"http://www.cs.fiu.edu/~rvara001/hurricane-katrina/hurricane-katrina%20008.jpg",
 * "size":"1.7MB","format":"jpeg","mimetype":"image/jpeg"}
 */
dojo.declare("littleshoot.YahooBossResult", null, 
    {
    
    constructor : function(resultData)
        {
        this.resultDiv = this.newResultDiv(resultData);
        },

    getDiv : function ()
        {
        return this.resultDiv;
        },
        
    newResultDiv : function (result)
        {
        if (!result)
            {
            console.error("Bad result: "+result);
            return;
            }
        var resultDiv = document.createElement("div");

        resultDiv.className = "searchResult";

        var href = this.createHrefUrl(result);
        var baseId = this.getSearchResultId(result);
        
        var resultLink = document.createElement("a");
        resultLink.className = "searchResultLink";
        
        resultLink.setAttribute("href", href);
        resultLink.setAttribute("target", "_blank");
        
        this.appendImage(result, resultLink, href, baseId);
        this.appendLink(result, href, resultLink, baseId, resultDiv);
        
        //linkDiv.appendChild(resultLink);
        resultDiv.appendChild(resultLink);
        resultDiv.appendChild(CommonUtils.clearBoth());
        return resultDiv;
        },

    createHrefUrl : function (result)
        {
        return result.clickurl;
        },
        
    getSearchResultId : function (result)
        {
        return this.getTitle(result) + result.source;
        },
        
    getTitle : function (result)
        {
        return result.title;
        },
        
    getIconParams : function (result)
        {
        //var pfx = "http://www.littleshoot.org/images/sources/";
        var pfx = Constants.IMAGES + "sources/";
        var params = {};
        
        sfx = "yahoo_logo_small.jpg";
        params.width = 68;
        params.height = 19;

        params.url = pfx + sfx;

        return params;
        },

    getIconImg : function (result)
        {
        var img = document.createElement ("img");
        var params = this.getIconParams (result);

        img.src = params.url;
        img.width = params.width;
        img.height = params.height;

        return img;
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
        
        var iconImg = this.getIconImg (result);
        linkDiv.appendChild (iconImg);

        var textSpan = document.createElement ("div");
        textSpan.className = "searchResultLinkText";

        var nameText = document.createTextNode(this.getTitle(result));
        textSpan.appendChild (nameText);

        linkDiv.appendChild (textSpan);
        
        var details = document.createElement("div");
        details.className = "searchResultDetails";
        
        linkDiv.appendChild(details);

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

        img.height = result.thumbnail_height;
        img.width = result.thumbnail_width;
        img.src = result.thumbnail_url;
        img.alt ="Loading Yahoo Image Thumbnail...";
        CommonUtils.showElement(img);

        resultDiv.appendChild(img);
        }
    });

