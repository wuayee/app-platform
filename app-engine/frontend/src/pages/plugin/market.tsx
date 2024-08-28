import React, { useState, useEffect } from 'react';
import { Tabs, Input, Tag, Button, Spin } from 'antd';
import Pagination from '@/components/pagination/index';
import PluginCard from '@/components/plugin-card';
import WorkflowCard from '@/components/plugin-card/workFlowCard';
import { getPlugins, getPluginWaterFlow } from '@/shared/http/plugin';
import { Icons } from '@/components/icons';
import EmptyItem from '@/components/empty/empty-item';
import { PluginCardTypeE, sourceTabs } from './helper';
import { debounce } from '@/shared/utils/common';
import UploadToolDrawer from './upload/uploadTool';
import { useTranslation } from 'react-i18next';
import { useAppSelector } from '@/store/hook';
import './styles/market.scss';

const MarketItems = ({ reload }) => {
  const { t } = useTranslation();
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(8);
  const [name, setName] = useState('');
  const [selectedSource, setSelectedSource] = useState(sourceTabs?.[0]?.key);
  const [pluginData, setPluginData] = useState([]);
  const [isOpenPlugin, setIsOpenPlugin] = useState(0);
  const [loading, setLoading] = useState(false);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);

  useEffect(() => {
    if (selectedSource === 'MYWATERFLOW') {
      getWaterFlowList();
      return
    }
    getPluginList();
  }, [selectedSource, name, pageNum, pageSize, reload]);

  // 获取插件列表
  const getPluginList = () => {
    let params: any = {
      name,
      pageNum: pageNum,
      pageSize
    }
    if (selectedSource === 'APP') {
      params.excludeTags = selectedSource;
    } else {
      params.includeTags = selectedSource;
    }
    setLoading(true);
    getPlugins(params).then(({ data, total }) => {
      setTotal(total);
      setPluginData(data || []);
      setLoading(false);
    }).catch(() => {
      setLoading(false);
    });
  };
  // 获取工具列表
  const getWaterFlowList = () => {
    getPluginWaterFlow(tenantId, {
      offset: (pageNum - 1) * pageSize,
      limit: pageSize,
      type: 'waterFlow',
      name,
    }).then(({ data }) => {
      const { results, range } = data;
      const list = results || [];
      list.forEach(item => {
        item.mapType = 'waterFlow';
      })
      setTotal(range.total || 0);
      setPluginData(list);
    }).catch(() => {
      setTotal(0);
      setPluginData([]);
    });
  }
  // 分页
  const selectPage = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
    if (pageSize !== curPageSize) {
      setPageSize(curPageSize);
    }
  };
  // 名称搜索
  const filterByName = (value: string) => {
    if (value !== name) {
      setName(value);
    }
  };
  const handleSearch = debounce(filterByName, 1000);

  return (
    <div className='aui-block market-block'>
      <div className='market-search'>
        <Input
          showCount
          maxLength={20}
          placeholder='Search'
          className='market-input'
          onChange={(e) => handleSearch(e.target.value)}
          prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
          defaultValue={name}
        />
        <Button className='market-button'
          onClick={(e) => {
            setIsOpenPlugin(e.timeStamp);
          }}
        >
          {t('upload')}
        </Button>
        <UploadToolDrawer openSignal={isOpenPlugin} refreshPluginList={getPluginList} />
      </div>
      <Tabs
        items={sourceTabs}
        activeKey={selectedSource}
        onChange={(key: string) => setSelectedSource(key)}
        style={{ width: '100%', textAlign: 'center' }}
        centered={true}
      />
      <Spin spinning={loading}>
        {pluginData && pluginData.length > 0 ?
          <>
            <div className='market-card' >
              {pluginData.map((card: any) => (
                card.mapType === 'waterFlow' ?
                  <WorkflowCard key={card.uniqueName} pluginData={card} type='plugin' /> :
                  <PluginCard
                    key={card.pluginId}
                    getPluginList={getPluginList}
                    pluginData={card}
                    cardType={PluginCardTypeE.MARKET}
                    pluginId={card.pluginId}
                  />
              ))}
            </div>
            <div className='market-page'>
              <Pagination
                total={total}
                current={pageNum}
                onChange={selectPage}
                pageSize={pageSize} />
            </div>
          </>
          :
          <div className='market-empty'>
            <EmptyItem />
          </div>
        }
      </Spin>
    </div>
  );
};
export default MarketItems;
