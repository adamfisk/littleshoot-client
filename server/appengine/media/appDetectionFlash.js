function onLittleShootFromFlash(data) {
    //console.info("Got LittleShoot from Flash!!!");
    onLittleShootBase(data);
}

function onNoLittleShootFromFlash() {
    //console.info("Got littleshoot not present from flash!!");
    //window.gotLittleShoot = false;
}

function onLittleShootSwf() {
    //console.info("Got callback from SWF load");
    dojo.addOnLoad(CommonUtils.pollForLittleShoot);
}

jQuery().ready(function() {
    dojo.addOnLoad(function() {
        if (window.gotLittleShoot) {
            //console.info("Updating status message");
            //$("#statusMessageDiv").css("visibility", "visible");
            //console.info("Updated status message");
            //$("#statusMessageDiv").fadeOut();
        }
        CommonUtils.loadFlash();
    });
});


