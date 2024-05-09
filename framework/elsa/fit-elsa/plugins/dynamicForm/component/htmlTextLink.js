import {deleteRegion} from "../regions/deleteRegion.js";
import {formComponent} from "../form.js";
import {rectangle} from "../../../core/rectangle.js";
import {rectangleDrawer} from "../../../core/drawers/rectangleDrawer.js";
import {ALIGN} from "../../../common/const.js";

/**
 * 文字链接.
 *
 * @override
 */
const htmlTextLink = (id, x, y, width, height, parent) => {
    const self = formComponent(rectangle, id, x, y, width, height, parent, textLinkDrawer);
    self.type = "htmlTextLink";
    self.componentId = "text_link_" + self.id;
    self.serializedFields.batchAdd("href", "linkType");
    self.text = "请输入";
    self.href = "";
    self.linkType = "_blank";
    self.hAlign = ALIGN.LEFT;
    self.meta = [{
        key: self.componentId, type: 'string', name: 'text_link_' + self.id
    }];
    self.deleteRegion = deleteRegion(self);

    /**
     * @override
     */
    self.getData = () => {
        // Todo 点击下一步的时候获取值给下游使用，具体取什么，格式待定
        return {};
    };

    /**
     * modeManager中history模式下重写需要用到.
     * @override
     */
    self.getEnableInteract = () => {
        return false;
    };

    /**
     * 设置跳转链接信息
     *
     * @param href 需要设置的跳转链接信息
     */
    self.setHref = (href) => {
        self.href = href;
        self.invalidate();
    };

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        const textLinkData = data[self.meta[0].key];
        if (textLinkData) {
            // Todo 待处理
            self.getForm().invalidate();
        }
    };

    return self;
};

/**
 * 文字链接绘制器.
 *
 * @override
 */
const textLinkDrawer = (shape, div, x, y) => {
    const self = rectangleDrawer(shape, div, x, y);
    self.type = "textLinkDrawer";
    self.aTagDom = document.createElement("a");
    self.aTagDom.id = "aTag-" + shape.id;
    self.aTagDom.appendChild(self.text);
    self.parent.appendChild(self.aTagDom);

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.aTagDom.className = "textlink-input";
        self.aTagDom.style.pointerEvents = "auto";
        self.aTagDom.style.outline = "none"; // 去除a标签编辑时周围黑框
        self.aTagDom.style.margin = "5px 10px";
        self.aTagDom.style.width = "100%";

        // 监听parent的大小变化，并通知form进行刷新.
        new ResizeObserver(() => {
            if (!self.parent
                || self.parent.clientHeight === 0
                || shape.height === self.parent.clientHeight) {
                return;
            }
            shape.height = self.parent.clientHeight;
            shape.getForm().invalidate();
        }).observe(self.parent);
    };

    /**
     * 去除parent的height style，使其随内部a标签的高度变化而变化.
     *
     * @override
     */
    const parentResize = self.parentResize;
    self.parentResize = (width, height) => {
        parentResize.apply(self, [width, height]);
        self.parent.style.height = null;
    };

    /**
     * text的宽度和父元素宽度保持一致.
     *
     * @override
     */
    const textResize = self.textResize;
    self.textResize = () => {
        textResize.apply(self);
        self.text.style.width = "100%";
    };

    /**
     * @override
     */
    const drawStatic = self.drawStatic;
    self.drawStatic = () => {
        drawStatic.apply(self);

        // 运行态才开启href和target.
        if (shape.getEnableInteract()) {
            self.aTagDom.href = shape.href;
            self.aTagDom.target = shape.linkType;
        }
    };

    return self;
};

export {htmlTextLink};