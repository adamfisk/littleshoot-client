jQuery().ready(function() {
    dojo.addOnLoad(function() {
        //if (top.location != self.location) {
        //    top.location.replace(self.location);
        //}
    
        
        $(".overlayCancelButton").click(function () {
            $.unblockUI();
        });
        
        $(".overlayButton").hover(function () {
            $(this).css("border", "2px solid #aaa").css("color", "black");
        }, function () {
            $(this).css("border", "2px solid #777").css("color", "#333");
        });
    });
});

var Common = {
        
    getDownloadUrl : function () {
        var baseBaseUrl = "http://cloudfront.littleshoot.org/LittleShoot-";
        //var baseUrl = baseBaseUrl + "0994";
        var baseUrl = baseBaseUrl + "09991";
        if(/Windows/.test(navigator.userAgent)) {
            //return "http://cloudfront.littleshoot.org/LittleShoot-0993.exe";
            return baseUrl + ".exe";
        }
        else if (/Mac/.test(navigator.userAgent)) {
            return baseBaseUrl + "09991.dmg";
        }
        else {
            return baseUrl + ".tgz";
        }
    }
};