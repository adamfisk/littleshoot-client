/* Start accordion navigation widget */
var linkMap = {};
var linkIndexMap = {};
var AccordionNav = {
    
    nav : function(linkData, contentId) {
        $(".contentBodySection").hide();
        
        if (linkData.type === "category") {
            $(".selectedAboutBody").each(function() {
                $(this).removeClass("selectedAboutBody");
                $(this).addClass("unSelectedAboutBody");
            });
        }
        
        var elem = dojo.byId(contentId);
        if (!elem) {
            console.error("No element with ID: "+contentId);
            return;
        }
        CommonUtils.showElement(elem);
        
        if (linkData.aboutContentId) {
            var aboutContent = dojo.byId(linkData.aboutContentId);
            dojo.removeClass(aboutContent, "unSelectedAboutBody");
            dojo.addClass(aboutContent, "selectedAboutBody");
        }
    
        var subNavTitleText;
        if (linkData.subTitle) {
            subNavTitleText = " - " +linkData.subTitle;
        }
        else if (CommonUtils.isEmpty(linkData.subTitle)) {
            // The data explicitly wants to exclude a subtitle.
            subNavTitleText = "";
        }
        else {
            subNavTitleText = " - " + linkData.name;
        }
        $(".contentTitleSub").html(subNavTitleText);
    
        var categoryText;
        if (linkData.category) {
            categoryText = linkData.category;
        }
        else {
            categoryText = "";
        }
        
        $(".contentTitleCategory").html(categoryText);
        },
    
    navBase : function(linkData)
        {
        //console.info("Loading: "+dojo.toJson(linkData));
        var contentId;
        if (dojo.isFunction(linkData.contentId))
            {
            linkData.contentId(linkData, function (linkData, contentId) {
                AccordionNav.nav(linkData, contentId);
            });
            }
        else
            {
            AccordionNav.nav(linkData, linkData.contentId);
            }
        }
    };

/**
 * Specialized for the download window.
 * 
 * @param linkData The data for the download link.
 * @param callbackFunc The callback function to call.
 */
function onDownloadContent(linkData, callbackFunc) {
    if (!dojo.isFunction(callbackFunc)) {
        console.error("Not a function: "+callbackFunc);
        return;
    }
    
    var htmlPage = "downloadContent";
    var sectionId = "downloadContentBodySection";
    var contentId = "downloadWrapperDiv";
    /*
    if(/Windows/.test(navigator.userAgent)) {
        htmlPage = "downloadWinContent";
        contentId = "downloadWin";
    }
    else if (/Mac/.test(navigator.userAgent)) {
        htmlPage = "downloadMacContent";
        contentId = "downloadMac";
    }
    else {
        htmlPage = "downloadOtherContent";
        contentId = "downloadOther";
    }
    */
    
    var onLoaded = function(contentDiv) {
        if (!contentDiv) {
            console.error("Could not access div with ID: "+contentId);
        }
        else {
            CommonUtils.showElement(contentDiv);
            $(".freeLink").click(function(evt) {
                evt.stopPropagation();
                evt.preventDefault();
                CommonUtils.downloadInstaller();
            });
            $(".proLink").click(function(evt) {
                evt.stopPropagation();
                evt.preventDefault();
                Pro.buy();
            });
        }
        callbackFunc(linkData, sectionId);
        //callbackFunc(linkData, contentId + "ContentBodySection");
        //CommonUtils.showDownloadConfirmDialog();
    };
    
    var contentDiv = dojo.byId(contentId);
    if (contentDiv) {
        // We've already loaded the download page.
        onLoaded(contentDiv);
    }
    else {
        CommonUtils.hideAll(".contentBodySection");
        CommonUtils.showSpinner();
        $("#downloadContentBody").load(htmlPage, function () {
            onLoaded(dojo.byId(contentId));
            CommonUtils.hideSpinner();
        });
    }
}

function loadContentInto(parentId, linkData, callbackFunc, contentId, url) {
    if (!dojo.isFunction(callbackFunc)) {
        console.error("Not a function: "+callbackFunc);
        return;
    }
    
    var onLoaded = function(contentDiv) {
        if (!contentDiv) {
            console.error("Could not access div with ID: "+contentId);
        }
        else {
            CommonUtils.showElement(contentDiv);
            callbackFunc(linkData, contentId);
        }
    };
    
    var contentDiv = dojo.byId(contentId);
    if (contentDiv) {
        // We've already loaded the download page.
        onLoaded(contentDiv);
    }
    else {
        $(".contentBodySection").hide();
        CommonUtils.showSpinner();
        // Configuration by convention.
        $(parentId).load(url, function () {
            onLoaded(dojo.byId(contentId));
            CommonUtils.hideSpinner();
        });
    }
}

