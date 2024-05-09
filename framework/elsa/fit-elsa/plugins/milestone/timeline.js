import {ALIGN, DOCK_MODE, FONT_WEIGHT} from '../../common/const.js';
import {container} from '../../core/container.js';
import {canvasContainerDrawer} from '../../core/drawers/containerDrawer.js';

/**
 * timeline容器
 * milestone在timeline里
 * 一个timeline里有多个milestone
 * 辉子 2020-05
 **/
let timeline = (id, x, y, width, height, parent) => {
    let self = container(id, x, y, width, height, parent, canvasContainerDrawer);
    self.type = "timeline";
    self.namespace = "timeline";
    self.hAlign = ALIGN.LEFT;
    self.width = 600;
    self.height = 240;
    self.dashWidth = 5;
    self.cornerRadius = 8;
    self.fontSize = 10;
    self.fontColor = "#ECD0A7";
    self.borderColor = "lightgray";
    self.backColor = "rgba(255,255,255,0.2)";
    self.mode = "standard";
    self.mainColor = "#ECD0A7";
    self.text = "Elsa Timeline";
    self.dockMode = DOCK_MODE.HORIZONTAL;
    self.dockAlign = ALIGN.LEFT;
    self.ifMaskItems = false;
    self.enableAnimation = true;

    self.colors = ["steelblue", "darkorange", "OLIVE", "teal", "darkred", "green", "#EAC117", "red", "gray",
        "lightblue", "#CD7F32", "steelblue"];

    let patterns = {};
    let x0 = 0, r0 = 5;
    patterns["standard"] = {
        drawStatic: (context, x, y) => {
            let x1 = x, y1 = y + self.height / 2, h1 = 2, h2 = 1, arrowWidth = 15;
            context.beginPath();
            context.moveTo(x1, y1 - h1);
            context.lineTo(x1 + self.width - arrowWidth, y1 - h2);
            context.lineTo(x1 + self.width - 2 * arrowWidth, y1 - 2 * h1);
            context.lineTo(x1 + self.width, y1);
            context.lineTo(x1 + self.width - 2 * arrowWidth, y1 + 2 * h1);
            context.lineTo(x1 + self.width - arrowWidth, y1 + h2);
            context.lineTo(x1, y1 + h1);
            context.closePath();
            context.fillStyle = self.mainColor;
            context.fill();
        }, drawDynamic: (context, x, y) => {
            context.beginPath();
            context.rect(x + x0 - self.width / 2, y - r0 / 2, r0, r0);
            context.fillStyle = "rgba(255,255,255,0.5)";
            context.fill();
            x0 += 3;
            r0 -= 0.01;
            if (r0 < 2) {
                r0 = 2;
            }
            if (x0 > self.width) {
                x0 = 0;
                r0 = 5;
            }
        }
    };

    self.childAllowed = c => c.isType('milestone');

    let invalidate = self.invalidate;
    self.invalidate = () => {
        self.getShapes().filter(i => i.isType('milestone')).orderBy(i => i.getIndex()).forEach((i, idx) => {
            i.vAlign = idx % 2 === 0 ? ALIGN.TOP : ALIGN.BOTTOM;
            i.fontColor = self.colors[idx % 12];
        });
        invalidate.apply(self);
    };
    let drawStatic = self.drawer.drawStatic;
    self.drawer.drawStatic = (context, x, y) => {
        drawStatic.apply(self.drawer, [context, x, y]);
        patterns[self.mode].drawStatic(context, x, y);
    };

    let drawDynamic = self.drawer.drawDynamic;
    self.drawer.drawDynamic = (context, x, y) => {
        drawDynamic.apply(self.drawer, [context, x, y]);
        patterns[self.mode].drawDynamic(context, x, y);
    };

    self.addMilestone = function (pre) {
        let ms = self.page.createShape("milestone", x, y);
        ms.container = self.id;
        if (pre !== undefined) {
            self.page.moveIndexAfter(ms, pre.getIndex());
        }
        self.invalidate();
    };

    let keyPressed = self.keyPressed;
    self.keyPressed = e => {
        if ((e.ctrlKey || e.metaKey) && e.code === "KeyI") {
            self.addMilestone();
            return;
        }

        return keyPressed.apply(self, [e]);
    };

    //self.priorityRegion.gety = self.infoTypeRegion.gety = self.progressRegion.gety = self.progressStatusRegion.gety = () => 0;
    self.initialize = args => {
        self.addMilestone();
        self.addMilestone();
        self.addMilestone();
        self.addMilestone();
    };
    return self;
};

