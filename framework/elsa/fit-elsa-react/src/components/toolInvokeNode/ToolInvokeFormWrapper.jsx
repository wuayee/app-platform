import {InvokeInput} from "@/components/common/InvokeInput.jsx";
import {InvokeOutput} from "@/components/common/InvokeOutput.jsx";
import PropTypes from "prop-types";

ToolInvokeFormWrapper.propTypes = {
    shapeStatus: PropTypes.object,
    data: PropTypes.object
};

/**
 * 工具调用表单Wrapper
 *
 * @param shapeStatus 节点状态.
 * @param data 数据
 * @returns {JSX.Element} 工具调用表单Wrapper的DOM
 */
export default function ToolInvokeFormWrapper({shapeStatus, data}) {
    const inputData = data && data.inputParams;
    const outputData = data && data.outputParams;

    return (<>
        <InvokeInput shapeStatus={shapeStatus} inputData={inputData}/>
        <InvokeOutput outputData={outputData}/>
    </>);
}