dojo.provide("littleshoot.InstallAndDownload");

var InstallAndDownload =
    {
    
    installAndDownload : function()
        {
        var delayedDownload = function()
            {
            setTimeout(function()
                {
                CommonUtils.downloadInstaller();
                }, 
                2000);   
            };
        dojo.addOnLoad(delayedDownload);
        }
    };
