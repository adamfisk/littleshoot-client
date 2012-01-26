/** 
 * flowplayer.js 3.0.1. The Flowplayer API
 * 
 * Copyright 2008 Flowplayer Oy
 * 
 * This file is part of Flowplayer.
 * 
 * Flowplayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Flowplayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Flowplayer.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Version: 3.0.1 - Fri Dec 05 2008 14:15:23 GMT-0000 (GMT+00:00)
 */
eval(function(p,a,c,k,e,r){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)r[e(c)]=k[c]||e(c);k=[function(e){return r[e]}];e=function(){return'\\w+'};c=1};while(c--)if(k[c])p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c]);return p}('(8(){8 1V(a){4(y 2E==\'Z\'){2E.5T("$f.2g: "+a.24(" | "))}E 4(y 20==\'Z\'){20.1V("$f.2g",[].2w.O(a))}}8 1b(a){4(!a||y a!=\'Z\'){u a}v b=G a.4I();X(v c 16 a){4(a.1F(c)){b[c]=1b(a[c])}}u b}8 z(a,c){4(!a){u}v d,i=0,B=a.B;4(B===N){X(d 16 a){4(c.O(a[d],d,a[d])===F){5J}}}E{X(v b=a[0];i<B&&c.O(b,i,b)!==F;b=a[++i]){}}u a}8 1P(a){u 18.2f(a)}8 D(c,d,e){4(c&&d){z(d,8(b,a){4(!e||y a!=\'8\'){c[b]=a}})}}8 2Q(a){v c=a.1k(".");4(c!=-1){v d=a.W(0,c)||"*";v e=a.W(c+1,a.B);v b=[];z(18.34(d),8(){4(w.2J&&w.2J.1k(e)!=-1){b.T(w)}});u b}}8 2e(e){e=e||Y.2D;4(e.2C){e.40();e.2C()}E{e.3U=F;e.3P=Q}u F}8 14(a,c,b){a[c]=a[c]||[];a[c].T(b)}8 1m(){u"1B"+(""+1Y.1Z()).W(2,10)}v s=8(j,n,l){v i=w;v o={};v m={};w.5E=n;4(y j==\'U\'){j={1p:j}}D(w,j,Q);z(("5v*,5r,5l*,5i*,5g*,56*,53*,50,4Z,4W,4S,4P").1K(","),8(){v b="2L"+w;4(b.1k("*")!=-1){b=b.W(0,b.B-1);v c="32"+b.W(2);i[c]=8(a){14(m,c,a);u i}}i[b]=8(a){14(m,b,a);u i};4(n==-1){4(i[c]){l[c]=i[c]}4(i[b]){l[b]=i[b]}}});D(w,{19:8(b,c){4(M.B==1){o.4u=[C,b];u i}4(y b==\'2k\'){b=[b]}v a=1m();o[a]=[b,c];4(l.1a()){l.H().2I(b,n,a)}u i},4g:8(a){D(i,a);4(l.1a()){l.H().4f(a,n)}v b=l.2F();v c=(n==-1)?b.c:b.L[n];D(c,a,Q)},K:8(h,e,g,c){4(h==\'1c\'){z(o,8(b,a){4(a[0]){l.H().2I(a[0],n,b)}});u F}4(n!=-1){c=i}4(h==\'19\'){v d=o[e];4(d){u d[1].O(l,c,g)}}4(h==\'3Z\'||h==\'1x\'){D(c,e);4(!c.1y){c.1y=e.2y.1y}E{c.3O=e.2y.1y}}v f=Q;z(m[h],8(){f=w.O(l,c,e)});u f}});4(j.19){v k=j.19;i.19.3H(i,y k==\'8\'?[k]:k);1g j.19}z(j,8(b,a){4(y a==\'8\'){14(m,b,a);1g j[b]}});4(n==-1){l.19=w.19}};v r=8(n,i,k,l){v m={};v h=w;v j=F;4(l){D(m,l)}z(i,8(b,a){4(y a==\'8\'){m[b]=a;1g i[b]}});D(w,{3A:8(b,a,c){4(!b){u h}4(y a==\'8\'){c=a;a=1S}4(y b==\'U\'){v d=b;b={};b[d]=a;a=1S}4(c){v e=1m();m[e]=c}4(a===N){a=1S}i=k.H().3w(n,b,a,e);u h},3v:8(b,a){4(a!==N){v c={};c[b]=a;b=c}i=k.H().3t(n,b);D(h,i);u h},2s:8(){w.1D=\'3q\';k.H().3o(n);u h},2r:8(){w.1D=\'3n\';k.H().3l(n);u h},2p:8(){w.1D=k.H().3k(n);u h},1W:8(o,a,b){4(y a==\'8\'){b=a;a=1S}4(b){v c=1m();m[c]=b}w.1D=k.H().3h(n,o,a,c);w.3f=o;u h},5S:8(b,a){u h.1W(1,b,a)},5Q:8(b,a){u h.1W(0,b,a)},5N:8(){u n},K:8(f,g){4(f==\'1x\'){v e=g||k.H().3b(n);4(!e){u}D(h,e);1g h.3a;4(!j){z(e.3a,8(){v c=""+w;h[c]=8(){v a=[].2w.O(M);v b=k.H().5I(n,c,a);u b==\'N\'?h:b}});j=Q}}v d=m[f];4(d){d.O(h,g);4(f.W(0,1)=="1B"){1g m[f]}}}})};8 1q(o,n,m){v l=w,A=C,1i,1r,L=[],J={},17={},1l,1N,25,2h,2n;D(l,{I:8(){u 1l},1a:8(){u(A!==C)},2Z:8(){u o},2r:8(a){4(a){o.1j.V="2V"}4(A){A.1j.V="2V"}u l},2s:8(){o.1j.V=2n+"2U";4(A){A.1j.V=2h+"2U"}u l},5a:8(){u A&&1f(A.1j.V,10)===0},1z:8(a){4(!A&&l.K("52")!==F){z(q,8(){w.2P()});1i=o.15;P(o,n,{4X:m});4(a){a.2N=Q;14(17,"1c",a)}}u l},2P:8(){4(A&&1i.R(/\\s/g,\'\')!==\'\'&&!A.4R()&&l.K("4Q")!==F){A.4O();o.15=1i;l.K("4M");A=C}u l},4L:8(a){4(a===N){a=25}u L[a]},4J:8(){u 1r},4H:8(){u L},4F:8(a){v b=J[a];4(!b&&l.1a()){v c=l.H().3b(a);4(c){b=G r(a,c,l);J[a]=b}}u b},4E:8(){u l.3e("4B")},4y:8(){u l.3e("1L")},2F:8(a){u a?1b(m):m},4v:8(){u n},4t:8(d,c,a,b){4(y a==\'8\'){b=a;a={}}v e=b?1m():"1B";l.H().4q(d,c,a,e);v f={};f[e]=b;v p=G r(d,C,l,f);J[d]=p;u p},4o:8(){u A?A.4n():-1},1T:8(a){8 1T(){4(a!==N){l.H().3c(a)}E{l.H().3c()}}4(A){1T()}E{l.1z(8(){1T()})}u l},1v:8(){v a="1H.4i 3.0.1";4(A){v b=A.4h();b.T(a);u b}u a},H:8(){4(!A){2H"2G "+l.I()+" 4e 4d. 4c 4b 4a O 49 48\'s 1c 2D";}u A},47:8(){20.1V(17)}});z(("46*,45*,44*,43*,42*,41*,3Y*,3X,3W*,3V,3T").1K(","),8(){v b="2L"+w;4(b.1k("*")!=-1){b=b.W(0,b.B-1);v c="32"+b.W(2);l[c]=8(a){14(17,c,a);u l}}l[b]=8(a){14(17,b,a);u l}});z(("3S,3R,3Q,3N,3M,2p,3L,3I,3G,3F,3E,3D,3C,3B,3z,3y,3x,2v").1K(","),8(){v c=w;l[c]=8(b){4(!A){u l}v a=(b===N)?A["2u"+c]():A["2u"+c](b);u a==\'N\'?l:a}});l.K=8(c,f,g,h){4(m.1A){1V(M)}4(c==\'1c\'&&!A){A=A||1P(1N);2h=A.2t;z(L,8(){w.K("1c")});z(J,8(a,p){p.K("1x")});1r.K("1c")}4(c==\'3u\'){z(m.3J[f],8(b,a){a.O(l)});u}4(c==\'3K\'){v k=f.1e||f;v p=J[k];4(p){4(f.1e){p.K("1x",f)}p.K(g)}u}4(c==\'3s\'){L=[];v e=0;z(f,8(){L.T(G s(w,e++))})}v d=Q;4(f===0||(f&&f>=0)){25=f;v j=L[f];4(j){d=j.K(c,g,h)}4(!j||d!==F){d=1r.K(c,g,h,j)}}v i=0;z(17[c],8(){d=w.O(l,f);4(w.2N){17[c].3r(i,1)}4(d===F){u F}i++});u d};8 1X(){4($f(o)){u C}2n=1f(o.1j.V,10)||o.2t;q.T(l);4(y n==\'U\'){n={12:n}}1l=o.I||"3p"+1m();1N=n.I||1l+"H";n.I=1N;m.1l=1l;4(y m==\'U\'){m={1d:{1p:m}}}m.1d=m.1d||{};1r=G s(m.1d,-1,l);4(o.2x("1C",2)){m.L=[{1p:o.2x("1C",2)}]}m.L=m.L||[m.1d];v d=0;z(m.L,8(){v c=w;4(y c==\'Z\'&&c.B){c=""+c}4(!c.1p&&y c==\'U\'){c={1p:c}}z(m.1d,8(b,a){4(c[b]===N&&y a!=\'8\'){c[b]=a}});m.L[d]=c;c=G s(c,d,l);L.T(c);d++});z(m,8(b,a){4(y a==\'8\'){14(17,b,a);1g m[b]}});z(m.J,8(a,b){4(b){J[a]=G r(a,b,l)}});4(!m.J||m.J.1L===N){J.1L=G r("1L",C,l)}n.2q=n.2q||"#3m";n.11=n.11||[9,0];n.1E=\'2A://2z.1H.3j/2o/3i.2o\';8 21(e){4(!l.1a()&&l.K("3g")!==F){l.1z()}u 2e(e)}1i=o.15;4(1i.R(/\\s/g,\'\')!==\'\'){4(o.1w){o.1w("2B",21,F)}E 4(o.1G){o.1G("5R",21)}}E{4(o.1w){o.1w("2B",2e,F)}l.1z()}}4(y o==\'U\'){P.3d(8(){v a=1P(o);4(!a){2H"2G 5P 5O 5M: "+o;}E{o=a;1X()}})}E{1X()}}v q=[];8 1U(b){w.B=b.B;w.z=8(a){z(b,a)};w.5L=8(){u b.B}}Y.1H=Y.$f=8(){v f=C;v e=M[0];4(!M.B){z(q,8(){4(w.1a()){f=w;u F}});u f||q[0]}4(M.B==1){4(y e==\'2k\'){u q[e]}E{4(e==\'*\'){u G 1U(q)}z(q,8(){4(w.I()==e.I||w.I()==e||w.2Z()==e){f=w;u F}});u f}}4(M.B>1){v c=M[1];v d=(M.B==3)?M[2]:{};4(y e==\'U\'){4(e.1k(".")!=-1){v a=[];z(2Q(e),8(){a.T(G 1q(w,1b(c),1b(d)))});u G 1U(a)}E{v b=1P(e);u G 1q(b!==C?b:e,c,d)}}E 4(e){u G 1q(e,c,d)}}u C};D(Y.$f,{2g:8(a,c,b,d,e){v p=$f(a);u p?p.K(c,b,d,e):C},5K:8(a,b){1q.2m[a]=b;u $f},z:z,D:D});4(18.22){Y.39=8(){$f("*").z(8(){4(w.1a()){w.2v()}})}}4(y 1t==\'8\'){1t.2m.1H=8(a,b){4(!M.B||y M[0]==\'2k\'){v c=[];w.z(8(){v p=$f(w);4(p){c.T(p)}});u M.B?c[M[0]]:G 1U(c)}u w.z(8(){$f(w,1b(a),b?1b(b):{})})}}})();(8(){v l=y 1t==\'8\';8 38(){4(m.23){u F}v d=18;4(d&&d.34&&d.2f&&d.5G){5F(m.1Q);m.1Q=C;X(v i=0;i<m.1u.B;i++){m.1u[i].O()}m.1u=C;m.23=Q}}v m=l?1t:8(f){4(m.23){u f()}4(m.1Q){m.1u.T(f)}E{m.1u=[f];m.1Q=5B(38,13)}};8 D(b,a){4(a){X(1O 16 a){4(a.1F(1O)){b[1O]=a[1O]}}}u b}8 2i(a){v b="";X(v c 16 a){4(a[c]){b+=[c]+\'=\'+1o(a[c])+\'&\'}}u b.W(0,b.B-1)}8 1o(b){5y(35(b)){1M\'U\':b=b.R(G 5x(\'(["\\\\\\\\])\',\'g\'),\'\\\\$1\');b=b.R(/^\\s?(\\d+)%/,"$5w");u\'"\'+b+\'"\';1M\'33\':u\'[\'+2K(b,8(a){u 1o(a)}).24(\',\')+\']\';1M\'8\':u\'"8()"\';1M\'Z\':v c=[];X(v d 16 b){4(b.1F(d)){c.T(\'"\'+d+\'":\'+1o(b[d]))}}u\'{\'+c.24(\',\')+\'}\'}u 5u(b).R(/\\s/g," ").R(/\\\'/g,"\\"")}8 35(a){4(a===C||a===N){u F}v b=y a;u(b==\'Z\'&&a.T)?\'33\':b}4(Y.1G){Y.1G("39",8(){5t=8(){};5q=8(){}})}8 2K(c,a){v b=[];X(v i 16 c){4(c.1F(i)){b[i]=a(c[i])}}u b}8 26(p,c){v a=\'<5p 31="2Y/x-2X-27" \';4(p.I){D(p,{1e:p.I})}X(v b 16 p){4(p[b]!==C){a+=b+\'="\'+p[b]+\'"\\n\\t\'}}4(c){a+=\'2W=\\\'\'+2i(c)+\'\\\'\'}a+=\'/>\';u a}8 2c(p,c,b){v a=\'<Z 5h="5c:5b-59-58-55-54" \';a+=\'1J="\'+p.1J+\'" V="\'+p.V+\'"\';4(!p.I&&18.22){p.I="1B"+(""+1Y.1Z()).W(5)}4(p.I){a+=\' I="\'+p.I+\'"\'}a+=\'>\';4(18.22){p.12+=((p.12.1k("?")!=-1?"&":"?")+1Y.1Z())}a+=\'\\n\\t<2b 1e="51" 2a="\'+p.12+\'" />\';v e=D({},p);e.I=e.1J=e.V=e.12=C;X(v k 16 e){4(e[k]!==C){a+=\'\\n\\t<2b 1e="\'+k+\'" 2a="\'+e[k]+\'" />\'}}4(c){a+=\'\\n\\t<2b 1e="2W" 2a=\\\'\'+2i(c)+\'\\\' />\'}4(b){a+=26(p,c)}a+="</Z>";u a}8 29(p,c){u 2c(p,c,Q)}8 1s(p,c){v a=1h.J&&1h.2O&&1h.2O.B;u(a)?26(p,c):2c(p,c)}Y.P=8(b,a,g){v f={12:\'#\',1J:\'2S%\',V:\'2S%\',11:C,1I:C,1E:C,1A:F,4Y:Q,57:\'2R\',4V:\'4U\',31:\'2Y/x-2X-27\',2M:\'2A://2z.4T.5d/5e/5f\'};4(y a==\'U\'){a={12:a}}D(f,a);v d=P.1v();v c=f.11;v i=f.1E;v j=f.1A;4(y b==\'U\'){v h=18.2f(b);4(h){b=h}E{m(8(){P(b,a,g)});u}}4(!b){u}4(!c||P.28(c)){f.1I=f.11=f.1E=f.1A=C;b.15=1s(f,g);u b.4N}E 4(f.1I){v e=f.1I.O(f,P.1v(),g);4(e===Q){b.15=e}}E 4(c&&i&&P.28([6,5j])){D(f,{12:i});g={5k:4K.1C,5m:\'5n\',5o:18.4G};b.15=1s(f,g)}E{4(b.15.R(/\\s/g,\'\')!==\'\'){}E{b.15="<2T>2d 11 "+c+" 5s 4D 30 4C</2T>"+"<37>"+(d[0]>0?"4A 11 30 "+d:"4z 5z 5A 27 4x 4w")+"</37>"+"<p>5C 5D 11 4s <a 1C=\'"+f.2M+"\'>4r</a></p>"}}u b};D(Y.P,{1v:8(){v d=[0,0];4(1h.J&&y 1h.J["36 2d"]=="Z"){v f=1h.J["36 2d"].5H;4(y f!="N"){f=f.R(/^.*\\s+(\\S+\\s+\\S+$)/,"$1");v c=1f(f.R(/^(.*)\\..*$/,"$1"),10);v a=/r/.4p(f)?1f(f.R(/^.*r(.*)$/,"$1"),10):0;d=[c,a]}}E 4(Y.1R){2j{v b=G 1R("1n.1n.7")}2l(e){2j{b=G 1R("1n.1n.6");d=[6,0];b.4m="2R"}2l(4l){4(d[0]==6){u}}2j{b=G 1R("1n.1n")}2l(4k){}}4(y b=="Z"){f=b.4j("$11");4(y f!="N"){f=f.R(/^\\S+\\s+(.*)$/,"$1").1K(",");d=[1f(f[0],10),1f(f[2],10)]}}}u d},28:8(c){v a=P.1v();v b=(a[0]>c[0])||(a[0]==c[0]&&a[1]>=c[1]);u b},3d:m,1o:1o,1s:1s,29:29});4(l){1t.2m.P=8(b,a){u w.z(8(){P(w,b,a)})}}})();',62,366,'||||if||||function||||||||||||||||||||||return|var|this||typeof|each|api|length|null|extend|else|false|new|_api|id|plugins|_fireEvent|playlist|arguments|undefined|call|flashembed|true|replace||push|string|height|substring|for|window|object||version|src||bind|innerHTML|in|listeners|document|onCuepoint|isLoaded|clone|onLoad|clip|name|parseInt|delete|navigator|html|style|indexOf|playerId|makeId|ShockwaveFlash|asString|url|Player|commonClip|getHTML|jQuery|ready|getVersion|addEventListener|onUpdate|duration|load|debug|_|href|display|expressInstall|hasOwnProperty|attachEvent|flowplayer|onFail|width|split|controls|case|apiId|key|el|timer|ActiveXObject|500|play|Iterator|log|fadeTo|init|Math|random|console|doClick|all|done|join|activeIndex|getEmbedCode|flash|isSupported|getFullHTML|value|param|getObjectCode|Flash|stopEvent|getElementById|fireEvent|swfHeight|concatVars|try|number|catch|prototype|wrapperHeight|swf|toggle|bgcolor|hide|show|clientHeight|fp_|close|slice|getAttribute|metaData|www|http|click|preventDefault|event|opera|getConfig|Flowplayer|throw|fp_addCuepoints|className|map|on|pluginspage|cached|mimeTypes|unload|select|always|100|h2|px|0px|flashvars|shockwave|application|getParent|is|type|onBefore|array|getElementsByTagName|typeOf|Shockwave|h3|isDomReady|onbeforeunload|methods|fp_getPlugin|fp_play|domReady|getPlugin|opacity|onBeforeClick|fp_fadeTo|expressinstall|org|fp_togglePlugin|fp_hidePlugin|000000|none|fp_showPlugin|fp|block|splice|onPlaylistReplace|fp_css|onContextMenu|css|fp_animate|reset|isFullscreen|stopBuffering|animate|startBuffering|isPlaying|isPaused|getTime|setVolume|getVolume|apply|getStatus|contextMenu|onPluginEvent|seek|stop|unmute|fullDuration|cancelBubble|mute|resume|pause|Error|returnValue|FullscreenExit|Fullscreen|PlaylistReplace|Unmute|onStart|stopPropagation|Mute|Volume|Keypress|Unload|Load|Click|_dump|player|to|your|moving|Try|loaded|not|fp_updateClip|update|fp_getVersion|js|GetVariable|eee|ee|AllowScriptAccess|fp_getState|getState|test|fp_loadPlugin|here|from|loadr|embedded|getFlashParams|installed|plugin|getControls|You|Your|screen|required|greater|getScreen|getr|title|getPlaylist|constructor|getCommons|location|gets|onUnload|firstChild|fp_close|BufferStop|onBeforeUnload|fp_isFullscreen|BufferEmpty|adobe|high|quality|BufferFull|config|allowfullscreen|Update|LastSecond|movie|onBeforeLoad|Finish|444553540000|96B8|Stop|allowscriptaccess|11cf|AE6D|isHidden|D27CDB6E|clsid|com|go|getflashplayer|Seek|classid|Resume|65|MMredirectURL|Pause|MMplayerType|PlugIn|MMdoctitle|embed|__flash_savedUnloadHandler|Start|or|__flash_unloadHandler|String|Begin|1pct|RegExp|switch|have|no|setInterval|Download|latest|index|clearInterval|body|description|fp_invoke|break|addr|size|element|getName|access|cannot|fadeOut|onclick|fadeIn|postError'.split('|'),0,{}))
if (flowPlayerConfig && flowPlayerConfig.callback) {flowPlayerConfig.callback();}
