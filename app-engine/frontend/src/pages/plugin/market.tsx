import React, { useState, useEffect } from 'react';
import { Tabs, Input, Tag } from 'antd';
import Pagination from '../../components/pagination/index';
import PluginCard from '../../components/plugin-card';
import { getPlugins } from '../../shared/http/plugin';
import '../../index.scss'
import { Icons } from "../../components/icons";
import EmptyItem from '../../components/empty/empty-item';
import { sourceTabs } from './helper';

const MarketItems = () => {
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [name, setName] = useState(undefined);
  const [selectedSource, setSelectedSource] = useState(sourceTabs?.[0]?.key);
  const [pluginData, setPluginData] = useState([]);

  const tabSearch = (
    <Input
       showCount
       maxLength={20}
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
  );

  useEffect(() => {
    getPluginList();
  }, [selectedSource, name, pageNum, pageSize]);

  const getPluginList = () => {
    getPlugins({ pageNum: pageNum - 1, pageSize, includeTags: selectedSource?.toUpperCase(), name })
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
    if (value !== name) {
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
    <Tabs
      items={sourceTabs}
      activeKey={selectedSource}
      onChange={(key: string) => setSelectedSource(key)}
      tabBarExtraContent={tabSearch}
    />
    {pluginData.length > 0 ? <>
      <div style={{
        overflowY: 'auto',
        height: '100%',
        display: 'flex',
        gap: '16px',
        flexWrap: 'wrap',
        alignContent: 'flex-start'
      }}>
        {pluginData.map((card: any) => <PluginCard key={card.uniqueName} pluginData={card}/>)}
      </div>
      <div style={{ paddingTop: 16 }}>
        <Pagination
          total={total}
          current={pageNum}
          onChange={selectPage}
          pageSize={pageSize}
        />
      </div>
    </>
      :
      <div style={{paddingTop: 100}}>
        <EmptyItem />
      </div>
    }
  </div>
}

export default MarketItems;
