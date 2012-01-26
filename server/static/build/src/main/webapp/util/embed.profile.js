dependencies =
    {
    layers:  
    [
        {
        name: "../littleshoot/littleshoot.js",
        dependencies: 
        [
        "littleshoot.AppMonitor",
        "littleshoot.ClientApi",
        "littleshoot.CommonUtils",
        "littleshoot.ClientApi",
        "littleshoot.HmacSha1",
        "littleshoot.Constants",
        "littleshoot.LittleShootLoader",
        "dojo.cookie",
        "dojo.io.script",
        "dojox.analytics.Urchin",
        ]
        },
    ],
    prefixes: 
    [
        [ "dijit", "../dijit" ],
        [ "dojox", "../dojox" ],
        [ "littleshoot", "../../littleshoot" ],
    ]
};
