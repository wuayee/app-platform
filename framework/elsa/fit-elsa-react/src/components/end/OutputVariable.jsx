import {Col, Collapse, Form, Popover, Row} from "antd";
import React from "react";
import {QuestionCircleOutlined} from '@ant-design/icons';
import "./style.css";
import OutputVariableRow from "@/components/end/OutputVariableRow.jsx";
import {useDataContext, useDispatch} from "@/components/DefaultRoot.jsx";

const {Panel} = Collapse;

/**
 * 输出变量的组件，包含多条输出变量的条目
 *
 * @param disabled 是否禁用.
 * @returns {JSX.Element}
 * @constructor
 */
export default function OutputVariable({disabled}) {
    const dispatch = useDispatch();
    const data = useDataContext();

    /**
     * 初始化数据
     *
     * @return {*}
     */
    const initData = () => {
        return data && data.inputParams;
    };

    /**
     * 处理输入发生变化的动作
     *
     * @param id id
     * @param changes 变更的字段
     */
    const handleItemChange = (id, changes) => {
        dispatch({type: "editOutputVariable", id: id, changes: changes});
    };


    const tips =
        <div className={"jade-font-size"} style={{lineHeight: "1.2"}}><p>这些变量将在机器人完成工作流调用后输出。</p>
            <p>在“返回变量”模式下，这些变量将由机器人汇总并回复给用户；</p>
            <p>在“直接回答”模式下，机器人将只回复配置卡时可以使用的变量</p></div>;

    return (
        <div>
            <Collapse bordered={false} className="jade-custom-collapse"
                      style={{marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%"}}
                      defaultActiveKey={['Output variable']}>
                <Panel
                    style={{marginBottom: 8, borderRadius: "8px", width: "100%"}}
                    header={
                        <div
                            style={{display: 'flex', alignItems: 'center', justifyContent: "flex-start"}}>
                            <span className="jade-panel-header-font">输出</span>
                            <Popover content={tips}>
                                <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                            </Popover>
                        </div>
                    }
                    className="jade-panel"
                    key='Output variable'
                >
                    <div className={"jade-custom-panel-content"}>
                        <Row gutter={16}>
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
                        <OutputVariableRow disabled={disabled} item={initData()[0]}
                                           handleItemChange={handleItemChange}/>
                    </div>
                </Panel>
            </Collapse>
        </div>
    );
}