DownloadsTab = {
    onOnLoad : function() {
        //console.info("Downloads Tab loaded.");
        if (window.searchResults) {
            window.searchResults.stop();
        }
        if (CommonUtils.hasLittleShoot()) {
            Downloads.loadAllDownloads();
        }
    }
};