function loadContent(linkData, callbackFunc, contentId) {
    //console.info("Loading content!!");
    var parentId = "#"+contentId + "ContentBody";
    var url = contentId +"Content";
    loadContentInto(parentId, linkData, callbackFunc, contentId, url);
}

var navData = {
    categories: [
        { id: 'navHome', name:'Home', category: 'Home', subTitle:'', type: 'category', contentId:"home", aboutContentId: 'homeContentBody',
             links: []
        },
        { id:'navWhatIs', name:'What Is LittleShoot?', category: 'What Is LittleShoot?', subTitle:'The Big Idea', type: 'category', contentId:function (linkData, callback) {return loadContent(linkData, callback, "whatIs");}, aboutContentId: 'whatIsContentBody',
            links: [
                { name:'Search', category: 'What Is LittleShoot?', contentId:'whatIsSearch', aboutContentId: 'whatIsContentBody'}, 
                { name:'Downloading', category: 'What Is LittleShoot?', contentId:'whatIsDownload', aboutContentId: 'whatIsContentBody' },
                { name:'Publishing', category: 'What Is LittleShoot?', contentId:'whatIsPublish', aboutContentId: 'whatIsContentBody' },
                { name:'Privacy', category: 'What Is LittleShoot?', contentId:'whatIsPrivacy', aboutContentId: 'whatIsContentBody' },
                { name:'Security', category: 'What Is LittleShoot?', contentId:'whatIsSecurity', aboutContentId: 'whatIsContentBody' }                    
            ]
        },
        { id:'navTechnology', name:'Technology', category: 'Technology', subTitle:'Introduction', type: 'category', contentId:function (linkData, callback) {return loadContent(linkData, callback, "technology");}, aboutContentId: 'technologyContentBody',
            links: [
                { name:'FAQ', category: 'Technology', contentId:'technologyFaq', aboutContentId: 'technologyContentBody'}, 
                { name:'Open Platform', category: 'Technology', contentId:'technologyOpenPlatform', aboutContentId: 'technologyContentBody'}, 
                { name:'Open Source', category: 'Technology', contentId:'technologyOpenSource', aboutContentId: 'technologyContentBody'},
                { name:'Open Data', category: 'Technology', contentId:'technologyOpenData', aboutContentId: 'technologyContentBody'},
                { name:'Security', category: 'Technology', contentId:'technologySecurity', aboutContentId: 'technologyContentBody'},
                
                { name:'Architecture', category: 'Technology', type: 'category', contentId:"technologyArchOverview", aboutContentId: 'technologyContentBody'},
                { name:'Search', category: 'Technology', contentId:'technologyArchSearch', aboutContentId: 'technologyContentBody'}, 
                { name:'Downloading', category: 'Technology', contentId:'technologyArchDownloading', aboutContentId: 'technologyContentBody'},
                { name:'Publishing', category: 'Technology', contentId:'technologyArchPublishing', aboutContentId: 'technologyContentBody'},
                { name:'NAT/Firewall', category: 'Technology', contentId:'technologyArchNatFirewall', aboutContentId: 'technologyContentBody'},
                { name:'Reliable UDP', category: 'Technology', contentId:'technologyArchRudp', aboutContentId: 'technologyContentBody'}
            ]
        },
        { id:'navHelp', name:'Help', category: 'Help', subTitle:'FAQ', type: 'category', contentId:function (linkData, callback) {return loadContent(linkData, callback, "help");}, aboutContentId: 'helpContentBody',
            links: [
                { name:'Searching', category: 'Help', contentId:'helpSearching' },
                { name:'Downloading', category: 'Help', contentId:'helpDownloading' },
                { name:'Publishing', category: 'Help', contentId:'helpPublishing' }
            ]
        },
        { id: 'navTeam', name:'The Team', category: 'The Team', subTitle:'', type: 'category', contentId : function (linkData, callback) {return loadContent(linkData, callback, "team");}, aboutContentId: 'teamContentBody',
            links: []
        },
        { id: 'navContact', name:'Contact Us', category: 'Contact', subTitle:'', type: 'category', contentId : function (linkData, callback) {return loadContent(linkData, callback, "contact");}, aboutContentId: 'contactContentBody',
            links: []
        },
        { id: 'navDownload', name:'Download LittleShoot', category: 'Download', subTitle:'', type: 'category', contentId : function (linkData, callback) {return onDownloadContent(linkData, callback);}, aboutContentId: 'downloadContentBody',
            links: []
        },
        { id: 'navDevelopers', name:'Developers', category: 'Developers', subTitle:'Introduction', type: 'category', contentId : function (linkData, callback) {return loadContent(linkData, callback, "developers");}, aboutContentId: 'developersContentBody',
            links: [
                { name:'Get the Code', category: 'Developers', href:"code" },
                { name:'Mailing List', category: 'Developers', href:"http://groups.google.com/group/littleshoot-developers/topics"},
                { name:'JIRA Tasks', category: 'Developers', href:"http://dev.littleshoot.org:8081" },
                { name:'Bugs Reported', category: 'Developers', href:"http://1.latest.littleshootbugs.appspot.com/"}
            ]
        }
        /*
        { id: 'navBeta', name:'Beta', category: 'Beta', subTitle:'', type: 'category', contentId : function (linkData, callback) {window.location.href = "beta";}, aboutContentId: 'betaContentBody',
            links: []
        }
        */
    ]
    
    };

