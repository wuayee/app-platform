import {Select} from "antd";

/**
 * 不会受Elsa冒泡机制影响的Select组件.
 *
 * @param props 参数.
 * @return {JSX.Element}
 * @constructor
 */
export const JadeStopPropagationSelect = (props) => {
    const {onMouseDown, ...rest} = props;

    /**
     * 选择框被鼠标点击时调用.
     *
     * @param e 事件对象.
     * @private
     */
    const _onMouseDown = (e) => {
        onMouseDown && onMouseDown(e);

        // 取消onMouseDown事件的冒泡.
        e.stopPropagation();
    };

    return <><Select onMouseDown={(e) => _onMouseDown(e)} {...rest}/></>
};