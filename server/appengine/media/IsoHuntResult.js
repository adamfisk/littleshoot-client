dojo.provide("littleshoot.IsoHuntResult");

dojo.declare("littleshoot.IsoHuntResult", null, 
    {
    
    constructor : function(resultData)
        {
        var category = resultData.category.toLowerCase();
        if (category == "audio")
            {
            resultData.mediaType = "audio";
            }
        this.resultDiv = this.newResultDiv(resultData, this);
        //this.resultDiv = newTorrentResultDiv(resultData, this);
        },

    /**
     * We need to do some quirky things in this case, so we have a custom
     * function to build the div.
     */
    newResultDiv : function (result, resultBuilder)
        {
        if (!result)
            {
            console.error("Bad result: "+result);
            return;
            }
        var resultDiv = document.createElement("div");
        /*
        if (result.downloadStatus)
            {
            resultDiv.className = "searchResultDownloading";
            }
        else
            {
            */
            resultDiv.className = "searchResult";
        //    }
        
        var href = resultBuilder.createHrefUrl(result);
        var baseId = SearchResultUtils.getSearchResultId(result);
        resultDiv.id = baseId;
        
        var resultLink = document.createElement("a");
        resultLink.className = "searchResultLink";
        
        // We just download it and don't try to stream with torrents.
        $(resultLink).click(function(evt) {
            evt.preventDefault();
            evt.stopPropagation();
            Download.asyncDownload(href);
        });
        
        resultBuilder.appendImage(result, resultLink, href, baseId);
        resultBuilder.appendLink(result, href, resultLink, baseId, resultDiv);
        
        resultDiv.appendChild(resultLink);
        resultDiv.appendChild(CommonUtils.clearBoth());
        return resultDiv;
        },
        
    getDiv : function ()
        {
        return this.resultDiv;
        },

    createHrefUrl : function (result) {
        result.uri = result.enclosure_url;
        var params = {
            name: result.title,
            size: result.length
        };
        var baseUrl;
        if (LittleShootUtils.hasTorrentDownloadRpc()) {
            baseUrl = Constants.START_TORRENT_DOWNLOAD_URL; 
        }
        else {
            baseUrl = Constants.DOWNLOAD_TORRENT_URL;
        }
        return SearchResultUtils.createLocalHrefUrl(result, params, baseUrl);
    },
        
    getSearchResultId : function (result)
        {
        return result.guid;
        },
        
    getTitle : function (result)
        {
        return result.title;
        },
        
    getIconParams : function (result)
        {
        var pfx = Constants.IMAGES + "sources/";
        var params = {};
        
        var sfx = "isohunt_100x30.png";
        
        params.width = 100;
        params.height = 30;
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
        
        if (result.files !== undefined)
            {
            SearchResultUtils.createSearchDetail(
                "Files: "+result.files, details);
            }
        
        if (result.Seeds !== undefined)
            {
            SearchResultUtils.createSearchDetail(
                "Seeds: "+result.Seeds, details);
            }
        
        if (result.leechers !== undefined)
            {
            SearchResultUtils.createSearchDetail(
                "Leechers: "+result.leechers, details);
            }
        
        if (result.downloads !== undefined)
            {
            SearchResultUtils.createSearchDetail(
                "Downloads: "+result.downloads, details);
            }
        
        if (result.length !== undefined)
            {
            SearchResultUtils.createSearchDetail(
                "Size: " + CommonUtils.bytesToMb(result.length) + " MB", details);
            }
        
        if (result.link !== undefined)
            {
            SearchResultUtils.createSearchDetailLink(
                "More Info ", result.link, details);
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
        
        var category = result.category.toLowerCase();
        switch (category)
            {
            case "audio":
                img.src = Constants.AUDIO_IMAGE;
                break;
            case "tv":
                img.src = Constants.VIDEOS_IMAGE;
                break;
            case "video/movies":
                img.src = Constants.VIDEOS_IMAGE;
                break;
            case "pics":
                img.src = Constants.IMAGES_IMAGE;
                break;
            case "books":
                img.src = Constants.DOCS_IMAGE;
                break;
            case "apps":
                img.src = Constants.APPS_IMAGE;
                break;
            case "games":
                img.src = Constants.APPS_IMAGE;
                break;
            default:
                img.src = Constants.DOCS_IMAGE;
            }
        img.width = 75;
        img.height = 75;
        
        
        CommonUtils.showElement(img);
        resultDiv.appendChild(img);
        }
    });

