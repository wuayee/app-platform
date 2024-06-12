import EndNodeWrapper from "@/components/end/EndNodeWrapper.jsx";
import {v4 as uuidv4} from "uuid";

/**
 * 结束节点组件
 *
 * @param jadeConfig
 */
export const endComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必填
     *
     * @return 组件信息
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            inputParams: [{
                id: uuidv4(),
                name: "finalOutput",
                type: "String",
                from: "Reference",
                referenceNode: "",
                referenceId: "",
                referenceKey: "",
                value: []
            }],
            outputParams: [{}],
        }
    };

    /**
     * @override
     */
    self.getReactComponents = (disabled) => {
        return (<EndNodeWrapper disabled={disabled}/>);
    };

    /**
     * @override
     */
    self.reducers = (config, action) => {
        const _editOutputVariable = () => {
            const inputParams = newConfig.inputParams.find(item => item.name === "finalOutput");
            action.changes.forEach(change => {
                inputParams[change.key] = change.value;
            })
        };

        const newConfig = {...config};
        switch (action.type) {
            // 格式：dispatch({type: 'editOutputVariable', item:{id: 0, name: "", type: "String", from: "Reference", value: ""})
            case 'editOutputVariable':
                _editOutputVariable();
                return newConfig;
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
}