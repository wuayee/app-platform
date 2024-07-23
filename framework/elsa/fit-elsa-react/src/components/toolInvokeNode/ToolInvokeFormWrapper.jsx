import {InvokeInput} from "@/components/common/InvokeInput.jsx";
import {InvokeOutput} from "@/components/common/InvokeOutput.jsx";

/**
 * 工具调用表单Wrapper
 *
 * @param disabled 是否禁用.
 * @param data 数据
 * @returns {JSX.Element} 工具调用表单Wrapper的DOM
 */
export default function ToolInvokeFormWrapper({disabled, data}) {
    const inputData = data && data.inputParams;
    const outputData = data && data.outputParams;

    return (<>
        <InvokeInput disabled={disabled} inputData={inputData}/>
        <InvokeOutput outputData={outputData}/>
    </>);
}