import {Input, Form} from "antd";
import PropTypes from "prop-types";

const {TextArea} = Input;

Description.propTypes = {
    propValue: PropTypes.string.isRequired, // 确保 propValue 是一个必需的字符串
    disableModifiable: PropTypes.bool.isRequired, // 确保 disableModifiable 是一个必需的bool值
    onChange: PropTypes.func.isRequired, // 确保 onChange 是一个必需的函数
};

/**
 * 开始节点关于入参的描述
 *
 * @param itemId 名称所属Item的唯一标识
 * @param propValue 描述的初始值
 * @param disableModifiable 该字段是否禁止修改
 * @param onChange 值被修改时调用的函数
 * @returns {JSX.Element} 开始节点关于入参描述的Dom
 */
export default function Description({itemId, propValue, disableModifiable, onChange}) {
    return (<Form.Item
        className="jade-form-item"
        label="字段描述"
        name={`description-${itemId}`}
        rules={[{required: true, message: "参数描述不能为空"}]}
        initialValue={propValue}
    >
        <TextArea
            className="jade-input"
            value={propValue}
            disabled={disableModifiable}
            onChange={e => onChange("description", e.target.value)} // 当文本输入框的值发生变化时调用父组件传递的回调函数
            placeholder="请输入字段描述"
            autoSize={{minRows: 4, maxRows: 4}} // 设置最小和最大行数，当内容小于或大于行数时，文本框大小不会变化
        />
    </Form.Item>);
}