import { AppstoreOutlined, BarsOutlined } from '@ant-design/icons';
import { Button, Radio, Tabs } from 'antd';
import { TabsProps } from 'antd/lib';
import React, { useEffect, useState } from 'react';
import './index.scoped.scss';
import ModelBaseCard from './list/card-list';
import ModelBaseTable from './list/table-list';

const ModelBase = () => {

  const [activeIndex, setActiveIndex] = useState('1');
  const [type, setType] = useState('card');

  const items: TabsProps['items'] = [
    {
      key: '1',
      label: '本地模型',
    },
    {
      key: '2',
      label: 'ModelLink',
    },
  ]

  return (
    <div className='aui-fullpage'>
      <div
        className='aui-header-1'
        style={{
          display: 'flex',
        }}
      >
        <div className='aui-title-1'>模型仓管理</div>
      </div>
      <div className='aui-block-tab'>
        <Tabs defaultActiveKey={activeIndex} items={items} onChange={(key) => { setActiveIndex(key) }} />
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            marginBottom: '16px'
          }}>
          <Button
            type='primary'
            style={{
              background: '#2673E5',
              width: '96px',
              height: '32px',
              fontSize: '14px',
              borderRadius: '4px',
              letterSpacing: '0',
            }}
          >刷新数据
          </Button>
          <div>
            <Radio.Group value={type} onChange={(e) => { setType(e.target.value) }}>
              <Radio.Button value='card' style={{ padding: '3px 4px' }}>
                <AppstoreOutlined style={{ fontSize: '24px' }} />
              </Radio.Button>
              <Radio.Button value='table' style={{ padding: '3px 4px' }}>
                <BarsOutlined style={{ fontSize: '24px' }} />
              </Radio.Button>
            </Radio.Group>
          </div>
        </div>
        <div hidden={type === 'table'}>
          <ModelBaseCard />
        </div>
        <div hidden={type === 'card'}>
          <ModelBaseTable />
        </div>
      </div>

    </div>
  );
};
export default ModelBase;
