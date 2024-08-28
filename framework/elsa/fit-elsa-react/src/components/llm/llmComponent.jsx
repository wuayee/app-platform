import LlmFormWrapper from "./LlmFormWrapper.jsx";
import {v4 as uuidv4} from "uuid";
import {defaultComponent} from "@/components/defaultComponent.js";

export const llmComponent = (jadeConfig) => {
    const self = defaultComponent(jadeConfig);
    const PLUGINS = "plugins"

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
                {id: uuidv4(), name: PLUGINS, type: "Array", from: "Expand", value: []},
                {id: uuidv4(), name: "systemPrompt", type: "String", from: "Input", value: ""},
            ],
            outputParams: [
                {
                    id: uuidv4(),
                    name: "output",
                    type: "Object",
                    from: "Expand",
                    value: [
                        {id: uuidv4(), name: "llmOutput", type: "String", from: "Input", description: "", value: ""}
                    ]
                }
            ]
        };
    };

    /**
     * 必须.
     *
     * @param shapeStatus 图形状态集合.
     * @param data 数据.
     */
    self.getReactComponents = (shapeStatus, data) => {
        return (<><LlmFormWrapper shapeStatus={shapeStatus} data={data}/></>);
    };

    /**
     * 必须.
     */
    const reducers = self.reducers;
    self.reducers = (data, action) => {
        const addInputParam = () => {
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
        };

        const addOutputParam = () => {
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
        };

        const changeInputParams = () => {
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
        };

        const changePrompt = () => {
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
        };

        const changeOutputParam = () => {
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
        };

        const changeConfig = () => {
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
        };

        const changeSkillConfig = () => {
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
        };

        const deleteInputParam = () => {
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
        };

        const getPlugins = () => [...data.inputParams.find(params => params.name === PLUGINS).value];

        const changePluginConfig = () => {
            const pluginsValue = getPlugins();
            const pluginMap = pluginsValue.reduce((map, plugin) => {
                const uniqueName = plugin.value[0].value.find(v => v.name === 'uniqueName').value;
                map[uniqueName] = plugin;
                return map;
            }, {});
            const actionValue = action.value;
            // 处理 actionValue 中的每个项
            actionValue.forEach(actionPlugin => {
                const key = actionPlugin.uniqueName;
                if (pluginMap[key]) {
                    // 更新现有条目
                    pluginMap[key].value[0].value.forEach(v => {
                        if (actionPlugin[v.name] !== undefined) {
                            v.value = actionPlugin[v.name];
                        }
                    });
                } else {
                    // 添加新条目
                    pluginsValue.push({
                        id: uuidv4(),
                        type: "Object",
                        from: "Expand",
                        value: [
                            {
                                id: uuidv4(),
                                from: "Expand",
                                type: "Object",
                                value: Object.keys(actionPlugin).map(key => ({
                                    id: uuidv4(),
                                    from: "input",
                                    name: key,
                                    type: "String",
                                    value: actionPlugin[key]
                                }))
                            }
                        ]
                    });
                }
            });

            // 删除多余的条目
            Object.keys(pluginMap).forEach(key => {
                if (!actionValue.find(item => item.uniqueName === key)) {
                    pluginsValue.splice(pluginsValue.indexOf(pluginMap[key]), 1);
                }
            });

            const newData = { ...data };
            newData.inputParams.find(param => param.name === PLUGINS).value = pluginsValue;
        };

        const deletePlugin = () => {
            const newData = {};
            Object.entries(data).forEach(([key, value]) => {
                if (key === "inputParams") {
                    newData[key] = value.map(item => {
                        if (item.name === PLUGINS) {
                            return {
                                ...item, value: item.value.filter(plugin => (plugin.id !== action.id))
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
        };

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
            case 'changePluginConfig': {
                return changePluginConfig();
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
            case 'deletePlugin': {
                return deletePlugin();
            }
            default: {
                return reducers.apply(self, [data, action]);
            }
        }
    };

    return self;
};