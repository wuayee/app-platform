import {jadeNode} from "@/components/jadeNode.jsx";
import {Button} from "antd";
import ManualCheckIcon from '../asserts/icon-manual-check.svg?react'; // 导入背景图片
import "./style.css";

/**
 * jadeStream中的人工检查节点.
 *
 * @override
 */
export const manualCheckNodeState = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "manualCheckNodeState";
    self.text = "人工检查";
    self.pointerEvents = "auto";
    self.componentName = "manualCheckComponent";
    self.flowMeta.triggerMode = "manual";
    delete self.flowMeta.jober;

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (
            <Button
                disabled={true}
                className="jade-node-custom-header-icon"
            >
                <ManualCheckIcon/>
            </Button>
        );
    };

    /**
     * 获取用户自定义组件.
     *
     * @return {*}
     */
    self.getComponent = () => {
        return self.graph.plugins[self.componentName](self.flowMeta.task);
    };

    /**
     * @override
     */
    self.serializerJadeConfig = () => {
        self.flowMeta.task = self.getLatestJadeConfig();
    };

    return self;
};