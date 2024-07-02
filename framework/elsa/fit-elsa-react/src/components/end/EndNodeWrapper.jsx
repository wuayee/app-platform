import SelectMode from "@/components/end/SelectMode.jsx";
import OutputVariable from "@/components/end/OutputVariable.jsx";
import ManualCheckForm from "@/components/manualCheck/ManualCheckForm.jsx";
import {useDataContext, useDispatch} from "@/components/DefaultRoot.jsx";

/**
 * 用来封装结束节点子组件的最顶层组件
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function EndNodeWrapper({disabled}) {
    const data = useDataContext();
    const dispatch = useDispatch();
    const mode = data.inputParams.find(item => item.name === "finalOutput") ? "variables" : "manualCheck";

    /**
     * 表单更改后的回调
     *
     * @param changeFormName 表单名
     * @param changeFormId 表单id
     * @param formOutput 表单输出
     */
    const handleFormChange = (changeFormName, changeFormId, formOutput) => {
        dispatch({
            type: "changeForm",
            formName: changeFormName,
            formId: changeFormId,
            entity: formOutput
        });
    };

    /**
     * 通过模式渲染结束节点
     *
     * @param mode
     * @return {JSX.Element}
     */
    const renderByMode = (mode) => {
        if (mode === "variables") {
            return (<OutputVariable disabled={disabled}/>);
        } else {
            return (<ManualCheckForm formName={data.inputParams.find(item => item.name === "endFormName").value}
                                     taskId={data.inputParams.find(item => item.name === "endFormId").value}
                                     handleFormChange={handleFormChange}/>);
        }
    };

    return (<>
        <div style={{backgroundColor: 'white'}}>
            <SelectMode mode={mode}/>
            {renderByMode(mode)}
        </div>
    </>);
}