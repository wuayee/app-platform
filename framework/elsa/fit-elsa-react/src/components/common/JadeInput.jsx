import {Button, Col, Collapse, Form, Input, Popover, Row} from 'antd';
import {InfoCircleOutlined, MinusCircleOutlined, PlusOutlined} from '@ant-design/icons';
import {useShapeContext} from "@/components/DefaultRoot.jsx";
import "./style.css";
import PropTypes from "prop-types";
import {v4 as uuidv4} from "uuid";
import {JadeStopPropagationSelect} from "./JadeStopPropagationSelect.jsx";
import {JadeReferenceTreeSelect} from "./JadeReferenceTreeSelect.jsx";

const {Panel} = Collapse;

JadeInput.propTypes = {
    items: PropTypes.array.isRequired, // 确保 items 是一个必需的数组类型
    addItem: PropTypes.func.isRequired, // 确保 addItem 是一个必需的函数类型
    updateItem: PropTypes.func.isRequired, // 确保 updateItem 是一个必需的函数类型
    deleteItem: PropTypes.func.isRequired, // 确保 deleteItem 是一个必需的函数类型
};

/**
 * InputForm组件用于展示一个表单，其中包含一些输入项，这些输入项根据用户选择的值源展示不同的组件。
 *
 * @returns {JSX.Element}
 * @constructor
 */
/**
 * Jade标准输入
 *
 * @param items 需要组件展示的item数据结构的数组
 * @param addItem 当添加一个新item时会调用此方法，此方法需要有id入参
 * @param updateItem 当修改一个已有item时会调用此方法，此方法需要有id，修改的key和修改的value组成的对象列表两个入参
 * @param deleteItem 当删除一个已有item时会调用此方法，此方法需要有id入参
 * @returns {JSX.Element} Jade标准输入表单的DOM
 */
