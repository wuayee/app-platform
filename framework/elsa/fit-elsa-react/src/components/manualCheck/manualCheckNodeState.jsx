import {jadeNode} from "@/components/jadeNode.jsx";
import "./style.css";
import {manualCheckNodeDrawer} from "@/components/manualCheck/manualCheckNodeDrawer.jsx";

/**
 * jadeStream中的人工检查节点.
 *
 * @override
 */
export const manualCheckNodeState = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer ? drawer : manualCheckNodeDrawer);
    self.type = "manualCheckNodeState";
    self.text = "人工检查";
    self.componentName = "manualCheckComponent";
    self.flowMeta.triggerMode = "manual";
    delete self.flowMeta.jober;

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
    self.serializerJadeConfig = (jadeConfig) => {
        self.flowMeta.task = jadeConfig;
    };

    return self;
};