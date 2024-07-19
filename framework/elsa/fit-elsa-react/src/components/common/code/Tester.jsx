import {Button, Card, message} from "antd";
import {CaretRightOutlined} from "@ant-design/icons";
import CodeEditor from "@/components/common/code/CodeEditor.jsx";
import {JsonViewer} from "@/components/common/JsonViewer.jsx";
import {useRef, useState} from "react";

/**
 * 测试组件.
 *
 * @param codeRef code编辑区代码引用
 * @param executeFunc 执行方法.
 * @param language 语言.
 * @param suggestions 建议，用于在输入中进行联想.
 * @return {JSX.Element}
 * @constructor
 */
export const Tester = ({codeRef, executeFunc, language, suggestions = []}) => {
    const [messageApi, contextHolder] = message.useMessage();
    const [outputJson, setOutputJson] = useState({});
    const [isRunning, setIsRunning] = useState(false);
    const inputRef = useRef("{}");

    /**
     * 使用老版本的API进行代码复制操作
     */
    const fallbackCopy = () => {
        const textToCopy = JSON.stringify(outputJson);

        // 创建一个临时的textarea元素
        const textArea = document.createElement("textarea");
        textArea.value = textToCopy;
        document.body.appendChild(textArea);
        textArea.select();
        textArea.setSelectionRange(0, 99999); // 对移动设备进行处理

        try {
            document.execCommand("copy");
            messageApi.open({type: "success", content: "拷贝成功"});
        } catch (err) {
            messageApi.open({type: "error", content: "拷贝失败"});
        }

        // 移除临时的textarea元素
        document.body.removeChild(textArea);
    };

    const onCopyClick = () => {
        if (navigator.clipboard) {
            navigator.clipboard.writeText(JSON.stringify(outputJson)).then(() => {
            });
            messageApi.open({type: "success", content: "拷贝成功"}).then(() => {
            }).catch(err => {
                console.error('Failed to copy using clipboard API: ', err);
                fallbackCopy();
            });
        } else {
            fallbackCopy();
        }
    };

    const onTestInputChange = (v) => {
        inputRef.current = v;
    };

    const runTest = () => {
        setIsRunning(true);
        executeFunc(codeRef.current, inputRef.current, language, (output) => {
            setOutputJson(output);
            setIsRunning(false);
        });
    };

    return (<>
        {contextHolder}
        <div className={"jade-code-test-input jade-code-parent"}>
            <div className={"jade-code-test-input-title"}>
                <span>输入</span>
            </div>
            <div className={"jade-code-test-input-content"}>
                <CodeEditor language={"json"}
                            code={"{}"}
                            options={{readOnly: false, lineNumbers: "off"}}
                            suggestions={suggestions}
                            onChange={onTestInputChange}/>
            </div>
            <div className={"jade-code-test-input-footer"}>
                <Button type="primary"
                        disabled={isRunning}
                        className={"jade-code-button"}
                        style={{marginLeft: 0, fontWeight: 700}}
                        icon={<CaretRightOutlined/>}
                        onClick={runTest}>
                    运行
                </Button>
            </div>
        </div>
        <div className={"jade-code-test-output jade-code-parent"}>
            <div className={"jade-code-test-output-title"}>
                <span>输出</span>
            </div>
            <div className={"jade-code-test-output-content"}>
                {typeof outputJson === "object" ?
                        <div className={"jade-code-test-output-content-wrapper"}>
                            <JsonViewer jsonData={outputJson}/>
                        </div>
                        :
                        <Card className={"code-run-style"}>
                            {outputJson}
                        </Card>}
            </div>
            <div className={"jade-code-test-output-footer"}>
                <Button className={"jade-code-button"}
                        style={{marginLeft: 0}}
                        onClick={onCopyClick}>
                    复制
                </Button>
            </div>
        </div>
    </>);
};