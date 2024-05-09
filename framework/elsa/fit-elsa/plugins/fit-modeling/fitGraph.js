import {ALIGN, DOCK_MODE, LINEMODE, PROGRESS_STATUS} from '../../common/const.js';
import {graph} from '../../core/graph.js';
import {container} from '../../core/container.js';
import {rectangle} from '../../core/rectangle.js';
import {line} from '../../core/line.js';
import {hitRegion} from '../../core/hitRegion.js';
import {containerDrawer} from '../../core/drawers/containerDrawer.js';
import {rectangleDrawer} from '../../core/drawers/rectangleDrawer.js';

//---------------------------------functions-----------------------------------
//todo: data will be replaced by remote call
let loadGenericable = genericableId => {
    let values = {};
    values["genericable-1"] = {
        genericableId: "genericable-1",
        name: "genericable example 1",
        progressStatus: PROGRESS_STATUS.RUNNING,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f1", name: "fitable-1", language: "Java", progressStatus: PROGRESS_STATUS.RUNNING},
            {id: "f2", name: "fitable-2", language: "C++", progressStatus: PROGRESS_STATUS.DOING},
            {id: "f3", name: "fitable-3", language: "Go", progressStatus: PROGRESS_STATUS.UNKNOWN}]
    };

    values["genericable-2"] = {
        genericableId: "genericable-2",
        name: "genericable example 2",
        progressStatus: PROGRESS_STATUS.RUNNING,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f4", name: "fitable-4", language: "Python", progressStatus: PROGRESS_STATUS.RUNNING},
            {id: "f5", name: "fitable-5", language: "C", progressStatus: PROGRESS_STATUS.UNKNOWN}]
    };
    values["genericable-3"] = {
        genericableId: "genericable-3",
        name: "genericable example 3",
        progressStatus: PROGRESS_STATUS.DOING,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f6", name: "fitable-6", language: "Python", progressStatus: PROGRESS_STATUS.COMPLETE}]
    };
    values["genericable-4"] = {
        genericableId: "genericable-4",
        name: "genericable example 4",
        progressStatus: PROGRESS_STATUS.DOING,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f7", name: "fitable-7", language: "C++", progressStatus: PROGRESS_STATUS.PAUSE}]
    };

    values["genericable-5"] = {
        genericableId: "genericable-5",
        name: "genericable example 5",
        progressStatus: PROGRESS_STATUS.RUNNING,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f8", name: "fitable-8", language: "Java", progressStatus: PROGRESS_STATUS.RUNNING}]
    };
    values["genericable-6"] = {
        genericableId: "genericable-6",
        name: "genericable example 6",
        progressStatus: PROGRESS_STATUS.COMPLETE,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f9", name: "fitable-9", language: "C", progressStatus: PROGRESS_STATUS.COMPLETE}]
    };
    values["genericable-7"] = {
        genericableId: "genericable-7",
        name: "genericable example 7",
        progressStatus: PROGRESS_STATUS.RUNNING,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f10", name: "fitable-10", language: "Python", progressStatus: PROGRESS_STATUS.RUNNING}]
    };
    values["genericable-8"] = {
        genericableId: "genericable-8",
        name: "genericable example 8",
        progressStatus: PROGRESS_STATUS.COMPLETE,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f11", name: "fitable-11", language: "Java", progressStatus: PROGRESS_STATUS.COMPLETE},
            {id: "f12", name: "fitable-12", language: "Java", progressStatus: PROGRESS_STATUS.PAUSE}]
    };
    values["genericable-9"] = {
        genericableId: "genericable-9",
        name: "genericable example 9",
        progressStatus: PROGRESS_STATUS.NOTSTARTED,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f13", name: "fitable-13", language: "C++", progressStatus: PROGRESS_STATUS.COMPLETE}]
    };
    values["genericable-10"] = {
        genericableId: "genericable-10",
        name: "genericable example 10",
        progressStatus: PROGRESS_STATUS.NOTSTARTED,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f14", name: "fitable-14", language: "C", progressStatus: PROGRESS_STATUS.COMPLETE}]
    };
    values["genericable-11"] = {
        genericableId: "genericable-11",
        name: "genericable example 11",
        progressStatus: PROGRESS_STATUS.NOTSTARTED,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f15", name: "fitable-15", language: "Go", progressStatus: PROGRESS_STATUS.RUNNING}]
    };
    values["genericable-12"] = {
        genericableId: "genericable-12",
        name: "genericable example 12",
        progressStatus: PROGRESS_STATUS.RUNNING,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f16", name: "fitable-16", language: "C++", progressStatus: PROGRESS_STATUS.RUNNING}]
    };
    values["genericable-13"] = {
        genericableId: "genericable-13",
        name: "genericable example 13",
        progressStatus: PROGRESS_STATUS.RUNNING,
        collectionId: "collection",
        collectionName: "collection example",
        fitables: [{id: "f17", name: "fitable-17", language: "Java", progressStatus: PROGRESS_STATUS.COMPLETE}]
    };

    return values[genericableId];
};

