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
const htmlTaskDisplay = (id, x, y, width, height, parent) => {
    const self = labelContainer(id, x, y, width, height, parent, htmlTaskDisplayDrawer);
    self.type = "htmlTaskDisplay";
    self.dockAlign = ALIGN.TOP;
    self.vAlign = ALIGN.MIDDLE;
    self.dockMode = DOCK_MODE.VERTICAL;
    // self.minHeight = 48;
    self.height = 110;
    // self.autoFit = true;
    self.componentId = "task_display_" + self.id;
    self.taskData = {};
    self.serializedFields.batchAdd("jsonData");

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
        result[self.meta[0].key] = JSON.stringify(self.jsonData);
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
        const taskData = data[self.meta[0].key];
        if (taskData) {
            await self.loadData(taskData);
        }
    };

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        self.label = initialize.apply(self);
        self.label.selectable = false;
        self.label.text = "任务列表";
    };

    /**
     * @override
     */
    self.childAllowed = (child) => {
        return child.isTypeof("htmlLabel");
    };

    self.loadData = async (jsonData) => {
        self.jsonData = JSON.parse(jsonData);
    };

    self.addDetection(['jsonData'], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        // self.getLabel().visible = false;
        // self.autoFit = false;
        self.drawer.display(value);
        self.height = self.drawer.getContentHeight();
    });

    /**
     * modeManager中history模式下重写需要用到.
     *
     * @override
     */
    self.getEnableInteract = () => {
        return false;
    };

    return self;
}

const htmlTaskDisplayDrawer = (shape, div, x, y) => {
    const self = containerDrawer(shape, div, x, y);
    self.type = "htmlTaskDisplayDrawer";

    /**
     * @override
     */
    const drawStatic = self.drawStatic;
    self.drawStatic = () => {
        drawStatic.apply(self);
        // 修复提交表单后，文件上传不展示文件名的问题
        if (shape.jsonData) {
            self.display(shape.jsonData);
        }
    };

    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);

        self.taskContainer = document.createElement("div");
        self.taskContainer.className = "aipp-task-display-container";

        self.container.appendChild(self.taskContainer);
        self.taskContainer.style.pointerEvents = shape.getEnableInteract() ? "auto" : "none";
    };

    self.display = (data) => {
        const label = shape.getLabel();
        if (label) {
            self.taskContainer.style.top = shape.getLabel().height + 2 + "px";
        }
        self.taskContainer.innerHTML = "";
        // const taskHeaderDom = document.createElement("div");
        // taskHeaderDom.innerText = "任务列表：";
        // taskHeaderDom.className = "aipp-task-display-header aipp-font-common";

        // self.taskContainer.appendChild(taskHeaderDom);

        let count = 0;
        for (let section of data) {
            const taskContentDom = document.createElement("div");
            taskContentDom.className = "aipp-task-content" + (count + 1);
            self.taskContainer.appendChild(taskContentDom);

            const taskCount = document.createElement("div");
            taskCount.innerText = "任务" + (count + 1);
            taskCount.className = "aipp-task-count aipp-font-common";

            const taskTitle = document.createElement("input");
            taskTitle.value = section.title;
            taskTitle.type = "text";
            taskTitle.className = "aipp-task-title aipp-task-common aipp-font-common";
            taskTitle.addEventListener('input', () => {
                section.title = taskTitle.value
            }, false);

            const taskOwnerTitle = document.createElement("div");
            taskOwnerTitle.innerText = "责任人";
            taskOwnerTitle.className = "aipp-task-owner-title aipp-font-common";

            const taskOwner = document.createElement("input");
            taskOwner.value = section.owner;
            taskOwner.type = "text";
            taskOwner.className = "aipp-task-owner aipp-task-common aipp-font-common";
            taskOwner.addEventListener('input', () => {
                section.owner = taskOwner.value
            }, false);

            const taskOwnerSimple = document.createElement("div");
            taskOwnerSimple.innerText = "当前不支持搜索，请按示例填写。示例：马莉亚 00558975";
            taskOwnerSimple.className = "aipp-task-owner-simple";

            const taskDetailTitle = document.createElement("span");
            taskDetailTitle.innerText = "详情";
            taskDetailTitle.className = "aipp-task-detail-title aipp-font-common";

            const taskDetail = document.createElement("textarea");
            taskDetail.innerText = section.task_detail;
            taskDetail.className = "aipp-task-detail aipp-task-common aipp-font-common";
            taskDetail.addEventListener('input', () => {
                section.task_detail = taskDetail.value
            }, false);

            taskContentDom.appendChild(taskCount);
            taskContentDom.appendChild(taskTitle);
            taskContentDom.appendChild(document.createElement("div"));
            taskContentDom.appendChild(taskOwnerTitle);
            taskContentDom.appendChild(taskOwner);
            taskContentDom.appendChild(taskOwnerSimple);
            taskContentDom.appendChild(document.createElement("div"));
            taskContentDom.appendChild(taskDetailTitle);
            taskContentDom.appendChild(taskDetail);
            count = count + 1;
        }

        const style = document.createElement("style");
        style.innerHTML = `
            .aipp-task-display-container {
                position: absolute;
                width: 95%;
                top: 16px;
                margin-bottom:15px;
            }
            
            .aipp-task-display-header {
                line-height: 22px;
                font-weight: 600;
                margin-bottom:10px;
            }
            
            .aipp-font-common {
                font-family: "微软雅黑, Arial";
                font-size: 12px;
                color:#333;
            }
            
            [class^="aipp-task-content"] {
                padding-top: 8px;
                margin-bottom: 10px;
                border-radius: 5px;
                background-color: #F3F6F9;
            }
            
            [class^="aipp-task-content"] > * {
                margin-bottom: 5px;
            }
            
            .aipp-task-common {
                border-width: 0;
                border-radius: 5px;
            }
            
            .aipp-task-owner-title, .aipp-task-count {
                display: inline-block;
                margin-left: 16px;
                margin-right: 8px;
                width: 72px;
                height: 20px;
                line-height: 20px;
                text-align: left;
            }
            
            .aipp-task-detail-title {
                display: inline-block;
                margin-left: 16px;
                margin-right: 8px;
                width: 72px;
                height: 20px;
                line-height: 20px;
                text-align: left;
                vertical-align: top;
            }
            
            .aipp-task-owner-simple {
                display: inline-block;
                margin-left: 8px;
                margin-right: 8px;
                width: 330px;
                height: 20px;
                line-height: 20px;
                text-align: left;
                font-family: "微软雅黑, Arial";
                font-size: 12px;
                color:#b6b6b6;
            }
            
            .aipp-task-title{
                display: inline-block;
                line-height: 32px;
                width: 75%;
                padding: 0 8px;
                margin-right: 40px;
            }
            
            .aipp-task-owner{
                display: inline-block;
                line-height: 32px;
                padding: 0 8px;
                width: 30%;
            }
            
            .aipp-task-detail {
                display: inline-block;
                line-height: 24px;
                height: 80px;
                width: 75%;
                padding: 5px 8px;
                resize: vertical;
            }
        `;
        self.parent.appendChild(style);
    };

    self.getContentHeight = () => {
        return self.taskContainer.offsetHeight + 32;
    }

    return self;
}

export {htmlTaskDisplay};