import {Col, Form, Row, Select} from "antd";
import React from "react";
import {JadeReferenceTreeSelect} from "@/components/common/JadeReferenceTreeSelect.jsx";
import {useFormContext} from "@/components/DefaultRoot.jsx";
import {JadeInput} from "@/components/common/JadeInput.jsx";

/**
 * 输出变量的每个条目
 *
 * @param item 当前条目数据
 * @param handleItemChange 更改值的回调
 * @param disabled 是否禁用.
 * @returns {JSX.Element}
 * @constructor
 */
export default function OutputVariableRow({item, handleItemChange, disabled}) {
    const inputName = `value-${item.id}`;
    const form = useFormContext();

    const _onReferencedValueChange = (value) => {
        handleItemChange(item.id, [{key: "referenceKey", value: value}]);
    };

    const _onReferencedKeyChange = (e) => {
        handleItemChange(item.id, [{key: 'referenceNode', value: e.referenceNode},
            {key: 'referenceId', value: e.referenceId},
            {key: 'value', value: e.value}]);
    };

    /**
     * 更改输出变量的属性
     *
     * @return {(function(*): void)|*}
     */
    const editOutputVariable = (e) => {
        if (!e.target.value) {
            return;
        }
        handleItemChange(item.id, [{key: 'value', value: e.target.value}]);
    };

    /**
     * 通过字段类型选择渲染组件
     *
     * @param from 类型
     * @return {JSX.Element|null}
     */
    const renderByType = (from) => {
        switch (from) {
            case 'Reference':
                return (<>
                    <JadeReferenceTreeSelect
                            disabled={disabled}
                            reference={item}
                            onReferencedValueChange={_onReferencedValueChange}
                            onReferencedKeyChange={_onReferencedKeyChange}
                            style={{fontSize: "12px"}}
                            placeholder="请选择"
                            onMouseDown={(e) => e.stopPropagation()}
                            showSearch
                            className="value-custom jade-select"
                            dropdownStyle={{
                                maxHeight: 400, overflow: 'auto'
                            }}
                            value={item.value}
                            rules={[{required: true, message: "字段值不能为空"}]}
                    />
                </>);
            case 'Input':
                return (<>
                    <Form.Item
                            style={{marginBottom: '8px'}}
                            id={`value-${item.id}`}
                            name={`value-${item.id}`}
                            rules={[{required: true, message: '字段值不能为空'}]}
                            validateTrigger="onBlur"
                            initialValue={item.value}
                    >
                        <JadeInput disabled={disabled}
                                   className="value-custom jade-input"
                                   style={{fontSize: "12px"}}
                                   placeholder="请输入"
                                   onBlur={editOutputVariable}
                        />
                    </Form.Item>
                </>);
            default:
                return null;
        }
    };

    return (
            <Row
                    key={`output-variable-${item.id}`}
                    gutter={16}
            >
                <Col span={8} style={{display: "flex", paddingTop: "5px"}}>
                    <span className="end-starred-text">finalOutput</span>
                </Col>
                <Col span={6} style={{paddingRight: 0}}>
                    <Form.Item
                            style={{marginBottom: '8px'}}
                            id={`valueSource-${item.id}`}
                            initialValue='Reference'
                    >
                        <Select disabled={disabled}
                                onMouseDown={(e) => e.stopPropagation()}
                                id={`valueSource-select-${item.id}`}
                                className={"value-source-custom jade-select"}
                                style={{width: "100%"}}
                                onChange={(value) => {
                                    form.resetFields([`reference-${item.id}`, inputName]);
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
                                    handleItemChange(item.id, changes);
                                }}
                                options={[
                                    {value: 'Reference', label: '引用'},
                                    {value: 'Input', label: '输入'}
                                ]}
                                value={item.from}
                        />
                    </Form.Item>
                </Col>
                <Col span={10} style={{paddingLeft: 0}}>
                    {renderByType(item.from)}
                </Col>
            </Row>
    );
}