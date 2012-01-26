dojo.addOnLoad(function () {
    $(document).ready(function() {
        $("#buyProButton").click(function(evt) {
            //alert("Clicked!");
            
            window.location.href="amazonFpsBuy";
            
        });
        
        $("#devPayBuyProButton").click(function(evt) {
            window.location.href="https://aws-portal.amazon.com/gp/aws/user/subscription/index.html?ie=UTF8&offeringCode=D97E650F";
        });
    });
});