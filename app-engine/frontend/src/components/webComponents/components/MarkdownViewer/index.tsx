/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect } from 'react';
import { marked } from 'marked';
import { message } from 'antd';
import { toClipboard } from './utils'
import hljs from 'highlight.js';
import 'highlight.js/styles/github.css';
import 'github-markdown-css';
import './style.scss';

const MarkdownViewer = ({ content }) => {
    // 自定义渲染器，添加复制按钮和语言标签
    const renderer = new marked.Renderer();
    const [messageApi, contextHolder] = message.useMessage();
    renderer.code = function (code, language) {
        const validLanguage = hljs.getLanguage(language) ? language : 'plaintext';
        const highlightedCode = hljs.highlight(code, { language: validLanguage }).value;
        // 生成唯一的 ID 用于复制功能
        const uniqueId = 'code-' + Math.random().toString(36).substr(2, 9);
        return `
            <div class="code-block">
                <pre><code id="${uniqueId}" class="hljs ${validLanguage}">${highlightedCode}</code></pre>
                <span class="copy-button" data-clipboard-target="${uniqueId}">复制</span>
            </div>
        `;
    };

    // 设置 marked 的选项
    marked.setOptions({
        highlight: (code, language) => {
            const validLanguage = hljs.getLanguage(language) ? language : 'plaintext';
            return hljs.highlight(code, { language: validLanguage }).value;
        },
        renderer, // 使用自定义渲染器
    });

    const htmlContent = marked(content);

    // 复制代码块
    const copyClick = (ele) => {
        const targetID = ele.getAttribute('data-clipboard-target');
        if (targetID) {
            const codeElements = document.getElementById(targetID);
            if (codeElements) {
                const codeTexts = codeElements.textContent;
                if (codeTexts) {
                    toClipboard(codeTexts);
                    copySuccess();
                }
            }
        }
    }
    const copySuccess = () => {
        messageApi.open({
            type: 'success',
            content: '复制成功',
        });
    }
    const codeClick = (event) => {
        if (event.target && event.target.classList.contains('copy-button')) {
            copyClick(event.target);
        }
    }

    useEffect(() => {
        const container = document.querySelector('.markdown-body');
        if (container) {
            container.addEventListener('click', codeClick);
        }
        return () => {
            container && container.removeEventListener('click', codeClick);
        }
    }, []);

    return (
        <>
            {contextHolder}
            <div
                className="markdown-body"
                dangerouslySetInnerHTML={{ __html: htmlContent }}
                style={{ textAlign: 'left' }}
            />
        </>
        
    );
};

export default MarkdownViewer;