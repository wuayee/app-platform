import {jadeNodeDrawer} from "@/components/base/jadeNodeDrawer.jsx";

/**
 * 评估节点绘制器.
 *
 * @override
 */
export const evaluationNodeDrawer = (shape, div, x, y) => {
    const self = jadeNodeDrawer(shape, div, x, y);
    self.type = "evaluationNodeDrawer";

    return self;
};