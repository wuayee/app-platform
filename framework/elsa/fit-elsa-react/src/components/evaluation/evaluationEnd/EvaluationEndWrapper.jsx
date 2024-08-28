import {EvaluationOutput} from "@/components/evaluation/evaluationEnd/EvaluationOutput.jsx";
import PropTypes from "prop-types";

EvaluationEndWrapper.propTypes = {
    data: PropTypes.object.isRequired,
    shapeStatus: PropTypes.object.isRequired
};

/**
 * 评估结束节点组件
 *
 * @param data 节点数据
 * @param shapeStatus 节点状态
 * @constructor
 */
export default function EvaluationEndWrapper({data, shapeStatus}) {
    const output = data.inputParams.find(item => item.name === "output");

    return (<>
        <EvaluationOutput output={output} shapeStatus={shapeStatus}/>
    </>);
}