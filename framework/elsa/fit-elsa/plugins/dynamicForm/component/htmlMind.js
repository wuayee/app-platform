import {ALIGN, DOCK_MODE} from "../../../common/const.js";
import {uuid} from "../../../common/util.js";
import {labelContainer} from "./labelContainer.js";
import {mindIcon} from "../icons/icons.js";
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
const htmlMind = (id, x, y, width, height, parent) => {
    const self = labelContainer(id, x, y, width, height, parent, htmlMindDrawer);
    self.type = "htmlMind";
    self.dockAlign = ALIGN.TOP;
    self.vAlign = ALIGN.MIDDLE;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.minHeight = 48;
    self.autoFit = true;
    self.componentId = "input_" + self.id;
    self.mouseInBorderColor = "#d9dadb";

    self.meta = [{
        key : self.componentId,
        type : 'string',
        name : 'mind_json_' + self.id
    }];

    self.getData = () => {
        let result = {};
        result[self.meta[0].key] = self.jsonData;
        return result;
    }

    self.formDataRetrieved = async (shapeStore, data) => {
        if (!data) {
            return;
        }
        const mindData = data[self.meta[0].key];
        if (mindData) {
            self.drawer.svgContainer.style.visibility = "hidden";
            await self.loadData(data[self.meta[0].key]);
        }
    };

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        const label = initialize.apply(self);
        label.selectable = false;
        label.text = "脑图";
        label.visible = false;
    };

    /**
     * @override
     */
    self.childAllowed = (child) => {
        return child.isTypeof("htmlLabel") || child.isTypeof("mind");
    };

    self.loadData = async (jsonData) => {
        async function buildSubtopics(root, parentTopic, children) {
            if (!children || children.length === 0) {
                return;
            }
            for (const c of children) {
                 let subTopic = await root.createTopic("subTopic", c.name);
                subTopic.parent = parentTopic.id;
                await buildSubtopics(root, subTopic, c.children);
            }
        }
        self.jsonData = jsonData;
        let mindData = typeof jsonData === "string" ? JSON.parse(jsonData) : jsonData;
        if (!mindData) {
            console.log("mind data wrong!")
            return;
        }

        const mind = self.page.createNew("mind", x, y, uuid(), {text: mindData.name});
        mind.container = self.id;

        let topic = mind.getShapes().find(s => s.isType('topic'));
        await buildSubtopics(topic, topic, mindData.children);

        self.getForm().invalidate();
        topic.place();
        mind.invalidate();
    }

    return self;
}

const htmlMindDrawer = (shape, div, x, y) => {
    const self = containerDrawer(shape, div, x, y);
    self.type = "mindDrawer";

    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);

        self.svgContainer = document.createElement("div");
        self.parent.appendChild(self.svgContainer);
        const svgDom = document.createElement("div");
        svgDom.innerHTML = mindIcon;
        svgDom.style.width = "100%";
        svgDom.style.height = "100%";
        self.svgContainer.appendChild(svgDom);
    };

    return self;
}

export {htmlMind};