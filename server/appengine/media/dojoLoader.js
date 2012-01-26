
var dojoLoader = {
    whenLoaded : function(func, module) {
        dojo.require(module);
        dojo.addOnLoad(func);
    },
    
    scriptGet : function(func) {
        dojoLoader.whenLoaded(func, "dojo.io.script");
    }
};