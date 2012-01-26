dojo.provide("littleshoot.Button");

Button =
    {
    changeButtonOver : function(button, bgUrl)
        {
        button.style.backgroundImage = bgUrl;
        button.style.color = "#000000";
        },
    
    changeButtonOut : function(button, bgUrl)
        {
        button.style.backgroundImage = bgUrl;
        button.style.color = "#545454";
        },
        
    buildButtons : function()
        {
        // Preload the rollover image into the browser's cache.
        (new Image( )).src = "http://littleshootimages.appspot.com/images/button100x22Over.gif";
        (new Image( )).src = "http://littleshootimages.appspot.com/images/button160x22Over.gif";
        dojo.query(".button").forEach(function(button) 
            {
            dojo.connect(button, "mouseover", null, function(event) 
                {
                Button.changeButtonOver(button, "url(http://littleshootimages.appspot.com/images/button100x22Over.gif)");
                }); 
            dojo.connect(button, "mouseout", null, function(event) 
                {
                Button.changeButtonOut(button, "url(http://littleshootimages.appspot.com/images/button100x22.gif)");
                });
            });
            
        dojo.query(".bigButton").forEach(function(button) 
            {
            dojo.connect(button, "mouseover", null, function(event) 
                {
                Button.changeButtonOver(button, "url(http://littleshootimages.appspot.com/images/button160x22Over.gif)");
                }); 
            dojo.connect(button, "mouseout", null, function(event) 
                {
                Button.changeButtonOut(button, "url(http://littleshootimages.appspot.com/images/button160x22.gif)");
                });
            });
        },
    
    enableButtonsByClass : function(buttonClass)
        {
        dojo.query("."+buttonClass).forEach(function(button) 
            {
            //console.log("Enabling button...");
            // TODO: This is different in current dojo API.
            //dojo.html.setOpacity(button, 1.0);
            });
        },
    
    enableButtons : function()
        {    
        Button.enableButtonsByClass("button");
        Button.enableButtonsByClass("bigButton");
        },
    
    disableButton : function(button)
        {
        // Not working...
        //console.info("Disabling button");
        dojo.connect(button, "onclick", null, function(event) 
            {
            // TODO: This is just problematic for now.
            //CommonUtils.showPluginRequiredDialog();
            });
        },
    
    disableButtonsByClass : function(buttonClass)
        {
        //console.log("Disabling buttons with class: "+buttonClass);
        dojo.query("."+buttonClass).forEach(function(button) 
            {
            //console.log("Disabling button...");
            Button.disableButton(button);
            });
        },
    
    disableButtons : function()
        {
        Button.disableButtonsByClass("button");
        Button.disableButtonsByClass("bigButton");
        }
    };