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

        /**
         * 选择form表单
         *
         * @return {*&{output: *, formName: (string|*), taskId: *}}
         * @private
         */
        const _changeForm = () => {
            return {
                ...config,
                taskId: action.formId,
                formName: action.formName,
                output: action.formOutput,
            };
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
                return _changeForm();
            case 'changeMode':
                return _changeMode();
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
}