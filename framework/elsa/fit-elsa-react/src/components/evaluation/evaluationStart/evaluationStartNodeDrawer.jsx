import {jadeNodeDrawer} from "@/components/jadeNodeDrawer.jsx";
import EvaluationStartIcon from "@/components/asserts/icon-evaluation-start.svg?react";

/**
 * 评估开始节点绘制器
 *
 * @override
 */
export const evaluationStartNodeDrawer = (shape, div, x, y) => {
    const self = jadeNodeDrawer(shape, div, x, y);
    self.type = "evaluationStartNodeDrawer";

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