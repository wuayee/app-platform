import {Button} from "antd";
import {toolInvokeNodeState} from "@/components/toolInvokeNode/toolInvokeNodeState.jsx";
import {v4 as uuidv4} from "uuid";

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
        processMetaData.apply(self, arguments);

        self.text = metaData.name;
        const taskIdParam = {
            id: "taskId_" + uuidv4(),
            name: "taskId",
            type: "String",
            from: "Input",
            value: metaData.schema.taskId
        }
        self.flowMeta.jober.converter.entity.inputParams.unshift(taskIdParam)
        self.drawer.unmountReact();
        self.invalidateAlone();
    }

    self.getHeaderIcon = () => {
        return (
            <Button disabled={true} className="jade-node-custom-header-icon">
                {/* Todo 待确认Icon*/}
                {/*<ApiInvokeIcon/>*/}
            </Button>
        );
    };

    return self;
}