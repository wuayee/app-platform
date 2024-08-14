import EvaluationStartIcon from "@/components/asserts/icon-evaluation-end.svg?react";
import {endNodeDrawer} from "@/components/end/endNodeDrawer.jsx";

/**
 * 评估结束节点绘制器
 *
 * @override
 */
export const evaluationEndNodeDrawer = (shape, div, x, y) => {
    const self = endNodeDrawer(shape, div, x, y);
    self.type = "evaluationEndNodeDrawer";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (<>
            <EvaluationStartIcon className="jade-node-custom-header-icon"/>
        </>);
    };

    return self;
};