import {jadeNode} from "@/components/jadeNode.jsx";
import "./style.css";
import {Button} from "antd";
import {DIRECTION} from "@fit-elsa/elsa-core";
import EndIcon from '../asserts/icon-end.svg?react'; // 导入背景图片

/**
 * 结束节点shape
 *
 @override
 */
export const endNodeEnd = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "endNodeEnd";
    self.backColor = 'white';
    self.pointerEvents = "auto";
    self.text = "结束";
    self.componentName = "endComponent";
    self.deletable = false;
    self.flowMeta = {
        "triggerMode": "auto",
        "callback": {
            "type": "general_callback",
            "name": "通知回调",
            "fitables": ["com.huawei.fit.jober.aipp.fitable.AippFlowEndCallback"],
            "converter": {
                "type": "mapping_converter"
            },
        }
    };
    self.toolMenus = [{
        key: '1', label: "重命名", action: (setEdit) => {
            setEdit(true);
        }
    }];

    /**
     * 设置E方向没有连接点
     *
     * @override
     */
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.apply(self);
        self.connectors.remove(c => c.direction.key === DIRECTION.E.key);
    };

    /**
     * 序列化组件信息
     *
     * @override
     */
    self.serializerJadeConfig = () => {
        self.flowMeta.callback.converter.entity = self.getLatestJadeConfig();
    }

    /**
     * 获取用户自定义组件.
     *
     * @override
     */
    self.getComponent = () => {
        return self.graph.plugins[self.componentName](self.flowMeta.callback.converter.entity);
    };

    self.getHeaderIcon = () => {
        return (
            <Button
                disabled={true}
                className="jade-node-custom-header-icon"
            >
                <EndIcon/>
            </Button>
        );
    };

    return self;
}