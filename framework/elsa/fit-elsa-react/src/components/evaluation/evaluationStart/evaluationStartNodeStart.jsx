import {evaluationStartNodeDrawer} from "@/components/evaluation/evaluationStart/evaluationStartNodeDrawer.jsx";
import {evaluationNode} from "@/components/evaluation/evaluationNode.jsx";

/**
 * 评估开始节点shape
 *
 * @override
 */
export const evaluationStartNodeStart = (id, x, y, width, height, parent, drawer) => {
    const self = evaluationNode(id, x, y, width, height, parent, drawer ? drawer : evaluationStartNodeDrawer);
    self.type = "evaluationStartNodeStart";
    self.componentName = "evaluationStartComponent";
    self.text = "评估开始"
    self.width = 368;
    self.deletable = false;
    self.isUnique = true;

    return self;
}