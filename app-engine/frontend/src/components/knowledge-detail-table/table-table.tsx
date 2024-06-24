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
import { getTableColumns, getTableList, getTextList } from '../../shared/http/knowledge';
 
interface props {
 
  // 知识表类型
  type: 'text' | 'table',
 
  // 知识库id
  reposId: string,
 
  // 知识表id
  id: string,
}
 
const KnowLedgeTable = React.forwardRef(({ type, reposId, id }: props, ref) => {
  const navigate = useNavigate();
 
  // 展示的数据
  const [data, setData] = useState<any[]>([])
 
    // 总条数
  const [total, setTotal] = useState(0);
  // 分页
  const [page, setPage] = useState(1);

  // 查询参数
  const [search, setSearch] = useState('');
 
  // 分页数
  const [pageSize, setPageSize] = useState(type == 'text' ? 12 : 10);
  const [columns, setColumns] = useState<any[]>([]);

  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    if(page!==curPage) {
      setPage(curPage);
    }
    if(pageSize!=curPageSize) {
      setPageSize(curPageSize);
    }
  }

  // 获取
  const pageSizeOptions = type == 'text' ? [12] : [10, 20, 50, 100];

  // 获取文本列表
  const getText = async () => {
    try {

      const res = await getTextList({
        knowledgeId: reposId,
        tableId: id,
        pageNo: page,
        pageSize: pageSize,
        content: search,
      });
      setData(res?.result || [])
      setTotal(res?.count || 0)
    } catch (error) {
      
    }
  }

  // 获取表格列
  const getTableCol = async () => {
    try {
    if(type !== 'table') {
      return;
    }
    let res: any[] = await getTableColumns(reposId, id);

    const newCol: any[] = res.filter(item=> !item.hidden).map(item=> ({
      title: item.name,
      dataIndex: item.name,
      key: item.name,
    }));

    if(newCol.length) {
      newCol.push({
        title: '操作',
        width: 200,
        render(_, record: any, index: any) {
              const deleteFunc =async () => {
              };
       
              const modifyFunc = async ()=> {
              }
              return (
              <>
                <div>
                  <Button type="link" size="small" onClick={modifyFunc} disabled={true}>修改</Button>
                  <Button type="link" size="small" onClick={deleteFunc} disabled={true}>删除</Button>
                </div>
              </>)
        }
      });
    }

    setColumns([...newCol]);
    } catch (error) {
      
    }
  }

  // 获取表格列表
  const getTable = async () => {
    try {
      if(!columns.length) {
        await getTableCol();
      }
      const {count =0, result = []} = await getTableList({
        repositoryId: reposId,
        knowledgeTableId: id,
        pageNum: page,
        pageSize: pageSize,
      });
      // setData(res?.result || [])
      
      setTotal(count || 0);
      
      setData([...result]);
    } catch (error) {
      
    }
  }

  // 获取列表
  const refresh = async () => {
    if(type ==='text') {
      getText()
    }

    if(type === 'table' ) {
      getTable()
    }
  }
 
  // 点击更多操作
  const clickOpera = (type: string, data: any) => {
 
  }
 
  // 添加行
  const onAdd = () => {}
 
  // 搜索值变更
  const onSearchValueChange = (val: string) => {
    setPage(1)
    setSearch(val)
  }

  useEffect(()=> {
    if(id) {
      // getKnowledgeBase(id);
      refresh();
    }
  }, [page, pageSize, search]);

  // 返回子组件数据
  React.useImperativeHandle(ref, () => ({
    getColumns: () => {
      return columns || [];
    }
  }));
  
 
  return (
    <>
      <div className='filter-area' >
        <Button type="primary"  onClick={onAdd} style={{
            borderRadius: 4,
            backgroundColor: '#2673E5',
            display: 'flex',
            alignItems: 'center'
          }} disabled={true}>
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
            disabled={type ==='text'? false : true}
            onChange={(e)=>onSearchValueChange(e.target.value)}
            prefix={<Icons.search color = {'rgb(230, 230, 230)'}/>}/>
      </div>
      
      {type === 'table' && <Table columns={columns} dataSource={data} size='small' pagination={false}/>}
      {type === 'text' && (<>
        <div className='containerArea' style={{
          width: '100%',
          boxSizing: 'border-box',
          paddingTop: '20px',
          paddingBottom: '20px',
          display:'Grid',
          justifyContent: 'space-between',
          gridGap: 17,
          gridTemplateColumns: 'repeat(auto-fill, 440px)'
        }}>
            {data.map((knowledge, index)=> (<>
              <DetailCard key={index} knowledge={knowledge} currentIndex={(page - 1) * pageSize + index + 1} style = {{
                flex: '0'
              }} clickMore={(e)=> clickOpera(e, knowledge)}/>
            </>))}
 
        </div>
      </>)}
      <div style={{
        height: 20
      }}></div>
      <Pagination showTotalFunc = {false} total = {total} current={page} onChange={paginationChange} pageSizeOptions={pageSizeOptions} pageSize={pageSize}/>
    </>
  )
})
export default KnowLedgeTable;