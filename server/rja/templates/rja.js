jQuery().ready(function() {
    //$("#frameText").hide();
    //$(".frameContent").hide();
    //$("div.overlay").hide();
    //$("#frameText").show();
    //$(".defaultFrameContent").show("slow");
    $(".courseLink").click(function(evt) {
        //console.info("Clicked course link");
        //console.info("Target: "+evt.target.id);
        
        var divName = evt.target.id + "Div";
        //console.info("Switching to div: "+divName);
        
        $(".selectedContent").hide("slow").removeClass("selectedContent");
        $("#"+divName).show("slow").addClass("selectedContent");
        $(".courseLink").removeClass("selectedNav");
        $("#"+evt.target.id).addClass("selectedNav");
        //$(".courseLink").removeClass("bigLink");
        //$("#"+evt.target.id).addClass("bigLink");
    });

    // Flow player stuff.
    var clip = {
        zIndex: 3000000,
        onStart: function(clip) {
            //console.info("Started clip...");
        },
        plugins:  {
            controls: {
                sliderColor:'#646464;', 
                sliderGradient:'low',
                bufferColor: '#edd8c0',
                progressColor: '#371804',
                timeColor: '#edd8c0',
                buttonColor: '#646464',
                buttonOverColor: '#222222',
                backgroundColor: '#111',
                zIndex: 6000001
            }
        }
    };
    $("a.player").flowplayer("swf/flowplayer-3.0.1.swf", clip); 
});

overlayConfig = {
    callback: function () {
        jQuery().ready(function() {
            //console.info("Got overlay callback!!");
            $(function() {
                $(".videoOverlay").overlay({  
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
                
                $(".overlayLink").overlay({  
                    // setup exposing (optional operation);
                    onBeforeLoad: function() {
                    //console.info("Got overlay callback!!");
                        this.expose({zIndex: 100000});  
                    },              
                    onLoad: function(content) {
                        // find the player contained inside this overlay and load it
                    //console.info("loading clip...");
                        //$("a.player", content).flowplayer(0).load();                    
                    },
                    onClose: function(content) {
                        //$("a.player", content).flowplayer(0).unload();
                        
                        // close exposing
                        $.unexpose();
                    }
                });  
            });
        });
    }
};