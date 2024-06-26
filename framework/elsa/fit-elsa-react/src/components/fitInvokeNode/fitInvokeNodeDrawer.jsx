import {jadeNodeDrawer} from "@/components/jadeNodeDrawer.jsx";
import ApiInvokeIcon from "../asserts/icon-fit-invoke.svg?react"; // 导入背景图片

/**
 * fitInvoke节点绘制器
 *
 * @override
 */
export const fitInvokeNodeDrawer = (shape, div, x, y) => {
    const self = jadeNodeDrawer(shape, div, x, y);
    self.type = "fitInvokeNodeDrawer";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (<>
            <ApiInvokeIcon className="jade-node-custom-header-icon"/>
        </>);
    };

    return self;
};