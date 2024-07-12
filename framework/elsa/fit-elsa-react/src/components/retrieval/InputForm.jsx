import {Col, Collapse, Form, Popover, Row} from "antd";
import {QuestionCircleOutlined} from "@ant-design/icons";
import React from "react";
import {useDataContext, useDispatch, useFormContext} from "@/components/DefaultRoot.jsx";
import {JadeReferenceTreeSelect} from "@/components/common/JadeReferenceTreeSelect.jsx";
import {JadeStopPropagationSelect} from "../common/JadeStopPropagationSelect.jsx";
import {JadeInput} from "@/components/common/JadeInput.jsx";

const {Panel} = Collapse;

/**
 * 输入节点组件
 *
 * @param disabled 禁用.
 * @returns {JSX.Element}
 */
export default function InputForm({disabled}) {
    const dispatch = useDispatch();
    const data = useDataContext();
    const currentData = data && data.inputParams.find(item => item.name === "query");
    const form = useFormContext();
    const name = `input-${currentData.id}`;

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
     * 引用的值发生改变
     *
     * @param item 子组件
     * @param value 值
     * @private
     */
    const _onReferencedValueChange = (item, value) => {
        handleItemChange(item.id, [{key: 'referenceKey', value: value}])
    };

    /**
     * 引用的对象发生变化
     *
     * @param item 子组件
     * @param e 动作
     * @private
     */
    const _onReferencedKeyChange = (item, e) => {
        handleItemChange(item.id, [{key: 'referenceNode', value: e.referenceNode},
            {key: 'referenceId', value: e.referenceId},
            {key: 'value', value: e.value}]);
    };

    /**
     * 更新input的属性
     *
     * @param item 子组件
     * @return {(function(*): void)|*}
     */
    const editInput = item => (e) => {
        if (!e.target.value) {
            return;
        }
        handleItemChange(item.id, [{key: 'value', value: e.target.value}]);
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
                return (<>
                    <JadeReferenceTreeSelect
                            disabled={disabled}
                            reference={item}
                            onReferencedValueChange={(v) => _onReferencedValueChange(item, v)}
                            onReferencedKeyChange={(e) => _onReferencedKeyChange(item, e)}
                            style={{fontSize: "12px"}}
                            placeholder="请选择"
                            onMouseDown={(e) => e.stopPropagation()}
                            showSearch
                            className="value-custom jade-select"
                            dropdownStyle={{
                                maxHeight: 400,
                                overflow: 'auto',
                            }}
                            rules={[{required: true, message: "字段值不能为空"}]}
                    />
                </>);
            case 'Input':
                return (<>
                    <Form.Item id={`input-${item.id}`}
                               name={`input-${item.id}`}
                               rules={[{required: true, message: "字段值不能为空"}, {
                                   pattern: /^[^\s]*$/,
                                   message: "禁止输入空格"
                               }]}
                               initialValue={item.value}
                               validateTrigger="onBlur"
                    >
                        <JadeInput disabled={disabled}
                                   className="value-custom jade-input"
                                   placeholder="清输入"
                                   value={item.value}
                                   onBlur={editInput(item)}
                        />
                    </Form.Item>
                </>);
            default:
                return null;
        }
    };
    const tips = <div className={"jade-font-size"}><p>输入需要从知识库中匹配的关键信息</p></div>;

    return (<div>
        <Collapse bordered={false} className="jade-custom-collapse"
                  defaultActiveKey={['Input']}>
            <Panel
                    header={
                        <div className="panel-header">
                            <span className="jade-panel-header-font">输入</span>
                            <Popover content={tips}>
                                <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                            </Popover>
                        </div>
                    }
                    className="jade-panel"
                    key='Input'
            >
                <div className={"jade-custom-panel-content"}>
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

                    <Row>
                        <Col span={8} style={{display: "flex", paddingTop: "5px"}}>
                            <span className="retrieval-starred-text jade-font-size">query</span>
                        </Col>

                        <Col span={8} style={{paddingRight: 0}}>
                            <Form.Item id={`valueSource`} initialValue="Reference">
                                <JadeStopPropagationSelect
                                    disabled={disabled}
                                    id={`valueSource-select-${currentData.id}`}
                                    className={"value-source-custom jade-select"}
                                    style={{width: "100%"}}
                                    onChange={(value) => {
                                        let changes = [{key: 'from', value: value}, {key: "value", value: ""}];
                                        if (value === "Input") {
                                            changes = [{key: 'from', value: value},
                                                {key: "value", value: ""},
                                                {key: "referenceNode", value: ""},
                                                {key: "referenceId", value: ""},
                                                {key: "referenceKey", value: ""}]
                                        }
                                        handleItemChange(currentData.id, changes);
                                        form.resetFields([`reference-${currentData.id}`, name]);
                                    }}
                                    options={[{value: 'Reference', label: '引用'}, {value: 'Input', label: '输入'}]}
                                    value={currentData.from}
                                />
                            </Form.Item>
                        </Col>
                        <Col span={8} style={{paddingLeft: 0}}>
                            {renderComponent(currentData)} {/* 渲染对应的组件 */}
                        </Col>
                    </Row>
                </div>
            </Panel>
        </Collapse>
    </div>)
}