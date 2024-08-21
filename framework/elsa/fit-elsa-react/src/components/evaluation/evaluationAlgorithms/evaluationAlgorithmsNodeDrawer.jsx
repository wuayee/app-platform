import {jadeNodeDrawer} from "@/components/base/jadeNodeDrawer.jsx";
import AlgorithmIcon from "@/components/asserts/icon-evaluation-algorithm.svg?react";

/**
 * 代码节点绘制器
 *
 * @override
 */
export const evaluationAlgorithmsNodeDrawer = (shape, div, x, y) => {
    const self = jadeNodeDrawer(shape, div, x, y);
    self.type = "evaluationAlgorithmsNodeDrawer";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (<>
            <AlgorithmIcon className="jade-node-custom-header-icon"/>
        </>);
    };

    return self;
};