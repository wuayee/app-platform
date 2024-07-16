import {v4 as uuidv4} from "uuid";
import CodeWrapper from "@/components/code/CodeWrapper.jsx";

/**
 * code节点组件
 *
 * @param jadeConfig
 */
export const codeComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            inputParams: [
                {
                    id: uuidv4(),
                    name: "args",
                    type: "Object",
                    from: "Expand",
                    value: [
                        {
                            id: uuidv4(),
                            name: "input",
                            type: "String",
                            from: "Reference",
                            value: "",
                            referenceNode: "",
                            referenceId: "",
                            referenceKey: ""
                        }
                    ]
                }, {
                    id: uuidv4(),
                    name: "code",
                    type: "String",
                    from: "Input",
                    language: "python",
                    value: "def main(args: Args) -> Output:\n" +
                            "    params = args.params\n" +
                            "    ret: Output = {\n" +
                            "        \"key0\": params['input'] + params['input'],\n" +
                            "        \"key1\": [\"hello\", \"world\"],\n" +
                            "        \"key2\": {\n" +
                            "            \"key21\": \"hi\"\n" +
                            "        },\n" +
                            "    }\n" +
                            "    return ret"
                }, {
                    id: uuidv4(),
                    name: "language",
                    type: "String",
                    from: "Input",
                    value: "python"
                }
            ],
            outputParams: [
                {
                    id: uuidv4(),
                    name: "output",
                    type: "Object",
                    from: "Expand",
                    value: [
                        {id: uuidv4(), name: "key0", type: "String", from: "Input", description: "", value: ""},
                        {
                            id: uuidv4(),
                            name: "key1",
                            type: "Array",
                            from: "Input",
                            description: "",
                            value: ""
                        },
                        {
                            id: uuidv4(),
                            name: "key2",
                            type: "Object",
                            from: "Expand",
                            description: "",
                            value: [{
                                id: uuidv4(),
                                name: "key21",
                                type: "String",
                                from: "Input",
                                description: "",
                                value: ""
                            }]
                        },
                    ]
                }
            ]
        };
    };

    /**
     * @override
     */
    self.getReactComponents = (disabled) => {
        return (<>
            <CodeWrapper disabled={disabled}/>
        </>);
    };

    /**
     * @override
     */
    self.reducers = (config, action) => {
        /**
         * 添加一个子项
         *
         * @private
         */
        const _addSubItem = () => {
            _recursionAdd(newConfig.outputParams, action.id);
        };

        /**
         * 递归添加子项
         *
         * @param items key的配置信息
         * @param id 需要添加子项的父项id
         */
        const _recursionAdd = (items, id) => {
            items.forEach(item => {
                if (item.id === id) {
                    const newItem = {
                        id: uuidv4(),
                        name: "",
                        type: "String",
                        from: "Input",
                        description: "",
                        value: ""
                    };
                    item.value.push(newItem);
                    return;
                }
                if (item.type === "Object") {
                    _recursionAdd(item.value, id);
                }
            });
        };

        /**
         * 删除一行(output不支持删除)
         *
         * @private
         */
        const _deleteRow = () => {
            const output = newConfig.outputParams.find(item => item.name === "output");
            output.value = removeItemById(output.value, action.id);
        };

        /**
         * 重新构造一个不包含需要删除id的数组
         *
         * @param arr 原始数组
         * @param idToRemove 需要删除的id对应的数据
         * @return {*}
         */
        const removeItemById = (arr, idToRemove) => arr.reduce((acc, item) => {
            if (item.id === idToRemove) {
                return acc;
            }

            if (item.type === "Object" && Array.isArray(item.value)) {
                item.value = removeItemById(item.value, idToRemove);
            }

            acc.push(item);
            return acc;
        }, []);

        /**
         * 编辑属性名
         *
         * @param id 属性对应id
         * @param value 新的属性值
         * @private
         */
        const _editOutputName = (id, value) => {
            const items = newConfig.outputParams.find(item => item.name === "output").value;
            _recursionEdit(items, action.id, value);
        };

        /**
         * 递归修改属性
         *
         * @param items 数据
         * @param id 属性对应id
         * @param value 新的属性值
         * @private
         */
        const _recursionEdit = (items, id, value) => {
            items.forEach(item => {
                if (item.id === id) {
                    action.changes.forEach(change => {
                        item[change.key] = change.value;
                    })
                    return;
                }
                if (item.type === "Object") {
                    _recursionEdit(item.value, id, value);
                }
            });
        };

        /**
         * 修改属性类型
         *
         * @param id 属性对应id
         * @param value 新的属性类型
         * @private
         */
        const _editOutputType = (id, value) => {
            _recursionEdit(newConfig.outputParams, action.id, value);
        };

        /**
         * 添加输入
         *
         * @private
         */
        const _addInput = () => {
            newConfig.inputParams.find(item => item.name === "args").value.push({
                id: action.id,
                name: "",
                type: "String",
                from: "Reference",
                value: "",
                referenceNode: "",
                referenceId: "",
                referenceKey: ""
            })
        };

        /**
         * 编辑输入属性
         *
         * @private
         */
        const _editInput = () => {
            newConfig.inputParams.find(item => item.name === "args").value.forEach(item => {
                if (item.id === action.id) {
                    action.changes.forEach(change => {
                        item[change.key] = change.value;
                    });
                }
            });
        };

        /**
         * 删除输入
         *
         * @private
         */
        const _deleteInput = () => {
            const input = newConfig.inputParams.find(item => item.name === "args");
            input.value = input.value.filter(item => item.id !== action.id);
        };

        /**
         * 修改code代码
         *
         * @private
         */
        const _editCode = () => {
            const code = newConfig.inputParams.find(item => item.name === "code");
            code.value = action.value;
        };

        let newConfig = {...config};
        switch (action.type) {
            case 'editInput':
                _editInput();
                return newConfig;
            case 'addInput':
                _addInput();
                return newConfig;
            case 'deleteInput':
                _deleteInput();
                return newConfig;
            case 'editCode':
                _editCode();
                return newConfig;
            case 'editOutputName':
                _editOutputName(action.id, action.value);
                return newConfig;
            case 'editOutputType':
                _editOutputType(action.id, action.value);
                return newConfig;
            case 'addSubItem':
                _addSubItem();
                return newConfig;
            case 'deleteRow':
                _deleteRow();
                return newConfig;
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
}
