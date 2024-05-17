import './style.css';
import { page as ta, line as na, LINEMODE as ra, isPointInRect as aa, node as oa, DIRECTION as Be, CopyPasteHelpers as ia, rectangleDrawer as sa, defaultGraph as la } from "@fit-elsa/elsa-core";
import * as C from "react";
import dt, { useState as ie, useRef as ft, useEffect as ye, createContext as pt, useContext as mt, useReducer as ca } from "react";
import ua from "react-dom";
import { Form as k, Input as be, Dropdown as da, Button as se, TreeSelect as fa, Row as de, Col as Y, Select as Vn, Collapse as J, Popover as Ce, Slider as Ut, Tree as qn, ConfigProvider as pa, InputNumber as Wn, Empty as fn } from "antd";
import ma from "axios";
const ha = (e, r, t, o) => {
  const i = ta(e, r, t, o);
  i.type = "jadeFlowPage", i.serializedFields.batchAdd("x", "y", "scaleX", "scaleY"), i.namespace = "jadeFlow", i.backgroundGrid = "point", i.backgroundGridSize = 16, i.backgroundGridMargin = 16, i.backColor = "#fbfbfc", i.focusBackColor = "#fbfbfc", i.gridColor = "#e1e1e3", i.disableContextMenu = !0, i.moveAble = !0, i.observableStore = ga();
  const s = i.onLoaded;
  i.onLoaded = () => {
    s.apply(i), i.shapes.forEach((c) => c.onPageLoaded && c.onPageLoaded());
  }, i.registerObservable = (c, p, h, v, f) => {
    i.observableStore.add(c, p, h, v, f);
  }, i.removeObservable = (c, p = null) => {
    i.observableStore.remove(c, p);
  }, i.getObservableList = (c) => i.observableStore.getObservableList(c), i.observeTo = (c, p, h) => {
    i.observableStore.addObserver(c, p, h);
  }, i.stopObserving = (c, p, h) => {
    i.observableStore.removeObserver(c, p, h);
  }, i.getObservable = (c, p) => i.observableStore.getObservable(c, p);
  const l = i.clear;
  i.clear = () => {
    l.apply(i), i.observableStore.clear();
  };
  const n = i.createNew;
  i.createNew = (c, p, h, v, f, g, m) => {
    d.filter((j) => j.type === "before").forEach((j) => j.handle(i, c, p, h, f, g));
    const y = n.apply(i, [c, p, h, v, f, g, m]);
    return d.filter((j) => j.type === "after").forEach((j) => j.handle(i, y)), y;
  };
  const d = [];
  return i.registerShapeCreationHandler = (c) => {
    d.push(c);
  }, i.getMenuScript = () => [], i.registerShapeCreationHandler({
    type: "before",
    handle: (c, p) => {
      if ((p === "startNodeStart" || p === "endNodeEnd") && c.shapes.find((h) => h.type === p))
        throw new Error("最多只能有一个开始或结束节点.");
    }
  }), i;
}, ga = () => {
  const e = {};
  e.store = /* @__PURE__ */ new Map(), e.add = (t, o, i, s, l) => {
    const n = r(e.store, t, () => /* @__PURE__ */ new Map()), d = r(n, o, () => ({
      observableId: o,
      value: null,
      type: null,
      observers: [],
      parentId: l
    }));
    d.value = i, d.type = s, d.parentId = l;
  }, e.remove = (t, o = null) => {
    if (o) {
      const i = e.store.get(t);
      i && o && (i.delete(o), i.size === 0 && e.store.delete(t));
    } else
      e.store.delete(t);
  }, e.addObserver = (t, o, i) => {
    const s = r(e.store, t, () => /* @__PURE__ */ new Map());
    r(s, o, () => ({
      observableId: o,
      value: null,
      observers: []
    })).observers.push(i);
  }, e.removeObserver = (t, o, i) => {
    const s = e.store.get(t);
    if (!s)
      return;
    const l = s.get(o);
    if (!l)
      return;
    const n = l.observers.findIndex((d) => d === i);
    n !== -1 && l.observers.splice(n, 1);
  };
  const r = (t, o, i) => {
    let s = t.get(o);
    return s || (s = i(), t.set(o, s)), s;
  };
  return e.getObservableList = (t) => {
    const o = e.store.get(t);
    return o ? Array.from(o.values()).map((i) => ({ nodeId: t, observableId: i.observableId, parentId: i.parentId, value: i.value, type: i.type })) : [];
  }, e.getObservable = (t, o) => {
    const i = e.store.get(t);
    return i ? i.get(o) : null;
  }, e.clear = () => {
    e.store.clear();
  }, e;
};
let va = (e, r, t, o, i, s, l) => {
  let n = na(e, r, t, o, i, s, l);
  n.type = "jadeEvent", n.borderWidth = 1, n.beginArrow = !1, n.endArrow = !0, n.lineMode = ra.AUTO_CURVE, n.borderColor = "#B1B1B7", n.mouseInBorderColor = "#B1B1B7", n.allowSwitchLineMode = !1;
  const d = n.getIndex;
  n.getIndex = () => {
    let h = d.call(n);
    return n.index = h - 200, n.index;
  }, n.onPageLoaded = () => {
    n.toShape && (n.currentToShape = n.page.getShapeById(n.toShape));
  };
  const c = n.initConnectors;
  n.initConnectors = () => {
    c.call(n), n.fromConnector.visible = !1, n.toConnector.direction.color = "transparent", n.toConnector.strokeStyle = "transparent";
    const h = n.toConnector.release;
    n.toConnector.release = (f) => {
      const g = n.fromShape, m = n.fromShapeConnector;
      function y() {
        return n.page.shapes.filter((j) => j.type === "jadeEvent").count((j) => j.fromShape === g && j.toShape === n.connectingShape.id && j.fromShapeConnector === m && j.toShapeConnector === n.connectingShape.linkingConnector) > 1;
      }
      if (n.isFocused = !1, h.call(n.toConnector, f), y())
        n.remove();
      else if (n.toShape === "")
        n.currentToShape && n.currentToShape.offConnect(), n.remove();
      else {
        const j = n.getToShape();
        j.onConnect(), n.currentToShape = j;
      }
    }, n.toConnector.radius = 4, n.toConnector.moving = (f, g, m, y) => {
      let j = n.page.disableReact;
      n.page.disableReact = !0;
      const R = n.from();
      n.resize(m - R.x, y - R.y), n.shapeLinking(n.to().x, n.to().y), n.toMoving = !0, n.page.disableReact = j, n.toConnector.afterMoving();
    };
    const v = n.toConnector.afterMoving;
    n.toConnector.afterMoving = () => {
      n.connectingShape && n.connectingShape.linkingConnector ? (n.definedToConnector = n.connectingShape.linkingConnector.direction.key, n.toShape = n.connectingShape.id) : (n.definedToConnector = "", n.toShape = ""), v.apply(n.toConnector);
    };
  };
  const p = n.remove;
  return n.remove = (h) => {
    const v = n.getToShape(), f = p.apply(n, [h]);
    return v && v.offConnect(), f;
  }, n.connect = (h, v, f, g) => {
    n.fromShape = h, n.definedFromConnector = v, n.toShape = f, n.definedToConnector = g, n.follow();
  }, n.validateLinking = (h, v, f) => aa({ x: v, y: f }, h.getBound()), n.shapeLinking = (h, v) => {
    if (!n.linkAble)
      return;
    const f = n.page.find(h, v, (g) => g.allowLink && n.validateLinking(g, h, v));
    n.shapeDelinking(f), n.connectingShape = f, n.connectingShape && n.connectingShape !== n.page && (n.connectingShape.linking = !0, n.connectingShape.linkingConnector = n.connectingShape.getClosestConnector(h, v, (g) => g.connectable && g.allowToLink), n.connectingShape.render());
  }, n.needGetToConnector = () => n.getToShape(), n;
};
function Un(e) {
  return e && e.__esModule && Object.prototype.hasOwnProperty.call(e, "default") ? e.default : e;
}
var At = { exports: {} }, Ke = {};
/**
 * @license React
 * react-jsx-runtime.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var pn;
function ya() {
  if (pn)
    return Ke;
  pn = 1;
  var e = dt, r = Symbol.for("react.element"), t = Symbol.for("react.fragment"), o = Object.prototype.hasOwnProperty, i = e.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner, s = { key: !0, ref: !0, __self: !0, __source: !0 };
  function l(n, d, c) {
    var p, h = {}, v = null, f = null;
    c !== void 0 && (v = "" + c), d.key !== void 0 && (v = "" + d.key), d.ref !== void 0 && (f = d.ref);
    for (p in d)
      o.call(d, p) && !s.hasOwnProperty(p) && (h[p] = d[p]);
    if (n && n.defaultProps)
      for (p in d = n.defaultProps, d)
        h[p] === void 0 && (h[p] = d[p]);
    return { $$typeof: r, type: n, key: v, ref: f, props: h, _owner: i.current };
  }
  return Ke.Fragment = t, Ke.jsx = l, Ke.jsxs = l, Ke;
}
var Ye = {};
/**
 * @license React
 * react-jsx-runtime.development.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var mn;
function ba() {
  return mn || (mn = 1, process.env.NODE_ENV !== "production" && function() {
    var e = dt, r = Symbol.for("react.element"), t = Symbol.for("react.portal"), o = Symbol.for("react.fragment"), i = Symbol.for("react.strict_mode"), s = Symbol.for("react.profiler"), l = Symbol.for("react.provider"), n = Symbol.for("react.context"), d = Symbol.for("react.forward_ref"), c = Symbol.for("react.suspense"), p = Symbol.for("react.suspense_list"), h = Symbol.for("react.memo"), v = Symbol.for("react.lazy"), f = Symbol.for("react.offscreen"), g = Symbol.iterator, m = "@@iterator";
    function y(u) {
      if (u === null || typeof u != "object")
        return null;
      var b = g && u[g] || u[m];
      return typeof b == "function" ? b : null;
    }
    var j = e.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED;
    function R(u) {
      {
        for (var b = arguments.length, w = new Array(b > 1 ? b - 1 : 0), O = 1; O < b; O++)
          w[O - 1] = arguments[O];
        $("error", u, w);
      }
    }
    function $(u, b, w) {
      {
        var O = j.ReactDebugCurrentFrame, U = O.getStackAddendum();
        U !== "" && (b += "%s", w = w.concat([U]));
        var Z = w.map(function(q) {
          return String(q);
        });
        Z.unshift("Warning: " + b), Function.prototype.apply.call(console[u], console, Z);
      }
    }
    var B = !1, E = !1, Ee = !1, qe = !1, We = !1, _e;
    _e = Symbol.for("react.module.reference");
    function Ue(u) {
      return !!(typeof u == "string" || typeof u == "function" || u === o || u === s || We || u === i || u === c || u === p || qe || u === f || B || E || Ee || typeof u == "object" && u !== null && (u.$$typeof === v || u.$$typeof === h || u.$$typeof === l || u.$$typeof === n || u.$$typeof === d || // This needs to include all possible module reference object
      // types supported by any Flight configuration anywhere since
      // we don't know which Flight build this will end up being used
      // with.
      u.$$typeof === _e || u.getModuleId !== void 0));
    }
    function Le(u, b, w) {
      var O = u.displayName;
      if (O)
        return O;
      var U = b.displayName || b.name || "";
      return U !== "" ? w + "(" + U + ")" : w;
    }
    function Me(u) {
      return u.displayName || "Context";
    }
    function le(u) {
      if (u == null)
        return null;
      if (typeof u.tag == "number" && R("Received an unexpected object in getComponentNameFromType(). This is likely a bug in React. Please file an issue."), typeof u == "function")
        return u.displayName || u.name || null;
      if (typeof u == "string")
        return u;
      switch (u) {
        case o:
          return "Fragment";
        case t:
          return "Portal";
        case s:
          return "Profiler";
        case i:
          return "StrictMode";
        case c:
          return "Suspense";
        case p:
          return "SuspenseList";
      }
      if (typeof u == "object")
        switch (u.$$typeof) {
          case n:
            var b = u;
            return Me(b) + ".Consumer";
          case l:
            var w = u;
            return Me(w._context) + ".Provider";
          case d:
            return Le(u, u.render, "ForwardRef");
          case h:
            var O = u.displayName || null;
            return O !== null ? O : le(u.type) || "Memo";
          case v: {
            var U = u, Z = U._payload, q = U._init;
            try {
              return le(q(Z));
            } catch {
              return null;
            }
          }
        }
      return null;
    }
    var oe = Object.assign, Se = 0, ce, xe, Ne, Ae, x, P, M;
    function F() {
    }
    F.__reactDisabledLog = !0;
    function N() {
      {
        if (Se === 0) {
          ce = console.log, xe = console.info, Ne = console.warn, Ae = console.error, x = console.group, P = console.groupCollapsed, M = console.groupEnd;
          var u = {
            configurable: !0,
            enumerable: !0,
            value: F,
            writable: !0
          };
          Object.defineProperties(console, {
            info: u,
            log: u,
            warn: u,
            error: u,
            group: u,
            groupCollapsed: u,
            groupEnd: u
          });
        }
        Se++;
      }
    }
    function V() {
      {
        if (Se--, Se === 0) {
          var u = {
            configurable: !0,
            enumerable: !0,
            writable: !0
          };
          Object.defineProperties(console, {
            log: oe({}, u, {
              value: ce
            }),
            info: oe({}, u, {
              value: xe
            }),
            warn: oe({}, u, {
              value: Ne
            }),
            error: oe({}, u, {
              value: Ae
            }),
            group: oe({}, u, {
              value: x
            }),
            groupCollapsed: oe({}, u, {
              value: P
            }),
            groupEnd: oe({}, u, {
              value: M
            })
          });
        }
        Se < 0 && R("disabledDepth fell below zero. This is a bug in React. Please file an issue.");
      }
    }
    var I = j.ReactCurrentDispatcher, T;
    function A(u, b, w) {
      {
        if (T === void 0)
          try {
            throw Error();
          } catch (U) {
            var O = U.stack.trim().match(/\n( *(at )?)/);
            T = O && O[1] || "";
          }
        return `
` + T + u;
      }
    }
    var W = !1, z;
    {
      var re = typeof WeakMap == "function" ? WeakMap : Map;
      z = new re();
    }
    function S(u, b) {
      if (!u || W)
        return "";
      {
        var w = z.get(u);
        if (w !== void 0)
          return w;
      }
      var O;
      W = !0;
      var U = Error.prepareStackTrace;
      Error.prepareStackTrace = void 0;
      var Z;
      Z = I.current, I.current = null, N();
      try {
        if (b) {
          var q = function() {
            throw Error();
          };
          if (Object.defineProperty(q.prototype, "props", {
            set: function() {
              throw Error();
            }
          }), typeof Reflect == "object" && Reflect.construct) {
            try {
              Reflect.construct(q, []);
            } catch (Pe) {
              O = Pe;
            }
            Reflect.construct(u, [], q);
          } else {
            try {
              q.call();
            } catch (Pe) {
              O = Pe;
            }
            u.call(q.prototype);
          }
        } else {
          try {
            throw Error();
          } catch (Pe) {
            O = Pe;
          }
          u();
        }
      } catch (Pe) {
        if (Pe && O && typeof Pe.stack == "string") {
          for (var D = Pe.stack.split(`
`), ae = O.stack.split(`
`), X = D.length - 1, ee = ae.length - 1; X >= 1 && ee >= 0 && D[X] !== ae[ee]; )
            ee--;
          for (; X >= 1 && ee >= 0; X--, ee--)
            if (D[X] !== ae[ee]) {
              if (X !== 1 || ee !== 1)
                do
                  if (X--, ee--, ee < 0 || D[X] !== ae[ee]) {
                    var pe = `
` + D[X].replace(" at new ", " at ");
                    return u.displayName && pe.includes("<anonymous>") && (pe = pe.replace("<anonymous>", u.displayName)), typeof u == "function" && z.set(u, pe), pe;
                  }
                while (X >= 1 && ee >= 0);
              break;
            }
        }
      } finally {
        W = !1, I.current = Z, V(), Error.prepareStackTrace = U;
      }
      var $e = u ? u.displayName || u.name : "", dn = $e ? A($e) : "";
      return typeof u == "function" && z.set(u, dn), dn;
    }
    function fe(u, b, w) {
      return S(u, !1);
    }
    function ze(u) {
      var b = u.prototype;
      return !!(b && b.isReactComponent);
    }
    function Ie(u, b, w) {
      if (u == null)
        return "";
      if (typeof u == "function")
        return S(u, ze(u));
      if (typeof u == "string")
        return A(u);
      switch (u) {
        case c:
          return A("Suspense");
        case p:
          return A("SuspenseList");
      }
      if (typeof u == "object")
        switch (u.$$typeof) {
          case d:
            return fe(u.render);
          case h:
            return Ie(u.type, b, w);
          case v: {
            var O = u, U = O._payload, Z = O._init;
            try {
              return Ie(Z(U), b, w);
            } catch {
            }
          }
        }
      return "";
    }
    var nt = Object.prototype.hasOwnProperty, Xt = {}, Qt = j.ReactDebugCurrentFrame;
    function rt(u) {
      if (u) {
        var b = u._owner, w = Ie(u.type, u._source, b ? b.type : null);
        Qt.setExtraStackFrame(w);
      } else
        Qt.setExtraStackFrame(null);
    }
    function Lr(u, b, w, O, U) {
      {
        var Z = Function.call.bind(nt);
        for (var q in u)
          if (Z(u, q)) {
            var D = void 0;
            try {
              if (typeof u[q] != "function") {
                var ae = Error((O || "React class") + ": " + w + " type `" + q + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + typeof u[q] + "`.This often happens because of typos such as `PropTypes.function` instead of `PropTypes.func`.");
                throw ae.name = "Invariant Violation", ae;
              }
              D = u[q](b, q, O, w, null, "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED");
            } catch (X) {
              D = X;
            }
            D && !(D instanceof Error) && (rt(U), R("%s: type specification of %s `%s` is invalid; the type checker function must return `null` or an `Error` but returned a %s. You may have forgotten to pass an argument to the type checker creator (arrayOf, instanceOf, objectOf, oneOf, oneOfType, and shape all require an argument).", O || "React class", w, q, typeof D), rt(null)), D instanceof Error && !(D.message in Xt) && (Xt[D.message] = !0, rt(U), R("Failed %s type: %s", w, D.message), rt(null));
          }
      }
    }
    var Mr = Array.isArray;
    function xt(u) {
      return Mr(u);
    }
    function Ar(u) {
      {
        var b = typeof Symbol == "function" && Symbol.toStringTag, w = b && u[Symbol.toStringTag] || u.constructor.name || "Object";
        return w;
      }
    }
    function zr(u) {
      try {
        return en(u), !1;
      } catch {
        return !0;
      }
    }
    function en(u) {
      return "" + u;
    }
    function tn(u) {
      if (zr(u))
        return R("The provided key is an unsupported type %s. This value must be coerced to a string before before using it here.", Ar(u)), en(u);
    }
    var He = j.ReactCurrentOwner, Dr = {
      key: !0,
      ref: !0,
      __self: !0,
      __source: !0
    }, nn, rn, jt;
    jt = {};
    function $r(u) {
      if (nt.call(u, "ref")) {
        var b = Object.getOwnPropertyDescriptor(u, "ref").get;
        if (b && b.isReactWarning)
          return !1;
      }
      return u.ref !== void 0;
    }
    function Br(u) {
      if (nt.call(u, "key")) {
        var b = Object.getOwnPropertyDescriptor(u, "key").get;
        if (b && b.isReactWarning)
          return !1;
      }
      return u.key !== void 0;
    }
    function Vr(u, b) {
      if (typeof u.ref == "string" && He.current && b && He.current.stateNode !== b) {
        var w = le(He.current.type);
        jt[w] || (R('Component "%s" contains the string ref "%s". Support for string refs will be removed in a future major release. This case cannot be automatically converted to an arrow function. We ask you to manually fix this case by using useRef() or createRef() instead. Learn more about using refs safely here: https://reactjs.org/link/strict-mode-string-ref', le(He.current.type), u.ref), jt[w] = !0);
      }
    }
    function qr(u, b) {
      {
        var w = function() {
          nn || (nn = !0, R("%s: `key` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", b));
        };
        w.isReactWarning = !0, Object.defineProperty(u, "key", {
          get: w,
          configurable: !0
        });
      }
    }
    function Wr(u, b) {
      {
        var w = function() {
          rn || (rn = !0, R("%s: `ref` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", b));
        };
        w.isReactWarning = !0, Object.defineProperty(u, "ref", {
          get: w,
          configurable: !0
        });
      }
    }
    var Ur = function(u, b, w, O, U, Z, q) {
      var D = {
        // This tag allows us to uniquely identify this as a React Element
        $$typeof: r,
        // Built-in properties that belong on the element
        type: u,
        key: b,
        ref: w,
        props: q,
        // Record the component responsible for creating this element.
        _owner: Z
      };
      return D._store = {}, Object.defineProperty(D._store, "validated", {
        configurable: !1,
        enumerable: !1,
        writable: !0,
        value: !1
      }), Object.defineProperty(D, "_self", {
        configurable: !1,
        enumerable: !1,
        writable: !1,
        value: O
      }), Object.defineProperty(D, "_source", {
        configurable: !1,
        enumerable: !1,
        writable: !1,
        value: U
      }), Object.freeze && (Object.freeze(D.props), Object.freeze(D)), D;
    };
    function Hr(u, b, w, O, U) {
      {
        var Z, q = {}, D = null, ae = null;
        w !== void 0 && (tn(w), D = "" + w), Br(b) && (tn(b.key), D = "" + b.key), $r(b) && (ae = b.ref, Vr(b, U));
        for (Z in b)
          nt.call(b, Z) && !Dr.hasOwnProperty(Z) && (q[Z] = b[Z]);
        if (u && u.defaultProps) {
          var X = u.defaultProps;
          for (Z in X)
            q[Z] === void 0 && (q[Z] = X[Z]);
        }
        if (D || ae) {
          var ee = typeof u == "function" ? u.displayName || u.name || "Unknown" : u;
          D && qr(q, ee), ae && Wr(q, ee);
        }
        return Ur(u, D, ae, U, O, He.current, q);
      }
    }
    var Ct = j.ReactCurrentOwner, an = j.ReactDebugCurrentFrame;
    function De(u) {
      if (u) {
        var b = u._owner, w = Ie(u.type, u._source, b ? b.type : null);
        an.setExtraStackFrame(w);
      } else
        an.setExtraStackFrame(null);
    }
    var wt;
    wt = !1;
    function Et(u) {
      return typeof u == "object" && u !== null && u.$$typeof === r;
    }
    function on() {
      {
        if (Ct.current) {
          var u = le(Ct.current.type);
          if (u)
            return `

Check the render method of \`` + u + "`.";
        }
        return "";
      }
    }
    function Kr(u) {
      {
        if (u !== void 0) {
          var b = u.fileName.replace(/^.*[\\\/]/, ""), w = u.lineNumber;
          return `

Check your code at ` + b + ":" + w + ".";
        }
        return "";
      }
    }
    var sn = {};
    function Yr(u) {
      {
        var b = on();
        if (!b) {
          var w = typeof u == "string" ? u : u.displayName || u.name;
          w && (b = `

Check the top-level render call using <` + w + ">.");
        }
        return b;
      }
    }
    function ln(u, b) {
      {
        if (!u._store || u._store.validated || u.key != null)
          return;
        u._store.validated = !0;
        var w = Yr(b);
        if (sn[w])
          return;
        sn[w] = !0;
        var O = "";
        u && u._owner && u._owner !== Ct.current && (O = " It was passed a child from " + le(u._owner.type) + "."), De(u), R('Each child in a list should have a unique "key" prop.%s%s See https://reactjs.org/link/warning-keys for more information.', w, O), De(null);
      }
    }
    function cn(u, b) {
      {
        if (typeof u != "object")
          return;
        if (xt(u))
          for (var w = 0; w < u.length; w++) {
            var O = u[w];
            Et(O) && ln(O, b);
          }
        else if (Et(u))
          u._store && (u._store.validated = !0);
        else if (u) {
          var U = y(u);
          if (typeof U == "function" && U !== u.entries)
            for (var Z = U.call(u), q; !(q = Z.next()).done; )
              Et(q.value) && ln(q.value, b);
        }
      }
    }
    function Jr(u) {
      {
        var b = u.type;
        if (b == null || typeof b == "string")
          return;
        var w;
        if (typeof b == "function")
          w = b.propTypes;
        else if (typeof b == "object" && (b.$$typeof === d || // Note: Memo only checks outer props here.
        // Inner props are checked in the reconciler.
        b.$$typeof === h))
          w = b.propTypes;
        else
          return;
        if (w) {
          var O = le(b);
          Lr(w, u.props, "prop", O, u);
        } else if (b.PropTypes !== void 0 && !wt) {
          wt = !0;
          var U = le(b);
          R("Component %s declared `PropTypes` instead of `propTypes`. Did you misspell the property assignment?", U || "Unknown");
        }
        typeof b.getDefaultProps == "function" && !b.getDefaultProps.isReactClassApproved && R("getDefaultProps is only used on classic React.createClass definitions. Use a static property named `defaultProps` instead.");
      }
    }
    function Zr(u) {
      {
        for (var b = Object.keys(u.props), w = 0; w < b.length; w++) {
          var O = b[w];
          if (O !== "children" && O !== "key") {
            De(u), R("Invalid prop `%s` supplied to `React.Fragment`. React.Fragment can only have `key` and `children` props.", O), De(null);
            break;
          }
        }
        u.ref !== null && (De(u), R("Invalid attribute `ref` supplied to `React.Fragment`."), De(null));
      }
    }
    function un(u, b, w, O, U, Z) {
      {
        var q = Ue(u);
        if (!q) {
          var D = "";
          (u === void 0 || typeof u == "object" && u !== null && Object.keys(u).length === 0) && (D += " You likely forgot to export your component from the file it's defined in, or you might have mixed up default and named imports.");
          var ae = Kr(U);
          ae ? D += ae : D += on();
          var X;
          u === null ? X = "null" : xt(u) ? X = "array" : u !== void 0 && u.$$typeof === r ? (X = "<" + (le(u.type) || "Unknown") + " />", D = " Did you accidentally export a JSX literal instead of a component?") : X = typeof u, R("React.jsx: type is invalid -- expected a string (for built-in components) or a class/function (for composite components) but got: %s.%s", X, D);
        }
        var ee = Hr(u, b, w, U, Z);
        if (ee == null)
          return ee;
        if (q) {
          var pe = b.children;
          if (pe !== void 0)
            if (O)
              if (xt(pe)) {
                for (var $e = 0; $e < pe.length; $e++)
                  cn(pe[$e], u);
                Object.freeze && Object.freeze(pe);
              } else
                R("React.jsx: Static children should always be an array. You are likely explicitly calling React.jsxs or React.jsxDEV. Use the Babel transform instead.");
            else
              cn(pe, u);
        }
        return u === o ? Zr(ee) : Jr(ee), ee;
      }
    }
    function Gr(u, b, w) {
      return un(u, b, w, !0);
    }
    function Xr(u, b, w) {
      return un(u, b, w, !1);
    }
    var Qr = Xr, ea = Gr;
    Ye.Fragment = o, Ye.jsx = Qr, Ye.jsxs = ea;
  }()), Ye;
}
process.env.NODE_ENV === "production" ? At.exports = ya() : At.exports = ba();
var a = At.exports, Ge = {}, Je = ua;
if (process.env.NODE_ENV === "production")
  Ge.createRoot = Je.createRoot, Ge.hydrateRoot = Je.hydrateRoot;
else {
  var at = Je.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED;
  Ge.createRoot = function(e, r) {
    at.usingClientEntryPoint = !0;
    try {
      return Je.createRoot(e, r);
    } finally {
      at.usingClientEntryPoint = !1;
    }
  }, Ge.hydrateRoot = function(e, r, t) {
    at.usingClientEntryPoint = !0;
    try {
      return Je.hydrateRoot(e, r, t);
    } finally {
      at.usingClientEntryPoint = !1;
    }
  };
}
const xa = ({ shape: e }) => {
  const [r, t] = ie(!1), o = ft(null);
  ye(() => {
    o.current && o.current.focus({
      cursor: "end"
    });
  });
  const i = () => {
    o.current.input.value !== "" && (e.text = o.current.input.value, t(!1));
  }, s = (d) => {
    e.toolMenus.find((p) => p.key === d.key).action(t);
  }, l = () => r ? /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(k, { initialValues: { title: e.text }, children: /* @__PURE__ */ a.jsx(k.Item, { name: "title", rules: [{ required: !0, message: "请输入名称" }], children: /* @__PURE__ */ a.jsx(
    be,
    {
      onBlur: (d) => i(),
      ref: o,
      placeholder: "请输入名称",
      style: { height: "24px", borderColor: e.focusBorderColor }
    }
  ) }) }) }) : /* @__PURE__ */ a.jsx("p", { style: { margin: 0 }, children: /* @__PURE__ */ a.jsx("span", { children: e.text }) }), n = () => {
    if (e.toolMenus && e.toolMenus.length > 0) {
      const d = e.toolMenus.map((c) => ({ key: c.key, label: c.label }));
      return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx("div", { children: /* @__PURE__ */ a.jsx(da, { menu: { items: d, onClick: (c) => s(c) }, placement: "bottomRight", children: /* @__PURE__ */ a.jsx(se, { type: "text", size: "small", style: {
        margin: 0,
        padding: 0,
        width: "28px",
        height: "28px",
        display: "flex",
        alignItems: "center",
        justifyContent: "center"
      }, children: /* @__PURE__ */ a.jsx(
        "svg",
        {
          xmlns: "http://www.w3.org/2000/svg",
          width: "16",
          height: "16",
          fill: "none",
          viewBox: "0 0 16 16",
          children: /* @__PURE__ */ a.jsx(
            "path",
            {
              fill: "#1C1D23",
              fillOpacity: "0.8",
              d: "M3.667 7.833a1.167 1.167 0 1 1-2.334 0 1.167 1.167 0 0 1 2.334 0ZM9.15 7.833a1.167 1.167 0 1 1-2.333 0 1.167 1.167 0 0 1 2.333 0ZM14.667 7.833a1.167 1.167 0 1 1-2.334 0 1.167 1.167 0 0 1 2.334 0Z"
            }
          )
        }
      ) }) }) }) });
    }
    return /* @__PURE__ */ a.jsx(a.Fragment, {});
  };
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsxs("div", { className: "react-node-header", children: [
    /* @__PURE__ */ a.jsxs("div", { className: "react-node-toolbar", style: { alignItems: "center" }, children: [
      /* @__PURE__ */ a.jsx("div", { style: { display: "flex", alignItems: "center" }, children: e.getHeaderIcon() }),
      /* @__PURE__ */ a.jsx("div", { className: "react-node-toolbar-name", children: l() }),
      n()
    ] }),
    /* @__PURE__ */ a.jsx("span", { className: "react-node-header-description", children: e.description })
  ] }) });
}, Hn = pt(null), Kn = pt(null), Yn = pt(null), ja = ({ shape: e, component: r }) => {
  const [t, o] = ca(r.reducers, r.getJadeConfig()), i = "react-root-" + e.id;
  return e.getLatestJadeConfig = () => JSON.parse(JSON.stringify(t)), ye(() => {
    e.observe();
  }, []), ye(() => {
    e.graph.onChangeCallback && e.graph.onChangeCallback();
  }, [t]), /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsxs("div", { id: i, style: { display: "block" }, children: [
    /* @__PURE__ */ a.jsx(xa, { shape: e }),
    /* @__PURE__ */ a.jsx(Kn.Provider, { value: e, children: /* @__PURE__ */ a.jsx(Hn.Provider, { value: t, children: /* @__PURE__ */ a.jsx(Yn.Provider, { value: o, children: /* @__PURE__ */ a.jsx("div", { className: "react-node-content", style: { borderRadius: e.borderRadius + "px" }, children: r.getReactComponents() }) }) }) })
  ] }) });
};
function G() {
  return mt(Hn);
}
function te() {
  return mt(Kn);
}
function Q() {
  return mt(Yn);
}
const ge = (e, r, t, o, i, s, l) => {
  const n = oa(e, r, t, o, i, s, !1, l || wa);
  n.type = "jadeNode", n.serializedFields.batchAdd("toolConfigs", "componentName", "flowMeta"), n.eventType = "jadeEvent", n.hideText = !0, n.autoHeight = !0, n.width = 360, n.borderColor = "rgba(28,31,35,.08)", n.mouseInBorderColor = "rgba(28,31,35,.08)", n.shadow = "0 2px 4px 0 rgba(0,0,0,.1)", n.focusShadow = "0 0 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)", n.borderWidth = 1, n.focusBorderWidth = 2, n.dashWidth = 0, n.backColor = "white", n.focusBackColor = "white", n.borderRadius = 8, n.cornerRadius = 8, n.enableAnimation = !1, n.modeRegion.visible = !1, n.flowMeta = {
    triggerMode: "auto",
    jober: {
      type: "general_jober",
      name: "",
      fitables: [],
      converter: {
        type: "mapping_converter"
      }
    }
  };
  const d = [];
  n.toolMenus = [{
    key: "1",
    label: "复制",
    action: () => {
      n.duplicate();
    }
  }, {
    key: "2",
    label: "删除",
    action: () => {
      n.remove();
    }
  }, {
    key: "3",
    label: "重命名",
    action: (f) => {
      f(!0);
    }
  }];
  const c = n.initConnectors;
  n.initConnectors = () => {
    c.apply(n), n.connectors.remove((f) => f.direction.key === Be.S.key || f.direction.key === Be.N.key || f.direction.key === "ROTATE"), n.connectors.forEach((f) => {
      f.isSolid = !0, f.direction.key === Be.W.key && (f.allowFromLink = !1), f.direction.key === Be.E.key && (f.allowToLink = !1);
    });
  }, n.getPreNodeInfos = () => {
    if (!n.allowToLink)
      return [];
    const f = n.page.shapes.filter((j) => j.type === "jadeEvent"), g = [], m = /* @__PURE__ */ new Set(), y = (j) => {
      if (m.has(j))
        return;
      m.add(j);
      const R = n.page.getShapeById(j);
      if (!R)
        return;
      g.push({
        id: R.id,
        node: R,
        name: R.text,
        observableList: R.page.getObservableList(R.id)
      });
      const $ = f.filter((B) => B.toShape === j);
      for (const B of $)
        y(B.fromShape);
    };
    return y(n.id), g.shift(), g;
  }, n.observe = () => {
    n.drawer.observe();
  }, n.getComponent = () => n.graph.plugins[n.componentName](n.flowMeta.jober.converter.entity), n.emit = (f, g) => {
    const m = n.page.getObservable(n.id, f);
    m && (m.observers.forEach((y) => {
      y.status === "enable" && y.observe(g || m.value);
    }), g && (m.value = g));
  };
  const p = n.remove;
  n.remove = (f) => {
    const m = n.page.shapes.filter((j) => j.isTypeof("jadeEvent")).filter((j) => j.fromShape === n.id || j.toShape === n.id).flatMap((j) => j.remove()), y = p.apply(n, [f]);
    return n.cleanObservables(), [...y, ...m];
  }, n.duplicate = () => {
    const f = JSON.stringify([n.serialize()]);
    ia.pasteShapes(f, "", n.page);
  };
  const h = n.serialize;
  n.serialize = () => (n.getLatestJadeConfig && n.serializerJadeConfig(), h.apply(n)), n.serializerJadeConfig = () => {
    n.flowMeta.jober.converter.entity = n.getLatestJadeConfig();
  }, n.addDetection(["componentName"], (f, g, m) => {
    g !== m && (n.drawer.unmountReact(), n.invalidateAlone());
  }), n.getHeaderIcon = () => {
  }, n.observeTo = (f, g, m) => {
    const y = n.getPreNodeInfos(), j = new Set(y.map((B) => B.id)), R = Ca(f, g, m);
    R.status = j.has(f) ? "enable" : "disable", d.push(R), n.page.observeTo(f, g, R);
    const $ = n.page.getObservable(f, g);
    return R.observe($.value), () => {
      const B = d.findIndex((E) => E === R);
      d.splice(B, 1), n.page.stopObserving(f, g, R);
    };
  }, n.offConnect = () => {
    const f = n.getPreNodeInfos(), g = new Set(f.map((y) => y.id));
    d.filter((y) => y.status === "enable").filter((y) => !g.has(y.nodeId)).forEach((y) => {
      y.observe(null), y.status = "disable";
    });
    const m = v();
    m.length > 0 && m.forEach((y) => y.offConnect());
  }, n.onConnect = () => {
    const f = n.getPreNodeInfos(), g = new Set(f.map((y) => y.id));
    d.filter((y) => y.status === "disable").filter((y) => g.has(y.nodeId)).forEach((y) => {
      y.status = "enable";
      const j = n.page.getObservable(y.nodeId, y.observableId);
      y.observe(j.value);
    });
    const m = v();
    m.length > 0 && m.forEach((y) => y.onConnect());
  }, n.cleanObservables = () => {
    n.page.removeObservable(n.id), d.forEach((f) => n.page.stopObserving(f.nodeId, f.observableId, f));
  };
  const v = () => {
    const f = n.page.shapes.filter((g) => g.type === "jadeEvent").filter((g) => g.fromShape === n.id);
    return !f || f.length === 0 ? [] : n.page.shapes.filter((g) => g.type !== "jadeEvent").filter((g) => f.some((m) => m.toShape === g.id));
  };
  return n.validate = () => {
    const f = n.getPreNodeInfos(), g = new Set(f.map((m) => m.id));
    d.forEach((m) => {
      const y = n.page.getShapeById(m.nodeId);
      if (!y)
        throw new Error("节点[" + m.nodeId + "]不存在.");
      if (!g.has(m.nodeId))
        throw new Error("节点[" + y.text + "]和节点[" + n.text + "]未连接.");
    });
  }, n;
}, Ca = (e, r, t) => {
  const o = {};
  return o.nodeId = e, o.observableId = r, o.status = "enable", o.origin = t, o.observe = (i) => {
    o.status === "enable" && o.origin(i);
  }, o;
}, wa = (e, r, t, o) => {
  const i = sa(e, r, t, o);
  i.reactContainer = null;
  const s = i.initialize;
  i.initialize = () => {
    s.apply(i), i.reactContainer = document.createElement("div"), i.reactContainer.id = "react-container-" + e.id, i.reactContainer.style.padding = "12px", i.reactContainer.style.width = "100%", i.reactContainer.style.borderRadius = e.borderRadius + "px", i.parent.appendChild(i.reactContainer), i.parent.style.pointerEvents = "auto";
  }, i.unmountReact = () => {
    i.root && (i.root.unmount(), i.root = null);
  }, i.drawStatic = () => {
    !e.componentName || i.root || (i.root = Ge.createRoot(i.reactContainer), i.root.render(/* @__PURE__ */ a.jsx(ja, { shape: e, component: e.getComponent() })));
  }, i.drawFocusFrame = () => {
  };
  const l = i.drawBorder;
  i.drawBorder = () => {
    l.apply(i), e.isFocused ? (i.parent.style.border = "", i.parent.style.border = e.borderWidth + "px solid " + e.borderColor, i.parent.style.outline = e.focusBorderWidth + "px solid " + e.getBorderColor()) : i.parent.style.outline = "";
  };
  const n = i.beforeRemove;
  i.beforeRemove = () => {
    n.apply(i), i.unmountReact();
  };
  let d = 0;
  return i.observe = () => {
    new ResizeObserver((c) => {
      d !== i.parent.offsetHeight && e.container !== "" && (e.resize(e.width, i.parent.offsetHeight), d = i.parent.offsetHeight);
    }).observe(i.parent);
  }, i;
};
function Ea() {
  const e = G();
  return /* @__PURE__ */ a.jsx("ul", { children: e.map((r) => /* @__PURE__ */ a.jsx("li", { children: /* @__PURE__ */ a.jsx(Sa, { task: r }) }, r.id)) });
}
function Sa({ task: e }) {
  const [r, t] = ie(!1), o = Q();
  let i;
  return r ? i = /* @__PURE__ */ a.jsxs(a.Fragment, { children: [
    /* @__PURE__ */ a.jsx(
      "input",
      {
        value: e.text,
        onChange: (s) => {
          o({
            type: "changed",
            task: {
              ...e,
              text: s.target.value
            }
          });
        }
      }
    ),
    /* @__PURE__ */ a.jsx("button", { onClick: () => t(!1), children: "Save" })
  ] }) : i = /* @__PURE__ */ a.jsxs(a.Fragment, { children: [
    e.text,
    /* @__PURE__ */ a.jsx("button", { onClick: () => t(!0), children: "Edit" })
  ] }), /* @__PURE__ */ a.jsxs("label", { children: [
    /* @__PURE__ */ a.jsx(
      "input",
      {
        type: "checkbox",
        checked: e.done,
        onChange: (s) => {
          o({
            type: "changed",
            task: {
              ...e,
              done: s.target.checked
            }
          });
        }
      }
    ),
    i,
    /* @__PURE__ */ a.jsx("button", { onClick: () => {
      o({
        type: "deleted",
        id: e.id
      });
    }, children: "Delete" })
  ] });
}
function Pa() {
  const [e, r] = ie(""), t = Q(), o = te();
  return /* @__PURE__ */ a.jsxs(a.Fragment, { children: [
    /* @__PURE__ */ a.jsx(
      "input",
      {
        placeholder: "Add task",
        value: e,
        onChange: (i) => r(i.target.value)
      }
    ),
    /* @__PURE__ */ a.jsx("button", { onClick: () => {
      r(""), console.log(o.serialize()), t({
        type: "added",
        id: Ra++,
        text: e
      });
    }, children: "Add" })
  ] });
}
let Ra = 3;
var Oa = /* @__PURE__ */ pt({});
const Jn = Oa;
function ke() {
  return ke = Object.assign ? Object.assign.bind() : function(e) {
    for (var r = 1; r < arguments.length; r++) {
      var t = arguments[r];
      for (var o in t)
        Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
    }
    return e;
  }, ke.apply(this, arguments);
}
function ka(e) {
  if (Array.isArray(e))
    return e;
}
function Na(e, r) {
  var t = e == null ? null : typeof Symbol < "u" && e[Symbol.iterator] || e["@@iterator"];
  if (t != null) {
    var o, i, s, l, n = [], d = !0, c = !1;
    try {
      if (s = (t = t.call(e)).next, r === 0) {
        if (Object(t) !== t)
          return;
        d = !1;
      } else
        for (; !(d = (o = s.call(t)).done) && (n.push(o.value), n.length !== r); d = !0)
          ;
    } catch (p) {
      c = !0, i = p;
    } finally {
      try {
        if (!d && t.return != null && (l = t.return(), Object(l) !== l))
          return;
      } finally {
        if (c)
          throw i;
      }
    }
    return n;
  }
}
function hn(e, r) {
  (r == null || r > e.length) && (r = e.length);
  for (var t = 0, o = new Array(r); t < r; t++)
    o[t] = e[t];
  return o;
}
function Ia(e, r) {
  if (e) {
    if (typeof e == "string")
      return hn(e, r);
    var t = Object.prototype.toString.call(e).slice(8, -1);
    if (t === "Object" && e.constructor && (t = e.constructor.name), t === "Map" || t === "Set")
      return Array.from(e);
    if (t === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(t))
      return hn(e, r);
  }
}
function Ta() {
  throw new TypeError(`Invalid attempt to destructure non-iterable instance.
In order to be iterable, non-array objects must have a [Symbol.iterator]() method.`);
}
function Zn(e, r) {
  return ka(e) || Na(e, r) || Ia(e, r) || Ta();
}
function Fe(e) {
  "@babel/helpers - typeof";
  return Fe = typeof Symbol == "function" && typeof Symbol.iterator == "symbol" ? function(r) {
    return typeof r;
  } : function(r) {
    return r && typeof Symbol == "function" && r.constructor === Symbol && r !== Symbol.prototype ? "symbol" : typeof r;
  }, Fe(e);
}
function Fa(e, r) {
  if (Fe(e) != "object" || !e)
    return e;
  var t = e[Symbol.toPrimitive];
  if (t !== void 0) {
    var o = t.call(e, r || "default");
    if (Fe(o) != "object")
      return o;
    throw new TypeError("@@toPrimitive must return a primitive value.");
  }
  return (r === "string" ? String : Number)(e);
}
function _a(e) {
  var r = Fa(e, "string");
  return Fe(r) == "symbol" ? r : r + "";
}
function zt(e, r, t) {
  return r = _a(r), r in e ? Object.defineProperty(e, r, {
    value: t,
    enumerable: !0,
    configurable: !0,
    writable: !0
  }) : e[r] = t, e;
}
function La(e, r) {
  if (e == null)
    return {};
  var t = {}, o = Object.keys(e), i, s;
  for (s = 0; s < o.length; s++)
    i = o[s], !(r.indexOf(i) >= 0) && (t[i] = e[i]);
  return t;
}
function Gn(e, r) {
  if (e == null)
    return {};
  var t = La(e, r), o, i;
  if (Object.getOwnPropertySymbols) {
    var s = Object.getOwnPropertySymbols(e);
    for (i = 0; i < s.length; i++)
      o = s[i], !(r.indexOf(o) >= 0) && Object.prototype.propertyIsEnumerable.call(e, o) && (t[o] = e[o]);
  }
  return t;
}
var Xn = { exports: {} };
/*!
	Copyright (c) 2018 Jed Watson.
	Licensed under the MIT License (MIT), see
	http://jedwatson.github.io/classnames
*/
(function(e) {
  (function() {
    var r = {}.hasOwnProperty;
    function t() {
      for (var s = "", l = 0; l < arguments.length; l++) {
        var n = arguments[l];
        n && (s = i(s, o(n)));
      }
      return s;
    }
    function o(s) {
      if (typeof s == "string" || typeof s == "number")
        return s;
      if (typeof s != "object")
        return "";
      if (Array.isArray(s))
        return t.apply(null, s);
      if (s.toString !== Object.prototype.toString && !s.toString.toString().includes("[native code]"))
        return s.toString();
      var l = "";
      for (var n in s)
        r.call(s, n) && s[n] && (l = i(l, n));
      return l;
    }
    function i(s, l) {
      return l ? s ? s + " " + l : s + l : s;
    }
    e.exports ? (t.default = t, e.exports = t) : window.classNames = t;
  })();
})(Xn);
var Ma = Xn.exports;
const Aa = /* @__PURE__ */ Un(Ma);
function me(e, r) {
  za(e) && (e = "100%");
  var t = Da(e);
  return e = r === 360 ? e : Math.min(r, Math.max(0, parseFloat(e))), t && (e = parseInt(String(e * r), 10) / 100), Math.abs(e - r) < 1e-6 ? 1 : (r === 360 ? e = (e < 0 ? e % r + r : e % r) / parseFloat(String(r)) : e = e % r / parseFloat(String(r)), e);
}
function za(e) {
  return typeof e == "string" && e.indexOf(".") !== -1 && parseFloat(e) === 1;
}
function Da(e) {
  return typeof e == "string" && e.indexOf("%") !== -1;
}
function $a(e) {
  return e = parseFloat(e), (isNaN(e) || e < 0 || e > 1) && (e = 1), e;
}
function ot(e) {
  return e <= 1 ? "".concat(Number(e) * 100, "%") : e;
}
function St(e) {
  return e.length === 1 ? "0" + e : String(e);
}
function Ba(e, r, t) {
  return {
    r: me(e, 255) * 255,
    g: me(r, 255) * 255,
    b: me(t, 255) * 255
  };
}
function Pt(e, r, t) {
  return t < 0 && (t += 1), t > 1 && (t -= 1), t < 1 / 6 ? e + (r - e) * (6 * t) : t < 1 / 2 ? r : t < 2 / 3 ? e + (r - e) * (2 / 3 - t) * 6 : e;
}
function Va(e, r, t) {
  var o, i, s;
  if (e = me(e, 360), r = me(r, 100), t = me(t, 100), r === 0)
    i = t, s = t, o = t;
  else {
    var l = t < 0.5 ? t * (1 + r) : t + r - t * r, n = 2 * t - l;
    o = Pt(n, l, e + 1 / 3), i = Pt(n, l, e), s = Pt(n, l, e - 1 / 3);
  }
  return { r: o * 255, g: i * 255, b: s * 255 };
}
function qa(e, r, t) {
  e = me(e, 255), r = me(r, 255), t = me(t, 255);
  var o = Math.max(e, r, t), i = Math.min(e, r, t), s = 0, l = o, n = o - i, d = o === 0 ? 0 : n / o;
  if (o === i)
    s = 0;
  else {
    switch (o) {
      case e:
        s = (r - t) / n + (r < t ? 6 : 0);
        break;
      case r:
        s = (t - e) / n + 2;
        break;
      case t:
        s = (e - r) / n + 4;
        break;
    }
    s /= 6;
  }
  return { h: s, s: d, v: l };
}
function Wa(e, r, t) {
  e = me(e, 360) * 6, r = me(r, 100), t = me(t, 100);
  var o = Math.floor(e), i = e - o, s = t * (1 - r), l = t * (1 - i * r), n = t * (1 - (1 - i) * r), d = o % 6, c = [t, l, s, s, n, t][d], p = [n, t, t, l, s, s][d], h = [s, s, n, t, t, l][d];
  return { r: c * 255, g: p * 255, b: h * 255 };
}
function Ua(e, r, t, o) {
  var i = [
    St(Math.round(e).toString(16)),
    St(Math.round(r).toString(16)),
    St(Math.round(t).toString(16))
  ];
  return o && i[0].startsWith(i[0].charAt(1)) && i[1].startsWith(i[1].charAt(1)) && i[2].startsWith(i[2].charAt(1)) ? i[0].charAt(0) + i[1].charAt(0) + i[2].charAt(0) : i.join("");
}
function gn(e) {
  return ue(e) / 255;
}
function ue(e) {
  return parseInt(e, 16);
}
var vn = {
  aliceblue: "#f0f8ff",
  antiquewhite: "#faebd7",
  aqua: "#00ffff",
  aquamarine: "#7fffd4",
  azure: "#f0ffff",
  beige: "#f5f5dc",
  bisque: "#ffe4c4",
  black: "#000000",
  blanchedalmond: "#ffebcd",
  blue: "#0000ff",
  blueviolet: "#8a2be2",
  brown: "#a52a2a",
  burlywood: "#deb887",
  cadetblue: "#5f9ea0",
  chartreuse: "#7fff00",
  chocolate: "#d2691e",
  coral: "#ff7f50",
  cornflowerblue: "#6495ed",
  cornsilk: "#fff8dc",
  crimson: "#dc143c",
  cyan: "#00ffff",
  darkblue: "#00008b",
  darkcyan: "#008b8b",
  darkgoldenrod: "#b8860b",
  darkgray: "#a9a9a9",
  darkgreen: "#006400",
  darkgrey: "#a9a9a9",
  darkkhaki: "#bdb76b",
  darkmagenta: "#8b008b",
  darkolivegreen: "#556b2f",
  darkorange: "#ff8c00",
  darkorchid: "#9932cc",
  darkred: "#8b0000",
  darksalmon: "#e9967a",
  darkseagreen: "#8fbc8f",
  darkslateblue: "#483d8b",
  darkslategray: "#2f4f4f",
  darkslategrey: "#2f4f4f",
  darkturquoise: "#00ced1",
  darkviolet: "#9400d3",
  deeppink: "#ff1493",
  deepskyblue: "#00bfff",
  dimgray: "#696969",
  dimgrey: "#696969",
  dodgerblue: "#1e90ff",
  firebrick: "#b22222",
  floralwhite: "#fffaf0",
  forestgreen: "#228b22",
  fuchsia: "#ff00ff",
  gainsboro: "#dcdcdc",
  ghostwhite: "#f8f8ff",
  goldenrod: "#daa520",
  gold: "#ffd700",
  gray: "#808080",
  green: "#008000",
  greenyellow: "#adff2f",
  grey: "#808080",
  honeydew: "#f0fff0",
  hotpink: "#ff69b4",
  indianred: "#cd5c5c",
  indigo: "#4b0082",
  ivory: "#fffff0",
  khaki: "#f0e68c",
  lavenderblush: "#fff0f5",
  lavender: "#e6e6fa",
  lawngreen: "#7cfc00",
  lemonchiffon: "#fffacd",
  lightblue: "#add8e6",
  lightcoral: "#f08080",
  lightcyan: "#e0ffff",
  lightgoldenrodyellow: "#fafad2",
  lightgray: "#d3d3d3",
  lightgreen: "#90ee90",
  lightgrey: "#d3d3d3",
  lightpink: "#ffb6c1",
  lightsalmon: "#ffa07a",
  lightseagreen: "#20b2aa",
  lightskyblue: "#87cefa",
  lightslategray: "#778899",
  lightslategrey: "#778899",
  lightsteelblue: "#b0c4de",
  lightyellow: "#ffffe0",
  lime: "#00ff00",
  limegreen: "#32cd32",
  linen: "#faf0e6",
  magenta: "#ff00ff",
  maroon: "#800000",
  mediumaquamarine: "#66cdaa",
  mediumblue: "#0000cd",
  mediumorchid: "#ba55d3",
  mediumpurple: "#9370db",
  mediumseagreen: "#3cb371",
  mediumslateblue: "#7b68ee",
  mediumspringgreen: "#00fa9a",
  mediumturquoise: "#48d1cc",
  mediumvioletred: "#c71585",
  midnightblue: "#191970",
  mintcream: "#f5fffa",
  mistyrose: "#ffe4e1",
  moccasin: "#ffe4b5",
  navajowhite: "#ffdead",
  navy: "#000080",
  oldlace: "#fdf5e6",
  olive: "#808000",
  olivedrab: "#6b8e23",
  orange: "#ffa500",
  orangered: "#ff4500",
  orchid: "#da70d6",
  palegoldenrod: "#eee8aa",
  palegreen: "#98fb98",
  paleturquoise: "#afeeee",
  palevioletred: "#db7093",
  papayawhip: "#ffefd5",
  peachpuff: "#ffdab9",
  peru: "#cd853f",
  pink: "#ffc0cb",
  plum: "#dda0dd",
  powderblue: "#b0e0e6",
  purple: "#800080",
  rebeccapurple: "#663399",
  red: "#ff0000",
  rosybrown: "#bc8f8f",
  royalblue: "#4169e1",
  saddlebrown: "#8b4513",
  salmon: "#fa8072",
  sandybrown: "#f4a460",
  seagreen: "#2e8b57",
  seashell: "#fff5ee",
  sienna: "#a0522d",
  silver: "#c0c0c0",
  skyblue: "#87ceeb",
  slateblue: "#6a5acd",
  slategray: "#708090",
  slategrey: "#708090",
  snow: "#fffafa",
  springgreen: "#00ff7f",
  steelblue: "#4682b4",
  tan: "#d2b48c",
  teal: "#008080",
  thistle: "#d8bfd8",
  tomato: "#ff6347",
  turquoise: "#40e0d0",
  violet: "#ee82ee",
  wheat: "#f5deb3",
  white: "#ffffff",
  whitesmoke: "#f5f5f5",
  yellow: "#ffff00",
  yellowgreen: "#9acd32"
};
function Ze(e) {
  var r = { r: 0, g: 0, b: 0 }, t = 1, o = null, i = null, s = null, l = !1, n = !1;
  return typeof e == "string" && (e = Ya(e)), typeof e == "object" && (Re(e.r) && Re(e.g) && Re(e.b) ? (r = Ba(e.r, e.g, e.b), l = !0, n = String(e.r).substr(-1) === "%" ? "prgb" : "rgb") : Re(e.h) && Re(e.s) && Re(e.v) ? (o = ot(e.s), i = ot(e.v), r = Wa(e.h, o, i), l = !0, n = "hsv") : Re(e.h) && Re(e.s) && Re(e.l) && (o = ot(e.s), s = ot(e.l), r = Va(e.h, o, s), l = !0, n = "hsl"), Object.prototype.hasOwnProperty.call(e, "a") && (t = e.a)), t = $a(t), {
    ok: l,
    format: e.format || n,
    r: Math.min(255, Math.max(r.r, 0)),
    g: Math.min(255, Math.max(r.g, 0)),
    b: Math.min(255, Math.max(r.b, 0)),
    a: t
  };
}
var Ha = "[-\\+]?\\d+%?", Ka = "[-\\+]?\\d*\\.\\d+%?", Oe = "(?:".concat(Ka, ")|(?:").concat(Ha, ")"), Rt = "[\\s|\\(]+(".concat(Oe, ")[,|\\s]+(").concat(Oe, ")[,|\\s]+(").concat(Oe, ")\\s*\\)?"), Ot = "[\\s|\\(]+(".concat(Oe, ")[,|\\s]+(").concat(Oe, ")[,|\\s]+(").concat(Oe, ")[,|\\s]+(").concat(Oe, ")\\s*\\)?"), ve = {
  CSS_UNIT: new RegExp(Oe),
  rgb: new RegExp("rgb" + Rt),
  rgba: new RegExp("rgba" + Ot),
  hsl: new RegExp("hsl" + Rt),
  hsla: new RegExp("hsla" + Ot),
  hsv: new RegExp("hsv" + Rt),
  hsva: new RegExp("hsva" + Ot),
  hex3: /^#?([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})$/,
  hex6: /^#?([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})$/,
  hex4: /^#?([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})$/,
  hex8: /^#?([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})$/
};
function Ya(e) {
  if (e = e.trim().toLowerCase(), e.length === 0)
    return !1;
  var r = !1;
  if (vn[e])
    e = vn[e], r = !0;
  else if (e === "transparent")
    return { r: 0, g: 0, b: 0, a: 0, format: "name" };
  var t = ve.rgb.exec(e);
  return t ? { r: t[1], g: t[2], b: t[3] } : (t = ve.rgba.exec(e), t ? { r: t[1], g: t[2], b: t[3], a: t[4] } : (t = ve.hsl.exec(e), t ? { h: t[1], s: t[2], l: t[3] } : (t = ve.hsla.exec(e), t ? { h: t[1], s: t[2], l: t[3], a: t[4] } : (t = ve.hsv.exec(e), t ? { h: t[1], s: t[2], v: t[3] } : (t = ve.hsva.exec(e), t ? { h: t[1], s: t[2], v: t[3], a: t[4] } : (t = ve.hex8.exec(e), t ? {
    r: ue(t[1]),
    g: ue(t[2]),
    b: ue(t[3]),
    a: gn(t[4]),
    format: r ? "name" : "hex8"
  } : (t = ve.hex6.exec(e), t ? {
    r: ue(t[1]),
    g: ue(t[2]),
    b: ue(t[3]),
    format: r ? "name" : "hex"
  } : (t = ve.hex4.exec(e), t ? {
    r: ue(t[1] + t[1]),
    g: ue(t[2] + t[2]),
    b: ue(t[3] + t[3]),
    a: gn(t[4] + t[4]),
    format: r ? "name" : "hex8"
  } : (t = ve.hex3.exec(e), t ? {
    r: ue(t[1] + t[1]),
    g: ue(t[2] + t[2]),
    b: ue(t[3] + t[3]),
    format: r ? "name" : "hex"
  } : !1)))))))));
}
function Re(e) {
  return !!ve.CSS_UNIT.exec(String(e));
}
var it = 2, yn = 0.16, Ja = 0.05, Za = 0.05, Ga = 0.15, Qn = 5, er = 4, Xa = [{
  index: 7,
  opacity: 0.15
}, {
  index: 6,
  opacity: 0.25
}, {
  index: 5,
  opacity: 0.3
}, {
  index: 5,
  opacity: 0.45
}, {
  index: 5,
  opacity: 0.65
}, {
  index: 5,
  opacity: 0.85
}, {
  index: 4,
  opacity: 0.9
}, {
  index: 3,
  opacity: 0.95
}, {
  index: 2,
  opacity: 0.97
}, {
  index: 1,
  opacity: 0.98
}];
function bn(e) {
  var r = e.r, t = e.g, o = e.b, i = qa(r, t, o);
  return {
    h: i.h * 360,
    s: i.s,
    v: i.v
  };
}
function st(e) {
  var r = e.r, t = e.g, o = e.b;
  return "#".concat(Ua(r, t, o, !1));
}
function Qa(e, r, t) {
  var o = t / 100, i = {
    r: (r.r - e.r) * o + e.r,
    g: (r.g - e.g) * o + e.g,
    b: (r.b - e.b) * o + e.b
  };
  return i;
}
function xn(e, r, t) {
  var o;
  return Math.round(e.h) >= 60 && Math.round(e.h) <= 240 ? o = t ? Math.round(e.h) - it * r : Math.round(e.h) + it * r : o = t ? Math.round(e.h) + it * r : Math.round(e.h) - it * r, o < 0 ? o += 360 : o >= 360 && (o -= 360), o;
}
function jn(e, r, t) {
  if (e.h === 0 && e.s === 0)
    return e.s;
  var o;
  return t ? o = e.s - yn * r : r === er ? o = e.s + yn : o = e.s + Ja * r, o > 1 && (o = 1), t && r === Qn && o > 0.1 && (o = 0.1), o < 0.06 && (o = 0.06), Number(o.toFixed(2));
}
function Cn(e, r, t) {
  var o;
  return t ? o = e.v + Za * r : o = e.v - Ga * r, o > 1 && (o = 1), Number(o.toFixed(2));
}
function Dt(e) {
  for (var r = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {}, t = [], o = Ze(e), i = Qn; i > 0; i -= 1) {
    var s = bn(o), l = st(Ze({
      h: xn(s, i, !0),
      s: jn(s, i, !0),
      v: Cn(s, i, !0)
    }));
    t.push(l);
  }
  t.push(st(o));
  for (var n = 1; n <= er; n += 1) {
    var d = bn(o), c = st(Ze({
      h: xn(d, n),
      s: jn(d, n),
      v: Cn(d, n)
    }));
    t.push(c);
  }
  return r.theme === "dark" ? Xa.map(function(p) {
    var h = p.index, v = p.opacity, f = st(Qa(Ze(r.backgroundColor || "#141414"), Ze(t[h]), v * 100));
    return f;
  }) : t;
}
var kt = {
  red: "#F5222D",
  volcano: "#FA541C",
  orange: "#FA8C16",
  gold: "#FAAD14",
  yellow: "#FADB14",
  lime: "#A0D911",
  green: "#52C41A",
  cyan: "#13C2C2",
  blue: "#1677FF",
  geekblue: "#2F54EB",
  purple: "#722ED1",
  magenta: "#EB2F96",
  grey: "#666666"
}, ut = {}, Nt = {};
Object.keys(kt).forEach(function(e) {
  ut[e] = Dt(kt[e]), ut[e].primary = ut[e][5], Nt[e] = Dt(kt[e], {
    theme: "dark",
    backgroundColor: "#141414"
  }), Nt[e].primary = Nt[e][5];
});
var eo = ut.blue;
function wn(e, r) {
  var t = Object.keys(e);
  if (Object.getOwnPropertySymbols) {
    var o = Object.getOwnPropertySymbols(e);
    r && (o = o.filter(function(i) {
      return Object.getOwnPropertyDescriptor(e, i).enumerable;
    })), t.push.apply(t, o);
  }
  return t;
}
function je(e) {
  for (var r = 1; r < arguments.length; r++) {
    var t = arguments[r] != null ? arguments[r] : {};
    r % 2 ? wn(Object(t), !0).forEach(function(o) {
      zt(e, o, t[o]);
    }) : Object.getOwnPropertyDescriptors ? Object.defineProperties(e, Object.getOwnPropertyDescriptors(t)) : wn(Object(t)).forEach(function(o) {
      Object.defineProperty(e, o, Object.getOwnPropertyDescriptor(t, o));
    });
  }
  return e;
}
function to() {
  return !!(typeof window < "u" && window.document && window.document.createElement);
}
function no(e, r) {
  if (!e)
    return !1;
  if (e.contains)
    return e.contains(r);
  for (var t = r; t; ) {
    if (t === e)
      return !0;
    t = t.parentNode;
  }
  return !1;
}
var En = "data-rc-order", Sn = "data-rc-priority", ro = "rc-util-key", $t = /* @__PURE__ */ new Map();
function tr() {
  var e = arguments.length > 0 && arguments[0] !== void 0 ? arguments[0] : {}, r = e.mark;
  return r ? r.startsWith("data-") ? r : "data-".concat(r) : ro;
}
function Ht(e) {
  if (e.attachTo)
    return e.attachTo;
  var r = document.querySelector("head");
  return r || document.body;
}
function ao(e) {
  return e === "queue" ? "prependQueue" : e ? "prepend" : "append";
}
function Kt(e) {
  return Array.from(($t.get(e) || e).children).filter(function(r) {
    return r.tagName === "STYLE";
  });
}
function nr(e) {
  var r = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
  if (!to())
    return null;
  var t = r.csp, o = r.prepend, i = r.priority, s = i === void 0 ? 0 : i, l = ao(o), n = l === "prependQueue", d = document.createElement("style");
  d.setAttribute(En, l), n && s && d.setAttribute(Sn, "".concat(s)), t != null && t.nonce && (d.nonce = t == null ? void 0 : t.nonce), d.innerHTML = e;
  var c = Ht(r), p = c.firstChild;
  if (o) {
    if (n) {
      var h = (r.styles || Kt(c)).filter(function(v) {
        if (!["prepend", "prependQueue"].includes(v.getAttribute(En)))
          return !1;
        var f = Number(v.getAttribute(Sn) || 0);
        return s >= f;
      });
      if (h.length)
        return c.insertBefore(d, h[h.length - 1].nextSibling), d;
    }
    c.insertBefore(d, p);
  } else
    c.appendChild(d);
  return d;
}
function oo(e) {
  var r = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {}, t = Ht(r);
  return (r.styles || Kt(t)).find(function(o) {
    return o.getAttribute(tr(r)) === e;
  });
}
function io(e, r) {
  var t = $t.get(e);
  if (!t || !no(document, t)) {
    var o = nr("", r), i = o.parentNode;
    $t.set(e, i), e.removeChild(o);
  }
}
function so(e, r) {
  var t = arguments.length > 2 && arguments[2] !== void 0 ? arguments[2] : {}, o = Ht(t), i = Kt(o), s = je(je({}, t), {}, {
    styles: i
  });
  io(o, s);
  var l = oo(r, s);
  if (l) {
    var n, d;
    if ((n = s.csp) !== null && n !== void 0 && n.nonce && l.nonce !== ((d = s.csp) === null || d === void 0 ? void 0 : d.nonce)) {
      var c;
      l.nonce = (c = s.csp) === null || c === void 0 ? void 0 : c.nonce;
    }
    return l.innerHTML !== e && (l.innerHTML = e), l;
  }
  var p = nr(e, s);
  return p.setAttribute(tr(s), r), p;
}
function rr(e) {
  var r;
  return e == null || (r = e.getRootNode) === null || r === void 0 ? void 0 : r.call(e);
}
function lo(e) {
  return rr(e) instanceof ShadowRoot;
}
function co(e) {
  return lo(e) ? rr(e) : null;
}
var Bt = {}, Yt = [], uo = function(r) {
  Yt.push(r);
};
function fo(e, r) {
  if (process.env.NODE_ENV !== "production" && !e && console !== void 0) {
    var t = Yt.reduce(function(o, i) {
      return i(o ?? "", "warning");
    }, r);
    t && console.error("Warning: ".concat(t));
  }
}
function po(e, r) {
  if (process.env.NODE_ENV !== "production" && !e && console !== void 0) {
    var t = Yt.reduce(function(o, i) {
      return i(o ?? "", "note");
    }, r);
    t && console.warn("Note: ".concat(t));
  }
}
function mo() {
  Bt = {};
}
function ar(e, r, t) {
  !r && !Bt[t] && (e(!1, t), Bt[t] = !0);
}
function ht(e, r) {
  ar(fo, e, r);
}
function ho(e, r) {
  ar(po, e, r);
}
ht.preMessage = uo;
ht.resetWarned = mo;
ht.noteOnce = ho;
function go(e) {
  return e.replace(/-(.)/g, function(r, t) {
    return t.toUpperCase();
  });
}
function vo(e, r) {
  ht(e, "[@ant-design/icons] ".concat(r));
}
function Pn(e) {
  return Fe(e) === "object" && typeof e.name == "string" && typeof e.theme == "string" && (Fe(e.icon) === "object" || typeof e.icon == "function");
}
function Rn() {
  var e = arguments.length > 0 && arguments[0] !== void 0 ? arguments[0] : {};
  return Object.keys(e).reduce(function(r, t) {
    var o = e[t];
    switch (t) {
      case "class":
        r.className = o, delete r.class;
        break;
      default:
        delete r[t], r[go(t)] = o;
    }
    return r;
  }, {});
}
function Vt(e, r, t) {
  return t ? /* @__PURE__ */ dt.createElement(e.tag, je(je({
    key: r
  }, Rn(e.attrs)), t), (e.children || []).map(function(o, i) {
    return Vt(o, "".concat(r, "-").concat(e.tag, "-").concat(i));
  })) : /* @__PURE__ */ dt.createElement(e.tag, je({
    key: r
  }, Rn(e.attrs)), (e.children || []).map(function(o, i) {
    return Vt(o, "".concat(r, "-").concat(e.tag, "-").concat(i));
  }));
}
function or(e) {
  return Dt(e)[0];
}
function ir(e) {
  return e ? Array.isArray(e) ? e : [e] : [];
}
var yo = `
.anticon {
  display: inline-flex;
  alignItems: center;
  color: inherit;
  font-style: normal;
  line-height: 0;
  text-align: center;
  text-transform: none;
  vertical-align: -0.125em;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.anticon > * {
  line-height: 1;
}

.anticon svg {
  display: inline-block;
}

.anticon::before {
  display: none;
}

.anticon .anticon-icon {
  display: block;
}

.anticon[tabindex] {
  cursor: pointer;
}

.anticon-spin::before,
.anticon-spin {
  display: inline-block;
  -webkit-animation: loadingCircle 1s infinite linear;
  animation: loadingCircle 1s infinite linear;
}

@-webkit-keyframes loadingCircle {
  100% {
    -webkit-transform: rotate(360deg);
    transform: rotate(360deg);
  }
}

@keyframes loadingCircle {
  100% {
    -webkit-transform: rotate(360deg);
    transform: rotate(360deg);
  }
}
`, bo = function(r) {
  var t = mt(Jn), o = t.csp, i = t.prefixCls, s = yo;
  i && (s = s.replace(/anticon/g, i)), ye(function() {
    var l = r.current, n = co(l);
    so(s, "@ant-design-icons", {
      prepend: !0,
      csp: o,
      attachTo: n
    });
  }, []);
}, xo = ["icon", "className", "onClick", "style", "primaryColor", "secondaryColor"], Xe = {
  primaryColor: "#333",
  secondaryColor: "#E6E6E6",
  calculated: !1
};
function jo(e) {
  var r = e.primaryColor, t = e.secondaryColor;
  Xe.primaryColor = r, Xe.secondaryColor = t || or(r), Xe.calculated = !!t;
}
function Co() {
  return je({}, Xe);
}
var gt = function(r) {
  var t = r.icon, o = r.className, i = r.onClick, s = r.style, l = r.primaryColor, n = r.secondaryColor, d = Gn(r, xo), c = C.useRef(), p = Xe;
  if (l && (p = {
    primaryColor: l,
    secondaryColor: n || or(l)
  }), bo(c), vo(Pn(t), "icon should be icon definiton, but got ".concat(t)), !Pn(t))
    return null;
  var h = t;
  return h && typeof h.icon == "function" && (h = je(je({}, h), {}, {
    icon: h.icon(p.primaryColor, p.secondaryColor)
  })), Vt(h.icon, "svg-".concat(h.name), je(je({
    className: o,
    onClick: i,
    style: s,
    "data-icon": h.name,
    width: "1em",
    height: "1em",
    fill: "currentColor",
    "aria-hidden": "true"
  }, d), {}, {
    ref: c
  }));
};
gt.displayName = "IconReact";
gt.getTwoToneColors = Co;
gt.setTwoToneColors = jo;
const Jt = gt;
function sr(e) {
  var r = ir(e), t = Zn(r, 2), o = t[0], i = t[1];
  return Jt.setTwoToneColors({
    primaryColor: o,
    secondaryColor: i
  });
}
function wo() {
  var e = Jt.getTwoToneColors();
  return e.calculated ? [e.primaryColor, e.secondaryColor] : e.primaryColor;
}
var Eo = ["className", "icon", "spin", "rotate", "tabIndex", "onClick", "twoToneColor"];
sr(eo.primary);
var vt = /* @__PURE__ */ C.forwardRef(function(e, r) {
  var t = e.className, o = e.icon, i = e.spin, s = e.rotate, l = e.tabIndex, n = e.onClick, d = e.twoToneColor, c = Gn(e, Eo), p = C.useContext(Jn), h = p.prefixCls, v = h === void 0 ? "anticon" : h, f = p.rootClassName, g = Aa(f, v, zt(zt({}, "".concat(v, "-").concat(o.name), !!o.name), "".concat(v, "-spin"), !!i || o.name === "loading"), t), m = l;
  m === void 0 && n && (m = -1);
  var y = s ? {
    msTransform: "rotate(".concat(s, "deg)"),
    transform: "rotate(".concat(s, "deg)")
  } : void 0, j = ir(d), R = Zn(j, 2), $ = R[0], B = R[1];
  return /* @__PURE__ */ C.createElement("span", ke({
    role: "img",
    "aria-label": o.name
  }, c, {
    ref: r,
    tabIndex: m,
    onClick: n,
    className: g
  }), /* @__PURE__ */ C.createElement(Jt, {
    icon: o,
    primaryColor: $,
    secondaryColor: B,
    style: y
  }));
});
vt.displayName = "AntdIcon";
vt.getTwoToneColor = wo;
vt.setTwoToneColor = sr;
const Qe = vt;
var So = { icon: { tag: "svg", attrs: { viewBox: "64 64 896 896", focusable: "false" }, children: [{ tag: "path", attrs: { d: "M811.4 368.9C765.6 248 648.9 162 512.2 162S258.8 247.9 213 368.8C126.9 391.5 63.5 470.2 64 563.6 64.6 668 145.6 752.9 247.6 762c4.7.4 8.7-3.3 8.7-8v-60.4c0-4-3-7.4-7-7.9-27-3.4-52.5-15.2-72.1-34.5-24-23.5-37.2-55.1-37.2-88.6 0-28 9.1-54.4 26.2-76.4 16.7-21.4 40.2-36.9 66.1-43.7l37.9-10 13.9-36.7c8.6-22.8 20.6-44.2 35.7-63.5 14.9-19.2 32.6-36 52.4-50 41.1-28.9 89.5-44.2 140-44.2s98.9 15.3 140 44.3c19.9 14 37.5 30.8 52.4 50 15.1 19.3 27.1 40.7 35.7 63.5l13.8 36.6 37.8 10c54.2 14.4 92.1 63.7 92.1 120 0 33.6-13.2 65.1-37.2 88.6-19.5 19.2-44.9 31.1-71.9 34.5-4 .5-6.9 3.9-6.9 7.9V754c0 4.7 4.1 8.4 8.8 8 101.7-9.2 182.5-94 183.2-198.2.6-93.4-62.7-172.1-148.6-194.9z" } }, { tag: "path", attrs: { d: "M376.9 656.4c1.8-33.5 15.7-64.7 39.5-88.6 25.4-25.5 60-39.8 96-39.8 36.2 0 70.3 14.1 96 39.8 1.4 1.4 2.7 2.8 4.1 4.3l-25 19.6a8 8 0 003 14.1l98.2 24c5 1.2 9.9-2.6 9.9-7.7l.5-101.3c0-6.7-7.6-10.5-12.9-6.3L663 532.7c-36.6-42-90.4-68.6-150.5-68.6-107.4 0-195 85.1-199.4 191.7-.2 4.5 3.4 8.3 8 8.3H369c4.2-.1 7.7-3.4 7.9-7.7zM703 664h-47.9c-4.2 0-7.7 3.3-8 7.6-1.8 33.5-15.7 64.7-39.5 88.6-25.4 25.5-60 39.8-96 39.8-36.2 0-70.3-14.1-96-39.8-1.4-1.4-2.7-2.8-4.1-4.3l25-19.6a8 8 0 00-3-14.1l-98.2-24c-5-1.2-9.9 2.6-9.9 7.7l-.4 101.4c0 6.7 7.6 10.5 12.9 6.3l23.2-18.2c36.6 42 90.4 68.6 150.5 68.6 107.4 0 195-85.1 199.4-191.7.2-4.5-3.4-8.3-8-8.3z" } }] }, name: "cloud-sync", theme: "outlined" };
const Po = So;
var Ro = function(r, t) {
  return /* @__PURE__ */ C.createElement(Qe, ke({}, r, {
    ref: t,
    icon: Po
  }));
}, lr = /* @__PURE__ */ C.forwardRef(Ro);
process.env.NODE_ENV !== "production" && (lr.displayName = "CloudSyncOutlined");
const Ve = lr;
var Oo = { icon: { tag: "svg", attrs: { viewBox: "64 64 896 896", focusable: "false" }, children: [{ tag: "path", attrs: { d: "M360 184h-8c4.4 0 8-3.6 8-8v8h304v-8c0 4.4 3.6 8 8 8h-8v72h72v-80c0-35.3-28.7-64-64-64H352c-35.3 0-64 28.7-64 64v80h72v-72zm504 72H160c-17.7 0-32 14.3-32 32v32c0 4.4 3.6 8 8 8h60.4l24.7 523c1.6 34.1 29.8 61 63.9 61h454c34.2 0 62.3-26.8 63.9-61l24.7-523H888c4.4 0 8-3.6 8-8v-32c0-17.7-14.3-32-32-32zM731.3 840H292.7l-24.2-512h487l-24.2 512z" } }] }, name: "delete", theme: "outlined" };
const ko = Oo;
var No = function(r, t) {
  return /* @__PURE__ */ C.createElement(Qe, ke({}, r, {
    ref: t,
    icon: ko
  }));
}, cr = /* @__PURE__ */ C.forwardRef(No);
process.env.NODE_ENV !== "production" && (cr.displayName = "DeleteOutlined");
const Io = cr;
var To = { icon: { tag: "svg", attrs: { viewBox: "64 64 896 896", focusable: "false" }, children: [{ tag: "path", attrs: { d: "M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" } }, { tag: "path", attrs: { d: "M464 336a48 48 0 1096 0 48 48 0 10-96 0zm72 112h-48c-4.4 0-8 3.6-8 8v272c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V456c0-4.4-3.6-8-8-8z" } }] }, name: "info-circle", theme: "outlined" };
const Fo = To;
var _o = function(r, t) {
  return /* @__PURE__ */ C.createElement(Qe, ke({}, r, {
    ref: t,
    icon: Fo
  }));
}, ur = /* @__PURE__ */ C.forwardRef(_o);
process.env.NODE_ENV !== "production" && (ur.displayName = "InfoCircleOutlined");
const we = ur;
var Lo = { icon: { tag: "svg", attrs: { viewBox: "64 64 896 896", focusable: "false" }, children: [{ tag: "path", attrs: { d: "M696 480H328c-4.4 0-8 3.6-8 8v48c0 4.4 3.6 8 8 8h368c4.4 0 8-3.6 8-8v-48c0-4.4-3.6-8-8-8z" } }, { tag: "path", attrs: { d: "M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" } }] }, name: "minus-circle", theme: "outlined" };
const Mo = Lo;
var Ao = function(r, t) {
  return /* @__PURE__ */ C.createElement(Qe, ke({}, r, {
    ref: t,
    icon: Mo
  }));
}, dr = /* @__PURE__ */ C.forwardRef(Ao);
process.env.NODE_ENV !== "production" && (dr.displayName = "MinusCircleOutlined");
const fr = dr;
var zo = { icon: { tag: "svg", attrs: { viewBox: "64 64 896 896", focusable: "false" }, children: [{ tag: "path", attrs: { d: "M482 152h60q8 0 8 8v704q0 8-8 8h-60q-8 0-8-8V160q0-8 8-8z" } }, { tag: "path", attrs: { d: "M192 474h672q8 0 8 8v60q0 8-8 8H160q-8 0-8-8v-60q0-8 8-8z" } }] }, name: "plus", theme: "outlined" };
const Do = zo;
var $o = function(r, t) {
  return /* @__PURE__ */ C.createElement(Qe, ke({}, r, {
    ref: t,
    icon: Do
  }));
}, pr = /* @__PURE__ */ C.forwardRef($o);
process.env.NODE_ENV !== "production" && (pr.displayName = "PlusOutlined");
const yt = pr, Bo = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  return n.type = "taskNode", n.text = "LLM", n.width = 700, n.jadeConfig = [
    { id: 0, text: "Philosopher’s Path", done: !0 },
    { id: 1, text: "Visit the temple", done: !1 },
    { id: 2, text: "Drink matcha", done: !1 }
  ], n.getReactComponents = () => /* @__PURE__ */ a.jsxs(a.Fragment, { children: [
    /* @__PURE__ */ a.jsx("h1", { children: "Day off in Kyoto" }),
    /* @__PURE__ */ a.jsx(Pa, {}),
    /* @__PURE__ */ a.jsx(Ea, {})
  ] }), n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(Ve, {}), n.reducers = (d, c) => {
    switch (c.type) {
      case "added":
        return [...d, {
          id: c.id,
          text: c.text,
          done: !1
        }];
      case "changed":
        return d.map((p) => p.id === c.task.id ? c.task : p);
      case "deleted":
        return d.filter((p) => p.id !== c.id);
      default:
        throw Error("Unknown action: " + c.type);
    }
  }, n;
}, Vo = (e) => /* @__PURE__ */ C.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ C.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ C.createElement("defs", null, /* @__PURE__ */ C.createElement("clipPath", { id: "clip4_13285" }, /* @__PURE__ */ C.createElement("rect", { id: "\\u56FE\\u6807/16/\\u5F00\\u59CB\\uFF0C\\u8D77\\u70B9\\uFF0C\\u7AEF\\u70B9", width: 16, height: 16, transform: "matrix(-1 0 0 1 20 4)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ C.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, transform: "matrix(-1 0 0 1 24 0)", fill: "#5E7CE0", fillOpacity: 1 }), /* @__PURE__ */ C.createElement("g", { clipPath: "url(#clip4_13285)" }, /* @__PURE__ */ C.createElement("path", { id: "path", d: "M7.41 11.52L5.83 11.52L5.83 6.31L12.53 6.31C15.64 6.37 18.16 8.89 18.16 11.99C18.16 15.1 15.64 17.62 12.53 17.67L5.83 17.67L5.83 12.46L10.92 12.46L10.92 13.52L13.91 13.52L13.91 10.47L10.92 10.47L10.92 11.53L7.41 11.53L7.41 11.52Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }))), qo = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  n.type = "endNodeEnd", n.backColor = "white", n.pointerEvents = "auto", n.text = "结束", n.componentName = "endComponent", n.deletable = !1, n.flowMeta = {
    triggerMode: "auto",
    callback: {
      type: "general_callback",
      name: "通知回调",
      fitables: ["com.huawei.fit.jober.aipp.fitable.AippFlowEndCallback"],
      converter: {
        type: "mapping_converter"
      }
    }
  }, n.toolMenus = [{
    key: "1",
    label: "重命名",
    action: (c) => {
      c(!0);
    }
  }];
  const d = n.initConnectors;
  return n.initConnectors = () => {
    d.apply(n), n.connectors.remove((c) => c.direction.key === Be.E.key);
  }, n.serializerJadeConfig = () => {
    n.flowMeta.callback.converter.entity = n.getLatestJadeConfig();
  }, n.getComponent = () => n.graph.plugins[n.componentName](n.flowMeta.callback.converter.entity), n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(
    se,
    {
      disabled: !0,
      className: "jade-node-custom-header-icon",
      children: /* @__PURE__ */ a.jsx(Vo, {})
    }
  ), n;
}, et = ({ reference: e, onReferencedValueChange: r, onReferencedKeyChange: t, ...o }) => {
  const i = te(), s = ft(null), [l, n] = ie([]), d = (h) => {
    if (!h)
      return;
    s.current && s.current();
    const v = new Map(l.map((m) => [m.id, m])), f = v.get(h), g = c(v, f);
    s.current = i.observeTo(f.nId, f.value, r), t({ referenceNode: f.nId, referenceId: f.value, value: g });
  }, c = (h, v) => {
    const f = [];
    f.unshift(v.title);
    let g = v.pId;
    for (; g; ) {
      const m = h.get(g);
      if (!m || m.pId === 0)
        break;
      f.unshift(m.title), g = m.pId;
    }
    return f;
  }, p = () => {
    const v = i.getPreNodeInfos().map((f) => {
      const g = [];
      return g.push({ id: f.id, pId: 0, value: f.id, title: f.name, selectable: !1 }), f.observableList.forEach((m) => {
        m.parentId || (m.parentId = f.id);
        const y = {
          nId: f.id,
          id: m.observableId,
          pId: m.parentId,
          value: m.observableId,
          title: m.value
        };
        g.push(y);
      }), g;
    }).flatMap((f) => f);
    n(v);
  };
  return ye(() => (e.referenceNode && e.referenceId && (s.current = i.observeTo(e.referenceNode, e.referenceId, r)), () => {
    s.current && s.current();
  }), []), /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(
    fa,
    {
      ...o,
      treeDataSimpleMode: !0,
      style: { fontSize: "12px", width: "100%" },
      value: e.referenceKey,
      dropdownStyle: { maxHeight: 400, overflow: "auto", minWidth: 250 },
      placeholder: "请选择",
      onChange: d,
      treeData: l,
      onDropdownVisibleChange: p,
      treeDefaultExpandAll: !1
    }
  ) });
};
function Wo({ item: e, handleItemChange: r }) {
  const t = (o) => {
    switch (o) {
      case "Reference":
        return /* @__PURE__ */ a.jsx(
          et,
          {
            reference: e,
            onReferencedValueChange: (i) => {
              r(e.id, [{ key: "referenceKey", value: i }]);
            },
            onReferencedKeyChange: (i) => {
              r(e.id, [
                { key: "referenceNode", value: i.referenceNode },
                { key: "referenceId", value: i.referenceId },
                { key: "value", value: i.value }
              ]);
            },
            style: { fontSize: "12px" },
            placeholder: "请选择",
            onMouseDown: (i) => i.stopPropagation(),
            showSearch: !0,
            className: "value-custom jade-select",
            dropdownStyle: {
              maxHeight: 400,
              overflow: "auto"
            },
            value: e.value
          }
        );
      case "String":
        return /* @__PURE__ */ a.jsx(
          be,
          {
            className: "value-custom jade-input",
            style: { fontSize: "12px" },
            placeholder: "请输入",
            value: e.value,
            onChange: (i) => r(e.id, [{ key: "value", value: i.target.value }])
          }
        );
      default:
        return null;
    }
  };
  return /* @__PURE__ */ a.jsxs(
    de,
    {
      gutter: 16,
      children: [
        /* @__PURE__ */ a.jsx(Y, { span: 8, style: { alignItems: "center", display: "flex" }, children: /* @__PURE__ */ a.jsx("span", { className: "jade-font-size", style: { marginBottom: "8px" }, children: "finalOutput" }) }),
        /* @__PURE__ */ a.jsx(Y, { span: 6, style: { paddingRight: 0 }, children: /* @__PURE__ */ a.jsx(
          k.Item,
          {
            style: { marginBottom: "8px" },
            id: `valueSource-${e.id}`,
            initialValue: "Reference",
            children: /* @__PURE__ */ a.jsx(
              Vn,
              {
                onMouseDown: (o) => o.stopPropagation(),
                id: `valueSource-select-${e.id}`,
                className: "value-source-custom jade-select",
                style: { width: "100%" },
                onChange: (o) => {
                  let i = [{ key: "from", value: o }, { key: "value", value: "" }];
                  o === "String" && (i = [
                    { key: "from", value: o },
                    { key: "value", value: "" },
                    { key: "referenceNode", value: "" },
                    { key: "referenceId", value: "" },
                    { key: "referenceKey", value: "" }
                  ]), r(e.id, i);
                },
                options: [
                  { value: "Reference", label: "引用" },
                  { value: "String", label: "输入" }
                ],
                value: e.from
              }
            )
          }
        ) }),
        /* @__PURE__ */ a.jsx(Y, { span: 10, style: { paddingLeft: 0 }, children: /* @__PURE__ */ a.jsx(
          k.Item,
          {
            style: { marginBottom: "8px" },
            id: `value-${e.id}`,
            children: t(e.from)
          }
        ) })
      ]
    },
    `output-variable-${e.id}`
  );
}
const { Panel: Uo } = J;
function Ho() {
  const e = Q(), r = G(), t = te(), o = () => r && r.inputParams, i = (l, n) => {
    e({ type: "editOutputVariable", id: l, changes: n });
  }, s = /* @__PURE__ */ a.jsxs("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: [
    /* @__PURE__ */ a.jsx("p", { children: "这些变量将在机器人完成工作流调用后输出。" }),
    /* @__PURE__ */ a.jsx("p", { children: "在“返回变量”模式下，这些变量将由机器人汇总并回复给用户；" }),
    /* @__PURE__ */ a.jsx("p", { children: "在“直接回答”模式下，机器人将只回复配置卡时可以使用的变量" })
  ] });
  return /* @__PURE__ */ a.jsx("div", { children: /* @__PURE__ */ a.jsx(
    J,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      style: { marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%" },
      defaultActiveKey: ["Output variable"],
      children: /* @__PURE__ */ a.jsx(
        Uo,
        {
          style: { marginBottom: 8, borderRadius: "8px", width: "100%" },
          header: /* @__PURE__ */ a.jsxs(
            "div",
            {
              style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
              children: [
                /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "输出" }),
                /* @__PURE__ */ a.jsx(Ce, { content: s, children: /* @__PURE__ */ a.jsx(we, { className: "jade-panel-header-popover-content" }) })
              ]
            }
          ),
          className: "jade-panel",
          children: /* @__PURE__ */ a.jsxs(
            k,
            {
              name: `Output variable_${t.id}`,
              layout: "vertical",
              className: "jade-form",
              children: [
                /* @__PURE__ */ a.jsxs(de, { gutter: 16, children: [
                  /* @__PURE__ */ a.jsx(Y, { span: 8, children: /* @__PURE__ */ a.jsx(k.Item, { style: { marginBottom: "8px" }, children: /* @__PURE__ */ a.jsx("span", { className: "jade-font-size jade-font-color", children: "字段名称" }) }) }),
                  /* @__PURE__ */ a.jsx(Y, { span: 16, children: /* @__PURE__ */ a.jsx(k.Item, { style: { marginBottom: "8px" }, children: /* @__PURE__ */ a.jsx("span", { className: "jade-font-size jade-font-color", children: "字段值" }) }) })
                ] }),
                /* @__PURE__ */ a.jsx(
                  Wo,
                  {
                    item: o()[0],
                    handleItemChange: i
                  }
                )
              ]
            }
          )
        },
        "Output variable"
      )
    }
  ) });
}
function Ko() {
  return /* @__PURE__ */ a.jsx("div", { style: { backgroundColor: "white" }, children: /* @__PURE__ */ a.jsx(Ho, {}) });
}
let lt;
const Yo = new Uint8Array(16);
function Jo() {
  if (!lt && (lt = typeof crypto < "u" && crypto.getRandomValues && crypto.getRandomValues.bind(crypto), !lt))
    throw new Error("crypto.getRandomValues() not supported. See https://github.com/uuidjs/uuid#getrandomvalues-not-supported");
  return lt(Yo);
}
const ne = [];
for (let e = 0; e < 256; ++e)
  ne.push((e + 256).toString(16).slice(1));