/*
 * 
 */
    
function connectLink(link, linkData, propagate) {
    $(link).click( function (evt) {
        if (!propagate) {
            evt.stopPropagation();
        }
        window.scrollTo(0,0);
        AccordionNav.navBase(linkData);
        
        $("#accordionNav .selectedNav").removeClass("selectedNav");
        var a = evt.target;
        $(a).addClass("selectedNav");
    });
}

function onCategoryClick(category) {
    window.scrollTo(0,0);
    AccordionNav.navBase(category);
    $("#accordionNav .selectedNav").removeClass("selectedNav");
}

function createCategory(category, categoryIndex) {
    var elem = $("#"+category.id);
    if (elem.size() === 0) {
        console.error("No element matching: "+ category.id);
        return;
    }
    linkMap[category.id] = category;
    linkIndexMap[category.id] = categoryIndex;
    
    elem.html(category.name).click(function (evt) {
        onCategoryClick(category);
    });

    var div = $("#"+category.id + "Div");
    
    if (div.size() === 0) {
        console.error("No div matching: "+ category.id + "Div");
        return;
    }
    
    //console.info("Building links for: "+dojo.toJson(category));
    for (var j = 0; j < category.links.length; j++) {
        var linkData = category.links[j];
        //console.info("Current: "+dojo.toJson(category.links[j]));
        //var link = document.createElement("a");
        var divLinkWrapper = document.createElement("div");
        var link = document.createElement("a");
        link.innerHTML = linkData.name;
        if (linkData.href !== undefined) {
            link.href = linkData.href;
        }
        else {
            connectLink(link, linkData);
        }
        divLinkWrapper.appendChild(link);
        div.append(divLinkWrapper);
        
        if (linkData.id !== undefined) {
            linkMap[linkData.id] = linkData;
            linkIndexMap[linkData.id] = categoryIndex;
        }
    }
}


//We need both jquery and dojo to be loaded for the accordion.
jQuery().ready(function() {
    dojo.addOnLoad(function() {
        //console.info("Loading accordion nav with jquery...");
        if ($("#accordionNav").size() === 0) {
            //console.info("Looks like we're on a page without an accordion");
            return;
        }
        for (var i = 0; i < navData.categories.length; i++) {
            var category = navData.categories[i];
            createCategory(category, i);
        }
        
        var developers = linkMap.navDevelopers;
        var developersIndex = linkIndexMap.navDevelopers;
        $(".openSourceLink").click(function(evt) {
            //console.info("Activating accordion index...");
            $("#navDevelopers").trigger("click");
            onCategoryClick(developers);
        });
   
        var curBannerIndex = 0;
        var bannerText = 
            [
            //"LittleShoot will now handle all of your <strong>BitTorrent downloads</strong> right in the browser!",
            "#nyTimesBanner",
            "#mashableBanner",
            "#lifehackerBanner",
            "#cnetBanner"
            ];
        
        var bannerLogos = 
            [
            //"LittleShoot will now handle all of your <strong>BitTorrent downloads</strong> right in the browser!",
            '<img src="images/nytlogo260x43.gif"/>',
            '<img src="images/mashable_logo_200_55.png"/>',
            '<img src="images/lifehacker_logo_321_55.png"/>',
            '<img src="images/cnet_logo.png"/>'
            ];

        var swapBanner = function () {
            $("#homeBanner").fadeOut("slow", function () {
                var bannerLogoHtml = bannerLogos[curBannerIndex];
                var bannerSelector = bannerText[curBannerIndex];
                var bannerLogoSelector = bannerSelector + "Logo";
                var bannerLogo = $(bannerLogoSelector);
                var banner = $(bannerSelector);
                
                bannerLogo.html(bannerLogoHtml);
                $(this).html(banner.html()).fadeIn("slow");

                curBannerIndex++;
                if (curBannerIndex === bannerText.length) {
                    curBannerIndex = 0;
                }
            });
        };
        swapBanner();
        setInterval(swapBanner, 14000);
        $(".freeLink").click(function(evt) {
            evt.stopPropagation();
            evt.preventDefault();
            CommonUtils.downloadInstaller();
        });
        $(".proLink").click(function(evt) {
            evt.stopPropagation();
            evt.preventDefault();
            Pro.buy();
        });
    });
});