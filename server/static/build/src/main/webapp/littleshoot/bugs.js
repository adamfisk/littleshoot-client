var Bugs =
    {
    reload : function()
        {
        console.log("Making dojo bind call to: "+this.url);
        var loadHandler = function(data, ioArgs)
            {
            console.log("Got server data: "+data);
            };
            
        var errorHandler = function(data, ioArgs)
            {
            alert("Error: Could not contact server!");
            };
        var deferred = dojo.xhrGet(
            {
            url: "http://www.lastbamboo.org/lastbamboo-common-bug-server/topBugs", 
            handleAs: "json",
            load: loadHandler,
            error: errorHandler
            });
        }
    };
