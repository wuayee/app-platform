import {jadeNode} from "@/components/base/jadeNode.jsx";
import {evaluationNodeDrawer} from "@/components/evaluation/evaluationNodeDrawer.jsx";

/**
 * 评估节点基类.
 *
 * @override
 */
export const evaluationNode = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer ? drawer : evaluationNodeDrawer);
    self.type = "evaluationNode";

    return self;
};