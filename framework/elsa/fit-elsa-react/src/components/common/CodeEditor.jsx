import {Editor} from "@monaco-editor/react";

/**
 * 代码编辑器.
 *
 * @param defaultLanguage 默认语言.
 * @param code 默认代码.
 * @param onChange 数据变化时触发.
 * @param theme 主题.
 * @param isReadOnly 是否只读.
 * @return {JSX.Element}
 * @constructor
 */
export const CodeEditor = ({defaultLanguage, code, onChange, theme = "vs-dark", isReadOnly = true}) => {
    return (<>
        <div className={"jade-code-editor-container"}>
            <Editor className={"jade-code-editor"}
                    defaultLanguage={defaultLanguage}
                    width={"100%"}
                    height={"100%"}
                    defaultValue={code}
                    theme={theme}
                    options={{readOnly: isReadOnly}}
                    onChange={(v, e) => onChange(v)}/>
        </div>
    </>);
};