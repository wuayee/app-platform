import {evaluationStartNodeDrawer} from "@/components/evaluation/evaluationStart/evaluationStartNodeDrawer.jsx";
import {evaluationNode} from "@/components/evaluation/evaluationNode.jsx";
import {DIRECTION} from "@fit-elsa/elsa-core";

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

    /**
     * 设置方向为W方向不出现连接点
     *
     * @override
     */
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.apply(self);
        self.connectors.remove(c => c.direction.key === DIRECTION.W.key);
    };

    return self;
}