
var LittleShootUtils = {
    hasTorrentDownloadRpc : function () {
        if (!window.appData) {
            var cookieData = dojo.cookie(Constants.CLIENT_COOKIE_KEY);
            if (cookieData) {
                var data = dojo.fromJson(cookieData);
                // We support RPC if we're running from the main line.
                return data.appVersion >= 0.95 || data.appVersion === 0;
            } else {
                // By default we just hope it's a newer, non-beta version.
                return true;
            }
        }
        
        // We don't check for null here because 0 is the same as null.
        if (window.appData.appVersion === undefined) {
            console.warn("Got app data with no version. Printing version.");
            console.dir(appData);
            return true;
        }
        // We support RPC if we're running from the main line.
        return window.appData.appVersion >= 0.95 || window.appData.appVersion === 0;
    }
};
