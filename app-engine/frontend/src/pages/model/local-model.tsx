import React, { useState, useEffect, ReactElement } from 'react';
import { Button, Input, message, Radio } from 'antd';

import Pagination from '../../components/pagination/index';
import { getModelList } from '../../shared/http/model';
import CardsTab, { ModelItem } from './cards-tab';
import TableTab from './table-tab';
import ModelCreate from './model-create';
import { Tabs } from 'antd';

import '../../index.scss';
import { AppstoreOutlined, BarsOutlined, ReloadOutlined } from '@ant-design/icons';

const LocalModelList = () => {
  // 总条数
  const [total, setTotal] = useState(0);

  const [type, setType] = useState('card');
  // 分页
  const [page, setPage] = useState(1);

  // 分页数
  const [pageSize, setPageSize] = useState(10);

  const [modelList, setModelList] = useState([]);

  const [openStar, setOpenStar] = useState(false);

  const [createItems, setCreateItems] = useState([]);
  const [modifyData, setModifyData] = useState<ModelItem>();

  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    if (page !== curPage) {
      setPage(curPage);
    }
    if (pageSize != curPageSize) {
      setPageSize(curPageSize);
    }
  };

  // 获取数据列表
  const queryModelList = (operationTip = false) => {
    getModelList({
      offset: page - 1,
      size: pageSize,
    }).then((res) => {
      if (res) {
        if (operationTip) {
          message.success('刷新成功');
        }
        setModelList(res.llms);
        setTotal(res.llms.length);
        res.llms.forEach((item: any) => {
          const createItem = { name: '', image: [], precision: [], gpu: [], max_token_size: {} };
          createItem.name = item.name;
          createItem.image = item.supported_images;
          createItem.precision = item.precision.supports;
          let gpuList = [];
          for (let gpu = item.gpu.min; gpu <= item.gpu.max; gpu++) {
            gpuList.push(gpu);
          }
          createItem.gpu = gpuList;
          createItem.max_token_size = item?.tokenSize;
          createItems.push(createItem);
        });
        setCreateItems(createItems);
      }
    });
  };

  const openModify = (item: ModelItem) => {
    setModifyData(item);
    setOpenStar(true);
  }

  useEffect(() => {
    queryModelList();
  }, [page, pageSize]);

  const changeShowType = (e: any) => {
    setType(e.target.value);
    sessionStorage.setItem('modelBaseListType', e.target.value);
  }

  return (
    <>
      <div
        className='operatorArea'
        style={{
          display: 'flex',
          gap: '16px',
          justifyContent: 'space-between'
        }}
      >
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
          onClick={() => {
            setOpenStar(true);
            setCreateItems(createItems);
            setModifyData(null);
          }}
        >
          创建
        </Button>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <Button style={{ marginRight: 16 }} icon={<ReloadOutlined />} onClick={() => queryModelList(true)}></Button>
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
      <div style={{ overflow: 'auto', maxHeight: 'calc(100vh - 270px)', marginBottom: '8px' }}>
        {type === 'card' && <CardsTab modelList={modelList} setModels={setModelList} openModify={openModify} />}
        {type === 'table' && (
          <TableTab modelList={modelList} setOpen={setOpenStar} setModels={setModelList} openModify={openModify} />
        )}
      </div>
      <Pagination total={total} current={page} onChange={paginationChange} pageSize={pageSize} pageSizeOptions={[10,20,30,40]}/>
      <ModelCreate
        open={openStar}
        setOpen={setOpenStar}
        createItems={createItems}
        setModels={setModelList}
        modifyData={modifyData}
      />
    </>
  );
};
export default LocalModelList;
