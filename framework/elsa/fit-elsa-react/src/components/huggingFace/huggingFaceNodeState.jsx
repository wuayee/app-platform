import {Button} from "antd";
import {toolInvokeNodeState} from "@/components/toolInvokeNode/toolInvokeNodeState.jsx";
import HuggingFaceIcon from '../asserts/icon-huggingface-header.svg?react'; // 导入背景图片
import {v4 as uuidv4} from "uuid";
import {JADE_MODEL_PREFIX, JADE_TASK_ID_PREFIX} from "@/common/Consts.js";

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
    self.sourcePlatform = "huggingFace"

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
        self.flowMeta.jober.entity.uniqueName = metaData.toolUniqueName;
        self.text = metaData.schema.name;
        const modelParam = {
            id: JADE_MODEL_PREFIX + uuidv4(),
            name: "model",
            type: "String",
            from: "Input",
            value: metaData.context.default_model
        }
        const taskIdParam = {
            id: JADE_TASK_ID_PREFIX + uuidv4(),
            name: "taskId",
            type: "String",
            from: "Input",
            value: metaData.taskId
        }
        self.flowMeta.jober.converter.entity.inputParams.unshift(modelParam)
        self.flowMeta.jober.converter.entity.inputParams.unshift(taskIdParam)
        self.drawer.unmountReact();
        self.invalidateAlone();
    }

    self.getHeaderIcon = () => {
        return (
            <Button disabled={true} className="jade-node-custom-header-icon">
                <HuggingFaceIcon/>
            </Button>
        );
    };

    return self;
}