export default function JadeInput({items, addItem, updateItem, deleteItem}) {
    /**
     * 示例items,其中id，name，from，value必须，涉及reference时，referenceNode, referenceId, referenceKey必须, value会变为列表
     *
     * [
     *    {id: uuidv4(), name: '', type: "String", from: 'Reference', value: '', referenceNode: "", referenceId: "", referenceKey: ""},
     *    {id: uuidv4(), name: '', type: "String", from: 'Reference', value: '', referenceNode: "", referenceId: "", referenceKey: ""},
     *    {id: uuidv4(), name: '', type: "String", from: 'Reference', value: '', referenceNode: "", referenceId: "", referenceKey: ""}
     * ]
     */
    const shape = useShapeContext();

    const handleAdd = () => {
        addItem(uuidv4());
    };

    const handleItemChange = (name, value, itemId) => {
        const changes = [{key: name, value}];
        // 如果字段为 from，则清空 value 字段
        if (name === 'from') {
            changes.push({key: "value", value: ""});
            changes.push({key: "referenceNode", value: ""});
            changes.push({key: "referenceId", value: ""});
            changes.push({key: "referenceKey", value: ""});
            document.activeElement.blur();// 在选择后取消焦点
        }
        updateItem(itemId, changes);
    };

    /**
     * 当reference对应监听对象自身发生变化时调用
     *
     * @param item 变化的item信息
     * @param e 变化值对象
     */
    const handleReferenceValueChange = (item, e) => {
        updateItem(item.id, [{key: "referenceKey", value: e}]);
    };

    /**
     * 当切换reference对应监听对象时调用
     *
     * @param item 变化的item信息
     * @param e 变化值对象
     */
    const handleReferenceKeyChange = (item, e) => {
        updateItem(item.id, [
                {key: "referenceNode", value: e.referenceNode},
                {key: "referenceId", value: e.referenceId},
                {key: "value", value: e.value}
        ]);
    };

    const handleDelete = (itemId) => {
        deleteItem(itemId);
    };

    const content = (
        <div>
            <p>输入需要添加到提示词模板中的信息，可被提示词模板引用</p>
        </div>
    );

    const handleSelectClick = (event) => {
        event.stopPropagation(); // 阻止事件冒泡
    };

    // 根据不同的值渲染不同的组件
    const renderComponent = (item) => {
        switch (item.from) {
            case 'Reference':
                return <Form.Item
                    id={`value-${item.id}`}
                >
                    <JadeReferenceTreeSelect
                        className="value-custom jade-select"
                        reference={item}
                        onReferencedValueChange={(e) => handleReferenceValueChange(item, e)}
                        onReferencedKeyChange={(e) => handleReferenceKeyChange(item, e)}
                    />
                </Form.Item>
            case 'Input':
                return <Form.Item
                    id={`value-${item.id}`}
                    name={`value-${item.id}`}
                    rules={[{required: true, message: "字段值不能为空"}, {pattern: /^[^\s]*$/, message: "禁止输入空格"},]}
                    initialValue={item.value}
                >
                    <Input
                        className="value-custom jade-input"
                        value={item.value}
                        onChange={(e) => handleItemChange('value', e.target.value, item.id)}
                    />
                </Form.Item>;
            default:
                return <></>;
        }
    };

    return (
        <Collapse bordered={false} className="jade-collapse-custom-background-color" defaultActiveKey={["inputPanel"]}>
            {
                <Panel
                    key={"inputPanel"}
                    header={
                        <div className="panel-header">
                            <span className="jade-panel-header-font">输入</span>
                            <Popover content={content}>
                                <InfoCircleOutlined className="jade-panel-header-popover-content"/>
                            </Popover>
                            <Button type="text" className="icon-button"
                                    style={{height: "22px", marginLeft: "76%", marginRight: "0"}}
                                    onClick={(event) => {
                                        handleAdd();
                                        handleSelectClick(event);
                                    }}>
                                <PlusOutlined/>
                            </Button>
                        </div>
                    }
                    className="jade-panel"
                >
                    <Form
                        name={`inputForm-${shape.id}`}
                        layout="vertical" // 设置全局的垂直布局
                        className={"jade-form"}
                    >
                        <Row gutter={16}>
                            <Col span={8}>
                                <Form.Item>
                                    <span className="jade-font-size jade-font-color">字段名称</span>
                                </Form.Item>
                            </Col>
                            <Col span={16}>
                                <Form.Item>
                                    <span className="jade-font-size jade-font-color">字段值</span>
                                </Form.Item>
                            </Col>
                        </Row>
                        {items.map((item) => (
                            <Row
                                key={item.id}
                                gutter={16}
                            >
                                <Col span={8}>
                                    <Form.Item
                                        id={`name-${item.id}`}
                                        name={`name-${item.id}`}
                                        rules={[
                                            {
                                                pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/,
                                                message: '只能包含字母、数字或下划线，且必须以字母或下划线开头'
                                            }
                                        ]}
                                        initialValue={item.name}
                                    >
                                        <Input
                                            className="jade-input"
                                            style={{paddingRight: "12px"}}
                                            value={item.name}
                                            onChange={(e) => handleItemChange('name', e.target.value, item.id)}
                                        />
                                    </Form.Item>
                                </Col>
                                <Col span={6} style={{paddingRight: 0}}>
                                    <Form.Item
                                        id={`from-${item.id}`}
                                        initialValue='Reference'
                                    >
                                        <JadeStopPropagationSelect
                                            id={`from-select-${item.id}`}
                                            className="value-source-custom jade-select"
                                            style={{width: "100%"}}
                                            onChange={(value) => handleItemChange('from', value, item.id)}
                                            options={[
                                                {value: 'Reference', label: '引用'},
                                                {value: 'Input', label: '输入'},
                                            ]}
                                            value={item.from}
                                        />
                                    </Form.Item>
                                </Col>
                                <Col span={8} style={{paddingLeft: 0}}>
                                        {renderComponent(item)} {/* 渲染对应的组件 */}
                                </Col>
                                <Col span={2} style={{paddingLeft: 0}}>
                                    <Form.Item>
                                        <Button type="text" className="icon-button"
                                                style={{height: "100%"}}
                                                onClick={() => handleDelete(item.id)}>
                                            <MinusCircleOutlined/>
                                        </Button>
                                    </Form.Item>
                                </Col>
                            </Row>
                        ))}
                    </Form>
                </Panel>
            }
        </Collapse>
    );
}