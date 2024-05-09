import { canvasRectangleDrawer } from "../../core/drawers/rectangleDrawer.js";
import { rectangle } from "../../core/rectangle.js";
import { ELSA_NAME_SPACE } from '../../common/const.js';
import { ellipse } from "../../core/ellipse.js";
import { smileyFace, doubleWave } from "./customShapesData.js";
import { connector } from "../../core/connector.js";

export const namespace = ELSA_NAME_SPACE + ".office";


const freeShape = (id, x, y, width, height, parent) => {
    // const self = rectangle(id, x, y, width, height, parent,canvasRectangleDrawer);
    const self = ellipse(id, x, y, width, height, parent);
    self.hideText = true;
    self.overflowHidden = false;
    self.borderWidth = 0;
    self.lineWidth = 1;
    self.tag = smileyFace;//doubleWave;
    self.cache = {};
    // self.margin = 0;

    const parseGds = (node, target) => {
        (!target.gds) && (target.gds = proxy(self));
        node.childNodes.forEach(child => {
            target.gds[child.getAttribute("name")] = child.getAttribute("fmla");
        })
    }
    const parseAvLst = (node, target) => {
        // const root = target.avLst = proxy(self);
        parseGds(node, target);
    };
    const parseGdLst = (node, target) => {
        // const root = target.gdLst = proxy(self);
        parseGds(node, target);
    };
    const parseAhLst = (node, target) => {
        const root = target.ahLst = [];
        node.childNodes.forEach(child => {//ahXy
            const ah = proxy(self);
            root.push(ah);
            for (let i = 0; i < child.attributes.length; i++) {
                const attr = child.attributes[i];
                ah[attr.name] = attr.value;
            }
            const pos = child.firstChild;
            ah.x = pos.getAttribute("x");
            ah.y = pos.getAttribute("y");

        });
    };
    const parseCxnLst = (node, target) => {
        const root = target.cxnLst = [];
        node.childNodes.forEach(child => {//ahXy
            const cxn = proxy(self);
            root.push(cxn);
            for (let i = 0; i < child.attributes.length; i++) {
                const attr = child.attributes[i];
                cxn[attr.name] = attr.value;
            }
            const pos = child.firstChild;
            cxn.x = pos.getAttribute("x");
            cxn.y = pos.getAttribute("y");

        });
    };
    const parseRect = (node, target) => {
        const root = target.rect = proxy(self);
        for (let i = 0; i < node.attributes.length; i++) {
            const attr = node.attributes[i];
            if (attr.name === "xmlns") continue;
            root[attr.name] = attr.value;
        }

    };

    const parseAttributes = (node, target) => {
        for (let i = 0; i < node.attributes.length; i++) {
            const attr = node.attributes[i];
            if (attr.name === "xmlns") continue;
            target[attr.name] = attr.value;
        }
    };
    const parseMoveTo = (node, target) => {
        const pt = node.firstChild;
        parseAttributes(pt, target);
    };
    const parseLineTo = (node, target) => {
        parseMoveTo(node, target);
    };
    const parseArcTo = (node, target) => {
        parseAttributes(node, target);
    };
    const parseBezTo = (node, target) => {
        target.points = [];
        node.childNodes.forEach(child => {
            const pt = proxy(self);
            target.points.push(pt);
            parseAttributes(child, pt);
        })
    };

    const parsePath = (node, target) => {
        target.steps = [];
        node.childNodes.forEach(child => {
            let step = {};
            switch (child.tagName) {
                case "moveTo":
                    step = proxy(self);
                    parseMoveTo(child, step);
                    break;
                case "lnTo":
                    step = proxy(self);
                    parseLineTo(child, step);
                    break;
                case "arcTo":
                    step = proxy(self);
                    parseArcTo(child, step);
                    break;
                case "quadBezTo":
                case "cubicBezTo":
                    parseBezTo(child, step);
                    break;
                default:
                    break;
            }
            target.steps.push(step);
            step.action = child.tagName;
            step.path = target;
        });
    };

    const parsePathLst = (node, target) => {
        const root = target.paths = [];
        node.childNodes.forEach(child => {
            const path = {};
            root.push(path);
            for (let i = 0; i < child.attributes.length; i++) {
                const attr = child.attributes[i];
                path[attr.name] = attr.value;
            }
            parsePath(child, path);
        });

    };
    self.getParsed = () => {
        if (self.parsed) return self.parsed;
        //get original data
        let data = self.tag;
        // if (self.tag === "" || self.tag === undefined || self.tag === null) {
        //     data = smileyFace;
        // } else {
        //     data = self.tag;
        // }

        self.parsed = {};
        const xmlparser = new DOMParser();
        const xmlDoc = xmlparser.parseFromString(data, "text/xml");
        const root = xmlDoc.getRootNode().firstChild;
        root.childNodes.forEach(node => {
            switch (node.tagName) {
                case "avLst":
                    parseAvLst(node, self.parsed);
                    break;
                case "gdLst":
                    parseGdLst(node, self.parsed);
                    break;
                case "ahLst":
                    parseAhLst(node, self.parsed);
                    break;
                case "cxnLst":
                    parseCxnLst(node, self.parsed);
                    break;
                case "rect":
                    parseRect(node, self.parsed);
                    break;
                case "pathLst":
                    parsePathLst(node, self.parsed);
                    break;
                default:
                    break;
            }
        })
        return self.parsed;
    };

    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.call(self);
        const parsed = self.getParsed();
        if (parsed.cxnLst) {//linking connectors
            self.connectors.remove(c => c.type === "connection");
            parsed.cxnLst.forEach((c, i) => {
                const x = c.x;
                const y = c.y;
                const rx = (s, c) => c.frame.x - s.x + x;
                const ry = (s, c) => c.frame.y - s.y + y;
                self.createUndragableConnector({ rx, ry, key: `conn-${i}` });
            });
        }
        if (parsed.ahLst) {//adjusting connectors
            parsed.ahLst.forEach((a, i) => {
                const key = `adj-${i}`;
                const adj = connector(self, (s, c) => c.frame.x - s.x + a.x, (s, c) => c.frame.y - s.y + a.y, s => { return { cursor: "crosshair", key, color: "gold" } }, s => true, s => true);
                adj.type = key;
                adj.minX = Number.parseInt(a.minX);
                adj.minY = Number.parseInt(a.minY);
                adj.maxX = Number.parseInt(a.maxX);
                adj.maxX = Number.parseInt(a.maxX);
                adj.refX = a.gdRefX;
                adj.refY = a.gdRefY;
                adj.gds = parsed.gds;

                adj.moving = (deltaX, deltaY, x, y) => {
                    let dx = Math.ceil(deltaX * 100000 / self.width), dy = Math.ceil(deltaY * 100000 / self.height);

                    if (adj.refX) {
                        const oldx = a.x;
                        const value = adj.gds[adj.refX]
                        let newValue = value + dx * (adj.refXop === undefined ? 1 : adj.refXop);
                        if (newValue < adj.minX) newValue = adj.minX;
                        if (newValue > adj.maxX) newValue = adj.maxX;
                        if (value === newValue) return;
                        const formular = adj.gds.formulars[adj.refX];
                        const newFormular = formular.replace(/-?\d+/, newValue);
                        adj.gds.formulars[adj.refX] = newFormular;
                        if (adj.refXop === undefined) {
                            self.cache = {};
                            adj.refXop = (a.x - oldx) / dx > 0 ? 1 : -1;
                            if (adj.refXop < 0) {//方向反了，重新算过
                                adj.gds.formulars[adj.refX] = formular;
                                adj.moving(deltaX, deltaY, x, y);
                            }
                        }
                    }
                    if (adj.refY) {
                        const oldy = a.y;
                        const value = adj.gds[adj.refY]
                        let newValue = value + dy * (adj.refYop === undefined ? 1 : adj.refYop);
                        if (newValue < adj.minY) newValue = adj.minY;
                        if (newValue > adj.maxY) newValue = adj.maxY;
                        if (value === newValue) return;
                        const formular = adj.gds.formulars[adj.refY];
                        const newFormular = formular.replace(/-?\d+/, newValue);
                        adj.gds.formulars[adj.refY] = newFormular;
                        if (adj.refYop === undefined) {
                            self.cache = {};
                            adj.refYop = (a.y - oldy) / dy > 0 ? 1 : -1;
                            if (adj.refYop < 0) {//方向反了，重新算过
                                adj.gds.formulars[adj.refY] = formular;
                                adj.moving(deltaX, deltaY, x, y);
                            }
                        }
                    }
                    self.cache = {};
                    self.render();
                }
            })
        }
    };

    const drawArcTo = (context, startx, starty, wr, hr, startAngle, endAngle, anticlockwise) => {
        const getDirection = inputAngle => {
            let angle = inputAngle % (2 * Math.PI);
            //可能转了很多圈
            let rest = inputAngle - angle;
            //将角度换算为-pi ~ pi的范围
            (angle > Math.PI) && (angle -= 2 * Math.PI);
            (angle < -Math.PI) && (angle += 2 * Math.PI);
            //象限不一样，决定atan时的矢量
            let direction = (angle > -Math.PI / 2 && angle <= Math.PI / 2) ? 1 : -1;
            return [rest,direction];
        }
        let[rest,direction] = getDirection(startAngle);
        //得到离心角
        let spAngle = Math.atan2(Math.tan(startAngle) * wr * direction, hr * direction) + rest;

        // spAngle = startAngle;
        let centerx = startx - wr * Math.cos(spAngle);
        let centery = starty - hr * Math.sin(spAngle);

        [rest,direction] = getDirection(endAngle);
        //得到离心角
        let epAngle = Math.atan2(Math.tan(endAngle) * wr * direction, hr * direction) + rest;

        // epAngle = endAngle;
        const endx = centerx + wr * Math.cos(epAngle);
        const endy = centery + hr * Math.sin(epAngle);

        // context.ellipse(centerx, centery, wr, hr, 0, startAngle, endAngle, anticlockwise);
        context.ellipse(centerx, centery, wr, hr, 0, spAngle, epAngle, anticlockwise);

        context.stroke();
        context.fillStyle = "orange";
        context.fillRect(endx - 2, endy - 2, 4, 4);
        context.fillRect(startx - 2, starty - 2, 4, 4);
        return { x: endx, y: endy };

    }
    self.drawer.drawStatic = (context, x, y) => {
        context.save();
        context.translate(x - 2, y - 2);
        context.strokeStyle = "green";
        // context.lineWidth = 5;
        //context.strokeRect(0,0,self.width,self.height);
        const parsed = self.getParsed();
        parsed.paths.forEach(path => {
            let x1, y1;
            context.beginPath();
            const parsex = x => {
                if (path.w === undefined) {
                    return x;
                } else {
                    return x * self.width / path.w;
                }
            }
            const parsey = y => {
                if (path.h === undefined) {
                    return y;
                } else {
                    return y * self.height / path.h;
                }
            }
            path.steps.forEach(step => {
                switch (step.action) {
                    case "moveTo":
                        // x1 = step.x + x, y1 = step.y + y;
                        x1 = parsex(step.x), y1 = parsey(step.y);
                        context.moveTo(x1, y1);
                        break;
                    case "lnTo":
                        x1 = parsex(step.x), y1 = parsey(step.y);
                        context.lineTo(x1, y1);
                        break;
                    case "arcTo":
                        let wr = parsex(step.wR), hr = parsey(step.hR), sa = step.stAng * Math.PI / (60000 * 180), sw = step.swAng * Math.PI / (60000 * 180);
                        let ea = sw + sa;
                        const endPoint = drawArcTo(context, x1, y1, wr, hr, sa, ea, sw < 0);
                        x1 = endPoint.x;
                        y1 = endPoint.y;
                        break;
                    case "quadBezTo":
                        x1 = parsex(step.points[1].x);
                        y1 = parsey(step.points[1].y);
                        context.quadraticCurveTo(parsex(step.points[0].x), parsey(step.points[0].y), x1, y1);
                        break;
                    case "cubicBezTo":
                        x1 = parsex(step.points[2].x);
                        y1 = parsey(step.points[2].y);
                        context.bezierCurveTo(parsex(step.points[0].x), parsey(step.points[0].y), parsex(step.points[1].x), parsey(step.points[1].y), x1, y1);
                        break;
                    case "close":
                        context.closePath();
                        break;
                    default:
                        break;
                }

            });


            if (path.fill !== 'none') {
                context.fillStyle = "gray";// self.backColor;
            }
            if (path.stroke === 'true') {
                // context.lineWidth = 2;
                context.strokeStyle = "orange";//self.borderColor;
                context.stroke();
            }
            context.fillStyle = "rgba(222,222,222,0.01)";
            context.fill();
            context.lineWidth = self.lineWidth;
            context.stroke();
        })
        context.restore();
        //todo: use rect to fill text
        parsed.rect;

    };

    self.setData = data => {
        self.tag = data;
        // delete self.parsed;
        self.invalidate();
    }
    self.serialized = () => {
        delete self.parsed;
    };
    self.addDetection(["tag"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        delete self.parsed;
        // self.invalidate();
    });
    self.addDetection(["width", "height"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        self.cache = {};
    });
    return self;
};

