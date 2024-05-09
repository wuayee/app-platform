import {ALIGN, DOCK_MODE} from "../../../common/const.js";
import {labelContainer} from "./labelContainer.js";
import {deleteRegion} from "../regions/deleteRegion.js";
import {containerDrawer} from "../../../core/drawers/containerDrawer.js";

/**
 * 单选框.
 *
 * @override
 */
const htmlRadioBox = (id, x, y, width, height, parent) => {
    const self = labelContainer(id, x, y, width, height, parent, radioBoxDrawer);
    self.type = "htmlRadioBox";
    self.serializedFields.batchAdd("options", "selectedValue");
    self.height = 80;
    self.dockAlign = ALIGN.TOP;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.componentId = "radio_" + self.id;
    self.meta = [{
        key: self.componentId, type: 'string', name: 'radio_' + self.id
    }];
    self.selectedValue = "";
    self.options = [];

    /**
     * @override
     */
    self.createDeleteRegion = () => {
        return deleteRegion(self, undefined, () => 16);
    };

    /**
     * @override
     */
    self.getData = () => {
        const result = {};
        if (self.selectedValue) {
            result[self.meta[0].key] = self.selectedValue;
        }
        return result;
    };

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        const label = initialize.apply(self);
        label.text = "单选框";
    };

    /**
     * 设置选项信息.
     * [{name: "keyWord", value: "科幻"}, {name: "keyWord", value: "魔幻"}]
     *
     * @param options 选项信息.
     */
    self.setOptions = (options) => {
        self.options = options;
        // 如果options里有selectedValue的值，那么就不重置
        if (!options.map(o => o.value).includes(self.selectedValue)) {
            self.selectedValue = options[0].value;
        }
        self.getForm().invalidate();
    };

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        const selectedValue = data[self.meta[0].key];
        if (selectedValue) {
            self.selectedValue = selectedValue;
        }
    };

    /**
     * @override
     */
    self.childAllowed = (child) => {
        return child.isTypeof("htmlLabel") ;
    };

    /**
     * modeManager中history模式下重写需要用到.
     * @override
     */
    self.getEnableInteract = () => {
        return true;
    };

    /**
     * radio选项发生变化时的回调.
     * @param value 当前选中的值.
     */
    self.onChange = (value) => {
        self.selectedValue = value;
    };

    return self;
};

const radioBoxDrawer = (shape, div, x, y) => {
    const self = containerDrawer(shape, div, x, y);
    self.type = "radioBoxDrawer";
    self.prevOptions = [];

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.radioContainer = document.createElement("div");
        self.radioContainer.className = "aipp-radiobox-container";

        self.selectDom = document.createElement("select");
        self.selectDom.className = "aipp-radiobox-select";

        self.selectDom.addEventListener("change", () => {
            shape.onChange(self.selectDom.value);
        });

        drawOptions();
        self.prevOptions = shape.options;

        self.radioContainer.style.pointerEvents = shape.getEnableInteract() ? "auto" : "none";

        self.radioContainer.appendChild(self.selectDom);
        self.parent.appendChild(self.radioContainer);

        const style = document.createElement("style");
        style.innerHTML = `
            .aipp-radiobox-select {
                width: 100%;
                height: 30px;
                border: 1px dashed #ccc;
                border-radius: 5px;
                font-size: 16px;
            }
            
            .aipp-radiobox-container {
                display: flex;
                width: 100%;
                position: absolute;
                top: 40px;
            }
        `;
        self.parent.appendChild(style);
    };

    /**
     * @override
     */
    const drawStatic = self.drawStatic;
    self.drawStatic = () => {
        drawStatic.apply(self);

        // 选项不变就无需重绘
        if (self.prevOptions.equals(shape.options)) {
            return;
        }

        // 清空选项节点
        self.selectDom.innerHTML = '';
        drawOptions();
        self.prevOptions = shape.options;
    };

    const drawOptions = () => {
        // 保持兼容性
        if (!shape.options) {
            shape.options = [];
        }

        shape.options.forEach(op => {
            const option = document.createElement("option");
            option.innerHTML = op.value;
            option.value = op.value;
            option.selected = op.value === shape.selectedValue;
            self.selectDom.appendChild(option);
        });
    };

    return self;
};

export {htmlRadioBox};