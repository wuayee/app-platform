import {v4 as uuidv4} from "uuid";
import ManualCheckFormWrapper from "@/components/manualCheck/ManualCheckFormWrapper.jsx";

export const manualCheckComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            inputParams: [
                {
                    id: uuidv4(),
                    name: "formName",
                    type: "Object",
                    from: "Input",
                    value: ""
                }
            ],
            outputParams: []
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
                inputParams: data.inputParams.map(item => {
                    if (item.name === "formName") {
                        return {
                            ...item,
                            value: action.formName
                        }
                    } else {
                        return item;
                    }
                }),
                outputParams: action.formOutput
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