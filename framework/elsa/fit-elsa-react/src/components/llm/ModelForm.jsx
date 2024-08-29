import {Col, Collapse, Form, Input, InputNumber, Popover, Row} from 'antd';
import {QuestionCircleOutlined} from '@ant-design/icons';
import {useDispatch} from "@/components/DefaultRoot.jsx";
import {JadeStopPropagationSelect} from "../common/JadeStopPropagationSelect.jsx";
import PropTypes from "prop-types";
import React from "react";
import {Trans, useTranslation} from "react-i18next";

const {TextArea} = Input;
const {Panel} = Collapse;

_ModelForm.propTypes = {
    shapeId: PropTypes.string.isRequired, // 确保 shapeId 是一个必需的string类型
    modelData: PropTypes.object.isRequired, // 确保 modelData 是一个必需的object类型
    modelOptions: PropTypes.array.isRequired, // 确保 modelOptions 是一个必需的array类型
    disabled: PropTypes.bool // 确保 modelOptions 是一个必需的array类型
};

/**
 * 大模型节点模型表单。
 *
 * @param shapeId 所属图形唯一标识。
 * @param modelData 数据.
 * @param modelOptions 模型选项。
 * @param disabled 是否禁用.
 * @returns {JSX.Element} 大模型节点模型表单的DOM。
 */
function _ModelForm({shapeId, modelData, modelOptions, disabled}) {
    const dispatch = useDispatch();
    const {t} = useTranslation();
    const [form] = Form.useForm();
    const model = modelData.model;
    const temperature = modelData.temperature;
    const systemPrompt = modelData.systemPrompt;
    const prompt = modelData.prompt;

    const handleSelectClick = (event) => {
        event.stopPropagation(); // 阻止事件冒泡
    };

    const content = (<div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
        <Trans i18nKey="llmTemperaturePopover" components={{p: <p/>}}/>
    </div>);

    const promptContent = (<div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
        <Trans i18nKey="promptPopover" components={{p: <p/>}}/>
    </div>);

    const formatter = (value, {input, userTyping}) => {
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
        let originValue = parseFloat(e.target.value); // 将输入值转换为浮点数
        // 如果转换后的值不是数字（NaN），则将其设为 0
        if (isNaN(originValue)) {
            originValue = 0;
        }
        let changeValue;
        if (originValue <= 0.0) {
            changeValue = 0;
        } else if (originValue >= 1.0) {
            changeValue = 1;
        } else {
            changeValue = Math.round(originValue*10)/10; // 保留小数点后一位
        }
        dispatch({actionType: actionType, id: id, value: changeValue});
    };

    return (<Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["modelPanel"]}>
        {<Panel
            key={"modelPanel"}
            header={<div className="panel-header">
                <span className="jade-panel-header-font">{t('llm')}</span>
            </div>}
            className="jade-panel"
        >
            <div className={"jade-custom-panel-content"}>
                <Row gutter={16}>
                    <Col span={12}>
                        <Form.Item
                            className="jade-form-item"
                            name={`model-${shapeId}`}
                            label={t('model')}
                            rules={[{required: true, message: t('pleaseSelectTheModelToBeUsed')}]}
                            initialValue={model.value} // 当组件套在Form.Item中的时候，内部组件的初始值使用Form.Item的initialValue进行赋值
                            validateTrigger="onBlur"
                        >
                            <JadeStopPropagationSelect
                                disabled={disabled}
                                className="jade-select"
                                onClick={handleSelectClick} // 点击下拉框时阻止事件冒泡
                                onChange={(e) => dispatch({
                                    actionType: "changeConfig",
                                    id: model.id,
                                    value: e
                                })}
                                options={modelOptions}
                            />
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item
                            className="jade-form-item"
                            name={`temperature-${shapeId}`}
                            label={<div style={{display: 'flex', alignItems: 'center'}}>
                                <span className="jade-second-title">{t('temperature')}</span>
                                <Popover content={content}>
                                    <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                                </Popover>
                            </div>}
                            rules={[{required: true, message: t('pleaseEnterAValueRangingFrom0To1')}]}
                            initialValue={temperature.value}
                            validateTrigger="onBlur"
                        >
                            <InputNumber disabled={disabled}
                                         formatter={formatter}
                                         className="jade-input"
                                         style={{width: "100%"}}
                                         type="number"
                                         precision={1}
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
                                <span className="jade-second-title">{t('prompt')}</span>
                                <Popover content={[promptContent]}>
                                    <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                                </Popover>
                            </div>}
                            rules={[{required: true, message: t('paramCannotBeEmpty')}]}
                            initialValue={prompt.value}
                            validateTrigger="onBlur"
                        >
                            <TextArea disabled={disabled}
                                      className="jade-textarea-input jade-font-size"
                                      onBlur={(e) => changeOnBlur(e, "changePrompt", prompt.id, true)}
                                      placeholder={t('systemPromptPlaceHolder')}
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
                                <span className="jade-second-title">{t('systemPrompt')}</span>
                            </div>}
                            initialValue={systemPrompt.value}
                            validateTrigger="onBlur"
                        >
                            <TextArea disabled={disabled}
                                      className="jade-textarea-input jade-font-size"
                                      onBlur={(e) => changeOnBlur(e, "changeConfig", systemPrompt.id, false)}
                                      placeholder={t('systemPromptPlaceHolder')}
                            />
                        </Form.Item>
                    </Col>
                </Row>
            </div>
        </Panel>}
    </Collapse>);
}

const areEqual = (prevProps, nextProps) => {
    return prevProps.modelData.model === nextProps.modelData.model
        && prevProps.modelData.temperature === nextProps.modelData.temperature
        && prevProps.modelData.systemPrompt === nextProps.modelData.systemPrompt
        && prevProps.modelData.prompt === nextProps.modelData.prompt
        && prevProps.modelOptions === nextProps.modelOptions
        && prevProps.disabled === nextProps.disabled;
};

export const ModelForm = React.memo(_ModelForm, areEqual);