let loadGenericableDepending = genericableId => {
    let values = {};
    values["genericable-1"] = [{genericableId: "genericable-2", fitableId: "f1", status: "green", weight: 2},
        {genericableId: "genericable-3", fitableId: "f1", status: "gray", weight: 1},
        {genericableId: "genericable-4", fitableId: "f3", status: "red", weight: 1}];
    values["genericable-2"] = [{genericableId: "genericable-5", fitableId: "f4", status: "green", weight: 1},
        {genericableId: "genericable-6", fitableId: "f4"}];
    values["genericable-5"] = [{genericableId: "genericable-7", fitableId: "f8", status: "green", weight: 1}];
    return values[genericableId];
};

let loadGenericableDepended = genericableId => {
    let values = {};
    values["genericable-1"] = [{genericableId: "genericable-8", fitableId: "f11"},
        {genericableId: "genericable-9", fitableId: "f13"}];
    values["genericable-8"] = [{genericableId: "genericable-10", fitableId: "f14"},
        {genericableId: "genericable-11", fitableId: "f15"}, {genericableId: "genericable-12", fitableId: "f16"}];
    values["genericable-12"] = [{genericableId: "genericable-13", fitableId: "f17"}];
    return values[genericableId];
    //todo
};
//-----------------------------------------------------------------------------

/**
 * 用于展示FIT拓扑图的graph
 * 辉子 2021
 */
let fitGraph = (div) => {
    let self = graph(div, "");

    let addPage = self.addPage;
    self.addPage = (name, genericableId) => {
        let page = addPage.call(self, name, div);
        page.loadGenericable = genericableId => {
            page.genericableId = genericableId;
            page.shapes = [];
            let g = page.createShape("genericable", page.width / 2 - 200, page.height / 2 - 100,genericableId);
            g.init(genericableId);
            g.load();
        };
        if (genericable !== undefined) {
            page.loadGenericable(genericableId);
        }
        page.reset();
        return page;
    };

    return self;
};

//--------------------------------shapes-------------------------------------
/**
 * 代表一个genericable
 * 辉子 2021
 */
