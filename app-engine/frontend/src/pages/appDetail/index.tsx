import React, { useState } from 'react';
import GoBack from '../../components/go-back/GoBack';
import { Tabs } from 'antd';
import AppAnalyse from './analyse';
import AppOverview from './overview';
import AppEvaluate from './evalute';

const onChange = (key: string) => {
  console.log(key);
};

const AppDetail: React.FC = () => {
  const items: TabsProps['items'] = [
    {
      key: '1',
      label: '概览',
      children: <AppOverview />
    },
    {
      key: '2',
      label: '分析',
      children: <AppAnalyse></AppAnalyse>,
    },
    {
      key: '3',
      label: '反馈',
      children: 'Content of Tab Pane 3',
    },
    {
      key: '4',
      label: '评估',
      children: <AppEvaluate />,
    },
  ];
  return(
  <div className='aui-fullpage'>
    <div className='aui-header-1'>
      <div className='aui-title-1'><GoBack path={'/app'} title='应用详情'/></div>
    </div>
    <div className='aui-block' style={{paddingTop:0}}>
       <Tabs defaultActiveKey="1" items={items} onChange={onChange} />
      <div />
    </div>
  </div>
)};

export default AppDetail;
