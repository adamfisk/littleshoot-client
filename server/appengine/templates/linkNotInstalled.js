
/**
 * This gets called after the user has downloaded and installed the app.
 */
function subscribeToInstall() {
    dojo.subscribe("littleShootDetected", function(args) {
        //console.info("Got LittleShoot from subscription!!");
        
        // IE requires an href change to be in response to user interaction, so
        // we pop up a dialog here.
        var startDownload = function() {
            window.location.href = p2pDownloadUrl;
        };
        CommonUtils.showConfirmDialog("LittleShoot Detected!", 
            "OK great, LittleShoot's running. Click 'OK' to start downloading '{{ title }}' now.", 
            startDownload);
    });
}

function downloadSelected() {
    console.info("Downloading installer...");
    //evt.stopPropagation();
    CommonUtils.downloadInstaller();
    //downloadBeta();
    
    $("#notDownloadingDiv").hide();
    
    $("#downloadingContainerDiv").show();
}
    
function cancelSelected() {
    console.info("User canceled download...");
    $("#notDownloadingDiv").show();
}

function downloadDialog() {
    CommonUtils.showConfirmDialog("Download LittleShoot?", 
        "You need LittleShoot, the P2P Browser Plugin, to download this link. "+
        "LittleShoot gives you super fast, cheap, and easy downloads wherever " +
        "you go on the web. Your download will start automatically when it's installed. "+
        "Would you like to start downloading LittleShoot now?", 
        downloadSelected,
        cancelSelected);
}

jQuery().ready(function() {
    dojo.addOnLoad(function() {
        $("#notDownloadingDiv").click(function() {
            downloadSelected();
        });
        subscribeToInstall();
        downloadDialog();
    });
});
