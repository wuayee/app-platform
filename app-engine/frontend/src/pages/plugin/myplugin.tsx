import React, { useState, useEffect } from 'react';
import { Input, Button, Dropdown } from 'antd';
import type { MenuProps } from 'antd';
import Pagination from '../../components/pagination/index';
import PluginCard from '../../components/plugin-card';
import { getPlugins } from '../../shared/http/plugin';
import '../../index.scss';
import { Icons } from "../../components/icons";
import { DownOutlined } from '@ant-design/icons';

const createItems: MenuProps['items'] = [
  {
    key: '1',
    label: '上传工具',
  },
  {
    key: '2',
    label: '创建工具流',
  }
];

const MyPlugins = () => {

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
  const [name, setName] = useState<string>('');
  const [pluginData, setPluginData] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('FAVOURITE');


  useEffect(() => {
    getPluginList();
  }, [selectedCategory, name, pageNum, pageSize]);

  const getPluginList = () => {
    getPlugins({ pageNum: pageNum - 1, pageSize, includeTags: selectedCategory, name })
      .then(({ data, total }) => {
        setTotal(total);
        setPluginData(data);
      })
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
    if (value) {
      setName(value);
    }
  }

  return <div
    className='aui-block'
    style={{
      height: 'calc(100vh - 140px)',
      display: 'flex',
      flexDirection: 'column',
      borderRadius: '0 8px 0 0',
    }}
  >
    <div>
      <Dropdown menu={{ items: createItems }}>
        <Button
          type='primary'
          icon={<DownOutlined />}
          iconPosition='end'
          style={{
            borderRadius: 4,
            background: '#2673E5',
            marginRight: 16
          }}
        >
          创建
        </Button>
      </Dropdown>
      <Input
        placeholder="搜索"
        style={{
          marginBottom: 16,
          width: '200px',
          borderRadius: '4px',
          border: '1px solid rgb(230, 230, 230)',
        }}
        onPressEnter={(e) => filterByName(e.target.value)}
        prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
        defaultValue={name}
      />
    </div>
    <div style={{
      overflowY: 'auto',
      height: '100%',
      display: 'flex',
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
  </div >
}

export default MyPlugins;
