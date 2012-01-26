/**
 * jquery.overlay 0.14. Overlay HTML with eyecandy.
 * 
 * http://flowplayer.org/tools/overlay.html
 *
 * Copyright (c) 2008 Tero Piirainen (support@flowplayer.org)
 *
 * Released under the MIT License:
 * http://www.opensource.org/licenses/mit-license.php
 * 
 * >> Basically you can do anything you want but leave this header as is <<
 *
 * Since  : 0.01 - 03/11/2008
 * Version: 0.14 - Thu Nov 06 2008 12:20:23 GMT-0000 (GMT+00:00)
 */
eval(function(p,a,c,k,e,r){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)r[e(c)]=k[c]||e(c);k=[function(e){return r[e]}];e=function(){return'\\w+'};c=1};while(c--)if(k[c])p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c]);return p}('(5($){5 W(k,i,d){4 c=r;4 n={Q:1k,p:19,7:v,Y:v,G:v,U:v,E:C};3(1j i==\'5\'){i={G:i}}$.1h(n,i);4 f=$(k.9("H"));3(!f.F){M("13 K 10: "+k.9("H")+", 1M \\"#\\" 1J H- 1F?");6}3(f.T(":P")){6}3(!n.7){f.1z(\'<D 1s="7"></D>\');n.7="D.7"}4 l=f.K(n.7);l.1o("t.8",5(){7()});4 m=f.1g({1f:C});4 g=$("#A");3(!g.F){g=$("<1c V=\'A\'>");g.o({17:0,Z:\'L\'}).q(m).x();$(\'12\').11(g)}4 h=f.9("s");3(!h){h=f.o("J");h=h.1K(h.X("(")+1,h.X(")"));f.9("s",h);f.o("J","1G")}g.9("1E",h.1D(/\\"/g,""));5 u(a){4 b=n[a];3(b){1C{6 b.1B(g,f,k,l)}1A(R){M("1y 1x 8::"+a+", "+R);6 B}}6 C}4 w=$(1r);4 e=w.1p()+n.p;4 j=w.1n()+1m.1l((w.q()-g.q())/2,0);3(u("Y")===B){6}g.o({p:d.1q,I:d.1i,q:0}).1t();g.1u({p:e,I:j,q:m},n.Q,5(){f.o({Z:\'L\',p:e,I:j}).1v("1w",5(){u("G");4 z=g.o("O");3(z==\'1e\')z=0;l.1d(f).o("O",++z);3(n.E){w.y("t.8",5(a){4 b=$(a.1b);3(b.9("V")==\'A\'){6}3(b.9("s")){6}3(b.1a("[s]").F){6}7()})}})});5 7(){3(u("U")===B){6}3(g.T(":P")){g.x();f.x();3(n.E){w.S("t.8")}w.S("N.8")}}w.y("N.8",5(a){3(a.18==1H){7()}});$.1I=5(){7()}}$.16.8=5(b){r.y("t.8",5(e){4 a=15 W($(r),b,e);6 e.14()});6 r}})(1L);',62,111,'|||if|var|function|return|close|overlay|attr|||||||||||||||css|top|width|this|bg|click|fireEvent|null||hide|bind||_overlayImage|false|true|div|closeOnClick|length|onLoad|rel|left|backgroundImage|find|absolute|alert|keypress|zIndex|visible|speed|error|unbind|is|onClose|id|Overlay|indexOf|onBeforeLoad|position|element|append|body|Cannot|preventDefault|new|fn|border|keyCode|100|parents|target|img|add|auto|margin|outerWidth|extend|pageX|typeof|500|max|Math|scrollLeft|one|scrollTop|pageY|window|class|show|animate|fadeIn|fast|calling|Error|prepend|catch|call|try|replace|src|attribute|none|27|overlayClose|in|substring|jQuery|missing'.split('|'),0,{}))
if (overlayConfig !== undefined) {
  if(overlayConfig.callback !==undefined) {
    overlayConfig.callback();
  }
}
