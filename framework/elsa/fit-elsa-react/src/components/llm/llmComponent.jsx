import LlmFormWrapper from "./LlmFormWrapper.jsx";
import {v4 as uuidv4} from "uuid";

export const llmComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            inputParams: [
                {
                    id: uuidv4(),
                    name: "model",
                    type: "String",
                    from: "Input",
                    value: ""
                },
                {
                    id: uuidv4(),
                    name: "temperature",
                    type: "Number",
                    from: "Input",
                    value: "0.3"
                },
                {
                    id: uuidv4(),
                    name: "prompt",
                    type: "Object",
                    from: "Expand",
                    value: [
                        {id: uuidv4(), name: "template", type: "String", from: "Input", value: ""},
                        {
                            id: uuidv4(), name: "variables", type: "Object", from: "Expand", value: [
                                {id: uuidv4(), name: undefined, type: "String", from: "Reference", value: "", referenceNode: "", referenceId: "", referenceKey: ""}
                            ]
                        }
                    ]
                },
                {id: uuidv4(), name: "tools", type: "Array", from: "Expand", value: []},
                {id: uuidv4(), name: "workflows", type: "Array", from: "Expand", value: []},
                {id: uuidv4(), name: "systemPrompt", type: "String", from: "Input", value: ""},
            ],
            outputParams: [
                {
                    id: uuidv4(),
                    name: "output",
                    type: "Object",
                    from: "Expand",
                    value: [
                        {id: uuidv4(), name: "llmOutput", type: "string", from: "Input", description: "", value: ""}
                    ]
                }
            ]
        };
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><LlmComponent/></>);
    };

    /**
     * 必须.
     */
    self.reducers = (data, action) => {
        function addInputParam() {
            const newData = {};
            Object.entries(data).forEach(([key, value]) => {
                if (key === "inputParams") {
                    newData[key] = value.map(item => {
                        if (item.name === "prompt") {
                            return {
                                ...item, value: item.value.map(promptItem => {
                                    if (promptItem.name === "variables") {
                                        return {
                                            ...promptItem, value: [...promptItem.value, {
                                                id: action.id,
                                                name: undefined,
                                                type: "String",
                                                from: "Reference",
                                                value: ""
                                            }]
                                        };
                                    } else {
                                        return promptItem;
                                    }
                                })
                            }
                        } else {
                            return item;
                        }
                    });
                } else {
                    newData[key] = value;
                }
            });
            return newData;
        }

        function addOutputParam() {
            const newData = {};
            Object.entries(data).forEach(([key, value]) => {
                if (key === "outputParams") {
                    newData[key] = value.map(item => {
                        if (item.name === "output") {
                            return {
                                ...item,
                                value: [...item.value, {
                                    id: action.id,
                                    name: "",
                                    type: "string",
                                    from: "Input",
                                    description: "",
                                    value: ""
                                }]
                            }
                        } else {
                            return item;
                        }
                    });
                } else {
                    newData[key] = value;
                }
            });
            return newData;
        }

        function changeInputParams() {
            const newData = {};
            Object.entries(data).forEach(([key, value]) => {
                if (key === "inputParams") {
                    newData[key] = value.map(item => {
                        if (item.name === "prompt") {
                            return {
                                ...item, value: item.value.map(promptItem => {
                                    if (promptItem.name === "variables") {
                                        return {
                                            ...promptItem, value: promptItem.value.map(inputItem => {
                                                if (inputItem.id === action.id) {
                                                    let updatedInputItem = { ...inputItem };
                                                    // 遍历 updateParams 中的每个对象，更新 updatedInputItem 中对应的属性
                                                    action.updateParams.map((item) => {
                                                        updatedInputItem[item.key] = item.value;
                                                    });
                                                    return updatedInputItem;
                                                } else {
                                                    return inputItem;
                                                }
                                            })
                                        }
                                    } else {
                                        return promptItem;
                                    }
                                })
                            }
                        } else {
                            return item;
                        }
                    });
                } else {
                    newData[key] = value;
                }
            });
            return newData;
        }

        function changePrompt() {
            const newData = {};
            Object.entries(data).forEach(([key, value]) => {
                if (key === "inputParams") {
                    newData[key] = value.map(item => {
                        if (item.name === "prompt") {
                            return {
                                ...item, value: item.value.map(promptItem => {
                                    if (action.id === promptItem.id && promptItem.name === "template") {
                                        return {
                                            ...promptItem, value: action.value
                                        }
                                    } else {
                                        return promptItem;
                                    }
                                })
                            }
                        } else {
                            return item;
                        }
                    });
                } else {
                    newData[key] = value;
                }
            });
            return newData;
        }

        function changeOutputParam() {
            const newData = {};
            Object.entries(data).forEach(([key, value]) => {
                if (key === "outputParams") {
                    newData[key] = value.map(item => {
                        if (item.name === "output") {
                            return {
                                ...item, value: item.value.map(outputItem => {
                                    if (outputItem.id === action.id) {
                                        return {...outputItem, [action.type]: action.value};
                                    } else {
                                        return outputItem;
                                    }
                                })
                            }
                        } else {
                            return item;
                        }
                    });
                } else {
                    newData[key] = value;
                }
            });
            return newData;
        }

        function changeConfig() {
            const newData = {};
            Object.entries(data).forEach(([key, value]) => {
                if (key === "inputParams") {
                    newData[key] = value.map(item => {
                        if (item.id === action.id) {
                            return {
                                ...item, value: action.value
                            };
                        } else {
                            return item;
                        }
                    });
                } else {
                    newData[key] = value;
                }
            });
            return newData;
        }

        function changeSkillConfig() {
            const newData = {};
            Object.entries(data).forEach(([key, value]) => {
                if (key === "inputParams") {
                    newData[key] = value.map(item => {
                        if (item.id === action.id) {
                            return {
                                ...item, value: action.value.map(value => {
                                        return {id: uuidv4(), type: "String", from: "Input", value: value}
                                })
                            };
                        } else {
                            return item;
                        }
                    });
                } else {
                    newData[key] = value;
                }
            });
            return newData;
        }

        function deleteInputParam() {
            const newData = {};
            Object.entries(data).forEach(([key, value]) => {
                if (key === "inputParams") {
                    newData[key] = value.map(item => {
                        if (item.name === "prompt") {
                            return {
                                ...item, value: item.value.map(promptItem => {
                                    if (promptItem.name === "variables") {
                                        return {
                                            ...promptItem, value: promptItem.value.filter(inputItem => (inputItem.id !== action.id))
                                        }
                                    } else {
                                        return promptItem;
                                    }
                                })
                            }
                        } else {
                            return item;
                        }
                    });
                } else {
                    newData[key] = value;
                }
            });
            return newData;
        }

        switch (action.actionType) {
            case 'addInputParam': {
                return addInputParam();
            }
            case 'addOutputParam': {
                return addOutputParam();
            }
            case 'changeInputParams': {
                return changeInputParams();
            }
            case 'changeOutputParam': {
                return changeOutputParam();
            }
            case 'changeConfig': {
                return changeConfig();
            }
            case 'changeSkillConfig': {
                return changeSkillConfig();
            }
            case 'changePrompt': {
                return changePrompt();
            }
            case 'deleteInputParam': {
                return deleteInputParam();
            }
            case 'deleteOutputParam': {
                return data.filter(item => item.id !== action.id);
            }
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
};

const LlmComponent = () => {
    return (<>
        <LlmFormWrapper/>
    </>)
};