import {ALIGN, DIRECTION, DOCK_MODE} from '../../common/const.js';
import {container} from '../../core/container.js';
import {rectangle} from '../../core/rectangle.js';
import {event} from '../flowable/nodes/event.js';
import {canvasContainerDrawer} from '../../core/drawers/containerDrawer.js';
import {canvasDrawer} from '../../core/drawers/canvasDrawer.js';
import {expanded} from './hitregion.js';

/*
package:包图
package里面可以放各种类，接口
是个容器
辉子 2020-04-14
*/
let umlPackage = (id, x, y, width, height, parent) => {
    let self = container(id, x, y, width, height, parent, canvasContainerDrawer);
    self.type = "umlPackage";
    self.namespace = "uml";
    self.hAlign = ALIGN.RIGHT;
    self.text = "package name";
    self.pad = 2;
    self.borderWidth = 0;

    self.drawer.drawStatic = (context, x, y) => {
        let itemPad = self.get("itemPad");
        context.beginPath();
        context.rect(x + 1, y + 1, self.width - 2, self.height - 2);
        context.fillStyle = self.backColor;
        context.globalAlpha = self.get("backAlpha");
        context.fill();

        context.beginPath();
        context.globalAlpha = self.get("globalAlpha");
        context.moveTo(x + 1, y + itemPad[2] - 2);
        context.lineTo(x + 1, y + 1);
        context.lineTo(x + Math.ceil(self.width / 3), y + 1);
        context.lineTo(x + Math.ceil(self.width / 3), y + itemPad[2] - 2);
        context.rect(x + 1, y + itemPad[2] - 2, self.width - itemPad[0] - itemPad[1] - self.get("borderWidth"), self.height - itemPad[2] - itemPad[3] + 3 - self.get("borderWidth"));
        context.stroke();
    };
    let reset = self.reset;
    self.reset = () => {
        reset.apply(self);
        if (self.width < 150) {
            self.width = 1.5 * self.height;
        }
    };
    return self;
};
/*
接口
容器：里面可以放方法和属性
辉子 2020-04-14
*/
let umlInterface = (id, x, y, width, height, parent) => {
    let self = container(id, x, y, width, height, parent, canvasContainerDrawer);
    self.type = "umlInterface";
    self.namespace = "uml";
    self.height = 100;
    self.width = 110;
    self.hAlign = ALIGN.LEFT;
    self.text = "interface name";
    self.dockMode = DOCK_MODE.VERTICAL;
    self.dockAlign = ALIGN.TOP;
    self.itemSpace = 0;
    self.isExpanded = true;//true：展开状态；false：收缩状态
    self.itemPad = [6, 6, 25, 6];
    self.dashWidth = 6;
    self.hideText = false;
    self.headColor = "transparent";

    self.childAllowed = child => child.isType('field');

    let initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.apply(self);
        self.connectors.forEach(c => {
            if (c.direction !== DIRECTION.N && c.direction !== DIRECTION.S) {
                return;
            }
            var shapeName = c.direction === DIRECTION.N ? "referLine" : "inherit";
            let getDirection = c.getDirection;
            c.getDirection = () => {
                let d = getDirection.apply(c);
                let nd = {};
                for (let f in d) nd[f] = d[f];
                nd.cursor = "crosshair";
                return nd;
            };
            c.moving = (deltaX, deltaY, x, y) => {
                let e = self.page.createShape(shapeName, x, y);
                e.resize(1, 1);
                e.container = self.container;
                e.fromShape = self.id;
                e.mousedownConnector = e.toConnector;
                self.page.mousedownShape = e;
            }
        });

    };

    let arrangeShapes = self.arrangeShapes;
    self.arrangeShapes = () => {
        arrangeShapes.apply(self);
        let height = self.getShapes().filter(c => c.visible).sum(s => s.height + self.itemSpace);
        height += 30;
        self.resize(self.width, height);
    };

    self.drawer.drawStatic = (context, x, y) => {
        context.strokeStyle = self.getBorderColor();
        context.beginPath();
        context.lineWidth = 2;
        context.moveTo(x + 1, y + self.itemPad[2]);
        context.lineTo(x + self.width - 1, y + self.itemPad[2]);
        context.stroke();
    };

    expanded(self, shape => shape.width - 15, () => 6);

    self.keyPressed = function (e) {
        if (!(e.ctrlKey || e.metaKey)) {
            return true;
        }
        if (e.code === "KeyI") {
            let f = self.page.createShape("field", 0, 0);
            f.container = self.id;
            self.invalidate();
            return false;
        }
    };

    //--------------------------serialization & detection------------------------------
    // self.serializedFields.batchAdd("isExpanded");

    self.addDetection(["isExpanded"], (property, value, preValue) => {
        self.getShapes().forEach(s => s.visible = self.isExpanded);
    });
    //----------------------------------------------------------------------------------
    return self;
};

