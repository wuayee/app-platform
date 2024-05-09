import {Col, Form, Input, Row, Select} from "antd";
import React from "react";
import {JadeReferenceTreeSelect} from "@/components/common/JadeReferenceTreeSelect.jsx";

/**
 * 输出变量的每个条目
 *
 * @param item 当前条目数据
 * @param handleItemChange 更改值的回调
 * @returns {JSX.Element}
 * @constructor
 */
export default function OutputVariableRow({item, handleItemChange}) {

    /**
     * 通过字段类型选择渲染组件
     *
     * @param from 类型
     * @return {JSX.Element|null}
     */
    const renderByType = (from) => {
        switch (from) {
            case 'Reference':
                return <JadeReferenceTreeSelect
                    reference={item}
                    onReferencedValueChange={(value) => {
                        handleItemChange(item.id, [{key: "referenceKey", value: value}]);
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
                </JadeReferenceTreeSelect>;
            case 'String':
                return <Input
                    className="value-custom jade-input"
                    style={{fontSize: "12px"}}
                    placeholder="请输入"
                    value={item.value}
                    onChange={(e) => handleItemChange(item.id, [{key: 'value', value: e.target.value}])}
                />;
            default:
                return null;
        }
    };

    return (
        <Row
            key={`output-variable-${item.id}`}
            gutter={16}
        >
            <Col span={8} style={{alignItems: "center", display: "flex"}}>
                <span className='jade-font-size' style={{marginBottom: '8px'}}>finalOutput</span>
            </Col>
            <Col span={6} style={{paddingRight: 0}}>
                <Form.Item
                    style={{marginBottom: '8px'}}
                    id={`valueSource-${item.id}`}
                    initialValue='Reference'
                >
                    <Select
                        onMouseDown={(e) => e.stopPropagation()}
                        id={`valueSource-select-${item.id}`}
                        className={"value-source-custom jade-select"}
                        style={{width: "100%"}}
                        onChange={(value) => {
                            let changes = [{key: 'from', value: value}, {key: "value", value: ""}];
                            if (value === "String") {
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
                            {value: 'String', label: '输入'}
                        ]}
                        value={item.from}
                    />
                </Form.Item>
            </Col>
            <Col span={10} style={{paddingLeft: 0}}>
                <Form.Item
                    style={{marginBottom: '8px'}}
                    id={`value-${item.id}`}
                >
                    {renderByType(item.from)}
                </Form.Item>
            </Col>
        </Row>
    );
}