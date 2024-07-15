import {jadeNodeDrawer} from "@/components/jadeNodeDrawer.jsx";
import CodeIcon from "@/components/asserts/icon-code.svg?react";

/**
 * 代码节点绘制器
 *
 * @override
 */
export const codeNodeDrawer = (shape, div, x, y) => {
    const self = jadeNodeDrawer(shape, div, x, y);
    self.type = "codeNodeDrawer";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (<>
            <CodeIcon className="jade-node-custom-header-icon"/>
        </>);
    };

    return self;
};