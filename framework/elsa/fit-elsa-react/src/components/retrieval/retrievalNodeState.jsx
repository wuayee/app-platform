import {jadeNode} from "@/components/jadeNode.jsx";
import "./style.css";
import {Button} from "antd";
import RetrievalIcon from '../asserts/icon-retrieval.svg?react';
import {SECTION_TYPE} from "@/common/Consts.js";

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
    self.getHeaderIcon = () => {
        return (<>
            <Button disabled={true} className="jade-node-custom-header-icon">
                <RetrievalIcon/>
            </Button>
        </>);
    };

    /**
     * 获取知识检索节点测试报告章节
     */
    self.getRunReportSections = () => {
        // 这里的data是每个节点的每个章节需要展示的数据，比如工具节点展示为输入、输出的数据
        let data = {};
        if (self.input) {
            data = {query: self.input.query};
        }
        return [{
            no: "1",
            name: "输入",
            type: SECTION_TYPE.DEFAULT,
            data: data
        }, {
            no: "2",
            name: "输出",
            type: SECTION_TYPE.DEFAULT,
            data: self.getOutputData(self.output)
        }];
    };

    return self;
}