let genericable = (id, x, y, width, height, parent) => {
    let self = container(id, x, y, width, height, parent, containerDrawer);
    self.type = "genericable";
    self.namespace = "fit-modeling";
    self.dockMode = DOCK_MODE.VERTICAL;
    self.dockAlign = ALIGN.TOP;
    self.text = "new genericable";
    self.genericableId = "";
    self.collectionId = "collectionId";
    self.collectionName = "some collection";
    self.backColor = "whitesmoke";
    self.defaultWidth = 180;
    self.defaultHeight = 80;
    self.pad = 2;
    self.borderWidth = 1;
    self.cornerRadius = 4;
    self.ifMaskItems = false;
    self.itemPad = [3, 3, 25, 3];
    self.progressStatus = PROGRESS_STATUS.NOTSTARTED;
    self.fontColor = self.borderColor = self.progressStatus.color;
    self.rotateAble = self.moveable = self.editable = self.resizeable = false;

    //---------------- genericable properties--------------
    self.genericableId = "";
    self.collectionId = "collectionId";
    self.collectionName = "some collection";
    //-----------------------------------------------

    //show on the top of canvas
    self.show = () => {
        self.globalAlpha = 1;
        self.page.moveIndexTop(self);
        self.invalidate();
    };

    self.hide = () => {
        if (self.globalAlpha === 1) {
            self.globalAlpha = 0.3;
        } else {
            self.globalAlpha = 0;
        }

        self.invalidate();
    };

    self.childAllowed = c => c.isType('fitable');

    let invalidate = self.invalidate;
    self.invalidate = () => {
        invalidate.apply(self);
        self.height = self.getShapes().sum(s => s.height + self.itemSpace) + 40;
        self.emphasized = (self.page.genericableId === self.genericableId)
    };

    //self.click = () => self.load();

    self.selected = () => {
        self.page.getFocusedShapes().filter(s => s !== self).forEach(s => s.isFocused = false);
        self.loadData();
        if (!self.isMaster()) {
            displayParents(self, self.isDepending);
        }
    };

    let hideOthers = (node, isDepending) => {
        node.hide();
        let from = isDepending ? "fromShape" : "toShape", to = isDepending ? "toShape" : "fromShape";
        let lines = self.page.shapes.filter(s => s[from] === node.id);
        lines.forEach(l => l.hide());
        lines.map(s => self.page.shapes.find(p => p.id === s[to])).forEach(n => hideOthers(n, n.isDepending));
    };

    let displayParents = (node, isDepending) => {
        node.show();
        let from = isDepending ? "fromShape" : "toShape", to = isDepending ? "toShape" : "fromShape";
        let plines = self.page.shapes.filter(s => s[to] === node.id);
        plines.forEach(l => l.show());
        let parents = plines.map(s => self.page.shapes.find(p => p.id === s[from]));
        parents.forEach(parent => {
            let lines = self.page.shapes.filter(s => s[from] === parent.id && s[to] !== node.id);
            lines.forEach(l => l.hide());
            lines.map(s => self.page.shapes.find(p => p.id === s[to])).forEach(n => {
                //n.hide();
                hideOthers(n, isDepending);
            });
            if (!parent.isMaster()) {
                displayParents(parent, parent.isDepending);
            }
        });
    }

    let gRegion = iconRegion(self, "G", "enericable", () => 2, () => -9);
    self.regions.push(gRegion);
    gRegion.click = () => {
        // let dx = Math.round(self.x + self.width / 2 - self.page.width / 2+self.page.x), dy = Math.round(self.y + self.height / 2 - self.page.height / 2+self.page.y);
        // self.page.x -= dx;
        // self.page.y -= dy;
        // self.page.invalidate();

    }

    let collection = iconRegion(self, "collection:", self.collectionName, () => 2, () => self.height - 14);
    self.regions.push(collection);
    collection.click = () => console.log("collection region is clicked");

    //---------------loading data------------------------
    self.isMaster = () => self.page.genericableId === self.genericableId;
    self.init = genericableId => {
        let data = loadGenericable(genericableId);
        //load basic properties
        self.genericableId = data.genericableId;
        self.progressStatus = data.progressStatus;
        self.text = data.name;
        self.collectionId = data.collectionId;
        self.collectionName = data.collectionName;

        //load fitables
        data.fitables.forEach(fitable => {
            if (self.getShapes().find(s => s.fitableId === fitable) !== undefined) {
                return;
            }
            let f = self.page.createShape("fitable", self.x + 1, self.y + 1,fitable.id);
            f.container = self.id;
            f.text = fitable.name;
            f.fitableId = fitable.id;
            f.language = fitable.language;
            f.progressStatus = fitable.progressStatus;
            f.reset();
        });
        self.reset();
    };

    self.loadData = () => {
        if (self.isMaster() || self.isDepended) {
            self.loadDepended();
        }
        if (self.isMaster() || self.isDepending) {
            self.loadDepending();
        }
    };

    let loadingMore = isDepending => {
        let OFFSET_X = 100, OFFSET_Y = 40;
        let data = isDepending ? loadGenericableDepending(self.genericableId) : loadGenericableDepended(self.genericableId);
        if (data === undefined) {
            return;
        }
        let height = 0;
        let gs = [];
        data.forEach(d => {
            let g = self.page.shapes.find(s => s.genericableId === d.genericableId);
            if (g !== undefined) {
                g.show();
                self.page.shapes.find(s => isDepending ? s.toShape === g.id : s.fromShape === g.id).show();
                return;
            }
            g = self.page.createShape("genericable", 0, 0,d.genericableId);
            g.init(d.genericableId);
            g.moveTo(self.x + (isDepending ? (self.width + OFFSET_X) : (-g.width - OFFSET_X)), self.y + height);
            g.isDepended = !isDepending;
            g.isDepending = isDepending;
            g.container = self.page.id;
            height += g.height + OFFSET_Y;
            const from = isDepending ? self.id : g.id;
            const to = isDepending ? g.id : self.id;
            let l = self.page.createShape("dependency", self.x, self.y + height,from+"|"+to);
            l.fromShape = from;
            l.toShape = to;
            l.fromFitable = d.fitableId;
            l.fontColor = l.borderColor = d.status === undefined ? "gray" : d.status;
            l.borderWidth = d.weight === undefined ? 1 : d.weight;
            l.text = self.page.shapes.find(s => s.fitableId === d.fitableId).text;
            l.follow();
            gs.push(g);
        });
        gs.forEach(g => g.y -= (height - OFFSET_Y) / 2 - self.height / 2);
    }
    self.loadDepending = () => loadingMore(true);
    self.loadDepended = () => loadingMore(false);

    //--------------------------serialization & detection------------------------------
    self.addDetection(["progressStatus"], (property, value, preValue) => {
        self.fontColor = self.borderColor = self.progressStatus.color;
    });
    //---------------------------------------------------------------------------------
    return self;
};

