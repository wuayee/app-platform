import React, { useState } from 'react';
import { Tabs } from 'antd';
import TestSet from './testSet';

const { TabPane } = Tabs;

const AppEvaluate: React.FC = () => {

  return (
    <div className='aui-tab'>
      <Tabs>
        <TabPane tab="任务中心" key="1">
        </TabPane>
        <TabPane tab="评估测试集" key="2">
          <TestSet />
        </TabPane>
      </Tabs>
    </div>
  )
}


export default AppEvaluate;
