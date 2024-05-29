import React, { useState, useEffect, ReactElement } from 'react';
import { Button, Input } from 'antd';

import Pagination from '../../components/pagination/index';
import { getModelList } from '../../shared/http/model';
import CardsTab from './cards-tab';
import TableTab from './table-tab';
import ModelCreate from './model-create';

import '../../index.scss';

const ModelList = () => {
  // 总条数
  const [total, setTotal] = useState(0);

  const [modelTab, setModelTab] = useState(1);

  // 分页
  const [page, setPage] = useState(1);

  // 分页数
  const [pageSize, setPageSize] = useState(10);

  const [modelList, setModelList] = useState([]);

  const [openStar, setOpenStar] = useState(false);

  const [createItems, setCreateItems] = useState([]);

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
  const queryModelList = () => {
    getModelList({
      offset: page - 1,
      size: pageSize,
    }).then((res) => {
      if (res) {
        setModelList(res.llms);
        setTotal(res.llms.length);
        res.llms.forEach((item: any) => {
          const createItem = { name: '', image: [], precision: [], gpu: [] };
          createItem.name = item.name;
          createItem.image = item.supported_images;
          createItem.precision = item.precision.supports;
          let gpuList = [];
          for (let gpu = item.gpu.min; gpu <= item.gpu.max; gpu++) {
            gpuList.push(gpu);
          }
          createItem.gpu = gpuList;
          createItems.push(createItem);
        });
        setCreateItems(createItems);
      }
    });
  };

  useEffect(() => {
    queryModelList();
  }, [page, pageSize]);

  return (
    <div className='aui-fullpage'>
      <div
        className='aui-header-1'
        style={{
          display: 'flex',
          gap: '1000px',
        }}
      >
        <div className='aui-title-1'>模型服务</div>
        <div
          className='aui-block'
          style={{
            background: 'transparent',
            textAlign: 'right',
          }}
        >
          {modelTab === 1 && (
            <img src='/src/assets/images/model/card-active.svg' onClick={() => setModelTab(1)} />
          )}
          {modelTab === 2 && (
            <img
              src='/src/assets/images/model/card.svg'
              onClick={() => setModelTab(1)}
              style={{
                cursor: 'pointer',
              }}
            />
          )}
          {modelTab === 1 && (
            <img
              src='/src/assets/images/model/table.svg'
              onClick={() => setModelTab(2)}
              style={{
                cursor: 'pointer',
              }}
            />
          )}
          {modelTab === 2 && (
            <img src='/src/assets/images/model/table-active.svg' onClick={() => setModelTab(2)} />
          )}
        </div>
      </div>
      <div className='aui-block'>
        <div
          className='operatorArea'
          style={{
            display: 'flex',
            gap: '16px',
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
            }}
          >
            创建
          </Button>
        </div>
        <div
          style={{
            marginLeft: -20,
          }}
        >
          {modelTab === 1 && <CardsTab modelList={modelList} setModels={setModelList} />}
          {modelTab === 2 && (
            <TableTab modelList={modelList} setOpen={setOpenStar} setModels={setModelList} />
          )}
        </div>
        <Pagination total={total} current={page} onChange={paginationChange} pageSize={pageSize} />
      </div>
      <ModelCreate
        open={openStar}
        setOpen={setOpenStar}
        createItems={createItems}
        setModels={setModelList}
      />
    </div>
  );
};
export default ModelList;
