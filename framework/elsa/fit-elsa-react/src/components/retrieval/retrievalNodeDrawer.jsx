import {jadeNodeDrawer} from "@/components/base/jadeNodeDrawer.jsx";
import RetrievalIcon from '../asserts/icon-retrieval.svg?react';

/**
 * 检索节点绘制器
 *
 * @override
 */
export const retrievalNodeDrawer = (shape, div, x, y) => {
    const self = jadeNodeDrawer(shape, div, x, y);
    self.type = "retrievalNodeDrawer";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (<>
            <RetrievalIcon className="jade-node-custom-header-icon"/>
        </>);
    };

    return self;
};