/*
类：继承自接口
辉子 2020-04-14
*/
let clazz = (id, x, y, width, height, parent) => {
    var self = umlInterface(id, x, y, width, height, parent);
    self.text = "class name";
    self.type = "clazz";
    self.namespace = "uml";
    self.dashWidth = undefined;

    return self;
};
/*
方法或者属性
辉子 2020-04-14
*/
let field = (id, x, y, width, height, parent) => {
    let self = rectangle(id, x, y, width, height, parent, canvasDrawer);
    self.type = "field";
    self.namespace = "uml";
    self.height = 20;
    self.borderWidth = 0;
    self.resizeAble = false;
    self.moveAble = false;
    self.text = "some field";
    self.margin = 0;
    self.autoHeight = false;
    self.hAlign = ALIGN.LEFT;
    self.backColor = "red";
    self.padLeft = 15;
    self.keyPressed = e => self.getContainer().keyPressed(e);
    self.initConnectors = () => {
        self.connectors = [];
        self.rotateConnector = null;
    };
    self.drawer.drawStatic = (context, x, y) => {
        context.beginPath();
        context.fillStyle = "rgba(255,255,255,0.02";
        context.rect(x, y, self.width, self.height);
        context.fill();
        context.beginPath();
        context.arc(x + 5, y + 14, 4, 0, 2 * Math.PI);
        context.strokeStyle = self.getBorderColor();
        context.stroke();
        if (self.text.indexOf(")") === self.text.length - 1 && self.text.indexOf("(") > 0) {
            context.fillStyle = self.getBorderColor();
            context.fill();
        }
    };

    let onMouseUp = self.onMouseUp;
    self.onMouseUp = position => {
        onMouseUp.apply(self, [position]);
        if (self.getContainer().namespace === self.namespace) {
            self.getContainer().invalidate();
        }
    };
    self.addDetection(["container"], (property, value, preValue) => {
        let previous = self.page.shapes.find(s => s.id == preValue);
        if (previous !== undefined && previous.namespace === self.namespace) {
            previous.invalidate();
        }
        let current = self.page.shapes.find(s => s.id == value);
        if (current !== undefined && current.namespace === self.namespace) {
            current.invalidate();
        }
    });
    return self;
};
/*
引用关系，从接口和类的上中connector拉出
辉子 2020-04-14
*/
let referLine = (id, x, y, width, height, parent) => {
    let self = event(id, x, y, width, height, parent);
    self.type = "referLine";
    self.namespace = "uml";
    self.autoGenerateShape = "interface";
    self.endArrowEmpty = false;
    self.endArrow = true;
    self.beginArrow = false;
    self.text = "";
    self.allowShine = false;

    // self.getContainer = () => {
    //     if (self.page !== undefined) self.container = self.page.id;
    //     return self.page;
    // }

    return self;
};
/*
继承，实现关系，从接口和类的下中connector拉出
辉子 2020-04-14
*/
let inherit = (id, x, y, width, height, parent) => {
    let self = referLine(id, x, y, width, height, parent);
    self.type = "inherit";
    self.beginArrow = true;
    self.beginArrowEmpty = true;
    self.endArrow = false;
    self.endArrowEmpty = false;
    self.beginArrowSize = 5;
    self.autoGenerateShape = "clazz";
    return self;
};

export {umlPackage, umlInterface, clazz, field, referLine, inherit};