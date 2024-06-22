import {jadeNodeDrawer} from "@/components/jadeNodeDrawer.jsx";
import ConditionIcon from "../asserts/icon-condition.svg?react";

/**
 * 条件节点绘制器
 *
 * @override
 */
export const conditionNodeDrawer = (shape, div, x, y) => {
    const self = jadeNodeDrawer(shape, div, x, y);
    self.type = "conditionNodeDrawer";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (<>
            <ConditionIcon className="jade-node-custom-header-icon"/>
        </>);
    };

    return self;
};