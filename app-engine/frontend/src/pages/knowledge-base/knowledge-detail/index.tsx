import React, { useEffect, useState } from 'react';
import { Form }from 'antd';
import { Button, Table } from 'antd';
import type { TableProps } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useSearchParams } from "react-router-dom";

import BreadcrumbSelf from '../../../components/breadcrumb';
import { KnowledgeIcons } from '../../../components/icons';
import { getKnowledgeBaseById, getKnowledgeDetailById } from '../../../shared/http/knowledge';
import { columns } from './table-config';
import Pagination from '../../../components/pagination/index';

const KnowledgeBaseDetail = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const id = searchParams.get("id");

  const [knowledgeDetail, setKnowledgeDetail] = useState<any>(null)
  const [data, setData] = useState<any>([]);

    // 总条数
  const [total, setTotal] = useState(0);
  // 分页
  const [page, setPage] = useState(1);

  // 分页数
  const [pageSize, setPageSize] = useState(10);

  // 获取知识库详情
  const getKnowledgeBase = async (id: string) => {
    try {
      const res = await getKnowledgeBaseById(id);
      setKnowledgeDetail({...res});
    } catch (error) {
      
    }
  }

  // 格式化时间
  const formateTime = (dateStr: Date)=> {
    if(!dateStr) return ''
    const date = new Date(dateStr);
    const y = date.getFullYear();
    const m = date.getMonth() + 1;
    const d = date.getDate();
    return `${y}.${m}.${d}`;
  };

  // 返回知识库
  const goBack = ()=> {
    navigate('/knowledge-base')
  }

  // 新增
  const onAdd = () => {
    navigate(`/knowledge-base/knowledge-detail/create-table?id=${id}`)
  }

  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    if(page!==curPage) {
      setPage(curPage);
    }
    if(pageSize!=curPageSize) {
      setPageSize(curPageSize);
    }
  }

  // 获取列表
  const refresh = ()=> {
    if(id) {
      getKnowledgeDetailById(id, {
        pageNum: page - 1,
        pageSize: pageSize
      }).then(res=> {
        if(res?.result) {
          setData(res?.result ?? []);
          setTotal(res?.count ?? 0);
        }
      })
    }

  }

  useEffect(()=> {
    if(id) {
      getKnowledgeBase(id);
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
    <div className='aui-fullpage'>
    <div className='aui-header-1'>
      <div className='aui-title-1'>
        <BreadcrumbSelf></BreadcrumbSelf>
      </div>
    </div>
      <div className='aui-block' style={{
        display: 'flex',
        flexDirection: 'column',
        gap: 8
      }}>
        <div className='knowledge-detail-header' style={{
          display: 'flex', 
          gap: 30,
          alignItems: 'center',
          paddingBottom: 30,
          borderBottom: '1px solid #CDD7E6'
        }}>
            <div className='detail-header-return' style={{
              cursor: 'pointer'
            }} onClick={goBack}>
              {<KnowledgeIcons.leftArrow/>}
            </div>
            <div className='detail-header-info' style={{
                flex: 1,

              }}>
              <div className='detail-info-title' style={{
                fontSize: 20,
                height: 30,
                color: 'rgba(5, 5, 5, .96)'
              }}>{knowledgeDetail?.name || ''}</div>
              <div className='detail-info-userinfo' style={{
                fontSize: 14,
                color: 'rgba(105, 105, 105, .96)'
              }}>
                {`${knowledgeDetail?.ownerName ?? ''}创建于${formateTime(knowledgeDetail?.createAt as any as Date)}` }
              </div>
            </div>
            <div className='detail-header-add'>
              <Button type="primary"  onClick={onAdd} style={{
                borderRadius: 4,
                backgroundColor: '#2673E5',
                display: 'flex',
                alignItems: 'center'
              }}>{<KnowledgeIcons.add/>} 添加</Button>
            </div>
        </div>
        <div className='knowledge-detail-table' >
          <Table columns={columns} dataSource={data} size='small' pagination={false}/>
          <Pagination total = {total} current={page} onChange={paginationChange} pageSize={pageSize}/>
        </div>
      <div />
    </div>
    </div>

    </>
  )
}
export default KnowledgeBaseDetail;