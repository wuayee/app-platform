import { rectangle } from "../../core/rectangle.js";
import { container } from "../../core/container.js";

const handwritingContainer = (id, x, y, width, height, parent) => {
    const UP_PAD = 40, PAD = 6;
    const self = container(id, x, y, width, height, parent);
    self.itemPad = [PAD, PAD, UP_PAD, PAD];

    const onMouseDown = self.onMouseDown;
    self.onMouseDown = (position) => {
        if (position.x - self.x < PAD || position.y - self.y < PAD || position.x + self.width - self.x < PAD || position.y + self.height - self.y < PAD || position.y - self.y > UP_PAD) {
            onMouseDown.call(self, position);
        } else {
            self.page.mousedownShape = self.page.createNew("customShape", position.x, position.y,undefined,{lineWidth:1,container:self.id});
            // self.page.mousedownShape.container = self.id;
            // self.page.mousedownShape.lineWidth = 1;
        }
    };
    return self;
};

const kanban = (id, x, y, width, height, parent) => {
    const WIDTH = 400, HEIGHT = 300;
    const self = handwritingContainer(id, x, y, width < WIDTH ? WIDTH : width, height < HEIGHT ? HEIGHT : height, parent);
    self.type = "kanban";
    self.minWidth = WIDTH;
    self.minHeight = HEIGHT;
    self.childAllowed = child => child.type === "kanbanLane" || child.type === "customShape";
    return self;
};

const kanbanLane = (id, x, y, width, height, parent) => {
    const self = handwritingContainer(id, x, y, width, height, parent);
    self.type = "kanbanLane";
    self.childAllowed = child => child.type === "kanbanTask" || child.type === "customShape";
    return self;
};

const kanbanTask = (id, x, y, width, height, parent) => {
    const self = rectangle(id, x, y, width, height, parent);
    self.type = "kanbanTask";
    return self;
};

export { kanban, kanbanLane, kanbanTask };