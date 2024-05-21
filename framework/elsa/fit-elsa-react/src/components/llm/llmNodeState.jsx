import {jadeNode} from "@/components/jadeNode.jsx";
import LlmIcon from '../asserts/icon-llm.svg?react'; // 导入背景图片
import {Button} from "antd";
import "./style.css";
import {v4 as uuidv4} from "uuid";

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

    return self;
};