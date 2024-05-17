import {Col, Collapse, Form, Input, InputNumber, Popover, Row} from 'antd';
import {InfoCircleOutlined} from '@ant-design/icons';
import {useDataContext, useDispatch, useFormContext} from "@/components/DefaultRoot.jsx";
import {JadeStopPropagationSelect} from "../common/JadeStopPropagationSelect.jsx";
import PropTypes from "prop-types";

const {TextArea} = Input;
const {Panel} = Collapse;

ModelForm.propTypes = {
    modelOptions: PropTypes.array.isRequired, // 确保 modelOptions 是一个必需的array类型
};

/**
 * 大模型节点模型表单。
 *
 * @param shapeId 所属图形唯一标识。
 * @param modelOptions 模型选项。
 * @returns {JSX.Element} 大模型节点模型表单的DOM。
 */
export default function ModelForm({shapeId, modelOptions}) {
    const data = useDataContext();
    const dispatch = useDispatch();
    const form = useFormContext();
    const model = data.inputParams.find(item => item.name === "model");
    const temperature = data.inputParams.find(item => item.name === "temperature");
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
        } else if (value === 1.0) {
            return '1';
        }
        return value;
    };

    // 失焦时才设置inputNumber的值.若为空，则不设置.
    const onInputNumberBlur = (e) => {
        if (e.target.value === "") {
            return;
        }
        dispatch({
            actionType: "changeConfig",
            id: temperature.id,
            value: e.target.value
        });
    };

    // 失焦时才设置prompt的值.若为空，则不设置.
    const onTextareaBlur = (e) => {
        if (e.target.value === "") {
            return;
        }
        dispatch({
            actionType: "changePrompt",
            id: prompt.id,
            value: e.target.value
        })
    };

    return (
        <Collapse bordered={false} className="jade-collapse-custom-background-color" defaultActiveKey={["modelPanel"]}>
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
                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item
                                    className="jade-form-item"
                                    name={`model-${shapeId}`}
                                    label="模型"
                                    rules={[{required: true, message: '请选择使用的模型'}]}
                                    initialValue={model.value} // 当组件套在Form.Item中的时候，内部组件的初始值使用Form.Item的initialValue进行赋值
                            >
                                <JadeStopPropagationSelect
                                        className="jade-select"
                                        onClick={handleSelectClick} // 点击下拉框时阻止事件冒泡
                                        onChange={(e) => dispatch({actionType: "changeConfig", id: model.id, value: e})}
                                        options={modelOptions}
                                        validateTrigger="onBlur"
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
                                            <InfoCircleOutlined className="jade-panel-header-popover-content"/>
                                        </Popover>
                                    </div>}
                                    rules={[{required: true, message: '请输入0-1之间的参数!'}]}
                                    initialValue={temperature.value}
                                    validateTrigger="onBlur"
                            >
                                <InputNumber
                                        formatter={formatter}
                                        className="jade-input"
                                        style={{width: "100%"}}
                                        min={0}
                                        max={1}
                                        step={0.1}
                                        onBlur={onInputNumberBlur}
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
                                        <span className="jade-second-title">提示词模板</span>
                                        <Popover content={[promptContent]}>
                                            <InfoCircleOutlined className="jade-panel-header-popover-content"/>
                                        </Popover>
                                    </div>}
                                    rules={[{required: true, message: '参数不能为空'}]}
                                    initialValue={prompt.value}
                                    validateTrigger="onBlur"
                            >
                                <TextArea
                                        className="jade-input jade-font-size"
                                        onBlur={(e) => onTextareaBlur(e)}
                                        placeholder="你可以用{{variable name}}来关联输入中的变量名"
                                        autoSize={{minRows: 4, maxRows: 4}}
                                />
                            </Form.Item>
                        </Col>
                    </Row>
                </Panel>
            }
        </Collapse>
    );
}