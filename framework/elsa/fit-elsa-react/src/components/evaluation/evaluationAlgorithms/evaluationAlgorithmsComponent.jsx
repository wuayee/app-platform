import {v4 as uuidv4} from "uuid";
import EvaluationAlgorithmsWrapper from "@/components/evaluation/evaluationAlgorithms/EvaluationAlgorithmsWrapper.jsx";
import {convertParameter, convertReturnFormat} from "@/components/util/MethodMetaDataParser.js";
import {updateInput} from "@/components/util/JadeConfigUtils.js";
import {defaultComponent} from "@/components/defaultComponent.js";

/**
 * 评估算法节点组件
 *
 * @param jadeConfig
 */
export const evaluationAlgorithmsComponent = (jadeConfig) => {
    const self = defaultComponent(jadeConfig);

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            inputParams: [],
            algorithm: {
                id: "algorithm_" + uuidv4(),
                name: "algorithm",
                type: "Object",
                from: "Expand",
                // 保存当前选中的算法信息
                value: [{id: uuidv4(), name: 'uniqueName', type: 'String', from: 'Input', value: ''}]
            }, score: {
                id: "score_" + uuidv4(),
                name: "score",
                type: "Integer",
                from: "Input",
                value: ""
            },
            outputParams: []
        };
    };

    /**
     * @override
     */
    self.getReactComponents = (disabled, data) => {
        return (<>
            <EvaluationAlgorithmsWrapper disabled={disabled} data={data}/>
        </>);
    };

    /**
     * @override
     */
    const reducers = self.reducers;
    self.reducers = (config, action) => {
        /**
         * 切换算法
         *
         * @private
         */
        const _changeAlgorithm = () => {
            // 这里action包含了整个接口返回的数据
            action.value && (newConfig.algorithm.value.find(item => item.name === "uniqueName").value = action.value.uniqueName);
        };

        /**
         * 修改及格分数
         *
         * @private
         */
        const _editScore = () => {
            newConfig.score.value = action.value;
        };

        /**
         * 清除选项
         *
         * @private
         */
        const _clearAlgorithm = () => {
            newConfig.algorithm.value = [];
        };

        /**
         * 创建输入
         *
         * @private
         */
        const _generateInput = () => {
            if (!action.value) {
                return;
            }
            const inputJson = action.value;
            const convertedParameters = Object.keys(inputJson.schema.parameters.properties).map(key => {
                return convertParameter({
                    propertyName: key,
                    property: inputJson.schema.parameters.properties[key]
                });
            });
            delete newConfig.inputParams;
            newConfig.inputParams = convertedParameters;
        };

        /**
         * 创建输出
         *
         * @private
         */
        const _generateOutput = () => {
            if (!action.value) {
                return;
            }
            const inputJson = action.value;
            const newOutputParams = convertReturnFormat(inputJson.schema.return);
            newConfig.outputParams = [newOutputParams];
        };

        /**
         * 清除输入输出
         *
         * @private
         */
        const _clearSchema = () => {
            newConfig.inputParams = [];
            newConfig.outputParams = [];
        };

        let newConfig = {...config};
        switch (action.type) {
            case 'changeAlgorithm':
                _changeAlgorithm();
                _generateInput();
                _generateOutput();
                return newConfig;
            case 'editScore':
                _editScore();
                return newConfig;
            case 'clearAlgorithm':
                _clearSchema();
                _clearAlgorithm();
                return newConfig;
            case 'update':
                newConfig.inputParams = updateInput(config.inputParams, action.id, action.changes);
                return newConfig;
            default: {
                return reducers.apply(self, [config, action]);
            }
        }
    };

    return self;
}
