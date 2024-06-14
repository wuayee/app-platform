import {Button} from "antd";
import {toolInvokeNodeState} from "@/components/toolInvokeNode/toolInvokeNodeState.jsx";
import HuggingFaceIcon from '../asserts/icon-huggingface-header.svg?react'; // 导入背景图片

/**
 * 工具调用节点shape
 *
 * @override
 */
export const huggingFaceNodeState = (id, x, y, width, height, parent, drawer) => {
    const self = toolInvokeNodeState(id, x, y, width, height, parent, drawer);
    self.type = "huggingFaceNodeState";
    self.text = "huggingFace调用";
    self.componentName = "huggingFaceComponent";
    self.sourcePlatform = "huggingFace";
    self.width = 368;

    /**
     * @override
     */
    const serializerJadeConfig = self.serializerJadeConfig;
    self.serializerJadeConfig = () => {
        serializerJadeConfig.apply(self);
        self.flowMeta.jober.entity.params = self.flowMeta.jober.converter.entity.inputParams.map(property => {
            return {name: property.name}
        });
    };

    /**
     * @override
     */
    const processMetaData = self.processMetaData;
    self.processMetaData = (metaData) => {
        processMetaData.apply(self, [metaData]);
        self.text = metaData.schema.name;
        const INPUT_FROM_TYPE_VALUE = "Input";
        self.flowMeta.jober.converter.entity.inputParams[0].from = INPUT_FROM_TYPE_VALUE;
        self.flowMeta.jober.converter.entity.inputParams[1].from = INPUT_FROM_TYPE_VALUE;
        self.flowMeta.jober.converter.entity.inputParams[0].value = metaData.schema.name;
        self.flowMeta.jober.converter.entity.inputParams[1].value = metaData.context.default_model;
        self.drawer.unmountReact();
        self.invalidateAlone();
    }

    self.getHeaderIcon = () => {
        return (
            <HuggingFaceIcon className="jade-node-custom-header-icon"/>
        );
    };

    return self;
}