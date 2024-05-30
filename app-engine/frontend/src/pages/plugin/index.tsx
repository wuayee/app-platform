import React, { useState, useEffect } from 'react';
import { Tabs, Input } from 'antd';
import Pagination from '../../components/pagination/index';

import PluginCard from '../../components/plugin-card';

import { getPlugins } from '../../shared/http/plugin';
import '../../index.scss'
import { Icons } from "../../components/icons";

const Plugin = () => {
  const categoryItems = [
    { key: 'FIT', label: '推荐' },
    { key: 'NEWS', label: '新闻阅读' },
    { key: 'UTILITY', label: '实用工具' },
    { key: 'SCIENCE', label: '科教' },
    { key: 'SOCIAL', label: '社交' },
    { key: 'LIFE', label: '便民生活' },
    { key: 'WEBSITE', label: '网站搜索' },
    { key: 'GAMES', label: '游戏娱乐' },
    { key: 'FINANCE', label: '财经商务' },
    { key: 'MEDIA', label: '摄影摄像' },
    { key: 'MEETING', label: '会议记录' },
  ];

  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [name, setName] = useState('');
  const [pluginCategory, setPluginCategory] = useState(categoryItems[0].key);
  const [pluginData, setPluginData] = useState([]);

  const getPluginList = (category = pluginCategory)=> {
    getPlugins({ pageNum: pageNum - 1, pageSize, includeTags: pluginCategory, name })
      .then(({ data, total }) => {
        setTotal(total);
        setPluginData(data);
      })
  }

  useEffect(()=> {
    getPluginList();
  }, [pluginCategory, name, pageNum, pageSize]);

  const selectCategory = (category: string) => {
    if (category !== pluginCategory) {
      setPluginCategory(category);
    }
  }

  const selectPage = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
    if (pageSize !== curPageSize) {
      setPageSize(curPageSize);
    }
  }

  const filterByName = (value: string) => {
    if(value !== name) {
      setName(value);
    }
  }

  const MarketItems = () => {
    return <div
      className='aui-block'
      style={{
        height: 'calc(100vh - 118px)',
        display: 'flex',
        flexDirection: 'column',
        borderRadius: '0 8px 0 0',
      }}
    >
      <Tabs
        items={categoryItems}
        activeKey={pluginCategory}
        onChange={(key: string) => selectCategory(key)}
      />
      <Input
        placeholder="搜索"
        style={{
          marginBottom: 16,
          width: '200px',
          borderRadius: '4px',
          border: '1px solid rgb(230, 230, 230)',
        }}
        onPressEnter={(e) => filterByName(e.target.value)}
        prefix={<Icons.search color={'rgb(230, 230, 230)'}/>}
        defaultValue={name}
      />
      <div style={{
        overflowY: 'auto',
        height: '100%',
        display:'flex',
        gap: '16px',
        flexWrap: 'wrap',
        alignContent: 'flex-start'
      }}>
        {pluginData.map((card: any) => <PluginCard key={card.uniqueName} pluginData={card} />)}
      </div>
      <div style={{ paddingTop: 16 }}>
        <Pagination
          total={total}
          current={pageNum}
          onChange={selectPage}
          pageSize={pageSize}
        />
      </div>
    </div>
  }

  const tabItems = [
    {
      key: 'market',
      label: '市场',
      children: <MarketItems />,
    },
    {
      key: 'user',
      label: '我的',
      disabled: true,
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
