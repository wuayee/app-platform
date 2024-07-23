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
    self.getReactComponents = (disabled, data) => {
        return (<EndNodeWrapper disabled={disabled} data={data}/>);
    };

    /**
     * @override
     */
    self.reducers = (config, action) => {
        const _editOutputVariable = () => {
            const finalOutput = newConfig.inputParams.find(item => item.name === "finalOutput");
            if (!finalOutput) {
                return;
            }
            const newFinalOutput = {...finalOutput};
            newConfig.inputParams = [
                    ...newConfig.inputParams.filter(item => item.name !== "finalOutput"),
                    newFinalOutput
            ];
            action.changes.forEach(change => {
                newFinalOutput[change.key] = change.value;
            });
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
            action.entity.inputParams && newConfig.inputParams.push(...action.entity.inputParams);
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