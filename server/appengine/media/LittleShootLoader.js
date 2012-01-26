
LittleShoot = {
            
    downloadPrompt : function () {
        CommonUtils.showConfirmDialog("LittleShoot Required", 
            "You need the LittleShoot plugin to use this site. " +
            "Would you like to install it now?", 
            CommonUtils.downloadInstaller);
    },
        
    p2pUrl : function (url, name) {
        if (!name) {
            name = CommonUtils.extractUriPath(url);
        }
        
        var p2pUrl = Constants.DOWNLOAD_URL + name + "?uri=";
        p2pUrl += encodeURIComponent(url);
        
        //console.info("P2P URL: "+p2pUrl);
        return p2pUrl;
    },
        
    /**
     * This uses various methods to detect if LittleShoot is installed, 
     * including potentially hitting our server to see if cookies indicate
     * LittleShoot is present.  The site itself may also have a cookie set
     * for LittleShoot.  
     * 
     * Variable browser cookie handling complicates this.  In particular, IE
     * does not allow the LittleShoot client to set a global cookie for the
     * .littleshoot.org domain, so a simple check on the server for cookies
     * the client has set does not work on IE. This forces us to set the 
     * cookie in JavaScript as well, but we're then limited if LittleShoot is
     * integrated on a third-party site.  We can set a cookie for that site and
     * look for it.
     */
    hasLittleShoot : function() {
        var getWhenLoaded = function() {
            //console.info("Running check for LittleShoot.");
            
            if (LittleShoot.hasPlugin()) {
                //console.info("Found LittleShoot plugin");
                // NOTE: the Flash checker always runs as well, and it will always
                // set the cookies appropriately.
                return;
            }
    
            console.warn("Did not find LittleShoot plugin!!");
            // Versions before the npapi plugin didn't have IsoHunt.
            //var cb = dijit.byId("isoHuntCheckBox");
            //cb.setAttribute("disabled", true);
            
            // This checks cookies, does not block and makes its own callback.
            var cookieSet = LittleShoot.checkLittleShootLocal();
    
            if (cookieSet) {
                //console.info("Just using local cookie data");
                return;
            }
            
            var loadHandler = function(data, ioArgs)
                {
                //console.info("Got data from server: "+dojo.toJson(data));
                
                if (data.appPresent) {
                    //console.info("Making callback...");
                    CommonUtils.littleShootRemoteCallback(true, data.appVersion);
                }
                else {
                    CommonUtils.littleShootRemoteCallback(false);
                }
                return data;
                };
                
            var errorHandler = function(data, ioArgs)
                {
                //console.info("LittleShootLoader got error");
                //console.info("Error on call: "+data);
                CommonUtils.littleShootRemoteCallback(false);
                return data;
                };
            
            var params = {};//CommonUtils.queryToObject();
            params.t = (new Date()).getTime();
            
            // We use the full URL here because platform code needs to be able to
            // call it from any site.
            var deferred = dojo.io.script.get(
                { 
                url: Constants.FULL_API_URL + "littleShootData", 
                callbackParamName: "callback",
                load: loadHandler,
                error: errorHandler,
                content: params,
                timeout: 30000, 
                noCache: (new Date()).getTime()
                });
            
            //return deferred;
        };
        dojoLoader.scriptGet(getWhenLoaded);
    },
    
    loadNpapi : function() {
        var mimetype = navigator.mimeTypes["application/x-littleshoot"];
        if (mimetype) {
            var plugin = mimetype.enabledPlugin;
            if (plugin) {
                if (!$.browser.msie) {
                    //console.info("Appending plugin!!");
                    $("#littleShootEmbed").html('<embed type="application/x-littleshoot" pluginspage="http://www.littleshoot.org"></embed>');
                    CommonUtils.littleShootNpapiCallback(true, 0.90);
                    LittleShoot.fadeLoading();
                    return true;
                } else {
                    return false;
                }
            } 
            else {
                //console.warn("Found MIME type but not plugin for application/x-littleshoot");
                return false;
            }
        }
        else {
            return false;
        }
    },
    
    hasPlugin : function() {
        
        if ($.browser.msie) {
            try { 
                var ax = new ActiveXObject('Mozilla.PluginHostCtrl.1');
                $("#littleShootEmbed").html(
                    '<OBJECT ID="littleshoot" CLASSID="CLSID:0CC00AEB-7E95-4a80-8C29-ED90939FC99F" CODEBASE="axlittleshoot.cab#version=0,9,0,0" width="1" height="1"><PARAM name="type" value="application/x-bittorrent"/></OBJECT>');
                CommonUtils.littleShootNpapiCallback(true, 0.90);
                LittleShoot.fadeLoading();
                return true;
            } catch (e) {
                //console.info("LittleShoot not installed.");
                return false;
            }
        } 
        else if (navigator.plugins && navigator.plugins.length) {
            if (!LittleShoot.loadNpapi()) {
                // 'false' here is for reload -- whether or not to reload
                // existing embed tags on the page.
                navigator.plugins.refresh(false);
                return LittleShoot.loadNpapi();
            }
            else {
                return true;
            }
        }
        else {
            // No, so tell them so
            //console.info("No plugin for application/x-littleshoot");
            return false;
        }
    },
        
    /**
     * Runs a local check for LittleShoot.  This only works if we're on a page
     * in the LittleShoot domain.
     */
    checkLittleShootLocal : function() {
        // Check the local cookie.
        var cookieData = dojo.cookie(Constants.CLIENT_COOKIE_KEY);
        var cookieResult = false;
        if (cookieData)
            {
            var data = dojo.fromJson(cookieData);
            var littleShootPresent = data.appPresent;
            if (littleShootPresent)
                {
                //console.info("Found local cookie!!");
                CommonUtils.littleShootCookieCallback(true, data.appVersion);
                
                LittleShoot.fadeLoading();
                cookieResult = true;
                }
            }
    
        // We always also run the manual check because this will reset cookies
        // if the user has uninstalled the app, for example.  This really does
        // 2 things:
        //
        // 1) If the app is installed, usually beats the server check to 
        // correctly set the state, avoiding false negatives from the server.
        // 
        // 2) If the app is not installed, this can avoid false positives in
        // the future in the case where the user has uninstalled LittleShoot.
        // This will set cookies to their correct values.
        //console.info("Running manual local check...");
        //var monitor = new littleshoot.AppMonitor();
        var deferred = AppMonitor.checkApp();
        
        if (deferred !== undefined) {
            deferred.addBoth(function() {
                LittleShoot.fadeLoading();
            });
        }
        
        return cookieResult;
        },
        
    fadeLoading : function()
        {
        if (!littleShootConfig)
            {
            logging.info("No littleshoot config");
            return;
            }
        if (littleShootConfig.littleShootLoading)
            {
            var loading = dojo.byId(littleShootConfig.littleShootLoading);
            if (loading)
                {
                var fo = dojo.fadeOut(
                    {
                    node: loading
                    });
                fo.play();
                if (loading.parentNode)
                    {
                    loading.parentNode.removeChild(loading);
                    }
                }
            }
        }
    };

/**
 * Auto-loading can be manually disabled, typically when we're performing
 * the check elsewhere.
 */

jQuery().ready(function() {
    dojo.require("dojo.cookie"); 
    //dojo.require("dojo.io.script"); 
    dojo.addOnLoad(function() {
        //console.info("Configuring LittleShoot check...");
        
        if (!littleShootConfig) {
            //console.info("No LittleShoot callbacks configured");
            littleShootConfig = {};
        }
        if (littleShootConfig.disableAutoLoad) {
            // The config can turn off auto-loading.
            //console.info("Not auto-loading...");
            return;
        }
        //console.info("Checking for LittleShoot");
        LittleShoot.hasLittleShoot();
    });
});
