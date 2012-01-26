
var nullCheck = function(label, item) {
    if (item) {
        return label + item;
    }
    return "";
}; 

var downloadsMap = {};
        
var directive = {
    /*
    "div div div.downloadStatus+" : function(arg) {
        var uri = arg.item.uri;
        //downloadsMap["'"+uri+"'"] = arg.item;
        return getDownloadStatus(arg.item);
    },
    */
    "div div div span.size" : function(arg) {
        return "Size: " + CommonUtils.bytesToMb(arg.item.size) +" MB"; 
    },
    "div div div span.numFiles" : function(arg) {
        return nullCheck("Num Files: ", arg.item.numFiles); 
    }
};

function newDownloadData (oldDownloads, newDownloads) {
    if (!oldDownloads) {
        //console.warn("No old downloads!!");
        return true;
    }
    if (oldDownloads.downloads.length === newDownloads.downloads.length) {
        return false;
    }
    return true;
}

function downloadsCall(path) {

    //console.info("Clearing downloads with path: "+path);
    
    $("#downloadsDiv").hide("slow", function (evt) {
        $(this).empty();
        window.oldDownloads = null;
    });
    
    var getParams = { 
        url: Constants.CLIENT_URL + path,
        callbackParamName: "callback",
        load: function (response, args) {
            return response;
            },
        error : function (response, args) {
            //console.info("Error clearing downloads: "+response+args);
            return response;
            },
        content: {},
        timeout : 4000
    };
    
    var getWhenLoaded = function() {
        var deferred = dojo.io.script.get(getParams);
        var success = function (response) {
            //console.info("Cleared inactive downloads");
            return response;
        };
        var error = function (response) {
            //console.info("Did not clear inactive downloads: "+response);
            return response;
        };
        deferred.addCallback(success);
        deferred.addErrback(error);
        deferred.addBoth(function () {
            $("#downloadsDiv").hide("slow", function (evt) {
                $("#downloadsDiv").show("slow");
            });
        });
    };
    dojoLoader.scriptGet(getWhenLoaded);
}

var jsonDownloadsTemplate = {"total":0,"numDownloads":0,"downloadDir":"dir",
    downloads: [{
        "title":"file", "downloadBytesRead":30, "numFiles": 10, 
        "downloadNumSources" : 20, "downloadSpeed": 2.2,
        "downloadStatus":2,"uri":"YIX","id":"r","lastModified":11,"size":13,
        "timeRemaining":10
    }]
};

