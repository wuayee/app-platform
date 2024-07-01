import React, { useState, useEffect } from 'react';
import { Input, Button, Dropdown, Tabs, Space } from 'antd';
import type { MenuProps } from 'antd';
import { DownOutlined } from '@ant-design/icons';
import Pagination from '../../components/pagination/index';
import PluginCard from '../../components/plugin-card';
import { getPluginTool, getPluginWaterFlow } from '../../shared/http/plugin';
import { Icons } from '../../components/icons';
import UploadToolDrawer from './upload/uploadTool';
import { useAppSelector } from '../../store/hook';
import WorkflowCard from '../../components/plugin-card/workFlowCard';
import '../../index.scss';
import './style.scoped.scss';

enum tabItemE {
  TOOL = 'FIT',
  TOOLFLOW = 'waterFlow',
}

const tabItems: TabsProps['items'] = [
  {
    key: tabItemE.TOOL,
    label: '工具',
  },
  {
    key: tabItemE.TOOLFLOW,
    label: '工作流',
  },
];

const appItems: TabsProps['items'] = [
  {
    key: 'draft',
    label: '草稿',
  },
  {
    key: 'published',
    label: '已发布',
  },
  {
    key: 'avaliable',
    label: '已上架',
  },
];

const MyPlugins = () => {
  const [total, setTotal] = useState(0);
  const [name, setName] = useState<string>(undefined);
  const [pluginData, setPluginData] = useState([]);
  const [openUploadDrawer, setOpenUploadDrawer] = useState(0); // 数字，非布尔值
  const [currentTab, setCurrentTab] = useState(tabItemE.TOOL);
  const [pagination, setPagination] = useState({ pageNum: 1, pageSize: 10 });
  const [refreshSignal, setRefreshSignal] = useState(0);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);

  const onTabChange = (activeKey) => {
    setCurrentTab(activeKey);
    setPagination({ pageNum: 1, pageSize: 10 });
    setRefreshSignal(new Date().valueOf());
  };

  const getPluginList = () => {
    const { pageNum, pageSize } = pagination;
    if (currentTab === tabItemE.TOOL) {
      getPluginTool(tenantId, { pageNum, pageSize, tag: currentTab, name }).then(({ data }) => {
        setTotal(data?.total);
        setPluginData(data?.toolData);
      });
    }
    if (currentTab === tabItemE.TOOLFLOW) {
      getPluginWaterFlow(tenantId, {
        offset: (pageNum - 1) * pageSize,
        limit: pageSize,
        type: currentTab,
        name,
      }).then(({ data }) => {
        setTotal(data?.range?.total);
        setPluginData(data?.results);
      });
    }
  };
  useEffect(() => {
    getPluginList();
  }, [refreshSignal]);

  const selectPage = (curPage: number, curPageSize: number) => {
    setPagination({ pageNum: curPage, pageSize: curPageSize });
    setRefreshSignal(new Date().valueOf());
  };

  const filterByName = (e) => {
    if (e?.target?.value) {
      setName(e.target.value);
      setRefreshSignal(e.timeStamp);
    }
  };

  return (
    <div className='aui-block myplugin'>
      <Tabs defaultActiveKey={tabItemE.TOOL} items={tabItems} onChange={onTabChange} />
      <div className='top-operate'>
        <div className='button-display'>
          <Button
            type='primary'
            iconPosition='end'
            onClick={(e) => {
              if (currentTab === tabItemE.TOOL) {
                setOpenUploadDrawer(e.timeStamp);
              }
            }}
          >
            创建
          </Button>
          <Input
            disabled
            showCount
            maxLength={20}
            placeholder='搜索'
            onPressEnter={(e) => filterByName(e)}
            prefix={<Icons.search color='rgb(230, 230, 230)' />}
            defaultValue={name}
          />
        </div>
        <div>
          <div hidden>
            <Dropdown menu={{ items: appItems }} trigger={['click']}>
              <Space className='app-select'>
                全部应用
                <DownOutlined />
              </Space>
            </Dropdown>
          </div>
        </div>
      </div>
      <div className='plugin-cards'>
        {pluginData.map((card: any) =>
          currentTab === tabItemE.TOOL ? (
            <PluginCard key={card.uniqueName} pluginData={card} />
          ) : (
            <WorkflowCard key={card.uniqueName} pluginData={card} />
          )
        )}
      </div>
      <div style={{ paddingTop: 16 }}>
        <Pagination
          total={total}
          current={pagination?.pageNum}
          onChange={selectPage}
          pageSize={pagination?.pageSize}
        />
      </div>
      <UploadToolDrawer openSignal={openUploadDrawer} />
    </div>
  );
};

export default MyPlugins;
