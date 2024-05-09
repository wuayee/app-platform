import {Slider} from "antd";
import "./style.css";
import PropTypes from "prop-types";

ByConversationTurn.propTypes = {
    propValue: PropTypes.string.isRequired, // 确保 propValue 是一个必需的number类型
    onValueChange: PropTypes.func.isRequired, // 确保 onNameChange 是一个必需的函数类型
};

/**
 * Memory按对话轮次选取
 *
 * @param propValue 前端渲染的值
 * @param onValueChange 参数变化所需调用方法
 * @returns {JSX.Element} Memory按对话轮次的Dom
 */
export default function ByConversationTurn({propValue, onValueChange}) {
    const intValue = parseInt(propValue);

    const defaultRecalls = {
        1: '1',
        [3]: '默认',
        10: '10',
    };

    return (<div style={{display: 'flex', alignItems: 'center'}}>
            <Slider
                style={{width: "95%"}} // 设置固定宽度
                min={1}
                max={10}
                defaultValue={3}
                marks={defaultRecalls}
                step={1} // 设置步长为1
                onChange={e => onValueChange("Integer", e.toString())}
                value={!isNaN(intValue) ? intValue : 3}
            />
        </div>);
}