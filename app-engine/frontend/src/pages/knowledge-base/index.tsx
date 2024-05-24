import React, { useState, useEffect, ReactElement } from 'react';
import { Button, Input }from 'antd';
import { HashRouter, Route, useNavigate, Routes } from 'react-router-dom';

import Pagination from '../../components/pagination/index';
import { Icons } from '../../components/icons';
import KnowledgeCard, { knowledgeBase } from '../../components/knowledge-card';
import '../../index.scss'
import { deleteKnowledgeBase, queryKnowledgeBase } from '../../shared/http/knowledge';
const KnowledgeBase = () => {

  // 路由
  const navigate = useNavigate();

  // 总条数
  const [total, setTotal] = useState(0);

  // 分页
  const [page, setPage] = useState(1);

  // 分页数
  const [pageSize, setPageSize] = useState(10);

  // 搜索名称
  const [searchName, setSearchName] = useState('');

  // 数据
  const [knowledgeData, setKnowledgeData] = useState<knowledgeBase[]>([]);

  // 获取数据列表
  const getKnowledgeList = ()=> {
    queryKnowledgeBase({
      offset: page - 1,
      size: pageSize,
      name: searchName,
    }).then(res=> {
      if(res) {
        let data = (res?.result || []).map((item: knowledgeBase)=> {
          return ({
            ...item,
            icon: ()=> (<>
              <img src='/src/assets/images/knowledge/knowledge-base.png'/>
            </>),
            
          })
        });
        setTotal(res?.count || 0);
        setKnowledgeData(data);
      }
    })
  }

  // 删除知识库
  const deleteKnowBase = (id: string) => {
    deleteKnowledgeBase(id).then((res: any) => {
      setPage(1);
    })
  }

  // 修改知识库
  const modifyKnowledgeBase = (id: string) => {
    navigate(`/knowledge-base/create?id=${id}`)
  }

  // 点击操作
  const clickOpera = (operaType: string, id: string) => {
    if(operaType === 'delete') {
      deleteKnowBase(id)
    } else if (operaType === 'modify') {
      modifyKnowledgeBase(id)
    }
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

  // 搜索值发生变化
  const onSearchValueChange = (newSearchVal: any) => {
    if(newSearchVal !== searchName) {
      setPage(1);
      setSearchName(newSearchVal);
    }
  }

  // 创建知识库
  const createKnowledge = () => {
    navigate('/knowledge-base/create')
  }

  useEffect(()=> {
    getKnowledgeList()
  }, [page, pageSize, searchName]);
  return (
    <div className='aui-fullpage'>
    <div className='aui-header-1'>
      <div className='aui-title-1'>知识库概览</div>
    </div>
    <div className='aui-block'>
        <div className='operatorArea' style={{
          display: 'flex',
          gap: '16px'
        }}>
          <Button type="primary" style={{
            background: '#2673E5',
            width: '96px',
            height: '32px',
            fontSize: '14px',
            borderRadius: '4px',
            letterSpacing: '0',
          }} onClick={createKnowledge}>创建</Button>
          <Input 
            placeholder="搜索"  
            style={{
            width: '200px',
            borderRadius: '4px',
            border: '1px solid rgb(230, 230, 230)',
            }} 
            onChange={(e)=>onSearchValueChange(e.target.value)}
            prefix={<Icons.search color = {'rgb(230, 230, 230)'}/>}/>

        </div>
        <div className='containerArea' style={{
          width: '100%',
          minHeight: '800px',
          maxHeight: 'calc(100% - 200px)',
          boxSizing: 'border-box',
          paddingTop: '20px',
          paddingBottom: '20px',
          display:'Grid',
          justifyContent: 'space-between',
          gridGap: 17,
          gridTemplateColumns: 'repeat(auto-fill, 380px)'
        }}>
            {knowledgeData.map(knowledge=> (<>
              <KnowledgeCard key={knowledge.id} knowledge={knowledge} style={{
                flex: '0'
              }} clickMore={(e)=> clickOpera(e, knowledge.id)}/>
            </>))}

        </div>
        <Pagination total = {total} current={page} onChange={paginationChange} pageSize={pageSize}/>
    </div>
  </div>

  )
}
export default KnowledgeBase;