function Zo(e, r = 0) {
  return ne[e[r + 0]] + ne[e[r + 1]] + ne[e[r + 2]] + ne[e[r + 3]] + "-" + ne[e[r + 4]] + ne[e[r + 5]] + "-" + ne[e[r + 6]] + ne[e[r + 7]] + "-" + ne[e[r + 8]] + ne[e[r + 9]] + "-" + ne[e[r + 10]] + ne[e[r + 11]] + ne[e[r + 12]] + ne[e[r + 13]] + ne[e[r + 14]] + ne[e[r + 15]];
}
const Go = typeof crypto < "u" && crypto.randomUUID && crypto.randomUUID.bind(crypto), On = {
  randomUUID: Go
};
function _(e, r, t) {
  if (On.randomUUID && !r && !e)
    return On.randomUUID();
  e = e || {};
  const o = e.random || (e.rng || Jo)();
  if (o[6] = o[6] & 15 | 64, o[8] = o[8] & 63 | 128, r) {
    t = t || 0;
    for (let i = 0; i < 16; ++i)
      r[t + i] = o[i];
    return r;
  }
  return Zo(o);
}
const Xo = (e) => {
  const r = {};
  return r.getJadeConfig = () => e || {
    inputParams: [{
      id: _(),
      name: "finalOutput",
      type: "String",
      from: "Reference",
      referenceNode: "",
      referenceId: "",
      referenceKey: "",
      value: []
    }],
    outputParams: [{}]
  }, r.getReactComponents = () => /* @__PURE__ */ a.jsx(Ko, {}), r.reducers = (t, o) => {
    const i = () => {
      const l = s.inputParams.find((n) => n.name === "finalOutput");
      o.changes.forEach((n) => {
        l[n.key] = n.value;
      });
    }, s = { ...t };
    switch (o.type) {
      case "editOutputVariable":
        return i(), s;
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, r;
}, Qo = (e) => /* @__PURE__ */ C.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ C.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ C.createElement("defs", null, /* @__PURE__ */ C.createElement("clipPath", { id: "clip4_13287" }, /* @__PURE__ */ C.createElement("rect", { id: "\\u56FE\\u6807/16/\\u6570\\u636E\\u68C0\\u7D22\\uFF0C\\u67E5\\u627E", width: 16, height: 16, transform: "translate(4.000000 4.000000)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ C.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#50D4AB", fillOpacity: 1 }), /* @__PURE__ */ C.createElement("g", { clipPath: "url(#clip4_13287)" }, /* @__PURE__ */ C.createElement("path", { id: "path", d: "M12.11 10.24C15.33 10.24 18.03 9.02 18.03 7.62C18.03 6.14 15.42 5.01 12.11 5.01C8.8 5.01 6.19 6.23 6.19 7.62C6.19 9.1 8.8 10.24 12.11 10.24ZM12.11 13.02L12.37 13.02C12.89 11.8 14.11 11.02 15.59 11.02C16.29 11.02 16.9 11.19 17.51 11.54C17.86 11.19 18.12 10.85 18.12 10.41L18.12 8.32C18.12 9.8 15.51 10.93 12.2 10.93C8.97 10.93 6.27 9.71 6.27 8.32L6.27 10.32C6.19 11.8 8.8 13.02 12.11 13.02ZM12.11 15.72L12.28 15.72C12.11 15.38 12.02 14.94 12.02 14.5C12.02 14.24 12.02 13.98 12.11 13.72C8.89 13.72 6.19 12.5 6.19 11.11L6.19 13.11C6.19 14.59 8.8 15.72 12.11 15.72ZM14.55 17.81C13.77 17.55 13.07 17.12 12.63 16.42L12.11 16.42C8.89 16.42 6.19 15.2 6.19 13.81L6.19 15.81C6.19 17.29 8.8 18.42 12.11 18.42C12.98 18.42 13.85 18.34 14.64 18.16C14.55 18.08 14.55 17.99 14.55 17.81ZM19.69 17.9L17.86 16.07L17.77 15.99C18.03 15.55 18.21 15.03 18.21 14.42C18.21 12.85 16.9 11.54 15.33 11.54C13.77 11.54 12.46 12.85 12.46 14.42C12.46 15.99 13.77 17.29 15.33 17.29C15.86 17.29 16.29 17.12 16.73 16.94C16.73 16.94 16.73 17.03 16.81 17.03L18.64 18.86C18.82 19.03 19.25 19.03 19.52 18.77C19.86 18.42 19.86 18.08 19.69 17.9ZM13.33 14.42C13.33 13.28 14.29 12.33 15.42 12.33C16.55 12.33 17.51 13.28 17.51 14.42C17.51 15.55 16.55 16.51 15.42 16.51C14.2 16.51 13.33 15.55 13.33 14.42Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }))), he = (e) => {
  const { onMouseDown: r, ...t } = e, o = (i) => {
    r && r(i), i.stopPropagation();
  };
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(Vn, { onMouseDown: (i) => o(i), ...t }) });
}, { Panel: ei } = J;
function mr() {
  const e = Q(), r = G(), t = te(), o = r && r.inputParams.find((n) => n.name === "query"), i = (n, d) => {
    e({ type: "editInput", id: n, changes: d });
  }, s = (n) => {
    switch (n.from) {
      case "Reference":
        return /* @__PURE__ */ a.jsx(k.Item, { children: /* @__PURE__ */ a.jsx(
          et,
          {
            reference: n,
            onReferencedValueChange: (d) => {
              i(n.id, [{ key: "referenceKey", value: d }]);
            },
            onReferencedKeyChange: (d) => {
              i(n.id, [
                { key: "referenceNode", value: d.referenceNode },
                { key: "referenceId", value: d.referenceId },
                { key: "value", value: d.value }
              ]);
            },
            style: { fontSize: "12px" },
            placeholder: "请选择",
            onMouseDown: (d) => d.stopPropagation(),
            showSearch: !0,
            className: "value-custom jade-select",
            dropdownStyle: {
              maxHeight: 400,
              overflow: "auto"
            },
            value: n.value
          }
        ) });
      case "Input":
        return /* @__PURE__ */ a.jsx(
          k.Item,
          {
            id: `input-${n.id}`,
            name: `input-${n.id}`,
            rules: [{ required: !0, message: "字段值不能为空" }, {
              pattern: /^[^\s]*$/,
              message: "禁止输入空格"
            }],
            initialValue: n.value,
            children: /* @__PURE__ */ a.jsx(
              be,
              {
                className: "value-custom jade-input",
                placeholder: "清输入",
                value: n.value,
                onChange: (d) => i(n.id, [{ key: "value", value: d.target.value }])
              }
            )
          }
        );
      default:
        return null;
    }
  }, l = /* @__PURE__ */ a.jsx("div", { className: "jade-font-size", children: /* @__PURE__ */ a.jsx("p", { children: "输入需要从知识库中匹配的关键信息" }) });
  return /* @__PURE__ */ a.jsx("div", { children: /* @__PURE__ */ a.jsx(
    J,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      style: { marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%" },
      defaultActiveKey: ["Input"],
      children: /* @__PURE__ */ a.jsx(
        ei,
        {
          style: { marginBottom: 8, borderRadius: "8px", width: "100%" },
          header: /* @__PURE__ */ a.jsxs(
            "div",
            {
              style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
              children: [
                /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "输入" }),
                /* @__PURE__ */ a.jsx(Ce, { content: l, children: /* @__PURE__ */ a.jsx(we, { className: "jade-panel-header-popover-content" }) })
              ]
            }
          ),
          className: "jade-panel",
          children: /* @__PURE__ */ a.jsxs(
            k,
            {
              name: `inputForm-${t.id}`,
              layout: "vertical",
              className: "jade-form",
              children: [
                /* @__PURE__ */ a.jsxs(de, { children: [
                  /* @__PURE__ */ a.jsx(Y, { span: 8, children: /* @__PURE__ */ a.jsx(k.Item, { style: { marginBottom: "8px" }, children: /* @__PURE__ */ a.jsx("span", { className: "jade-font-size jade-font-color", children: "字段名称" }) }) }),
                  /* @__PURE__ */ a.jsx(Y, { span: 16, children: /* @__PURE__ */ a.jsx(k.Item, { style: { marginBottom: "8px" }, children: /* @__PURE__ */ a.jsx("span", { className: "jade-font-size jade-font-color", children: "字段值" }) }) })
                ] }),
                /* @__PURE__ */ a.jsxs(de, { className: "jade-row", children: [
                  /* @__PURE__ */ a.jsx(Y, { span: 8, children: /* @__PURE__ */ a.jsx("span", { className: "starred-text", children: "query" }) }),
                  /* @__PURE__ */ a.jsx(Y, { span: 8, style: { paddingRight: 0 }, children: /* @__PURE__ */ a.jsx(
                    k.Item,
                    {
                      id: "valueSource",
                      initialValue: "Reference",
                      children: /* @__PURE__ */ a.jsx(
                        he,
                        {
                          id: `valueSource-select-${o.id}`,
                          className: "value-source-custom jade-select",
                          style: { width: "100%" },
                          onMouseDown: (n) => n.stopPropagation(),
                          onChange: (n) => {
                            let d = [{ key: "from", value: n }, { key: "value", value: "" }];
                            n === "Input" && (d = [
                              { key: "from", value: n },
                              { key: "value", value: "" },
                              { key: "referenceNode", value: "" },
                              { key: "referenceId", value: "" },
                              { key: "referenceKey", value: "" }
                            ]), i(o.id, d);
                          },
                          options: [
                            { value: "Reference", label: "引用" },
                            { value: "Input", label: "输入" }
                          ],
                          value: o.from
                        }
                      )
                    }
                  ) }),
                  /* @__PURE__ */ a.jsx(Y, { span: 8, style: { paddingLeft: 0 }, children: /* @__PURE__ */ a.jsxs(
                    k.Item,
                    {
                      id: `value-${o.id}`,
                      rules: [{ required: !0, message: "参数不能为空!" }],
                      children: [
                        s(o),
                        " "
                      ]
                    }
                  ) })
                ] })
              ]
            }
          )
        },
        "Input"
      )
    }
  ) });
}
function ti() {
  const e = Q(), r = G(), t = r && r.inputParams.find((s) => s.name === "maximum").value, o = /* @__PURE__ */ a.jsx("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: /* @__PURE__ */ a.jsx("p", { children: "从知识返回到模型的最大段落数。数字越大，返回的内容越多。" }) }), i = {
    1: "1",
    3: "默认",
    10: "10"
  };
  return /* @__PURE__ */ a.jsxs(de, { className: "jade-row", children: [
    /* @__PURE__ */ a.jsx(Y, { span: 12, className: "jade-column", children: /* @__PURE__ */ a.jsxs(k.Item, { children: [
      /* @__PURE__ */ a.jsx("span", { style: {
        fontSize: "12px",
        fontFamily: "SF Pro Display",
        letterSpacing: "0.12px",
        lineHeight: "16px",
        alignItems: "center",
        userSelect: "none",
        marginRight: "4px",
        color: "rgba(28, 29, 35, 0.35)"
      }, children: "返回最大值" }),
      /* @__PURE__ */ a.jsx(Ce, { content: o, children: /* @__PURE__ */ a.jsx(we, { className: "jade-panel-header-popover-content" }) })
    ] }) }),
    /* @__PURE__ */ a.jsx(Y, { span: 12, children: /* @__PURE__ */ a.jsx(
      k.Item,
      {
        style: { marginBottom: "0" },
        id: "valueSource",
        initialValue: "Reference",
        children: /* @__PURE__ */ a.jsx(
          Ut,
          {
            className: "jade-slider",
            style: { width: "90%" },
            min: 1,
            max: 10,
            step: 1,
            marks: i,
            defaultValue: 3,
            value: t,
            onChange: (s) => e({ type: "changeMaximum", key: "maximum", value: s })
          }
        )
      }
    ) })
  ] });
}
const ni = (e = {}) => {
  const r = ma.create({
    withCredentials: !0,
    // 设置 withCredentials 为 true
    timeout: 1e4,
    // 设置请求超时时间（毫秒）
    headers: {
      "Content-Type": "application/json",
      // 设置默认请求头
      ...e
      // 合并自定义的请求头
    }
    // 这里可以添加拦截器,更多关于拦截器的内容可以参考 axios 文档：https://axios-http.com/docs/interceptors
  });
  return r.interceptors.request.use((t) => t, (t) => Promise.reject(t)), r.interceptors.response.use((t) => t.data, (t) => Promise.reject(t)), r;
}, bt = (e, r, t, o, i = () => {
}, s = () => {
}) => {
  ni(t).request({
    method: e,
    url: r,
    data: o
  }).then((n) => {
    i(n);
  }).catch((n) => {
    s(n);
  });
}, ri = (e, r = /* @__PURE__ */ new Map(), t, o) => {
  bt("Get", e, r, {}, t, o);
}, ai = (e, r, t = /* @__PURE__ */ new Map(), o, i) => {
  bt("Post", e, t, r, o, i);
}, oi = (e, r, t = /* @__PURE__ */ new Map(), o, i) => {
  bt("Put", e, t, r, o, i);
}, ii = (e, r = /* @__PURE__ */ new Map(), t, o) => {
  bt("Delete", e, r, {}, t, o);
}, Te = {
  get: ri,
  post: ai,
  put: oi,
  del: ii
};
function Zt(e) {
  const [r, t] = ie([]), [o, i] = ie(!1), [s, l] = ie(1), {
    buildUrl: n,
    onChange: d,
    getOptions: c,
    disabled: p,
    dealResponse: h,
    ...v
  } = e, f = n(s), g = async (j) => {
    j && r.length === 0 && (i(!0), Te.get(f, void 0, (R) => {
      const $ = h(R);
      $ && t($), i(!1);
    }, (R) => {
      console.error("Error fetching options:", R), i(!1);
    }));
  }, m = async (j) => {
    const { target: R } = j;
    if (R.scrollTop + R.clientHeight !== R.scrollHeight)
      return;
    console.log("Scroll to bottom. Loading new page...");
    const $ = s;
    l(s + 1), i(!0), Te.get(f, void 0, (B) => {
      const E = B.data;
      E && t([...r, ...E]), i(!1);
    }, (B) => {
      console.error("Error fetching options:", B), i(!1), l($);
    });
  }, y = (j) => {
    d(j, r);
  };
  return /* @__PURE__ */ a.jsx(
    he,
    {
      className: "jade-select",
      style: { width: "100%" },
      onPopupScroll: m,
      onDropdownVisibleChange: g,
      onChange: y,
      disabled: p || !1,
      options: c(r),
      loading: o,
      mode: "single",
      ...v
    }
  );
}
const { Panel: si } = J;
function hr() {
  const e = Q(), r = G(), t = te(), o = r && [...r.inputParams.find((m) => m.name === "knowledge").value], i = t.graph.configs && t.graph.configs.find((m) => m.node === "knowledgeState").urls.knowledgeUrl, s = (m) => i + "?pageNum=" + m + "&pageSize=10", l = () => o.length <= 1, n = (m) => {
    e({ type: "addKnowledge", id: _() }), m.stopPropagation();
  }, d = (m, y, j, R) => {
    e({ type: "editKnowledge", id: m, key: y, value: R.find(($) => $.id === j) });
  }, c = (m) => {
    e({ type: "clearKnowledge", id: m });
  }, p = (m) => {
    e({ type: "deleteKnowledge", id: m });
  }, h = (m) => m.data.items, v = (m) => m.map((y) => ({
    value: y.id,
    label: y.name
  })), f = (m) => m.value && m.value.length !== 0 ? m.value.find((y) => y.name === "name").value : "", g = /* @__PURE__ */ a.jsxs("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: [
    /* @__PURE__ */ a.jsx("p", { children: "选择需要匹配的知识范围，" }),
    /* @__PURE__ */ a.jsx("p", { children: "仅从所选知识中调出信息" })
  ] });
  return /* @__PURE__ */ a.jsx(
    J,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      style: { marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%" },
      defaultActiveKey: ["Knowledge"],
      children: /* @__PURE__ */ a.jsx(
        si,
        {
          style: { marginBottom: 8, borderRadius: "8px", width: "100%" },
          header: /* @__PURE__ */ a.jsxs(
            "div",
            {
              style: { display: "flex", alignItems: "center" },
              children: [
                /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "知识库" }),
                /* @__PURE__ */ a.jsx(Ce, { content: g, children: /* @__PURE__ */ a.jsx(we, { className: "jade-panel-header-popover-content" }) }),
                /* @__PURE__ */ a.jsx(
                  se,
                  {
                    type: "text",
                    className: "icon-button",
                    onClick: (m) => n(m),
                    style: { height: "22px", marginLeft: "auto" },
                    children: /* @__PURE__ */ a.jsx(yt, {})
                  }
                )
              ]
            }
          ),
          className: "jade-panel",
          children: /* @__PURE__ */ a.jsxs(
            k,
            {
              name: `knowledgeForm-${t.id}`,
              layout: "vertical",
              className: "jade-form",
              children: [
                o.map((m) => /* @__PURE__ */ a.jsxs(
                  de,
                  {
                    gutter: 16,
                    children: [
                      /* @__PURE__ */ a.jsx(Y, { span: 22, children: /* @__PURE__ */ a.jsx(
                        k.Item,
                        {
                          style: { marginBottom: "8px" },
                          id: `from-${m.id}`,
                          initialValue: "Reference",
                          children: /* @__PURE__ */ a.jsx(
                            Zt,
                            {
                              allowClear: !0,
                              placeholder: "选择知识库",
                              id: `valueSource-select-${m.id}`,
                              onClear: () => c(m.id),
                              onChange: (y, j) => d(m.id, "value", y, j),
                              buildUrl: s,
                              disabled: !1,
                              getOptions: v,
                              dealResponse: h,
                              value: f(m)
                            }
                          )
                        },
                        `from-${m.id}`
                      ) }),
                      /* @__PURE__ */ a.jsx(Y, { span: 2, style: { paddingLeft: "2px" }, children: /* @__PURE__ */ a.jsx(
                        k.Item,
                        {
                          style: { marginBottom: "8px" },
                          children: /* @__PURE__ */ a.jsx(
                            se,
                            {
                              disabled: l(),
                              type: "text",
                              className: "icon-button",
                              style: {
                                alignItems: "center",
                                marginLeft: "auto"
                              },
                              onClick: () => p(m.id),
                              children: /* @__PURE__ */ a.jsx(fr, {})
                            }
                          )
                        },
                        `button-${m.id}`
                      ) })
                    ]
                  },
                  `knowledgeRow-${m.id}`
                )),
                /* @__PURE__ */ a.jsx(ti, {})
              ]
            }
          )
        },
        "Knowledge"
      )
    }
  );
}
const tt = ({ data: e }) => {
  if (!Array.isArray(e))
    throw new Error("data must be array.");
  const [r, t] = ie(null), o = te();
  ye(() => {
    const n = e.map((d) => s(d, null));
    return t(n), () => {
      n && i(n, (d) => {
        o.page.removeObservable(o.id, d.key);
      });
    };
  }, []);
  const i = (n, d) => {
    n.forEach((c) => {
      c.children && i(c.children, d), d(c);
    });
  }, s = (n, d) => (o.page.registerObservable(o.id, n.id, n.name, n.type, d ? d.id : null), n.type === "Object" ? {
    title: n.name,
    type: n.type,
    key: n.id,
    children: n.value.map((c) => s(c, n))
  } : {
    title: n.name,
    type: n.type,
    key: n.id,
    isLeaf: !0
  }), l = (n) => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx("div", { className: "jade-observable-tree-node-div", children: /* @__PURE__ */ a.jsxs("div", { style: { display: "flex" }, children: [
    /* @__PURE__ */ a.jsx("span", { className: "jade-observable-tree-node-title", children: n.title }),
    /* @__PURE__ */ a.jsx("div", { className: "jade-observable-tree-node-type-div", children: /* @__PURE__ */ a.jsx("span", { className: "jade-observable-tree-node-type-name", children: n.type }) })
  ] }) }) });
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(
    qn,
    {
      treeData: r,
      titleRender: (n) => l(n),
      showLine: !0,
      selectable: !1
    }
  ) });
}, { Panel: li } = J;
function gr() {
  const e = G(), r = te(), t = e && e.outputParams, o = /* @__PURE__ */ a.jsx("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: /* @__PURE__ */ a.jsx("p", { children: "输出列表是与输入参数最匹配的信息，从所有选定的知识库中调用" }) });
  return /* @__PURE__ */ a.jsx(
    J,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      style: { marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%" },
      defaultActiveKey: ["Output"],
      children: /* @__PURE__ */ a.jsx(
        li,
        {
          header: /* @__PURE__ */ a.jsxs(
            "div",
            {
              style: { display: "flex", alignItems: "center", paddingLeft: "-16px" },
              children: [
                /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "输出" }),
                /* @__PURE__ */ a.jsx(Ce, { content: o, children: /* @__PURE__ */ a.jsx(we, { className: "jade-panel-header-popover-content" }) })
              ]
            }
          ),
          className: "jade-panel",
          children: /* @__PURE__ */ a.jsx(
            k,
            {
              name: `outputForm-${r.id}`,
              layout: "vertical",
              className: "jade-form",
              children: /* @__PURE__ */ a.jsx(tt, { data: t })
            }
          )
        },
        "Output"
      )
    }
  );
}
const ci = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  return n.type = "retrievalNodeState", n.backColor = "white", n.pointerEvents = "auto", n.text = "普通检索", n.componentName = "retrievalComponent", n.flowMeta.jober.fitables.push("com.huawei.fit.jober.aipp.fitable.NaiveRAGComponent"), n.flowMeta.triggerMode = "auto", n.getReactComponents = () => /* @__PURE__ */ a.jsxs(a.Fragment, { children: [
    /* @__PURE__ */ a.jsx(mr, {}),
    /* @__PURE__ */ a.jsx(hr, {}),
    /* @__PURE__ */ a.jsx(gr, {})
  ] }), n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(
    se,
    {
      disabled: !0,
      className: "jade-node-custom-header-icon",
      children: /* @__PURE__ */ a.jsx(Qo, {})
    }
  ), n;
}, ui = (e) => {
  const r = {};
  return r.getJadeConfig = () => e || {
    inputParams: [{
      id: "query_" + _(),
      name: "query",
      type: "String",
      from: "Reference",
      referenceNode: "",
      referenceId: "",
      referenceKey: "",
      value: []
    }, {
      id: "knowledge_" + _(),
      name: "knowledge",
      type: "Array",
      from: "Expand",
      value: [{
        id: _(),
        type: "Object",
        from: "Expand",
        value: []
      }]
    }, {
      id: "maximum_" + _(),
      name: "maximum",
      type: "Integer",
      from: "Input",
      value: 3
    }],
    outputParams: [{
      id: "output_" + _(),
      name: "output",
      type: "Object",
      from: "Expand",
      value: [{
        id: _(),
        name: "retrievalOutput",
        type: "String",
        from: "Input",
        value: "String"
      }]
    }]
  }, r.getReactComponents = () => /* @__PURE__ */ a.jsxs(a.Fragment, { children: [
    /* @__PURE__ */ a.jsx(mr, {}),
    /* @__PURE__ */ a.jsx(hr, {}),
    /* @__PURE__ */ a.jsx(gr, {})
  ] }), r.reducers = (t, o) => {
    const i = () => {
      const v = h.inputParams.find((f) => f.name === "query");
      o.changes.map((f) => {
        v[f.key] = f.value;
      });
    }, s = () => {
      c().push({
        id: o.id,
        name: "",
        type: "Object",
        from: "Expand",
        value: []
      });
    }, l = () => {
      const v = c(), f = v.findIndex((g) => g.id === o.id);
      f !== -1 && v.splice(f, 1);
    }, n = () => {
      if (!o.value)
        return;
      const v = o.value.id, f = o.value.name, g = c().find((m) => m.id === o.id).value;
      g.length === 0 ? (g.push({ id: _(), name: "id", from: "Input", type: "String", value: v }), g.push({ id: _(), name: "name", from: "Input", type: "String", value: f })) : g.forEach((m) => {
        m.name === "id" && (m.value = v), m.name === "name" && (m.value = f);
      });
    }, d = () => {
      h.inputParams.filter((v) => v.name === "maximum").forEach((v) => {
        v.value = o.value;
      });
    }, c = () => h.inputParams.find((v) => v.name === "knowledge").value, p = () => {
      c().find((v) => v.id === o.id).value = [];
    };
    let h = { ...t };
    switch (o.type) {
      case "editInput":
        return i(), h;
      case "addKnowledge":
        return s(), h;
      case "deleteKnowledge":
        return l(), h;
      case "editKnowledge":
        return n(), h;
      case "changeMaximum":
        return d(), h;
      case "clearKnowledge":
        return p(), h;
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, r;
}, di = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  return n.type = "listener1Node", n.text = "被监听者", n.componentName = "listener1Component", n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(Ve, {}), n;
}, qt = (e) => {
  const { onChange: r, ...t } = e;
  if (!t.id)
    throw new Error("JadeObservableInput requires an id property.");
  const o = te();
  if (!o)
    throw new Error("JadeObservableInput must be wrapped by ShapeContext.");
  const i = (s) => {
    r && r(s), o.emit(t.id, s.target.value);
  };
  return ye(() => (o.page.registerObservable(o.id, t.id, t.value, t.type, t.parent), () => {
    o.page.removeObservable(o.id, t.id);
  }), []), ye(() => {
    o.page.registerObservable(o.id, t.id, t.value, t.type, t.parent);
  }, [t.type]), /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(be, { ...t, onChange: (s) => i(s) }) });
}, fi = (e) => {
  const r = {};
  return r.getJadeConfig = () => e || [{
    id: "listener1-name",
    name: "name",
    type: "String",
    value: "请输入一个名字"
  }, {
    id: "listener1-firstName",
    name: "firstName",
    type: "String",
    value: "请输入第一名字"
  }], r.getReactComponents = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(pi, {}) }), r.reducers = (t, o) => {
    switch (o.type) {
      case "updateName":
        return t.map((i) => i.id === o.id ? { ...i, name: o.name } : i);
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, r;
}, pi = () => {
  const e = G(), r = Q(), t = (o, i) => {
    r({ type: "updateName", id: o, name: i.target.value });
  };
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsxs("div", { children: [
    /* @__PURE__ */ a.jsx(
      qt,
      {
        id: e[0].id,
        value: e[0].name,
        onChange: (o) => t(e[0].id, o)
      }
    ),
    /* @__PURE__ */ a.jsx(
      qt,
      {
        id: e[1].id,
        parent: e[0].id,
        value: e[1].name,
        onChange: (o) => t(e[1].id, o)
      }
    )
  ] }) });
}, mi = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  return n.type = "listener2Node", n.text = "监听者", n.componentName = "listener2Component", n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(Ve, {}), n;
}, hi = (e) => {
  const r = {};
  return r.getJadeConfig = () => e || [{
    id: "123456",
    name: "zzzzz",
    type: "Reference",
    value: [],
    referenceNode: "",
    referenceId: "",
    referenceKey: ""
  }], r.getReactComponents = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(gi, {}) }), r.reducers = (t, o) => {
    switch (o.type) {
      case "updateValue":
        return t.map((i) => i.id === o.id ? { ...i, referenceKey: o.referenceKey } : i);
      case "update":
        return t.map((i) => i.id === o.id ? { ...i, referenceNode: o.referenceNode, referenceId: o.referenceId, value: o.value } : i);
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, r;
}, gi = () => {
  const e = Q(), r = G();
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(et, { reference: r[0], onReferencedValueChange: (t) => {
    e({ type: "updateValue", id: r[0].id, referenceKey: t });
  }, onReferencedKeyChange: (t) => {
    e({ type: "update", id: r[0].id, referenceNode: t.referenceNode, referenceId: t.referenceId, value: t.value });
  } }) });
}, vi = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  return n.type = "listener3Node", n.text = "被监听者", n.componentName = "listener3Component", n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(Ve, {}), n;
}, yi = (e) => {
  const r = {};
  return r.getJadeConfig = () => e || {
    output: [{
      id: "uuid1",
      name: "person",
      type: "Object",
      value: [{ id: "uuid2", name: "name", type: "String" }, { id: "uuid3", name: "age", type: "Integer" }]
    }]
  }, r.getReactComponents = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(bi, {}) }), r.reducers = () => {
  }, r;
}, bi = () => {
  const e = G();
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx("div", { children: /* @__PURE__ */ a.jsx(tt, { data: e.output }) }) });
}, xi = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  return n.type = "jadeInputTreeNode", n.text = "被监听者", n.componentName = "jadeInputTreeComponent", n.width = 360, n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(Ve, {}), n;
};
var Wt = { exports: {} }, ct = { exports: {} }, H = {};
/** @license React v16.13.1
 * react-is.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var kn;
function ji() {
  if (kn)
    return H;
  kn = 1;
  var e = typeof Symbol == "function" && Symbol.for, r = e ? Symbol.for("react.element") : 60103, t = e ? Symbol.for("react.portal") : 60106, o = e ? Symbol.for("react.fragment") : 60107, i = e ? Symbol.for("react.strict_mode") : 60108, s = e ? Symbol.for("react.profiler") : 60114, l = e ? Symbol.for("react.provider") : 60109, n = e ? Symbol.for("react.context") : 60110, d = e ? Symbol.for("react.async_mode") : 60111, c = e ? Symbol.for("react.concurrent_mode") : 60111, p = e ? Symbol.for("react.forward_ref") : 60112, h = e ? Symbol.for("react.suspense") : 60113, v = e ? Symbol.for("react.suspense_list") : 60120, f = e ? Symbol.for("react.memo") : 60115, g = e ? Symbol.for("react.lazy") : 60116, m = e ? Symbol.for("react.block") : 60121, y = e ? Symbol.for("react.fundamental") : 60117, j = e ? Symbol.for("react.responder") : 60118, R = e ? Symbol.for("react.scope") : 60119;
  function $(E) {
    if (typeof E == "object" && E !== null) {
      var Ee = E.$$typeof;
      switch (Ee) {
        case r:
          switch (E = E.type, E) {
            case d:
            case c:
            case o:
            case s:
            case i:
            case h:
              return E;
            default:
              switch (E = E && E.$$typeof, E) {
                case n:
                case p:
                case g:
                case f:
                case l:
                  return E;
                default:
                  return Ee;
              }
          }
        case t:
          return Ee;
      }
    }
  }
  function B(E) {
    return $(E) === c;
  }
  return H.AsyncMode = d, H.ConcurrentMode = c, H.ContextConsumer = n, H.ContextProvider = l, H.Element = r, H.ForwardRef = p, H.Fragment = o, H.Lazy = g, H.Memo = f, H.Portal = t, H.Profiler = s, H.StrictMode = i, H.Suspense = h, H.isAsyncMode = function(E) {
    return B(E) || $(E) === d;
  }, H.isConcurrentMode = B, H.isContextConsumer = function(E) {
    return $(E) === n;
  }, H.isContextProvider = function(E) {
    return $(E) === l;
  }, H.isElement = function(E) {
    return typeof E == "object" && E !== null && E.$$typeof === r;
  }, H.isForwardRef = function(E) {
    return $(E) === p;
  }, H.isFragment = function(E) {
    return $(E) === o;
  }, H.isLazy = function(E) {
    return $(E) === g;
  }, H.isMemo = function(E) {
    return $(E) === f;
  }, H.isPortal = function(E) {
    return $(E) === t;
  }, H.isProfiler = function(E) {
    return $(E) === s;
  }, H.isStrictMode = function(E) {
    return $(E) === i;
  }, H.isSuspense = function(E) {
    return $(E) === h;
  }, H.isValidElementType = function(E) {
    return typeof E == "string" || typeof E == "function" || E === o || E === c || E === s || E === i || E === h || E === v || typeof E == "object" && E !== null && (E.$$typeof === g || E.$$typeof === f || E.$$typeof === l || E.$$typeof === n || E.$$typeof === p || E.$$typeof === y || E.$$typeof === j || E.$$typeof === R || E.$$typeof === m);
  }, H.typeOf = $, H;
}
var K = {};
/** @license React v16.13.1
 * react-is.development.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var Nn;
function Ci() {
  return Nn || (Nn = 1, process.env.NODE_ENV !== "production" && function() {
    var e = typeof Symbol == "function" && Symbol.for, r = e ? Symbol.for("react.element") : 60103, t = e ? Symbol.for("react.portal") : 60106, o = e ? Symbol.for("react.fragment") : 60107, i = e ? Symbol.for("react.strict_mode") : 60108, s = e ? Symbol.for("react.profiler") : 60114, l = e ? Symbol.for("react.provider") : 60109, n = e ? Symbol.for("react.context") : 60110, d = e ? Symbol.for("react.async_mode") : 60111, c = e ? Symbol.for("react.concurrent_mode") : 60111, p = e ? Symbol.for("react.forward_ref") : 60112, h = e ? Symbol.for("react.suspense") : 60113, v = e ? Symbol.for("react.suspense_list") : 60120, f = e ? Symbol.for("react.memo") : 60115, g = e ? Symbol.for("react.lazy") : 60116, m = e ? Symbol.for("react.block") : 60121, y = e ? Symbol.for("react.fundamental") : 60117, j = e ? Symbol.for("react.responder") : 60118, R = e ? Symbol.for("react.scope") : 60119;
    function $(S) {
      return typeof S == "string" || typeof S == "function" || // Note: its typeof might be other than 'symbol' or 'number' if it's a polyfill.
      S === o || S === c || S === s || S === i || S === h || S === v || typeof S == "object" && S !== null && (S.$$typeof === g || S.$$typeof === f || S.$$typeof === l || S.$$typeof === n || S.$$typeof === p || S.$$typeof === y || S.$$typeof === j || S.$$typeof === R || S.$$typeof === m);
    }
    function B(S) {
      if (typeof S == "object" && S !== null) {
        var fe = S.$$typeof;
        switch (fe) {
          case r:
            var ze = S.type;
            switch (ze) {
              case d:
              case c:
              case o:
              case s:
              case i:
              case h:
                return ze;
              default:
                var Ie = ze && ze.$$typeof;
                switch (Ie) {
                  case n:
                  case p:
                  case g:
                  case f:
                  case l:
                    return Ie;
                  default:
                    return fe;
                }
            }
          case t:
            return fe;
        }
      }
    }
    var E = d, Ee = c, qe = n, We = l, _e = r, Ue = p, Le = o, Me = g, le = f, oe = t, Se = s, ce = i, xe = h, Ne = !1;
    function Ae(S) {
      return Ne || (Ne = !0, console.warn("The ReactIs.isAsyncMode() alias has been deprecated, and will be removed in React 17+. Update your code to use ReactIs.isConcurrentMode() instead. It has the exact same API.")), x(S) || B(S) === d;
    }
    function x(S) {
      return B(S) === c;
    }
    function P(S) {
      return B(S) === n;
    }
    function M(S) {
      return B(S) === l;
    }
    function F(S) {
      return typeof S == "object" && S !== null && S.$$typeof === r;
    }
    function N(S) {
      return B(S) === p;
    }
    function V(S) {
      return B(S) === o;
    }
    function I(S) {
      return B(S) === g;
    }
    function T(S) {
      return B(S) === f;
    }
    function A(S) {
      return B(S) === t;
    }
    function W(S) {
      return B(S) === s;
    }
    function z(S) {
      return B(S) === i;
    }
    function re(S) {
      return B(S) === h;
    }
    K.AsyncMode = E, K.ConcurrentMode = Ee, K.ContextConsumer = qe, K.ContextProvider = We, K.Element = _e, K.ForwardRef = Ue, K.Fragment = Le, K.Lazy = Me, K.Memo = le, K.Portal = oe, K.Profiler = Se, K.StrictMode = ce, K.Suspense = xe, K.isAsyncMode = Ae, K.isConcurrentMode = x, K.isContextConsumer = P, K.isContextProvider = M, K.isElement = F, K.isForwardRef = N, K.isFragment = V, K.isLazy = I, K.isMemo = T, K.isPortal = A, K.isProfiler = W, K.isStrictMode = z, K.isSuspense = re, K.isValidElementType = $, K.typeOf = B;
  }()), K;
}
var In;
function vr() {
  return In || (In = 1, process.env.NODE_ENV === "production" ? ct.exports = ji() : ct.exports = Ci()), ct.exports;
}
/*
object-assign
(c) Sindre Sorhus
@license MIT
*/
var It, Tn;
function wi() {
  if (Tn)
    return It;
  Tn = 1;
  var e = Object.getOwnPropertySymbols, r = Object.prototype.hasOwnProperty, t = Object.prototype.propertyIsEnumerable;
  function o(s) {
    if (s == null)
      throw new TypeError("Object.assign cannot be called with null or undefined");
    return Object(s);
  }
  function i() {
    try {
      if (!Object.assign)
        return !1;
      var s = new String("abc");
      if (s[5] = "de", Object.getOwnPropertyNames(s)[0] === "5")
        return !1;
      for (var l = {}, n = 0; n < 10; n++)
        l["_" + String.fromCharCode(n)] = n;
      var d = Object.getOwnPropertyNames(l).map(function(p) {
        return l[p];
      });
      if (d.join("") !== "0123456789")
        return !1;
      var c = {};
      return "abcdefghijklmnopqrst".split("").forEach(function(p) {
        c[p] = p;
      }), Object.keys(Object.assign({}, c)).join("") === "abcdefghijklmnopqrst";
    } catch {
      return !1;
    }
  }
  return It = i() ? Object.assign : function(s, l) {
    for (var n, d = o(s), c, p = 1; p < arguments.length; p++) {
      n = Object(arguments[p]);
      for (var h in n)
        r.call(n, h) && (d[h] = n[h]);
      if (e) {
        c = e(n);
        for (var v = 0; v < c.length; v++)
          t.call(n, c[v]) && (d[c[v]] = n[c[v]]);
      }
    }
    return d;
  }, It;
}
var Tt, Fn;
function Gt() {
  if (Fn)
    return Tt;
  Fn = 1;
  var e = "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED";
  return Tt = e, Tt;
}
var Ft, _n;
function yr() {
  return _n || (_n = 1, Ft = Function.call.bind(Object.prototype.hasOwnProperty)), Ft;
}
var _t, Ln;
function Ei() {
  if (Ln)
    return _t;
  Ln = 1;
  var e = function() {
  };
  if (process.env.NODE_ENV !== "production") {
    var r = Gt(), t = {}, o = yr();
    e = function(s) {
      var l = "Warning: " + s;
      typeof console < "u" && console.error(l);
      try {
        throw new Error(l);
      } catch {
      }
    };
  }
  function i(s, l, n, d, c) {
    if (process.env.NODE_ENV !== "production") {
      for (var p in s)
        if (o(s, p)) {
          var h;
          try {
            if (typeof s[p] != "function") {
              var v = Error(
                (d || "React class") + ": " + n + " type `" + p + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + typeof s[p] + "`.This often happens because of typos such as `PropTypes.function` instead of `PropTypes.func`."
              );
              throw v.name = "Invariant Violation", v;
            }
            h = s[p](l, p, d, n, null, r);
          } catch (g) {
            h = g;
          }
          if (h && !(h instanceof Error) && e(
            (d || "React class") + ": type specification of " + n + " `" + p + "` is invalid; the type checker function must return `null` or an `Error` but returned a " + typeof h + ". You may have forgotten to pass an argument to the type checker creator (arrayOf, instanceOf, objectOf, oneOf, oneOfType, and shape all require an argument)."
          ), h instanceof Error && !(h.message in t)) {
            t[h.message] = !0;
            var f = c ? c() : "";
            e(
              "Failed " + n + " type: " + h.message + (f ?? "")
            );
          }
        }
    }
  }
  return i.resetWarningCache = function() {
    process.env.NODE_ENV !== "production" && (t = {});
  }, _t = i, _t;
}
var Lt, Mn;
function Si() {
  if (Mn)
    return Lt;
  Mn = 1;
  var e = vr(), r = wi(), t = Gt(), o = yr(), i = Ei(), s = function() {
  };
  process.env.NODE_ENV !== "production" && (s = function(n) {
    var d = "Warning: " + n;
    typeof console < "u" && console.error(d);
    try {
      throw new Error(d);
    } catch {
    }
  });
  function l() {
    return null;
  }
  return Lt = function(n, d) {
    var c = typeof Symbol == "function" && Symbol.iterator, p = "@@iterator";
    function h(x) {
      var P = x && (c && x[c] || x[p]);
      if (typeof P == "function")
        return P;
    }
    var v = "<<anonymous>>", f = {
      array: j("array"),
      bigint: j("bigint"),
      bool: j("boolean"),
      func: j("function"),
      number: j("number"),
      object: j("object"),
      string: j("string"),
      symbol: j("symbol"),
      any: R(),
      arrayOf: $,
      element: B(),
      elementType: E(),
      instanceOf: Ee,
      node: Ue(),
      objectOf: We,
      oneOf: qe,
      oneOfType: _e,
      shape: Me,
      exact: le
    };
    function g(x, P) {
      return x === P ? x !== 0 || 1 / x === 1 / P : x !== x && P !== P;
    }
    function m(x, P) {
      this.message = x, this.data = P && typeof P == "object" ? P : {}, this.stack = "";
    }
    m.prototype = Error.prototype;
    function y(x) {
      if (process.env.NODE_ENV !== "production")
        var P = {}, M = 0;
      function F(V, I, T, A, W, z, re) {
        if (A = A || v, z = z || T, re !== t) {
          if (d) {
            var S = new Error(
              "Calling PropTypes validators directly is not supported by the `prop-types` package. Use `PropTypes.checkPropTypes()` to call them. Read more at http://fb.me/use-check-prop-types"
            );
            throw S.name = "Invariant Violation", S;
          } else if (process.env.NODE_ENV !== "production" && typeof console < "u") {
            var fe = A + ":" + T;
            !P[fe] && // Avoid spamming the console because they are often not actionable except for lib authors
            M < 3 && (s(
              "You are manually calling a React.PropTypes validation function for the `" + z + "` prop on `" + A + "`. This is deprecated and will throw in the standalone `prop-types` package. You may be seeing this warning due to a third-party PropTypes library. See https://fb.me/react-warning-dont-call-proptypes for details."
            ), P[fe] = !0, M++);
          }
        }
        return I[T] == null ? V ? I[T] === null ? new m("The " + W + " `" + z + "` is marked as required " + ("in `" + A + "`, but its value is `null`.")) : new m("The " + W + " `" + z + "` is marked as required in " + ("`" + A + "`, but its value is `undefined`.")) : null : x(I, T, A, W, z);
      }
      var N = F.bind(null, !1);
      return N.isRequired = F.bind(null, !0), N;
    }
    function j(x) {
      function P(M, F, N, V, I, T) {
        var A = M[F], W = ce(A);
        if (W !== x) {
          var z = xe(A);
          return new m(
            "Invalid " + V + " `" + I + "` of type " + ("`" + z + "` supplied to `" + N + "`, expected ") + ("`" + x + "`."),
            { expectedType: x }
          );
        }
        return null;
      }
      return y(P);
    }
    function R() {
      return y(l);
    }
    function $(x) {
      function P(M, F, N, V, I) {
        if (typeof x != "function")
          return new m("Property `" + I + "` of component `" + N + "` has invalid PropType notation inside arrayOf.");
        var T = M[F];
        if (!Array.isArray(T)) {
          var A = ce(T);
          return new m("Invalid " + V + " `" + I + "` of type " + ("`" + A + "` supplied to `" + N + "`, expected an array."));
        }
        for (var W = 0; W < T.length; W++) {
          var z = x(T, W, N, V, I + "[" + W + "]", t);
          if (z instanceof Error)
            return z;
        }
        return null;
      }
      return y(P);
    }
    function B() {
      function x(P, M, F, N, V) {
        var I = P[M];
        if (!n(I)) {
          var T = ce(I);
          return new m("Invalid " + N + " `" + V + "` of type " + ("`" + T + "` supplied to `" + F + "`, expected a single ReactElement."));
        }
        return null;
      }
      return y(x);
    }
    function E() {
      function x(P, M, F, N, V) {
        var I = P[M];
        if (!e.isValidElementType(I)) {
          var T = ce(I);
          return new m("Invalid " + N + " `" + V + "` of type " + ("`" + T + "` supplied to `" + F + "`, expected a single ReactElement type."));
        }
        return null;
      }
      return y(x);
    }
    function Ee(x) {
      function P(M, F, N, V, I) {
        if (!(M[F] instanceof x)) {
          var T = x.name || v, A = Ae(M[F]);
          return new m("Invalid " + V + " `" + I + "` of type " + ("`" + A + "` supplied to `" + N + "`, expected ") + ("instance of `" + T + "`."));
        }
        return null;
      }
      return y(P);
    }
    function qe(x) {
      if (!Array.isArray(x))
        return process.env.NODE_ENV !== "production" && (arguments.length > 1 ? s(
          "Invalid arguments supplied to oneOf, expected an array, got " + arguments.length + " arguments. A common mistake is to write oneOf(x, y, z) instead of oneOf([x, y, z])."
        ) : s("Invalid argument supplied to oneOf, expected an array.")), l;
      function P(M, F, N, V, I) {
        for (var T = M[F], A = 0; A < x.length; A++)
          if (g(T, x[A]))
            return null;
        var W = JSON.stringify(x, function(re, S) {
          var fe = xe(S);
          return fe === "symbol" ? String(S) : S;
        });
        return new m("Invalid " + V + " `" + I + "` of value `" + String(T) + "` " + ("supplied to `" + N + "`, expected one of " + W + "."));
      }
      return y(P);
    }
    function We(x) {
      function P(M, F, N, V, I) {
        if (typeof x != "function")
          return new m("Property `" + I + "` of component `" + N + "` has invalid PropType notation inside objectOf.");
        var T = M[F], A = ce(T);
        if (A !== "object")
          return new m("Invalid " + V + " `" + I + "` of type " + ("`" + A + "` supplied to `" + N + "`, expected an object."));
        for (var W in T)
          if (o(T, W)) {
            var z = x(T, W, N, V, I + "." + W, t);
            if (z instanceof Error)
              return z;
          }
        return null;
      }
      return y(P);
    }
    function _e(x) {
      if (!Array.isArray(x))
        return process.env.NODE_ENV !== "production" && s("Invalid argument supplied to oneOfType, expected an instance of array."), l;
      for (var P = 0; P < x.length; P++) {
        var M = x[P];
        if (typeof M != "function")
          return s(
            "Invalid argument supplied to oneOfType. Expected an array of check functions, but received " + Ne(M) + " at index " + P + "."
          ), l;
      }
      function F(N, V, I, T, A) {
        for (var W = [], z = 0; z < x.length; z++) {
          var re = x[z], S = re(N, V, I, T, A, t);
          if (S == null)
            return null;
          S.data && o(S.data, "expectedType") && W.push(S.data.expectedType);
        }
        var fe = W.length > 0 ? ", expected one of type [" + W.join(", ") + "]" : "";
        return new m("Invalid " + T + " `" + A + "` supplied to " + ("`" + I + "`" + fe + "."));
      }
      return y(F);
    }
    function Ue() {
      function x(P, M, F, N, V) {
        return oe(P[M]) ? null : new m("Invalid " + N + " `" + V + "` supplied to " + ("`" + F + "`, expected a ReactNode."));
      }
      return y(x);
    }
    function Le(x, P, M, F, N) {
      return new m(
        (x || "React class") + ": " + P + " type `" + M + "." + F + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + N + "`."
      );
    }
    function Me(x) {
      function P(M, F, N, V, I) {
        var T = M[F], A = ce(T);
        if (A !== "object")
          return new m("Invalid " + V + " `" + I + "` of type `" + A + "` " + ("supplied to `" + N + "`, expected `object`."));
        for (var W in x) {
          var z = x[W];
          if (typeof z != "function")
            return Le(N, V, I, W, xe(z));
          var re = z(T, W, N, V, I + "." + W, t);
          if (re)
            return re;
        }
        return null;
      }
      return y(P);
    }
    function le(x) {
      function P(M, F, N, V, I) {
        var T = M[F], A = ce(T);
        if (A !== "object")
          return new m("Invalid " + V + " `" + I + "` of type `" + A + "` " + ("supplied to `" + N + "`, expected `object`."));
        var W = r({}, M[F], x);
        for (var z in W) {
          var re = x[z];
          if (o(x, z) && typeof re != "function")
            return Le(N, V, I, z, xe(re));
          if (!re)
            return new m(
              "Invalid " + V + " `" + I + "` key `" + z + "` supplied to `" + N + "`.\nBad object: " + JSON.stringify(M[F], null, "  ") + `
Valid keys: ` + JSON.stringify(Object.keys(x), null, "  ")
            );
          var S = re(T, z, N, V, I + "." + z, t);
          if (S)
            return S;
        }
        return null;
      }
      return y(P);
    }
    function oe(x) {
      switch (typeof x) {
        case "number":
        case "string":
        case "undefined":
          return !0;
        case "boolean":
          return !x;
        case "object":
          if (Array.isArray(x))
            return x.every(oe);
          if (x === null || n(x))
            return !0;
          var P = h(x);
          if (P) {
            var M = P.call(x), F;
            if (P !== x.entries) {
              for (; !(F = M.next()).done; )
                if (!oe(F.value))
                  return !1;
            } else
              for (; !(F = M.next()).done; ) {
                var N = F.value;
                if (N && !oe(N[1]))
                  return !1;
              }
          } else
            return !1;
          return !0;
        default:
          return !1;
      }
    }
    function Se(x, P) {
      return x === "symbol" ? !0 : P ? P["@@toStringTag"] === "Symbol" || typeof Symbol == "function" && P instanceof Symbol : !1;
    }
    function ce(x) {
      var P = typeof x;
      return Array.isArray(x) ? "array" : x instanceof RegExp ? "object" : Se(P, x) ? "symbol" : P;
    }
    function xe(x) {
      if (typeof x > "u" || x === null)
        return "" + x;
      var P = ce(x);
      if (P === "object") {
        if (x instanceof Date)
          return "date";
        if (x instanceof RegExp)
          return "regexp";
      }
      return P;
    }
    function Ne(x) {
      var P = xe(x);
      switch (P) {
        case "array":
        case "object":
          return "an " + P;
        case "boolean":
        case "date":
        case "regexp":
          return "a " + P;
        default:
          return P;
      }
    }
    function Ae(x) {
      return !x.constructor || !x.constructor.name ? v : x.constructor.name;
    }
    return f.checkPropTypes = i, f.resetWarningCache = i.resetWarningCache, f.PropTypes = f, f;
  }, Lt;
}
var Mt, An;
function Pi() {
  if (An)
    return Mt;
  An = 1;
  var e = Gt();
  function r() {
  }
  function t() {
  }
  return t.resetWarningCache = r, Mt = function() {
    function o(l, n, d, c, p, h) {
      if (h !== e) {
        var v = new Error(
          "Calling PropTypes validators directly is not supported by the `prop-types` package. Use PropTypes.checkPropTypes() to call them. Read more at http://fb.me/use-check-prop-types"
        );
        throw v.name = "Invariant Violation", v;
      }
    }
    o.isRequired = o;
    function i() {
      return o;
    }
    var s = {
      array: o,
      bigint: o,
      bool: o,
      func: o,
      number: o,
      object: o,
      string: o,
      symbol: o,
      any: o,
      arrayOf: i,
      element: o,
      elementType: o,
      instanceOf: i,
      node: o,
      objectOf: i,
      oneOf: i,
      oneOfType: i,
      shape: i,
      exact: i,
      checkPropTypes: t,
      resetWarningCache: r
    };
    return s.PropTypes = s, s;
  }, Mt;
}
if (process.env.NODE_ENV !== "production") {
  var Ri = vr(), Oi = !0;
  Wt.exports = Si()(Ri.isElement, Oi);
} else
  Wt.exports = Pi()();
