import {useDataContext} from "@/components/DefaultRoot.jsx";
import InvokeInput from "@/components/common/InvokeInput.jsx";
import {CustomizedModelSelect} from "@/components/common/CustomizedModelSelect.jsx";
import InvokeOutput from "@/components/common/InvokeOutput.jsx";

/**
 * HuggingFace表单Wrapper
 *
 * @param disabled 是否禁用.
 * @returns {JSX.Element} HuggingFace表单Wrapper的DOM
 */
export default function HuggingFaceFormWrapper({disabled}) {
    const data = useDataContext();
    const inputData = data && data.inputParams;
    const filteredInputData = inputData ? inputData.slice(2) : [];
    const modelDefaultValue = inputData ? inputData[1].value : undefined;

    return (<>
        <InvokeInput inputData={filteredInputData} disabled={disabled}/>
        <CustomizedModelSelect defaultValue={modelDefaultValue} disabled={disabled}/>
        <InvokeOutput disabled={disabled}/>
    </>);
}