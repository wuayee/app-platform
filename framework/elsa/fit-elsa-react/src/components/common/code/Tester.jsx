import {Button, message} from "antd";
import {CaretRightOutlined} from "@ant-design/icons";
import {CodeEditor} from "@/components/common/code/CodeEditor.jsx";
import {JsonViewer} from "@/components/common/JsonViewer.jsx";
import {useRef, useState} from "react";

/**
 * 测试组件.
 *
 * @param executeFunc 执行方法.
 * @param language 语言.
 * @param suggestions 建议，用于在输入中进行联想.
 * @return {JSX.Element}
 * @constructor
 */
export const Tester = ({executeFunc, language, suggestions = []}) => {
    const [messageApi, contextHolder] = message.useMessage();
    const [outputJson, setOutputJson] = useState({});
    const [isRunning, setIsRunning] = useState(false);
    const inputRef = useRef("{}");

    const onCopyClick = () => {
        navigator.clipboard.writeText(JSON.stringify(outputJson)).then(() => {
        });
        messageApi.open({type: "success", content: "拷贝成功"}).then(() => {
        });
    };

    const onTestInputChange = (v) => {
        inputRef.current = v;
    };

    const runTest = () => {
        setIsRunning(true);
        executeFunc(inputRef.current, language, (output) => {
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
                <CodeEditor language={"JSON"}
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
                <div className={"jade-code-test-output-content-wrapper"}>
                    <JsonViewer jsonData={outputJson}/>
                </div>
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