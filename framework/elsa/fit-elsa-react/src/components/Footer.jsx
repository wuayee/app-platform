import React from "react";
import OfficialIcon from "./asserts/icon-official.svg?react";
import HuggingFaceIcon from "./asserts/icon-huggingface.svg?react";
import LlamaIndexIcon from "./asserts/icon-llamaindex.svg?react";
import LangChainIcon from "./asserts/icon-langchain.svg?react";
import {Button} from "antd";

// 使用对象字面量来创建图标映射
const iconMap = {
    official: <OfficialIcon className="jade-node-footer-icon" style={{height: "20px", width: "50px"}}/>,
    huggingFace: <HuggingFaceIcon className="jade-node-footer-icon" style={{height: "20px", width: "100px"}}/>,
    llamaIndex: <LlamaIndexIcon className="jade-node-footer-icon" style={{height: "20px", width: "100px"}}/>,
    langChain: <LangChainIcon className="jade-node-footer-icon" style={{height: "20px", width: "100px"}}/>,
};

// 将对象转换为 sourceFooterIconMap
const sourceFooterIconMap = new Map(Object.entries(iconMap));

/**
 * 脚部.
 *
 * @param shape 图形.
 * @return {JSX.Element}
 */
export const Footer = ({shape}) => {
    return (<>
        <div className="react-node-footer">
            <div style={{display: "flex", alignItems: "center"}}>
                <Button disabled={true} className="jade-node-footer-button">
                    {sourceFooterIconMap.get(shape.sourcePlatform)}
                </Button>
            </div>
        </div>
    </>);
};