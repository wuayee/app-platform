import React, {useEffect, useState} from "react";
import {Header} from "@/components/Header.jsx";

/**
 * 头部.
 *
 * @param shape 图形.
 * @param disabled 是否禁用.
 * @return {JSX.Element}
 * @constructor
 */
export const EndNodeHeader = ({shape, disabled}) => {
    const [, setEndNodeCount] = useState(0);

    useEffect(() => {
        // 节点数量变化
        shape.page.addEventListener("TOOL_MENU_CHANGE", (value) => {
            setEndNodeCount(value[0])
        });
    }, []);

    return (<>
        <Header shape={shape} disabled={disabled}/>
    </>);
};