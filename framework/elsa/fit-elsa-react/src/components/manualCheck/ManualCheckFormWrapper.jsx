import "./style.css";
import {useDataContext, useDispatch} from "@/components/DefaultRoot.jsx";
import {JadeObservableTree} from "@/components/common/JadeObservableTree.jsx";
import ManualCheckForm from "@/components/manualCheck/ManualCheckForm.jsx";
import JadePanelCollapse from "@/components/manualCheck/JadePanelCollapse.jsx";

/**
 * 人工检查表单Wrapper
 *
 * @returns {JSX.Element} 人工检查表单Wrapper的DOM
 */
export default function ManualCheckFormWrapper() {
    const data = useDataContext();
    const dispatch = useDispatch();
    const output = data.outputParams;

    /**
     * 渲染输出组件
     *
     * @return {JSX.Element|null} output组件
     */
    const renderOutput = () => {
        if (!output || !Array.isArray(output) || !output.length > 0) {
            return null;
        }
        return (<>
            <JadePanelCollapse
                    defaultActiveKey={["manualCheckOutputPanel"]}
                    panelKey="manualCheckOutputPanel"
                    headerText="输出"
            >
                <JadeObservableTree data={output}/>
            </JadePanelCollapse>
        </>);
    };

    const handleFormChange = (changeFormName, changeFormId, formOutput) => {
        dispatch({
            actionType: "changeFormAndSetOutput",
            formName: changeFormName,
            formId: changeFormId,
            formOutput: formOutput
        });
    };

    return (<>
        <div>
            <ManualCheckForm data={data} handleFormChange={handleFormChange}/>
            {renderOutput()}
        </div>
    </>);
}