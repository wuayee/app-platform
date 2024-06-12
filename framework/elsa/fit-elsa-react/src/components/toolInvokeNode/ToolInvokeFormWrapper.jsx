import {useDataContext} from "@/components/DefaultRoot.jsx";
import InvokeInput from "@/components/common/InvokeInput.jsx";
import InvokeOutput from "@/components/common/InvokeOutput.jsx";

/**
 * 工具调用表单Wrapper
 *
 * @returns {JSX.Element} 工具调用表单Wrapper的DOM
 */
export default function ToolInvokeFormWrapper() {
    const data = useDataContext();
    const inputData = data && data.inputParams;

    return (<>
        <InvokeInput inputData={inputData}/>
        <InvokeOutput/>
    </>);
}