/**
 * 某个里程碑
 * 放在timeline里
 * 辉子 2021
 */
let milestone = (id, x, y, width, height, parent) => {
    const HEAD_HEIGHT = 32;
    let self = container(id, x, y, width, height, parent, canvasContainerDrawer);
    self.type = "milestone";
    self.namespace = "timeline";
    self.ifMaskItems = false;
    self.width = 120;
    self.height = 60;
    self.borderWidth = 0;
    self.fontSize = 15;
    self.fontWeight = FONT_WEIGHT.BOLD;
    self.hAlign = ALIGN.LEFT;
    self.vAlign = ALIGN.TOP;
    self.pad = 5;
    self.time = new Date().getUTCFullYear().toString();
    self.emphasized = false;
    self.ongoing = false;
    self.text = "";
    self.editable = false;
    self.caption = "new phase";
    self.childAllowed = c => c.type !== "milestone";
    let manageConnectors = self.manageConnectors;
    self.manageConnectors = () => {
        self.connectors.remove(c => c.type !== "right");
        manageConnectors.apply(self);
    }

    let time = self.page.createShape("rectangle", self.x + 1, self.y + 1);
    time.text = self.time;
    time.height = 25;
    time.fontSize = 12;
    time.rotateAble = time.deletable = time.moveable = false;

    let caption = self.page.createShape("rectangle", self.x + 1, self.y + 1);
    caption.text = self.caption;
    caption.height = 20;
    caption.fontSize = 10;
    caption.rotateAble = caption.deletable = caption.moveable = false;

    time.width = caption.width = self.width - 2;

    let content = self.page.createShape("rectangle", self.x + 1, self.y + 1);
    content.autoHeight = true;
    content.rotateAble = content.deletable = content.moveable = false;
    content.fontSize = 9;
    content.fontWeight = FONT_WEIGHT.LIGHTER;

    content.fontColor = "gray";
    content.height = self.height - HEAD_HEIGHT;
    content.text = "something is going to be done in this phase";

    time.pad = caption.pad = content.pad = 2;
    time.container = caption.container = content.container = self.id;

    self.drawSomethingOn = direction => {
        let left = direction === "L" ? 0 : self.width;
        let context = self.drawer.canvas.getContext("2d");
        context.strokeStyle = "orange";
        context.beginPath();
        context.moveTo(left, 0);
        context.lineTo(left, self.height);
        context.stroke();
    };

    let drawStatic = self.drawer.drawStatic;
    self.drawer.drawStatic = (context, x, y) => {
        self.backColor = self.isFocused ? "orange" : "white";
        drawStatic.apply(self.drawer, [context, x, y]);
        time.borderWidth = caption.borderWidth = content.borderWidth = 0;
        time.backColor = caption.backColor = content.backColor = "transparent";
        time.hAlign = caption.hAlign = content.hAlign = ALIGN.LEFT;
        time.resizeable = caption.resizeable = content.resizeable = false;
        // caption.x = content.x = self.x + 8;
        // caption.width = content.width = self.width - 9;
        caption.resize(self.width - 9, caption.height);
        content.resize(self.width - 9, content.height);
        time.fontColor = caption.fontColor = self.fontColor;
        // time.x = self.x + 13;
        // time.width = self.width - 20;
        // time.height = 18;
        // time.y = self.y + self.height / 2 - 9;

        time.moveTo(self.x + 13, self.y + self.height / 2 - 9);
        time.resize(self.width - 20, 18);

        const x1 = self.x + 8;
        if (self.get("vAlign") === ALIGN.TOP) {
            // caption.y = self.y + 27 - caption.fontSize;
            // content.y = self.y + 35;
            caption.moveTo(x1, self.y + 27 - caption.fontSize);
            content.moveTo(x1, self.y + 35);
        } else {
            // caption.y = self.y + 2 * self.height / 3 - caption.fontSize - 5;
            // content.y = self.y + 2 * self.height / 3 + 8;
            caption.moveTo(x1, self.y + 2 * self.height / 3 - caption.fontSize - 5);
            content.moveTo(x1, self.y + 2 * self.height / 3 + 8);
        }

        let x1 = x + 6;
        let y1 = self.get("vAlign") === ALIGN.TOP ? y + HEAD_HEIGHT : y + self.height * 2 / 3;// - HEAD_HEIGHT;
        let g1 = context.createLinearGradient(x1, y1, x + self.width, y1);
        g1.addColorStop(0, self.fontColor);
        g1.addColorStop(1, "rgba(255,255,255,0)");

        context.beginPath();
        context.moveTo(x1, y1);
        context.lineTo(x1 + self.width, y1);
        context.strokeStyle = g1;
        context.stroke();

        let y2 = self.get("vAlign") === ALIGN.TOP ? y + HEAD_HEIGHT - 10 : y + self.height - 10;
        let g2 = context.createLinearGradient(x1, y2, x1, y + self.height / 2);
        g2.addColorStop(0, "rgba(255,255,255,0)");
        g2.addColorStop(0.5, self.fontColor);
        g2.addColorStop(1, self.fontColor);

        context.strokeStyle = g2;
        context.beginPath();
        context.moveTo(x1, y2);
        context.lineTo(x1, y + self.height / 2);
        context.stroke();

        context.strokeStyle = self.fontColor;
        context.fillStyle = "white";
        context.lineWidth = 1;
        context.beginPath();
        context.arc(x1, y + self.height / 2, 5, 0, 2 * Math.PI);
        context.fill();
        context.stroke();

        context.fillStyle = self.fontColor;
        context.beginPath();
        context.arc(x1, y + self.height / 2, 3, 0, 2 * Math.PI);
        context.fill();

    };

    let getDraggingTarget = target => {
        let closest = {x: 0};
        if (target.type === self.type && target !== self) {
            closest = target;
        }
        if (target.isType('timeline')) {
            let items = target.getShapes();
            if (items.length === 0) {
                return;
            }
            items.filter(i => i !== self && i.x <= self.x).forEach(i => {
                if (self.x - i.x < self.x - closest.x) {
                    closest = i;
                }
            });

        }
        if (closest !== self) {
            if (self.x - closest.x < closest.width / 2) {
                return {
                    target: closest, direction: "L", run: () => {
                        self.page.moveIndexBefore(self, closest.getIndex())
                    }
                };
            } else {
                return {
                    target: closest, direction: "R", run: () => {
                        self.page.moveIndexAfter(self, closest.getIndex())
                    }
                };
            }
        }
        return {
            target: null, direction: "L", run: () => {
            }
        };
    };

    let lastDrggingTarget = null;
    self.dragging = target => {
        if (lastDrggingTarget !== null) {
            lastDrggingTarget.invalidate();
        }
        let t = getDraggingTarget(target);
        if (t === null) {
            return;
        }
        t.target.invalidate();
        t.target.drawSomethingOn(t.direction);
        lastDrggingTarget = t.target;
    };

    let endDrag = self.endDrag;
    self.endDrag = target => {
        endDrag.call(self, target);
        getDraggingTarget(target).run();
        self.getContainer().invalidate();
    };

    self.priorityRegion.gety = self.infoTypeRegion.gety = self.progressRegion.gety = self.progressStatusRegion.gety = () => self.vAlign === ALIGN.TOP ? 1 : self.height - 20;

    let keyPressed = self.keyPressed;
    self.keyPressed = function (e) {
        if ((e.ctrlKey || e.metaKey) && e.code === "KeyI" && self.getContainer().isType('timeline')) {
            self.getContainer().addMilestone(self);
            return;
        }
        return keyPressed.apply(self, [e]);
    };

    //--------------------------serialization & detection------------------------------
    self.addDetection(["width"], (property, value, preValue) => {
        self.getContainer().invalidate();
    });
    //---------------------------------------------------------------------------------
    return self;
};

export {timeline, milestone};