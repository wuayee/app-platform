import EvaluationStartWrapper from "@/components/evaluation/evaluationStart/EvaluationStartWrapper.jsx";
import {defaultComponent} from "@/components/defaultComponent.js";

/**
 * 评估开始节点组件
 *
 * @param jadeConfig
 */
export const evaluationStartComponent = (jadeConfig) => {
    const self = defaultComponent(jadeConfig);

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
    self.getReactComponents = (shapeStatus, data) => {
        return (<>
            <EvaluationStartWrapper shapeStatus={shapeStatus} data={data}/>
        </>);
    };

    return self;
}
