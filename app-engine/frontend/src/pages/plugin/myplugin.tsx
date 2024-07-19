import React, { useState, useEffect } from 'react';
import { Input, Button, Dropdown, Tabs, Space } from 'antd';
import type { MenuProps } from 'antd';
import Pagination from '../../components/pagination/index';
import PluginCard from '../../components/plugin-card';
import { getPluginTool } from '../../shared/http/plugin';
import { Icons } from "../../components/icons";
import { DownOutlined } from '@ant-design/icons';
import '../../index.scss';
import './style.scoped.scss';
import UploadToolDrawer from './upload/uploadTool';
import { useAppSelector } from '../../store/hook';

enum tabItemE {
  TOOL='FIT',
  TOOLFLOW='waterFlow'
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
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [name, setName] = useState<string>('');
  const [pluginData, setPluginData] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(tabItemE.TOOL);
  const [openUploadDrawer,setOpenUploadDrawer]=useState(0); // 数字，非布尔值
  const [currentTab,setCurrentTab]=useState(tabItemE.TOOL);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  
  const onTabChange=(activeKey)=>{
    setCurrentTab(activeKey);
  }
  useEffect(() => {
    getPluginList();
  }, [selectedCategory, name, pageNum, pageSize]);

  const getPluginList = () => {
    getPluginTool(tenantId,{ pageNum, pageSize, tag:selectedCategory})
      .then(({ data }) => {
        setTotal(data.total);
        setPluginData(data.toolData);
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

  const refreshPluginList = () => {
    setPageNum(1);
    setName('');
    getPluginList();
  }

  return <div className='aui-block myplugin'>
    <Tabs defaultActiveKey={tabItemE.TOOL} items={tabItems} onChange={onTabChange} />
    <div className='top-operate'>
      <div className='button-display'>
        <Button
          type='primary'
          iconPosition='end'
          onClick={(e)=>{
            if(currentTab===tabItemE.TOOL){
              setOpenUploadDrawer(e.timeStamp);
            }
          }}
        >
          创建
        </Button>
        <Input
          showCount
          maxLength={20}
          placeholder="搜索"
          onPressEnter={(e) => filterByName(e.target.value)}
          prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
          defaultValue={name}
        />
      </div>
      <div>
      <Dropdown menu={{ items:appItems }} trigger={['click']}>
        <Space className='app-select'>
          全部应用
          <DownOutlined />
        </Space>
      </Dropdown>
      </div>
      </div>
    <div className='plugin-cards'>
      {pluginData?.map((card: any) => <PluginCard key={card.uniqueName} pluginData={card}/>)}
    </div>
    <div style={{ paddingTop: 16 }}>
      <Pagination
        total={total}
        current={pageNum}
        onChange={selectPage}
        pageSize={pageSize}
      />
    </div>
    <UploadToolDrawer openSignal={openUploadDrawer} refreshPluginList={refreshPluginList} />
  </div >
}

export default MyPlugins;
