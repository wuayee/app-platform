import {htmlText, htmlTextDrawer} from "../form.js";
import {ALIGN} from "../../../common/const.js";

/**
 * 报告标题组件.
 *
 * @override
 */
const htmlReportTitle = (id, x, y, width, height, parent, hDrawer = htmlReportTitleDrawer) => {
    const self = htmlText(id, x, y, width, height, parent, hDrawer);
    self.type = "htmlReportTitle";
    self.serializedFields.add("data");
    self.autoHeight = true;
    self.placeholder = "请输入标题...";

    // 适配ucd样式.
    self.hAlign = ALIGN.MIDDLE;
    self.textAlign = ALIGN.LEFT;
    self.fontSize = 16;
    self.fontWeight = 600;
    self.lineHeight = "22px";
    self.fontColor = "rgb(37, 43, 58)";
    self.focusFontColor = "rgb(37, 43, 58)";

    /**
     * @override
     */
    self.getData = () => {
        const ans = {...self.data};
        ans.query = self.text ? self.getShapeText() : "";
        return ans;
    };

    /**
     * @override
     */
    const beginEdit = self.beginEdit;
    self.beginEdit = (x, y, autoFocus = false) => {
        beginEdit.apply(self, [x, y, autoFocus]);
        self.drawer.text.style.border = "1px solid rgb(215, 216, 218)";
        self.drawer.text.style.borderRadius = "4px";
    };

    const endEdit = self.endEdit;
    self.endEdit = () => {
        endEdit.apply(self);
        self.drawer.text.style.border = "none";
    };

    /**
     * 报表标题，需要在点击编辑之后才进入编辑态.
     *
     * @override
     */
    self.formLoaded = () => {
    };

    /**
     * 报表标题，需要在点击编辑之后才进入编辑态.
     *
     * @override
     */
    self.click = () => {};

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        if (!data) {
            return;
        }
        self.data = data;
        const text = data.query;
        if (text && typeof text === 'string') {
            self.text = text.replaceAll("\n", "<br>");
        }
    };

    /*-- 注册事件处理 --*/
    self.addPageEventListener("report_editable", (data) => {
        // 只接受当前页面的事件.
        if (self.page.id !== data.page) {
            return;
        }
        self.beginEdit(0, 0);
    });

    self.addPageEventListener("report_readonly", (data) => {
        // 只接受当前页面的事件.
        if (self.page.id !== data.page) {
            return;
        }
        self.endEdit();
    });

    return self;
};

/**
 * 文本标题绘制器.
 *
 * @override
 */
const htmlReportTitleDrawer = (shape, div, x, y) => {
    const self = htmlTextDrawer(shape, div, x, y);
    self.type = "reportTitleDrawer";

    self.text.addEventListener("mouseover", () => {
        self.text.style.borderColor = "rgb(4, 123, 252)";
    });

    self.text.addEventListener("mouseleave", () => {
        self.text.style.borderColor = "rgb(215, 216, 218)";
    });

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

export {htmlReportTitle};