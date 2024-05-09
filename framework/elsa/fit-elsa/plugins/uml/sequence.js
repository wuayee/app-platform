/*
时序图：程序员们都懂
辉子 2020-04-15
*/
let sequence = (id, x, y, width, height, parent) => {
    let self = container(id, x, y, width, height, parent, undefined, me => {
        //默认加三列
        let actor = me.page.createShape("actor", 0, 0);
        actor.container = me.id;
        actor.role = ACTOR_ROLE.ACTOR;
        actor.text = "Actor";
        me.page.createShape("actor", 0, 0).container = me.id;
        me.page.createShape("actor", 0, 0).container = me.id;
    });
    self.type = "sequence";
    self.namespace = "uml";
    self.height = 400;
    self.width = 500;
    self.dockMode = DOCK_MODE.HORIZONTAL;
    self.dockAlign = ALIGN.LEFT;
    self.backAlpha = 1;
    self.editable = false;
    self.methodHeight = 30;
    self.blockWidth = 8;
    self.itemPad = [10, 5, 30, 5];
    //self.itemSpace = 20;
    self.text = "sequence diagram";
    self.methodPad = 70;//从这个位置开始可以画method
    self.mouseIndex = -1;//当前鼠标在哪个method位置

    self.childAllowed = child => child.isType('actor') || child.isType('method');

    let arrangeShapes = self.arrangeShapes;
    self.arrangeShapes = function () {
        arrangeShapes.apply(self);
        self.getShapes().filter(c => c.isType('method') && !c.inProcess).orderBy("sequence").forEach((m, index) => {
            m.sequence = index;
            if (m.inProcess) {
                return;
            }//正在画的method不管
            if (self.mouseIndex !== -1 && m.sequence >= self.mouseIndex) {
                m.sequence++;
            }
        });
    }

    return self;
};
/*
时序图里的一列：可以是用户角色，类，对象
辉子 2020-04-15
*/
let actor = (id, x, y, width, height, parent) => {
    let self = rectangle(id, x, y, width, height, parent, canvasRectangleDrawer);
    self.type = "actor";
    self.namespace = "uml";
    self.moveable = false;
    self.headHeight = 30;
    self.width = 150;
    self.pad = 0;
    self.margin = 0;
    self.text = "Some Object";
    self.hAlign = ALIGN.MIDDLE;
    self.vAlign = ALIGN.TOP;
    self.role = ACTOR_ROLE.OBJECT;
    self.borderWidth = 0;

    let initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.apply(self);
        self.connectors.remove(c => c.type !== "right");
        self.rotateConnector = null;

        self.dynamicConnector = connector(self, s => s.width / 2, s => s.getContainer().mouseIndex * s.getContainer().methodHeight + s.getContainer().methodPad, () => DIRECTION.T, s => s.getContainer().mouseIndex >= 0, () => true, () => true, () => true);

        self.dynamicConnector.moving = (deltaX, deltaY, x, y) => {
            let method = self.page.createShape("method", self.x + self.width / 2, self.y + self.dynamicConnector.y);
            method.container = self.getContainer().id;
            // method.index += 20; 1
            method.fromShape = self.id;
            method.sequence = self.getContainer().mouseIndex;
            method.inProcess = true;
            method.mousedownConnector = method.connectors.find(c => c.isType('to'));
        };

    };

    self.keyPressed = function (e) {
        if (!(e.ctrlKey || e.metaKey)) {
            return true;
        }
        if (e.code === "KeyI") {
            let newActor = self.page.createShape("actor", self.x + self.width, self.y);
            newActor.container = self.container;
            self.getContainer().invalidate();
            return false;
        }
        if (e.code === "KeyM") {
            switch (self.role) {
                case ACTOR_ROLE.OBJECT:
                    self.role = ACTOR_ROLE.ACTOR;
                    self.text = "Actor";
                    break;
                case ACTOR_ROLE.CLASS:
                    self.role = ACTOR_ROLE.OBJECT;
                    self.text = "Some Object";
                    break;
                default:
                    self.role = ACTOR_ROLE.CLASS;
                    self.text = ":Some Class";
                    break;
            }
            self.invalidate();
            return false;
        }
    };

    self.drawer.drawStatic = (context, x, y) => {
        let headOffset = -20;
        self.drawer.text.style.paddingTop = self.role + "px";
        context.beginPath();
        context.rect(x, y, self.width, self.height);
        context.fill();
        if (self.role === ACTOR_ROLE.ACTOR) {//画角色
            context.beginPath();
            context.strokeStyle = self.getBorderColor();
            context.arc(x + self.width / 2, y + 9, 5, 0, 2 * Math.PI);
            context.moveTo(x + self.width / 2, y + 14);
            context.lineTo(x + self.width / 2, y + 23);
            context.lineTo(x + self.width / 2 + 8, y + 30);
            context.moveTo(x + self.width / 2, y + 23);
            context.lineTo(x + self.width / 2 - 8, y + 30);
            context.moveTo(x + self.width / 2 - 8, y + 19);
            context.lineTo(x + self.width / 2 + 8, y + 19);
            context.stroke();
        }

        //draw downward dash line
        context.dashedLineTo(x + self.width / 2, y + self.get("pad") + self.headHeight - headOffset, x + self.width / 2, y + self.height, 5, self.get("borderWidth"), self.getBorderColor());

        if (self == self.getContainer().getShapes()[0]) {
            return;
        }
        //draw block
        let methods = self.getContainer().getShapes().filter(i => i.isType('method') && !i.inProcess).orderBy("sequence");// && (i.fromShape === self.id || i.toShape === self.id)
        let blockY1 = 0, blockY2 = 0, blockHead = 15, newBlock = false;
        let myActor = self;
        let mySeq = self.getContainer();
        let drawBlock = (myActor, mySeq, blockY1, blockY2) => {
            context.beginPath();
            context.rect(x + myActor.width / 2 - mySeq.blockWidth / 2, y + blockY1 - myActor.y, mySeq.blockWidth, blockY2 - blockY1);
            context.fillStyle = "whitesmoke";
            context.fill();
            context.stroke();
        };
        methods.forEach((m, index) => {
            if (m.fromShape !== self.id && m.toShape !== self.id) {
                return;
            }
            let isNewBlock = () => {
                if (m.toShape === self.id && m.width > 0) {
                    return true;
                }
                if (index === 0) {
                    return false;
                }
                if (m.fromShape === self.id && methods[index - 1].fromShape !== self.id && methods[index - 1].toShape !== self.id) {
                    return true;
                }
                return false;
            };
            if (isNewBlock()) {//发现开始时序
                if (newBlock) {//发现上一个时序结束
                    drawBlock(myActor, mySeq, blockY1, blockY2);
                } else {
                    newBlock = true;
                }
                blockY1 = m.y - 2;//-blockHead;
            }
            blockY2 = m.y + blockHead;
        });
        if (newBlock) {
            drawBlock(myActor, mySeq, blockY1, blockY2);
        }
    };

    self.click = function () {
    };
    let mouseMoveOut = self.mouseMoveOut;
    self.mouseMoveOut = function (e) {
        let self = this;
        if (self.container === "") {
            return;
        }
        self.unSelect();
        mouseMoveOut.apply(self, [e]);
        self.container.mouseIndex = -1;
        self.container.invalidate(true);
    };

    let onMouseMove = self.onMouseMove;
    self.onMouseMove = position => {
        onMouseMove.apply(self, [position]);

        let cx = self.x + self.width / 2;
        if (Math.abs(position.x - cx) > 10 || !self.isFocused) {
            return;
        }

        let container = self.getContainer();
        // if (container.selectedActor !== undefined) container.selectedActor.unSelect();
        // self.select();
        // container.selectedActor = self;
        container.mouseIndex = Math.round((position.y - self.y - container.methodPad) / container.methodHeight);
        self.dynamicConnector.refresh();
        self.getContainer().invalidate();
    };

    let remove = self.remove;
    self.remove = source => {
        if (remove.apply(self, [source])) {
            self.getContainer().getShapes().filter(s => s.fromShape === self.id || s.toShape === self.id).forEach(c => c.remove(source));
        }
    }

    //--------------------------serialization & detection------------------------------
    self.addDetection(["width"], (property, value, preValue) => {
        self.getContainer().invalidate();
    });
    //---------------------------------------------------------------------------------
    return self;
};
/*
时序图里的线，其实就是一个方法
辉子 2020-04-15
*/
let method = (id, x, y, width, height, parent) => {
    let self = line(id, x, y, width, height, parent);
    self.type = "method";
    self.namespace = "uml";
    self.margin = 30;
    self.fontSize = 10;
    self.text = "新方法";
    self.beginArrow = false;
    self.borderWidth = 1;
    self.resize(1, 1);

    let initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.apply(self);
        let container = self.getContainer();
        self.fromConnector.moving = (deltaX, deltaY, x, y) => {
            self.inProcess = true;
            self.x += deltaX;
            self.resize(self.width - deltaX, 0);
            let from = self.page.find(x, y, s => s !== self);
            if (from.isType('actor')) {
                self.fromShape = from.id;
            } else {
                self.fromShape = "";
            }
        };
        self.toConnector.moving = (deltaX, deltaY, x, y) => {
            self.inProcess = true;
            self.resize(self.width + deltaX, 0);
            let to = self.page.find(x, y, s => s !== self);
            if (to.isType('actor') || to.isType('sequence')) {
                self.toShape = to.id;
            } else {
                self.toShape = ""
            }
        };
        self.toConnector.release = function () {
            if (self.toShape === "") {
                self.remove();
            } else {
                self.inProcess = false;
                container.mouseIndex = -1;
                //self.dashWidth = self.width > 0 ? 0 : 4;
                if (self.getToShape().isType('sequence')) {
                    let newActor = self.page.createShape("actor", 0, 0);
                    newActor.container = container.id;
                    self.page.moveIndexBefore(newActor, container.getShapes().filter(s => s.isType('actor') && s !== newActor).max(s => s.getIndex()) + 1);
                    self.toShape = newActor.id;
                }
                if (self.fromShape === self.toShape) {
                    self.textConnector.move(20, 0);
                }
                container.invalidate(true);
            }
        };
        let refresh = self.endpoint.refresh;
        self.endpoint.refresh = () => {
            if (self.fromShape === self.toShape) {
                self.endpoint.x = self.endpoint.getX(shape, self);
                self.endpoint.y = self.endpoint.getY(shape, self);
            } else {
                refresh.apply(self.endpoint);
            }
        };
    };

    self.follow = function () {
        if (self.inProcess) {
            return;
        }
        let fromShape = self.getFromShape(), toShape = self.getToShape(), container = self.getContainer();
        if (self.container === "") {
            return;
        }
        if (self.fromShape === "" || self.fromShape === undefined || self.toShape === "" || self.toShape === undefined) {
            return;
        }

        if (self.fromShape === self.toShape && self.fromShape !== "" && !self.inProcess) {
            const WIDTH = 10;
            self.lineMode = LINEMODE.BROKEN;
            self.resize(0, WIDTH);
            self.moveTo(fromShape.x + fromShape.width / 2 + container.blockWidth / 2, self.y);
            self.arrowBeginPoint = {x: WIDTH, y: 0};
            self.arrowEndPoint = {x: WIDTH, y: WIDTH};
            self.brokenPoint1 = {x: 2 * WIDTH, y: 0};
            self.brokenPoint2 = {x: 2 * WIDTH, y: WIDTH};
            self.manageConnectors();

        } else {
            if (self.width > 0) {
                self.dashWidth = 0;
                self.moveTo(fromShape.x + fromShape.width / 2 + container.blockWidth / 2, self.y);
                self.resize(toShape.x + toShape.width / 2 - (fromShape.x + fromShape.width / 2) - container.blockWidth, self.height);
            } else {
                self.dashWidth = 4;
                self.moveTo(fromShape.x + fromShape.width / 2 - container.blockWidth / 2, self.y);
                self.resize(toShape.x + toShape.width / 2 - (fromShape.x + fromShape.width / 2) + container.blockWidth, self.height);
            }
        }
    };

    self.keyPressed = e => {
        if (!(e.ctrlKey || e.metaKey)) {
            return true;
        }
        if (e.code === "KeyN") {
            self.newBlock = true;
            self.getContainer().invalidate(true);
            return false;
        }
        if (e.code === "KeyB") {
            self.newBlock = false;
            self.getContainer().invalidate(true);
            return false;
        }
        return (e.code.indexOf("Backspace") >= 0 || e.code.indexOf("Delete") == 0);
    };

    let invalidate = self.invalidate;
    self.invalidate = function () {
        if (self.sequence === undefined) {
            return;
        }
        let container = self.getContainer();
        let oldy = self.y, newy = self.getFromShape().y + container.methodPad + self.sequence * container.methodHeight;
        if (newy !== oldy) {
            self.y = newy;
            self.getContainer().invalidate();
        } else {
            invalidate.apply(self);
        }
        self.drawer.text.innerHTML = (self.sequence + 1) + ":" + self.text;
    };
    return self;
};

const ACTOR_ROLE = {
    CLASS: 8, OBJECT: 10, ACTOR: 30
};
