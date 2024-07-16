import {Button, Col, Collapse, Form, Row} from 'antd';
import {PlusOutlined} from '@ant-design/icons';
import {useDataContext, useDispatch} from "@/components/DefaultRoot.jsx";
import "../common/style.css";
import {JadeStopPropagationSelect} from "../common/JadeStopPropagationSelect.jsx";
import PropTypes from "prop-types";
import HttpUtil from "@/components/util/httpUtil.jsx";

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
 * @param disabled 是否禁用.
 * @returns {JSX.Element} 大模型节点技能表单的DOM。
 */
export default function SkillForm({toolOptions, workflowOptions, config, disabled}) {
    const data = useDataContext();
    const dispatch = useDispatch();
    const tool = data.inputParams.find(item => item.name === "tools");
    const workflow = data.inputParams.find(item => item.name === "workflows");
    const aippUrl = config.urls.aippUrl;

    // 新增工具流
    const handleAddWaterFlow = (event) => {
        event.stopPropagation(); // 阻止事件冒泡
        if (!config || !config.params || !config.params.tenantId || !config.params.appId) {
            console.error('Cannot get config.params.tenantId or config.params.appId.');
        } else {
            const timeStr = new Date().getTime().toString();
            HttpUtil.post(`${aippUrl}/${(config.params.tenantId)}/app/${'df87073b9bc85a48a9b01eccc9afccc3'}`, {
                type: 'waterFlow',
                name: timeStr
            }, null, (response) => {
                if (response.code === 0) {
                    window.open(`#/app-develop/${config.params.tenantId}/app-detail/add-flow/${(response.data.id)}`);
                }
            }, (error) => {
                console.log("create aipp error errorMsg=", error)
            });
        }
    };

    // Filter `option.label` match the user type `input`
    const filterOption = (input, option) =>
            (option?.label ?? '').toLowerCase().includes(input.toLowerCase());

    const handleSkillChange = (itemId, value) => {
        dispatch({actionType: "changeSkillConfig", id: itemId, value: value});
    };

    return (
            <Collapse bordered={false} className="jade-custom-collapse"
                      defaultActiveKey={["skillPanel"]}>
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
                        <div className={"jade-custom-panel-content"}>
                            <Row gutter={16} style={{marginBottom: "6px", marginRight: 0, marginLeft: "-3%"}}>
                                <Col span={21}>
                                    <span className="jade-font-size jade-font-color"
                                          style={{marginLeft: "6px"}}>工具</span>
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
                                    disabled={disabled}
                                    mode="multiple"
                                    showSearch
                                    allowClear
                                    className="jade-select"
                                    placeholder="选择合适的工具"
                                    filterOption={filterOption}
                                    optionFilterProp="label"
                                    value={tool.value}
                                    onChange={(e) => handleSkillChange(tool.id, e)}
                                    options={toolOptions}
                                />
                            </Form.Item>
                            <Row gutter={16} style={{marginBottom: "6px", marginRight: 0, marginLeft: "-3%"}}>
                                <Col span={22}>
                                <span className="jade-font-size jade-font-color"
                                      style={{marginLeft: "6px"}}>工具流</span>
                                </Col>
                                <Col span={2} style={{paddingLeft: "3%"}}>
                                    <Button disabled={disabled}
                                            type="text"
                                            className="icon-button"
                                            style={{height: "22px"}}
                                            onClick={(event) => {
                                                handleAddWaterFlow(event);
                                            }}>
                                        <PlusOutlined/>
                                    </Button>
                                </Col>
                            </Row>

                            <Form.Item>
                                <JadeStopPropagationSelect
                                    disabled={disabled}
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
                        </div>
                    </Panel>
                }
            </Collapse>
    );
}