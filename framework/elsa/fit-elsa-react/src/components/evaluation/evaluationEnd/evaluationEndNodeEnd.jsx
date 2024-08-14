import {evaluationNode} from "@/components/evaluation/evaluationNode.jsx";
import {evaluationEndComponent} from "@/components/evaluation/evaluationEnd/evaluationEndComponent.jsx";
import {evaluationEndNodeDrawer} from "@/components/evaluation/evaluationEnd/evaluationEndNodeDrawer.jsx";
import {DIRECTION} from "@fit-elsa/elsa-core";

/**
 * 评估结束节点shape
 *
 * @override
 */
export const evaluationEndNodeEnd = (id, x, y, width, height, parent, drawer) => {
    const self = evaluationNode(id, x, y, width, height, parent, drawer ? drawer : evaluationEndNodeDrawer);
    self.type = "evaluationEndNodeEnd";
    self.componentName = "evaluationEndComponent";
    self.text = "评估结束";
    self.width = 461;
    self.deletable = false;
    self.isUnique = true;

    /**
     * 设置E方向没有连接点
     *
     * @override
     */
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.apply(self);
        self.connectors.remove(c => c.direction.key === DIRECTION.E.key);
    };

    return self;
}