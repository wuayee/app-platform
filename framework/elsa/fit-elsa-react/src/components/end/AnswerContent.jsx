import React, {useState} from 'react';
import {Collapse, Input, Popover, Switch} from 'antd';
import {InfoCircleOutlined} from "@ant-design/icons";

const {Panel} = Collapse;
const {TextArea} = Input;

/**
 * 回答内容组件，一个折叠区域中包含一个文本输入框
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function AnswerContent() {
    const [value, setValue] = useState('');
    const [required, setRequired] = useState(true);

    const onClick = (checked, event) => {
        event.stopPropagation();
        setRequired(checked);
    };

    const tips =
        <div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
            <p>编辑机器人的回复内容，工作流完成后，机器人中的</p>
            <p>大模型将不再组织语言，而是直接用此处编辑的原始内容回复对话。</p>
            <p>可以使用{"{{变量名称}}"}格式引用输入参数中的变量</p>
        </div>;

    return (
        <div>
            <Collapse bordered={false} style={{marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%"}}
                      defaultActiveKey={["Answer content"]}>
                <Panel header={
                    <div style={{
                        display: 'flex',
                        alignItems: 'center',
                        height: "22px",
                        justifyContent: "space-between"
                    }}>
                        <div className="Answer-content">
                            <span
                                className="Answer-content-text">Answer content</span>
                            <Popover content={tips}>
                                <InfoCircleOutlined/>
                            </Popover>
                        </div>

                        <div
                            style={{display: 'flex'}}>
                            <div className="Streaming-output" style={{marginLeft: "auto", marginRight: "10px"}}>
                            <span
                                className='Streaming-output-text'>Streaming output</span>
                                <Popover content={tips}>
                                    <InfoCircleOutlined/>
                                </Popover>
                            </div>
                            <div className="switch-container">
                                <Switch onChange={onClick} defaultChecked className="required-switch"/>
                            </div>
                        </div>
                    </div>
                } key={"Answer content"}>
                    <TextArea rows={4}
                              placeholder="Variables in output parameters can be referenced using {{variable name}}"
                              onChange={(e) => setValue(e.target.value)}/>
                </Panel>
            </Collapse>
        </div>
    )
}