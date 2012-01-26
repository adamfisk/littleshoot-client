dojo.provide("littleshoot.AboutTab");

AboutTab = {
    alreadyDownloaded : false,
    onOnLoad : function() {
        if (!AboutTab.alreadyDownloaded &&
            CommonUtils.endsWith(window.location.pathname, "download"))
            {
            //CommonUtils.showDownloadConfirmDialog();
            AboutTab.alreadyDownloaded = true;
            }
    }
};