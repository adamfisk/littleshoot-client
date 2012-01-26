dojo.provide("littleshoot.Servers");

var Servers =
    {
    lastReturned: true,
    lastData: null,
    check : function()
        {
        console.log("Checking server data.  Last returned is: "+Servers.lastReturned);
        if (Servers.lastReturned)
            {
            Servers.lastReturned = false;
            }
        else
            {
            console.log("Aborting call because the last one hasn't returned.");
            return;
            }
        
        if (Servers.lastReturned)
            {
            console.error("Last returned now true!!");
            }
        
        var loadHandler = function(data, ioArgs)
            {
            console.log("Got server data: "+data);
            Servers.lastData = data;
            clearTimeout(Servers.timeoutId);
            var serversInnerDiv = document.createElement("div");
            serversInnerDiv.id = "serversInnerDiv";
            for (var i = 0; i < data.servers.length; i++)
                {
                console.log("Appending result to 1 with i: "+i);
                Servers.appendServer(data.servers[i], serversInnerDiv);
                }
            
            var serversDiv = dojo.byId("serversDiv");
            var existing = dojo.byId("serversInnerDiv");
            console.log("Existing: "+existing);
            if (!existing)
                {
                serversDiv.appendChild(serversInnerDiv);
                }
            else
                {
                serversDiv.replaceChild(serversInnerDiv, existing);
                }
            
            Servers.scheduleCall();
            };
            
            
        var errorHandler = function(data, ioArgs)
            {
            console.error("Could not contact server: "+data);
            clearTimeout(Servers.timeoutId);
            CommonUtils.showError("Could not access servers: "+data);
            Servers.scheduleCall();
            };
        console.log("Making dojo bind call");
        
        var url = "/lastbamboo-server-site/api/serverMonitor";
        
        console.log("url is: "+url);
        
        var deferred = dojo.xhrGet(
            {
            url: url,
            load: loadHandler,
            error: errorHandler,
            handleAs: "json",
            timeout: 40000
            });
        deferred.addBoth(function()
            {
            Servers.lastReturned = true;
            });
        },
    
    appendServer : function(server, div)
        {
        console.log("Appending result for: "+server);
        var serverSpan = document.createElement("span");
        serverSpan.className = "serverSpan";
        var text = "<br>SERVER DATA:<br>";
        text += "<br>Server Address: ";
        text += server.socketAddress;
        text += "<br>Load average: "; 
        text += server.systemLoadAverage;
        text += "<br>Uptime: ";
        text += server.uptime;
        text += "<br>Peak Thread Count: ";
        text += server.peakThreadCount;
        text += "<br>Heap Memory Usage:<br>";
        text += server.heapMemoryUsage;
        text += "<br>Non Heap Memory Usage:<br>";
        text += server.nonHeapMemoryUsage;
        text += "<br>VM Version: ";
        text += server.vmVersion;
        text += "<br><br><br>SIP DATA:<br>";
        text += "<br>Users Registered: "+server.sipNumRegistered;
        text += "<br>Max Users Registered: "+server.sipMaxRegistered;
        text += "<br><br><br>TURN DATA:<br>";
        text += "<br>Current TURN Clients: ";
        text += server.numTurnClients;
        text += "<br>Maximum TURN Clients: ";
        text += server.maxNumTurnClients;
        text += "<br>Current Remote Hosts Connected to TURN Clients: ";
        text += server.numRemoteTurnClients;
        text += "<br>Maximum Remote Hosts Connected to TURN Clients: ";
        text += server.maxNumRemoteTurnClients;
        text += "<br>Maximum Remote Hosts Connected to a Single TURN Client: ";
        text += server.maxNumRemoteSingleTurnClient;
        
        console.log("Setting text: "+text);
        serverSpan.innerHTML = text;
        div.appendChild(serverSpan);
        

        },
    
    start : function()
        {
        console.log("Starting server...");
        Servers.check();
        },
    
    scheduleCall : function()
        {
        var refreshTime = 2000;
        Servers.timeoutId = setTimeout(Servers.check, refreshTime);
        }
    };
