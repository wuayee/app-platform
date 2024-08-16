import {SECTION_TYPE} from "@/common/Consts.js";
import "./style.css";
import {
    evaluationAlgorithmsNodeDrawer
} from "@/components/evaluation/evaluationAlgorithms/evaluationAlgorithmsNodeDrawer.jsx";
import {evaluationNode} from "@/components/evaluation/evaluationNode.jsx";

/**
 * 评估算法节点shape
 *
 * @override
 */
export const evaluationTestSetNodeState = (id, x, y, width, height, parent, drawer) => {
    const self = evaluationNode(id, x, y, width, height, parent, drawer ? drawer : evaluationAlgorithmsNodeDrawer);
    self.type = "evaluationTestSetNodeState";
    self.componentName = "evaluationTestSetComponent";
    self.text = "测试集"
    self.width = 368;
    self.flowMeta.jober.fitables.push("com.huawei.fit.jober.aipp.fitable.EvaluationTestSetComponent");

    /**
     * 测试集节点的测试报告章节
     */
    self.getRunReportSections = () => {
        return [{
            no: "1",
            name: "输出",
            type: SECTION_TYPE.DEFAULT,
            data: self.getOutputData(self.input)
        }];
    };

    return self;
}