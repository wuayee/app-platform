
import React from 'react';
import ChartTable from './components/chart-table';
import ChartGraphs from './components/chart-graphs';
import '../../styles/chart-item.scss';

const ChatItem = (props) => {
  const { chartAnswer, chatItem } = props;
  const { chartType } = chatItem;
  return <>{(
    <div>
      <div className='recieve-mark'>{ chartAnswer || '' }</div>
      {  chartType === 'TABLE' ? <ChartTable chatItem={chatItem} /> : <ChartGraphs chatItem={chatItem}/> }
    </div>
  )}</>
};

export default ChatItem;
