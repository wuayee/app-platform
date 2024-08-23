import * as monaco from 'monaco-editor';
import editorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker';
import jsonWorker from 'monaco-editor/esm/vs/language/json/json.worker?worker';
import cssWorker from 'monaco-editor/esm/vs/language/css/css.worker?worker';
import htmlWorker from 'monaco-editor/esm/vs/language/html/html.worker?worker';
import tsWorker from 'monaco-editor/esm/vs/language/typescript/ts.worker?worker';
import {useEffect, useRef} from "react";
import PropTypes from "prop-types";

self.MonacoEnvironment = {
    getWorker(_, label) {
        if (label === 'json') {
            return new jsonWorker();
        }
        if (label === 'css' || label === 'scss' || label === 'less') {
            return new cssWorker();
        }
        if (label === 'html' || label === 'handlebars' || label === 'razor') {
            return new htmlWorker();
        }
        if (label === 'typescript' || label === 'javascript') {
            return new tsWorker();
        }
        return new editorWorker();
    },
};

CodeEditor.propTypes = {
    language: PropTypes.string.isRequired,
    height: PropTypes.number,
    code: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
    theme: PropTypes.string,
    options: PropTypes.object,
    suggestions: PropTypes.array,
};

/**
 * 代码编辑器.
 *
 * @param language 默认语言.
 * @param code 默认代码.
 * @param onChange 数据变化时触发.
 * @param theme 主题.
 * @param isReadOnly 是否只读.
 * @param suggestions 自动补全. 格式: {label: "args.input", insertText: "args.input"} .
 * @return {JSX.Element}
 * @constructor
 */
export default function CodeEditor({language,
                                       code,
                                       onChange,
                                       theme = "vs-dark",
                                       options = {readOnly: true},
                                       suggestions = []
                                   }) {
    const monacoRef = useRef(null);
    const providerRef = useRef(null);
    const divRef = useRef();

    useEffect(() => {
        if (suggestions.length <= 0) {
            return;
        }
        // 在每次 suggestions 变化时重新注册补全提示
        !options.readOnly && registerSuggestions();
    }, [suggestions]);

    const registerSuggestions = () => {
        // 如果存在，注销之后重新注册
        if (providerRef.current) {
            providerRef.current.dispose();
        }
        providerRef.current = monaco.languages.registerCompletionItemProvider(language, {
            provideCompletionItems: () => {
                return {
                    suggestions: suggestions.map(s => {
                        return {
                            label: s.label,
                            kind: monaco.languages.CompletionItemKind.Field,
                            insertText: _getInsertText(language, s),
                            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet
                        };
                    })
                };
            }
        });
    };

    useEffect(() => {
        monacoRef.current = monaco.editor.create(divRef.current, {
            value: code,
            language: language,
            lineNumbers: "on",
            roundedSelection: false,
            scrollBeyondLastLine: false,
            readOnly: options.readOnly,
            theme: theme,
            automaticLayout: true
        });
        monacoRef.current.onDidChangeModelContent(() => {
            onChange && onChange(monacoRef.current.getModel().getValue());
        });
        registerSuggestions();
        return () => {
            monacoRef.current.dispose();
        };
    }, []);

    /**
     * 构造对应语言的代码补全提示
     *
     * @param language 语言
     * @param suggestion 注册的提示对象
     * @return {*|string} 代码补全提示
     * @private
     */
    const _getInsertText = (language, suggestion) => {
        if (language === 'json') {
            return `"${suggestion.insertText}": ""`;
        }
        return suggestion.insertText;
    };

    return (<>
        <div ref={divRef} className={"jade-code-editor"} style={{width: "100%", height: "100%"}}/>
    </>);
}