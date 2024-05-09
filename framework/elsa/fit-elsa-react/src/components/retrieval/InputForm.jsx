import {Col, Collapse, Form, Input, Popover, Row} from "antd";
import {InfoCircleOutlined} from "@ant-design/icons";
import React from "react";
import {useDataContext, useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";
import {JadeReferenceTreeSelect} from "@/components/common/JadeReferenceTreeSelect.jsx";
import {JadeStopPropagationSelect} from "../common/JadeStopPropagationSelect.jsx";

const {Panel} = Collapse;

/**
 * 输入节点组件
 *
 * @returns {JSX.Element}
 */
export default function InputForm() {
    const dispatch = useDispatch();
    const data = useDataContext();
    const shape = useShapeContext();
    const currentData = data && data.inputParams.find(item => item.name === "query");

    /**
     * 处理输入发生变化的动作
     *
     * @param id id
     * @param changes 变更的字段
     */
    const handleItemChange = (id, changes) => {
        dispatch({type: "editInput", id: id, changes: changes});
    };

    /**
     * 根据值渲染组件
     *
     * @param item 值
     * @return {JSX.Element|null}
     */
    const renderComponent = (item) => {
        switch (item.from) {
            case 'Reference':
                return <Form.Item>
                    <JadeReferenceTreeSelect
                        reference={item} onReferencedValueChange={(value) => {
                        handleItemChange(item.id, [{key: 'referenceKey', value: value}])
                    }}
                        onReferencedKeyChange={(e) => {
                            handleItemChange(item.id, [{key: 'referenceNode', value: e.referenceNode},
                                {key: 'referenceId', value: e.referenceId},
                                {key: 'value', value: e.value}]);
                        }}
                        style={{fontSize: "12px"}}
                        placeholder="请选择"
                        onMouseDown={(e) => e.stopPropagation()}
                        showSearch
                        className="value-custom jade-select"
                        dropdownStyle={{
                            maxHeight: 400,
                            overflow: 'auto',
                        }}
                        value={item.value}
                    >
                    </JadeReferenceTreeSelect>
                </Form.Item>
            case 'Input':
                return <Form.Item
                    id={`input-${item.id}`}
                    name={`input-${item.id}`}
                    rules={[{required: true, message: "字段值不能为空"}, {
                        pattern: /^[^\s]*$/,
                        message: "禁止输入空格"
                    }]}
                    initialValue={item.value}
                >
                    <Input
                        className="value-custom jade-input"
                        placeholder="清输入"
                        value={item.value}
                        onChange={(e) => handleItemChange(item.id, [{key: 'value', value: e.target.value}])}/>
                </Form.Item>
            default:
                return null;
        }
    };
    const tips = <div className={"jade-font-size"}><p>输入需要从知识库中匹配的关键信息</p></div>;

    return (<div>
        <Collapse bordered={false} className="jade-collapse-custom-background-color"
                  style={{marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%"}}
                  defaultActiveKey={['Input']}>
            <Panel
                style={{marginBottom: 8, borderRadius: "8px", width: "100%"}}
                header={
                    <div
                        style={{display: 'flex', alignItems: 'center', justifyContent: "flex-start"}}>
                        <span className="jade-panel-header-font">输入</span>
                        <Popover content={tips}>
                            <InfoCircleOutlined className="jade-panel-header-popover-content"/>
                        </Popover>
                    </div>
                }
                className="jade-panel"
                key='Input'
            >
                <Form
                    name={`inputForm-${shape.id}`}
                    layout="vertical"
                    className={"jade-form"}
                >
                    <Row>
                        <Col span={8}>
                            <Form.Item style={{marginBottom: "8px"}}>
                                <span className="jade-font-size jade-font-color">字段名称</span>
                            </Form.Item>
                        </Col>
                        <Col span={16}>
                            <Form.Item style={{marginBottom: "8px"}}>
                                <span className="jade-font-size jade-font-color">字段值</span>
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row className="jade-row">
                        <Col span={8}>
                            <span className="starred-text">query</span>
                        </Col>

                        <Col span={8} style={{paddingRight: 0}}>
                            <Form.Item
                                id={`valueSource`}
                                initialValue='Reference'
                            >
                                <JadeStopPropagationSelect
                                    id={`valueSource-select-${currentData.id}`}
                                    className={"value-source-custom jade-select"}
                                    style={{width: "100%"}}
                                    onMouseDown={(e) => e.stopPropagation()}
                                    onChange={(value) => {
                                        let changes = [{key: 'from', value: value}, {key: "value", value: ""}];
                                        if (value === "Input") {
                                            changes = [
                                                {key: 'from', value: value},
                                                {key: "value", value: ""},
                                                {key: "referenceNode", value: ""},
                                                {key: "referenceId", value: ""},
                                                {key: "referenceKey", value: ""}
                                            ]
                                        }
                                        handleItemChange(currentData.id, changes);
                                    }}
                                    options={[
                                        {value: 'Reference', label: '引用'},
                                        {value: 'Input', label: '输入'},
                                    ]}
                                    value={currentData.from}
                                />
                            </Form.Item>
                        </Col>
                        <Col span={8} style={{paddingLeft: 0}}>
                            <Form.Item
                                id={`value-${currentData.id}`}
                                rules={[{required: true, message: '参数不能为空!'}]}
                            >
                                {renderComponent(currentData)} {/* 渲染对应的组件 */}
                            </Form.Item>
                        </Col>
                    </Row>
                </Form>
            </Panel>
        </Collapse>
    </div>)
}