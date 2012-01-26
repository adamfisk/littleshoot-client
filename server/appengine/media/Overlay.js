jQuery().ready(function() {
    dojo.addOnLoad(function() {
        $(".overlayCloseIcon").click(function () {
            $.unblockUI();
        });
        
        $(".overlayCloseIconDiv").hover(
            function() { $(this).addClass('ui-state-hover'); }, 
            function() { $(this).removeClass('ui-state-hover'); }
        );
    });
});