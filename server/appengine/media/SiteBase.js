
SiteBase =
    {
    onOnLoad : function()
        {
        if (!CommonUtils.isBrowserSupported())
            {
            CommonUtils.showIncompatibleBrowserDialog();
            //console.info("Bad browser or os...");
            }

        littleShootConfig = 
            {
            littleShootLoading: "loading",
            
            disableAutoLoad : true,
            
            littleShootPresent : function (apiVersion) 
                {
                console.info("LittleShoot present");
                window.gotLittleShoot = true;
                //Site.appPresent();
                },
            littleShootNotPresent : function () 
                {
                console.info("LittleShoot not present");
                window.gotLittleShoot = false;
                //Site.appNotPresent();
                },
            oldLittleShootVersion : function ()
                {
                console.info("LittleShoot old version");
                window.gotLittleShoot = true;
                //Site.oldVersion();
                }
            };
        LittleShoot.hasLittleShoot();
        
        Button.buildButtons();
        //CommonUtils.loadUrchin();
        
        var vp = dijit.getViewport();
        if (vp.w > 1000)
            {
            console.info("Showing extra nav elements...");
            CommonUtils.showAll(".forumsLink");
            }
        else
            {
            console.info("Not showing extra nav elements...width: "+vp.w);
            }
        
        CommonUtils.commonLoad();
        }
    };