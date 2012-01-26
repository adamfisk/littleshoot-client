var Pro = {
        
    messageCss: { 
        top: '20%',
        width: '50%',
        left: '25%',
        border: 'none', 
        padding: '15px', 
        backgroundColor: '#000', 
        '-webkit-border-radius': '10px', 
        '-moz-border-radius': '10px', 
        opacity: '.7', 
        color: '#fff',
        cursor: 'default'
    },
    
    donate : function (page) {
        $.unblockUI();
        CommonUtils.showSpinner();
        if (pageTracker) {
            pageTracker._trackPageview("https://www.paypal.com/cgi-bin/webscr?donate&"+page);
        }
        $("#donateForm").trigger("submit");
    },
    
    buy : function() {
        var creditCardLogos =
            '<img src="images/visa.png" style="margin-right: 10px;"/>'+
            '<img src="images/mastercard.png" style="margin-right: 10px;"/>'+
            '<img src="images/discover.png" style="margin-right: 10px;"/>'+
            '<img src="images/amex.png"/>';
        
        $("#creditCardDiv").html(creditCardLogos);

        $("#proOkButton").click(function () {
            $.unblockUI();
            CommonUtils.showSpinner();
            if (pageTracker) {
                pageTracker._trackPageview("https://www.paypal.com/cgi-bin/webscr");
            }
            $("#buyProForm").trigger("submit");
        }).hover(function () {
            $(this).css("border", "2px solid #aaa").css("color", "black");
            
        }, function () {
            $(this).css("border", "2px solid #777").css("color", "#333");
        });
        
        $.blockUI({ 
            message: $("#payPalMessageDiv"),
            css: Pro.messageCss
        }); 
    },
    
    cancelled : function() {

    },
    
    success : function () {
        $("#successButton").click(function () {
            $.unblockUI();
        }).hover(function () {
            $(this).css("border", "2px solid #aaa").css("color", "black");
            
        }, function () {
            $(this).css("border", "2px solid #777").css("color", "#333");
        });
        
        $.blockUI({ 
            message: $("#proSuccessMessageDiv"),
            css: Pro.messageCss
        }); 
    },
    
    failed : function () {
        $("#failedButton").click(function () {
            $.unblockUI();
        }).hover(function () {
            $(this).css("border", "2px solid #aaa").css("color", "black");
            
        }, function () {
            $(this).css("border", "2px solid #777").css("color", "#333");
        });
        
        $.blockUI({ 
            message: $("#proFailedMessageDiv"),
            css: Pro.messageCss
        }); 
    },
    
    donateSuccess : function () {
        $("#successButton").click(function () {
            $.unblockUI();
        }).hover(function () {
            $(this).css("border", "2px solid #aaa").css("color", "black");
            
        }, function () {
            $(this).css("border", "2px solid #777").css("color", "#333");
        });
        
        $.blockUI({ 
            message: $("#donateSuccessMessageDiv"),
            css: Pro.messageCss
        }); 
    }
    
    /*
    showComparisonChartOverlay : function () {
        $("#comparisonChartOverlayOkButton").click(function () {
            $.unblockUI();
        }).hover(function () {
            $(this).css("border", "2px solid #aaa").css("color", "black");
            
        }, function () {
            $(this).css("border", "2px solid #777").css("color", "#333");
        });
        
        $.blockUI({ 
            message: $("#comparisonChartOverlay"),
            css: { 
                top: '5%',
                width: '50%',
                left: '25%',
                border: 'none', 
                padding: '15px', 
                backgroundColor: '#000', 
                '-webkit-border-radius': '10px', 
                '-moz-border-radius': '10px', 
                opacity: '.7', 
                color: '#fff',
                cursor: 'default'
            } 
        }); 
    }
    */
};


/*
function buyPro() {
    //CommonUtils.showMessage("")
    CommonUtils.showSpinner();
    $.getScript("http://littleshootjs.appspot.com/jquery.form.js", function() {
        CommonUtils.hideSpinner();
        var regFormDiv = document.createElement("div");
        regFormDiv.id = "registrationFormContainerDiv";
        
        var cssObj = {
            'padding' : '16px'
        };
        
        //$(regFormDiv).css("width", "400px").css("height", "300px");
        $(regFormDiv).css(cssObj);

        var regForm = document.createElement("form");
        regForm.id = "registrationForm";
        
        var formTable = document.createElement("table");
        regForm.appendChild(formTable);
        
        var messageButtonDiv = document.createElement("div");
        regForm.appendChild(messageButtonDiv);   
        
        var messageDiv = document.createElement("div");
        var textDiv = document.createElement("div");
        $(textDiv).addClass("normalMedium").html("Please take a moment to register before purchasing LittleShoot Pro.").css("margin-bottom", "10px");
        messageDiv.appendChild(textDiv);
        messageDiv.appendChild(regForm);
        
        $.getJSON("/accounts/registration_form/", function(data){
            $(formTable).html(data.content);
        });
        
        // The containing element of dijit elements using this format must 
        // already have a parent the dijit wiring code can access, as it actually
        // replaces the element instead of adding to it.
        var regButton = new dijit.form.Button({ 
            type : "submit", label : "Next"
        }, messageButtonDiv);

        regFormDiv.appendChild(messageDiv);
        
        var messageDialog = 
            CommonUtils.createRawMessage ("LittleShoot Pro Wizard", regFormDiv);
        messageDialog.show(); 

        function beforeSubmit() {
            CommonUtils.showSpinner();
        }
        
        function formSuccess(jsonResponseBody) {
            CommonUtils.hideSpinner();  
            if (jsonResponseBody.success === true) {
                //console.info("Processing success");
                //messageDialog.hide();
                //var messageDiv = document.createElement("div");
                //$(messageDiv).html(jsonResponseBody.content);
                //CommonUtils.showMessage("Registration Succeeded", messageDiv);
                var messageDiv = document.createElement("div");
                var textDiv = document.createElement("div");
                $(textDiv).addClass("normalMedium").css("margin-bottom", "10px").html(
                    "OK, great, we've got your info. LittleShoot has partnered with Amazon for LittleShoot Pro purchases, "+
                    "and we'll take you to the Amazon purchase page now. Click here to learn more.");
                messageDiv.appendChild(textDiv);
                
                var messageButtonDiv = document.createElement("div");
                messageDiv.appendChild(messageButtonDiv);
                
                var goToAmazon = function(evt) {
                    evt.stopPropagation();
                    evt.preventDefault();
                    window.location.href = "https://aws-portal.amazon.com/gp/aws/user/subscription/index.html?ie=UTF8&offeringCode=D97E650F";
                };
                var regButton = new dijit.form.Button({ 
                    onClick : goToAmazon,
                    type : "submit", label : "Purchase Pro on Amazon"
                }, messageButtonDiv);
            
                $(regFormDiv).empty().append(messageDiv);
            } else {
                console.info("Processing error"); 
                $(formTable).html(jsonResponseBody.content);
            }
        }
        
        $('#registrationForm').ajaxForm({ 
            beforeSubmit: beforeSubmit,
            url: "/accounts/registration_submit/",
            type: 'get',
            dataType:  'json',
            success: formSuccess
        });
    });
}
*/