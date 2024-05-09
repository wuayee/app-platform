import {LevitationUtils} from "../../levitation/levitationUtils.js";
import Utils from "../common/Utils.js";

/**
 * 增强focused相关事件.
 *
 * @param shape 图形对象.
 */
export const enhanceFocused = (shape) => {
    /**
     * 当选中的图形处于嵌入态时，则不能进行多选.
     *
     * @inheritDoc
     * @override
     */
    const select = shape.select;
    shape.select = (x, y) => {
        const focusedShapes = shape.page.getFocusedShapes();
        if (focusedShapes.length > 0 && !isOnlyDocSection(focusedShapes)) {
            const newFocusedShapes = [...focusedShapes, shape];
            if (newFocusedShapes.find(s => s.isEmbedded())) {
                return;
            }
        }

        select.apply(shape, [x, y]);
    }

    const isOnlyDocSection = (focusedShapes) => {
        return focusedShapes.every(s => s.isTypeof("docSection"));
    }

    /**
     * 添加监听，来处理悬浮窗事件.
     */
    shape.addDetection(["inDragging"], (property, value, preValue) => {
        if (preValue === value) {
            return;
        }

        if (!value) {
            LevitationUtils.show(shape);
        } else {
            LevitationUtils.remove(shape);
        }
    });

    /**
     * 添加监听，来处理悬浮窗事件.
     */
    shape.addDetection(["mousedownConnector"], (property, value, preValue) => {
        if (value === null && preValue === null) {
            return;
        }

        if (value === null) {
            LevitationUtils.show(shape);
        } else {
            LevitationUtils.remove(shape);
        }
    });

    /**
     * 添加对isFocused属性的监听，以显示或删除悬浮窗.
     */
    shape.addDetection(["isFocused"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        const focusedShapes = shape.page.getFocusedShapes();
        if (focusedShapes.length === 1 && !focusedShapes[0].isTypeof("docSection")) {
            LevitationUtils.show(focusedShapes[0]);
        } else {
            LevitationUtils.remove(shape);
        }
    });

    /**
     * 移动坐标时，满足条件，需要重新生成悬浮窗，使其位置正确.
     *
     * @inheritDoc
     * @override
     */
    const moveTo = shape.moveTo;
    shape.moveTo = (x, y, after) => {
        moveTo.apply(shape, [x, y, after]);
        const focusedShapes = shape.page.getFocusedShapes();

        // 当之后一个focusedShape时，才显示悬浮窗.
        if (focusedShapes.length === 1 && focusedShapes[0] === shape && !Utils.isDragging(shape)) {
            LevitationUtils.show(shape);
        }
    };

    /**
     * 删除时，同时删除悬浮窗.
     *
     * @inheritDoc
     * @override
     */
    const remove = shape.remove;
    shape.remove = (source) => {
        try {
            return remove.apply(shape, [source]);
        } finally {
            LevitationUtils.remove(shape);
        }
    }
}