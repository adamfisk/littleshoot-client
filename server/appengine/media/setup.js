$(document).ready(function() {
    $(".simpleButton").hover(function () {
        $(this).addClass("simpleButtonOver");
    }, function () {
        $(this).removeClass("simpleButtonOver");
    });
    
    $(".simpleGrayButton").hover(function () {
        $(this).addClass("simpleGrayButtonOver");
    }, function () {
        $(this).removeClass("simpleGrayButtonOver");
    });
});