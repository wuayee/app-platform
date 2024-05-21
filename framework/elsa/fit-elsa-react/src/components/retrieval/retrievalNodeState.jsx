import {jadeNode} from "@/components/jadeNode.jsx";
import "./style.css";
import {Button} from "antd";
import RetrievalIcon from '../asserts/icon-retrieval.svg?react';
import InputForm from "@/components/retrieval/InputForm.jsx";
import KnowledgeForm from "@/components/retrieval/KnowledgeForm.jsx";
import OutputForm from "@/components/retrieval/OutputForm.jsx";

/**
 * 知识检索shape
 *
 * @override
 */
export const retrievalNodeState = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "retrievalNodeState";
    self.backColor = 'white';
    self.pointerEvents = "auto";
    self.text = "普通检索";
    self.componentName = "retrievalComponent";
    self.flowMeta.jober.fitables.push("com.huawei.fit.jober.aipp.fitable.NaiveRAGComponent");
    self.flowMeta.triggerMode = 'auto';

    /**
     * @override
     */
    self.getReactComponents = () => {
        return (<>
            <InputForm/>
            <KnowledgeForm/>
            <OutputForm/>
        </>);
    };

    self.getHeaderIcon = () => {
        return (
                <Button
                        disabled={true}
                        className="jade-node-custom-header-icon"
                >
                    <RetrievalIcon/>
                </Button>
        );
    };

    return self;
}