import {hitRegion} from "../../../core/hitRegion.js";
import {reportEditIcon} from "../icons/icons.js";

/**
 * 编辑region，用于使图形处于可编辑状态.
 *
 */
export const reportEditRegion = (shape, getX = undefined, getY = undefined) => {
    const self = hitRegion(shape, getX, getY, () => 20, () => 20);
    self.type = "deleteRegion";
    self.disableCanvas = true;
    self.div = null;

    /**
     * @override
     */
    self.draw = () => {
        self.x = self.getx(shape, self);
        self.y = self.gety(shape, self);

        const width = self.getWidth(shape, self);
        const height = self.getHeight(shape, self);

        if (!self.div) {
            self.div = document.createElement("div");
            self.div.id = "edit-region:" + shape.id;
            self.div.style.zIndex = shape.index + 1 + (self.index ? self.index : 1);
            self.div.style.width = width + "px";
            self.div.style.height = height + "px";
            self.div.style.position = "absolute";
            shape.drawer.parent.appendChild(self.div);
        }
        self.div.style.left = self.x + "px";
        self.div.style.top = self.y + "px";
        self.div.innerHTML = reportEditIcon;
    };

    /**
     * 重写click方法.点击删除图形.
     *
     * @override
     */
    self.click = () => {
        shape.page.triggerEvent({type: "report_editable", value: {page: shape.page.id}});
    };

    return self;
};