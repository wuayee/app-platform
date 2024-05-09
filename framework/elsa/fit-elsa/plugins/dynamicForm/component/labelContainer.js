import {htmlDiv} from "../form.js";
import {deleteRegion} from "../regions/deleteRegion.js";

/**
 * label容器.
 *
 * @override
 */
const labelContainer = (id, x, y, width, height, parent, drawer) => {
    const self = htmlDiv(id, x, y, width, height, parent, drawer);
    self.type = "labelContainer";

    /**
     * 初始化.
     */
    self.initialize = () => {
        const label = self.page.createLabel(x, y, self);
        label.container = self.id;

        // 适配ucd样式.
        label.fontColor = label.mouseInFontColor = "#333333";
        label.fontWeight = 400;
        label.fontSize = 16;
        label.autoHeight = true;
        return label;
    };

    /**
     * @override
     */
    const created = self.created;
    self.created = () => {
        created.apply(self);
        self.deleteRegion = self.createDeleteRegion();
    };

    /**
     * 创建deleteRegion.
     */
    self.createDeleteRegion = () => {
        return deleteRegion(self);
    };

    /**
     * 获取标签对象.
     *
     * @returns {*} 标签对象.
     */
    self.getLabel = () => {
        let label = self.getShapes().find(s => s.type === "htmlLabel");
        if (!label) {
            throw new Error("label not exist in the htmlInput.");
        }
        return label;
    };

    /**
     * 选中之后，开始编辑.
     *
     * @override
     */
    const selected = self.selected;
    self.selected = () => {
        selected.apply(self);
        self.getLabel().beginEdit();
    };

    /**
     * 取消选中，结束编辑.
     *
     * @override
     */
    const unselected = self.unselected;
    self.unselected = () => {
        unselected.apply(self);
        self.getLabel().endEdit();
    };

    return self;
};

export {labelContainer};