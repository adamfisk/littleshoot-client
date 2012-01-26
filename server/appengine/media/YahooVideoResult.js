dojo.provide("littleshoot.YahooVideoResult");

/**
 * Here's what the JSON for each result looks like:
 * 
 * {"Summary":"American Red Cross and/orASPCA (benefit affected animals) 
 * Havilah Tower -  Here We Go Again : Live performance/Yoga: Katrina 
 * Hurricane Fundraiser - Quicktime 480x360 Havilah Tower -  Breathe : Live 
 * performance/Yoga: Katrina Hurricane Fundraiser - Quicktime 480x360 Havilah 
 * Tower -  Tick Tick Tock : Live performance/Yoga: Katrina Hurricane Fundraiser -",
 * "Height":"240",
 * "Url":"http://www.havilahtower.com/videos/katrina_benefit-Breathe-HavilahTower.mov",
 * "Channels":"2",
 * "ClickUrl":"http://www.havilahtower.com/videos/katrina_benefit-Breathe-HavilahTower.mov",
 * "FileFormat":"quicktime","RefererUrl":"http://www.havilahtower.com/videos.html",
 * "Width":"320","Streaming":"false","Duration":"190",
 * "Title":"katrina_benefit-Breathe-HavilahTower.mov",
 * "Thumbnail":{"Width":"140","Height":"105","Url":"http://scd.mm-so.yimg.com/image/1706145147"},
 * "FileSize":"9723816"}
 */
dojo.declare("littleshoot.YahooVideoResult", null, 
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
        return result.uri;
        },
        
    getSearchResultId : function (result)
        {
        return this.getTitle(result) + result.source;
        },
        
    getTitle : function (result)
        {
        return result.Title;
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
        
        if (result.FileSize)
            {
            fileSize = CommonUtils.bytesToMb(result.FileSize);
            var fileSpan = document.createElement("span");
            fileSpan.className = "searchResultDetail";
            var filesText = document.createTextNode("Size: " + fileSize+ " MB");
            fileSpan.appendChild(filesText);
            details.appendChild(fileSpan);
            }

        if (result.Duration)
            {
            duration = CommonUtils.secondsToMinutes(result.Duration);
            var durationSpan = document.createElement("span");
            durationSpan.className = "searchResultDetail";
            var durationText = 
                document.createTextNode("Duration: " + duration);
            durationSpan.appendChild(durationText);
            details.appendChild(durationSpan);
            }
        
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

        img.height = result.Thumbnail.Height;
        img.width = result.Thumbnail.Width;
        img.src = result.Thumbnail.Url;
        img.alt ="Loading Yahoo Video Thumbnail...";
        CommonUtils.showElement(img);

        resultDiv.appendChild(img);
        }
    });