/**
 * 代表一个fitable
 * 辉子 2021
 */
let fitable = (id, x, y, width, height, parent) => {
    let self = rectangle(id, x, y, width, height, parent, rectangleDrawer);
    self.type = "fitable";
    self.namespace = "fit-modeling";
    self.defaultHeight = 30;
    self.text = "new fitable";
    self.pad = 2;
    self.borderWidth = 1;
    self.cornerRadius = 3;
    self.borderColor = "silver";
    self.hAlign = ALIGN.LEFT;
    self.padLeft = 10;
    self.padTop = 8;
    self.moveable = false;
    self.progressStatus = PROGRESS_STATUS.NOTSTARTED;
    self.fontColor = self.progressStatus.color;

    //---------------fitable properties-------------
    self.fitableId = "";
    self.package = "";
    self.language = "Unknown";
    //-----------------------------------------------

    self.connectors.remove(c => c.direction.key !== DIRECTION.W.key && c.direction.key !== DIRECTION.E.key);

    self.connectors.forEach(c => {
        let getDirection = c.getDirection;
        c.getDirection = () => {
            let d = getDirection.apply(c);
            let nd = {cursor: "crosshair"};
            nd.key = d.key;
            nd.color = d.color;
            return nd;
        };
        c.getEnable = c.getVisibility = () => true;
        c.moving = (deltaX, deltaY, x, y) => {
            let e = self.page.createShape("dependency", x, y);
            e.fromShape = self.container;
            e.fromFitable = self.id;
            e.mousedownConnector = e.toConnector;
            e.text = self.text;
            e.fromConnector.getVisibility = () => false;
        };
    });

    let lang = languageRegion(self);
    lang.click = () => console.log("language region is clicked.");
    self.regions.push(lang);

    self.textChanged = (value, preValue) => {
        self.page.shapes.forEach(s => {
            if (s.fromFitable === self.fitableId) {
                s.text = value;
            }
        });
    };

    self.selected = () => {
        self.page.shapes.forEach(s => {
            if (s.fromFitable === self.fitableId) {
                s.isFocused = true;
            }
        });
    };

    self.click = () => {
        console.log("goes into related classes design");
    };

    //--------------------------serialization & detection------------------------------
    self.addDetection(["progressStatus"], (property, value, preValue) => {
        self.fontColor = self.progressStatus.color;
    });
    //---------------------------------------------------------------------------------
    return self;
};

