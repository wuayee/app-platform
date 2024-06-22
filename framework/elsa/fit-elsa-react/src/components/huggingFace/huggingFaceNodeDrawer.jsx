import HuggingFaceIcon from "../asserts/icon-huggingface-header.svg?react";
import {toolInvokeNodeDrawer} from "@/components/toolInvokeNode/toolInvokeNodeDrawer.jsx"; // 导入背景图片

/**
 * huggingFace节点绘制器
 *
 * @override
 */
export const huggingFaceNodeDrawer = (shape, div, x, y) => {
    const self = toolInvokeNodeDrawer(shape, div, x, y);
    self.type = "huggingFaceNodeDrawer";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (<>
            <HuggingFaceIcon className="jade-node-custom-header-icon"/>
        </>);
    };

    return self;
};