var ki = Wt.exports;
const L = /* @__PURE__ */ Un(ki), br = (e, r) => {
  if (e.from === "Expand")
    return {
      id: e.id,
      title: e.name,
      type: e.type,
      key: e.id,
      level: r,
      from: e.from,
      children: e.value.map((t) => br(t, r + 1))
    };
  {
    const t = {
      id: e.id,
      title: e.name,
      type: e.type,
      key: e.id,
      level: r,
      value: e.value,
      from: e.from,
      referenceKey: e.referenceKey,
      referenceNode: e.referenceNode,
      referenceId: e.referenceId,
      isLeaf: !0
    };
    return e.type === "Object" && (t.props = e.props), t;
  }
};
xr.propTypes = {
  data: L.array.isRequired,
  updateItem: L.func.isRequired
};
const Ni = 110, Ii = 24;
function xr({ data: e, updateItem: r }) {
  const t = e.map((c) => br(c, 0)), o = (c, p, h) => {
    r(c, [{ key: p, value: h.target.value }]);
  }, i = (c, p) => {
    r(c, [{ key: "referenceKey", value: p }]);
  }, s = (c, p) => {
    r(c, [
      { key: "referenceNode", value: p.referenceNode },
      { key: "referenceId", value: p.referenceId },
      { key: "value", value: p.value }
    ]);
  }, l = (c) => c.from === "Input" ? /* @__PURE__ */ a.jsx(
    be,
    {
      className: "jade-input",
      style: { borderRadius: "0px 8px 8px 0px" },
      placeholder: "请输入",
      value: c.value,
      onChange: (p) => o(c.id, "value", p)
    }
  ) : c.from === "Reference" ? /* @__PURE__ */ a.jsx(
    et,
    {
      className: "jade-input-tree-title-tree-select jade-select",
      reference: c,
      onReferencedKeyChange: (p) => s(c.id, p),
      onReferencedValueChange: (p) => i(c.id, p)
    }
  ) : null, n = (c) => {
    switch (c.type) {
      case "Object":
        return [{ value: "Reference", label: "引用" }, { value: "Expand", label: "展开" }];
      case "Array":
        return [{ value: "Reference", label: "引用" }];
      default:
        return [{ value: "Reference", label: "引用" }];
    }
  }, d = (c) => {
    const p = Ni - c.level * Ii;
    return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx("div", { className: "jade-input-tree-title", children: /* @__PURE__ */ a.jsxs(de, { wrap: !1, children: [
      /* @__PURE__ */ a.jsx(Y, { flex: "0 0 " + p + "px", children: /* @__PURE__ */ a.jsx(
        "div",
        {
          className: "jade-input-tree-title-child",
          style: { display: "flex", alignItems: "center" },
          children: /* @__PURE__ */ a.jsx("span", { children: c.title })
        }
      ) }),
      /* @__PURE__ */ a.jsx(Y, { flex: "0 0 70px", style: { paddingRight: 0 }, children: /* @__PURE__ */ a.jsx("div", { className: "jade-input-tree-title-child", children: /* @__PURE__ */ a.jsx(Ti, { node: c, options: n(c), updateItem: r }) }) }),
      /* @__PURE__ */ a.jsx(Y, { flex: "1 1 auto", children: /* @__PURE__ */ a.jsx("div", { className: "jade-input-tree-title-child", children: l(c) }) })
    ] }) }) });
  };
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(pa, { theme: { components: { Tree: { nodeSelectedBg: "transparent", nodeHoverBg: "transparent" } } }, children: /* @__PURE__ */ a.jsx(
    qn,
    {
      blockNode: !0,
      treeData: t,
      className: "jade-ant-tree",
      titleRender: d,
      showLine: !0
    }
  ) }) });
}
const Ti = ({ node: e, options: r, updateItem: t }) => {
  const o = (i) => {
    i === "Expand" ? t(e.id, [
      { key: "from", value: i },
      { key: "referenceNode", value: null },
      { key: "referenceId", value: null },
      { key: "referenceKey", value: null },
      { key: "value", value: e.props }
    ]) : i === "Input" ? t(e.id, [
      { key: "from", value: i },
      { key: "referenceNode", value: null },
      { key: "referenceId", value: null },
      { key: "referenceKey", value: null },
      { key: "value", value: null }
    ]) : t(e.id, [{ key: "from", value: i }]);
  };
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(
    he,
    {
      style: { background: "#f7f7f7", width: "100%" },
      placeholder: "请选择",
      defaultValue: e.from,
      className: "jade-input-tree-title-select jade-select",
      onChange: o,
      options: r
    }
  ) });
}, { Panel: Fi } = J;
function jr({ data: e, updateItem: r }) {
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(
    J,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      defaultActiveKey: ["jadeInputTreePanel"],
      children: /* @__PURE__ */ a.jsx(
        Fi,
        {
          header: /* @__PURE__ */ a.jsx("div", { children: /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "输入" }) }),
          className: "jade-panel",
          children: /* @__PURE__ */ a.jsx(xr, { data: e, updateItem: r })
        },
        "jadeInputTreePanel"
      )
    }
  ) });
}
const _i = (e) => {
  const r = {};
  r.getJadeConfig = () => e || {
    input: [{
      id: "uuid1",
      name: "person",
      type: "Object",
      from: "Expand",
      value: [{
        id: "uuid2",
        name: "name",
        type: "Object",
        from: "Expand",
        value: [{ id: "uuid3", name: "surname", type: "String", value: null, from: "Input" }],
        props: [{ id: "uuid3", name: "surname", type: "String", value: null, from: "Input" }]
      }],
      props: [{
        id: "uuid2",
        name: "name",
        type: "Object",
        from: "Expand",
        value: [{ id: "uuid3", name: "surname", type: "String", value: null, from: "Input" }],
        props: [{ id: "uuid3", name: "surname", type: "String", value: null, from: "Input" }]
      }]
    }, {
      id: "uuid4",
      name: "school",
      type: "Object",
      from: "Reference",
      value: [],
      props: [{ id: "uuid5", name: "schoolName", type: "String", value: "绵阳中学", from: "Input" }]
    }]
  }, r.getReactComponents = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(Li, {}) });
  const t = (o, i, s) => o.map((l) => {
    const n = { ...l };
    return l.id === i ? (s.forEach((d) => {
      n[d.key] = d.value;
    }), n) : (n.type === "Object" && Array.isArray(n.value) && (n.value = t(n.value, i, s)), n);
  });
  return r.reducers = (o, i) => {
    switch (i.type) {
      case "update":
        return { input: t(o.input, i.id, i.changes) };
      default:
        throw Error("Unknown action: " + i.type);
    }
  }, r;
}, Li = () => {
  const e = G(), r = Q(), t = (o, i) => {
    r({ type: "update", id: o, changes: i });
  };
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx("div", { children: /* @__PURE__ */ a.jsx(jr, { data: e.input, updateItem: t }) }) });
}, Mi = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  return n.type = "testNode", n.text = "测试组件", n.componentName = "testComponent", n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(Ve, {}), n;
}, Ai = (e) => {
  const r = {};
  return r.getJadeConfig = () => e || [{ name: "description", type: "String", value: "这是一个测试" }], r.getReactComponents = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(zi, {}) }), r.reducers = (t, o) => {
    if (o.type === "update")
      return [{ ...t[0], value: o.value }];
  }, r;
}, zi = (e) => {
  const r = ft(null), t = G(), o = Q(), i = () => {
    o({ type: "update", value: r.current.input.value });
  };
  return /* @__PURE__ */ a.jsxs(a.Fragment, { children: [
    /* @__PURE__ */ a.jsx("div", { children: /* @__PURE__ */ a.jsx(be, { ref: r, value: t[0].value, placeholder: "Basic usage", onChange: () => i() }) }),
    /* @__PURE__ */ a.jsx(Di, { ...e })
  ] });
}, Di = (e) => /* @__PURE__ */ a.jsxs(a.Fragment, { children: [
  /* @__PURE__ */ a.jsx("span", { children: e.a }),
  /* @__PURE__ */ a.jsx("span", { children: e.b }),
  /* @__PURE__ */ a.jsx("span", { children: e.c })
] }), $i = (e) => {
  const r = {};
  return r.getJadeConfig = () => e || [{ name: "description", type: "String", value: "替换之前的输入框" }], r.getReactComponents = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(Bi, {}) }), r.reducers = (t, o) => {
    if (o.type === "update")
      return [{ ...t[0], value: o.value }];
  }, r;
}, Bi = () => {
  const e = ft(null), r = G(), t = Q(), o = () => {
    t({ type: "update", value: e.current.input.value });
  };
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx("div", { children: /* @__PURE__ */ a.jsx(be, { ref: e, value: r[0].value, placeholder: "Basic usage", onChange: () => o() }) }) });
}, Vi = (e) => /* @__PURE__ */ C.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ C.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ C.createElement("defs", null, /* @__PURE__ */ C.createElement("clipPath", { id: "clip4_13280" }, /* @__PURE__ */ C.createElement("rect", { id: "\\u56FE\\u6807/16/\\u5F00\\u59CB\\uFF0C\\u8D77\\u70B9\\uFF0C\\u7AEF\\u70B9", width: 16, height: 16, transform: "translate(4.000000 4.000000)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ C.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#5E7CE0", fillOpacity: 1 }), /* @__PURE__ */ C.createElement("g", { clipPath: "url(#clip4_13280)" }, /* @__PURE__ */ C.createElement("path", { id: "path", d: "M16.58 11.52L18.16 11.52L18.16 6.31L11.46 6.31C8.35 6.37 5.83 8.89 5.83 11.99C5.83 15.1 8.35 17.62 11.46 17.67L18.16 17.67L18.16 12.46L13.07 12.46L13.07 13.52L10.08 13.52L10.08 10.47L13.07 10.47L13.07 11.53L16.58 11.53L16.58 11.52Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }))), qi = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  n.type = "startNodeStart", n.text = "开始", n.pointerEvents = "auto", n.componentName = "startComponent", n.deletable = !1, n.toolMenus = [{
    key: "1",
    label: "重命名",
    action: (c) => {
      c(!0);
    }
  }], delete n.flowMeta.jober;
  const d = n.initConnectors;
  return n.initConnectors = () => {
    d.apply(n), n.connectors.remove((c) => c.direction.key === Be.W.key);
  }, n.getComponent = () => n.graph.plugins[n.componentName](n.flowMeta.inputParams), n.serializerJadeConfig = () => {
    n.flowMeta.inputParams = n.getLatestJadeConfig();
  }, n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(
    se,
    {
      disabled: !0,
      className: "jade-node-custom-header-icon",
      children: /* @__PURE__ */ a.jsx(Vi, {})
    }
  ), n;
};
Cr.propTypes = {
  itemId: L.string.isRequired,
  // 确保 itemId 是一个必需的字符串
  propValue: L.string.isRequired,
  // 确保 propValue 是一个必需的字符串
  onChange: L.func.isRequired
  // 确保 onChange 是一个必需的函数
};
function Cr({ itemId: e, propValue: r, type: t, onChange: o }) {
  return /* @__PURE__ */ a.jsx(
    k.Item,
    {
      className: "jade-form-item",
      label: "字段名称",
      name: "name",
      rules: [{ required: !0, message: "参数名称不能为空" }, { pattern: /^[^\s]*$/, message: "禁止输入空格" }],
      initialValue: r,
      children: /* @__PURE__ */ a.jsx(
        qt,
        {
          className: "jade-input",
          id: e,
          value: r,
          type: t,
          placeholder: "请输入字段名称",
          showCount: !0,
          maxLength: 20,
          onChange: (i) => o("name", i.target.value)
        }
      )
    }
  );
}
wr.propTypes = {
  propValue: L.string.isRequired,
  // 确保 propValue 是一个必需的字符串
  onChange: L.func.isRequired
  // 确保 onChange 是一个必需的函数
};
function wr({ propValue: e, onChange: r }) {
  const t = (i) => {
    i.stopPropagation();
  }, o = (i) => {
    r("type", i), document.activeElement.blur();
  };
  return /* @__PURE__ */ a.jsx(
    k.Item,
    {
      className: "jade-form-item",
      label: "字段类型",
      name: "type",
      initialValue: e,
      children: /* @__PURE__ */ a.jsx(
        he,
        {
          className: "jade-select",
          value: e,
          style: { width: "100%" },
          onClick: t,
          onChange: o,
          options: [
            { value: "String", label: "String" },
            { value: "Integer", label: "Integer" },
            { value: "Boolean", label: "Boolean" },
            { value: "Number", label: "Number" }
          ]
        }
      )
    }
  );
}
const { TextArea: Wi } = be;
Er.propTypes = {
  propValue: L.string.isRequired,
  // 确保 propValue 是一个必需的字符串
  onChange: L.func.isRequired
  // 确保 onChange 是一个必需的函数
};
function Er({ propValue: e, onChange: r }) {
  return /* @__PURE__ */ a.jsx(
    k.Item,
    {
      className: "jade-form-item",
      label: "字段描述",
      name: "description",
      initialValue: e,
      children: /* @__PURE__ */ a.jsx(
        Wi,
        {
          className: "jade-input",
          value: e,
          onChange: (t) => r("description", t.target.value),
          placeholder: "请输入字段描述",
          autoSize: { minRows: 4, maxRows: 4 }
        }
      )
    }
  );
}
Sr.propTypes = {
  item: L.shape({
    id: L.string.isRequired,
    name: L.string.isRequired,
    type: L.string.isRequired,
    description: L.string.isRequired,
    from: L.string.isRequired,
    value: L.string.isRequired
  }).isRequired,
  formName: L.string.isRequired
  // 确保 formName 属性是一个必需的字符串类型
};
function Sr({ item: e, formName: r }) {
  const t = Q(), o = e.id, i = (s, l) => {
    t({ actionType: "changeInputParam", id: o, type: s, value: l });
  };
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsxs(
    k,
    {
      name: r,
      labelCol: {
        span: 8
        // wrapperCol宽度24意味着 label的布局占8格
      },
      wrapperCol: {
        span: 24
        // 在 Ant Design 的栅格系统中，默认将页面分为 24 格，因此每行的总宽度为 24, wrapperCol宽度24意味着 表单项内容的布局占满一行
      },
      style: {
        paddingTop: "8px",
        maxWidth: 600
        // 禁用表单字段的自动填充功能，这样用户就无法从浏览器的自动填充列表中选择之前输入过的值来填充表单字段
      },
      layout: "vertical",
      autoComplete: "off",
      children: [
        /* @__PURE__ */ a.jsx(Cr, { itemId: e.id, propValue: e.name, type: e.type, onChange: i }),
        /* @__PURE__ */ a.jsx(wr, { propValue: e.type, onChange: i }),
        /* @__PURE__ */ a.jsx(Er, { propValue: e.description, onChange: i })
      ]
    }
  ) });
}
Pr.propTypes = {
  propValue: L.string.isRequired,
  // 确保 propValue 是一个必需的number类型
  onValueChange: L.func.isRequired
  // 确保 onNameChange 是一个必需的函数类型
};
function Pr({ propValue: e, onValueChange: r }) {
  const t = parseInt(e), o = {
    1: "1",
    3: "默认",
    10: "10"
  };
  return /* @__PURE__ */ a.jsx("div", { style: { display: "flex", alignItems: "center" }, children: /* @__PURE__ */ a.jsx(
    Ut,
    {
      style: { width: "95%" },
      min: 1,
      max: 10,
      defaultValue: 3,
      marks: o,
      step: 1,
      onChange: (i) => r("Integer", i.toString()),
      value: isNaN(t) ? 3 : t
    }
  ) });
}
Rr.propTypes = {
  propValue: L.string.isRequired,
  // 确保 propValue 是一个必需的number类型
  onValueChange: L.func.isRequired
  // 确保 onNameChange 是一个必需的函数类型
};
function Rr({ propValue: e, onValueChange: r }) {
  const t = parseFloat(e);
  return /* @__PURE__ */ a.jsxs("div", { style: { display: "flex", alignItems: "center" }, children: [
    /* @__PURE__ */ a.jsx(
      Ut,
      {
        style: { width: "90%" },
        min: 1,
        max: 100,
        defaultValue: 20,
        step: 1,
        onChange: (o) => r("Integer", o.toString()),
        value: isNaN(t) ? 20 : t
      }
    ),
    /* @__PURE__ */ a.jsx("span", { style: { marginLeft: "8px" }, children: t })
  ] });
}
Or.propTypes = {
  propValue: L.string.isRequired,
  // 确保 propValue 是一个必需的number类型
  onValueChange: L.func.isRequired
  // 确保 onNameChange 是一个必需的函数类型
};
function Or({ propValue: e, onValueChange: r }) {
  const t = parseInt(e), o = (i) => {
    const s = Math.floor(i);
    r("Integer", s.toString());
  };
  return /* @__PURE__ */ a.jsx("div", { style: { display: "flex", alignItems: "center" }, children: /* @__PURE__ */ a.jsx(
    Wn,
    {
      style: {
        width: "100%"
      },
      min: 1,
      max: 1e4,
      step: 100,
      onChange: o,
      stringMode: !0,
      value: isNaN(t) ? 1e3 : t
    }
  ) });
}
kr.propTypes = {
  propValue: L.string.isRequired,
  // 确保 propValue 是一个必需的string类型
  onValueChange: L.func.isRequired
  // 确保 onNameChange 是一个必需的函数类型
};
function kr({ propValue: e, onValueChange: r }) {
  const t = e, o = (l) => {
    l.stopPropagation();
  }, i = (l, n) => ((n == null ? void 0 : n.label) ?? "").toLowerCase().includes(l.toLowerCase()), s = (l) => {
    r("String", l), document.activeElement.blur();
  };
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(
    he,
    {
      showSearch: !0,
      className: "jade-select",
      style: { width: "100%" },
      onClick: o,
      onChange: s,
      filterOption: i,
      options: [
        { value: "oneHour", label: "近1小时" },
        { value: "sixHour", label: "近6小时" },
        { value: "twelveHour", label: "近12小时" },
        { value: "oneDay", label: "近1天" },
        { value: "twoDays", label: "近2天" },
        { value: "oneWeek", label: "近1周" }
      ],
      value: t
    }
  ) });
}
Nr.propTypes = {
  propValue: L.oneOfType([L.string, L.oneOf([null])]),
  // 确保 propValue 是一个必需的string类型或者null
  onValueChange: L.func.isRequired,
  // 确保 onValueChange 是一个必需的函数类型
  config: L.object.isRequired
  // 确保 config 是一个必需的对象类型
};
function Nr({ propValue: e, onValueChange: r, config: t }) {
  const o = e, [i, s] = ie([]), l = (c) => {
    c.stopPropagation();
  }, n = (c, p) => ((p == null ? void 0 : p.label) ?? "").toLowerCase().includes(c.toLowerCase()), d = (c) => {
    r("String", c), document.activeElement.blur();
  };
  return ye(() => {
    Te.get(t.urls.customHistoryUrl, {}, (c) => s(c.data.map((p) => ({
      value: p.fitableId,
      label: p.name
    }))));
  }, []), /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(
    he,
    {
      allowClear: !0,
      showSearch: !0,
      className: "jade-select",
      style: { width: "100%" },
      onClick: l,
      onChange: d,
      filterOption: n,
      placeholder: "选择合适的获取历史记录服务",
      options: i,
      value: o
    }
  ) });
}
Ir.propTypes = {
  config: L.object.isRequired
  // 确保 config 是一个必需的对象类型
};
function Ir({ config: e }) {
  const r = Q(), t = G(), o = t.find((c) => c.name === "memory").value.find((c) => c.name === "type").value, i = t.find((c) => c.name === "memory").value.find((c) => c.name === "value").value, s = (c) => {
    c.stopPropagation();
  }, l = (c, p) => {
    r({ actionType: "changeMemory", memoryType: o, memoryValueType: c, memoryValue: p });
  }, n = () => {
    switch (o) {
      case "ByConversationTurn":
        return /* @__PURE__ */ a.jsx(Pr, { propValue: i, onValueChange: l });
      case "ByNumber":
        return /* @__PURE__ */ a.jsx(Rr, { propValue: i, onValueChange: l });
      case "ByTokenSize":
        return /* @__PURE__ */ a.jsx(Or, { propValue: i, onValueChange: l });
      case "ByTime":
        return /* @__PURE__ */ a.jsx(kr, { propValue: i, onValueChange: l });
      case "Customizing":
        return /* @__PURE__ */ a.jsx(Nr, { propValue: i, onValueChange: l, config: e });
      case "UserSelect":
        return null;
      case "NotUseMemory":
        return null;
      default:
        return null;
    }
  }, d = (c) => {
    let p = "", h = null;
    switch (c) {
      case "ByConversationTurn":
        p = "Integer", h = "3";
        break;
      case "ByNumber":
        p = "Integer", h = "20";
        break;
      case "ByTokenSize":
        p = "Integer", h = "1000";
        break;
      case "ByTime":
        p = "String", h = "oneHour";
        break;
      case "Customizing":
        p = "String";
        break;
    }
    r({
      actionType: "changeMemory",
      memoryType: c,
      memoryValueType: p,
      memoryValue: h
    }), document.activeElement.blur();
  };
  return /* @__PURE__ */ a.jsxs(a.Fragment, { children: [
    /* @__PURE__ */ a.jsx(
      he,
      {
        className: "jade-select",
        defaultValue: o,
        style: { width: "100%", marginBottom: "8px", marginTop: "8px" },
        onClick: s,
        onChange: (c) => d(c),
        options: [
          { value: "ByConversationTurn", label: "按对话轮次" },
          // 430演示大模型选项不需要按条数、按Token大小、按时间，暂时屏蔽
          // {value: 'ByNumber', label: '按条数'},
          // {value: 'ByTokenSize', label: '按Token大小'},
          // {value: 'ByTime', label: '按时间'},
          { value: "Customizing", label: "自定义" },
          { value: "UserSelect", label: "用户自勾选" },
          { value: "NotUseMemory", label: "不使用历史记录" }
        ]
      }
    ),
    n(),
    " "
  ] });
}
const { Panel: zn } = J;
function Ui() {
  const e = Q(), r = G(), t = te().graph.configs.find((p) => p.node === "startNodeStart"), i = r.find((p) => p.name === "input").value, [s, l] = ie(() => i.map((p) => p.id)), n = () => {
    const p = "input_" + _();
    l([...s, p]), e({ actionType: "addInputParam", id: p });
  }, d = (p) => {
    const h = s.filter((v) => v !== p);
    l(h), e({ actionType: "deleteInputParam", id: p });
  }, c = /* @__PURE__ */ a.jsxs("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: [
    /* @__PURE__ */ a.jsx("p", { children: "定义启动工作流所需的输入参数，这些内容将由" }),
    /* @__PURE__ */ a.jsx("p", { children: "大模型在机器人对话过程中读取，允许大模型" }),
    /* @__PURE__ */ a.jsx("p", { children: "在适当的时间启动工作流并填写正确的信息。" })
  ] });
  return /* @__PURE__ */ a.jsxs("div", { children: [
    /* @__PURE__ */ a.jsxs("div", { style: {
      display: "flex",
      alignItems: "center",
      marginBottom: "8px",
      paddingLeft: "8px",
      paddingRight: "4px"
    }, children: [
      /* @__PURE__ */ a.jsx("div", { className: "jade-panel-header-font", children: "输入" }),
      /* @__PURE__ */ a.jsx(Ce, { content: c, children: /* @__PURE__ */ a.jsx(we, { className: "jade-top-header-popover-content" }) }),
      /* @__PURE__ */ a.jsx(
        se,
        {
          type: "text",
          className: "icon-button",
          onClick: n,
          style: { height: "32px", marginLeft: "76%" },
          children: /* @__PURE__ */ a.jsx(yt, {})
        }
      )
    ] }),
    /* @__PURE__ */ a.jsx(
      J,
      {
        bordered: !1,
        activeKey: s,
        onChange: (p) => l(p),
        className: "jade-collapse-custom-background-color",
        children: i.map((p) => /* @__PURE__ */ a.jsx(
          zn,
          {
            header: /* @__PURE__ */ a.jsxs("div", { className: "panel-header", children: [
              /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: p.name }),
              " ",
              /* @__PURE__ */ a.jsx(
                se,
                {
                  type: "text",
                  className: "icon-button",
                  style: { height: "22px", marginLeft: "auto" },
                  onClick: () => d(p.id),
                  children: /* @__PURE__ */ a.jsx(Io, {})
                }
              )
            ] }),
            className: "jade-panel",
            style: { marginBottom: 8, borderRadius: "8px", width: "100%" },
            children: /* @__PURE__ */ a.jsx(
              Sr,
              {
                item: p,
                formName: "startForm" + p.id
              }
            )
          },
          p.id
        ))
      }
    ),
    /* @__PURE__ */ a.jsx(J, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["historicalRecordsPanel"], children: /* @__PURE__ */ a.jsx(
      zn,
      {
        header: /* @__PURE__ */ a.jsx(
          "div",
          {
            className: "panel-header",
            style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
            children: /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "历史记录" })
          }
        ),
        className: "jade-panel",
        style: { width: "100%" },
        children: /* @__PURE__ */ a.jsx(Ir, { config: t })
      },
      "historicalRecordsPanel"
    ) })
  ] });
}
const Hi = (e) => {
  const r = {};
  return r.getJadeConfig = () => e || [
    {
      id: _(),
      name: "input",
      type: "Object",
      from: "Expand",
      value: [{ id: "input_" + _(), name: "Question", type: "String", from: "Input", description: "", value: "" }]
    },
    {
      id: _(),
      name: "memory",
      type: "Object",
      from: "Expand",
      value: [{
        id: _(),
        name: "type",
        type: "String",
        from: "Input",
        value: "ByConversationTurn"
      }, {
        id: _(),
        name: "value",
        type: "Integer",
        from: "Input",
        value: "3"
      }]
    }
  ], r.getReactComponents = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(Ki, {}) }), r.reducers = (t, o) => {
    function i() {
      return t.map((d) => d.name === "input" ? {
        ...d,
        value: [...d.value, { id: o.id, name: "", type: "string", from: "init", description: "", value: "" }]
      } : d);
    }
    function s() {
      return t.map((d) => d.name === "input" ? {
        ...d,
        value: d.value.map((c) => c.id === o.id ? {
          ...c,
          [o.type]: o.value
        } : c)
      } : d);
    }
    function l() {
      return t.map((d) => d.name === "memory" ? {
        ...d,
        value: d.value.map((c) => c.name === "type" ? { ...c, value: o.memoryType } : c.name === "value" ? { ...c, type: o.memoryValueType, value: o.memoryValue } : c)
      } : d);
    }
    function n() {
      return t.map((d) => d.name === "input" ? {
        ...d,
        value: d.value.filter((c) => c.id !== o.id)
      } : d);
    }
    switch (o.actionType) {
      case "addInputParam":
        return i();
      case "changeInputParam":
        return s();
      case "changeMemory":
        return l();
      case "deleteInputParam":
        return n();
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, r;
}, Ki = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(Ui, {}) }), Yi = (e) => /* @__PURE__ */ C.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ C.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ C.createElement("defs", null, /* @__PURE__ */ C.createElement("clipPath", { id: "clip4_13238" }, /* @__PURE__ */ C.createElement("rect", { id: "\\u56FE\\u6807/16/\\u5927\\u6A21\\u578B", width: 16, height: 16, transform: "translate(4.000000 4.000000)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ C.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#047BFC", fillOpacity: 1 }), /* @__PURE__ */ C.createElement("g", { clipPath: "url(#clip4_13238)" }, /* @__PURE__ */ C.createElement("path", { id: "path", d: "M12 4.66L18.33 8.33L18.33 15.66L11.99 19.33L5.66 15.66L5.66 8.33L12 4.66ZM8.33 10.64L11.33 12.38L11.33 15.75L12.66 15.75L12.66 12.38L15.66 10.64L15 9.49L12 11.23L9 9.49L8.33 10.64Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }))), Ji = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  return n.type = "llmNodeState", n.text = "大模型", n.pointerEvents = "auto", n.componentName = "llmComponent", n.flowMeta.jober.fitables.push("com.huawei.fit.jober.aipp.fitable.LLMComponent"), n.flowMeta.jober.isAsync = "true", n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(
    se,
    {
      disabled: !0,
      className: "jade-node-custom-header-icon",
      children: /* @__PURE__ */ a.jsx(Yi, {})
    }
  ), n;
}, { TextArea: Zi } = be, { Panel: Gi } = J;
Tr.propTypes = {
  modelOptions: L.array.isRequired
  // 确保 modelOptions 是一个必需的array类型
};
function Tr({ modelOptions: e }) {
  const r = te(), t = G(), o = Q(), i = t.inputParams.find((p) => p.name === "model"), s = t.inputParams.find((p) => p.name === "temperature"), l = t.inputParams.filter((p) => p.name === "prompt").flatMap((p) => p.value).find((p) => p.name === "template"), n = (p) => {
    p.stopPropagation();
  }, d = /* @__PURE__ */ a.jsxs("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: [
    /* @__PURE__ */ a.jsx("p", { children: "用于控制生成文本的大型模型的随机性。" }),
    /* @__PURE__ */ a.jsx("p", { children: "当设置较高时，模型将生成更多样化的文本，增加不确定性；" }),
    /* @__PURE__ */ a.jsx("p", { children: "当设置较低时，模型将生成高概率词，减少不确定性。" })
  ] }), c = /* @__PURE__ */ a.jsxs("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: [
    /* @__PURE__ */ a.jsx("p", { children: "编辑大模型的提示词，实现相应的功能。" }),
    /* @__PURE__ */ a.jsxs("p", { children: [
      "可以使用",
      "{{变量名}}",
      "从输入参数中引入变量。"
    ] })
  ] });
  return /* @__PURE__ */ a.jsx(J, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["modelPanel"], children: /* @__PURE__ */ a.jsx(
    Gi,
    {
      header: /* @__PURE__ */ a.jsx("div", { className: "panel-header", children: /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "大模型" }) }),
      className: "jade-panel",
      children: /* @__PURE__ */ a.jsxs(
        k,
        {
          name: `modelForm-${r.id}`,
          layout: "vertical",
          className: "jade-form",
          children: [
            /* @__PURE__ */ a.jsxs(de, { gutter: 16, children: [
              /* @__PURE__ */ a.jsx(Y, { span: 12, children: /* @__PURE__ */ a.jsx(
                k.Item,
                {
                  className: "jade-form-item",
                  name: "model",
                  label: "模型",
                  initialValue: i.value,
                  children: /* @__PURE__ */ a.jsx(
                    he,
                    {
                      className: "jade-select",
                      onClick: n,
                      onChange: (p) => o({ actionType: "changeConfig", id: i.id, value: p }),
                      options: e
                    }
                  )
                }
              ) }),
              /* @__PURE__ */ a.jsx(Y, { span: 12, children: /* @__PURE__ */ a.jsx(
                k.Item,
                {
                  className: "jade-form-item",
                  name: "temperature",
                  label: /* @__PURE__ */ a.jsxs("div", { style: { display: "flex", alignItems: "center" }, children: [
                    /* @__PURE__ */ a.jsx("span", { className: "jade-second-title", children: "温度" }),
                    /* @__PURE__ */ a.jsx(Ce, { content: d, children: /* @__PURE__ */ a.jsx(we, { className: "jade-panel-header-popover-content" }) })
                  ] }),
                  rules: [{ required: !0, message: "请输入0-1之间的参数!" }],
                  initialValue: s.value,
                  children: /* @__PURE__ */ a.jsx(
                    Wn,
                    {
                      className: "jade-input",
                      style: { width: "100%" },
                      min: 0,
                      max: 1,
                      step: 0.1,
                      onChange: (p) => o({
                        actionType: "changeConfig",
                        id: s.id,
                        value: p
                      }),
                      stringMode: !0
                    }
                  )
                }
              ) })
            ] }),
            /* @__PURE__ */ a.jsx(de, { gutter: 16, children: /* @__PURE__ */ a.jsx(Y, { span: 24, children: /* @__PURE__ */ a.jsx(
              k.Item,
              {
                className: "jade-form-item",
                name: "propmt",
                label: /* @__PURE__ */ a.jsxs("div", { style: { display: "flex", alignItems: "center" }, children: [
                  /* @__PURE__ */ a.jsx("span", { className: "jade-second-title", children: "提示词模板" }),
                  /* @__PURE__ */ a.jsx(Ce, { content: [c], children: /* @__PURE__ */ a.jsx(we, { className: "jade-panel-header-popover-content" }) })
                ] }),
                rules: [{ required: !0, message: "参数不能为空" }],
                initialValue: l.value,
                children: /* @__PURE__ */ a.jsx(
                  Zi,
                  {
                    className: "jade-input jade-font-size",
                    onChange: (p) => o({
                      actionType: "changePrompt",
                      id: l.id,
                      value: p.target.value
                    }),
                    placeholder: "你可以用{{variable name}}来关联输入中的变量名",
                    autoSize: { minRows: 4, maxRows: 4 }
                  }
                )
              }
            ) }) })
          ]
        }
      )
    },
    "modelPanel"
  ) });
}
const { Panel: Xi } = J;
Fr.propTypes = {
  items: L.array.isRequired,
  // 确保 items 是一个必需的数组类型
  addItem: L.func.isRequired,
  // 确保 addItem 是一个必需的函数类型
  updateItem: L.func.isRequired,
  // 确保 updateItem 是一个必需的函数类型
  deleteItem: L.func.isRequired
  // 确保 deleteItem 是一个必需的函数类型
};
function Fr({ items: e, addItem: r, updateItem: t, deleteItem: o }) {
  const i = te(), s = () => {
    r(_());
  }, l = (f, g, m) => {
    const y = [{ key: f, value: g }];
    f === "from" && (y.push({ key: "value", value: "" }), y.push({ key: "referenceNode", value: "" }), y.push({ key: "referenceId", value: "" }), y.push({ key: "referenceKey", value: "" }), document.activeElement.blur()), t(m, y);
  }, n = (f, g) => {
    t(f.id, [{ key: "referenceKey", value: g }]);
  }, d = (f, g) => {
    t(f.id, [
      { key: "referenceNode", value: g.referenceNode },
      { key: "referenceId", value: g.referenceId },
      { key: "value", value: g.value }
    ]);
  }, c = (f) => {
    o(f);
  }, p = /* @__PURE__ */ a.jsx("div", { children: /* @__PURE__ */ a.jsx("p", { children: "输入需要添加到提示词模板中的信息，可被提示词模板引用" }) }), h = (f) => {
    f.stopPropagation();
  }, v = (f) => {
    switch (f.from) {
      case "Reference":
        return /* @__PURE__ */ a.jsx(
          k.Item,
          {
            id: `value-${f.id}`,
            children: /* @__PURE__ */ a.jsx(
              et,
              {
                className: "value-custom jade-select",
                reference: f,
                onReferencedValueChange: (g) => n(f, g),
                onReferencedKeyChange: (g) => d(f, g)
              }
            )
          }
        );
      case "Input":
        return /* @__PURE__ */ a.jsx(
          k.Item,
          {
            id: `value-${f.id}`,
            name: `value-${f.id}`,
            rules: [{ required: !0, message: "字段值不能为空" }, { pattern: /^[^\s]*$/, message: "禁止输入空格" }],
            initialValue: f.value,
            children: /* @__PURE__ */ a.jsx(
              be,
              {
                className: "value-custom jade-input",
                value: f.value,
                onChange: (g) => l("value", g.target.value, f.id)
              }
            )
          }
        );
      default:
        return /* @__PURE__ */ a.jsx(a.Fragment, {});
    }
  };
  return /* @__PURE__ */ a.jsx(J, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["inputPanel"], children: /* @__PURE__ */ a.jsx(
    Xi,
    {
      header: /* @__PURE__ */ a.jsxs("div", { className: "panel-header", children: [
        /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "输入" }),
        /* @__PURE__ */ a.jsx(Ce, { content: p, children: /* @__PURE__ */ a.jsx(we, { className: "jade-panel-header-popover-content" }) }),
        /* @__PURE__ */ a.jsx(
          se,
          {
            type: "text",
            className: "icon-button",
            style: { height: "22px", marginLeft: "76%", marginRight: "0" },
            onClick: (f) => {
              s(), h(f);
            },
            children: /* @__PURE__ */ a.jsx(yt, {})
          }
        )
      ] }),
      className: "jade-panel",
      children: /* @__PURE__ */ a.jsxs(
        k,
        {
          name: `inputForm-${i.id}`,
          layout: "vertical",
          className: "jade-form",
          children: [
            /* @__PURE__ */ a.jsxs(de, { gutter: 16, children: [
              /* @__PURE__ */ a.jsx(Y, { span: 8, children: /* @__PURE__ */ a.jsx(k.Item, { children: /* @__PURE__ */ a.jsx("span", { className: "jade-font-size jade-font-color", children: "字段名称" }) }) }),
              /* @__PURE__ */ a.jsx(Y, { span: 16, children: /* @__PURE__ */ a.jsx(k.Item, { children: /* @__PURE__ */ a.jsx("span", { className: "jade-font-size jade-font-color", children: "字段值" }) }) })
            ] }),
            e.map((f) => /* @__PURE__ */ a.jsxs(
              de,
              {
                gutter: 16,
                children: [
                  /* @__PURE__ */ a.jsx(Y, { span: 8, children: /* @__PURE__ */ a.jsx(
                    k.Item,
                    {
                      id: `name-${f.id}`,
                      name: `name-${f.id}`,
                      rules: [
                        {
                          pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/,
                          message: "只能包含字母、数字或下划线，且必须以字母或下划线开头"
                        }
                      ],
                      initialValue: f.name,
                      children: /* @__PURE__ */ a.jsx(
                        be,
                        {
                          className: "jade-input",
                          style: { paddingRight: "12px" },
                          value: f.name,
                          onChange: (g) => l("name", g.target.value, f.id)
                        }
                      )
                    }
                  ) }),
                  /* @__PURE__ */ a.jsx(Y, { span: 6, style: { paddingRight: 0 }, children: /* @__PURE__ */ a.jsx(
                    k.Item,
                    {
                      id: `from-${f.id}`,
                      initialValue: "Reference",
                      children: /* @__PURE__ */ a.jsx(
                        he,
                        {
                          id: `from-select-${f.id}`,
                          className: "value-source-custom jade-select",
                          style: { width: "100%" },
                          onChange: (g) => l("from", g, f.id),
                          options: [
                            { value: "Reference", label: "引用" },
                            { value: "Input", label: "输入" }
                          ],
                          value: f.from
                        }
                      )
                    }
                  ) }),
                  /* @__PURE__ */ a.jsxs(Y, { span: 8, style: { paddingLeft: 0 }, children: [
                    v(f),
                    " "
                  ] }),
                  /* @__PURE__ */ a.jsx(Y, { span: 2, style: { paddingLeft: 0 }, children: /* @__PURE__ */ a.jsx(k.Item, { children: /* @__PURE__ */ a.jsx(
                    se,
                    {
                      type: "text",
                      className: "icon-button",
                      style: { height: "100%" },
                      onClick: () => c(f.id),
                      children: /* @__PURE__ */ a.jsx(fr, {})
                    }
                  ) }) })
                ]
              },
              f.id
            ))
          ]
        }
      )
    },
    "inputPanel"
  ) });
}
const { Panel: Qi } = J;
function es() {
  const e = te(), r = G(), o = r.outputParams, i = /* @__PURE__ */ a.jsx("div", { className: "jade-font-size", children: /* @__PURE__ */ a.jsx("p", { children: "大模型运行完成后生成的内容。" }) });
  return /* @__PURE__ */ a.jsx(J, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["outputPanel"], children: /* @__PURE__ */ a.jsx(
    Qi,
    {
      header: /* @__PURE__ */ a.jsxs(
        "div",
        {
          className: "panel-header",
          style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
          children: [
            /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "输出" }),
            /* @__PURE__ */ a.jsx(Ce, { content: i, children: /* @__PURE__ */ a.jsx(we, { className: "jade-panel-header-popover-content" }) })
          ]
        }
      ),
      className: "jade-panel",
      children: /* @__PURE__ */ a.jsx(
        k,
        {
          name: `outputForm-${e.id}`,
          layout: "vertical",
          className: "jade-form",
          children: /* @__PURE__ */ a.jsx(tt, { data: o })
        }
      )
    },
    "outputPanel"
  ) });
}
const { Panel: ts } = J;
_r.propTypes = {
  toolOptions: L.array.isRequired,
  // 确保 toolOptions 是一个必需的array类型
  workflowOptions: L.array.isRequired,
  // 确保 workflowOptions 是一个必需的array类型
  config: L.object.isRequired
  // 确保 config 是一个必需的object类型
};
function _r({ toolOptions: e, workflowOptions: r, config: t }) {
  const o = te(), i = G(), s = Q(), l = i.inputParams.find((h) => h.name === "tools"), n = i.inputParams.find((h) => h.name === "workflows"), d = (h) => {
    !t || !t.params || !t.params.tenantId || !t.params.appId ? console.error("Cannot get config.params.tenantId or config.params.appId.") : (window.open("/aipp/" + t.params.tenantId + "/addFlow/" + t.params.appId, "_blank"), h.stopPropagation());
  }, c = (h, v) => ((v == null ? void 0 : v.label) ?? "").toLowerCase().includes(h.toLowerCase()), p = (h, v) => {
    s({ actionType: "changeSkillConfig", id: h, value: v });
  };
  return /* @__PURE__ */ a.jsx(J, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["skillPanel"], children: /* @__PURE__ */ a.jsx(
    ts,
    {
      header: /* @__PURE__ */ a.jsx(
        "div",
        {
          className: "panel-header",
          style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
          children: /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "技能" })
        }
      ),
      className: "jade-panel",
      children: /* @__PURE__ */ a.jsxs(
        k,
        {
          name: `skillForm-${o.id}`,
          layout: "vertical",
          className: "jade-form",
          children: [
            /* @__PURE__ */ a.jsx(de, { gutter: 16, style: { marginBottom: "6px", marginRight: 0, marginLeft: "-3%" }, children: /* @__PURE__ */ a.jsx(Y, { span: 21, children: /* @__PURE__ */ a.jsx("span", { className: "jade-font-size jade-font-color", style: { marginLeft: "6px" }, children: "工具" }) }) }),
            /* @__PURE__ */ a.jsx(k.Item, { children: /* @__PURE__ */ a.jsx(
              he,
              {
                mode: "multiple",
                showSearch: !0,
                allowClear: !0,
                className: "jade-select",
                placeholder: "选择合适的工具",
                filterOption: c,
                optionFilterProp: "label",
                value: l.value,
                onMouseDown: (h) => h.stopPropagation(),
                onChange: (h) => p(l.id, h),
                options: [
                  // Todo 获取对应值
                  { value: "tool1", label: "查天气" },
                  { value: "tool2", label: "查新闻" },
                  { value: "tool3", label: "查电影" }
                ]
              }
            ) }),
            /* @__PURE__ */ a.jsxs(de, { gutter: 16, style: { marginBottom: "6px", marginRight: 0, marginLeft: "-3%" }, children: [
              /* @__PURE__ */ a.jsx(Y, { span: 22, children: /* @__PURE__ */ a.jsx("span", { className: "jade-font-size jade-font-color", style: { marginLeft: "6px" }, children: "工具流" }) }),
              /* @__PURE__ */ a.jsx(Y, { span: 2, style: { paddingLeft: "3%" }, children: /* @__PURE__ */ a.jsx(
                se,
                {
                  type: "text",
                  className: "icon-button",
                  style: { height: "22px" },
                  onClick: (h) => {
                    d(h);
                  },
                  children: /* @__PURE__ */ a.jsx(yt, {})
                }
              ) })
            ] }),
            /* @__PURE__ */ a.jsx(k.Item, { children: /* @__PURE__ */ a.jsx(
              he,
              {
                mode: "multiple",
                showSearch: !0,
                allowClear: !0,
                className: "jade-select",
                placeholder: "选择合适的工具流",
                filterOption: c,
                optionFilterProp: "label",
                value: n.value,
                onChange: (h) => p(n.id, h),
                options: [
                  // Todo 获取对应值
                  { value: "flow1", label: "撰写经营分析报告" },
                  { value: "flow2", label: "发送邮件" },
                  { value: "flow3", label: "面试问题" },
                  { value: "flow4", label: "面试总结" },
                  { value: "flow5", label: "文件提取" }
                ]
              }
            ) })
          ]
        }
      )
    },
    "skillPanel"
  ) });
}
function ns() {
  const e = Q(), r = G(), t = te();
  let o;
  !t || !t.graph || !t.graph.configs ? console.error("Cannot get shape.graph.configs.") : o = t.graph.configs.find((g) => g.node === "llmNodeState");
  const [i, s] = ie([]), [l, n] = ie([]), [d, c] = ie([]), p = () => r.inputParams.filter((g) => g.name === "prompt").flatMap((g) => g.value).filter((g) => g.name === "variables").flatMap((g) => g.value), h = (g) => {
    e({ actionType: "addInputParam", id: g });
  }, v = (g, m) => {
    e({ actionType: "changeInputParams", id: g, updateParams: m });
  }, f = (g) => {
    e({ actionType: "deleteInputParam", id: g });
  };
  return ye(() => {
    !o || !o.urls ? console.error("Cannot get config.urls.") : (o.urls.llmModelEndpoint ? Te.get(o.urls.llmModelEndpoint + "/model-gateway/v1/models", {}, (g) => s(g.data.map((m) => ({
      value: m.id,
      label: m.id
    })))) : console.error("Cannot get config.urls.llmModelEndpoint."), o.urls.toolListEndpoint ? Te.get(o.urls.toolListEndpoint + "/queryToolGroupsInfo?category=App&tag=Tool&offset=0&limit=10", {}, (g) => n(g.map((m) => ({
      value: m.hash,
      label: m.name
    })))) : console.error("Cannot get config.urls.toolListEndpoint."), o.urls.workflowListEndpoint ? Te.get(o.urls.workflowListEndpoint + "/queryToolGroupsInfo?category=App&tag=Workflow&offset=0&limit=10", {}, (g) => c(g.map((m) => ({
      value: m.hash,
      label: m.name
    })))) : console.error("Cannot get config.urls.workflowListEndpoint."));
  }, []), /* @__PURE__ */ a.jsxs("div", { children: [
    /* @__PURE__ */ a.jsx(Fr, { items: p(), addItem: h, updateItem: v, deleteItem: f }),
    /* @__PURE__ */ a.jsx(Tr, { modelOptions: i }),
    /* @__PURE__ */ a.jsx(_r, { toolOptions: l, workflowOptions: d, config: o }),
    /* @__PURE__ */ a.jsx(es, {})
  ] });
}
const rs = (e) => {
  const r = {};
  return r.getJadeConfig = () => e || {
    inputParams: [
      {
        id: _(),
        name: "model",
        type: "String",
        from: "Input",
        value: ""
      },
      {
        id: _(),
        name: "temperature",
        type: "Number",
        from: "Input",
        value: "0.3"
      },
      {
        id: _(),
        name: "prompt",
        type: "Object",
        from: "Expand",
        value: [
          { id: _(), name: "template", type: "String", from: "Input", value: "" },
          {
            id: _(),
            name: "variables",
            type: "Object",
            from: "Expand",
            value: [
              { id: _(), name: "", type: "String", from: "Reference", value: "", referenceNode: "", referenceId: "", referenceKey: "" }
            ]
          }
        ]
      },
      { id: _(), name: "tools", type: "Array", from: "Input", value: [] },
      { id: _(), name: "workflows", type: "Array", from: "Input", value: [] },
      { id: _(), name: "systemPrompt", type: "String", from: "Input", value: "" }
    ],
    outputParams: [
      {
        id: _(),
        name: "output",
        type: "Object",
        from: "Expand",
        value: [
          { id: _(), name: "llmOutput", type: "string", from: "Input", description: "", value: "" }
        ]
      }
    ]
  }, r.getReactComponents = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(as, {}) }), r.reducers = (t, o) => {
    function i() {
      const v = {};
      return Object.entries(t).forEach(([f, g]) => {
        f === "inputParams" ? v[f] = g.map((m) => m.name === "prompt" ? {
          ...m,
          value: m.value.map((y) => y.name === "variables" ? {
            ...y,
            value: [...y.value, {
              id: o.id,
              name: "",
              type: "String",
              from: "Reference",
              value: ""
            }]
          } : y)
        } : m) : v[f] = g;
      }), v;
    }
    function s() {
      const v = {};
      return Object.entries(t).forEach(([f, g]) => {
        f === "outputParams" ? v[f] = g.map((m) => m.name === "output" ? {
          ...m,
          value: [...m.value, {
            id: o.id,
            name: "",
            type: "string",
            from: "Input",
            description: "",
            value: ""
          }]
        } : m) : v[f] = g;
      }), v;
    }
    function l() {
      const v = {};
      return Object.entries(t).forEach(([f, g]) => {
        f === "inputParams" ? v[f] = g.map((m) => m.name === "prompt" ? {
          ...m,
          value: m.value.map((y) => y.name === "variables" ? {
            ...y,
            value: y.value.map((j) => {
              if (j.id === o.id) {
                let R = { ...j };
                return o.updateParams.map(($) => {
                  R[$.key] = $.value;
                }), R;
              } else
                return j;
            })
          } : y)
        } : m) : v[f] = g;
      }), v;
    }
    function n() {
      const v = {};
      return Object.entries(t).forEach(([f, g]) => {
        f === "inputParams" ? v[f] = g.map((m) => m.name === "prompt" ? {
          ...m,
          value: m.value.map((y) => o.id === y.id && y.name === "template" ? {
            ...y,
            value: o.value
          } : y)
        } : m) : v[f] = g;
      }), v;
    }
    function d() {
      const v = {};
      return Object.entries(t).forEach(([f, g]) => {
        f === "outputParams" ? v[f] = g.map((m) => m.name === "output" ? {
          ...m,
          value: m.value.map((y) => y.id === o.id ? { ...y, [o.type]: o.value } : y)
        } : m) : v[f] = g;
      }), v;
    }
    function c() {
      const v = {};
      return Object.entries(t).forEach(([f, g]) => {
        f === "inputParams" ? v[f] = g.map((m) => m.id === o.id ? {
          ...m,
          value: o.value
        } : m) : v[f] = g;
      }), v;
    }
    function p() {
      const v = {};
      return Object.entries(t).forEach(([f, g]) => {
        f === "inputParams" ? v[f] = g.map((m) => m.id === o.id ? {
          ...m,
          value: o.value.map((y) => ({ id: _(), type: "String", from: "Input", value: y }))
        } : m) : v[f] = g;
      }), v;
    }
    function h() {
      const v = {};
      return Object.entries(t).forEach(([f, g]) => {
        f === "inputParams" ? v[f] = g.map((m) => m.name === "prompt" ? {
          ...m,
          value: m.value.map((y) => y.name === "variables" ? {
            ...y,
            value: y.value.filter((j) => j.id !== o.id)
          } : y)
        } : m) : v[f] = g;
      }), v;
    }
    switch (o.actionType) {
      case "addInputParam":
        return i();
      case "addOutputParam":
        return s();
      case "changeInputParams":
        return l();
      case "changeOutputParam":
        return d();
      case "changeConfig":
        return c();
      case "changeSkillConfig":
        return p();
      case "changePrompt":
        return n();
      case "deleteInputParam":
        return h();
      case "deleteOutputParam":
        return t.filter((v) => v.id !== o.id);
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, r;
}, as = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(ns, {}) }), os = (e) => /* @__PURE__ */ C.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ C.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ C.createElement("defs", null, /* @__PURE__ */ C.createElement("clipPath", { id: "clip4_13297" }, /* @__PURE__ */ C.createElement("rect", { id: "\\u56FE\\u6807/16/\\u68C0\\u67E5\\u5217\\u8868", width: 16, height: 16, transform: "translate(4.000000 4.000000)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ C.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#A97AF8", fillOpacity: 1 }), /* @__PURE__ */ C.createElement("g", { clipPath: "url(#clip4_13297)" }, /* @__PURE__ */ C.createElement("path", { id: "path", d: "M15 7.68L14.98 7.68C14.78 7.48 14.78 7.17 14.98 6.97C15.17 6.78 15.48 6.78 15.68 6.97L15.68 7L15 7.68ZM18.32 9.64L18.35 9.64C18.55 9.84 18.55 10.15 18.35 10.35C18.15 10.55 17.84 10.55 17.64 10.35L17.64 10.32L18.32 9.64Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M15.33 7.33L18 10", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M15.68 10.32L15.68 10.35C15.48 10.55 15.17 10.55 14.98 10.35C14.78 10.15 14.78 9.84 14.98 9.64L15 9.64L15.68 10.32ZM17.64 7L17.64 6.97C17.84 6.78 18.15 6.78 18.35 6.97C18.55 7.17 18.55 7.48 18.35 7.68L18.32 7.68L17.64 7Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M15.33 10L18 7.33", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M18.29 13.69L18.29 13.67C18.47 13.46 18.78 13.43 18.99 13.62C19.2 13.8 19.22 14.11 19.04 14.32L19.01 14.33L18.29 13.69ZM14.67 15.68L14.64 15.68C14.44 15.48 14.44 15.17 14.64 14.97C14.84 14.78 15.15 14.78 15.35 14.97L15.35 15L14.67 15.68Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M18.66 14L16.33 16.66L15 15.33", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M12.66 7.33L5.33 7.33L5.33 10L12.66 10L12.66 7.33Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M5.33 7.33L5.33 10L12.66 10L12.66 7.33L5.33 7.33Z", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M12.66 14L5.33 14L5.33 16.66L12.66 16.66L12.66 14Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M5.33 14L5.33 16.66L12.66 16.66L12.66 14L5.33 14Z", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round" }))), is = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  return n.type = "manualCheckNodeState", n.text = "人工检查", n.pointerEvents = "auto", n.componentName = "manualCheckComponent", n.flowMeta.triggerMode = "manual", n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(
    se,
    {
      disabled: !0,
      className: "jade-node-custom-header-icon",
      children: /* @__PURE__ */ a.jsx(os, {})
    }
  ), n;
};
function ss() {
  return /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(fn, { image: fn.PRESENTED_IMAGE_DEFAULT }) });
}
const { Panel: Dn } = J;
function ls() {
  const e = te(), r = Q(), t = G(), o = te().graph.configs.find((h) => h.node === "manualCheckNodeState"), i = t.inputParams.find((h) => h.name === "formName").value, s = t.outputParams, [l, n] = ie([]);
  ye(() => {
    Te.get(o.urls.runtimeFormUrl, {}, (h) => n(h.data.map((v) => ({
      value: v.name,
      label: v.name
    }))));
  }, []);
  const d = () => i && i.length > 0 ? e.graph.plugins[i]().getReactComponents() : /* @__PURE__ */ a.jsx(ss, {}), c = (h) => {
    let v = "";
    h && h.length > 0 && (v = e.graph.plugins[h]().getJadeConfig()), r({ actionType: "changeFormAndSetOutput", formName: h, formOutput: v });
  }, p = () => !s || !Array.isArray(s) || !s.length > 0 ? null : /* @__PURE__ */ a.jsx(J, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["manualCheckOutputPanel"], children: /* @__PURE__ */ a.jsx(
    Dn,
    {
      header: /* @__PURE__ */ a.jsx(
        "div",
        {
          className: "panel-header",
          style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
          children: /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "输出" })
        }
      ),
      className: "jade-panel",
      children: /* @__PURE__ */ a.jsx(tt, { data: s })
    },
    "manualCheckOutputPanel"
  ) });
  return /* @__PURE__ */ a.jsxs("div", { children: [
    /* @__PURE__ */ a.jsx(J, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["manualCheckFormPanel"], children: /* @__PURE__ */ a.jsx(
      Dn,
      {
        header: /* @__PURE__ */ a.jsx(
          "div",
          {
            className: "panel-header",
            style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
            children: /* @__PURE__ */ a.jsx("span", { className: "jade-panel-header-font", children: "表单" })
          }
        ),
        className: "jade-panel",
        style: { marginBottom: 8, borderRadius: "8px", width: "100%" },
        children: /* @__PURE__ */ a.jsx(
          k,
          {
            name: `manualCheckForm-${e.id}`,
            layout: "vertical",
            className: "jade-form",
            children: /* @__PURE__ */ a.jsxs(k.Item, { children: [
              /* @__PURE__ */ a.jsx(
                he,
                {
                  allowClear: !0,
                  className: "jade-select",
                  defaultValue: i,
                  style: { width: "100%", marginBottom: "8px" },
                  onChange: (h) => c(h),
                  options: l
                }
              ),
              d(),
              " "
            ] })
          }
        )
      },
      "manualCheckFormPanel"
    ) }),
    p()
  ] });
}
const cs = (e) => {
  const r = {};
  return r.getJadeConfig = () => e || {
    inputParams: [
      {
        id: _(),
        name: "formName",
        type: "Object",
        from: "Input",
        value: ""
      }
    ],
    outputParams: []
  }, r.getReactComponents = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(us, {}) }), r.reducers = (t, o) => {
    function i() {
      return {
        ...t,
        inputParams: t.inputParams.map((s) => s.name === "formName" ? {
          ...s,
          value: o.formName
        } : s),
        outputParams: o.formOutput
      };
    }
    switch (o.actionType) {
      case "changeFormAndSetOutput":
        return i();
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, r;
}, us = () => /* @__PURE__ */ a.jsx(a.Fragment, { children: /* @__PURE__ */ a.jsx(ls, {}) }), ds = (e) => /* @__PURE__ */ C.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ C.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ C.createElement("defs", null, /* @__PURE__ */ C.createElement("clipPath", { id: "clip4_13318" }, /* @__PURE__ */ C.createElement("rect", { id: "\\u56FE\\u6807/16/API \\u63A5\\u53E3", width: 16, height: 16, transform: "translate(4.000000 4.000000)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ C.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#FA9941", fillOpacity: 1 }), /* @__PURE__ */ C.createElement("g", { clipPath: "url(#clip4_13318)" }, /* @__PURE__ */ C.createElement("path", { id: "path", d: "M16.33 11.33L15.33 12.33L11.66 8.66L12.66 7.66C13.16 7.16 14.99 6.33 16.33 7.66C17.66 9 16.83 10.83 16.33 11.33Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M15.33 12.33L11.66 8.66L12.66 7.66C13.16 7.16 14.99 6.33 16.33 7.66C17.66 9 16.83 10.83 16.33 11.33L15.33 12.33Z", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M16.68 7.99L16.68 8.02C16.48 8.21 16.17 8.21 15.98 8.02C15.78 7.82 15.78 7.51 15.98 7.31L16 7.31L16.68 7.99ZM17.64 5.67L17.64 5.64C17.84 5.44 18.15 5.44 18.35 5.64C18.55 5.84 18.55 6.15 18.35 6.35L18.32 6.35L17.64 5.67Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M16.33 7.66L18 5.99", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M7.66 12.66L8.66 11.66L12.33 15.33L11.33 16.33C10.83 16.83 9 17.66 7.66 16.33C6.33 15 7.16 13.16 7.66 12.66Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M8.66 11.66L12.33 15.33L11.33 16.33C10.83 16.83 9 17.66 7.66 16.33C6.33 15 7.16 13.16 7.66 12.66L8.66 11.66Z", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M12.01 14.99L12.01 15.02C11.82 15.21 11.51 15.21 11.31 15.02C11.11 14.82 11.11 14.51 11.31 14.31L11.34 14.31L12.01 14.99ZM12.64 13L12.64 12.97C12.84 12.78 13.15 12.78 13.35 12.97C13.55 13.17 13.55 13.48 13.35 13.68L13.32 13.68L12.64 13Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M11.66 14.66L12.99 13.33", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M6.35 18.32L6.35 18.35C6.15 18.55 5.84 18.55 5.64 18.35C5.44 18.15 5.44 17.84 5.64 17.64L5.67 17.64L6.35 18.32ZM7.31 16L7.31 15.97C7.51 15.78 7.82 15.78 8.02 15.97C8.21 16.17 8.21 16.48 8.02 16.68L7.99 16.68L7.31 16Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M6 18L7.66 16.33", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M9.68 12.65L9.68 12.68C9.48 12.88 9.17 12.88 8.98 12.68C8.78 12.48 8.78 12.17 8.98 11.97L9 11.97L9.68 12.65ZM10.31 10.67L10.31 10.64C10.51 10.44 10.82 10.44 11.02 10.64C11.21 10.84 11.21 11.15 11.02 11.35L10.99 11.35L10.31 10.67Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ C.createElement("path", { id: "path", d: "M9.33 12.33L10.66 11", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }))), fs = (e, r, t, o, i, s, l) => {
  const n = ge(e, r, t, o, i, s, l);
  n.type = "fitInvokeNodeState", n.width = 360, n.backColor = "white", n.pointerEvents = "auto", n.text = "FIT调用", n.componentName = "fitInvokeComponent", n.flowMeta.triggerMode = "auto", n.flowMeta.jober.type = "GENERICABLE_JOBER";
  const d = {
    genericable: {
      id: "",
      params: []
    }
  }, c = n.serializerJadeConfig;
  return n.serializerJadeConfig = () => {
    c.apply(n);
    const p = n.flowMeta.jober.converter.entity.fitable.value.find((h) => h.name === "id").value;
    p && (n.flowMeta.jober.fitables = [p]), d.genericable.params = n.flowMeta.jober.converter.entity.inputParams.map((h) => ({ name: h.name })), d.genericable.id = n.flowMeta.jober.converter.entity.genericable.value.find((h) => h.name === "id").value, n.flowMeta.jober.entity = d;
  }, n.getHeaderIcon = () => /* @__PURE__ */ a.jsx(se, { disabled: !0, className: "jade-node-custom-header-icon", children: /* @__PURE__ */ a.jsx(ds, {}) }), n;
};
function ps() {
  const e = Q(), r = G(), t = r && r.inputParams, o = (i, s) => {
    e({ type: "update", id: i, changes: s });
  };
  return /* @__PURE__ */ a.jsx(jr, { data: t, updateItem: o });
}
function ms() {
  const e = Q(), r = te(), t = r.graph.configs && r.graph.configs.find((l) => l.node === "fitInvokeState").urls.serviceListEndpoint, o = (l) => t + "?pageNum=" + l + "&pageSize=10", i = (l, n) => {
    e({ type: "selectGenericable", value: l });
  }, s = (l) => l.map((n) => ({
    value: n,
    label: n
  }));
  return /* @__PURE__ */ a.jsx(
    k.Item,
    {
      name: "必选",
      label: /* @__PURE__ */ a.jsx("span", { style: { color: "red" } }),
      rules: [{ required: !0, message: "请选择一个服务" }],
      colon: !1,
      children: /* @__PURE__ */ a.jsx(
        Zt,
        {
          className: "jade-select-genericable",
          placeholder: "请选择一个服务",
          onChange: i,
          buildUrl: o,
          disabled: !1,
          getOptions: s,
          dealResponse: (l) => l.data
        }
      )
    }
  );
}
function hs() {
  const e = Q(), r = G(), t = te(), o = r && r.genericable.value, i = t.graph.configs.find((c) => c.node === "fitInvokeState").urls.fitableMetaInfoUrl, s = o.find((c) => c.name === "id").value === "", l = (c) => i + o + "?pageNum=" + c + "pageSize=10", n = (c, p) => {
    e({ type: "selectFitable", value: p.find((h) => h.name === c) });
  }, d = (c) => c.map((p) => ({
    value: p.name,
    label: p.name
  }));
  return /* @__PURE__ */ a.jsx(
    k.Item,
    {
      name: "必选",
      label: /* @__PURE__ */ a.jsx("span", { style: { color: "red" } }),
      rules: [{ required: !0, message: "请选择一个服务，再选择实现" }],
      colon: !1,
      children: /* @__PURE__ */ a.jsx(
        Zt,
        {
          className: "jade-select-tool",
          placeholder: "请选择一个服务，再选择实现",
          onChange: n,
          buildUrl: l,
          disabled: s,
          getOptions: d,
          dealResponse: (c) => c.data
        }
      )
    }
  );
}
const { Panel: gs } = J;
function vs() {
  return /* @__PURE__ */ a.jsx(
    J,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      defaultActiveKey: ["FitInvokeService"],
      children: /* @__PURE__ */ a.jsxs(
        gs,
        {
          className: "jade-panel",
          header: /* @__PURE__ */ a.jsx("div", { style: { display: "flex", alignItems: "center" }, children: /* @__PURE__ */ a.jsx("span", { className: "title", children: "FIT服务" }) }),
          children: [
            /* @__PURE__ */ a.jsx("div", { style: { marginTop: "8px" }, children: /* @__PURE__ */ a.jsx("span", { className: "select-genericable", children: "选择服务" }) }),
            /* @__PURE__ */ a.jsx(ms, {}),
            /* @__PURE__ */ a.jsx("div", { style: { marginTop: "8px" }, children: /* @__PURE__ */ a.jsx("span", { className: "select-fitable", children: "选择实现" }) }),
            /* @__PURE__ */ a.jsx(hs, {})
          ]
        },
        "FitInvokeService"
      )
    }
  );
}
const { Panel: ys } = J;
function bs() {
  const e = G(), r = e && e.outputParams;
  return /* @__PURE__ */ a.jsx(
    J,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      defaultActiveKey: ["FitInvokeOutput"],
      children: /* @__PURE__ */ a.jsx(
        ys,
        {
          className: "jade-panel",
          header: /* @__PURE__ */ a.jsx("div", { style: { display: "flex", alignItems: "center" }, children: /* @__PURE__ */ a.jsx("span", { className: "title", children: "输出" }) }),
          children: /* @__PURE__ */ a.jsx(tt, { data: r })
        },
        "FitInvokeOutput"
      )
    }
  );
}
const xs = (e) => {
  const r = {};
  r.getJadeConfig = () => e || {
    inputParams: [],
    genericable: {
      id: "genericable_" + _(),
      name: "genericable",
      type: "Object",
      from: "Expand",
      // 保存当前选中的Genericable信息
      value: [{ id: _(), name: "id", type: "String", from: "Input", value: "" }]
    },
    fitable: {
      id: "fitable_" + _(),
      name: "fitable",
      type: "Object",
      from: "Expand",
      // 保存当前选中的fitable信息
      value: [{ id: _(), name: "id", type: "String", from: "Input", value: "" }]
    },
    outputParams: []
  }, r.getReactComponents = () => /* @__PURE__ */ a.jsxs(a.Fragment, { children: [
    /* @__PURE__ */ a.jsx(ps, {}),
    /* @__PURE__ */ a.jsx(vs, {}),
    /* @__PURE__ */ a.jsx(bs, {})
  ] });
  const t = (s) => {
    const l = {
      id: "output_" + _(),
      name: "output",
      type: "",
      value: []
    };
    if (s.return.type === "object") {
      l.type = "Object";
      const n = s.return.properties;
      for (const d in n) {
        const c = n[d];
        c.type === "object" ? l.value.push(...o(d, c)) : l.value.push({
          id: _(),
          name: d,
          type: c.type.capitalize(),
          value: c.type.capitalize()
        });
      }
    } else
      l.type = s.return.type.capitalize();
    return l;
  };
  function o(s, l) {
    const n = [];
    if (l.type === "object")
      for (const d in l.properties) {
        const c = l.properties[d];
        c.type === "object" ? n.push(...o(d, c)) : n.push({
          id: _(),
          name: d,
          type: c.type.capitalize(),
          value: c.type.capitalize()
        });
      }
    return [{
      id: "output_" + _(),
      name: s,
      type: "Object",
      value: n
    }];
  }
  const i = (s) => {
    const l = {
      id: s.name + "_" + _(),
      name: s.name,
      type: s.parameter.type === "object" ? "Object" : s.parameter.type.capitalize(),
      // 对象默认展开
      from: s.parameter.type === "object" ? "Expand" : "Reference",
      referenceNode: "",
      referenceId: "",
      referenceKey: "",
      value: []
    };
    if (s.parameter.type === "object") {
      const n = s.parameter.properties;
      l.value = Object.keys(n).map((d) => i({
        name: d,
        parameter: n[d]
      })), l.props = [...l.value];
    }
    return l;
  };
  return r.reducers = (s, l) => {
    const n = (f, g, m) => f.map((y) => {
      const j = { ...y };
      return y.id === g ? (m.forEach((R) => {
        j[R.key] = R.value;
      }), j) : (j.type === "Object" && Array.isArray(j.value) && j.from !== "Reference" && (j.value = n(j.value, g, m)), j);
    }), d = () => {
      v.genericable.value.find((f) => f.name === "id").value = l.value, v.fitable.value.find((f) => f.name === "id").value = "";
    }, c = () => {
      v.fitable.value.find((f) => f.name === "id").value = l.value.schema.parameters.fitableId;
    }, p = () => {
      const f = l.value, g = t(f.schema);
      delete v.outputParams, v.outputParams = g;
    }, h = () => {
      const f = l.value, g = Object.keys(f.schema.parameters.properties).map((m) => i({
        name: m,
        parameter: f.schema.parameters.properties[m]
      }));
      delete v.inputParams, v.inputParams = g;
    };
    let v = { ...s };
    switch (l.type) {
      case "generateInput":
        return h(), v;
      case "selectGenericable":
        return d(), v;
      case "selectFitable":
        return c(), h(), p(), v;
      case "update":
        return v.inputParams = n(s.inputParams, l.id, l.changes), v;
      default:
        throw Error("Unknown action: " + l.type);
    }
  }, r;
}, $n = (e, r) => {
  const t = la(e, r);
  t.type = "jadeFlowGraph", t.pageType = "jadeFlowPage", t.enableText = !1, t.flowMeta = {
    exceptionFitables: ["com.huawei.fit.jober.aipp.fitable.AippFlowExceptionHandler"]
  }, t.setting.borderColor = "#047bfc", t.setting.focusBorderColor = "#047bfc", t.setting.mouseInBorderColor = "#047bfc";
  const o = t.serialize;
  t.serialize = () => {
    const l = o.apply(t);
    return l.flowMeta = t.flowMeta, l;
  };
  const i = t.initialize;
  t.initialize = async () => (t.registerPlugin("jadeFlowPage", ha), t.registerPlugin("jadeEvent", va), t.registerPlugin("taskNode", Bo), t.registerPlugin("endNodeEnd", qo), t.registerPlugin("endComponent", Xo), t.registerPlugin("retrievalNodeState", ci), t.registerPlugin("retrievalComponent", ui), t.registerPlugin("listener1Node", di), t.registerPlugin("listener1Component", fi), t.registerPlugin("listener2Node", mi), t.registerPlugin("listener2Component", hi), t.registerPlugin("listener3Node", vi), t.registerPlugin("listener3Component", yi), t.registerPlugin("jadeInputTreeNode", xi), t.registerPlugin("jadeInputTreeComponent", _i), t.registerPlugin("testNode", Mi), t.registerPlugin("testComponent", Ai), t.registerPlugin("replaceComponent", $i), t.registerPlugin("startNodeStart", qi), t.registerPlugin("startComponent", Hi), t.registerPlugin("llmNodeState", Ji), t.registerPlugin("llmComponent", rs), t.registerPlugin("manualCheckNodeState", is), t.registerPlugin("manualCheckComponent", cs), t.registerPlugin("fitInvokeNodeState", fs), t.registerPlugin("fitInvokeComponent", xs), i.apply(t)), t.registerPlugin = (l, n, d = null) => {
    d ? t.plugins[`${d}.${l}`] = n : t.plugins[l] = n;
  };
  const s = t.dirtied;
  return t.dirtied = (l, n) => {
    s.call(t, l, n), t.onChangeCallback && t.onChangeCallback();
  }, t;
}, Bn = (e) => {
  const r = {};
  return r.graph = e, r.want = (t, o) => {
    e.activePage.want(t, o);
  }, r.import = (t) => r.graph.staticImport(t), r.serialize = () => (e.activePage.serialize(), e.serialize()), r.getAvailableNodes = () => [
    { type: "retrievalNodeState", name: "数据检索" },
    { type: "llmNodeState", name: "大模型" },
    { type: "manualCheckNodeState", name: "人工检查" },
    { type: "fitInvokeNodeState", name: "FIT调用" }
  ], r.createNode = (t, o) => {
    console.log("call createNode...");
    const i = e.activePage.calculatePosition(o);
    e.activePage.createNew(t, i.x, i.y);
  }, r.createNodeByPosition = (t, o) => {
    console.log("call createNodeByPosition..."), e.activePage.createNew(t, o.x, o.y);
  }, r.onChange = (t) => {
    e.onChangeCallback = t;
  }, r.getNodeConfigs = () => e.activePage.shapes.filter((t) => t.isTypeof("jadeNode")).map((t) => ({
    [t.id]: t.getLatestJadeConfig()
  })), r.validate = () => {
    e.activePage.shapes.filter((t) => t.isTypeof("jadeNode")).forEach((t) => t.validate());
  }, r;
}, Ps = (() => {
  const e = {};
  return e.new = async (r, t) => {
    const o = $n(r, "jadeFlow");
    o.configs = t, o.collaboration.mute = !0, await o.initialize();
    const i = o.addPage("newFlowPage"), s = i.createShape("startNodeStart", 100, 100), l = i.createShape("endNodeEnd", s.x + s.width + 200, 100), n = i.createNew("jadeEvent", 0, 0);
    return i.reset(), n.connect(s.id, "E", l.id, "W"), i.fillScreen(), Bn(o);
  }, e.edit = async (r, t, o) => {
    const i = $n(r, "jadeFlow");
    i.configs = o, i.collaboration.mute = !0, await i.initialize(), i.addPage("newFlowPage"), i.deSerialize(t);
    const s = i.getPageData(0);
    return await i.edit(0, r, s.id), Bn(i);
  }, e;
})();
export {
  Ps as JadeFlow
};
//# sourceMappingURL=fit-elsa-react.js.map
