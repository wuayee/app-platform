import IfForm from "@/components/condition/IfForm.jsx";
import ElseForm from "@/components/condition/ElseForm.jsx";
import {useDispatch} from "@/components/DefaultRoot.jsx";
import {Button} from "antd";
import {PlusOutlined} from "@ant-design/icons";
import {ConnectorProvider} from "@/components/common/ConnectorProvider.jsx";

/**
 * 条件节点表单Wrapper
 *
 * @returns {JSX.Element} 条件节点表单Wrapper的DOM
 */
export default function ConditionFormWrapper({disabled, data}) {
    const dispatch = useDispatch();

    const branches = data.branches;

    const addBranch = () => {
        dispatch({actionType: "addBranch"});
    };

    const deleteBranch = (branchId) => {
        dispatch({actionType: "deleteBranch", branchId: branchId});
    };

    const changeConditionRelation = (branchId, conditionRelation) => {
        dispatch({actionType: "changeConditionRelation", branchId: branchId, conditionRelation: conditionRelation});
    };

    const addCondition = (branchId) => {
        dispatch({actionType: "addCondition", branchId: branchId});
    };

    const deleteCondition = (branchId, conditionId) => {
        dispatch({actionType: "deleteCondition", branchId: branchId, conditionId: conditionId});
    };

    const changeConditionConfig = (branchId, conditionId, updateParams) => {
        dispatch({
            actionType: "changeConditionConfig",
            branchId: branchId,
            conditionId: conditionId,
            updateParams: updateParams
        });
    };

    return (<div>
        <div style={{
            display: "flex", alignItems: "center", marginBottom: "8px", paddingLeft: "8px", paddingRight: "4px"
        }}>
            <div className="jade-panel-header-font">条件分支</div>
            <Button type="link" className="icon-button"
                    onClick={addBranch}
                    disabled={disabled}
                    style={{"height": "32px", marginLeft: "auto"}}>
                <PlusOutlined/>
                <span>添加分支</span>
            </Button>
        </div>
        {branches.filter(branch => branch.type === "if").map((branch, index) => (
                <ConnectorProvider key={"dynamic-" + index} name={"dynamic-" + index + "|" + branch.id}>
                    <IfForm key={branch.id}
                            branch={branch}
                            index={index}
                            name={index === 0 ? "If" : "Else if"}
                            totalItemNum={branches.length + 1}
                            disabled={disabled}
                            deleteBranch={deleteBranch}
                            changeConditionRelation={changeConditionRelation}
                            addCondition={addCondition}
                            deleteCondition={deleteCondition}
                            changeConditionConfig={changeConditionConfig}/>
                </ConnectorProvider>
        ))}
        <ConnectorProvider name={`dynamic-${branches.length + 1}`}>
            <ElseForm/>
        </ConnectorProvider>
    </div>);
}