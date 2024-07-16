import React, { useState, useEffect, useRef } from 'react';
import { Button, Input, Modal }from 'antd';
import { HashRouter, Route, useNavigate, Routes } from 'react-router-dom';
import Pagination from '../../components/pagination/index';
import { Icons } from '../../components/icons';
import KnowledgeCard, { knowledgeBase } from '../../components/knowledge-card';
import { Message } from '@/shared/utils/message';
import '../../index.scss';
import './styles/index.scoped.scss';
import { deleteKnowledgeBase, queryKnowledgeBase } from '../../shared/http/knowledge';

const KnowledgeBase = () => {
  // 路由
  const navigate = useNavigate();

  // 总条数
  const [total, setTotal] = useState(0);

  // 分页
  const [page, setPage] = useState(1);

  // 分页数
  const [pageSize, setPageSize] = useState(8);

  // 搜索名称
  const [searchName, setSearchName] = useState('');

  // 数据
  const [knowledgeData, setKnowledgeData] = useState<knowledgeBase[]>([]);
  const modalRef = useRef()

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
    modalRef.current = Modal.warning({
      title: '删除知识库',
      centered: true,
      okText: '确定',
      footer: (
        <div className='drawer-footer'>
          <Button onClick={() => modalRef.current.destroy()}>取消</Button>
          <Button type="primary" onClick={() => confirm(id)}>确定</Button>
        </div>
      ),
      content: (
        <div style={{ margin: '8px 0' }}>
          <span>删除后无法恢复，是否确定删除</span>
        </div>
      )
    })
  }
  const confirm = (id) => {
    deleteKnowledgeBase(id).then((res: any) => {
      Message({ type: 'success', content: '删除成功' });
      setPage(1);
      getKnowledgeList();
      modalRef.current.destroy()
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
          <div className='operatorArea'>
            <Button type='primary' onClick={createKnowledge}>创建</Button>
            <Input 
              className='knowledge-search'
              showCount
              style={{ width: '200px' }}
              maxLength={20}
              placeholder="搜索"
              onChange={(e)=>onSearchValueChange(e.target.value)}
              prefix={<Icons.search color = {'rgb(230, 230, 230)'}/>}/>

          </div>
          <div className='containerArea'>
              {knowledgeData.map(knowledge=> (<>
                <KnowledgeCard key={knowledge.id} knowledge={knowledge} style={{
                  flex: '0'
                }} clickMore={(e)=> clickOpera(e, knowledge.id)}/>
              </>))}

          </div>
          <Pagination 
            total = {total} 
            current={page} 
            onChange={paginationChange}
            pageSizeOptions={[8,16,32,60]}
            showQuickJumper
            pageSize={pageSize}
          />
      </div>
    </div>
  )
}
export default KnowledgeBase;
