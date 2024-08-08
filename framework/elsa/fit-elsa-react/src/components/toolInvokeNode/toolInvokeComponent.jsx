import ToolInvokeFormWrapper from "@/components/toolInvokeNode/ToolInvokeFormWrapper.jsx";
import {updateInput} from "@/components/util/JadeConfigUtils.js";

/**
 * 工具调用节点组件
 *
 * @param jadeConfig
 */
export const toolInvokeComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必填
     *
     * @return 组件信息
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            inputParams: [],
            outputParams: []
        };
    };

    /**
     * @override
     */
    self.getReactComponents = (disabled, data) => {
        return (<>
            <ToolInvokeFormWrapper disabled={disabled} data={data}/>
        </>);
    };

    /**
     * @override
     */
    self.reducers = (config, action) => {

        let newConfig = {...config};
        switch (action.type) {
            case "update":
                newConfig.inputParams = updateInput(config.inputParams, action.id, action.changes);
                return newConfig;
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
}