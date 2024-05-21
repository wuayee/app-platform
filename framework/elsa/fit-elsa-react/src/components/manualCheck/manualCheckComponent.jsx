import ManualCheckFormWrapper from "@/components/manualCheck/ManualCheckFormWrapper.jsx";

export const manualCheckComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            "converter": {},
            "taskId": "",
            "type": "AIPP_SMART_FORM",
            "formName": "",
            "outputParams": ""
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
    self.reducers = (data, action) => {
        function changeFormAndSetOutput() {
            return {
                ...data,
                taskId: action.formId,
                formName: action.formName,
                output: action.formOutput,
            }
        }

        switch (action.actionType) {
            case 'changeFormAndSetOutput': {
                return changeFormAndSetOutput();
            }
            default: {
                throw Error('Unknown action: ' + action.type);
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