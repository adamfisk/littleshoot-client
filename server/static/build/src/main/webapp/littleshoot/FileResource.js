dojo.provide("littleshoot.FileResource");

dojo.declare("littleshoot.FileResource", null, 
    {
    constructor : function()
        {
        },
    createResource : function (result)
        {
        //console.info("Appending result for: "+result.title);
        var resultDiv = document.createElement("div");
        resultDiv.className = "searchResult";
        
        var resultDataDiv = document.createElement("div");
        resultDataDiv.className = "publishResultData";
        resultDiv.appendChild(resultDataDiv);
        
        var resultTitleDiv = document.createElement("div");
        resultTitleDiv.className = "publishResultTitle normalBold";
        resultTitleDiv.appendChild(document.createTextNode(result.title));
        resultDataDiv.appendChild(resultTitleDiv);
        
        var resultTagsDiv = document.createElement("div");
        resultTagsDiv.className = "publishResultTags normal";
        
        var tagsText = "";
        if (result.tags && result.tags.length > 0)
            {
            tagsText += result.tags[0];
            for (var i=1; i<result.tags.length; i++)
                {
                tagsText += ", ";
                tagsText += result.tags[i];
                }
            }
        if (CommonUtils.isBlank(tagsText)) 
            {
            tagsText = "None";
            }
        //console.info('Done with tags, got: ', tagsText);
        resultTagsDiv.appendChild(document.createTextNode("Tags: "+tagsText));
        resultDataDiv.appendChild(resultTagsDiv);
        
        var img = document.createElement("img");
        img.className = "publishResultThumbnail";
        CommonUtils.setDefaultImgProperties(img, result.mediaType);

        resultDiv.appendChild(img);
        var resultControlDiv = document.createElement("div");
        resultControlDiv.className = "publishResultControlDiv";
    
        
        // Add the REMOVE button.
        var resultRemoveDiv = document.createElement("span");
        var removeText = document.createTextNode("REMOVE");
        resultRemoveDiv.className = "publishResultRemove normalBoldLink";
        dojo.connect(resultRemoveDiv, "mouseup", null, function(event) 
            {
            //console.info("Creating file remover...")
            var fileRemover = new littleshoot.FileRemover(result.sha1, result.title);
            fileRemover.removeFile();
            });
                    
        resultRemoveDiv.appendChild(removeText);
        resultControlDiv.appendChild(resultRemoveDiv);
        // End REMOVE button.
        
        // Add the EDIT button.
        var resultEditDiv = document.createElement("span");
        var editText = document.createTextNode("EDIT");
        resultEditDiv.appendChild(editText);
        resultEditDiv.className = "publishResultEdit normalBoldLink";
        dojo.connect(resultEditDiv, "mouseup", null, function(event) 
            {
            CommonUtils.setValue("editTagsTextBoxInput", tagsText);
            CommonUtils.setValue("editTagsSha1", result.sha1);
            dijit.byId("editFileDialog").show();
            });
                    
        resultControlDiv.appendChild(resultEditDiv);
        // End EDIT button.
        
        // Add the OPEN button.
        var resultOpen = document.createElement("a");
        var openText = document.createTextNode("OPEN");
        resultOpen.appendChild(openText);
        resultOpen.className = "publishResultOpen normalBoldLink";
        resultOpen.setAttribute("target", "_blank");
        resultOpen.setAttribute("href", this.createOpenUrl(result));
        resultControlDiv.appendChild(resultOpen);
        if (result.mediaType == "audio")
            {
            dojo.addClass(resultOpen, "ymp-play-class ymwp-track-paused-class");
            dojo.attr(resultOpen, "title", result.title);
            if (result.mimeType)
                {
                dojo.attr(resultOpen, "type", result.mimeType);
                }
            }
        // End OPEN button.
        
        resultControlDiv.appendChild(CommonUtils.clearBoth());
        
        resultDataDiv.appendChild(resultControlDiv);
        resultDiv.appendChild(resultDataDiv);
        
        resultDiv.appendChild(CommonUtils.clearBoth());
        return resultDiv;
        },
        
    createOpenUrl : function (result)
        {
        var cancelOnStreamClose;
        if (result.mediaType == "audio")
            {
            cancelOnStreamClose = false;
            }
        else
            {
            cancelOnStreamClose = true;
            }
        var params = 
            {
            uri: result.sha1, 
            name: result.title,
            urn: result.sha1,
            size: result.size,
            mimeType: result.mimeType,
            cancelOnStreamClose: cancelOnStreamClose,
            noCache: (new Date()).getTime()
            };
        return Constants.DOWNLOAD_URL +
            encodeURIComponent(result.title) +
            "?"+
            dojo.objectToQuery(params);
        }
    });
