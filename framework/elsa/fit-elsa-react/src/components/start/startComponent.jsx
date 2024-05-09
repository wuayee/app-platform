import StartFormWrapper from "./StartFormWrapper.jsx";
import {v4 as uuidv4} from "uuid";

export const startComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : [
            {
                id: uuidv4(),
                name: "input",
                type: "Object",
                from: "Expand",
                value: [{id: "input_" + uuidv4(), name: "Question", type: "String", from: "Input", description: "", value: ""}]
            },
            {
                id: uuidv4(),
                name: "memory",
                type: "Object",
                from: "Expand",
                value: [{
                    id: uuidv4(),
                    name: "type",
                    type: "String",
                    from: "Input",
                    value: "ByConversationTurn"
                }, {
                    id: uuidv4(),
                    name: "value",
                    type: "Integer",
                    from: "Input",
                    value: "3"
                }]
            }
        ];
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><StartComponent/></>);
    };

    /**
     * 必须.
     */
    self.reducers = (data, action) => {
        function addInputParam() {
            return data.map(item => {
                if (item.name === "input") {
                    return {
                        ...item, value: [...item.value, {id: action.id, name: "", type: "string", from: "init", description: "", value: ""}]
                    }
                } else {
                    return item;
                }
            })
        }

        function changeInputParam() {
            return data.map(item => {
                if (item.name === "input") {
                    return {
                        ...item, value: item.value.map(inputItem => {
                            if (inputItem.id === action.id) {
                                return {
                                    ...inputItem, [action.type]: action.value
                                }
                            } else {
                                return inputItem;
                            }
                        })
                    }
                } else {
                    return item;
                }
            });
        }

        function changeMemory() {
            return data.map(item => {
                if (item.name === "memory") {
                    return {
                        ...item, value: item.value.map(memoryItem => {
                            if (memoryItem.name === "type") {
                                return {...memoryItem, value: action.memoryType};
                            } else if (memoryItem.name === "value") {
                                return {...memoryItem, type: action.memoryValueType, value: action.memoryValue};
                            } else {
                                return memoryItem;
                            }
                        })
                    }
                } else {
                    return item;
                }
            });
        }

        function deleteInputParam() {
            return data.map(item => {
                if (item.name === "input") {
                    return {
                        ...item, value: item.value.filter(inputItem => inputItem.id !== action.id)
                    }
                } else {
                    return item;
                }
            });
        }

        switch (action.actionType) {
            case 'addInputParam': {
                return addInputParam();
            }
            case 'changeInputParam': {
                return changeInputParam();
            }
            case 'changeMemory': {
                return changeMemory();
            }
            case 'deleteInputParam': {
                return deleteInputParam();
            }
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
};

const StartComponent = () => {
    return (<>
        <StartFormWrapper/>
    </>)
};