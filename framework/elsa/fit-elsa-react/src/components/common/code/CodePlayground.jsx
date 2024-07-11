import {useRef, useState} from "react";
import "./codePlaygroundStyle.css";
import {Tester} from "@/components/common/code/Tester.jsx";
import {CodeEditor} from "@/components/common/code/CodeEditor.jsx";
import {Button, Dropdown, Space} from "antd";
import {CloseOutlined, DownOutlined, RightOutlined} from "@ant-design/icons";

const TEST_WIDTH = 600;

/**
 * 代码调试器.
 *
 * @param width 宽度.
 * @param languages 语言.
 * @param editorConfig 编辑器配置.
 * @param onClose 关闭时的回调.
 * @param onConfirm 确认时的回调.
 * @param executeFunc 执行函数，该函数第一个参数为参数args，第二参数为回调.
 * @return {JSX.Element}
 * @constructor
 */
export const CodePlayground = ({width, languages, editorConfig, onClose, onConfirm, executeFunc}) => {
    const [ctl, setCtl] = useState({testStatus: "none", codeWidth: width});
    const codeRef = useRef(editorConfig.code);
    const [language, setLanguage] = useState(editorConfig.language);
    const defaultKey = languages.indexOf(editorConfig.language);
    const items = languages.map((l, i) => {
        return {key: i + "", label: l};
    });

    const onLanguageClick = (e) => {
        setLanguage(items.find(i => i.key === e.key)?.label);
    };

    /**
     * 语言下拉组件.
     *
     * @return {JSX.Element} 组件对象.
     * @constructor
     */
    const LanguageDropDown = () => {
        return (<>
            <div className={"code-title-language"} style={{display: "flex"}}>
                <span style={{color: "rgb(128, 128, 128)"}}>语言</span>
                <div style={{marginLeft: 8}}>
                    <Dropdown trigger={"click"}
                              menu={{
                                  items, selectable: true, defaultSelectedKeys: [defaultKey], onClick: onLanguageClick
                              }}
                    >
                        <a onClick={(e) => e.preventDefault()}>
                            <Space style={{color: "rgb(26, 26, 26)"}}>
                                {language}
                                <DownOutlined/>
                            </Space>
                        </a>
                    </Dropdown>
                </div>
            </div>
        </>);
    };

    /**
     * 代码标题组件.
     *
     * @return {JSX.Element} 组件对象.
     * @constructor
     */
    const CodeTitle = () => {
        return (<>
            <div style={{width: "8%", minWidth: 100}}>
                <div className="code-title-text"><span>编辑代码</span></div>
            </div>
            <div style={{width: "77%"}}>
                <LanguageDropDown/>
            </div>
            <div style={{width: "15%", display: "flex", justifyContent: "end"}}>
                <div style={{display: "flex", alignItems: "center"}}>
                    <Button onClick={onTestButtonClick} className={"code-title-test-text"} type="text">测试代码</Button>
                    <Button onClick={onClose}
                            style={{width: 16, height: 16, marginRight: 0}}
                            type="text"
                            icon={<CloseOutlined/>}/>
                </div>
            </div>
        </>);
    };

    const onTestClose = () => {
        setCtl({testStatus: "none", codeWidth: width});
    };

    const onTestButtonClick = () => {
        const isFlex = ctl.testStatus === "flex";
        setCtl({
            testStatus: isFlex ? "none" : "flex", codeWidth: isFlex ? width : width - TEST_WIDTH
        });
    };

    return (<>
        <div className={"jade-code-container"} style={{width: width}}>
            <div className={"jade-code-code jade-code-parent"} style={{width: ctl.codeWidth}}>
                <div className={"jade-code-code-title"}>
                    <CodeTitle/>
                </div>
                <div className={"jade-code-code-content"}>
                    <CodeEditor language={language}
                                code={editorConfig.code}
                                options={{readOnly: false}}
                                suggestions={editorConfig.suggestions}
                                onChange={(v) => codeRef.current = v}/>
                </div>
                <div className={"jade-code-code-footer"}>
                    <Button className={"jade-code-button"} style={{marginRight: 16}} onClick={onClose}>取消</Button>
                    <Button type="primary"
                            className={"jade-code-button"}
                            style={{marginRight: 0, fontWeight: 700}}
                            onClick={() => onConfirm(codeRef.current)}>确定</Button>
                </div>
            </div>
            <div className={"jade-code-test jade-code-parent"} style={{display: ctl.testStatus}}>
                <div className={"jade-code-test-header"}>
                    <div className={"jade-code-test-header-text"}>
                        <span>测试代码</span>
                    </div>
                    <div className={"jade-code-test-header-close"}>
                        <Button onClick={onTestClose}
                                style={{width: 16, height: 16, marginLeft: 16}}
                                type="text"
                                icon={<RightOutlined/>}/>
                    </div>
                </div>
                <div className={"jade-code-test-content jade-code-parent"}>
                    <Tester executeFunc={executeFunc} suggestions={editorConfig.suggestions} language={language} />
                </div>
            </div>
        </div>
    </>);
};