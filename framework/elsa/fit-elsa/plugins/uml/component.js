/*
component:组件图
是个容器
辉子 2020-04-27
*/
let component = (id, x, y, width, height, parent) => {
    let self = container(id, x, y, width, height, parent);
    self.type = "component";
    self.namespace = "uml";
    self.hAlign = ALIGN.LEFT;
    self.text = "component name";
    self.width = 150;
    self.height = 100;
    self.pad = 5;
    self.borderWidth = 1;
    self.ifMaskItems = false;
    self.keyPressed = e => {
        if (e.code === "Enter") {
            let int = self.page.createShape("interface_component", self.x, self.y + self.height / 2);
            int.moveTo(self.x, self.y + self.height / 2);
            int.container = self.id;
            self.invalidate();
        }
    };
    packageRegion(self, shape => shape.width - 30, () => 5);
    return self;
};

let interface_component = (x, y, width, height, parent) => {
    const WIDTH = 6, HEIGHT = 7;
    const WEST = {
        resize: shape => {
            shape.width = 2 * WIDTH;
            shape.height = 2 * HEIGHT;
        }, getx: () => -20, gety: () => self.height / 2 - 1, draw: (context, shape, x, y) => {
            context.strokeStyle = shape.getBorderColor();
            context.beginPath();
            context.arc(x - 12, y + 6, 5, 0, 2 * Math.PI);
            context.moveTo(x - 7, y + 6);
            context.lineTo(x, y + 6);
            context.stroke();
            if (shape.isConnected()) {
                context.beginPath();
                context.arc(x - 12, y + 6, 8, 0.5 * Math.PI, 1.5 * Math.PI);
                context.stroke();
            }
            shape.connector.getHitRegion = () => {
                return {x: shape.connector.x, y: shape.connector.y - 5, width: 20, height: 10};
            };
            shape.connector.direction = {cursor: "default", key: "W"};
        }
    };
    const EAST = {
        resize: shape => {
            shape.width = 2 * WIDTH;
            shape.height = 2 * HEIGHT;
        }, getx: () => self.width + 20, gety: () => self.height / 2, draw: (context, shape, x, y) => {
            context.strokeStyle = shape.getBorderColor();
            context.beginPath();
            context.arc(x + 21, y + 6, 5, 0, 2 * Math.PI);
            context.moveTo(x + 10, y + 6);
            context.lineTo(x + 17, y + 6);
            context.stroke();
            if (shape.isConnected()) {
                context.beginPath();
                context.arc(x + 21, y + 6, 8, 1.5 * Math.PI, 0.5 * Math.PI);
                context.stroke();
            }
            shape.connector.getHitRegion = () => {
                return {x: shape.connector.x - 20, y: shape.connector.y - 5, width: 20, height: 10};
            };
            shape.connector.direction = {cursor: "default", key: "E"};
        }
    };
    const NORTH = {
        resize: shape => {
            shape.width = 2 * HEIGHT;
            shape.height = 2 * WIDTH;
        }, getx: () => self.width / 2 + 1, gety: () => -20, draw: (context, shape, x, y) => {
            context.strokeStyle = shape.getBorderColor();
            context.beginPath();
            context.arc(x + 6, y - 12, 5, 0, 2 * Math.PI);
            context.moveTo(x + 6, y - 7);
            context.lineTo(x + 6, y);
            context.stroke();
            if (shape.isConnected()) {
                context.beginPath();
                context.arc(x + 6, y - 12, 8, Math.PI, 2 * Math.PI);
                context.stroke();
            }
            shape.connector.getHitRegion = () => {
                return {x: shape.connector.x - 5, y: shape.connector.y, width: 10, height: 20};
            };
            shape.connector.direction = {cursor: "default", key: "N"};
        }
    };
    const SOUTH = {
        resize: shape => {
            shape.width = 2 * HEIGHT;
            shape.height = 2 * WIDTH;
        }, getx: () => self.width / 2, gety: () => self.height + 20, draw: (context, shape, x, y) => {
            context.strokeStyle = shape.getBorderColor();
            context.beginPath();
            context.arc(x + 6, y + 21, 5, 0, 2 * Math.PI);
            context.moveTo(x + 6, y + 10);
            context.lineTo(x + 6, y + 17);
            context.stroke();
            if (shape.isConnected()) {
                context.beginPath();
                context.arc(x + 6, y + 21, 8, 0, Math.PI);
                context.stroke();
            }
            shape.connector.getHitRegion = () => {
                return {x: shape.connector.x - 5, y: shape.connector.y - 20, width: 10, height: 20};
            };
            shape.connector.direction = {cursor: "default", key: "S"};
        }
    };
    let self = rectangle(undefined, x, y, 2 * WIDTH, 2 * HEIGHT, parent, canvasRectangleDrawer);
    self.type = "interface_component";
    self.text = "";
    self.namespace = "uml";
    self.margin = 25;
    self.borderWidth = 1;
    self.cornerRadius = 0;
    self.direction = WEST;

    self.isConnected = () => self.page.shapes.contains(s => s.toShape === self.id);
    self.manageConnectors = () => {
        self.connectors = [];
        self.connector = connector(self, shape => shape.direction.getx(), shape => shape.direction.gety(), () => DIRECTION.NONE, () => true, () => true, () => true);
        self.connector.draw = () => {
        };
        self.connector.refresh();
        self.connectors.push(self.connector);
    };
    self.containerAllowed = parent => parent.isType('component');
    let moveTo = self.moveTo;
    self.moveTo = (x, y, followContainer) => {
        moveTo.apply(self, [x, y, followContainer]);
        let container = self.getContainer();
        if (container.isType('component')) {
            let w = self.x - container.x,
                e = container.x + container.width - self.x - 2 * WIDTH,
                n = self.y - container.y,
                s = container.y + container.height - self.y - 2 * WIDTH;
            let min = Math.min(w, e, n, s);
            let x1 = self.x, y1 = self.y;
            if (min === w) {
                x1 = container.x - WIDTH;
                self.direction = WEST;
            }
            if (min === e) {
                x1 = container.x + container.width - WIDTH;
                self.direction = EAST;
            }
            if (min === n) {
                y1 = container.y - WIDTH;
                self.direction = NORTH;
            }
            if (min === s) {
                y1 = container.y + container.height - WIDTH;
                self.direction = SOUTH;
            }
            self.moveTo(x1, y1);
            self.direction.resize(self);
        }
    };

    let invalidate = self.invalidate;
    self.invalidate = () => {
        let container = self.getContainer();
        self.borderColor = container.borderColor;
        self.backColor = container.backColor;
        self.backAlpha = 1;
        invalidate.apply(self);
    };
    let drawStatic = self.drawer.drawStatic;
    self.drawer.drawStatic = (context, x, y) => {
        drawStatic.apply(self.drawer, [context, x, y]);
        self.direction.draw(context, self, x, y);
    }
    return self;
};
