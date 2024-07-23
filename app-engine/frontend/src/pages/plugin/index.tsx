import React, { useState ,useEffect } from 'react';
import { Tabs } from 'antd';
import MarketItems from './market';
import MyPlugins from './myplugin';

const Plugin = () => {
  const [activeKey, setActiveKey] = useState('market');
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
  const tabClick = (key) => {
    setActiveKey(key);
  }
  useEffect(() => {
    let isAdd = sessionStorage.getItem('pluginType');
    if (isAdd) {
      setActiveKey('user');
    }
  }, [])
  return (
    <div className='aui-fullpage' style={{ display: 'flex', flexDirection: 'column' }}>
      <div className='aui-header-1'>
        <div className='aui-title-1'>插件</div>
      </div>
      <Tabs
        type='card'
        activeKey={activeKey}
        items={tabItems}
        onTabClick={tabClick}
        tabBarStyle={{ margin: 0 }}
        style={{ flexGrow: 1 }}
      />
    </div>
  )
}
export default Plugin;
