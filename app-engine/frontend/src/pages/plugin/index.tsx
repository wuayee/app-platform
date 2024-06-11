import React from 'react';
import { Tabs } from 'antd';
import MarketItems from './market';
import MyPlugins from './myplugin';

const Plugin = () => {

  const tabItems = [
    {
      key: 'market',
      label: '市场',
      children: <MarketItems />,
    },
    {
      key: 'user',
      label: '我的',
      children: <MyPlugins />
    },
  ];

  return (
    <div className='aui-fullpage' style={{ display: 'flex', flexDirection: 'column' }}>
      <div className='aui-header-1'>
        <div className='aui-title-1'>插件</div>
      </div>
      <Tabs
        type='card'
        defaultActiveKey='market'
        items={tabItems}
        tabBarStyle={{ margin: 0 }}
        style={{ flexGrow: 1 }}
      />
    </div>
  )
}
export default Plugin;
