import {formComponent} from "../form.js";
import {rectangle} from "../../../core/rectangle.js";
import {rectangleDrawer} from "../../../core/drawers/rectangleDrawer.js";
import {ALIGN} from "../../../common/const.js";
import {deleteRegion} from "../regions/deleteRegion.js";

/**
 * 文本展示组件.
 *
 * @override
 */
const htmlTextDisplay = (id, x, y, width, height, parent) => {
    const self = formComponent(rectangle, id, x, y, width, height, parent, htmlTextDisplayDrawer);
    self.type = "htmlTextDisplay";
    self.autoHeight = true;
    self.componentId = "radio_" + self.id;
    self.meta = [{
        key: self.componentId, type: 'string', name: 'text_display_' + self.id
    }];

    self.backColor = "#ffffff";
    self.borderColor = "#d9dadb";
    self.focusBackColor = "#f7faff";
    self.focusBorderColor = "#047bfc";
    self.dashWidth = 5;
    self.hAlign = ALIGN.LEFT;
    self.fontFace = "微软雅黑,arial";
    self.fontWeight = 400;
    self.fontColor = "rgb(37, 43, 58)";
    self.mouseInFontColor = "rgb(37, 43, 58)";

    self.text = "文本展示";

    self.deleteRegion = deleteRegion(self);

    /**
     * 只是为了能modeManager中能进行重写.
     *
     * @override
     */
    self.getBorderWidth = () => {
        return self.borderWidth;
    };

    // 不需要编辑事件.
    self.beginEdit = () => {};
    self.endEdit = () => {};

    self.removeDetection("text");
    self.addDetection(["text"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        self.drawer && self.drawer.renderText();
        self.getForm().invalidate();
    });

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        const text = data[self.meta[0].key];
        text && (self.text = text);
    };

    /**
     * 只是为了能modeManager中能进行forbid.
     *
     * @override
     */
    self.enableTextPointerEvents = () => {
        self.drawer.enableTextPointerEvents();
    };

    return self;
};

/**
 * 文本展示组件绘制器.
 *
 * @override
 */
const htmlTextDisplayDrawer = (shape, div, x, y) => {
    const self = rectangleDrawer(shape, div, x, y);
    self.type = "htmlTextDisplayDrawer";

    /**
     * @override
     */
    self.textInitialize = () => {
        shape.text !== "" && self.renderText();
    };

    /**
     * @override
     */
    self.renderText = () => {
        if (shape.hideText) {
            return;
        }
        self.text.innerHTML = shape.text;
    };

    /**
     * 不影响其他图形，这里暂时全部拷贝resize方法.
     *
     * @override
     */
    self.resize = () => {
        self.textResize();
        const width = self.calculateWidth(shape);
        shape.autoHeight && (shape.height = self.text.offsetHeight + 2 * shape.borderWidth + 1);
        const height = Math.abs(shape.height);
        self.backgroundRefresh();
        self.parentResize(width, height);
        self.animationResize(width, height);
        if (shape.width < 0 || shape.height < 0) {
            self.move();
        }
        return {width, height};
    };

    /**
     * @override
     */
    const textResize = self.textResize;
    self.textResize = () => {
        textResize.apply(self);
        self.text.style.overflowWrap = "break-word";
        self.text.style.whiteSpace = "pre-wrap";
        shape.enableTextPointerEvents();
    };

    /**
     * @override
     */
    self.renderTextByEditor = () => {};

    return self;
};

export {htmlTextDisplay};

