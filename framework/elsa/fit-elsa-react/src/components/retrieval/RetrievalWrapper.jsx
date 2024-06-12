import InputForm from "@/components/retrieval/InputForm.jsx";
import KnowledgeForm from "@/components/retrieval/KnowledgeForm.jsx";
import OutputForm from "@/components/retrieval/OutputForm.jsx";

/**
 * retrieval组件Wrapper
 *
 * @param disabled 是否禁用.
 * @returns {JSX.Element} retrieval组件dom
 */
export const RetrievalWrapper = ({disabled}) => {
    return (<>
        <InputForm disabled={disabled}/>
        <KnowledgeForm disabled={disabled}/>
        <OutputForm/>
    </>);
};