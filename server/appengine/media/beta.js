function downloadBeta() {
    var baseUrl = "http://cloudfront.littleshoot.org/";
    var baseFileName = "LittleBeta096";
    var fileName;
    if(/Windows/.test(navigator.userAgent)) {
        if ($.browser.msie) {
            CommonUtils.showMessage("IE Not Ready!", "We're sorry, but there's no beta available for Internet Explorer at this time.");
            //fileName = "LittleShootIe1.exe";
            return;
        }
        else {
            fileName = baseFileName+".exe";
            //fileName = "LittleBeta9.exe";
            //fileName = "LittleBeta096.exe";
        }
    }
    else if (/Mac/.test(navigator.userAgent)) {
        fileName = baseFileName+".dmg";
        //fileName = "LittleBeta090.dmg";
    } 
    else {
        CommonUtils.showMessage("Linux Not Ready!", "We're sorry, but there's no beta available for Linux at this time.");
        return;
    }
    var downloadUrl = baseUrl + fileName;
    window.location.href = downloadUrl;
    
    if (pageTracker) {
        pageTracker._trackPageview(downloadUrl);
    }
}

jQuery().ready(function() {
    dojo.addOnLoad(function() {
        $(".betaDownloadLink").click(function (evt) {
            evt.stopPropagation();
            evt.preventDefault();
            downloadBeta();
        });
    });
});