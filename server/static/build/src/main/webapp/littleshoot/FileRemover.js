dojo.provide("littleshoot.FileRemover");

dojo.declare("littleshoot.FileRemover", null, 
    {
    constructor : function(urn, name)
        {
        this.urn = urn; 
        this.name = name;
        },
    
    removeLocal : function (keyId)
        {
        var params =
            {
            sha1 : this.urn,
            uri : this.urn,
            name : this.name,
            keyId : keyId
            };
        
        if (CommonUtils.inGroup())
            {
            params.groupName = CommonUtils.getGroupName();
            }
        var url = Constants.CLIENT_SECURE_URL + "removeFile";             
        var siteKey = dojo.cookie("siteKey");
        if (!siteKey)
            {
            User.showMessage("Error!", 
                "You don't appear to have credentials to remove files.");
            throw new Error("No site key!");
            }
        var sig = ClientApi.createSignature(url, params, siteKey);
        params.signature = sig;
        
        var removeParams =
            {
            url: url,
            timeout: 20000,
            content: params,
            handleAs: "json",
            callbackParamName: "callback",
            load: function(response, ioArgs)
                {
                //console.log("Final publish got the response: " + dojo.toJson(response));
                if (response.success)
                    {
                    var dialog = CommonUtils.createMessage("Successfully Removed", 
                        "Successfully removed the file: \""+response.fileName+"\".");
                    dojo.connect(dialog,"hide", CommonUtils.loadFiles);
                    dialog.show();
                    }
                else
                    {
                    CommonUtils.showMessage("Error Removing", 
                        response.message);
                    }
                return response;
                },
            error: function(response, ioArgs)
                {
                CommonUtils.showError(dojo.toJson(response));
                return response;
                }
            };
        
        var deferred = CommonUtils.get(removeParams);
        },
        
    removeFile : function ()
        {
        var keyId = Math.floor(Math.random() * 1000000000);
        var success = dojo.hitch(this, function (response)
            {
            console.info("Got deferred response: "+response);
            this.removeLocal(keyId);
            });
        
        var error = dojo.hitch(this, function (response)
            {
            console.error("Got error response: "+response);
            });
        
        //console.info("Removing file");
        var ok = dojo.hitch(this, function(data) 
            {
            var deferred = CommonUtils.requestKey(keyId);
            deferred.addCallback(success);
            deferred.addErrback(error);
            });
        
        CommonUtils.showConfirmDialog("Remove File?", 
            "Are you sure you want to remove \""+this.name+"\"?", ok);
        }
    });
