var BitlyUtils = {    
    
    BITLY_VERSION: "2.0.1",
    
    shorten : function (longUrl, successCallback) {
        var params = {};
        params.format = "json";
        params.version = BitlyUtils.BITLY_VERSION;
        params.longUrl = longUrl;
        params.apiKey = "R_60cdce3fae3d41d3337607b13dc343a5";
        params.login = "littleshoot";
        
        $.getJSON("http://api.bit.ly/shorten?callback=?", 
            params,
            function (data) {
                try {
                    var shortUrl = data.results[longUrl].shortUrl;
                    //console.info("Got short URL: "+shortUrl);
                    successCallback(shortUrl);
                } catch (err) {
                    console.error("Error making Bitly callback: "+err);
                }
            }
        );
    },
    
    stats : function (shortUrl, callback) {
        var params = {};
        params.format = "json";
        params.version = BitlyUtils.BITLY_VERSION;
        params.shortUrl = shortUrl;
        params.apiKey = "R_60cdce3fae3d41d3337607b13dc343a5";
        params.login = "littleshoot";
        
        $.getJSON("http://api.bit.ly/stats?callback=?", 
            params,
            function (data) {
                
                try {
                    callback(data);
                } catch (err) {
                    console.error("Error making Bitly callback: "+err);
                }
            }
        );
    }
};