

jQuery().ready(function() {
    dojo.addOnLoad(function() {
        dojo.subscribe("littleShootDetected", function(appData) {
            //console.info("Got app data: "+dojo.toJson(appData));
            //if (appData.isPro === true) {
                var proImage =
                    '<span class="normalSmall" style="color: #8abc33">VERSION: '+appData.appVersion+'</span>';
                    //'<img src="images/pro.gif" alt="Running LittleShoot Pro!" /><span class="normal">&nbsp;Version: '+appData.appVersion+'</span>';
                $("#proLogoDiv").html(proImage).show(2000);
                
                /*
                $("#welcomeMessage").fadeOut("slow", function() {
                    $(this).html("Welcome to LittleShoot PRO!").fadeIn("slow");
                });
                */
            //}
        });
        
        $(".comparisonChartLink").click(function (evt) {
            evt.stopPropagation();
            evt.preventDefault();
            //Pro.showComparisonChartOverlay();
            window.location.href="download";
        });
    });
});