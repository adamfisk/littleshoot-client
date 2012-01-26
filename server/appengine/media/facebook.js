/*
var LittleShootFacebook = function () {

    var publicInterface = {};
    
    function processFiles(jsonData) {
        console.info("Processing files!!");
        var files = jsonData.files;
        var fd = $("#filesDiv");
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            var fileDiv = dojo.create("div");
            $(fileDiv).html(file.name);
            fd.append(fileDiv);
        }
    }
    
    function addUploadForm(rawForm) {
        //console.info("Got raw form: "+rawForm);
        $("#uploadFormDiv").html(rawForm);
    }
    
    function dojoXhr (targetUrl, responseType, successFunc) {
        return dojoXhrWithArgs(targetUrl, responseType, successFunc, {});
    }
    
    function dojoXhrWithArgs (targetUrl, responseType, successFunc, args) {
        //console.info("About to load published files...");
        var deferred = dojo.xhrGet({
            url: targetUrl,
            timeout: 20000,
            handleAs: responseType,
            preventCache: true,
            content: args,
            load: function(response, ioArgs) {
                console.info("Final listing got the response: " + dojo.toJson(response));
                return response;
            },
            error: function(response, ioArgs) {
                console.warn("Error response: "+response);
                console.dir(ioArgs);
                return response;
            }
        });
        
        var success = dojo.hitch(this, function (response) {
            //console.info("Got deferred response: ", dojo.toJson(response));
            successFunc(response);
            //processFiles(response);
            return response;
        });
        
        var error = dojo.hitch(this, function (err) {
            console.error("Got error response: "+ err);
            return err;
        });
        deferred.addCallback(success);
        deferred.addErrback(error);
        
        return deferred;
    }
    
    function onConnected () {
        //alert("Connected!!!");
        //$("#fbLogoutDiv").show();
        $("#fbDiv").show();
    }
    
    function onNotConnected () {
        //alert("Not Connected!!!");
        
        //$("#fbLogoutDiv").hide();
        //var fbLogin = $("#fbLoginDiv");
        //fbLogin.html("<fb:login-button autologoutlink='true'></fb:login-button>");
        $("#fbDiv").hide();
        //FB.XFBML.Host.parseDomTree();
    }
    

    function createHrefUrl (result) {
        var params = {
            name: result.title,
            size: result.size
        };
        result.source = "littleshoot";
        var baseUrl = Constants.START_TORRENT_DOWNLOAD_URL; 

        console.info("Creating link from: "+dojo.toJson(result));
        return SearchResultUtils.createLocalHrefUrl(result, params, baseUrl);
    }
    
    function addFriends () {
        var pd = $("#profileDiv");
        pd.html("<span>" + 
                "<fb:profile-pic uid='loggedinuser' size='small'></fb:profile-pic>" + 
                "Welcome, <fb:name uid='loggedinuser' useyou='false'></fb:name>. You are signed in with your Facebook account." + 
                "</span>");
                
        //console.info("Added profile...");
        //console.dir(FB.ApiClient);
        
        //FB.Facebook.apiClient.friends_get([], function (results, ex) {
        FB.Facebook.apiClient.friends_getAppUsers(function (results, ex) {
        
        //FB.ApiClient.friends_getAppUsers(function (results, ex) {
        //FB.ApiClient.friends_get(null, function (results, ex) {
            console.info("Got friends...");
            //console.dir(FB.Facebook.apiClient);
            //console.dir(results);
            console.info("Got friends...putting into data");
            var data = {};
            data.items = results;
            var friendsStore = new dojo.data.ItemFileReadStore({data: data});
            var fd = $("#friendsDiv");
            
            var showEverything = function (friendIds) {
                console.info("Got Facebook friends!!!");
                //console.dir(friendIds);
                for (var i = 0; i < friendIds.length; i++) {
                    var newDiv = dojo.create("div");
                    newDiv.id = friendIds[i]+"Div";
                    console.info("Adding profile: "+friendIds[i]);
                    
                    // For some reason, the 'facebook-logo' attribute is not
                    // working -- Facebook returns a 1x1 pixel image instead.
                    newDiv.innerHTML =
                        '<fb:profile-pic uid="'+friendIds[i]+'" size="small"></fb:profile-pic>'+
                        '<fb:name uid="'+friendIds[i]+'" useyou="false"></fb:name>';
                        //"<span><fb:name uid='"+friendIds[i]+"' useyou='false'></fb:name></span>"+
                        //"<fb:profile-pic uid='"+friendIds[i]+"' facebook-logo='true' size='small'></fb:profile-pic>";
                    
                    fd.append(newDiv);    
                    //$(newDiv).html("<fb:profile-pic uid='"+friendIds[i]+"' facebook-logo='true' size='small'></fb:profile-pic>");
                    //"</span>";
                }
            };
            
            var PAGE_SIZE = 10;
            var all = friendsStore.fetch({onComplete: showEverything, start: 0, count:PAGE_SIZE});
            
            //console.dir(friendsStore);
            //store.fetchItemByIdentity({
            //    identity: 1, 
            //    onItem: function(item){
            //      console.debug("Pepper is in aisle ", pantryStore.getValue(item,"aisle"));
            //    }
            //  });
        
            // Because this is XFBML, we need to tell Facebook to 
            // re-process the document 
            console.info("About to parse DOM");
            FB.XFBML.Host.parseDomTree();
            
            var listFilesForUser = function(friendId) {
                var friendDivId = "#"+friendId+"Div";
                console.info("Looking for friend div: "+friendDivId);
                var friendDiv = $(friendDivId);
                var args = {
                    "userId" : friendId
                };
                dojoXhrWithArgs("/api/listS3FilesForId", "json", function (jsonData) {
                    console.info("GOT RESPONSE FOR FRIEND: "+dojo.toJson(jsonData));
                    
                    var files = jsonData.files;
                    for (var i = 0; i < files.length; i++) {
                        var file = files[i];
                        var fileDiv = dojo.create("div");
                        var link = dojo.create("a");
                        var jqueryLink = $(link);
                        jqueryLink.attr("title", file.title);
                        jqueryLink.html(file.title);
                        
                        if (CommonUtils.hasLittleShoot()) {
                            //var link = "<a title='"+file.title+"' href='"+curUrl+"'>"+file.title+"</a>";
                            var hrefUrl = createHrefUrl(file)
                            jqueryLink.attr("href", hrefUrl);

                            // We asynchronously start the download.  This 
                            // requires future polling call to update the 
                            // download progress for the user.
                            jqueryLink.click(function(evt) {
                                evt.preventDefault();
                                evt.stopPropagation();
                                console.info("Initiating async download for: "+hrefUrl);
                                Download.asyncDownload(hrefUrl);
                            });
                        } 
                        else {
                            jqueryLink.attr("href", file.uri);
                            jqueryLink.click(function(evt) {
                                evt.preventDefault();
                                evt.stopPropagation();
                                CommonUtils.showLittleShootRequiredConfirmDialog();
                            });
                        }
                        $(fileDiv).append(jqueryLink);
                        
                        console.info("Appending: "+dojo.toJson(file));
                        friendDiv.append(fileDiv);
                    }
                }, args);
            };
            
            var addFiles = function (friendIds) {
                console.info("Got Facebook friends!!!");
                //console.dir(friendIds);
                for (var i = 0; i < friendIds.length; i++) {
                    listFilesForUser(friendIds[i]);
                }
            };
            //console.info("Friends store 2:");
            //console.dir(friendsStore);
            var friendResults = friendsStore.fetch({onComplete: addFiles, start: 0, count:PAGE_SIZE});
        });
    }
    
    publicInterface.init = function () {
        FB_RequireFeatures(["XFBML", "Connect"], function() {
            FB.Facebook.init("875676e62306905463045067eb30aaba", "/xd_receiver.htm",
                {"ifUserConnected":onConnected, 
                 "ifUserNotConnected":onNotConnected});
            //FB.ensureInit(function() {
            //    FB.FBDebug.logLevel = 4;
            //    FB.FBDebug.isEnabled = true;
            //    //FB.XFBML.Host.addElement(new FB.XFBML.LoginButton($('#fbLoginDiv')));
            //    });
            FB.Facebook.get_sessionWaitable().waitUntilReady(function() {
                addFriends();
                
                dojoXhr("/api/listS3Files", "json", processFiles);
                dojoXhr("/freeUploadForm", "text", addUploadForm);
                
                // ENABLE THIS!!
                // You get the bundle ID from developers.facebook.com under Tools and Feed Template Console
                // See: http://www.facebook.com/video/video.php?v=636853997423 
                // at 5 minute mark
                // Look at video in conjunction with http://developers.facebook.com/tools.php?feed
                var fbCb = $("#publishToFacebook");
                if (fbCb && fbCb.checked) {
                    var templateData = {
                        "post-title" : "LittleShoot File",
                        "post-url" : "http://www.littleshoot.org",
                        "comment-text" : "Wow, this is cool",
                        "images" : 
                        [
                            {"src" : "http://www.littleshoot.org/test.png", "href" : "http://www.littleshoot.org"}
                        ]
                    };
                    //FB.Connect.showFeedDialog(BUNDLE_ID, templateData);
                }
            });
        });
    };
    
    return publicInterface;
}();
*/
