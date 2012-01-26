dojo.provide("littleshoot.SearchResult");

/**
 * This should really be a factory for creating search results.
 */
dojo.declare("littleshoot.SearchResult", null, 
    {
    
    constructor : function(resultData, downloadPath)
        {
        this.resultDiv = this.newResultDiv(resultData, downloadPath);
        },

    getDiv : function ()
        {
        return this.resultDiv;
        },
        

    createHrefUrl : function (result)
        {
        var params = 
            {
            name: result.title,
            size: result.size
            };
        return SearchResultUtils.createLocalHrefUrl(result, params);
        },
    
    newResultDiv : function (result, downloadPath)
        {
        if (!result)
            {
            console.error("Bad result: "+result);
            return;
            }
        
        //console.info("Got source: "+result.source);
        switch (result.source)
            {
            case "isohunt":
                var isoHuntResult = new littleshoot.IsoHuntResult(result);
                return isoHuntResult.getDiv();
                
            case "youtube":
                var ytResult = new littleshoot.YouTubeResult(result);
                return ytResult.getDiv();
 
            case "flickr":
                var flickrResult = new littleshoot.FlickrResult(result);
                return flickrResult.getDiv();
                
            case "yahoo_video":
                var yahooResult = new littleshoot.YahooVideoResult(result);
                return yahooResult.getDiv();
            
            case "yahoo_boss_image":
                var yahooImageResult = new littleshoot.YahooBossResult(result);
                return yahooImageResult.getDiv();
            default:
                return this.newStandardResultDiv(result, downloadPath);
            }
        },
        
    newStandardResultDiv : function (result, downloadPath)
        {
        //console.info("About to process result...");
        //console.dir(result);
        var resultDiv = document.createElement("div");
        
        if (result.downloadStatus)
            {
            resultDiv.className = "searchResultDownloading";
            }
        else
            {
            resultDiv.className = "searchResult";
            }

        var href = this.createHrefUrl(result);
        //var linkDiv = document.createElement("div");
        var baseId = SearchResultUtils.getSearchResultId(result);
        if (result.sha1)
            {
            resultDiv.id = baseId;
            //linkDiv.id = baseId + "linkDiv";
            }
        
        var resultLink = document.createElement("a");
        resultLink.className = "searchResultLink";
        
        resultLink.setAttribute("href", href);

        //console.info("Download status: "+result.downloadStatus);
        if (result.mediaType == "audio")
        //if (result.title.endsWith(".mp3"))    
            {
            dojo.connect(resultLink, "onclick", function (evt) {
                //console.info("Got a click on yahoo audio!!");
                evt.stopPropagation();
                evt.preventDefault();
                playYahooMedia(resultLink);
            });
            
        
            //console.info("mime: "+result.mimeType);
            if (result.mimeType)
                {
                dojo.attr(resultLink, "type", result.mimeType);
                }
            }
        else
            {
            resultLink.setAttribute("target", "_blank");
            }
        
        this.appendImage(result, resultLink, href, baseId);
        this.appendLink(result, href, resultLink, baseId, downloadPath, resultDiv);
        
        //linkDiv.appendChild(resultLink);
        resultDiv.appendChild(resultLink);
        resultDiv.appendChild(CommonUtils.clearBoth());
        return resultDiv;
        },
        
    getIconParams : function (result)
        {
        //var pfx = "http://www.littleshoot.org/images/sources/";
        var pfx = Constants.IMAGES + "sources/";
        var sfx = undefined;
        var params = {};

        // It is easier and faster to hand-edit the image sizes rather than
        // scale them dynamically based on their aspect ratios.  However, it
        // preserves some flexibility, since the images themselves are slightly
        // larger.
        switch (result.source)
            {

            case "littleshoot":
                sfx = "littleshoot_small.gif";
                params.width = 25;
                params.height = 25;
                break;

            case "limewire":
                //sfx = "limewire_green_small.png";
                
                // For some reason the FireFox scaling looks better than
                // PhotoShop's in this case.
                sfx = "limewire.gif";
                params.width = 70;
                params.height = 21;
                break;
            
            default:
                sfx = "littleshoot_small.gif";
                params.width = 25;
                params.height = 25;
                break;
            }

        params.url = pfx + sfx;

        return params;
        },
    
    /**
     * Appends the link text to the result.
     */
    appendLink : function(result, href, resultLink, baseId, downloadPath, 
        resultDiv)
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

        var nameText = document.createTextNode(result.title);
        textSpan.appendChild (nameText);

        linkDiv.appendChild (textSpan);
        
        var details = document.createElement("div");
        details.className = "searchResultDetails";
        
        // This is LimeWire.
        if (result.numSources)
            {
            //console.info("Appending numSources");
            var sourcesText;
            if (result.numSources < 3)
                {
                sourcesText = "Sources: "+result.numSources+" (More is Better!),";
                }
            else
                {
                sourcesText = "Sources: "+result.numSources+",";
                }
            
            SearchResultUtils.createSearchDetail(sourcesText, details);
            }
        
        // This is LittleShoot.
        if (result.numOnlineInstances)
            {
            SearchResultUtils.createSearchDetail(
                "Sources: "+result.numOnlineInstances+",", details);
            }
        
        if (result.numDownloads)
            {
            SearchResultUtils.createSearchDetail(
                "Downloads: "+result.numDownloads+",", details);
            }
        
        if (result.size && result.size >= 0)
            {
            SearchResultUtils.createSearchDetail(
                "Size: " + CommonUtils.bytesToMb(result.size) + " MB", details);
            }
        
        if (result.audios__audio__bitrate__)
            {
            SearchResultUtils.createSearchDetail(
                "Bitrate: " + result.audios__audio__bitrate__, details);
            }
        
        linkDiv.appendChild(details);
        
        if (result.downloadStatus)
            {
            var downloadDetails = 
                SearchResultUtils.newDownloadDetailsDiv(result, downloadPath, resultDiv);
            linkDiv.appendChild(downloadDetails);
            }
        resultLink.appendChild(linkDiv);
        linkDiv.appendChild(CommonUtils.clearBoth());
        linkDiv = null;
        },
    
        
    resizeImage : function (img, width, height)
        {
        // We can only resize the image if we know its dimensions.  The
        // dimensions are only available when the image is completely loaded.
        if (img.complete)
            {
            if (img.width > img.height)
                {
                img.width = width;
                }
            else
                {
                img.height = height;
                }
            CommonUtils.showElement(img);
            }
        else
            {
            // We do not know the dimensions.  We set a timer to try again in
            // 800 milliseconds, just hoping that the image was loaded.  This
            // does not seem to be the best way to do this, since it seems like
            // you should be able to get notification of a complete load.  This
            // is the best thing I could figure out for now.  2007_12_08_jjc
            var self = this;
            window.setTimeout (
                function () { self.resizeImage (img, width, height); },
                800);
            }
        },
    
    /**
     * Sets the dimensions for a thumbnail image using JSON data if available
     * and just setting it dynamically when the image loads otherwise.
     */
    setDimensions : function (img, result)
        {
        var MAX_WIDTH = 100;
        var MAX_HEIGHT = 75;
        if (result.thumbWidth && result.thumbHeight)
            {
            if (result.thumbWidth <= MAX_WIDTH && result.thumHeight <= MAX_HEIGHT)
                {
                img.width = result.thumbWidth;
                img.height = result.thumbHeight;
                }
            // Otherwise, one's over the limit, and we need to scale.
            else
                {
                var widthOver = result.thumbWidth - MAX_WIDTH;
                var heightOver = result.thumbHeight - MAX_HEIGHT;
                if (widthOver > heightOver)
                    {
                    img.width = MAX_WIDTH;
                    img.height = (MAX_WIDTH/result.thumbWidth) * result.thumbHeight;
                    }
                else
                    {
                    img.height = MAX_HEIGHT;
                    img.width = (MAX_HEIGHT/result.thumbHeight) * result.thumbWidth; 
                    }
                }
            
            CommonUtils.showElement(img);
            }
        else
            {
            var self = this;
            dojo.addOnLoad (function () { self.resizeImage (img, 75, 75); });
            }
        },
        
    /**
     * Appends the image to the result, often a thumbnail.
     */    
    appendImage : function (result, resultDiv, linkUrl, baseId)
        {
        var img = document.createElement("img");
        CommonUtils.hideElement(img);
        
        img.setAttribute("border", 0);
        if (result.downloadStatus)
            {
            img.className = "searchResultThumbnailDownloading";
            }
        else
            {
            img.className = "searchResultThumbnail";
            }
        img.id = baseId + "Image";
        //img.src = result.thumbnailUrl;
        
        if (result.source == "littleshoot")
            {
            CommonUtils.setDefaultImgProperties(img, result.mediaType);
            }
        else if (result.source == "limewire")
            {
            CommonUtils.setDefaultImgProperties(img, result.mediaType);
            }
        
        CommonUtils.showElement(img);
        resultDiv.appendChild(img);
        }
    });