/**
 * fitable与genericable的调用关系
 * 辉子 2021
 */
let dependency = (id, x, y, width, height, parent) => {
    const STEP = 10;

    let self = line(id, x, y, width, height, parent);
    self.type = "dependency";
    self.namespace = "fit-modeling";
    self.fromFitable = "";
    self.borderWidth = 1;
    self.borderColor = "gray";
    self.beginArrow = false;
    self.lineMode = LINEMODE.BROKEN;
    self.allowShine = true;
    self.containerAllowed = parent => parent.id === self.page.id;
    self.validateLinking = s => s.isType('genericable');

    self.hide = () => {
        if (self.globalAlpha === 1) {
            self.globalAlpha = 0.3;
        } else {
            self.globalAlpha = 0;
        }
        self.invalidate();
    };

    self.show = () => {
        self.globalAlpha = 1;
        self.page.moveIndexTop(self);
        self.invalidate();
    };

    self.selected = () => {
        let fitable = self.page.shapes.find(s => s.fitableId === self.fromFitable)
        if (fitable !== undefined) {
            fitable.isFocused = true;
            fitable.invalidate();
        }
    }

    let reset = self.reset;
    self.reset = () => {
        reset.apply(self);
        if (self.fromShape === "" || self.toShape === "") {
            self.remove();
        } else {
            self.toConnector.getVisibility = () => false;
            self.moveable = false;
        }
    };

    // self.serializedFields("fromFitable");
    return self;
};
//-----------------------------------------------------------------------------

//----------------------------------hitregions---------------------------------
/**
 * 类型图标：是genericable还是fitable
 * 辉子 2021
 */
let iconRegion = (item, caption, detail, getx, gety) => {
    let self = hitRegion(item, getx, gety);
    self.type = "fitIcon";
    self.height = 12;

    self.drawStatic = (context, x, y) => {
        self.width = item.width;
        context.beginPath();
        context.strokeStyle = "whitesmoke";
        context.fillStyle = "steelblue";
        context.font = "italic bold 14px Arial";
        context.strokeText(caption, x, y + 10);
        context.fillText(caption, x, y + 10);
        let w = context.measureText(caption).width;
        context.font = "italic bold 9px Arial";
        context.strokeText(detail, x + w, y + 10);
        context.fillText(detail, x + w, y + 10);
    };
    self.drawDynamic = (context, x, y) => {
    };

    return self;
};

/**
 * fitable实现语言图标
 * 辉子 2021
 */
let languageRegion = (item) => {
    let self = hitRegion(item, () => 2, () => -3);

    self.type = "fitIcon";
    self.height = 16;
    let colors = {
        "unknown": "gray", "java": "orange", "go": "green", "c": "purple", "c++": "gray", "python": "steelblue"
    };

    self.drawStatic = (context, x, y) => {
        let caption = item.language.substr(0, 1);
        let detail = item.language.substr(1);
        let color = colors[item.language.toLowerCase()];
        self.width = item.width - 10;
        // let gradient = context.createLinearGradient(x, y, x + self.width + 30, y);;
        // gradient.addColorStop(0, color);
        // gradient.addColorStop(1, "rgba(255,255,255,0");
        // context.beginPath();
        // context.strokeStyle = gradient;
        // context.lineWidth = 2;
        // context.moveTo(x + 12, y + 3);
        // context.lineTo(x + self.width, y + 3);
        //context.stroke();
        context.beginPath();
        context.strokeStyle = "whitesmoke";
        context.fillStyle = color;
        context.font = "italic bold 13px Arial";
        context.strokeText(caption, x, y + 10);
        context.fillText(caption, x, y + 10);
        let w = context.measureText(caption).width;
        context.font = "italic bold 9px Arial";
        context.strokeText(detail, x + w, y + 10);
        context.fillText(detail, x + w, y + 10);
    };
    self.drawDynamic = (context, x, y) => {
    };

    return self;
};

export {fitGraph, fitable, genericable, dependency};