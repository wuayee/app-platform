
import React, { useEffect, useState } from 'react';
import Markdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism'
import ChartMessage from '../chart-message.jsx';

// 消息详情
const MessageBox = (props) => {
  const { content, markdownSyntax, chartConfig } = props;
  const CodeBlock = ({ children = [], className, ...props }) => {
    const match = /language-(\w+)/.exec(className || '');
    if (className) {
      return (
        <SyntaxHighlighter
            language={match?.[1]}
            showLineNumbers={ false }
            style={ oneDark }
            PreTag='div'
            className='syntax-hight-wrapper'
            {...props}
          >
            {children}
        </SyntaxHighlighter>)
    }
    return (<code>{children}</code>) 
  } 
  return(
    <>{(
      <div className="recieve-info">
        { 
          chartConfig ? 
          ( <ChartMessage chartConfig={ chartConfig } /> ) : 
          (
            markdownSyntax ? (<Markdown
              components={{ code: CodeBlock, p: 'div'}}>
              { content }
            </Markdown>) : <div>{ content }</div>
          ) 
        }
        <div className="recieve-tips">
          以上内容为AI生成，不代表开发者立场，请勿删除或修改本标记
        </div>
      </div>
    )}</>
  )
}


export default MessageBox;
