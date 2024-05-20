import {jadeNode} from "@/components/jadeNode.jsx";
import ConditionIcon from '../asserts/icon-condition.svg?react'; // 导入背景图片
import {Button} from "antd";
import {DIRECTION} from "@fit-elsa/elsa-core";

/**
 * jadeStream中的条件节点.
 *
 * @override
 */
export const conditionNodeCondition = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "conditionNodeCondition";
    self.text = "条件";
    self.width = 600;
    self.pointerEvents = "auto";
    self.componentName = "conditionComponent";
    delete self.flowMeta.jober;

    /**
     * 去除方向为E的连接点.
     *
     * @override
     */
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.apply(self);
        self.connectors.remove(c => c.direction.key === DIRECTION.E.key);
    };

    /**
     * 获取用户自定义组件.
     *
     * @return {*}
     */
    self.getComponent = () => {
        return self.graph.plugins[self.componentName](self.flowMeta.conditionParams);
    };

    /**
     * @override
     */
    self.serializerJadeConfig = () => {
        self.flowMeta.conditionParams = self.getLatestJadeConfig();
    }

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (
            <Button disabled={true} className="jade-node-custom-header-icon">
                <ConditionIcon/>
            </Button>
        );
    };

    return self;
};