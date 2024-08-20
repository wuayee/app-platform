import React, { useState, useEffect } from 'react';
import { Tabs, Input, Tag, Button } from 'antd';
import Pagination from '../../components/pagination/index';
import PluginCard from '../../components/plugin-card';
import { getPlugins } from '../../shared/http/plugin';
import '../../index.scss';
import { Icons } from '../../components/icons';
import EmptyItem from '../../components/empty/empty-item';
import { PluginCardTypeE, sourceTabs } from './helper';
import UploadToolDrawer from './upload/uploadTool';

const MarketItems = () => {
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [name, setName] = useState(undefined);
  const [selectedSource, setSelectedSource] = useState(sourceTabs?.[0]?.key);
  const [pluginData, setPluginData] = useState([]);
  const [isOpenPlugin, setIsOpenPlugin] = useState(0);

  useEffect(() => {
    getPluginList();
  }, [selectedSource, name, pageNum, pageSize]);
  const getPluginList = async () => {
    let params;
    params =
      selectedSource === 'APP'
        ? {
          name,
          pageNum: pageNum,
          pageSize,
          excludeTags: selectedSource,
        }
        : {
          name,
          pageNum: pageNum,
          pageSize,
          includeTags: selectedSource,
        };
    getPlugins(params).then(({ data, total }) => {
      setTotal(total);
      setPluginData(data);
    });
  };

  const selectPage = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
    if (pageSize !== curPageSize) {
      setPageSize(curPageSize);
    }
  };

  const filterByName = (value: string) => {
    if (value !== name) {
      setName(value);
    }
  };

  return (
    <div
      className='aui-block'
      style={{
        height: 'calc(100vh - 132px)',
        display: 'flex',
        flexDirection: 'column',
        borderRadius: '0 0 8px 8px',
      }}
    >
      <div style={{ display: 'flex', justifyContent: 'center', width: '100%' }}>
        <Input
          showCount
          maxLength={20}
          placeholder='Search'
          style={{
            marginBottom: 16,
            width: '600px',
            height: '40px',
            borderRadius: '20px',
            border: '1px solid rgb(230, 230, 230)',
          }}
          onPressEnter={(e) => filterByName(e.target.value)}
          prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
          defaultValue={name}
        />
        <Button
          style={{
            width: '98px',
            height: '40px',
            marginLeft: '8px',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            color: '#fff',
            padding: '0px 16px 0px 16px',
            borderRadius: '23px',
            background:
              'linear-gradient(135.00deg, rgb(94, 124, 224) 0%,rgb(70, 146, 255) 0%,rgb(111, 88, 254) 99.878%)',
          }}
          onClick={(e) => {
            setIsOpenPlugin(e.timeStamp);
          }}
        >
          上传
        </Button>
        <UploadToolDrawer openSignal={isOpenPlugin} refreshPluginList={getPluginList} />
      </div>
      <Tabs
        items={sourceTabs}
        activeKey={selectedSource}
        onChange={(key: string) => setSelectedSource(key)}
        style={{ width: '100%', textAlign: 'center' }}
        centered={true}
      // tabBarExtraContent={tabSearch}
      />
      {pluginData && pluginData.length > 0 ? (
        <>
          <div
            style={{
              overflowY: 'auto',
              height: '100%',
              display: 'flex',
              gap: '16px',
              flexWrap: 'wrap',
              alignContent: 'flex-start',
            }}
          >
            {pluginData.map((card: any) => (
              <PluginCard
                key={card.uniqueName}
                getPluginList={getPluginList}
                pluginData={card}
                cardType={PluginCardTypeE.MARKET}
                pluginId={card.pluginId}
              />
            ))}
          </div>
          <div style={{ paddingTop: 16 }}>
            <Pagination total={total} current={pageNum} onChange={selectPage} pageSize={pageSize} />
          </div>
        </>
      ) : (
          <div style={{ paddingTop: 100, height: '100%' }}>
            <EmptyItem />
          </div>
        )}
    </div>
  );
};

export default MarketItems;
