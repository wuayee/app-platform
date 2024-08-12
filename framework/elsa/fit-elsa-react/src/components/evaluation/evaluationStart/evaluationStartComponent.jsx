import EvaluationStartWrapper from "@/components/evaluation/evaluationStart/EvaluationStartWrapper.jsx";

/**
 * 评估开始节点组件
 *
 * @param jadeConfig
 */
export const evaluationStartComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            inputParams: [],
            outputParams: [{}],
        }
    };

    /**
     * @override
     */
    self.getReactComponents = (disabled, data) => {
        return (<>
            <EvaluationStartWrapper disabled={disabled} data={data}/>
        </>);
    };

    return self;
}
