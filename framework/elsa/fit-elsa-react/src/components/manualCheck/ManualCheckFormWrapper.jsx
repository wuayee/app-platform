import "./style.css";
import {useDataContext, useDispatch} from "@/components/DefaultRoot.jsx";
import ManualCheckForm from "@/components/manualCheck/ManualCheckForm.jsx";

/**
 * 人工检查表单Wrapper
 *
 * @returns {JSX.Element} 人工检查表单Wrapper的DOM
 */
export default function ManualCheckFormWrapper() {
    const data = useDataContext();
    const dispatch = useDispatch();

    const handleFormChange = (changeFormName, changeFormId, formOutput) => {
        dispatch({
            actionType: "changeFormAndSetOutput",
            formName: changeFormName,
            formId: changeFormId,
            entity: formOutput
        });
    };

    return (<>
        <div>
            <ManualCheckForm data={data} handleFormChange={handleFormChange}/>
        </div>
    </>);
}