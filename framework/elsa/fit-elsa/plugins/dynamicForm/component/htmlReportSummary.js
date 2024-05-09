import {htmlReportTitle} from "./htmlReportTitle.js";

/**
 * 报告概述组件.
 *
 * @override
 */
const htmlReportSummary = (id, x, y, width, height, parent, hDrawer) => {
    const self = htmlReportTitle(id, x, y, width, height, parent, hDrawer);
    self.type = "htmlReportSummary";

    // 适配ucd样式.
    self.fontSize = 14;
    self.fontWeight = 400;
    self.lineHeight = "20px";
    self.fontColor = "rgb(113, 117, 127)";

    /**
     * @override
     */
    self.getData = () => {
        return self.text ? self.getShapeText() : "";
    };

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        if (!data) {
            return;
        }

        if (data && typeof data === 'string') {
            self.text = data.replaceAll("\n", "<br>");
        }
    };

    return self;
};

export {htmlReportSummary};