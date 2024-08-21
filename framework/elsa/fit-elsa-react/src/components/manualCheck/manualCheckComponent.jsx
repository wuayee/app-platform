import ManualCheckFormWrapper from "@/components/manualCheck/ManualCheckFormWrapper.jsx";
import {defaultComponent} from "@/components/defaultComponent.js";

export const manualCheckComponent = (jadeConfig) => {
    const self = defaultComponent(jadeConfig);

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            "converter": {
                "type": "mapping_converter",
                "entity": {
                    "inputParams": [],
                    "outputParams": []
                }
            },
            "taskId": "",
            "type": "AIPP_SMART_FORM",
            "formName": ""
        };
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><ManualCheckComponent/></>);
    };

    /**
     * 必须.
     */
    const reducers = self.reducers;
    self.reducers = (data, action) => {
        function changeFormAndSetOutput() {
            return {
                ...data,
                taskId: action.formId,
                formName: action.formName,
                converter: {
                    ...data.converter,
                    entity: action.entity
                }
            }
        }

        switch (action.actionType) {
            case 'changeFormAndSetOutput': {
                return changeFormAndSetOutput();
            }
            default: {
                return reducers.apply(self, [data, action]);
            }
        }
    };

    return self;
};

const ManualCheckComponent = () => {
    return (<>
        <ManualCheckFormWrapper/>
    </>)
};