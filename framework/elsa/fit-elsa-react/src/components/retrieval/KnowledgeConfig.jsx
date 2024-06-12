import {Col, Form, Popover, Row, Slider} from "antd";
import {InfoCircleOutlined} from "@ant-design/icons";
import React from "react";
import {useDataContext, useDispatch} from "@/components/DefaultRoot.jsx";

/**
 * 知识配置组件
 *
 * @param disabled 禁用状态.
 * @returns {JSX.Element}
 * @constructor
 */
export default function KnowledgeConfig({disabled}) {
    const dispatch = useDispatch();
    const data = useDataContext();
    const config = data && data.inputParams.find(item => item.name === "maximum").value;

    const maxRecallsTip = <div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
        <p>从知识返回到模型的最大段落数。数字越大，返回的内容越多。</p>
    </div>;

    const defaultRecalls = {
        1: '1',
        [3]: '默认',
        10: '10',
    };

    return (
        <Row className="jade-row">
            <Col span={12} className="jade-column">
                <Form.Item>
                    <span style={{
                                fontSize: '12px',
                                fontFamily: 'SF Pro Display',
                                letterSpacing: '0.12px',
                                lineHeight: '16px',
                                alignItems: "center",
                                userSelect: 'none',
                                marginRight: '4px',
                                color: 'rgba(28, 29, 35, 0.35)'
                            }}>返回最大值</span>
                    <Popover content={maxRecallsTip}>
                        <InfoCircleOutlined className="jade-panel-header-popover-content"/>
                    </Popover>
                </Form.Item>
            </Col>
            <Col span={12}>
                <Form.Item
                    style={{marginBottom: '0'}}
                    id={`valueSource`}
                    initialValue='Reference'
                >
                    <Slider disabled={disabled}
                        className="jade-slider"
                        style={{width: "90%"}}
                        min={1}
                        max={10}
                        step={1}
                        marks={defaultRecalls}
                        defaultValue={3}
                        value={config}
                        onChange={(value) => dispatch({type: "changeMaximum", key: "maximum", value: value})}
                    />
                </Form.Item>
            </Col>
        </Row>
    );
};