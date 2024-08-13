
import React from 'react';
import Markdown from 'react-markdown';
import { trans } from '@shared/utils/common';
import ChartMessage from '../chart-message/chart-message';

// 消息详情
const MessageBox = (props) => {
  const { content, markdownSyntax, chartConfig, finished } = props;
  return (
    <>{(
      <div className='receive-info'>
        {
          chartConfig ?
            (<ChartMessage chartConfig={chartConfig} />) :
            (
              markdownSyntax ?
                (<Markdown>{content}</Markdown>) :
                <div className='receive-info-html' dangerouslySetInnerHTML={{ __html: trans(content) }}></div>
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
