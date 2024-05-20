import PropTypes from 'prop-types';
import {Form} from "antd";
import {JadeObservableInput} from "../common/JadeObservableInput.jsx";

Name.propTypes = {
    itemId: PropTypes.string.isRequired, // 确保 itemId 是一个必需的字符串
    propValue: PropTypes.string.isRequired, // 确保 propValue 是一个必需的字符串
    type: PropTypes.string.isRequired, // 确保 type 是一个必需的字符串
    disableModifiable: PropTypes.bool.isRequired, // 确保 disableModifiable 是一个必需的bool值
    onChange: PropTypes.func.isRequired, // 确保 onChange 是一个必需的函数
};

/**
 * 开始节点关于入参的名称
 *
 * @param itemId 名称所属Item的唯一标识
 * @param propValue 名称的初始值
 * @param type 名称对应的字段类型
 * @param disableModifiable 该字段是否禁止修改
 * @param onChange 值被修改时调用的函数
 * @returns {JSX.Element} 开始节点关于入参名称的Dom
 */
export default function Name({itemId, propValue, type, disableModifiable, onChange}) {
    return (<Form.Item
        className="jade-form-item"
        label="字段名称"
        name={`name-${itemId}`}
        rules={[{required: true, message: "参数名称不能为空"},
            {pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/, message: '只能包含字母、数字或下划线，且必须以字母或下划线开头'}]}
        validateTrigger="onBlur"
        initialValue={propValue}
    >
        <JadeObservableInput
            className="jade-input"
            id={itemId}
            value={propValue}
            type={type}
            disabled={disableModifiable}
            placeholder="请输入字段名称"
            showCount
            maxLength={20}
            onChange={e => onChange && onChange("name", e.target.value)} // 当输入框的值发生变化时调用父组件传递的回调函数
        />
    </Form.Item>);
}

