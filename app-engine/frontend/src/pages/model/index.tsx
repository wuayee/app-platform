import React, { useState } from 'react';
import { Tabs } from 'antd';

import '../../index.scss';
import LocalModelList from './local-model';
import ExternalModel from './extenal-model';

const ModelList = () => {

  const tabItems: TabsProps['items'] = [
    {
      key: '1',
      label: '本地模型服务',
      children: <LocalModelList />
    },
    {
      key: '2',
      label: '外部模型服务',
      children: <ExternalModel />
    },
  ];

  return (
    <div className='aui-fullpage'>
      <div
        className='aui-header-1'
        style={{
          display: 'flex',
          gap: '1000px',
        }}
      >
        <div className='aui-title-1'>模型服务</div>
      </div>
      <div className='aui-block'>
        <Tabs defaultActiveKey='1' items={tabItems} />
      </div>
    </div>
  );
};
export default ModelList;
