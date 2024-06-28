import React, { useState, useEffect } from 'react';
import { Input, Button, Dropdown } from 'antd';
import type { MenuProps } from 'antd';
import { DownOutlined } from '@ant-design/icons';
import Pagination from '../../components/pagination/index';
import PluginCard from '../../components/plugin-card';
import { getPluginTool, getPluginWaterFlow } from '../../shared/http/plugin';
import { Icons } from '../../components/icons';
import '../../index.scss';
import './style.scoped.scss';
import UploadToolDrawer from './upload/uploadTool';
import { useAppSelector } from '../../store/hook';
import WorkflowCard from '../../components/plugin-card/workFlowCard';

enum tabItemE {
  TOOL = 'FIT',
  TOOLFLOW = 'waterFlow',
}

const createItems: MenuProps['items'] = [
  {
    key: '1',
    label: '上传工具',
  },
  {
    key: '2',
    label: '创建工具流',
  },
];

const MyPlugins = () => {
  const [total, setTotal] = useState(0);
  const [name, setName] = useState<string>('');
  const [pluginData, setPluginData] = useState([]);
  const [openUploadDrawer, setOpenUploadDrawer] = useState(0); // 数字，非布尔值
  const [currentTab, setCurrentTab] = useState(tabItemE.TOOL);
  const [pagination, setPagination] = useState({ pageNum: 1, pageSize: 10 });
  const tenantId = useAppSelector((state) => state.appStore.tenantId);

  const onTabChange = (activeKey) => {
    setCurrentTab(activeKey);
    setPagination({ pageNum: 1, pageSize: 10 });
    getPluginList();
  };

  useEffect(() => {
    getPluginList();
  }, [name, pagination]);

  const getPluginList = () => {
    const { pageNum, pageSize } = pagination;
    if (currentTab === tabItemE.TOOL) {
      getPluginTool(tenantId, { pageNum, pageSize, tag: currentTab }).then(({ data }) => {
        setTotal(data?.total);
        setPluginData(data?.toolData);
      });
    }
    if (currentTab === tabItemE.TOOLFLOW) {
      getPluginWaterFlow(tenantId, {
        offset: (pageNum - 1) * pageSize,
        limit: pageSize,
        type: currentTab,
      }).then(({ data }) => {
        setTotal(data?.range?.total);
        setPluginData(data?.results);
      });
    }
  };

  const selectPage = (curPage: number, curPageSize: number) => {
    setPagination({ pageNum: curPage, pageSize: curPageSize });
  };

  const filterByName = (value: string) => {
    if (value) {
      setName(value);
    }
  };

  return (
    <div
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
              marginRight: 16,
            }}
          >
            创建
          </Button>
          <Input
            showCount
            maxLength={20}
            placeholder='搜索'
            onPressEnter={(e) => filterByName(e.target.value)}
            prefix={<Icons.search color='rgb(230, 230, 230)' />}
            defaultValue={name}
          />
        </Dropdown>
        <div hidden>
          <Dropdown menu={{ items: appItems }} trigger={['click']}>
            <Space className='app-select'>
              全部应用
              <DownOutlined />
            </Space>
          </Dropdown>
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
      </div>
    </div>
  );
};

export default MyPlugins;
