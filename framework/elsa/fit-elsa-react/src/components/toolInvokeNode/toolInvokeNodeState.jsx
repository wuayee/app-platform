import {jadeNode} from "@/components/jadeNode.jsx";
import {Button} from "antd";
import ApiInvokeIcon from '../asserts/icon-api-invoke.svg?react';
import {convertParameter, convertReturnFormat} from "@/components/util/MethodMetaDataParser.js";

/**
 * 工具调用节点shape
 *
 * @override
 */
export const toolInvokeNodeState = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "toolInvokeNodeState";
    self.width = 360;
    self.backColor = 'white';
    self.pointerEvents = "auto";
    self.text = "工具调用";
    self.componentName = "toolInvokeComponent";
    self.flowMeta.triggerMode = 'auto';
    self.flowMeta.jober.type = 'STORE_JOBER';
    const toolEntity = {
        uniqueName: "",
        params: [],
        return: {
            type: ""
        }
    };
    const template = {
        inputParams: [],
        outputParams: []
    };

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
    self.processMetaData = (metaData) => {
        const _generateOutput = () => {
            newConfig.outputParams.push(convertReturnFormat(metaData.schema.return));
        }

        const _generateInput = () => {
            // 这里需要确认，返回的到底是什么数据类型，data是个数组还是对象
            delete newConfig.inputParams;
            newConfig.inputParams = Object.keys(metaData.schema.parameters.properties).map(key => {
                return convertParameter({
                    propertyName: key,
                    property: metaData.schema.parameters.properties[key]
                });
            });
        };

        const newConfig = {...template};
        _generateInput();
        _generateOutput();
        self.flowMeta.jober.converter.entity = newConfig;
        self.flowMeta.jober.entity = toolEntity;
        self.flowMeta.jober.entity.uniqueName = metaData.uniqueName;
        self.flowMeta.jober.entity.return.type = metaData.schema.return.type;
        self.drawer.unmountReact();
        self.invalidateAlone();
    }

    self.getHeaderIcon = () => {
        return (
                <Button disabled={true} className="jade-node-custom-header-icon">
                    <ApiInvokeIcon/>
                </Button>
        );
    };

    return self;
}