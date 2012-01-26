var Link = {
        
    linkNotInstalledMessage : function(link, title) {
        jQuery().ready(function() {
            dojo.addOnLoad(function() {
                //console.info("linkNotInstalled");
                Link.subscribeToInstall(link, title);
                $("#linkCloseButton").click(function () {
                    $.unblockUI();
                    if (pageTracker) {
                        pageTracker._trackPageview("/installFromLink");
                    }
                    CommonUtils.downloadInstaller();
                    Link.showWaitingForInstallMessage();
                }).hover(function () {
                    $(this).css("border", "2px solid #aaa").css("color", "black");
                    
                }, function () {
                    $(this).css("border", "2px solid #777").css("color", "#333");
                });
                $.blockUI({ 
                    message: $("#linkNotInstalledMessageDiv"),
                    css: { 
                        top: '10%',
                        width: '50%',
                        left: '25%',
                        border: 'none', 
                        padding: '15px', 
                        backgroundColor: '#000', 
                        '-webkit-border-radius': '10px', 
                        '-moz-border-radius': '10px', 
                        opacity: '.6', 
                        color: '#fff',
                        cursor: 'default'
                    } 
                });
            });
        });
    },
    
    showWaitingForInstallMessage : function() {
        $("#linkWaitingCloseButton").click(function () {
            $.unblockUI();
        }).hover(function () {
            $(this).css("border", "2px solid #aaa").css("color", "black");
            
        }, function () {
            $(this).css("border", "2px solid #777").css("color", "#333");
        });
        $.blockUI({ 
            message: $("#linkWaitingMessageDiv"),
            css: { 
                top: '10%',
                width: '50%',
                left: '25%',
                border: 'none', 
                padding: '15px', 
                backgroundColor: '#000', 
                '-webkit-border-radius': '10px', 
                '-moz-border-radius': '10px', 
                opacity: '.6', 
                color: '#fff',
                cursor: 'default'
            } 
        });
    },
    
    subscribeToInstall : function (link, title) {
        dojo.subscribe("littleShootDetected", function(args) {
            $.unblockUI();
            // IE requires an href change to be in response to user interaction, so
            // we pop up a dialog here.
            var startDownload = function() {
                if (pageTracker) {
                    pageTracker._trackPageview("/fileFromInstallFromLink");
                }
                var params = {
                    uri: link,
                    title: title,
                    installed: true
                };
                // We go back to the link URL because we don't want the referrer
                // header to get in the way.
                window.location.href = "link?"+dojo.objectToQuery(params);
            };
            CommonUtils.showConfirmDialog("LittleShoot Detected!", 
                "OK great, LittleShoot's running. Click 'OK' to start downloading '"+title+"' now.", 
                startDownload);
        });
    }
};
