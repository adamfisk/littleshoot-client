$(document).ready(function() {
 
   //alert("Adding accordion js");
    //ACCORDION BUTTON ACTION   
    $('div.accordionButton').click(function() {
        $('div.accordionContent').slideUp('normal');    
        $(this).next().slideDown('normal');
    });
 
    //HIDE THE DIVS ON PAGE LOAD    
    $("div.accordionContent").hide();
    
    $("#accordionNav").show();
 
});