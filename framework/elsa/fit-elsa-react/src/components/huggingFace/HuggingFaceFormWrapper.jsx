import {useDataContext} from "@/components/DefaultRoot.jsx";
import InvokeInput from "@/components/common/InvokeInput.jsx";
import {CustomizedModelSelect} from "@/components/common/CustomizedModelSelect.jsx";
import InvokeOutput from "@/components/common/InvokeOutput.jsx";
import {JADE_MODEL_PREFIX, JADE_TASK_ID_PREFIX} from "@/common/Consts.js";

/**
 * HuggingFace表单Wrapper
 *
 * @returns {JSX.Element} HuggingFace表单Wrapper的DOM
 */
export default function HuggingFaceFormWrapper() {
    const data = useDataContext();
    const inputData = data && data.inputParams;
    const filteredInputData = inputData ? inputData.filter(item => !item.id.startsWith(JADE_TASK_ID_PREFIX) && !item.id.startsWith(JADE_MODEL_PREFIX)) : [];
    const modelDefaultValue = inputData ? inputData.find(item => item.id.startsWith(JADE_MODEL_PREFIX)).value : undefined;

    return (<>
        <InvokeInput inputData={filteredInputData}/>
        <CustomizedModelSelect defaultValue={modelDefaultValue}/>
        <InvokeOutput/>
    </>);
}