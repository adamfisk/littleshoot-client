
function navigateToTab(tab) {
    dojo.query("#tabs .selectedTab").forEach(function(item) {
        dojo.removeClass(item, "selectedTab");
        dojo.removeClass(item, "sprite-selectedTab");
        dojo.addClass(item, "unselectedTab");
        dojo.addClass(item, "sprite-unselectedTab");
    });
    var toggle;
    if (tab.nodeName === "DIV") {
        toggle = tab;
    }
    else {
        toggle = tab.parentNode;
    }
    dojo.removeClass(toggle, "unselectedTab");
    dojo.removeClass(toggle, "sprite-unselectedTab");
    dojo.addClass(toggle, "selectedTab");
    dojo.addClass(toggle, "sprite-selectedTab");
    dojo.query("#mainContentWhiteDiv .selectedContainer").forEach(function(item) {
        dojo.removeClass(item, "selectedContainer");
        CommonUtils.hideElement(item);
    });
    
    if (!dojo.hasClass(tab, "searchNav")) {
        // Always stop the continuous loading of search results when we navigate
        // between pages.  The search actually continues, but the browser doesn't
        // waste resources constantly reloading the data.
        if (window.searchResults) {
            // This also hides the spinner.
            window.searchResults.stop();
        }
    }
    var container;
    if (dojo.hasClass(tab, "searchNav")) {
        container = dojo.byId("searchContainer");
        Search.onOnLoad();
    }
    else if (dojo.hasClass(tab, "downloadsNav")) {
        container = dojo.byId("downloadsContainer");
        DownloadsTab.onOnLoad();
    }
    else if (dojo.hasClass(tab, "publishNav")) {
        container = dojo.byId("publishContainer");
        Publisher.onOnLoad();
    }
    else if (dojo.hasClass(tab, "aboutNav")) {
        //console.info("Loading about...");
        container = dojo.byId("aboutContainer");
        AboutTab.onOnLoad();
        
        // We just handle this here.
        if (dojo.query(".selectedAboutBody").length === 0) {
            var home = dojo.byId("homeContentBody");
            dojo.removeClass(home, "unSelectedAboutBody");
            dojo.addClass(home, "selectedAboutBody");
        }
    }
    
    if (container) {
        CommonUtils.showElement(container);
        dojo.addClass(container, "selectedContainer");
    }
}

TabHistory = {
    addHistory : function (tab) {
        //console.info("Adding history");
        
        if (!tab) {
            console.error("Need a tab to add to history!!");
        }
        var appState = {
            tab : tab,
            back: function(){ navigateToTab(this.tab); },
            forward: function(){ navigateToTab(this.tab); },
            changeUrl: true
        };
        
        dojo.back.addToHistory(appState);
    }            
};

function setInitialHistoryState(tabId) {
    var state =
        {
        tab : dojo.byId(tabId),
        back: function(){ navigateToTab(this.tab); },
        forward: function(){ navigateToTab(this.tab); },
        changeUrl: true
        };
    dojo.back.setInitialState(state);
}

function goToTab (evt, tab) {   
    window.scrollTo(0,0);
    evt.stopPropagation();
    evt.preventDefault();
    navigateToTab(tab);
    TabHistory.addHistory(tab);
}

function showIeDownloadMessage() {
    CommonUtils.showMessage("Internet Explorer Not Supported", 
        "We're sorry, but we're still working out some kinks in the LittleShoot version for Internet Explorer. LittleShoot works great on Google Chrome, FireFox, Safari, or Opera however. "+
        "We should have this fixed soon, but in the meantime you have to use a different browser to install LittleShoot. We apologize for the inconvenience.");
}

lighBoxSettings = {
    callback : function () {
        //console.info("got lightbox callback - enabling");
        Search.enableLightBox();
        
        // We do this in a timeout because we want to make sure the plugin 
        // returns from its init method.
        setTimeout(function () {
            $('a[rel*=lightbox]').lightBox({fixedNavigation:true, 
                extraText : function (href) {
                    return "   <a href='"+href+"' target='blank'>Open in New Window</a>";
                }
            }); 
        }, 10);
    } 
};

function delayedVideoOverlay() {
    //Delay all this so we don't hang up loading.
    setTimeout(function () {
        flowPlayerConfig = {
            callback: function () {
                //console.info("Got flowplayer callback!!");
                // install flowplayers
                
                var clip = {
                    zIndex: 3000000,
                    onStart: function(clip) {
                        //console.info("Started clip...");
                    },
                    plugins:  {
                        controls: {
                            sliderColor:'#646464;', 
                            sliderGradient:'high',
                            bufferColor: '#6e9f1a',
                            progressColor: '#6e9f1a',
                            timeColor: '#8abc33',
                            buttonColor: '#646464',
                            buttonOverColor: '#222222',
                            backgroundColor: '#000000',
                            zIndex: 3000001
                        }
                    }
                };
                $("a.player").flowplayer("swf/flowplayer-3.0.1.swf", clip); 
                $("#techCrunchVideoDiv").hide().show();
            }
        };
        overlayConfig = {
            callback: function () {
                //console.info("Got overlay callback!!");
                $(function() {
                    $("#techCrunchVideoLink").overlay({  
                        
                        // setup exposing (optional operation);
                        onBeforeLoad: function() {
                            this.expose({zIndex: 100000});  
                        },              
                        
                        onLoad: function(content) {
                            // find the player contained inside this overlay and load it
                            $("a.player", content).flowplayer(0).load();                    
                        },
                        
                        onClose: function(content) {
                            $("a.player", content).flowplayer(0).unload();
                            
                            // close exposing
                            $.unexpose();
                        }
                    });             
                }); 
            }
        };
        
        CommonUtils.loadJs("timestamped/extra-combined.js?version=littleShootTimeStampedResource");
        
    }, 2000);
}


