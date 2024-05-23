import React, { useEffect, useState } from 'react';
import { Form }from 'antd';
import { Button, Table, Input } from 'antd';
import type { TableProps } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useSearchParams } from "react-router-dom";

import Pagination from '../../components/pagination/index';

import './index.scoped.scss';
import { Icons, KnowledgeIcons } from '../icons';
import DetailCard from '../knowledge-card/detail-card';

interface props {

  // 知识表类型
  type: 'text' | 'table',

  // 知识库id
  reposId: string,

  // 知识表id
  id: string,
}

const KnowLedgeTable = ({ type, reposId, id }: props) => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  // 展示的数据
  const [data, setData] = useState<any[]>([])

    // 总条数
  const [total, setTotal] = useState(0);
  // 分页
  const [page, setPage] = useState(1);

  // 分页数
  const [pageSize, setPageSize] = useState(type == 'text' ? 12 : 10);

  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    if(page!==curPage) {
      setPage(curPage);
    }
    if(pageSize!=curPageSize) {
      setPageSize(curPageSize);
    }
  }

  const pageSizeOptions = type == 'text' ? [12] : [10, 20, 50, 100]

  // 获取列表
  const refresh = ()=> {

  }

  // 点击更多操作
  const clickOpera = (type, data) => {

  }

  // 添加行
  const onAdd = () => {}

  // 搜索值变更
  const onSearchValueChange = (val: string) => {

  }

  const columns: TableProps<any>['columns'] = [
    {
      title: 'key',
      dataIndex: 'key',
      key: 'key',
    },
    {
      title: 'val',
      dataIndex: 'val',
      key: 'val',
    },
    {
      title: '详细信息',
      dataIndex: 'desc',
      key: 'desc',
    },
    {
      title: '操作',
      dataIndex: 'operator',
      key: 'operator',
      width: 200,
      render(_, record, index) {
        const deleteFunc =async () => {
        };

        const modifyFunc = async ()=> {
        }
        return (
        <>
          <div>
            <Button type="link" size="small" onClick={modifyFunc}>修改</Button>
            <Button type="link" size="small" onClick={deleteFunc}>删除</Button>
          </div>
        </>)
      },
    },
  ]

  useEffect(()=> {
    if(id) {
    }
  }, []);

  useEffect(()=> {
    if(id) {
      // getKnowledgeBase(id);
      refresh();
    }
  }, [page, pageSize]);
  

  return (
    <>
      <div className='filter-area' >
        <Button type="primary"  onClick={onAdd} style={{
            borderRadius: 4,
            backgroundColor: '#2673E5',
            display: 'flex',
            alignItems: 'center'
          }}>
            {<KnowledgeIcons.add/>} 添加行
        </Button>
        <Input 
            placeholder="搜索"  
            style={{
            width: 368,
            height: 32,
            borderRadius: '4px',
            border: '1px solid rgb(230, 230, 230)',
            }} 
            onChange={(e)=>onSearchValueChange(e.target.value)}
            prefix={<Icons.search color = {'rgb(230, 230, 230)'}/>}/>
      </div>
      
      {type === 'table' && <Table columns={columns} dataSource={data} size='small' pagination={false}/>}
      {type === 'text' && (<>
        <div className='containerArea' style={{
          width: '100%',
          minHeight: '800px',
          maxHeight: 'calc(100% - 200px)',
          boxSizing: 'border-box',
          paddingTop: '20px',
          paddingBottom: '20px',
          display:'flex',
          gap: '17px',
          flexWrap: 'wrap'
        }}>
            {data.map(knowledge=> (<>
              <DetailCard key={knowledge.id} knowledge={knowledge} style = {{
                flex: '0'
              }} clickMore={(e)=> clickOpera(e, knowledge.id)}/>
            </>))}

        </div>
      </>)}
      <Pagination total = {total} current={page} onChange={paginationChange} pageSizeOptions={pageSizeOptions} pageSize={pageSize}/>
    </>
  )
}
export default KnowLedgeTable;