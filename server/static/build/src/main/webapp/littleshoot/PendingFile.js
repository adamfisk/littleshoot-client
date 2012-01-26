dojo.provide("littleshoot.PendingFile");
dojo.require("dijit.ProgressBar");

dojo.declare("littleshoot.PendingFile", null, 
    {
    
    constructor : function(json)
        {
        
        var div = document.createElement("div");
        div.className = "searchResult";
        div.id = json.path;
        
        var dataDiv = document.createElement("div");
        dataDiv.className = "publishResultData";
        div.appendChild(dataDiv);
        
        var titleDiv = document.createElement("div");
        titleDiv.className = "publishResultTitle normalBold";
        titleDiv.appendChild(document.createTextNode(json.title));
        dataDiv.appendChild(titleDiv);
        
        
        var tagsDiv = document.createElement("div");
        tagsDiv.className = "publishResultTags normal";
        tagsDiv.appendChild(document.createTextNode("Tags: "+json.tags));
        dataDiv.appendChild(tagsDiv);
        
        var statusDiv = document.createElement("div");
        statusDiv.className = "pendingFileStatusDiv normal";
        var statusId = json.path + "Status";
        statusDiv.id = statusId;
        var statusText = document.createTextNode("Status: "+json.status);
        
        statusDiv.appendChild(statusText);
        dataDiv.appendChild(statusDiv);
        
                
        var img = document.createElement("img");
        img.className = "publishResultThumbnail";
        CommonUtils.setDefaultImgProperties(img, json.mediaType);
        
        div.appendChild(img);
        
        
        var downloadProgressDiv = document.createElement("div");
        downloadProgressDiv.className = "pendingProgress normal";
    
        var pbId = json.path + "ProgressBar";
        var percentComplete = 
            100 * ((json.size - (json.size - json.bytesHashed))/json.size);
        
        // We might have previously published and removed this file, in which
        // case the progress bar could still be kicking around.  We re-use it
        // if it is.
        var pb = dijit.byId(pbId);
        if (pb)
            {
            pb.update({ progress: percentComplete});
            }
        else
            {
            pb = new dijit.ProgressBar(
                { 
                progress: percentComplete, 
                maximum: 100, 
                id: pbId
                });
            }
        
        downloadProgressDiv.appendChild(pb.domNode);
        dataDiv.appendChild(downloadProgressDiv);
        
        div.appendChild(dataDiv);
        
        div.appendChild(CommonUtils.clearBoth());
        this.div = div;
        },
        
    getDiv : function()
        {
        return this.div;
        }

    });