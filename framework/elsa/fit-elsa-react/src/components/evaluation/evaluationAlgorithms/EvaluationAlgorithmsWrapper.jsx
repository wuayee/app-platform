import React, {useEffect, useState} from "react";
import {EvaluationAlgorithmsSelect} from "@/components/evaluation/evaluationAlgorithms/EvaluationAlgorithmsSelect.jsx";
import {PassingScore} from "@/components/evaluation/evaluationAlgorithms/PassingScore.jsx";
import {InvokeOutput} from "@/components/common/InvokeOutput.jsx";
import {useShapeContext} from "@/components/DefaultRoot.jsx";
import {EvaluationInput} from "@/components/evaluation/evaluationAlgorithms/EvaluationInput.jsx";
import PropTypes from "prop-types";
import httpUtil from "@/components/util/httpUtil.jsx";

EvaluationAlgorithmsWrapper.propTypes = {
    data: PropTypes.object,
    disabled: PropTypes.bool,
};

/**
 * 获取评估算法节点配置数据
 *
 * @param shape 评估算法节点shape
 * @return {*} 配置数据
 */
const getEvaluationAlgorithmsConfig = shape => {
    if (!shape || !shape.graph || !shape.graph.configs) {
        console.error('Cannot get shape.graph.configs.');
        throw new Error('Cannot get shape.graph.configs.');
    } else {
        return shape.graph.configs.find(node => node.node === "evaluationAlgorithmsNodeState");
    }
};

/**
 * 评估算法节点组件
 *
 * @param data 节点数据
 * @param disabled 是否禁用
 * @constructor
 */
export default function EvaluationAlgorithmsWrapper({data, disabled}) {
    const shape = useShapeContext();
    const selectedAlgorithm = data && (data.algorithm.value.find(item => item.name === "uniqueName")?.value ?? '');
    const score = data.score.value;
    const config = getEvaluationAlgorithmsConfig(shape);
    const [algorithms, setAlgorithms] = useState([]);

    useEffect(() => {
        if (!config.urls.evaluationAlgorithmsUrl) {
            console.error('Cannot get config.urls.evaluationAlgorithmsUrl.');
            throw new Error('Cannot get config.urls.evaluationAlgorithmsUrl.');
        } else {
            httpUtil.get(config.urls.evaluationAlgorithmsUrl + '?includeTags=ALGORITHM&pageNum=1&pageSize=10',
                {},
                (jsonData) => setAlgorithms(jsonData),
                (error) => {
                    throw new Error("get evaluation algorithms failed: " + error);
                });
        }
    }, []); // useEffect 依赖数组为空，表示只在组件挂载时执行一次

    /**
     * 获取输出描述信息
     *
     * @param outputData 输出数据
     * @return {JSX.Element}
     */
    const getDescription = (outputData) => {
        return <div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
            <p>ActualInput: 实际输入</p>
            <p>ExpectOutput:预期输出</p>
            <p>ActualOutput:实际输出</p>
            <p>Score:算法评分</p>
            <p>IsPass:是否通过</p>
        </div>;
    };

    return (<>
        <EvaluationInput inputData={data.inputParams} disabled={disabled}/>
        <EvaluationAlgorithmsSelect disabled={disabled} algorithms={algorithms} selectedAlgorithm={selectedAlgorithm}/>
        <PassingScore disabled={disabled} score={score}/>
        <InvokeOutput outputData={data.outputParams} getDescription={getDescription}/>
    </>);
}