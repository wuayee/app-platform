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
            inputParams && action.changes.forEach(change => {
                inputParams[change.key] = change.value;
            })
        };

        /**
         * 选择form表单
         *
         * @return {*&{output: *, formName: (string|*), taskId: *}}
         * @private
         */
        const _changeForm = () => {
            newConfig.inputParams.find(item => item.name === 'endFormId').value = action.formId;
            newConfig.inputParams.find(item => item.name === 'endFormName').value = action.formName;
            newConfig.inputParams.find(item => item.name === 'reportResult').value = action.entity.inputParams.find(item => item.name === "reportResult").value;
        };

        /**
         * 切换整个jadeConfig的值
         *
         * @return {*}
         * @private
         */
        const _changeMode = () => {
            return action.value;
        };

        const newConfig = {...config};
        switch (action.type) {
            case 'editOutputVariable':
                _editOutputVariable();
                return newConfig;
            case 'changeForm':
                _changeForm();
                return newConfig;
            case 'changeMode':
                newConfig.inputParams = _changeMode();
                return newConfig;
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
}