import {Button, Col, Collapse, Form, Row} from 'antd';
import {PlusOutlined} from '@ant-design/icons';
import {useDataContext, useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";
import "../common/style.css";
import {JadeStopPropagationSelect} from "../common/JadeStopPropagationSelect.jsx";
import PropTypes from "prop-types";

const {Panel} = Collapse;

SkillForm.propTypes = {
    toolOptions: PropTypes.array.isRequired, // 确保 toolOptions 是一个必需的array类型
    workflowOptions: PropTypes.array.isRequired, // 确保 workflowOptions 是一个必需的array类型
    config: PropTypes.object.isRequired, // 确保 config 是一个必需的object类型
};

/**
 * 大模型节点技能表单。
 *
 * @param toolOptions 工具选项。
 * @param workflowOptions 工具流选项。
 * @param config 相关配置。
 * @returns {JSX.Element} 大模型节点技能表单的DOM。
 */
export default function SkillForm({toolOptions, workflowOptions, config}) {
    const shape = useShapeContext();
    const data = useDataContext();
    const dispatch = useDispatch();
    const tool = data.inputParams.find(item => item.name === "tools");
    const workflow = data.inputParams.find(item => item.name === "workflows");

    const handleClick = (event) => {
        if (!config || !config.params || !config.params.tenantId || !config.params.appId) {
            console.error('Cannot get config.params.tenantId or config.params.appId.');
        } else {
            // 使用 window.open() 打开一个新页面
            window.open('/appbuilder/#/aipp/' + config.params.tenantId + '/addFlow/' + config.params.appId, '_blank');
            event.stopPropagation(); // 阻止事件冒泡
        }
    };

    // Filter `option.label` match the user type `input`
    const filterOption = (input, option) =>
        (option?.label ?? '').toLowerCase().includes(input.toLowerCase());

    const handleSkillChange = (itemId, value) => {
        dispatch({actionType: "changeSkillConfig", id: itemId, value: value});
    };

    return (
        <Collapse bordered={false} className="jade-collapse-custom-background-color" defaultActiveKey={["skillPanel"]}>
            {
                <Panel
                    key={"skillPanel"}
                    header={
                        <div className="panel-header"
                             style={{display: 'flex', alignItems: 'center', justifyContent: "flex-start"}}>
                            <span className="jade-panel-header-font">技能</span>
                        </div>
                    }
                    className="jade-panel"
                >
                    <Row gutter={16} style={{marginBottom: "6px", marginRight: 0, marginLeft: "-3%"}}>
                        <Col span={21}>
                            <span className="jade-font-size jade-font-color" style={{marginLeft: "6px"}}>工具</span>
                        </Col>
                        {/*430演示工具不需要+号跳转，暂时屏蔽*/}
                        {/*<Col span={3}>*/}
                        {/*    <Button type="text" className="icon-button"*/}
                        {/*            style={{height: "22px", marginLeft: "4px"}}*/}
                        {/*            onClick={(event) => {*/}
                        {/*                handleClick(event);*/}
                        {/*            }}>*/}
                        {/*        <PlusOutlined/>*/}
                        {/*    </Button>*/}
                        {/*</Col>*/}
                    </Row>
                    <Form.Item>
                        <JadeStopPropagationSelect
                            mode="multiple"
                            showSearch
                            allowClear
                            className="jade-select"
                            placeholder="选择合适的工具"
                            filterOption={filterOption}
                            optionFilterProp="label"
                            value={tool.value}
                            onMouseDown={(e) => e.stopPropagation()}
                            onChange={(e) => handleSkillChange(tool.id, e)}
                            options={toolOptions}
                        />
                    </Form.Item>
                    <Row gutter={16} style={{marginBottom: "6px", marginRight: 0, marginLeft: "-3%"}}>
                        <Col span={22}>
                            <span className="jade-font-size jade-font-color" style={{marginLeft: "6px"}}>工具流</span>
                        </Col>
                        <Col span={2} style={{paddingLeft: "3%"}}>
                            <Button type="text" className="icon-button"
                                    style={{height: "22px"}}
                                    onClick={(event) => {
                                        handleClick(event);
                                    }}>
                                <PlusOutlined/>
                            </Button>
                        </Col>
                    </Row>

                    <Form.Item>
                        <JadeStopPropagationSelect
                            mode="multiple"
                            showSearch
                            allowClear
                            className="jade-select"
                            placeholder="选择合适的工具流"
                            filterOption={filterOption}
                            optionFilterProp="label"
                            value={workflow.value}
                            onChange={(e) => handleSkillChange(workflow.id, e)}
                            options={workflowOptions}
                        />
                    </Form.Item>
                </Panel>
            }
        </Collapse>
    );
}