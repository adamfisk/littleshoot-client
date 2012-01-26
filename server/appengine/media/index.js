LittleShootIndex = {
        
    onOnLoad : function()
        {
        if (!CommonUtils.isBrowserSupported())
            {
            CommonUtils.showIncompatibleBrowserDialog();
            }
        
        littleShootConfig = 
            {
            disableAutoLoad : true,
            
            littleShootPresent : function (apiVersion) 
                {
                console.info("LittleShoot present");
                window.gotLittleShoot = true;
                LittleShootIndex.enableAll();
                },
            littleShootNotPresent : function () 
                {
                window.gotLittleShoot = false;
                },
            oldLittleShootVersion : function ()
                {
                console.info("LittleShoot update");
                window.gotLittleShoot = true;
                LittleShootIndex.enableAll();
                },
            littleShootLoading : "loading"
            };
        
        LittleShoot.hasLittleShoot();
        Button.buildButtons();
        //Publisher.activatePublish();
        CommonUtils.hideSpinner();
        
        Search.activate();
        CommonUtils.setPage("index");
        CommonUtils.commonLoad();
        
        $("#searchFormDiv").show();
        },
          
    enableAll : function ()
        {
        Button.enableButtons();
        Search.enableSearchButtons();
        }
};

