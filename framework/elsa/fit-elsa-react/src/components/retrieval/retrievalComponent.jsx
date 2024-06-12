import {v4 as uuidv4} from "uuid";
import {RetrievalWrapper} from "@/components/retrieval/RetrievalWrapper.jsx";

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
    self.getReactComponents = (disabled) => {
        return (<>
            <RetrievalWrapper disabled={disabled}/>
        </>);
    };

    /**
     * @override
     */
    self.reducers = (config, action) => {
        const _editInput = () => {
            const newQuery = newConfig.inputParams.find(item => item.name === "query");
            action.changes.map(change => {
                newQuery[change.key] = change.value;
            })
        }

        const _addKnowledge = () => {
            getKnowledgeValue().push({
                id: action.id,
                name: "",
                type: "Object",
                from: "Expand",
                value: []
            });
        };

        const _deleteKnowledge = () => {
            const knowledgeValue = getKnowledgeValue();
            const indexToDelete = knowledgeValue.findIndex(item => item.id === action.id);
            indexToDelete !== -1 && knowledgeValue.splice(indexToDelete, 1);
        };

        const _editKnowledge = () => {
            if (!action.value) {
                return;
            }
            const knowledgeId = action.value.id;
            const knowledgeName = action.value.name;
            const value = getKnowledgeValue().find(item => item.id === action.id).value;
            if (value.length === 0) {
                value.push({id: uuidv4(), name: 'id', from: 'Input', type: 'String', value: knowledgeId});
                value.push({id: uuidv4(), name: 'name', from: 'Input', type: 'String', value: knowledgeName});
            } else {
                value.forEach(item => {
                    if (item.name === 'id') {
                        item.value = knowledgeId;
                    }
                    if (item.name === 'name') {
                        item.value = knowledgeName;
                    }
                });
            }
        };

        const _changeMaximum = () => {
            newConfig.inputParams.filter(newTask => newTask.name === "maximum").forEach(item => {
                item.value = action.value;
            });
        };

        const getKnowledgeValue = () => newConfig.inputParams.find(newTask => newTask.name === "knowledge").value;

        const _clearKnowledge = () => {
            getKnowledgeValue().find(item => item.id === action.id).value = [];
        };

        let newConfig = {...config};
        switch (action.type) {
            // 格式：dispatch({type: 'editInput', item:{name: "Query", type: "String", from: "Reference", value: ""})
            case 'editInput':
                _editInput();
                return newConfig;
            case 'addKnowledge':
                _addKnowledge();
                return newConfig;
            // 格式：dispatch({type: 'deleteOutputVariable', id:"id")
            case 'deleteKnowledge':
                _deleteKnowledge();
                return newConfig;
            // 格式：dispatch({type: 'editKnowledge', item:{id: 0, name: "", type: "", from: "value", value: ""})
            case 'editKnowledge':
                _editKnowledge();
                return newConfig;
            // 格式：dispatch({type: 'changemaximum', value:"")
            case 'changeMaximum':
                _changeMaximum();
                return newConfig;
            case 'clearKnowledge':
                _clearKnowledge();
                return newConfig;
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
}
