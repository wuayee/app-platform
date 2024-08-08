import {v4 as uuidv4} from "uuid";
import RetrievalWrapper from "@/components/retrieval/RetrievalWrapper.jsx";

/**
 * retrieval节点组件
 *
 * @param jadeConfig
 */
export const retrievalComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必填
     *
     * @return 组件信息
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            inputParams: [{
                id: "query_" + uuidv4(),
                name: "query",
                type: "String",
                from: "Reference",
                referenceNode: "",
                referenceId: "",
                referenceKey: "",
                value: []
            }, {
                id: "knowledge_" + uuidv4(),
                name: "knowledge",
                type: "Array",
                from: "Expand",
                value: [{
                    id: uuidv4(),
                    type: "Object",
                    from: "Expand",
                    value: []
                }]
            }, {
                id: "maximum_" + uuidv4(),
                name: "maximum",
                type: "Integer",
                from: "Input",
                value: 3
            }],
            outputParams: [{
                id: "output_" + uuidv4(),
                name: "output",
                type: "Object",
                from: "Expand",
                value: [{
                    id: uuidv4(),
                    name: "retrievalOutput",
                    type: "String",
                    from: "Input",
                    value: "String"
                }]
            }]
        };
    };

    /**
     * @override
     */
    self.getReactComponents = (disabled, data) => {
        return (<>
            <RetrievalWrapper disabled={disabled} data={data} />
        </>);
    };

    /**
     * @override
     */
    self.reducers = (config, action) => {
        const _editInput = () => {
            const query = newConfig.inputParams.find(item => item.name === "query");
            const newQuery = {...query};
            newConfig.inputParams = [...newConfig.inputParams.filter(item => item.name !== "query"), newQuery];
            action.changes.map(change => {
                newQuery[change.key] = change.value;
            });
        };

        const _getTypeOfValue = (value) => {
            if (typeof itemValue === 'number') {
                return 'Integer';
            } else if (typeof itemValue === 'boolean') {
                return 'Boolean';
            } else {
                return 'String';
            }
        }

        const _updateKnowledge = () => {
            const knowledgeValue = getKnowledgeValue();
            // 将 knowledgeValue 转换成更易操作的格式
            const knowledgeMap = knowledgeValue.reduce((map, item) => {
                if (item.value && item.value.length > 0) {
                    const repoIdObj = item.value.find(v => v.name === 'repoId');
                    const tableIdObj = item.value.find(v => v.name === 'tableId');

                    if (repoIdObj && tableIdObj) {
                        const repoId = repoIdObj.value;
                        const tableId = tableIdObj.value;
                        map[`${repoId}-${tableId}`] = item;
                    }
                }
                return map;
            }, {});

            const actionValue = action.value;
            // 处理 actionValue 中的每个项
            actionValue.forEach(actionItem => {
                const key = `${actionItem.repoId}-${actionItem.tableId}`;
                if (knowledgeMap[key]) {
                    // 更新现有条目
                    knowledgeMap[key].value.forEach(v => {
                        if (actionItem[v.name] !== undefined) {
                            v.value = actionItem[v.name];
                        }
                    });
                } else {
                    // 添加新条目
                    knowledgeValue.push({
                        id: uuidv4(),
                        type: "Object",
                        from: "Expand",
                        value: Object.keys(actionItem).map(key => ({
                            id: uuidv4(),
                            from: "input",
                            name: key,
                            type: _getTypeOfValue(actionItem[key]),
                            value: actionItem[key]
                        }))
                    });
                }
            });

            // 删除多余的条目
            Object.keys(knowledgeMap).forEach(key => {
                const [repoId, tableId] = key.split('-').map(Number);
                if (!actionValue.find(item => item.repoId === repoId && item.tableId === tableId)) {
                    knowledgeValue.splice(knowledgeValue.indexOf(knowledgeMap[key]), 1);
                }
            });

            newConfig.inputParams.find(newTask => newTask.name === "knowledge").value = knowledgeValue;
        };

        const _deleteKnowledge = () => {
            const knowledgeValue = getKnowledgeValue();
            const indexToDelete = knowledgeValue.findIndex(item => item.id === action.id);
            indexToDelete !== -1 && knowledgeValue.splice(indexToDelete, 1);
            newConfig.inputParams.find(newTask => newTask.name === "knowledge").value = knowledgeValue;
        };

        const _changeMaximum = () => {
            newConfig.inputParams.filter(newTask => newTask.name === "maximum").forEach(item => {
                item.value = action.value;
            });
        };

        const getKnowledgeValue = () => [...newConfig.inputParams.find(newTask => newTask.name === "knowledge").value];

        let newConfig = {...config};
        switch (action.type) {
            // 格式：dispatch({type: 'editInput', item:{name: "Query", type: "String", from: "Reference", value: ""})
            case 'editInput':
                _editInput();
                return newConfig;
            case 'updateKnowledge':
                _updateKnowledge();
                return newConfig;
            // 格式：dispatch({type: 'deleteOutputVariable', id:"id")
            case 'deleteKnowledge':
                _deleteKnowledge();
                return newConfig;
            // 格式：dispatch({type: 'changemaximum', value:"")
            case 'changeMaximum':
                _changeMaximum();
                return newConfig;
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
}
