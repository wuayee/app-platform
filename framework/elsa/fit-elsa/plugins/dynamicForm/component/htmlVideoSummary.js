import {ALIGN, DOCK_MODE} from "../../../common/const.js";
import {labelContainer} from "./labelContainer.js";
import {containerDrawer} from "../../../core/drawers/containerDrawer.js";

/**
 * 脑图组件.
 *
 * @param id 唯一标识.
 * @param x 横坐标.
 * @param y 纵坐标.
 * @param width 宽度.
 * @param height 高度.
 * @param parent 父dom元素.
 * @returns {{enhanced}|*}
 */
const htmlVideoSummary = (id, x, y, width, height, parent) => {
    const self = labelContainer(id, x, y, width, height, parent, htmlVideoSummaryDrawer);
    self.type = "htmlVideoSummary";
    self.dockAlign = ALIGN.TOP;
    self.vAlign = ALIGN.MIDDLE;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.minHeight = 48;
    self.autoFit = true;
    self.componentId = "video_summary_" + self.id;
    self.summaryData = {};

    self.meta = [{
        key: self.componentId, type: 'string', name: 'video_summary_' + self.id
    }];

    /**
     * 获取数据.
     *
     * @returns {{}} 数据.
     */
    self.getData = () => {
        let result = {};
        result[self.meta[0].key] = self.summaryData;
        return result;
    };

    /**
     * 接收数据并设置.
     *
     * @override
     */
    self.formDataRetrieved = async (shapeStore, data) => {
        if (!data) {
            return;
        }
        const summaryData = data[self.meta[0].key];
        if (summaryData) {
            await self.loadData(summaryData);
        }
    };

    /**
     * @override
     */
    self.initialize = () => {};

    /**
     * @override
     */
    self.childAllowed = (child) => {
        return child.isTypeof("htmlLabel");
    };

    self.loadData = async (jsonData) => {
        self.jsonData = JSON.parse(jsonData);
        self.getForm().invalidate();
    }

    self.addDetection(['jsonData'], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        self.autoFit = false;
        self.drawer.display(value);
        self.height = self.drawer.getContentHeight();
    });

    return self;
}

const htmlVideoSummaryDrawer = (shape, div, x, y) => {
    const self = containerDrawer(shape, div, x, y);
    self.type = "htmlVideoSummaryDrawer";

    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);

        self.summaryContainer = document.createElement("div");
        self.summaryContainer.className = "aipp-video-summary-container";

        self.parent.appendChild(self.summaryContainer);
    };

    self.display = (data) => {
        const titleDom = document.createElement("div");
        titleDom.innerText = "视频摘要：";
        titleDom.className = "aipp-video-summary-title";

        const summaryDom = document.createElement("div");
        summaryDom.innerText = data["summary"];
        summaryDom.className = "aipp-video-summary-content";

        const keyPointHeaderDom = document.createElement("div");
        keyPointHeaderDom.innerText = "关键点：";
        keyPointHeaderDom.className = "aipp-video-keypoint-header";

        const keyPointContentDom = document.createElement("div");
        keyPointContentDom.className = "aipp-video-keypoint-content";

        self.summaryContainer.appendChild(titleDom);
        self.summaryContainer.appendChild(summaryDom);
        self.summaryContainer.appendChild(keyPointHeaderDom);
        self.summaryContainer.appendChild(keyPointContentDom);

        for (let section of data['sectionList']) {
            const keyPointPosDom = document.createElement("div");
            keyPointPosDom.innerText = section.position;
            keyPointPosDom.className = "aipp-video-keypoint-position";

            const keyPointTitleDom = document.createElement("div");
            keyPointTitleDom.innerText = section.title;
            keyPointTitleDom.className = "aipp-video-keypoint-title";

            const keyPointTextDom = document.createElement("div");
            keyPointTextDom.innerText = section.text;
            keyPointTextDom.className = "aipp-video-keypoint-text";

            keyPointContentDom.appendChild(keyPointPosDom);
            keyPointContentDom.appendChild(keyPointTitleDom);
            keyPointContentDom.appendChild(keyPointTextDom);
        }

        const style = document.createElement("style");
        style.innerHTML = `
            .aipp-video-summary-container {
                position: absolute;
                top: 0px;
                left: 0px;
            }
            
            .aipp-video-summary-title, .aipp-video-keypoint-header {
                line-height: 22px;
                font-size: 16px;
                font-weight: 600;
            }
            
            .aipp-video-summary-content {
                line-height: 22px;
                font-size: 16px;
                font-weight: 400;
                margin-top: 8px;
                margin-bottom: 16px;
            }
            
            .aipp-video-keypoint-content {
                padding-top: 8px;
            }
            
            .aipp-video-keypoint-position {
                background-color: #F2F2F3;
                display: inline-block;
                margin-right: 16px;
                width: 72px;
                height: 20px;
                border-radius: 40px;
                line-height: 20px;
                font-size: 14px;
                text-align: center;
            }
            
            .aipp-video-keypoint-title {
                display: inline-block;
                line-height: 22px;
                font-size: 16px;
            }
            
            .aipp-video-keypoint-text {
                padding-left: 88px;
                line-height: 20px;
                font-size: 14px;
                color: #71757F;
            }
        `;
        self.parent.appendChild(style);
    };

    self.getContentHeight = () => {
        return self.summaryContainer.offsetHeight + 32;
    }

    return self;
}

export {htmlVideoSummary};