dojo.provide("littleshoot.PendingFiles");
dojo.require("littleshoot.PendingFile");

dojo.declare("littleshoot.PendingFiles", null, 
    {
    
    constructor : function(cancelled, reload)
        {
        if (!cancelled)
            {
            this.cancelled = false;
            }
        else
            {
            this.cancelled = cancelled;
            }
        this.reload = reload;
        },

    processPendingFiles : function (json, containerId, resourceLoader)
        {
        if (this.cancelled)
            {
            console.info("Cancelled!!");
            return;
            }
        
        var container = dojo.byId(containerId);
        var files = json.files;
        
        console.info("Got files: "+dojo.toJson(files));
        //console.dir(json);
        //console.info("length: "+files.length);
        for (var i = 0; i < json.size; i++)
            {
            console.info("Looping..."+i);
            var file = files[i];
            var existing = dojo.byId(file.path);
            
            if (existing)
                {
                console.info("Updating file: "+files[i].title);
                var percentComplete = 
                    100 * ((file.size - (file.size - file.bytesHashed))/file.size);
                
                var pbId = file.path + "ProgressBar";
                
                var pb = dijit.byId(pbId);
                pb.update({ progress: percentComplete});
                
                var statusId = file.path + "Status";
                var oldStatusDiv = dojo.byId(statusId);
                var newStatus = document.createTextNode("Status: "+file.status);
                var newStatusDiv = document.createElement("div");
                newStatusDiv.id = statusId;
                newStatusDiv.className = oldStatusDiv.className;
                newStatusDiv.appendChild(newStatus);
                oldStatusDiv.parentNode.replaceChild(newStatusDiv, oldStatusDiv);
                }
            else
                {
                console.info("Building file: "+files[i].title);
                var pf = new littleshoot.PendingFile(files[i]);
                var fileDiv = pf.getDiv();
                console.info("Adding file to div: "+container.id);
                container.appendChild(fileDiv);
                }
            }
        
        // Keep loading if there are still pending files.
        if (json.size > 0 && !this.cancelled)
            {
            var loadAgain = dojo.hitch(this, function()
                {
                console.info("Loading again");
                var pending = 
                    new littleshoot.PendingFiles(this.cancelled, true);
                pending.loadLocalPendingResources(containerId, resourceLoader);
                });
            setTimeout(loadAgain, 1000);
            }
        },
        
    cancel : function()
        {
        this.cancelled = true;
        },
        
    loadLocalPendingResources : function(containerId, resourceLoader)
        {
        console.info("Loading local pending resources.  Loader: "+resourceLoader);
        if (this.cancelled)
            {
            console.info("Loading cancelled");
            CommonUtils.hideElement(dojo.byId(containerId));
            return;
            }
        var processPendingFiles = this.processPendingFiles;
            
        var deferred = dojo.io.script.get(
            { 
            url: Constants.CLIENT_URL + "pendingFiles",
            callbackParamName: "callback",
            load: function (response, ioArgs)
                {
                console.info("Got response: "+dojo.toJson(response));
                return response;
                },
            error: function (response, ioArgs)
                {
                return response;
                },
            timeout: 6000
            });
        var success = dojo.hitch(this, function (response)
            {
            console.info("Got deferred response: ", response);
            if (response.size > 0)
                {
                console.info("Processing response...");
                CommonUtils.showElement(dojo.byId(containerId));
                processPendingFiles(response, containerId, resourceLoader);
                }
            else
                {
                console.info("No pending files...clearing...");
                CommonUtils.hideElement(dojo.byId(containerId));
                var div = dojo.byId(containerId);
                CommonUtils.clearChildren(div);

                // We only want to reload if we've actually been updating 
                // locally-published files.
                if (this.reload)
                    {
                    // We need to hitch here because the resource loader relied 
                    // on its internal state (aka 'this').
                    var list = dojo.hitch(this, function()
                        {
                        console.info("Loading again");
                        resourceLoader.listResources();
                        });
                    // We give LittleShoot a second to publish the file before
                    // reloading the resources.
                    setTimeout(list, 600);
                    }
                }
            return response;
            });
        
        var error = dojo.hitch(this, function (err)
            {
            console.info("Got error response...LittleShoot not running?", err);
            //CommonUtils.showMessage("LittleShoot Not Found", 
            //    "We're sorry, but LittleShoot does not seem to be running on your "+
            //    "system.  Are you sure it successfully installed?");
            CommonUtils.hideElement(dojo.byId(containerId));
            return err;
            });
        deferred.addCallback(success);
        deferred.addErrback(error);
        return deferred;
        }
    });