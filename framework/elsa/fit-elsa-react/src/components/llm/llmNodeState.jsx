import {jadeNode} from "@/components/jadeNode.jsx";
import LlmIcon from '../asserts/icon-llm.svg?react'; // 导入背景图片
import {Button} from "antd";
import "./style.css";
import {SECTION_TYPE} from "@/common/Consts.js";

/**
 * jadeStream中的大模型节点.
 *
 * @override
 */
export const llmNodeState = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "llmNodeState";
    self.text = "大模型";
    self.pointerEvents = "auto";
    self.componentName = "llmComponent";
    self.flowMeta.jober.fitables.push("com.huawei.fit.jober.aipp.fitable.LLMComponent");
    self.flowMeta.jober.isAsync = "true";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (
            <Button
                disabled={true}
                className="jade-node-custom-header-icon"
            >
                <LlmIcon/>
            </Button>
        );
    };

    /**
     * 获取大模型节点测试报告章节
     */
    self.getRunReportSections = () => {
        // 这里的data是每个节点的每个章节需要展示的数据，比如工具节点展示为输入、输出的数据
        return [{
            no: "1",
            name: "输入",
            type: SECTION_TYPE.DEFAULT,
            data: self.input ? self.input.prompt.variables : {}
        }, {
            no: "2",
            name: "输出",
            type: SECTION_TYPE.DEFAULT,
            data: self.getOutputData(self.output)
        }];
    };

    return self;
};