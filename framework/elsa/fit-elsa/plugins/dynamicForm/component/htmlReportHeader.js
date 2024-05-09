import {htmlLabel} from "../form.js";
import {ALIGN} from "../../../common/const.js";
import {rectangleDrawer} from "../../../core/drawers/rectangleDrawer.js";

/**
 * 报告顶层标题组件.
 *
 * @override
 */
const htmlReportHeader = (id, x, y, width, height, parent, hDrawer = htmlReportHeaderDrawer) => {
    const self = htmlLabel(id, x, y, width, height, parent, hDrawer);
    self.type = "htmlReportHeader";
    self.autoHeight = true;
    self.placeholder = "请输入标题...";

    // 适配ucd样式.
    self.hAlign = ALIGN.MIDDLE;
    self.textAlign = ALIGN.MIDDLE;
    self.fontSize = 28;
    self.fontWeight = 600;
    self.lineHeight = "30px";
    self.fontColor = "rgb(37, 43, 58)";

    /**
     * 拷贝删除，新增文本等情况时导致text高度发生变化，需要刷新form表单的高度.
     */
    self.textChanged = () => {
        if (self.drawer.text.offsetHeight === self.height) {
            return;
        }
        self.getForm().invalidate();
    };

    return self;
};

/**
 * 报告顶层标题绘制器.
 *
 * @override
 */
const htmlReportHeaderDrawer = (shape, div, x, y) => {
    const self = rectangleDrawer(shape, div, x, y);
    self.type = "reportHeaderDrawer";

    /**
     * text的宽度改为父元素宽度的98%，考虑给padding留出空间.
     *
     * @override
     */
    const textResize = self.textResize;
    self.textResize = () => {
        textResize.apply(self);
        self.text.style.width = "98%";
    };

    return self;
};

export {htmlReportHeader};