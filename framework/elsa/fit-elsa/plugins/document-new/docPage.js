import {page} from '../../core/page.js';
import {EVENT_TYPE, PAGE_MODE, Z_INDEX_OFFSET} from "../../common/const.js";
import {docInteractDrawer} from "./drawer/docInteractDrawer.js";
import {enhanceDraggable} from "./enhancer/draggableEnhancer.js";
import {enhanceEntanglement} from "./enhancer/entanglementEnhancer.js";
import {enhanceFocused} from "./enhancer/focusedEnhancer.js";
import {enhanceDrawer} from "./enhancer/drawerEnhancer.js";
import {SHAPE_IN_DOCUMENT_MODE} from "./common/const.js";
import {enhanceKeyAction} from "./enhancer/keyActionEnhancer.js";
import {deleteCommand} from "../../core/commands.js";

// todo@zhangyue 若以后发现用户浏览器无法充满，则需要修改该逻辑，看如何更好的实现.
const DEFAULT_HEIGHT = 1200;

/**
 * 承载文档相关的page.
 *
 * @author z00559346 张越.
 */
export const docPage = (div, graph, name, id) => {
    const self = page(div, graph, name, id);
    div && (div.style.overflow = "visible");
    self.type = "docPage";
    self.namespace = "document";
    self.backColor = "#edebe9";
    self.mouseWheelAble = true;

    self.interactDrawer = docInteractDrawer(graph, self, div);

    /**
     * {@link #docPage} 初始化时，需要创建 {@link #docFrame}.
     *
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        self.createNew("docFrame", 0, 0);
        initialize.call(self);
    };

    /**
     * 获取 {@link #docFrame} 对象.
     *
     * @return {T} {@link #docFrame} 对象.
     */
    self.getFrame = () => self.shapes.find(s => s.isType('docFrame'));

    /**
     * 重写onload方法，当page中只存在一个docSection时，默认将其状态设置为focused编辑状态.
     *
     * @override
     */
    const onLoaded = self.onLoaded;
    self.onLoaded = () => {
        onLoaded.apply(self);
        const topSection = getTopSection();
        if (topSection) {
            topSection.beginEdit(0, 0, true);

            // 忽略disableReact.
            const ignoreDisableReact = (action) => {
                const disableReact = self.disableReact;
                self.disableReact = false;
                try {
                    action();
                } finally {
                    self.disableReact = disableReact;
                }
            }

            topSection.onHeightChange(height => {
                self.resize(self.width, height + 20);
                ignoreDisableReact(() => self.getFrame().resize(self.div.clientWidth, height + 20));
            });

            // 加载完成后，需要触发一次该事件.
            self.triggerEvent({type: EVENT_TYPE.FOCUSED_SHAPE_CHANGE, value: [topSection]});
        }
    };

    const getTopSection = () => {
        if (self.topSection) {
            return self.topSection;
        }
        const topDocSections = self.shapes.filter(s => s.isTypeof("docSection") && s.isTopSection());
        return topDocSections.length > 0 ? topDocSections[0] : null;
    }

    /**
     * 填充屏幕.
     *
     * @override
     */
    self.fillScreen = () => {
        self.drawer.reset();
        self.interactDrawer.reset();
    }

    /**
     * @inheritDoc
     * @override
     */
    const resize = self.resize;
    self.resize = (width, height) => {
        resize.apply(self);
        self.div.style.height = height + "px";
    }

    /**
     * 在take之后，需要进行一次fillScreen的操作，适配屏幕大小.
     *
     * @inheritDoc
     * @override
     */
    const take = self.take;
    self.take = async (data) => {
        await take.call(self, data);
        self.fillScreenInDocPage();
    }

    self.fillScreenInDocPage = () => {
        self.fillScreen();
    }

    /**
     * 当没有focusedShapes时，返回顶层 {@link #docSection} 对象.
     *
     * @override
     */
    const getFocusedShapes = self.getFocusedShapes;
    self.getFocusedShapes = () => {
        const focusedShapes = getFocusedShapes.apply(self);
        const topSection = getTopSection();
        if (focusedShapes.length === 0 && topSection) {
            return [topSection];
        }
        return focusedShapes;
    };

    /**
     * 普通 {@link #shape} 只能放于 {@link #docSection} 之上.
     *
     * @override
     */
    const moveIndexBottom = self.moveIndexBottom;
    self.moveIndexBottom = (shape) => {
        if (shape.isTypeof("docSection")) {
            moveIndexBottom.apply(self, [shape]);
        } else {
            const index = self.shapes.findLastIndex(s => s.isTypeof("docSection"));
            self.moveIndexBefore(shape, index + Z_INDEX_OFFSET + 1);
        }
    };

    /**
     * 普通 {@link #shape} 对象不能放于 {@link #docSection} 之前.
     *
     * @override
     */
    const moveIndexBefore = self.moveIndexBefore;
    self.moveIndexBefore = (shape, index) => {
        if (shape.isTypeof("docSection")) {
            moveIndexBefore.apply(self, [shape, index]);
        } else {
            const lastDocSectionIndex = self.shapes.findLastIndex(s => s.isTypeof("docSection"));
            (lastDocSectionIndex + Z_INDEX_OFFSET) < index && moveIndexBefore.apply(self, [shape, index]);
        }
    };

    /**
     * 创建图形.
     * 1、为普通图形添加文档融合相关操作，比如嵌入，悬浮等.
     * 2、让docSection适配docPage.在docPage中和在其他graph中的表现形式不一致.
     *
     * @override
     */
    const createShape = self.createShape;
    self.createShape = (shapeType, x, y, id, ignoreLimit, parent) => {
        const shape = createShape.apply(self, [shapeType, x, y, id, ignoreLimit, parent]);

        // docFrame和docSection不用进行增强.
        // 主要针对普通图形.
        if (shapeType !== "docFrame" && !shape.isTypeof("docSection")) {
            const topSection = getTopSection();
            enhanceEntanglement(shape, topSection);
            enhanceDraggable(shape, topSection);
            enhanceFocused(shape);
            enhanceDrawer(shape);
            enhanceKeyAction(shape, topSection);
        }

        if (shape.isTypeof("docSection")) {
            enhanceDocSection(shape);
        }

        return shape;
    }

    const enhanceDocSection = (shape) => {
        /**
         * 在docPage中，比默认高度大，才进行设置.
         *
         * @override
         */
        shape.setHeight = (height) => {
            shape.height = height > DEFAULT_HEIGHT ? height : DEFAULT_HEIGHT;
        }

        const drawer = shape.drawer;

        /**
         * @override
         */
        const containerResize = drawer.containerResize;
        drawer.containerResize = (width, height) => {
            containerResize.apply(drawer, [width, height]);
            if (shape.isTopSection()) {
                drawer.container.style.height = "100%";
                drawer.updateIfChange(drawer.text.style, 'width', "100%", 'text_width');
                drawer.updateIfChange(drawer.text.style, 'height', "100%", 'text_height');
                drawer.updateIfChange(drawer.text.style, 'maxWidth', "none", 'text_maxWidth');
                drawer.updateIfChange(drawer.text.style, 'minHeight', "100%", 'text_minHeight');
            }
        };

        /**
         * @override
         */
        const parentResize = drawer.parentResize;
        drawer.parentResize = (width, height) => {
            parentResize.apply(drawer, [width, height]);
            if (shape.isTopSection()) {
                drawer.parent.style.width = "100%";
                drawer.parent.style.height = "100%";
            }
        };
    };

    /**
     * 获得所有悬浮态的图形.
     *
     * @return {*} 处于悬浮状态的图形集合.
     */
    self.getSuspensionShapes = () => {
        return self.shapes.filter(s => s.mode && s.mode === SHAPE_IN_DOCUMENT_MODE.SUSPENSION);
    };

    /**
     * 禁用当前page中图形的的指针事件.让指针事件向下透传.
     */
    self.disablePointerEvents = () => {
        const shapes = self.shapes
            .filter(s => !s.isTypeof("docSection") && s.type !== "docFrame" && s.type !== "referenceVector" && !s.isLevitation);
        shapes.forEach(s => s.disablePointerEvents());
    }

    /**
     * 恢复当前page中图形的指针事件.
     */
    self.recoverPointerEvents = () => {
        const shapes = self.shapes
            .filter(s => !s.isTypeof("docSection") && s.type !== "docFrame" && s.type !== "referenceVector" && !s.isLevitation);
        shapes.forEach(s => s.recoverPointerEvents());
    }

    /**
     * 删除悬浮框.
     */
    self.removeLevitation = () => {
        self.shapes.filter(s => s.isLevitation).forEach(s => s.remove());
    }

    self.renderToolbar = (div) => {
        const topSection = getTopSection();
        topSection && topSection.drawer.renderToolbar(div);
    }

    /**
     * 文档中屏蔽右键弹出菜单.
     *
     * @override
     */
    self.mouseRightClick = () => {
        return false;
    }

    return self;
}