import {InputForm} from "@/components/retrieval/InputForm.jsx";
import {KnowledgeForm} from "@/components/retrieval/KnowledgeForm.jsx";
import {OutputForm} from "@/components/retrieval/OutputForm.jsx";
import PropTypes from "prop-types";

RetrievalWrapper.propTypes = {
    data: PropTypes.object.isRequired,
    disabled: PropTypes.bool
};


/**
 * retrieval组件Wrapper
 *
 * @param data 数据.
 * @param disabled 是否禁用.
 * @returns {JSX.Element} retrieval组件dom
 */
export default function RetrievalWrapper({data, disabled}) {
    const queryData = data && data.inputParams.find(item => item.name === "query");
    const knowledge = data && (data.inputParams.find(item => item.name === "knowledge")?.value ?? []);
    const maximum = data && data.inputParams.find(item => item.name === "maximum").value;
    const outputParams = data && data.outputParams;

    return (<>
        <InputForm disabled={disabled} queryData={queryData}/>
        <KnowledgeForm knowledge={knowledge} maximum={maximum} disabled={disabled}/>
        <OutputForm outputParams={outputParams}/>
    </>);
}