import React, {useEffect, useState} from "react";
import {InvokeOutput} from "@/components/common/InvokeOutput.jsx";
import {useShapeContext} from "@/components/DefaultRoot.jsx";
import {EvaluationTestSetSelect} from "@/components/evaluation/evaluationTestset/EvaluationTestSetSelect.jsx";
import httpUtil from "@/components/util/httpUtil.jsx";
import {message} from "antd";
import PropTypes from "prop-types";

EvaluationTestSetWrapper.propTypes = {
    data: PropTypes.object,
    disabled: PropTypes.bool,
};

/**
 * 获取测试集节点配置数据
 *
 * @param shape 评估算法节点shape
 * @return {*} 配置数据
 */
const getEvaluationTestSetConfig = shape => {
    if (!shape || !shape.graph || !shape.graph.configs) {
        console.error('Cannot get shape.graph.configs.');
        throw new Error('Cannot get shape.graph.configs.');
    } else {
        return shape.graph.configs.find(node => node.node === "evaluationTestSetNodeState");
    }
};

/**
 * 评估算法节点组件
 *
 * @param data 节点数据
 * @param disabled 是否禁用
 * @constructor
 */
export default function EvaluationTestSetWrapper({data, disabled}) {
    const shape = useShapeContext();
    const selectedTestSet = data && (data.inputParams[0].value.find(item => item.name === "name")?.value ?? '');
    // const testQuantity = data && (data.inputParams[0].value.find(item => item.name === "quantity")?.value ?? 0); 暂时注释，后续放开
    const config = getEvaluationTestSetConfig(shape);
    const [testSets, setTestSets] = useState([]);

    useEffect(() => {
        if (!config.urls.datasetUrlPrefix) {
            console.error('Cannot get config.urls.datasetUrlPrefix.');
            throw new Error('Cannot get config.urls.datasetUrlPrefix.');
        } else {
            httpUtil.get(config.urls.datasetUrlPrefix + 'dataset?appId=1&pageIndex=1&pageSize=10', // 此接口查询多个版本数据，取最大版本
                {},
                (jsonData) => setTestSets(jsonData.data),
                (error) => {
                    message.error("数据集查寻失败，请联系系统管理员");
                    throw new Error("get evaluation test sets failed: " + error);
                });
        }
    }, []); // useEffect 依赖数组为空，表示只在组件挂载时执行一次

    return (<>
        <EvaluationTestSetSelect disabled={disabled} testSets={testSets} selectedTestSet={selectedTestSet}
                                 config={config}/>
        {/*<TestQuantity disabled={disabled} quantity={testQuantity}/>*/}
        <InvokeOutput outputData={data.outputParams}/>
    </>);
}