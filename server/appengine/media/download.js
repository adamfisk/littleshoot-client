//var addthis_options = 'twitter, email, facebook, more';
var addthis_options = 'email, favorites, digg, delicious, myspace, google, facebook, reddit, live, more';

var Download = {

    //IDLE_STATE = 0;
    //GETTING_SOURCES_STATE = 1;
    //DOWNLOADING_STATE = 2;
    //NO_SOURCES_STATE = 3;
    //COULD_NOT_DETERMINE_SOURCES_STATE = 4;
    //CANCELED_STATE = 5;
    //COMPLETE_STATE = 6;
    //FAILED_STATE = 7;
    //PAUSED_STATE = 8;
    
    isComplete : function (dlData) {
        switch (dlData.downloadStatus) {
            case 6:
                return true;
            case 100:
                return true;
            case 101:
                return true;
            case 102:
                return true;
            case 200:
                return true;
            case 201:
                return true;
            case 202:
                return true;
            case 203:
                return true;
            default:
                return false;
        }
    },
    
    isFailedOrCanceled : function (dlData) {
        switch (dlData.downloadStatus) {
            case 5:
                return true;
            case 7:
                return true;
            case 3:
                return true;
            case 4:
                return true;
            default:
                return false;
        }
    },
    
    asyncDownload : function (url) {
        //$("#downloadsDiv").hide("slow");
        var getParams = { 
            url: url,
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
        var getWhenLoaded = function () {
            var deferred = dojo.io.script.get(getParams);
            
            var success = function (response) {
                //console.info("Success on IsoHunt download");
                return response;
            };
            
            var error = function (response) {
                //console.error("Error on IsoHunt download: "+response);
                return response;
            };
            deferred.addCallback(success);
            deferred.addErrback(error);
            deferred.addBoth(function () {
                //$("#downloadsDiv").show("slow");
            });
        };
        dojoLoader.scriptGet(getWhenLoaded);
    },

    createOpenFolderUrl : function (result) {
        if (!result) {
            //console.info("Null result: "+result);
            console.error("No result");
            return "";
        }
        
        var params = {};
        params.uri = result.uri;
        params.name = result.title;
        params.noCache = (new Date()).getTime();
        
        var fileUrlBase = "http://www.littleshoot.org/api/client/openDownloadFolder/";
        
        var fileUrl = fileUrlBase +
            encodeURIComponent(result.title) +
            "?"+
            dojo.objectToQuery(params);
        
        //console.info("Returning URL: "+fileUrl);
        return fileUrl;
    },

    openFolder : function (result) {
        var url = Download.createOpenFolderUrl(result);
        var getParams = { 
            url: url,
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
                //console.info("Got success response to open folder");
                return response;
            };
            var error = function (response) {
                //console.info("Error opening folder: "+response);
                return response;
            };
            deferred.addCallback(success);
            deferred.addErrback(error);
            deferred.addBoth(function () {
            });
        };
        dojoLoader.scriptGet(getWhenLoaded);
    },
    
    createLocalHrefUrl : function (result) {
        if (!result) {
            //console.info("Null result: "+result);
            //console.error("No result");
            return "";
        }
        
        var params = {};
        params.uri = result.uri;
        params.name = result.title;
        params.size = result.size;
        params.cancelOnStreamClose = false;
        params.noCache = (new Date()).getTime();
        
        var fileUrlBase = "http://www.littleshoot.org/api/client/streamDownload/";//Constants.STREAM_DOWNLOAD_URL;
        
        var fileUrl = fileUrlBase +
            encodeURIComponent(result.title) +
            "?"+
            dojo.objectToQuery(params);
        
        //console.info("Returning URL: "+fileUrl);
        return fileUrl;
    },
        
    calculatePercentBase : function (numberator, denominator) {
        var raw = 100 * (numberator/denominator);
        return dojo.number.format(raw, {pattern: "####.##"});
    },

    calculatePercent : function(dlData) {
        return Download.calculatePercentBase(dlData.downloadBytesRead, dlData.size);
    },
        
    addTorrentState : function (result, str) {
         //Here's the enum of states as a reference.
         //enum state_t
         //   {
         //   0    queued_for_checking, 
         //   1    checking_files, 
         //   2    downloading_metadata,
         //   3    downloading,
         //   4    finished,
         //   5    seeding,
         //   6    allocating
         //   };
         
        if (result.torrentState) {
            switch (result.torrentState) {
                case 0:
                    str += " &nbsp &nbsp Queued";
                    break;
                case 1:
                    str += " &nbsp &nbsp Checking Files";
                    break;
                case 2:
                    str += " &nbsp &nbsp Downloading Metadata";
                    break;
                case 3:
                    str += " &nbsp &nbsp Downloading";
                    break;
                case 4:
                    str += " &nbsp &nbsp Finished";
                    break;
                case 5:
                    str += " &nbsp &nbsp Seeding";
                    break;
                case 6:
                    str += " &nbsp &nbsp Allocating";
                    break;
                default:
                    break;
            }
        }
        return str;
    },

    getDownloadStatus : function (result) {
        var str;
        var percentComplete;
        switch (result.downloadStatus) {
            case 0:
                str = "Download starting...";
                break;
            case 1:
                str = "Accessing download sources...";
                break;
            case 2:
                percentComplete = Download.calculatePercent(result);
                var read = 
                    "Read: " + CommonUtils.bytesToMb(result.downloadBytesRead) +" MB ("+percentComplete+"%)";
                var speed = CommonUtils.twoPlaces(result.downloadSpeed);
                //var speed = result.downloadSpeed;
                str = read +"&nbsp&nbsp Speed: "+speed+" KB/s";
                break;
            case 3:
                str = "Could not find sources";
                break;
            case 4:
                str = "Could not locate sources";
                break;
            case 5:
                str = "Canceled";
                break;
            case 6:
                str = Download.addTorrentState(result, "Complete");
                percentComplete = 100;
                break;
            case 7:
                str = "Failed";
                break;
            case 8:
                str = "Paused";
                break;
            case 100:
                str = "Verifying download integrity";
                percentComplete = 100;
                break;
            case 101:
                str = "Download corrupted";
                percentComplete = 100;
                break;
            case 102:
                str = "Download verified";
                percentComplete = 100;
                break;
            case 200:
                str = "Moving file";
                percentComplete = 100;
                break;
            case 201:
                str = Download.addTorrentState(result, "Moved file to "+result.path);
                percentComplete = 100;
                break;
            case 202:
                str = Download.addTorrentState(result, "Move file failed");
                percentComplete = 100;
                break;
            case 203:
                str = Download.addTorrentState(result, "Added to iTunes. Saved at "+result.path);
                percentComplete = 100;
                break;
            default:
                str = "Unknown download state: "+statusCode;
        }
        return str;
    },
        
    addOpenLink : function (curDiv, curDl) {
        //console.info("Adding open link");
        var jqueryDiv = $(curDiv);
        var clickAdded = jqueryDiv.data("clickAdded");
        
        if (clickAdded) {
            return;
        }
        // Make sure we always set this.
        jqueryDiv.data("clickAdded", true);
        if (curDl.numFiles === 1) {
            //console.info("Adding open link");
            jqueryDiv.click(function (evt) {
                evt.preventDefault();
                evt.stopPropagation(); 
                var href = Download.createLocalHrefUrl(curDl);
                window.open(href, curDl.uri,'toolbar=0,status=0,width=700,height=480');
                return false;
            });
        } else {
            jqueryDiv.click(function (evt) {
                evt.preventDefault();
                evt.stopPropagation(); 
                Download.openFolder(curDl);
                return false;
            });
        } 
    }, 
    
    updateDownloadBase : function(curDl, curDiv, updatedDownloadsData, capabilities) {
        var complete = Download.isComplete(curDl);
        if (!$(curDiv).attr("id")) {
            //console.error ("No id for div: "+curDiv);
            return;
        }
        if (curDl.downloadBytesRead) {
            var progress = Download.calculatePercent(curDl);
            $(".progressBarDiv", curDiv).progressbar("value", progress);
        } else if (complete) {
            $(".progressBarDiv", curDiv).progressbar("value", 100);
        }
        if (curDl.downloadNumSources !== undefined) {
            $(".numSourcesDetail", curDiv).html("Sources: "+curDl.downloadNumSources);
        } else {
            $(".numSourcesDetail", curDiv).html("");
        }
        
        if (curDl.timeRemaining !== undefined) {
            if (CommonUtils.startsWith(curDl.timeRemaining, "2147483647")) {
                $(".timeRemaining", curDiv).html("Remaining: &#8734;");
            } 
            else {
                $(".timeRemaining", curDiv).html("Remaining: "+curDl.timeRemaining);
            }
        } else {
            $(".timeRemaining", curDiv).html("");
        }
        $(".downloadStatus", curDiv).html(Download.getDownloadStatus(curDl));
        
        if (capabilities) {
            $(".downloadStopIcon", curDiv).show();
        }
        // Show the cancel icon if we're playing or paused.
        if (curDl.downloadStatus === 2 || curDl.downloadStatus === 8) {
            if (!capabilities) {
                $(".downloadStopIcon", curDiv).show();
            }
            $(".downloadPauseResumeIcon", curDiv).show();
            
            if (curDl.downloadStatus === 8) {
                //console.info("Setting resume icon...");
                var resumeKids = $(".downloadPauseResumeIcon", curDiv).children();
                resumeKids.addClass("ui-icon-play").removeClass("ui-icon-pause").attr("title", "Resume the Download");
            } else {
                // This is a little redundant since it will be called a lot,
                // but it's necessary to set back an oringinally paused 
                // download back to the paused state if and when it starts.
                var pauseKids = $(".downloadPauseResumeIcon", curDiv).children();
                pauseKids.addClass("ui-icon-pause").removeClass("ui-icon-play").attr("title", "Pause the Download");
            }
        } else {
            if (!capabilities) {
                $(".downloadStopIcon", curDiv).hide();
            }
            $(".downloadPauseResumeIcon", curDiv).hide();
        }
        
        var failedOrCanceled = Download.isFailedOrCanceled(curDl);
        if (failedOrCanceled) {
            $(curDiv).unbind();
            $(".streamStatusDiv", curDiv).empty();
        }
       
        var streamable = curDl.streamable;
        // Stream if we can.
        if (curDl.numFiles === 1) {
            var speed = curDl.downloadSpeed;
            //console.info("Speed: "+speed);
            var minByteForStreaming;
            if (speed === undefined) {
                // This will be the case if the file is complete, canceled, 
                // whatever.
                minByteForStreaming = 0;
            }
            else if (speed > 200) {
                minByteForStreaming = curDl.size/60;
            } 
            else if (speed > 150) {
                minByteForStreaming = curDl.size/30;
            }
            else if (speed > 100) {
                minByteForStreaming = curDl.size/15;
            }
            else {
                minByteForStreaming = curDl.size/8;
            }
            //console.info("Min for streaming: "+minByteForStreaming);
            if (complete) {
                $(".streamStatusDiv", curDiv).html("<a href='#'>Click to Open</a>");
                Download.addOpenLink(curDiv, curDl);
            } else if (!failedOrCanceled && streamable && (curDl.maxByte > minByteForStreaming || curDl.downloadSource != 2)) {
                $(".streamStatusDiv", curDiv).html("<a href='#'>Click to Stream</a>");
                Download.addOpenLink(curDiv, curDl);
            } else if (!failedOrCanceled && streamable) {
                $(".streamStatusDiv", curDiv).html("Buffering for Streaming...");
            }
        } 
        else {
            if (complete) {
                $(".streamStatusDiv", curDiv).html("<a href='#'>Open Folder</a>");
                Download.addOpenLink(curDiv, curDl);
            } 
            else if (!failedOrCanceled) {
                $(".streamStatusDiv", curDiv).html("Downloading Multiple Files");
            }
        }
    }
};