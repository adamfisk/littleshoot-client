function onLittleShootBase(data) {
    // NOTE: This currently gets called twice
    if (document.handledLittleShootCallback === true) {
        //console.info("Already received LittleShoot callback.");
        return;
    }
    
    document.handledLittleShootCallback = true;
    //console.info("Running onLittleShootBase!!");
    //console.info("Got littleshoot data: "+dojo.toJson(data));
    window.gotLittleShoot = true;

    window.appData = data;
    var clientJson = dojo.toJson(data);
    CommonUtils.setCookie(Constants.CLIENT_COOKIE_KEY, clientJson);
    
    $("#loadingIconMessage").hide();

    var changeMessage = function() {
        var version = data.appVersion;
        var message;
        if (data.isPro) {
            message = "Loaded LittleShoot Pro Version "+version+"!";
        } 
        else {
            message = "Loaded LittleShoot Version "+version+"!";
        }
            
        $("#statusMessage").html(message);
    };
    
    $("#statusMessageDiv").slideUp(1000, function() {
        $(this).slideDown(1000);
    });
    setTimeout(changeMessage, 800);
    
    var out = function () {
        $("#statusMessageDiv").slideUp(1000);
    };
    setTimeout(out, 8000);
    
    dojo.publish("littleShootDetected", [data]);
}

function onLittleShootFromJavaScript(data) {
    //console.info("Got LittleShoot from straight JavaScript!!!");
    onLittleShootBase(data);
}

