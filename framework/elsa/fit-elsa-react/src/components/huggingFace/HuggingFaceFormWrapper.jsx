import {InvokeInput} from "@/components/common/InvokeInput.jsx";
import {CustomizedModelSelect} from "@/components/common/CustomizedModelSelect.jsx";
import {InvokeOutput} from "@/components/common/InvokeOutput.jsx";

/**
 * HuggingFace表单Wrapper
 *
 * @param disabled 是否禁用.
 * @param data 数据
 * @returns {JSX.Element} HuggingFace表单Wrapper的DOM
 */
export default function HuggingFaceFormWrapper({disabled, data}) {
    const inputData = data && data.inputParams;
    const outputData = data && data.outputParams;
    const filteredInputData = inputData ? inputData.slice(2) : [];
    const modelDefaultValue = inputData ? inputData[1].value : undefined;

    return (<>
        <InvokeInput inputData={filteredInputData} disabled={disabled}/>
        <div style={{marginTop: "16px", marginBottom: "16px"}}>
            <CustomizedModelSelect defaultValue={modelDefaultValue} disabled={disabled}/>
        </div>
        <InvokeOutput outputData={outputData}/>
    </>);
}