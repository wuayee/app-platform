import { Button } from 'antd';
import React, { useState } from 'react';
import type { PaginationProps } from 'antd';
import { Tabs } from 'antd';
import './style.scss';
import TestSet from './testSet';

const { TabPane } = Tabs;

const Evaluate: React.FC = () => {

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


export default Evaluate;
