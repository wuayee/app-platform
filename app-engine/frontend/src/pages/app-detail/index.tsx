import { Tabs } from 'antd';
import React, { useState } from 'react';
import Evaluate from './evalute';

const { TabPane } = Tabs;

const AppDetail = () => {
  return (
    <div>
      <Tabs>
        <TabPane tab="任务中心" key="1">
          <Component1 />
        </TabPane>
        <TabPane tab="评估数据集" key="2">
          <Evaluate />
        </TabPane>
      </Tabs>
    </div>
  )
}

const Component1 = () => {
  return <div>任务中心</div>;
};


export default AppDetail;
