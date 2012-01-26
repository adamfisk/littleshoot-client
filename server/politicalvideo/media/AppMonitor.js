
var AppMonitor = function () {

    var pub = {};
    
    /**
     * Called from the server when it has received the app check request.
     */
    function onAppChecked (json) {
        if (!json) {
            console.error("JSON is not defined");
            return;
        }
        //console.info("Got json: "+dojo.toJson(json));
        if (json.appPresent) {
            //console.info("LittleShoot found...");
            CommonUtils.setCookie(Constants.CLIENT_COOKIE_KEY, dojo.toJson(json));
            
            // The application is running.  Simply continue.
            if (json.appVersion < Constants.CURRENT_VERSION &&
                json.appVersion != Constants.MAIN_LINE_VERSION) {
                CommonUtils.showUpdateDialog();
            }
            CommonUtils.littleShootLocalCallback(true, json.appVersion);
            
            if (dojo.isFunction(onLittleShootFromJavaScript)) {
                onLittleShootFromJavaScript(json);
            }
            else {
                console.warning("No onLittleShootFromJavaScript func");
            }
        }
        else {
            //console.info("LittleShoot not found...");
            //CommonUtils.setCookie(Constants.CLIENT_COOKIE_KEY, "");
            
            // Delete the cookies!!  The local check always overrides anything
            // else.
            CommonUtils.deleteCookie(Constants.CLIENT_COOKIE_KEY);
            // This one won't always work depending on our domain, as it's 
            // domain is .littleshoot if it's there at all.
            CommonUtils.deleteCookie(Constants.SET_FROM_CLIENT_COOKIE_KEY);
            
            // Notify any relevant parties LittleShoot was not found...
            CommonUtils.littleShootLocalCallback(false);
        }
    } 
    
    /**
     * This method checks for the existence of LittleShoot.  There are 
     * several possible scenarios for LittleShoot's presence, described below:
     * 
     * 1) There is a cookie for the LittleShoot client.  This can mean:
     *     a) LittleShoot is installed and running
     *     b) LittleShoot was installed but has since been uninstalled.
     * 
     * 2) If there's not a cookie, LittleShoot could be:
     *     a) Not installed
     *     b) Installed, but either hasn't been accessed by this browser before
     *     or the user has cleared cookies.
     *     
     * So, if there's not a cookie, we unfortunately can't assume LittleShoot is 
     * not there.
     * 
     * If there is a cookie, it's also possible LittleShoot has since been
     * uninstalled.  To limit this false positive potential, we just erase the
     * cookie if our call to the LittleShoot client fails.
     */
    pub.checkApp  = function() {
        //console.info("Checking app.");
        var loadHandler = function(data, ioArgs) {
            //console.info("AppMonitor call loaded.");
            onAppChecked(data);
            return data;
        };
            
        var errorHandler = function(data, ioArgs) {
            //console.info("AppMonitor error on call: "+data);
            onAppChecked( {
                appPresent: false, 
                appVersion: Constants.CURRENT_VERSION
            });
            return data;
        };
        
        var params = {};
        params.t = (new Date()).getTime();
        
            
        var deferred = dojo.io.script.get({ 
            url: Constants.CLIENT_URL + "appCheck", 
            callbackParamName: "callback",
            load: loadHandler,
            error: errorHandler,
            content: params,
            timeout: 3000,
            preventCache: true,
            noCache: (new Date()).getTime()
        });
        
        return deferred;
    };

    pub.monitor = function() {
        //console.debug("Starting monitor");
        var scheduleCheck = function() {
            instance.timeoutId = setTimeout(function() {
                var deferred = instance.checkApp();
                deferred.addBoth(scheduleCheck);    
            }, 2000);   
        };
            
        // This will continue to issue checks indefinitely.
        scheduleCheck();
    };
    
    return pub;
}();