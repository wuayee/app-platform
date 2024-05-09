import {freeLine} from "../../core/svg.js";
import {group} from "../../core/group.js";

const whiteBoardFreeLine = (id, x, y, width, height, parent) => {
    let self = freeLine(id, x, y, width, height, parent);
    self.type = 'whiteBoardFreeLine';
    let containerAllowed = self.containerAllowed;
    self.containerAllowed = container => {
        return containerAllowed.apply(self, [container]) || container.isTypeof('freeLineSelection');
    };
    return self;
}

const freeSelectionLine = (id, x, y, width, height, parent) => {
    let self = freeLine(id, x, y, width, height, parent);
    self.type = 'freeSelectionLine';
    self.serializable = false;
    self.dasharray = '10';
    self.fill = 'orange';
    self.fillOpacity = '0.5';
    return self;
}

const whiteBoardGroup = (id, x, y, width, height, parent) => {
    let self = group(id, x, y, width, height, parent);
    self.type = 'whiteBoardGroup';
    self.groupBorder = 0;
    self.dynamicAddItem = false;
    self.borderWidth = 2;
    self.borderColor = '#0A59F766';
    self.addDetection(["borderColor", "focusBorderColor"], (property, value, preValue) => {
        self.getShapes().forEach(shape => {
            shape[property] = value;
        })
    });

    function showBorder() {
        let focused = self.isFocused;
        if (focused) {
            return true;
        }
        let container = self.getContainer();
        return container.isFocused && container.isTypeof("freeLineSelection");
    }

    self.drawer.drawBorder = () => {
        if (showBorder()) {
            self.drawer.parent.style.border = "1px solid ";
            self.drawer.parent.style.borderColor = self.borderColor;
        } else {
            self.drawer.parent.style.border = "0px";
        }
    }
    return self;
}


const freeLineSelection = (id, x, y, width, height, parent) => {
    let self = group(id, x, y, width, height, parent);
    self.type = 'freeLineSelection';
    self.groupBorder = 0;
    self.backColor = 'RGBA(255,255,255,0.1)';
    self.addDetection(["isFocused"], (property, value, preValue) => {
        if (!value) {
            self.break(false);
        }
    });
    self.addDetection(["borderColor", "focusBorderColor"], (property, value, preValue) => {
        self.getShapes().forEach(shape => {
            shape[property] = value;
        })
    });
    return self;
}

export {whiteBoardFreeLine, freeSelectionLine, freeLineSelection, whiteBoardGroup};