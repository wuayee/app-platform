import './style.css';
import { page as fa, uuid as pa, line as ma, LINEMODE as ha, isPointInRect as ga, node as va, DIRECTION as Fe, CopyPasteHelpers as ya, rectangleDrawer as ba, connector as xa, defaultGraph as ja } from "@fit-elsa/elsa-core";
import * as w from "react";
import vt, { useState as de, useRef as rt, useEffect as ge, createContext as at, useContext as ot, useReducer as Ca } from "react";
import wa from "react-dom";
import { Form as F, Input as xe, Dropdown as Ea, Button as se, TreeSelect as Na, Row as fe, Col as U, Select as Yn, Collapse as G, Popover as Se, Slider as Jt, Tree as Zn, ConfigProvider as Sa, InputNumber as Yt, Empty as vn, Switch as Ra } from "antd";
import ka from "axios";
const Pa = (e, a, t, o) => {
  const i = fa(e, a, t, o);
  i.type = "jadeFlowPage", i.serializedFields.batchAdd("x", "y", "scaleX", "scaleY"), i.namespace = "jadeFlow", i.backgroundGrid = "point", i.backgroundGridSize = 16, i.backgroundGridMargin = 16, i.backColor = "#fbfbfc", i.focusBackColor = "#fbfbfc", i.gridColor = "#e1e1e3", i.disableContextMenu = !0, i.moveAble = !0, i.observableStore = Ia();
  const s = i.onLoaded;
  i.onLoaded = () => {
    s.apply(i), i.shapes.forEach((c) => c.onPageLoaded && c.onPageLoaded());
  };
  const l = i.onCopy;
  i.onCopy = (c) => {
    const f = c.filter((g) => !g.isUnique && !g.isTypeof("jadeEvent"));
    return l.apply(i, [f]);
  }, i.registerObservable = (c, f, g, v, h) => {
    i.observableStore.add(c, f, g, v, h);
  }, i.removeObservable = (c, f = null) => {
    i.observableStore.remove(c, f);
  }, i.getObservableList = (c) => i.observableStore.getObservableList(c), i.observeTo = (c, f, g) => {
    i.observableStore.addObserver(c, f, g);
  }, i.stopObserving = (c, f, g) => {
    i.observableStore.removeObserver(c, f, g);
  }, i.getObservable = (c, f) => i.observableStore.getObservable(c, f);
  const n = i.clear;
  i.clear = () => {
    n.apply(i), i.observableStore.clear();
  };
  const d = (c) => (c == null || c === "" ? c = "jade" + pa() : c.startsWith("jade") || (c = "jade" + c), c), u = i.createNew;
  i.createNew = (c, f, g, v, h, y, b, x) => {
    m.filter((I) => I.type === "before").forEach((I) => I.handle(i, c, f, g, h, y));
    const N = u.apply(i, [c, f, g, v, h, y, b, x]);
    return m.filter((I) => I.type === "after").forEach((I) => I.handle(i, N)), N;
  };
  const m = [];
  return i.registerShapeCreationHandler = (c) => {
    m.push(c);
  }, i.getMenuScript = () => [], i.registerShapeCreationHandler({
    type: "before",
    handle: (c, f) => {
      if (f === "startNodeStart" && c.shapes.find((g) => g.type === f))
        throw new Error("最多只能有一个开始或结束节点.");
    }
  }), i.registerShapeCreationHandler({
    type: "after",
    handle: (c, f) => {
      const g = c.shapes.filter((b) => b.isTypeof("jadeNode")), v = g.map((b) => b.text);
      if (!v.find((b) => b === f.text))
        return;
      const h = "_";
      if (g.filter((b) => b.type === f.type).length <= 1)
        return;
      let y = 1;
      for (; ; ) {
        const b = f.text.lastIndexOf(h), x = f.text.substring(b + 1, f.text.length);
        if (isNaN(parseInt(x)) ? f.text = f.text + h + y : f.text = f.text.substring(0, b) + h + y, !v.includes(f.text))
          return;
        y++;
      }
    }
  }), i.registerShapeCreationHandler({
    type: "after",
    handle: (c, f) => {
      f.type !== "jadeEvent" && (f.id = d(f.id));
    }
  }), i;
}, Ia = () => {
  const e = {};
  e.store = /* @__PURE__ */ new Map(), e.add = (t, o, i, s, l) => {
    const n = a(e.store, t, () => /* @__PURE__ */ new Map()), d = a(n, o, () => ({
      observableId: o,
      value: null,
      type: null,
      observers: [],
      parentId: l
    }));
    d.value = i, d.type = s, d.parentId = l, d.observers.length > 0 && d.observers.forEach((u) => u.observe({ value: i, type: s }));
  }, e.remove = (t, o = null) => {
    if (o) {
      const i = e.store.get(t);
      i && o && (i.delete(o), i.size === 0 && e.store.delete(t));
    } else
      e.store.delete(t);
  }, e.addObserver = (t, o, i) => {
    const s = a(e.store, t, () => /* @__PURE__ */ new Map());
    a(s, o, () => ({
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
  const a = (t, o, i) => {
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
let Oa = (e, a, t, o, i, s, l) => {
  let n = ma(e, a, t, o, i, s, l);
  n.type = "jadeEvent", n.borderWidth = 1, n.beginArrow = !1, n.endArrow = !0, n.lineMode = ha.AUTO_CURVE, n.borderColor = "#B1B1B7", n.mouseInBorderColor = "#B1B1B7", n.allowSwitchLineMode = !1;
  const d = n.getIndex;
  n.getIndex = () => {
    let c = d.call(n);
    return n.index = c - 200, n.index;
  }, n.onPageLoaded = () => {
    n.toShape && (n.currentToShape = n.page.getShapeById(n.toShape));
  };
  const u = n.initConnectors;
  n.initConnectors = () => {
    u.call(n), n.fromConnector.visible = !1, n.toConnector.direction.color = "transparent", n.toConnector.strokeStyle = "transparent";
    const c = n.toConnector.release;
    n.toConnector.release = (g) => {
      const v = n.fromShape, h = n.fromShapeConnector;
      function y() {
        return n.page.shapes.filter((b) => b.type === "jadeEvent").count((b) => b.fromShape === v && b.toShape === n.connectingShape.id && b.fromShapeConnector === h && b.toShapeConnector === n.connectingShape.linkingConnector) > 1;
      }
      if (n.isFocused = !1, c.call(n.toConnector, g), y())
        n.remove();
      else if (n.toShape === "")
        n.currentToShape && n.currentToShape.offConnect(), n.remove();
      else {
        const b = n.getToShape();
        b.onConnect(), n.currentToShape = b;
      }
    }, n.toConnector.radius = 4, n.toConnector.moving = (g, v, h, y) => {
      let b = n.page.disableReact;
      n.page.disableReact = !0;
      const x = n.from();
      n.resize(h - x.x, y - x.y), n.shapeLinking(n.to().x, n.to().y), n.toMoving = !0, n.page.disableReact = b, n.toConnector.afterMoving();
    };
    const f = n.toConnector.afterMoving;
    n.toConnector.afterMoving = () => {
      n.connectingShape && n.connectingShape.linkingConnector ? (n.definedToConnector = n.connectingShape.linkingConnector.direction.key, n.toShape = n.connectingShape.id) : (n.definedToConnector = "", n.toShape = ""), f.apply(n.toConnector);
    };
  };
  const m = n.remove;
  return n.remove = (c) => {
    const f = n.getToShape(), g = m.apply(n, [c]);
    return f && f.offConnect(), g;
  }, n.connect = (c, f, g, v) => {
    n.fromShape = c, n.definedFromConnector = f, n.toShape = g, n.definedToConnector = v, n.follow();
  }, n.validateLinking = (c, f, g) => ga({ x: f, y: g }, c.getBound()), n.shapeLinking = (c, f) => {
    if (!n.linkAble)
      return;
    const g = n.page.find(c, f, (v) => v.allowLink && n.validateLinking(v, c, f));
    n.shapeDelinking(g), n.connectingShape = g, n.connectingShape && n.connectingShape !== n.page && (n.connectingShape.linking = !0, n.connectingShape.linkingConnector = n.connectingShape.getClosestConnector(c, f, (v) => v.connectable && v.allowToLink), n.connectingShape.render());
  }, n.needGetToConnector = () => n.getToShape(), n;
};
function Gn(e) {
  return e && e.__esModule && Object.prototype.hasOwnProperty.call(e, "default") ? e.default : e;
}
var zt = { exports: {} }, Ge = {};
/**
 * @license React
 * react-jsx-runtime.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var yn;
function Ta() {
  if (yn)
    return Ge;
  yn = 1;
  var e = vt, a = Symbol.for("react.element"), t = Symbol.for("react.fragment"), o = Object.prototype.hasOwnProperty, i = e.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner, s = { key: !0, ref: !0, __self: !0, __source: !0 };
  function l(n, d, u) {
    var m, c = {}, f = null, g = null;
    u !== void 0 && (f = "" + u), d.key !== void 0 && (f = "" + d.key), d.ref !== void 0 && (g = d.ref);
    for (m in d)
      o.call(d, m) && !s.hasOwnProperty(m) && (c[m] = d[m]);
    if (n && n.defaultProps)
      for (m in d = n.defaultProps, d)
        c[m] === void 0 && (c[m] = d[m]);
    return { $$typeof: a, type: n, key: f, ref: g, props: c, _owner: i.current };
  }
  return Ge.Fragment = t, Ge.jsx = l, Ge.jsxs = l, Ge;
}
var Xe = {};
/**
 * @license React
 * react-jsx-runtime.development.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var bn;
function Fa() {
  return bn || (bn = 1, process.env.NODE_ENV !== "production" && function() {
    var e = vt, a = Symbol.for("react.element"), t = Symbol.for("react.portal"), o = Symbol.for("react.fragment"), i = Symbol.for("react.strict_mode"), s = Symbol.for("react.profiler"), l = Symbol.for("react.provider"), n = Symbol.for("react.context"), d = Symbol.for("react.forward_ref"), u = Symbol.for("react.suspense"), m = Symbol.for("react.suspense_list"), c = Symbol.for("react.memo"), f = Symbol.for("react.lazy"), g = Symbol.for("react.offscreen"), v = Symbol.iterator, h = "@@iterator";
    function y(p) {
      if (p === null || typeof p != "object")
        return null;
      var j = v && p[v] || p[h];
      return typeof j == "function" ? j : null;
    }
    var b = e.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED;
    function x(p) {
      {
        for (var j = arguments.length, R = new Array(j > 1 ? j - 1 : 0), L = 1; L < j; L++)
          R[L - 1] = arguments[L];
        N("error", p, R);
      }
    }
    function N(p, j, R) {
      {
        var L = b.ReactDebugCurrentFrame, Z = L.getStackAddendum();
        Z !== "" && (j += "%s", R = R.concat([Z]));
        var ee = R.map(function(J) {
          return String(J);
        });
        ee.unshift("Warning: " + j), Function.prototype.apply.call(console[p], console, ee);
      }
    }
    var I = !1, C = !1, ae = !1, ye = !1, Me = !1, Ie;
    Ie = Symbol.for("react.module.reference");
    function Ae(p) {
      return !!(typeof p == "string" || typeof p == "function" || p === o || p === s || Me || p === i || p === u || p === m || ye || p === g || I || C || ae || typeof p == "object" && p !== null && (p.$$typeof === f || p.$$typeof === c || p.$$typeof === l || p.$$typeof === n || p.$$typeof === d || // This needs to include all possible module reference object
      // types supported by any Flight configuration anywhere since
      // we don't know which Flight build this will end up being used
      // with.
      p.$$typeof === Ie || p.getModuleId !== void 0));
    }
    function Oe(p, j, R) {
      var L = p.displayName;
      if (L)
        return L;
      var Z = j.displayName || j.name || "";
      return Z !== "" ? R + "(" + Z + ")" : R;
    }
    function Te(p) {
      return p.displayName || "Context";
    }
    function S(p) {
      if (p == null)
        return null;
      if (typeof p.tag == "number" && x("Received an unexpected object in getComponentNameFromType(). This is likely a bug in React. Please file an issue."), typeof p == "function")
        return p.displayName || p.name || null;
      if (typeof p == "string")
        return p;
      switch (p) {
        case o:
          return "Fragment";
        case t:
          return "Portal";
        case s:
          return "Profiler";
        case i:
          return "StrictMode";
        case u:
          return "Suspense";
        case m:
          return "SuspenseList";
      }
      if (typeof p == "object")
        switch (p.$$typeof) {
          case n:
            var j = p;
            return Te(j) + ".Consumer";
          case l:
            var R = p;
            return Te(R._context) + ".Provider";
          case d:
            return Oe(p, p.render, "ForwardRef");
          case c:
            var L = p.displayName || null;
            return L !== null ? L : S(p.type) || "Memo";
          case f: {
            var Z = p, ee = Z._payload, J = Z._init;
            try {
              return S(J(ee));
            } catch {
              return null;
            }
          }
        }
      return null;
    }
    var O = Object.assign, _ = 0, B, te, le, Ve, E, P, V;
    function D() {
    }
    D.__reactDisabledLog = !0;
    function A() {
      {
        if (_ === 0) {
          B = console.log, te = console.info, le = console.warn, Ve = console.error, E = console.group, P = console.groupCollapsed, V = console.groupEnd;
          var p = {
            configurable: !0,
            enumerable: !0,
            value: D,
            writable: !0
          };
          Object.defineProperties(console, {
            info: p,
            log: p,
            warn: p,
            error: p,
            group: p,
            groupCollapsed: p,
            groupEnd: p
          });
        }
        _++;
      }
    }
    function K() {
      {
        if (_--, _ === 0) {
          var p = {
            configurable: !0,
            enumerable: !0,
            writable: !0
          };
          Object.defineProperties(console, {
            log: O({}, p, {
              value: B
            }),
            info: O({}, p, {
              value: te
            }),
            warn: O({}, p, {
              value: le
            }),
            error: O({}, p, {
              value: Ve
            }),
            group: O({}, p, {
              value: E
            }),
            groupCollapsed: O({}, p, {
              value: P
            }),
            groupEnd: O({}, p, {
              value: V
            })
          });
        }
        _ < 0 && x("disabledDepth fell below zero. This is a bug in React. Please file an issue.");
      }
    }
    var $ = b.ReactCurrentDispatcher, z;
    function q(p, j, R) {
      {
        if (z === void 0)
          try {
            throw Error();
          } catch (Z) {
            var L = Z.stack.trim().match(/\n( *(at )?)/);
            z = L && L[1] || "";
          }
        return `
` + z + p;
      }
    }
    var Y = !1, W;
    {
      var ue = typeof WeakMap == "function" ? WeakMap : Map;
      W = new ue();
    }
    function k(p, j) {
      if (!p || Y)
        return "";
      {
        var R = W.get(p);
        if (R !== void 0)
          return R;
      }
      var L;
      Y = !0;
      var Z = Error.prepareStackTrace;
      Error.prepareStackTrace = void 0;
      var ee;
      ee = $.current, $.current = null, A();
      try {
        if (j) {
          var J = function() {
            throw Error();
          };
          if (Object.defineProperty(J.prototype, "props", {
            set: function() {
              throw Error();
            }
          }), typeof Reflect == "object" && Reflect.construct) {
            try {
              Reflect.construct(J, []);
            } catch (ke) {
              L = ke;
            }
            Reflect.construct(p, [], J);
          } else {
            try {
              J.call();
            } catch (ke) {
              L = ke;
            }
            p.call(J.prototype);
          }
        } else {
          try {
            throw Error();
          } catch (ke) {
            L = ke;
          }
          p();
        }
      } catch (ke) {
        if (ke && L && typeof ke.stack == "string") {
          for (var H = ke.stack.split(`
`), pe = L.stack.split(`
`), oe = H.length - 1, ie = pe.length - 1; oe >= 1 && ie >= 0 && H[oe] !== pe[ie]; )
            ie--;
          for (; oe >= 1 && ie >= 0; oe--, ie--)
            if (H[oe] !== pe[ie]) {
              if (oe !== 1 || ie !== 1)
                do
                  if (oe--, ie--, ie < 0 || H[oe] !== pe[ie]) {
                    var Ce = `
` + H[oe].replace(" at new ", " at ");
                    return p.displayName && Ce.includes("<anonymous>") && (Ce = Ce.replace("<anonymous>", p.displayName)), typeof p == "function" && W.set(p, Ce), Ce;
                  }
                while (oe >= 1 && ie >= 0);
              break;
            }
        }
      } finally {
        Y = !1, $.current = ee, K(), Error.prepareStackTrace = Z;
      }
      var Ue = p ? p.displayName || p.name : "", gn = Ue ? q(Ue) : "";
      return typeof p == "function" && W.set(p, gn), gn;
    }
    function je(p, j, R) {
      return k(p, !1);
    }
    function qe(p) {
      var j = p.prototype;
      return !!(j && j.isReactComponent);
    }
    function Be(p, j, R) {
      if (p == null)
        return "";
      if (typeof p == "function")
        return k(p, qe(p));
      if (typeof p == "string")
        return q(p);
      switch (p) {
        case u:
          return q("Suspense");
        case m:
          return q("SuspenseList");
      }
      if (typeof p == "object")
        switch (p.$$typeof) {
          case d:
            return je(p.render);
          case c:
            return Be(p.type, j, R);
          case f: {
            var L = p, Z = L._payload, ee = L._init;
            try {
              return Be(ee(Z), j, R);
            } catch {
            }
          }
        }
      return "";
    }
    var lt = Object.prototype.hasOwnProperty, rn = {}, an = b.ReactDebugCurrentFrame;
    function ct(p) {
      if (p) {
        var j = p._owner, R = Be(p.type, p._source, j ? j.type : null);
        an.setExtraStackFrame(R);
      } else
        an.setExtraStackFrame(null);
    }
    function Hr(p, j, R, L, Z) {
      {
        var ee = Function.call.bind(lt);
        for (var J in p)
          if (ee(p, J)) {
            var H = void 0;
            try {
              if (typeof p[J] != "function") {
                var pe = Error((L || "React class") + ": " + R + " type `" + J + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + typeof p[J] + "`.This often happens because of typos such as `PropTypes.function` instead of `PropTypes.func`.");
                throw pe.name = "Invariant Violation", pe;
              }
              H = p[J](j, J, L, R, null, "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED");
            } catch (oe) {
              H = oe;
            }
            H && !(H instanceof Error) && (ct(Z), x("%s: type specification of %s `%s` is invalid; the type checker function must return `null` or an `Error` but returned a %s. You may have forgotten to pass an argument to the type checker creator (arrayOf, instanceOf, objectOf, oneOf, oneOfType, and shape all require an argument).", L || "React class", R, J, typeof H), ct(null)), H instanceof Error && !(H.message in rn) && (rn[H.message] = !0, ct(Z), x("Failed %s type: %s", R, H.message), ct(null));
          }
      }
    }
    var Kr = Array.isArray;
    function wt(p) {
      return Kr(p);
    }
    function Jr(p) {
      {
        var j = typeof Symbol == "function" && Symbol.toStringTag, R = j && p[Symbol.toStringTag] || p.constructor.name || "Object";
        return R;
      }
    }
    function Yr(p) {
      try {
        return on(p), !1;
      } catch {
        return !0;
      }
    }
    function on(p) {
      return "" + p;
    }
    function sn(p) {
      if (Yr(p))
        return x("The provided key is an unsupported type %s. This value must be coerced to a string before before using it here.", Jr(p)), on(p);
    }
    var Ze = b.ReactCurrentOwner, Zr = {
      key: !0,
      ref: !0,
      __self: !0,
      __source: !0
    }, ln, cn, Et;
    Et = {};
    function Gr(p) {
      if (lt.call(p, "ref")) {
        var j = Object.getOwnPropertyDescriptor(p, "ref").get;
        if (j && j.isReactWarning)
          return !1;
      }
      return p.ref !== void 0;
    }
    function Xr(p) {
      if (lt.call(p, "key")) {
        var j = Object.getOwnPropertyDescriptor(p, "key").get;
        if (j && j.isReactWarning)
          return !1;
      }
      return p.key !== void 0;
    }
    function Qr(p, j) {
      if (typeof p.ref == "string" && Ze.current && j && Ze.current.stateNode !== j) {
        var R = S(Ze.current.type);
        Et[R] || (x('Component "%s" contains the string ref "%s". Support for string refs will be removed in a future major release. This case cannot be automatically converted to an arrow function. We ask you to manually fix this case by using useRef() or createRef() instead. Learn more about using refs safely here: https://reactjs.org/link/strict-mode-string-ref', S(Ze.current.type), p.ref), Et[R] = !0);
      }
    }
    function ea(p, j) {
      {
        var R = function() {
          ln || (ln = !0, x("%s: `key` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", j));
        };
        R.isReactWarning = !0, Object.defineProperty(p, "key", {
          get: R,
          configurable: !0
        });
      }
    }
    function ta(p, j) {
      {
        var R = function() {
          cn || (cn = !0, x("%s: `ref` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", j));
        };
        R.isReactWarning = !0, Object.defineProperty(p, "ref", {
          get: R,
          configurable: !0
        });
      }
    }
    var na = function(p, j, R, L, Z, ee, J) {
      var H = {
        // This tag allows us to uniquely identify this as a React Element
        $$typeof: a,
        // Built-in properties that belong on the element
        type: p,
        key: j,
        ref: R,
        props: J,
        // Record the component responsible for creating this element.
        _owner: ee
      };
      return H._store = {}, Object.defineProperty(H._store, "validated", {
        configurable: !1,
        enumerable: !1,
        writable: !0,
        value: !1
      }), Object.defineProperty(H, "_self", {
        configurable: !1,
        enumerable: !1,
        writable: !1,
        value: L
      }), Object.defineProperty(H, "_source", {
        configurable: !1,
        enumerable: !1,
        writable: !1,
        value: Z
      }), Object.freeze && (Object.freeze(H.props), Object.freeze(H)), H;
    };
    function ra(p, j, R, L, Z) {
      {
        var ee, J = {}, H = null, pe = null;
        R !== void 0 && (sn(R), H = "" + R), Xr(j) && (sn(j.key), H = "" + j.key), Gr(j) && (pe = j.ref, Qr(j, Z));
        for (ee in j)
          lt.call(j, ee) && !Zr.hasOwnProperty(ee) && (J[ee] = j[ee]);
        if (p && p.defaultProps) {
          var oe = p.defaultProps;
          for (ee in oe)
            J[ee] === void 0 && (J[ee] = oe[ee]);
        }
        if (H || pe) {
          var ie = typeof p == "function" ? p.displayName || p.name || "Unknown" : p;
          H && ea(J, ie), pe && ta(J, ie);
        }
        return na(p, H, pe, Z, L, Ze.current, J);
      }
    }
    var Nt = b.ReactCurrentOwner, un = b.ReactDebugCurrentFrame;
    function We(p) {
      if (p) {
        var j = p._owner, R = Be(p.type, p._source, j ? j.type : null);
        un.setExtraStackFrame(R);
      } else
        un.setExtraStackFrame(null);
    }
    var St;
    St = !1;
    function Rt(p) {
      return typeof p == "object" && p !== null && p.$$typeof === a;
    }
    function dn() {
      {
        if (Nt.current) {
          var p = S(Nt.current.type);
          if (p)
            return `

Check the render method of \`` + p + "`.";
        }
        return "";
      }
    }
    function aa(p) {
      {
        if (p !== void 0) {
          var j = p.fileName.replace(/^.*[\\\/]/, ""), R = p.lineNumber;
          return `

Check your code at ` + j + ":" + R + ".";
        }
        return "";
      }
    }
    var fn = {};
    function oa(p) {
      {
        var j = dn();
        if (!j) {
          var R = typeof p == "string" ? p : p.displayName || p.name;
          R && (j = `

Check the top-level render call using <` + R + ">.");
        }
        return j;
      }
    }
    function pn(p, j) {
      {
        if (!p._store || p._store.validated || p.key != null)
          return;
        p._store.validated = !0;
        var R = oa(j);
        if (fn[R])
          return;
        fn[R] = !0;
        var L = "";
        p && p._owner && p._owner !== Nt.current && (L = " It was passed a child from " + S(p._owner.type) + "."), We(p), x('Each child in a list should have a unique "key" prop.%s%s See https://reactjs.org/link/warning-keys for more information.', R, L), We(null);
      }
    }
    function mn(p, j) {
      {
        if (typeof p != "object")
          return;
        if (wt(p))
          for (var R = 0; R < p.length; R++) {
            var L = p[R];
            Rt(L) && pn(L, j);
          }
        else if (Rt(p))
          p._store && (p._store.validated = !0);
        else if (p) {
          var Z = y(p);
          if (typeof Z == "function" && Z !== p.entries)
            for (var ee = Z.call(p), J; !(J = ee.next()).done; )
              Rt(J.value) && pn(J.value, j);
        }
      }
    }
    function ia(p) {
      {
        var j = p.type;
        if (j == null || typeof j == "string")
          return;
        var R;
        if (typeof j == "function")
          R = j.propTypes;
        else if (typeof j == "object" && (j.$$typeof === d || // Note: Memo only checks outer props here.
        // Inner props are checked in the reconciler.
        j.$$typeof === c))
          R = j.propTypes;
        else
          return;
        if (R) {
          var L = S(j);
          Hr(R, p.props, "prop", L, p);
        } else if (j.PropTypes !== void 0 && !St) {
          St = !0;
          var Z = S(j);
          x("Component %s declared `PropTypes` instead of `propTypes`. Did you misspell the property assignment?", Z || "Unknown");
        }
        typeof j.getDefaultProps == "function" && !j.getDefaultProps.isReactClassApproved && x("getDefaultProps is only used on classic React.createClass definitions. Use a static property named `defaultProps` instead.");
      }
    }
    function sa(p) {
      {
        for (var j = Object.keys(p.props), R = 0; R < j.length; R++) {
          var L = j[R];
          if (L !== "children" && L !== "key") {
            We(p), x("Invalid prop `%s` supplied to `React.Fragment`. React.Fragment can only have `key` and `children` props.", L), We(null);
            break;
          }
        }
        p.ref !== null && (We(p), x("Invalid attribute `ref` supplied to `React.Fragment`."), We(null));
      }
    }
    function hn(p, j, R, L, Z, ee) {
      {
        var J = Ae(p);
        if (!J) {
          var H = "";
          (p === void 0 || typeof p == "object" && p !== null && Object.keys(p).length === 0) && (H += " You likely forgot to export your component from the file it's defined in, or you might have mixed up default and named imports.");
          var pe = aa(Z);
          pe ? H += pe : H += dn();
          var oe;
          p === null ? oe = "null" : wt(p) ? oe = "array" : p !== void 0 && p.$$typeof === a ? (oe = "<" + (S(p.type) || "Unknown") + " />", H = " Did you accidentally export a JSX literal instead of a component?") : oe = typeof p, x("React.jsx: type is invalid -- expected a string (for built-in components) or a class/function (for composite components) but got: %s.%s", oe, H);
        }
        var ie = ra(p, j, R, Z, ee);
        if (ie == null)
          return ie;
        if (J) {
          var Ce = j.children;
          if (Ce !== void 0)
            if (L)
              if (wt(Ce)) {
                for (var Ue = 0; Ue < Ce.length; Ue++)
                  mn(Ce[Ue], p);
                Object.freeze && Object.freeze(Ce);
              } else
                x("React.jsx: Static children should always be an array. You are likely explicitly calling React.jsxs or React.jsxDEV. Use the Babel transform instead.");
            else
              mn(Ce, p);
        }
        return p === o ? sa(ie) : ia(ie), ie;
      }
    }
    function la(p, j, R) {
      return hn(p, j, R, !0);
    }
    function ca(p, j, R) {
      return hn(p, j, R, !1);
    }
    var ua = ca, da = la;
    Xe.Fragment = o, Xe.jsx = ua, Xe.jsxs = da;
  }()), Xe;
}
process.env.NODE_ENV === "production" ? zt.exports = Ta() : zt.exports = Fa();
var r = zt.exports, tt = {}, Qe = wa;
if (process.env.NODE_ENV === "production")
  tt.createRoot = Qe.createRoot, tt.hydrateRoot = Qe.hydrateRoot;
else {
  var ut = Qe.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED;
  tt.createRoot = function(e, a) {
    ut.usingClientEntryPoint = !0;
    try {
      return Qe.createRoot(e, a);
    } finally {
      ut.usingClientEntryPoint = !1;
    }
  }, tt.hydrateRoot = function(e, a, t) {
    ut.usingClientEntryPoint = !0;
    try {
      return Qe.hydrateRoot(e, a, t);
    } finally {
      ut.usingClientEntryPoint = !1;
    }
  };
}
const Xn = ({ shape: e }) => {
  const [a, t] = de(!1), o = rt(null), [i, s] = de(0);
  ge(() => {
    o.current && o.current.focus({
      cursor: "end"
    });
  }), ge(() => {
    e.page.addEventListener("TOOL_MENU_CHANGE", (m) => {
      e.type === "endNodeEnd" && s(m[0]);
    });
  }, []);
  const l = () => {
    o.current.input.value !== "" && (e.text = o.current.input.value, t(!1));
  }, n = (m) => {
    e.getToolMenus().find((f) => f.key === m.key).action(t);
  }, d = () => a ? /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(F.Item, { name: "title", rules: [{ required: !0, message: "请输入名称" }], initialValue: e.text, children: /* @__PURE__ */ r.jsx(
    xe,
    {
      onBlur: (m) => l(),
      ref: o,
      placeholder: "请输入名称",
      style: { height: "24px", borderColor: e.focusBorderColor }
    }
  ) }) }) : /* @__PURE__ */ r.jsx("p", { style: { margin: 0 }, children: /* @__PURE__ */ r.jsx("span", { children: e.text }) }), u = () => {
    if (e.getToolMenus().length > 0) {
      const m = e.getToolMenus().map((c) => ({ key: c.key, label: c.label }));
      return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx("div", { children: /* @__PURE__ */ r.jsx(Ea, { menu: { items: m, onClick: (c) => n(c) }, placement: "bottomRight", children: /* @__PURE__ */ r.jsx(se, { type: "text", size: "small", style: {
        margin: 0,
        padding: 0,
        width: "28px",
        height: "28px",
        display: "flex",
        alignItems: "center",
        justifyContent: "center"
      }, children: /* @__PURE__ */ r.jsx(
        "svg",
        {
          xmlns: "http://www.w3.org/2000/svg",
          width: "16",
          height: "16",
          fill: "none",
          viewBox: "0 0 16 16",
          children: /* @__PURE__ */ r.jsx(
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
    return /* @__PURE__ */ r.jsx(r.Fragment, {});
  };
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsxs("div", { className: "react-node-header", children: [
    /* @__PURE__ */ r.jsxs("div", { className: "react-node-toolbar", style: { alignItems: "center" }, children: [
      /* @__PURE__ */ r.jsx("div", { style: { display: "flex", alignItems: "center" }, children: e.getHeaderIcon() }),
      /* @__PURE__ */ r.jsx("div", { className: "react-node-toolbar-name", children: d() }),
      u()
    ] }),
    /* @__PURE__ */ r.jsx("span", { className: "react-node-header-description", children: e.description })
  ] }) });
}, Qn = at(null), er = at(null), tr = at(null), nr = at(null), La = ({ shape: e, component: a }) => {
  const [t, o] = Ca(a.reducers, a.getJadeConfig()), i = "react-root-" + e.id, [s] = F.useForm();
  return e.getLatestJadeConfig = () => JSON.parse(JSON.stringify(t)), e.validateForm = () => s.validateFields(), ge(() => {
    e.observe();
  }, []), ge(() => {
    e.graph.onChangeCallback && e.graph.onChangeCallback();
  }, [t]), /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx("div", { id: i, style: { display: "block" }, children: /* @__PURE__ */ r.jsxs(
    F,
    {
      form: s,
      name: `form-${e.id}`,
      layout: "vertical",
      className: "jade-form",
      children: [
        e.getHeaderComponent(),
        /* @__PURE__ */ r.jsx(nr.Provider, { value: s, children: /* @__PURE__ */ r.jsx(er.Provider, { value: e, children: /* @__PURE__ */ r.jsx(Qn.Provider, { value: t, children: /* @__PURE__ */ r.jsx(tr.Provider, { value: o, children: /* @__PURE__ */ r.jsx("div", { className: "react-node-content", style: { borderRadius: e.borderRadius + "px" }, children: a.getReactComponents() }) }) }) }) })
      ]
    }
  ) }) });
};
function ne() {
  return ot(Qn);
}
function he() {
  return ot(er);
}
function re() {
  return ot(tr);
}
function Ke() {
  return ot(nr);
}
let dt;
const _a = new Uint8Array(16);
function Ma() {
  if (!dt && (dt = typeof crypto < "u" && crypto.getRandomValues && crypto.getRandomValues.bind(crypto), !dt))
    throw new Error("crypto.getRandomValues() not supported. See https://github.com/uuidjs/uuid#getrandomvalues-not-supported");
  return dt(_a);
}
const ce = [];
for (let e = 0; e < 256; ++e)
  ce.push((e + 256).toString(16).slice(1));
function Aa(e, a = 0) {
  return ce[e[a + 0]] + ce[e[a + 1]] + ce[e[a + 2]] + ce[e[a + 3]] + "-" + ce[e[a + 4]] + ce[e[a + 5]] + "-" + ce[e[a + 6]] + ce[e[a + 7]] + "-" + ce[e[a + 8]] + ce[e[a + 9]] + "-" + ce[e[a + 10]] + ce[e[a + 11]] + ce[e[a + 12]] + ce[e[a + 13]] + ce[e[a + 14]] + ce[e[a + 15]];
}
const Ba = typeof crypto < "u" && crypto.randomUUID && crypto.randomUUID.bind(crypto), xn = {
  randomUUID: Ba
};
function T(e, a, t) {
  if (xn.randomUUID && !a && !e)
    return xn.randomUUID();
  e = e || {};
  const o = e.random || (e.rng || Ma)();
  if (o[6] = o[6] & 15 | 64, o[8] = o[8] & 63 | 128, a) {
    t = t || 0;
    for (let i = 0; i < 16; ++i)
      a[t + i] = o[i];
    return a;
  }
  return Aa(o);
}
const ve = (e, a, t, o, i, s, l) => {
  const n = va(e, a, t, o, i, s, !1, l || za);
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
  n.getToolMenus = () => [{
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
    action: (h) => {
      h(!0);
    }
  }];
  const u = n.initConnectors;
  n.initConnectors = () => {
    u.apply(n), n.connectors.remove((h) => h.direction.key === Fe.S.key || h.direction.key === Fe.N.key || h.direction.key === "ROTATE"), n.connectors.forEach((h) => {
      h.isSolid = !0, h.direction.key === Fe.W.key && (h.allowFromLink = !1), h.direction.key === Fe.E.key && (h.allowToLink = !1);
    });
  }, n.getPreNodeInfos = () => {
    if (!n.allowToLink)
      return [];
    const h = n.page.shapes.filter((N) => N.type === "jadeEvent"), y = [], b = /* @__PURE__ */ new Set(), x = (N) => {
      if (b.has(N))
        return;
      b.add(N);
      const I = n.page.getShapeById(N);
      if (!I)
        return;
      y.push({
        id: I.id,
        node: I,
        name: I.text,
        observableList: I.page.getObservableList(I.id)
      });
      const C = h.filter((ae) => ae.toShape === N);
      for (const ae of C)
        x(ae.fromShape);
    };
    return x(n.id), y.shift(), y;
  }, n.observe = () => {
    n.drawer.observe();
  }, n.getHeaderComponent = () => /* @__PURE__ */ r.jsx(Xn, { shape: n }), n.getComponent = () => n.graph.plugins[n.componentName](n.flowMeta.jober.converter.entity), n.emit = (h, { value: y, type: b }) => {
    const x = n.page.getObservable(n.id, h);
    x && (x.observers.forEach((N) => {
      N.status === "enable" && N.observe({
        value: y ?? x.value,
        type: b ?? x.type
      });
    }), y != null && (x.value = y), b != null && (x.type = b));
  };
  const m = n.remove;
  n.remove = (h) => {
    const b = n.page.shapes.filter((N) => N.isTypeof("jadeEvent")).filter((N) => N.fromShape === n.id || N.toShape === n.id).flatMap((N) => N.remove()), x = m.apply(n, [h]);
    return n.cleanObservables(), [...x, ...b];
  }, n.duplicate = () => {
    const h = JSON.stringify([n.serialize()]);
    ya.pasteShapes(h, "", n.page);
  };
  const c = n.serialize;
  n.serialize = () => (n.getLatestJadeConfig && n.serializerJadeConfig(), c.apply(n)), n.serializerJadeConfig = () => {
    n.flowMeta.jober.converter.entity = n.getLatestJadeConfig();
  }, n.addDetection(["componentName"], (h, y, b) => {
    y !== b && (n.drawer.unmountReact(), n.invalidateAlone());
  });
  const f = (h) => {
    typeof h == "object" && h !== null && (Array.isArray(h) ? h.forEach((y) => {
      f(y);
    }) : Object.keys(h).forEach((y) => {
      y === "id" ? h[y] = T() : f(h[y]);
    }));
  };
  n.pasted = () => {
    f(n.getEntity());
  }, n.getEntity = () => n.flowMeta.jober.converter.entity, n.getHeaderIcon = () => {
  }, n.observeTo = (h, y, b) => {
    const x = n.getPreNodeInfos(), N = new Set(x.map((ae) => ae.id)), I = $a(h, y, b);
    I.status = N.has(h) ? "enable" : "disable", d.push(I), n.page.observeTo(h, y, I);
    const C = n.page.getObservable(h, y);
    return I.observe({ value: C.value, type: C.type }), () => {
      const ae = d.findIndex((ye) => ye === I);
      d.splice(ae, 1), n.page.stopObserving(h, y, I);
    };
  }, n.offConnect = () => {
    const h = n.getPreNodeInfos(), y = new Set(h.map((x) => x.id));
    d.filter((x) => x.status === "enable").filter((x) => !y.has(x.nodeId)).forEach((x) => {
      x.observe({ value: null, type: null }), x.status = "disable";
    });
    const b = g();
    b.length > 0 && b.forEach((x) => x.offConnect());
  }, n.onConnect = () => {
    const h = n.getPreNodeInfos(), y = new Set(h.map((x) => x.id));
    d.filter((x) => x.status === "disable").filter((x) => y.has(x.nodeId)).forEach((x) => {
      x.status = "enable";
      const N = n.page.getObservable(x.nodeId, x.observableId);
      x.observe({ value: N.value, type: N.type });
    });
    const b = g();
    b.length > 0 && b.forEach((x) => x.onConnect());
  }, n.cleanObservables = () => {
    n.page.removeObservable(n.id), d.forEach((h) => n.page.stopObserving(h.nodeId, h.observableId, h));
  };
  const g = () => {
    const h = n.page.shapes.filter((y) => y.type === "jadeEvent").filter((y) => y.fromShape === n.id);
    return !h || h.length === 0 ? [] : n.page.shapes.filter((y) => y.type !== "jadeEvent").filter((y) => h.some((b) => b.toShape === y.id));
  };
  n.validate = () => new Promise((h, y) => {
    try {
      const b = n.getPreNodeInfos(), x = new Set(b.map((N) => N.id));
      d.forEach((N) => {
        const I = n.page.getShapeById(N.nodeId);
        if (!I)
          throw new Error("节点[" + N.nodeId + "]不存在.");
        if (!x.has(N.nodeId))
          throw new Error("节点[" + I.text + "]和节点[" + n.text + "]未连接.");
      }), n.validateForm().then(h).catch(y);
    } catch (b) {
      y(b);
    }
  });
  const v = n.unSelect;
  return n.unSelect = () => {
    v.apply(n), n.validateForm && n.validateForm();
  }, n;
}, $a = (e, a, t) => {
  const o = {};
  return o.nodeId = e, o.observableId = a, o.status = "enable", o.origin = t, o.observe = (i) => {
    o.status === "enable" && o.origin(i);
  }, o;
}, za = (e, a, t, o) => {
  const i = ba(e, a, t, o);
  i.reactContainer = null;
  const s = i.initialize;
  i.initialize = () => {
    s.apply(i), i.reactContainer = document.createElement("div"), i.reactContainer.id = "react-container-" + e.id, i.reactContainer.style.padding = "12px", i.reactContainer.style.width = "100%", i.reactContainer.style.borderRadius = e.borderRadius + "px", i.parent.appendChild(i.reactContainer), i.parent.style.pointerEvents = "auto";
  }, i.unmountReact = () => {
    i.root && (i.root.unmount(), i.root = null);
  }, i.processMetaData = (u) => {
  }, i.drawStatic = () => {
    !e.componentName || i.root || (i.root = tt.createRoot(i.reactContainer), i.root.render(/* @__PURE__ */ r.jsx(La, { shape: e, component: e.getComponent() })));
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
    new ResizeObserver((u) => {
      d !== i.parent.offsetHeight && e.container !== "" && (e.resize(e.width, i.parent.offsetHeight), d = i.parent.offsetHeight);
    }).observe(i.parent);
  }, i;
};
function Da() {
  const e = ne();
  return /* @__PURE__ */ r.jsx("ul", { children: e.map((a) => /* @__PURE__ */ r.jsx("li", { children: /* @__PURE__ */ r.jsx(Va, { task: a }) }, a.id)) });
}
function Va({ task: e }) {
  const [a, t] = de(!1), o = re();
  let i;
  return a ? i = /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
    /* @__PURE__ */ r.jsx(
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
    /* @__PURE__ */ r.jsx("button", { onClick: () => t(!1), children: "Save" })
  ] }) : i = /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
    e.text,
    /* @__PURE__ */ r.jsx("button", { onClick: () => t(!0), children: "Edit" })
  ] }), /* @__PURE__ */ r.jsxs("label", { children: [
    /* @__PURE__ */ r.jsx(
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
    /* @__PURE__ */ r.jsx("button", { onClick: () => {
      o({
        type: "deleted",
        id: e.id
      });
    }, children: "Delete" })
  ] });
}
function qa() {
  const [e, a] = de(""), t = re(), o = he();
  return /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
    /* @__PURE__ */ r.jsx(
      "input",
      {
        placeholder: "Add task",
        value: e,
        onChange: (i) => a(i.target.value)
      }
    ),
    /* @__PURE__ */ r.jsx("button", { onClick: () => {
      a(""), console.log(o.serialize()), t({
        type: "added",
        id: Wa++,
        text: e
      });
    }, children: "Add" })
  ] });
}
let Wa = 3;
var Ua = /* @__PURE__ */ at({});
const rr = Ua;
function _e() {
  return _e = Object.assign ? Object.assign.bind() : function(e) {
    for (var a = 1; a < arguments.length; a++) {
      var t = arguments[a];
      for (var o in t)
        Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
    }
    return e;
  }, _e.apply(this, arguments);
}
function Ha(e) {
  if (Array.isArray(e))
    return e;
}
function Ka(e, a) {
  var t = e == null ? null : typeof Symbol < "u" && e[Symbol.iterator] || e["@@iterator"];
  if (t != null) {
    var o, i, s, l, n = [], d = !0, u = !1;
    try {
      if (s = (t = t.call(e)).next, a === 0) {
        if (Object(t) !== t)
          return;
        d = !1;
      } else
        for (; !(d = (o = s.call(t)).done) && (n.push(o.value), n.length !== a); d = !0)
          ;
    } catch (m) {
      u = !0, i = m;
    } finally {
      try {
        if (!d && t.return != null && (l = t.return(), Object(l) !== l))
          return;
      } finally {
        if (u)
          throw i;
      }
    }
    return n;
  }
}
function jn(e, a) {
  (a == null || a > e.length) && (a = e.length);
  for (var t = 0, o = new Array(a); t < a; t++)
    o[t] = e[t];
  return o;
}
function Ja(e, a) {
  if (e) {
    if (typeof e == "string")
      return jn(e, a);
    var t = Object.prototype.toString.call(e).slice(8, -1);
    if (t === "Object" && e.constructor && (t = e.constructor.name), t === "Map" || t === "Set")
      return Array.from(e);
    if (t === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(t))
      return jn(e, a);
  }
}
function Ya() {
  throw new TypeError(`Invalid attempt to destructure non-iterable instance.
In order to be iterable, non-array objects must have a [Symbol.iterator]() method.`);
}
function ar(e, a) {
  return Ha(e) || Ka(e, a) || Ja(e, a) || Ya();
}
function ze(e) {
  "@babel/helpers - typeof";
  return ze = typeof Symbol == "function" && typeof Symbol.iterator == "symbol" ? function(a) {
    return typeof a;
  } : function(a) {
    return a && typeof Symbol == "function" && a.constructor === Symbol && a !== Symbol.prototype ? "symbol" : typeof a;
  }, ze(e);
}
function Za(e, a) {
  if (ze(e) != "object" || !e)
    return e;
  var t = e[Symbol.toPrimitive];
  if (t !== void 0) {
    var o = t.call(e, a || "default");
    if (ze(o) != "object")
      return o;
    throw new TypeError("@@toPrimitive must return a primitive value.");
  }
  return (a === "string" ? String : Number)(e);
}
function Ga(e) {
  var a = Za(e, "string");
  return ze(a) == "symbol" ? a : a + "";
}
function Dt(e, a, t) {
  return a = Ga(a), a in e ? Object.defineProperty(e, a, {
    value: t,
    enumerable: !0,
    configurable: !0,
    writable: !0
  }) : e[a] = t, e;
}
function Xa(e, a) {
  if (e == null)
    return {};
  var t = {}, o = Object.keys(e), i, s;
  for (s = 0; s < o.length; s++)
    i = o[s], !(a.indexOf(i) >= 0) && (t[i] = e[i]);
  return t;
}
function or(e, a) {
  if (e == null)
    return {};
  var t = Xa(e, a), o, i;
  if (Object.getOwnPropertySymbols) {
    var s = Object.getOwnPropertySymbols(e);
    for (i = 0; i < s.length; i++)
      o = s[i], !(a.indexOf(o) >= 0) && Object.prototype.propertyIsEnumerable.call(e, o) && (t[o] = e[o]);
  }
  return t;
}
var ir = { exports: {} };
/*!
	Copyright (c) 2018 Jed Watson.
	Licensed under the MIT License (MIT), see
	http://jedwatson.github.io/classnames
*/
(function(e) {
  (function() {
    var a = {}.hasOwnProperty;
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
        a.call(s, n) && s[n] && (l = i(l, n));
      return l;
    }
    function i(s, l) {
      return l ? s ? s + " " + l : s + l : s;
    }
    e.exports ? (t.default = t, e.exports = t) : window.classNames = t;
  })();
})(ir);
var Qa = ir.exports;
const eo = /* @__PURE__ */ Gn(Qa);
function we(e, a) {
  to(e) && (e = "100%");
  var t = no(e);
  return e = a === 360 ? e : Math.min(a, Math.max(0, parseFloat(e))), t && (e = parseInt(String(e * a), 10) / 100), Math.abs(e - a) < 1e-6 ? 1 : (a === 360 ? e = (e < 0 ? e % a + a : e % a) / parseFloat(String(a)) : e = e % a / parseFloat(String(a)), e);
}
function to(e) {
  return typeof e == "string" && e.indexOf(".") !== -1 && parseFloat(e) === 1;
}
function no(e) {
  return typeof e == "string" && e.indexOf("%") !== -1;
}
function ro(e) {
  return e = parseFloat(e), (isNaN(e) || e < 0 || e > 1) && (e = 1), e;
}
function ft(e) {
  return e <= 1 ? "".concat(Number(e) * 100, "%") : e;
}
function kt(e) {
  return e.length === 1 ? "0" + e : String(e);
}
function ao(e, a, t) {
  return {
    r: we(e, 255) * 255,
    g: we(a, 255) * 255,
    b: we(t, 255) * 255
  };
}
function Pt(e, a, t) {
  return t < 0 && (t += 1), t > 1 && (t -= 1), t < 1 / 6 ? e + (a - e) * (6 * t) : t < 1 / 2 ? a : t < 2 / 3 ? e + (a - e) * (2 / 3 - t) * 6 : e;
}
function oo(e, a, t) {
  var o, i, s;
  if (e = we(e, 360), a = we(a, 100), t = we(t, 100), a === 0)
    i = t, s = t, o = t;
  else {
    var l = t < 0.5 ? t * (1 + a) : t + a - t * a, n = 2 * t - l;
    o = Pt(n, l, e + 1 / 3), i = Pt(n, l, e), s = Pt(n, l, e - 1 / 3);
  }
  return { r: o * 255, g: i * 255, b: s * 255 };
}
function io(e, a, t) {
  e = we(e, 255), a = we(a, 255), t = we(t, 255);
  var o = Math.max(e, a, t), i = Math.min(e, a, t), s = 0, l = o, n = o - i, d = o === 0 ? 0 : n / o;
  if (o === i)
    s = 0;
  else {
    switch (o) {
      case e:
        s = (a - t) / n + (a < t ? 6 : 0);
        break;
      case a:
        s = (t - e) / n + 2;
        break;
      case t:
        s = (e - a) / n + 4;
        break;
    }
    s /= 6;
  }
  return { h: s, s: d, v: l };
}
function so(e, a, t) {
  e = we(e, 360) * 6, a = we(a, 100), t = we(t, 100);
  var o = Math.floor(e), i = e - o, s = t * (1 - a), l = t * (1 - i * a), n = t * (1 - (1 - i) * a), d = o % 6, u = [t, l, s, s, n, t][d], m = [n, t, t, l, s, s][d], c = [s, s, n, t, t, l][d];
  return { r: u * 255, g: m * 255, b: c * 255 };
}
function lo(e, a, t, o) {
  var i = [
    kt(Math.round(e).toString(16)),
    kt(Math.round(a).toString(16)),
    kt(Math.round(t).toString(16))
  ];
  return o && i[0].startsWith(i[0].charAt(1)) && i[1].startsWith(i[1].charAt(1)) && i[2].startsWith(i[2].charAt(1)) ? i[0].charAt(0) + i[1].charAt(0) + i[2].charAt(0) : i.join("");
}
function Cn(e) {
  return be(e) / 255;
}
function be(e) {
  return parseInt(e, 16);
}
var wn = {
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
function et(e) {
  var a = { r: 0, g: 0, b: 0 }, t = 1, o = null, i = null, s = null, l = !1, n = !1;
  return typeof e == "string" && (e = fo(e)), typeof e == "object" && (Pe(e.r) && Pe(e.g) && Pe(e.b) ? (a = ao(e.r, e.g, e.b), l = !0, n = String(e.r).substr(-1) === "%" ? "prgb" : "rgb") : Pe(e.h) && Pe(e.s) && Pe(e.v) ? (o = ft(e.s), i = ft(e.v), a = so(e.h, o, i), l = !0, n = "hsv") : Pe(e.h) && Pe(e.s) && Pe(e.l) && (o = ft(e.s), s = ft(e.l), a = oo(e.h, o, s), l = !0, n = "hsl"), Object.prototype.hasOwnProperty.call(e, "a") && (t = e.a)), t = ro(t), {
    ok: l,
    format: e.format || n,
    r: Math.min(255, Math.max(a.r, 0)),
    g: Math.min(255, Math.max(a.g, 0)),
    b: Math.min(255, Math.max(a.b, 0)),
    a: t
  };
}
var co = "[-\\+]?\\d+%?", uo = "[-\\+]?\\d*\\.\\d+%?", Le = "(?:".concat(uo, ")|(?:").concat(co, ")"), It = "[\\s|\\(]+(".concat(Le, ")[,|\\s]+(").concat(Le, ")[,|\\s]+(").concat(Le, ")\\s*\\)?"), Ot = "[\\s|\\(]+(".concat(Le, ")[,|\\s]+(").concat(Le, ")[,|\\s]+(").concat(Le, ")[,|\\s]+(").concat(Le, ")\\s*\\)?"), Ee = {
  CSS_UNIT: new RegExp(Le),
  rgb: new RegExp("rgb" + It),
  rgba: new RegExp("rgba" + Ot),
  hsl: new RegExp("hsl" + It),
  hsla: new RegExp("hsla" + Ot),
  hsv: new RegExp("hsv" + It),
  hsva: new RegExp("hsva" + Ot),
  hex3: /^#?([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})$/,
  hex6: /^#?([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})$/,
  hex4: /^#?([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})$/,
  hex8: /^#?([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})$/
};
function fo(e) {
  if (e = e.trim().toLowerCase(), e.length === 0)
    return !1;
  var a = !1;
  if (wn[e])
    e = wn[e], a = !0;
  else if (e === "transparent")
    return { r: 0, g: 0, b: 0, a: 0, format: "name" };
  var t = Ee.rgb.exec(e);
  return t ? { r: t[1], g: t[2], b: t[3] } : (t = Ee.rgba.exec(e), t ? { r: t[1], g: t[2], b: t[3], a: t[4] } : (t = Ee.hsl.exec(e), t ? { h: t[1], s: t[2], l: t[3] } : (t = Ee.hsla.exec(e), t ? { h: t[1], s: t[2], l: t[3], a: t[4] } : (t = Ee.hsv.exec(e), t ? { h: t[1], s: t[2], v: t[3] } : (t = Ee.hsva.exec(e), t ? { h: t[1], s: t[2], v: t[3], a: t[4] } : (t = Ee.hex8.exec(e), t ? {
    r: be(t[1]),
    g: be(t[2]),
    b: be(t[3]),
    a: Cn(t[4]),
    format: a ? "name" : "hex8"
  } : (t = Ee.hex6.exec(e), t ? {
    r: be(t[1]),
    g: be(t[2]),
    b: be(t[3]),
    format: a ? "name" : "hex"
  } : (t = Ee.hex4.exec(e), t ? {
    r: be(t[1] + t[1]),
    g: be(t[2] + t[2]),
    b: be(t[3] + t[3]),
    a: Cn(t[4] + t[4]),
    format: a ? "name" : "hex8"
  } : (t = Ee.hex3.exec(e), t ? {
    r: be(t[1] + t[1]),
    g: be(t[2] + t[2]),
    b: be(t[3] + t[3]),
    format: a ? "name" : "hex"
  } : !1)))))))));
}
function Pe(e) {
  return !!Ee.CSS_UNIT.exec(String(e));
}
var pt = 2, En = 0.16, po = 0.05, mo = 0.05, ho = 0.15, sr = 5, lr = 4, go = [{
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
function Nn(e) {
  var a = e.r, t = e.g, o = e.b, i = io(a, t, o);
  return {
    h: i.h * 360,
    s: i.s,
    v: i.v
  };
}
function mt(e) {
  var a = e.r, t = e.g, o = e.b;
  return "#".concat(lo(a, t, o, !1));
}
function vo(e, a, t) {
  var o = t / 100, i = {
    r: (a.r - e.r) * o + e.r,
    g: (a.g - e.g) * o + e.g,
    b: (a.b - e.b) * o + e.b
  };
  return i;
}
function Sn(e, a, t) {
  var o;
  return Math.round(e.h) >= 60 && Math.round(e.h) <= 240 ? o = t ? Math.round(e.h) - pt * a : Math.round(e.h) + pt * a : o = t ? Math.round(e.h) + pt * a : Math.round(e.h) - pt * a, o < 0 ? o += 360 : o >= 360 && (o -= 360), o;
}
function Rn(e, a, t) {
  if (e.h === 0 && e.s === 0)
    return e.s;
  var o;
  return t ? o = e.s - En * a : a === lr ? o = e.s + En : o = e.s + po * a, o > 1 && (o = 1), t && a === sr && o > 0.1 && (o = 0.1), o < 0.06 && (o = 0.06), Number(o.toFixed(2));
}
function kn(e, a, t) {
  var o;
  return t ? o = e.v + mo * a : o = e.v - ho * a, o > 1 && (o = 1), Number(o.toFixed(2));
}
function Vt(e) {
  for (var a = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {}, t = [], o = et(e), i = sr; i > 0; i -= 1) {
    var s = Nn(o), l = mt(et({
      h: Sn(s, i, !0),
      s: Rn(s, i, !0),
      v: kn(s, i, !0)
    }));
    t.push(l);
  }
  t.push(mt(o));
  for (var n = 1; n <= lr; n += 1) {
    var d = Nn(o), u = mt(et({
      h: Sn(d, n),
      s: Rn(d, n),
      v: kn(d, n)
    }));
    t.push(u);
  }
  return a.theme === "dark" ? go.map(function(m) {
    var c = m.index, f = m.opacity, g = mt(vo(et(a.backgroundColor || "#141414"), et(t[c]), f * 100));
    return g;
  }) : t;
}
var Tt = {
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
}, gt = {}, Ft = {};
Object.keys(Tt).forEach(function(e) {
  gt[e] = Vt(Tt[e]), gt[e].primary = gt[e][5], Ft[e] = Vt(Tt[e], {
    theme: "dark",
    backgroundColor: "#141414"
  }), Ft[e].primary = Ft[e][5];
});
var yo = gt.blue;
function Pn(e, a) {
  var t = Object.keys(e);
  if (Object.getOwnPropertySymbols) {
    var o = Object.getOwnPropertySymbols(e);
    a && (o = o.filter(function(i) {
      return Object.getOwnPropertyDescriptor(e, i).enumerable;
    })), t.push.apply(t, o);
  }
  return t;
}
function Ne(e) {
  for (var a = 1; a < arguments.length; a++) {
    var t = arguments[a] != null ? arguments[a] : {};
    a % 2 ? Pn(Object(t), !0).forEach(function(o) {
      Dt(e, o, t[o]);
    }) : Object.getOwnPropertyDescriptors ? Object.defineProperties(e, Object.getOwnPropertyDescriptors(t)) : Pn(Object(t)).forEach(function(o) {
      Object.defineProperty(e, o, Object.getOwnPropertyDescriptor(t, o));
    });
  }
  return e;
}
function bo() {
  return !!(typeof window < "u" && window.document && window.document.createElement);
}
function xo(e, a) {
  if (!e)
    return !1;
  if (e.contains)
    return e.contains(a);
  for (var t = a; t; ) {
    if (t === e)
      return !0;
    t = t.parentNode;
  }
  return !1;
}
var In = "data-rc-order", On = "data-rc-priority", jo = "rc-util-key", qt = /* @__PURE__ */ new Map();
function cr() {
  var e = arguments.length > 0 && arguments[0] !== void 0 ? arguments[0] : {}, a = e.mark;
  return a ? a.startsWith("data-") ? a : "data-".concat(a) : jo;
}
function Zt(e) {
  if (e.attachTo)
    return e.attachTo;
  var a = document.querySelector("head");
  return a || document.body;
}
function Co(e) {
  return e === "queue" ? "prependQueue" : e ? "prepend" : "append";
}
function Gt(e) {
  return Array.from((qt.get(e) || e).children).filter(function(a) {
    return a.tagName === "STYLE";
  });
}
function ur(e) {
  var a = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
  if (!bo())
    return null;
  var t = a.csp, o = a.prepend, i = a.priority, s = i === void 0 ? 0 : i, l = Co(o), n = l === "prependQueue", d = document.createElement("style");
  d.setAttribute(In, l), n && s && d.setAttribute(On, "".concat(s)), t != null && t.nonce && (d.nonce = t == null ? void 0 : t.nonce), d.innerHTML = e;
  var u = Zt(a), m = u.firstChild;
  if (o) {
    if (n) {
      var c = (a.styles || Gt(u)).filter(function(f) {
        if (!["prepend", "prependQueue"].includes(f.getAttribute(In)))
          return !1;
        var g = Number(f.getAttribute(On) || 0);
        return s >= g;
      });
      if (c.length)
        return u.insertBefore(d, c[c.length - 1].nextSibling), d;
    }
    u.insertBefore(d, m);
  } else
    u.appendChild(d);
  return d;
}
function wo(e) {
  var a = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {}, t = Zt(a);
  return (a.styles || Gt(t)).find(function(o) {
    return o.getAttribute(cr(a)) === e;
  });
}
function Eo(e, a) {
  var t = qt.get(e);
  if (!t || !xo(document, t)) {
    var o = ur("", a), i = o.parentNode;
    qt.set(e, i), e.removeChild(o);
  }
}
function No(e, a) {
  var t = arguments.length > 2 && arguments[2] !== void 0 ? arguments[2] : {}, o = Zt(t), i = Gt(o), s = Ne(Ne({}, t), {}, {
    styles: i
  });
  Eo(o, s);
  var l = wo(a, s);
  if (l) {
    var n, d;
    if ((n = s.csp) !== null && n !== void 0 && n.nonce && l.nonce !== ((d = s.csp) === null || d === void 0 ? void 0 : d.nonce)) {
      var u;
      l.nonce = (u = s.csp) === null || u === void 0 ? void 0 : u.nonce;
    }
    return l.innerHTML !== e && (l.innerHTML = e), l;
  }
  var m = ur(e, s);
  return m.setAttribute(cr(s), a), m;
}
function dr(e) {
  var a;
  return e == null || (a = e.getRootNode) === null || a === void 0 ? void 0 : a.call(e);
}
function So(e) {
  return dr(e) instanceof ShadowRoot;
}
function Ro(e) {
  return So(e) ? dr(e) : null;
}
var Wt = {}, Xt = [], ko = function(a) {
  Xt.push(a);
};
function Po(e, a) {
  if (process.env.NODE_ENV !== "production" && !e && console !== void 0) {
    var t = Xt.reduce(function(o, i) {
      return i(o ?? "", "warning");
    }, a);
    t && console.error("Warning: ".concat(t));
  }
}
function Io(e, a) {
  if (process.env.NODE_ENV !== "production" && !e && console !== void 0) {
    var t = Xt.reduce(function(o, i) {
      return i(o ?? "", "note");
    }, a);
    t && console.warn("Note: ".concat(t));
  }
}
function Oo() {
  Wt = {};
}
function fr(e, a, t) {
  !a && !Wt[t] && (e(!1, t), Wt[t] = !0);
}
function bt(e, a) {
  fr(Po, e, a);
}
function To(e, a) {
  fr(Io, e, a);
}
bt.preMessage = ko;
bt.resetWarned = Oo;
bt.noteOnce = To;
function Fo(e) {
  return e.replace(/-(.)/g, function(a, t) {
    return t.toUpperCase();
  });
}
function Lo(e, a) {
  bt(e, "[@ant-design/icons] ".concat(a));
}
function Tn(e) {
  return ze(e) === "object" && typeof e.name == "string" && typeof e.theme == "string" && (ze(e.icon) === "object" || typeof e.icon == "function");
}
function Fn() {
  var e = arguments.length > 0 && arguments[0] !== void 0 ? arguments[0] : {};
  return Object.keys(e).reduce(function(a, t) {
    var o = e[t];
    switch (t) {
      case "class":
        a.className = o, delete a.class;
        break;
      default:
        delete a[t], a[Fo(t)] = o;
    }
    return a;
  }, {});
}
function Ut(e, a, t) {
  return t ? /* @__PURE__ */ vt.createElement(e.tag, Ne(Ne({
    key: a
  }, Fn(e.attrs)), t), (e.children || []).map(function(o, i) {
    return Ut(o, "".concat(a, "-").concat(e.tag, "-").concat(i));
  })) : /* @__PURE__ */ vt.createElement(e.tag, Ne({
    key: a
  }, Fn(e.attrs)), (e.children || []).map(function(o, i) {
    return Ut(o, "".concat(a, "-").concat(e.tag, "-").concat(i));
  }));
}
function pr(e) {
  return Vt(e)[0];
}
function mr(e) {
  return e ? Array.isArray(e) ? e : [e] : [];
}
var _o = `
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
`, Mo = function(a) {
  var t = ot(rr), o = t.csp, i = t.prefixCls, s = _o;
  i && (s = s.replace(/anticon/g, i)), ge(function() {
    var l = a.current, n = Ro(l);
    No(s, "@ant-design-icons", {
      prepend: !0,
      csp: o,
      attachTo: n
    });
  }, []);
}, Ao = ["icon", "className", "onClick", "style", "primaryColor", "secondaryColor"], nt = {
  primaryColor: "#333",
  secondaryColor: "#E6E6E6",
  calculated: !1
};
function Bo(e) {
  var a = e.primaryColor, t = e.secondaryColor;
  nt.primaryColor = a, nt.secondaryColor = t || pr(a), nt.calculated = !!t;
}
function $o() {
  return Ne({}, nt);
}
var xt = function(a) {
  var t = a.icon, o = a.className, i = a.onClick, s = a.style, l = a.primaryColor, n = a.secondaryColor, d = or(a, Ao), u = w.useRef(), m = nt;
  if (l && (m = {
    primaryColor: l,
    secondaryColor: n || pr(l)
  }), Mo(u), Lo(Tn(t), "icon should be icon definiton, but got ".concat(t)), !Tn(t))
    return null;
  var c = t;
  return c && typeof c.icon == "function" && (c = Ne(Ne({}, c), {}, {
    icon: c.icon(m.primaryColor, m.secondaryColor)
  })), Ut(c.icon, "svg-".concat(c.name), Ne(Ne({
    className: o,
    onClick: i,
    style: s,
    "data-icon": c.name,
    width: "1em",
    height: "1em",
    fill: "currentColor",
    "aria-hidden": "true"
  }, d), {}, {
    ref: u
  }));
};
xt.displayName = "IconReact";
xt.getTwoToneColors = $o;
xt.setTwoToneColors = Bo;
const Qt = xt;
function hr(e) {
  var a = mr(e), t = ar(a, 2), o = t[0], i = t[1];
  return Qt.setTwoToneColors({
    primaryColor: o,
    secondaryColor: i
  });
}
function zo() {
  var e = Qt.getTwoToneColors();
  return e.calculated ? [e.primaryColor, e.secondaryColor] : e.primaryColor;
}
var Do = ["className", "icon", "spin", "rotate", "tabIndex", "onClick", "twoToneColor"];
hr(yo.primary);
var jt = /* @__PURE__ */ w.forwardRef(function(e, a) {
  var t = e.className, o = e.icon, i = e.spin, s = e.rotate, l = e.tabIndex, n = e.onClick, d = e.twoToneColor, u = or(e, Do), m = w.useContext(rr), c = m.prefixCls, f = c === void 0 ? "anticon" : c, g = m.rootClassName, v = eo(g, f, Dt(Dt({}, "".concat(f, "-").concat(o.name), !!o.name), "".concat(f, "-spin"), !!i || o.name === "loading"), t), h = l;
  h === void 0 && n && (h = -1);
  var y = s ? {
    msTransform: "rotate(".concat(s, "deg)"),
    transform: "rotate(".concat(s, "deg)")
  } : void 0, b = mr(d), x = ar(b, 2), N = x[0], I = x[1];
  return /* @__PURE__ */ w.createElement("span", _e({
    role: "img",
    "aria-label": o.name
  }, u, {
    ref: a,
    tabIndex: h,
    onClick: n,
    className: v
  }), /* @__PURE__ */ w.createElement(Qt, {
    icon: o,
    primaryColor: N,
    secondaryColor: I,
    style: y
  }));
});
jt.displayName = "AntdIcon";
jt.getTwoToneColor = zo;
jt.setTwoToneColor = hr;
const it = jt;
var Vo = { icon: { tag: "svg", attrs: { viewBox: "64 64 896 896", focusable: "false" }, children: [{ tag: "path", attrs: { d: "M811.4 368.9C765.6 248 648.9 162 512.2 162S258.8 247.9 213 368.8C126.9 391.5 63.5 470.2 64 563.6 64.6 668 145.6 752.9 247.6 762c4.7.4 8.7-3.3 8.7-8v-60.4c0-4-3-7.4-7-7.9-27-3.4-52.5-15.2-72.1-34.5-24-23.5-37.2-55.1-37.2-88.6 0-28 9.1-54.4 26.2-76.4 16.7-21.4 40.2-36.9 66.1-43.7l37.9-10 13.9-36.7c8.6-22.8 20.6-44.2 35.7-63.5 14.9-19.2 32.6-36 52.4-50 41.1-28.9 89.5-44.2 140-44.2s98.9 15.3 140 44.3c19.9 14 37.5 30.8 52.4 50 15.1 19.3 27.1 40.7 35.7 63.5l13.8 36.6 37.8 10c54.2 14.4 92.1 63.7 92.1 120 0 33.6-13.2 65.1-37.2 88.6-19.5 19.2-44.9 31.1-71.9 34.5-4 .5-6.9 3.9-6.9 7.9V754c0 4.7 4.1 8.4 8.8 8 101.7-9.2 182.5-94 183.2-198.2.6-93.4-62.7-172.1-148.6-194.9z" } }, { tag: "path", attrs: { d: "M376.9 656.4c1.8-33.5 15.7-64.7 39.5-88.6 25.4-25.5 60-39.8 96-39.8 36.2 0 70.3 14.1 96 39.8 1.4 1.4 2.7 2.8 4.1 4.3l-25 19.6a8 8 0 003 14.1l98.2 24c5 1.2 9.9-2.6 9.9-7.7l.5-101.3c0-6.7-7.6-10.5-12.9-6.3L663 532.7c-36.6-42-90.4-68.6-150.5-68.6-107.4 0-195 85.1-199.4 191.7-.2 4.5 3.4 8.3 8 8.3H369c4.2-.1 7.7-3.4 7.9-7.7zM703 664h-47.9c-4.2 0-7.7 3.3-8 7.6-1.8 33.5-15.7 64.7-39.5 88.6-25.4 25.5-60 39.8-96 39.8-36.2 0-70.3-14.1-96-39.8-1.4-1.4-2.7-2.8-4.1-4.3l25-19.6a8 8 0 00-3-14.1l-98.2-24c-5-1.2-9.9 2.6-9.9 7.7l-.4 101.4c0 6.7 7.6 10.5 12.9 6.3l23.2-18.2c36.6 42 90.4 68.6 150.5 68.6 107.4 0 195-85.1 199.4-191.7.2-4.5-3.4-8.3-8-8.3z" } }] }, name: "cloud-sync", theme: "outlined" };
const qo = Vo;
var Wo = function(a, t) {
  return /* @__PURE__ */ w.createElement(it, _e({}, a, {
    ref: t,
    icon: qo
  }));
}, gr = /* @__PURE__ */ w.forwardRef(Wo);
process.env.NODE_ENV !== "production" && (gr.displayName = "CloudSyncOutlined");
const Je = gr;
var Uo = { icon: { tag: "svg", attrs: { viewBox: "64 64 896 896", focusable: "false" }, children: [{ tag: "path", attrs: { d: "M360 184h-8c4.4 0 8-3.6 8-8v8h304v-8c0 4.4 3.6 8 8 8h-8v72h72v-80c0-35.3-28.7-64-64-64H352c-35.3 0-64 28.7-64 64v80h72v-72zm504 72H160c-17.7 0-32 14.3-32 32v32c0 4.4 3.6 8 8 8h60.4l24.7 523c1.6 34.1 29.8 61 63.9 61h454c34.2 0 62.3-26.8 63.9-61l24.7-523H888c4.4 0 8-3.6 8-8v-32c0-17.7-14.3-32-32-32zM731.3 840H292.7l-24.2-512h487l-24.2 512z" } }] }, name: "delete", theme: "outlined" };
const Ho = Uo;
var Ko = function(a, t) {
  return /* @__PURE__ */ w.createElement(it, _e({}, a, {
    ref: t,
    icon: Ho
  }));
}, vr = /* @__PURE__ */ w.forwardRef(Ko);
process.env.NODE_ENV !== "production" && (vr.displayName = "DeleteOutlined");
const Jo = vr;
var Yo = { icon: { tag: "svg", attrs: { viewBox: "64 64 896 896", focusable: "false" }, children: [{ tag: "path", attrs: { d: "M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" } }, { tag: "path", attrs: { d: "M464 336a48 48 0 1096 0 48 48 0 10-96 0zm72 112h-48c-4.4 0-8 3.6-8 8v272c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V456c0-4.4-3.6-8-8-8z" } }] }, name: "info-circle", theme: "outlined" };
const Zo = Yo;
var Go = function(a, t) {
  return /* @__PURE__ */ w.createElement(it, _e({}, a, {
    ref: t,
    icon: Zo
  }));
}, yr = /* @__PURE__ */ w.forwardRef(Go);
process.env.NODE_ENV !== "production" && (yr.displayName = "InfoCircleOutlined");
const Re = yr;
var Xo = { icon: { tag: "svg", attrs: { viewBox: "64 64 896 896", focusable: "false" }, children: [{ tag: "path", attrs: { d: "M696 480H328c-4.4 0-8 3.6-8 8v48c0 4.4 3.6 8 8 8h368c4.4 0 8-3.6 8-8v-48c0-4.4-3.6-8-8-8z" } }, { tag: "path", attrs: { d: "M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" } }] }, name: "minus-circle", theme: "outlined" };
const Qo = Xo;
var ei = function(a, t) {
  return /* @__PURE__ */ w.createElement(it, _e({}, a, {
    ref: t,
    icon: Qo
  }));
}, br = /* @__PURE__ */ w.forwardRef(ei);
process.env.NODE_ENV !== "production" && (br.displayName = "MinusCircleOutlined");
const yt = br;
var ti = { icon: { tag: "svg", attrs: { viewBox: "64 64 896 896", focusable: "false" }, children: [{ tag: "path", attrs: { d: "M482 152h60q8 0 8 8v704q0 8-8 8h-60q-8 0-8-8V160q0-8 8-8z" } }, { tag: "path", attrs: { d: "M192 474h672q8 0 8 8v60q0 8-8 8H160q-8 0-8-8v-60q0-8 8-8z" } }] }, name: "plus", theme: "outlined" };
const ni = ti;
var ri = function(a, t) {
  return /* @__PURE__ */ w.createElement(it, _e({}, a, {
    ref: t,
    icon: ni
  }));
}, xr = /* @__PURE__ */ w.forwardRef(ri);
process.env.NODE_ENV !== "production" && (xr.displayName = "PlusOutlined");
const Ye = xr, ai = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  return n.type = "taskNode", n.text = "LLM", n.width = 700, n.jadeConfig = [
    { id: 0, text: "Philosopher’s Path", done: !0 },
    { id: 1, text: "Visit the temple", done: !1 },
    { id: 2, text: "Drink matcha", done: !1 }
  ], n.getReactComponents = () => /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
    /* @__PURE__ */ r.jsx("h1", { children: "Day off in Kyoto" }),
    /* @__PURE__ */ r.jsx(qa, {}),
    /* @__PURE__ */ r.jsx(Da, {})
  ] }), n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(Je, {}), n.reducers = (d, u) => {
    switch (u.type) {
      case "added":
        return [...d, {
          id: u.id,
          text: u.text,
          done: !1
        }];
      case "changed":
        return d.map((m) => m.id === u.task.id ? u.task : m);
      case "deleted":
        return d.filter((m) => m.id !== u.id);
      default:
        throw Error("Unknown action: " + u.type);
    }
  }, n;
}, oi = (e) => /* @__PURE__ */ w.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ w.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ w.createElement("defs", null, /* @__PURE__ */ w.createElement("clipPath", { id: "clip4_13285" }, /* @__PURE__ */ w.createElement("rect", { id: "\\u56FE\\u6807/16/\\u5F00\\u59CB\\uFF0C\\u8D77\\u70B9\\uFF0C\\u7AEF\\u70B9", width: 16, height: 16, transform: "matrix(-1 0 0 1 20 4)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ w.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, transform: "matrix(-1 0 0 1 24 0)", fill: "#5E7CE0", fillOpacity: 1 }), /* @__PURE__ */ w.createElement("g", { clipPath: "url(#clip4_13285)" }, /* @__PURE__ */ w.createElement("path", { id: "path", d: "M7.41 11.52L5.83 11.52L5.83 6.31L12.53 6.31C15.64 6.37 18.16 8.89 18.16 11.99C18.16 15.1 15.64 17.62 12.53 17.67L5.83 17.67L5.83 12.46L10.92 12.46L10.92 13.52L13.91 13.52L13.91 10.47L10.92 10.47L10.92 11.53L7.41 11.53L7.41 11.52Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }))), ii = ({ shape: e }) => {
  const [a, t] = de(0);
  return ge(() => {
    e.page.addEventListener("TOOL_MENU_CHANGE", (o) => {
      t(o[0]);
    });
  }, []), /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Xn, { shape: e }) });
}, si = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  n.type = "endNodeEnd", n.backColor = "white", n.pointerEvents = "auto", n.text = "结束", n.componentName = "endComponent", n.flowMeta = {
    triggerMode: "auto",
    callback: {
      type: "general_callback",
      name: "通知回调",
      fitables: ["com.huawei.fit.jober.aipp.fitable.AippFlowEndCallback"],
      converter: {
        type: "mapping_converter"
      }
    }
  };
  const d = n.getToolMenus;
  n.getToolMenus = () => n.page.shapes.filter((f) => f.type === n.type).length === 1 ? [{
    key: "1",
    label: "复制",
    action: () => {
      n.duplicate();
    }
  }, {
    key: "2",
    label: "重命名",
    action: (f) => {
      f(!0);
    }
  }] : d.apply(n);
  const u = n.remove;
  n.remove = (f) => {
    if (n.page.shapes.filter((y) => y.type === "endNodeEnd").length <= 1 && n.type === "endNodeEnd")
      return [];
    const v = u.apply(n, [f]);
    return n.page.shapes.filter((y) => y.type === "endNodeEnd").length === 1 && n.page.triggerEvent({
      type: "TOOL_MENU_CHANGE",
      value: [1]
    }), v;
  };
  const m = n.initConnectors;
  n.initConnectors = () => {
    m.apply(n), n.connectors.remove((f) => f.direction.key === Fe.E.key);
  }, n.serializerJadeConfig = () => {
    n.flowMeta.callback.converter.entity = n.getLatestJadeConfig();
  }, n.getComponent = () => n.graph.plugins[n.componentName](n.flowMeta.callback.converter.entity), n.getEntity = () => n.flowMeta.callback.converter.entity, n.getHeaderComponent = () => /* @__PURE__ */ r.jsx(ii, { shape: n }), n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(se, { disabled: !0, className: "jade-node-custom-header-icon", children: /* @__PURE__ */ r.jsx(oi, {}) }) });
  const c = n.created;
  return n.created = () => {
    c.apply(n), n.page.shapes.filter((g) => g.type === n.type).length === 2 && n.page.triggerEvent({
      type: "TOOL_MENU_CHANGE",
      value: [n.page.shapes.filter((g) => g.type === "endNodeEnd").length]
    });
  }, n;
}, De = ({ reference: e, onReferencedValueChange: a, onReferencedKeyChange: t, rules: o, className: i, ...s }) => {
  const l = he(), n = Ke(), d = rt(null), [u, m] = de([]), c = `reference-${e.id}`, f = `jade-tree-select ${i || ""}`.trim(), g = (b) => {
    if (!b)
      return;
    d.current && d.current();
    const x = new Map(u.map((C) => [C.id, C])), N = x.get(b), I = v(x, N);
    d.current = l.observeTo(N.nId, N.value, (C) => y(C.value, C.type)), t({ referenceNode: N.nId, referenceId: N.value, value: I });
  }, v = (b, x) => {
    const N = [];
    N.unshift(x.title);
    let I = x.pId;
    for (; I; ) {
      const C = b.get(I);
      if (!C || C.pId === 0)
        break;
      N.unshift(C.title), I = C.pId;
    }
    return N;
  }, h = () => {
    const x = l.getPreNodeInfos().map((N) => {
      const I = [];
      return I.push({ id: N.id, pId: 0, value: N.id, title: N.name, selectable: !1 }), N.observableList.forEach((C) => {
        C.parentId || (C.parentId = N.id);
        const ae = {
          nId: N.id,
          id: C.observableId,
          pId: C.parentId,
          value: C.observableId,
          title: C.value
        };
        I.push(ae);
      }), I;
    }).flatMap((N) => N);
    m(x);
  };
  ge(() => (e.referenceNode && e.referenceId && (d.current = l.observeTo(e.referenceNode, e.referenceId, (b) => y(b.value, b.type))), () => {
    d.current && d.current();
  }), []);
  const y = (b, x) => {
    n.setFieldsValue({ [c]: b }), a(b, x);
  };
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
    F.Item,
    {
      id: `reference-id-${e.id}`,
      name: [c],
      initialValue: e.referenceKey,
      rules: o,
      validateTrigger: "onBlur",
      children: /* @__PURE__ */ r.jsx(
        Na,
        {
          ...s,
          className: f,
          treeDataSimpleMode: !0,
          dropdownStyle: { maxHeight: 400, overflow: "auto", minWidth: 250 },
          placeholder: "请选择",
          onChange: g,
          treeData: u,
          onDropdownVisibleChange: h,
          treeDefaultExpandAll: !1,
          onMouseDown: (b) => b.stopPropagation()
        }
      )
    }
  ) });
};
function li({ item: e, handleItemChange: a }) {
  const t = `value-${e.id}`, o = Ke(), i = (d) => {
    a(e.id, [{ key: "referenceKey", value: d }]);
  }, s = (d) => {
    a(e.id, [
      { key: "referenceNode", value: d.referenceNode },
      { key: "referenceId", value: d.referenceId },
      { key: "value", value: d.value }
    ]);
  }, l = () => (d) => {
    d.target.value && a(e.id, [{ key: "value", value: d.target.value }]);
  }, n = (d) => {
    switch (d) {
      case "Reference":
        return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
          De,
          {
            reference: e,
            onReferencedValueChange: i,
            onReferencedKeyChange: s,
            style: { fontSize: "12px" },
            placeholder: "请选择",
            onMouseDown: (u) => u.stopPropagation(),
            showSearch: !0,
            className: "value-custom jade-select",
            dropdownStyle: {
              maxHeight: 400,
              overflow: "auto"
            },
            value: e.value,
            rules: [{ required: !0, message: "字段值不能为空" }]
          }
        ) });
      case "String":
        return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
          F.Item,
          {
            style: { marginBottom: "8px" },
            id: `value-${e.id}`,
            name: `value-${e.id}`,
            rules: [{ required: !0, message: "字段值不能为空" }],
            validateTrigger: "onBlur",
            children: /* @__PURE__ */ r.jsx(
              xe,
              {
                className: "value-custom jade-input",
                style: { fontSize: "12px" },
                placeholder: "请输入",
                value: e.value,
                onBlur: l
              }
            )
          }
        ) });
      default:
        return null;
    }
  };
  return /* @__PURE__ */ r.jsxs(
    fe,
    {
      gutter: 16,
      children: [
        /* @__PURE__ */ r.jsx(U, { span: 8, style: { display: "flex", paddingTop: "5px" }, children: /* @__PURE__ */ r.jsx("span", { className: "end-starred-text", children: "finalOutput" }) }),
        /* @__PURE__ */ r.jsx(U, { span: 6, style: { paddingRight: 0 }, children: /* @__PURE__ */ r.jsx(
          F.Item,
          {
            style: { marginBottom: "8px" },
            id: `valueSource-${e.id}`,
            initialValue: "Reference",
            children: /* @__PURE__ */ r.jsx(
              Yn,
              {
                onMouseDown: (d) => d.stopPropagation(),
                id: `valueSource-select-${e.id}`,
                className: "value-source-custom jade-select",
                style: { width: "100%" },
                onChange: (d) => {
                  o.resetFields([`reference-${e.id}`, t]);
                  let u = [{ key: "from", value: d }, { key: "value", value: "" }];
                  d === "String" && (u = [
                    { key: "from", value: d },
                    { key: "value", value: "" },
                    { key: "referenceNode", value: "" },
                    { key: "referenceId", value: "" },
                    { key: "referenceKey", value: "" }
                  ]), a(e.id, u);
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
        /* @__PURE__ */ r.jsx(U, { span: 10, style: { paddingLeft: 0 }, children: n(e.from) })
      ]
    },
    `output-variable-${e.id}`
  );
}
const { Panel: ci } = G;
function ui() {
  const e = re(), a = ne(), t = () => a && a.inputParams, o = (s, l) => {
    e({ type: "editOutputVariable", id: s, changes: l });
  }, i = /* @__PURE__ */ r.jsxs("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: [
    /* @__PURE__ */ r.jsx("p", { children: "这些变量将在机器人完成工作流调用后输出。" }),
    /* @__PURE__ */ r.jsx("p", { children: "在“返回变量”模式下，这些变量将由机器人汇总并回复给用户；" }),
    /* @__PURE__ */ r.jsx("p", { children: "在“直接回答”模式下，机器人将只回复配置卡时可以使用的变量" })
  ] });
  return /* @__PURE__ */ r.jsx("div", { children: /* @__PURE__ */ r.jsx(
    G,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      style: { marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%" },
      defaultActiveKey: ["Output variable"],
      children: /* @__PURE__ */ r.jsxs(
        ci,
        {
          style: { marginBottom: 8, borderRadius: "8px", width: "100%" },
          header: /* @__PURE__ */ r.jsxs(
            "div",
            {
              style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
              children: [
                /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "输出" }),
                /* @__PURE__ */ r.jsx(Se, { content: i, children: /* @__PURE__ */ r.jsx(Re, { className: "jade-panel-header-popover-content" }) })
              ]
            }
          ),
          className: "jade-panel",
          children: [
            /* @__PURE__ */ r.jsxs(fe, { gutter: 16, children: [
              /* @__PURE__ */ r.jsx(U, { span: 8, children: /* @__PURE__ */ r.jsx(F.Item, { style: { marginBottom: "8px" }, children: /* @__PURE__ */ r.jsx("span", { className: "jade-font-size jade-font-color", children: "字段名称" }) }) }),
              /* @__PURE__ */ r.jsx(U, { span: 16, children: /* @__PURE__ */ r.jsx(F.Item, { style: { marginBottom: "8px" }, children: /* @__PURE__ */ r.jsx("span", { className: "jade-font-size jade-font-color", children: "字段值" }) }) })
            ] }),
            /* @__PURE__ */ r.jsx(li, { item: t()[0], handleItemChange: o })
          ]
        },
        "Output variable"
      )
    }
  ) });
}
function di() {
  return /* @__PURE__ */ r.jsx("div", { style: { backgroundColor: "white" }, children: /* @__PURE__ */ r.jsx(ui, {}) });
}
const fi = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || {
    inputParams: [{
      id: T(),
      name: "finalOutput",
      type: "String",
      from: "Reference",
      referenceNode: "",
      referenceId: "",
      referenceKey: "",
      value: []
    }],
    outputParams: [{}]
  }, a.getReactComponents = () => /* @__PURE__ */ r.jsx(di, {}), a.reducers = (t, o) => {
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
  }, a;
}, pi = (e) => /* @__PURE__ */ w.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ w.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ w.createElement("defs", null, /* @__PURE__ */ w.createElement("clipPath", { id: "clip4_13287" }, /* @__PURE__ */ w.createElement("rect", { id: "\\u56FE\\u6807/16/\\u6570\\u636E\\u68C0\\u7D22\\uFF0C\\u67E5\\u627E", width: 16, height: 16, transform: "translate(4.000000 4.000000)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ w.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#50D4AB", fillOpacity: 1 }), /* @__PURE__ */ w.createElement("g", { clipPath: "url(#clip4_13287)" }, /* @__PURE__ */ w.createElement("path", { id: "path", d: "M12.11 10.24C15.33 10.24 18.03 9.02 18.03 7.62C18.03 6.14 15.42 5.01 12.11 5.01C8.8 5.01 6.19 6.23 6.19 7.62C6.19 9.1 8.8 10.24 12.11 10.24ZM12.11 13.02L12.37 13.02C12.89 11.8 14.11 11.02 15.59 11.02C16.29 11.02 16.9 11.19 17.51 11.54C17.86 11.19 18.12 10.85 18.12 10.41L18.12 8.32C18.12 9.8 15.51 10.93 12.2 10.93C8.97 10.93 6.27 9.71 6.27 8.32L6.27 10.32C6.19 11.8 8.8 13.02 12.11 13.02ZM12.11 15.72L12.28 15.72C12.11 15.38 12.02 14.94 12.02 14.5C12.02 14.24 12.02 13.98 12.11 13.72C8.89 13.72 6.19 12.5 6.19 11.11L6.19 13.11C6.19 14.59 8.8 15.72 12.11 15.72ZM14.55 17.81C13.77 17.55 13.07 17.12 12.63 16.42L12.11 16.42C8.89 16.42 6.19 15.2 6.19 13.81L6.19 15.81C6.19 17.29 8.8 18.42 12.11 18.42C12.98 18.42 13.85 18.34 14.64 18.16C14.55 18.08 14.55 17.99 14.55 17.81ZM19.69 17.9L17.86 16.07L17.77 15.99C18.03 15.55 18.21 15.03 18.21 14.42C18.21 12.85 16.9 11.54 15.33 11.54C13.77 11.54 12.46 12.85 12.46 14.42C12.46 15.99 13.77 17.29 15.33 17.29C15.86 17.29 16.29 17.12 16.73 16.94C16.73 16.94 16.73 17.03 16.81 17.03L18.64 18.86C18.82 19.03 19.25 19.03 19.52 18.77C19.86 18.42 19.86 18.08 19.69 17.9ZM13.33 14.42C13.33 13.28 14.29 12.33 15.42 12.33C16.55 12.33 17.51 13.28 17.51 14.42C17.51 15.55 16.55 16.51 15.42 16.51C14.2 16.51 13.33 15.55 13.33 14.42Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }))), me = (e) => {
  const { onMouseDown: a, ...t } = e, o = (i) => {
    a && a(i), i.stopPropagation();
  };
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Yn, { onMouseDown: (i) => o(i), ...t }) });
}, { Panel: mi } = G;
function jr() {
  const e = re(), a = ne(), t = a && a.inputParams.find((c) => c.name === "query"), o = Ke(), i = `input-${t.id}`, s = (c, f) => {
    e({ type: "editInput", id: c, changes: f });
  }, l = (c, f) => {
    s(c.id, [{ key: "referenceKey", value: f }]);
  }, n = (c, f) => {
    s(c.id, [
      { key: "referenceNode", value: f.referenceNode },
      { key: "referenceId", value: f.referenceId },
      { key: "value", value: f.value }
    ]);
  }, d = (c) => (f) => {
    f.target.value && s(c.id, [{ key: "value", value: f.target.value }]);
  }, u = (c) => {
    switch (c.from) {
      case "Reference":
        return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
          De,
          {
            reference: c,
            onReferencedValueChange: (f) => l(c, f),
            onReferencedKeyChange: (f) => n(c, f),
            style: { fontSize: "12px" },
            placeholder: "请选择",
            onMouseDown: (f) => f.stopPropagation(),
            showSearch: !0,
            className: "value-custom jade-select",
            dropdownStyle: {
              maxHeight: 400,
              overflow: "auto"
            },
            rules: [{ required: !0, message: "字段值不能为空" }]
          }
        ) });
      case "Input":
        return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
          F.Item,
          {
            id: `input-${c.id}`,
            name: `input-${c.id}`,
            rules: [{ required: !0, message: "字段值不能为空" }, {
              pattern: /^[^\s]*$/,
              message: "禁止输入空格"
            }],
            initialValue: c.value,
            validateTrigger: "onBlur",
            children: /* @__PURE__ */ r.jsx(
              xe,
              {
                className: "value-custom jade-input",
                placeholder: "清输入",
                value: c.value,
                onBlur: d(c)
              }
            )
          }
        ) });
      default:
        return null;
    }
  }, m = /* @__PURE__ */ r.jsx("div", { className: "jade-font-size", children: /* @__PURE__ */ r.jsx("p", { children: "输入需要从知识库中匹配的关键信息" }) });
  return /* @__PURE__ */ r.jsx("div", { children: /* @__PURE__ */ r.jsx(
    G,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      defaultActiveKey: ["Input"],
      children: /* @__PURE__ */ r.jsxs(
        mi,
        {
          header: /* @__PURE__ */ r.jsxs("div", { className: "panel-header", children: [
            /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "输入" }),
            /* @__PURE__ */ r.jsx(Se, { content: m, children: /* @__PURE__ */ r.jsx(Re, { className: "jade-panel-header-popover-content" }) })
          ] }),
          className: "jade-panel",
          children: [
            /* @__PURE__ */ r.jsxs(fe, { children: [
              /* @__PURE__ */ r.jsx(U, { span: 8, children: /* @__PURE__ */ r.jsx(F.Item, { style: { marginBottom: "8px" }, children: /* @__PURE__ */ r.jsx("span", { className: "jade-font-size jade-font-color", children: "字段名称" }) }) }),
              /* @__PURE__ */ r.jsx(U, { span: 16, children: /* @__PURE__ */ r.jsx(F.Item, { style: { marginBottom: "8px" }, children: /* @__PURE__ */ r.jsx("span", { className: "jade-font-size jade-font-color", children: "字段值" }) }) })
            ] }),
            /* @__PURE__ */ r.jsxs(fe, { children: [
              /* @__PURE__ */ r.jsx(U, { span: 8, style: { display: "flex", paddingTop: "5px" }, children: /* @__PURE__ */ r.jsx("span", { className: "retrieval-starred-text", children: "query" }) }),
              /* @__PURE__ */ r.jsx(U, { span: 8, style: { paddingRight: 0 }, children: /* @__PURE__ */ r.jsx(F.Item, { id: "valueSource", initialValue: "Reference", children: /* @__PURE__ */ r.jsx(
                me,
                {
                  id: `valueSource-select-${t.id}`,
                  className: "value-source-custom jade-select",
                  style: { width: "100%" },
                  onChange: (c) => {
                    let f = [{ key: "from", value: c }, { key: "value", value: "" }];
                    c === "Input" && (f = [
                      { key: "from", value: c },
                      { key: "value", value: "" },
                      { key: "referenceNode", value: "" },
                      { key: "referenceId", value: "" },
                      { key: "referenceKey", value: "" }
                    ]), s(t.id, f), o.resetFields([`reference-${t.id}`, i]);
                  },
                  options: [{ value: "Reference", label: "引用" }, { value: "Input", label: "输入" }],
                  value: t.from
                }
              ) }) }),
              /* @__PURE__ */ r.jsxs(U, { span: 8, style: { paddingLeft: 0 }, children: [
                u(t),
                " "
              ] })
            ] })
          ]
        },
        "Input"
      )
    }
  ) });
}
function hi() {
  const e = re(), a = ne(), t = a && a.inputParams.find((s) => s.name === "maximum").value, o = /* @__PURE__ */ r.jsx("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: /* @__PURE__ */ r.jsx("p", { children: "从知识返回到模型的最大段落数。数字越大，返回的内容越多。" }) }), i = {
    1: "1",
    3: "默认",
    10: "10"
  };
  return /* @__PURE__ */ r.jsxs(fe, { className: "jade-row", children: [
    /* @__PURE__ */ r.jsx(U, { span: 12, className: "jade-column", children: /* @__PURE__ */ r.jsxs(F.Item, { children: [
      /* @__PURE__ */ r.jsx("span", { style: {
        fontSize: "12px",
        fontFamily: "SF Pro Display",
        letterSpacing: "0.12px",
        lineHeight: "16px",
        alignItems: "center",
        userSelect: "none",
        marginRight: "4px",
        color: "rgba(28, 29, 35, 0.35)"
      }, children: "返回最大值" }),
      /* @__PURE__ */ r.jsx(Se, { content: o, children: /* @__PURE__ */ r.jsx(Re, { className: "jade-panel-header-popover-content" }) })
    ] }) }),
    /* @__PURE__ */ r.jsx(U, { span: 12, children: /* @__PURE__ */ r.jsx(
      F.Item,
      {
        style: { marginBottom: "0" },
        id: "valueSource",
        initialValue: "Reference",
        children: /* @__PURE__ */ r.jsx(
          Jt,
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
const gi = (e = {}) => {
  const a = ka.create({
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
  return a.interceptors.request.use((t) => t, (t) => Promise.reject(t)), a.interceptors.response.use((t) => t.data, (t) => Promise.reject(t)), a;
}, Ct = (e, a, t, o, i = () => {
}, s = () => {
}) => {
  gi(t).request({
    method: e,
    url: a,
    data: o
  }).then((n) => {
    i(n);
  }).catch((n) => {
    s(n);
  });
}, vi = (e, a = /* @__PURE__ */ new Map(), t, o) => {
  Ct("Get", e, a, {}, t, o);
}, yi = (e, a, t = /* @__PURE__ */ new Map(), o, i) => {
  Ct("Post", e, t, a, o, i);
}, bi = (e, a, t = /* @__PURE__ */ new Map(), o, i) => {
  Ct("Put", e, t, a, o, i);
}, xi = (e, a = /* @__PURE__ */ new Map(), t, o) => {
  Ct("Delete", e, a, {}, t, o);
}, $e = {
  get: vi,
  post: yi,
  put: bi,
  del: xi
};
function en(e) {
  const [a, t] = de([]), [o, i] = de(!1), [s, l] = de(1), {
    buildUrl: n,
    onChange: d,
    getOptions: u,
    disabled: m,
    dealResponse: c,
    ...f
  } = e, g = n(s), v = async (b) => {
    b && a.length === 0 && (i(!0), $e.get(g, void 0, (x) => {
      const N = c(x);
      N && t(N), i(!1);
    }, (x) => {
      console.error("Error fetching options:", x), i(!1);
    }));
  }, h = async (b) => {
    const { target: x } = b;
    if (x.scrollTop + x.clientHeight !== x.scrollHeight)
      return;
    console.log("Scroll to bottom. Loading new page...");
    const N = s;
    l(s + 1), i(!0), $e.get(g, void 0, (I) => {
      const C = I.data;
      C && t([...a, ...C]), i(!1);
    }, (I) => {
      console.error("Error fetching options:", I), i(!1), l(N);
    });
  }, y = (b) => {
    d(b, a);
  };
  return /* @__PURE__ */ r.jsx(
    me,
    {
      className: "jade-select",
      style: { width: "100%" },
      onPopupScroll: h,
      onDropdownVisibleChange: v,
      onChange: y,
      disabled: m || !1,
      options: u(a),
      loading: o,
      mode: "single",
      ...f
    }
  );
}
const { Panel: ji } = G;
function Cr() {
  const e = re(), a = ne(), t = he(), o = a && [...a.inputParams.find((h) => h.name === "knowledge").value], i = t.graph.configs && t.graph.configs.find((h) => h.node === "knowledgeState").urls.knowledgeUrl, s = (h) => i + "?pageNum=" + h + "&pageSize=10", l = () => o.length <= 1, n = (h) => {
    e({ type: "addKnowledge", id: T() }), h.stopPropagation();
  }, d = (h, y, b, x) => {
    e({ type: "editKnowledge", id: h, key: y, value: x.find((N) => N.id === b) });
  }, u = (h) => {
    e({ type: "clearKnowledge", id: h });
  }, m = (h) => {
    e({ type: "deleteKnowledge", id: h });
  }, c = (h) => h.data.items, f = (h) => h.map((y) => ({
    value: y.id,
    label: y.name
  })), g = (h) => h.value && h.value.length !== 0 ? h.value.find((y) => y.name === "name").value : "", v = /* @__PURE__ */ r.jsxs("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: [
    /* @__PURE__ */ r.jsx("p", { children: "选择需要匹配的知识范围，" }),
    /* @__PURE__ */ r.jsx("p", { children: "仅从所选知识中调出信息" })
  ] });
  return /* @__PURE__ */ r.jsx(
    G,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      style: { marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%" },
      defaultActiveKey: ["Knowledge"],
      children: /* @__PURE__ */ r.jsxs(
        ji,
        {
          style: { marginBottom: 8, borderRadius: "8px", width: "100%" },
          header: /* @__PURE__ */ r.jsxs(
            "div",
            {
              style: { display: "flex", alignItems: "center" },
              children: [
                /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "知识库" }),
                /* @__PURE__ */ r.jsx(Se, { content: v, children: /* @__PURE__ */ r.jsx(Re, { className: "jade-panel-header-popover-content" }) }),
                /* @__PURE__ */ r.jsx(
                  se,
                  {
                    type: "text",
                    className: "icon-button",
                    onClick: (h) => n(h),
                    style: { height: "22px", marginLeft: "auto" },
                    children: /* @__PURE__ */ r.jsx(Ye, {})
                  }
                )
              ]
            }
          ),
          className: "jade-panel",
          children: [
            o.map((h) => /* @__PURE__ */ r.jsxs(
              fe,
              {
                gutter: 16,
                children: [
                  /* @__PURE__ */ r.jsx(U, { span: 22, children: /* @__PURE__ */ r.jsx(
                    F.Item,
                    {
                      style: { marginBottom: "8px" },
                      id: `knowledge-${h.id}`,
                      children: /* @__PURE__ */ r.jsx(
                        en,
                        {
                          allowClear: !0,
                          placeholder: "选择知识库",
                          id: `valueSource-select-${h.id}`,
                          onClear: () => u(h.id),
                          onChange: (y, b) => d(h.id, "value", y, b),
                          buildUrl: s,
                          disabled: !1,
                          getOptions: f,
                          dealResponse: c,
                          value: g(h)
                        }
                      )
                    },
                    `knowledge-${h.id}`
                  ) }),
                  /* @__PURE__ */ r.jsx(U, { span: 2, style: { paddingLeft: "2px" }, children: /* @__PURE__ */ r.jsx(F.Item, { style: { marginBottom: "8px" }, children: /* @__PURE__ */ r.jsx(
                    se,
                    {
                      disabled: l(),
                      type: "text",
                      className: "icon-button",
                      style: { alignItems: "center", marginLeft: "auto" },
                      onClick: () => m(h.id),
                      children: /* @__PURE__ */ r.jsx(yt, {})
                    }
                  ) }, `button-${h.id}`) })
                ]
              },
              `knowledgeRow-${h.id}`
            )),
            /* @__PURE__ */ r.jsx(hi, {})
          ]
        },
        "Knowledge"
      )
    }
  );
}
const st = ({ data: e }) => {
  if (!Array.isArray(e))
    throw new Error("data must be array.");
  const [a, t] = de(null), o = he();
  ge(() => {
    const n = e.map((d) => s(d, null));
    return t(n), () => {
      n && i(n, (d) => {
        o.page.removeObservable(o.id, d.key);
      });
    };
  }, []);
  const i = (n, d) => {
    n.forEach((u) => {
      u.children && i(u.children, d), d(u);
    });
  }, s = (n, d) => (o.page.registerObservable(o.id, n.id, n.name, n.type, d ? d.id : null), n.type === "Object" ? {
    title: n.name,
    type: n.type,
    key: n.id,
    children: n.value.map((u) => s(u, n))
  } : {
    title: n.name,
    type: n.type,
    key: n.id,
    isLeaf: !0
  }), l = (n) => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx("div", { className: "jade-observable-tree-node-div", children: /* @__PURE__ */ r.jsxs("div", { style: { display: "flex" }, children: [
    /* @__PURE__ */ r.jsx("span", { className: "jade-observable-tree-node-title", children: n.title }),
    /* @__PURE__ */ r.jsx("div", { className: "jade-observable-tree-node-type-div", children: /* @__PURE__ */ r.jsx("span", { className: "jade-observable-tree-node-type-name", children: n.type }) })
  ] }) }) });
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
    Zn,
    {
      treeData: a,
      titleRender: (n) => l(n),
      showLine: !0,
      selectable: !1
    }
  ) });
}, { Panel: Ci } = G;
function wr() {
  const e = ne();
  he();
  const a = e && e.outputParams, t = /* @__PURE__ */ r.jsx("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: /* @__PURE__ */ r.jsx("p", { children: "输出列表是与输入参数最匹配的信息，从所有选定的知识库中调用" }) });
  return /* @__PURE__ */ r.jsx(
    G,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      style: { marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%" },
      defaultActiveKey: ["Output"],
      children: /* @__PURE__ */ r.jsx(
        Ci,
        {
          header: /* @__PURE__ */ r.jsxs(
            "div",
            {
              style: { display: "flex", alignItems: "center", paddingLeft: "-16px" },
              children: [
                /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "输出" }),
                /* @__PURE__ */ r.jsx(Se, { content: t, children: /* @__PURE__ */ r.jsx(Re, { className: "jade-panel-header-popover-content" }) })
              ]
            }
          ),
          className: "jade-panel",
          children: /* @__PURE__ */ r.jsx(st, { data: a })
        },
        "Output"
      )
    }
  );
}
const wi = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  return n.type = "retrievalNodeState", n.backColor = "white", n.pointerEvents = "auto", n.text = "普通检索", n.componentName = "retrievalComponent", n.flowMeta.jober.fitables.push("com.huawei.fit.jober.aipp.fitable.NaiveRAGComponent"), n.flowMeta.triggerMode = "auto", n.getReactComponents = () => /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
    /* @__PURE__ */ r.jsx(jr, {}),
    /* @__PURE__ */ r.jsx(Cr, {}),
    /* @__PURE__ */ r.jsx(wr, {})
  ] }), n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(
    se,
    {
      disabled: !0,
      className: "jade-node-custom-header-icon",
      children: /* @__PURE__ */ r.jsx(pi, {})
    }
  ), n;
}, Ei = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || {
    inputParams: [{
      id: "query_" + T(),
      name: "query",
      type: "String",
      from: "Reference",
      referenceNode: "",
      referenceId: "",
      referenceKey: "",
      value: []
    }, {
      id: "knowledge_" + T(),
      name: "knowledge",
      type: "Array",
      from: "Expand",
      value: [{
        id: T(),
        type: "Object",
        from: "Expand",
        value: []
      }]
    }, {
      id: "maximum_" + T(),
      name: "maximum",
      type: "Integer",
      from: "Input",
      value: 3
    }],
    outputParams: [{
      id: "output_" + T(),
      name: "output",
      type: "Object",
      from: "Expand",
      value: [{
        id: T(),
        name: "retrievalOutput",
        type: "String",
        from: "Input",
        value: "String"
      }]
    }]
  }, a.getReactComponents = () => /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
    /* @__PURE__ */ r.jsx(jr, {}),
    /* @__PURE__ */ r.jsx(Cr, {}),
    /* @__PURE__ */ r.jsx(wr, {})
  ] }), a.reducers = (t, o) => {
    const i = () => {
      const f = c.inputParams.find((g) => g.name === "query");
      o.changes.map((g) => {
        f[g.key] = g.value;
      });
    }, s = () => {
      u().push({
        id: o.id,
        name: "",
        type: "Object",
        from: "Expand",
        value: []
      });
    }, l = () => {
      const f = u(), g = f.findIndex((v) => v.id === o.id);
      g !== -1 && f.splice(g, 1);
    }, n = () => {
      if (!o.value)
        return;
      const f = o.value.id, g = o.value.name, v = u().find((h) => h.id === o.id).value;
      v.length === 0 ? (v.push({ id: T(), name: "id", from: "Input", type: "String", value: f }), v.push({ id: T(), name: "name", from: "Input", type: "String", value: g })) : v.forEach((h) => {
        h.name === "id" && (h.value = f), h.name === "name" && (h.value = g);
      });
    }, d = () => {
      c.inputParams.filter((f) => f.name === "maximum").forEach((f) => {
        f.value = o.value;
      });
    }, u = () => c.inputParams.find((f) => f.name === "knowledge").value, m = () => {
      u().find((f) => f.id === o.id).value = [];
    };
    let c = { ...t };
    switch (o.type) {
      case "editInput":
        return i(), c;
      case "addKnowledge":
        return s(), c;
      case "deleteKnowledge":
        return l(), c;
      case "editKnowledge":
        return n(), c;
      case "changeMaximum":
        return d(), c;
      case "clearKnowledge":
        return m(), c;
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, a;
}, Ni = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  return n.type = "listener1Node", n.text = "被监听者", n.componentName = "listener1Component", n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(Je, {}), n;
}, Ht = (e) => {
  const { onChange: a, onBlur: t, ...o } = e;
  if (!o.id)
    throw new Error("JadeObservableInput requires an id property.");
  const i = he();
  if (!i)
    throw new Error("JadeObservableInput must be wrapped by ShapeContext.");
  const s = (n) => {
    a && a(n), i.emit(o.id, { value: n.target.value });
  }, l = (n) => {
    t && t(n);
  };
  return ge(() => (i.page.registerObservable(i.id, o.id, o.value, o.type, o.parent), () => {
    i.page.removeObservable(i.id, o.id);
  }), []), ge(() => {
    i.emit(o.id, { type: o.type });
  }, [o.type]), /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(xe, { ...o, onChange: (n) => s(n), onBlur: l }) });
}, Si = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || [{
    id: "listener1-name",
    name: "name",
    type: "String",
    value: "请输入一个名字"
  }, {
    id: "listener1-firstName",
    name: "firstName",
    type: "String",
    value: "请输入第一名字"
  }], a.getReactComponents = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Ri, {}) }), a.reducers = (t, o) => {
    switch (o.type) {
      case "updateName":
        return t.map((i) => i.id === o.id ? { ...i, name: o.name } : i);
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, a;
}, Ri = () => {
  const e = ne(), a = re(), t = (o, i) => {
    a({ type: "updateName", id: o, name: i.target.value });
  };
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsxs("div", { children: [
    /* @__PURE__ */ r.jsx(
      Ht,
      {
        id: e[0].id,
        value: e[0].name,
        onChange: (o) => t(e[0].id, o)
      }
    ),
    /* @__PURE__ */ r.jsx(
      Ht,
      {
        id: e[1].id,
        parent: e[0].id,
        value: e[1].name,
        onChange: (o) => t(e[1].id, o)
      }
    )
  ] }) });
}, ki = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  return n.type = "listener2Node", n.text = "监听者", n.componentName = "listener2Component", n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(Je, {}), n;
}, Pi = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || [{
    id: "123456",
    name: "zzzzz",
    type: "Reference",
    value: [],
    referenceNode: "",
    referenceId: "",
    referenceKey: ""
  }], a.getReactComponents = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Ii, {}) }), a.reducers = (t, o) => {
    switch (o.type) {
      case "updateValue":
        return t.map((i) => i.id === o.id ? { ...i, referenceKey: o.referenceKey } : i);
      case "update":
        return t.map((i) => i.id === o.id ? { ...i, referenceNode: o.referenceNode, referenceId: o.referenceId, value: o.value } : i);
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, a;
}, Ii = () => {
  const e = re(), a = ne();
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(De, { reference: a[0], onReferencedValueChange: (t) => {
    e({ type: "updateValue", id: a[0].id, referenceKey: t });
  }, onReferencedKeyChange: (t) => {
    e({ type: "update", id: a[0].id, referenceNode: t.referenceNode, referenceId: t.referenceId, value: t.value });
  } }) });
}, Oi = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  return n.type = "listener3Node", n.text = "被监听者", n.componentName = "listener3Component", n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(Je, {}), n;
}, Ti = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || {
    output: [{
      id: "uuid1",
      name: "person",
      type: "Object",
      value: [{ id: "uuid2", name: "name", type: "String" }, { id: "uuid3", name: "age", type: "Integer" }]
    }]
  }, a.getReactComponents = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Fi, {}) }), a.reducers = () => {
  }, a;
}, Fi = () => {
  const e = ne();
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx("div", { children: /* @__PURE__ */ r.jsx(st, { data: e.output }) }) });
}, Li = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  return n.type = "jadeInputTreeNode", n.text = "被监听者", n.componentName = "jadeInputTreeComponent", n.width = 360, n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(Je, {}), n;
};
var Kt = { exports: {} }, ht = { exports: {} }, X = {};
/** @license React v16.13.1
 * react-is.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var Ln;
function _i() {
  if (Ln)
    return X;
  Ln = 1;
  var e = typeof Symbol == "function" && Symbol.for, a = e ? Symbol.for("react.element") : 60103, t = e ? Symbol.for("react.portal") : 60106, o = e ? Symbol.for("react.fragment") : 60107, i = e ? Symbol.for("react.strict_mode") : 60108, s = e ? Symbol.for("react.profiler") : 60114, l = e ? Symbol.for("react.provider") : 60109, n = e ? Symbol.for("react.context") : 60110, d = e ? Symbol.for("react.async_mode") : 60111, u = e ? Symbol.for("react.concurrent_mode") : 60111, m = e ? Symbol.for("react.forward_ref") : 60112, c = e ? Symbol.for("react.suspense") : 60113, f = e ? Symbol.for("react.suspense_list") : 60120, g = e ? Symbol.for("react.memo") : 60115, v = e ? Symbol.for("react.lazy") : 60116, h = e ? Symbol.for("react.block") : 60121, y = e ? Symbol.for("react.fundamental") : 60117, b = e ? Symbol.for("react.responder") : 60118, x = e ? Symbol.for("react.scope") : 60119;
  function N(C) {
    if (typeof C == "object" && C !== null) {
      var ae = C.$$typeof;
      switch (ae) {
        case a:
          switch (C = C.type, C) {
            case d:
            case u:
            case o:
            case s:
            case i:
            case c:
              return C;
            default:
              switch (C = C && C.$$typeof, C) {
                case n:
                case m:
                case v:
                case g:
                case l:
                  return C;
                default:
                  return ae;
              }
          }
        case t:
          return ae;
      }
    }
  }
  function I(C) {
    return N(C) === u;
  }
  return X.AsyncMode = d, X.ConcurrentMode = u, X.ContextConsumer = n, X.ContextProvider = l, X.Element = a, X.ForwardRef = m, X.Fragment = o, X.Lazy = v, X.Memo = g, X.Portal = t, X.Profiler = s, X.StrictMode = i, X.Suspense = c, X.isAsyncMode = function(C) {
    return I(C) || N(C) === d;
  }, X.isConcurrentMode = I, X.isContextConsumer = function(C) {
    return N(C) === n;
  }, X.isContextProvider = function(C) {
    return N(C) === l;
  }, X.isElement = function(C) {
    return typeof C == "object" && C !== null && C.$$typeof === a;
  }, X.isForwardRef = function(C) {
    return N(C) === m;
  }, X.isFragment = function(C) {
    return N(C) === o;
  }, X.isLazy = function(C) {
    return N(C) === v;
  }, X.isMemo = function(C) {
    return N(C) === g;
  }, X.isPortal = function(C) {
    return N(C) === t;
  }, X.isProfiler = function(C) {
    return N(C) === s;
  }, X.isStrictMode = function(C) {
    return N(C) === i;
  }, X.isSuspense = function(C) {
    return N(C) === c;
  }, X.isValidElementType = function(C) {
    return typeof C == "string" || typeof C == "function" || C === o || C === u || C === s || C === i || C === c || C === f || typeof C == "object" && C !== null && (C.$$typeof === v || C.$$typeof === g || C.$$typeof === l || C.$$typeof === n || C.$$typeof === m || C.$$typeof === y || C.$$typeof === b || C.$$typeof === x || C.$$typeof === h);
  }, X.typeOf = N, X;
}
var Q = {};
/** @license React v16.13.1
 * react-is.development.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var _n;
function Mi() {
  return _n || (_n = 1, process.env.NODE_ENV !== "production" && function() {
    var e = typeof Symbol == "function" && Symbol.for, a = e ? Symbol.for("react.element") : 60103, t = e ? Symbol.for("react.portal") : 60106, o = e ? Symbol.for("react.fragment") : 60107, i = e ? Symbol.for("react.strict_mode") : 60108, s = e ? Symbol.for("react.profiler") : 60114, l = e ? Symbol.for("react.provider") : 60109, n = e ? Symbol.for("react.context") : 60110, d = e ? Symbol.for("react.async_mode") : 60111, u = e ? Symbol.for("react.concurrent_mode") : 60111, m = e ? Symbol.for("react.forward_ref") : 60112, c = e ? Symbol.for("react.suspense") : 60113, f = e ? Symbol.for("react.suspense_list") : 60120, g = e ? Symbol.for("react.memo") : 60115, v = e ? Symbol.for("react.lazy") : 60116, h = e ? Symbol.for("react.block") : 60121, y = e ? Symbol.for("react.fundamental") : 60117, b = e ? Symbol.for("react.responder") : 60118, x = e ? Symbol.for("react.scope") : 60119;
    function N(k) {
      return typeof k == "string" || typeof k == "function" || // Note: its typeof might be other than 'symbol' or 'number' if it's a polyfill.
      k === o || k === u || k === s || k === i || k === c || k === f || typeof k == "object" && k !== null && (k.$$typeof === v || k.$$typeof === g || k.$$typeof === l || k.$$typeof === n || k.$$typeof === m || k.$$typeof === y || k.$$typeof === b || k.$$typeof === x || k.$$typeof === h);
    }
    function I(k) {
      if (typeof k == "object" && k !== null) {
        var je = k.$$typeof;
        switch (je) {
          case a:
            var qe = k.type;
            switch (qe) {
              case d:
              case u:
              case o:
              case s:
              case i:
              case c:
                return qe;
              default:
                var Be = qe && qe.$$typeof;
                switch (Be) {
                  case n:
                  case m:
                  case v:
                  case g:
                  case l:
                    return Be;
                  default:
                    return je;
                }
            }
          case t:
            return je;
        }
      }
    }
    var C = d, ae = u, ye = n, Me = l, Ie = a, Ae = m, Oe = o, Te = v, S = g, O = t, _ = s, B = i, te = c, le = !1;
    function Ve(k) {
      return le || (le = !0, console.warn("The ReactIs.isAsyncMode() alias has been deprecated, and will be removed in React 17+. Update your code to use ReactIs.isConcurrentMode() instead. It has the exact same API.")), E(k) || I(k) === d;
    }
    function E(k) {
      return I(k) === u;
    }
    function P(k) {
      return I(k) === n;
    }
    function V(k) {
      return I(k) === l;
    }
    function D(k) {
      return typeof k == "object" && k !== null && k.$$typeof === a;
    }
    function A(k) {
      return I(k) === m;
    }
    function K(k) {
      return I(k) === o;
    }
    function $(k) {
      return I(k) === v;
    }
    function z(k) {
      return I(k) === g;
    }
    function q(k) {
      return I(k) === t;
    }
    function Y(k) {
      return I(k) === s;
    }
    function W(k) {
      return I(k) === i;
    }
    function ue(k) {
      return I(k) === c;
    }
    Q.AsyncMode = C, Q.ConcurrentMode = ae, Q.ContextConsumer = ye, Q.ContextProvider = Me, Q.Element = Ie, Q.ForwardRef = Ae, Q.Fragment = Oe, Q.Lazy = Te, Q.Memo = S, Q.Portal = O, Q.Profiler = _, Q.StrictMode = B, Q.Suspense = te, Q.isAsyncMode = Ve, Q.isConcurrentMode = E, Q.isContextConsumer = P, Q.isContextProvider = V, Q.isElement = D, Q.isForwardRef = A, Q.isFragment = K, Q.isLazy = $, Q.isMemo = z, Q.isPortal = q, Q.isProfiler = Y, Q.isStrictMode = W, Q.isSuspense = ue, Q.isValidElementType = N, Q.typeOf = I;
  }()), Q;
}
var Mn;
function Er() {
  return Mn || (Mn = 1, process.env.NODE_ENV === "production" ? ht.exports = _i() : ht.exports = Mi()), ht.exports;
}
/*
object-assign
(c) Sindre Sorhus
@license MIT
*/
var Lt, An;
function Ai() {
  if (An)
    return Lt;
  An = 1;
  var e = Object.getOwnPropertySymbols, a = Object.prototype.hasOwnProperty, t = Object.prototype.propertyIsEnumerable;
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
      var d = Object.getOwnPropertyNames(l).map(function(m) {
        return l[m];
      });
      if (d.join("") !== "0123456789")
        return !1;
      var u = {};
      return "abcdefghijklmnopqrst".split("").forEach(function(m) {
        u[m] = m;
      }), Object.keys(Object.assign({}, u)).join("") === "abcdefghijklmnopqrst";
    } catch {
      return !1;
    }
  }
  return Lt = i() ? Object.assign : function(s, l) {
    for (var n, d = o(s), u, m = 1; m < arguments.length; m++) {
      n = Object(arguments[m]);
      for (var c in n)
        a.call(n, c) && (d[c] = n[c]);
      if (e) {
        u = e(n);
        for (var f = 0; f < u.length; f++)
          t.call(n, u[f]) && (d[u[f]] = n[u[f]]);
      }
    }
    return d;
  }, Lt;
}
var _t, Bn;
function tn() {
  if (Bn)
    return _t;
  Bn = 1;
  var e = "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED";
  return _t = e, _t;
}
var Mt, $n;
function Nr() {
  return $n || ($n = 1, Mt = Function.call.bind(Object.prototype.hasOwnProperty)), Mt;
}
var At, zn;
function Bi() {
  if (zn)
    return At;
  zn = 1;
  var e = function() {
  };
  if (process.env.NODE_ENV !== "production") {
    var a = tn(), t = {}, o = Nr();
    e = function(s) {
      var l = "Warning: " + s;
      typeof console < "u" && console.error(l);
      try {
        throw new Error(l);
      } catch {
      }
    };
  }
  function i(s, l, n, d, u) {
    if (process.env.NODE_ENV !== "production") {
      for (var m in s)
        if (o(s, m)) {
          var c;
          try {
            if (typeof s[m] != "function") {
              var f = Error(
                (d || "React class") + ": " + n + " type `" + m + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + typeof s[m] + "`.This often happens because of typos such as `PropTypes.function` instead of `PropTypes.func`."
              );
              throw f.name = "Invariant Violation", f;
            }
            c = s[m](l, m, d, n, null, a);
          } catch (v) {
            c = v;
          }
          if (c && !(c instanceof Error) && e(
            (d || "React class") + ": type specification of " + n + " `" + m + "` is invalid; the type checker function must return `null` or an `Error` but returned a " + typeof c + ". You may have forgotten to pass an argument to the type checker creator (arrayOf, instanceOf, objectOf, oneOf, oneOfType, and shape all require an argument)."
          ), c instanceof Error && !(c.message in t)) {
            t[c.message] = !0;
            var g = u ? u() : "";
            e(
              "Failed " + n + " type: " + c.message + (g ?? "")
            );
          }
        }
    }
  }
  return i.resetWarningCache = function() {
    process.env.NODE_ENV !== "production" && (t = {});
  }, At = i, At;
}
var Bt, Dn;
function $i() {
  if (Dn)
    return Bt;
  Dn = 1;
  var e = Er(), a = Ai(), t = tn(), o = Nr(), i = Bi(), s = function() {
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
  return Bt = function(n, d) {
    var u = typeof Symbol == "function" && Symbol.iterator, m = "@@iterator";
    function c(E) {
      var P = E && (u && E[u] || E[m]);
      if (typeof P == "function")
        return P;
    }
    var f = "<<anonymous>>", g = {
      array: b("array"),
      bigint: b("bigint"),
      bool: b("boolean"),
      func: b("function"),
      number: b("number"),
      object: b("object"),
      string: b("string"),
      symbol: b("symbol"),
      any: x(),
      arrayOf: N,
      element: I(),
      elementType: C(),
      instanceOf: ae,
      node: Ae(),
      objectOf: Me,
      oneOf: ye,
      oneOfType: Ie,
      shape: Te,
      exact: S
    };
    function v(E, P) {
      return E === P ? E !== 0 || 1 / E === 1 / P : E !== E && P !== P;
    }
    function h(E, P) {
      this.message = E, this.data = P && typeof P == "object" ? P : {}, this.stack = "";
    }
    h.prototype = Error.prototype;
    function y(E) {
      if (process.env.NODE_ENV !== "production")
        var P = {}, V = 0;
      function D(K, $, z, q, Y, W, ue) {
        if (q = q || f, W = W || z, ue !== t) {
          if (d) {
            var k = new Error(
              "Calling PropTypes validators directly is not supported by the `prop-types` package. Use `PropTypes.checkPropTypes()` to call them. Read more at http://fb.me/use-check-prop-types"
            );
            throw k.name = "Invariant Violation", k;
          } else if (process.env.NODE_ENV !== "production" && typeof console < "u") {
            var je = q + ":" + z;
            !P[je] && // Avoid spamming the console because they are often not actionable except for lib authors
            V < 3 && (s(
              "You are manually calling a React.PropTypes validation function for the `" + W + "` prop on `" + q + "`. This is deprecated and will throw in the standalone `prop-types` package. You may be seeing this warning due to a third-party PropTypes library. See https://fb.me/react-warning-dont-call-proptypes for details."
            ), P[je] = !0, V++);
          }
        }
        return $[z] == null ? K ? $[z] === null ? new h("The " + Y + " `" + W + "` is marked as required " + ("in `" + q + "`, but its value is `null`.")) : new h("The " + Y + " `" + W + "` is marked as required in " + ("`" + q + "`, but its value is `undefined`.")) : null : E($, z, q, Y, W);
      }
      var A = D.bind(null, !1);
      return A.isRequired = D.bind(null, !0), A;
    }
    function b(E) {
      function P(V, D, A, K, $, z) {
        var q = V[D], Y = B(q);
        if (Y !== E) {
          var W = te(q);
          return new h(
            "Invalid " + K + " `" + $ + "` of type " + ("`" + W + "` supplied to `" + A + "`, expected ") + ("`" + E + "`."),
            { expectedType: E }
          );
        }
        return null;
      }
      return y(P);
    }
    function x() {
      return y(l);
    }
    function N(E) {
      function P(V, D, A, K, $) {
        if (typeof E != "function")
          return new h("Property `" + $ + "` of component `" + A + "` has invalid PropType notation inside arrayOf.");
        var z = V[D];
        if (!Array.isArray(z)) {
          var q = B(z);
          return new h("Invalid " + K + " `" + $ + "` of type " + ("`" + q + "` supplied to `" + A + "`, expected an array."));
        }
        for (var Y = 0; Y < z.length; Y++) {
          var W = E(z, Y, A, K, $ + "[" + Y + "]", t);
          if (W instanceof Error)
            return W;
        }
        return null;
      }
      return y(P);
    }
    function I() {
      function E(P, V, D, A, K) {
        var $ = P[V];
        if (!n($)) {
          var z = B($);
          return new h("Invalid " + A + " `" + K + "` of type " + ("`" + z + "` supplied to `" + D + "`, expected a single ReactElement."));
        }
        return null;
      }
      return y(E);
    }
    function C() {
      function E(P, V, D, A, K) {
        var $ = P[V];
        if (!e.isValidElementType($)) {
          var z = B($);
          return new h("Invalid " + A + " `" + K + "` of type " + ("`" + z + "` supplied to `" + D + "`, expected a single ReactElement type."));
        }
        return null;
      }
      return y(E);
    }
    function ae(E) {
      function P(V, D, A, K, $) {
        if (!(V[D] instanceof E)) {
          var z = E.name || f, q = Ve(V[D]);
          return new h("Invalid " + K + " `" + $ + "` of type " + ("`" + q + "` supplied to `" + A + "`, expected ") + ("instance of `" + z + "`."));
        }
        return null;
      }
      return y(P);
    }
    function ye(E) {
      if (!Array.isArray(E))
        return process.env.NODE_ENV !== "production" && (arguments.length > 1 ? s(
          "Invalid arguments supplied to oneOf, expected an array, got " + arguments.length + " arguments. A common mistake is to write oneOf(x, y, z) instead of oneOf([x, y, z])."
        ) : s("Invalid argument supplied to oneOf, expected an array.")), l;
      function P(V, D, A, K, $) {
        for (var z = V[D], q = 0; q < E.length; q++)
          if (v(z, E[q]))
            return null;
        var Y = JSON.stringify(E, function(ue, k) {
          var je = te(k);
          return je === "symbol" ? String(k) : k;
        });
        return new h("Invalid " + K + " `" + $ + "` of value `" + String(z) + "` " + ("supplied to `" + A + "`, expected one of " + Y + "."));
      }
      return y(P);
    }
    function Me(E) {
      function P(V, D, A, K, $) {
        if (typeof E != "function")
          return new h("Property `" + $ + "` of component `" + A + "` has invalid PropType notation inside objectOf.");
        var z = V[D], q = B(z);
        if (q !== "object")
          return new h("Invalid " + K + " `" + $ + "` of type " + ("`" + q + "` supplied to `" + A + "`, expected an object."));
        for (var Y in z)
          if (o(z, Y)) {
            var W = E(z, Y, A, K, $ + "." + Y, t);
            if (W instanceof Error)
              return W;
          }
        return null;
      }
      return y(P);
    }
    function Ie(E) {
      if (!Array.isArray(E))
        return process.env.NODE_ENV !== "production" && s("Invalid argument supplied to oneOfType, expected an instance of array."), l;
      for (var P = 0; P < E.length; P++) {
        var V = E[P];
        if (typeof V != "function")
          return s(
            "Invalid argument supplied to oneOfType. Expected an array of check functions, but received " + le(V) + " at index " + P + "."
          ), l;
      }
      function D(A, K, $, z, q) {
        for (var Y = [], W = 0; W < E.length; W++) {
          var ue = E[W], k = ue(A, K, $, z, q, t);
          if (k == null)
            return null;
          k.data && o(k.data, "expectedType") && Y.push(k.data.expectedType);
        }
        var je = Y.length > 0 ? ", expected one of type [" + Y.join(", ") + "]" : "";
        return new h("Invalid " + z + " `" + q + "` supplied to " + ("`" + $ + "`" + je + "."));
      }
      return y(D);
    }
    function Ae() {
      function E(P, V, D, A, K) {
        return O(P[V]) ? null : new h("Invalid " + A + " `" + K + "` supplied to " + ("`" + D + "`, expected a ReactNode."));
      }
      return y(E);
    }
    function Oe(E, P, V, D, A) {
      return new h(
        (E || "React class") + ": " + P + " type `" + V + "." + D + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + A + "`."
      );
    }
    function Te(E) {
      function P(V, D, A, K, $) {
        var z = V[D], q = B(z);
        if (q !== "object")
          return new h("Invalid " + K + " `" + $ + "` of type `" + q + "` " + ("supplied to `" + A + "`, expected `object`."));
        for (var Y in E) {
          var W = E[Y];
          if (typeof W != "function")
            return Oe(A, K, $, Y, te(W));
          var ue = W(z, Y, A, K, $ + "." + Y, t);
          if (ue)
            return ue;
        }
        return null;
      }
      return y(P);
    }
    function S(E) {
      function P(V, D, A, K, $) {
        var z = V[D], q = B(z);
        if (q !== "object")
          return new h("Invalid " + K + " `" + $ + "` of type `" + q + "` " + ("supplied to `" + A + "`, expected `object`."));
        var Y = a({}, V[D], E);
        for (var W in Y) {
          var ue = E[W];
          if (o(E, W) && typeof ue != "function")
            return Oe(A, K, $, W, te(ue));
          if (!ue)
            return new h(
              "Invalid " + K + " `" + $ + "` key `" + W + "` supplied to `" + A + "`.\nBad object: " + JSON.stringify(V[D], null, "  ") + `
Valid keys: ` + JSON.stringify(Object.keys(E), null, "  ")
            );
          var k = ue(z, W, A, K, $ + "." + W, t);
          if (k)
            return k;
        }
        return null;
      }
      return y(P);
    }
    function O(E) {
      switch (typeof E) {
        case "number":
        case "string":
        case "undefined":
          return !0;
        case "boolean":
          return !E;
        case "object":
          if (Array.isArray(E))
            return E.every(O);
          if (E === null || n(E))
            return !0;
          var P = c(E);
          if (P) {
            var V = P.call(E), D;
            if (P !== E.entries) {
              for (; !(D = V.next()).done; )
                if (!O(D.value))
                  return !1;
            } else
              for (; !(D = V.next()).done; ) {
                var A = D.value;
                if (A && !O(A[1]))
                  return !1;
              }
          } else
            return !1;
          return !0;
        default:
          return !1;
      }
    }
    function _(E, P) {
      return E === "symbol" ? !0 : P ? P["@@toStringTag"] === "Symbol" || typeof Symbol == "function" && P instanceof Symbol : !1;
    }
    function B(E) {
      var P = typeof E;
      return Array.isArray(E) ? "array" : E instanceof RegExp ? "object" : _(P, E) ? "symbol" : P;
    }
    function te(E) {
      if (typeof E > "u" || E === null)
        return "" + E;
      var P = B(E);
      if (P === "object") {
        if (E instanceof Date)
          return "date";
        if (E instanceof RegExp)
          return "regexp";
      }
      return P;
    }
    function le(E) {
      var P = te(E);
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
    function Ve(E) {
      return !E.constructor || !E.constructor.name ? f : E.constructor.name;
    }
    return g.checkPropTypes = i, g.resetWarningCache = i.resetWarningCache, g.PropTypes = g, g;
  }, Bt;
}
var $t, Vn;
function zi() {
  if (Vn)
    return $t;
  Vn = 1;
  var e = tn();
  function a() {
  }
  function t() {
  }
  return t.resetWarningCache = a, $t = function() {
    function o(l, n, d, u, m, c) {
      if (c !== e) {
        var f = new Error(
          "Calling PropTypes validators directly is not supported by the `prop-types` package. Use PropTypes.checkPropTypes() to call them. Read more at http://fb.me/use-check-prop-types"
        );
        throw f.name = "Invariant Violation", f;
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
      resetWarningCache: a
    };
    return s.PropTypes = s, s;
  }, $t;
}
if (process.env.NODE_ENV !== "production") {
  var Di = Er(), Vi = !0;
  Kt.exports = $i()(Di.isElement, Vi);
} else
  Kt.exports = zi()();
var qi = Kt.exports;
const M = /* @__PURE__ */ Gn(qi), Sr = (e, a) => {
  if (e.from === "Expand")
    return {
      id: e.id,
      title: e.name,
      type: e.type,
      key: e.id,
      level: a,
      from: e.from,
      children: e.value.map((t) => Sr(t, a + 1))
    };
  {
    const t = {
      id: e.id,
      title: e.name,
      type: e.type,
      key: e.id,
      level: a,
      value: e.value,
      from: e.from,
      referenceKey: e.referenceKey,
      referenceNode: e.referenceNode,
      referenceId: e.referenceId,
      isLeaf: !0
    };
    return e.generic && (t.generic = e.generic), e.type === "Object" && (t.props = e.props), t;
  }
};
Rr.propTypes = {
  data: M.array.isRequired,
  updateItem: M.func.isRequired
};
const Wi = 110, Ui = 24;
function Rr({ data: e, updateItem: a }) {
  const t = e.map((u) => Sr(u, 0)), o = (u, m, c) => {
    a(u, [{ key: m, value: c.target.value }]);
  }, i = (u, m) => {
    a(u, [{ key: "referenceKey", value: m }]);
  }, s = (u, m) => {
    a(u, [
      { key: "referenceNode", value: m.referenceNode },
      { key: "referenceId", value: m.referenceId },
      { key: "value", value: m.value }
    ]);
  }, l = (u) => u.from === "Input" ? /* @__PURE__ */ r.jsx(
    xe,
    {
      className: "jade-input",
      style: { borderRadius: "0px 8px 8px 0px" },
      placeholder: "请输入",
      value: u.value,
      onChange: (m) => o(u.id, "value", m)
    }
  ) : u.from === "Reference" ? /* @__PURE__ */ r.jsx(
    De,
    {
      className: "jade-input-tree-title-tree-select jade-select",
      rules: [{ required: !0, message: "字段值不能为空" }],
      reference: u,
      onReferencedKeyChange: (m) => s(u.id, m),
      onReferencedValueChange: (m) => i(u.id, m)
    }
  ) : null, n = (u) => {
    switch (u.type) {
      case "Object":
        return u.hasOwnProperty("generic") ? [{ value: "Reference", label: "引用" }] : [{ value: "Reference", label: "引用" }, { value: "Expand", label: "展开" }];
      case "Array":
        return [{ value: "Reference", label: "引用" }];
      default:
        return [{ value: "Reference", label: "引用" }];
    }
  }, d = (u) => {
    const m = Wi - u.level * Ui;
    return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx("div", { className: "jade-input-tree-title", children: /* @__PURE__ */ r.jsxs(fe, { wrap: !1, children: [
      /* @__PURE__ */ r.jsx(U, { flex: "0 0 " + m + "px", children: /* @__PURE__ */ r.jsx(
        F.Item,
        {
          name: `property-${u.id}`,
          children: /* @__PURE__ */ r.jsx(
            "div",
            {
              className: "jade-input-tree-title-child",
              style: { display: "flex", alignItems: "center" },
              children: /* @__PURE__ */ r.jsx("span", { children: u.title })
            }
          )
        }
      ) }),
      /* @__PURE__ */ r.jsx(U, { flex: "0 0 70px", style: { paddingRight: 0 }, children: /* @__PURE__ */ r.jsx(
        F.Item,
        {
          name: `value-select-${u.id}`,
          children: /* @__PURE__ */ r.jsx("div", { className: "jade-input-tree-title-child", children: /* @__PURE__ */ r.jsx(Hi, { node: u, options: n(u), updateItem: a }) })
        }
      ) }),
      /* @__PURE__ */ r.jsx(U, { children: /* @__PURE__ */ r.jsx("div", { className: "jade-input-tree-title-child", children: l(u) }) })
    ] }) }) });
  };
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Sa, { theme: { components: { Tree: { nodeSelectedBg: "transparent", nodeHoverBg: "transparent" } } }, children: /* @__PURE__ */ r.jsx(
    Zn,
    {
      blockNode: !0,
      treeData: t,
      className: "jade-ant-tree",
      titleRender: d,
      showLine: !0
    }
  ) }) });
}
const Hi = ({ node: e, options: a, updateItem: t }) => {
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
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
    me,
    {
      style: { background: "#f7f7f7", width: "100%" },
      placeholder: "请选择",
      defaultValue: e.from,
      className: "jade-input-tree-title-select jade-select",
      onChange: o,
      options: a
    }
  ) });
}, { Panel: Ki } = G;
function kr({ data: e, updateItem: a }) {
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
    G,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      defaultActiveKey: ["jadeInputTreePanel"],
      children: /* @__PURE__ */ r.jsx(
        Ki,
        {
          header: /* @__PURE__ */ r.jsx("div", { children: /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "输入" }) }),
          className: "jade-panel",
          children: /* @__PURE__ */ r.jsx(Rr, { data: e, updateItem: a })
        },
        "jadeInputTreePanel"
      )
    }
  ) });
}
const Ji = (e) => {
  const a = {};
  a.getJadeConfig = () => e || {
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
  }, a.getReactComponents = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Yi, {}) });
  const t = (o, i, s) => o.map((l) => {
    const n = { ...l };
    return l.id === i ? (s.forEach((d) => {
      n[d.key] = d.value;
    }), n) : (n.type === "Object" && Array.isArray(n.value) && (n.value = t(n.value, i, s)), n);
  });
  return a.reducers = (o, i) => {
    switch (i.type) {
      case "update":
        return { input: t(o.input, i.id, i.changes) };
      default:
        throw Error("Unknown action: " + i.type);
    }
  }, a;
}, Yi = () => {
  const e = ne(), a = re(), t = (o, i) => {
    a({ type: "update", id: o, changes: i });
  };
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx("div", { children: /* @__PURE__ */ r.jsx(kr, { data: e.input, updateItem: t }) }) });
}, Zi = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  return n.type = "testNode", n.text = "测试组件", n.componentName = "testComponent", n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(Je, {}), n;
}, Gi = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || [{ name: "description", type: "String", value: "这是一个测试" }], a.getReactComponents = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Xi, {}) }), a.reducers = (t, o) => {
    if (o.type === "update")
      return [{ ...t[0], value: o.value }];
  }, a;
}, Xi = (e) => {
  const a = rt(null), t = ne(), o = re(), i = () => {
    o({ type: "update", value: a.current.input.value });
  };
  return /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
    /* @__PURE__ */ r.jsx("div", { children: /* @__PURE__ */ r.jsx(xe, { ref: a, value: t[0].value, placeholder: "Basic usage", onChange: () => i() }) }),
    /* @__PURE__ */ r.jsx(Qi, { ...e })
  ] });
}, Qi = (e) => /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
  /* @__PURE__ */ r.jsx("span", { children: e.a }),
  /* @__PURE__ */ r.jsx("span", { children: e.b }),
  /* @__PURE__ */ r.jsx("span", { children: e.c })
] }), es = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || [{ name: "description", type: "String", value: "替换之前的输入框" }], a.getReactComponents = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(ts, {}) }), a.reducers = (t, o) => {
    if (o.type === "update")
      return [{ ...t[0], value: o.value }];
  }, a;
}, ts = () => {
  const e = rt(null), a = ne(), t = re(), o = () => {
    t({ type: "update", value: e.current.input.value });
  };
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx("div", { children: /* @__PURE__ */ r.jsx(xe, { ref: e, value: a[0].value, placeholder: "Basic usage", onChange: () => o() }) }) });
}, ns = (e) => /* @__PURE__ */ w.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ w.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ w.createElement("defs", null, /* @__PURE__ */ w.createElement("clipPath", { id: "clip4_13280" }, /* @__PURE__ */ w.createElement("rect", { id: "\\u56FE\\u6807/16/\\u5F00\\u59CB\\uFF0C\\u8D77\\u70B9\\uFF0C\\u7AEF\\u70B9", width: 16, height: 16, transform: "translate(4.000000 4.000000)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ w.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#5E7CE0", fillOpacity: 1 }), /* @__PURE__ */ w.createElement("g", { clipPath: "url(#clip4_13280)" }, /* @__PURE__ */ w.createElement("path", { id: "path", d: "M16.58 11.52L18.16 11.52L18.16 6.31L11.46 6.31C8.35 6.37 5.83 8.89 5.83 11.99C5.83 15.1 8.35 17.62 11.46 17.67L18.16 17.67L18.16 12.46L13.07 12.46L13.07 13.52L10.08 13.52L10.08 10.47L13.07 10.47L13.07 11.53L16.58 11.53L16.58 11.52Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }))), rs = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  n.type = "startNodeStart", n.text = "开始", n.pointerEvents = "auto", n.componentName = "startComponent", n.deletable = !1, n.toolMenus = [{
    key: "1",
    label: "重命名",
    action: (u) => {
      u(!0);
    }
  }], n.isUnique = !0, delete n.flowMeta.jober;
  const d = n.initConnectors;
  return n.initConnectors = () => {
    d.apply(n), n.connectors.remove((u) => u.direction.key === Fe.W.key);
  }, n.getComponent = () => n.graph.plugins[n.componentName](n.flowMeta.inputParams), n.serializerJadeConfig = () => {
    n.flowMeta.inputParams = n.getLatestJadeConfig();
  }, n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(
    se,
    {
      disabled: !0,
      className: "jade-node-custom-header-icon",
      children: /* @__PURE__ */ r.jsx(ns, {})
    }
  ), n;
};
Pr.propTypes = {
  itemId: M.string.isRequired,
  // 确保 itemId 是一个必需的字符串
  propValue: M.string.isRequired,
  // 确保 propValue 是一个必需的字符串
  type: M.string.isRequired,
  // 确保 type 是一个必需的字符串
  disableModifiable: M.bool.isRequired,
  // 确保 disableModifiable 是一个必需的bool值
  onChange: M.func.isRequired
  // 确保 onChange 是一个必需的函数
};
function Pr({ itemId: e, propValue: a, type: t, disableModifiable: o, onChange: i }) {
  return /* @__PURE__ */ r.jsx(
    F.Item,
    {
      className: "jade-form-item",
      label: "字段名称",
      name: `name-${e}`,
      rules: [
        { required: !0, message: "参数名称不能为空" },
        { pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/, message: "只能包含字母、数字或下划线，且必须以字母或下划线开头" }
      ],
      validateTrigger: "onBlur",
      initialValue: a,
      children: /* @__PURE__ */ r.jsx(
        Ht,
        {
          className: "jade-input",
          id: e,
          value: a,
          type: t,
          disabled: o,
          placeholder: "请输入字段名称",
          showCount: !0,
          maxLength: 20,
          onChange: (s) => i && i("name", s.target.value)
        }
      )
    }
  );
}
Ir.propTypes = {
  itemId: M.string.isRequired,
  // 确保 itemId 是一个必需的字符串
  propValue: M.string.isRequired,
  // 确保 propValue 是一个必需的字符串
  disableModifiable: M.bool.isRequired,
  // 确保 disableModifiable 是一个必需的bool值
  onChange: M.func.isRequired
  // 确保 onChange 是一个必需的函数
};
function Ir({ itemId: e, propValue: a, disableModifiable: t, onChange: o }) {
  const i = (l) => {
    l.stopPropagation();
  }, s = (l) => {
    o("type", l), document.activeElement.blur();
  };
  return /* @__PURE__ */ r.jsx(
    F.Item,
    {
      className: "jade-form-item",
      label: "字段类型",
      name: `type-${e}`,
      initialValue: a,
      children: /* @__PURE__ */ r.jsx(
        me,
        {
          className: "jade-select",
          value: a,
          disabled: t,
          style: { width: "100%" },
          onClick: i,
          onChange: s,
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
const { TextArea: as } = xe;
Or.propTypes = {
  propValue: M.string.isRequired,
  // 确保 propValue 是一个必需的字符串
  disableModifiable: M.bool.isRequired,
  // 确保 disableModifiable 是一个必需的bool值
  onChange: M.func.isRequired
  // 确保 onChange 是一个必需的函数
};
function Or({ itemId: e, propValue: a, disableModifiable: t, onChange: o }) {
  return /* @__PURE__ */ r.jsx(
    F.Item,
    {
      className: "jade-form-item",
      label: "字段描述",
      name: `description-${e}`,
      rules: [{ required: !0, message: "参数描述不能为空" }],
      initialValue: a,
      children: /* @__PURE__ */ r.jsx(
        as,
        {
          className: "jade-input",
          value: a,
          disabled: t,
          onChange: (i) => o("description", i.target.value),
          placeholder: "请输入字段描述",
          autoSize: { minRows: 4, maxRows: 4 }
        }
      )
    }
  );
}
Tr.propTypes = {
  item: M.shape({
    id: M.string.isRequired,
    name: M.string.isRequired,
    type: M.string.isRequired,
    description: M.string.isRequired,
    from: M.string.isRequired,
    value: M.string.isRequired
  }).isRequired
};
function Tr({ item: e }) {
  const a = re(), t = e.id, o = (i, s) => {
    a({ actionType: "changeInputParam", id: t, type: i, value: s });
  };
  return /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
    /* @__PURE__ */ r.jsx(
      Pr,
      {
        itemId: e.id,
        propValue: e.name,
        type: e.type,
        disableModifiable: e.disableModifiable,
        onChange: o
      }
    ),
    /* @__PURE__ */ r.jsx(Ir, { itemId: e.id, propValue: e.type, disableModifiable: e.disableModifiable, onChange: o }),
    /* @__PURE__ */ r.jsx(Or, { itemId: e.id, propValue: e.description, disableModifiable: e.disableModifiable, onChange: o })
  ] });
}
Fr.propTypes = {
  propValue: M.string.isRequired,
  // 确保 propValue 是一个必需的number类型
  onValueChange: M.func.isRequired
  // 确保 onNameChange 是一个必需的函数类型
};
function Fr({ propValue: e, onValueChange: a }) {
  const t = parseInt(e), o = {
    1: "1",
    3: "默认",
    10: "10"
  };
  return /* @__PURE__ */ r.jsx("div", { style: { display: "flex", alignItems: "center" }, children: /* @__PURE__ */ r.jsx(
    Jt,
    {
      style: { width: "95%" },
      min: 1,
      max: 10,
      defaultValue: 3,
      marks: o,
      step: 1,
      onChange: (i) => a("Integer", i.toString()),
      value: isNaN(t) ? 3 : t
    }
  ) });
}
Lr.propTypes = {
  propValue: M.string.isRequired,
  // 确保 propValue 是一个必需的number类型
  onValueChange: M.func.isRequired
  // 确保 onNameChange 是一个必需的函数类型
};
function Lr({ propValue: e, onValueChange: a }) {
  const t = parseFloat(e);
  return /* @__PURE__ */ r.jsxs("div", { style: { display: "flex", alignItems: "center" }, children: [
    /* @__PURE__ */ r.jsx(
      Jt,
      {
        style: { width: "90%" },
        min: 1,
        max: 100,
        defaultValue: 20,
        step: 1,
        onChange: (o) => a("Integer", o.toString()),
        value: isNaN(t) ? 20 : t
      }
    ),
    /* @__PURE__ */ r.jsx("span", { style: { marginLeft: "8px" }, children: t })
  ] });
}
_r.propTypes = {
  propValue: M.string.isRequired,
  // 确保 propValue 是一个必需的number类型
  onValueChange: M.func.isRequired
  // 确保 onNameChange 是一个必需的函数类型
};
function _r({ propValue: e, onValueChange: a }) {
  const t = parseInt(e), o = (i) => {
    const s = Math.floor(i);
    a("Integer", s.toString());
  };
  return /* @__PURE__ */ r.jsx("div", { style: { display: "flex", alignItems: "center" }, children: /* @__PURE__ */ r.jsx(
    Yt,
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
Mr.propTypes = {
  propValue: M.string.isRequired,
  // 确保 propValue 是一个必需的string类型
  onValueChange: M.func.isRequired
  // 确保 onNameChange 是一个必需的函数类型
};
function Mr({ propValue: e, onValueChange: a }) {
  const t = e, o = (l) => {
    l.stopPropagation();
  }, i = (l, n) => ((n == null ? void 0 : n.label) ?? "").toLowerCase().includes(l.toLowerCase()), s = (l) => {
    a("String", l), document.activeElement.blur();
  };
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
    me,
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
Ar.propTypes = {
  propValue: M.oneOfType([M.string, M.oneOf([null])]),
  // 确保 propValue 是一个必需的string类型或者null
  onValueChange: M.func.isRequired,
  // 确保 onValueChange 是一个必需的函数类型
  config: M.object.isRequired
  // 确保 config 是一个必需的对象类型
};
function Ar({ propValue: e, onValueChange: a, config: t }) {
  const o = e, [i, s] = de([]), l = (u) => {
    u.stopPropagation();
  }, n = (u, m) => ((m == null ? void 0 : m.label) ?? "").toLowerCase().includes(u.toLowerCase()), d = (u) => {
    a("String", u), document.activeElement.blur();
  };
  return ge(() => {
    $e.get(t.urls.customHistoryUrl, {}, (u) => s(u.data.map((m) => ({
      value: m.fitableId,
      label: m.name
    }))));
  }, []), /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
    me,
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
Br.propTypes = {
  config: M.object.isRequired
  // 确保 config 是一个必需的对象类型
};
function Br({ config: e }) {
  const a = re(), t = ne(), o = t.find((u) => u.name === "memory").value.find((u) => u.name === "type").value, i = t.find((u) => u.name === "memory").value.find((u) => u.name === "value").value, s = (u) => {
    u.stopPropagation();
  }, l = (u, m) => {
    a({ actionType: "changeMemory", memoryType: o, memoryValueType: u, memoryValue: m });
  }, n = () => {
    switch (o) {
      case "ByConversationTurn":
        return /* @__PURE__ */ r.jsx(Fr, { propValue: i, onValueChange: l });
      case "ByNumber":
        return /* @__PURE__ */ r.jsx(Lr, { propValue: i, onValueChange: l });
      case "ByTokenSize":
        return /* @__PURE__ */ r.jsx(_r, { propValue: i, onValueChange: l });
      case "ByTime":
        return /* @__PURE__ */ r.jsx(Mr, { propValue: i, onValueChange: l });
      case "Customizing":
        return /* @__PURE__ */ r.jsx(Ar, { propValue: i, onValueChange: l, config: e });
      case "UserSelect":
        return null;
      case "NotUseMemory":
        return null;
      default:
        return null;
    }
  }, d = (u) => {
    let m = "", c = null;
    switch (u) {
      case "ByConversationTurn":
        m = "Integer", c = "3";
        break;
      case "ByNumber":
        m = "Integer", c = "20";
        break;
      case "ByTokenSize":
        m = "Integer", c = "1000";
        break;
      case "ByTime":
        m = "String", c = "oneHour";
        break;
      case "Customizing":
        m = "String";
        break;
    }
    a({
      actionType: "changeMemory",
      memoryType: u,
      memoryValueType: m,
      memoryValue: c
    }), document.activeElement.blur();
  };
  return /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
    /* @__PURE__ */ r.jsx(
      me,
      {
        className: "jade-select",
        defaultValue: o,
        style: { width: "100%", marginBottom: "8px", marginTop: "8px" },
        onClick: s,
        onChange: (u) => d(u),
        options: [
          { value: "ByConversationTurn", label: "按对话轮次" },
          // 430演示大模型选项不需要按条数、按Token大小、按时间，暂时屏蔽
          // {value: 'ByNumber', label: '按条数'},
          // {value: 'ByTokenSize', label: '按Token大小'},
          // {value: 'ByTime', label: '按时间'},
          // {value: 'Customizing', label: '自定义'},
          { value: "UserSelect", label: "用户自勾选" },
          { value: "NotUseMemory", label: "不使用历史记录" }
        ]
      }
    ),
    n(),
    " "
  ] });
}
const { Panel: qn } = G;
function os() {
  const e = re(), a = ne(), t = he().graph.configs.find((f) => f.node === "startNodeStart"), i = a.find((f) => f.name === "input").value, [s, l] = de(() => i.map((f) => f.id)), n = () => {
    const f = "input_" + T();
    l([...s, f]), e({ actionType: "addInputParam", id: f });
  }, d = () => {
    var g, v;
    const f = (v = (g = a.find((h) => h.name === "input")) == null ? void 0 : g.config) == null ? void 0 : v.find((h) => h.hasOwnProperty("allowAdd"));
    if (f && f.allowAdd)
      return /* @__PURE__ */ r.jsx(
        se,
        {
          type: "text",
          className: "icon-button",
          onClick: n,
          style: { height: "32px", marginLeft: "auto", marginRight: "12px" },
          children: /* @__PURE__ */ r.jsx(Ye, {})
        }
      );
  }, u = (f) => {
    if (!f.disableModifiable)
      return /* @__PURE__ */ r.jsx(
        se,
        {
          type: "text",
          className: "icon-button",
          style: { height: "22px", marginLeft: "auto" },
          onClick: () => m(f.id),
          children: /* @__PURE__ */ r.jsx(Jo, {})
        }
      );
  }, m = (f) => {
    const g = s.filter((v) => v !== f);
    l(g), e({ actionType: "deleteInputParam", id: f });
  }, c = /* @__PURE__ */ r.jsxs("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: [
    /* @__PURE__ */ r.jsx("p", { children: "定义启动工作流所需的输入参数，这些内容将由" }),
    /* @__PURE__ */ r.jsx("p", { children: "大模型在机器人对话过程中读取，允许大模型" }),
    /* @__PURE__ */ r.jsx("p", { children: "在适当的时间启动工作流并填写正确的信息。" })
  ] });
  return /* @__PURE__ */ r.jsxs("div", { children: [
    /* @__PURE__ */ r.jsxs("div", { style: {
      display: "flex",
      alignItems: "center",
      marginBottom: "8px",
      paddingLeft: "8px",
      paddingRight: "4px",
      height: "32px"
    }, children: [
      /* @__PURE__ */ r.jsx("div", { className: "jade-panel-header-font", children: "输入" }),
      /* @__PURE__ */ r.jsx(Se, { content: c, children: /* @__PURE__ */ r.jsx(Re, { className: "jade-top-header-popover-content" }) }),
      d()
    ] }),
    /* @__PURE__ */ r.jsx(
      G,
      {
        bordered: !1,
        activeKey: s,
        onChange: (f) => l(f),
        className: "jade-collapse-custom-background-color",
        children: i.map((f) => /* @__PURE__ */ r.jsx(
          qn,
          {
            header: /* @__PURE__ */ r.jsxs("div", { className: "panel-header", children: [
              /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: f.name }),
              " ",
              u(f)
            ] }),
            className: "jade-panel",
            style: { marginBottom: 8, borderRadius: "8px", width: "100%" },
            children: /* @__PURE__ */ r.jsx(Tr, { item: f })
          },
          f.id
        ))
      }
    ),
    /* @__PURE__ */ r.jsx(G, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["historicalRecordsPanel"], children: /* @__PURE__ */ r.jsx(
      qn,
      {
        header: /* @__PURE__ */ r.jsx(
          "div",
          {
            className: "panel-header",
            style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
            children: /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "历史记录" })
          }
        ),
        className: "jade-panel",
        style: { width: "100%" },
        children: /* @__PURE__ */ r.jsx(Br, { config: t })
      },
      "historicalRecordsPanel"
    ) })
  ] });
}
const is = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || [
    {
      id: T(),
      name: "input",
      type: "Object",
      from: "Expand",
      value: [{ id: "input_" + T(), name: "Question", type: "String", from: "Input", description: "这是用户输入的问题", value: "", disableModifiable: !0 }]
    },
    {
      id: T(),
      name: "memory",
      type: "Object",
      from: "Expand",
      value: [{
        id: T(),
        name: "type",
        type: "String",
        from: "Input",
        value: "ByConversationTurn"
      }, {
        id: T(),
        name: "value",
        type: "Integer",
        from: "Input",
        value: "3"
      }]
    }
  ], a.getReactComponents = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(ss, {}) }), a.reducers = (t, o) => {
    function i() {
      return t.map((d) => d.name === "input" ? {
        ...d,
        value: [...d.value, { id: o.id, name: "", type: "String", from: "Input", description: "", value: "", disableModifiable: !1 }]
      } : d);
    }
    function s() {
      return t.map((d) => d.name === "input" ? {
        ...d,
        value: d.value.map((u) => u.id === o.id ? {
          ...u,
          [o.type]: o.value
        } : u)
      } : d);
    }
    function l() {
      return t.map((d) => d.name === "memory" ? {
        ...d,
        value: d.value.map((u) => u.name === "type" ? { ...u, value: o.memoryType } : u.name === "value" ? { ...u, type: o.memoryValueType, value: o.memoryValue } : u)
      } : d);
    }
    function n() {
      return t.map((d) => d.name === "input" ? {
        ...d,
        value: d.value.filter((u) => u.id !== o.id)
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
  }, a;
}, ss = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(os, {}) }), ls = (e) => /* @__PURE__ */ w.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ w.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ w.createElement("defs", null, /* @__PURE__ */ w.createElement("clipPath", { id: "clip4_13238" }, /* @__PURE__ */ w.createElement("rect", { id: "\\u56FE\\u6807/16/\\u5927\\u6A21\\u578B", width: 16, height: 16, transform: "translate(4.000000 4.000000)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ w.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#047BFC", fillOpacity: 1 }), /* @__PURE__ */ w.createElement("g", { clipPath: "url(#clip4_13238)" }, /* @__PURE__ */ w.createElement("path", { id: "path", d: "M12 4.66L18.33 8.33L18.33 15.66L11.99 19.33L5.66 15.66L5.66 8.33L12 4.66ZM8.33 10.64L11.33 12.38L11.33 15.75L12.66 15.75L12.66 12.38L15.66 10.64L15 9.49L12 11.23L9 9.49L8.33 10.64Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }))), cs = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  return n.type = "llmNodeState", n.text = "大模型", n.pointerEvents = "auto", n.componentName = "llmComponent", n.flowMeta.jober.fitables.push("com.huawei.fit.jober.aipp.fitable.LLMComponent"), n.flowMeta.jober.isAsync = "true", n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(
    se,
    {
      disabled: !0,
      className: "jade-node-custom-header-icon",
      children: /* @__PURE__ */ r.jsx(ls, {})
    }
  ), n;
}, { TextArea: us } = xe, { Panel: ds } = G;
$r.propTypes = {
  modelOptions: M.array.isRequired
  // 确保 modelOptions 是一个必需的array类型
};
function $r({ shapeId: e, modelOptions: a }) {
  const t = ne(), o = re();
  Ke();
  const i = t.inputParams.find((g) => g.name === "model"), s = t.inputParams.find((g) => g.name === "temperature"), l = t.inputParams.filter((g) => g.name === "prompt").flatMap((g) => g.value).find((g) => g.name === "template"), n = (g) => {
    g.stopPropagation();
  }, d = /* @__PURE__ */ r.jsxs("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: [
    /* @__PURE__ */ r.jsx("p", { children: "用于控制生成文本的大型模型的随机性。" }),
    /* @__PURE__ */ r.jsx("p", { children: "当设置较高时，模型将生成更多样化的文本，增加不确定性；" }),
    /* @__PURE__ */ r.jsx("p", { children: "当设置较低时，模型将生成高概率词，减少不确定性。" })
  ] }), u = /* @__PURE__ */ r.jsxs("div", { className: "jade-font-size", style: { lineHeight: "1.2" }, children: [
    /* @__PURE__ */ r.jsx("p", { children: "编辑大模型的提示词，实现相应的功能。" }),
    /* @__PURE__ */ r.jsxs("p", { children: [
      "可以使用",
      "{{变量名}}",
      "从输入参数中引入变量。"
    ] })
  ] }), m = (g, { input: v, userTyping: h }) => h ? v : g === 0 ? "0" : g === 1 ? "1" : g, c = (g) => {
    g.target.value !== "" && o({
      actionType: "changeConfig",
      id: s.id,
      value: g.target.value
    });
  }, f = (g) => {
    g.target.value !== "" && o({
      actionType: "changePrompt",
      id: l.id,
      value: g.target.value
    });
  };
  return /* @__PURE__ */ r.jsx(G, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["modelPanel"], children: /* @__PURE__ */ r.jsxs(
    ds,
    {
      header: /* @__PURE__ */ r.jsx("div", { className: "panel-header", children: /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "大模型" }) }),
      className: "jade-panel",
      children: [
        /* @__PURE__ */ r.jsxs(fe, { gutter: 16, children: [
          /* @__PURE__ */ r.jsx(U, { span: 12, children: /* @__PURE__ */ r.jsx(
            F.Item,
            {
              className: "jade-form-item",
              name: `model-${e}`,
              label: "模型",
              rules: [{ required: !0, message: "请选择使用的模型" }],
              initialValue: i.value,
              children: /* @__PURE__ */ r.jsx(
                me,
                {
                  className: "jade-select",
                  onClick: n,
                  onChange: (g) => o({ actionType: "changeConfig", id: i.id, value: g }),
                  options: a,
                  validateTrigger: "onBlur"
                }
              )
            }
          ) }),
          /* @__PURE__ */ r.jsx(U, { span: 12, children: /* @__PURE__ */ r.jsx(
            F.Item,
            {
              className: "jade-form-item",
              name: `temperature-${e}`,
              label: /* @__PURE__ */ r.jsxs("div", { style: { display: "flex", alignItems: "center" }, children: [
                /* @__PURE__ */ r.jsx("span", { className: "jade-second-title", children: "温度" }),
                /* @__PURE__ */ r.jsx(Se, { content: d, children: /* @__PURE__ */ r.jsx(Re, { className: "jade-panel-header-popover-content" }) })
              ] }),
              rules: [{ required: !0, message: "请输入0-1之间的参数!" }],
              initialValue: s.value,
              validateTrigger: "onBlur",
              children: /* @__PURE__ */ r.jsx(
                Yt,
                {
                  formatter: m,
                  className: "jade-input",
                  style: { width: "100%" },
                  min: 0,
                  max: 1,
                  step: 0.1,
                  onBlur: c,
                  stringMode: !0
                }
              )
            }
          ) })
        ] }),
        /* @__PURE__ */ r.jsx(fe, { gutter: 16, children: /* @__PURE__ */ r.jsx(U, { span: 24, children: /* @__PURE__ */ r.jsx(
          F.Item,
          {
            className: "jade-form-item",
            name: `propmt-${e}`,
            label: /* @__PURE__ */ r.jsxs("div", { style: { display: "flex", alignItems: "center" }, children: [
              /* @__PURE__ */ r.jsx("span", { className: "jade-second-title", children: "提示词模板" }),
              /* @__PURE__ */ r.jsx(Se, { content: [u], children: /* @__PURE__ */ r.jsx(Re, { className: "jade-panel-header-popover-content" }) })
            ] }),
            rules: [{ required: !0, message: "参数不能为空" }],
            initialValue: l.value,
            validateTrigger: "onBlur",
            children: /* @__PURE__ */ r.jsx(
              us,
              {
                className: "jade-input jade-font-size",
                onBlur: (g) => f(g),
                placeholder: "你可以用{{variable name}}来关联输入中的变量名",
                autoSize: { minRows: 4, maxRows: 4 }
              }
            )
          }
        ) }) })
      ]
    },
    "modelPanel"
  ) });
}
const { Panel: fs } = G;
zr.propTypes = {
  items: M.array.isRequired,
  // 确保 items 是一个必需的数组类型
  addItem: M.func.isRequired,
  // 确保 addItem 是一个必需的函数类型
  updateItem: M.func.isRequired,
  // 确保 updateItem 是一个必需的函数类型
  deleteItem: M.func.isRequired
  // 确保 deleteItem 是一个必需的函数类型
};
function zr({ items: e, addItem: a, updateItem: t, deleteItem: o }) {
  const i = Ke(), s = () => {
    a(T());
  }, l = (g, v, h) => {
    const y = [{ key: g, value: v }];
    g === "from" && (y.push({ key: "value", value: "" }), y.push({ key: "referenceNode", value: "" }), y.push({ key: "referenceId", value: "" }), y.push({ key: "referenceKey", value: "" }), document.activeElement.blur(), i.setFieldsValue({ [`value-${h}`]: void 0 }), i.setFieldsValue({ [`reference-${h}`]: void 0 })), t(h, y);
  }, n = (g, v) => {
    t(g.id, [{ key: "referenceKey", value: v }]);
  }, d = (g, v) => {
    t(g.id, [
      { key: "referenceNode", value: v.referenceNode },
      { key: "referenceId", value: v.referenceId },
      { key: "value", value: v.value }
    ]);
  }, u = (g) => {
    o(g);
  }, m = /* @__PURE__ */ r.jsx("div", { children: /* @__PURE__ */ r.jsx("p", { children: "输入需要添加到提示词模板中的信息，可被提示词模板引用" }) }), c = (g) => {
    g.stopPropagation();
  }, f = (g) => {
    switch (g.from) {
      case "Reference":
        return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
          De,
          {
            rules: [{ required: !0, message: "字段值不能为空" }],
            className: "value-custom jade-select",
            reference: g,
            onReferencedValueChange: (v) => n(g, v),
            onReferencedKeyChange: (v) => d(g, v)
          }
        ) });
      case "Input":
        return /* @__PURE__ */ r.jsx(
          F.Item,
          {
            id: `value-${g.id}`,
            name: `value-${g.id}`,
            rules: [{ required: !0, message: "字段值不能为空" }, { pattern: /^[^\s]*$/, message: "禁止输入空格" }],
            initialValue: g.value,
            validateTrigger: "onBlur",
            children: /* @__PURE__ */ r.jsx(
              xe,
              {
                className: "value-custom jade-input",
                value: g.value,
                onChange: (v) => l("value", v.target.value, g.id)
              }
            )
          }
        );
      default:
        return /* @__PURE__ */ r.jsx(r.Fragment, {});
    }
  };
  return /* @__PURE__ */ r.jsx(G, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["inputPanel"], children: /* @__PURE__ */ r.jsxs(
    fs,
    {
      header: /* @__PURE__ */ r.jsxs("div", { className: "panel-header", children: [
        /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "输入" }),
        /* @__PURE__ */ r.jsx(Se, { content: m, children: /* @__PURE__ */ r.jsx(Re, { className: "jade-panel-header-popover-content" }) }),
        /* @__PURE__ */ r.jsx(
          se,
          {
            type: "text",
            className: "icon-button jade-panel-header-icon-position",
            onClick: (g) => {
              s(), c(g);
            },
            children: /* @__PURE__ */ r.jsx(Ye, {})
          }
        )
      ] }),
      className: "jade-panel",
      children: [
        /* @__PURE__ */ r.jsxs(fe, { gutter: 16, children: [
          /* @__PURE__ */ r.jsx(U, { span: 8, children: /* @__PURE__ */ r.jsx(F.Item, { children: /* @__PURE__ */ r.jsx("span", { className: "jade-font-size jade-font-color", children: "字段名称" }) }) }),
          /* @__PURE__ */ r.jsx(U, { span: 16, children: /* @__PURE__ */ r.jsx(F.Item, { children: /* @__PURE__ */ r.jsx("span", { className: "jade-font-size jade-font-color", children: "字段值" }) }) })
        ] }),
        e.map((g) => /* @__PURE__ */ r.jsxs(
          fe,
          {
            gutter: 16,
            children: [
              /* @__PURE__ */ r.jsx(U, { span: 8, children: /* @__PURE__ */ r.jsx(
                F.Item,
                {
                  id: `name-${g.id}`,
                  name: `name-${g.id}`,
                  rules: [{ required: !0, message: "字段值不能为空" }, { pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/, message: "只能包含字母、数字或下划线，且必须以字母或下划线开头" }],
                  initialValue: g.name,
                  children: /* @__PURE__ */ r.jsx(
                    xe,
                    {
                      className: "jade-input",
                      placeholder: "请输入字段名称",
                      style: { paddingRight: "12px" },
                      value: g.name,
                      onChange: (v) => l("name", v.target.value, g.id)
                    }
                  )
                }
              ) }),
              /* @__PURE__ */ r.jsx(U, { span: 6, style: { paddingRight: 0 }, children: /* @__PURE__ */ r.jsx(
                F.Item,
                {
                  id: `from-${g.id}`,
                  initialValue: "Reference",
                  children: /* @__PURE__ */ r.jsx(
                    me,
                    {
                      id: `from-select-${g.id}`,
                      className: "value-source-custom jade-select",
                      style: { width: "100%" },
                      onChange: (v) => l("from", v, g.id),
                      options: [
                        { value: "Reference", label: "引用" },
                        { value: "Input", label: "输入" }
                      ],
                      value: g.from
                    }
                  )
                }
              ) }),
              /* @__PURE__ */ r.jsxs(U, { span: 8, style: { paddingLeft: 0 }, children: [
                f(g),
                " "
              ] }),
              /* @__PURE__ */ r.jsx(U, { span: 2, style: { paddingLeft: 0 }, children: /* @__PURE__ */ r.jsx(F.Item, { children: /* @__PURE__ */ r.jsx(
                se,
                {
                  type: "text",
                  className: "icon-button",
                  style: { height: "100%" },
                  onClick: () => u(g.id),
                  children: /* @__PURE__ */ r.jsx(yt, {})
                }
              ) }) })
            ]
          },
          g.id
        ))
      ]
    },
    "inputPanel"
  ) });
}
const { Panel: ps } = G;
function ms() {
  const e = he(), a = ne(), o = a.outputParams, i = /* @__PURE__ */ r.jsx("div", { className: "jade-font-size", children: /* @__PURE__ */ r.jsx("p", { children: "大模型运行完成后生成的内容。" }) });
  return /* @__PURE__ */ r.jsx(G, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["outputPanel"], children: /* @__PURE__ */ r.jsx(
    ps,
    {
      header: /* @__PURE__ */ r.jsxs(
        "div",
        {
          className: "panel-header",
          style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
          children: [
            /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "输出" }),
            /* @__PURE__ */ r.jsx(Se, { content: i, children: /* @__PURE__ */ r.jsx(Re, { className: "jade-panel-header-popover-content" }) })
          ]
        }
      ),
      className: "jade-panel",
      children: /* @__PURE__ */ r.jsx(
        F,
        {
          name: `outputForm-${e.id}`,
          layout: "vertical",
          className: "jade-form",
          children: /* @__PURE__ */ r.jsx(st, { data: o })
        }
      )
    },
    "outputPanel"
  ) });
}
const { Panel: hs } = G;
Dr.propTypes = {
  toolOptions: M.array.isRequired,
  // 确保 toolOptions 是一个必需的array类型
  workflowOptions: M.array.isRequired,
  // 确保 workflowOptions 是一个必需的array类型
  config: M.object.isRequired
  // 确保 config 是一个必需的object类型
};
function Dr({ toolOptions: e, workflowOptions: a, config: t }) {
  const o = he(), i = ne(), s = re(), l = i.inputParams.find((c) => c.name === "tools"), n = i.inputParams.find((c) => c.name === "workflows"), d = (c) => {
    !t || !t.params || !t.params.tenantId || !t.params.appId ? console.error("Cannot get config.params.tenantId or config.params.appId.") : (window.open("/appbuilder/#/aipp/" + t.params.tenantId + "/addFlow/" + t.params.appId, "_blank"), c.stopPropagation());
  }, u = (c, f) => ((f == null ? void 0 : f.label) ?? "").toLowerCase().includes(c.toLowerCase()), m = (c, f) => {
    s({ actionType: "changeSkillConfig", id: c, value: f });
  };
  return /* @__PURE__ */ r.jsx(G, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["skillPanel"], children: /* @__PURE__ */ r.jsx(
    hs,
    {
      header: /* @__PURE__ */ r.jsx(
        "div",
        {
          className: "panel-header",
          style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
          children: /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "技能" })
        }
      ),
      className: "jade-panel",
      children: /* @__PURE__ */ r.jsxs(
        F,
        {
          name: `skillForm-${o.id}`,
          layout: "vertical",
          className: "jade-form",
          children: [
            /* @__PURE__ */ r.jsx(fe, { gutter: 16, style: { marginBottom: "6px", marginRight: 0, marginLeft: "-3%" }, children: /* @__PURE__ */ r.jsx(U, { span: 21, children: /* @__PURE__ */ r.jsx("span", { className: "jade-font-size jade-font-color", style: { marginLeft: "6px" }, children: "工具" }) }) }),
            /* @__PURE__ */ r.jsx(F.Item, { children: /* @__PURE__ */ r.jsx(
              me,
              {
                mode: "multiple",
                showSearch: !0,
                allowClear: !0,
                className: "jade-select",
                placeholder: "选择合适的工具",
                filterOption: u,
                optionFilterProp: "label",
                value: l.value,
                onMouseDown: (c) => c.stopPropagation(),
                onChange: (c) => m(l.id, c),
                options: e
              }
            ) }),
            /* @__PURE__ */ r.jsxs(fe, { gutter: 16, style: { marginBottom: "6px", marginRight: 0, marginLeft: "-3%" }, children: [
              /* @__PURE__ */ r.jsx(U, { span: 22, children: /* @__PURE__ */ r.jsx("span", { className: "jade-font-size jade-font-color", style: { marginLeft: "6px" }, children: "工具流" }) }),
              /* @__PURE__ */ r.jsx(U, { span: 2, style: { paddingLeft: "3%" }, children: /* @__PURE__ */ r.jsx(
                se,
                {
                  type: "text",
                  className: "icon-button",
                  style: { height: "22px" },
                  onClick: (c) => {
                    d(c);
                  },
                  children: /* @__PURE__ */ r.jsx(Ye, {})
                }
              ) })
            ] }),
            /* @__PURE__ */ r.jsx(F.Item, { children: /* @__PURE__ */ r.jsx(
              me,
              {
                mode: "multiple",
                showSearch: !0,
                allowClear: !0,
                className: "jade-select",
                placeholder: "选择合适的工具流",
                filterOption: u,
                optionFilterProp: "label",
                value: n.value,
                onChange: (c) => m(n.id, c),
                options: a
              }
            ) })
          ]
        }
      )
    },
    "skillPanel"
  ) });
}
function gs() {
  const e = re(), a = ne(), t = he();
  let o;
  !t || !t.graph || !t.graph.configs ? console.error("Cannot get shape.graph.configs.") : o = t.graph.configs.find((v) => v.node === "llmNodeState");
  const [i, s] = de([]), [l, n] = de([]), [d, u] = de([]), m = () => a.inputParams.filter((v) => v.name === "prompt").flatMap((v) => v.value).filter((v) => v.name === "variables").flatMap((v) => v.value), c = (v) => {
    e({ actionType: "addInputParam", id: v });
  }, f = (v, h) => {
    e({ actionType: "changeInputParams", id: v, updateParams: h });
  }, g = (v) => {
    e({ actionType: "deleteInputParam", id: v });
  };
  return ge(() => {
    !o || !o.urls ? console.error("Cannot get config.urls.") : (o.urls.llmModelEndpoint ? $e.get(o.urls.llmModelEndpoint + "/model-gateway/v1/models", {}, (v) => s(v.data.map((h) => ({
      value: h.id,
      label: h.id
    })))) : console.error("Cannot get config.urls.llmModelEndpoint."), o.urls.toolListEndpoint ? $e.get(o.urls.toolListEndpoint + "/api/jober/store/platform/jade/categories/TOOL?pageNum=0&pageSize=10&includeTags=FIT", {}, (v) => n(v.data.map((h) => ({
      value: h.uniqueName,
      label: h.name
    })))) : console.error("Cannot get config.urls.toolListEndpoint."), o.urls.workflowListEndpoint ? $e.get(o.urls.workflowListEndpoint + "/api/jober/store/platform/jade/categories/TOOL?pageNum=0&pageSize=10&includeTags=WATERFLOW", {}, (v) => u(v.data.map((h) => ({
      value: h.uniqueName,
      label: h.name
    })))) : console.error("Cannot get config.urls.workflowListEndpoint."));
  }, []), /* @__PURE__ */ r.jsxs("div", { children: [
    /* @__PURE__ */ r.jsx(zr, { items: m(), addItem: c, updateItem: f, deleteItem: g }),
    /* @__PURE__ */ r.jsx($r, { shapeId: t.id, modelOptions: i }),
    /* @__PURE__ */ r.jsx(Dr, { toolOptions: l, workflowOptions: d, config: o }),
    /* @__PURE__ */ r.jsx(ms, {})
  ] });
}
const vs = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || {
    inputParams: [
      {
        id: T(),
        name: "model",
        type: "String",
        from: "Input",
        value: ""
      },
      {
        id: T(),
        name: "temperature",
        type: "Number",
        from: "Input",
        value: "0.3"
      },
      {
        id: T(),
        name: "prompt",
        type: "Object",
        from: "Expand",
        value: [
          { id: T(), name: "template", type: "String", from: "Input", value: "" },
          {
            id: T(),
            name: "variables",
            type: "Object",
            from: "Expand",
            value: [
              { id: T(), name: void 0, type: "String", from: "Reference", value: "", referenceNode: "", referenceId: "", referenceKey: "" }
            ]
          }
        ]
      },
      { id: T(), name: "tools", type: "Array", from: "Input", value: [] },
      { id: T(), name: "workflows", type: "Array", from: "Input", value: [] },
      { id: T(), name: "systemPrompt", type: "String", from: "Input", value: "" }
    ],
    outputParams: [
      {
        id: T(),
        name: "output",
        type: "Object",
        from: "Expand",
        value: [
          { id: T(), name: "llmOutput", type: "string", from: "Input", description: "", value: "" }
        ]
      }
    ]
  }, a.getReactComponents = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(ys, {}) }), a.reducers = (t, o) => {
    function i() {
      const f = {};
      return Object.entries(t).forEach(([g, v]) => {
        g === "inputParams" ? f[g] = v.map((h) => h.name === "prompt" ? {
          ...h,
          value: h.value.map((y) => y.name === "variables" ? {
            ...y,
            value: [...y.value, {
              id: o.id,
              name: void 0,
              type: "String",
              from: "Reference",
              value: ""
            }]
          } : y)
        } : h) : f[g] = v;
      }), f;
    }
    function s() {
      const f = {};
      return Object.entries(t).forEach(([g, v]) => {
        g === "outputParams" ? f[g] = v.map((h) => h.name === "output" ? {
          ...h,
          value: [...h.value, {
            id: o.id,
            name: "",
            type: "string",
            from: "Input",
            description: "",
            value: ""
          }]
        } : h) : f[g] = v;
      }), f;
    }
    function l() {
      const f = {};
      return Object.entries(t).forEach(([g, v]) => {
        g === "inputParams" ? f[g] = v.map((h) => h.name === "prompt" ? {
          ...h,
          value: h.value.map((y) => y.name === "variables" ? {
            ...y,
            value: y.value.map((b) => {
              if (b.id === o.id) {
                let x = { ...b };
                return o.updateParams.map((N) => {
                  x[N.key] = N.value;
                }), x;
              } else
                return b;
            })
          } : y)
        } : h) : f[g] = v;
      }), f;
    }
    function n() {
      const f = {};
      return Object.entries(t).forEach(([g, v]) => {
        g === "inputParams" ? f[g] = v.map((h) => h.name === "prompt" ? {
          ...h,
          value: h.value.map((y) => o.id === y.id && y.name === "template" ? {
            ...y,
            value: o.value
          } : y)
        } : h) : f[g] = v;
      }), f;
    }
    function d() {
      const f = {};
      return Object.entries(t).forEach(([g, v]) => {
        g === "outputParams" ? f[g] = v.map((h) => h.name === "output" ? {
          ...h,
          value: h.value.map((y) => y.id === o.id ? { ...y, [o.type]: o.value } : y)
        } : h) : f[g] = v;
      }), f;
    }
    function u() {
      const f = {};
      return Object.entries(t).forEach(([g, v]) => {
        g === "inputParams" ? f[g] = v.map((h) => h.id === o.id ? {
          ...h,
          value: o.value
        } : h) : f[g] = v;
      }), f;
    }
    function m() {
      const f = {};
      return Object.entries(t).forEach(([g, v]) => {
        g === "inputParams" ? f[g] = v.map((h) => h.id === o.id ? {
          ...h,
          value: o.value.map((y) => ({ id: T(), type: "String", from: "Input", value: y }))
        } : h) : f[g] = v;
      }), f;
    }
    function c() {
      const f = {};
      return Object.entries(t).forEach(([g, v]) => {
        g === "inputParams" ? f[g] = v.map((h) => h.name === "prompt" ? {
          ...h,
          value: h.value.map((y) => y.name === "variables" ? {
            ...y,
            value: y.value.filter((b) => b.id !== o.id)
          } : y)
        } : h) : f[g] = v;
      }), f;
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
        return u();
      case "changeSkillConfig":
        return m();
      case "changePrompt":
        return n();
      case "deleteInputParam":
        return c();
      case "deleteOutputParam":
        return t.filter((f) => f.id !== o.id);
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, a;
}, ys = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(gs, {}) }), bs = (e) => /* @__PURE__ */ w.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ w.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ w.createElement("defs", null, /* @__PURE__ */ w.createElement("clipPath", { id: "clip4_13297" }, /* @__PURE__ */ w.createElement("rect", { id: "\\u56FE\\u6807/16/\\u68C0\\u67E5\\u5217\\u8868", width: 16, height: 16, transform: "translate(4.000000 4.000000)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ w.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#A97AF8", fillOpacity: 1 }), /* @__PURE__ */ w.createElement("g", { clipPath: "url(#clip4_13297)" }, /* @__PURE__ */ w.createElement("path", { id: "path", d: "M15 7.68L14.98 7.68C14.78 7.48 14.78 7.17 14.98 6.97C15.17 6.78 15.48 6.78 15.68 6.97L15.68 7L15 7.68ZM18.32 9.64L18.35 9.64C18.55 9.84 18.55 10.15 18.35 10.35C18.15 10.55 17.84 10.55 17.64 10.35L17.64 10.32L18.32 9.64Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M15.33 7.33L18 10", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M15.68 10.32L15.68 10.35C15.48 10.55 15.17 10.55 14.98 10.35C14.78 10.15 14.78 9.84 14.98 9.64L15 9.64L15.68 10.32ZM17.64 7L17.64 6.97C17.84 6.78 18.15 6.78 18.35 6.97C18.55 7.17 18.55 7.48 18.35 7.68L18.32 7.68L17.64 7Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M15.33 10L18 7.33", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M18.29 13.69L18.29 13.67C18.47 13.46 18.78 13.43 18.99 13.62C19.2 13.8 19.22 14.11 19.04 14.32L19.01 14.33L18.29 13.69ZM14.67 15.68L14.64 15.68C14.44 15.48 14.44 15.17 14.64 14.97C14.84 14.78 15.15 14.78 15.35 14.97L15.35 15L14.67 15.68Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M18.66 14L16.33 16.66L15 15.33", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M12.66 7.33L5.33 7.33L5.33 10L12.66 10L12.66 7.33Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M5.33 7.33L5.33 10L12.66 10L12.66 7.33L5.33 7.33Z", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M12.66 14L5.33 14L5.33 16.66L12.66 16.66L12.66 14Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M5.33 14L5.33 16.66L12.66 16.66L12.66 14L5.33 14Z", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round" }))), xs = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  return n.type = "manualCheckNodeState", n.text = "人工检查", n.pointerEvents = "auto", n.componentName = "manualCheckComponent", n.flowMeta.triggerMode = "manual", n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(
    se,
    {
      disabled: !0,
      className: "jade-node-custom-header-icon",
      children: /* @__PURE__ */ r.jsx(bs, {})
    }
  ), n;
};
function js() {
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(vn, { image: vn.PRESENTED_IMAGE_DEFAULT }) });
}
const { Panel: Wn } = G;
function Cs() {
  const e = he(), a = re(), t = ne(), o = he().graph.configs.find((c) => c.node === "manualCheckNodeState"), i = t.inputParams.find((c) => c.name === "formName").value, s = t.outputParams, [l, n] = de([]);
  ge(() => {
    $e.get(o.urls.runtimeFormUrl, {}, (c) => n(c.data.map((f) => {
      var g;
      return {
        label: f.name,
        value: `${((g = f.appearance[0]) == null ? void 0 : g.name) || ""}|${f.id}`
      };
    })));
  }, []);
  const d = () => i && i.length > 0 ? e.graph.plugins[i]().getReactComponents() : /* @__PURE__ */ r.jsx(js, {}), u = (c) => {
    let f = "", g = "", v = "";
    if (c && c.length > 0) {
      const [h, y] = c.split("|");
      g = h + "Component", v = y;
      try {
        f = e.graph.plugins[g]().getJadeConfig();
      } catch (b) {
        console.error("Error getting JadeConfig:", b);
      }
    }
    a({ actionType: "changeFormAndSetOutput", formName: g, formId: v, formOutput: f });
  }, m = () => !s || !Array.isArray(s) || !s.length > 0 ? null : /* @__PURE__ */ r.jsx(G, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["manualCheckOutputPanel"], children: /* @__PURE__ */ r.jsx(
    Wn,
    {
      header: /* @__PURE__ */ r.jsx(
        "div",
        {
          className: "panel-header",
          style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
          children: /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "输出" })
        }
      ),
      className: "jade-panel",
      children: /* @__PURE__ */ r.jsx(st, { data: s })
    },
    "manualCheckOutputPanel"
  ) });
  return /* @__PURE__ */ r.jsxs("div", { children: [
    /* @__PURE__ */ r.jsx(G, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["manualCheckFormPanel"], children: /* @__PURE__ */ r.jsx(
      Wn,
      {
        header: /* @__PURE__ */ r.jsx(
          "div",
          {
            className: "panel-header",
            style: { display: "flex", alignItems: "center", justifyContent: "flex-start" },
            children: /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "表单" })
          }
        ),
        className: "jade-panel",
        style: { marginBottom: 8, borderRadius: "8px", width: "100%" },
        children: /* @__PURE__ */ r.jsx(
          F,
          {
            name: `manualCheckForm-${e.id}`,
            layout: "vertical",
            className: "jade-form",
            children: /* @__PURE__ */ r.jsxs(F.Item, { children: [
              /* @__PURE__ */ r.jsx(
                me,
                {
                  allowClear: !0,
                  className: "jade-select",
                  defaultValue: i,
                  style: { width: "100%", marginBottom: "8px" },
                  onChange: (c) => u(c),
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
    m()
  ] });
}
const ws = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || {
    inputParams: [
      {
        id: T(),
        name: "formName",
        type: "String",
        from: "Input",
        value: ""
      },
      {
        id: T(),
        name: "formId",
        type: "String",
        from: "Input",
        value: ""
      }
    ],
    outputParams: []
  }, a.getReactComponents = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Es, {}) }), a.reducers = (t, o) => {
    function i() {
      return {
        ...t,
        inputParams: t.inputParams.map((s) => s.name === "formName" ? {
          ...s,
          value: o.formName
        } : s.name === "formId" ? {
          ...s,
          value: o.formId
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
  }, a;
}, Es = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Cs, {}) }), Ns = (e) => /* @__PURE__ */ w.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ w.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ w.createElement("defs", null), /* @__PURE__ */ w.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#6CBFFF", fillOpacity: 1 }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M12 11.02C12 11.11 11.92 11.18 11.83 11.18L9.73 11.18L9.59 11.81L9.57 11.88C9.06 14.13 8.81 15.18 8.57 16.03C8.16 17.5 7.21 18.36 5.83 18.48C5.77 18.49 5.72 18.49 5.66 18.49C5.57 18.5 5.49 18.43 5.49 18.34L5.49 17.03C5.49 16.94 5.56 16.87 5.64 16.87C5.74 16.86 5.82 16.85 5.87 16.84C6.45 16.73 6.8 16.35 7 15.6C7.23 14.78 7.48 13.74 7.98 11.52L8 11.45L8.06 11.18L6.47 11.18C6.38 11.18 6.3 11.11 6.3 11.02L6.3 9.72C6.3 9.63 6.38 9.56 6.47 9.56L8.44 9.56C8.62 8.75 8.74 8.26 8.87 7.74C9.35 5.87 10.99 5.19 13.44 5.62C13.49 5.63 13.55 5.64 13.62 5.65C13.71 5.67 13.77 5.75 13.75 5.84L13.75 5.84L13.49 7.12C13.47 7.21 13.39 7.26 13.3 7.24C13.22 7.23 13.15 7.22 13.08 7.2C11.44 6.93 10.67 7.27 10.45 8.14C10.35 8.56 10.25 8.97 10.11 9.56L11.83 9.56C11.92 9.56 12 9.63 12 9.72L12 11.02ZM18.5 11.02C18.5 11.11 18.43 11.18 18.34 11.18L17.86 11.18L16.35 12.85L17.16 14.43L17.52 14.43C17.61 14.43 17.69 14.51 17.69 14.6L17.69 15.9C17.69 15.99 17.61 16.06 17.52 16.06L16.17 16.06L15.18 14.14L13.45 16.06L12.16 16.06C12.07 16.06 11.99 15.99 11.99 15.9L11.99 14.6C11.99 14.51 12.07 14.44 12.16 14.44L12.73 14.44L14.39 12.6L13.66 11.18L12.97 11.18C12.88 11.18 12.81 11.11 12.81 11.02L12.81 9.72C12.81 9.63 12.88 9.56 12.97 9.56L14.66 9.56L15.56 11.31L17.14 9.56L18.34 9.56C18.43 9.56 18.5 9.63 18.5 9.72L18.5 11.02Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" })), Ss = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  n.type = "fitInvokeNodeState", n.width = 360, n.backColor = "white", n.pointerEvents = "auto", n.text = "FIT调用", n.componentName = "fitInvokeComponent", n.flowMeta.triggerMode = "auto", n.flowMeta.jober.type = "GENERICABLE_JOBER";
  const d = {
    genericable: {
      id: "",
      params: []
    }
  }, u = n.serializerJadeConfig;
  return n.serializerJadeConfig = () => {
    u.apply(n);
    const m = n.flowMeta.jober.converter.entity.fitable.value.find((c) => c.name === "id").value;
    m && (n.flowMeta.jober.fitables = [m]), d.genericable.params = n.flowMeta.jober.converter.entity.inputParams.map((c) => ({ name: c.name })), d.genericable.id = n.flowMeta.jober.converter.entity.genericable.value.find((c) => c.name === "id").value, n.flowMeta.jober.entity = d;
  }, n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(se, { disabled: !0, className: "jade-node-custom-header-icon", children: /* @__PURE__ */ r.jsx(Ns, {}) }), n;
};
function Vr() {
  const e = re(), a = ne(), t = a && a.inputParams, o = (i, s) => {
    e({ type: "update", id: i, changes: s });
  };
  return /* @__PURE__ */ r.jsx(kr, { data: t, updateItem: o });
}
function Rs() {
  const e = re(), a = he(), t = a.graph.configs && a.graph.configs.find((l) => l.node === "fitInvokeState").urls.serviceListEndpoint, o = (l) => t + "?pageNum=" + l + "&pageSize=10", i = (l, n) => {
    e({ type: "selectGenericable", value: l });
  }, s = (l) => l.map((n) => ({
    value: n,
    label: n
  }));
  return /* @__PURE__ */ r.jsx(
    F.Item,
    {
      name: `select-genericable-${a.id}`,
      label: /* @__PURE__ */ r.jsx("span", { style: { color: "red" } }),
      rules: [{ required: !0, message: "请选择一个服务" }],
      colon: !1,
      children: /* @__PURE__ */ r.jsx(
        en,
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
function ks() {
  const e = re(), a = ne(), t = he(), o = a && a.genericable.value.find((u) => u.name === "id").value, i = t.graph.configs.find((u) => u.node === "fitInvokeState").urls.fitableMetaInfoUrl, s = o === "", l = (u) => i + o + "?pageNum=" + u + "&pageSize=10", n = (u, m) => {
    e({ type: "selectFitable", value: m.find((c) => c.name === u) });
  }, d = (u) => u.map((m) => ({
    value: m.name,
    label: m.name
  }));
  return /* @__PURE__ */ r.jsx(
    F.Item,
    {
      name: `select-fitable-${t.id}`,
      label: /* @__PURE__ */ r.jsx("span", { style: { color: "red" } }),
      rules: [{ required: !0, message: "请选择一个服务，再选择实现" }],
      colon: !1,
      children: /* @__PURE__ */ r.jsx(
        en,
        {
          className: "jade-select-tool",
          placeholder: "请选择一个服务，再选择实现",
          onChange: n,
          buildUrl: l,
          disabled: s,
          getOptions: d,
          dealResponse: (u) => u.data
        }
      )
    }
  );
}
const { Panel: Ps } = G;
function Is() {
  return /* @__PURE__ */ r.jsx(
    G,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      defaultActiveKey: ["FitInvokeService"],
      children: /* @__PURE__ */ r.jsxs(
        Ps,
        {
          className: "jade-panel",
          header: /* @__PURE__ */ r.jsx("div", { style: { display: "flex", alignItems: "center" }, children: /* @__PURE__ */ r.jsx("span", { className: "title", children: "FIT服务" }) }),
          children: [
            /* @__PURE__ */ r.jsx("div", { style: { marginTop: "8px" }, children: /* @__PURE__ */ r.jsx("span", { className: "select-genericable", children: "选择服务" }) }),
            /* @__PURE__ */ r.jsx(Rs, {}),
            /* @__PURE__ */ r.jsx("div", { style: { marginTop: "8px" }, children: /* @__PURE__ */ r.jsx("span", { className: "select-fitable", children: "选择实现" }) }),
            /* @__PURE__ */ r.jsx(ks, {})
          ]
        },
        "FitInvokeService"
      )
    }
  );
}
const { Panel: Os } = G;
function qr() {
  const e = ne(), a = e && e.outputParams;
  return /* @__PURE__ */ r.jsx(
    G,
    {
      bordered: !1,
      className: "jade-collapse-custom-background-color",
      defaultActiveKey: ["InvokeOutput"],
      children: /* @__PURE__ */ r.jsx(
        Os,
        {
          className: "jade-panel",
          header: /* @__PURE__ */ r.jsx("div", { style: { display: "flex", alignItems: "center" }, children: /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "输出" }) }),
          children: /* @__PURE__ */ r.jsx(st, { data: a })
        },
        "InvokeOutput"
      )
    }
  );
}
const Wr = (e) => {
  const a = (o, i) => {
    const s = [];
    if (i.type === "object")
      for (const l in i.properties) {
        const n = i.properties[l];
        n.type === "object" ? s.push(...a(l, n)) : s.push({
          id: T(),
          name: l,
          type: n.type.capitalize(),
          value: n.type.capitalize()
        });
      }
    return [{
      id: "output_" + T(),
      name: o,
      type: "Object",
      value: s
    }];
  }, t = {
    id: "output_" + T(),
    name: "output",
    type: "",
    value: []
  };
  if (e.type === "object") {
    if (t.type = "Object", e.hasOwnProperty("additionalProperties") && e.additionalProperties.hasOwnProperty("type"))
      return t;
    const i = e.properties;
    for (const s in i) {
      const l = i[s];
      l.type === "object" ? t.value.push(...a(s, l)) : t.value.push({
        id: T(),
        name: s,
        type: l.type.capitalize(),
        value: l.type.capitalize()
      });
    }
  } else
    t.type = e.type.capitalize();
  return t;
}, nn = (e) => {
  const a = e.property.hasOwnProperty("additionalProperties") && e.property.additionalProperties.hasOwnProperty("type"), t = {
    id: e.propertyName + "_" + T(),
    name: e.propertyName,
    type: e.property.type === "object" ? "Object" : e.property.type.capitalize(),
    // 对象默认展开，map直接为引用
    from: e.property.type === "object" ? a ? "Reference" : "Expand" : "Reference",
    referenceNode: "",
    referenceId: "",
    referenceKey: "",
    value: []
  };
  if (a && (t.generic = "Map"), e.property.type === "object" && !a) {
    const o = e.property.properties;
    t.value = Object.keys(o).map((i) => nn({
      propertyName: i,
      property: o[i]
    })), t.props = [...t.value];
  }
  return t;
}, Ur = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || {
    inputParams: [],
    outputParams: []
  }, a.getReactComponents = () => /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
    /* @__PURE__ */ r.jsx(Vr, {}),
    /* @__PURE__ */ r.jsx(qr, {})
  ] }), a.reducers = (t, o) => {
    const i = (l, n, d) => l.map((u) => {
      const m = { ...u };
      return u.id === n ? (d.forEach((c) => {
        m[c.key] = c.value, c.value === "Reference" && (m.value = []);
      }), m) : (m.from === "Expand" && (m.value = i(m.value, n, d)), m);
    });
    let s = { ...t };
    switch (o.type) {
      case "update":
        return s.inputParams = i(t.inputParams, o.id, o.changes), s;
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, a;
}, Ts = (e) => {
  const a = Ur(e);
  a.getJadeConfig = () => e || {
    inputParams: [],
    genericable: {
      id: "genericable_" + T(),
      name: "genericable",
      type: "Object",
      from: "Expand",
      // 保存当前选中的Genericable信息
      value: [{ id: T(), name: "id", type: "String", from: "Input", value: "" }]
    },
    fitable: {
      id: "fitable_" + T(),
      name: "fitable",
      type: "Object",
      from: "Expand",
      // 保存当前选中的fitable信息
      value: [{ id: T(), name: "id", type: "String", from: "Input", value: "" }]
    },
    outputParams: []
  }, a.getReactComponents = () => /* @__PURE__ */ r.jsxs(r.Fragment, { children: [
    /* @__PURE__ */ r.jsx(Vr, {}),
    /* @__PURE__ */ r.jsx(Is, {}),
    /* @__PURE__ */ r.jsx(qr, {})
  ] });
  const t = a.reducers;
  return a.reducers = (o, i) => {
    const s = () => {
      u.genericable.value.find((m) => m.name === "id").value = i.value, u.fitable.value.find((m) => m.name === "id").value = "";
    }, l = () => {
      u.fitable.value.find((m) => m.name === "id").value = i.value.schema.parameters.fitableId;
    }, n = () => {
      const m = i.value, c = Wr(m.schema.return);
      u.outputParams.push(c);
    }, d = () => {
      const m = i.value, c = Object.keys(m.schema.parameters.properties).map((f) => nn({
        propertyName: f,
        property: m.schema.parameters.properties[f]
      }));
      delete u.inputParams, u.inputParams = c;
    };
    let u = { ...o };
    switch (i.type) {
      case "generateInput":
        return d(), u;
      case "selectGenericable":
        return s(), u;
      case "selectFitable":
        return l(), d(), n(), u;
    }
    return t.apply(a, [o, i]);
  }, a;
}, Fs = (e) => /* @__PURE__ */ w.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ w.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ w.createElement("defs", null, /* @__PURE__ */ w.createElement("clipPath", { id: "clip4_13318" }, /* @__PURE__ */ w.createElement("rect", { id: "\\u56FE\\u6807/16/API \\u63A5\\u53E3", width: 16, height: 16, transform: "translate(4.000000 4.000000)", fill: "white", fillOpacity: 0 }))), /* @__PURE__ */ w.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#FA9941", fillOpacity: 1 }), /* @__PURE__ */ w.createElement("g", { clipPath: "url(#clip4_13318)" }, /* @__PURE__ */ w.createElement("path", { id: "path", d: "M16.33 11.33L15.33 12.33L11.66 8.66L12.66 7.66C13.16 7.16 14.99 6.33 16.33 7.66C17.66 9 16.83 10.83 16.33 11.33Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M15.33 12.33L11.66 8.66L12.66 7.66C13.16 7.16 14.99 6.33 16.33 7.66C17.66 9 16.83 10.83 16.33 11.33L15.33 12.33Z", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M16.68 7.99L16.68 8.02C16.48 8.21 16.17 8.21 15.98 8.02C15.78 7.82 15.78 7.51 15.98 7.31L16 7.31L16.68 7.99ZM17.64 5.67L17.64 5.64C17.84 5.44 18.15 5.44 18.35 5.64C18.55 5.84 18.55 6.15 18.35 6.35L18.32 6.35L17.64 5.67Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M16.33 7.66L18 5.99", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M7.66 12.66L8.66 11.66L12.33 15.33L11.33 16.33C10.83 16.83 9 17.66 7.66 16.33C6.33 15 7.16 13.16 7.66 12.66Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "nonzero" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M8.66 11.66L12.33 15.33L11.33 16.33C10.83 16.83 9 17.66 7.66 16.33C6.33 15 7.16 13.16 7.66 12.66L8.66 11.66Z", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M12.01 14.99L12.01 15.02C11.82 15.21 11.51 15.21 11.31 15.02C11.11 14.82 11.11 14.51 11.31 14.31L11.34 14.31L12.01 14.99ZM12.64 13L12.64 12.97C12.84 12.78 13.15 12.78 13.35 12.97C13.55 13.17 13.55 13.48 13.35 13.68L13.32 13.68L12.64 13Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M11.66 14.66L12.99 13.33", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M6.35 18.32L6.35 18.35C6.15 18.55 5.84 18.55 5.64 18.35C5.44 18.15 5.44 17.84 5.64 17.64L5.67 17.64L6.35 18.32ZM7.31 16L7.31 15.97C7.51 15.78 7.82 15.78 8.02 15.97C8.21 16.17 8.21 16.48 8.02 16.68L7.99 16.68L7.31 16Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M6 18L7.66 16.33", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M9.68 12.65L9.68 12.68C9.48 12.88 9.17 12.88 8.98 12.68C8.78 12.48 8.78 12.17 8.98 11.97L9 11.97L9.68 12.65ZM10.31 10.67L10.31 10.64C10.51 10.44 10.82 10.44 11.02 10.64C11.21 10.84 11.21 11.15 11.02 11.35L10.99 11.35L10.31 10.67Z", fill: "#000000", fillOpacity: 0, fillRule: "nonzero" }), /* @__PURE__ */ w.createElement("path", { id: "path", d: "M9.33 12.33L10.66 11", stroke: "#FFFFFF", strokeOpacity: 1, strokeWidth: 1, strokeLinejoin: "round", strokeLinecap: "round" }))), Ls = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  n.type = "toolInvokeNodeState", n.width = 360, n.backColor = "white", n.pointerEvents = "auto", n.text = "工具调用", n.componentName = "toolInvokeComponent", n.flowMeta.triggerMode = "auto", n.flowMeta.jober.type = "STORE_JOBER";
  const d = {
    uniqueName: "",
    params: [],
    return: {
      type: ""
    }
  }, u = {
    inputParams: [],
    outputParams: []
  }, m = n.serializerJadeConfig;
  return n.serializerJadeConfig = () => {
    m.apply(n), n.flowMeta.jober.entity.params = n.flowMeta.jober.converter.entity.inputParams.map((c) => ({ name: c.name }));
  }, n.processMetaData = (c) => {
    const f = () => {
      v.outputParams.push(Wr(c.schema.return));
    }, g = () => {
      delete v.inputParams, v.inputParams = Object.keys(c.schema.parameters.properties).map((h) => nn({
        propertyName: h,
        property: c.schema.parameters.properties[h]
      }));
    }, v = { ...u };
    g(), f(), n.flowMeta.jober.converter.entity = v, n.flowMeta.jober.entity = d, n.flowMeta.jober.entity.uniqueName = c.uniqueName, n.flowMeta.jober.entity.return.type = c.schema.return.type, n.drawer.unmountReact(), n.invalidateAlone();
  }, n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(se, { disabled: !0, className: "jade-node-custom-header-icon", children: /* @__PURE__ */ r.jsx(Fs, {}) }), n;
}, _s = (e) => /* @__PURE__ */ w.createElement("svg", { width: 24, height: 24, viewBox: "0 0 24 24", fill: "none", xmlns: "http://www.w3.org/2000/svg", xmlnsXlink: "http://www.w3.org/1999/xlink", ...e }, /* @__PURE__ */ w.createElement("desc", null, `
			Created with Pixso.
	`), /* @__PURE__ */ w.createElement("defs", null), /* @__PURE__ */ w.createElement("rect", { id: "\\u753B\\u677F 856", rx: 4, width: 24, height: 24, fill: "#F3689A", fillOpacity: 1 }), /* @__PURE__ */ w.createElement("path", { id: "if", d: "M17.34 9.15L14.45 9.15L14.5 8.77C14.56 8.26 14.7 7.89 14.92 7.65C15.15 7.41 15.51 7.29 16 7.29C16.11 7.29 16.43 7.3 16.97 7.33L17.52 7.36L17.79 5.58Q16.79 5.5 16.03 5.5Q14.47 5.5 13.61 6.27Q12.79 7.01 12.61 8.46L12.52 9.15L10.58 9.15L10.36 10.95L12.31 10.95L11.38 18.5L13.31 18.5L14.24 10.95L17.12 10.95L17.34 9.15ZM7.61 5.88L7.36 7.9L9.55 7.9L9.81 5.88L7.61 5.88ZM6.2 18.5L8.13 18.5L9.29 9.15L7.36 9.15L6.2 18.5Z", fill: "#FFFFFF", fillOpacity: 1, fillRule: "evenodd" })), Ms = (e, a, t, o, i, s, l) => {
  const n = ve(e, a, t, o, i, s, l);
  n.type = "conditionNodeCondition", n.text = "条件", n.width = 600, n.pointerEvents = "auto", n.componentName = "conditionComponent", delete n.flowMeta.jober;
  const d = n.initConnectors;
  return n.initConnectors = () => {
    d.apply(n), n.connectors.remove((u) => u.direction.key === Fe.E.key);
  }, n.getComponent = () => n.graph.plugins[n.componentName](n.flowMeta.conditionParams), n.serializerJadeConfig = () => {
    n.flowMeta.conditionParams = n.getLatestJadeConfig();
  }, n.getHeaderIcon = () => /* @__PURE__ */ r.jsx(se, { disabled: !0, className: "jade-node-custom-header-icon", children: /* @__PURE__ */ r.jsx(_s, {}) }), n;
}, { Panel: As } = G;
function Bs({
  branch: e,
  name: a,
  index: t,
  totalItemNum: o,
  deleteBranch: i,
  changeConditionRelation: s,
  addCondition: l,
  deleteCondition: n,
  changeConditionConfig: d
}) {
  const u = Ke(), m = ["is empty", "is not empty", "is true", "is false"], c = () => {
    i(e.id);
  }, f = (S) => {
    n(e.id, S);
  }, g = (S) => {
    s(e.id, S);
  }, v = (S, O) => {
    u.setFieldsValue({ [`condition-${S}`]: O }), d(e.id, S, [{ key: "condition", value: O }]);
  }, h = (S, O, _, B) => {
    d(e.id, S, [{ key: O, value: [{ key: "referenceKey", value: _ }, { key: "type", value: B }] }]);
  }, y = (S, O, _) => {
    d(e.id, S, [{ key: O, value: [
      { key: "referenceNode", value: _.referenceNode },
      { key: "referenceId", value: _.referenceId },
      { key: "value", value: _.value }
    ] }]);
  }, b = (S, O, _) => {
    d(e.id, S, [{ key: O, value: _ }]);
  }, x = (S) => {
    switch (S) {
      case "String":
        return [
          { value: "equal", label: "equal" },
          { value: "not equal", label: "not equal" },
          // {value: 'longer than', label: 'longer than'},
          // {value: 'longer than or equal', label: 'longer than or equal'},
          // {value: 'shorter than', label: 'shorter than'},
          // {value: 'shorter than or equal', label: 'shorter than or equal'},
          // {value: 'contain', label: 'contain'},
          // {value: 'not contain', label: 'not contain'},
          { value: "is empty", label: "is empty" },
          { value: "is not empty", label: "is not empty" }
        ];
      case "Boolean":
        return [
          { value: "equal", label: "equal" },
          { value: "not equal", label: "not equal" },
          { value: "is empty", label: "is empty" },
          { value: "is not empty", label: "is not empty" },
          { value: "is true", label: "is true" },
          { value: "is false", label: "is false" }
        ];
      case "Integer":
      case "Number":
        return [
          { value: "equal", label: "equal" },
          { value: "not equal", label: "not equal" },
          { value: "is empty", label: "is empty" },
          { value: "is not empty", label: "is not empty" },
          { value: "greater than", label: "greater than" },
          { value: "greater than or equal", label: "greater than or equal" },
          { value: "less than", label: "less than" },
          { value: "less than or equal", label: "less than or equal" }
        ];
      case "Array<String>":
      case "Array<Integer>":
      case "Array<Boolean>":
      case "Array<Number>":
        return [
          // {value: 'longer than', label: 'longer than'},
          // {value: 'longer than or equal', label: 'longer than or equal'},
          // {value: 'shorter than', label: 'shorter than'},
          // {value: 'shorter than or equal', label: 'shorter than or equal'},
          // {value: 'contain', label: 'contain'},
          // {value: 'not contain', label: 'not contain'},
          { value: "is empty", label: "is empty" },
          { value: "is not empty", label: "is not empty" }
        ];
      default:
        return [];
    }
  }, N = () => o > 2 ? /* @__PURE__ */ r.jsx("div", { className: "priority-tag", children: /* @__PURE__ */ r.jsxs("div", { className: "priority-inner-text jade-font-size", children: [
    "Priority ",
    t + 1
  ] }) }) : null, I = () => o > 2 ? /* @__PURE__ */ r.jsx(
    se,
    {
      type: "text",
      className: "jade-panel-header-icon-position icon-button",
      onClick: () => c(),
      children: /* @__PURE__ */ r.jsx(yt, {})
    }
  ) : null, C = () => /* @__PURE__ */ r.jsx("div", { className: "condition-left-diagram", children: /* @__PURE__ */ r.jsx(
    me,
    {
      id: `condition-left-select-${e.id}`,
      className: "jade-select operation",
      placement: "bottomLeft",
      popupClassName: "condition-left-drop",
      onChange: (S) => g(S),
      options: [
        { value: "and", label: "And" },
        { value: "or", label: "Or" }
      ],
      value: e.conditionRelation
    }
  ) }), ae = (S, O) => {
    if (S > 0)
      return /* @__PURE__ */ r.jsx(F.Item, { children: /* @__PURE__ */ r.jsx(
        se,
        {
          type: "text",
          className: "icon-button",
          style: { height: "100%" },
          onClick: () => f(O),
          children: /* @__PURE__ */ r.jsx(yt, {})
        }
      ) });
  }, ye = (S, O) => m.includes(S) ? [{}] : O || [{ required: !0, message: "字段值不能为空" }], Me = (S, O, _, B) => {
    switch (O.from) {
      case "Reference":
        return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(
          De,
          {
            className: "value-custom jade-select",
            disabled: m.includes(B),
            rules: ye(B),
            reference: O,
            onReferencedValueChange: (te, le) => h(S, O.id, te, le),
            onReferencedKeyChange: (te) => y(S, O.id, te)
          }
        ) });
      case "Input":
        switch (_) {
          case "String":
          case "Array<String>":
            return /* @__PURE__ */ r.jsx(
              F.Item,
              {
                id: `value-${O.id}`,
                name: `value-${O.id}`,
                rules: ye(B),
                initialValue: O.value,
                children: /* @__PURE__ */ r.jsx(
                  xe,
                  {
                    className: "value-custom jade-input",
                    disabled: m.includes(B),
                    value: O.value,
                    onChange: (te) => b(S, O.id, [{ key: "value", value: te.target.value }])
                  }
                )
              }
            );
          case "Boolean":
          case "Array<Boolean>":
            return /* @__PURE__ */ r.jsx(
              F.Item,
              {
                id: `value-${O.id}`,
                name: `value-${O.id}`,
                initialValue: O.value,
                rules: ye(B),
                style: { marginLeft: "8px" },
                children: /* @__PURE__ */ r.jsx(Ra, { disabled: m.includes(B), onChange: (te) => b(S, O.id, [{ key: "value", value: te }]), value: O.value, defaultChecked: !0 })
              }
            );
          case "Integer":
          case "Number":
          case "Array<Number>":
          case "Array<Integer>":
            return /* @__PURE__ */ r.jsx(
              F.Item,
              {
                id: `value-${O.id}`,
                name: `value-${O.id}`,
                rules: ye(B),
                initialValue: O.value,
                children: /* @__PURE__ */ r.jsx(
                  Yt,
                  {
                    className: "value-custom jade-input",
                    disabled: m.includes(B),
                    step: 1,
                    onChange: (te) => b(S, O.id, [{ key: "value", value: te }]),
                    stringMode: !0
                  }
                )
              }
            );
          default:
            return /* @__PURE__ */ r.jsx(
              F.Item,
              {
                id: `value-${O.id}`,
                name: `value-${O.id}`,
                rules: ye(B, [{ required: !0, message: "字段值不能为空" }, {
                  pattern: /^[^\s]*$/,
                  message: "禁止输入空格"
                }]),
                initialValue: O.value,
                children: /* @__PURE__ */ r.jsx(
                  xe,
                  {
                    className: "value-custom jade-input",
                    disabled: m.includes(B),
                    value: O.value,
                    onChange: (te) => b(S, O.id, [{ key: "value", value: te.target.value }])
                  }
                )
              }
            );
        }
      default:
        return /* @__PURE__ */ r.jsx(r.Fragment, {});
    }
  }, Ie = (S, O, _) => {
    const B = S.value.find((le) => le.name === "left");
    if (B.type === _ && B.referenceKey === O)
      return;
    h(S.id, S.value.find((le) => le.name === "left").id, O, _), v(S.id, null);
    const te = _ === "Boolean" ? !1 : "";
    S.value.find((le) => le.name === "right").from === "Input" && (u.setFieldsValue({ [`value-${S.value.find((le) => le.name === "right").id}`]: te }), b(S.id, S.value.find((le) => le.name === "right").id, [{
      key: "value",
      value: te
    }]));
  }, Ae = (S, O) => {
    const _ = S.value.find((B) => B.name === "left").type === "Boolean" ? !1 : "";
    u.setFieldsValue({ [`value-${S.value.find((B) => B.name === "right").id}`]: _ }), b(
      S.id,
      S.value.find((B) => B.name === "right").id,
      [
        { key: "from", value: O },
        { key: "type", value: S.value.find((B) => B.name === "left").type },
        { key: "value", value: _ },
        { key: "referenceNode", value: "" },
        { key: "referenceId", value: "" },
        { key: "referenceKey", value: "" }
      ]
    );
  }, Oe = (S, O) => /* @__PURE__ */ r.jsxs(fe, { gutter: 16, style: { marginBottom: "6px", marginRight: 0 }, children: [
    /* @__PURE__ */ r.jsx(U, { span: 6, children: /* @__PURE__ */ r.jsx(
      De,
      {
        className: "jade-select",
        rules: [{ required: !0, message: "字段值不能为空" }],
        reference: S.value.find((_) => _.name === "left"),
        onReferencedValueChange: (_, B) => {
          Ie(S, _, B);
        },
        onReferencedKeyChange: (_) => y(S.id, S.value.find((B) => B.name === "left").id, _)
      }
    ) }),
    /* @__PURE__ */ r.jsx(U, { span: 6, children: /* @__PURE__ */ r.jsx(
      F.Item,
      {
        name: `condition-${S.id}`,
        rules: [{ required: !0, message: "字段值不能为空" }],
        initialValue: S.condition,
        children: /* @__PURE__ */ r.jsx(
          me,
          {
            className: "jade-select",
            style: { width: "100%" },
            placeholder: "请选择条件",
            options: x(S.value.find((_) => _.name === "left").type),
            value: S.condition,
            onChange: (_) => v(S.id, _)
          }
        )
      }
    ) }),
    /* @__PURE__ */ r.jsx(U, { span: 4, style: { paddingRight: 0 }, children: /* @__PURE__ */ r.jsx(F.Item, { children: /* @__PURE__ */ r.jsx(
      me,
      {
        id: `from-select-${S.id}`,
        disabled: m.includes(S.condition),
        className: "value-source-custom jade-select",
        style: { width: "100%" },
        onChange: (_) => Ae(S, _),
        options: [
          { value: "Reference", label: "引用" },
          { value: "Input", label: "输入" }
        ],
        value: S.value.find((_) => _.name === "right").from
      }
    ) }) }),
    /* @__PURE__ */ r.jsxs(U, { span: 7, style: { paddingLeft: 0 }, children: [
      Me(S.id, S.value.find((_) => _.name === "right"), S.value.find((_) => _.name === "left").type, S.condition),
      " "
    ] }),
    /* @__PURE__ */ r.jsx(U, { span: 1, style: { paddingLeft: 0 }, children: ae(O, S.id) })
  ] }, "row-" + O), Te = () => /* @__PURE__ */ r.jsxs("div", { style: { display: "flex" }, children: [
    e.conditions.length > 1 && C(),
    /* @__PURE__ */ r.jsxs(
      "div",
      {
        className: "jade-form condition-right-component",
        children: [
          /* @__PURE__ */ r.jsxs(fe, { gutter: 16, children: [
            /* @__PURE__ */ r.jsx(U, { span: 6, children: /* @__PURE__ */ r.jsx(F.Item, { children: /* @__PURE__ */ r.jsx("span", { className: "jade-font-size jade-font-color", children: "变量" }) }) }),
            /* @__PURE__ */ r.jsx(U, { span: 6, children: /* @__PURE__ */ r.jsx(F.Item, { children: /* @__PURE__ */ r.jsx("span", { className: "jade-font-size jade-font-color", children: "条件" }) }) }),
            /* @__PURE__ */ r.jsx(U, { span: 12, children: /* @__PURE__ */ r.jsx(F.Item, { children: /* @__PURE__ */ r.jsx("span", { className: "jade-font-size jade-font-color", children: "比较对象" }) }) })
          ] }),
          e.conditions.map((S, O) => Oe(S, O)),
          /* @__PURE__ */ r.jsx(fe, { gutter: 16, style: { marginBottom: "6px", marginRight: 0 }, children: /* @__PURE__ */ r.jsxs(
            se,
            {
              type: "link",
              className: "icon-button",
              onClick: () => l(e.id),
              style: { height: "32px", paddingLeft: "8px" },
              children: [
                /* @__PURE__ */ r.jsx(Ye, {}),
                /* @__PURE__ */ r.jsx("span", { children: "添加条件" })
              ]
            }
          ) })
        ]
      }
    )
  ] });
  return /* @__PURE__ */ r.jsx(G, { bordered: !1, className: "jade-collapse-custom-background-color", defaultActiveKey: ["ifPanel"], children: /* @__PURE__ */ r.jsx(
    As,
    {
      header: /* @__PURE__ */ r.jsxs("div", { className: "panel-header", children: [
        /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: a }),
        N(),
        I()
      ] }),
      className: "jade-panel",
      children: Te()
    },
    "ifPanel"
  ) });
}
const { Panel: $s } = G;
function zs() {
  return /* @__PURE__ */ r.jsx(
    $s,
    {
      header: /* @__PURE__ */ r.jsx("div", { className: "panel-header", children: /* @__PURE__ */ r.jsx("span", { className: "jade-panel-header-font", children: "Else" }) }),
      className: "jade-panel"
    },
    "elsePanel"
  );
}
const Un = ({ name: e, children: a }) => {
  const t = he(), o = rt();
  ge(() => {
    const n = xa(
      t,
      () => {
        const d = o.current, u = t.drawer.parent, m = d.getBoundingClientRect(), c = u.getBoundingClientRect();
        return (m.right - c.left) / t.page.scaleX;
      },
      () => {
        const d = o.current, u = t.drawer.parent, m = d.getBoundingClientRect(), c = u.getBoundingClientRect();
        return (m.top - c.top + (m.bottom - m.top) / 2) / t.page.scaleY;
      },
      () => ({
        cursor: "crosshair",
        key: e,
        color: "white",
        ax: "x",
        vector: 1,
        value: Fe.E
      }),
      (d) => d.visible,
      () => !0,
      () => !0,
      () => {
      },
      () => !1
    );
    return n.type = "dynamic", t.activeConnector(n), n.isSolid = !0, n.allowToLink = !1, n.allowFromLink = !0, () => {
      t.connectors = t.connectors.filter((d) => d.getDirection().key !== e), i();
    };
  }, []);
  const i = () => {
    t.page.shapes.filter((l) => l.fromShape === t.id || l.toShape === t.id).filter((l) => l.definedFromConnector === e).forEach((l) => l.remove());
  };
  return /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx("div", { id: "connector-provider-" + t.id, ref: o, children: a }) });
};
function Ds() {
  const e = re(), t = ne().branches, o = () => {
    e({ actionType: "addBranch" });
  }, i = (u) => {
    e({ actionType: "deleteBranch", branchId: u });
  }, s = (u, m) => {
    e({ actionType: "changeConditionRelation", branchId: u, conditionRelation: m });
  }, l = (u) => {
    e({ actionType: "addCondition", branchId: u });
  }, n = (u, m) => {
    e({ actionType: "deleteCondition", branchId: u, conditionId: m });
  }, d = (u, m, c) => {
    e({ actionType: "changeConditionConfig", branchId: u, conditionId: m, updateParams: c });
  };
  return /* @__PURE__ */ r.jsxs("div", { children: [
    /* @__PURE__ */ r.jsxs("div", { style: {
      display: "flex",
      alignItems: "center",
      marginBottom: "8px",
      paddingLeft: "8px",
      paddingRight: "4px"
    }, children: [
      /* @__PURE__ */ r.jsx("div", { className: "jade-panel-header-font", children: "条件分支" }),
      /* @__PURE__ */ r.jsxs(
        se,
        {
          type: "link",
          className: "icon-button",
          onClick: o,
          style: { height: "32px", marginLeft: "auto" },
          children: [
            /* @__PURE__ */ r.jsx(Ye, {}),
            /* @__PURE__ */ r.jsx("span", { children: "添加分支" })
          ]
        }
      )
    ] }),
    t.map((u, m) => /* @__PURE__ */ r.jsx(Un, { name: "dynamic-" + m, children: /* @__PURE__ */ r.jsx(
      Bs,
      {
        branch: u,
        index: m,
        name: m === 0 ? "If" : "Else if",
        totalItemNum: t.length + 1,
        deleteBranch: i,
        changeConditionRelation: s,
        addCondition: l,
        deleteCondition: n,
        changeConditionConfig: d
      },
      u.id
    ) }, "dynamic-" + m)),
    /* @__PURE__ */ r.jsx(Un, { name: `dynamic-${t.length + 1}`, children: /* @__PURE__ */ r.jsx(zs, {}) })
  ] });
}
const Vs = (e) => {
  const a = {};
  return a.getJadeConfig = () => e || {
    branches: [
      {
        id: T(),
        conditionRelation: "and",
        conditions: [
          {
            id: T(),
            condition: void 0,
            value: [
              {
                id: T(),
                name: "left",
                type: "",
                from: "Reference",
                value: "",
                referenceNode: "",
                referenceId: "",
                referenceKey: ""
              },
              {
                id: T(),
                name: "right",
                type: "",
                from: "Reference",
                value: "",
                referenceNode: "",
                referenceId: "",
                referenceKey: ""
              }
            ]
          }
        ]
      }
    ]
  }, a.getReactComponents = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Ws, {}) }), a.reducers = (t, o) => {
    const i = () => new He(t).updateBranch(o, (m) => m.updateCondition(o)), s = () => new He(t).deleteBranch(o.branchId), l = () => new He(t).updateBranch(o, (m) => m.changeConditionRelation(o.conditionRelation)), n = () => {
      const m = {
        id: T(),
        condition: void 0,
        value: [
          { id: T(), name: "left", type: "", from: "Reference", value: [], referenceNode: "" },
          { id: T(), name: "right", type: "", from: "Reference", value: [], referenceNode: "" }
        ]
      };
      return new He(t).updateBranch(o, (c) => c.addCondition(m));
    }, d = () => new He(t).updateBranch(o, (m) => m.deleteCondition(o.conditionId)), u = () => new He(t).addBranch();
    switch (o.actionType) {
      case "addBranch":
        return u();
      case "deleteBranch":
        return s();
      case "changeConditionRelation":
        return l();
      case "addCondition":
        return n();
      case "deleteCondition":
        return d();
      case "changeConditionConfig":
        return i();
      default:
        throw Error("Unknown action: " + o.type);
    }
  }, a;
};
class qs {
  constructor(a) {
    this.condition = { ...a };
  }
  updateValue(a) {
    this.condition.value = this.condition.value.map((t) => a.key === t.id ? {
      ...t,
      ...Object.fromEntries(a.value.map((o) => [o.key, o.value]))
    } : t);
  }
  update(a) {
    return a.forEach((t) => {
      t.key === "condition" ? this.condition[t.key] = t.value : this.updateValue(t);
    }), this.condition;
  }
}
class Hn {
  constructor(a) {
    this.branch = { ...a };
  }
  static createNewBranch() {
    return {
      id: T(),
      conditionRelation: "and",
      conditions: [
        {
          id: T(),
          condition: "",
          value: [
            {
              id: T(),
              name: "left",
              type: "",
              from: "Reference",
              value: [],
              referenceNode: ""
            },
            {
              id: T(),
              name: "right",
              type: "",
              from: "Reference",
              value: [],
              referenceNode: ""
            }
          ]
        }
      ]
    };
  }
  updateCondition(a) {
    return this.branch.conditions = this.branch.conditions.map((t) => t.id === a.conditionId ? new qs(t).update(a.updateParams) : t), this.branch;
  }
  deleteCondition(a) {
    return this.branch.conditions = this.branch.conditions.filter((t) => t.id !== a), this.branch;
  }
  addCondition(a) {
    return this.branch.conditions.push(a), this.branch;
  }
  changeConditionRelation(a) {
    return this.branch.conditionRelation = a, this.branch;
  }
}
class He {
  constructor(a) {
    this.data = { ...a };
  }
  updateBranch(a, t) {
    return this.data.branches = this.data.branches.map((o) => o.id === a.branchId ? t(new Hn(o)) : o), this.data;
  }
  deleteBranch(a) {
    return this.data.branches = this.data.branches.filter((t) => t.id !== a), this.data;
  }
  addBranch() {
    const a = Hn.createNewBranch();
    return this.data.branches.push(a), this.data;
  }
}
const Ws = () => /* @__PURE__ */ r.jsx(r.Fragment, { children: /* @__PURE__ */ r.jsx(Ds, {}) }), Kn = (e, a) => {
  const t = ja(e, a);
  t.type = "jadeFlowGraph", t.pageType = "jadeFlowPage", t.enableText = !1, t.flowMeta = {
    exceptionFitables: ["com.huawei.fit.jober.aipp.fitable.AippFlowExceptionHandler"]
  }, t.setting.borderColor = "#047bfc", t.setting.focusBorderColor = "#047bfc", t.setting.mouseInBorderColor = "#047bfc";
  const o = t.serialize;
  t.serialize = () => {
    const l = o.apply(t);
    return l.flowMeta = t.flowMeta, l;
  };
  const i = t.initialize;
  t.initialize = async () => (t.registerPlugin("jadeFlowPage", Pa), t.registerPlugin("jadeEvent", Oa), t.registerPlugin("taskNode", ai), t.registerPlugin("endNodeEnd", si), t.registerPlugin("endComponent", fi), t.registerPlugin("retrievalNodeState", wi), t.registerPlugin("retrievalComponent", Ei), t.registerPlugin("listener1Node", Ni), t.registerPlugin("listener1Component", Si), t.registerPlugin("listener2Node", ki), t.registerPlugin("listener2Component", Pi), t.registerPlugin("listener3Node", Oi), t.registerPlugin("listener3Component", Ti), t.registerPlugin("jadeInputTreeNode", Li), t.registerPlugin("jadeInputTreeComponent", Ji), t.registerPlugin("testNode", Zi), t.registerPlugin("testComponent", Gi), t.registerPlugin("replaceComponent", es), t.registerPlugin("startNodeStart", rs), t.registerPlugin("startComponent", is), t.registerPlugin("llmNodeState", cs), t.registerPlugin("llmComponent", vs), t.registerPlugin("manualCheckNodeState", xs), t.registerPlugin("manualCheckComponent", ws), t.registerPlugin("fitInvokeNodeState", Ss), t.registerPlugin("toolInvokeNodeState", Ls), t.registerPlugin("fitInvokeComponent", Ts), t.registerPlugin("toolInvokeComponent", Ur), t.registerPlugin("conditionNodeCondition", Ms), t.registerPlugin("conditionComponent", Vs), i.apply(t)), t.registerPlugin = (l, n, d = null) => {
    d ? t.plugins[`${d}.${l}`] = n : t.plugins[l] = n;
  };
  const s = t.dirtied;
  return t.dirtied = (l, n) => {
    s.call(t, l, n), t.onChangeCallback && t.onChangeCallback();
  }, t;
}, Jn = (e) => {
  const a = {};
  return a.graph = e, a.want = (t, o) => {
    e.activePage.want(t, o);
  }, a.import = (t) => a.graph.staticImport(t), a.serialize = () => (e.activePage.serialize(), e.serialize()), a.getAvailableNodes = () => [
    { type: "retrievalNodeState", name: "数据检索" },
    { type: "llmNodeState", name: "大模型" },
    { type: "manualCheckNodeState", name: "人工检查" },
    { type: "fitInvokeNodeState", name: "FIT调用" }
  ], a.createNode = (t, o, i) => {
    console.log("call createNode...");
    const s = e.activePage.calculatePosition(o);
    e.activePage.createNew(t, s.x, s.y).processMetaData(i);
  }, a.createNodeByPosition = (t, o, i) => {
    console.log("call createNodeByPosition..."), e.activePage.createNew(t, o.x, o.y).processMetaData(i);
  }, a.onChange = (t) => {
    e.onChangeCallback = t;
  }, a.getNodeConfigs = () => e.activePage.shapes.filter((t) => t.isTypeof("jadeNode")).map((t) => ({
    [t.id]: t.getLatestJadeConfig()
  })), a.validate = async () => {
    const o = e.activePage.shapes.filter((l) => l.isTypeof("jadeNode")).map((l) => l.validate().catch((n) => n)), s = (await Promise.all(o)).filter((l) => l.errorFields);
    return s.length > 0 ? Promise.reject(s) : Promise.resolve();
  }, a;
}, Zs = (() => {
  const e = {};
  return e.new = async (a, t) => {
    const o = Kn(a, "jadeFlow");
    o.configs = t, o.collaboration.mute = !0, await o.initialize();
    const i = o.addPage("newFlowPage"), s = i.createShape("startNodeStart", 100, 100), l = i.createShape("endNodeEnd", s.x + s.width + 200, 100), n = i.createNew("jadeEvent", 0, 0);
    return i.reset(), n.connect(s.id, "E", l.id, "W"), i.fillScreen(), Jn(o);
  }, e.edit = async (a, t, o, i = []) => {
    const s = Kn(a, "jadeFlow");
    s.configs = o, s.collaboration.mute = !0;
    for (let n = 0; n < i.length; n++)
      await s.dynamicImportStatement(i[n]);
    await s.initialize(), s.deSerialize(t);
    const l = s.getPageData(0);
    return await s.edit(0, a, l.id), Jn(s);
  }, e;
})();
export {
  Zs as JadeFlow
};
//# sourceMappingURL=fit-elsa-react.js.map
