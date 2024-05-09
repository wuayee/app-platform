import {jadeNode} from "@/components/jadeNode.jsx";
import "./style.css";
import {Button} from "antd";
import ApiInvokeIcon from '../asserts/icon-api-invoke.svg?react';

/**
 * FIT调用节点shape
 *
 * @override
 */
export const fitInvokeNodeState = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "fitInvokeNodeState";
    self.width = 360;
    self.backColor = 'white';
    self.pointerEvents = "auto";
    self.text = "FIT调用";
    self.componentName = "fitInvokeComponent";
    self.flowMeta.triggerMode = 'auto';
    self.flowMeta.jober.type = 'GENERICABLE_JOBER';
    const fitEntity = {
        genericable: {
            id: "",
            params: []
        }
    };

    /**
     * @override
     */
    const serializerJadeConfig = self.serializerJadeConfig;
    self.serializerJadeConfig = () => {
        serializerJadeConfig.apply(self);
        const fitableId = self.flowMeta
                .jober
                .converter
                .entity
                .fitable
                .value
                .find(item => item.name === 'id')
                .value;
        if (fitableId) {
            self.flowMeta.jober.fitables = [fitableId];
        }
        fitEntity.genericable.params = self.flowMeta.jober.converter.entity.inputParams.map(property => {
            return {name: property.name}
        });
        fitEntity.genericable.id = self.flowMeta
                .jober
                .converter
                .entity
                .genericable
                .value
                .find(item => item.name === 'id')
                .value;
        self.flowMeta.jober.entity = fitEntity;
    };

    self.getHeaderIcon = () => {
        return (
            <Button disabled={true} className="jade-node-custom-header-icon">
                <ApiInvokeIcon/>
            </Button>
        );
    };

    return self;
}