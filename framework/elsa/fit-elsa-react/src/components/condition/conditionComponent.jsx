import ConditionFormWrapper from "@/components/condition/ConditionFormWrapper.jsx";
import {v4 as uuidv4} from "uuid";

export const conditionComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            branches: [
                {
                    id: uuidv4(),
                    conditionRelation: "and",
                    type: "if",
                    conditions: [
                        {
                            id: uuidv4(),
                            condition: undefined,
                            value: [
                                {
                                    id: uuidv4(),
                                    name: "left",
                                    type: "",
                                    from: "Reference",
                                    value: "",
                                    referenceNode: "",
                                    referenceId: "",
                                    referenceKey: ""
                                },
                                {
                                    id: uuidv4(),
                                    name: "right",
                                    type: "",
                                    from: "Reference",
                                    value: "",
                                    referenceNode: "",
                                    referenceId: "",
                                    referenceKey: ""
                                }
                            ]
                        },
                    ]
                },
                {
                    id: uuidv4(),
                    conditionRelation: "and",
                    type: "else",
                    conditions: [
                        {
                            id: uuidv4(),
                            condition: "true",
                            value: []
                        },
                    ]
                }
            ]
        };
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><ConditionComponent/></>);
    };

    /**
     * 必须.
     */
    self.reducers = (data, action) => {
        // Functions to be used for updating the data
        const changeConditionConfig = () => {
            return new Data(data).updateBranch(action, branchUpdater => branchUpdater.updateCondition(action));
        }

        const deleteBranch = () => {
            return new Data(data).deleteBranch(action.branchId);
        }

        const changeConditionRelation = () => {
            return new Data(data).updateBranch(action, branchUpdater => branchUpdater.changeConditionRelation(action.conditionRelation));
        }

        const addCondition = () => {
            const newCondition = {
                id: uuidv4(),
                condition: undefined,
                value: [
                    { id: uuidv4(), name: "left", type: "", from: "Reference", value: [], referenceNode: "" },
                    { id: uuidv4(), name: "right", type: "", from: "Reference", value: [], referenceNode: "" }
                ]
            };
            return new Data(data).updateBranch(action, branchUpdater => branchUpdater.addCondition(newCondition));
        }

        const deleteCondition = () => {
            return new Data(data).updateBranch(action, branchUpdater => branchUpdater.deleteCondition(action.conditionId));
        }

        const addBranch = () => {
            return new Data(data).addBranch();
        }

        switch (action.actionType) {
            case 'addBranch': {
                return addBranch();
            }
            case 'deleteBranch': {
                return deleteBranch();
            }
            case 'changeConditionRelation': {
                return changeConditionRelation();
            }
            case 'addCondition': {
                return addCondition();
            }
            case 'deleteCondition': {
                return deleteCondition();
            }
            case 'changeConditionConfig': {
                return changeConditionConfig();
            }
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
};

class Condition {
    constructor(condition) {
        this.condition = { ...condition };
    }

    updateValue(item) {
        this.condition.value = this.condition.value.map(conditionValue => {
            if (item.key === conditionValue.id) {
                return {
                    ...conditionValue,
                    ...Object.fromEntries(item.value.map(itemValue => [itemValue.key, itemValue.value]))
                };
            }
            return conditionValue;
        });
    }

    update(updateParams) {
        updateParams.forEach(item => {
            if (item.key === "condition") {
                this.condition[item.key] = item.value;
            } else {
                this.updateValue(item);
            }
        });
        return this.condition;
    }
}

class Branch {
    constructor(branch) {
        this.branch = { ...branch };
    }

    static createNewBranch() {
        return {
            id: uuidv4(),
            conditionRelation: "and",
            type: "if",
            conditions: [
                {
                    id: uuidv4(),
                    condition: "",
                    value: [
                        {
                            id: uuidv4(),
                            name: "left",
                            type: "",
                            from: "Reference",
                            value: [],
                            referenceNode: ""
                        },
                        {
                            id: uuidv4(),
                            name: "right",
                            type: "",
                            from: "Reference",
                            value: [],
                            referenceNode: ""
                        }
                    ]
                }
            ]
        };
    }

    updateCondition(action) {
        this.branch.conditions = this.branch.conditions.map(condition => {
            if (condition.id === action.conditionId) {
                return new Condition(condition).update(action.updateParams);
            }
            return condition;
        });
        return this.branch;
    }

    deleteCondition(conditionId) {
        this.branch.conditions = this.branch.conditions.filter(condition => condition.id !== conditionId);
        return this.branch;
    }

    addCondition(newCondition) {
        this.branch.conditions.push(newCondition);
        return this.branch;
    }

    changeConditionRelation(conditionRelation) {
        this.branch.conditionRelation = conditionRelation;
        return this.branch;
    }
}

class Data {
    constructor(data) {
        this.data = { ...data };
    }

    updateBranch(action, updateBranchFn) {
        this.data.branches = this.data.branches.map(branch => {
            if (branch.id === action.branchId) {
                return updateBranchFn(new Branch(branch));
            }
            return branch;
        });
        return this.data;
    }

    deleteBranch(branchId) {
        this.data.branches = this.data.branches.filter(branch => branch.id !== branchId);
        return this.data;
    }

    addBranch() {
        const newBranch = Branch.createNewBranch();
        const elseBranchIndex = this.data.branches.findIndex(branch => branch.type === "else");

        if (elseBranchIndex !== -1) {
            this.data.branches.splice(elseBranchIndex, 0, newBranch);
        } else {
            this.data.branches.push(newBranch);
        }
        return this.data;
    }
}

const ConditionComponent = () => {
    return (<>
        <ConditionFormWrapper/>
    </>)
};