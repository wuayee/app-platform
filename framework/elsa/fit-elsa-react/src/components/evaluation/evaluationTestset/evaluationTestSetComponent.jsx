import {v4 as uuidv4} from "uuid";
import {convertReturnFormat} from "@/components/util/MethodMetaDataParser.js";
import EvaluationTestSetWrapper from "@/components/evaluation/evaluationTestset/EvaluationTestSetWrapper.jsx";
import {filterChain} from "@/components/evaluation/evaluationTestset/Interceptor.js";

const chain = filterChain();
chain.createFilter("array");
chain.createFilter("object");
chain.createFilter("string");
chain.createFilter("number");

/**
 * 评估测试集节点组件
 *
 * @param jadeConfig
 */
export const evaluationTestSetComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            inputParams: [],
            testSet: {
                id: "testSet_" + uuidv4(),
                name: "testSet",
                type: "Object",
                from: "Expand",
                value: [
                    {id: uuidv4(), name: 'name', type: 'String', from: 'Input', value: ""},
                    {
                        id: "quantity_" + uuidv4(),
                        name: "quantity",
                        type: "Integer",
                        from: "Input",
                        value: ""
                    }]
            },
            outputParams: []
        };
    };

    /**
     * @override
     */
    self.getReactComponents = (disabled, data) => {
        return (<>
            <EvaluationTestSetWrapper disabled={disabled} data={data}/>
        </>);
    };

    /**
     * @override
     */
    self.reducers = (config, action) => {
        /**
         * 切换测试集
         *
         * @private
         */
        const _changeTestSet = () => {
            // 这里action包含了整个接口返回的数据
            if (action.value) {
                newConfig.testSet.value.find(item => item.name === "name").value = action.value.name;
                newConfig.inputParams = action.value.data;
            }
        };

        /**
         * 清除选择的测试集
         *
         * @private
         */
        const _clearTestSet = () => {
            newConfig.testSet.value.forEach(item => item.value = "");
        };

        /**
         * 转换输出用于展示
         *
         * @private
         */
        const _generateOutput = () => {
            if (!action.value) {
                return;
            }
            const inputJson = action.value;
            const newOutputParams = convertReturnFormat(JSON.parse(inputJson.schema));
            newConfig.outputParams = [newOutputParams];
        };

        /**
         * 将测试集数据转换成jadeConfig格式
         *
         * @private
         */
        const _parseDataset = () => {
            if (!action.value) {
                return;
            }
            // 更新数据集数量
            newConfig.testSet.value.find(item => item.name === "quantity").value = action.value.total;
            const input = parseData(action.value.items);
            delete newConfig.inputParams;
            newConfig.inputParams = input;
        };

        /**
         * 批量处理json列表数据，转换为jadeConfig格式
         *
         * @param data json对象列表
         * @return {*} json对象的jadeConfig格式描述
         */
        const parseData = (data) => {
            return data.map((item) => {
                return ({
                    id: uuidv4(),
                    name: item.id,
                    type: 'Object',
                    from: 'Expand',
                    value: chain.doFilter(JSON.parse(item.content))
                });
            });
        };

        /**
         * 清除输出
         *
         * @private
         */
        const _clearSchema = () => {
            newConfig.outputParams = [];
        };

        let newConfig = {...config};
        switch (action.type) {
            case 'changeTestSet':
                _changeTestSet();
                _generateOutput();
                return newConfig;
            case 'clearTestSet':
                _clearSchema();
                _clearTestSet();
                return newConfig;
            case 'parseDataset':
                _parseDataset();
                return newConfig;
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
}
