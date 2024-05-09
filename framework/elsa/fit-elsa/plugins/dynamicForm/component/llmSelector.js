import {formComponent, htmlDiv} from "../form.js";
import {ALIGN, DOCK_MODE} from "../../../common/const.js";
import {rectangle} from "../../../core/rectangle.js";
import {rectangleDrawer} from "../../../core/drawers/rectangleDrawer.js";
import {labelContainer} from "./labelContainer.js";

/**
 * 大数据组件.
 *
 * @param id 唯一标识.
 * @param x 横坐标.
 * @param y 纵坐标.
 * @param width 宽度.
 * @param height 高度.
 * @param parent 父dom元素.
 * @returns {{enhanced}|*}
 */
const llmSelector = (id, x, y, width, height, parent) => {
    const self = labelContainer(id, x, y, width, height, parent);
    self.type = "llmSelector";
    self.dragableByChild = true;
    self.dockAlign = ALIGN.TOP;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.height = 0;
    self.autoFit = true;
    self.componentId = "llmSelector_" + self.id;

    self.meta = [{
        key: self.componentId, type: 'string', name: 'llmSelector_' + self.id
    }];

    /**
     * @override
     */
    self.getData = () => {
        const result = {};
        if (self.metaValue) {
            result[self.meta[0].key] = self.metaValue;
        }
        return result;
    };

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        const label = initialize.apply(self);
        label.selectable = false;
        label.text = "选择模型";
    };

    /**
     * @override
     */
    self.childAllowed = (child) => {
        return child.isTypeof("htmlLabel") || child.isTypeof("htmlLlmSelectorCardContainer");
    };

    /**
     * 获取url.
     *
     * @returns {string} url地址.
     */
    self.getUrl = () => {
        const form = self.getForm();
        return form.protocol + "://" + form.domains.llm + "/v1/platform/model/list?offset=0&size=50";
    };

    /**
     * form加载完成后，获取大模型数据.
     */
    self.formRendered = () => {
        self.page.httpUtil.get(self.getUrl(), (data) => {
            const dataArray = data.data.data;
            const groupMap = new Map();
            dataArray.forEach(d => {
                let grouped = groupMap.get(d.baseName);
                if (!grouped) {
                    grouped = {
                        name: d.baseName, modelTypes: [], models: []
                    };
                    groupMap.set(d.baseName, grouped);
                }
                !grouped.modelTypes.contains(mt => mt === d.modelType) && grouped.modelTypes.push(d.modelType);
                grouped.models.push(d.name);
            });

            self.loadData(Array.from(groupMap.values()));
        });
    };

    /**
     * 数据格式:
     * [{
     *     icon: "",
     *     name: "紫东太初",
     *     modelTypes: ["基础模型", "L0模型"],
     *     models: ["紫东太初-2.0-13B", "紫东太初-2.0-6B"]
     * }]
     *
     * @param data 数据.
     */
    self.loadData = (data) => {
        if (!Array.isArray(data)) {
            throw new Error("Data format invalid.");
        }

        const cardContainer = self.page.createShape("htmlLlmSelectorCardContainer");
        cardContainer.container = self.id;
        cardContainer.selectable = false;
        cardContainer.division = data.length;

        data.forEach(d => {
            const card = self.page.createShape("htmlLlmSelectorCard");
            card.llmSelector = self;
            card.container = cardContainer.id;
            card.configData = d;
            card.selectorKey = self.meta[0].name;
        });

        self.getForm().invalidate();
    };

    return self;
};

/**
 * 卡片容器.
 *
 * @param id 唯一标识.
 * @param x 横坐标.
 * @param y 纵坐标.
 * @param width 宽度.
 * @param height 高度.
 * @param parent 父dom元素.
 * @returns {{enhanced}|*}
 */
const htmlLlmSelectorCardContainer = (id, x, y, width, height, parent) => {
    const self = htmlDiv(id, x, y, width, height, parent);
    self.type = "htmlLlmSelectorCardContainer";
    self.dockAlign = ALIGN.LEFT;
    self.dockMode = DOCK_MODE.HORIZONTAL;
    self.height = 140;

    /**
     * @override
     */
    self.childAllowed = (child) => {
        return child.isTypeof("htmlLlmSelectorCard");
    };

    return self;
};

/**
 * 卡片容器.
 *
 * @param id 唯一标识.
 * @param x 横坐标.
 * @param y 纵坐标.
 * @param width 宽度.
 * @param height 高度.
 * @param parent 父dom元素.
 * @returns {{enhanced}|*}
 */
