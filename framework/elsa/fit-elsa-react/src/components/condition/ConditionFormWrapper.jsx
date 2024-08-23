import IfForm from "@/components/condition/IfForm.jsx";
import ElseForm from "@/components/condition/ElseForm.jsx";
import {useDispatch} from "@/components/DefaultRoot.jsx";
import {Button} from "antd";
import {PlusOutlined} from "@ant-design/icons";
import {ConnectorProvider} from "@/components/common/ConnectorProvider.jsx";
import PropTypes from "prop-types";
import {useTranslation} from "react-i18next";
import {NODE_STATUS} from "@";

ConditionFormWrapper.propTypes = {
    shapeStatus: PropTypes.object,
    data: PropTypes.object
};

/**
 * 条件节点表单Wrapper
 *
 * @param shapeStatus 节点状态
 * @param data jadeConfig
 * @returns {JSX.Element} 条件节点表单Wrapper的DOM
 */
export default function ConditionFormWrapper({shapeStatus, data}) {
    const dispatch = useDispatch();

    const {t} = useTranslation();

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

    // 是否需要禁用branch.
    const isDisabled = (branch) => {
        const runStatus = shapeStatus.runStatus;
        return runStatus === NODE_STATUS.UN_RUNNING
            || runStatus === NODE_STATUS.RUNNING
            ||  !branch.runnable && shapeStatus.disabled;
    };

    return (<div>
        <div style={{
            display: "flex", alignItems: "center", marginBottom: "8px", paddingLeft: "8px", paddingRight: "4px"
        }}>
            <div className="jade-panel-header-font">{t('conditionBranch')}</div>
            <Button type="link" className="icon-button"
                    onClick={addBranch}
                    disabled={shapeStatus.disabled}
                    style={{"height": "32px", marginLeft: "auto"}}>
                <PlusOutlined/>
                <span>{t('addBranch')}</span>
            </Button>
        </div>
        {branches.filter(branch => branch.type === "if").map((branch, index) => (
            <ConnectorProvider key={"dynamic-" + index} name={"dynamic-" + index + "|" + branch.id}>
                <IfForm key={branch.id}
                        branch={branch}
                        index={index}
                        name={index === 0 ? "If" : "Else if"}
                        totalItemNum={branches.length + 1}
                        disabled={isDisabled(branch)}
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