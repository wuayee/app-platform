import {Button, Collapse, Form, Popover, Typography} from "antd";
import {QuestionCircleOutlined} from "@ant-design/icons";
import {CodeEditor} from "@/components/common/code/CodeEditor.jsx";
import React, {useState} from "react";
import EditCode from '../asserts/icon-edit.svg?react';
import {useFormContext, useShapeContext} from "@/components/DefaultRoot.jsx";
import {CodeDrawer} from "@/components/common/code/CodeDrawer.jsx";
import httpUtil from "@/components/util/httpUtil.jsx";

const {Panel} = Collapse;
const {Text} = Typography;
const defaultEditorHeight = 272;
const defaultDrawerWidth = 1232;
const responseCode = 0;

/**
 * code编辑器面板
 *
 * @param disabled 是否禁用
 * @param data 数据
 * @param dispatch 回调
 * @return {JSX.Element}
 * @constructor
 */
export default function CodePanel({disabled, data, dispatch}) {
    const [open, setOpen] = useState(false);
    const [currentCode, setCurrentCode] = useState(data.inputParams.find(item => item.name === "code").value);
    const shape = useShapeContext();
    const url = shape.graph.configs.find(config => config.node === "codeNodeState") && shape.graph.configs.find(config => config.node === "codeNodeState").urls.testCodeUrl;
    const disableCodeDrawer = true;
    const suggestions = data.inputParams.find(item => item.name === "args").value.map(arg => {
        return {label: arg.name, insertText: arg.name}
    });
    const form = useFormContext();
    const selectedLanguage = data.inputParams.find(item => item.name === "language").value;
    const code = data.inputParams.find(item => item.name === "code").value;

    /**
     * 更新code代码
     *
     * @param value 新的code代码
     */
    const editCode = (value) => {
        setCurrentCode(value);
        dispatch({type: "editCode", value: value});
        form.setFieldsValue({[`codeEditor-${shape.id}`]: value});
    };

    /**
     * 点击弹出code编辑页面
     *
     * @param event 事件
     */
    const handleEditClick = (event) => {
        event.stopPropagation();
        setOpen(true);
    };

    /**
     * 代码执行结果展示的回调方法
     *
     * @param args 入参
     * @param language 编程语言
     * @param callback 回调方法
     */
    const executeFunc = (args, language, callback) => {
        console.log("execute: ", args)
        const input = {};
        input.args = args;
        input.code = currentCode;
        input.language = language;
        httpUtil.post(url, input, undefined, (response) => {
            if (response.code === responseCode) {
                callback(response.data);
            } else {
                callback(response.message);
            }
        }, (err) => {
            console.error('Error test code:', err);
            callback("系统运行异常，请联系系统管理员");
        });
    };

    return (<>
        <Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["codeOutputPanel"]}>
            {<Panel onMouseDown={(e) => e.stopPropagation()}
                    key={"codeOutputPanel"}
                    header={<Header disableCodeDrawer={disableCodeDrawer}
                                    handleEditClick={handleEditClick}/>}
                    className="jade-panel"
            >
                <Form.Item name={`codeEditor-${shape.id}`}
                           initialValue={code}
                           rules={[
                               {
                                   required: true,
                                   message: '代码不能为空！',
                               },
                           ]}>
                    <CodeEditor code={currentCode} height={defaultEditorHeight} language={selectedLanguage}
                                options={{readOnly: !disableCodeDrawer || disabled}}
                                onChange={editCode}/>
                </Form.Item>
                <CodeDrawer container={shape.page.graph.div.parentElement}
                            width={defaultDrawerWidth}
                            open={open}
                            languages={["python"]}
                            editorConfig={{
                                language: selectedLanguage, code: currentCode, suggestions: suggestions
                            }}
                            onClose={() => setOpen(false)}
                            onConfirm={(v) => editCode(v)}
                            executeFunc={executeFunc}/>
            </Panel>}
        </Collapse>
    </>);
}

/**
 * 代码折叠区域头部组件
 *
 * @param disableCodeDrawer 是否展示弹出编辑弹框按钮
 * @param handleEditClick 弹出编辑弹框按钮的回调
 * @return {JSX.Element} Header组件
 * @constructor
 */
const Header = ({disableCodeDrawer, handleEditClick}) => {
    const content = (<div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
        <p>参考代码示例编写一个函数的结构，你可以直接使用输入参</p>
        <p>数中的变量，并通过return一个对象、数组或者其他基本</p>
        <p>类型的数据作为输出结果。方法名称必须是main，方法不</p>
        <p>能是异步方法，并且不支持编写多个函数。</p></div>);

    return (<>
        <div className="panel-header">
            <span>
                <Text type="danger">*</Text>
                <span className="jade-panel-header-font"> 代码</span>
            </span>
            <Popover content={content}>
                <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
            </Popover>
            <Button type="link"
                    className={"button-edit"}
                    onClick={handleEditClick}
                    style={{visibility: disableCodeDrawer ? "hidden" : "visible"}}
            >
                <div className={"button-wrapper"}><EditCode/>
                    <div className={"edit-button-text"}>在IDE中编辑</div>
                </div>
            </Button>
        </div>
    </>);
};