function playYahooMedia(resultLink) {
    //console.info("Playing Yahoo file...");
    
    YAHOO.MediaPlayer.addTracks(resultLink.parentNode);
    
    // Note that "next" is not the last element in the list if the user
    // has manually selected different songs.  We assume they like where 
    // they are if they've manually selected a play list index, and we only
    // go to the next file if they're at the end of the list.
    YAHOO.MediaPlayer.next();
    YAHOO.MediaPlayer.play();
    YAHOO.MediaPlayer.setPlayerViewState(1);
    //console.info("Finished adding player data.");
    YAHOO.MediaPlayer.setQueueViewState(1);
}

function form_swap_values(){
    swapValues = [];
    jQuery(".swap_value").each(function(i){
        swapValues[i] = jQuery(this).val();
        jQuery(this).focus(function(){
            if (jQuery(this).val() === swapValues[i]) {jQuery(this).val("");}}).blur(function(){
            if (jQuery.trim(jQuery(this).val()) === "") {jQuery(this).val(swapValues[i]);}});
    });
}

function handleLogins() {
    $("#logoutLink").click(function (evt) {
        evt.stopPropagation();
        evt.preventDefault();
        
        var logoutUrl = $(this).attr("rel");
        
        var logoutCallback = function() {
            $.get(logoutUrl, function(data){
                CommonUtils.showMessage("Logout Status", data);
                $(".loggedIn").hide();
                $(".loggedOut").show();
            });
        };
        
        CommonUtils.showConfirmDialog("Confirm Logout", "Are you sure you want to logout?", logoutCallback);
    });
    
    $("#loginLink").click(function (evt) {
        CommonUtils.showSpinner();
        evt.stopPropagation();
        evt.preventDefault();
        
        var loginUrl = $(this).attr("rel");
        
        var loginCallback = function() {
            $.get(loginUrl, function(data){
                CommonUtils.showMessage("Login Status", data);
                $(".loggedOut").hide();
                $(".loggedIn").show();
            });
        };
            
        var loginDiv = document.createElement("div");
        loginDiv.className = "normal";
        
        var form = document.createElement("form");
        form.id = "loginForm";
        form.className = "loginForm";
        
        var centered = document.createElement("center");
        centered.appendChild(loginDiv);
        
        var msg = CommonUtils.createRawMessage("Login", centered);
        
        $.ajax({
            type: "GET",
            url: "/accounts/login/", 
            dataType: "jsonp",
            data: {noCache: (new Date()).getTime()}, 
            success: function(data){
                var tableDiv = document.createElement("div");
                tableDiv.id = "loginFormTableDiv";
                $(tableDiv).html(data.message);
                form.appendChild(tableDiv);
                
                var messageButtonDiv = document.createElement("div");
                form.appendChild(messageButtonDiv);
                
                // The containing element of dijit elements using this format must 
                // already have a parent the dijit wiring code can access, as it actually
                // replaces the element instead of adding to it.
                var loginButtonDiv = document.createElement("div");
                var cancelButtonDiv = document.createElement("div");
                messageButtonDiv.appendChild(loginButtonDiv);
                messageButtonDiv.appendChild(cancelButtonDiv);
                
                var loginButton = new dijit.form.Button({ 
                    type : "submit", label : "Login"
                }, loginButtonDiv);
                
                var cancelButton = new dijit.form.Button({
                    label : "Cancel",
                    onClick: function (evt) {msg.hide();}
                }, cancelButtonDiv);
                
                loginDiv.appendChild(form);
                CommonUtils.hideSpinner();
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.error("Error: "+textStatus+" Thrown: "+errorThrown);
                console.dir(XMLHttpRequest);
            }
        });
        
        msg.show();
        
        $(form).submit(function() {
            CommonUtils.showSpinner();
            var args = dojo.formToObject($(this).get(0));
            console.info("Posting form data...");
            
            var postSuccess = function(data) {
                console.info("Got form submit via GET response");
                CommonUtils.hideSpinner();
                if (data.success) {
                    msg.hide();
                    CommonUtils.showMessage("Login Succeeded", data.message);
                } else {
                    $("#loginFormTableDiv").hide("fast", function() {
                        $(this).html(data.message).show("fast");
                    });
                }
            };
            console.info("Logging in...");
            var appspotUrl = "http://littleshootapi.appspot.com";
            $.ajax({
                type: "GET",
                url: appspotUrl + "/accounts/loginSubmit/", 
                dataType: "jsonp",
                data: args, 
                success: postSuccess,
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    console.error("Error: "+textStatus+" Thrown: "+errorThrown);
                    console.dir(XMLHttpRequest);
                }
            });
            
            return false;
        });
    });
}

jQuery().ready(function() {
    dojo.addOnLoad(function() {
        $("#tabs .tabNav").click(function(evt) {
            var tab = evt.target;
            goToTab(evt, tab);
        });
        
        $(".tabNavLink").click(function(evt) {
            var tabId = $(this).attr("href");
            var tab = $(tabId).get(0);
            goToTab(evt, tab);
        });
        
        delayedVideoOverlay();
        
        $("#emailForm").submit(function() {
            var email = $("#quickSignupEmail").val();
            return CommonUtils.isValidEmail(email);
        });
        
        $("#logoDiv").click(function (evt) {
            window.location.href = "home";
        });

        form_swap_values();
        
        handleLogins();
    });
});

