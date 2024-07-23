import {Button, Collapse, Form, Popover, Typography} from "antd";
import {QuestionCircleOutlined} from "@ant-design/icons";
import CodeEditor from "@/components/common/code/CodeEditor.jsx";
import React, {useState} from "react";
import EditCode from '../asserts/icon-edit.svg?react';
import {useFormContext, useShapeContext} from "@/components/DefaultRoot.jsx";
import {CodeDrawer} from "@/components/common/code/CodeDrawer.jsx";
import httpUtil from "@/components/util/httpUtil.jsx";
import ArrayUtil from "@/components/util/ArrayUtil.js";
import PropTypes from "prop-types";

const {Panel} = Collapse;
const {Text} = Typography;
const defaultEditorHeight = 272;
const defaultDrawerWidth = 1232;
const successCode = 0;

_CodePanel.propTypes = {
    input: PropTypes.array.isRequired,
    disabled: PropTypes.bool,
    dispatch: PropTypes.func
};

/**
 * code编辑器面板
 *
 * @param disabled 是否禁用
 * @param input 数据
 * @param dispatch 回调
 * @return {JSX.Element}
 * @constructor
 */
function _CodePanel({disabled, input, dispatch}) {
    const [open, setOpen] = useState(false);
    const shape = useShapeContext();
    const url = shape.graph.configs.find(config => config.node === "codeNodeState") && shape.graph.configs.find(config => config.node === "codeNodeState").urls.testCodeUrl;
    const form = useFormContext();
    const suggestions = input.find(item => item.name === "args").value.map(arg => {
        return {label: arg.name, insertText: arg.name}
    });
    const selectedLanguage = input.find(item => item.name === "language").value;
    const code = input.find(item => item.name === "code").value;

    /**
     * 更新code代码
     *
     * @param value 新的code代码
     */
    const editCode = (value) => {
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
     * @param currentCode 调试区代码
     * @param args 入参
     * @param language 编程语言
     * @param callback 回调方法
     * @param errorCallback 错误发生时的回调
     */
    const executeFunc = (currentCode, args, language, callback, errorCallback) => {
        console.log("execute: ", args)
        const input = {};
        try {
            input.args = JSON.parse(args);
        } catch (e) {
            console.error('Error process params:', e.message);
            errorCallback("输入格式错误：" + e.message);
            return;
        }
        input.code = currentCode;
        input.language = language;
        httpUtil.post(url, input, undefined, (response) => {
            if (response.code === successCode) {
                callback(response.data);
            } else {
                console.error('Test code error:', response.msg);
                errorCallback(response.msg);
            }
        }, (err) => {
            console.error('Error invoke test code api:', err);
            errorCallback("系统运行异常，请联系系统管理员");
        });
    };

    return (<>
        <Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["codeOutputPanel"]}>
            {<Panel onMouseDown={(e) => e.stopPropagation()}
                    key={"codeOutputPanel"}
                    header={<Header handleEditClick={handleEditClick}
                                    disabled={disabled}/>}
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
                    <CodeEditor code={code}
                                height={defaultEditorHeight}
                                language={selectedLanguage}
                                onChange={editCode}/>
                </Form.Item>
                <CodeDrawer container={shape.page.graph.div.parentElement}
                            width={defaultDrawerWidth}
                            open={open}
                            languages={["python"]}
                            editorConfig={{
                                language: selectedLanguage, code: code, suggestions: suggestions
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
 * @param handleEditClick 弹出编辑弹框按钮的回调
 * @param disabled 是否禁用
 * @return {JSX.Element} Header组件
 * @constructor
 */
const Header = ({handleEditClick, disabled}) => {
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
                    disabled={disabled}
            >
                <div className={"button-wrapper"}><EditCode/>
                    <div className={"edit-button-text"}>在IDE中编辑</div>
                </div>
            </Button>
        </div>
    </>);
};

const areEqual = (prevProps, nextProps) => {
    return prevProps.disabled === nextProps.disabled && ArrayUtil.isEqual(prevProps.input, nextProps.input);
};

export const CodePanel = React.memo(_CodePanel, areEqual);