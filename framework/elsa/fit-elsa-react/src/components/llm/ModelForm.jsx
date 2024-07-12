import {Col, Collapse, Form, Input, InputNumber, Popover, Row} from 'antd';
import {QuestionCircleOutlined} from '@ant-design/icons';
import {useDataContext, useDispatch} from "@/components/DefaultRoot.jsx";
import {JadeStopPropagationSelect} from "../common/JadeStopPropagationSelect.jsx";
import PropTypes from "prop-types";

const {TextArea} = Input;
const {Panel} = Collapse;

ModelForm.propTypes = {
    shapeId: PropTypes.string.isRequired, // 确保 shapeId 是一个必需的string类型
    modelOptions: PropTypes.array.isRequired, // 确保 modelOptions 是一个必需的array类型
};

/**
 * 大模型节点模型表单。
 *
 * @param shapeId 所属图形唯一标识。
 * @param modelOptions 模型选项。
 * @param disabled 是否禁用.
 * @returns {JSX.Element} 大模型节点模型表单的DOM。
 */
export default function ModelForm({shapeId, modelOptions, disabled}) {
    const data = useDataContext();
    const dispatch = useDispatch();
    const model = data.inputParams.find(item => item.name === "model");
    const temperature = data.inputParams.find(item => item.name === "temperature");
    const systemPrompt = data.inputParams.find(item => item.name === "systemPrompt");
    const prompt = data.inputParams.filter(item => item.name === "prompt").flatMap(item => item.value).find(item => item.name === "template");

    const handleSelectClick = (event) => {
        event.stopPropagation(); // 阻止事件冒泡
    };

    const content = (
        <div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
            <p>用于控制生成文本的大型模型的随机性。</p>
            <p>当设置较高时，模型将生成更多样化的文本，增加不确定性；</p>
            <p>当设置较低时，模型将生成高概率词，减少不确定性。</p>
        </div>
    );

    const promptContent = (
        <div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
            <p>编辑大模型的提示词，实现相应的功能。</p>
            <p>可以使用{`{{变量名}}`}从输入参数中引入变量。</p>
        </div>
    );

    const formatter = (value, { input, userTyping }) => {
        if (userTyping) {
            return input;
        }
        if (value === 0.0) {
            return '0';
        }
        if (value === 1.0) {
            return '1';
        }
        return value;
    };

    /**
     * 失焦时才设置值，对于必填项.若为空，则不设置
     *
     * @param e
     * @param actionType
     * @param id
     * @param required
     */
    const changeOnBlur = (e, actionType, id, required) => {
        if (required && e.target.value === "") {
            return;
        }
        dispatch({actionType: actionType, id: id, value: e.target.value});
    };

    /**
     * 数字输入对应失焦时才设置值，对于必填项.若为空，则不设置。并对其中值进行范围内标准化
     *
     * @param e
     * @param actionType
     * @param id
     * @param required
     */
    const inputNumberChangeOnBlur = (e, actionType, id, required) => {
        if (required && e.target.value === "") {
            return;
        }
        let originValue = e.target.value;
        let changeValue;
        if (originValue <= 0.0) {
            changeValue = 0;
        } else if (originValue >= 1.0) {
            changeValue = 1;
        } else {
            changeValue = originValue;
        }
        dispatch({actionType: actionType, id: id, value: changeValue});
    };

    return (
        <Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["modelPanel"]}>
            {
                <Panel
                    key={"modelPanel"}
                    header={
                        <div className="panel-header">
                            <span className="jade-panel-header-font">大模型</span>
                        </div>
                    }
                    className="jade-panel"
                >
                    <div className={"jade-custom-panel-content"}>
                        <Row gutter={16}>
                            <Col span={12}>
                                <Form.Item
                                    className="jade-form-item"
                                    name={`model-${shapeId}`}
                                    label="模型"
                                    rules={[{required: true, message: '请选择使用的模型'}]}
                                    initialValue={model.value} // 当组件套在Form.Item中的时候，内部组件的初始值使用Form.Item的initialValue进行赋值
                                    validateTrigger="onBlur"
                                >
                                    <JadeStopPropagationSelect
                                        disabled={disabled}
                                        className="jade-select"
                                        onClick={handleSelectClick} // 点击下拉框时阻止事件冒泡
                                        onChange={(e) => dispatch({actionType: "changeConfig", id: model.id, value: e})}
                                        options={modelOptions}
                                    />
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item
                                    className="jade-form-item"
                                    name={`temperature-${shapeId}`}
                                    label={<div style={{display: 'flex', alignItems: 'center'}}>
                                        <span className="jade-second-title">温度</span>
                                        <Popover content={content}>
                                            <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                                        </Popover>
                                    </div>}
                                    rules={[{required: true, message: '请输入0-1之间的参数!'}]}
                                    initialValue={temperature.value}
                                    validateTrigger="onBlur"
                                >
                                    <InputNumber disabled={disabled}
                                                 formatter={formatter}
                                                 className="jade-input"
                                                 style={{width: "100%"}}
                                                 min={0}
                                                 max={1}
                                                 step={0.1}
                                                 onBlur={(e) => inputNumberChangeOnBlur(e, "changeConfig", temperature.id, true)}
                                                 stringMode
                                    />
                                </Form.Item>
                            </Col>
                        </Row>
                        <Row gutter={16}>
                            <Col span={24}>
                                <Form.Item
                                    className="jade-form-item"
                                    name={`propmt-${shapeId}`}
                                    label={<div style={{display: 'flex', alignItems: 'center'}}>
                                        <span className="jade-second-title">用户提示词模板</span>
                                        <Popover content={[promptContent]}>
                                            <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                                        </Popover>
                                    </div>}
                                    rules={[{required: true, message: '参数不能为空'}]}
                                    initialValue={prompt.value}
                                    validateTrigger="onBlur"
                                >
                                    <TextArea disabled={disabled}
                                              onMouseDown={(e) => e.stopPropagation()}
                                              className="jade-textarea-input jade-font-size"
                                              onBlur={(e) => changeOnBlur(e, "changePrompt", prompt.id, true)}
                                              placeholder="你可以用{{variable name}}来关联输入中的变量名"
                                    />
                                </Form.Item>
                            </Col>
                        </Row>
                        <Row gutter={16}>
                            <Col span={24}>
                                <Form.Item
                                    className="jade-form-item"
                                    name={`system-prompt-${shapeId}`}
                                    label={<div style={{display: 'flex', alignItems: 'center'}}>
                                        <span className="jade-second-title">系统提示词</span>
                                    </div>}
                                    initialValue={systemPrompt.value}
                                    validateTrigger="onBlur"
                                >
                                    <TextArea disabled={disabled}
                                              onMouseDown={(e) => e.stopPropagation()}
                                              className="jade-textarea-input jade-font-size"
                                              onBlur={(e) => changeOnBlur(e, "changeConfig", systemPrompt.id, false)}
                                              placeholder="输入一段提示词，可以给应用预设身份"
                                    />
                                </Form.Item>
                            </Col>
                        </Row>
                    </div>
                </Panel>
            }
        </Collapse>
    );
}