/* Yahoo! Media Player Loader, Minified Build 2.0.31.  Copyright (c) 2008, Yahoo! Inc.  All rights reserved.
 * Your use of this Yahoo! Media Player is subject to the Yahoo! Terms of Service
 * located at http://info.yahoo.com/legal/us/yahoo/utos/utos-173.html.
 */

function yui_Namespace() {
    var a = arguments,
    o = null,
    i, j, d;
    for (i = 0; i < a.length; ++i) {
        d = a[i].split(".");
        o = YAHOO;
        for (j = (d[0] == "YAHOO") ? 1 : 0; j < d.length; ++j) {
            o[d[j]] = o[d[j]] || {};
            o = o[d[j]];
        }
    }
    return o;
}
if (typeof YAHOO == "undefined") {
    var YAHOO = {}
};
if (typeof YAHOO.namespace == "undefined") YAHOO.namespace = yui_Namespace;
YAHOO.ympyui = (function() {
    var YAHOO = {};
    if (typeof YAHOO == "undefined" || !YAHOO) {
        var YAHOO = {};
    }
    YAHOO.namespace = function() {
        var A = arguments,
        E = null,
        C, B, D;
        for (C = 0; C < A.length; C = C + 1) {
            D = A[C].split(".");
            E = YAHOO;
            for (B = (D[0] == "YAHOO") ? 1 : 0; B < D.length; B = B + 1) {
                E[D[B]] = E[D[B]] || {};
                E = E[D[B]];
            }
        }
        return E;
    };
    YAHOO.log = function(D, A, C) {
        var B = YAHOO.widget.Logger;
        if (B && B.log) {
            return B.log(D, A, C);
        } else {
            return false;
        }
    };
    YAHOO.register = function(A, E, D) {
        var I = YAHOO.env.modules;
        if (!I[A]) {
            I[A] = {
                versions: [],
                builds: []
            };
        }
        var B = I[A],
        H = D.version,
        G = D.build,
        F = YAHOO.env.listeners;
        B.name = A;
        B.version = H;
        B.build = G;
        B.versions.push(H);
        B.builds.push(G);
        B.mainClass = E;
        for (var C = 0; C < F.length; C = C + 1) {
            F[C](B);
        }
        if (E) {
            E.VERSION = H;
            E.BUILD = G;
        } else {
            YAHOO.log("mainClass is undefined for module " + A, "warn");
        }
    };
    YAHOO.env = YAHOO.env || {
        modules: [],
        listeners: []
    };
    YAHOO.env.getVersion = function(A) {
        return YAHOO.env.modules[A] || null;
    };
    YAHOO.env.ua = function() {
        var C = {
            ie: 0,
            opera: 0,
            gecko: 0,
            webkit: 0,
            mobile: null,
            air: 0
        };
        var B = navigator.userAgent,
        A;
        if ((/KHTML/).test(B)) {
            C.webkit = 1;
        }
        A = B.match(/AppleWebKit\/([^\s]*)/);
        if (A && A[1]) {
            C.webkit = parseFloat(A[1]);
            if (/ Mobile\//.test(B)) {
                C.mobile = "Apple";
            } else {
                A = B.match(/NokiaN[^\/]*/);
                if (A) {
                    C.mobile = A[0];
                }
            }
            A = B.match(/AdobeAIR\/([^\s]*)/);
            if (A) {
                C.air = A[0];
            }
        }
        if (!C.webkit) {
            A = B.match(/Opera[\s\/]([^\s]*)/);
            if (A && A[1]) {
                C.opera = parseFloat(A[1]);
                A = B.match(/Opera Mini[^;]*/);
                if (A) {
                    C.mobile = A[0];
                }
            } else {
                A = B.match(/MSIE\s([^;]*)/);
                if (A && A[1]) {
                    C.ie = parseFloat(A[1]);
                } else {
                    A = B.match(/Gecko\/([^\s]*)/);
                    if (A) {
                        C.gecko = 1;
                        A = B.match(/rv:([^\s\)]*)/);
                        if (A && A[1]) {
                            C.gecko = parseFloat(A[1]);
                        }
                    }
                }
            }
        }
        return C;
    } (); (function() {
        YAHOO.namespace("util", "widget", "example");
        if ("undefined" !== typeof YAHOO_config) {
            var B = YAHOO_config.listener,
            A = YAHOO.env.listeners,
            D = true,
            C;
            if (B) {
                for (C = 0; C < A.length; C = C + 1) {
                    if (A[C] == B) {
                        D = false;
                        break;
                    }
                }
                if (D) {
                    A.push(B);
                }
            }
        }
    })();
    YAHOO.lang = YAHOO.lang || {
        isArray: function(B) {
            if (B) {
                var A = YAHOO.lang;
                return A.isNumber(B.length) && A.isFunction(B.splice);
            }
            return false;
        },
        isBoolean: function(A) {
            return typeof A === "boolean";
        },
        isFunction: function(A) {
            return typeof A === "function";
        },
        isNull: function(A) {
            return A === null;
        },
        isNumber: function(A) {
            return typeof A === "number" && isFinite(A);
        },
        isObject: function(A) {
            return (A && (typeof A === "object" || YAHOO.lang.isFunction(A))) || false;
        },
        isString: function(A) {
            return typeof A === "string";
        },
        isUndefined: function(A) {
            return typeof A === "undefined";
        },
        hasOwnProperty: function(A, B) {
            if (Object.prototype.hasOwnProperty) {
                return A.hasOwnProperty(B);
            }
            return ! YAHOO.lang.isUndefined(A[B]) && A.constructor.prototype[B] !== A[B];
        },
        _IEEnumFix: function(C, B) {
            if (YAHOO.env.ua.ie) {
                var E = ["toString", "valueOf"],
                A;
                for (A = 0; A < E.length; A = A + 1) {
                    var F = E[A],
                    D = B[F];
                    if (YAHOO.lang.isFunction(D) && D != Object.prototype[F]) {
                        C[F] = D;
                    }
                }
            }
        },
        extend: function(D, E, C) {
            if (!E || !D) {
                throw new Error("YAHOO.lang.extend failed, please check that " + "all dependencies are included.");
            }
            var B = function() {};
            B.prototype = E.prototype;
            D.prototype = new B();
            D.prototype.constructor = D;
            D.superclass = E.prototype;
            if (E.prototype.constructor == Object.prototype.constructor) {
                E.prototype.constructor = E;
            }
            if (C) {
                for (var A in C) {
                    D.prototype[A] = C[A];
                }
                YAHOO.lang._IEEnumFix(D.prototype, C);
            }
        },
        augmentObject: function(E, D) {
            if (!D || !E) {
                throw new Error("Absorb failed, verify dependencies.");
            }
            var A = arguments,
            C, F, B = A[2];
            if (B && B !== true) {
                for (C = 2; C < A.length; C = C + 1) {
                    E[A[C]] = D[A[C]];
                }
            } else {
                for (F in D) {
                    if (B || !E[F]) {
                        E[F] = D[F];
                    }
                }
                YAHOO.lang._IEEnumFix(E, D);
            }
        },
        augmentProto: function(D, C) {
            if (!C || !D) {
                throw new Error("Augment failed, verify dependencies.");
            }
            var A = [D.prototype, C.prototype];
            for (var B = 2; B < arguments.length; B = B + 1) {
                A.push(arguments[B]);
            }
            YAHOO.lang.augmentObject.apply(this, A);
        },
        dump: function(A, G) {
            var C = YAHOO.lang,
            D, F, I = [],
            J = "{...}",
            B = "f(){...}",
            H = ", ",
            E = " => ";
            if (!C.isObject(A)) {
                return A + "";
            } else {
                if (A instanceof Date || ("nodeType" in A && "tagName" in A)) {
                    return A;
                } else {
                    if (C.isFunction(A)) {
                        return B;
                    }
                }
            }
            G = (C.isNumber(G)) ? G: 3;
            if (C.isArray(A)) {
                I.push("[");
                for (D = 0, F = A.length; D < F; D = D + 1) {
                    if (C.isObject(A[D])) {
                        I.push((G > 0) ? C.dump(A[D], G - 1) : J);
                    } else {
                        I.push(A[D]);
                    }
                    I.push(H);
                }
                if (I.length > 1) {
                    I.pop();
                }
                I.push("]");
            } else {
                I.push("{");
                for (D in A) {
                    if (C.hasOwnProperty(A, D)) {
                        I.push(D + E);
                        if (C.isObject(A[D])) {
                            I.push((G > 0) ? C.dump(A[D], G - 1) : J);
                        } else {
                            I.push(A[D]);
                        }
                        I.push(H);
                    }
                }
                if (I.length > 1) {
                    I.pop();
                }
                I.push("}");
            }
            return I.join("");
        },
        substitute: function(Q, B, J) {
            var G, F, E, M, N, P, D = YAHOO.lang,
            L = [],
            C,
            H = "dump",
            K = " ",
            A = "{",
            O = "}";
            for (;;) {
                G = Q.lastIndexOf(A);
                if (G < 0) {
                    break;
                }
                F = Q.indexOf(O, G);
                if (G + 1 >= F) {
                    break;
                }
                C = Q.substring(G + 1, F);
                M = C;
                P = null;
                E = M.indexOf(K);
                if (E > -1) {
                    P = M.substring(E + 1);
                    M = M.substring(0, E);
                }
                N = B[M];
                if (J) {
                    N = J(M, N, P);
                }
                if (D.isObject(N)) {
                    if (D.isArray(N)) {
                        N = D.dump(N, parseInt(P, 10));
                    } else {
                        P = P || "";
                        var I = P.indexOf(H);
                        if (I > -1) {
                            P = P.substring(4);
                        }
                        if (N.toString === Object.prototype.toString || I > -1) {
                            N = D.dump(N, parseInt(P, 10));
                        } else {
                            N = N.toString();
                        }
                    }
                } else {
                    if (!D.isString(N) && !D.isNumber(N)) {
                        N = "~-" + L.length + "-~";
                        L[L.length] = C;
                    }
                }
                Q = Q.substring(0, G) + N + Q.substring(F + 1);
            }
            for (G = L.length - 1; G >= 0; G = G - 1) {
                Q = Q.replace(new RegExp("~-" + G + "-~"), "{" + L[G] + "}", "g");
            }
            return Q;
        },
        trim: function(A) {
            try {
                return A.replace(/^\s+|\s+$/g, "");
            } catch(B) {
                return A;
            }
        },
        merge: function() {
            var D = {},
            B = arguments;
            for (var C = 0,
            A = B.length; C < A; C = C + 1) {
                YAHOO.lang.augmentObject(D, B[C], true);
            }
            return D;
        },
        later: function(H, B, I, D, E) {
            H = H || 0;
            B = B || {};
            var C = I,
            G = D,
            F, A;
            if (YAHOO.lang.isString(I)) {
                C = B[I];
            }
            if (!C) {
                throw new TypeError("method undefined");
            }
            if (!YAHOO.lang.isArray(G)) {
                G = [D];
            }
            F = function() {
                C.apply(B, G);
            };
            A = (E) ? setInterval(F, H) : setTimeout(F, H);
            return {
                interval: E,
                cancel: function() {
                    if (this.interval) {
                        clearInterval(A);
                    } else {
                        clearTimeout(A);
                    }
                }
            };
        },
        isValue: function(B) {
            var A = YAHOO.lang;
            return (A.isObject(B) || A.isString(B) || A.isNumber(B) || A.isBoolean(B));
        }
    };
    YAHOO.util.Lang = YAHOO.lang;
    YAHOO.lang.augment = YAHOO.lang.augmentProto;
    YAHOO.augment = YAHOO.lang.augmentProto;
    YAHOO.extend = YAHOO.lang.extend;
    YAHOO.register("yahoo", YAHOO, {
        version: "2.5.1",
        build: "984"
    }); (function() {
        var B = YAHOO.util,
        K, I, J = {},
        F = {},
        M = window.document;
        YAHOO.env._id_counter = YAHOO.env._id_counter || 0;
        var C = YAHOO.env.ua.opera,
        L = YAHOO.env.ua.webkit,
        A = YAHOO.env.ua.gecko,
        G = YAHOO.env.ua.ie;
        var E = {
            HYPHEN: /(-[a-z])/i,
            ROOT_TAG: /^body|html$/i,
            OP_SCROLL: /^(?:inline|table-row)$/i
        };
        var N = function(P) {
            if (!E.HYPHEN.test(P)) {
                return P;
            }
            if (J[P]) {
                return J[P];
            }
            var Q = P;
            while (E.HYPHEN.exec(Q)) {
                Q = Q.replace(RegExp.$1, RegExp.$1.substr(1).toUpperCase());
            }
            J[P] = Q;
            return Q;
        };
        var O = function(Q) {
            var P = F[Q];
            if (!P) {
                P = new RegExp("(?:^|\\s+)" + Q + "(?:\\s+|$)");
                F[Q] = P;
            }
            return P;
        };
        if (M.defaultView && M.defaultView.getComputedStyle) {
            K = function(P, S) {
                var R = null;
                if (S == "float") {
                    S = "cssFloat";
                }
                var Q = P.ownerDocument.defaultView.getComputedStyle(P, "");
                if (Q) {
                    R = Q[N(S)];
                }
                return P.style[S] || R;
            };
        } else {
            if (M.documentElement.currentStyle && G) {
                K = function(P, R) {
                    switch (N(R)) {
                    case "opacity":
                        var T = 100;
                        try {
                            T = P.filters["DXImageTransform.Microsoft.Alpha"].opacity;
                        } catch(S) {
                            try {
                                T = P.filters("alpha").opacity;
                            } catch(S) {}
                        }
                        return T / 100;
                    case "float":
                        R = "styleFloat";
                    default:
                        var Q = P.currentStyle ? P.currentStyle[R] : null;
                        return (P.style[R] || Q);
                    }
                };
            } else {
                K = function(P, Q) {
                    return P.style[Q];
                };
            }
        }
        if (G) {
            I = function(P, Q, R) {
                switch (Q) {
                case "opacity":
                    if (YAHOO.lang.isString(P.style.filter)) {
                        P.style.filter = "alpha(opacity=" + R * 100 + ")";
                        if (!P.currentStyle || !P.currentStyle.hasLayout) {
                            P.style.zoom = 1;
                        }
                    }
                    break;
                case "float":
                    Q = "styleFloat";
                default:
                    P.style[Q] = R;
                }
            };
        } else {
            I = function(P, Q, R) {
                if (Q == "float") {
                    Q = "cssFloat";
                }
                P.style[Q] = R;
            };
        }
        var D = function(P, Q) {
            return P && P.nodeType == 1 && (!Q || Q(P));
        };
        YAHOO.util.Dom = {
            get: function(R) {
                if (R && (R.nodeType || R.item)) {
                    return R;
                }
                if (YAHOO.lang.isString(R) || !R) {
                    return M.getElementById(R);
                }
                if (R.length !== undefined) {
                    var S = [];
                    for (var Q = 0,
                    P = R.length; Q < P; ++Q) {
                        S[S.length] = B.Dom.get(R[Q]);
                    }
                    return S;
                }
                return R;
            },
            getStyle: function(P, R) {
                R = N(R);
                var Q = function(S) {
                    return K(S, R);
                };
                return B.Dom.batch(P, Q, B.Dom, true);
            },
            setStyle: function(P, R, S) {
                R = N(R);
                var Q = function(T) {
                    I(T, R, S);
                };
                B.Dom.batch(P, Q, B.Dom, true);
            },
            getXY: function(P) {
                var Q = function(R) {
                    if ((R.parentNode === null || R.offsetParent === null || this.getStyle(R, "display") == "none") && R != R.ownerDocument.body) {
                        return false;
                    }
                    return H(R);
                };
                return B.Dom.batch(P, Q, B.Dom, true);
            },
            getX: function(P) {
                var Q = function(R) {
                    return B.Dom.getXY(R)[0];
                };
                return B.Dom.batch(P, Q, B.Dom, true);
            },
            getY: function(P) {
                var Q = function(R) {
                    return B.Dom.getXY(R)[1];
                };
                return B.Dom.batch(P, Q, B.Dom, true);
            },
            setXY: function(P, S, R) {
                var Q = function(V) {
                    var U = this.getStyle(V, "position");
                    if (U == "static") {
                        this.setStyle(V, "position", "relative");
                        U = "relative";
                    }
                    var X = this.getXY(V);
                    if (X === false) {
                        return false;
                    }
                    var W = [parseInt(this.getStyle(V, "left"), 10), parseInt(this.getStyle(V, "top"), 10)];
                    if (isNaN(W[0])) {
                        W[0] = (U == "relative") ? 0 : V.offsetLeft;
                    }
                    if (isNaN(W[1])) {
                        W[1] = (U == "relative") ? 0 : V.offsetTop;
                    }
                    if (S[0] !== null) {
                        V.style.left = S[0] - X[0] + W[0] + "px";
                    }
                    if (S[1] !== null) {
                        V.style.top = S[1] - X[1] + W[1] + "px";
                    }
                    if (!R) {
                        var T = this.getXY(V);
                        if ((S[0] !== null && T[0] != S[0]) || (S[1] !== null && T[1] != S[1])) {
                            this.setXY(V, S, true);
                        }
                    }
                };
                B.Dom.batch(P, Q, B.Dom, true);
            },
            setX: function(Q, P) {
                B.Dom.setXY(Q, [P, null]);
            },
            setY: function(P, Q) {
                B.Dom.setXY(P, [null, Q]);
            },
            getRegion: function(P) {
                var Q = function(R) {
                    if ((R.parentNode === null || R.offsetParent === null || this.getStyle(R, "display") == "none") && R != R.ownerDocument.body) {
                        return false;
                    }
                    var S = B.Region.getRegion(R);
                    return S;
                };
                return B.Dom.batch(P, Q, B.Dom, true);
            },
            getClientWidth: function() {
                return B.Dom.getViewportWidth();
            },
            getClientHeight: function() {
                return B.Dom.getViewportHeight();
            },
            getElementsByClassName: function(T, X, U, V) {
                X = X || "*";
                U = (U) ? B.Dom.get(U) : null || M;
                if (!U) {
                    return [];
                }
                var Q = [],
                P = U.getElementsByTagName(X),
                W = O(T);
                for (var R = 0,
                S = P.length; R < S; ++R) {
                    if (W.test(P[R].className)) {
                        Q[Q.length] = P[R];
                        if (V) {
                            V.call(P[R], P[R]);
                        }
                    }
                }
                return Q;
            },
            hasClass: function(R, Q) {
                var P = O(Q);
                var S = function(T) {
                    return P.test(T.className);
                };
                return B.Dom.batch(R, S, B.Dom, true);
            },
            addClass: function(Q, P) {
                var R = function(S) {
                    if (this.hasClass(S, P)) {
                        return false;
                    }
                    S.className = YAHOO.lang.trim([S.className, P].join(" "));
                    return true;
                };
                return B.Dom.batch(Q, R, B.Dom, true);
            },
            removeClass: function(R, Q) {
                var P = O(Q);
                var S = function(T) {
                    if (!Q || !this.hasClass(T, Q)) {
                        return false;
                    }
                    var U = T.className;
                    T.className = U.replace(P, " ");
                    if (this.hasClass(T, Q)) {
                        this.removeClass(T, Q);
                    }
                    T.className = YAHOO.lang.trim(T.className);
                    return true;
                };
                return B.Dom.batch(R, S, B.Dom, true);
            },
            replaceClass: function(S, Q, P) {
                if (!P || Q === P) {
                    return false;
                }
                var R = O(Q);
                var T = function(U) {
                    if (!this.hasClass(U, Q)) {
                        this.addClass(U, P);
                        return true;
                    }
                    U.className = U.className.replace(R, " " + P + " ");
                    if (this.hasClass(U, Q)) {
                        this.replaceClass(U, Q, P);
                    }
                    U.className = YAHOO.lang.trim(U.className);
                    return true;
                };
                return B.Dom.batch(S, T, B.Dom, true);
            },
            generateId: function(P, R) {
                R = R || "yui-gen";
                var Q = function(S) {
                    if (S && S.id) {
                        return S.id;
                    }
                    var T = R + YAHOO.env._id_counter++;
                    if (S) {
                        S.id = T;
                    }
                    return T;
                };
                return B.Dom.batch(P, Q, B.Dom, true) || Q.apply(B.Dom, arguments);
            },
            isAncestor: function(P, Q) {
                P = B.Dom.get(P);
                Q = B.Dom.get(Q);
                if (!P || !Q) {
                    return false;
                }
                if (P.contains && Q.nodeType && !L) {
                    return P.contains(Q);
                } else {
                    if (P.compareDocumentPosition && Q.nodeType) {
                        return !! (P.compareDocumentPosition(Q) & 16);
                    } else {
                        if (Q.nodeType) {
                            return !! this.getAncestorBy(Q,
                            function(R) {
                                return R == P;
                            });
                        }
                    }
                }
                return false;
            },
            inDocument: function(P) {
                return this.isAncestor(M.documentElement, P);
            },
            getElementsBy: function(W, Q, R, T) {
                Q = Q || "*";
                R = (R) ? B.Dom.get(R) : null || M;
                if (!R) {
                    return [];
                }
                var S = [],
                V = R.getElementsByTagName(Q);
                for (var U = 0,
                P = V.length; U < P; ++U) {
                    if (W(V[U])) {
                        S[S.length] = V[U];
                        if (T) {
                            T(V[U]);
                        }
                    }
                }
                return S;
            },
            batch: function(T, W, V, R) {
                T = (T && (T.tagName || T.item)) ? T: B.Dom.get(T);
                if (!T || !W) {
                    return false;
                }
                var S = (R) ? V: window;
                if (T.tagName || T.length === undefined) {
                    return W.call(S, T, V);
                }
                var U = [];
                for (var Q = 0,
                P = T.length; Q < P; ++Q) {
                    U[U.length] = W.call(S, T[Q], V);
                }
                return U;
            },
            getDocumentHeight: function() {
                var Q = (M.compatMode != "CSS1Compat") ? M.body.scrollHeight: M.documentElement.scrollHeight;
                var P = Math.max(Q, B.Dom.getViewportHeight());
                return P;
            },
            getDocumentWidth: function() {
                var Q = (M.compatMode != "CSS1Compat") ? M.body.scrollWidth: M.documentElement.scrollWidth;
                var P = Math.max(Q, B.Dom.getViewportWidth());
                return P;
            },
            getViewportHeight: function() {
                var P = self.innerHeight;
                var Q = M.compatMode;
                if ((Q || G) && !C) {
                    P = (Q == "CSS1Compat") ? M.documentElement.clientHeight: M.body.clientHeight;
                }
                return P;
            },
            getViewportWidth: function() {
                var P = self.innerWidth;
                var Q = M.compatMode;
                if (Q || G) {
                    P = (Q == "CSS1Compat") ? M.documentElement.clientWidth: M.body.clientWidth;
                }
                return P;
            },
            getAncestorBy: function(P, Q) {
                while (P = P.parentNode) {
                    if (D(P, Q)) {
                        return P;
                    }
                }
                return null;
            },
            getAncestorByClassName: function(Q, P) {
                Q = B.Dom.get(Q);
                if (!Q) {
                    return null;
                }
                var R = function(S) {
                    return B.Dom.hasClass(S, P);
                };
                return B.Dom.getAncestorBy(Q, R);
            },
            getAncestorByTagName: function(Q, P) {
                Q = B.Dom.get(Q);
                if (!Q) {
                    return null;
                }
                var R = function(S) {
                    return S.tagName && S.tagName.toUpperCase() == P.toUpperCase();
                };
                return B.Dom.getAncestorBy(Q, R);
            },
            getPreviousSiblingBy: function(P, Q) {
                while (P) {
                    P = P.previousSibling;
                    if (D(P, Q)) {
                        return P;
                    }
                }
                return null;
            },
            getPreviousSibling: function(P) {
                P = B.Dom.get(P);
                if (!P) {
                    return null;
                }
                return B.Dom.getPreviousSiblingBy(P);
            },
            getNextSiblingBy: function(P, Q) {
                while (P) {
                    P = P.nextSibling;
                    if (D(P, Q)) {
                        return P;
                    }
                }
                return null;
            },
            getNextSibling: function(P) {
                P = B.Dom.get(P);
                if (!P) {
                    return null;
                }
                return B.Dom.getNextSiblingBy(P);
            },
            getFirstChildBy: function(P, R) {
                var Q = (D(P.firstChild, R)) ? P.firstChild: null;
                return Q || B.Dom.getNextSiblingBy(P.firstChild, R);
            },
            getFirstChild: function(P, Q) {
                P = B.Dom.get(P);
                if (!P) {
                    return null;
                }
                return B.Dom.getFirstChildBy(P);
            },
            getLastChildBy: function(P, R) {
                if (!P) {
                    return null;
                }
                var Q = (D(P.lastChild, R)) ? P.lastChild: null;
                return Q || B.Dom.getPreviousSiblingBy(P.lastChild, R);
            },
            getLastChild: function(P) {
                P = B.Dom.get(P);
                return B.Dom.getLastChildBy(P);
            },
            getChildrenBy: function(Q, S) {
                var R = B.Dom.getFirstChildBy(Q, S);
                var P = R ? [R] : [];
                B.Dom.getNextSiblingBy(R,
                function(T) {
                    if (!S || S(T)) {
                        P[P.length] = T;
                    }
                    return false;
                });
                return P;
            },
            getChildren: function(P) {
                P = B.Dom.get(P);
                if (!P) {}
                return B.Dom.getChildrenBy(P);
            },
            getDocumentScrollLeft: function(P) {
                P = P || M;
                return Math.max(P.documentElement.scrollLeft, P.body.scrollLeft);
            },
            getDocumentScrollTop: function(P) {
                P = P || M;
                return Math.max(P.documentElement.scrollTop, P.body.scrollTop);
            },
            insertBefore: function(Q, P) {
                Q = B.Dom.get(Q);
                P = B.Dom.get(P);
                if (!Q || !P || !P.parentNode) {
                    return null;
                }
                return P.parentNode.insertBefore(Q, P);
            },
            insertAfter: function(Q, P) {
                Q = B.Dom.get(Q);
                P = B.Dom.get(P);
                if (!Q || !P || !P.parentNode) {
                    return null;
                }
                if (P.nextSibling) {
                    return P.parentNode.insertBefore(Q, P.nextSibling);
                } else {
                    return P.parentNode.appendChild(Q);
                }
            },
            getClientRegion: function() {
                var R = B.Dom.getDocumentScrollTop(),
                Q = B.Dom.getDocumentScrollLeft(),
                S = B.Dom.getViewportWidth() + Q,
                P = B.Dom.getViewportHeight() + R;
                return new B.Region(R, S, P, Q);
            }
        };
        var H = function() {
            if (M.documentElement.getBoundingClientRect) {
                return function(Q) {
                    var R = Q.getBoundingClientRect();
                    var P = Q.ownerDocument;
                    return [R.left + B.Dom.getDocumentScrollLeft(P), R.top + B.Dom.getDocumentScrollTop(P)];
                };
            } else {
                return function(R) {
                    var S = [R.offsetLeft, R.offsetTop];
                    var Q = R.offsetParent;
                    var P = (L && B.Dom.getStyle(R, "position") == "absolute" && R.offsetParent == R.ownerDocument.body);
                    if (Q != R) {
                        while (Q) {
                            S[0] += Q.offsetLeft;
                            S[1] += Q.offsetTop;
                            if (!P && L && B.Dom.getStyle(Q, "position") == "absolute") {
                                P = true;
                            }
                            Q = Q.offsetParent;
                        }
                    }
                    if (P) {
                        S[0] -= R.ownerDocument.body.offsetLeft;
                        S[1] -= R.ownerDocument.body.offsetTop;
                    }
                    Q = R.parentNode;
                    while (Q.tagName && !E.ROOT_TAG.test(Q.tagName)) {
                        if (Q.scrollTop || Q.scrollLeft) {
                            if (!E.OP_SCROLL.test(B.Dom.getStyle(Q, "display"))) {
                                if (!C || B.Dom.getStyle(Q, "overflow") !== "visible") {
                                    S[0] -= Q.scrollLeft;
                                    S[1] -= Q.scrollTop;
                                }
                            }
                        }
                        Q = Q.parentNode;
                    }
                    return S;
                };
            }
        } ();
    })();
    YAHOO.util.Region = function(C, D, A, B) {
        this.top = C;
        this[1] = C;
        this.right = D;
        this.bottom = A;
        this.left = B;
        this[0] = B;
    };
    YAHOO.util.Region.prototype.contains = function(A) {
        return (A.left >= this.left && A.right <= this.right && A.top >= this.top && A.bottom <= this.bottom);
    };
    YAHOO.util.Region.prototype.getArea = function() {
        return ((this.bottom - this.top) * (this.right - this.left));
    };
    YAHOO.util.Region.prototype.intersect = function(E) {
        var C = Math.max(this.top, E.top);
        var D = Math.min(this.right, E.right);
        var A = Math.min(this.bottom, E.bottom);
        var B = Math.max(this.left, E.left);
        if (A >= C && D >= B) {
            return new YAHOO.util.Region(C, D, A, B);
        } else {
            return null;
        }
    };
    YAHOO.util.Region.prototype.union = function(E) {
        var C = Math.min(this.top, E.top);
        var D = Math.max(this.right, E.right);
        var A = Math.max(this.bottom, E.bottom);
        var B = Math.min(this.left, E.left);
        return new YAHOO.util.Region(C, D, A, B);
    };
    YAHOO.util.Region.prototype.toString = function() {
        return ("Region {" + "top: " + this.top + ", right: " + this.right + ", bottom: " + this.bottom + ", left: " + this.left + "}");
    };
    YAHOO.util.Region.getRegion = function(D) {
        var F = YAHOO.util.Dom.getXY(D);
        var C = F[1];
        var E = F[0] + D.offsetWidth;
        var A = F[1] + D.offsetHeight;
        var B = F[0];
        return new YAHOO.util.Region(C, E, A, B);
    };
    YAHOO.util.Point = function(A, B) {
        if (YAHOO.lang.isArray(A)) {
            B = A[1];
            A = A[0];
        }
        this.x = this.right = this.left = this[0] = A;
        this.y = this.top = this.bottom = this[1] = B;
    };
    YAHOO.util.Point.prototype = new YAHOO.util.Region();
    YAHOO.register("dom", YAHOO.util.Dom, {
        version: "2.5.1",
        build: "984"
    });
    YAHOO.util.CustomEvent = function(D, B, C, A) {
        this.type = D;
        this.scope = B || window;
        this.silent = C;
        this.signature = A || YAHOO.util.CustomEvent.LIST;
        this.subscribers = [];
        if (!this.silent) {}
        var E = "_YUICEOnSubscribe";
        if (D !== E) {
            this.subscribeEvent = new YAHOO.util.CustomEvent(E, this, true);
        }
        this.lastError = null;
    };
    YAHOO.util.CustomEvent.LIST = 0;
    YAHOO.util.CustomEvent.FLAT = 1;
    YAHOO.util.CustomEvent.prototype = {
        subscribe: function(B, C, A) {
            if (!B) {
                throw new Error("Invalid callback for subscriber to '" + this.type + "'");
            }
            if (this.subscribeEvent) {
                this.subscribeEvent.fire(B, C, A);
            }
            this.subscribers.push(new YAHOO.util.Subscriber(B, C, A));
        },
        unsubscribe: function(D, F) {
            if (!D) {
                return this.unsubscribeAll();
            }
            var E = false;
            for (var B = 0,
            A = this.subscribers.length; B < A; ++B) {
                var C = this.subscribers[B];
                if (C && C.contains(D, F)) {
                    this._delete(B);
                    E = true;
                }
            }
            return E;
        },
        fire: function() {
            var D = this.subscribers.length;
            if (!D && this.silent) {
                return true;
            }
            var H = [].slice.call(arguments, 0),
            F = true,
            C,
            I = false;
            if (!this.silent) {}
            var B = this.subscribers.slice();
            for (C = 0; C < D; ++C) {
                var K = B[C];
                if (!K) {
                    I = true;
                } else {
                    if (!this.silent) {}
                    var J = K.getScope(this.scope);
                    if (this.signature == YAHOO.util.CustomEvent.FLAT) {
                        var A = null;
                        if (H.length > 0) {
                            A = H[0];
                        }
                        try {
                            F = K.fn.call(J, A, K.obj);
                        } catch(E) {
                            this.lastError = E;
                        }
                    } else {
                        try {
                            F = K.fn.call(J, this.type, H, K.obj);
                        } catch(G) {
                            this.lastError = G;
                        }
                    }
                    if (false === F) {
                        if (!this.silent) {}
                        return false;
                    }
                }
            }
            return true;
        },
        unsubscribeAll: function() {
            for (var A = this.subscribers.length - 1; A > -1; A--) {
                this._delete(A);
            }
            this.subscribers = [];
            return A;
        },
        _delete: function(A) {
            var B = this.subscribers[A];
            if (B) {
                delete B.fn;
                delete B.obj;
            }
            this.subscribers.splice(A, 1);
        },
        toString: function() {
            return "CustomEvent: " + "'" + this.type + "', " + "scope: " + this.scope;
        }
    };
    YAHOO.util.Subscriber = function(B, C, A) {
        this.fn = B;
        this.obj = YAHOO.lang.isUndefined(C) ? null: C;
        this.override = A;
    };
    YAHOO.util.Subscriber.prototype.getScope = function(A) {
        if (this.override) {
            if (this.override === true) {
                return this.obj;
            } else {
                return this.override;
            }
        }
        return A;
    };
    YAHOO.util.Subscriber.prototype.contains = function(A, B) {
        if (B) {
            return (this.fn == A && this.obj == B);
        } else {
            return (this.fn == A);
        }
    };
    YAHOO.util.Subscriber.prototype.toString = function() {
        return "Subscriber { obj: " + this.obj + ", override: " + (this.override || "no") + " }";
    };
    if (!YAHOO.util.Event) {
        YAHOO.util.Event = function() {
            var H = false;
            var I = [];
            var J = [];
            var G = [];
            var E = [];
            var C = 0;
            var F = [];
            var B = [];
            var A = 0;
            var D = {
                63232 : 38,
                63233 : 40,
                63234 : 37,
                63235 : 39,
                63276 : 33,
                63277 : 34,
                25 : 9
            };
            return {
                POLL_RETRYS: 2000,
                POLL_INTERVAL: 20,
                EL: 0,
                TYPE: 1,
                FN: 2,
                WFN: 3,
                UNLOAD_OBJ: 3,
                ADJ_SCOPE: 4,
                OBJ: 5,
                OVERRIDE: 6,
                lastError: null,
                isSafari: YAHOO.env.ua.webkit,
                webkit: YAHOO.env.ua.webkit,
                isIE: YAHOO.env.ua.ie,
                _interval: null,
                _dri: null,
                DOMReady: false,
                startInterval: function() {
                    if (!this._interval) {
                        var K = this;
                        var L = function() {
                            K._tryPreloadAttach();
                        };
                        this._interval = setInterval(L, this.POLL_INTERVAL);
                    }
                },
                onAvailable: function(P, M, Q, O, N) {
                    var K = (YAHOO.lang.isString(P)) ? [P] : P;
                    for (var L = 0; L < K.length; L = L + 1) {
                        F.push({
                            id: K[L],
                            fn: M,
                            obj: Q,
                            override: O,
                            checkReady: N
                        });
                    }
                    C = this.POLL_RETRYS;
                    this.startInterval();
                },
                onContentReady: function(M, K, N, L) {
                    this.onAvailable(M, K, N, L, true);
                },
                onDOMReady: function(K, M, L) {
                    if (this.DOMReady) {
                        setTimeout(function() {
                            var N = window;
                            if (L) {
                                if (L === true) {
                                    N = M;
                                } else {
                                    N = L;
                                }
                            }
                            K.call(N, "DOMReady", [], M);
                        },
                        0);
                    } else {
                        this.DOMReadyEvent.subscribe(K, M, L);
                    }
                },
                addListener: function(M, K, V, Q, L) {
                    if (!V || !V.call) {
                        return false;
                    }
                    if (this._isValidCollection(M)) {
                        var W = true;
                        for (var R = 0,
                        T = M.length; R < T; ++R) {
                            W = this.on(M[R], K, V, Q, L) && W;
                        }
                        return W;
                    } else {
                        if (YAHOO.lang.isString(M)) {
                            var P = this.getEl(M);
                            if (P) {
                                M = P;
                            } else {
                                this.onAvailable(M,
                                function() {
                                    YAHOO.util.Event.on(M, K, V, Q, L);
                                });
                                return true;
                            }
                        }
                    }
                    if (!M) {
                        return false;
                    }
                    if ("unload" == K && Q !== this) {
                        J[J.length] = [M, K, V, Q, L];
                        return true;
                    }
                    var Y = M;
                    if (L) {
                        if (L === true) {
                            Y = Q;
                        } else {
                            Y = L;
                        }
                    }
                    var N = function(Z) {
                        return V.call(Y, YAHOO.util.Event.getEvent(Z, M), Q);
                    };
                    var X = [M, K, V, N, Y, Q, L];
                    var S = I.length;
                    I[S] = X;
                    if (this.useLegacyEvent(M, K)) {
                        var O = this.getLegacyIndex(M, K);
                        if (O == -1 || M != G[O][0]) {
                            O = G.length;
                            B[M.id + K] = O;
                            G[O] = [M, K, M["on" + K]];
                            E[O] = [];
                            M["on" + K] = function(Z) {
                                YAHOO.util.Event.fireLegacyEvent(YAHOO.util.Event.getEvent(Z), O);
                            };
                        }
                        E[O].push(X);
                    } else {
                        try {
                            this._simpleAdd(M, K, N, false);
                        } catch(U) {
                            this.lastError = U;
                            this.removeListener(M, K, V);
                            return false;
                        }
                    }
                    return true;
                },
                fireLegacyEvent: function(O, M) {
                    var Q = true,
                    K, S, R, T, P;
                    S = E[M].slice();
                    for (var L = 0,
                    N = S.length; L < N; ++L) {
                        R = S[L];
                        if (R && R[this.WFN]) {
                            T = R[this.ADJ_SCOPE];
                            P = R[this.WFN].call(T, O);
                            Q = (Q && P);
                        }
                    }
                    K = G[M];
                    if (K && K[2]) {
                        K[2](O);
                    }
                    return Q;
                },
                getLegacyIndex: function(L, M) {
                    var K = this.generateId(L) + M;
                    if (typeof B[K] == "undefined") {
                        return - 1;
                    } else {
                        return B[K];
                    }
                },
                useLegacyEvent: function(L, M) {
                    if (this.webkit && ("click" == M || "dblclick" == M)) {
                        var K = parseInt(this.webkit, 10);
                        if (!isNaN(K) && K < 418) {
                            return true;
                        }
                    }
                    return false;
                },
                removeListener: function(L, K, T) {
                    var O, R, V;
                    if (typeof L == "string") {
                        L = this.getEl(L);
                    } else {
                        if (this._isValidCollection(L)) {
                            var U = true;
                            for (O = L.length - 1; O > -1; O--) {
                                U = (this.removeListener(L[O], K, T) && U);
                            }
                            return U;
                        }
                    }
                    if (!T || !T.call) {
                        return this.purgeElement(L, false, K);
                    }
                    if ("unload" == K) {
                        for (O = J.length - 1; O > -1; O--) {
                            V = J[O];
                            if (V && V[0] == L && V[1] == K && V[2] == T) {
                                J.splice(O, 1);
                                return true;
                            }
                        }
                        return false;
                    }
                    var P = null;
                    var Q = arguments[3];
                    if ("undefined" === typeof Q) {
                        Q = this._getCacheIndex(L, K, T);
                    }
                    if (Q >= 0) {
                        P = I[Q];
                    }
                    if (!L || !P) {
                        return false;
                    }
                    if (this.useLegacyEvent(L, K)) {
                        var N = this.getLegacyIndex(L, K);
                        var M = E[N];
                        if (M) {
                            for (O = 0, R = M.length; O < R; ++O) {
                                V = M[O];
                                if (V && V[this.EL] == L && V[this.TYPE] == K && V[this.FN] == T) {
                                    M.splice(O, 1);
                                    break;
                                }
                            }
                        }
                    } else {
                        try {
                            this._simpleRemove(L, K, P[this.WFN], false);
                        } catch(S) {
                            this.lastError = S;
                            return false;
                        }
                    }
                    delete I[Q][this.WFN];
                    delete I[Q][this.FN];
                    I.splice(Q, 1);
                    return true;
                },
                getTarget: function(M, L) {
                    var K = M.target || M.srcElement;
                    return this.resolveTextNode(K);
                },
                resolveTextNode: function(L) {
                    try {
                        if (L && 3 == L.nodeType) {
                            return L.parentNode;
                        }
                    } catch(K) {}
                    return L;
                },
                getPageX: function(L) {
                    var K = L.pageX;
                    if (!K && 0 !== K) {
                        K = L.clientX || 0;
                        if (this.isIE) {
                            K += this._getScrollLeft();
                        }
                    }
                    return K;
                },
                getPageY: function(K) {
                    var L = K.pageY;
                    if (!L && 0 !== L) {
                        L = K.clientY || 0;
                        if (this.isIE) {
                            L += this._getScrollTop();
                        }
                    }
                    return L;
                },
                getXY: function(K) {
                    return [this.getPageX(K), this.getPageY(K)];
                },
                getRelatedTarget: function(L) {
                    var K = L.relatedTarget;
                    if (!K) {
                        if (L.type == "mouseout") {
                            K = L.toElement;
                        } else {
                            if (L.type == "mouseover") {
                                K = L.fromElement;
                            }
                        }
                    }
                    return this.resolveTextNode(K);
                },
                getTime: function(M) {
                    if (!M.time) {
                        var L = new Date().getTime();
                        try {
                            M.time = L;
                        } catch(K) {
                            this.lastError = K;
                            return L;
                        }
                    }
                    return M.time;
                },
                stopEvent: function(K) {
                    this.stopPropagation(K);
                    this.preventDefault(K);
                },
                stopPropagation: function(K) {
                    if (K.stopPropagation) {
                        K.stopPropagation();
                    } else {
                        K.cancelBubble = true;
                    }
                },
                preventDefault: function(K) {
                    if (K.preventDefault) {
                        K.preventDefault();
                    } else {
                        K.returnValue = false;
                    }
                },
                getEvent: function(M, K) {
                    var L = M || window.event;
                    if (!L) {
                        var N = this.getEvent.caller;
                        while (N) {
                            L = N.arguments[0];
                            if (L && Event == L.constructor) {
                                break;
                            }
                            N = N.caller;
                        }
                    }
                    return L;
                },
                getCharCode: function(L) {
                    var K = L.keyCode || L.charCode || 0;
                    if (YAHOO.env.ua.webkit && (K in D)) {
                        K = D[K];
                    }
                    return K;
                },
                _getCacheIndex: function(O, P, N) {
                    for (var M = 0,
                    L = I.length; M < L; M = M + 1) {
                        var K = I[M];
                        if (K && K[this.FN] == N && K[this.EL] == O && K[this.TYPE] == P) {
                            return M;
                        }
                    }
                    return - 1;
                },
                generateId: function(K) {
                    var L = K.id;
                    if (!L) {
                        L = "yuievtautoid-" + A; ++A;
                        K.id = L;
                    }
                    return L;
                },
                _isValidCollection: function(L) {
                    try {
                        return (L && typeof L !== "string" && L.length && !L.tagName && !L.alert && typeof L[0] !== "undefined");
                    } catch(K) {
                        return false;
                    }
                },
                elCache: {},
                getEl: function(K) {
                    return (typeof K === "string") ? document.getElementById(K) : K;
                },
                clearCache: function() {},
                DOMReadyEvent: new YAHOO.util.CustomEvent("DOMReady", this),
                _load: function(L) {
                    if (!H) {
                        H = true;
                        var K = YAHOO.util.Event;
                        K._ready();
                        K._tryPreloadAttach();
                    }
                },
                _ready: function(L) {
                    var K = YAHOO.util.Event;
                    if (!K.DOMReady) {
                        K.DOMReady = true;
                        K.DOMReadyEvent.fire();
                        K._simpleRemove(document, "DOMContentLoaded", K._ready);
                    }
                },
                _tryPreloadAttach: function() {
                    if (F.length === 0) {
                        C = 0;
                        clearInterval(this._interval);
                        this._interval = null;
                        return;
                    }
                    if (this.locked) {
                        return;
                    }
                    if (this.isIE) {
                        if (!this.DOMReady) {
                            this.startInterval();
                            return;
                        }
                    }
                    this.locked = true;
                    var Q = !H;
                    if (!Q) {
                        Q = (C > 0 && F.length > 0);
                    }
                    var P = [];
                    var R = function(T, U) {
                        var S = T;
                        if (U.override) {
                            if (U.override === true) {
                                S = U.obj;
                            } else {
                                S = U.override;
                            }
                        }
                        U.fn.call(S, U.obj);
                    };
                    var L, K, O, N, M = [];
                    for (L = 0, K = F.length; L < K; L = L + 1) {
                        O = F[L];
                        if (O) {
                            N = this.getEl(O.id);
                            if (N) {
                                if (O.checkReady) {
                                    if (H || N.nextSibling || !Q) {
                                        M.push(O);
                                        F[L] = null;
                                    }
                                } else {
                                    R(N, O);
                                    F[L] = null;
                                }
                            } else {
                                P.push(O);
                            }
                        }
                    }
                    for (L = 0, K = M.length; L < K; L = L + 1) {
                        O = M[L];
                        R(this.getEl(O.id), O);
                    }
                    C--;
                    if (Q) {
                        for (L = F.length - 1; L > -1; L--) {
                            O = F[L];
                            if (!O || !O.id) {
                                F.splice(L, 1);
                            }
                        }
                        this.startInterval();
                    } else {
                        clearInterval(this._interval);
                        this._interval = null;
                    }
                    this.locked = false;
                },
                purgeElement: function(O, P, R) {
                    var M = (YAHOO.lang.isString(O)) ? this.getEl(O) : O;
                    var Q = this.getListeners(M, R),
                    N,
                    K;
                    if (Q) {
                        for (N = Q.length - 1; N > -1; N--) {
                            var L = Q[N];
                            this.removeListener(M, L.type, L.fn);
                        }
                    }
                    if (P && M && M.childNodes) {
                        for (N = 0, K = M.childNodes.length; N < K; ++N) {
                            this.purgeElement(M.childNodes[N], P, R);
                        }
                    }
                },
                getListeners: function(M, K) {
                    var P = [],
                    L;
                    if (!K) {
                        L = [I, J];
                    } else {
                        if (K === "unload") {
                            L = [J];
                        } else {
                            L = [I];
                        }
                    }
                    var R = (YAHOO.lang.isString(M)) ? this.getEl(M) : M;
                    for (var O = 0; O < L.length; O = O + 1) {
                        var T = L[O];
                        if (T) {
                            for (var Q = 0,
                            S = T.length; Q < S; ++Q) {
                                var N = T[Q];
                                if (N && N[this.EL] === R && (!K || K === N[this.TYPE])) {
                                    P.push({
                                        type: N[this.TYPE],
                                        fn: N[this.FN],
                                        obj: N[this.OBJ],
                                        adjust: N[this.OVERRIDE],
                                        scope: N[this.ADJ_SCOPE],
                                        index: Q
                                    });
                                }
                            }
                        }
                    }
                    return (P.length) ? P: null;
                },
                _unload: function(Q) {
                    var K = YAHOO.util.Event,
                    N, M, L, P, O, R = J.slice();
                    for (N = 0, P = J.length; N < P; ++N) {
                        L = R[N];
                        if (L) {
                            var S = window;
                            if (L[K.ADJ_SCOPE]) {
                                if (L[K.ADJ_SCOPE] === true) {
                                    S = L[K.UNLOAD_OBJ];
                                } else {
                                    S = L[K.ADJ_SCOPE];
                                }
                            }
                            L[K.FN].call(S, K.getEvent(Q, L[K.EL]), L[K.UNLOAD_OBJ]);
                            R[N] = null;
                            L = null;
                            S = null;
                        }
                    }
                    J = null;
                    if (I) {
                        for (M = I.length - 1; M > -1; M--) {
                            L = I[M];
                            if (L) {
                                K.removeListener(L[K.EL], L[K.TYPE], L[K.FN], M);
                            }
                        }
                        L = null;
                    }
                    G = null;
                    K._simpleRemove(window, "unload", K._unload);
                },
                _getScrollLeft: function() {
                    return this._getScroll()[1];
                },
                _getScrollTop: function() {
                    return this._getScroll()[0];
                },
                _getScroll: function() {
                    var K = document.documentElement,
                    L = document.body;
                    if (K && (K.scrollTop || K.scrollLeft)) {
                        return [K.scrollTop, K.scrollLeft];
                    } else {
                        if (L) {
                            return [L.scrollTop, L.scrollLeft];
                        } else {
                            return [0, 0];
                        }
                    }
                },
                regCE: function() {},
                _simpleAdd: function() {
                    if (window.addEventListener) {
                        return function(M, N, L, K) {
                            M.addEventListener(N, L, (K));
                        };
                    } else {
                        if (window.attachEvent) {
                            return function(M, N, L, K) {
                                M.attachEvent("on" + N, L);
                            };
                        } else {
                            return function() {};
                        }
                    }
                } (),
                _simpleRemove: function() {
                    if (window.removeEventListener) {
                        return function(M, N, L, K) {
                            M.removeEventListener(N, L, (K));
                        };
                    } else {
                        if (window.detachEvent) {
                            return function(L, M, K) {
                                L.detachEvent("on" + M, K);
                            };
                        } else {
                            return function() {};
                        }
                    }
                } ()
            };
        } (); (function() {
            var EU = YAHOO.util.Event;
            EU.on = EU.addListener;
            if (EU.isIE) {
                YAHOO.util.Event.onDOMReady(YAHOO.util.Event._tryPreloadAttach, YAHOO.util.Event, true);
                var n = document.createElement("p");
                EU._dri = setInterval(function() {
                    try {
                        n.doScroll("left");
                        clearInterval(EU._dri);
                        EU._dri = null;
                        EU._ready();
                        n = null;
                    } catch(ex) {}
                },
                EU.POLL_INTERVAL);
            } else {
                if (EU.webkit && EU.webkit < 525) {
                    EU._dri = setInterval(function() {
                        var rs = document.readyState;
                        if ("loaded" == rs || "complete" == rs) {
                            clearInterval(EU._dri);
                            EU._dri = null;
                            EU._ready();
                        }
                    },
                    EU.POLL_INTERVAL);
                } else {
                    EU._simpleAdd(document, "DOMContentLoaded", EU._ready);
                }
            }
            EU._simpleAdd(window, "load", EU._load);
            EU._simpleAdd(window, "unload", EU._unload);
            EU._tryPreloadAttach();
        })();
    }
    YAHOO.util.EventProvider = function() {};
    YAHOO.util.EventProvider.prototype = {
        __yui_events: null,
        __yui_subscribers: null,
        subscribe: function(A, C, F, E) {
            this.__yui_events = this.__yui_events || {};
            var D = this.__yui_events[A];
            if (D) {
                D.subscribe(C, F, E);
            } else {
                this.__yui_subscribers = this.__yui_subscribers || {};
                var B = this.__yui_subscribers;
                if (!B[A]) {
                    B[A] = [];
                }
                B[A].push({
                    fn: C,
                    obj: F,
                    override: E
                });
            }
        },
        unsubscribe: function(C, E, G) {
            this.__yui_events = this.__yui_events || {};
            var A = this.__yui_events;
            if (C) {
                var F = A[C];
                if (F) {
                    return F.unsubscribe(E, G);
                }
            } else {
                var B = true;
                for (var D in A) {
                    if (YAHOO.lang.hasOwnProperty(A, D)) {
                        B = B && A[D].unsubscribe(E, G);
                    }
                }
                return B;
            }
            return false;
        },
        unsubscribeAll: function(A) {
            return this.unsubscribe(A);
        },
        createEvent: function(G, D) {
            this.__yui_events = this.__yui_events || {};
            var A = D || {};
            var I = this.__yui_events;
            if (I[G]) {} else {
                var H = A.scope || this;
                var E = (A.silent);
                var B = new YAHOO.util.CustomEvent(G, H, E, YAHOO.util.CustomEvent.FLAT);
                I[G] = B;
                if (A.onSubscribeCallback) {
                    B.subscribeEvent.subscribe(A.onSubscribeCallback);
                }
                this.__yui_subscribers = this.__yui_subscribers || {};
                var F = this.__yui_subscribers[G];
                if (F) {
                    for (var C = 0; C < F.length; ++C) {
                        B.subscribe(F[C].fn, F[C].obj, F[C].override);
                    }
                }
            }
            return I[G];
        },
        fireEvent: function(E, D, A, C) {
            this.__yui_events = this.__yui_events || {};
            var G = this.__yui_events[E];
            if (!G) {
                return null;
            }
            var B = [];
            for (var F = 1; F < arguments.length; ++F) {
                B.push(arguments[F]);
            }
            return G.fire.apply(G, B);
        },
        hasEvent: function(A) {
            if (this.__yui_events) {
                if (this.__yui_events[A]) {
                    return true;
                }
            }
            return false;
        }
    };
    YAHOO.util.KeyListener = function(A, F, B, C) {
        if (!A) {} else {
            if (!F) {} else {
                if (!B) {}
            }
        }
        if (!C) {
            C = YAHOO.util.KeyListener.KEYDOWN;
        }
        var D = new YAHOO.util.CustomEvent("keyPressed");
        this.enabledEvent = new YAHOO.util.CustomEvent("enabled");
        this.disabledEvent = new YAHOO.util.CustomEvent("disabled");
        if (typeof A == "string") {
            A = document.getElementById(A);
        }
        if (typeof B == "function") {
            D.subscribe(B);
        } else {
            D.subscribe(B.fn, B.scope, B.correctScope);
        }
        function E(J, I) {
            if (!F.shift) {
                F.shift = false;
            }
            if (!F.alt) {
                F.alt = false;
            }
            if (!F.ctrl) {
                F.ctrl = false;
            }
            if (J.shiftKey == F.shift && J.altKey == F.alt && J.ctrlKey == F.ctrl) {
                var G;
                if (F.keys instanceof Array) {
                    for (var H = 0; H < F.keys.length; H++) {
                        G = F.keys[H];
                        if (G == J.charCode) {
                            D.fire(J.charCode, J);
                            break;
                        } else {
                            if (G == J.keyCode) {
                                D.fire(J.keyCode, J);
                                break;
                            }
                        }
                    }
                } else {
                    G = F.keys;
                    if (G == J.charCode) {
                        D.fire(J.charCode, J);
                    } else {
                        if (G == J.keyCode) {
                            D.fire(J.keyCode, J);
                        }
                    }
                }
            }
        }
        this.enable = function() {
            if (!this.enabled) {
                YAHOO.util.Event.addListener(A, C, E);
                this.enabledEvent.fire(F);
            }
            this.enabled = true;
        };
        this.disable = function() {
            if (this.enabled) {
                YAHOO.util.Event.removeListener(A, C, E);
                this.disabledEvent.fire(F);
            }
            this.enabled = false;
        };
        this.toString = function() {
            return "KeyListener [" + F.keys + "] " + A.tagName + (A.id ? "[" + A.id + "]": "");
        };
    };
    YAHOO.util.KeyListener.KEYDOWN = "keydown";
    YAHOO.util.KeyListener.KEYUP = "keyup";
    YAHOO.util.KeyListener.KEY = {
        ALT: 18,
        BACK_SPACE: 8,
        CAPS_LOCK: 20,
        CONTROL: 17,
        DELETE: 46,
        DOWN: 40,
        END: 35,
        ENTER: 13,
        ESCAPE: 27,
        HOME: 36,
        LEFT: 37,
        META: 224,
        NUM_LOCK: 144,
        PAGE_DOWN: 34,
        PAGE_UP: 33,
        PAUSE: 19,
        PRINTSCREEN: 44,
        RIGHT: 39,
        SCROLL_LOCK: 145,
        SHIFT: 16,
        SPACE: 32,
        TAB: 9,
        UP: 38
    };
    YAHOO.register("event", YAHOO.util.Event, {
        version: "2.5.1",
        build: "984"
    });
    YAHOO.register("yahoo-dom-event", YAHOO, {
        version: "2.5.1",
        build: "984"
    });
    YAHOO.util.Event._load();
    return YAHOO;
})();
var isMWPSupported = true;
var badUserAgentStrings = ['NETSCAPE6', 'NETSCAPE/7'];
if (navigator) {
    var len = badUserAgentStrings.length;
    for (var i = 0; i < len; i++) {
        if (navigator.userAgent.toUpperCase().indexOf(badUserAgentStrings[i]) !== -1) {
            isMWPSupported = false;
        }
    }
}
if (isMWPSupported === true) {
    if (typeof YAHOO.mediaplayer == "undefined") {
        YAHOO.namespace("YAHOO.mediaplayer");
    }
    YAHOO.mediaplayer.playerAlreadyLoaded = function() {
        if (YAHOO.mediaplayer.Controller) {
            return true;
        }
        var aScripts = document.getElementsByTagName("script");
        if (aScripts.length > 0) {
            var count = aScripts.length;
            for (var i = 0; i < count; i++) {
                var elmScript = aScripts[i];
                var sHref = elmScript.getAttribute("src");
                if (sHref && sHref.length > 0 && (sHref.indexOf("http://l.yimg.com/us.js.yimg.com/lib/mus/js/ymwp/mediaplayer-") > -1 || sHref.indexOf("http://l.yimg.com/us.js.yimg.com/lib/mus/js/ymwp/webplayer-") > -1)) {
                    return true;
                }
            }
        }
    };
    if (YAHOO.mediaplayer.playerAlreadyLoaded() !== true) {
        YAHOO.mediaplayer.partnerId = "42858483";
        if (typeof YMPParams == "undefined") {
            var YMPParams = {};
        }
        YAHOO.mediaplayer.loadPlayerScript = function() {
            if (Boolean(arguments.callee.bCalled) || (YAHOO.mediaplayer.playerAlreadyLoaded() === true)) {
                return;
            };
            arguments.callee.bCalled = true;
            function webplayerjs() {
                var suffix = '-min-2.0.31.js';
                var path = "http://l.yimg.com/us.js.yimg.com/lib/mus/js/ymwp/";
                return path + 'mediaplayer' + suffix;
            };
            var playerScriptSrc = webplayerjs();
            if (playerScriptSrc && playerScriptSrc.length > 0) {
                YAHOO.mediaplayer.elPlayerSource = document.createElement("script");
                YAHOO.mediaplayer.elPlayerSource.type = "text/javascript";
                YAHOO.mediaplayer.elPlayerSource.src = playerScriptSrc;
                document.getElementsByTagName("head")[0].appendChild(YAHOO.mediaplayer.elPlayerSource);
            }
        };
        YAHOO.ympyui.util.Event.addListener(window, "load", YAHOO.mediaplayer.loadPlayerScript);
        YAHOO.namespace("YAHOO.MediaPlayer");
        YAHOO.MediaPlayer = function() {
            this.controller = null;
        };
        YAHOO.MediaPlayer.onAPIReady = new YAHOO.ympyui.util.CustomEvent("onAPIReady", null, false, YAHOO.ympyui.util.CustomEvent.FLAT);
    }
}
