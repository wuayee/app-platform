import {Editor} from "@monaco-editor/react";
import {useEffect, useRef} from "react";

/**
 * 代码编辑器.
 *
 * @param language 默认语言.
 * @param height 高度.
 * @param code 默认代码.
 * @param onChange 数据变化时触发.
 * @param theme 主题.
 * @param isReadOnly 是否只读.
 * @param suggestions 自动补全. 格式: {label: "args.input", insertText: "args.input"} .
 * @return {JSX.Element}
 * @constructor
 */
export const CodeEditor = ({
                               language,
                               height,
                               code,
                               onChange,
                               theme = "vs-dark",
                               options = {readOnly: true},
                               suggestions = []
                           }) => {
    const monacoRef = useRef(null);
    const _height = isNaN(height) ? height : height + "px";

    const onMount = (editor, monaco) => {
        monacoRef.current = monaco;
        registerSuggestions(monaco);
    };

    useEffect(() => {
        if (monacoRef.current && suggestions.length > 0) {
            registerSuggestions(monacoRef.current);
        }
    });

    const registerSuggestions = (monaco) => {
        monaco.languages.registerCompletionItemProvider(language, {
            provideCompletionItems: () => {
                return {
                    suggestions: suggestions.map(s => {
                        return {
                            label: s.label,
                            kind: monaco.languages.CompletionItemKind.Field,
                            insertText: s.label,
                            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet
                        };
                    })
                };
            }
        })
    };

    return (<>
        <Editor className={"jade-code-editor"}
                defaultLanguage={language}
                language={language}
                width={"100%"}
                height={_height}
                defaultValue={code}
                theme={theme}
                options={{...options}}
                onMount={onMount}
                onChange={(v, e) => onChange && onChange(v)}/>
    </>);
};