/* Yahoo! Media Player, Minified Build 2.0.31.  Copyright (c) 2008, Yahoo! Inc.  All rights reserved.
 * Your use of this Yahoo! Media Player is subject to the Yahoo! Terms of Service
 * located at http://info.yahoo.com/legal/us/yahoo/utos/utos-173.html.
 */

YAHOO.mediaplayer.goosecss = "\r\n/* Reset CSS */  \r\n#ymp-player div, #ymp-tray div{display:block;}\r\n#ymp-player div, #ymp-player ul, #ymp-player li, #ymp-player textarea, #ymp-player p, #ymp-player td,\r\n#ymp-tray div, #ymp-tray ul, #ymp-tray li, #ymp-tray textarea, #ymp-tray p{margin:0;padding:0;}\r\n#ymp-player img, #ymp-tray img{border:0;}\r\n#ymp-player em, #ymp-player strong,\r\n#ymp-tray em, #ymp-tray strong{font-style:normal;font-weight:normal;}\r\n#ymp-player li, #ymp-tray li{list-style:none;}\r\n#ymp-player sup, #ymp-tray sup{vertical-align:text-top;}\r\n#ymp-player sub, #ymp-tray sub{vertical-align:text-bottom;}\r\n#ymp-player input, #ymp-player textarea, #ymp-player select,\r\n#ymp-tray input, #ymp-tray textarea, #ymp-tray select{font-family:inherit;font-size:inherit;font-weight:inherit;*font-size:100%;}                                                      \r\n#ymp-player table{border-collapse:collapse;border-spacing:0;}\r\n\r\n/* Player Body */\r\n#ymp-player\r\n{\r\n /* Hide the player initially, show it only after it's properly positioned */\r\n /*display:none;*/\r\n position:fixed;\r\n overflow:hidden;\r\n bottom:10px;\r\n left:0;          \r\n width:33px; /* Initial width of the player */\r\n margin:0;\r\n padding:0;\r\n z-index:9999;\r\n color:#595959;\r\n text-align:left;\r\n}\r\n#ymp-body\r\n{\r\n position:relative;\r\n overflow:hidden;\r\n margin:0;\r\n padding:0;\r\n height:71px; /* +10px for shadow */\r\n background-position:100% -313px;\r\n font-family:Arial, Helvetica;\r\n font-size:12px;\r\n}\r\n#ymp-body h1, #ymp-tray h1,\r\n#ymp-body h2, #ymp-tray h2, \r\n#ymp-body h3, #ymp-tray h3, \r\n#ymp-body h4, #ymp-tray h4, \r\n#ymp-body h5, #ymp-tray h5,\r\n#ymp-body h6 #ymp-tray h6\r\n{\r\n position:absolute;\r\n left:-10000px;\r\n height:0;\r\n width:0;\r\n}\r\n#ymp-body a, #ymp-tray a\r\n{              \r\n margin:0;\r\n padding:0;\r\n outline:none;\r\n text-decoration:none;\r\n font-family:Arial, Helvetica;\r\n border:none;\r\n}\r\n#ymp-body a span, #ymp-tray a span\r\n{               \r\n margin:0;\r\n padding:0;\r\n border:none;\r\n}\r\n#ymp-body a:hover\r\n{\r\n text-decoration:underline;\r\n}\r\n.ymp-player-min #ymp-body\r\n{\r\n width:33px; /* +10px for shadow */\r\n background-position:-655px -313px;\r\n}\r\n.ymp-player-max #ymp-body\r\n{\r\n width:690px; /* +10px for shadow */\r\n}\r\n#ymp-body #ymp-body-base\r\n{                       \r\n position:relative;\r\n width:680px;\r\n height:62px;\r\n opacity:.9;\r\n -moz-opacity:.9;\r\n z-index:1;\r\n}\r\n#ymp-body #ymp-body-strip\r\n{                    \r\n width:677px;\r\n height:100%;\r\n filter:alpha(opacity=90);\r\n}                             \r\n#ymp-body #ymp-body-cap\r\n{                            \r\n position:absolute;\r\n top:0;\r\n right:0;\r\n width:4px; /* IE7 Quirks mode does not play well with position:absolute; and right:0; with odd width */\r\n height:100%;\r\n filter:alpha(opacity=90); \r\n}\r\n.ymp-player-min #ymp-body #ymp-body-base\r\n{\r\n width:24px;\r\n}\r\n.ymp-player-min #ymp-body #ymp-body-strip\r\n{\r\n width:21px;\r\n}\r\n#ymp-body #ymp-meta\r\n{            \r\n position:absolute;           \r\n top:4px;\r\n left:180px;\r\n width:344px;\r\n height:54px;\r\n background-position:-180px 0;\r\n z-index:2;\r\n}\r\n#ymp-body #ymp-meta-bottom-right\r\n{\r\n position:absolute;\r\n bottom:5px;\r\n right:5px;\r\n}                            \r\n#ymp-body #ymp-meta-top, #ymp-body #ymp-meta-bottom\r\n{                     \r\n display:table;\r\n position:absolute;\r\n top:5px;\r\n left:58px;\r\n width:280px;\r\n}                     \r\n#ymp-body #ymp-meta-bottom\r\n{             \r\n top:auto;\r\n bottom:5px;\r\n}\r\n#ymp-body #ymp-meta-bottom td\r\n{\r\n height:16px;\r\n vertical-align:bottom;   \r\n font-size:11px;\r\n}\r\n#ymp-body #ymp-meta-top td\r\n{\r\n height:32px;\r\n vertical-align:top;\r\n}\r\n#ymp-body #ymp-meta-progress\r\n{                            \r\n position:relative;\r\n top:2px;\r\n white-space:nowrap;\r\n font-size:11px; \r\n line-height:16px;        \r\n margin:0 5px 0 0;\r\n}\r\n#ymp-body #ymp-stickwall\r\n{\r\n display:none;\r\n position:absolute;\r\n top:0;\r\n left:0;\r\n width:344px;\r\n height:54px;                        \r\n z-index:3;\r\n background-position:-179px -199px;\r\n}\r\n#ymp-body .ymp-stickwall-gradient\r\n{                       \r\n position:absolute;\r\n top:0;\r\n left:0;\r\n width:100%;\r\n height:100%;\r\n background-position:-180px -54px;\r\n}          \r\n#ymp-body #ymp-yahoo-logo\r\n{\r\n position:absolute;\r\n top:22px;\r\n left:11px;                       \r\n width:28px;\r\n height:17px;\r\n background-position:-120px -62px;\r\n z-index:2;\r\n}\r\n  \r\n/* Pixels for rounded corners */         \r\n#ymp-body .ymp-pix-dark, #ymp-tray .ymp-pix-dark,\r\n#ymp-body .ymp-pix-light, #ymp-tray .ymp-pix-light\r\n{             \r\n position:absolute;\r\n display:block;               \r\n overflow:hidden;\r\n width:1px;\r\n height:1px;  \r\n}                                    \r\n#ymp-body .ymp-pix-light, #ymp-tray .ymp-pix-light\r\n{\r\n opacity:.19;\r\n -moz-opacity:.19;\r\n filter:alpha(opacity=19);\r\n}\r\n#ymp-body .ymp-pix-dark, #ymp-tray .ymp-pix-dark\r\n{\r\n opacity:.69;\r\n -moz-opacity:.69;\r\n filter:alpha(opacity=69);\r\n} \r\n#ymp-body .ymp-pix-tr1, #ymp-tray .ymp-pix-tr1\r\n{\r\n top:0;\r\n right:2px;\r\n}         \r\n#ymp-body .ymp-pix-tr2, #ymp-tray .ymp-pix-tr2\r\n{\r\n top:0;\r\n right:1px;\r\n}\r\n#ymp-body .ymp-pix-tr3, #ymp-tray .ymp-pix-tr3\r\n{\r\n top:1px;\r\n right:0;                          \r\n}           \r\n#ymp-body .ymp-pix-tr4, #ymp-tray .ymp-pix-tr4\r\n{\r\n top:2px;\r\n right:0;                              \r\n}\r\n#ymp-body .ymp-pix-tl1, #ymp-tray .ymp-pix-tl1\r\n{\r\n top:2px;\r\n left:0;\r\n}         \r\n#ymp-body .ymp-pix-tl2, #ymp-tray .ymp-pix-tl2\r\n{\r\n top:1px;\r\n left:0;\r\n}\r\n#ymp-body .ymp-pix-tl3, #ymp-tray .ymp-pix-tl3\r\n{\r\n top:0;\r\n left:1px;\r\n}           \r\n#ymp-body .ymp-pix-tl4, #ymp-tray .ymp-pix-tl4\r\n{\r\n top:0;\r\n left:2px;\r\n}\r\n#ymp-body .ymp-pix-br1, #ymp-tray .ymp-pix-br1\r\n{\r\n bottom:0;\r\n right:2px;\r\n}         \r\n#ymp-body .ymp-pix-br2, #ymp-tray .ymp-pix-br2\r\n{\r\n bottom:0;\r\n right:1px;                                \r\n}\r\n#ymp-body .ymp-pix-br3, #ymp-tray .ymp-pix-br3\r\n{\r\n bottom:1px;\r\n right:0;                                  \r\n}           \r\n#ymp-body .ymp-pix-br4, #ymp-tray .ymp-pix-br4\r\n{\r\n bottom:2px;\r\n right:0;                                  \r\n}           \r\n#ymp-body .ymp-cap-body1,\r\n#ymp-body .ymp-cap-body2,\r\n#ymp-body .ymp-stickwall-body1,\r\n#ymp-body .ymp-stickwall-body2,\r\n#ymp-body .ymp-stickwall-body3,\r\n#ymp-body .ymp-stickwall-body4,\r\n#ymp-body .ymp-stickwall-body5\r\n{                            \r\n position:absolute;\r\n overflow:hidden;\r\n}                    \r\n#ymp-body .ymp-cap-body1\r\n{\r\n top:1px;\r\n right:1px;\r\n width:2px;\r\n height:60px;   \r\n}               \r\n#ymp-body .ymp-cap-body2\r\n{\r\n top:3px;\r\n right:0;\r\n width:1px;\r\n height:56px;           \r\n}\r\n#ymp-body .ymp-stickwall-body1\r\n{          \r\n top:2px;\r\n left:0;\r\n width:1px;\r\n height:50px;\r\n}\r\n#ymp-body .ymp-stickwall-body2\r\n{\r\n top:1px;\r\n left:1px;\r\n width:1px;\r\n height:52px;\r\n}\r\n#ymp-body .ymp-stickwall-body3\r\n{\r\n top:1px;\r\n right:1px;\r\n width:1px;\r\n height:52px;\r\n}\r\n#ymp-body .ymp-stickwall-body4\r\n{\r\n top:2px;\r\n right:0;\r\n width:1px;\r\n height:50px;\r\n}\r\n#ymp-body .ymp-stickwall-body5\r\n{\r\n top:0;\r\n left:2px;\r\n width:340px;\r\n height:100%;\r\n}\r\n#ymp-body #ymp-rhap-stickwall, #ymp-body #ymp-error-stickwall\r\n{\r\n display:none;\r\n}               \r\n#ymp-body .ymp-rhap-stickwall .ymp-stickwall-body1,\r\n#ymp-body .ymp-rhap-stickwall .ymp-stickwall-body2, \r\n#ymp-body .ymp-rhap-stickwall .ymp-stickwall-body3,\r\n#ymp-body .ymp-rhap-stickwall .ymp-stickwall-body4,\r\n#ymp-body .ymp-rhap-stickwall .ymp-stickwall-body5,\r\n#ymp-body .ymp-error-stickwall .ymp-stickwall-body1,\r\n#ymp-body .ymp-error-stickwall .ymp-stickwall-body2, \r\n#ymp-body .ymp-error-stickwall .ymp-stickwall-body3,\r\n#ymp-body .ymp-error-stickwall .ymp-stickwall-body4,\r\n#ymp-body .ymp-error-stickwall .ymp-stickwall-body5\r\n{\r\n background-color:#6CABD2;\r\n}\r\n\r\n#ymp-body .ymp-error-stickwall h2\r\n{\r\n position:absolute;\r\n top:12px;\r\n left:11px;\r\n width:295px;\r\n margin:0;\r\n padding:0 0 0 34px;\r\n font-size:11px;\r\n line-height:14px;\r\n color:#444;\r\n}\r\n#ymp-body .ymp-error-stickwall span.ymp-skin\r\n{\r\n display:block;\r\n position:absolute;\r\n top:5px;\r\n left:0;\r\n width:24px;\r\n height:20px;\r\n background-position:-524px 0;\r\n}\r\n#ymp-body .ymp-error-stickwall a\r\n{\r\n color:#444;\r\n text-decoration:underline;\r\n}\r\n      \r\n/* Themeable styles */\r\n.ymp-color-main\r\n{\r\n background-color:#BFBFBF; /* #B2B2B2 */\r\n}\r\n.ymp-color-tray\r\n{\r\n background-color:#8D8E8D; /* #8D8E8D */\r\n}                   \r\n.ymp-color-text-main, .ymp-color-text-main:link\r\n{\r\n color:#595959;\r\n}\r\n.ymp-color-text-tray, .ymp-color-text-tray:link\r\n{\r\n color:#FFF;\r\n}\r\n.ymp-skin\r\n{\r\n background-image:url(http://l.yimg.com/us.yimg.com/i/us/mus/ymwp/mediaplayer-2.0.31.png);\r\n -background-image:url(http://l.yimg.com/us.yimg.com/i/us/mus/ymwp/mediaplayer-2.0.31.gif);\r\n background-repeat:no-repeat;\r\n}\r\n\r\n/* Override YUI style */\r\n.show-scrollbars\r\n{\r\n overflow:hidden;\r\n}\r\n\r\n/* Playlist Tray */\r\n#ymp-tray\r\n{   \r\n /* Hide it initially */         \r\n display:none; \r\n position:fixed;\r\n overflow:hidden;\r\n bottom:81px;\r\n left:180px;   \r\n width:344px;\r\n height:18px;\r\n margin:0;\r\n padding:0;\r\n z-index:9998;\r\n font-family:Arial, Helvetica;\r\n font-size:11px;         \r\n text-align:left;\r\n}\r\n#ymp-tray-body\r\n{\r\n position:relative;\r\n width:100%; \r\n height:100%;\r\n background-position:-180px -108px; \r\n opacity:.95;\r\n -moz-opacity:.95;\r\n filter:alpha(opacity=95);\r\n}\r\n#ymp-tray-top\r\n{\r\n position:absolute;\r\n overflow:hidden;\r\n top:18px;\r\n left:0;\r\n width:100%;\r\n height:6px;\r\n}              \r\n.ymp-tray-body1\r\n{             \r\n display:block;\r\n overflow:hidden;\r\n margin:0 0 0 3px;\r\n width:100%;\r\n height:1px;\r\n}\r\n.ymp-tray-body2\r\n{             \r\n display:block;  \r\n overflow:hidden;\r\n margin:0 0 0 1px;\r\n width:100%;\r\n height:2px;\r\n}         \r\n.ymp-tray-body3\r\n{             \r\n display:block;\r\n width:100%;\r\n height:100%;\r\n}\r\n#ymp-tray ul#ymp-tray-list\r\n{\r\n position:absolute;\r\n top:24px;\r\n left:0;\r\n width:100%;\r\n height:180px;\r\n margin:0; \r\n padding:0;\r\n overflow-x:hidden;\r\n overflow-y:hidden; /* this gets switched to auto via JS */\r\n}\r\n#ymp-tray ul li\r\n{                         \r\n position:relative;\r\n overflow:hidden;\r\n color:#FFF;\r\n}\r\n#ymp-tray ul li a\r\n{\r\n display:block;\r\n overflow:hidden;      \r\n white-space:nowrap;\r\n width:100%;\r\n margin:0;\r\n padding:0 0 0 15px;\r\n text-decoration:none;\r\n line-height:20px;\r\n font-size:11px;\r\n text-overflow:ellipsis;\r\n}\r\n#ymp-tray ul li a:hover,\r\n#ymp-tray ul li a.ymp-tray-track-focus\r\n{\r\n background-color:#9E9E9E;\r\n color:#000;\r\n}\r\n#ymp-tray ul li a.playing\r\n{\r\n background-color:#889B64;\r\n}\r\n#ymp-tray ul.ymp-nested-list\r\n{                  \r\n position:relative;\r\n width:100%;\r\n height:auto;\r\n overflow:hidden;\r\n}\r\n#ymp-tray ul.ymp-nested-list a.ymp-tray-track\r\n{\r\n padding-left:44px;\r\n}\r\n#ymp-tray #ymp-btn-tray\r\n{\r\n position:absolute;\r\n display:block;\r\n top:0;\r\n right:0;\r\n height:18px;     \r\n padding:0 3px 0 3px;\r\n color:#FFF;                  \r\n text-decoration:none;\r\n font-family:Arial, Helvetica;\r\n font-size:9px;\r\n font-weight:bold;\r\n text-transform:uppercase;\r\n cursor:pointer; \r\n}\r\n#ymp-tray #ymp-btn-tray em\r\n{\r\n display:block;\r\n position:relative;\r\n overflow:hidden;\r\n float:left; /* For IE */\r\n height:18px;   \r\n line-height:9px;\r\n -line-height:8px; \r\n padding:4px 15px 0 5px;\r\n font-style:normal;\r\n font-weight:bold;\r\n}   \r\n#ymp-tray .ymp-up-arrow,\r\n#ymp-tray .ymp-down-arrow,\r\n#ymp-tray .ymp-right-arrow\r\n{          \r\n display:block;\r\n position:absolute;\r\n overflow:hidden;\r\n top:5px;\r\n right:6px;     \r\n width:7px;\r\n height:6px;\r\n background-position:-135px -80px;\r\n z-index:2;\r\n}                                   \r\n#ymp-tray .ymp-up-arrow\r\n{\r\n background-position:-135px -86px;\r\n}\r\n#ymp-tray .ymp-right-arrow\r\n{                      \r\n width:6px;\r\n height:7px;\r\n background-position:-135px -92px;\r\n}\r\n#ymp-tray ul#ymp-tray-list .ymp-down-arrow, #ymp-tray ul#ymp-tray-list .ymp-right-arrow\r\n{\r\n top:7px;\r\n left:5px;                         \r\n right:auto;\r\n}\r\n.ymp-btn-tray-body1\r\n{           \r\n display:block;\r\n position:absolute;\r\n top:3px;\r\n left:0;\r\n width:1px;\r\n height:100%;   \r\n}\r\n.ymp-btn-tray-body2\r\n{           \r\n display:block;\r\n position:absolute;\r\n top:1px;\r\n left:1px;\r\n width:2px;\r\n height:100%;   \r\n}\r\n.ymp-btn-tray-body3\r\n{           \r\n display:block;\r\n position:absolute;\r\n top:3px;\r\n right:0;\r\n width:1px;\r\n height:100%;\r\n}\r\n.ymp-btn-tray-body4\r\n{           \r\n display:block;\r\n position:absolute;\r\n top:1px;\r\n right:1px;\r\n width:2px;\r\n height:100%;   \r\n}\r\n.ymp-pix-bl\r\n{\r\n bottom:0;\r\n left:-1px;\r\n}\r\n\r\n/* Buttons and Links */                           \r\n#ymp-body #ymp-control\r\n{\r\n position:absolute;\r\n top:13px;\r\n left:49px;\r\n width:104px;\r\n height:44px;\r\n z-index:2;\r\n background-position:-525px -113px;\r\n}\r\n#ymp-body a.ymp-btn-play, #ymp-body a.ymp-btn-pause\r\n{\r\n display:block;\r\n position:absolute;\r\n top:2px;\r\n left:32px;\r\n width:35px;\r\n height:33px;\r\n cursor:pointer;\r\n background-position:-50px 0;\r\n z-index:2;\r\n text-indent:-9000px;\r\n}\r\n#ymp-body a:hover.ymp-btn-play\r\n{\r\n background-position:-50px -34px;\r\n}\r\n#ymp-body a:active.ymp-btn-play\r\n{\r\n background-position:-50px -68px;\r\n}\r\n#ymp-body a.ymp-btn-play-disabled,\r\n#ymp-body a:hover.ymp-btn-play-disabled,\r\n#ymp-body a:active.ymp-btn-play-disabled\r\n{\r\n cursor:default;\r\n background-position:-50px -99px;\r\n}\r\n#ymp-body a.ymp-btn-pause\r\n{\r\n background-position:-84px 0;\r\n}\r\n#ymp-body a:hover.ymp-btn-pause\r\n{\r\n background-position:-84px -34px;\r\n}\r\n#ymp-body a:active.ymp-btn-pause\r\n{\r\n background-position:-84px -68px;\r\n}\r\n#ymp-body a.ymp-btn-pause-disabled,\r\n#ymp-body a:hover.ymp-btn-pause-disabled,\r\n#ymp-body a:active.ymp-btn-pause-disabled\r\n{\r\n cursor:default;\r\n background-position:-81px -99px;\r\n}\r\n#ymp-body a.ymp-btn-next, #ymp-body a.ymp-btn-prev\r\n{\r\n display:block;\r\n position:absolute;\r\n top:7px;\r\n left:72px;\r\n width:25px;\r\n height:25px;\r\n cursor:pointer;\r\n background-position:-25px 0;\r\n z-index:2;\r\n text-indent:-1000px;\r\n}\r\n#ymp-body a:hover.ymp-btn-next\r\n{\r\n background-position:-25px -25px;\r\n}\r\n#ymp-body a:active.ymp-btn-next\r\n{\r\n background-position:-25px -51px; \r\n}\r\n#ymp-body a.ymp-btn-next-disabled,\r\n#ymp-body a:hover.ymp-btn-next-disabled,\r\n#ymp-body a:active.ymp-btn-next-disabled\r\n{\r\n cursor:default;\r\n background-position:-25px -75px;\r\n}\r\n#ymp-body a.ymp-btn-prev\r\n{\r\n left:3px;\r\n background-position:0 0;\r\n}\r\n#ymp-body a:hover.ymp-btn-prev\r\n{\r\n background-position:0 -25px;\r\n}\r\n#ymp-body a:active.ymp-btn-prev\r\n{\r\n background-position:0 -51px;\r\n}\r\n#ymp-body a.ymp-btn-prev-disabled,\r\n#ymp-body a:hover.ymp-btn-prev-disabled,\r\n#ymp-body a:active.ymp-btn-prev-disabled\r\n{\r\n cursor:default;\r\n background-position:0 -75px;\r\n}\r\n#ymp-body #ymp-btn-min\r\n{\r\n display:block;\r\n overflow:hidden;\r\n position:absolute;\r\n top:0;\r\n right:10px;\r\n width:15px;\r\n height:62px;\r\n cursor:pointer;\r\n text-indent:-1000px;\r\n z-index:2;    \r\n background-position:-159px -35px;\r\n}\r\n#ymp-body #ymp-btn-max\r\n{\r\n display:block;\r\n overflow:hidden;\r\n position:absolute;\r\n top:0;\r\n left:0;\r\n width:24px;\r\n height:62px;\r\n cursor:pointer;\r\n text-indent:-1000px;\r\n z-index:2;                       \r\n}\r\n#ymp-body #ymp-btn-max span\r\n{\r\n display:block;\r\n overflow:hidden;\r\n position:absolute;\r\n top:23px;\r\n left:9px;\r\n width:8px;\r\n height:17px;\r\n background-position:-119px -80px;\r\n}\r\n#ymp-body #ymp-btn-max span.ymp-animarrow\r\n{\r\n background:url(http://l.yimg.com/us.yimg.com/i/us/mus/ymwp/mediaplayer-animarrow-2.0.31.gif) no-repeat 0 0;\r\n}\r\n.ymp-player-max #ymp-body #ymp-btn-max,\r\n.ymp-player-min #ymp-body #ymp-btn-min, .ymp-player-min #ymp-body #ymp-btn-close, .ymp-player-min #ymp-body #ymp-btn-pop,\r\n.ymp-player-hidden\r\n{\r\n display:none;\r\n}\r\n#ymp-body a#ymp-btn-close, #ymp-body a#ymp-btn-pop\r\n{\r\n display:block;\r\n overflow:hidden;\r\n position:absolute;\r\n top:2px;\r\n right:27px;\r\n width:14px;\r\n height:14px;\r\n cursor:pointer;\r\n text-indent:-1000px;\r\n z-index:2;    \r\n background-position:-609px 0;\r\n}\r\n#ymp-body a:hover#ymp-btn-close\r\n{\r\n background-position:-609px -14px;\r\n}   \r\n#ymp-body a:active#ymp-btn-close\r\n{\r\n background-position:-609px -28px;\r\n}\r\n#ymp-body a#ymp-btn-pop\r\n{\r\n top:18px;\r\n background-position:-595px 0;\r\n}\r\n#ymp-body a:hover#ymp-btn-pop\r\n{\r\n background-position:-595px -14px;\r\n}   \r\n#ymp-body a:active#ymp-btn-pop\r\n{\r\n background-position:-595px -28px;\r\n}\r\n#ymp-body a#ymp-btn-target\r\n{\r\n display:block;\r\n float:right;\r\n width:16px;\r\n height:16px;\r\n cursor:pointer;\r\n z-index:2;\r\n text-indent:-9000px;\r\n background-position:-623px 0;\r\n}\r\n#ymp-body a:hover#ymp-btn-target\r\n{\r\n background-position:-623px -16px;\r\n}\r\n#ymp-body a:active#ymp-btn-target\r\n{\r\n background-position:-623px -32px;\r\n}\r\n#ymp-body .ymp-error-icon, #ymp-tray .ymp-error-icon\r\n{\r\n display:block;\r\n width:16px;\r\n height:16px;\r\n overflow: hidden;\r\n cursor:pointer;\r\n z-index:2;\r\n text-indent:-9000px;\r\n background-position:-150px 0;\r\n}\r\n#ymp-tray .ymp-error-icon\r\n{\r\n position:absolute;\r\n top:2px;\r\n right:3px;\r\n}\r\na.ymp-btn-page-play, a.ymp-btn-page-pause\r\n{\r\n position:relative !important;\r\n padding-left:20px !important;\r\n outline:none !important;\r\n}   \r\na.ymp-btn-page-play em.ymp-skin,\r\na.ymp-btn-page-pause em.ymp-skin\r\n{   \r\n display:block;\r\n position:absolute;           \r\n overflow:hidden;\r\n /*bottom:15%;*/       \r\n    top:15%;\r\n *top:0.22em;\r\n left:0;\r\n width:14px;\r\n height:14px;    \r\n background-position:-676px 0;\r\n cursor:pointer;\r\n}\r\na:hover.ymp-btn-page-play em.ymp-skin\r\n{\r\n background-position:-676px -14px;\r\n}\r\na:active.ymp-btn-page-play em.ymp-skin\r\n{\r\n background-position:-676px -28px;\r\n}\r\na.ymp-btn-page-pause em.ymp-skin\r\n{\r\n background-position:-662px 0;\r\n}\r\na:hover.ymp-btn-page-pause em.ymp-skin\r\n{\r\n background-position:-662px -14px;\r\n}\r\na:active.ymp-btn-page-pause em.ymp-skin\r\n{\r\n background-position:-662px -28px;\r\n}                    \r\na.ymp-btn-page-target em.ymp-skin,\r\na:hover.ymp-btn-page-target em.ymp-skin,\r\na:active.ymp-btn-page-target em.ymp-skin\r\n{\r\n background:url(http://l.yimg.com/us.yimg.com/i/us/mus/ymwp/mediaplayer-findlink-2.0.31.gif) no-repeat 0 0;\r\n}              \r\n#ymp-body a.ymp-btn\r\n{\r\n display:block;\r\n position:relative;\r\n height:16px;                     \r\n margin:0 0 0 7px;                \r\n padding:0 7px 0 0;\r\n z-index:2;    \r\n font-size:11px;\r\n line-height:16px;\r\n cursor:pointer;\r\n background-position:100% -48px;\r\n}\r\n#ymp-body a.ymp-btn em\r\n{\r\n display:block;     \r\n overflow:hidden;\r\n position:absolute;\r\n top:0;\r\n left:-7px;\r\n width:7px;\r\n height:100%;\r\n background-position:-525px -48px;\r\n} \r\n#ymp-body a:hover.ymp-btn\r\n{               \r\n text-decoration:none;\r\n background-position:100% -64px;\r\n}\r\n#ymp-body a:hover.ymp-btn em\r\n{\r\n background-position:-525px -64px;\r\n}\r\n#ymp-body a:active.ymp-btn\r\n{\r\n background-position:100% -80px;\r\n}\r\n#ymp-body a:active.ymp-btn em\r\n{\r\n background-position:-525px -80px;\r\n}   \r\n#ymp-body #ymp-btn-buy\r\n{\r\n margin-right:2px;\r\n}\r\n#ymp-body .ymp-icon-buy\r\n{\r\n display:block;\r\n position:relative;\r\n top:1px;\r\n left:-1px;\r\n width:17px;\r\n height:13px;       \r\n background-position:-142px -79px;\r\n}\r\n#ymp-body a.ymp-btn-alt\r\n{\r\n display:block;\r\n position:relative;\r\n height:16px;                     \r\n margin:0 0 0 7px;                \r\n padding:0 7px 0 0;\r\n z-index:2;    \r\n font-size:11px;\r\n font-weight:normal;\r\n color:#666;\r\n line-height:16px;\r\n cursor:pointer;\r\n background-position:100% -64px;\r\n}\r\n#ymp-body a.ymp-btn-alt em\r\n{\r\n display:block;     \r\n overflow:hidden;\r\n position:absolute;\r\n top:0;\r\n left:-7px;\r\n width:7px;\r\n height:100%;\r\n background-position:-525px -64px;\r\n}\r\n#ymp-body a:hover.ymp-btn-alt\r\n{                \r\n text-decoration:none;\r\n}                    \r\n#ymp-body .ymp-meta-box\r\n{\r\n position:relative;\r\n line-height:13px;\r\n}\r\n#ymp-body #ymp-meta-track-title, #ymp-body #ymp-meta-album-title, #ymp-body #ymp-meta-artist-title\r\n{                  \r\n display:block;\r\n overflow:hidden;          \r\n position:absolute; \r\n top:0;\r\n left:0;\r\n width:100%;\r\n font-size:12px;\r\n font-weight:bold;\r\n white-space:nowrap;\r\n text-overflow:ellipsis;\r\n -o-text-overflow:ellipsis; /* Opera 9 */ \r\n}\r\n#ymp-body #ymp-meta-album-title\r\n{\r\n top:1px;\r\n font-size:11px;\r\n font-weight:normal;\r\n}                                                               \r\n#ymp-body #ymp-meta-artist-title\r\n{\r\n top:18px;\r\n font-size:11px;\r\n font-weight:normal;\r\n}\r\n#ymp-body window \r\n{\r\n width:100%;\r\n -moz-user-focus:normal;\r\n -moz-user-select:text;\r\n}\r\n#ymp-body description \r\n{\r\n -moz-user-focus:normal;\r\n -moz-user-select:text;\r\n}                              \r\n#ymp-body #ymp-meta-image\r\n{         \r\n position:absolute;\r\n display:block;\r\n overflow:hidden;\r\n top:3px;\r\n left:2px;\r\n width:46px;\r\n height:46px;                                                                                                 \r\n border:1px solid #ADACAC;\r\n background:url(http://l.yimg.com/us.yimg.com/i/us/mus/ymwp/mediaplayer-default-album.gif) no-repeat 0 0;\r\n cursor:pointer;\r\n}                     \r\n/* Relevance section */ \r\n#ymp-body #ymp-relevance {\r\n position:absolute;\r\n left:527px;\r\n top:0;\r\n width:120px;\r\n height:62px;\r\n overflow:hidden;\r\n z-index:2;\r\n}\r\n#ymp-body #ymp-getplayer\r\n{\r\n display:block;\r\n position:absolute;\r\n top:18px;\r\n left:3px;\r\n width:121px;\r\n color:#3D9AD0;\r\n font-size:13px;\r\n line-height:14px;\r\n font-weight:bold;\r\n text-align:center;\r\n z-index:2;\r\n}\r\n#ymp-body #ymp-getlyrics,\r\n#ymp-body #ymp-watchvideo\r\n{\r\n display:none;\r\n position:absolute;\r\n top:16px;\r\n left:17px;\r\n color:#3D9AD0;\r\n font-size:13px;\r\n font-weight:bold;\r\n z-index:2;\r\n}\r\n#ymp-body #ymp-getlyrics em,\r\n#ymp-body #ymp-watchvideo em\r\n{\r\n font-size:11px;\r\n font-style:normal;\r\n font-weight:normal;\r\n color:#686868;\r\n}\r\n#ymp-body #ymp-getlyrics span,\r\n#ymp-body #ymp-watchvideo span\r\n{                            \r\n display:block;\r\n position:absolute;\r\n overflow:hidden;\r\n top:17px;\r\n left:63px;       \r\n width:14px;\r\n height:13px;\r\n background-position:-648px 0;\r\n cursor:pointer;\r\n}\r\n#ymp-body a#ymp-getlyrics:hover em,\r\n#ymp-body a#ymp-watchvideo:hover em\r\n{\r\n color:#3D9AD0;\r\n}\r\n#ymp-body a#ymp-getlyrics:hover span,\r\n#ymp-body a#ymp-watchvideo:hover span\r\n{\r\n background-position:-648px -13px;   \r\n}\r\n\r\n/* Volume Control */\r\n#ymp-body #ymp-volume\r\n{\r\n position:absolute;\r\n top:12px;\r\n left:158px;\r\n width:12px;\r\n height:39px;                     \r\n z-index:2;\r\n background-position:-125px -21px;\r\n}\r\n#ymp-body #ymp-volume-cover\r\n{\r\n position:absolute;\r\n overflow:hidden;\r\n bottom:0;\r\n left:0;\r\n width:100%;\r\n height:23px; /* (volume-cover span height) - (volume-thumb's top) */\r\n}\r\n#ymp-body #ymp-volume-cover span\r\n{\r\n display:block;\r\n position:absolute;\r\n bottom:0;\r\n left:0;\r\n width:100%;\r\n height:39px;\r\n background-position:-140px -21px;\r\n}\r\n#ymp-body a#ymp-volume-thumb\r\n{       \r\n display:block;      \r\n position:absolute;\r\n overflow:hidden;\r\n top:16px; /* Initial position of volume-thumb, this does not initialize the actual volume however */\r\n left:0;\r\n width:13px;\r\n height:7px;\r\n text-indent:-1000px;\r\n background-position:-119px 0;\r\n /* cursor:default; */\r\n}\r\n#ymp-body a:hover#ymp-volume-thumb\r\n{\r\n background-position:-119px -7px;\r\n}\r\n#ymp-body a:active#ymp-volume-thumb\r\n{                                   .\r\n background-position:-119px -14px;\r\n}\r\n\r\n/* Miscellaneous */\r\n#ymp-error-bubble\r\n{             \r\n display:none;\r\n position:absolute;\r\n top:0;\r\n left:0;              \r\n z-index:10000;\r\n width:200px;\r\n background-color:#FFF;\r\n border:1px solid #999;\r\n}\r\n#ymp-error-msg\r\n{\r\n margin:10px;\r\n font-size:11px;\r\n}                     \r\n.ymp-error-tail\r\n{\r\n display:block;\r\n position:absolute;\r\n bottom:-18px;\r\n left:0;\r\n width:21px;\r\n height:18px;\r\n background-position:-158px -17px;\r\n}\r\n#ymp-secret-bubble\r\n{             \r\n display:none;\r\n position:absolute;\r\n top:0;\r\n left:0;              \r\n z-index:10000;\r\n width:400px;\r\n background-color:#FFF;\r\n border:1px solid #999;\r\n}\r\n#ymp-secret-msg\r\n{\r\n margin:10px;\r\n font-size:11px;\r\n}\r\n#ymp-secret-msg #ymp-secret-msg-header\r\n{\r\n font-weight:bold;\r\n font-style:normal;\r\n text-align:center;\r\n position:relative;\r\n width:100%;\r\n}\r\n#ymp-secret-msg table\r\n{\r\n width:100%;\r\n}\r\n#ymp-secret-msg th, #ymp-secret-msg td\r\n{\r\n text-align:center;\r\n}\r\n#ymp-secret-bubble a#ymp-btn-close-secret\r\n{\r\n display:block;\r\n overflow:hidden;\r\n position:absolute;\r\n top:2px;\r\n right:2px;\r\n width:14px;\r\n height:14px;\r\n cursor:pointer;\r\n text-indent:-1000px;\r\n z-index:2;    \r\n background-position:-609px 0;\r\n}\r\n#ymp-secret-bubble a:hover#ymp-btn-close-secret\r\n{\r\n background-position:-609px -14px;\r\n}   \r\n#ymp-secret-bubble a:active#ymp-btn-close-secret\r\n{\r\n background-position:-609px -28px;\r\n}\r\n\r\n/* Rhapsody stuff */\r\n#ymp-body .ymp-rhap-powered\r\n{\r\n display:block;\r\n position:relative;\r\n overflow:hidden; \r\n top:-2px;\r\n width:107px;\r\n height:13px;\r\n margin:0 0 0 4px;\r\n text-indent:-9000px;\r\n background:url(http://l.yimg.com/us.yimg.com/i/us/mus/ymwp/mediaplayer-rhapsody-2.0.31.png) no-repeat 0 0;\r\n}\r\n#ymp-body .ymp-rhap-stickwall .ymp-rhap-powered\r\n{            \r\n position:absolute;\r\n top:3px;\r\n right:5px;\r\n margin:0;\r\n}\r\n#ymp-body #ymp-rhap-brand                                        \r\n{\r\n display:none;\r\n}\r\n#ymp-body .ymp-rhap-stickwall h2\r\n{                 \r\n position:absolute;\r\n top:6px;\r\n left:15px;\r\n margin:0;\r\n padding:0;\r\n font-size:14px;\r\n color:#58A9CF;\r\n width:100%;\r\n}                    \r\n#ymp-body #ymp-rhap-stickwall-action                                                      \r\n{\r\n position:absolute;\r\n bottom:6px;\r\n left:15px;\r\n}\r\n#ymp-body #ymp-rhap-stickwall-action .ymp-btn-alt\r\n{\r\n float:left;\r\n margin-right:5px;\r\n}\r\n#ymp-body #ymp-rhap-getunlimited\r\n{          \r\n display:none;\r\n position:absolute;\r\n top:11px;\r\n right:0;   \r\n max-height:14px;\r\n overflow:hidden;\r\n font-size:10px;   \r\n font-weight:normal;\r\n color:#3D9AD0;\r\n}      \r\n#ymp-body #ymp-rhap-playinfo\r\n{                 \r\n display:none;\r\n position:absolute;        \r\n top:14px;\r\n *top:8px;\r\n left:17px;\r\n width:92px;\r\n font-weight:bold; \r\n color:#686868; \r\n line-height:11px;\r\n *line-height:24px;\r\n text-align:right;\r\n z-index:2;\r\n}           \r\n#ymp-body #ymp-rhap-playinfo a\r\n{\r\n display:block;                   \r\n margin:0;\r\n padding:2px 0 0 0;\r\n *padding:1px 0 0 0;\r\n font-size:11px;   \r\n font-weight:normal;\r\n *line-height:13px;\r\n color:#3D9AD0;\r\n}\r\n#ymp-body #ymp-rhap-playinfo a em\r\n{               \r\n font-style:normal;\r\n font-size:14px;\r\n}    \r\n#ymp-body #ymp-rhap-playinfo a:hover\r\n{                       \r\n text-decoration:underline;\r\n}\r\n#ymp-body #ymp-rhap-playcount\r\n{   \r\n margin:0 5px 0 0;            \r\n font-size:30px;\r\n font-weight:bold;\r\n color:#FFF;\r\n}\r\n#ymwp-contplay-form {\r\n    height:0;\r\n    width:0;\r\n    margin:0;\r\n    padding:0;\r\n}\r\n";
if (YAHOO.mediaplayer.goosecss && YAHOO.mediaplayer.goosecss.length > 0) {
    var style = document.createElement("style");
    style.setAttribute("type", "text/css");
    if (style.styleSheet) {
        style.styleSheet.cssText = YAHOO.mediaplayer.goosecss;
    } else {
        var cssText = document.createTextNode(YAHOO.mediaplayer.goosecss);
        style.appendChild(cssText);
    }
    var headRef = document.getElementsByTagName('head')[0];
    headRef.appendChild(style);
    headRef = null;
    style = null;
} (function() {
    var B = YAHOO.ympyui.util;
    var A = function(D, C, E, F) {
        if (!D) {}
        this.init(D, C, E, F);
    };
    A.NAME = "Anim";
    A.prototype = {
        toString: function() {
            var C = this.getEl() || {};
            var D = C.id || C.tagName;
            return (this.constructor.NAME + ": " + D);
        },
        patterns: {
            noNegatives: /width|height|opacity|padding/i,
            offsetAttribute: /^((width|height)|(top|left))$/,
            defaultUnit: /width|height|top$|bottom$|left$|right$/i,
            offsetUnit: /\d+(em|%|en|ex|pt|in|cm|mm|pc)$/i
        },
        doMethod: function(C, E, D) {
            return this.method(this.currentFrame, E, D - E, this.totalFrames);
        },
        setAttribute: function(C, E, D) {
            if (this.patterns.noNegatives.test(C)) {
                E = (E > 0) ? E: 0;
            }
            B.Dom.setStyle(this.getEl(), C, E + D);
        },
        getAttribute: function(C) {
            var E = this.getEl();
            var G = B.Dom.getStyle(E, C);
            if (G !== "auto" && !this.patterns.offsetUnit.test(G)) {
                return parseFloat(G);
            }
            var D = this.patterns.offsetAttribute.exec(C) || [];
            var H = !!(D[3]);
            var F = !!(D[2]);
            if (F || (B.Dom.getStyle(E, "position") == "absolute" && H)) {
                G = E["offset" + D[0].charAt(0).toUpperCase() + D[0].substr(1)];
            } else {
                G = 0;
            }
            return G;
        },
        getDefaultUnit: function(C) {
            if (this.patterns.defaultUnit.test(C)) {
                return "px";
            }
            return "";
        },
        setRuntimeAttribute: function(D) {
            var I;
            var E;
            var F = this.attributes;
            this.runtimeAttributes[D] = {};
            var H = function(J) {
                return (typeof J !== "undefined");
            };
            if (!H(F[D]["to"]) && !H(F[D]["by"])) {
                return false;
            }
            I = (H(F[D]["from"])) ? F[D]["from"] : this.getAttribute(D);
            if (H(F[D]["to"])) {
                E = F[D]["to"];
            } else {
                if (H(F[D]["by"])) {
                    if (I.constructor == Array) {
                        E = [];
                        for (var G = 0,
                        C = I.length; G < C; ++G) {
                            E[G] = I[G] + F[D]["by"][G] * 1;
                        }
                    } else {
                        E = I + F[D]["by"] * 1;
                    }
                }
            }
            this.runtimeAttributes[D].start = I;
            this.runtimeAttributes[D].end = E;
            this.runtimeAttributes[D].unit = (H(F[D].unit)) ? F[D]["unit"] : this.getDefaultUnit(D);
            return true;
        },
        init: function(E, J, I, C) {
            var D = false;
            var F = null;
            var H = 0;
            E = B.Dom.get(E);
            this.attributes = J || {};
            this.duration = !YAHOO.ympyui.lang.isUndefined(I) ? I: 1;
            this.method = C || B.Easing.easeNone;
            this.useSeconds = true;
            this.currentFrame = 0;
            this.totalFrames = B.AnimMgr.fps;
            this.setEl = function(M) {
                E = B.Dom.get(M);
            };
            this.getEl = function() {
                return E;
            };
            this.isAnimated = function() {
                return D;
            };
            this.getStartTime = function() {
                return F;
            };
            this.runtimeAttributes = {};
            this.animate = function() {
                if (this.isAnimated()) {
                    return false;
                }
                this.currentFrame = 0;
                this.totalFrames = (this.useSeconds) ? Math.ceil(B.AnimMgr.fps * this.duration) : this.duration;
                if (this.duration === 0 && this.useSeconds) {
                    this.totalFrames = 1;
                }
                B.AnimMgr.registerElement(this);
                return true;
            };
            this.stop = function(M) {
                if (!this.isAnimated()) {
                    return false;
                }
                if (M) {
                    this.currentFrame = this.totalFrames;
                    this._onTween.fire();
                }
                B.AnimMgr.stop(this);
            };
            var L = function() {
                this.onStart.fire();
                this.runtimeAttributes = {};
                for (var M in this.attributes) {
                    this.setRuntimeAttribute(M);
                }
                D = true;
                H = 0;
                F = new Date();
            };
            var K = function() {
                var O = {
                    duration: new Date() - this.getStartTime(),
                    currentFrame: this.currentFrame
                };
                O.toString = function() {
                    return ("duration: " + O.duration + ", currentFrame: " + O.currentFrame);
                };
                this.onTween.fire(O);
                var N = this.runtimeAttributes;
                for (var M in N) {
                    this.setAttribute(M, this.doMethod(M, N[M].start, N[M].end), N[M].unit);
                }
                H += 1;
            };
            var G = function() {
                var M = (new Date() - F) / 1000;
                var N = {
                    duration: M,
                    frames: H,
                    fps: H / M
                };
                N.toString = function() {
                    return ("duration: " + N.duration + ", frames: " + N.frames + ", fps: " + N.fps);
                };
                D = false;
                H = 0;
                this.onComplete.fire(N);
            };
            this._onStart = new B.CustomEvent("_start", this, true);
            this.onStart = new B.CustomEvent("start", this);
            this.onTween = new B.CustomEvent("tween", this);
            this._onTween = new B.CustomEvent("_tween", this, true);
            this.onComplete = new B.CustomEvent("complete", this);
            this._onComplete = new B.CustomEvent("_complete", this, true);
            this._onStart.subscribe(L);
            this._onTween.subscribe(K);
            this._onComplete.subscribe(G);
        }
    };
    B.Anim = A;
})();
YAHOO.ympyui.util.AnimMgr = new
function() {
    var C = null;
    var B = [];
    var A = 0;
    this.fps = 1000;
    this.delay = 1;
    this.registerElement = function(F) {
        B[B.length] = F;
        A += 1;
        F._onStart.fire();
        this.start();
    };
    this.unRegister = function(G, F) {
        F = F || E(G);
        if (!G.isAnimated() || F == -1) {
            return false;
        }
        G._onComplete.fire();
        B.splice(F, 1);
        A -= 1;
        if (A <= 0) {
            this.stop();
        }
        return true;
    };
    this.start = function() {
        if (C === null) {
            C = setInterval(this.run, this.delay);
        }
    };
    this.stop = function(H) {
        if (!H) {
            clearInterval(C);
            for (var G = 0,
            F = B.length; G < F; ++G) {
                this.unRegister(B[0], 0);
            }
            B = [];
            C = null;
            A = 0;
        } else {
            this.unRegister(H);
        }
    };
    this.run = function() {
        for (var H = 0,
        F = B.length; H < F; ++H) {
            var G = B[H];
            if (!G || !G.isAnimated()) {
                continue;
            }
            if (G.currentFrame < G.totalFrames || G.totalFrames === null) {
                G.currentFrame += 1;
                if (G.useSeconds) {
                    D(G);
                }
                G._onTween.fire();
            } else {
                YAHOO.ympyui.util.AnimMgr.stop(G, H);
            }
        }
    };
    var E = function(H) {
        for (var G = 0,
        F = B.length; G < F; ++G) {
            if (B[G] == H) {
                return G;
            }
        }
        return - 1;
    };
    var D = function(G) {
        var J = G.totalFrames;
        var I = G.currentFrame;
        var H = (G.currentFrame * G.duration * 1000 / G.totalFrames);
        var F = (new Date() - G.getStartTime());
        var K = 0;
        if (F < G.duration * 1000) {
            K = Math.round((F / H - 1) * G.currentFrame);
        } else {
            K = J - (I + 1);
        }
        if (K > 0 && isFinite(K)) {
            if (G.currentFrame + K >= J) {
                K = J - (I + 1);
            }
            G.currentFrame += K;
        }
    };
};
YAHOO.ympyui.util.Bezier = new
function() {
    this.getPosition = function(E, D) {
        var F = E.length;
        var C = [];
        for (var B = 0; B < F; ++B) {
            C[B] = [E[B][0], E[B][1]];
        }
        for (var A = 1; A < F; ++A) {
            for (B = 0; B < F - A; ++B) {
                C[B][0] = (1 - D) * C[B][0] + D * C[parseInt(B + 1, 10)][0];
                C[B][1] = (1 - D) * C[B][1] + D * C[parseInt(B + 1, 10)][1];
            }
        }
        return [C[0][0], C[0][1]];
    };
}; (function() {
    var A = function(F, E, G, H) {
        A.superclass.constructor.call(this, F, E, G, H);
    };
    A.NAME = "ColorAnim";
    var C = YAHOO.ympyui.util;
    YAHOO.ympyui.extend(A, C.Anim);
    var D = A.superclass;
    var B = A.prototype;
    B.patterns.color = /color$/i;
    B.patterns.rgb = /^rgb\(([0-9]+)\s*,\s*([0-9]+)\s*,\s*([0-9]+)\)$/i;
    B.patterns.hex = /^#?([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})$/i;
    B.patterns.hex3 = /^#?([0-9A-F]{1})([0-9A-F]{1})([0-9A-F]{1})$/i;
    B.patterns.transparent = /^transparent|rgba\(0, 0, 0, 0\)$/;
    B.parseColor = function(E) {
        if (E.length == 3) {
            return E;
        }
        var F = this.patterns.hex.exec(E);
        if (F && F.length == 4) {
            return [parseInt(F[1], 16), parseInt(F[2], 16), parseInt(F[3], 16)];
        }
        F = this.patterns.rgb.exec(E);
        if (F && F.length == 4) {
            return [parseInt(F[1], 10), parseInt(F[2], 10), parseInt(F[3], 10)];
        }
        F = this.patterns.hex3.exec(E);
        if (F && F.length == 4) {
            return [parseInt(F[1] + F[1], 16), parseInt(F[2] + F[2], 16), parseInt(F[3] + F[3], 16)];
        }
        return null;
    };
    B.getAttribute = function(E) {
        var G = this.getEl();
        if (this.patterns.color.test(E)) {
            var H = YAHOO.ympyui.util.Dom.getStyle(G, E);
            if (this.patterns.transparent.test(H)) {
                var F = G.parentNode;
                H = C.Dom.getStyle(F, E);
                while (F && this.patterns.transparent.test(H)) {
                    F = F.parentNode;
                    H = C.Dom.getStyle(F, E);
                    if (F.tagName.toUpperCase() == "HTML") {
                        H = "#fff";
                    }
                }
            }
        } else {
            H = D.getAttribute.call(this, E);
        }
        return H;
    };
    B.doMethod = function(F, J, G) {
        var I;
        if (this.patterns.color.test(F)) {
            I = [];
            for (var H = 0,
            E = J.length; H < E; ++H) {
                I[H] = D.doMethod.call(this, F, J[H], G[H]);
            }
            I = "rgb(" + Math.floor(I[0]) + "," + Math.floor(I[1]) + "," + Math.floor(I[2]) + ")";
        } else {
            I = D.doMethod.call(this, F, J, G);
        }
        return I;
    };
    B.setRuntimeAttribute = function(F) {
        D.setRuntimeAttribute.call(this, F);
        if (this.patterns.color.test(F)) {
            var H = this.attributes;
            var J = this.parseColor(this.runtimeAttributes[F].start);
            var G = this.parseColor(this.runtimeAttributes[F].end);
            if (typeof H[F]["to"] === "undefined" && typeof H[F]["by"] !== "undefined") {
                G = this.parseColor(H[F].by);
                for (var I = 0,
                E = J.length; I < E; ++I) {
                    G[I] = J[I] + G[I];
                }
            }
            this.runtimeAttributes[F].start = J;
            this.runtimeAttributes[F].end = G;
        }
    };
    C.ColorAnim = A;
})();
YAHOO.ympyui.util.Easing = {
    easeNone: function(B, A, D, C) {
        return D * B / C + A;
    },
    easeIn: function(B, A, D, C) {
        return D * (B /= C) * B + A;
    },
    easeOut: function(B, A, D, C) {
        return - D * (B /= C) * (B - 2) + A;
    },
    easeBoth: function(B, A, D, C) {
        if ((B /= C / 2) < 1) {
            return D / 2 * B * B + A;
        }
        return - D / 2 * ((--B) * (B - 2) - 1) + A;
    },
    easeInStrong: function(B, A, D, C) {
        return D * (B /= C) * B * B * B + A;
    },
    easeOutStrong: function(B, A, D, C) {
        return - D * ((B = B / C - 1) * B * B * B - 1) + A;
    },
    easeBothStrong: function(B, A, D, C) {
        if ((B /= C / 2) < 1) {
            return D / 2 * B * B * B * B + A;
        }
        return - D / 2 * ((B -= 2) * B * B * B - 2) + A;
    },
    elasticIn: function(C, A, G, F, B, E) {
        if (C == 0) {
            return A;
        }
        if ((C /= F) == 1) {
            return A + G;
        }
        if (!E) {
            E = F * 0.3;
        }
        if (!B || B < Math.abs(G)) {
            B = G;
            var D = E / 4;
        } else {
            var D = E / (2 * Math.PI) * Math.asin(G / B);
        }
        return - (B * Math.pow(2, 10 * (C -= 1)) * Math.sin((C * F - D) * (2 * Math.PI) / E)) + A;
    },
    elasticOut: function(C, A, G, F, B, E) {
        if (C == 0) {
            return A;
        }
        if ((C /= F) == 1) {
            return A + G;
        }
        if (!E) {
            E = F * 0.3;
        }
        if (!B || B < Math.abs(G)) {
            B = G;
            var D = E / 4;
        } else {
            var D = E / (2 * Math.PI) * Math.asin(G / B);
        }
        return B * Math.pow(2, -10 * C) * Math.sin((C * F - D) * (2 * Math.PI) / E) + G + A;
    },
    elasticBoth: function(C, A, G, F, B, E) {
        if (C == 0) {
            return A;
        }
        if ((C /= F / 2) == 2) {
            return A + G;
        }
        if (!E) {
            E = F * (0.3 * 1.5);
        }
        if (!B || B < Math.abs(G)) {
            B = G;
            var D = E / 4;
        } else {
            var D = E / (2 * Math.PI) * Math.asin(G / B);
        }
        if (C < 1) {
            return - 0.5 * (B * Math.pow(2, 10 * (C -= 1)) * Math.sin((C * F - D) * (2 * Math.PI) / E)) + A;
        }
        return B * Math.pow(2, -10 * (C -= 1)) * Math.sin((C * F - D) * (2 * Math.PI) / E) * 0.5 + G + A;
    },
    backIn: function(B, A, E, D, C) {
        if (typeof C == "undefined") {
            C = 1.70158;
        }
        return E * (B /= D) * B * ((C + 1) * B - C) + A;
    },
    backOut: function(B, A, E, D, C) {
        if (typeof C == "undefined") {
            C = 1.70158;
        }
        return E * ((B = B / D - 1) * B * ((C + 1) * B + C) + 1) + A;
    },
    backBoth: function(B, A, E, D, C) {
        if (typeof C == "undefined") {
            C = 1.70158;
        }
        if ((B /= D / 2) < 1) {
            return E / 2 * (B * B * (((C *= (1.525)) + 1) * B - C)) + A;
        }
        return E / 2 * ((B -= 2) * B * (((C *= (1.525)) + 1) * B + C) + 2) + A;
    },
    bounceIn: function(B, A, D, C) {
        return D - YAHOO.ympyui.util.Easing.bounceOut(C - B, 0, D, C) + A;
    },
    bounceOut: function(B, A, D, C) {
        if ((B /= C) < (1 / 2.75)) {
            return D * (7.5625 * B * B) + A;
        } else {
            if (B < (2 / 2.75)) {
                return D * (7.5625 * (B -= (1.5 / 2.75)) * B + 0.75) + A;
            } else {
                if (B < (2.5 / 2.75)) {
                    return D * (7.5625 * (B -= (2.25 / 2.75)) * B + 0.9375) + A;
                }
            }
        }
        return D * (7.5625 * (B -= (2.625 / 2.75)) * B + 0.984375) + A;
    },
    bounceBoth: function(B, A, D, C) {
        if (B < C / 2) {
            return YAHOO.ympyui.util.Easing.bounceIn(B * 2, 0, D, C) * 0.5 + A;
        }
        return YAHOO.ympyui.util.Easing.bounceOut(B * 2 - C, 0, D, C) * 0.5 + D * 0.5 + A;
    }
}; (function() {
    var A = function(H, G, I, J) {
        if (H) {
            A.superclass.constructor.call(this, H, G, I, J);
        }
    };
    A.NAME = "Motion";
    var E = YAHOO.ympyui.util;
    YAHOO.ympyui.extend(A, E.ColorAnim);
    var F = A.superclass;
    var C = A.prototype;
    C.patterns.points = /^points$/i;
    C.setAttribute = function(G, I, H) {
        if (this.patterns.points.test(G)) {
            H = H || "px";
            F.setAttribute.call(this, "left", I[0], H);
            F.setAttribute.call(this, "top", I[1], H);
        } else {
            F.setAttribute.call(this, G, I, H);
        }
    };
    C.getAttribute = function(G) {
        if (this.patterns.points.test(G)) {
            var H = [F.getAttribute.call(this, "left"), F.getAttribute.call(this, "top")];
        } else {
            H = F.getAttribute.call(this, G);
        }
        return H;
    };
    C.doMethod = function(G, K, H) {
        var J = null;
        if (this.patterns.points.test(G)) {
            var I = this.method(this.currentFrame, 0, 100, this.totalFrames) / 100;
            J = E.Bezier.getPosition(this.runtimeAttributes[G], I);
        } else {
            J = F.doMethod.call(this, G, K, H);
        }
        return J;
    };
    C.setRuntimeAttribute = function(P) {
        if (this.patterns.points.test(P)) {
            var H = this.getEl();
            var J = this.attributes;
            var G;
            var L = J["points"]["control"] || [];
            var I;
            var M, O;
            if (L.length > 0 && !(L[0] instanceof Array)) {
                L = [L];
            } else {
                var K = [];
                for (M = 0, O = L.length; M < O; ++M) {
                    K[M] = L[M];
                }
                L = K;
            }
            if (E.Dom.getStyle(H, "position") == "static") {
                E.Dom.setStyle(H, "position", "relative");
            }
            if (D(J["points"]["from"])) {
                E.Dom.setXY(H, J["points"]["from"]);
            } else {
                E.Dom.setXY(H, E.Dom.getXY(H));
            }
            G = this.getAttribute("points");
            if (D(J["points"]["to"])) {
                I = B.call(this, J["points"]["to"], G);
                var N = E.Dom.getXY(this.getEl());
                for (M = 0, O = L.length; M < O; ++M) {
                    L[M] = B.call(this, L[M], G);
                }
            } else {
                if (D(J["points"]["by"])) {
                    I = [G[0] + J["points"]["by"][0], G[1] + J["points"]["by"][1]];
                    for (M = 0, O = L.length; M < O; ++M) {
                        L[M] = [G[0] + L[M][0], G[1] + L[M][1]];
                    }
                }
            }
            this.runtimeAttributes[P] = [G];
            if (L.length > 0) {
                this.runtimeAttributes[P] = this.runtimeAttributes[P].concat(L);
            }
            this.runtimeAttributes[P][this.runtimeAttributes[P].length] = I;
        } else {
            F.setRuntimeAttribute.call(this, P);
        }
    };
    var B = function(G, I) {
        var H = E.Dom.getXY(this.getEl());
        G = [G[0] - H[0] + I[0], G[1] - H[1] + I[1]];
        return G;
    };
    var D = function(G) {
        return (typeof G !== "undefined");
    };
    E.Motion = A;
})(); (function() {
    var D = function(F, E, G, H) {
        if (F) {
            D.superclass.constructor.call(this, F, E, G, H);
        }
    };
    D.NAME = "Scroll";
    var B = YAHOO.ympyui.util;
    YAHOO.ympyui.extend(D, B.ColorAnim);
    var C = D.superclass;
    var A = D.prototype;
    A.doMethod = function(E, H, F) {
        var G = null;
        if (E == "scroll") {
            G = [this.method(this.currentFrame, H[0], F[0] - H[0], this.totalFrames), this.method(this.currentFrame, H[1], F[1] - H[1], this.totalFrames)];
        } else {
            G = C.doMethod.call(this, E, H, F);
        }
        return G;
    };
    A.getAttribute = function(E) {
        var G = null;
        var F = this.getEl();
        if (E == "scroll") {
            G = [F.scrollLeft, F.scrollTop];
        } else {
            G = C.getAttribute.call(this, E);
        }
        return G;
    };
    A.setAttribute = function(E, H, G) {
        var F = this.getEl();
        if (E == "scroll") {
            F.scrollLeft = H[0];
            F.scrollTop = H[1];
        } else {
            C.setAttribute.call(this, E, H, G);
        }
    };
    B.Scroll = D;
})();
YAHOO.ympyui.register("animation", YAHOO.ympyui.util.Anim, {
    version: "2.5.1",
    build: "984"
});
YAHOO.namespace('YAHOO.mediaplayer');
if (typeof YMPParams === "undefined") {
    YMPParams = {};
}
if (typeof YMPParams.autoplay === "undefined") {
    YMPParams.autoplay = false;
}
if (typeof YMPParams.parse === "undefined") {
    YMPParams.parse = true;
}
if (typeof YMPParams.autoadvance === "undefined") {
    YMPParams.autoadvance = true;
}
if (typeof YMPParams.playlink === "undefined") {
    YMPParams.playlink = true;
}
if (typeof YMPParams.defaultalbumart === "undefined") {
    YMPParams.defaultalbumart = 'http://l.yimg.com/us.yimg.com/i/us/mus/ymwp/mediaplayer-default-album.gif';
}
if (typeof YMPParams.displaystate === "undefined") {
    YMPParams.displaystate = 0;
}
if (YMPParams.displaystate != -1 && YMPParams.displaystate != 0 && YMPParams.displaystate != 1 && YMPParams.displaystate != 3) {
    YMPParams.displaystate = 0;
}
if (typeof YMPParams.volume === "number") {
    if (YMPParams.volume > 1) {
        YMPParams.volume = 1;
    }
    if (YMPParams.volume < 0) {
        YMPParams.volume = 0;
    }
} else {
    if (typeof YMPParams.volume !== "undefined") {
        delete YMPParams.volume;
    }
}
if (YMPParams.amazonid == null || YMPParams.amazonid.length < 1) {
    var aMeta = document.getElementsByTagName("meta");
    if (aMeta && aMeta.length > 0) {
        var nCount = aMeta.length;
        for (var i = 0; i < nCount; i++) {
            var elMeta = aMeta[i];
            var sName = elMeta.name;
            if (typeof sName == "undefined") {
                sName = elMeta.getAttribute("name");
            }
            if (sName && sName.length > 0 && sName.toLowerCase() == "amazonid") {
                var sContent = elMeta.content;
                if (typeof sContent == "undefined") {
                    sContent = elMeta.getAttribute("content");
                }
                if (sContent.length > 0) {
                    YMPParams.amazonid = sContent;
                }
                break;
                elMeta = null;
            }
            elMeta = null;
        }
    }
}
if (typeof YMPParams.rhappcode === "undefined") {
    YMPParams.rhappcode = "yahoooffnet";
}
YAHOO.mediaplayer.YMPParams = {};
for (var props in YMPParams) {
    YAHOO.mediaplayer.YMPParams[props] = YMPParams[props];
}
YMPParams = null;
YAHOO.namespace('YAHOO.mediaplayer');
String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, '');
};
Number.GUID = function() {
    var aGUID = [];
    for (var nI = 0; nI < 32; nI++) {
        aGUID.push(Math.floor(Math.random() * 0xF).toString(0xF));
    }
    return aGUID.join('');
};
Math.getRnd = function(nMn, nMx) {
    if (!isNaN(nMn)) {
        if (!isNaN(nMx)) {
            nMx -= nMn;
        } else {
            nMx = nMn,
            nMn = 0;
        }
    } else {
        nMn = 0,
        nMx = 100;
    }
    return Math.round(Math.random() * (nMx - nMn)) + nMn;
};
YAHOO.mediaplayer.Util = {
    BROWSER: "Unknown",
    BROWSER_VERSION: "Unknown",
    OS: "Unknown",
    DOCTYPE: "Unknown",
    allBrowser: [{
        string: navigator.userAgent,
        subString: "OmniWeb",
        versionSearch: "OmniWeb/",
        identity: "OmniWeb"
    },
    {
        string: navigator.vendor,
        subString: "Apple",
        identity: "Safari"
    },
    {
        prop: window.opera,
        identity: "Opera"
    },
    {
        string: navigator.vendor,
        subString: "iCab",
        identity: "iCab"
    },
    {
        string: navigator.vendor,
        subString: "KDE",
        identity: "Konqueror"
    },
    {
        string: navigator.userAgent,
        subString: "Firefox",
        identity: "Firefox"
    },
    {
        string: navigator.vendor,
        subString: "Camino",
        identity: "Camino"
    },
    {
        string: navigator.userAgent,
        subString: "Netscape",
        identity: "Netscape"
    },
    {
        string: navigator.userAgent,
        subString: "MSIE",
        identity: "MSIE",
        versionSearch: "MSIE"
    },
    {
        string: navigator.userAgent,
        subString: "Gecko",
        identity: "Mozilla",
        versionSearch: "rv"
    },
    {
        string: navigator.userAgent,
        subString: "Mozilla",
        identity: "Netscape",
        versionSearch: "Mozilla"
    }],
    allOS: [{
        string: navigator.platform,
        subString: "Win",
        identity: "Windows"
    },
    {
        string: navigator.platform,
        subString: "Mac",
        identity: "Mac"
    },
    {
        string: navigator.platform,
        subString: "Linux",
        identity: "Linux"
    }],
    convertToHexadecimal: function(num) {
        try {
            var hex_str = [];
            for (var i = 3,
            mask = 0xff000000,
            byteNumber, byteString; i >= 0; i--) {
                byteNumber = Number((num & mask) >>> (i * 8));
                byteString = byteNumber.toString(16);
                if (byteString.length < 2) {
                    byteString = '0' + byteString;
                }
                hex_str.push(byteString);
                mask >>>= 8;
            }
            return hex_str.join('').toUpperCase();
        } catch(ex) {
            return null;
        }
    },
    init: function() {
        this.getBrowserOS();
    },
    returnString: function(data) {
        for (var i = 0; i < data.length; i++) {
            var dataString = data[i].string;
            var dataProp = data[i].prop;
            this.versionSearchString = data[i].versionSearch || data[i].identity;
            if (dataString) {
                if (dataString.indexOf(data[i].subString) != -1) {
                    return data[i].identity;
                }
            } else if (dataProp) {
                return data[i].identity;
            }
        }
    },
    returnVersion: function(data) {
        var index = data.indexOf(this.versionSearchString);
        if (index == -1) {
            return;
        }
        return parseFloat(data.substring(index + this.versionSearchString.length + 1));
    },
    getBrowserOS: function() {
        this.BROWSER = this.returnString(this.allBrowser) || "Unknown";
        this.BROWSER_VERSION = this.returnVersion(navigator.userAgent) || this.returnVersion(navigator.appVersion) || "Unknown";
        this.OS = this.returnString(this.allOS) || "Unknown";
        this.DOCTYPE = document.compatMode;
    },
    detectPlugin: function(pluginName, activexName) {
        if (typeof window.ActiveXObject != "undefined") {
            var control = null;
            try {
                control = new ActiveXObject(activexName);
            } catch(e) {}
            if (control) {
                var result = activexName;
                control = null;
                return result;
            }
        } else {
            if (navigator && navigator.plugins && navigator.plugins.length) {
                for (var i = 0; i < navigator.plugins.length; i++) {
                    var pi = navigator.plugins[i];
                    if (pi.name.indexOf(pluginName) > -1) {
                        var result = pi.name;
                        pi = null;
                        return result;
                    }
                    pi = null;
                }
            }
        }
        return null;
    },
    sprintf: function(fstring, stringsArray) {
        var format_RE = new RegExp('(.*?)(%%|%\\d+|$)(\\$[sdf])?', 'g');
        retstr = "";
        while (format_arr = format_RE.exec(fstring)) {
            retstr += format_arr[1];
            if (format_arr[2] == '') break;
            if (format_arr[2] == "%%") {
                retstr += "%";
            } else {
                retstr += stringsArray[Number(format_arr[2].substr(1)) - 1];
            }
        }
        return retstr;
    },
    isArray: function(obj) {
        if (obj.constructor.toString().indexOf("Array") == -1) return false;
        else return true;
    },
    keycodes: {
        KEY_SPACE: 32,
        KEY_LEFT: 37,
        KEY_UP: 38,
        KEY_RIGHT: 39,
        KEY_DOWN: 40,
        KEY_P: 80
    }
};
YAHOO.mediaplayer.Util.init();
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.EventDelegate = new
function() {
    this.evDelFn = {};
    this.on = function(className, evType, root, fn, obj, override) {
        if (typeof(fn) !== "function") {
            return;
        }
        if (this.evDelFn[evType] == null) {
            this.evDelFn[evType] = {};
        }
        if (this.evDelFn[evType][className] == null) {
            this.evDelFn[evType][className] = [];
        }
        root = YAHOO.ympyui.util.Dom.get(root);
        this.evDelFn[evType][className].push([fn, obj, override, root]);
        var rootAlreadyAdded = false;
        var rootListeners = YAHOO.ympyui.util.Event.getListeners(root, evType);
        if (rootListeners != null) {
            for (var i = 0,
            ilen = rootListeners.length; i < ilen; i++) {
                if (rootListeners[i].fn === this.handleEventDelegation) {
                    rootAlreadyAdded = true;
                }
            }
        }
        if (!rootAlreadyAdded) {
            YAHOO.ympyui.util.Event.addListener(root, evType, this.handleEventDelegation, [this, root]);
        }
    };
    this.removeListener = function(className, root, evType, fn) {
        if (this.evDelFn[evType] != null && this.evDelFn[evType][className] != null) {
            var classFns = this.evDelFn[evType][className];
            root = YAHOO.ympyui.util.Dom.get(root);
            for (var i = 0; i < classFns.length; i++) {
                if (root != null && classFns[i][3] != root) {
                    continue;
                }
                if (fn != null && classFns[i][0] != fn) {
                    continue;
                }
                classFns.splice(i, 1);
                i--;
            }
        }
    };
    this.handleEventDelegation = function(ev, obj) {
        var elTarget = YAHOO.ympyui.util.Event.getTarget(ev);
        while (elTarget != obj[1]) {
            for (var className in obj[0].evDelFn[ev.type]) {
                if (YAHOO.ympyui.util.Dom.hasClass(elTarget, className)) {
                    var classFns = obj[0].evDelFn[ev.type][className];
                    for (var i = 0; i < classFns.length; i++) {
                        if (this == classFns[i][3]) {
                            var scope = elTarget;
                            var override = classFns[i][2];
                            var obj2 = classFns[i][1];
                            if (override) {
                                if (override === true) {
                                    scope = obj2;
                                }
                            }
                            classFns[i][0].call(scope, YAHOO.ympyui.util.Event.getEvent(ev), obj2);
                        }
                    }
                    return;
                }
            }
            elTarget = elTarget.parentNode;
        }
    };
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.SWFObject = function() {
    var UNDEF = "undefined",
    OBJECT = "object",
    SHOCKWAVE_FLASH = "Shockwave Flash",
    SHOCKWAVE_FLASH_AX = "ShockwaveFlash.ShockwaveFlash",
    FLASH_MIME_TYPE = "application/x-shockwave-flash",
    EXPRESS_INSTALL_ID = "SWFObjectExprInst",
    win = window,
    doc = document,
    nav = navigator,
    domLoadFnArr = [],
    regObjArr = [],
    timer = null,
    storedAltContent = null,
    storedAltContentId = null,
    isDomLoaded = false,
    isExpressInstallActive = false;
    var ua = function() {
        var w3cdom = typeof doc.getElementById != UNDEF && typeof doc.getElementsByTagName != UNDEF && typeof doc.createElement != UNDEF && typeof doc.appendChild != UNDEF && typeof doc.replaceChild != UNDEF && typeof doc.removeChild != UNDEF && typeof doc.cloneNode != UNDEF,
        playerVersion = [0, 0, 0],
        d = null;
        if (typeof nav.plugins != UNDEF && typeof nav.plugins[SHOCKWAVE_FLASH] == OBJECT) {
            d = nav.plugins[SHOCKWAVE_FLASH].description;
            if (d) {
                d = d.replace(/^.*\s+(\S+\s+\S+$)/, "$1");
                playerVersion[0] = parseInt(d.replace(/^(.*)\..*$/, "$1"), 10);
                playerVersion[1] = parseInt(d.replace(/^.*\.(.*)\s.*$/, "$1"), 10);
                playerVersion[2] = /r/.test(d) ? parseInt(d.replace(/^.*r(.*)$/, "$1"), 10) : 0;
            }
        } else if (typeof win.ActiveXObject != UNDEF) {
            var a = null,
            fp6Crash = false;
            try {
                a = new ActiveXObject(SHOCKWAVE_FLASH_AX + ".7");
            } catch(e) {
                try {
                    a = new ActiveXObject(SHOCKWAVE_FLASH_AX + ".6");
                    playerVersion = [6, 0, 21];
                    a.AllowScriptAccess = "always";
                } catch(e) {
                    if (playerVersion[0] == 6) {
                        fp6Crash = true;
                    }
                }
                if (!fp6Crash) {
                    try {
                        a = new ActiveXObject(SHOCKWAVE_FLASH_AX);
                    } catch(e) {}
                }
            }
            if (!fp6Crash && a) {
                try {
                    d = a.GetVariable("$version");
                    if (d) {
                        d = d.split(" ")[1].split(",");
                        playerVersion = [parseInt(d[0], 10), parseInt(d[1], 10), parseInt(d[2], 10)];
                    }
                } catch(e) {}
            }
        }
        var u = nav.userAgent.toLowerCase(),
        p = nav.platform.toLowerCase(),
        av = navigator.appVersion.toLowerCase(),
        webkit = /webkit/.test(u) ? parseFloat(u.replace(/^.*webkit\/(\d+(\.\d+)?).*$/, "$1")) : false,
        ie = u ? /msie/.test(u) : /msie/.test(av),
        windows = p ? /win/.test(p) : /win/.test(u),
        mac = p ? /mac/.test(p) : /mac/.test(u);
        return {
            w3cdom: w3cdom,
            pv: playerVersion,
            webkit: webkit,
            ie: ie,
            win: windows,
            mac: mac
        };
    } ();
    var onDomLoad = function() {
        if (!ua.w3cdom) {
            return;
        }
        addDomLoadEvent(main);
        if (ua.ie && ua.win) {
            try {
                doc.write("<scr" + "ipt id=__ie_ondomload defer=true src=//:></scr" + "ipt>");
                var s = getElementById("__ie_ondomload");
                if (s) {
                    s.onreadystatechange = function() {
                        if (this.readyState == "complete") {
                            this.parentNode.removeChild(this);
                            callDomLoadFunctions();
                        }
                    };
                }
            } catch(e) {}
        }
        if (ua.webkit && typeof doc.readyState != UNDEF) {
            timer = setInterval(function() {
                if (/loaded|complete/.test(doc.readyState)) {
                    callDomLoadFunctions();
                }
            },
            10);
        }
        if (typeof doc.addEventListener != UNDEF) {
            doc.addEventListener("DOMContentLoaded", callDomLoadFunctions, null);
        }
        addLoadEvent(callDomLoadFunctions);
    } ();
    function callDomLoadFunctions() {
        if (isDomLoaded) {
            return;
        }
        if (ua.ie && ua.win) {
            var s = createElement("span");
            try {
                var t = doc.getElementsByTagName("body")[0].appendChild(s);
                t.parentNode.removeChild(t);
            } catch(e) {
                return;
            }
        }
        isDomLoaded = true;
        if (timer) {
            clearInterval(timer);
            timer = null;
        }
        var dl = domLoadFnArr.length;
        for (var i = 0; i < dl; i++) {
            domLoadFnArr[i]();
        }
    }
    function addDomLoadEvent(fn) {
        if (isDomLoaded) {
            fn();
        } else {
            domLoadFnArr[domLoadFnArr.length] = fn;
        }
    }
    function addLoadEvent(fn) {
        if (typeof win.addEventListener != UNDEF) {
            win.addEventListener("load", fn, false);
        } else if (typeof doc.addEventListener != UNDEF) {
            doc.addEventListener("load", fn, false);
        } else if (typeof win.attachEvent != UNDEF) {
            win.attachEvent("onload", fn);
        } else if (typeof win.onload == "function") {
            var fnOld = win.onload;
            win.onload = function() {
                fnOld();
                fn();
            };
        } else {
            win.onload = fn;
        }
    }
    function main() {
        var rl = regObjArr.length;
        for (var i = 0; i < rl; i++) {
            var id = regObjArr[i].id;
            if (ua.pv[0] > 0) {
                var obj = getElementById(id);
                if (obj) {
                    regObjArr[i].width = obj.getAttribute("width") ? obj.getAttribute("width") : "0";
                    regObjArr[i].height = obj.getAttribute("height") ? obj.getAttribute("height") : "0";
                    if (hasPlayerVersion(regObjArr[i].swfVersion)) {
                        if (ua.webkit && ua.webkit < 312) {
                            fixParams(obj);
                        }
                        setVisibility(id, true);
                    } else if (regObjArr[i].expressInstall && !isExpressInstallActive && hasPlayerVersion("6.0.65") && (ua.win || ua.mac)) {
                        showExpressInstall(regObjArr[i]);
                    } else {
                        displayAltContent(obj);
                    }
                }
            } else {
                setVisibility(id, true);
            }
        }
    }
    function fixParams(obj) {
        var nestedObj = obj.getElementsByTagName(OBJECT)[0];
        if (nestedObj) {
            var e = createElement("embed"),
            a = nestedObj.attributes;
            if (a) {
                var al = a.length;
                for (var i = 0; i < al; i++) {
                    if (a[i].nodeName.toLowerCase() == "data") {
                        e.setAttribute("src", a[i].nodeValue);
                    } else {
                        e.setAttribute(a[i].nodeName, a[i].nodeValue);
                    }
                }
            }
            var c = nestedObj.childNodes;
            if (c) {
                var cl = c.length;
                for (var j = 0; j < cl; j++) {
                    if (c[j].nodeType == 1 && c[j].nodeName.toLowerCase() == "param") {
                        e.setAttribute(c[j].getAttribute("name"), c[j].getAttribute("value"));
                    }
                }
            }
            obj.parentNode.replaceChild(e, obj);
        }
    }
    function fixObjectLeaks(id) {
        if (ua.ie && ua.win && hasPlayerVersion("8.0.0")) {
            win.attachEvent("onunload",
            function() {
                var obj = getElementById(id);
                if (obj) {
                    for (var i in obj) {
                        if (typeof obj[i] == "function") {
                            obj[i] = function() {};
                        }
                    }
                    obj.parentNode.removeChild(obj);
                }
            });
        }
    }
    function showExpressInstall(regObj) {
        isExpressInstallActive = true;
        var obj = getElementById(regObj.id);
        if (obj) {
            if (regObj.altContentId) {
                var ac = getElementById(regObj.altContentId);
                if (ac) {
                    storedAltContent = ac;
                    storedAltContentId = regObj.altContentId;
                }
            } else {
                storedAltContent = abstractAltContent(obj);
            }
            if (! (/%$/.test(regObj.width)) && parseInt(regObj.width, 10) < 310) {
                regObj.width = "310";
            }
            if (! (/%$/.test(regObj.height)) && parseInt(regObj.height, 10) < 137) {
                regObj.height = "137";
            }
            doc.title = doc.title.slice(0, 47) + " - Flash Player Installation";
            var pt = ua.ie && ua.win ? "ActiveX": "PlugIn",
            dt = doc.title,
            fv = "MMredirectURL=" + win.location + "&MMplayerType=" + pt + "&MMdoctitle=" + dt,
            replaceId = regObj.id;
            if (ua.ie && ua.win && obj.readyState != 4) {
                var newObj = createElement("div");
                replaceId += "SWFObjectNew";
                newObj.setAttribute("id", replaceId);
                obj.parentNode.insertBefore(newObj, obj);
                obj.style.display = "none";
                win.attachEvent("onload",
                function() {
                    obj.parentNode.removeChild(obj);
                });
            }
            createSWF({
                data: regObj.expressInstall,
                id: EXPRESS_INSTALL_ID,
                width: regObj.width,
                height: regObj.height
            },
            {
                flashvars: fv
            },
            replaceId);
        }
    }
    function displayAltContent(obj) {
        if (ua.ie && ua.win && obj.readyState != 4) {
            var el = createElement("div");
            obj.parentNode.insertBefore(el, obj);
            el.parentNode.replaceChild(abstractAltContent(obj), el);
            obj.style.display = "none";
            win.attachEvent("onload",
            function() {
                obj.parentNode.removeChild(obj);
            });
        } else {
            obj.parentNode.replaceChild(abstractAltContent(obj), obj);
        }
    }
    function abstractAltContent(obj) {
        var ac = createElement("div");
        if (ua.win && ua.ie) {
            ac.innerHTML = obj.innerHTML;
        } else {
            var nestedObj = obj.getElementsByTagName(OBJECT)[0];
            if (nestedObj) {
                var c = nestedObj.childNodes;
                if (c) {
                    var cl = c.length;
                    for (var i = 0; i < cl; i++) {
                        if (! (c[i].nodeType == 1 && c[i].nodeName.toLowerCase() == "param") && !(c[i].nodeType == 8)) {
                            ac.appendChild(c[i].cloneNode(true));
                        }
                    }
                }
            }
        }
        return ac;
    }
    function createSWF(attObj, parObj, id) {
        var r, el = getElementById(id);
        if (typeof attObj.id == UNDEF) {
            attObj.id = id;
        }
        if (ua.ie && ua.win) {
            var att = "";
            for (var i in attObj) {
                if (attObj[i] != Object.prototype[i]) {
                    if (i == "data") {
                        parObj.movie = attObj[i];
                    } else if (i.toLowerCase() == "styleclass") {
                        att += ' class="' + attObj[i] + '"';
                    } else if (i != "classid") {
                        att += ' ' + i + '="' + attObj[i] + '"';
                    }
                }
            }
            var par = "";
            for (var j in parObj) {
                if (parObj[j] != Object.prototype[j]) {
                    par += '<param name="' + j + '" value="' + parObj[j] + '" />';
                }
            }
            el.outerHTML = '<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"' + att + '>' + par + '</object>';
            fixObjectLeaks(attObj.id);
            r = getElementById(attObj.id);
        } else if (ua.webkit && ua.webkit < 312) {
            var e = createElement("embed");
            e.setAttribute("type", FLASH_MIME_TYPE);
            for (var k in attObj) {
                if (attObj[k] != Object.prototype[k]) {
                    if (k == "data") {
                        e.setAttribute("src", attObj[k]);
                    } else if (k.toLowerCase() == "styleclass") {
                        e.setAttribute("class", attObj[k]);
                    } else if (k != "classid") {
                        e.setAttribute(k, attObj[k]);
                    }
                }
            }
            for (var l in parObj) {
                if (parObj[l] != Object.prototype[l]) {
                    if (l != "movie") {
                        e.setAttribute(l, parObj[l]);
                    }
                }
            }
            el.parentNode.replaceChild(e, el);
            r = e;
        } else {
            var o = createElement(OBJECT);
            o.setAttribute("type", FLASH_MIME_TYPE);
            for (var m in attObj) {
                if (attObj[m] != Object.prototype[m]) {
                    if (m.toLowerCase() == "styleclass") {
                        o.setAttribute("class", attObj[m]);
                    } else if (m != "classid") {
                        o.setAttribute(m, attObj[m]);
                    }
                }
            }
            for (var n in parObj) {
                if (parObj[n] != Object.prototype[n] && n != "movie") {
                    createObjParam(o, n, parObj[n]);
                }
            }
            el.parentNode.replaceChild(o, el);
            r = o;
        }
        return r;
    }
    function createObjParam(el, pName, pValue) {
        var p = createElement("param");
        p.setAttribute("name", pName);
        p.setAttribute("value", pValue);
        el.appendChild(p);
    }
    function getElementById(id) {
        return doc.getElementById(id);
    }
    function createElement(el) {
        return doc.createElement(el);
    }
    function hasPlayerVersion(rv) {
        var pv = ua.pv,
        v = rv.split(".");
        v[0] = parseInt(v[0], 10);
        v[1] = parseInt(v[1], 10);
        v[2] = parseInt(v[2], 10);
        return (pv[0] > v[0] || (pv[0] == v[0] && pv[1] > v[1]) || (pv[0] == v[0] && pv[1] == v[1] && pv[2] >= v[2])) ? true: false;
    }
    function createCSS(sel, decl) {
        if (ua.ie && ua.mac) {
            return;
        }
        var h = doc.getElementsByTagName("head")[0],
        s = createElement("style");
        s.setAttribute("type", "text/css");
        s.setAttribute("media", "screen");
        if (! (ua.ie && ua.win) && typeof doc.createTextNode != UNDEF) {
            s.appendChild(doc.createTextNode(sel + " {" + decl + "}"));
        }
        h.appendChild(s);
        if (ua.ie && ua.win && typeof doc.styleSheets != UNDEF && doc.styleSheets.length > 0) {
            var ls = doc.styleSheets[doc.styleSheets.length - 1];
            if (typeof ls.addRule == OBJECT) {
                ls.addRule(sel, decl);
            }
        }
    }
    function setVisibility(id, isVisible) {
        var v = isVisible ? "visible": "hidden";
        if (isDomLoaded) {
            document.getElementById(id).style.visibility = v;
        } else {
            createCSS("#" + id, "visibility:" + v);
        }
    }
    return {
        registerObject: function(objectIdStr, swfVersionStr, xiSwfUrlStr) {
            if (!ua.w3cdom || !objectIdStr || !swfVersionStr) {
                return;
            }
            var regObj = {};
            regObj.id = objectIdStr;
            regObj.swfVersion = swfVersionStr;
            regObj.expressInstall = xiSwfUrlStr ? xiSwfUrlStr: false;
            regObjArr[regObjArr.length] = regObj;
            setVisibility(objectIdStr, false);
        },
        getObjectById: function(objectIdStr) {
            var r = null;
            if (ua.w3cdom && isDomLoaded) {
                var o = getElementById(objectIdStr);
                if (o) {
                    var n = o.getElementsByTagName(OBJECT)[0];
                    if (!n || (n && typeof o.SetVariable != UNDEF)) {
                        r = o;
                    } else if (typeof n.SetVariable != UNDEF) {
                        r = n;
                    }
                }
            }
            return r;
        },
        embedSWF: function(swfUrlStr, replaceElemIdStr, widthStr, heightStr, swfVersionStr, xiSwfUrlStr, flashvarsObj, parObj, attObj) {
            if (!ua.w3cdom || !swfUrlStr || !replaceElemIdStr || !widthStr || !heightStr || !swfVersionStr) {
                return;
            }
            widthStr += "";
            heightStr += "";
            if (hasPlayerVersion(swfVersionStr)) {
                setVisibility(replaceElemIdStr, false);
                var att = (typeof attObj == OBJECT) ? attObj: {};
                att.data = swfUrlStr;
                att.width = widthStr;
                att.height = heightStr;
                var par = (typeof parObj == OBJECT) ? parObj: {};
                if (typeof flashvarsObj == OBJECT) {
                    for (var i in flashvarsObj) {
                        if (flashvarsObj[i] != Object.prototype[i]) {
                            if (typeof par.flashvars != UNDEF) {
                                par.flashvars += "&" + i + "=" + flashvarsObj[i];
                            } else {
                                par.flashvars = i + "=" + flashvarsObj[i];
                            }
                        }
                    }
                }
                createSWF(att, par, replaceElemIdStr);
                if (att.id == replaceElemIdStr) {
                    setVisibility(replaceElemIdStr, true);
                }
            } else if (xiSwfUrlStr && !isExpressInstallActive && hasPlayerVersion("6.0.65") && (ua.win || ua.mac)) {
                setVisibility(replaceElemIdStr, false);
                var regObj = {};
                regObj.id = regObj.altContentId = replaceElemIdStr;
                regObj.width = widthStr;
                regObj.height = heightStr;
                regObj.expressInstall = xiSwfUrlStr;
                showExpressInstall(regObj);
            }
        },
        getFlashPlayerVersion: function() {
            return {
                major: ua.pv[0],
                minor: ua.pv[1],
                release: ua.pv[2]
            };
        },
        hasFlashPlayerVersion: hasPlayerVersion,
        createSWF: function(attObj, parObj, replaceElemIdStr) {
            if (ua.w3cdom && isDomLoaded) {
                return createSWF(attObj, parObj, replaceElemIdStr);
            } else {
                return undefined;
            }
        },
        createCSS: function(sel, decl) {
            if (ua.w3cdom) {
                createCSS(sel, decl);
            }
        },
        addDomLoadEvent: addDomLoadEvent,
        addLoadEvent: addLoadEvent,
        getQueryParamValue: function(param) {
            var q = doc.location.search || doc.location.hash;
            if (param == null) {
                return q;
            }
            if (q) {
                var pairs = q.substring(1).split("&");
                for (var i = 0; i < pairs.length; i++) {
                    if (pairs[i].substring(0, pairs[i].indexOf("=")) == param) {
                        return pairs[i].substring((pairs[i].indexOf("=") + 1));
                    }
                }
            }
            return "";
        },
        expressInstallCallback: function() {
            if (isExpressInstallActive && storedAltContent) {
                var obj = getElementById(EXPRESS_INSTALL_ID);
                if (obj) {
                    obj.parentNode.replaceChild(storedAltContent, obj);
                    if (storedAltContentId) {
                        setVisibility(storedAltContentId, true);
                        if (ua.ie && ua.win) {
                            storedAltContent.style.display = "block";
                        }
                    }
                    storedAltContent = null;
                    storedAltContentId = null;
                    isExpressInstallActive = false;
                }
            }
        },
        getSWF: function(name) {
            switch (YAHOO.mediaplayer.Util.BROWSER) {
            case "MSIE":
                return window[name];
            default:
                if (document[name] != null && document[name].length != undefined) {
                    return document[name][1];
                } else {
                    return document[name];
                }
            }
        },
        hasPlayerVersion: hasPlayerVersion
    };
} ();
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.SWFProxy = new
function() {
    this.map = {};
    this.magicNumber = 0;
    this.id = "swfproxy";
    this.available = false;
    this.ready = false;
    this.queue = [];
    this.flashTimeoutID = null;
    this.timeout = 30000;
    this.init = function() {
        var dummyContainer = document.createElement('span');
        dummyContainer.id = "dummy-swfproxy";
        document.body.appendChild(dummyContainer);
        try {
            if (YAHOO.mediaplayer.SWFObject.hasPlayerVersion('9.0.0')) {
                var flashVars = {
                    onLoad: "YAHOO.mediaplayer.SWFProxy.onLoad",
                    rhappcode: YAHOO.mediaplayer.YMPParams.rhappcode
                };
                var params = {
                    allowScriptAccess: "always",
                    allowNetworking: "all"
                };
                var attributes = {
                    id: this.id,
                    name: this.id,
                    style: "position:absolute; top:0; left:-30px;"
                };
                YAHOO.mediaplayer.SWFObject.embedSWF("http://l.yimg.com/us.yimg.com/i/us/mus/swf/ymwp/swfproxy-2.0.31.swf", dummyContainer.id, "1", "1", "9.0.0", false, flashVars, params, attributes);
                this.available = true;
            }
        } catch(e) {}
    };
    this.onLoad = function() {
        this.ready = true;
        YAHOO.mediaplayer.SWFObject.getSWF(this.id).flAddListener('Success', 'YAHOO.mediaplayer.SWFProxy.successHandler');
        YAHOO.mediaplayer.SWFObject.getSWF(this.id).flAddListener('Failure', 'YAHOO.mediaplayer.SWFProxy.failureHandler');
        for (var i = 0,
        ilen = this.queue.length; i < ilen; i++) {
            this.queue[i].callee.apply(this, this.queue[i]);
        }
    };
    this.getPlayThisPage = function(url, callback) {
        if (this.ready) {
            if (callback.scope == null) {
                callback.scope = window;
            }
            if (callback.timeout == null) {
                callback.timeout = this.timeout;
            }
            var id = this.magicNumber++;
            YAHOO.mediaplayer.SWFObject.getSWF(this.id).flGetPlayThisPage(id, url, callback.timeout);
            this.map[id] = callback;
        } else {
            this.addToQueue(arguments);
        }
    };
    this.getRhapMetadata = function(ids, callback) {
        if (this.ready) {
            if (callback.scope == null) {
                callback.scope = window;
            }
            if (callback.timeout == null) {
                callback.timeout = this.timeout;
            }
            var id = this.magicNumber++;
            YAHOO.mediaplayer.SWFObject.getSWF(this.id).flGetRhapMetadata(id, ids);
            this.map[id] = callback;
        } else {
            this.addToQueue(arguments);
        }
    };
    this.getWsapiMetadata = function(ids, callback) {
        if (this.ready) {
            if (callback.scope == null) {
                callback.scope = window;
            }
            if (callback.timeout == null) {
                callback.timeout = this.timeout;
            }
            var id = this.magicNumber++;
            YAHOO.mediaplayer.SWFObject.getSWF(this.id).flGetWsapiMetadata(id, ids);
            this.map[id] = callback;
        } else {
            this.addToQueue(arguments);
        }
    };
    this.addToQueue = function(args) {
        this.queue.push(args);
        if (this.flashTimeoutID == null) {
            this.flashTimeoutID = window.setTimeout('YAHOO.mediaplayer.SWFProxy.checkFlashLoaded()', 5000);
        }
    };
    this.checkFlashLoaded = function() {
        if (!this.ready) {
            var callbackObj, func, scope, args, o;
            for (var i = 0,
            ilen = this.queue.length; i < ilen; i++) {
                callbackObj = this.queue[i][1];
                if (callbackObj.scope == null) {
                    callbackObj.scope = window;
                }
                func = callbackObj.failure;
                scope = callbackObj.scope;
                args = callbackObj.argument;
                o = {
                    status: "Flash proxy failed to load",
                    argument: args
                };
                func.call(scope, o);
            }
            this.queue = [];
        }
    };
    this.successHandler = function(id, result) {
        var func = this.map[id].success;
        var scope = this.map[id].scope;
        var args = this.map[id].argument;
        var o = {
            responseText: result,
            argument: args
        };
        func.call(scope, o);
    };
    this.failureHandler = function(id, statusText) {
        var func = this.map[id].failure;
        var scope = this.map[id].scope;
        var args = this.map[id].argument;
        var o = {
            status: statusText,
            argument: args
        };
        func.call(scope, o);
    };
};
YAHOO.mediaplayer.SWFProxy.init();
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.Closure = {
    objects: [],
    functions: [],
    closures: {}
};
Function.prototype.closure = function(obj) {
    try {
        if (typeof(obj) === 'undefined') {
            throw new Error('Invalid argument exception. "obj" is undefined.');
        }
        var func = this;
        var objId = obj.__objId;
        if (typeof(objId) !== 'number') {
            objId = obj.__objId = YAHOO.mediaplayer.Closure.objects.length;
            YAHOO.mediaplayer.Closure.objects[objId] = obj;
        }
        var funcId = func.__funcId;
        if (typeof(funcId) !== 'number') {
            funcId = func.__funcId = YAHOO.mediaplayer.Closure.functions.length;
            YAHOO.mediaplayer.Closure.functions[funcId] = func;
        }
        var closureId = objId + '_' + funcId;
        var closure = YAHOO.mediaplayer.Closure.closures[closureId];
        if (typeof(closure) !== 'function') {
            closure = YAHOO.mediaplayer.Closure.closures[closureId] = function() {
                return YAHOO.mediaplayer.Closure.functions[funcId].apply(YAHOO.mediaplayer.Closure.objects[objId], arguments);
            };
        }
        return closure;
    } catch(ex) {
        throw new Error('ERROR in Function.closure(). ' + ex.message);
    }
};
YAHOO.ympyui.util.Event.addListener(window, 'unload',
function() {
    try {
        window.setTimeout('YAHOO.mediaplayer.Closure = Function.prototype.closure = null', 500);
    } catch(ex) {}
    if (window.CollectGarbage) {
        window.CollectGarbage();
    }
});
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.EventManager = function(owner, eventTypes) {
    this.getOwner = function() {
        return this;
    }.closure(owner);
    this.toString = function() {
        return this + '.EventManager';
    }.closure(owner);
    this.events = [];
    if (eventTypes && eventTypes.constructor === Array) {
        this.addEvents(eventTypes);
    }
};
YAHOO.mediaplayer.EventManager.prototype.addEvents = function(eventTypes) {
    try {
        if (!eventTypes || eventTypes.constructor !== Array) {
            throw new Error('Invalid argument exception. "eventTypes" is not an array.');
        }
        for (var idx = 0,
        len = eventTypes.length; idx < len; idx++) {
            this.addEvent(eventTypes[idx]);
        }
    } catch(ex) {
        throw new Error('ERROR in ' + this + '.addEvents(). ' + ex.message);
    }
};
YAHOO.mediaplayer.EventManager.prototype.addEvent = function(eventType) {
    try {
        if (typeof(eventType) !== 'string' || eventType.length === 0) {
            throw new Error('Invalid argument exception. "eventType":' + eventType + ' is not a valid string or is empty.');
        }
        if (!this[eventType]) {
            this.events.push(eventType);
            this[eventType] = new YAHOO.ympyui.util.CustomEvent(eventType, this);
        }
    } catch(ex) {
        throw new Error('ERROR in ' + this + '.addEvent(). ' + ex.message);
    }
};
YAHOO.mediaplayer.EventManager.prototype.subscribe = function(obj, eventTypes) {
    try {
        if (!obj || typeof(obj.handleEvent) !== 'function') {
            throw new Error('Invalid argument exception. "obj" is not a valid object.');
        }
        if (!eventTypes || eventTypes.constructor !== Array) {
            throw new Error('Invalid argument exception. "eventTypes" is not an array.');
        }
        for (var idx = 0,
        len = eventTypes.length,
        eventType; idx < len; idx++) {
            eventType = eventTypes[idx];
            if (!this[eventType]) {
                this[eventType] = new YAHOO.ympyui.util.CustomEvent(eventType, this);
            }
            this[eventType].subscribe(obj.handleEvent, obj, true);
        }
    } catch(ex) {
        throw new Error('ERROR in ' + this + '.subscribe(). ' + ex.message);
    }
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.ControllerBase = function ControllerBase() {
    this.EventManager = new YAHOO.mediaplayer.EventManager(this);
};
YAHOO.mediaplayer.ControllerBase.prototype.toString = function() {
    return 'YAHOO.mediaplayer.ControllerBase';
};
YAHOO.mediaplayer.ControllerBase.prototype.handleEvent = function handleEvent(evType, args) {
    try {
        if (typeof(this[evType]) === 'function') {
            this[evType](args[0]);
        } else if (this.EventManager[evType]) {
            this.EventManager[evType].fire(args[0]);
        }
    } catch(ex) {
        throw new Error('ERROR in ' + this + '.handleEvent(evType:"' + evType + '"). ' + ex.message);
    }
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.BaseObject = function BaseObject(controller, subscribeToControllersEvents) {
    try {
        if (typeof(this.refByName) !== 'string' || this.refByName.length === 0) {
            throw new Error('Invalid required property exception. this.refByName:"' + this.refByName + '" is invalid.');
        }
        YAHOO.mediaplayer.BaseObject.superclass.constructor.call(this);
        this.getController = function() {
            return this;
        }.closure(controller);
        if (!this.EventManager || this.EventManager.constructor !== YAHOO.mediaplayer.EventManager) {
            throw new Error('Invalid required property exception. this.EventManager is invalid.');
        }
        if (subscribeToControllersEvents && subscribeToControllersEvents.constructor === Array && subscribeToControllersEvents.length > 0) {
            controller.EventManager.subscribe(this, subscribeToControllersEvents);
        }
    } catch(ex) {
        throw new Error('ERROR in ' + this + ' constructor. ' + ex.message);
    }
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.BaseObject, YAHOO.mediaplayer.ControllerBase);
YAHOO.mediaplayer.BaseObject.prototype.toString = function() {
    return 'YAHOO.mediaplayer.BaseObject';
};
YAHOO.mediaplayer.BaseObject.prototype.initController = function(controller) {
    try {
        if (typeof(this.refByName) !== 'string' || this.refByName.length < 0) {
            throw new Error('Invalid required property exception. this.refByName:"' + this.refByName + '" is invalid.');
        }
        if (!this.EventManager || this.EventManager.constructor !== YAHOO.mediaplayer.EventManager) {
            throw new Error('Invalid required property exception. this.EventManager is invalid.');
        }
        controller[this.refByName] = this;
        this.EventManager.subscribe(controller, this.EventManager.events);
        for (var idx = 0,
        len = this.EventManager.events.length,
        eventType; idx < len; idx++) {
            eventType = this.EventManager.events[idx];
            if (!controller.EventManager[eventType]) {
                controller.EventManager.addEvent(eventType);
            }
        }
    } catch(ex) {
        throw new Error('ERROR in ' + this + '.initController(). ' + ex.message);
    }
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.Controller = function() {
    YAHOO.mediaplayer.Controller.superclass.constructor.call(this, arguments);
    this.isInitialState = true;
    this.errorCount = 0;
    this.maxErrors = 5;
    YAHOO.ympyui.util.Event.on(window, 'unload', this.onWindowUnload, this, true);
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.Controller, YAHOO.mediaplayer.ControllerBase);
YAHOO.mediaplayer.Controller.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Controller';
};
YAHOO.mediaplayer.Controller.prototype.init = function() {
    new YAHOO.mediaplayer.Parser(this);
    new YAHOO.mediaplayer.PlaylistManager(this);
    new YAHOO.mediaplayer.MediaResolver(this);
    new YAHOO.mediaplayer.MediaEngine(this);
    new YAHOO.mediaplayer.Logger(this);
    if (YAHOO.mediaplayer.Rhapsody != null) {
        new YAHOO.mediaplayer.Rhapsody(this);
    }
    YAHOO.MediaPlayer.init();
    var tracks = null;
    if (YAHOO.mediaplayer.YMPParams.parse === true) {
        tracks = this.parser.parse(null);
    }
    if (tracks && typeof(tracks.length) === "number" && tracks.length > 0) {
        this.playlistmanager.add(tracks);
    }
};
YAHOO.mediaplayer.Controller.prototype.onPlaylistUpdate = function(playlist) {
    if (playlist != null && !(playlist instanceof YAHOO.mediaplayer.Playlist) && playlist.length > 0) {
        if (typeof this.view === "undefined") {
            new YAHOO.mediaplayer.View(this);
        }
    }
    this.EventManager.onPlaylistUpdate.fire(playlist);
};
YAHOO.mediaplayer.Controller.prototype.onPlayStateChange = function(o) {
    var media = o.media;
    if (this.errorCount > 0 && o.newState === YAHOO.mediaplayer.MediaEngine.PlayState.PLAYING) {
        this.errorCount = 0;
    }
    if (media.mimeType == YAHOO.mediaplayer.Parser.MimeTypes.rhapsody) {
        switch (o.newState) {
        case YAHOO.mediaplayer.MediaEngine.PlayState.PLAYING:
            if (this.rhapsody.timeForStickwall()) {
                this.view.displayRhapsodyStickwall();
                window.setTimeout('YAHOO.mediaplayer.Controller.mediaengine.stop()', 200);
            }
            break;
        }
    }
    this.EventManager.onPlayStateChange.fire(o);
};
YAHOO.mediaplayer.Controller.prototype.onPlayRequest = function(media) {
    this.view.hideStickwall();
    this.EventManager.onPlayRequest.fire(media);
};
YAHOO.mediaplayer.Controller.prototype.onError = function(eventObj) {
    if (eventObj.type === YAHOO.mediaplayer.ErrorDefinitions.Types.CRITICAL) {
        this.mediaengine.stop();
    }
    if (eventObj.playback && eventObj.playback === true) {
        this.errorCount++;
        if (this.errorCount >= this.maxErrors) {
            this.errorCount = 0;
            this.mediaengine.stop();
            this.EventManager.onError.fire(new YAHOO.mediaplayer.Error("2", null));
        }
    }
    this.EventManager.onError.fire(eventObj);
    this.logger.logError(eventObj);
};
YAHOO.mediaplayer.Controller.prototype.onWindowUnload = function(eventObj) {
    var pluginIds = ['ymp-flash-engine', 'ymp-rhapsody-engine', 'ymp-qt-engine', 'ymp-wmpff3-engine', 'ymp-wmp-engine', 'ymp-flv-engine'];
    var len = pluginIds.length;
    var plugin = null;
    for (var i = 0; i < len; i++) {
        plugin = document.getElementById(pluginIds[i]);
        if (plugin) {
            plugin.parentNode.removeChild(plugin);
        }
    }
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.Parser = function(controller) {
    var subscribeToControllersEvents = [];
    YAHOO.mediaplayer.Parser.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.initController(controller);
    this.controller = this.getController();
    this.audioClass = "htrack";
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.Parser, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.Parser.prototype.refByName = 'parser';
YAHOO.mediaplayer.Parser.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Controller.' + this.refByName;
};
YAHOO.mediaplayer.Parser.TypeApplication = {
    'xspf+xml': true,
    'x-xspf+xml': true,
    'mpeg': true,
    'mp3': true,
    'rhapsody': true
};
YAHOO.mediaplayer.Parser.MimeTypes = {
    mp3: "audio/mpeg",
    wav: "audio/x-wav",
    wma: "audio/x-ms-wma",
    m4a: "audio/mp4",
    flv: "video/x-flv",
    xspf: "application/xspf+xml",
    m3u: "audio/x-mpegurl",
    m4u: "audio/x-mpegurl",
    asx: "video/x-ms-asf",
    pls: "audio/x-scpls",
    unknown: "audio/unknown",
    rhapsody: "audio/rhapsody",
    yahoo: "audio/yahoo"
};
YAHOO.mediaplayer.Parser.prototype.parse = function(domElement) {
    try {
        var mediaTracks = [];
        var anchorCollection = this.getAnchors(domElement);
        var anchorCollectionLen = anchorCollection.length;
        var mt = "";
        var type = "";
        var parts = "";
        for (var i = 0; i < anchorCollectionLen; i++) {
            if (YAHOO.ympyui.util.Dom.hasClass(anchorCollection[i], this.audioClass)) {
                type = String(anchorCollection[i].type).toLowerCase();
                parts = type.split('/');
                if (parts.length === 2 && (parts[0] === 'audio' || (parts[0] === 'application' && YAHOO.mediaplayer.Parser.TypeApplication[parts[1]] === true) || (parts[0] === 'video' && parts[1] === 'x-flv'))) {
                    if (this.checkForLocalHost(anchorCollection[i].href) === false && (i == 0 || anchorCollection[i].href !== anchorCollection[i - 1].href)) {
                        mediaTracks.push({
                            anchor: anchorCollection[i],
                            mimeType: type
                        });
                    }
                } else {
                    mt = this.getMimeTypeFromExtension(anchorCollection[i].href);
                    if (this.checkForLocalHost(anchorCollection[i].href) === false && (i == 0 || anchorCollection[i].href !== anchorCollection[i - 1].href)) {
                        mediaTracks.push({
                            anchor: anchorCollection[i],
                            mimeType: mt
                        });
                    }
                }
            }
        }
        if (mediaTracks.length <= 0) {
            for (var i = 0; i < anchorCollectionLen; i++) {
                type = String(anchorCollection[i].type).toLowerCase();
                parts = type.split('/');
                if (parts.length === 2 && (parts[0] === 'audio' || (parts[0] === 'application' && YAHOO.mediaplayer.Parser.TypeApplication[parts[1]] === true) || (parts[0] === 'video' && parts[1] === 'x-flv'))) {
                    if (this.checkForLocalHost(anchorCollection[i].href) === false && (i == 0 || anchorCollection[i].href !== anchorCollection[i - 1].href)) {
                        mediaTracks.push({
                            anchor: anchorCollection[i],
                            mimeType: type
                        });
                    }
                    continue;
                }
                if (YAHOO.ympyui.util.Dom.hasClass(anchorCollection[i], 'playthispage')) {
                    if (this.checkForLocalHost(anchorCollection[i].href) === false) {
                        mt = this.getMimeTypeFromExtension(anchorCollection[i].href);
                        if (mt == null || !YAHOO.mediaplayer.MediaResolver.PlaylistMimeTypes[mt]) {
                            mt = YAHOO.mediaplayer.Parser.MimeTypes.xspf
                        }
                        mediaTracks.push({
                            anchor: anchorCollection[i],
                            mimeType: mt
                        });
                    }
                    continue;
                }
                mt = this.getMimeTypeFromExtension(anchorCollection[i].href);
                if (mt != YAHOO.mediaplayer.Parser.MimeTypes["unknown"]) {
                    if (this.checkForLocalHost(anchorCollection[i].href) === false && (i == 0 || anchorCollection[i].href !== anchorCollection[i - 1].href)) {
                        mediaTracks.push({
                            anchor: anchorCollection[i],
                            mimeType: mt
                        });
                    }
                }
            }
        }
        mediaTracks = this.sortByTabIndex(mediaTracks);
        return mediaTracks;
    } catch(e) {
        return [];
    }
};
YAHOO.mediaplayer.Parser.prototype.getAnchors = function(domElement) {
    var anchorCollection = [];
    var rootNode = domElement;
    if (rootNode == null) {
        rootNode = document.body;
    }
    var aTags = rootNode.getElementsByTagName('a');
    for (var i = 0,
    ilen = aTags.length; i < ilen; i++) {
        anchorCollection[i] = aTags[i];
    }
    rootNode = null;
    aTags = null;
    return anchorCollection;
};
YAHOO.mediaplayer.Parser.prototype.getMimeTypeFromExtension = function(url) {
    try {
        if (YAHOO.mediaplayer.Rhapsody.regex.track.test(url)) {
            return YAHOO.mediaplayer.Parser.MimeTypes['rhapsody'];
        }
        if (YAHOO.mediaplayer.ymu.regex.track.test(url)) {
            return YAHOO.mediaplayer.Parser.MimeTypes['yahoo'];
        }
        var str = url;
        var questionIndex = str.indexOf('?');
        if (questionIndex != -1) {
            str = str.substring(0, questionIndex);
        }
        var dotIndex = str.lastIndexOf(".");
        var pattern = str.substring(dotIndex + 1, str.length).toLowerCase();
        for (var extension in YAHOO.mediaplayer.Parser.MimeTypes) {
            if (pattern === extension) {
                return YAHOO.mediaplayer.Parser.MimeTypes[extension];
            }
        }
        return YAHOO.mediaplayer.Parser.MimeTypes["unknown"];
    } catch(e) {
        return YAHOO.mediaplayer.Parser.MimeTypes["unknown"];
    }
};
YAHOO.mediaplayer.Parser.prototype.sortByTabIndex = function(mediaTracks) {
    try {
        var temp = null;
        var tbidx1 = null;
        var atbidx2 = null;
        var len = mediaTracks.length;
        for (var i = 0; i < len; i++) {
            for (j = i + 1; j < len; j++) {
                tbidx1 = mediaTracks[i].anchor.tabIndex;
                tbidx2 = mediaTracks[j].anchor.tabIndex;
                if (tbidx2 > 0 && (tbidx1 > tbidx2)) {
                    temp = mediaTracks[i];
                    mediaTracks[i] = mediaTracks[j];
                    mediaTracks[j] = temp;
                }
            }
        }
        return mediaTracks;
    } catch(e) {
        return null;
    }
};
YAHOO.mediaplayer.Parser.prototype.checkForLocalHost = function(url) {
    return (url.toLowerCase().indexOf("http://localhost") >= 0);
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.PlaylistManager = function(controller) {
    var subscribeToControllersEvents = ['onPlayRequest', 'onPauseRequest', 'onStopRequest', 'onPreviousRequest', 'onNextRequest', 'onPlayStateChange', 'onMediaUpdate', 'onPlaylistUpdate'];
    YAHOO.mediaplayer.PlaylistManager.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.EventManager.addEvents(['onPlaylistUpdate', 'onNextRequest', 'onCurrentMediaSet']);
    this.initController(controller);
    this.controller = this.getController();
    this.playlistArray = [];
    this.allMedia = [];
    this.currentIndex = -1;
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.PlaylistManager, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.PlaylistManager.prototype.refByName = 'playlistmanager';
YAHOO.mediaplayer.PlaylistManager.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Controller.' + this.refByName;
};
YAHOO.mediaplayer.PlaylistManager.prototype.add = function(mediaAnchorArray) {
    var media2Resolve = [];
    var indexWhereAdded = 0;
    if (this.playlistArray.length > 0) {
        indexWhereAdded = this.playlistArray.length - 1;
    }
    if (mediaAnchorArray == null) {
        return;
    }
    for (var i = 0,
    ilen = mediaAnchorArray.length,
    newMedia; i < ilen; i++) {
        if (YAHOO.mediaplayer.MediaResolver.PlaylistMimeTypes[mediaAnchorArray[i].mimeType]) {
            newMedia = this.createMediaObject(mediaAnchorArray[i], "Playlist");
        } else {
            newMedia = this.createMediaObject(mediaAnchorArray[i], "Track");
        }
        media2Resolve.push(newMedia);
        this.playlistArray.push(newMedia);
    }
    this.fireupdateAndResolve(media2Resolve);
    return indexWhereAdded;
};
YAHOO.mediaplayer.PlaylistManager.prototype.fireupdateAndResolve = function(mediaArray) {
    this.EventManager.onPlaylistUpdate.fire(this.playlistArray);
    for (var i = 0,
    ilen = mediaArray.length; i < ilen; i++) {
        this.controller.mediaresolver.resolve(mediaArray[i]);
    }
    this.controller.mediaresolver.resolveRhapsodyMedia();
    this.controller.mediaresolver.resolveYmuMedia();
};
YAHOO.mediaplayer.PlaylistManager.prototype.createMediaObject = function(obj, type) {
    var temp = null;
    if (type === "Track") {
        temp = new YAHOO.mediaplayer.Media.Track(this.controller);
    } else if (type === "Playlist") {
        temp = new YAHOO.mediaplayer.Playlist(this.controller);
    }
    temp.anchor = obj.anchor;
    temp.mimeType = obj.mimeType;
    this.allMedia[temp.id] = temp;
    return temp;
};
YAHOO.mediaplayer.PlaylistManager.prototype.getMediaById = function(id) {
    if (id == null) {
        return null;
    }
    return this.allMedia[id];
};
YAHOO.mediaplayer.PlaylistManager.prototype.getMediaIndex = function(media) {
    if (media == null) {
        return - 1;
    }
    for (var i = 0,
    ilen = this.playlistArray.length; i < ilen; i++) {
        if (this.playlistArray[i] == media) {
            return i;
        }
    }
};
YAHOO.mediaplayer.PlaylistManager.prototype.onPlayRequest = function(o) {
    var media, seek;
    if (o != null) {
        media = o.media;
        seek = o.seek;
    }
    var mediaIndex = -1;
    if (media == null) {
        mediaIndex = this.currentIndex;
        media = this.playlistArray[this.currentIndex];
    }
    if (media instanceof YAHOO.mediaplayer.Playlist) {
        if (media.mediaArray.length > 0) {
            var firstMediaIndex = this.getMediaIndex(media.mediaArray[0]);
            if (this.currentIndex >= firstMediaIndex && this.currentIndex < firstMediaIndex + media.mediaArray.length) {
                mediaIndex = this.currentIndex;
            } else {
                mediaIndex = firstMediaIndex;
            }
        }
    } else if (mediaIndex == -1) {
        mediaIndex = this.getMediaIndex(media);
    }
    if (mediaIndex == -1) {
        return;
    }
    if (this.currentIndex != mediaIndex) {
        this.currentIndex = mediaIndex;
        this.EventManager.onCurrentMediaSet.fire(this.playlistArray[this.currentIndex]);
    }
    YAHOO.mediaplayer.Controller.mediaengine.play(this.playlistArray[this.currentIndex], seek);
};
YAHOO.mediaplayer.PlaylistManager.prototype.onPauseRequest = function() {
    YAHOO.mediaplayer.Controller.mediaengine.pause();
};
YAHOO.mediaplayer.PlaylistManager.prototype.onStopRequest = function() {
    YAHOO.mediaplayer.Controller.mediaengine.stop();
};
YAHOO.mediaplayer.PlaylistManager.prototype.onNextRequest = function() {
    if (this.currentIndex + 1 < this.playlistArray.length) {
        this.currentIndex++;
        var media = this.playlistArray[this.currentIndex];
        this.EventManager.onCurrentMediaSet.fire(media);
        var currentEngineState = YAHOO.mediaplayer.Controller.mediaengine.currentPlayState;
        if (currentEngineState != YAHOO.mediaplayer.MediaEngine.PlayState.PAUSED && currentEngineState != YAHOO.mediaplayer.MediaEngine.PlayState.ENDED && currentEngineState != YAHOO.mediaplayer.MediaEngine.PlayState.STOPPED) {
            YAHOO.mediaplayer.Controller.mediaengine.play(media);
        }
    }
};
YAHOO.mediaplayer.PlaylistManager.prototype.onPreviousRequest = function() {
    if (this.currentIndex - 1 >= 0) {
        this.currentIndex--;
        var media = this.playlistArray[this.currentIndex];
        this.EventManager.onCurrentMediaSet.fire(media);
        var currentEngineState = YAHOO.mediaplayer.Controller.mediaengine.currentPlayState;
        if (currentEngineState != YAHOO.mediaplayer.MediaEngine.PlayState.PAUSED && currentEngineState != YAHOO.mediaplayer.MediaEngine.PlayState.ENDED && currentEngineState != YAHOO.mediaplayer.MediaEngine.PlayState.STOPPED) {
            YAHOO.mediaplayer.Controller.mediaengine.play(media);
        }
    }
};
YAHOO.mediaplayer.PlaylistManager.prototype.onPlayStateChange = function(o) {
    switch (o.newState) {
    case YAHOO.mediaplayer.MediaEngine.PlayState.ENDED:
        if (this.currentIndex + 1 < this.playlistArray.length && YAHOO.mediaplayer.YMPParams.autoadvance === true) {
            this.currentIndex++;
            var media = this.playlistArray[this.currentIndex];
            this.EventManager.onCurrentMediaSet.fire(media);
            YAHOO.mediaplayer.Controller.mediaengine.play(media);
        }
        break;
    }
};
YAHOO.mediaplayer.PlaylistManager.prototype.onMediaUpdate = function(media) {
    if (this.currentIndex == -1) {
        var mediaIndex = this.getMediaIndex(media);
        if (mediaIndex != -1) {
            this.currentIndex = mediaIndex;
            this.EventManager.onCurrentMediaSet.fire(this.playlistArray[this.currentIndex]);
            if (YAHOO.mediaplayer.YMPParams.autoplay === true) {
                this.controller.onPlayRequest(this.playlistArray[this.currentIndex]);
            }
        }
    }
};
YAHOO.mediaplayer.PlaylistManager.prototype.onPlaylistUpdate = function(playlist) {
    if (playlist instanceof YAHOO.mediaplayer.Playlist) {
        var playlistIndex = -1;
        for (var i = 0,
        ilen = this.playlistArray.length; i < ilen; i++) {
            if (this.playlistArray[i] == playlist) {
                playlistIndex = i;
            }
        }
        if (playlistIndex != -1) {
            this.playlistArray.splice(playlistIndex, 1);
            for (var i = playlist.mediaArray.length - 1; i >= 0; i--) {
                this.playlistArray.splice(playlistIndex, 0, playlist.mediaArray[i]);
                this.allMedia[playlist.mediaArray[i].id] = playlist.mediaArray[i];
            }
            if (this.currentIndex >= playlistIndex) {
                this.currentIndex += playlist.mediaArray.length - 1;
            }
        }
    }
};
YAHOO.mediaplayer.PlaylistManager.prototype.clear = function() {
    this.playlistArray = [];
    this.currentIndex = -1;
    this.EventManager.onPlaylistUpdate.fire(this.playlistArray);
};
YAHOO.mediaplayer.PlaylistManager.prototype.insert = function(mediaTracks, index) {
    if (this.playlistArray[index]) {
        while (index > 0 && this.playlistArray[index].parent !== null && this.playlistArray[index - 1].parent !== null) {
            index--;
        }
        var len = mediaTracks.length;
        var newMedia = null;
        var media2Resolve = [];
        for (var i = 0; i < len; i++) {
            if (YAHOO.mediaplayer.MediaResolver.PlaylistMimeTypes[mediaTracks[i].mimeType]) {
                newMedia = this.createMediaObject(mediaTracks[i], "Playlist");
            } else {
                newMedia = this.createMediaObject(mediaTracks[i], "Track");
            }
            media2Resolve.push(newMedia);
            this.playlistArray.splice(index + i, 0, newMedia);
        }
        this.fireupdateAndResolve(media2Resolve);
        return index;
    } else {
        this.add(mediaTracks);
    }
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.MediaResolver = function(controller) {
    var subscribeToControllersEvents = [];
    YAHOO.mediaplayer.MediaResolver.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.EventManager.addEvents(['onPlaylistUpdate', 'onError']);
    this.initController(controller);
    this.controller = this.getController();
    this.rhapsodyMediaCollection = [];
    this.ymuMediaCollection = [];
    this.tempRhapMedia = [];
    this.tempYmuMedia = [];
    this.retries = 0;
    this.maxRetries = 2;
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.MediaResolver, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.MediaResolver.prototype.refByName = 'mediaresolver';
YAHOO.mediaplayer.MediaResolver.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Controller.' + this.refByName;
};
YAHOO.mediaplayer.MediaResolver.PlaylistMimeTypes = {
    "application/xspf+xml": true,
    "application/x-xspf+xml": true,
    "audio/x-mpegurl": true,
    "audio/x-scpls": true,
    "audio/pn-realaudio": true,
    "video/x-ms-asf": true,
    "video/ms-asf": true
};
YAHOO.mediaplayer.MediaResolver.prototype.isSimpleMedia = function(mimeType) {
    if (mimeType && mimeType.length > 0) {
        if (!YAHOO.mediaplayer.MediaResolver.PlaylistMimeTypes[mimeType]) {
            return true;
        }
    } else {
        return false;
    }
};
YAHOO.mediaplayer.MediaResolver.prototype.resolve = function(media) {
    var temp;
    if (YAHOO.mediaplayer.MediaResolver.PlaylistMimeTypes[media.mimeType]) {
        YAHOO.mediaplayer.SWFProxy.getPlayThisPage(media.anchor.href, {
            success: YAHOO.mediaplayer.Controller.mediaresolver.onPTPSuccess,
            failure: YAHOO.mediaplayer.Controller.mediaresolver.onPTPFail,
            argument: media,
            scope: this
        });
    } else {
        var props = this.getSimpleMediaProperties(media);
        if (media.mimeType === YAHOO.mediaplayer.Parser.MimeTypes.rhapsody) {
            var rid = media.anchor.getAttribute("rid");
            var match = YAHOO.mediaplayer.Rhapsody.regex.track.exec(media.anchor.href);
            if (typeof(rid) == "string" && rid.length > 0) {
                props.token = rid;
            } else {
                props.token = match[5];
            }
            temp = media;
            this.rhapsodyMediaCollection[this.rhapsodyMediaCollection.length] = temp;
        }
        if (media.mimeType === YAHOO.mediaplayer.Parser.MimeTypes.yahoo) {
            var match = YAHOO.mediaplayer.ymu.regex.track.exec(media.anchor.href);
            props.token = match[5];
            props.yTrackID = props.token;
            temp = media;
            this.ymuMediaCollection[this.ymuMediaCollection.length] = temp;
        }
        media.setProperties(props);
    }
};
YAHOO.mediaplayer.MediaResolver.prototype.resolveRhapsodyMedia = function() {
    if (this.rhapsodyMediaCollection.length > 0) {
        var match = null;
        var rcidsArray = [];
        var len = this.rhapsodyMediaCollection.length;
        for (var i = 0; i < len; i++) {
            rcidsArray.push(this.rhapsodyMediaCollection[i].token);
        }
        YAHOO.mediaplayer.SWFProxy.getRhapMetadata(rcidsArray, {
            success: YAHOO.mediaplayer.Controller.mediaresolver.onRhapsodyMetadataReady,
            failure: YAHOO.mediaplayer.Controller.mediaresolver.onRhapsodyMetadataFail,
            argument: null,
            scope: this
        });
    }
};
YAHOO.mediaplayer.MediaResolver.prototype.resolveYmuMedia = function() {
    if (this.ymuMediaCollection.length > 0) {
        var rcidsArray = [];
        var len = this.ymuMediaCollection.length;
        for (var i = 0; i < len; i++) {
            rcidsArray.push(this.ymuMediaCollection[i].token);
        }
        YAHOO.mediaplayer.SWFProxy.getWsapiMetadata(rcidsArray, {
            success: YAHOO.mediaplayer.Controller.mediaresolver.onWsapiMetadataReady,
            failure: YAHOO.mediaplayer.Controller.mediaresolver.onWsapiMetadataFail,
            argument: null,
            scope: this
        });
    }
};
YAHOO.mediaplayer.MediaResolver.prototype.onRhapsodyMetadataReady = function(o) {
    var respText = o.responseText;
    var jsonObject = eval('(' + respText + ')');
    if (jsonObject && jsonObject.status && jsonObject.status.success === true && jsonObject.status.blocked === false) {
        var data = jsonObject.data;
        var len1 = this.rhapsodyMediaCollection.length;
        var i = 0;
        var j = 0;
        while (i < len1) {
            if (this.rhapsodyMediaCollection[i].token.toLowerCase() === jsonObject.data[j].trackId.toLowerCase()) {
                var props = {};
                if (typeof(data[j].displayArtistName) === "string" && data[j].displayArtistName !== "") {
                    props.artistName = data[j].displayArtistName;
                }
                if (typeof(data[j].displayAlbumName) === "string" && data[j].displayAlbumName !== "") {
                    props.albumName = data[j].displayAlbumName;
                }
                if (typeof(data[j].name) === "string" && data[j].name !== "") {
                    props.title = data[j].name;
                }
                if (data[j].album && typeof(data[j].album.albumArt162X162Url) === "string" && data[j].album.albumArt162X162Url !== "") {
                    props.albumArt = data[j].album.albumArt162X162Url;
                }
                if (typeof(data[j].purchaseInfo.url) === "string" && data[j].purchaseInfo.url.length > 0) {
                    props.buyURL = "http://mp3.rhapsody.com/goto?rcid=" + jsonObject.data[j].trackId.toLowerCase() + "&pcode=" + YAHOO.mediaplayer.YMPParams.rhappcode + "&ocode=" + YAHOO.mediaplayer.YMPParams.rhappcode + "&cpath=buylink&rsrc=" + ((YAHOO.mediaplayer.YMPParams.ypartner && YAHOO.mediaplayer.YMPParams.ypartner.length > 0) ? YAHOO.mediaplayer.YMPParams.ypartner: 'yahoo');
                }
                this.rhapsodyMediaCollection[i].setProperties(props);
                i++;
                j++;
            } else {
                i++;
            }
        }
    } else {
        var errorArgs = {};
        if (jsonObject && jsonObject.status && jsonObject.status.errorMessage.length > 0) {
            errorArgs.displayMessageArgs = [jsonObject.status.errorMessage];
        }
        var errorObj = new YAHOO.mediaplayer.Error("10", errorArgs);
        this.EventManager.onError.fire(errorObj);
    }
    this.rhapsodyMediaCollection = [];
};
YAHOO.mediaplayer.MediaResolver.prototype.onRhapsodyMetadataFail = function() {
    for (var i = 0,
    ilen = this.rhapsodyMediaCollection.length,
    errorObj; i < ilen; i++) {
        errorObj = new YAHOO.mediaplayer.Error("10", {
            displayMessageArgs: ['']
        });
        errorObj.media = this.rhapsodyMediaCollection[i];
        errorObj.display = false;
        this.EventManager.onError.fire(errorObj);
    }
    this.rhapsodyMediaCollection = [];
};
YAHOO.mediaplayer.MediaResolver.prototype.onWsapiMetadataReady = function(o) {
    var jsonObject = eval('(' + o.responseText + ')');
    if (jsonObject && typeof(jsonObject) === "object") {
        var len1 = this.ymuMediaCollection.length;
        var i = 0;
        var j = 0;
        while (i < len1) {
            var retTrack = (YAHOO.mediaplayer.Util.isArray(jsonObject.Tracks.Track)) ? jsonObject.Tracks.Track[j] : jsonObject.Tracks.Track;
            if (retTrack && typeof(retTrack) === "object") {
                if (this.ymuMediaCollection[i].token === retTrack.id) {
                    var props = {};
                    var mappingFailure = false;
                    if (retTrack.Artist) {
                        var artistObj = null;
                        if (YAHOO.mediaplayer.Util.isArray(retTrack.Artist)) {
                            artistObj = retTrack.Artist[0];
                        } else {
                            artistObj = retTrack.Artist;
                        }
                        if (artistObj && typeof(artistObj.name) === "string" && artistObj.name.length > 0) {
                            props.artistName = artistObj.name;
                            props.yArtistID = artistObj.id;
                        }
                        artistObj = null;
                    }
                    if (retTrack.Album && retTrack.Album.Release && typeof(retTrack.Album.Release.title) === "string" && retTrack.Album.Release.title.length > 0) {
                        props.albumName = retTrack.Album.Release.title;
                        props.yAlbumID = retTrack.Album.Release.id;
                    }
                    if (typeof(retTrack.title) === "string" && retTrack.title.length > 0) {
                        props.title = retTrack.title;
                    }
                    if (retTrack.Album && retTrack.Album.Release && retTrack.Album.Release.Image) {
                        if (YAHOO.mediaplayer.Util.isArray(retTrack.Album.Release.Image)) {
                            for (var k = 0; k < retTrack.Album.Release.Image.length; k++) {
                                if (retTrack.Album.Release.Image[k].size === "40") {
                                    props.albumArt = retTrack.Album.Release.Image[k].url;
                                    break;
                                }
                            }
                        } else {
                            props.albumArt = retTrack.Album.Release.Image.url;
                        }
                    }
                    if (retTrack.Video && retTrack.Video.id) {
                        props.yVideoID = retTrack.Video.id;
                    }
                    if (retTrack.Mappings && typeof(retTrack.Mappings) === "object") {
                        var map = null;
                        if (YAHOO.mediaplayer.Util.isArray(retTrack.Mappings.Mapping)) {
                            for (var k = 0; k < retTrack.Mappings.Mapping.length; k++) {
                                if (retTrack.Mappings.Mapping[k].catalogID === YAHOO.mediaplayer.ymu.rhapsodyCatalogId) {
                                    map = retTrack.Mappings.Mapping[k];
                                    break;
                                }
                            }
                        } else {
                            map = retTrack.Mappings.Mapping;
                        }
                        if (map && typeof(map) === "object") {
                            props.token = map.id;
                            var rights = parseInt(map.rights);
                            if (!isNaN(rights)) {
                                var downloadFlag = parseInt(YAHOO.mediaplayer.ymu.rightsFlags.DOWNLOAD);
                                if ((rights & downloadFlag) != 0) {
                                    var buyURL = "http://mp3.rhapsody.com/goto?rcid=" + map.id.toLowerCase() + "&pcode=" + YAHOO.mediaplayer.YMPParams.rhappcode + "&ocode=" + YAHOO.mediaplayer.YMPParams.rhappcode + "&cpath=buylink&rsrc=" + ((YAHOO.mediaplayer.YMPParams.ypartner && YAHOO.mediaplayer.YMPParams.ypartner.length > 0) ? YAHOO.mediaplayer.YMPParams.ypartner: 'yahoo');
                                    props.buyURL = buyURL;
                                }
                            }
                        } else {
                            mappingFailure = true;
                        }
                    } else {
                        mappingFailure = true;
                    }
                    if (mappingFailure === true) {
                        props.token = "";
                        errorObj = new YAHOO.mediaplayer.Error("12", {
                            displayMessageArgs: ['']
                        });
                        errorObj.media = this.ymuMediaCollection[i];
                        errorObj.display = false;
                        this.EventManager.onError.fire(errorObj);
                    }
                    this.ymuMediaCollection[i].setProperties(props);
                    i++;
                    j++;
                } else {
                    errorObj = new YAHOO.mediaplayer.Error("12", {
                        displayMessageArgs: ['']
                    });
                    errorObj.media = this.ymuMediaCollection[i];
                    errorObj.media.setProperties({
                        token: ""
                    });
                    errorObj.display = false;
                    this.EventManager.onError.fire(errorObj);
                    i++;
                }
            } else {
                errorObj = new YAHOO.mediaplayer.Error("12", {
                    displayMessageArgs: ['']
                });
                errorObj.media = this.ymuMediaCollection[i];
                errorObj.media.setProperties({
                    token: ""
                });
                errorObj.display = false;
                this.EventManager.onError.fire(errorObj);
                i++;
            }
        }
    } else {
        var errorArgs = {};
        var errorObj = new YAHOO.mediaplayer.Error("12", errorArgs);
        this.EventManager.onError.fire(errorObj);
    }
    this.ymuMediaCollection = [];
};
YAHOO.mediaplayer.MediaResolver.prototype.onWsapiMetadataFail = function(o) {
    for (var i = 0,
    ilen = this.ymuMediaCollection.length,
    errorObj; i < ilen; i++) {
        errorObj = new YAHOO.mediaplayer.Error("12", {
            displayMessageArgs: ['']
        });
        errorObj.media = this.ymuMediaCollection[i];
        errorObj.media.token = "";
        errorObj.display = false;
        this.EventManager.onError.fire(errorObj);
    }
    this.ymuMediaCollection = [];
};
YAHOO.mediaplayer.MediaResolver.prototype.onPTPSuccess = function(o) {
    this.tempRhapMedia = [];
    this.tempYmuMedia = [];
    try {
        var json = eval('(' + o.responseText + ')');
        var playlist = o.argument;
        if (json.playlist != null) {
            var temp = json.playlist.title;
            if (typeof(temp) === "string") {
                playlist.title = temp;
            }
            temp = json.playlist.info;
            if (typeof(temp) === "string") {
                playlist.info = temp;
            }
            playlist.url = playlist.anchor.href;
            if (json.playlist.track.length <= 0) {
                var errorObj = new YAHOO.mediaplayer.Error("5", null);
                errorObj.media = playlist;
                this.EventManager.onError.fire(errorObj);
            }
            for (var i = 0,
            ilen = json.playlist.track.length; i < ilen; i++) {
                var temp = new YAHOO.mediaplayer.Media.Track(YAHOO.mediaplayer.Controller);
                if (json.playlist.track[i].location.constructor == Array && json.playlist.track[i].location.length > 0) {
                    temp.mimeType = YAHOO.mediaplayer.Controller.parser.getMimeTypeFromExtension(json.playlist.track[i].location[0]);
                }
                temp.parent = playlist;
                playlist.mediaArray.push(temp);
            }
            this.EventManager.onPlaylistUpdate.fire(playlist);
            for (var i = 0,
            ilen = json.playlist.track.length; i < ilen; i++) {
                var props = {};
                if (json.playlist.track[i].location && json.playlist.track[i].location.constructor == Array && json.playlist.track[i].location.length > 0) {
                    props.token = json.playlist.track[i].location[0];
                }
                if (json.playlist.track[i].type && json.playlist.track[i].type.constructor == Array && json.playlist.track[i].type.length > 0) {
                    props.mimeType = json.playlist.track[i].type[0];
                }
                if (typeof(json.playlist.track[i].title) === "string") {
                    props.title = json.playlist.track[i].title;
                }
                if (props.title == null || props.title == "") {
                    props.title = decodeURIComponent(props.token.substring(props.token.lastIndexOf("/") + 1, props.token.length));
                }
                if (typeof(json.playlist.track[i].creator) === "string") {
                    props.artistName = json.playlist.track[i].creator;
                }
                if (typeof(json.playlist.track[i].album) === "string") {
                    props.albumName = json.playlist.track[i].album;
                }
                if (typeof(json.playlist.track[i].image) === "string") {
                    props.albumArt = json.playlist.track[i].image;
                }
                if (props.mimeType === "audio/rhapsody" || props.mimeType === "audio/yahoo") {
                    if (props.mimeType === "audio/rhapsody") {
                        match = YAHOO.mediaplayer.Rhapsody.regex.track.exec(props.token);
                        this.tempRhapMedia[this.tempRhapMedia.length] = playlist.mediaArray[i];
                        props.token = match[5];
                    } else {
                        match = YAHOO.mediaplayer.ymu.regex.track.exec(props.token);
                        this.tempYmuMedia[this.tempYmuMedia.length] = playlist.mediaArray[i];
                        props.token = match[5];
                    }
                }
                playlist.mediaArray[i].setProperties(props);
            }
            this.rhapCheck();
            this.ymuCheck();
        } else {
            var errorObj = new YAHOO.mediaplayer.Error("5", null);
            errorObj.media = playlist;
            this.EventManager.onError.fire(errorObj);
        }
    } catch(e) {}
};
YAHOO.mediaplayer.MediaResolver.prototype.rhapCheck = function() {
    if (this.tempRhapMedia.length > 0 && this.retries < this.maxRetries) {
        if (this.rhapsodyMediaCollection.length > 0) {
            this.retries++;
            setTimeout("YAHOO.mediaplayer.Controller.mediaresolver.rhapCheck()", 500);
        } else {
            this.rhapsodyMediaCollection = this.tempRhapMedia;
            this.resolveRhapsodyMedia();
        }
    }
};
YAHOO.mediaplayer.MediaResolver.prototype.ymuCheck = function(arr) {
    if (this.tempYmuMedia.length > 0 && this.retries < this.maxRetries) {
        if (this.ymuMediaCollection.length > 0) {
            this.retries++;
            setTimeout("YAHOO.mediaplayer.Controller.mediaresolver.ymuCheck()", 500);
        } else {
            this.ymuMediaCollection = this.tempYmuMedia;
            this.resolveYmuMedia();
        }
    }
};
YAHOO.mediaplayer.MediaResolver.prototype.onPTPFail = function(o) {
    var errorObj = new YAHOO.mediaplayer.Error("4", null);
    errorObj.media = o.argument;
    this.EventManager.onError.fire(errorObj);
};
YAHOO.mediaplayer.MediaResolver.prototype.getSimpleMediaProperties = function(media) {
    try {
        var props = {};
        var href = media.anchor.href.trim();
        if (href.substr(0, 24) === "http://us.lrd.yahoo.com/") {
            var intStart = href.indexOf("**http");
            if (typeof intStart == "number" && intStart > 0) {
                intStart += 2;
                href = href.substr(intStart, href.length - intStart);
                href = decodeURIComponent(href);
            }
        }
        props.token = href;
        if (props.token == null || props.token == "") {
            return null;
        }
        props.title = media.anchor.getAttribute('title');
        if (props.title == null || props.title == "") {
            props.title = this.parseTextNode(media.anchor);
            if (props.title == "") {
                props.title = decodeURIComponent(media.anchor.href.substring(media.anchor.href.lastIndexOf("/") + 1, media.anchor.href.length));
            }
        }
        props.albumName = media.anchor.getAttribute('album');
        if (props.albumName == null) {
            props.albumName = "";
        }
        props.artistName = media.anchor.getAttribute('artist');
        if (props.artistName == null) {
            props.artistName = "";
        }
        var albumImg = media.anchor.getElementsByTagName('img')[0];
        if (albumImg != null) {
            props.albumArt = albumImg.src;
        }
        return props;
    } catch(e) {
        return null;
    }
};
YAHOO.mediaplayer.MediaResolver.prototype.parseTextNode = function(elm) {
    try {
        if (typeof(elm.innerText) === 'string') {
            return elm.innerText;
        }
        if (elm.nodeType == 3) {
            return elm.nodeValue;
        }
        var textNodes = [],
        i = 0;
        while (elm.childNodes[i]) {
            textNodes.push(this.parseTextNode(elm.childNodes[i++]));
        }
        return textNodes.join('');
    } catch(e) {
        return "";
    }
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.Playlist = function(controller, obj) {
    var subscribeToControllersEvents = [];
    YAHOO.mediaplayer.Playlist.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.initController(controller);
    this.controller = this.getController();
    this.id = Number.GUID(Math.getRnd(0, 1000));
    this.title = "";
    this.url = "";
    this.creator = "";
    this.anchor = null;
    this.mimeType = "";
    this.info = "";
    this.mediaArray = [];
    for (var props in obj) {
        this[props] = obj[props];
    }
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.Playlist, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.Playlist.prototype.refByName = 'playlist';
YAHOO.mediaplayer.Playlist.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Controller.' + this.refByName;
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.Media = function(controller) {
    var subscribeToControllersEvents = [];
    YAHOO.mediaplayer.Media.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.EventManager.addEvents(['onMediaUpdate']);
    this.initController(controller);
    this.controller = this.getController();
    this.id = Number.GUID(Math.getRnd(0, 1000));
    this.token = null;
    this.title = "";
    this.mimeType = "";
    this.anchor = null;
    this.parent = null;
    this.buyURL = "";
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.Media, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.Media.prototype.refByName = 'media';
YAHOO.mediaplayer.Media.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Controller.' + this.refByName;
};
YAHOO.mediaplayer.Media.prototype.setProperties = function(obj) {
    for (var props in obj) {
        this[props] = obj[props];
    }
    this.EventManager.onMediaUpdate.fire(this);
};
YAHOO.namespace('YAHOO.mediaplayer.Media');
YAHOO.mediaplayer.Media.Track = function(controller) {
    YAHOO.mediaplayer.Media.Track.superclass.constructor.call(this, controller);
    this.albumName = "";
    this.artistName = "";
    this.albumArt = "";
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.Media.Track, YAHOO.mediaplayer.Media);
YAHOO.mediaplayer.Media.Track.prototype.refByName = 'track';
YAHOO.mediaplayer.Media.Track.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Controller.' + this.refByName;
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.MediaEngine = function(controller) {
    var subscribeToControllersEvents = ['onPlayStateChange', 'onVolumeChangeRequest'];
    YAHOO.mediaplayer.MediaEngine.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.EventManager.addEvents(['onPlayStateChange', 'onMediaProgress', 'onError', 'onVolumeChange']);
    this.initController(controller);
    this.controller = this.getController();
    this.currentEngine = null;
    this.currentMedia = null;
    this.players = [];
    this.currentPlayState = 0;
    this.progressIntervalID = null;
    this.vol = YAHOO.mediaplayer.YMPParams.volume ? YAHOO.mediaplayer.YMPParams.volume: 0.5;
    this.playbackTimeout = 20000;
    this.playbackTimeoutID = null;
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.MediaEngine, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.MediaEngine.prototype.refByName = 'mediaengine';
YAHOO.mediaplayer.MediaEngine.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Controller.' + this.refByName;
};
YAHOO.mediaplayer.MediaEngine.PlayState = {
    STOPPED: 0,
    PAUSED: 1,
    PLAYING: 2,
    BUFFERING: 3,
    ENDED: 4
};
YAHOO.mediaplayer.MediaEngine.prototype.ErrorState = {};
YAHOO.mediaplayer.MediaEngine.prototype.play = function(media, seek) {
    if (this.currentEngine != null) {
        if (this.currentMedia == media) {
            this.currentEngine.setVolume(this.vol, true);
            this.currentEngine.play(media, seek);
            this.startPlaybackTimeout();
            return;
        } else {
            if (this.currentPlayState != YAHOO.mediaplayer.MediaEngine.PlayState.STOPPED && this.currentPlayState != YAHOO.mediaplayer.MediaEngine.PlayState.ENDED) {
                this.currentEngine.stop();
            }
        }
    }
    this.currentMedia = media;
    this.setMediaEngine(media);
    if (this.currentEngine != null) {
        try {
            this.currentEngine.setVolume(this.vol, true);
            this.currentEngine.play(media, seek);
            this.startPlaybackTimeout();
        } catch(e) {}
    } else {
        this.changePlayState(YAHOO.mediaplayer.MediaEngine.PlayState.ENDED);
    }
};
YAHOO.mediaplayer.MediaEngine.prototype.pause = function() {
    try {
        this.currentEngine.pause();
    } catch(e) {}
};
YAHOO.mediaplayer.MediaEngine.prototype.stop = function() {
    try {
        this.currentEngine.stop();
        this.clearPlaybackTimeout();
    } catch(e) {}
};
YAHOO.mediaplayer.MediaEngine.prototype.startPlaybackTimeout = function() {
    this.clearPlaybackTimeout();
    this.playbackTimeoutID = window.setTimeout(this.toString() + ".checkSongPlayback()", this.playbackTimeout);
};
YAHOO.mediaplayer.MediaEngine.prototype.clearPlaybackTimeout = function() {
    if (this.playbackTimeoutID != null) {
        window.clearTimeout(this.playbackTimeoutID);
        this.playbackTimeoutID = null;
    }
};
YAHOO.mediaplayer.MediaEngine.prototype.checkSongPlayback = function() {
    var elapsed = this.currentEngine.getElapsed();
    if (elapsed <= 0) {
        var errorObj = new YAHOO.mediaplayer.Error("11");
        errorObj.media = this.currentMedia;
        this.EventManager.onError.fire(errorObj);
        this.currentEngine.stop(true);
        if (this.currentEngine.id !== "ymp-flash-engine" && this.currentEngine.id !== "ymp-flv-engine") {
            this.currentEngine.currentState = YAHOO.mediaplayer.MediaEngine.PlayState.ENDED;
            this.changePlayState(YAHOO.mediaplayer.MediaEngine.PlayState.ENDED);
        }
    }
};
YAHOO.mediaplayer.MediaEngine.prototype.getElapsed = function() {
    if (this.currentEngine != null) {
        return this.currentEngine.getElapsed();
    }
    return 0;
};
YAHOO.mediaplayer.MediaEngine.prototype.getDuration = function() {
    if (this.currentEngine != null) {
        return this.currentEngine.getDuration();
    }
    return 0;
};
YAHOO.mediaplayer.MediaEngine.prototype.fireProgress = function() {
    var elapsed = this.currentEngine.getElapsed();
    var duration = this.currentEngine.getDuration();
    this.EventManager.onMediaProgress.fire({
        elapsed: elapsed,
        duration: duration
    });
};
YAHOO.mediaplayer.MediaEngine.prototype.changePlayState = function(newState) {
    if (newState != this.currentPlayState) {
        var oldState = this.currentPlayState;
        this.currentPlayState = newState;
        this.EventManager.onPlayStateChange.fire({
            media: this.currentMedia,
            oldState: oldState,
            newState: newState
        });
    }
};
YAHOO.mediaplayer.MediaEngine.prototype.handleError = function(errorCode, args) {
    this.clearPlaybackTimeout();
    var errorObj = new YAHOO.mediaplayer.Error(errorCode, args);
    errorObj.media = this.currentMedia;
    this.EventManager.onError.fire(errorObj);
};
YAHOO.mediaplayer.MediaEngine.prototype.setMediaEngine = function(media) {
    switch (media.mimeType) {
    case "audio/mp3":
    case "audio/mpeg":
    case "audio/mpeg3":
    case "audio/x-mpeg-3":
        switch (YAHOO.mediaplayer.Util.OS) {
        case "Windows":
            switch (YAHOO.mediaplayer.Util.BROWSER) {
            case "Firefox":
            case "Safari":
            case "Netscape":
            case "Mozilla":
                this.currentEngine = this.getAvailableMediaEngine(['FlashEngine', 'QTEngine', 'WMPEngine']);
                break;
            case "MSIE":
                this.currentEngine = this.getAvailableMediaEngine(['FlashEngine', 'WMPEngine', 'QTEngine']);
                break;
            case "Opera":
                this.currentEngine = this.getAvailableMediaEngine(['FlashEngine', 'QTEngine']);
                break;
            default:
                this.currentEngine = this.getAvailableMediaEngine(['FlashEngine', 'QTEngine', 'WMPEngine']);
                break;
            }
            break;
        case "Mac":
            switch (YAHOO.mediaplayer.Util.BROWSER) {
            case "Firefox":
            case "Safari":
            case "Opera":
            case "Camino":
            case "Netscape":
            case "Mozilla":
                this.currentEngine = this.getAvailableMediaEngine(['FlashEngine', 'QTEngine']);
                break;
            default:
                this.currentEngine = this.getAvailableMediaEngine(['FlashEngine', 'QTEngine']);
                break;
            }
            break;
        case "Linux":
            this.currentEngine = this.getAvailableMediaEngine(['FlashEngine']);
            break;
        default:
        }
        break;
    case "audio/wma":
    case "audio/x-ms-wma":
    case "audio/ms-wma":
        switch (YAHOO.mediaplayer.Util.OS) {
        case "Windows":
            switch (YAHOO.mediaplayer.Util.BROWSER) {
            case "Firefox":
                if (YAHOO.mediaplayer.Util.BROWSER_VERSION != 3) {
                    this.currentEngine = this.getAvailableMediaEngine(['WMPEngine']);
                } else {
                    this.currentEngine = this.getAvailableMediaEngine(['WMPFF3Engine']);
                }
                break;
            case "Opera":
                this.currentEngine = this.getAvailableMediaEngine(['WMPFF3Engine']);
                break;
            case "MSIE":
            case "Netscape":
            case "Mozilla":
                this.currentEngine = this.getAvailableMediaEngine(['WMPEngine']);
                break;
            case "Safari":
            default:
                this.currentEngine = this.getAvailableMediaEngine(['WMPEngine']);
                break;
            }
            break;
        case "Mac":
            switch (YAHOO.mediaplayer.Util.BROWSER) {
            case "Firefox":
            case "Safari":
            case "Opera":
            case "Camino":
            case "Netscape":
            case "Mozilla":
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
                break;
            default:
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
                break;
            }
            break;
        case "Linux":
            break;
        default:
        }
        break;
    case "audio/wav":
    case "audio/x-wav":
        switch (YAHOO.mediaplayer.Util.OS) {
        case "Windows":
            switch (YAHOO.mediaplayer.Util.BROWSER) {
            case "MSIE":
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine', 'WMPEngine']);
                break;
            case "Firefox":
            case "Safari":
            case "Camino":
            case "Netscape":
            case "Mozilla":
            case "Opera":
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
                break;
            default:
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
            }
            break;
        case "Mac":
            this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
            break;
        }
        break;
    case "audio/rhapsody":
    case "audio/yahoo":
        this.currentEngine = this.getAvailableMediaEngine(['RhapsodyEngine']);
        break;
    case "audio/mp4":
        switch (YAHOO.mediaplayer.Util.OS) {
        case "Windows":
            switch (YAHOO.mediaplayer.Util.BROWSER) {
            case "Firefox":
            case "Safari":
            case "Netscape":
            case "Mozilla":
            case "MSIE":
            case "Opera":
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
                break;
            default:
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
                break;
            }
            break;
        case "Mac":
            switch (YAHOO.mediaplayer.Util.BROWSER) {
            case "Firefox":
            case "Safari":
            case "Opera":
            case "Camino":
            case "Netscape":
            case "Mozilla":
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
                break;
            default:
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
                break;
            }
            break;
        case "Linux":
            this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
            break;
        default:
        }
        break;
    case "audio/unknown":
        switch (YAHOO.mediaplayer.Util.OS) {
        case "Windows":
            this.currentEngine = this.getAvailableMediaEngine(['WMPEngine']);
            break;
        }
        break;
    case "video/x-flv":
        this.currentEngine = this.getAvailableMediaEngine(['FlvEngine']);
        break;
    default:
        switch (YAHOO.mediaplayer.Util.OS) {
        case "Windows":
            switch (YAHOO.mediaplayer.Util.BROWSER) {
            case "MSIE":
                this.currentEngine = this.getAvailableMediaEngine(['WMPEngine', 'QTEngine']);
                break;
            case "Firefox":
            case "Safari":
            case "Camino":
            case "Netscape":
            case "Mozilla":
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine', 'WMPEngine']);
                break;
            case "Opera":
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
                break;
            default:
                this.currentEngine = this.getAvailableMediaEngine(['QTEngine', 'WMPEngine']);
            }
            break;
        case "Mac":
            this.currentEngine = this.getAvailableMediaEngine(['QTEngine']);
            break;
        }
        break;
    }
};
YAHOO.mediaplayer.MediaEngine.prototype.getAvailableMediaEngine = function(engineList) {
    if (YAHOO.mediaplayer.Util.BROWSER == "Firefox" && YAHOO.mediaplayer.Util.BROWSER_VERSION == 3) {
        var plugin = document.getElementById("ymp-qt-engine");
        if (plugin) {
            plugin.parentNode.removeChild(plugin);
            YAHOO.mediaplayer.Controller.qtengine = null;
        }
    }
    for (var i = 0,
    ilen = engineList.length,
    engine, engineRefName; i < ilen; i++) {
        engineRefName = engineList[i].toLowerCase();
        engine = YAHOO.mediaplayer.Controller[engineRefName];
        if (engine == null) {
            engine = new YAHOO.mediaplayer[engineList[i]](this.controller);
        }
        if (engine.available) {
            return engine;
        }
    }
    return null;
};
YAHOO.mediaplayer.MediaEngine.prototype.getVolume = function() {
    return this.vol;
};
YAHOO.mediaplayer.MediaEngine.prototype.onPlayStateChange = function(o) {
    switch (o.newState) {
    case YAHOO.mediaplayer.MediaEngine.PlayState.PLAYING:
        if (this.progressIntervalID == null) {
            this.fireProgress();
            this.progressIntervalID = window.setInterval(this.toString() + '.fireProgress()', 1000);
        }
        break;
    default:
        if (this.progressIntervalID != null) {
            window.clearInterval(this.progressIntervalID);
            this.progressIntervalID = null;
        }
        break;
    }
};
YAHOO.mediaplayer.MediaEngine.prototype.onVolumeChangeRequest = function(vol) {
    try {
        this.vol = vol;
        if (this.currentEngine != null && this.currentEngine.available) {
            this.currentEngine.setVolume(vol);
        }
        this.EventManager.onVolumeChange.fire(vol);
    } catch(e) {}
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.FlashEngine = function(controller) {
    var subscribeToControllersEvents = [];
    YAHOO.mediaplayer.FlashEngine.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.initController(controller);
    this.controller = this.getController();
    this.id = "ymp-flash-engine";
    this.available = false;
    this.ready = false;
    this.currentMedia = null;
    this.vol = 0.5;
    this.seek = null;
    this.init();
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.FlashEngine, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.FlashEngine.prototype.refByName = 'flashengine';
YAHOO.mediaplayer.FlashEngine.prototype.toString = function() {
    return "YAHOO.mediaplayer.Controller." + this.refByName;
};
YAHOO.mediaplayer.FlashEngine.prototype.init = function() {
    if (YAHOO.mediaplayer.SWFObject.hasPlayerVersion("9.0.0")) {
        var dummyContainer = document.createElement('span');
        dummyContainer.id = "dummy-flashengine";
        document.body.appendChild(dummyContainer);
        var flashVars = {
            onLoad: this.toString() + ".onLoad",
            timeout: 20000
        };
        var params = {
            allowScriptAccess: "always",
            allowNetworking: "all"
        };
        var attributes = {
            id: this.id,
            name: this.id,
            style: "position:absolute; top:0; left:-30px;"
        };
        try {
            if (YAHOO.mediaplayer.SWFObject.hasPlayerVersion('9.0.0')) {
                YAHOO.mediaplayer.SWFObject.embedSWF("http://l.yimg.com/us.yimg.com/i/us/mus/swf/ymwp/flashsound-2.0.31.swf", dummyContainer.id, "1", "1", "9.0.0", false, flashVars, params, attributes);
                this.available = true;
            }
        } catch(e) {}
    }
};
YAHOO.mediaplayer.FlashEngine.prototype.onLoad = function() {
    this.ready = true;
    this.getSWF().flAddListener('PlayStateChange', this.toString() + '.onPlayStateChange');
    this.getSWF().flAddListener('Error', this.toString() + '.onError');
    if (this.currentMedia != null) {
        this.play(this.currentMedia, this.seek);
    }
    this.setVolume(this.vol);
};
YAHOO.mediaplayer.FlashEngine.prototype.play = function(media, seek) {
    this.seek = seek;
    if (media != null) {
        this.currentMedia = media;
    }
    if (this.ready) {
        if (media != null) {
            this.getSWF().flLoadMedia(this.currentMedia.token);
        }
        this.getSWF().flPlay(this.seek);
    }
};
YAHOO.mediaplayer.FlashEngine.prototype.pause = function() {
    this.getSWF().flPause();
};
YAHOO.mediaplayer.FlashEngine.prototype.stop = function(organic) {
    if (organic == null) {
        organic = false;
    }
    this.getSWF().flStop(organic);
};
YAHOO.mediaplayer.FlashEngine.prototype.getElapsed = function() {
    return this.getSWF().flGetElapsed();
};
YAHOO.mediaplayer.FlashEngine.prototype.getDuration = function() {
    return this.getSWF().flGetDuration();
};
YAHOO.mediaplayer.FlashEngine.prototype.setVolume = function(vol) {
    this.vol = vol;
    if (this.ready) {
        this.getSWF().flSetVolume(vol);
    }
};
YAHOO.mediaplayer.FlashEngine.prototype.onError = function(args) {
    YAHOO.mediaplayer.Controller.mediaengine.handleError(args, null);
};
YAHOO.mediaplayer.FlashEngine.prototype.onPlayStateChange = function(newState) {
    YAHOO.mediaplayer.Controller.mediaengine.changePlayState(newState);
};
YAHOO.mediaplayer.FlashEngine.prototype.getSWF = function() {
    if (this.player == null) {
        this.player = YAHOO.mediaplayer.SWFObject.getSWF(this.id);
    }
    return this.player;
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.WMPEngine = function(controller) {
    try {
        var subscribeToControllersEvents = [];
        YAHOO.mediaplayer.WMPEngine.superclass.constructor.call(this, controller, subscribeToControllersEvents);
        this.EventManager.addEvents([]);
        this.initController(controller);
        this.controller = this.getController();
        this.id = "ymp-wmp-engine";
        this.version = null;
        this.player = null;
        this.available = false;
        this.currentState = null;
        this.currentMedia = null;
        this.ready = false;
        this.volume = null;
        this.seek = null;
        this.init();
    } catch(ex) {}
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.WMPEngine, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.WMPEngine.prototype.refByName = 'wmpengine';
YAHOO.mediaplayer.WMPEngine.prototype.toString = function() {
    return 'YAHOO.music.WebPlayer.' + this.refByName;
};
YAHOO.mediaplayer.WMPEngine.prototype.PlayStateEnum = {
    9 : 3,
    6 : 3,
    3 : 2,
    2 : 1,
    8 : 4,
    1 : 0
};
YAHOO.mediaplayer.WMPEngine.prototype.init = function() {
    try {
        if (!this.player) {
            var pluginInstalled = false;
            if (YAHOO.mediaplayer.Util.detectPlugin("Windows Media Player Firefox", "WMPlayer.OCX") !== null) {
                pluginInstalled = true;
            }
            if (pluginInstalled === true) {
                var dummyContainer = document.createElement('span');
                dummyContainer.id = "dummy-wmpengine";
                document.body.appendChild(dummyContainer);
                var html = "";
                if (YAHOO.mediaplayer.Util.BROWSER === "MSIE") {
                    html = "<object id='" + this.id + "' style='width:1px; height:1px; position:absolute; top:0; left:-30px; display:none;'" + " classid=CLSID:6BF52A52-394A-11D3-B153-00C04F79FAA6" + " type='application/x-oleobject'>" + "<param name='autostart' value='true'>" + "</object>";
                } else if (YAHOO.mediaplayer.Util.BROWSER === "Firefox" || YAHOO.mediaplayer.Util.BROWSER === "Opera" || YAHOO.mediaplayer.Util.BROWSER === "Camino" || YAHOO.mediaplayer.Util.BROWSER === "Netscape" || YAHOO.mediaplayer.Util.BROWSER === "Mozilla" || YAHOO.mediaplayer.Util.BROWSER === "Unknown") {
                    html = "<object id='" + this.id + "' style='width:1px; height:1px;'" + "type='application/x-ms-wmp' data = ''>" + "<param name='URL' value='' /><param name='uiMode' value='none'>" + "</object>";
                }
                html += '<script for="' + this.id + '" type="text/javascript" event="PlayStateChange(newState)">';
                html += 'YAHOO.mediaplayer.Controller.wmpengine.onPlayStateChange(newState);';
                html += '</script>';
                html += '<script for="' + this.id + '" type="text/javascript" event="Error()">';
                html += 'YAHOO.mediaplayer.Controller.wmpengine.onError();';
                html += '</script>';
                dummyContainer.innerHTML = html;
                this.currentState = 0;
                setTimeout('YAHOO.mediaplayer.Controller.wmpengine.onLoad()', 250);
                this.available = true;
            } else {
                this.available = false;
                if (YAHOO.mediaplayer.Util.BROWSER === "Firefox") {
                    this.controller.mediaengine.handleError("9", null);
                }
            }
        }
    } catch(ex) {}
};
YAHOO.mediaplayer.WMPEngine.prototype.onLoad = function() {
    this.player = document.getElementById(this.id);
    this.version = "WMP " + this.player.versionInfo;
    this.ready = true;
    if (this.currentMedia != null) {
        if (typeof(this.volume) !== "number") {
            this.volume = 0.5;
        }
        this.setVolume(this.volume);
        this.play(this.currentMedia);
    }
};
YAHOO.mediaplayer.WMPEngine.prototype.play = function(mediaObject, seek) {
    if (typeof(seek) === "number") {
        this.seek = seek / 1000;
    }
    if (this.currentState === this.PlayStateEnum[2]) {
        this.player.controls.play();
    } else {
        if (mediaObject != null) {
            this.currentMedia = mediaObject;
        }
        if (this.ready) {
            this.player.URL = this.currentMedia.token;
        }
    }
};
YAHOO.mediaplayer.WMPEngine.prototype.pause = function() {
    this.player.controls.pause();
};
YAHOO.mediaplayer.WMPEngine.prototype.stop = function() {
    this.player.controls.stop();
};
YAHOO.mediaplayer.WMPEngine.prototype.getElapsed = function() {
    if (this.player && this.player.controls) {
        return this.player.controls.currentPosition * 1000;
    } else {
        return null;
    }
};
YAHOO.mediaplayer.WMPEngine.prototype.getDuration = function() {
    if (this.player && this.player.controls && this.player.controls.currentItem) {
        return this.player.controls.currentItem.duration * 1000;
    } else {
        return null;
    }
};
YAHOO.mediaplayer.WMPEngine.prototype.onPlayStateChange = function(newState) {
    if (typeof(this.PlayStateEnum[newState]) === "number") {
        this.currentState = this.PlayStateEnum[newState];
        if (this.currentState === 2 && typeof(this.seek) === "number") {
            this.player.controls.currentPosition = this.seek;
            this.seek = null;
        }
        YAHOO.mediaplayer.Controller.mediaengine.changePlayState(this.PlayStateEnum[newState]);
    }
};
YAHOO.mediaplayer.WMPEngine.prototype.setVolume = function(vol) {
    this.volume = vol;
    if (this.player) {
        this.player.settings.volume = parseInt(vol * 100, 10);
    }
};
YAHOO.mediaplayer.WMPEngine.prototype.getVolume = function() {
    return this.player.settings.volume / 100;
};
YAHOO.mediaplayer.WMPEngine.prototype.onError = function() {
    try {
        if (this.player.error.errorCount > 0) {
            var error = null;
            var errorCode = this.player.error.item(0).errorCode;
            errorCode = YAHOO.mediaplayer.Util.convertToHexadecimal(errorCode);
            switch (errorCode) {
            case "C00D1197":
            case "80070037":
            case "800704CF":
            case "C00D001F":
                this.controller.mediaengine.handleError("1", null);
                break;
            case "800C2EE2":
            case "C00D000F":
            case "C00D1198":
            case "C00D1198":
            case "C00D11CB":
                this.controller.mediaengine.handleError("7", null);
                break;
            default:
                this.controller.mediaengine.handleError("3", null);
                break;
            }
            this.player.error.clearErrorQueue();
            this.stop();
        }
    } catch(ex) {}
    this.onPlayStateChange(8);
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.WMPFF3Engine = function(controller) {
    try {
        var subscribeToControllersEvents = [];
        YAHOO.mediaplayer.WMPFF3Engine.superclass.constructor.call(this, controller, subscribeToControllersEvents);
        this.EventManager.addEvents([]);
        this.initController(controller);
        this.controller = this.getController();
        this.id = "ymp-wmpff3-engine";
        this.version = null;
        this.player = null;
        this.available = false;
        this.currentState = null;
        this.currentMedia = null;
        this.ready = false;
        this.volume = null;
        this.naturalStop = true;
        this.seek = null;
        this.timeoutInterval = 100;
        this.timeoutId = null;
        this.init();
    } catch(ex) {}
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.WMPFF3Engine, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.WMPFF3Engine.prototype.refByName = 'wmpff3engine';
YAHOO.mediaplayer.WMPFF3Engine.prototype.toString = function() {
    return 'YAHOO.music.WebPlayer.' + this.refByName;
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.PlayStateEnum = {
    9 : 3,
    3 : 2,
    2 : 1,
    1 : 4,
    0 : 0
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.init = function() {
    try {
        if (!this.player) {
            var pluginInstalled = false;
            if (YAHOO.mediaplayer.Util.detectPlugin("Windows Media Player Firefox", "WMPlayer.OCX") !== null) {
                pluginInstalled = true;
            }
            if (pluginInstalled === true) {
                var dummyContainer = document.createElement('span');
                dummyContainer.id = "dummy-wmpff3engine";
                document.body.appendChild(dummyContainer);
                var html = "";
                if (YAHOO.mediaplayer.Util.BROWSER === "MSIE") {
                    html = "<object id='" + this.id + "' style='width:1px; height:1px; position:absolute; top:0; left:-30px; display:none;'" + " classid=CLSID:6BF52A52-394A-11D3-B153-00C04F79FAA6" + " type='application/x-oleobject'>" + "<param name='autostart' value='true'>" + "</object>";
                } else if (YAHOO.mediaplayer.Util.BROWSER === "Firefox" || YAHOO.mediaplayer.Util.BROWSER === "Opera" || YAHOO.mediaplayer.Util.BROWSER === "Camino" || YAHOO.mediaplayer.Util.BROWSER === "Netscape" || YAHOO.mediaplayer.Util.BROWSER === "Mozilla" || YAHOO.mediaplayer.Util.BROWSER === "Unknown") {
                    html = "<object id='" + this.id + "' style='width:1px; height:1px;'" + "type='application/x-ms-wmp' data = ''>" + "<param name='URL' value='' /><param name='uiMode' value='none'>" + "</object>";
                }
                html += '<script for="' + this.id + '" type="text/javascript" event="PlayStateChange(newState)">';
                html += 'YAHOO.mediaplayer.Controller.wmpff3engine.onPlayStateChange(newState);';
                html += '</script>';
                html += '<script for="' + this.id + '" type="text/javascript" event="Error()">';
                html += 'YAHOO.mediaplayer.Controller.wmpff3engine.onError();';
                html += '</script>';
                dummyContainer.innerHTML = html;
                this.currentState = 0;
                setTimeout('YAHOO.mediaplayer.Controller.wmpff3engine.onLoad()', 250);
                this.available = true;
            } else {
                this.available = false;
                if (YAHOO.mediaplayer.Util.BROWSER === "Firefox") {
                    this.controller.mediaengine.handleError("9", null);
                }
            }
        }
    } catch(ex) {}
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.onLoad = function() {
    this.player = document.getElementById(this.id);
    this.version = "WMPFF3 " + this.player.versionInfo;
    this.ready = true;
    if (this.currentMedia != null) {
        if (typeof(this.volume) !== "number") {
            this.volume = 0.5;
        }
        this.setVolume(this.volume);
        this.play(this.currentMedia);
    }
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.play = function(mediaObject, seek) {
    if (typeof(seek) === "number") {
        this.seek = seek / 1000;
    }
    if (this.currentState === this.PlayStateEnum[2]) {
        this.player.controls.play();
    } else {
        if (mediaObject != null) {
            this.currentMedia = mediaObject;
        }
        if (this.ready) {
            this.timeoutId = window.setTimeout('YAHOO.mediaplayer.Controller.wmpff3engine.checkPlayState()', this.timeoutInterval);
            this.player.URL = this.currentMedia.token;
        }
    }
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.stop = function() {
    this.naturalStop = false;
    this.onPlayStateChange(0);
    if (this.timeoutId) {
        window.clearTimeout(this.timeoutId);
    }
    this.player.controls.stop();
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.getElapsed = function() {
    if (this.player && this.player.controls) {
        return this.player.controls.currentPosition * 1000;
    } else {
        return null;
    }
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.getDuration = function() {
    if (this.player && this.player.controls && this.player.controls.currentItem) {
        return this.player.controls.currentItem.duration * 1000;
    } else {
        return null;
    }
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.onPlayStateChange = function(newState) {
    if (typeof(this.PlayStateEnum[newState]) === "number") {
        this.currentState = this.PlayStateEnum[newState];
        if (this.currentState === 2 && typeof(this.seek) === "number") {
            this.player.controls.currentPosition = this.seek;
            this.seek = null;
        }
        YAHOO.mediaplayer.Controller.mediaengine.changePlayState(this.PlayStateEnum[newState]);
    }
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.setVolume = function(vol) {
    this.volume = vol;
    if (this.player) {
        this.player.settings.volume = parseInt(vol * 100, 10);
    }
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.getVolume = function() {
    return this.player.settings.volume / 100;
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.onError = function() {
    try {
        if (this.player.error.errorCount > 0) {
            var error = null;
            var errorCode = String(this.player.error.item(0).errorCode);
            switch (errorCode) {
            case "C00D1197":
            case "80070037":
            case "800704CF":
            case "C00D001F":
                this.controller.mediaengine.handleError("1", null);
                break;
            case "800C2EE2":
            case "C00D000F":
            case "C00D1198":
            case "C00D1198":
            case "C00D11CB":
                this.controller.mediaengine.handleError("7", null);
                break;
            default:
                this.controller.mediaengine.handleError("3", null);
                break;
            }
            this.player.error.clearErrorQueue();
            this.stop();
        }
    } catch(ex) {}
    this.onPlayStateChange(1);
};
YAHOO.mediaplayer.WMPFF3Engine.prototype.checkPlayState = function() {
    window.clearTimeout(this.timeoutId);
    if (this.player.error.errorCount > 0) {
        this.onError(1);
        return;
    } else if (this.player && this.PlayStateEnum[this.player.playState] !== this.currentState) {
        if (this.naturalStop === false && this.player.playState == 1) {
            this.naturalStop = true;
        } else {
            this.onPlayStateChange(this.player.playState);
        }
    }
    if (this.currentState >= 1) {
        this.timeoutId = window.setInterval('YAHOO.mediaplayer.Controller.wmpff3engine.checkPlayState()', this.timeoutInterval);
    }
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.QTEngine = function(controller) {
    try {
        var subscribeToControllersEvents = [];
        YAHOO.mediaplayer.QTEngine.superclass.constructor.call(this, controller, subscribeToControllersEvents);
        this.EventManager.addEvents(['onVolumeChange']);
        this.initController(controller);
        this.controller = this.getController();
        this.id = "ymp-qt-engine";
        this.available = false;
        this.ready = false;
        this.currentMedia = null;
        this.player = null;
        this.version = null;
        this.volume = null;
        this.currentState = null;
        this.naturalStop = null;
        this.seek = null;
        this.init();
        this.timeOut = null;
    } catch(ex) {}
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.QTEngine, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.QTEngine.prototype.refByName = 'qtengine';
YAHOO.mediaplayer.QTEngine.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Controller.' + this.refByName;
};
YAHOO.mediaplayer.QTEngine.PlayStateEnum = {
    qt_play: 2,
    qt_buffer: 3,
    qt_pause: 1,
    qt_ended: 4,
    qt_stopped: 0
};
YAHOO.mediaplayer.QTEngine.prototype.init = function() {
    try {
        var pluginInstalled = false;
        if (YAHOO.mediaplayer.Util.detectPlugin("QuickTime Plug-in", "QuickTime.QuickTime") !== null) {
            pluginInstalled = true;
        }
        if (pluginInstalled === true) {
            var dummyContainer = document.createElement('span');
            dummyContainer.id = "dummy-qtengine";
            document.body.appendChild(dummyContainer);
            var html = "";
            if (YAHOO.mediaplayer.Util.BROWSER === "MSIE") {
                html += '<object id="qt_event_source" classid="clsid:CB927D12-4FF7-4a9e-A169-56E4B8A75598"' + ' codebase="http://www.apple.com/qtactivex/qtplugin.cab#version=7,2,1,0" ></object>' + '<object classid="clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B"' + ' codebase="http://www.apple.com/qtactivex/qtplugin.cab#version=7,2,1,0"' + ' width="0" height="0" type="audio/quicktime" id="' + this.id + '"' + ' controller="false" style="behavior:url(#qt_event_source);">' + '<param name="controller" value="false"/><param name="src" value=""/><param name="postdomevents" value="true"/>' + '</object>';
            } else {
                html += "<embed width='1px' height='1px' " + "id='" + this.id + "' " + "name='" + this.id + "' " + "type='video/quicktime' " + "src='' " + "pluginspage='http://www.apple.com/quicktime/download/' " + "enablejavascript='true' " + "controller='false' " + "style='position:fixed; top:0; right:0;' " + "autoplay='true' postdomevents='true'" + "/>";
            }
            dummyContainer.innerHTML = html;
            this.timeOut = window.setTimeout('YAHOO.mediaplayer.Controller.qtengine.checkLoad();', 500);
            this.currentState = YAHOO.mediaplayer.QTEngine.PlayStateEnum.qt_stopped;
            this.available = true;
        } else {
            this.available = false;
        }
    } catch(ex) {}
};
YAHOO.mediaplayer.QTEngine.prototype.checkLoad = function() {
    this.player = document.getElementById(this.id);
    if (this.player) {
        this.version = this.player.GetQuickTimeVersion();
        if (this.version) {
            this.ready = true;
            if (document.addEventListener) {
                this.player.addEventListener('qt_play', YAHOO.mediaplayer.Controller.qtengine.changePlayState, false);
                this.player.addEventListener('qt_pause', YAHOO.mediaplayer.Controller.qtengine.changePlayState, false);
                this.player.addEventListener('qt_error', YAHOO.mediaplayer.Controller.qtengine.onError, false);
                this.player.addEventListener('qt_ended', YAHOO.mediaplayer.Controller.qtengine.changePlayState, false);
                this.player.addEventListener('qt_volumechange', YAHOO.mediaplayer.Controller.qtengine.onVolumeChangeHandler, false);
            } else {
                this.player.attachEvent('onqt_play', YAHOO.mediaplayer.Controller.qtengine.changePlayState);
                this.player.attachEvent('onqt_pause', YAHOO.mediaplayer.Controller.qtengine.changePlayState);
                this.player.attachEvent('onqt_error', YAHOO.mediaplayer.Controller.qtengine.onError);
                this.player.attachEvent('onqt_ended', YAHOO.mediaplayer.Controller.qtengine.changePlayState);
                this.player.attachEvent('onqt_volumechange', YAHOO.mediaplayer.Controller.qtengine.onVolumeChangeHandler);
            }
            if (typeof(this.volume) !== "number") {
                this.volume = 0.5;
            }
            this.setVolume(this.volume);
            this.play(this.currentMedia);
        }
    }
};
YAHOO.mediaplayer.QTEngine.prototype.play = function(mediaObj, seek) {
    this.naturalStop = true;
    if (typeof(seek) === "number") {
        this.seek = seek;
    }
    if (this.currentState === YAHOO.mediaplayer.QTEngine.PlayStateEnum.qt_pause) {
        this.player.Play();
    } else {
        if (mediaObj != null) {
            this.currentMedia = mediaObj;
        }
        if (this.ready) {
            if (mediaObj != null) {
                this.changePlayState({
                    type: 'qt_buffer'
                });
                this.player.SetURL(mediaObj.token);
            }
        }
    }
};
YAHOO.mediaplayer.QTEngine.prototype.changePlayState = function(args) {
    if (args.type === "qt_play") {
        if (typeof(YAHOO.mediaplayer.Controller.qtengine.seek) === "number") {
            YAHOO.mediaplayer.Controller.qtengine.player.SetTime((YAHOO.mediaplayer.Controller.qtengine.seek / 1000) * YAHOO.mediaplayer.Controller.qtengine.player.GetTimeScale());
            YAHOO.mediaplayer.Controller.qtengine.seek = null;
        }
        YAHOO.mediaplayer.Controller.qtengine.setVolume(YAHOO.mediaplayer.Controller.qtengine.volume);
    }
    if (YAHOO.mediaplayer.Controller.qtengine.naturalStop === false && args.type === "qt_pause") {
        YAHOO.mediaplayer.Controller.qtengine.currentState = YAHOO.mediaplayer.QTEngine.PlayStateEnum.qt_stopped;
        YAHOO.mediaplayer.Controller.qtengine.naturalStop = true;
    } else {
        YAHOO.mediaplayer.Controller.qtengine.currentState = YAHOO.mediaplayer.QTEngine.PlayStateEnum[args.type];
        YAHOO.mediaplayer.Controller.mediaengine.changePlayState(YAHOO.mediaplayer.Controller.qtengine.currentState);
    }
};
YAHOO.mediaplayer.QTEngine.prototype.onError = function(args) {};
YAHOO.mediaplayer.QTEngine.prototype.getElapsed = function() {
    var progress = 0;
    try {
        progress = this.player.GetTime() / this.player.GetTimeScale() * 1000;
    } catch(e) {
        return 0;
    }
    return isNaN(progress) ? 0 : progress;
};
YAHOO.mediaplayer.QTEngine.prototype.getDuration = function() {
    var duration = 0;
    try {
        duration = this.player.GetDuration() / this.player.GetTimeScale() * 1000;
    } catch(e) {
        return 0;
    }
    return isNaN(duration) ? 0 : duration;
};
YAHOO.mediaplayer.QTEngine.prototype.pause = function() {
    this.player.Stop();
};
YAHOO.mediaplayer.QTEngine.prototype.stop = function() {
    try {
        this.player.Stop();
        this.player.SetTime(0);
    } catch(e) {}
    this.player.SetURL("");
    YAHOO.mediaplayer.Controller.mediaengine.changePlayState(0);
    this.naturalStop = false;
};
YAHOO.mediaplayer.QTEngine.prototype.setVolume = function(volume) {
    try {
        this.volume = volume;
        if (!isNaN(volume)) {
            this.player.SetVolume(parseInt(volume * 768, 10));
        }
    } catch(e) {};
};
YAHOO.mediaplayer.QTEngine.prototype.getVolume = function() {
    return this.player.GetVolume() / 768;
};
YAHOO.mediaplayer.QTEngine.prototype.onVolumeChangeHandler = function(eventObj) {
    if (YAHOO.mediaplayer.Util.BROWSER == "Firefox" && YAHOO.mediaplayer.Util.BROWSER_VERSION == 3) {
        YAHOO.mediaplayer.Controller.qtengine.volume = YAHOO.mediaplayer.Controller.qtengine.getVolume();
        YAHOO.mediaplayer.Controller.qtengine.EventManager.onVolumeChange.fire(YAHOO.mediaplayer.Controller.qtengine.getVolume());
    }
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.RhapsodyEngine = function(controller) {
    var subscribeToControllersEvents = [];
    YAHOO.mediaplayer.RhapsodyEngine.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.initController(controller);
    this.controller = this.getController();
    this.id = "ymp-rhapsody-engine";
    this.available = false;
    this.ready = false;
    this.currentMedia = null;
    this.vol = 0.5;
    this.seek = null;
    this.init();
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.RhapsodyEngine, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.RhapsodyEngine.prototype.refByName = 'rhapsodyengine';
YAHOO.mediaplayer.RhapsodyEngine.prototype.toString = function() {
    return "YAHOO.mediaplayer.Controller." + this.refByName;
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.init = function() {
    if (YAHOO.mediaplayer.SWFObject.hasPlayerVersion("9.0.0")) {
        var dummyContainer = document.createElement('span');
        dummyContainer.id = "dummy-rhapsodyengine";
        document.body.appendChild(dummyContainer);
        var flashVars = {
            env: "production",
            fp_context: "popout",
            pcode: YAHOO.mediaplayer.YMPParams.rhappcode,
            onEngineReady: "YAHOO.mediaplayer.Controller.rhapsodyengine.onEngineReady"
        };
        var params = {
            allowScriptAccess: "always",
            allowNetworking: "all"
        };
        var attributes = {
            id: this.id,
            name: this.id,
            style: "position:absolute; top:0; left:-30px;"
        };
        try {
            if (YAHOO.mediaplayer.SWFObject.hasPlayerVersion('9.0.0')) {
                YAHOO.mediaplayer.SWFObject.embedSWF("http://playback.rhapsody.com/-static/players/engine/1_1_0_14/rhapsodyPlaybackEngine.swf", dummyContainer.id, "1", "1", "9.0.0", false, flashVars, params, attributes);
                this.available = true;
            }
        } catch(e) {}
    }
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.onEngineReady = function() {
    this.ready = true;
    this.setVolume(this.vol, true);
    this.getSWF().addListener('onPlayStateChanged', this.toString() + '.onPlayStateChange');
    this.getSWF().addListener('onTrackClosed', this.toString() + '.onTrackClosed');
    this.getSWF().addListener('onError', this.toString() + '.onError');
    if (this.currentMedia != null) {
        this.play(this.currentMedia, this.seek);
    }
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.play = function(media, seek) {
    this.seek = seek;
    if (media != null) {
        this.currentMedia = media;
    }
    if (this.ready) {
        if (media != null) {
            var currentTrackID = this.getSWF().getCurrentTrackId();
            if (currentTrackID != null && currentTrackID.toLowerCase() == this.currentMedia.token) {
                this.getSWF().doPlay();
            } else {
                this.getSWF().doPlayTrack(this.currentMedia.token);
                this.controller.logger.logPlay(this.currentMedia.token);
            }
        } else {
            this.getSWF().doPlay();
            this.controller.logger.logPlay(this.currentMedia.token);
        }
        if (this.seek != null) {
            this.getSWF().setPosition(seek);
        }
    }
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.pause = function() {
    this.getSWF().doPause();
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.stop = function() {
    this.getSWF().doStop();
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.getElapsed = function() {
    return this.getSWF().getPosition();
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.getDuration = function() {
    return this.getSWF().getTrackLength();
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.setVolume = function(vol, storeInCookie) {
    if (storeInCookie == null) {
        storeInCookie = false;
    }
    this.vol = vol;
    if (this.ready) {
        this.getSWF().setVolume(vol, storeInCookie);
    }
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.onError = function(errorCode) {
    switch (errorCode) {
    case 3:
    case 5:
        errorCode = "7";
        break;
    case 4:
        errorCode = "13";
        break;
    case 6:
        errorCode = "14";
        break;
    case 7:
        errorCode = "15";
        break;
    case 8:
        errorCode = "16";
        break;
    case 9:
        errorCode = "17";
        break;
    case 10:
        errorCode = "18";
        break;
    case 13:
        errorCode = "19";
        break;
    case 15:
        errorCode = "20";
        break;
    case 1:
    case 2:
    case 11:
    case 12:
    case 14:
    default:
        errorCode = "3";
    }
    YAHOO.mediaplayer.Controller.mediaengine.handleError(errorCode, null);
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.onPlayStateChange = function(oldState, newState) {
    switch (newState) {
    case 0:
        newState = YAHOO.mediaplayer.MediaEngine.PlayState.STOPPED;
        break;
    case 1:
    case 2:
    case 3:
        newState = YAHOO.mediaplayer.MediaEngine.PlayState.BUFFERING;
        break;
    case 4:
        newState = YAHOO.mediaplayer.MediaEngine.PlayState.PLAYING;
        break;
    case 5:
        newState = YAHOO.mediaplayer.MediaEngine.PlayState.PAUSED;
        break;
    }
    YAHOO.mediaplayer.Controller.mediaengine.changePlayState(newState);
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.onTrackClosed = function() {
    YAHOO.mediaplayer.Controller.mediaengine.changePlayState(YAHOO.mediaplayer.MediaEngine.PlayState.ENDED);
};
YAHOO.mediaplayer.RhapsodyEngine.prototype.getSWF = function() {
    if (this.player == null) {
        this.player = YAHOO.mediaplayer.SWFObject.getSWF(this.id);
    }
    return this.player;
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.Rhapsody = function(controller) {
    var subscribeToControllersEvents = [];
    YAHOO.mediaplayer.Rhapsody.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.initController(controller);
    this.controller = this.getController();
    this.stickwallShownAt = -1;
    this.playsAfterZero = 0;
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.Rhapsody, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.Rhapsody.prototype.refByName = 'rhapsody';
YAHOO.mediaplayer.Rhapsody.prototype.toString = function() {
    return "YAHOO.mediaplayer.Controller." + this.refByName;
};
YAHOO.mediaplayer.Rhapsody.regex = {
    track: /http(\:|%3A)\/\/([^:\/\s]+\.rhapsody\.com)\/(player|goto)(\?|%3F)rcid=(tra\.[0-9]+).*$/i
};
YAHOO.mediaplayer.Rhapsody.prototype.getFreePlays = function() {
    return YAHOO.mediaplayer.Controller.rhapsodyengine.getSWF().getAccountProperty('freePlaysRemaining');
};
YAHOO.mediaplayer.Rhapsody.prototype.getSubType = function() {
    return YAHOO.mediaplayer.Controller.rhapsodyengine.getSWF().getAccountProperty('subscriptionType');
};
YAHOO.mediaplayer.Rhapsody.prototype.timeForStickwall = function() {
    var currentSubType = this.getSubType();
    var currentFreePlays = this.getFreePlays();
    if (currentSubType == null || currentSubType == "RHAPSODY_25") {
        if (currentFreePlays <= 0 && (this.playsAfterZero % 10) == 0) {
            if (this.stickwallShownAt != currentFreePlays) {
                this.stickwallShownAt = currentFreePlays;
                return true;
            } else {
                this.playsAfterZero++;
            }
        }
    }
    return false;
};
YAHOO.mediaplayer.Rhapsody.rightsFlags = {
    STREAM: 2,
    DOWNLOAD_PORTABLE: 16,
    DOWNLOAD_NONPORTABLE: 32,
    PURCHASEDRMFREE: 512
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.ymu = function(controller) {
    var subscribeToControllersEvents = [];
    YAHOO.mediaplayer.ymu.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.initController(controller);
    this.controller = this.getController();
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.ymu, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.ymu.prototype.refByName = 'ymu';
YAHOO.mediaplayer.ymu.prototype.toString = function() {
    return "YAHOO.mediaplayer.Controller." + this.refByName;
};
YAHOO.mediaplayer.ymu.regex = {
    track: /http(\:|%3A)\/\/([^:\/\s]+\.yahoo\.com)(\/\w+)*\/(track)\/(\d+$)$/
};
YAHOO.mediaplayer.ymu.rhapsodyCatalogId = "157431055";
YAHOO.mediaplayer.ymu.rightsFlags = {
    STREAM: 128,
    DOWNLOAD: 32,
    DOANLOADALBUM: 64
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.FlvEngine = function(controller) {
    var subscribeToControllersEvents = [];
    YAHOO.mediaplayer.FlvEngine.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.initController(controller);
    this.controller = this.getController();
    this.id = "ymp-flv-engine";
    this.available = false;
    this.ready = false;
    this.currentMedia = null;
    this.vol = 0.5;
    this.seek = null;
    this.init();
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.FlvEngine, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.FlvEngine.prototype.refByName = 'flvengine';
YAHOO.mediaplayer.FlvEngine.prototype.toString = function() {
    return "YAHOO.mediaplayer.Controller." + this.refByName;
};
YAHOO.mediaplayer.FlvEngine.prototype.init = function() {
    if (YAHOO.mediaplayer.SWFObject.hasPlayerVersion("9.0.0")) {
        var dummyContainer = document.createElement('span');
        dummyContainer.id = "dummy-flvengine";
        document.body.appendChild(dummyContainer);
        var flashVars = {
            onLoad: this.toString() + ".onLoad",
            timeout: 20000
        };
        var params = {
            allowScriptAccess: "always",
            allowNetworking: "all"
        };
        var attributes = {
            id: this.id,
            name: this.id,
            style: "position:absolute; top:0; left:-30px;"
        };
        try {
            if (YAHOO.mediaplayer.SWFObject.hasPlayerVersion('9.0.0')) {
                YAHOO.mediaplayer.SWFObject.embedSWF("http://l.yimg.com/us.yimg.com/i/us/mus/swf/ymwp/flvplayer-2.0.31.swf", dummyContainer.id, "1", "1", "9.0.0", false, flashVars, params, attributes);
                this.available = true;
            }
        } catch(e) {}
    }
};
YAHOO.mediaplayer.FlvEngine.prototype.onLoad = function() {
    this.ready = true;
    this.getSWF().flAddListener('PlayStateChange', this.toString() + '.onPlayStateChange');
    this.getSWF().flAddListener('Error', this.toString() + '.onError');
    if (this.currentMedia != null) {
        this.play(this.currentMedia, this.seek);
    }
    this.setVolume(this.vol);
};
YAHOO.mediaplayer.FlvEngine.prototype.play = function(media, seek) {
    this.seek = seek;
    if (media != null) {
        this.currentMedia = media;
    }
    if (this.ready) {
        if (media != null) {
            this.getSWF().flLoadMedia(this.currentMedia.token);
        }
        this.getSWF().flPlay(this.seek);
    }
};
YAHOO.mediaplayer.FlvEngine.prototype.pause = function() {
    this.getSWF().flPause();
};
YAHOO.mediaplayer.FlvEngine.prototype.stop = function(organic) {
    if (organic == null) {
        organic = false;
    }
    this.getSWF().flStop(organic);
};
YAHOO.mediaplayer.FlvEngine.prototype.getElapsed = function() {
    var elapsed = this.getSWF().flGetElapsed() * 1000;
    return elapsed;
};
YAHOO.mediaplayer.FlvEngine.prototype.getDuration = function() {
    var duration = this.getSWF().flGetDuration() * 1000;
    return duration;
};
YAHOO.mediaplayer.FlvEngine.prototype.setVolume = function(vol) {
    this.vol = vol;
    if (this.ready) {
        this.getSWF().flSetVolume(vol);
    }
};
YAHOO.mediaplayer.FlvEngine.prototype.onError = function(args) {
    YAHOO.mediaplayer.Controller.mediaengine.handleError(args, null);
};
YAHOO.mediaplayer.FlvEngine.prototype.onPlayStateChange = function(newState) {
    YAHOO.mediaplayer.Controller.mediaengine.changePlayState(newState);
};
YAHOO.mediaplayer.FlvEngine.prototype.getSWF = function() {
    if (this.player == null) {
        this.player = YAHOO.mediaplayer.SWFObject.getSWF(this.id);
    }
    return this.player;
};
if (YAHOO.mediaplayer.DisplayStrings == null) {
    YAHOO.mediaplayer.DisplayStrings = {
        ui: {
            PLAYLIST_TAB: "Playlist",
            PLAYLIST_LOADING: "Loading playlist ...",
            BUFFERING: "Buffering",
            HDR_MAIN: "Yahoo! Media Player",
            HDR_CONTROLS: "Playback Controls",
            HDR_PLAYLIST: "Media Player Playlist"
        },
        tooltips: {
            PREVIOUS: "Previous Track (Shift+Arrow Left)",
            PLAY: "Play (Shift+Space)",
            PAUSE: "Pause (Shift+Space)",
            NEXT: "Next Track (Shift+Arrow Right)",
            VOLUME: "Volume %1% (Shift+Arrow Up/Down)",
            NOWPLAYING_TRACK: "%1",
            NOWPLAYING_ARTIST: "%1",
            NOWPLAYING_ALBUM: "%1",
            BUY: "Buy this song",
            OPENPLAYLIST: "Open playlist (Ctrl+Shift+P)",
            CLOSEPLAYLIST: "Close playlist (Ctrl+Shift+P)",
            MINIMIZE: "Minimize Player",
            MAXIMIZE: "Expand Player",
            CLOSE: "Close player",
            FINDONPAGE: "Find song on page"
        },
        errors: {
            "1": "We're sorry, we could not find the track you requested",
            "2": "We are unable to play media on this page at this time. Refresh the page and try again.",
            "3": "We're sorry, there was an error in playback",
            "4": "We're sorry, we are unable to retrieve the playlist",
            "5": "We're sorry, we could not find any media to play in this playlist",
            "6": "We're sorry, there was an error in downloading the media file. Please retry later",
            "7": "We're sorry, there was an error in connecting to the server. Please retry later",
            "8": "DRM error place-holder",
            "9": "This file requires the Windows Media Player plug-in for Firefox. <a target='_top' href='http://port25.technet.com/pages/windows-media-player-firefox-plugin-download.aspx'>Click here</a> for instructions to install the plugin",
            "10": "Rhapsody metadata unavailable. %1",
            "11": "We're sorry, playback timed out",
            "12": "We're sorry, the track could not be resolved. %1",
            "13": "We're sorry, the username/password combination for the Rhapsody service is invalid",
            "14": "We're sorry, the playback system is not initialized, please try again later",
            "15": "We're sorry, the user token is invalid. Please sign in again",
            "16": "Access denied",
            "17": "We're sorry, an invalid request was made to the server",
            "18": "We're sorry, a user property was requested that is not available",
            "19": "We're sorry, user can be logged in and listening to the service from only one location",
            "20": "We're sorry, this service is available only in the United States",
            "21": "We're sorry, this track does not have streaming rights."
        }
    };
}
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.View = function(controller) {
    var subscribeToControllersEvents = ['onPlaylistUpdate', 'onPlayStateChange', 'onCurrentMediaSet', 'onMediaUpdate', 'onMediaProgress', 'onError', 'onVolumeChange'];
    YAHOO.mediaplayer.View.superclass.constructor.call(this, controller, subscribeToControllersEvents);
    this.EventManager.addEvents(['onPlayRequest', 'onPauseRequest', 'onStopRequest', 'onPreviousRequest', 'onNextRequest', 'onVolumeChangeRequest']);
    this.volControlHeight = 0;
    this.volTopConstraint = 0;
    this.volBottomConstraint = 0;
    this.volControlY = 0;
    this.currentPlaylist = [];
    this.playlistArray = null;
    this.currentMedia = null;
    this.currentViewState = null;
    this.pageTargetAnchor = null;
    this.pageTargetTimeoutID = null;
    this.XULWin = null;
    this.firstPlay = true;
    this.currentStickwall = null;
    this.playlistViewState = 0;
    this.carouselTimeoutID = null;
    this.carouselContent = null;
    this.carouselIndex = 0;
    this.defaultCarouselContent = [{
        id: "ymp-getplayer",
        time: 0
    }];
    this.rhap25CarouselContent = [{
        id: "ymp-rhap-playinfo",
        time: 30000
    },
    {
        id: "ymp-getplayer",
        time: 10000
    },
    {
        id: "ymp-getlyrics",
        time: 10000
    },
    {
        id: "ymp-watchvideo",
        time: 10000
    }];
    this.rhapCarouselContent = [{
        id: "ymp-getplayer",
        time: 10000
    },
    {
        id: "ymp-getlyrics",
        time: 10000
    },
    {
        id: "ymp-watchvideo",
        time: 10000
    }];
    this.errorBubbleTimeoutID = null;
    this.initController(controller);
    this.controller = this.getController();
    if (YAHOO.mediaplayer.YMPParams.displaystate != YAHOO.mediaplayer.View.DisplayState.NOUI) {
        this.setupUI();
    }
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.View, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.View.prototype.refByName = 'view';
YAHOO.mediaplayer.View.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Controller.' + this.refByName;
};
YAHOO.mediaplayer.View.DisplayState = {
    HIDDEN: -1,
    MIN: 0,
    MAX: 1,
    NOUI: 3
};
YAHOO.mediaplayer.View.PlaylistState = {
    MIN: 0,
    MAX: 1
};
YAHOO.mediaplayer.View.MINHEIGHT = 262;
YAHOO.mediaplayer.View.MINWIDTH = 660;
YAHOO.mediaplayer.View.prototype.setupUI = function() {
    var bodyElm = document.createElement("div");
    bodyElm.id = "ymp-player";
    bodyElm.innerHTML = YAHOO.mediaplayer.ViewMarkup.body;
    document.body.appendChild(bodyElm);
    var vpHeight = YAHOO.ympyui.util.Dom.getViewportHeight();
    var vpWidth = YAHOO.ympyui.util.Dom.getViewportWidth();
    if (vpHeight < YAHOO.mediaplayer.View.MINHEIGHT || vpWidth < YAHOO.mediaplayer.View.MINWIDTH) {
        YAHOO.mediaplayer.YMPParams.displaystate = YAHOO.mediaplayer.View.DisplayState.HIDDEN;
    }
    trayElm = document.createElement("div");
    trayElm.id = "ymp-tray";
    trayElm.innerHTML = YAHOO.mediaplayer.ViewMarkup.tray;
    document.body.appendChild(trayElm);
    var errorElm = document.createElement("div");
    errorElm.id = "ymp-error-bubble";
    errorElm.innerHTML = '<div id="ymp-error-msg" class="ymp-color-text-main">Testing<br/>Hahahaha</div><span class="ymp-error-tail ymp-skin"></span>';
    document.body.appendChild(errorElm);
    var secretElm = document.createElement("div");
    secretElm.id = "ymp-secret-bubble";
    secretElm.innerHTML = '<div id="ymp-secret-msg" class="ymp-color-text-main"><div id="ymp-secret-msg-header">Yahoo! Media Player</div><table><tr><th>Engineers</th><th>Design</th><th>Product</th></tr><tr><td>Mike Davis</td><td>Lino Wiehen</td><td>Lucas Gonze</td></tr><tr><td>William Khoe</td><td>Douglas Kim</td><td>Dave Warmerdam</td></tr><tr><td>Amit Behere</td><td></td><td>Suman Nichani</td></tr></table></div><a id="ymp-btn-close-secret" href="#" class="ymp-skin" title="Close this dialog"></a>';
    document.body.appendChild(secretElm);
    if (YAHOO.mediaplayer.Util.BROWSER == "MSIE" && (YAHOO.mediaplayer.Util.BROWSER_VERSION <= 6 || YAHOO.mediaplayer.Util.DOCTYPE == "BackCompat")) {
        YAHOO.ympyui.util.Dom.setStyle('ymp-player', 'position', 'absolute');
        YAHOO.ympyui.util.Dom.setStyle('ymp-tray', 'position', 'absolute');
        YAHOO.ympyui.util.Event.on(window, 'scroll', this.onWindowScroll, this, true);
    }
    this.resizePlayer(YAHOO.mediaplayer.YMPParams.displaystate);
    YAHOO.ympyui.util.Event.on('ymp-btn-tray', 'click', this.toggleTray, this, true);
    YAHOO.ympyui.util.Event.on('ymp-play', 'click', this.play, this);
    YAHOO.ympyui.util.Event.on('ymp-prev', 'click', this.prev, this);
    YAHOO.ympyui.util.Event.on('ymp-next', 'click', this.next, this);
    YAHOO.ympyui.util.Event.on(['ymp-btn-max', 'ymp-btn-min'], 'click', this.togglePlayerSize, this, true);
    YAHOO.ympyui.util.Event.on('ymp-btn-target', 'click', this.targetMedia, this, true);
    YAHOO.ympyui.util.Event.on('ymp-btn-close', 'click', this.hidePlayer, this, true);
    YAHOO.ympyui.util.Event.on('ymp-btn-pop', 'click', this.popPlayer, this, true);
    YAHOO.ympyui.util.Event.on('ymp-current-media-error', 'click',
    function(e) {
        YAHOO.ympyui.util.Event.stopEvent(e);
    });
    YAHOO.ympyui.util.Event.on(['ymp-current-media-error', 'ymp-error-bubble'], 'mouseover', this.onErrorMouseOver, this);
    YAHOO.ympyui.util.Event.on(['ymp-current-media-error', 'ymp-error-bubble'], 'mouseout', this.onErrorMouseOut, this);
    YAHOO.ympyui.util.Event.on(document, 'keydown', this.keyHandler, this, true);
    YAHOO.ympyui.util.Event.on('ymp-tray', 'keydown', this.trayKeyHandler, this, true);
    YAHOO.ympyui.util.Event.on('ymp-tray', 'mouseover', this.trayMouseOverHandler, this, true);
    YAHOO.ympyui.util.Event.on('ymp-yahoo-logo', 'mousedown', this.showSecretMsg, this);
    YAHOO.ympyui.util.Event.on('ymp-btn-close-secret', 'click', this.hideSecretMsg, this);
    YAHOO.mediaplayer.EventDelegate.on('ymp-tray-track', 'click', 'ymp-tray-list', this.onTrayMediaClick, this);
    YAHOO.mediaplayer.EventDelegate.on('ymp-error-icon', 'mouseover', 'ymp-tray-list', this.onErrorMouseOver, this);
    YAHOO.mediaplayer.EventDelegate.on('ymp-error-icon', 'mouseout', 'ymp-tray-list', this.onErrorMouseOut, this);
    YAHOO.mediaplayer.EventDelegate.on('ymp-tray-playlist', 'click', 'ymp-tray-list', this.onTrayPlaylistClick, this);
    YAHOO.ympyui.util.Event.on('ymp-rhap-continue', 'click', this.onRhapContinueClick, this, true);
    this.volControlHeight = parseInt(YAHOO.ympyui.util.Dom.getStyle('ymp-volume', 'height'));
    this.volTopConstraint = Math.round(parseInt(YAHOO.ympyui.util.Dom.getStyle('ymp-volume-thumb', 'height')) / 2);
    this.volBottomConstraint = this.volControlHeight - this.volTopConstraint;
    this.onVolumeChange(YAHOO.mediaplayer.Controller.mediaengine.getVolume());
    YAHOO.ympyui.util.Event.on('ymp-volume', 'mousedown', this.volStartDrag, this, true);
    YAHOO.ympyui.util.Event.on('ymp-volume', 'click',
    function stopEvent(e) {
        YAHOO.ympyui.util.Event.stopEvent(e);
    });
    if (YAHOO.mediaplayer.Util.BROWSER == 'Firefox') {
        var div = document.createElement('div');
        var xulNS = 'http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul';
        var desc = document.createElementNS(xulNS, 'description');
        desc.setAttribute('crop', 'end');
        this.XULWin = document.createElementNS(xulNS, 'window');
        this.XULWin.appendChild(desc);
    }
    this.setUpUiStrings();
};
YAHOO.mediaplayer.View.prototype.onErrorMouseOver = function(e, obj) {
    obj.clearBubbleTimeout();
    if (this.id != "ymp-error-bubble") {
        var elm = this;
        var pos = [YAHOO.ympyui.util.Event.getPageX(e), YAHOO.ympyui.util.Event.getPageY(e)];
        obj.errorBubbleTimeoutID = window.setTimeout(function() {
            obj.showErrorBubble(elm, pos);
        },
        350);
    }
};
YAHOO.mediaplayer.View.prototype.onErrorMouseOut = function(e, obj) {
    obj.clearBubbleTimeout();
    var elm = this;
    obj.errorBubbleTimeoutID = window.setTimeout(obj.hideErrorBubble, 350);
};
YAHOO.mediaplayer.View.prototype.showErrorBubble = function(errorIconElm, pos) {
    var errorElm = document.getElementById('ymp-error-bubble');
    var errorID = this.getErrorIDFromClassName(errorIconElm.className);
    var errorObj = YAHOO.mediaplayer.ErrorCollection[errorID];
    document.getElementById('ymp-error-msg').innerHTML = errorObj.getDisplayMessage();
    YAHOO.ympyui.util.Dom.setStyle(errorElm, 'display', 'block');
    YAHOO.ympyui.util.Dom.setXY(errorElm, [pos[0] + 13, pos[1] - errorElm.offsetHeight - 23]);
};
YAHOO.mediaplayer.View.prototype.hideErrorBubble = function() {
    YAHOO.ympyui.util.Dom.setStyle('ymp-error-bubble', 'display', 'none');
};
YAHOO.mediaplayer.View.prototype.clearBubbleTimeout = function() {
    if (this.errorBubbleTimeoutID != null) {
        window.clearTimeout(this.errorBubbleTimeoutID);
        this.errorBubbleTimeoutID = null;
    }
};
YAHOO.mediaplayer.View.prototype.getErrorIDFromClassName = function(className) {
    var regex = /ymp-error-id-([^\s]*)?/i;
    var match = regex.exec(className);
    return match[1];
};
YAHOO.mediaplayer.View.prototype.onTrayPlaylistClick = function(e, obj) {
    if (e != null) {
        YAHOO.ympyui.util.Event.stopEvent(e);
    }
    var nestedList = YAHOO.ympyui.util.Dom.getElementsByClassName('ymp-nested-list', 'ul', this.parentNode)[0];
    if (YAHOO.ympyui.util.Dom.hasClass(nestedList, 'ymp-nested-list-closed')) {
        YAHOO.ympyui.util.Dom.setStyle(nestedList, 'display', 'block');
        YAHOO.ympyui.util.Dom.removeClass(nestedList, 'ymp-nested-list-closed');
        YAHOO.ympyui.util.Dom.addClass(nestedList, 'ymp-nested-list-open');
        var rightArrow = YAHOO.ympyui.util.Dom.getElementsByClassName('ymp-right-arrow', null, this.parentNode)[0];
        YAHOO.ympyui.util.Dom.removeClass(rightArrow, 'ymp-right-arrow');
        YAHOO.ympyui.util.Dom.addClass(rightArrow, 'ymp-down-arrow');
    } else {
        YAHOO.ympyui.util.Dom.setStyle(nestedList, 'display', 'none');
        YAHOO.ympyui.util.Dom.removeClass(nestedList, 'ymp-nested-list-open');
        YAHOO.ympyui.util.Dom.addClass(nestedList, 'ymp-nested-list-closed');
        var downArrow = YAHOO.ympyui.util.Dom.getElementsByClassName('ymp-down-arrow', null, this.parentNode)[0];
        YAHOO.ympyui.util.Dom.removeClass(downArrow, 'ymp-down-arrow');
        YAHOO.ympyui.util.Dom.addClass(downArrow, 'ymp-right-arrow');
    }
};
YAHOO.mediaplayer.View.prototype.onTrayMediaClick = function(e, obj) {
    if (e != null) {
        YAHOO.ympyui.util.Event.stopEvent(e);
    }
    var media = YAHOO.mediaplayer.Controller.playlistmanager.getMediaById(this.id);
    obj.EventManager.onPlayRequest.fire({
        media: media
    });
};
YAHOO.mediaplayer.View.prototype.onPageMediaClick = function(e, obj) {
    if (e != null) {
        YAHOO.ympyui.util.Event.stopEvent(e);
    }
    if (obj.firstPlay || YAHOO.ympyui.util.Dom.hasClass('ymp-player', 'ymp-player-hidden')) {
        var vpHeight = YAHOO.ympyui.util.Dom.getViewportHeight();
        var vpWidth = YAHOO.ympyui.util.Dom.getViewportWidth();
        if (vpHeight >= YAHOO.mediaplayer.View.MINHEIGHT && vpWidth >= YAHOO.mediaplayer.View.MINWIDTH) {
            obj.resizePlayer(YAHOO.mediaplayer.View.DisplayState.MAX);
        }
        obj.firstPlay = false;
    }
    var regex = /ymp-media-([^\s]*)?/i;
    var match = regex.exec(this.className);
    var mediaID = match[1];
    var media = YAHOO.mediaplayer.Controller.playlistmanager.getMediaById(mediaID);
    if (YAHOO.ympyui.util.Dom.hasClass(this, 'ymp-btn-page-play')) {
        obj.EventManager.onPlayRequest.fire({
            media: media
        });
    } else {
        obj.EventManager.onPauseRequest.fire(media);
    }
};
YAHOO.mediaplayer.View.prototype.volStartDrag = function(e) {
    YAHOO.ympyui.util.Event.stopEvent(e);
    this.volControlY = YAHOO.ympyui.util.Dom.getY('ymp-volume');
    this.notifyVolumeChange(e);
    YAHOO.ympyui.util.Event.on(document, 'mousemove', this.notifyVolumeChange, this, true);
    YAHOO.ympyui.util.Event.on(document, 'mouseup', this.volMouseUp, this, true);
};
YAHOO.mediaplayer.View.prototype.volMouseUp = function(e) {
    YAHOO.ympyui.util.Event.stopEvent(e);
    YAHOO.ympyui.util.Event.removeListener(document, 'mousemove', this.notifyVolumeChange);
    YAHOO.ympyui.util.Event.removeListener(document, 'mouseup', this.volMouseUp);
};
YAHOO.mediaplayer.View.prototype.notifyVolumeChange = function(e) {
    YAHOO.ympyui.util.Event.stopEvent(e);
    var newMouseY = YAHOO.ympyui.util.Event.getPageY(e);
    var yDiff = newMouseY - this.volControlY;
    var yOffset = 0;
    if (YAHOO.mediaplayer.Util.BROWSER == 'Firefox' && YAHOO.mediaplayer.Util.BROWSER_VERSION != 3) {
        yOffset = document.documentElement.scrollTop;
    } else if (YAHOO.mediaplayer.Util.BROWSER == 'Safari') {
        yOffset = document.body.scrollTop;
    }
    yDiff -= yOffset;
    var thumbTop;
    if (yDiff >= this.volTopConstraint && yDiff < this.volBottomConstraint) {
        thumbTop = yDiff - this.volTopConstraint;
    } else if (yDiff >= this.volBottomConstraint) {
        thumbTop = this.volBottomConstraint - this.volTopConstraint;
    } else if (yDiff < this.volTopConstraint) {
        thumbTop = 0;
    }
    var vol = 1 - (thumbTop / (this.volBottomConstraint - this.volTopConstraint));
    this.EventManager.onVolumeChangeRequest.fire(vol);
};
YAHOO.mediaplayer.View.prototype.onWindowScroll = function(e, obj) {
    if (YAHOO.mediaplayer.Util.BROWSER_VERSION <= 6) {
        YAHOO.ympyui.util.Dom.addClass('ymp-player', 'ymp-dummy');
        YAHOO.ympyui.util.Dom.removeClass('ymp-player', 'ymp-dummy');
    } else if (YAHOO.mediaplayer.Util.BROWSER_VERSION >= 7 && YAHOO.mediaplayer.Util.DOCTYPE == "BackCompat") {
        var scrollYOffset = Math.max(document.body.scrollTop, document.documentElement.scrollTop);
        var newBodyY = 10 - scrollYOffset;
        var newTrayY = 81 - scrollYOffset;
        YAHOO.ympyui.util.Dom.setStyle('ymp-player', 'bottom', newBodyY + 'px');
        YAHOO.ympyui.util.Dom.setStyle('ymp-tray', 'bottom', newTrayY + 'px');
    }
};
YAHOO.mediaplayer.View.prototype.play = function(e, obj) {
    var elm = null;
    if (typeof this !== "object") {
        elm = this;
    } else {
        elm = document.getElementById("ymp-play");
    }
    if (YAHOO.ympyui.util.Dom.hasClass(elm, 'ymp-btn-pause')) {
        obj.pause.call(this, e, obj);
        return;
    }
    if (e) {
        YAHOO.ympyui.util.Event.stopEvent(e);
    }
    obj.EventManager.onPlayRequest.fire();
};
YAHOO.mediaplayer.View.prototype.pause = function(e, obj) {
    YAHOO.ympyui.util.Event.stopEvent(e);
    obj.EventManager.onPauseRequest.fire();
};
YAHOO.mediaplayer.View.prototype.stop = function(e, obj) {
    YAHOO.ympyui.util.Event.stopEvent(e);
    obj.EventManager.onStopRequest.fire();
};
YAHOO.mediaplayer.View.prototype.next = function(e, obj) {
    YAHOO.ympyui.util.Event.stopEvent(e);
    obj.EventManager.onNextRequest.fire();
};
YAHOO.mediaplayer.View.prototype.prev = function(e, obj) {
    YAHOO.ympyui.util.Event.stopEvent(e);
    obj.EventManager.onPreviousRequest.fire();
};
YAHOO.mediaplayer.View.prototype.togglePlayerSize = function(e) {
    if (e != null) {
        YAHOO.ympyui.util.Event.stopEvent(e);
    }
    if (YAHOO.ympyui.util.Dom.hasClass('ymp-player', 'ymp-player-max')) {
        this.resizePlayer(YAHOO.mediaplayer.View.DisplayState.MIN);
    } else if (YAHOO.ympyui.util.Dom.hasClass('ymp-player', 'ymp-player-min')) {
        this.resizePlayer(YAHOO.mediaplayer.View.DisplayState.MAX);
    }
};
YAHOO.mediaplayer.View.prototype.hidePlayer = function(e) {
    if (e != null) {
        YAHOO.ympyui.util.Event.stopEvent(e);
    }
    this.resizePlayer(YAHOO.mediaplayer.View.DisplayState.HIDDEN);
    this.EventManager.onStopRequest.fire();
};
YAHOO.mediaplayer.View.prototype.resizePlayer = function(viewState) {
    this.currentViewState = viewState;
    if (viewState == YAHOO.mediaplayer.View.DisplayState.MAX && !YAHOO.ympyui.util.Dom.hasClass('ymp-player', 'ymp-player-max')) {
        YAHOO.ympyui.util.Dom.setStyle('ymp-yahoo-logo', 'display', 'block');
        YAHOO.ympyui.util.Dom.removeClass('ymp-player', 'ymp-player-hidden');
        YAHOO.ympyui.util.Dom.removeClass('ymp-player', 'ymp-player-min');
        YAHOO.ympyui.util.Dom.addClass('ymp-player', 'ymp-player-max');
        var widthTo = parseInt(YAHOO.ympyui.util.Dom.getStyle('ymp-body', 'width'));
        var anim = new YAHOO.ympyui.util.Anim('ymp-player', {
            width: {
                to: widthTo
            }
        },
        .35, YAHOO.ympyui.util.Easing.easeOut);
        anim.onComplete.subscribe(this.showTray);
        anim.animate();
    } else if (viewState == YAHOO.mediaplayer.View.DisplayState.MIN && !YAHOO.ympyui.util.Dom.hasClass('ymp-player', 'ymp-player-min')) {
        YAHOO.ympyui.util.Dom.setStyle('ymp-yahoo-logo', 'display', 'none');
        YAHOO.ympyui.util.Dom.removeClass('ymp-player', 'ymp-player-hidden');
        YAHOO.ympyui.util.Dom.removeClass('ymp-player', 'ymp-player-max');
        YAHOO.ympyui.util.Dom.addClass('ymp-player', 'ymp-player-min');
        this.hideTray();
        var widthTo = parseInt(YAHOO.ympyui.util.Dom.getStyle('ymp-body', 'width'));
        YAHOO.ympyui.util.Dom.setStyle('ymp-player', 'width', widthTo + 'px');
    } else if (viewState == YAHOO.mediaplayer.View.DisplayState.HIDDEN && !YAHOO.ympyui.util.Dom.hasClass('ymp-player', 'ymp-player-hidden')) {
        YAHOO.ympyui.util.Dom.setStyle('ymp-yahoo-logo', 'display', 'none');
        YAHOO.ympyui.util.Dom.removeClass('ymp-player', 'ymp-player-max');
        YAHOO.ympyui.util.Dom.addClass('ymp-player', 'ymp-player-min');
        var widthTo = parseInt(YAHOO.ympyui.util.Dom.getStyle('ymp-body', 'width'));
        YAHOO.ympyui.util.Dom.setStyle('ymp-player', 'width', widthTo + 'px');
        this.hideTray();
        YAHOO.ympyui.util.Dom.removeClass('ymp-player', 'ymp-player-min');
        YAHOO.ympyui.util.Dom.addClass('ymp-player', 'ymp-player-hidden');
    }
};
YAHOO.mediaplayer.View.prototype.popPlayer = function(e) {
    YAHOO.ympyui.util.Event.stopEvent(e);
    var contplayForm = document.contplayform;
    contplayForm.vol.value = YAHOO.mediaplayer.Controller.mediaengine.getVolume();
    contplayForm.seek.value = YAHOO.mediaplayer.Controller.mediaengine.getElapsed();
    if (YAHOO.mediaplayer.Controller.mediaengine.currentPlayState == YAHOO.mediaplayer.MediaEngine.PlayState.PLAYING) {
        this.EventManager.onStopRequest.fire();
        contplayForm.token.value = this.currentMedia.token;
    }
    var anchorHTML = "";
    var playlistArray = YAHOO.mediaplayer.Controller.playlistmanager.playlistArray;
    var attrWeCareAbt = ["type", "title", "artist", "album"];
    for (var i = 0,
    ilen = playlistArray.length,
    prevAnchor = null,
    anchor; i < ilen; i++) {
        anchor = playlistArray[i].anchor;
        if (anchor == null && playlistArray[i].parent != null) {
            anchor = playlistArray[i].parent.anchor;
        }
        if (anchor != null && anchor != prevAnchor) {
            anchorHTML += '<a href="' + anchor.href + '"';
            if (anchor.className != "") {
                anchorHTML += ' class="' + anchor.className + '"';
            }
            for (var j = 0,
            jlen = attrWeCareAbt.length; j < jlen; j++) {
                if (anchor.getAttribute(attrWeCareAbt[j]) != null && anchor.getAttribute(attrWeCareAbt[j]) != "") {
                    anchorHTML += ' ' + attrWeCareAbt[j] + '="' + anchor.getAttribute(attrWeCareAbt[j]) + '"';
                }
            }
            anchorHTML += ">" + anchor.innerHTML + '</a>';
        }
        prevAnchor = anchor;
    }
    contplayForm.trackhtml.value = anchorHTML;
    contplayForm.action = "http://mediaplayer.yahoo.com/contplay/index.php?url=" + encodeURIComponent(window.location.href);
    contplayForm.submit();
    this.hidePlayer();
};
YAHOO.mediaplayer.View.prototype.toggleTray = function(e) {
    if (e != null) {
        YAHOO.ympyui.util.Event.stopEvent(e);
    }
    if (YAHOO.ympyui.util.Dom.hasClass('ymp-btn-tray', 'ymp-btn-tray-open')) {
        this.closeTray();
    } else if (YAHOO.ympyui.util.Dom.hasClass('ymp-btn-tray', 'ymp-btn-tray-closed')) {
        this.openTray();
    }
};
YAHOO.mediaplayer.View.prototype.openTray = function() {
    YAHOO.ympyui.util.Dom.removeClass('ymp-playlist-arrow', 'ymp-up-arrow');
    YAHOO.ympyui.util.Dom.addClass('ymp-playlist-arrow', 'ymp-down-arrow');
    YAHOO.ympyui.util.Dom.removeClass('ymp-btn-tray', 'ymp-btn-tray-closed');
    YAHOO.ympyui.util.Dom.addClass('ymp-btn-tray', 'ymp-btn-tray-open');
    var anim = new YAHOO.ympyui.util.Anim('ymp-tray', {
        height: {
            to: 204
        }
    },
    .35, YAHOO.ympyui.util.Easing.easeOut);
    anim.onComplete.subscribe(this.addTrayScrollBar);
    anim.animate();
    this.playlistViewState = YAHOO.mediaplayer.View.PlaylistState.MAX;
    var elm = document.getElementById('ymp-btn-tray');
    if (elm) {
        elm.setAttribute("title", YAHOO.mediaplayer.DisplayStrings.tooltips.CLOSEPLAYLIST);
        elm = null;
    }
};
YAHOO.mediaplayer.View.prototype.closeTray = function() {
    YAHOO.ympyui.util.Dom.removeClass('ymp-playlist-arrow', 'ymp-down-arrow');
    YAHOO.ympyui.util.Dom.addClass('ymp-playlist-arrow', 'ymp-up-arrow');
    YAHOO.ympyui.util.Dom.setStyle('ymp-tray', 'height', parseInt(YAHOO.ympyui.util.Dom.getStyle('ymp-btn-tray', 'height')) + 'px');
    YAHOO.ympyui.util.Dom.addClass('ymp-btn-tray', 'ymp-btn-tray-closed');
    YAHOO.ympyui.util.Dom.removeClass('ymp-btn-tray', 'ymp-btn-tray-open');
    YAHOO.ympyui.util.Dom.setStyle('ymp-tray-list', 'overflow-y', 'hidden');
    this.playlistViewState = YAHOO.mediaplayer.View.PlaylistState.MIN;
    var elm = document.getElementById('ymp-btn-tray');
    if (elm) {
        elm.setAttribute("title", YAHOO.mediaplayer.DisplayStrings.tooltips.OPENPLAYLIST);
        elm = null;
    }
    this.clearFocusClass();
};
YAHOO.mediaplayer.View.prototype.addTrayScrollBar = function() {
    YAHOO.ympyui.util.Dom.setStyle('ymp-tray-list', 'overflow-y', 'auto');
};
YAHOO.mediaplayer.View.prototype.hideTray = function() {
    if (YAHOO.ympyui.util.Dom.hasClass('ymp-btn-tray', 'ymp-btn-tray-open')) {
        this.toggleTray();
    }
    YAHOO.ympyui.util.Dom.setStyle('ymp-tray', 'display', 'none');
};
YAHOO.mediaplayer.View.prototype.showTray = function() {
    YAHOO.ympyui.util.Dom.setStyle('ymp-tray', 'display', 'block');
    var trayHeight = parseInt(YAHOO.ympyui.util.Dom.getStyle('ymp-tray', 'height'));
    YAHOO.ympyui.util.Dom.setStyle('ymp-tray', 'height', '0px');
    var anim = new YAHOO.ympyui.util.Anim('ymp-tray', {
        height: {
            to: trayHeight
        }
    },
    .15, YAHOO.ympyui.util.Easing.easeOut);
    anim.animate();
};
YAHOO.ympyui.util.Scroll.prototype.setAttribute = function(attr, val, unit) {
    var el = this.getEl();
    if (attr == 'scroll') {
        if (unit == "pagescroll") {
            window.scrollTo(val[0], val[1]);
        } else {
            el.scrollLeft = val[0];
            el.scrollTop = val[1];
        }
    } else {
        superclass.setAttribute.call(this, attr, val, unit);
    }
};
YAHOO.mediaplayer.View.prototype.targetMedia = function(e) {
    if (e != null) {
        YAHOO.ympyui.util.Event.stopEvent(e);
    }
    var anchor = (this.currentMedia.anchor != null) ? this.currentMedia.anchor: this.currentMedia.parent.anchor;
    var jumpY = 200;
    var currentPageY = YAHOO.ympyui.util.Dom.getDocumentScrollTop();
    var currentPageHeight = YAHOO.ympyui.util.Dom.getViewportHeight();
    var buttonY = Math.floor(YAHOO.ympyui.util.Dom.getY(anchor));
    var scrollToY = Math.floor(buttonY - (currentPageHeight / 2));
    if (scrollToY > currentPageHeight) {} else if (scrollToY < 0) {
        scrollToY = 0;
    }
    var scrollFromY = currentPageY;
    if (Math.abs(scrollToY - currentPageY) > jumpY) {
        scrollFromY = (scrollToY > currentPageY) ? scrollToY - jumpY: scrollToY + jumpY;
    }
    if (scrollFromY > currentPageHeight) {
        scrollFromY = currentPageHeight;
    } else if (scrollFromY < 0) {
        scrollFromY = 0;
    }
    if (scrollToY != currentPageY) {
        var anim = new YAHOO.ympyui.util.Scroll(document.body, {
            scroll: {
                from: [0, scrollFromY],
                to: [0, scrollToY],
                unit: "pagescroll"
            }
        },
        .20, YAHOO.ympyui.util.Easing.easeOut);
        anim.animate();
    }
    if (this.pageTargetTimeoutID != null) {
        window.clearTimeout(this.pageTargetTimeoutID);
        YAHOO.ympyui.util.Dom.removeClass(this.pageTargetAnchor, 'ymp-btn-page-target');
        this.pageTargetAnchor = null;
    }
    YAHOO.ympyui.util.Dom.addClass(anchor, 'ymp-btn-page-target');
    this.pageTargetAnchor = anchor;
    this.pageTargetTimeoutID = window.setTimeout(function() {
        YAHOO.ympyui.util.Dom.removeClass(anchor, 'ymp-btn-page-target');
    },
    5000);
};
YAHOO.mediaplayer.View.prototype.showErrorStickwall = function(msg) {
    if (document.getElementById('ymp-player') != null) {
        this.resizePlayer(YAHOO.mediaplayer.View.DisplayState.MAX);
        document.getElementById('ymp-critical-error-msg').innerHTML = msg;
        YAHOO.ympyui.util.Dom.removeClass('ymp-stickwall', this.currentStickwall);
        YAHOO.ympyui.util.Dom.addClass('ymp-stickwall', 'ymp-error-stickwall');
        YAHOO.ympyui.util.Dom.setStyle('ymp-stickwall', 'display', 'block');
        YAHOO.ympyui.util.Dom.setStyle(this.currentStickwall, 'display', 'none');
        YAHOO.ympyui.util.Dom.setStyle('ymp-error-stickwall', 'display', 'block');
        this.currentStickwall = 'ymp-error-stickwall';
    }
};
YAHOO.mediaplayer.View.prototype.hideStickwall = function() {
    YAHOO.ympyui.util.Dom.setStyle('ymp-stickwall', 'display', 'none');
};
YAHOO.mediaplayer.View.prototype.runCarousel = function(carouselContent) {
    if (this.carouselContent != carouselContent) {
        window.clearTimeout(this.carouselTimeoutID);
        this.carouselContent = carouselContent;
        this.carouselIndex = 0;
        var allCarouselItems = YAHOO.ympyui.util.Dom.getElementsByClassName('ymp-carousel-item', null, 'ymp-body');
        YAHOO.ympyui.util.Dom.setStyle(allCarouselItems, 'display', 'none');
        YAHOO.ympyui.util.Dom.setStyle(this.carouselContent[this.carouselIndex].id, 'display', 'block');
        var timeout = this.carouselContent[this.carouselIndex].time;
        if (timeout != 0) {
            this.carouselTimeoutID = window.setTimeout(this.toString() + ".nextCarousel()", timeout);
        }
    }
};
YAHOO.mediaplayer.View.prototype.nextCarousel = function() {
    YAHOO.ympyui.util.Dom.setStyle(this.carouselContent[this.carouselIndex].id, 'display', 'none');
    this.carouselIndex++;
    if (this.carouselIndex >= this.carouselContent.length) {
        this.carouselIndex = 0;
    }
    YAHOO.ympyui.util.Dom.setStyle(this.carouselContent[this.carouselIndex].id, 'display', 'block');
    var timeout = this.carouselContent[this.carouselIndex].time;
    if (timeout != 0) {
        this.carouselTimeoutID = window.setTimeout(this.toString() + ".nextCarousel()", timeout);
    }
};
YAHOO.mediaplayer.View.prototype.onVolumeChange = function(vol) {
    var thumbTop = (1 - vol) * (this.volBottomConstraint - this.volTopConstraint);
    YAHOO.ympyui.util.Dom.setStyle('ymp-volume-thumb', 'top', thumbTop + "px");
    YAHOO.ympyui.util.Dom.setStyle('ymp-volume-cover', 'height', this.volControlHeight - thumbTop + "px");
    var elm = document.getElementById("ymp-volume-thumb");
    if (elm) {
        var normalizedVol = parseInt(vol * 100);
        args = [normalizedVol];
        str = YAHOO.mediaplayer.Util.sprintf(YAHOO.mediaplayer.DisplayStrings.tooltips.VOLUME, args);
        elm.setAttribute("title", str);
    }
    elm = null;
};
YAHOO.mediaplayer.View.prototype.onPlaylistUpdate = function(playlist) {
    if (document.getElementById('ymp-player') != null) {
        var trayList = document.getElementById('ymp-tray-list');
        if (playlist instanceof YAHOO.mediaplayer.Playlist) {
            for (var i = 0; i < this.controller.playlistmanager.playlistArray.length; i++) {
                this.currentPlaylist[i] = this.controller.playlistmanager.playlistArray[i];
            }
            var playlistElm = document.getElementById(playlist.id);
            if (playlist.mediaArray.length > 0) {
                playlistElm.getElementsByTagName('b')[0].innerHTML = playlist.title;
                playlistElm.setAttribute("title", 'Expand/collapse nested playlist');
                var rightArrow = YAHOO.ympyui.util.Dom.getElementsByClassName('ymp-right-arrow', null, playlistElm.parentNode)[0];
                YAHOO.ympyui.util.Dom.removeClass(rightArrow, 'ymp-right-arrow');
                YAHOO.ympyui.util.Dom.addClass(rightArrow, 'ymp-down-arrow');
                var ul = document.createElement('ul');
                ul.className = "ymp-nested-list";
                for (var i = 0,
                ilen = playlist.mediaArray.length,
                li; i < ilen; i++) {
                    li = document.createElement('li');
                    li.innerHTML = '<a id="' + playlist.mediaArray[i].id + '" class="ymp-tray-track ymp-color-text-tray" href="#"><b>Loading track ...</b><em></em>' + '</a>';
                    ul.appendChild(li);
                }
                playlistElm.parentNode.appendChild(ul);
            } else {
                var arrowElm = YAHOO.ympyui.util.Dom.getElementsByClassName('ymp-right-arrow', null, playlistElm)[0];
                playlistElm.removeChild(arrowElm);
                playlistElm.getElementsByTagName('b')[0].innerHTML = "Playlist Unavailable";
            }
        } else {
            this.playlistArray = playlist;
            if (playlist && playlist.length === 0) {
                this.clearPlaylistTray(trayList);
            }
            trayItems = trayList.getElementsByTagName("a");
            if (trayItems.length <= 0) {
                for (var i = 0; i < playlist.length; i++) {
                    this.currentPlaylist[i] = playlist[i];
                }
                var plength = playlist.length;
                for (var i = 0; i < plength; i++) {
                    var li = this.getLi(playlist[i]);
                    trayList.appendChild(li);
                }
                this.reorderPlaylistTray(trayList);
            } else {
                this.updatePlaylist(playlist, trayList);
            }
        }
        var elmTab = document.getElementById("ymp-btn-tray");
        if (elmTab) {
            var aElms = elmTab.getElementsByTagName("em");
            if (aElms && aElms.length > 0) {
                var elmEm = aElms[0];
                var args = [this.playlistArray.length];
                elmEm.innerHTML = YAHOO.mediaplayer.Util.sprintf(YAHOO.mediaplayer.DisplayStrings.ui.PLAYLIST_TAB, args);
                elmEm = null;
            }
            elmTab = null;
        }
    }
};
YAHOO.mediaplayer.View.prototype.onMediaUpdate = function(media) {
    var mediaElm = document.getElementById(media.id);
    if (document.getElementById('ymp-player') != null) {
        mediaElm.href = media.token;
        mediaElm.getElementsByTagName('b')[0].innerHTML = media.title;
        if (media.artistName != null && media.artistName != "") {
            mediaElm.getElementsByTagName('em')[0].innerHTML = "&nbsp;- " + media.artistName;
        }
        if (media == this.currentMedia) {
            this.updateMediaMetadata();
        }
    }
    var anchor = media.anchor;
    var mediaID = media.id;
    if (anchor == null) {
        anchor = media.parent.anchor;
        mediaID = media.parent.id;
    }
    var anchor = (media.anchor != null) ? media.anchor: media.parent.anchor;
    if (!YAHOO.ympyui.util.Dom.hasClass(anchor, 'ymp-btn-page-play')) {
        YAHOO.ympyui.util.Dom.addClass(anchor, 'ymp-btn-page-play');
        YAHOO.ympyui.util.Dom.addClass(anchor, 'ymp-media-' + mediaID);
    }
    if (anchor.getElementsByTagName("em").length == 0) {
        var em = document.createElement('em');
        em.className = "ymp-skin";
        anchor.appendChild(em);
    } else {
        var em = anchor.getElementsByTagName("em")[0];
        if (!YAHOO.ympyui.util.Dom.hasClass(em, 'ymp-skin')) {
            em = document.createElement('em');
            em.className = "ymp-skin";
            anchor.appendChild(em);
        }
    }
    if (YAHOO.mediaplayer.YMPParams.playlink === true) {
        YAHOO.ympyui.util.Event.removeListener(anchor, "click", this.onPageMediaClick);
        YAHOO.ympyui.util.Event.on(anchor, 'click', this.onPageMediaClick, this);
    } else {
        YAHOO.ympyui.util.Event.removeListener(em, "click", this.onPageMediaClick);
        YAHOO.ympyui.util.Event.on(em, 'click', this.onPageMediaClick, this, anchor);
    }
};
YAHOO.mediaplayer.View.prototype.onMediaProgress = function(time) {
    var elapsedSeconds = Math.round(time.elapsed / 1000);
    var durationSeconds = Math.round(time.duration / 1000);
    var temp = this.formatTime(elapsedSeconds);
    if (durationSeconds > 0) {
        temp += " / " + this.formatTime(durationSeconds);
    }
    document.getElementById('ymp-meta-progress').innerHTML = temp;
    temp = "";
};
YAHOO.mediaplayer.View.prototype.formatTime = function(secs) {
    var minutes = Math.floor(secs / 60);
    var seconds = Math.floor(secs % 60);
    var hours = "";
    if (seconds < 10) {
        seconds = "0" + seconds;
    }
    if (minutes >= 60) {
        hours = Math.floor(minutes / 60);
        hours = hours + ":";
        minutes = Math.floor(minutes % 60);
        if (minutes < 10) {
            minutes = "0" + minutes;
        }
    }
    return hours + minutes + ':' + seconds;
};
YAHOO.mediaplayer.View.prototype.onCurrentMediaSet = function(media) {
    if (document.getElementById('ymp-player') != null && document.getElementById('ymp-tray') != null) {
        if (this.currentMedia != null) {
            YAHOO.ympyui.util.Dom.removeClass(this.currentMedia.id, 'playing');
        }
        this.currentMedia = media;
        YAHOO.ympyui.util.Dom.addClass(this.currentMedia.id, 'playing');
        this.updateMediaMetadata();
        if (media.mimeType == YAHOO.mediaplayer.Parser.MimeTypes.rhapsody || media.mimeType == YAHOO.mediaplayer.Parser.MimeTypes.yahoo) {
            YAHOO.ympyui.util.Dom.setStyle('ymp-rhap-brand', 'display', 'block');
        } else {
            YAHOO.ympyui.util.Dom.setStyle('ymp-rhap-brand', 'display', 'none');
            YAHOO.ympyui.util.Dom.setStyle('ymp-rhap-getunlimited', 'display', 'none');
            this.runCarousel(this.defaultCarouselContent);
        }
        var errorIconElm = YAHOO.ympyui.util.Dom.getElementsByClassName('ymp-error-icon', null, this.currentMedia.id)[0];
        if (errorIconElm != null) {
            this.showCurrentErrorIcon(errorIconElm);
        } else {
            this.hideCurrentErrorIcon();
        }
    }
};
YAHOO.mediaplayer.View.prototype.updateMediaMetadata = function() {
    document.getElementById('ymp-meta-progress').innerHTML = "";
    var trackTitleElm = document.getElementById('ymp-meta-track-title');
    var artistTitleElm = document.getElementById('ymp-meta-artist-title');
    var albumTitleElm = document.getElementById('ymp-meta-album-title');
    var albumArtElm = document.getElementById('ymp-meta-image');
    if (YAHOO.mediaplayer.Util.BROWSER == 'Firefox') {
        var win = this.XULWin.cloneNode(true);
        win.firstChild.setAttribute('value', this.currentMedia.title);
        trackTitleElm.innerHTML = "";
        trackTitleElm.appendChild(win);
        win = this.XULWin.cloneNode(true);
        win.firstChild.setAttribute('value', this.currentMedia.artistName);
        artistTitleElm.innerHTML = "";
        artistTitleElm.appendChild(win);
        win = this.XULWin.cloneNode(true);
        win.firstChild.setAttribute('value', this.currentMedia.albumName);
        albumTitleElm.innerHTML = "";
        albumTitleElm.appendChild(win);
    } else {
        trackTitleElm.innerHTML = this.currentMedia.title;
        artistTitleElm.innerHTML = this.currentMedia.artistName;
        albumTitleElm.innerHTML = this.currentMedia.albumName;
    }
    var args = [];
    var str = "";
    args = [this.currentMedia.title];
    str = YAHOO.mediaplayer.Util.sprintf(YAHOO.mediaplayer.DisplayStrings.tooltips.NOWPLAYING_TRACK, args);
    trackTitleElm.title = str;
    if (this.currentMedia.artistName && this.currentMedia.artistName.length > 0) {
        args = [this.currentMedia.artistName];
        str = YAHOO.mediaplayer.Util.sprintf(YAHOO.mediaplayer.DisplayStrings.tooltips.NOWPLAYING_ARTIST, args);
        artistTitleElm.title = str;
    } else {
        artistTitleElm.title = "";
    }
    if (this.currentMedia.albumName && this.currentMedia.albumName.length > 0) {
        args = [this.currentMedia.albumName];
        str = YAHOO.mediaplayer.Util.sprintf(YAHOO.mediaplayer.DisplayStrings.tooltips.NOWPLAYING_ALBUM, args);
        albumTitleElm.title = str;
    } else {
        albumTitleElm.title = "";
    }
    if (typeof(this.currentMedia.albumArt) === "string" && this.currentMedia.albumArt.length > 0) {
        albumArtElm.getElementsByTagName('img')[0].src = this.currentMedia.albumArt;
    } else {
        albumArtElm.getElementsByTagName('img')[0].src = YAHOO.mediaplayer.YMPParams.defaultalbumart;
    }
    var prefix = "http://search.yahoo.com/search?fr=client_ymp&p=";
    var yMusicPrefix = "http://music.yahoo.com/";
    var temp = "";
    if (this.currentMedia.mimeType === YAHOO.mediaplayer.Parser.MimeTypes.yahoo && this.currentMedia.yTrackID && this.currentMedia.yTrackID.length > 0) {
        trackTitleElm.href = yMusicPrefix + "track/" + this.currentMedia.yTrackID;
    } else if (typeof(this.currentMedia.title) === "string" && this.currentMedia.title.length > 0) {
        temp = this.currentMedia.title;
        if (typeof(this.currentMedia.artistName) === "string") {
            temp += " " + this.currentMedia.artistName;
        }
        temp = encodeURIComponent(temp.replace(/["]/g, ''));
        trackTitleElm.href = prefix + temp;
    }
    if (this.currentMedia.mimeType === YAHOO.mediaplayer.Parser.MimeTypes.yahoo && this.currentMedia.yAlbumID && this.currentMedia.yAlbumID.length > 0) {
        albumTitleElm.href = yMusicPrefix + "release/" + this.currentMedia.yAlbumID;
        albumArtElm.href = albumTitleElm.href;
    } else if (typeof(this.currentMedia.albumName) === "string" && this.currentMedia.albumName.length > 0) {
        temp = this.currentMedia.albumName;
        if (typeof(this.currentMedia.artistName) === "string") {
            temp += " " + this.currentMedia.artistName;
        }
        temp = encodeURIComponent(temp.replace(/["]/g, ''));
        albumTitleElm.href = prefix + temp;
        albumArtElm.href = albumTitleElm.href;
    } else {
        var domainRegex = /^([a-zA-Z]+:\/\/)?([^\/]+)\/.*?$/;
        if (this.currentMedia.token.match(domainRegex) && document.domain !== RegExp.$2) {
            albumTitleElm.href = albumTitleElm.innerHTML = albumTitleElm.title = RegExp.$1 + RegExp.$2;
        } else {
            albumTitleElm.href = albumTitleElm.innerHTML = albumTitleElm.title = "";
        }
        albumArtElm.href = trackTitleElm.href;
    }
    if (this.currentMedia.mimeType === YAHOO.mediaplayer.Parser.MimeTypes.yahoo && this.currentMedia.yArtistID && this.currentMedia.yArtistID.length > 0) {
        artistTitleElm.href = yMusicPrefix + "ar-" + this.currentMedia.yArtistID;
    } else if (typeof(this.currentMedia.artistName) === "string" && this.currentMedia.artistName.length > 0) {
        temp = encodeURIComponent(this.currentMedia.artistName.replace(/["]/g, ''));
        artistTitleElm.href = prefix + temp;
    } else {
        artistTitleElm.href = "";
    }
    document.getElementById('ymp-getlyrics').href = "http://search.music.yahoo.com/search/?m=lyrics&p=" + encodeURIComponent(this.currentMedia.title);
    var videoLinkElm = document.getElementById('ymp-watchvideo');
    if (this.currentMedia.mimeType === YAHOO.mediaplayer.Parser.MimeTypes.yahoo && this.currentMedia.yVideoID && this.currentMedia.yVideoID.length > 0) {
        videoLinkElm.href = "http://new.music.yahoo.com/videos/--" + this.currentMedia.yVideoID;
        videoLinkElm.innerHTML = 'watch the video<br/><em>for this song</em><span class="ymp-skin"></span>';
    } else {
        document.getElementById('ymp-watchvideo').href = "http://search.music.yahoo.com/search/?m=video&p=" + encodeURIComponent(this.currentMedia.title);
        videoLinkElm.innerHTML = 'find the video<br/><em>for this song</em><span class="ymp-skin"></span>';
    }
    if (this.currentMedia == this.playlistArray[0]) {
        YAHOO.ympyui.util.Dom.removeClass('ymp-next', 'ymp-btn-next-disabled');
        YAHOO.ympyui.util.Dom.addClass('ymp-prev', 'ymp-btn-prev-disabled');
    } else if (this.currentMedia == this.playlistArray[this.playlistArray.length - 1]) {
        YAHOO.ympyui.util.Dom.removeClass('ymp-prev', 'ymp-btn-prev-disabled');
        YAHOO.ympyui.util.Dom.addClass('ymp-next', 'ymp-btn-next-disabled');
    } else {
        YAHOO.ympyui.util.Dom.removeClass('ymp-next', 'ymp-btn-next-disabled');
        YAHOO.ympyui.util.Dom.removeClass('ymp-prev', 'ymp-btn-prev-disabled');
    }
    if (this.currentMedia.mimeType == YAHOO.mediaplayer.Parser.MimeTypes.rhapsody || this.currentMedia.mimeType == YAHOO.mediaplayer.Parser.MimeTypes.yahoo) {
        if (typeof(this.currentMedia.buyURL) === "string" && this.currentMedia.buyURL.length > 0) {
            YAHOO.ympyui.util.Dom.setStyle('ymp-btn-buy', 'display', 'block');
            this.updateBuyButtonURL(this.currentMedia);
        } else {
            YAHOO.ympyui.util.Dom.setStyle('ymp-btn-buy', 'display', 'none');
        }
    } else {
        if (typeof(YAHOO.mediaplayer.YMPParams.amazonid) === "string" && YAHOO.mediaplayer.YMPParams.amazonid.length > 0) {
            YAHOO.ympyui.util.Dom.setStyle('ymp-btn-buy', 'display', 'block');
            this.updateBuyButtonURL(this.currentMedia);
        } else {
            YAHOO.ympyui.util.Dom.setStyle('ymp-btn-buy', 'display', 'none');
        }
    }
    trackTitleElm = null;
    artistTitleElm = null;
    albumTitleElm = null;
    albumArtElm = null;
};
YAHOO.mediaplayer.View.prototype.showCurrentErrorIcon = function(errorIconElm) {
    var errorID = this.getErrorIDFromClassName(errorIconElm.className);
    document.getElementById('ymp-current-media-error').className = "ymp-error-icon ymp-skin ymp-error-id-" + errorID;
    YAHOO.ympyui.util.Dom.setStyle('ymp-current-media-error', 'display', 'block');
};
YAHOO.mediaplayer.View.prototype.hideCurrentErrorIcon = function() {
    YAHOO.ympyui.util.Dom.setStyle('ymp-current-media-error', 'display', 'none');
};
YAHOO.mediaplayer.View.prototype.updateBuyButtonURL = function(media) {
    var elmBuy = document.getElementById("ymp-btn-buy");
    if (media.mimeType == YAHOO.mediaplayer.Parser.MimeTypes.rhapsody || media.mimeType == YAHOO.mediaplayer.Parser.MimeTypes.yahoo) {
        if (media.buyURL && media.buyURL.length > 0) {
            elmBuy.setAttribute("href", media.buyURL);
        } else {}
    } else {
        var affiliateID = YAHOO.mediaplayer.YMPParams.amazonid;
        if (affiliateID == null) {
            var randomnumber = Math.floor(Math.random() * 2);
            var yAmazonId = (randomnumber == 0) ? "thremid-20": "williamkhoes-20";
            affiliateID = yAmazonId;
        }
        var amazonLink = "http://www.amazon.com/gp/search?ie=UTF8&tag=" + affiliateID + "&index=blended&linkCode=ur2&camp=1789&creative=9325&keywords=";
        if (media.artistName != null && media.artistName != "") {
            amazonLink += encodeURIComponent(media.artistName);
        }
        if (media.title != null && media.title != "") {
            amazonLink += encodeURIComponent(" " + media.title);
        }
        elmBuy.setAttribute("href", amazonLink);
    }
};
YAHOO.mediaplayer.View.prototype.onPlayStateChange = function(o) {
    var icon, maxBtn = document.getElementById('ymp-btn-max');
    if (maxBtn != null) {
        icon = maxBtn.getElementsByTagName('span')[0];
    }
    var metaProgress = document.getElementById('ymp-meta-progress');
    switch (o.newState) {
    case YAHOO.mediaplayer.MediaEngine.PlayState.PLAYING:
        YAHOO.ympyui.util.Dom.removeClass('ymp-play', 'ymp-btn-play');
        YAHOO.ympyui.util.Dom.addClass('ymp-play', 'ymp-btn-pause');
        var elm = document.getElementById('ymp-play');
        if (elm) {
            elm.setAttribute("title", YAHOO.mediaplayer.DisplayStrings.tooltips.PAUSE);
            elm = null;
        }
        var anchor = o.media.anchor;
        if (anchor == null) {
            anchor = o.media.parent.anchor;
        }
        YAHOO.ympyui.util.Dom.removeClass(anchor, 'ymp-btn-page-play');
        YAHOO.ympyui.util.Dom.addClass(anchor, 'ymp-btn-page-pause');
        if (document.getElementById('ymp-player') != null) {
            YAHOO.ympyui.util.Dom.addClass(icon, 'ymp-animarrow');
            if (o.media.mimeType == YAHOO.mediaplayer.Parser.MimeTypes.rhapsody || o.media.mimeType == YAHOO.mediaplayer.Parser.MimeTypes.yahoo) {
                var subType = YAHOO.mediaplayer.Controller.rhapsody.getSubType();
                var freePlays = YAHOO.mediaplayer.Controller.rhapsody.getFreePlays();
                if (subType == null || subType == "RHAPSODY_25") {
                    YAHOO.ympyui.util.Dom.setStyle('ymp-rhap-getunlimited', 'display', 'block');
                    document.getElementById('ymp-rhap-playcount').innerHTML = freePlays;
                    this.runCarousel(this.rhap25CarouselContent);
                } else {
                    YAHOO.ympyui.util.Dom.setStyle('ymp-rhap-getunlimited', 'display', 'none');
                    this.runCarousel(this.rhapCarouselContent);
                }
            }
        }
        break;
    case YAHOO.mediaplayer.MediaEngine.PlayState.ENDED:
    case YAHOO.mediaplayer.MediaEngine.PlayState.STOPPED:
        if (metaProgress != null) {
            metaProgress.innerHTML = "";
        }
    case YAHOO.mediaplayer.MediaEngine.PlayState.PAUSED:
        YAHOO.ympyui.util.Dom.removeClass('ymp-play', 'ymp-btn-pause');
        YAHOO.ympyui.util.Dom.addClass('ymp-play', 'ymp-btn-play');
        var elm = document.getElementById('ymp-play');
        if (elm) {
            elm.setAttribute("title", YAHOO.mediaplayer.DisplayStrings.tooltips.PLAY);
            elm = null;
        }
        var anchor = o.media.anchor;
        if (anchor == null) {
            anchor = o.media.parent.anchor;
        }
        YAHOO.ympyui.util.Dom.removeClass(anchor, 'ymp-btn-page-pause');
        YAHOO.ympyui.util.Dom.addClass(anchor, 'ymp-btn-page-play');
        YAHOO.ympyui.util.Dom.removeClass(icon, 'ymp-animarrow');
        break;
    case YAHOO.mediaplayer.MediaEngine.PlayState.BUFFERING:
        if (metaProgress != null) {
            metaProgress.innerHTML = YAHOO.mediaplayer.DisplayStrings.ui.BUFFERING;
        }
        if (document.getElementById(o.media.id) != null) {
            var errorIconElm = YAHOO.ympyui.util.Dom.getElementsByClassName('ymp-error-icon', null, o.media.id)[0];
            errorIconElm.parentNode.removeChild(errorIconElm);
            this.hideCurrentErrorIcon();
        }
        break;
    }
};
YAHOO.mediaplayer.View.prototype.onError = function(error) {
    document.getElementById('ymp-meta-progress').innerHTML = "";
    if (error.type == YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD) {
        var mediaTrayAnchor = document.getElementById(error.media.id);
        var errorIconElm = document.createElement('span');
        errorIconElm.className = "ymp-error-icon ymp-skin ymp-error-id-" + error.id;
        mediaTrayAnchor.appendChild(errorIconElm);
        if (error.media == this.currentMedia) {
            this.showCurrentErrorIcon(errorIconElm);
        }
    } else {
        this.showErrorStickwall(error.getDisplayMessage());
    }
};
YAHOO.mediaplayer.View.prototype.setUpUiStrings = function() {
    var str = "";
    var args = [];
    var elm = document.getElementById("ymp-prev");
    if (elm) {
        str = YAHOO.mediaplayer.DisplayStrings.tooltips.PREVIOUS;
        elm.setAttribute("title", str);
    }
    elm = document.getElementById("ymp-play");
    if (elm) {
        str = YAHOO.mediaplayer.DisplayStrings.tooltips.PLAY;
        elm.setAttribute("title", str);
    }
    elm = document.getElementById("ymp-next");
    if (elm) {
        str = YAHOO.mediaplayer.DisplayStrings.tooltips.NEXT;
        elm.setAttribute("title", str);
    }
    elm = document.getElementById("ymp-volume-thumb");
    if (elm) {
        var vol = parseInt(this.controller.mediaengine.getVolume() * 100);
        args = [vol];
        str = YAHOO.mediaplayer.Util.sprintf(YAHOO.mediaplayer.DisplayStrings.tooltips.VOLUME, args);
        elm.setAttribute("title", str);
    }
    elm = document.getElementById("ymp-btn-buy");
    if (elm) {
        str = YAHOO.mediaplayer.DisplayStrings.tooltips.BUY;
        elm.setAttribute("title", str);
    }
    elm = document.getElementById("ymp-btn-tray");
    if (elm) {
        str = YAHOO.mediaplayer.DisplayStrings.tooltips.OPENPLAYLIST;
        elm.setAttribute("title", str);
    }
    elm = document.getElementById("ymp-btn-max");
    if (elm) {
        str = YAHOO.mediaplayer.DisplayStrings.tooltips.MAXIMIZE;
        elm.setAttribute("title", str);
    }
    elm = document.getElementById("ymp-btn-min");
    if (elm) {
        str = YAHOO.mediaplayer.DisplayStrings.tooltips.MINIMIZE;
        elm.setAttribute("title", str);
    }
    elm = document.getElementById("ymp-btn-close");
    if (elm) {
        str = YAHOO.mediaplayer.DisplayStrings.tooltips.CLOSE;
        elm.setAttribute("title", str);
    }
    elm = document.getElementById("ymp-btn-target");
    if (elm) {
        str = YAHOO.mediaplayer.DisplayStrings.tooltips.FINDONPAGE;
        elm.setAttribute("title", str);
    }
    elm = null;
};
YAHOO.mediaplayer.View.prototype.displayRhapsodyStickwall = function() {
    if (document.getElementById('ymp-player') != null) {
        this.resizePlayer(YAHOO.mediaplayer.View.DisplayState.MAX);
        YAHOO.ympyui.util.Dom.removeClass('ymp-stickwall', this.currentStickwall);
        YAHOO.ympyui.util.Dom.addClass('ymp-stickwall', 'ymp-rhap-stickwall');
        YAHOO.ympyui.util.Dom.setStyle('ymp-stickwall', 'display', 'block');
        YAHOO.ympyui.util.Dom.setStyle(this.currentStickwall, 'display', 'none');
        YAHOO.ympyui.util.Dom.setStyle('ymp-rhap-stickwall', 'display', 'block');
        this.currentStickwall = 'ymp-rhap-stickwall';
    }
};
YAHOO.mediaplayer.View.prototype.onRhapContinueClick = function(e) {
    YAHOO.ympyui.util.Event.stopEvent(e);
    this.EventManager.onPlayRequest.fire();
};
YAHOO.mediaplayer.View.prototype.keyHandler = function(e) {
    try {
        var shift = e.shiftKey;
        var alt = e.altKey;
        var ctrl = e.ctrlKey;
        var key = e.keyCode;
        var kc = YAHOO.mediaplayer.Util.keycodes;
        if (key == kc.KEY_SPACE && shift === true) {
            YAHOO.ympyui.util.Event.stopEvent(e);
            var elm = document.getElementById("ymp-play");
            if (YAHOO.ympyui.util.Dom.hasClass(elm, 'ymp-btn-pause')) {
                YAHOO.MediaPlayer.pause();
            } else {
                YAHOO.MediaPlayer.play();
            }
            elm = null;
            return false;
        }
        if (key == kc.KEY_LEFT && shift === true) {
            YAHOO.ympyui.util.Event.stopEvent(e);
            YAHOO.MediaPlayer.previous();
            return false;
        }
        if (key == kc.KEY_RIGHT && shift === true) {
            YAHOO.ympyui.util.Event.stopEvent(e);
            YAHOO.MediaPlayer.next();
            return false;
        }
        if ((key == kc.KEY_UP || key == kc.KEY_DOWN) && shift === true) {
            YAHOO.ympyui.util.Event.stopEvent(e);
            var currVol = YAHOO.MediaPlayer.getVolume();
            if (key == kc.KEY_UP && currVol < 1) {
                YAHOO.MediaPlayer.setVolume(currVol + 0.01);
            }
            if (key == kc.KEY_DOWN && currVol > 0) {
                YAHOO.MediaPlayer.setVolume(currVol - 0.01);
            }
            return false;
        }
        if (key == kc.KEY_P && shift === true && ctrl === true) {
            YAHOO.ympyui.util.Event.stopEvent(e);
            this.toggleTray();
            var elm = document.getElementById("ymp-btn-tray");
            if (elm) {
                elm.focus();
                elm = null;
            }
            return false;
        }
    } catch(err) {}
};
YAHOO.mediaplayer.View.prototype.trayKeyHandler = function(e) {
    try {
        if (YAHOO.ympyui.util.Dom.hasClass('ymp-btn-tray', 'ymp-btn-tray-open')) {
            var key = e.keyCode;
            var kc = YAHOO.mediaplayer.Util.keycodes;
            if ((key === kc.KEY_UP || key === kc.KEY_DOWN) && this.playlistArray.length > 0) {
                YAHOO.ympyui.util.Event.stopEvent(e);
                var currentID = (e.srcElement) ? e.srcElement.id: e.target.id;
                var trackLink = null;
                if (currentID === "ymp-btn-tray") {
                    trackLink = document.getElementById(this.playlistArray[0].id);
                } else {
                    for (var i = 0; i < (this.playlistArray.length); i++) {
                        if (this.playlistArray[i].id == currentID) {
                            if (key === kc.KEY_DOWN && i < (this.playlistArray.length - 1)) {
                                trackLink = document.getElementById(this.playlistArray[i + 1].id);
                                break;
                            } else if (key === kc.KEY_UP && i > 0) {
                                trackLink = document.getElementById(this.playlistArray[i - 1].id);
                                break;
                            }
                        }
                    }
                }
                if (trackLink) {
                    this.clearFocusClass(trackLink);
                    trackLink.focus();
                    trackLink = null;
                }
                return false;
            }
        }
    } catch(err) {}
};
YAHOO.mediaplayer.View.prototype.trayMouseOverHandler = function(e) {
    this.clearFocusClass();
};
YAHOO.mediaplayer.View.prototype.clearFocusClass = function(objLinkNoClear) {
    for (var i = 0,
    len = this.playlistArray.length; i < len; i++) {
        link = document.getElementById(this.playlistArray[i].id);
        if (objLinkNoClear && (link === objLinkNoClear)) {
            YAHOO.ympyui.util.Dom.addClass(objLinkNoClear, 'ymp-tray-track-focus');
        } else {
            YAHOO.ympyui.util.Dom.removeClass(link, 'ymp-tray-track-focus');
        }
        link = null;
    }
};
YAHOO.mediaplayer.View.prototype.clearPlaylistTray = function(trayList) {
    var listItems = trayList.getElementsByTagName("a");
    while (listItems.length > 0) {
        YAHOO.ympyui.util.Dom.removeClass(this.controller.playlistmanager.getMediaById(listItems[0].id).anchor, 'ymp-btn-page-play');
        trayList.removeChild(listItems[0].parentNode);
        listItems = trayList.getElementsByTagName("a");
    }
};
YAHOO.mediaplayer.View.prototype.reorderTraylist = function(trayList) {
    trayItems = trayList.getElementsByTagName("a");
    var trayItemsLen = trayItems.length;
    var j = 1;
    for (var i = 0; i < trayItemsLen; i++) {
        var temp = YAHOO.ympyui.util.Dom.getElementsByClassName("ymp-list-numbering", null, trayItems[i]);
        if (temp.length > 0) {
            temp[0].innerHTML = j;
            j++;
        }
    }
};
YAHOO.mediaplayer.View.prototype.updatePlaylist = function(playlist, trayList) {
    var len1 = playlist.length;
    var li, listItem
    for (var i = 0; i < len1; i++) {
        if (!this.currentPlaylist[i]) {
            li = this.getLi(playlist[i]);
            trayList.appendChild(li);
            this.currentPlaylist[i] = playlist[i];
        } else if (this.currentPlaylist[i] == playlist[i]) {
            continue;
        } else {
            if (i == 0) {
                li = this.getLi(playlist[i]);
                listItem = YAHOO.ympyui.util.Dom.getChildren(trayList)[0];
                YAHOO.ympyui.util.Dom.insertBefore(li, listItem);
                this.currentPlaylist.splice(i, 0, playlist[i]);
            } else {
                li = this.getLi(playlist[i]);
                listItem = document.getElementById(playlist[i - 1].id).parentNode;
                YAHOO.ympyui.util.Dom.insertAfter(li, listItem);
                this.currentPlaylist.splice(i, 0, playlist[i]);
            }
        }
    }
    this.reorderPlaylistTray(trayList);
};
YAHOO.mediaplayer.View.prototype.getLi = function(mediaObject) {
    var li = document.createElement('li');
    if (mediaObject instanceof YAHOO.mediaplayer.Playlist) {
        li.innerHTML = '<a id="' + mediaObject.id + '" class="ymp-tray-playlist ymp-color-text-tray" href="#"><span class="ymp-numbering"></span>. <b>Loading playlist ...</b><span class="ymp-skin ymp-right-arrow"></span>' + '</a>';
    } else {
        li.innerHTML = '<a id="' + mediaObject.id + '" class="ymp-tray-track ymp-color-text-tray" href="#"><span class="ymp-numbering"></span>. <b>Loading track ...</b><em></em>' + '</a>';
    }
    return li;
};
YAHOO.mediaplayer.View.prototype.reorderPlaylistTray = function(trayList) {
    var listItems = YAHOO.ympyui.util.Dom.getElementsByClassName("ymp-numbering", "span", trayList);
    var len = listItems.length;
    for (var i = 0; i < len; i++) {
        listItems[i].innerHTML = i + 1;
    }
};
YAHOO.mediaplayer.View.prototype.showSecretMsg = function(e) {
    if (e.button && e.button == 2) {
        var elm = document.getElementById("ymp-secret-bubble");
        if (elm) {
            var pos = [YAHOO.ympyui.util.Event.getPageX(e), YAHOO.ympyui.util.Event.getPageY(e)];
            YAHOO.ympyui.util.Dom.setStyle(elm, 'display', 'block');
            YAHOO.ympyui.util.Dom.setXY(elm, [pos[0] + 13, pos[1] - elm.offsetHeight - 23]);
            elm = null;
        }
        YAHOO.ympyui.util.Event.stopEvent(e);
        return false;
    }
};
YAHOO.mediaplayer.View.prototype.hideSecretMsg = function(e) {
    var elm = document.getElementById("ymp-secret-bubble");
    if (elm) {
        YAHOO.ympyui.util.Dom.setStyle(elm, 'display', 'none');
        elm = null;
    }
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.ViewMarkup = {
    body: '  <div id="ymp-body" class="ymp-skin">   <h2>' + YAHOO.mediaplayer.DisplayStrings.ui.HDR_MAIN + '</h2>   <a id="ymp-btn-max" href="#"><span class="ymp-skin" title="Maximize the player"></span></a>   <div id="ymp-yahoo-logo" class="ymp-skin" oncontextmenu="return false;"></div>   <div id="ymp-control" class="ymp-skin">    <h3>' + YAHOO.mediaplayer.DisplayStrings.ui.HDR_CONTROLS + '</h3>    <a id="ymp-prev" class="ymp-btn-prev ymp-skin" href="#" title="Previous track">Previous</a>    <a id="ymp-play" class="ymp-btn-play ymp-skin" href="#" title="Play/pause track">Play</a>    <a id="ymp-next" class="ymp-btn-next ymp-skin" href="#" title="Next track">Next</a>   </div>   <div id="ymp-volume" class="ymp-skin">    <div id="ymp-volume-cover"><span class="ymp-skin"></span></div>    <a id="ymp-volume-thumb" class="ymp-skin" href="#" title="Adjust volume">Vol</a>   </div>   <div id="ymp-meta" class="ymp-skin">    <div id="ymp-stickwall">     <div class="ymp-stickwall-body1"></div>     <div class="ymp-stickwall-body2"></div>     <div class="ymp-stickwall-body3"></div>     <div class="ymp-stickwall-body4"></div>     <div class="ymp-stickwall-body5"></div>     <div class="ymp-stickwall-gradient ymp-skin"></div>     <div id="ymp-rhap-stickwall">      <a class="ymp-rhap-powered" href="#">powered by Rhapsody</a>      <h2>Keep the music going</h2>      <div id="ymp-rhap-stickwall-action">       <a id="ymp-rhap-continue" href="#" class="ymp-btn-alt ymp-skin">Continue enjoying the music<em class="ymp-skin"></em></a>       <a href="http://offer.rhapsody.com/yahooplayer/?ocode=' + YAHOO.mediaplayer.YMPParams.rhappcode + '&pcode=' + YAHOO.mediaplayer.YMPParams.rhappcode + '&cpath=unlplaylink&rsrc=' + ((YAHOO.mediaplayer.YMPParams.ypartner && YAHOO.mediaplayer.YMPParams.ypartner.length > 0) ? YAHOO.mediaplayer.YMPParams.ypartner: 'yahoo') + '" target="_blank" class="ymp-btn-alt ymp-skin">Get Rhapsody Unlimited Now<em class="ymp-skin"></em></a>      </div>       </div>     <div id="ymp-error-stickwall">      <h2><span class="ymp-skin"></span><span id="ymp-critical-error-msg"></span></h2>       </div>    </div>    <a id="ymp-meta-image" href="#" target="_blank"><img src="http://l.yimg.com/us.yimg.com/i/us/mus/ymwp/mediaplayer-default-album.gif" width="46" height="46"/></a>    <table id="ymp-meta-top" cellspacing="0" cellpadding="0" border="0">     <tr>      <td width="100%"><div class="ymp-meta-box"><a id="ymp-meta-track-title" class="ymp-color-text-main" href="#" target="_blank"></a><a id="ymp-meta-artist-title" class="ymp-color-text-main" href="#" target="_blank"></a>&nbsp;</div></td>      <td><a id="ymp-current-media-error" class="ymp-error-icon ymp-skin" href="#" style="display:none;">Error</a></td>      <td>       <div class="ymp-meta-box">        <a id="ymp-rhap-getunlimited" href="http://offer.rhapsody.com/yahooplayer/?ocode=' + YAHOO.mediaplayer.YMPParams.rhappcode + '&pcode=' + YAHOO.mediaplayer.YMPParams.rhappcode + '&cpath=unlplaylink&rsrc=' + ((YAHOO.mediaplayer.YMPParams.ypartner && YAHOO.mediaplayer.YMPParams.ypartner.length > 0) ? YAHOO.mediaplayer.YMPParams.ypartner: 'yahoo') + '" target="_blank">get unlimited plays <em>&raquo;</em></a>        <a id="ymp-rhap-brand" class="ymp-rhap-powered" href="http://offer.rhapsody.com/yahooplayer/?ocode=' + YAHOO.mediaplayer.YMPParams.rhappcode + '&pcode=' + YAHOO.mediaplayer.YMPParams.rhappcode + '&cpath=powerby&rsrc=' + ((YAHOO.mediaplayer.YMPParams.ypartner && YAHOO.mediaplayer.YMPParams.ypartner.length > 0) ? YAHOO.mediaplayer.YMPParams.ypartner: 'yahoo') + '" target="_blank">powered by Rhapsody</a>       </div>      </td>     </tr>    </table>    <table id="ymp-meta-bottom" cellspacing="0" cellpadding="0" border="0">     <tr>      <td width="100%"><div class="ymp-meta-box"><a id="ymp-meta-album-title" class="ymp-color-text-main" href="#" target="_blank"></a>&nbsp;</div></td>      <td><div id="ymp-meta-progress" class="ymp-color-text-main"></div></td>      <td>       <a id="ymp-btn-buy" class="ymp-btn ymp-skin" href="#" target="_blank" title="Buy track">        <span class="ymp-skin ymp-icon-buy"></span>        <em class="ymp-skin"></em>       </a>      </td>      <td>       <div class="ymp-meta-box">        <a id="ymp-btn-target" class="ymp-skin" href="#" title="Find track on page">Focus on current media</a>       </div>      </td>     </tr>    </table>   </div>   <div id="ymp-relevance">    <div id="ymp-rhap-playinfo" class="ymp-carousel-item">     <span id="ymp-rhap-playcount">24</span><span>plays left</span>     <a href="http://offer.rhapsody.com/yahooplayer/?ocode=' + YAHOO.mediaplayer.YMPParams.rhappcode + '&pcode=' + YAHOO.mediaplayer.YMPParams.rhappcode + '&cpath=playcntlink&rsrc=' + ((YAHOO.mediaplayer.YMPParams.ypartner && YAHOO.mediaplayer.YMPParams.ypartner.length > 0) ? YAHOO.mediaplayer.YMPParams.ypartner: 'yahoo') + '" target="_blank">get unlimited plays learn how <em>&raquo;</em></a>    </div>    <a id="ymp-getplayer" class="ymp-carousel-item" href="' + ((YAHOO.mediaplayer.YMPParams["injected-by"] == "foxytunes-ff") ? "http://www.foxytunes.com/mediaplayers/yahoo-media-player/": "http://mediaplayer.yahoo.com") + '" target="_blank">learn more about this player</a>    <a id="ymp-getlyrics" class="ymp-carousel-item" href="http://search.music.yahoo.com/search/?m=lyrics&p=" target="_blank">find the lyrics<br/><em>for this song</em><span class="ymp-skin"></span></a>    <a id="ymp-watchvideo" class="ymp-carousel-item" href="http://search.music.yahoo.com/search/?m=video&p=" target="_blank">find the video<br/><em>for this song</em><span class="ymp-skin"></span></a>   </div>   <a id="ymp-btn-close" href="#" class="ymp-skin" title="Close the player"></a>   <a id="ymp-btn-pop" href="#" class="ymp-skin" title="Continue playback in a separate window"></a>   <a id="ymp-btn-min" class="ymp-skin" href="#" title="Minimize the player"></a>   <div id="ymp-body-base">    <div id="ymp-body-strip" class="ymp-color-main"></div>    <div id="ymp-body-cap">     <div class="ymp-color-main ymp-pix-dark ymp-pix-tr1"></div>              <div class="ymp-color-main ymp-pix-light ymp-pix-tr2"></div>              <div class="ymp-color-main ymp-pix-light ymp-pix-tr3"></div>              <div class="ymp-color-main ymp-pix-dark ymp-pix-tr4"></div>     <div class="ymp-color-main ymp-cap-body1"></div>     <div class="ymp-color-main ymp-cap-body2"></div>     <div class="ymp-color-main ymp-pix-dark ymp-pix-br1"></div>              <div class="ymp-color-main ymp-pix-light ymp-pix-br2"></div>              <div class="ymp-color-main ymp-pix-light ymp-pix-br3"></div>              <div class="ymp-color-main ymp-pix-dark ymp-pix-br4"></div>    </div>   </div>  </div>  <form id="ymwp-contplay-form" name="contplayform" action="" method="post" target="ymediaplayer">   <input name="token" type="hidden" value="0"/>   <input name="seek" type="hidden" value="0"/>   <input name="vol" type="hidden" value="0"/>   <input name="trackhtml" type="hidden" value=""/>  </form> ',
    tray: ' <div id="ymp-tray-body" class="ymp-skin">  <a id="ymp-btn-tray" class="ymp-btn-tray-closed" href="#" title="Open/close the Playlist tray">   <span class="ymp-color-tray ymp-pix-dark ymp-pix-tl1"></span>            <span class="ymp-color-tray ymp-pix-light ymp-pix-tl2"></span>           <span class="ymp-color-tray ymp-pix-light ymp-pix-tl3"></span>           <span class="ymp-color-tray ymp-pix-dark ymp-pix-tl4"></span>   <span class="ymp-color-tray ymp-btn-tray-body1"></span>   <span class="ymp-color-tray ymp-btn-tray-body2"></span>   <span class="ymp-color-tray ymp-btn-tray-body3"></span>   <span class="ymp-color-tray ymp-btn-tray-body4"></span>   <span class="ymp-color-tray ymp-pix-dark ymp-pix-tr1"></span>            <span class="ymp-color-tray ymp-pix-light ymp-pix-tr2"></span>            <span class="ymp-color-tray ymp-pix-light ymp-pix-tr3"></span>            <span class="ymp-color-tray ymp-pix-dark ymp-pix-tr4"></span>            <span class="ymp-color-tray ymp-pix-dark ymp-pix-bl"></span>   <span id="ymp-playlist-arrow" class="ymp-skin ymp-up-arrow"></span>   <em class="ymp-color-tray ymp-color-text-tray">Playlist</em>  </a>  <div id="ymp-tray-top">   <span class="ymp-color-tray ymp-pix-dark ymp-pix-tl1"></span>            <span class="ymp-color-tray ymp-pix-light ymp-pix-tl2"></span>            <span class="ymp-color-tray ymp-pix-light ymp-pix-tl3"></span>            <span class="ymp-color-tray ymp-pix-dark ymp-pix-tl4"></span>   <span class="ymp-color-tray ymp-tray-body1"></span>   <span class="ymp-color-tray ymp-tray-body2"></span>   <span class="ymp-color-tray ymp-tray-body3"></span>  </div>  <h3>' + YAHOO.mediaplayer.DisplayStrings.ui.HDR_PLAYLIST + '</h3>  <ul id="ymp-tray-list" class="ymp-color-tray">  </ul> </div> '
};
YAHOO.MediaPlayer.init = function() {
    this.controller = YAHOO.mediaplayer.Controller;
    YAHOO.MediaPlayer.onPlaylistUpdate = new YAHOO.ympyui.util.CustomEvent("onPlaylistUpdate", null, false, YAHOO.ympyui.util.CustomEvent.FLAT);
    YAHOO.MediaPlayer.onProgress = new YAHOO.ympyui.util.CustomEvent("onProgress", null, false, YAHOO.ympyui.util.CustomEvent.FLAT);
    YAHOO.MediaPlayer.onTrackStart = new YAHOO.ympyui.util.CustomEvent("onTrackStart", null, false, YAHOO.ympyui.util.CustomEvent.FLAT);
    YAHOO.MediaPlayer.onTrackPause = new YAHOO.ympyui.util.CustomEvent("onTrackPause", null, false, YAHOO.ympyui.util.CustomEvent.FLAT);
    YAHOO.MediaPlayer.onTrackComplete = new YAHOO.ympyui.util.CustomEvent("onTrackComplete", null, false, YAHOO.ympyui.util.CustomEvent.FLAT);
    YAHOO.MediaPlayer.onMediaUpdate = new YAHOO.ympyui.util.CustomEvent("onMediaUpdate", null, false, YAHOO.ympyui.util.CustomEvent.FLAT);
    var subscribeToControllersEvents = ['onPlaylistUpdate', 'onMediaProgress', 'onPlayStateChange', 'onMediaUpdate'];
    this.controller.EventManager.subscribe(this, subscribeToControllersEvents);
    YAHOO.MediaPlayer.onAPIReady.fire();
    if (document.createEvent) {
        var evt = document.createEvent("Event");
        evt.initEvent("GooseInit", false, false);
        if (document.body.dispatchEvent) {
            document.body.dispatchEvent(evt);
        }
    }
};
YAHOO.MediaPlayer.toString = function() {
    return 'YAHOO.MediaPlayer';
};
YAHOO.MediaPlayer.handleEvent = function(evType, args) {
    try {
        var suffix = 'Handler';
        if (typeof(this[evType + suffix]) === 'function') {
            this[evType + suffix](args[0]);
        }
    } catch(ex) {
        throw new Error('ERROR in YAHOO.MediaPlayer.handleEvent(evType:"' + evType + '"). ' + ex.message);
    }
};
YAHOO.MediaPlayer.onPlaylistUpdateHandler = function(playlistArray) {
    YAHOO.MediaPlayer.onPlaylistUpdate.fire(playlistArray);
};
YAHOO.MediaPlayer.onMediaUpdateHandler = function(mediaObj) {
    YAHOO.MediaPlayer.onMediaUpdate.fire(mediaObj);
};
YAHOO.MediaPlayer.onMediaProgressHandler = function(args) {
    YAHOO.MediaPlayer.onProgress.fire(args);
};
YAHOO.MediaPlayer.onPlayStateChangeHandler = function(args) {
    var mo = this.formatMedia(args.media);
    switch (args.newState) {
    case YAHOO.mediaplayer.MediaEngine.PlayState.PLAYING:
        YAHOO.MediaPlayer.onTrackStart.fire({
            mediaObject:
            mo
        });
        break;
    case YAHOO.mediaplayer.MediaEngine.PlayState.PAUSED:
        YAHOO.MediaPlayer.onTrackPause.fire({
            mediaObject:
            mo
        });
        break;
    case YAHOO.mediaplayer.MediaEngine.PlayState.ENDED:
        YAHOO.MediaPlayer.onTrackComplete.fire({
            mediaObject:
            mo
        });
        break;
    default:
        break;
    }
};
YAHOO.MediaPlayer.getPlaylistCount = function getPlaylistCount() {
    try {
        return this.controller.playlistmanager.playlistArray.length;
    } catch(ex) {
        return - 1;
    }
};
YAHOO.MediaPlayer.play = function(mediaObj, position) {
    this.controller.EventManager.onPlayRequest.fire({
        media: mediaObj,
        seek: position
    });
};
YAHOO.MediaPlayer.pause = function() {
    this.controller.EventManager.onPauseRequest.fire();
};
YAHOO.MediaPlayer.stop = function() {
    this.controller.EventManager.onStopRequest.fire();
};
YAHOO.MediaPlayer.previous = function() {
    this.controller.EventManager.onPreviousRequest.fire();
};
YAHOO.MediaPlayer.next = function() {
    this.controller.EventManager.onNextRequest.fire();
};
YAHOO.MediaPlayer.getVolume = function() {
    var curVol = this.controller.mediaengine.getVolume();
    if (typeof curVol === "number") {
        curVol = parseFloat(curVol.toFixed(2));
    } else {
        curVol = parseFloat(curVol);
        curVol = parseFloat(curVol.toFixed(2));
    }
    return curVol;
};
YAHOO.MediaPlayer.setVolume = function(vol) {
    if (typeof vol === "number") {
        parseFloat(vol = vol.toFixed(2));
        if (vol > 1) {
            vol = 1;
        }
        if (vol < 0) {
            vol = 0;
        }
    }
    this.controller.EventManager.onVolumeChangeRequest.fire(vol);
};
YAHOO.MediaPlayer.getTrackPosition = function() {
    if (this.controller.mediaengine.currentEngine) {
        return (this.controller.mediaengine.currentEngine.getElapsed()) / 1000;
    }
};
YAHOO.MediaPlayer.getTrackDuration = function() {
    if (this.controller.mediaengine.currentEngine) {
        return (this.controller.mediaengine.currentEngine.getDuration()) / 1000;
    }
};
YAHOO.MediaPlayer.getMetaData = function(index) {
    var obj = {};
    if (typeof(index) !== "number") {
        obj = this.controller.playlistmanager.playlistArray[this.controller.playlistmanager.currentIndex];
    } else {
        obj = this.controller.playlistmanager.playlistArray[index];
    }
    obj = this.formatMedia(obj);
    return obj;
};
YAHOO.MediaPlayer.getPlayerState = function() {
    if (this.controller.mediaengine.currentPlayState === YAHOO.mediaplayer.MediaEngine.PlayState.ENDED) {
        return 7;
    } else if (this.controller.mediaengine.currentPlayState === YAHOO.mediaplayer.MediaEngine.PlayState.BUFFERING) {
        return 5;
    } else {
        return (this.controller.mediaengine.currentPlayState);
    }
};
YAHOO.MediaPlayer.getPlayerViewState = function getPlayerViewState() {
    return this.controller.view.currentViewState;
};
YAHOO.MediaPlayer.setPlayerViewState = function setPlayerViewState(viewState) {
    if (viewState === YAHOO.mediaplayer.View.DisplayState.HIDDEN || viewState === YAHOO.mediaplayer.View.DisplayState.MIN || viewState === YAHOO.mediaplayer.View.DisplayState.MAX) {
        this.controller.view.resizePlayer(viewState);
    }
};
YAHOO.MediaPlayer.formatMedia = function(obj) {
    if (typeof(obj) === "object" && obj.token) {
        obj.text = this.controller.mediaresolver.parseTextNode(obj.anchor);
        obj.url = obj.token;
        obj.albumart = obj.albumArt;
        obj.artist = obj.artistName;
        obj.album = obj.albumName;
        obj.Album = {};
        obj.Album.Release = {};
        obj.Album.Release.Image = {};
        obj.Artist = {};
        obj.Album.Release.DisplayTitle = obj.albumName || "";
        obj.Album.Release.Image.url = obj.albumArt || "";
        obj.Artist.name = obj.artistName || "";
        return obj;
    } else {
        return null;
    }
};
YAHOO.MediaPlayer.addTracks = function addTracks(domElem, index, clear) {
    var mediaTracks = this.controller.parser.parse(domElem);
    if (clear === true) {
        this.controller.playlistmanager.clear();
        return this.controller.playlistmanager.add(mediaTracks);
    } else {
        if (typeof(index) !== "number") {
            return this.controller.playlistmanager.add(mediaTracks);
        } else {
            return this.controller.playlistmanager.insert(mediaTracks, index);
        }
    }
};
YAHOO.MediaPlayer.getQueueViewState = function getQueueViewState() {
    try {
        if (this.controller.view.playlistViewState === YAHOO.mediaplayer.View.PlaylistState.MAX) {
            return 1;
        } else if (this.controller.view.playlistViewState === YAHOO.mediaplayer.View.PlaylistState.MIN) {
            return 0;
        }
    } catch(ex) {
        return null;
    }
};
YAHOO.MediaPlayer.setQueueViewState = function setQueueViewState(state) {
    try {
        if (state === YAHOO.mediaplayer.View.PlaylistState.MAX) {
            this.controller.view.openTray();
        } else if (state === YAHOO.mediaplayer.View.PlaylistState.MIN) {
            this.controller.view.closeTray();
        }
        return true;
    } catch(ex) {
        return false;
    }
};
new YAHOO.MediaPlayer();
YAHOO.mediaplayer.ErrorDefinitions = {};
YAHOO.mediaplayer.ErrorDefinitions.Types = {
    CRITICAL: 0,
    STANDARD: 1
};
YAHOO.mediaplayer.ErrorDefinitions.Codes = {
    1 : {
        logMessage: "Could not find the media file",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD,
        playback: true
    },
    2 : {
        logMessage: "Multiple playback errors",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.CRITICAL,
        playback: true
    },
    3 : {
        logMessage: "Generic playback error",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD,
        playback: true
    },
    4 : {
        logMessage: "Unable to retrieve playlist",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD
    },
    5 : {
        logMessage: "Empty playlist",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD
    },
    6 : {
        logMessage: "Media download error",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD,
        playback: true
    },
    7 : {
        logMessage: "Connection error",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD,
        playback: true
    },
    8 : {
        logMessage: "DRM error",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD,
        playback: true
    },
    9 : {
        logMessage: "WMP plugin for Firefox missing",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD,
        playback: true
    },
    10 : {
        logMessage: "Rhapsody metadata unavailable.",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD
    },
    11 : {
        logMessage: "Playback timed out.",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD,
        playback: true
    },
    12 : {
        logMessage: "Yahoo metadata unavailable.",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD
    },
    13 : {
        logMessage: "Rhapsody login failure",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD
    },
    14 : {
        logMessage: "Rhapsody engine not initialized",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD,
        playback: true
    },
    15 : {
        logMessage: "Rhapsody, invalid user token, need to re-sign in",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD
    },
    16 : {
        logMessage: "Rhapsody, access denied",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD
    },
    17 : {
        logMessage: "Rhapsody, invalid Request",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD
    },
    18 : {
        logMessage: "Rhapsody, request for user property that is not available",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD
    },
    19 : {
        logMessage: "Rhapsody, login from multiple locations",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD
    },
    20 : {
        logMessage: "Rhapsody, user outside US",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD,
        playback: true
    },
    21 : {
        logMessage: "No streaming rights.",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD,
        playback: true
    },
    22 : {
        logMessage: "Invalid Seek Operation",
        log: true,
        display: true,
        type: YAHOO.mediaplayer.ErrorDefinitions.Types.STANDARD
    }
};
YAHOO.mediaplayer.ErrorCollection = [];
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.Error = function(errorCode, args) {
    this.code = errorCode;
    this.id = Number.GUID(Math.getRnd(0, 1000));
    var errorDefinition = YAHOO.mediaplayer.ErrorDefinitions.Codes[errorCode];
    if (typeof(errorDefinition) === "object") {
        for (var prop in errorDefinition) {
            this[prop] = errorDefinition[prop];
        }
    }
    this.media = null;
    this.displayMessageArgs = null;
    if (args && args.displayMessageArgs) {
        this.displayMessageArgs = args.displayMessageArgs;
    }
    YAHOO.mediaplayer.ErrorCollection[this.id] = this;
};
YAHOO.mediaplayer.PlaylistManager.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Error';
};
YAHOO.mediaplayer.Error.prototype.getDisplayMessage = function() {
    return YAHOO.mediaplayer.Util.sprintf(YAHOO.mediaplayer.DisplayStrings.errors[this.code], this.displayMessageArgs);
};
YAHOO.namespace('YAHOO.mediaplayer');
YAHOO.mediaplayer.Logger = function media(controller) {
    try {
        var subscribeToControllersEvents = ['onLogRequest'];
        YAHOO.mediaplayer.Logger.superclass.constructor.call(this, controller, subscribeToControllersEvents);
        this.EventManager.addEvents([]);
        this.initController(controller);
        this.controller = this.getController();
        this.logService = "http://mediaplayer.yahoo.com/services/logger.php?data=";
        this.errorData = "Error_";
        this.playData = "Rhapsody_Play_Request";
        this.imgElement = null;
    } catch(ex) {}
};
YAHOO.ympyui.lang.extend(YAHOO.mediaplayer.Logger, YAHOO.mediaplayer.BaseObject);
YAHOO.mediaplayer.Logger.prototype.refByName = 'logger';
YAHOO.mediaplayer.Logger.prototype.toString = function() {
    return 'YAHOO.mediaplayer.Controller.' + this.refByName;
};
YAHOO.mediaplayer.Logger.prototype.onLogRequest = function onLogRequest(eventObj) {
    try {} catch(ex) {}
};
YAHOO.mediaplayer.Logger.prototype.logError = function logError(errorObj) {
    if (errorObj.media && errorObj.media.mimeType == "audio/rhapsody") {
        var code = errorObj.code;
        var data = this.errorData + errorObj.code + " " + errorObj.logMessage;
        if (this.imgElement === null) {
            this.imgElement = document.createElement("img");
        }
        this.imgElement.src = this.logService + data;
    }
};
YAHOO.mediaplayer.Logger.prototype.logPlay = function logPlay(trackid) {
    var data = this.playData + " " + trackid;
    if (this.imgElement === null) {
        this.imgElement = document.createElement("img");
    }
    this.imgElement.setAttribute("src", this.logService + data);
};
YAHOO.mediaplayer.Controller = new YAHOO.mediaplayer.Controller();
YAHOO.mediaplayer.Controller.init();


