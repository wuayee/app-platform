import React, {useEffect, useState} from "react";
import {Header} from "@/components/Header.jsx";
import PropTypes from "prop-types";

/**
 * 头部.
 *
 * @param shape 图形.
 * @param shapeStatus 图形状态集合.
 * @return {JSX.Element}
 * @constructor
 */
export const EndNodeHeader = ({shape, shapeStatus}) => {
    const [, setEndNodeCount] = useState(0);

    useEffect(() => {
        // 节点数量变化
        shape.page.addEventListener("TOOL_MENU_CHANGE", (value) => {
            setEndNodeCount(value[0])
        });
    }, []);

    return (<>
        <Header shape={shape} shapeStatus={shapeStatus}/>
    </>);
};

EndNodeHeader.propTypes = {
    shape: PropTypes.object,
    shapeStatus: PropTypes.object
};