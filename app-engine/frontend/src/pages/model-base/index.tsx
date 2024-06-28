import { AppstoreOutlined, BarsOutlined } from '@ant-design/icons';
import { Button, message, Radio, Tabs } from 'antd';
import { TabsProps } from 'antd/lib';
import React, { useEffect, useState } from 'react';
import { modelbaseSync, modelbaseSyncStatus } from '../../shared/http/model-base';
import './index.scoped.scss';
import ModelBaseCard from './list/card-list';
import ModelBaseTable from './list/table-list';

const ModelBase = () => {
  let syncTimer = null;
  let syncTimeout = null;
  const [activeIndex, setActiveIndex] = useState('1');
  const [type, setType] = useState('card');
  const [canSync, setCanSync] = useState(false);
  const [syncButtonText, setSyncButtonText] = useState('刷新数据');

  const items: TabsProps['items'] = [
    {
      key: '1',
      label: '本地模型',
    },
  ];

  useEffect(() => {
    const mode = sessionStorage.getItem('modelBaseListType') ?? 'card';
    sessionStorage.setItem('modelBaseListType', mode);
    setType(mode);
    syncTimer = setInterval(() => getSyncStatus(true), 5000);
    if (syncTimeout) {
      clearTimeout(syncTimeout);
    }
    syncTimeout = setTimeout(() => getSyncStatus(true), 500);

    return () => {
      if (syncTimer) {
        clearInterval(syncTimer);
      }
      if (syncTimeout) {
        clearTimeout(syncTimeout);
      }
    };
  }, []);

  const changeShowType = (e: any) => {
    setType(e.target.value);
    sessionStorage.setItem('modelBaseListType', e.target.value);
  };

  //手动同步模型仓
  const syncModel = () => {
    modelbaseSync().then((res) => {
      if (syncTimer) {
        clearInterval(syncTimer);
      }
      syncTimer = setInterval(() => getSyncStatus(true), 5000);
      if (syncTimeout) {
        clearTimeout(syncTimeout);
      }
      syncTimeout = setTimeout(() => getSyncStatus(true), 500);
    });
  };

  //查询同步状态，进入模型仓时会固定查询一次，若有正在同步的任务，则不允许点击同步按钮；
  //若已同步完成或暂未有同步任务，可点击同步
  const getSyncStatus = (showMessage = false) => {
    setSyncButtonText('刷新中');
    setCanSync(false);
    modelbaseSyncStatus().then((res) => {
      switch (res.code) {
        case 200: //成功
          if (showMessage) message.success('数据刷新成功');
          setSyncButtonText('刷新数据');
          setCanSync(true);
          clearInterval(syncTimer);
          break;
        case 1: //刷新中
          setSyncButtonText('刷新中');
          setCanSync(false);
          break;
        case 2: //2失败
          if (showMessage) message.error('数据刷新失败');
          setSyncButtonText('刷新数据');
          setCanSync(true);
          clearInterval(syncTimer);
          break;
        default:
          setSyncButtonText('刷新数据');
          setCanSync(true);
          clearInterval(syncTimer);
          break;
      }
    });
  };

  return (
    <div className='aui-fullpage'>
      <div
        className='aui-header-1'
        style={{
          display: 'flex',
        }}
      >
        <div className='aui-title-1'>模型仓库</div>
      </div>
      <div className='aui-block-tab'>
        <Tabs
          defaultActiveKey={activeIndex}
          items={items}
          onChange={(key) => {
            setActiveIndex(key);
          }}
        />
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            marginBottom: '16px',
          }}
        >
          <Button
            type='primary'
            style={{
              minWidth: '96px',
              height: '32px',
              fontSize: '14px',
              borderRadius: '4px',
              letterSpacing: '0',
            }}
            loading={!canSync}
            onClick={syncModel}
          >
            {syncButtonText}
          </Button>
          <div>
            <Radio.Group value={type} onChange={changeShowType}>
              <Radio.Button value='card' style={{ padding: '3px 4px' }}>
                <AppstoreOutlined style={{ fontSize: '24px' }} />
              </Radio.Button>
              <Radio.Button value='table' style={{ padding: '3px 4px' }}>
                <BarsOutlined style={{ fontSize: '24px' }} />
              </Radio.Button>
            </Radio.Group>
          </div>
        </div>
        {type === 'card' && <ModelBaseCard />}
        {type === 'table' && <ModelBaseTable />}
      </div>
    </div>
  );
};
export default ModelBase;
