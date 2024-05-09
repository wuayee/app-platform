import {labelContainer} from "./labelContainer.js";
import {formComponent} from "../form.js";
import {rectangle} from "../../../core/rectangle.js";
import {ALIGN, DOCK_MODE} from "../../../common/const.js";
import {rectangleDrawer} from "../../../core/drawers/rectangleDrawer.js";

/**
 * iframe组件.
 *
 * @override
 */
const htmlIframe = (id, x, y, width, height, parent) => {
    const self = labelContainer(id, x, y, width, height, parent);
    self.type = "htmlIframe";
    self.dockAlign = ALIGN.TOP;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.componentId = "iframe_" + self.id;
    self.autoFit = true;
    self.meta = [{
        key: self.componentId, type: 'string', name: '嵌入框_' + self.id
    }];
    self.serializedFields.add("url");

    /**
     * @override
     */
    self.childAllowed = (s) => {
        return s.isTypeof("htmlLabel") || s.isTypeof("iframe");
    };

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        const url = data[self.meta[0].key];
        if (url) {
            self.url = url;
        }
    };

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        const label = initialize.apply(self);
        label.text = "嵌入框";
    };

    /**
     * 获取iframe对象.
     *
     * @returns {*} iframe对象.
     */
    self.getIFrame = () => {
        return self.getShapes().find(s => s.isTypeof("iframe"));
    };

    // 监听url的变化.
    self.addDetection(["url"], (property, value, preValue) => {
        if (preValue === value) {
            return;
        }
        let iframe = self.getIFrame();
        if (!iframe) {
            iframe = self.page.createShape("iframe", x, y);
            iframe.container = self.id;
            iframe.height = 300;
        }
        iframe.url = value;
        self.getForm().invalidate();
    });

    return self;
};

/**
 * iframe子组件.
 *
 * @override
 */
const iframe = (id, x, y, width, height, parent) => {
    const self = formComponent(rectangle, id, x, y, width, height, parent, iframeDrawer);
    self.type = "iframe";
    self.hideText = true;
    self.borderWidth = 0;
    return self;
};

/**
 * iframe绘制器.
 *
 * @override
 */
const iframeDrawer = (shape, div, x, y) => {
    const self = rectangleDrawer(shape, div, x, y);
    self.iframe = null;

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.iframe = document.createElement("iframe");
        self.parent.appendChild(self.iframe);
    };

    /**
     * @override
     */
    self.drawStatic = () => {
        if (shape.url) {
            self.iframe.src = shape.url;
            self.iframe.width = "100%";
            self.iframe.height = shape.height;
            self.iframe.frameBorder = 0;
            self.iframe.sandbox = "allow-scripts allow-same-origin allow-forms";
        }
        self.parent.style.pointerEvents = "auto";
    };

    return self;
};

export {htmlIframe, iframe};