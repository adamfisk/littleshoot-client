dojo.provide("littleshoot.Publisher");
//dojo.require("littleshoot.CommonUtils");

PublishData =
    {
    publisher : null
    };

Publisher = {
    
    activated : false, 
    onOnLoad : function ()
        {
        //Publisher.activatePublish();
        //CommonUtils.loadFiles();
        if (window.searchResults)
            {
            window.searchResults.stop();
            }
        //console.info("About to start polling...");
        //document.LittleShootApplet.pollForLittleShoot();
        },
    
    onTags : function(publishFields)
        {
        //console.info("Publish called with tags"); 
        //console.info("Got tags: "+publishFields.tags);
        PublishData.publisher.postPublish(publishFields.tags);
        },
        
    onPublish : function()
        {
        //console.info("Publishing.....");
        var values = dijit.byId("publishForm").attr("value");
        
        // We have to do this because it's not a dijit widget.
        var message = dojo.byId("twitterMessageTextArea").value;
        
        var max = 114;
        if (message.length > max)
            {
            message = message.substring(0, max);
            }
        
        values.twitterMessage = message;
        //console.info("Got values: "+dojo.toJson(values));
        
        PublishData.publisher.postPublish(values);
        },
    
    toPublishPage : function()
        {
        
        if (CommonUtils.onIndexPage())
            {
            window.location.href = "publish";
            }
        else
            {
            CommonUtils.loadFiles();
            }
        },
        
    activatePublish : function() {
        //console.info("Activating publish....");
        // We use a Java applet file upload widget for FireFox 3 and Opera
        // because their HTML file upload widgets don't supply the full path
        // to the file.
        if (CommonUtils.useApplet() && !Publisher.activated) {
            Publisher.activated = true;
            // This call does nothing if the applet's already loaded.
            //CommonUtils.loadLittleShootApplet();
            
            //console.info("Loading the applet...");
            // Load the applet
            $("#fakeFileUpload").hide();
            
            $("#ff3FileUpload").show().click(function (evt) {
                if (!CommonUtils.hasLittleShoot()) {
                    LittleShoot.downloadPrompt();
                }
                else {
                    CommonUtils.showFileDialog();
                }
            });
            
            $("#publishButtonDiv").append($("#ff3FileUpload"));
            $("#publishDiv").show();
        }
        else
            {
            var fakeFileUpload = dojo.byId("fakeFileUpload");
            CommonUtils.hideElement(dojo.byId("loginFileUpload"));
            CommonUtils.showElement(fakeFileUpload);
            dojo.byId("publishButtonDiv").appendChild(fakeFileUpload);
            //console.info("Setting input location...");
             
            var button = dojo.query(".buttonFileInput")[0];
            var input = Publisher.getHiddenInputElement();
            //console.info("Found input: "+input);
        
            // The following isn't perfect, as the mouseover event only occurs 
            // when entering the element, and the hidden input field inevitably
            // spills over the side of the button in some places.    
            dojo.connect(input, "mouseover", null, function(event) 
                {
                if (Publisher.isOver(button, event))
                    {
                    Button.changeButtonOver(button, "url(" + Constants.IMAGES + "button100x22Over.gif)");
                    }
                });
               
            dojo.connect(input, "mouseout", null, function(event)     
                {
                Button.changeButtonOut(button, "url(" + Constants.IMAGES + "button100x22.gif)");
                });
            }
        },
        
    loginToPublish : function ()
        {
        $("#loginFileUpload").show();
        $("#fakeFileUpload").hide();
        dojo.connect(loginFileUpload, "onclick", null, User.showLoginPublishDialog); 
        dojo.byId("publishButtonDiv").appendChild(loginFileUpload);
        },

    isOver : function(element, event)
        {
        //console.info("Checking over...");
        var x = CommonUtils.pointerX(event);
        var y = CommonUtils.pointerY(event);
        var within = CommonUtils.within(element, x, y);
        
        return within;
        },
    
    getHiddenInputElement : function()
        {
        return dojo.byId("invisibleFileInput");
        },
        
    /**
     * Called in response to onchange events in the hidden file input field for
     * publishing files.
     */    
    onFileChanged : function()
        {
        //console.info("Got file changed event");
        /*
        if(dojo.isFF >= 3)
            {
            try 
                {
                netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
                } 
            catch (e) 
                {
                CommonUtils.showMessage("Permissions Error", 'LittleShoot was unable to access local files due to browser security settings. '+
                    'To overcome this, follow these steps: (1) Enter "about:config" in the URL field; (2) '+
                    'Right click and select New->Boolean; (3) Enter "signed.applets.codebase_principal_support"'+
                    '(without the quotes) as a new preference name; (4) Click OK and try loading the file again.');
                return;
                }
            
            var ff3Message =
                "We're sorry, but FireFox 3 is not compatible with LittleShoot publishing.  " +
                "FireFox 3 handles file uploads differently from other browsers.  We should have "+
                "this fixed soon." + 
                "<br><br>" +
                "For more details, see this FireFox "+
                "<a href='https://bugzilla.mozilla.org/show_bug.cgi?id=405630' target='_blank'>bug</a>.";
            
            CommonUtils.showMessage("FireFox 3 Publishing Not Supported", ff3Message);
            return;
            }
            */
        
        var file = Publisher.getHiddenInputElement().value;
        Publisher.onFileSelected(file);
        },
        
    onFileSelected : function (file)
        {
        //console.info("Publishing file: "+file);
        PublishData.publisher = new littleshoot.Publisher(file);
        PublishData.publisher.askForTags();
        }
    };
    