/**
 * 公式里的相互引用不能确保哪个先执行，所以在这里做lazy处理
 */
const proxy = (() => {
    //------------所有系统规定的没有参数的变量函数-----------------------
    const getAngle = (value) => value / 60000 * Math.PI / 180;
    const varFunctions = {};
    varFunctions.ssd = (shape, n) => varFunctions.ss(shape) / n;
    varFunctions.wd = (shape, n) => varFunctions.w(shape) / n;
    varFunctions.hd = (shape, n) => varFunctions.h(shape) / n;
    varFunctions.cd = (shape, n1, n2) => 60000 * 360 * (n2 ? n2 : 1) / n1;
    varFunctions.vc = (shape) => varFunctions.h(shape) / 2;
    varFunctions.hc = (shape) => varFunctions.w(shape) / 2;
    varFunctions.ls = (shape) => Math.max(varFunctions.h(shape), varFunctions.w(shape));
    varFunctions.ss = (shape) => Math.min(varFunctions.h(shape), varFunctions.w(shape));
    varFunctions.t = (shape) => 0;
    varFunctions.l = (shape) => 0;
    varFunctions.w = (shape) => shape.width;
    varFunctions.h = (shape) => shape.height;
    varFunctions.b = (shape) => varFunctions.h(shape);
    varFunctions.r = (shape) => varFunctions.w(shape);

    const expFunctions = {};
    expFunctions.val = (value) => value;
    expFunctions.pin = (x, y, z) => y < x ? x : (y > z ? z : y);
    expFunctions["*/"] = (x, y, z) => (x * y) / z
    expFunctions["+-"] = (x, y, z) => (x + y) - z
    expFunctions["+/"] = (x, y, z) => (x + y) / z
    expFunctions["?:"] = (x, y, z) => x > 0 ? y : z;
    expFunctions.abs = (x) => Math.abs(x);
    expFunctions.sin = (x, y) => x * Math.sin(getAngle(y));
    expFunctions.cos = (x, y) => x * Math.cos(getAngle(y));
    expFunctions.tan = (x, y) => x * Math.tan(getAngle(y));
    expFunctions.at2 = (x, y) => Math.atan2(y, x) * 180 * 60000 / Math.PI;
    expFunctions.cat2 = (x, y, z) => x * Math.cos(Math.atan2(z, y));
    expFunctions.max = (x, y) => Math.max(x, y);
    expFunctions.min = (x, y) => Math.min(x, y);
    expFunctions.mod = (x, y, z) => Math.sqrt(x * x + y * y + z * z);
    expFunctions.sat2 = (x, y, z) => x * Math.sin(Math.atan2(z, y));
    expFunctions.sqrt = (x) => Math.sqrt(x);

    const getArguments = (func) => {
        const funcStr = func.toString();
        const argRegex = /\(([^)]*)\)/;
        const match = argRegex.exec(funcStr);
        if (match === null) {
            return [];
        }
        const argStr = match[1];
        const args = argStr.split(',').map(arg => arg.trim());
        return args;
    }

    const varPatterns = (() => {
        const patterns = [];
        for (let f in varFunctions) {
            const args = getArguments(varFunctions[f]);
            // if (args[0] !== "shape") continue;
            if (args.length < 2) {
                patterns.push(new RegExp(`^(?<name>${f})$`));
            }
            if (args.length === 2) {
                patterns.push(new RegExp(`^(?<name>${f})(?<arg1>-?[1-9]+)$`));
            }
            if (args.length === 3) {
                patterns.push(new RegExp(`(?<arg2>[2-9][1-9]*)?(?<name>${f})(?<arg1>[1-9]+)$`));
            }
        }
        return patterns;
    })();
    const matchPattern = string => {
        const funcMeta = { args: [] };
        for (let i = 0; i < varPatterns.length; i++) {
            const pattern = varPatterns[i];
            if (pattern.test(string)) {
                let match = pattern.exec(string);
                const groups = match.groups;
                for (let g in groups) {
                    if (groups[g] === undefined) continue;
                    switch (g) {
                        case "name":
                            funcMeta.name = groups[g];
                            break;
                        case "arg1":
                        case "arg2":
                            funcMeta.args.push(groups[g]);
                    }
                }
                return funcMeta;
            }
        }
    }
    const runVariable = (shape, string) => {
        let value = Number.parseInt(string);
        if (!Number.isNaN(value)) {
            return value;
            // if (tagName.indexOf("Ang") >= 0) {
            //     return value;
            // } else {
            //     return shape.width*value/100000;// * 96 / 914400;
            // }
        }
        //find gds first
        // let result = shape.parsed.gds[string];
        let result = (shape.cache[string] === undefined) ? shape.getParsed().gds[string] : shape.cache[string];
        if (result !== undefined && result !== null) {
            // result = runVariable(shape,result);
            shape.cache[string] = result;
            //console.log(`gd expression result: ${result}`);
            return result;
        } else {
            //and find system function
            const varMeta = matchPattern(string);
            if (varMeta === undefined) return string;
            const args = [shape];
            args.push.apply(args, varMeta.args)
            const varFunc = varFunctions[varMeta.name];
            result = varFunc.apply(varFunctions, args);
            shape.cache[string] = result;
            //console.log(`var expression result: ${result}`);
            return result;
        }
    };

    const runGdComputation = (shape, funcName, args) => {
        const newArgs = [];
        args.forEach(arg => {
            let value = Number.parseInt(arg);
            if (!Number.isNaN(value)) {
                newArgs.push(value);
            } else {
                newArgs.push(runVariable(shape, arg));
            }
        })
        return expFunctions[funcName].apply(expFunctions, newArgs);
    };


    return (shape) => {
        const formulars = {};
        const parseVariable = (string) => {
            const args = string.split(/\s+/g);//0 is functions, the rest is arguments
            return exec(args.shift(), args);
        };


        const exec = (funcName, args) => {
            if (args.length === 0) {//compute variable
                return runVariable(shape, funcName);
            } else {//compute gd
                return runGdComputation(shape, funcName, args);
            }

        };
        const ignoreKeys = { gdRefX: "gdRefX", gdRefY: "gdRefY" };
        //---------end of closure functions and variables------------------

        return new Proxy({ formulars }, {
            get: function (target, propKey, receiver) {
                let value = target[propKey];
                if (formulars[propKey]) {//*96/914400
                    value = parseVariable(formulars[propKey]);
                }
                return value;
            },
            set: function (target, propKey, value, receiver) {
                if (typeof value !== "string" || ignoreKeys[propKey]) {
                    target[propKey] = value;
                } else {
                    formulars[propKey] = value;
                    target[propKey] = null;
                }
                return true;
            }
        });
    };
})()

export { freeShape }