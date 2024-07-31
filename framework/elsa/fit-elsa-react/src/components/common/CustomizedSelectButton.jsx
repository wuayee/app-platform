import {Button} from "antd";
import React from "react";
import {useShapeContext} from "@/components/DefaultRoot.jsx";
import PropTypes from "prop-types";

_CustomizedSelectButton.propTypes = {
    buttonText: PropTypes.string,
    disabled: PropTypes.bool,
    customizedEvent: PropTypes.object
}

/**
 * 自定义选择按钮组件，可以自由选择按钮中文本及点击按钮后触发事件.
 * 该组件点击选择时触发自定义事件，如何选择由事件监听方定义，选择后调用回调设置即可.
 *
 * @param disabled 是否禁用.
 * @param buttonText 按钮文本.
 * @param customizedEvent 触发的自定义事件.
 * @return {JSX.Element} 组件.
 * @constructor
 */
function _CustomizedSelectButton({disabled, buttonText, customizedEvent}) {
    const shape = useShapeContext();

    const triggerSelect = (e) => {
        e.preventDefault();
        shape.page.triggerEvent(customizedEvent);
    };

    return (<>
            <Button className={"jade-custom-select-button"} disabled={disabled} onClick={e => triggerSelect(e)}>
                {buttonText}
            </Button>
    </>);
}

const areEqual = (prevProps, nextProps) => {
    return prevProps.disabled === nextProps.disabled;
};

export const CustomizedSelectButton = React.memo(_CustomizedSelectButton, areEqual);