
import React from 'react';
import { marked } from 'marked';
import hljs from 'highlight.js';
import ChartMessage from '../chart-message/chart-message';
import { useTranslation } from 'react-i18next';
import 'highlight.js/styles/monokai-sublime.min.css';

// 消息详情
const MessageBox = (props) => {
  const { t } = useTranslation();
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
            {t('receiveTips')}
          </div>
        }
      </div>
    )}</>
  )
}

export default MessageBox;
