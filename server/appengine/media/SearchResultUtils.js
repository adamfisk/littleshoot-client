dojo.provide("littleshoot.SearchResultUtils");
dojo.require("dijit.ProgressBar");

/**
 * This class contains easy to use static utility methods.
 */
SearchResultUtils =
    {
    forceReload : function ()
        {
        //console.info("Forcing search reload...");
        SearchResultUtils.reload(dojo.cookie("lastSearchGuid"), true);
        },

    /**
     * This function should be called when we want to reload existing search 
     * data, with the search ID matching the specified GUID.
     */
    reload : function (guid, forceReload)
        {
        if (!guid)
            {
            //console.info("No guid!  Not loading results.");
            return;
            }
        //console.info("Reloading search for guid: "+guid);
        
        // If we've got existing results, make sure we set the associated
        // data appropriately.
        if (window.searchResults)
            {
            window.searchResults.stop();
            window.searchResults = null;
            if (window.lastJsonSearchData && guid)
                {
                var data = 
                    SearchResultUtils.getSearchData(window.lastJsonSearchData, guid);
                Search.setKeyword(data.keywords);
                }
            }
        //console.info("Creating results instance...");
        
        window.searchResults = new littleshoot.SearchResults(guid, forceReload);
        window.searchResults.loadResultsPage(0);
        },
        
    createLocalHrefUrl : function (result, params, baseUrl)
        {
        if (!result)
            {
            //console.info("Null result: "+result);
            console.error("No result");
            return "";
            }
        
        // We can identify all instances of the resource with the URN, so use it
        // if its there.
        var uri;
        if (result.sha1)
            {
            uri = result.sha1;
            }
        else
            {
            uri = result.uri;
            }
        
        //console.info("Result is: "+dojo.toJson(result));
        
        if (result.mediaType === "audio")
            {
            cancelOnStreamClose = false;
            }
        else
            {
            // Just let the user manually cancel downloads.
            cancelOnStreamClose = false;
            }
        
        //console.info("Creating params");
        params.uri = uri;
        params.cancelOnStreamClose = cancelOnStreamClose;
        params.noCache = (new Date()).getTime();
        
        if (result.mimeType !== undefined)
            {
            params.mimeType = result.mimeType;
            }
        if (result.sha1 !== undefined)
            {
            params.urn = result.sha1;
            }
        
        // Hack for backwards compatibility with 0.32.
        if (result.source === "limewire")
            {
            params.source = result.source;
            }
        var fileUrlBase;
        
        if (baseUrl) {
            fileUrlBase = baseUrl;
        } 
        else {
            fileUrlBase = Constants.DOWNLOAD_URL;
        }
        
        var fileUrl = fileUrlBase +
            encodeURIComponent(result.title) +
            "?"+
            dojo.objectToQuery(params);
        
        //console.info("Returning URL: "+fileUrl);
        return fileUrl;
        },
        
    getSearchData : function (jsonData, guid)
        {
        if (!jsonData)
            {
            //console.info("No data...");
            return;
            }
        for (var i = 0; i < jsonData.searchData.length; i++)
            {
            var curData = jsonData.searchData[i];
            var curGuid = curData.guid;
            
            //console.info("Comparing "+curGuid +" to "+ guid);
            if (curGuid == guid)
                {
                //console.info("Got match!!");
                return curData;
                }
            }
        
        //console.info("No matching data for guid "+guid+" in " + dojo.toJson(jsonData.searchData));
        },

    newResultDiv : function (result, resultBuilder)
        {
        if (!result)
            {
            console.error("Bad result: "+result);
            return;
            }
        var resultDiv = document.createElement("div");
    
        resultDiv.className = "searchResult";
    
        var href = resultBuilder.createHrefUrl(result);
        var baseId = resultBuilder.getSearchResultId(result);
        
        var resultLink = document.createElement("a");
        resultLink.className = "searchResultLink";
        
        resultLink.setAttribute("href", href);
        if (result.mediaType == "audio")
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
        
        resultBuilder.appendImage(result, resultLink, href, baseId);
        resultBuilder.appendLink(result, href, resultLink, baseId, resultDiv);
        
        resultDiv.appendChild(resultLink);
        resultDiv.appendChild(CommonUtils.clearBoth());
        return resultDiv;
        },
        
    getIconImg : function (result, resultBuilder)
        {
        var img = document.createElement ("img");
        var params = resultBuilder.getIconParams (result);
        
        img.src = params.url;
        img.width = params.width;
        if (params.height !== undefined)
            {
            img.height = params.height;
            }
        
        return img;
        },
        
        
    updateDownload : function (result, path)
        {
        //console.info("Updating download: "+dojo.toJson(result));
        //console.info("Updating with path: "+path);
        if (!path)
            {
            console.error("No path!!");
            throw new Error("No path!!");
            }
        var resultId = SearchResultUtils.getSearchResultId(result);
        
        //console.info("Created ID: "+resultId);
        var resultDiv = dojo.byId(resultId);
        if (!resultDiv)
            {
            console.error("No result div.  Mismatched ID assignment?");
            throw new Error("No result div!");
            }
        var stopId = SearchResultUtils.newStopIconId(result);
        if (!dojo.hasClass(resultDiv, "searchResultDownloading"))
            {
            //console.info("Adding download details...");
            dojo.removeClass(resultDiv, "searchResult");
            dojo.addClass(resultDiv, "searchResultDownloading");
            var downloadDetails = 
                SearchResultUtils.newDownloadDetailsDiv(result, path, resultDiv);
            dojo.byId(resultId + "LinkDiv").appendChild(downloadDetails);
            
            var img = dojo.byId(resultId + "Image");
            dojo.removeClass(img, "searchResultThumbnail");
            dojo.addClass (img, "searchResultThumbnailDownloading");
            }
        else
            {
            //console.info("Updating download details...");
            SearchResultUtils.updateDownloadDetails(result, path);
            }
        resultDiv = null;
        },
        
    updateDownloadDetails : function (result, path)
        {
        var stopId = SearchResultUtils.newStopIconId(result);
        var stopDiv = dojo.byId(stopId);
        if (!stopDiv)
            {
            console.error("Could not find stop ID: "+stopId);
            }
        else
            {
            // Only show the cancel icon when we're in the downloading state.
            if (result.downloadStatus === 2)
                {
                CommonUtils.showElement(stopDiv);
                }
            else
                {
                CommonUtils.hideElement(stopDiv);
                }
            }
        var baseId = SearchResultUtils.getDownloadId(result);
        var pbId = baseId + "Pb";
        var pb = dijit.byId(pbId);
        if (!pb)
            {
            console.error("Could not find progress bar with ID: "+pbId);
            }
        var details = SearchResultUtils.newDownloadDetails(result, pb, path);
        var downloadDetailsDiv = dojo.byId(baseId);
        downloadDetailsDiv.replaceChild(details, downloadDetailsDiv.lastChild);
        
        baseId = null;
        pb = null;
        downloadDetailsDiv = null;
        },
        
    newDownloadDetailsDiv : function (result, path, resultDiv)
        {
        //console.info("Creating new download details div");
        var downloadDetailsDiv = document.createElement("div");
        var baseId = SearchResultUtils.getDownloadId(result);
        downloadDetailsDiv.id = baseId;
        downloadDetailsDiv.className = "downloadDetails";
        dojo.addClass(downloadDetailsDiv, "searchResultDetails");
        
        var downloadProgressDiv = document.createElement("div");
        downloadProgressDiv.className = "downloadProgress";
        var pbId = baseId + "Pb";
        var pb = dijit.byId(pbId);
        if (pb)
            {
            var parent = pb.domNode.parentNode;
            if (parent)
                {
                parent.removeChild(pb.domNode);
                }
            
            pb.update({ progress: 0});
            }
        else
            {
            pb = new dijit.ProgressBar(
                { 
                progress: 0, 
                maximum: 100, 
                id: pbId
                });
            }
        
        downloadProgressDiv.appendChild(pb.domNode);
        downloadDetailsDiv.appendChild(downloadProgressDiv);

        var stopImg = document.createElement("div");
        stopImg.id = SearchResultUtils.newStopIconId(result);
        dojo.addClass(stopImg, "stopDownloadImage");
        dojo.addClass(stopImg, "smallIcon");
        dojo.addClass(stopImg, "stopEnabled");
        
        dojo.connect(stopImg, "onclick", null, function(event)
            {
            event.stopPropagation();
            
            var okCallback = function()
                {
                var stopLoadHandler = function(data, ioArgs) 
                    {
                    //console.info("Stop returned...");
                    return data;
                    };
                var stopErrorHandler = function(data, ioArgs)
                    {
                    console.error("Stop failed: "+data);
                    return data;
                    };
                var deferred = dojo.io.script.get(
                    { 
                    url: Constants.CLIENT_URL + "stopDownload", 
                    callbackParamName: "callback",
                    load: stopLoadHandler,
                    error: stopErrorHandler,
                    content: {'uri' : result.uri},
                    timeout: 20000,
                    noCache: (new Date()).getTime()
                    });
                };
            
            CommonUtils.showConfirmDialog("Cancel Download?", 
                "Are you sure you want to cancel downloading '"+result.title+"'? " +
                "You will lose all downloaded data and cannot undo this action.", 
                function() {
                    dojoLoader.scriptGet(okCallback);
                });
            });
        
        resultDiv.appendChild(stopImg);
        CommonUtils.hideElement(stopImg);
        
        var details = SearchResultUtils.newDownloadDetails(result, pb, path);
        
        downloadDetailsDiv.appendChild(details);
        return downloadDetailsDiv;
        },
        
    newDownloadDetails : function (result, pb, path)
        {
        var str;
        var percentComplete;
        switch (result.downloadStatus)
            {
            case 0:
                str = "Download starting...";
                break;
            case 1:
                str = "Accessing download sources...";
                break;
            case 2:
                var speed = dojo.number.format(result.downloadSpeed, {pattern: "###,###,###.##"});
                str = "Downloading at "+speed+" KB/s from "+result.downloadNumSources+" source(s)";
                var numBytes;
                // IsoHunt uses length, for example.
                if (result.length)
                    {
                    numBytes = result.length;
                    }
                else 
                    {
                    numBytes = result.size;
                    }
                percentComplete = 
                    Math.floor(100 * (result.downloadBytesRead/numBytes));
                break;
            case 3:
                str = "Could not find sources";
                break;
            case 4:
                str = "Could not locate sources";
                break;
            case 5:
                str = "Canceled";
                break;
            case 6:
                str = "Complete";
                percentComplete = 100;
                break;
            case 7:
                str = "Failed";
                break;
            case 100:
                str = "Verifying download integrity";
                percentComplete = 100;
                break;
            case 101:
                str = "Download corrupted";
                percentComplete = 100;
                break;
            case 102:
                str = "Download verified";
                percentComplete = 100;
                break;
            case 200:
                str = "Moving file";
                percentComplete = 100;
                break;
            case 201:
                str = "Moved file to "+path;
                percentComplete = 100;
                break;
            case 202:
                str = "Move file failed";
                percentComplete = 100;
                break;
            case 203:
                str = "Added to iTunes. Saved at "+path;
                percentComplete = 100;
                break;
            default:
                str = "Unknown download state: "+statusCode;
            }
        
        var details = document.createElement("span");
        details.id = SearchResultUtils.getDownloadId(result) + "Status";
        details.className = "downloadStatusSpan";
        details.appendChild(document.createTextNode(str));
        
        //details.appendChild(document.createElement("br"));
        //details.appendChild(document.createTextNode("Folder: " + path));
            
        if (percentComplete)
            {
            //console.info("Updating percent complete to: "+percentComplete);
            pb.update({ progress: percentComplete});
            }

        return details;
        },     

    newStopIconId : function(result)
        {
        return SearchResultUtils.getBaseId(result) + "-StopImage";
        },
        
    getSearchResultId : function (result)
        {
        return SearchResultUtils.getBaseId(result) + "-SearchResult";
        },
        
    getDownloadId : function (result)
        {
        return SearchResultUtils.getBaseId(result) + "-Download";
        },
        
    getBaseId : function (result)
        {
        if (result.sha1)
            {
            return result.sha1 + result.source;
            }
        else
            {
            // Some characters are not allowed in IDs.  See:
            // http://www.w3.org/TR/REC-html40/types.html#type-name
            if (CommonUtils.startsWith(result.uri, "http://"))
                {
                return result.uri.substring(7) + result.source;
                }
            
            else if (CommonUtils.startsWith(result.uri, "urn:sha1:"))
                {
                return result.uri.substring(9) + result.source;
                }
            else
                {
                return result.uri + result.source;
                }
            }
        },
        
    createSearchDetail : function (txt, container)
        {
        if (!container)
            {
            console.error("No container!!");
            return;
            }
            
        var sp = document.createElement("span");
        sp.className = "searchResultDetail";
        var spText = document.createTextNode(txt);
        sp.appendChild(spText);
        container.appendChild(sp);
        return sp;
        },
        
    createSearchDetailLink : function (txt, link, container)
        {
        var linkText = document.createTextNode(txt);
        var a = document.createElement("a");
        a.className = "searchResultDetailLink";
        a.setAttribute("href", link);
        a.setAttribute("target", "_blank");
        a.appendChild(linkText);
        
        $(a).click(function(evt) {
            evt.stopPropagation();
        });
        container.appendChild(a);
        }
    };