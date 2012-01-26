dojo.provide("littleshoot.CommonUtils");
dojo.require("dojo.number"); 
dojo.require("dijit.form.Button");
dojo.require("dijit.Dialog");
    
/**
 * This class contains easy to use static utility methods.
 */
CommonUtils =
    {
    
    currentPage : '',
    
    commonLoad : function()
        {
        //dojo.parser.parse(dojo.byId('bottomhalf'));
        },

    windowId : function()
        {
        if (!window.uuid)
            {
            window.uuid = Math.floor(Math.random() * 1000000000);
            }
        return window.uuid;
        },
        
    /**
     * Shows a message to the user with an OK button.
     * 
     * @param {Object} titleText The title for the message.
     * @param {Object} bodyText The text for the message.
     */
    showMessage : function (titleText, body)
        {
        var elem;
        if (typeof body === 'string')
            {
            elem = CommonUtils.createTextElement(body);
            }
        else {
            elem = body;
        }
        var messageDialog = CommonUtils.createMessage (titleText, elem);
        messageDialog.show();
        return messageDialog;
        }, 

    /**
     * Creates a text element to show in messages.
     */
    createTextElement : function (text) {
        var messageSpan = document.createElement("span");
        $(messageSpan).addClass("normalMedium").addClass("commonDialogText").html(text);
        return messageSpan;
    },
    

    /**
     * Creates a raw message with no button attached.
     */
    createRawMessage : function (titleText, element) {
        var centered = document.createElement("center");
        var messageDiv = document.createElement("div");
        centered.appendChild(messageDiv);
        dojo.addClass(messageDiv, "commonDialogDiv");
        dojo.addClass(messageDiv, "littleShoot");
        
        messageDiv.appendChild(element);
        
        var messageDialog = new dijit.Dialog({title: titleText});
        messageDialog.startup();
        messageDialog.setContent(centered);
        return messageDialog;
    },
        
    /**
     * Shows a message to the user with an OK button.
     * 
     * @param {Object} titleText The title for the message.
     * @param {Object} bodyText The text for the message.
     */
    createMessage : function (titleText, element) {
        var centered = document.createElement("center");
        var messageDiv = document.createElement("div");
        centered.appendChild(messageDiv);
        dojo.addClass(messageDiv, "commonDialogDiv");
        dojo.addClass(messageDiv, "littleShoot");
        
        messageDiv.appendChild(element);
        messageDiv.appendChild(document.createElement("br"));
        messageDiv.appendChild(document.createElement("br"));
        
        var messageButtonDiv = document.createElement("div");
        messageDiv.appendChild(messageButtonDiv);
        var messageButton = new dijit.form.Button(
            {
            type : "submit",
            label : "OK"
            },
            messageButtonDiv);
        
        var messageDialog = new dijit.Dialog({title: titleText});
        messageDialog.startup();
        messageDialog.setContent(centered);
        return messageDialog;
    }, 
        
    showError : function (detail)
        {
        //console.error("Error: "+detail);
        CommonUtils.showMessage("Error","We seem to be experiencing problems.  "+
            "There was an unknown error described as: "+detail);
        },
        
    showChoicesDialog : function (titleText, bodyText, okCallback, 
        cancelCallback, submitText, noSubmitText) {
        var centered = document.createElement("center");
        var messageDiv = document.createElement("div");
        centered.appendChild(messageDiv);
        dojo.addClass(messageDiv, "commonDialogDiv");
        dojo.addClass(messageDiv, "littleShoot");
        
        var messageSpan = document.createElement("span");
        dojo.addClass(messageSpan, "normalMedium");
        dojo.addClass(messageSpan, "commonDialogText");
        messageSpan.innerHTML = bodyText;
        
        messageDiv.appendChild(messageSpan);
        messageDiv.appendChild(document.createElement("br"));
        messageDiv.appendChild(document.createElement("br"));
        
        var okButtonDiv = document.createElement("div");
        messageDiv.appendChild(okButtonDiv);
        var okButton = new dijit.form.Button(
            {
            type : "submit",
            label : submitText
            },
            okButtonDiv);
            
        var cancelButtonDiv = document.createElement("div");
        messageDiv.appendChild(cancelButtonDiv);
        
        var messageDialog = 
            new dijit.Dialog({title: titleText, execute : okCallback});
        function closeDialog()
            {
            messageDialog.hide();
            if (cancelCallback)
                {
                cancelCallback();
                }
            }
        var cancelButton = new dijit.form.Button(
            {
            label : noSubmitText,
            onClick: closeDialog
            },
            cancelButtonDiv);

        messageDialog.startup();
        messageDialog.setContent(centered);
        messageDialog.show();   
        return messageDialog;
    },
    
    /**
     * Shows a confirmation dialog with "OK" and "Cancel" buttons.  The 
     * caller must specify a callback for the OK button.  If the user selects
     * "Cancel", the dialog simply closes.
     * 
     * @param {Object} titleText The text to put in the title of the dialog.
     * @param {Object} bodyText The text to put in the body of the dialog.
     * @param {Object} okCallback The callback method to call when the OK 
     * button is pressed.
     */
    showConfirmDialog : function (titleText, bodyText, okCallback, cancelCallback) {
        return CommonUtils.showChoicesDialog(titleText, bodyText, okCallback, cancelCallback, "OK", "Cancel");
    }, 
        
    /**
     * Sets the value of a dijit widget.
     */
    setValue : function(elementId, value)
        {
        var widget = dijit.byId(elementId);
        if (!widget)
            {
            //console.error("Could not find element to set value: "+elementId);
            return;
            }
        widget.setValue(value);
        //console.info("Set value!!");
        },
       
    /**
     * Sets the inner HTML of the element with the given ID to the specified
     * message.  The element should be an element that simply displays a
     * message.
     */
    setMessage : function(elementId, msg)
        {   
        $("#"+elementID).html(msg);
        },
        
        
    /**
     * Clears the inner HTML of the element with the given ID.  The element 
     * should be an element that simply displays a message.
     */
    clearMessage : function(elementId)
        {   
        var div = dojo.byId(elementId);
        if (!div)
            {
            //console.error("Could not find element to clear: "+elementId);
            return;
            }
        div.innerHTML = "";
        //console.info("Cleared message!!");     
        },
        
    showPluginRequiredDialog : function()
        {
        var message = "We're sorry, but LittleShoot must be installed to use this "+
            "feature.  You'll be notified as soon as you can join!  Thanks for your patience.";
            
        CommonUtils.showMessage("Sorry!", message);
        },
        
    isValidEmail : function (email, callback) {
        // This is snagged from the jquery validation plugin.
        return (/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i).test(email);
    },
        
    /**
     * Issues an HTTP request for the resource at the specified URL,
     * typically an HTML fragment to embed in the page.
     * 
     * @param {Object} url The URL to retrieve the fragment from.  This
     * can be relative.
     */    
    getHtml : function(url)
        {
        //console.debug("Getting fragment at: "+url);
        return dojo.xhrGet( 
            { 
            url: url, 
            handleAs: "text",
            timeout: 20000, 
            load: function(response, ioArgs) 
                { 
                //console.log("Got the response!");
                return response; 
                },
            error: function(response, ioArgs) 
                { 
                //console.error("HTTP satus code: ", ioArgs.xhr.status); 
                //console.error("Response body: ", response); 
                CommonUtils.showMessage("Error Loading Page",
                    "Sorry, but we seem to be having problems.  " +
                    "We should have this fixed soon.");
                return response; 
                }
            });
        },
    
    isBrowserSupported : function()
        {
        if(/Windows/.test(navigator.userAgent))
            {
            if (/msie/.test(navigator.userAgent.toLowerCase()))
                {
                //console.log("On IE...");
                if (!this.isIe7OrAbove())
                    {
                    //console.log("Incompatible browser: "+navigator.userAgent);
                    return false;
                    }
                }
            }
        
        //console.log("Passed compatibility check...");
        return true;   
        },
        
    isIe7OrAbove : function ()
        {
        //console.log("Checking for IE 7");
        return (dojo.isIE > 6);
        },

        /*
    onSearchPage : function ()
        {
        return CommonUtils.endsWith(window.location.pathname, "search");
        },
        
    onPublishPage : function ()
        {
        return CommonUtils.endsWith(window.location.pathname, "publish");
        },
        */
    setPage : function (page)
        {
        CommonUtils.currentPage = page;
        },
        
    onIndexPage : function ()
        {
        if (!CommonUtils.currentPage)
            {
            return false;
            }
        else if (CommonUtils.currentPage === "index")
            {
            return true;
            }
        return false;
        /*
        if (CommonUtils.isBlank(window.location.pathname))
            {
            return true;
            }
        else if (window.location.pathname == "/")
            {
            return true;
            }
        return CommonUtils.startsWith(window.location.pathname, "/index.html");
        */
        },

    startsWith: function(str, pattern) 
        {
        return str.indexOf(pattern) === 0;
        },
     
    endsWith: function(str, pattern) 
        {
        var d = str.length - pattern.length;
        return d >= 0 && str.lastIndexOf(pattern) === d;
        },
    
    showIncompatibleBrowserDialog : function() 
        {
        dojo.addOnLoad(function() {
            var message = "We're sorry, but LittleShoot does not support "+
                "Internet Explorer 6 at this time.  Please use "+
                "FireFox, Opera, or Internet Explorer 7.  We recommend "+
                "FireFox.  You can download it at http://www.getfirefox.com.";
            CommonUtils.showMessage("Incompatible Browser", message);
            });
        },
        
    queryToObject : function ()
        {
        // We get the substring here to remove the first "?".
        var str = window.location.search.substring(1);
        return dojo.queryToObject(str);
        },

    getDecodedArg : function (arg)
        {
        return decodeURIComponent(CommonUtils.getArg(arg));
        },
        
    getArg : function (arg)
        {
        var args = CommonUtils.queryToObject();
        return args[arg];
        },
        
    hasArg : function (arg)
        {
        return CommonUtils.getArg(arg);
        },
        
    isArgTrue : function (arg)
        {
        return CommonUtils.getArg(arg) == "true";
        },
        
    isLinux : function () {
        if(/Windows/.test(navigator.userAgent)) {
            return false;
        }
        else if (/Mac/.test(navigator.userAgent)) {
            return false;
        }
        else {
            return true;
        }
    },
            
    showAll : function(selector)
        {            
        dojo.query(selector).forEach(function(toggleable) 
            {
            CommonUtils.showElement(toggleable);
            });
        //console.debug("All shown");
        },    
        
    hideAll : function (selector)
        { 
        dojo.query(selector).forEach(function(toggleable) 
            {
            CommonUtils.hideElement(toggleable);
            });          
        },
        
    hideElement: function(element) 
        {
        if (!element)
            {
            console.warn("Element does not exist");
            return null;
            }
        element.style.display = 'none';
        return element;
        },

    showElement: function(element) 
        {
        if (!element)
            {
            console.warn("Element does not exist");
            return null;
            }
        element.style.display = '';
        return element;
        },
        
    pointerX: function(event) 
        {
        return event.pageX || (event.clientX +
            (document.documentElement.scrollLeft || document.body.scrollLeft));
        },

    empty : function()
        {
        },
        
    pointerY: function(event) 
        {
        return event.pageY || (event.clientY +
        (document.documentElement.scrollTop || document.body.scrollTop));
        },

    realOffset: function(element) 
        {
        var valueT = 0, valueL = 0;
        do 
            {
            valueT += element.scrollTop  || 0;
            valueL += element.scrollLeft || 0;
            element = element.parentNode;
            } 
        while (element);
        return [valueL, valueT];
        },

    withinIncludingScrolloffsets: function(element, x, y) 
        {
        var offsetcache = this.realOffset(element);
        this.xcomp = x + offsetcache[0] - this.deltaX;
        this.ycomp = y + offsetcache[1] - this.deltaY;
        this.offset = this.cumulativeOffset(element);

        return (this.ycomp >= this.offset[1] &&
            this.ycomp <  this.offset[1] + element.offsetHeight &&
            this.xcomp >= this.offset[0] &&
            this.xcomp <  this.offset[0] + element.offsetWidth);
        },
  
    cumulativeOffset: function(element) 
        {
        var valueT = 0, valueL = 0;
        do 
            {
            valueT += element.offsetTop  || 0;
            valueL += element.offsetLeft || 0;
            element = element.offsetParent;
            }
        while (element);
        return [valueL, valueT];
        },
 
    within: function(element, x, y) 
        {
        if (this.includeScrollOffsets)
            {
            return this.withinIncludingScrolloffsets(element, x, y);
            }
        this.xcomp = x;
        this.ycomp = y;
        this.offset = this.cumulativeOffset(element);

        return (y >= this.offset[1] &&
            y <  this.offset[1] + element.offsetHeight &&
            x >= this.offset[0] &&
            x <  this.offset[0] + element.offsetWidth);
        },  
          
    isEmpty: function(str) 
        {
        return str === '';
        },

    isBlank: function(str) 
        {
        if (!str)
            {
            return true;
            }
        return (/^\s*$/).test(str);
        },
        
    capitalize: function(str) 
        {
        return str.charAt(0).toUpperCase() + str.substring(1).toLowerCase();
        },
        
    setCookie : function(cookieName, cookieValue)
        {
        var expireTime = new Date();
        
        var oneWeek = (1000 * 60 * 60 * 24 * 7);
        
        var weeks = oneWeek * 10;
        expireTime.setTime(expireTime.getTime() + weeks);
        
        if (CommonUtils.domainIsLittleShoot())
            {
            dojo.cookie(cookieName, cookieValue, {expires: expireTime, domain: ".littleshoot.org"});
            }
        else
            {
            // If we're not on a LittleShoot page, just use the domain we're on.
            dojo.cookie(cookieName, cookieValue, {expires: expireTime});
            }
        
        //console.info("Set cookie!!");
        },
        
    deleteCookie : function (cookieName)
        {
        if (CommonUtils.domainIsLittleShoot())
            {
            dojo.cookie(cookieName, null, {expires: -1, domain: ".littleshoot.org"});
            }
        else
            {
            // If we're not on a LittleShoot page, just use the domain we're on.
            dojo.cookie(cookieName, null, {expires: -1});
            }
        },
    
    domainIsLittleShoot : function ()
        {
        return CommonUtils.endsWith(window.location.hostname, "littleshoot.org");
        },
        
    param : function(param)
        {
        if (CommonUtils.isBlank(param))
            {
            throw new Error("Blank paramater!!");
            }
        },
        
    inGroup : function()
        {
        return CommonUtils.inGroupBase(window.location.hostname);
        },
        
    inGroupBase : function(groupString)
        {
        ////console.info("Checking for group in string: "+groupString);
        var groups = /(.*)\.littleshoot.org/i.exec(groupString);
        ////console.info("Group names: "+groups);
        if (!groups)
            {
            return false;
            }
        if (groups.length < 2)
            {
            return false;
            }
        var group = groups[1];
        if (group == "www")
            {
            return false;
            }
        
        return true;
        },
        
    getGroupName : function()
        {
        ////console.info("Accessing group name");
        return CommonUtils.getGroupNameBase(window.location.hostname);
        },
        
    getGroupNameBase : function(searchTerm)
        {
        //console.info("Accessing group name base..");
        var group = /(.*)\.littleshoot.org/i.exec(searchTerm);
        //console.info("Group name: "+group);
        return group[1];
        },
    
    loadPlayer : function ()
        {
        CommonUtils.loadJs("http://mediaplayer.yahoo.com/js");
        },
    
    loadJs : function (url, id)
        {
        var script = CommonUtils.buildScript(url, id);
        dojo.doc.getElementsByTagName("head")[0].appendChild(script);
        },
        
    buildScript : function (url, id)
        {
        var element = dojo.doc.createElement("script");
        element.type = "text/javascript";
        element.src = url;
        if (id)
            {
            element.id = id;
            }
        return element;
        },
        
        
    showDownloadConfirmDialog : function()
        {
        CommonUtils.showConfirmDialog("Download LittleShoot?", 
            "Would you like to start downloading the LittleShoot P2P Plugin now?",
            function () {CommonUtils.downloadInstaller();});
        },
        
    showLittleShootRequiredConfirmDialog : function() {
        CommonUtils.showConfirmDialog("Download LittleShoot?", 
            "The LittleShoot P2P Plugin is required to use this functionality.  Would you like to start downloading and installing LittleShoot now?",
            function () {CommonUtils.downloadInstaller();});
    },
        
    downloadInstaller : function() {
        //console.info("Downloading installer...");
        
        if (CommonUtils.isLinux()) {
            CommonUtils.showMessage("Linux Not Ready!", "We're sorry, but the Linux version of the latest LittleShoot is not ready at this time. We should have this fixed in the next several months.");
        }
        else {
            CommonUtils.setCookie(Constants.DOWNLOADED_COOKIE_KEY, true);
            var downloadUrl = Common.getDownloadUrl();
            window.location.href = downloadUrl;
        
            if (pageTracker) {
                pageTracker._trackPageview(downloadUrl);
            }
        }
    },
        
    /**
     * Creates an HTTPS url from the current location.  This is useful if we
     * are testing locally, for example.
     */
    createHttpsUrl : function (apiCall)
        {
        return "https://" + window.location.host + 
            "/lastbamboo-server-site/api/"+apiCall; 
        },
        
    showSpinner : function (elementId)
        {
        if (!elementId)
            {
            elementId = "body";
            }
        
        var existing = dojo.byId(elementId+"Loading");
        if (existing)
            {
            ////console.info("Element already exists");
            existing = null;
            return;
            }
        
        //console.info(elementId);
        var div = document.createElement("div");
        div.id = elementId+"Loading";
        div.className="loading";
        var img = document.createElement("img");
        img.setAttribute("src", Constants.IMAGES+"loading.gif");
        div.appendChild(img);
        var elem = dojo.byId(elementId);
        if (!elem)
            {
            console.error("Element does not exist: "+elementId);
            }
        else
            {
            elem.appendChild(div);
            }
        div = null;
        elem = null;
        img = null;
        },

    showHighSpinner : function ()
        {
        //console.info("Showing high spinner");
        var elementId = "body";
        var div = document.createElement("div");
        div.id = elementId+"Loading";
        div.className="loadingOffsetHigh";
        var img = document.createElement("img");
        img.setAttribute("src", Constants.IMAGES+"loading.gif");
        
        div.appendChild(img);
        var elem = dojo.byId(elementId);
        if (!elem)
            {
            //console.error("Element does not exist: "+elementId);
            }
        else
            {
            elem.appendChild(div);
            }
        div = null;
        elem = null;
        img = null;
        },
        
    hideSpinner : function (elementId)
        {
        if (!elementId)
            {
            elementId = "body";
            }
        var spinner = dojo.byId(elementId+"Loading");
        if (!spinner)
            {
            //console.info("Spinner does not exist for: "+elementId);
            }
        else
            {
            spinner.parentNode.removeChild(spinner);
            }
        spinner = null;
        },
        
    addGroupLogoNoFade : function()
        {
        if (!CommonUtils.inGroup())
            {
            //console.info("Not in group.");
            return;
            }
        //console.info("In group!!!");
        var groupName = CommonUtils.getGroupName();
        
        var imgUrl = Constants.IMAGES + groupName+".jpg";
        //console.info("Setting image to: "+imgUrl);
        var img = document.createElement("img");
        img.setAttribute("src", imgUrl);
        
        var customLogoDiv = dojo.byId("customLogoDiv");
        if (!customLogoDiv)
            {
            //console.info("Could not fine custom div");
            return;
            }
        customLogoDiv.appendChild(img);
        },
        
    addGroupLogo : function()
        {
        if (!CommonUtils.inGroup())
            {
            //console.info("Not in group.");
            return;
            }
        //console.info("In group!!!");
        var groupName = CommonUtils.getGroupName();
        
        var imgUrl = Constants.IMAGES+groupName+".jpg";
        //console.info("Setting image to: "+imgUrl);
        var img = document.createElement("img");
        img.setAttribute("src", imgUrl);
        
        var customLogoDiv = dojo.byId("customLogoDiv");
        if (!customLogoDiv)
            {
            //console.info("Could not fine custom div");
            return;
            }
        customLogoDiv.appendChild(img);
        var fo = dojo.fadeIn(
            {
            node: customLogoDiv,
            duration: 6000
            });
        fo.play();
        },
    
    isLoggedIn : function ()
        {
        return User.isLoggedIn();
        },
        
    loadFiles : function()
        {
        //console.info("Loading files...");
        var resourceCreator = new littleshoot.FileResource();
        var resourceLoader = new littleshoot.ResourceLoader("files", resourceCreator, true);
        
        resourceLoader.loadResultsPage(0);
        },
        
    bytesToMb : function (bytes) {
        if(!bytes) {
            return 0;
        }
        var num = bytes/1048576;
        //return dojo.number.format(num, {pattern: "#,###,###,###.##"});
        return dojo.number.format(num, {places: 2});
    },
        
    secondsToMinutes : function (seconds) {
        // toFixed with no args removes any decimels.
        return Math.floor(seconds / 60) + ":" + (seconds % 60).toFixed();
    },
        
    extractUriPath : function (str) {
        var re = /.*\/(.*)/;
        
        var array = re.exec(str);
        return array[1];
    },
    
    /**
     * Finds the parent node with the specified class name, up to 5 levels.
     * Returns null if no match can be found.
     */
    findParent : function (node, nameOfClass)
        {
        var parent = node;
        for (i = 0; i < 5; i++)
            {
            parent = parent.parentNode;
            if (dojo.hasClass(parent, nameOfClass))
                {
                return parent;
                }
            }
        return null;
        },
        
    keyId : function ()
        {
        return Math.floor(Math.random() * 1000000000);
        },
        
    requestKey : function (id)
        {
        var params = 
            {
            keyId : id
            };
        
        CommonUtils.showSpinner();
        var deferred = dojo.xhrPost(
            {
            url: Constants.SERVER_URL + "key/",
            content : params,
            handleAs: "text",
            
            load: function (response,args)
                {
                //console.info("Got response data: "+response);
                return response;
                },
            
            error : function (response, args)
                {
                console.dir(args);
                return response;
                },
            timeout: 20000
            });
        
        deferred.addBoth(function () {CommonUtils.hideSpinner();});
        return deferred;
        },
    
    clearChildren : function (node)
        {
        var kids = node.childNodes;
        for (i = kids.length-1; i >= 0; i--)
            {
            node.removeChild(kids[i]);
            }
        },
        
    clearBoth : function ()
        {
        var div = document.createElement("div");
        div.className = "clearBoth";
        return div;
        },
        
    fileDialogApplet : null,
        
    APPLET_NAME : "LittleShootApplet",
    
    appletLoadCalled : false,
    
    showFileDialog : function ()
        {
        if (CommonUtils.fileDialogApplet === null)
            {
            CommonUtils.showSpinner();
            CommonUtils.loadLittleShootApplet();
            
            // Give it a second to load.
            setTimeout(function () {
                try {
                    CommonUtils.fileDialogApplet = $("#LittleShootAppletId")[0];
                    CommonUtils.fileDialogApplet.newFileDialog();
                } catch (error) {
                    // This will make it load again next time.
                    CommonUtils.fileDialogApplet = null;
                    CommonUtils.showMessage("File Dialog Error", 
                      "We're sorry, but there was an error loading the LittleShoot publishing dialog. "+
                      "It should be work in a second if you try again. If you keep getting errors, "+
                      "please e-mail us at bugs@littleshoot.org with your browser details. The reported error "+
                      "is "+error+". Thanks!");
                }
                CommonUtils.hideSpinner();
            }, 2000);
            }
        else
            {
            CommonUtils.fileDialogApplet.newFileDialog();
            }
        },

    useApplet : function ()
        {
        return true;
        //return (dojo.isIE > 6 || dojo.isFF >= 3 || dojo.isOpera > 0);
        },
        
    loadLittleShootApplet : function ()
        {
        if (CommonUtils.useApplet() && !CommonUtils.appletLoadCalled)
            {
            CommonUtils.appletLoadCalled = true;
            CommonUtils.loadApplet(CommonUtils.APPLET_NAME);
            }
        },
        
    loadApplet : function (appletName)
        {
        
        //console.info("Creating applet in JavaScript!!!");
        var applet = CommonUtils.newAppletElement(appletName);
        document.getElementsByTagName("body")[0].appendChild(applet);
        //console.info("Appended applet to body");
        },
        
    newAppletElement : function (appletName)
        {
        var applet = document.createElement("applet");
        applet.setAttribute("name", appletName);
        applet.id = appletName + "Id";
        applet.setAttribute("code", appletName + ".class");
        applet.setAttribute("mayscript", true);
        applet.setAttribute("width", 1);
        applet.setAttribute("height", 1);
        
        // Offscreen.
        var style = 
            {
            position : "absolute",
            top: "-300px"
            };
        dojo.style(applet, style);

        var appletDiv = document.createElement("div");
        appletDiv.appendChild(applet);
        return appletDiv;
        },
        
    setDefaultImgProperties : function (img, mediaType)
        {
        switch (mediaType)
            {
            case "audio":
                img.src = Constants.AUDIO_IMAGE;
                break;
            case "video":
                img.src = Constants.VIDEOS_IMAGE;
                break;
            case "image":
                img.src = Constants.IMAGES_IMAGE;
                break;
            case "document":
                img.src = Constants.DOCS_IMAGE;
                break;
            case "application/mac":
                img.src = Constants.APPS_IMAGE;
                break;
            case "application/linux":
                img.src = Constants.APPS_IMAGE;
                break;
            case "application/win":
                img.src = Constants.APPS_IMAGE;
                break;
            default:
                img.src = Constants.DOCS_IMAGE;
            }
        img.width = 75;
        img.height = 75;
        },

    littleShootNpapiCallback : function (found, version)
        {
        if (!littleShootConfig)
            {
            console.info("LittleShoot config not defined");
            return;
            }
        
        // We just consider it a "cookie" callback for now.
        littleShootConfig.cookieCallbackComplete = true;
        CommonUtils.makeCallbacks(found, version);
        },
        
    littleShootCookieCallback : function (found, version)
        {
        if (!littleShootConfig)
            {
            console.info("LittleShoot config not defined");
            return;
            }
        littleShootConfig.cookieCallbackComplete = true;
        CommonUtils.makeCallbacks(found, version);
        },
        
    littleShootLocalCallback : function (found, version)
        {
        if (!littleShootConfig)
            {
            console.info("LittleShoot config not defined");
            return;
            }
        littleShootConfig.localCallbackComplete = true;
        
        if (littleShootConfig.cookieCallbackComplete)
            {
            console.info("Cookie already made callback");
            return;
            }
        if (littleShootConfig.remoteCallbackComplete)
            {
            console.info("Remote callback already complete!!");
            return;
            }
        CommonUtils.makeCallbacks(found, version);
        },
        
    littleShootRemoteCallback : function (found, version)
        {
        if (!littleShootConfig)
            {
            console.info("LittleShoot config not defined");
            return;
            }
        if (littleShootConfig.cookieCallbackComplete)
            {
            console.info("Cookie already made callback");
            return;
            }
        if (littleShootConfig.localCallbackComplete)
            {
            console.info("Local callback already complete!!");
            return;
            }
        littleShootConfig.remoteCallbackComplete = true;
        CommonUtils.makeCallbacks(found, version);
        },
        
    makeCallbacks : function (found, version)
        {
        if (!found)
            {
            if (littleShootConfig.littleShootNotPresent)
                {
                littleShootConfig.littleShootNotPresent();
                }
            }
        else 
            {
            if (littleShootConfig.littleShootPresent)
                {
                littleShootConfig.littleShootPresent(version);
                }
            }
        },
    
    showUpdateDialog : function ()
        {
        //console.info("Newer version available...");
        CommonUtils.showConfirmDialog("New Version", 
            Constants.VERSION_MESSAGE, 
            CommonUtils.downloadInstaller);
        },
        
    pollForLittleShoot : function()
        {
        //console.info("About to load movie");
        var movie = dojo.byId("littleShootFlashContent");
        //console.info("Got movie: "+movie);
        movie.poll();
        },
        
    loadFlash : function()
        {
        if (!swfobject)
            {
            console.error("No swfobject");
            throw new Error("No swfobject!!");
            }
        //console.info("Loading Flash...");
        var flashvars = {};
        var params = {};
        params.play = "true";
        params.loop = "false";
        params.quality = "high";
        params.allowscriptaccess = "always";
        var attributes = {};
        attributes.id = "littleShootFlashContent";
        attributes.name = "LittleShoot";
        attributes.align = "middle";
        swfobject.embedSWF("LittleShoot.swf", "littleShootNoFlash", "0%", "0%", "9.0.0", "expressInstall.swf", flashvars, params, attributes);
        //console.info("Embedded swf...");
        },
        
    hasLittleShoot : function() {
        return window.gotLittleShoot;
    },
    
    twoPlaces : function (numero) {
        return dojo.number.format(numero, {pattern: "###,###,###.##"});
    }
    
    };