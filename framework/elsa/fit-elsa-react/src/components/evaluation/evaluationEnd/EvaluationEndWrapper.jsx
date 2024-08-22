import {EvaluationOutput} from "@/components/evaluation/evaluationEnd/EvaluationOutput.jsx";
import PropTypes from "prop-types";

EvaluationEndWrapper.propTypes = {
    data: PropTypes.object,
    disabled: PropTypes.bool,
};

/**
 * 评估结束节点组件
 *
 * @param data 节点数据
 * @param disabled 是否禁用
 * @constructor
 */
export default function EvaluationEndWrapper({data, disabled}) {
    const output = data.inputParams.find(item => item.name === "output");

    return (<>
        <EvaluationOutput output={output} disabled={disabled}/>
    </>);
}