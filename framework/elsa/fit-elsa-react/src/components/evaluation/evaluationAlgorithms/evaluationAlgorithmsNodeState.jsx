import {SECTION_TYPE} from "@/common/Consts.js";
import {
    evaluationAlgorithmsNodeDrawer
} from "@/components/evaluation/evaluationAlgorithms/evaluationAlgorithmsNodeDrawer.jsx";
import {evaluationNode} from "@/components/evaluation/evaluationNode.jsx";

/**
 * 评估算法节点shape
 *
 * @override
 */
export const evaluationAlgorithmsNodeState = (id, x, y, width, height, parent, drawer) => {
    const self = evaluationNode(id, x, y, width, height, parent, drawer ? drawer : evaluationAlgorithmsNodeDrawer);
    self.type = "evaluationAlgorithmsNodeState";
    self.componentName = "evaluationAlgorithmsComponent";
    self.text = "评估算法"
    self.width = 368;
    self.flowMeta.jober.type = 'STORE_JOBER';
    const toolEntity = {
        uniqueName: "",
        params: [],
        return: {
            type: ""
        }
    };

    /**
     * @override
     */
    const serializerJadeConfig = self.serializerJadeConfig;
    self.serializerJadeConfig = (jadeConfig) => {
        serializerJadeConfig.apply(self, [jadeConfig]);
        if (!self.flowMeta.jober.entity) {
            self.flowMeta.jober.entity = toolEntity;
        }
        self.flowMeta.jober.entity.params = self.flowMeta.jober.converter.entity.inputParams.map(property => {
            return {name: property.name}
        });
        self.flowMeta.jober.entity.uniqueName = self.flowMeta.jober.converter.entity.algorithm.value
            .find(item => item.name === "uniqueName").value;
    };

    /**
     * 评估算法节点的测试报告章节
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