var Downloads = {
    loadAllDownloads : function () {
        
        if (window.downloadsLoading) {
            //console.info("Not starting to load downloads again...");
            return;
        }
        window.downloadsLoading = true;
        $("#returnToTorrentLink").click(function(evt) {
            //console.info("Got click");
            window.history.go(-2);
        });
        CommonUtils.showSpinner();
        //window.resizeTo(500,500);
        $("#downloadsTemplate").compile("templateFunc", directive, jsonDownloadsTemplate);
        
        //$("#loadingDownloadsDiv").fadeIn("fast");
    
        var hasSucceeded = false;
        var failureCount = 0;
        
        function queueRequest() {
            setTimeout(function () {
                //console.info("Refreshing after timeout...");
                try {
                    refreshJson();
                } catch (err) {
                    console.error("Caught error: "+err);
                }
            }, 2000);
        }
        
        function refreshJson() {
            //console.info("Refreshing JSON");
            var params = {
                pageIndex:0,
                resultsPerPage: 40
            };
            var getParams = { 
                url: Constants.CLIENT_URL + "downloads",
                callbackParamName: "callback",
                load: function (response, args) {
                    return response;
                },
                error : function (response, args) {
                    //console.info("Error accessing downloads: "+response+args);
                    return response;
                },
                content: params,
                timeout : 4000
            };
            var getWhenLoaded = function() {
                var deferred = dojo.io.script.get(getParams);
                var success = function (response) {
                    try {
                        CommonUtils.hideSpinner();
                        failureCount = 0;
                        updateDownloads(response);
                        window.oldDownloads = response;
                        if (!hasSucceeded) {
                            $("#downloadsErrorDiv").html(
                                "We're having trouble contacting the LittleShoot Plugin." +
                                "<br><br>You might want to try refreshing the page. Thanks.");
                        }
                        hasSucceeded = true;
                    } catch (err) {
                        console.error("Caught error: "+err);
                    }
                    queueRequest();
                    return response;
                };
                
                var error = function (response) {
                    console.warn("Got error: "+response);
                    try {
                        failureCount++;
                        if (failureCount > 6) {
                            CommonUtils.hideSpinner();
                            $("#downloadsDiv").empty();
                            $("#loadingDownloadsDiv").fadeOut("slow", function(evt) {
                                $("#noDownloadsDiv").fadeOut("slow", function(evt) {
                                    $("#downloadsErrorDiv").show("slow");
                                });
                            });
                        }
                    } catch (err) {
                        console.error("Caught error: "+err);
                    }
                    queueRequest();
                    return response;
                };
                deferred.addCallback(success);
                deferred.addErrback(error);
            
            };
            dojoLoader.scriptGet(getWhenLoaded);
        }
        
        var rebuildDownloads = function (updatedDownloadsData, jQueryDownloadsDiv) {
            $(".singleDownload", jQueryDownloadsDiv).each(function(index, domElement) {
                var addUrl = $(this).attr("rel");
                if (!addUrl) {
                    // This happens every time with the template element itself.
                    return;
                }
               if (!CommonUtils.startsWith(addUrl, "http")) {
                   //console.info("Not linking to non-HTTP URI for now");
                   return;
               }
               var title = $(this).attr("linkTitle");
               
               var params = {
                   //sender:
                   uri: addUrl,
                   title: title
               };
               
               // We don't use https here because https doesn't have all the
               // cookies we need for LittleShoot detection.
               //var fullUrl = "https://littleshootapi.appspot.com/link?"+
               var fullUrl = "http://www.littleshoot.org/link?"+
                   dojo.objectToQuery(params);
               
               var addElement = document.createElement("a");
               
               var add = $(addElement);
               add.addClass("viralLink");
               add.attr("href", 'http://www.addthis.com/bookmark.php?v=20');
               //add.attr("onmouseout", 'addthis_close()');
               add.mouseout(function() {
                   addthis_close();
               });
               //add.attr("onclick",'return addthis_sendto()');
               add.click(function() {
                   return addthis_sendto();
               });
               
               //add.attr("onmouseover", "return addthis_open(this, '', '"+fullUrl+"', '"+title+"')");
               add.html("More...");
               
               var tweetDiv = document.createElement("div");
               var fbDiv = document.createElement("div");
               
               var twitterElement = document.createElement("a");
               var twitter = $(twitterElement);
               twitter.addClass("viralLink");
               twitter.html("Tweet File");
               twitter.attr("title", "Post a short link to this file to Twitter.");
               //twitter.attr("target", "_blank");
               twitter.easyTooltip({
                   useElement: "twitterTooltip"
               });
               
               var fbElement = document.createElement("a");
               var fb = $(fbElement);
               fb.addClass("viralLink");
               fb.html("Post File to Facebook");
               
               fb.attr("title", "Post a short link to this file on Facebook. ");
               fb.attr("href", "#");
               //fb.attr("target", "_blank");
        
               fb.easyTooltip({
                   useElement: "fbTooltip"
               });
               
               // We have to request the short url first, and not on the click,
               // because otherwise the window.open call is outside the context
               // of the click and triggers popup blocking.
               BitlyUtils.shorten(fullUrl, function(tiny) {
                   var twitterBaseUrl = "http://twitter.com/home?status=";
                   var twitterUrl = twitterBaseUrl +
                       encodeURIComponent("#shoot Check out this LittleShoot file: "+tiny);
                   twitter.click(function(evt) {
                       evt.preventDefault();
                       evt.stopPropagation();
                       //window.open(twitterUrl);
                       window.open(twitterUrl, 'LittleTwit','width=800,height=400');
                       if (pageTracker) {
                           pageTracker._trackPageview("/twitterDownloadLink");
                       }
                       return false;
                   });
                   
                   add.mouseover(function() {
                       return addthis_open(this, '', tiny, title);
                   });
                   
                   $(fbDiv).click(function(evt) {
                       evt.preventDefault();
                       evt.stopPropagation(); 
                       var fbParams = {
                           u : tiny,
                           t : title
                       };
                       
                       var fbUrl = "http://www.facebook.com/sharer.php?" +
                           dojo.objectToQuery(fbParams);
                       window.open(fbUrl, 'LittleBook','toolbar=0,status=0,width=626,height=436');
                       if (pageTracker) {
                           pageTracker._trackPageview("/facebookDownloadLink");
                       }
                       return false;
                   });
               });
               
               twitter.attr("href", "#");
               
               var addDiv = document.createElement("div");
               $(addDiv).addClass("viralDiv");
              
               tweetDiv.appendChild(twitterElement);
               fbDiv.appendChild(fbElement);
               $(tweetDiv).addClass("viralLinkDiv");
               $(fbDiv).addClass("viralLinkDiv");
               
               addDiv.appendChild(tweetDiv);
               addDiv.appendChild(fbDiv);
               
               addDiv.appendChild(addElement);
               $(this).append(addDiv);
           });
           
           $(".progressBarDiv", jQueryDownloadsDiv).progressbar({value: 0.0});
           
           $(".downloadPauseResumeIcon", jQueryDownloadsDiv).click(function (evt) {
               evt.preventDefault();
               evt.stopPropagation();
               var uri = $(evt.currentTarget).attr("rel");
               var downloadJson = downloadsMap["'"+uri+"'"];
        
               var params = {};
               params.uri = uri;
               
               var $target = $(evt.currentTarget);
               
               var path;
               if (downloadJson.downloadStatus === 8) {
                   path = "resumeDownload";
                   var resumeKids = $target.children();
                   resumeKids.removeClass("ui-icon-play").addClass("ui-icon-pause").attr("title", "Pause the Download");
               }
               else {
                   path = "pauseDownload";
                   var pauseKids = $target.children();
                   pauseKids.addClass("ui-icon-play").removeClass("ui-icon-pause").attr("title", "Resume the Download");
               }
               $.getJSON(Constants.CLIENT_URL + path + "?callback=?", 
                   params,
                   function (data) {
                       //console.info("Got response: "+data);
                   }
               );
           });
           
           $(".downloadIcon", jQueryDownloadsDiv).hover(
               function() { $(this).addClass('ui-state-hover'); }, 
               function() { $(this).removeClass('ui-state-hover'); }
           );
           
           $(".downloadStopIcon", jQueryDownloadsDiv).click(function (evt) {
               var uri = $(evt.currentTarget).attr("rel");
               //console.info("got uri: "+uri);
               var downloadJson = downloadsMap["'"+uri+"'"];
               //console.info("got downloadJson: "+downloadJson);
               //console.info("from map: "+downloadsMap);
        
               var params = {};
               params.uri = uri; 
               
               var stopDownloadCallback = function(removeFiles) {
                   params.removeFiles = removeFiles;
                   $.getJSON(Constants.CLIENT_URL + "stopDownload?callback=?", 
                       params,
                       function (data) {
                           //console.info("Got response...");
                       }
                   );
               };
               
               var curStopIcon = $(this);
               var okCallback = function(stopOnNo) {
                   // Versions before 0.994 did not support also deleting 
                   // associated files (had no "capabilities" array).
                   if (!updatedDownloadsData.capabilities) {
                       stopDownloadCallback(false);
                   }
                   else {
                       var yesFilesCallback = function() {
                           //console.info("Stopping downloads and removing files");
                           stopDownloadCallback(true);
                       };
                       
                       var noFilesCallback = function() {
                           //console.info("Stopping downloads and not removing files");
                           
                           // If we kill the download without removing the files,
                           // we can't remove the files later.  In some cases,
                           // "killing the download" doesn't mean anything 
                           // anyway, such as when it's already canceled.
                           if (stopOnNo) {
                               stopDownloadCallback(false);
                           }
                       };
                       //CommonUtils.showChoicesDialog(titleText, bodyText, okCallback, cancelCallback, "OK", "Cancel");
                       CommonUtils.showChoicesDialog("Clear All Files?", 
                           "Would you like to remove all downloaded files for '"+downloadJson.title+"'?", yesFilesCallback, noFilesCallback, "Yes", "No");
                   }
               };
               evt.preventDefault();
               evt.stopPropagation();
               
               var complete = Download.isComplete(downloadJson);
               var failedOrCanceled = 
                   Download.isFailedOrCanceled(downloadJson);
               
               // If it's not downloading, "canceling" the download doesn't
               // really make sense -- we just want to check if we should
               // clear the files.
               if (complete || failedOrCanceled) {
                   okCallback(false);
               }
               else {
                   CommonUtils.showConfirmDialog("Cancel Download?", 
                       "Are you sure you want to cancel downloading '"+downloadJson.title+"'? " +
                       "You will lose all downloaded data and cannot undo this action.", 
                       function () {
                           okCallback(true);
                   });
               }
           });//.hide();
           
           $(".singleDownload", jQueryDownloadsDiv).hover(
               function () {
                   $(this).addClass("singleDownloadHover");
               }, 
               function () {
                   $(this).removeClass("singleDownloadHover");
               }
           );
        };
    
        var updateDownloads = function(updatedDownloadsData) {
           
            //console.info("Updating with num downloads: "+updatedDownloadsData.downloads.length);
            
            //console.info("Updating data with downloads: "+dojo.toJson(updatedDownloadsData));
            if (!updatedDownloadsData) {
                //console.error("No data!!");
                return;
               //throw Error("No data!!");
            }
            
            if (updatedDownloadsData.downloads.length === 0) {
                $("#downloadsDiv").empty();
                $("#loadingDownloadsDiv").fadeOut("slow", function(evt) {
                    $("#downloadsErrorDiv").fadeOut("slow", function(evt) {
                        $("#noDownloadsDiv").fadeIn("slow");
                    });
                });
                return;
            } 
            else {
                $("#loadingDownloadsDiv").fadeOut("fast", function(evt) {
                    $("#downloadsErrorDiv").fadeOut("fast", function(evt) {
                        $("#noDownloadsDiv").fadeOut("fast", function(evt) {
                            $("#downloadsDiv").show("slow");
                        });
                    });
                });
                
            }
            
            if (newDownloadData(window.oldDownloads, updatedDownloadsData)) {
                try {
                    // Reset the map.
                    downloadsMap.length = 0;
                    //downloadsMap = {};
                    
                    var jQueryDownloadsDiv = $("#downloadsDiv");
                    jQueryDownloadsDiv.empty();
                    jQueryDownloadsDiv.html($p.render(updatedDownloadsData, "templateFunc"));
                    rebuildDownloads(updatedDownloadsData);
                } catch (updateDownloadErr) {
                    console.error("Caught error updating downloads: "+updateDownloadErr);
                    return;
                }
            }
    
            //console.info("Looping through "+updatedDownloadsData.downloads.length+" downloads...")
            var capabilities = updatedDownloadsData.capabilities;
            for (var i = 0; i < updatedDownloadsData.downloads.length; i++) {
                var curDl = updatedDownloadsData.downloads[i];
                var curDiv = $("#"+curDl.id).get(0);
                downloadsMap["'"+curDl.uri+"'"] = curDl;
                try {
                    Download.updateDownloadBase(curDl, curDiv, capabilities);
                } catch (err) {
                    console.error("Caught error updating single download: "+err);
                }
            }
           
            var totalUpload = updatedDownloadsData.totalPayloadUploadBytes +
                updatedDownloadsData.historicUploadBytes;
            var totalDownload = updatedDownloadsData.totalPayloadDownloadBytes +
                updatedDownloadsData.historicDownloadBytes;
            $("#totalDownloadBytes").html(CommonUtils.bytesToMb(totalDownload)+" MB");
            $("#totalUploadBytes").html(CommonUtils.bytesToMb(totalUpload)+" MB");
            $("#downloadRate").html(CommonUtils.twoPlaces(updatedDownloadsData.payloadDownloadRate/1024) + " KB/s");
            $("#uploadRate").html(CommonUtils.twoPlaces(updatedDownloadsData.payloadUploadRate/1024) + " KB/s");
           
    
            var ratio = totalUpload/totalDownload;
            $("#ratio").html(CommonUtils.twoPlaces(ratio));
        }; // End updateDownloads
        
        $("#logoDiv").click(function() { 
            window.location.href="home";
        });
        
        refreshJson();
        
        $("#clearInactiveButton").click(function(evt) {
            evt.preventDefault();
            evt.stopPropagation();
            downloadsCall("clearInactiveDownloads");
        });
        $("#clearFailedButton").click(function(evt) {
            evt.preventDefault();
            evt.stopPropagation();
            downloadsCall("clearFailedDownloads");
        });
    }
};