const htmlLlmSelectorCard = (id, x, y, width, height, parent) => {
    const self = formComponent(rectangle, id, x, y, width, height, parent, htmlLlmSelectorCardDrawer);
    self.type = "htmlLlmSelectorCard";
    self.backColor = "white";
    self.width = 100;
    self.selectable = false;
    self.hideText = true;
    self.serializedFields.add("configData");

    /**
     * 值变化时的回调方法.
     *
     * @param value 变化的值.
     */
    self.onChange = (value) => {
        if (!self.llmSelector) {
            return;
        }
        self.llmSelector.metaValue = value;
    };

    return self;
};

/**
 * 大模型卡片绘制器.
 *
 * @override
 */
const htmlLlmSelectorCardDrawer = (shape, div, x, y) => {
    const self = rectangleDrawer(shape, div, x, y);
    self.inputListeners = new Map();

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.header = document.createElement("div");

        // 标题.
        self.nameDiv = document.createElement("div");
        self.nameDiv.className = "aipp-form-llm-header";
        self.header.appendChild(self.nameDiv);

        // tags
        self.modelTypesDiv = document.createElement("div");
        self.modelTypesDiv.className = "aipp-form-llm-model-type";
        self.modelTypesDiv.style.display = "flex";
        self.header.appendChild(self.modelTypesDiv);

        self.parent.appendChild(self.header);

        // 内容.
        self.modelsDiv = document.createElement("div");
        self.modelsDiv.className = "aipp-form-llm-models"
        self.parent.appendChild(self.modelsDiv);

        const style = document.createElement("style");
        style.innerHTML = `
            .aipp-form-llm-header {
                font-weight: 600;
                margin: 10px;
                font-size: 20px;
            }
            
            .aipp-form-llm-model-type span {
                margin-left: 10px;
                padding-left: 10px;
                padding-right: 10px;
                background: #EBF6FF;
                font-size: 14px;
            }
           
            .aipp-form-llm-models {
                pointer-events: none;
                margin: 10px;
                position: absolute;
                bottom: 0px;
                padding: 0px;
                display: inline-flex;
                margin-right: 1rem;
                z-index: 1;
                min-height: 1.5rem;
            }
            
            .aipp-form-llm-radio-input {
                box-sizing: border-box;
                z-index: -1;
                width: 1rem;
                height: 1.25rem;
            }
           
            .aipp-form-llm-radio-label {
                position: relative;
                margin-bottom: 0;
                vertical-align: top;
                top: 2px;
                margin-right: 20px;
            }
            
            .aipp-form-radio-label::after {
                position: absolute;
                top: 0.25rem;
                left: -1.5rem;
                display: block;
                width: 1rem;
                height: 1rem;
                content: "";
                background: 50%/50% 50% no-repeat;
            }
        `;
        self.parent.appendChild(style);
    };

    /**
     * @override
     */
    const drawStatic = self.drawStatic;
    self.drawStatic = (x, y) => {
        drawStatic.apply(self, [x, y]);
        if (!shape.configData) {
            return;
        }

        self.parent.style.display = "";
        const configData = shape.configData;
        if (configData.name) {
            self.nameDiv.innerHTML = "";
            const span = document.createElement("span");
            span.innerHTML = configData.name;
            self.nameDiv.appendChild(span);
        }

        if (configData.modelTypes && configData.modelTypes.length > 0) {
            self.modelTypesDiv.innerHTML = "";
            configData.modelTypes.forEach(modelType => {
                const span = document.createElement("span");
                span.innerHTML = modelType;
                self.modelTypesDiv.appendChild(span);
            });
        }

        if (configData.models && configData.models.length > 0) {
            self.modelsDiv.innerHTML = "";
            self.inputListeners.forEach((value, key) => key.removeEventListener("change", value));
            self.inputListeners.clear();
            configData.models.forEach(model => {
                const input = createInputByModel(model, shape.selectorKey);
                const label = document.createElement("label");
                label.className = "aipp-form-llm-radio-label";
                input.className = "aipp-form-llm-radio-input";
                label.for = model;
                label.innerHTML = model;
                self.modelsDiv.appendChild(input);
                self.modelsDiv.appendChild(label);
            });
        }

        // 判断是否能进行交互.
        self.modelsDiv.style.pointerEvents = shape.getEnableInteract() ? "auto" : "none";
    };

    const createInputByModel = (model, name) => {
        const input = document.createElement("input");
        input.type = "radio";
        input.id = model;
        input.name = name;
        input.value = model;

        const handler = () => shape.onChange(input.value);
        input.addEventListener("change", handler);
        self.inputListeners.set(input, handler);
        return input;
    };

    return self;
};

export {llmSelector, htmlLlmSelectorCardContainer, htmlLlmSelectorCard};