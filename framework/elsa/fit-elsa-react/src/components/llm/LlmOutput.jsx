import {Collapse, Popover} from 'antd';
import {QuestionCircleOutlined} from '@ant-design/icons';
import {useDataContext, useShapeContext} from "@/components/DefaultRoot.jsx";
import "../common/style.css";
import {JadeObservableTree} from "@/components/common/JadeObservableTree.jsx";

const {Panel} = Collapse;

/**
 * 大模型节点输出表单。
 *
 * @returns {JSX.Element} 大模型节点输出表单的DOM。
 */
export default function LlmOutput() {
    const shape = useShapeContext();
    const data = useDataContext();

    const initItems = () => {
        return data.outputParams;
    };

    const outputItems = initItems();

    // 430演示大模型输出不需要新增和删除，暂时屏蔽
    // 添加新元素到 items 数组中，并将其 key 添加到当前展开的面板数组中
    // const addItem = () => {
    //     dispatch({actionType: "addOutputParam", id: "uuidv4()})
    // };

    // 430演示大模型输出不允许用户修改，暂时屏蔽
    // const handleItemChange = (name, value, itemId) => {
    //     dispatch({actionType: "changeOutputParam", id: itemId, type: name, value: value});
    //     if (name === "type") {
    //         document.activeElement.blur();// 在选择后取消焦点
    //     }
    // };

    // 430演示大模型输出不需要新增和删除，暂时屏蔽
    // const handleDelete = (itemId) => {
    //     dispatch({actionType: "deleteOutputParam", id: itemId});
    // };

    // 430演示大模型输出不需要新增和删除，暂时屏蔽
    // const handleSelectClick = (event) => {
    //     event.stopPropagation(); // 阻止事件冒泡
    // };

    const content = (
        <div className={"jade-font-size"}>
            <p>大模型运行完成后生成的内容。</p>
        </div>
    );

    return (
        <Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["outputPanel"]}>
            {
                <Panel
                    key={"outputPanel"}
                    header={
                        <div className="panel-header"
                             style={{display: 'flex', alignItems: 'center', justifyContent: "flex-start"}}>
                            <span className="jade-panel-header-font">输出</span>
                            <Popover content={content}>
                                <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                            </Popover>
                            {/*430演示大模型输出不需要新增和删除，暂时屏蔽*/}
                            {/*<Button type="text" className="icon-button"*/}
                            {/*        style={{"height": "22px", "marginLeft": "auto"}}*/}
                            {/*        onClick={(event) => {*/}
                            {/*            addItem();*/}
                            {/*            handleSelectClick(event);*/}
                            {/*        }}>*/}
                            {/*    <PlusOutlined/>*/}
                            {/*</Button>*/}
                        </div>
                    }
                    className="jade-panel"
                >
                    <div className={"jade-custom-panel-content"}>
                        <JadeObservableTree data={outputItems}/>
                        {/*430演示大模型输出不允许用户操作，写死*/}
                        {/*<Row gutter={16}>*/}
                        {/*    <Col span={7}>*/}
                        {/*        <Form.Item>*/}
                        {/*            <span style={{color: "rgba(28,31,35,.35)"}}>Name</span>*/}
                        {/*        </Form.Item>*/}
                        {/*    </Col>*/}
                        {/*    <Col span={5}>*/}
                        {/*        <Form.Item>*/}
                        {/*            <span style={{color: "rgba(28,31,35,.35)"}}>Type</span>*/}
                        {/*        </Form.Item>*/}
                        {/*    </Col>*/}
                        {/*    <Col span={12}>*/}
                        {/*        <Form.Item>*/}
                        {/*            <span style={{color: "rgba(28,31,35,.35)"}}>Description</span>*/}
                        {/*        </Form.Item>*/}
                        {/*    </Col>*/}
                        {/*</Row>*/}
                        {/*430演示大模型输出不允许用户操作，写死*/}
                        {/*{outputItems.map((item) => (*/}
                        {/*    <Row*/}
                        {/*        key={item.id}*/}
                        {/*        gutter={16}*/}
                        {/*    >*/}
                        {/*<Col span={7}>*/}
                        {/*    <Form.Item*/}
                        {/*        id={`name-${item.id}`}*/}
                        {/*        name={`name-${item.id}`}*/}
                        {/*        rules={[{required: true, message: '输出参数名称不能为空!'}]}*/}
                        {/*        initialValue={item.name}*/}
                        {/*    >*/}
                        {/*        <Input*/}
                        {/*            style={{paddingRight: "12px"}}*/}
                        {/*            value={item.name}*/}
                        {/*            onChange={(e) => handleItemChange('name', e.target.value, item.id)}*/}
                        {/*        />*/}
                        {/*    </Form.Item>*/}
                        {/*</Col>*/}
                        {/*<Col span={5}>*/}
                        {/*    <Form.Item*/}
                        {/*        id={`type-${item.id}`}*/}
                        {/*        initialValue='String'*/}
                        {/*    >*/}
                        {/*        <JadeStopPropagationSelect*/}
                        {/*            id={`type-select-${item.id}`}*/}
                        {/*            style={{width: "100%"}}*/}
                        {/*            onChange={(value) => handleItemChange('type', value, item.id)}*/}
                        {/*            options={[*/}
                        {/*                {value: 'String', label: 'String'},*/}
                        {/*                {value: 'Integer', label: 'Integer'},*/}
                        {/*                {value: 'Boolean', label: 'Boolean'},*/}
                        {/*                {value: 'Number', label: 'Number'},*/}
                        {/*                {value: 'Object', label: 'Object', disabled: true},*/}
                        {/*                {value: 'Array<String>', label: 'Array<String>'},*/}
                        {/*                {value: 'Array<Integer>', label: 'Array<Integer>'},*/}
                        {/*                {value: 'Array<Boolean>', label: 'Array<Boolean>'},*/}
                        {/*                {value: 'Array<Number>', label: 'Array<Number>'},*/}
                        {/*                {value: 'Array<Object>', label: 'Array<Object>', disabled: true},*/}
                        {/*            ]}*/}
                        {/*            value={item.type}*/}
                        {/*        />*/}
                        {/*    </Form.Item>*/}
                        {/*</Col>*/}
                        {/*<Col span={11}>*/}
                        {/*    <Form.Item*/}
                        {/*        id={`description-${item.id}`}*/}
                        {/*    >*/}
                        {/*        <Input*/}
                        {/*            style={{paddingRight: "12px"}}*/}
                        {/*            value={item.description}*/}
                        {/*            onChange={(e) => handleItemChange('description', e.target.value, item.id)}*/}
                        {/*        />*/}
                        {/*    </Form.Item>*/}
                        {/*</Col>*/}


                        {/*430演示大模型输出不需要新增和删除，暂时屏蔽*/}
                        {/*<Col span={1} style={{paddingLeft: "2px"}}>*/}
                        {/*    <Form.Item>*/}
                        {/*        <Button type="text" className="icon-button"*/}
                        {/*                style={{"height": "100%", "marginLeft": "auto"}}*/}
                        {/*                onClick={() => handleDelete(item.id)}>*/}
                        {/*            <MinusCircleOutlined/>*/}
                        {/*        </Button>*/}
                        {/*    </Form.Item>*/}
                        {/*</Col>*/}
                        {/*</Row>*/}
                        {/*))}*/}
                    </div>
                </Panel>
            }
        </Collapse>
    );
}