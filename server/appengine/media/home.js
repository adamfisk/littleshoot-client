jQuery().ready(function() {
    dojo.addOnLoad(function() {
    
        $("#buttonDivFree").click(function(evt) {
            if ($.browser.msie) {
                showIeDownloadMessage();
            }
            else {
                CommonUtils.downloadInstaller();
                /*
                var okCallback = function (evt) {
                    downloadBeta();
                };
                var noCallback = function(evt) {
                    CommonUtils.downloadInstaller();
                };
                CommonUtils.showChoicesDialog("Try the BitTorrent Beta?", 
                    "Would you like to try the new beta with BitTorrent support and much more? You can also download the older version without BitTorrent.", okCallback, noCallback, "OK, Give Me the Beta!", "Just Give Me the Old One");
                    */
                
            }
        }).hover(function () {
            //$("#freeBullets").show();
        }, function () {
            //$("#freeBullets").hide();
        });
        
        $(".donateLink").click(function(evt) {
            evt.stopPropagation();
            evt.preventDefault();
            $.blockUI({ 
                message: $("#donateOverlay"),
                css: { 
                    top: '10%',
                    width: '40%',
                    left: '30%',
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
            
        $(".donateLinkDirect").click(function () {
            Pro.donate();
        });
        $(".overlayCancelButton").click(function () {
            $.unblockUI();
        });
        
        $(".overlayOkButton").click(function () {
            $.unblockUI();
        });
            
        $("#buttonDivPro").click(function(evt) {
            if ($.browser.msie) {
                showIeDownloadMessage();
            }
            else {
                Pro.buy();
            }
        }).hover(function () {
            $("#proBullets").show();
        }, function () {
            $("#proBullets").hide();
        });
        
        
        $(".buttonDiv").hover(function () {
            $(".buttonNotOver", this).hide();
            $(".buttonOver", this).show();
        }, function () {
            $(".buttonOver", this).hide();
            $(".buttonNotOver", this).show();
        }).show();
        
        
        /*
        $("#proFreeComparison").click(function(evt) {
            evt.stopPropagation();
            evt.preventDefault();
            //$.blockUI({ message: $('#comparisonChart') }); 
            $('#test').block({  
                // disable horz centering 
                centerX: false, 
                // disable vertical centering 
                centerY: false, 
                // apply css props as desired 
            }); 
            
            //setTimeout($.unblockUI, 2000); 
        });
        */
        
    });
});