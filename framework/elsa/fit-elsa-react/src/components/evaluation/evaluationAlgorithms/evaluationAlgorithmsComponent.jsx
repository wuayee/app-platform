import {v4 as uuidv4} from "uuid";
import EvaluationAlgorithmsWrapper from "@/components/evaluation/evaluationAlgorithms/EvaluationAlgorithmsWrapper.jsx";
import {convertParameter} from "@/components/util/MethodMetaDataParser.js";
import {updateInput} from "@/components/util/JadeConfigUtils.js";
import {defaultComponent} from "@/components/defaultComponent.js";
import {EVALUATION_ALGORITHM_NODE_CONST} from "@/common/Consts.js";

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
            inputParams: [{
                id: "algorithm_" + uuidv4(),
                name: "algorithm",
                type: "Object",
                from: "Expand",
                // 保存当前选中的算法信息
                value: [{id: uuidv4(), name: 'uniqueName', type: 'String', from: 'Input', value: ''}]
            }, {
                id: "passScore_" + uuidv4(),
                name: "passScore",
                type: "Integer",
                from: "Input",
                value: ""
            }],
            outputParams: [{
                id: "output_" + uuidv4(),
                name: "output",
                type: "Object",
                from: "Expand",
                value: [
                    {
                        id: "isPass_" + uuidv4(),
                        name: "isPass",
                        type: "Boolean",
                        from: "Input",
                        value: "Boolean"
                    },
                    {
                        id: "score_" + uuidv4(),
                        name: "score",
                        type: "Number",
                        from: "Input",
                        value: "Number"
                    }
                ]
            }]
        };
    };

    /**
     * @override
     */
    self.getReactComponents = (shapeStatus, data) => {
        return (<>
            <EvaluationAlgorithmsWrapper shapeStatus={shapeStatus} data={data}/>
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
            action.value && (newConfig.inputParams.find(item => item.name === EVALUATION_ALGORITHM_NODE_CONST.ALGORITHM).value.find(item => item.name === EVALUATION_ALGORITHM_NODE_CONST.UNIQUE_NAME).value = action.value.uniqueName);
        };

        /**
         * 修改及格分数
         *
         * @private
         */
        const _editScore = () => {
            newConfig.inputParams.find(item => item.name === EVALUATION_ALGORITHM_NODE_CONST.PASS_SCORE).value = action.value;
        };

        /**
         * 清除选项
         *
         * @private
         */
        const _clearAlgorithm = () => {
            newConfig.inputParams.find(item => item.name === EVALUATION_ALGORITHM_NODE_CONST.ALGORITHM).value = [];
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
            newConfig.inputParams.remove(item => item.name !== EVALUATION_ALGORITHM_NODE_CONST.ALGORITHM && item.name !== EVALUATION_ALGORITHM_NODE_CONST.PASS_SCORE);
            newConfig.inputParams.push(...convertedParameters);
        };

        /**
         * 清除输入输出
         *
         * @private
         */
        const _clearSchema = () => {
            newConfig.inputParams = [];
            newConfig.outputParams.remove(item => item.name !== EVALUATION_ALGORITHM_NODE_CONST.IS_PASS && item.name !== EVALUATION_ALGORITHM_NODE_CONST.SCORE);
        };

        let newConfig = {...config};
        switch (action.type) {
            case 'changeAlgorithm':
                _changeAlgorithm();
                _generateInput();
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
