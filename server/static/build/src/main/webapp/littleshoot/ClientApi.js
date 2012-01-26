dojo.provide("littleshoot.ClientApi");

/*jslint evil: true */

Date.prototype.toISODate =
new Function("with (this)\n    return " +
   "getFullYear()+'-'+addZero(getMonth()+1)+'-'" +
   "+addZero(getDate())+'T'+addZero(getHours())+':'" +
   "+addZero(getMinutes())+':'+addZero(getSeconds())+'.000Z'");

var ClientApi =
    {
    /////////////////// Auth /////////////////////////////////////////////////////


    addZero : function(n)
        {
        return ( n < 0 || n > 9 ? "" : "0" ) + n;
        },
        
    getNowTimeStamp : function() 
        {
        var time = new Date();
        var gmtTime = new Date(time.getTime() + (time.getTimezoneOffset() * 60000));
        return gmtTime.toISODate() ;
        },
    
    ignoreCaseSort : function(a, b) 
        {
        var ret = 0;
        a = a.toLowerCase();
        b = b.toLowerCase();
        if(a > b) {ret = 1;}
        if(a < b) {ret = -1;}
        return ret;
        },
    
    generateV1Signature : function(url, key) 
        {
        console.info("Signing: "+url+" with key: "+key);
        var stringToSign = url;
        var signed =   b64_hmac_sha1(key, stringToSign);
        return signed;
        },
    
    /////////////////// String To Sign /////////////////////////////////////////////////////
    getStringToSign : function (url) 
        {
        var stringToSign = "";
        var query = url.split("?")[1];
    
        var params = query.split("&");
        params.sort(ClientApi.ignoreCaseSort);
        for (var i = 0; i < params.length; i++) 
            {
            var param = params[i].split("=");
            var name =   param[0];
            var value =  param[1];
            if (name == 'Signature' || !value) {continue;}
                stringToSign += name;
                stringToSign += decodeURIComponent(value);
             }
        return stringToSign;
        },
    
    createSignature : function (url, params, key)
        {
        
        console.info("Signing...");
        var query = "?" + dojo.objectToQuery(params);
        console.info("Got: "+query);
        
        url += query;
        
        return this.generateV1Signature(url, key);
        },
        
    test : function ()
        {
        var params = 
            {
            file: "/Users/adamfisk/blah.txt",
            tags: "tag1 tag2 tag3", 
            userId: 249372904
            // Make sure we bypass the cache.
            //t: (new Date()).getTime()
            };
        var url = Constants.CLIENT_URL + "publishFile"; 
        var sig = ClientApi.createSignature(url, params, "U792792SFJOOE");
        console.info("Got full url: "+sig);
        },
        
    
    /////////////////// Signed URL /////////////////////////////////////////////////////
    generateSignedUrl : function(url, secretKey) 
        {
        
        //var url = endpoint + "?SignatureVersion=1&Action=" + actionName + "&Version=" + encodeURIComponent(version) + "&";
        
        /*
        for (var i = 0; i < params.length; ++i) 
            {
            var elementName = params[i].name;
            var elementValue = params[i].value;
            if (elementValue) 
                {
                url += elementName;
                url += "=";
                url += encodeURIComponent(elementValue);
                url += "&";
                }
            else
                {
                throw new Error("No value for param: "+elementName);
                }
            
            }
            */
        //var timestamp = this.getNowTimeStamp();
        //url += "Timestamp=" + encodeURIComponent(timestamp);
       
        //url += "&userId=" + encodeURIComponent(userId);
        var signature = this.generateV1Signature(url, secretKey);
        url += "&Signature=" + encodeURIComponent(signature); 
       
        return url;
        },
    
    /////////////////// Build Form Fields /////////////////////////////////////////////////////
    getFormFieldsFromUrl : function (url) 
        {
        var fields  = "";
        var query = url.split("?")[1];
        var params = query.split("&");
        for (var i = 0; i < params.length; i++) 
            {
            var param = params[i].split("=");
            var name =   param[0];
            var value =  param[1];
             fields += "<input type=\"hidden\" name=\""+name+"\" value=\""+decodeURIComponent(value)+"\">";
            }
        return fields;
        }
    };