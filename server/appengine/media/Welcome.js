var Welcome = {
        
    welcome : function() {
        jQuery().ready(function() {
            dojo.addOnLoad(function() {
                $("#welcomeCloseButton").click(function () {
                    $.unblockUI();
                }).hover(function () {
                    $(this).css("border", "2px solid #aaa").css("color", "black");
                    
                }, function () {
                    $(this).css("border", "2px solid #777").css("color", "#333");
                });
                $.blockUI({ 
                    message: $("#welcomeMessageDiv"),
                    css: { 
                        top: '10%',
                        width: '50%',
                        left: '25%',
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
        });
    }
};

