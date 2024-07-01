import React from "react";
import OfficialIcon from "./asserts/icon-official.svg?react";
import HuggingFaceIcon from "./asserts/icon-huggingface.svg?react";
import LlamaIndexIcon from "./asserts/icon-llamaindex.svg?react";
import LangChainIcon from "./asserts/icon-langchain.svg?react";
import {Button} from "antd";
import {SOURCE_PLATFORM} from "@/common/Consts.js";

// 使用对象字面量来创建图标映射
const iconMap = {
    [SOURCE_PLATFORM.OFFICIAL]: <OfficialIcon className="jade-node-footer-icon" style={{height: "20px", width: "50px"}}/>,
    [SOURCE_PLATFORM.HUGGING_FACE]: <HuggingFaceIcon className="jade-node-footer-icon" style={{height: "20px", width: "100px"}}/>,
    [SOURCE_PLATFORM.LLAMA_INDEX]: <LlamaIndexIcon className="jade-node-footer-icon" style={{height: "20px", width: "100px"}}/>,
    [SOURCE_PLATFORM.LANG_CHAIN]: <LangChainIcon className="jade-node-footer-icon" style={{height: "20px", width: "100px"}}/>,
};

/**
 * 脚部.
 *
 * @param shape 图形.
 * @return {JSX.Element}
 */
export const Footer = ({shape}) => {
    const getSourceIcon = (sourcePlatform) => {
        return iconMap[sourcePlatform] || iconMap[SOURCE_PLATFORM.OFFICIAL];
    };

    return (<>
        <div className="react-node-footer">
            <div style={{display: "flex", alignItems: "center"}}>
                <Button disabled={true} className="jade-node-footer-button">
                    {getSourceIcon(shape.sourcePlatform)}
                </Button>
            </div>
        </div>
    </>);
};