dojo.declare("littleshoot.Publisher", null, {
    constructor : function(file) {
        //console.info("Creating Publisher");
        this.file = file;
        if (CommonUtils.isBlank(file)) {
            CommonUtils.showError("File is blank");
            throw new Error("File is blank!!");
        }
        //console.info("Created publisher with file name: "+this.file);
    },
    
    isFolder : function ()
        {
        return this.folder;
        },
      
    publishLocal : function (file, params, keyId) {
        params.file = file;
        params.keyId = keyId;
        params.permission = Constants.PUBLIC_PERMISSION;
        
        var url = Constants.CLIENT_SECURE_URL + "publishFile"; 

        var siteKey = dojo.cookie("siteKey");
        
        
        if (!siteKey)
            {
            CommonUtils.showMessage("No Key", "We're sorry, but there was an " +
                "error accessing the LittleShoot servers.  Please try again later.");
            throw new Error("No site key in cookie -- not publishing!!");
            }
        
        //console.info("Using site key: "+siteKey);
        var sig = ClientApi.createSignature(url, params, siteKey);
        params.signature = sig;
        
        CommonUtils.showSpinner();
        
        var getParams = { 
            url: url,
            callbackParamName: "callback",
            load: function (response, ioArgs)
                {
                //console.info("Got response: "+response);
                if (!response.success)
                    {
                    CommonUtils.showMessage("Publishing Error", response.message);
                    return;
                    }
                
                var dialog = CommonUtils.createMessage("Successfully Published", 
                    "Successfully published the file: "+response.fileName+".");
                dojo.connect(dialog,"hide", Publisher.toPublishPage);
                dialog.show();
                
                return response;
                },
            error: function (response, ioArgs)
                {
                //console.error("Dojo returned: "+response);
                /*
                CommonUtils.showMessage("LittleShoot Not Found", 
                    "We're sorry, but LittleShoot does not seem to be running on your "+
                    "system.  Are you sure it successfully installed?");
                    */
                return response;
                },
            content: params,
            timeout: 40000
        };
        
        var getWhenLoaded = function () {
            var deferred = dojo.io.script.get(getParams);
            
            var hideSpinner = function() {
                //console.info("Hiding spinner from deferred...");
                CommonUtils.hideSpinner();
            };
            deferred.addBoth(hideSpinner);
            return deferred;
        };
        
        dojoLoader.scriptGet(getWhenLoaded);
    },
        
    postPublish : function (values)
        {
        var id = CommonUtils.keyId();
        var deferred = CommonUtils.requestKey(id);
        var success = dojo.hitch(this, function (data, ioArgs)
            {
            //console.info("Got deferred response with data: "+data);
            this.publishLocal(this.file, values, id);
            });
        
        var error = dojo.hitch(this, function (data, ioArgs)
            {
            console.error("Got deferred response with data: "+data);
            });
        deferred.addCallback(success);
        deferred.addErrback(error);
        },
    
    askForTags : function ()
        {
        //console.info("Asking for tags");
        dijit.byId("publishDialog").show();
        var tweet = dojo.byId("twitterMessageTextArea");
        
        var setCharCount = function(evt)
            {
            //console.info("Now: "+tweet.value.length+" chars...");
            var count = dojo.byId("twitterMessageCharCount");
            if (count)
                {
                var chars = 114 - tweet.value.length;

                if (chars < 0)
                    {
                    count.className = "redCount";
                    }
                else
                    {
                    count.className = "grayCount";
                    }
                count.innerHTML = chars + "*";
                }
            };
        if (tweet)
            {
            
            if (document.twitterCharCountId)
                {
                dojo.disconnect(document.twitterCharCountId);
                }
            document.twitterCharCountId =
                dojo.connect(tweet, "onkeyup", setCharCount);
            setCharCount();
            }
        else
            {
            console.error("Could not find tweet");
            }
        },
    
    getPath : function ()
        {
        //console.info("Getting file path");
        return this.file;
        }
    });

/**
 * Java applet callback.
 * 
 * @param file The path to the file -- this is a java.lang.String converted to
 * a JavaScript object, not a JavaScript string.
 */
function onFileDialogFile(file) {
    //console.info("Got file: "+file);

    // We need to convert the java String to a JavaScript string.  See:
    // http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Guide:LiveConnect_Overview:Data_Type_Conversions:Java_to_JavaScript_Conversions
    var javaScriptFile = file + "";
    Publisher.onFileSelected(javaScriptFile);
}

/**
 * Java file dialog applet callback for when the user has canceled the file
 * selection.
 */
function onFileDialogCancel() {
    //console.info("File dialog canceled");
}
    
