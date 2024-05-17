import { Tabs } from 'antd';
import React, { useState } from 'react';
import Evaluate from './evalute';
import AppOverview from './overview';
import './style.scss';

const { TabPane } = Tabs;

//z30048784 应用评估TODO：DDL20240530
const AppDetail = () => {
  return (
    <div className='aui-fullpage'>
      <div className='aui-tab'>
        <Tabs>
          <TabPane tab="概览" key="1">
            <AppOverview />
          </TabPane>
          <TabPane tab="评估" key="2">
            <Evaluate />
          </TabPane>
        </Tabs>
      </div>
    </div>
  )
}

const Component1 = () => {
  return <div>任务中心</div>;
};


export default AppDetail;
