import PropTypes from "prop-types";
import {Form} from "antd";
import {JadeStopPropagationSelect} from "../common/JadeStopPropagationSelect.jsx";

Type.propTypes = {
    propValue: PropTypes.string.isRequired, // 确保 propValue 是一个必需的字符串
    onChange: PropTypes.func.isRequired, // 确保 onChange 是一个必需的函数
};

/**
 * 开始节点关于入参的类型
 *
 * @param propValue 类型的初始值
 * @param onChange 值被修改时调用的函数
 * @returns {JSX.Element} 开始节点关于入参类型的Dom
 */
export default function Type({propValue, onChange}) {
    const handleSelectClick = (event) => {
        event.stopPropagation(); // 阻止事件冒泡
    };

    const handleChange = (value) => {
        onChange("type", value); // 当选择框的值发生变化时调用父组件传递的回调函数
        document.activeElement.blur();// 在选择后取消焦点
    };

    return (<Form.Item
        className="jade-form-item"
        label="字段类型"
        name="type"
        initialValue={propValue}
    >
        <JadeStopPropagationSelect
            className="jade-select"
            value={propValue}
            style={{width: "100%"}}
            onClick={handleSelectClick} // 点击下拉框时阻止事件冒泡
            onChange={handleChange}
            options={[{value: 'String', label: 'String'}, {value: 'Integer', label: 'Integer'},
                {value: 'Boolean', label: 'Boolean'}, {value: 'Number', label: 'Number'},]}
        />
    </Form.Item>);
}