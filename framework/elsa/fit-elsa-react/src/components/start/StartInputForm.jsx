import {Form} from "antd";
import Name from './Name.jsx';
import Type from './Type.jsx';
import Description from './Description.jsx';
import PropTypes from 'prop-types'; // 导入 PropTypes
import {useDispatch} from "@/components/DefaultRoot.jsx";

StartInputForm.propTypes = {
    item: PropTypes.shape({
        id: PropTypes.string.isRequired,
        name: PropTypes.string.isRequired,
        type: PropTypes.string.isRequired,
        description: PropTypes.string.isRequired,
        from: PropTypes.string.isRequired,
        value: PropTypes.string.isRequired,
    }).isRequired,
    formName: PropTypes.string.isRequired, // 确保 formName 属性是一个必需的字符串类型
};

/**
 * 开始节点入参表单
 *
 * @param item 表单对应的对象
 * @param formName 表单名称
 * @returns {JSX.Element} 开始节点入参表单的DOM
 */
export default function StartInputForm({item, formName}) {
    const dispatch = useDispatch();
    const formId = item.id;

    /**
     * 传递至子组件，用于使用dispatch更新
     *
     * @param type 子组件对应的type
     * @param value 更新后的value
     */
    const handleFormValueChange = (type, value) => {
        dispatch({actionType: "changeInputParam", id: formId, type: type, value: value});
    };

    return (<>
        <Form
            name={formName} // 使用通过 props 传入的 formName
            labelCol={{
                span: 8, // wrapperCol宽度24意味着 label的布局占8格
            }}
            wrapperCol={{
                span: 24, // 在 Ant Design 的栅格系统中，默认将页面分为 24 格，因此每行的总宽度为 24, wrapperCol宽度24意味着 表单项内容的布局占满一行
            }}
            style={{
                paddingTop: "8px",
                maxWidth: 600, // 禁用表单字段的自动填充功能，这样用户就无法从浏览器的自动填充列表中选择之前输入过的值来填充表单字段
            }}
            layout="vertical" // 设置全局的垂直布局
            autoComplete="off" // 禁用表单字段的自动填充功能，这样用户就无法从浏览器的自动填充列表中选择之前输入过的值来填充表单字段
        >
            <Name itemId={item.id} propValue={item.name} onChange={handleFormValueChange}/>
            <Type propValue={item.type} onChange={handleFormValueChange}/>
            <Description propValue={item.description} onChange={handleFormValueChange}/>
            {/*0430版本暂不需要required参数*/}
            {/*<Form.Item name="required">*/}
            {/*    <Required/>*/}
            {/*</Form.Item>*/}
        </Form>
    </>);
}