
import React from 'react';
import { marked } from 'marked';
import hljs from 'highlight.js';
import ChartMessage from '../chart-message/chart-message';
import 'highlight.js/styles/monokai-sublime.min.css'

// 消息详情
const MessageBox = (props) => {
  const { content, chartConfig, finished } = props;

  const markedProcess = (content) => {
    return marked(content, {
      highlight: (code, lang) => {
        if (code) {
          const validLanguage = hljs.getLanguage(lang) ? lang : 'javascript';
          return hljs.highlight(code, { language: validLanguage }).value;
        }
      }
    })
  }
  return (
    <>{(
      <div className='receive-info'>
        {
          chartConfig ?
            (<ChartMessage chartConfig={chartConfig} />) :
            (
              <div className='receive-info-html' dangerouslySetInnerHTML={{ __html: markedProcess(content) }}></div>
            )
        }
        {
          finished &&
          <div className='receive-tips'>
            以上内容为AI生成，不代表开发者立场，请勿删除或修改本标记
          </div>
        }
      </div>
    )}</>
  )
}

export default MessageBox;
