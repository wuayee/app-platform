import { Button, Drawer, Input } from 'antd';
import React, { Ref, useState, useEffect } from 'react';
import type { PaginationProps, TableProps, TableColumnType } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { Table, Space } from 'antd';
import CreateSet from './createTestset/createTestSet';
import SetDetail from './detail';
import { getColumnSearchProps } from '../../../../components/table-filter/input';
import { getColumnTimePickerProps } from '../../../../components/table-filter/time-picker';
import { createAssessmentTasks, deleteDataSetData, getEvalDataList } from '../../../../shared/http/apps';
import { useParams } from 'react-router-dom';
import Pagination from '../../../../components/pagination';
import AppEvalute from './app-evalute/app-evalute';
import { getAippInfo } from '../../../../shared/http/aipp';

const showTotal: PaginationProps['showTotal'] = (total) => `共 ${total} 条`;

interface DataType {
  key: string;
  name: string;
  age: number;
  address: string;
  desc: string;
}

type DataIndex = keyof DataType;
type OnChange = NonNullable<TableProps<DataType>['onChange']>;
type Filters = Parameters<OnChange>[1];


const TestSet: React.FC = () => {
  const [open, setOpen] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);

  // 评估弹窗开关
  const [evaluteFlag, setEvaluteFlag] = useState(false);
  const [detailInfo, setDetailInfo] = useState({});
  const [filteredInfo, setFilteredInfo] = useState<Filters>({});

  const [data, setData] = useState([])
  // 总条数
  const [total, setTotal] = useState(0);

  // 分页
  const [page, setPage] = useState(1);

  // 分页数
  const [pageSize, setPageSize] = useState(10);

  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    if(page!==curPage) {
      setPage(curPage);
    }
    if(pageSize!=curPageSize) {
      setPageSize(curPageSize);
    }
  }

   
  const { tenantId, appId} = useParams()

  // 搜索值变更
  const filterChange = (...rest: any[]) => {
    rest[1]();
    setFilteredInfo({...filteredInfo, [rest[2]]: rest[0]});
    setPage(1);
  };

  // 时间转换
  const formateDate = (str: Date) => {
    if(!str) return '';
    const date = new Date(str)
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hour = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${year}-${month.toString().padStart(2)}-${day} ${hour}:${minutes}:${seconds}`
  }

  // 构建搜索参数
  const buildQuery = ()=> {
    let query: any = {
      appId,
      pageIndex: page,
      pageSize: pageSize
    };
    
    Object.keys(filteredInfo).forEach((key)=> {
      const item: any = filteredInfo[key];
      if (key==='createTime' || key === 'modifyTime') {
        query[`${key}To`] = formateDate(item[1]);
        query[`${key}From`] = formateDate(item[0]);
      } else {
        query[key] = item[0];
      }
    })
    return query;
  }

  // 获取评估测试集列表
  const refresh = async () => {
    try {
      let res = await getEvalDataList(buildQuery());

      setTotal(res?.total || 0);
      setData((res?.data || []).map((item: any)=> ({
        ...item,
        key: item.id
      })));
    } catch (error) {
      
    }
  };

  const columns = [
    {
      key: 'id',
      dataIndex: 'id',
      title: 'ID'
    },
    {
      key: 'datasetName',
      dataIndex: 'datasetName',
      title: '测试集名称',
      ...getColumnSearchProps('datasetName', filterChange)
    },
    {
      key: 'description',
      dataIndex: 'description',
      title: '测试集描述',
      filteredValue: filteredInfo.description || null,
      ...getColumnSearchProps('description', filterChange)
    },
    {
      key: 'author',
      dataIndex: 'author',
      title: '创建人',
      filteredValue: filteredInfo.author || null,
      ...getColumnSearchProps('author', filterChange)
    },
    {
      key: 'createTime',
      dataIndex: 'createTime',
      title: '创建时间',
      filteredValue: filteredInfo.createTime || null,
      ...getColumnTimePickerProps('createTime', filterChange)
    },
    {
      key: 'modifyTime',
      dataIndex: 'modifyTime',
      title: '修改时间',
      filteredValue: filteredInfo.modifyTime || null,
      ...getColumnTimePickerProps('modifyTime', filterChange)
    },
    {
      key: 'action',
      title: '操作',
      render(_: any, record: any) {
        const viewDetail = () => {
          setDetailInfo(record);
          setDetailOpen(true);
        }
        const deleteData = async ()=> {
          try {
            await deleteDataSetData(record?.id)
            refresh();
          } catch (error) {
            
          }
        }
        return (
          <Space size='middle'>
            <a onClick={viewDetail}>查看</a>
            <a onClick={deleteData}>删除</a>
          </Space>
        )
      },
    }
  ];

  const showDrawer = () => {
    setOpen(true);
  };

  const callback = (type: string, data: any) => {
    setOpen(false);
    refresh();
  }

  const detailCallback = () => {
    setDetailOpen(false);
    refresh();
  }

  const [appInfo, setAppInfo] = useState<any>({});

  // 获取应用信息
  const getAppInfo = async () => {
    try {
      const res = await getAippInfo(tenantId, appId);
      setAppInfo(res?.data || {});
    } catch (error) {
      
    }
  }

  const [selectedList, setSelectedList] = useState<any[]>([]);

  // 获取当前登录用户名
  const getLoaclUser = () => {
    return localStorage.getItem('currentUserIdComplete') ?? '';
  }

  // 评估回调参数
  const evalCallback = async (type: string, data: any) => {
    if(type === 'submit') {
      try {
        await createAssessmentTasks({
          endNodeId: 'end',
          startNodeId: 'start',
          datasetIds: selectedList,
          evalAlgorithmId: data.algorithms,
          passScore: data.scope,
          author: getLoaclUser(),
          appId: appId,
          version: appInfo.version
        });
        setEvaluteFlag(false)
      } catch (error) {
        
      }
    } else {
      setEvaluteFlag(false)
    }
  }


  const selectChange = (selectedRowKeys: React.Key[], selectedRows: DataType[], d) => {
    setSelectedList([...selectedRowKeys])
  }

  useEffect(()=> {
    getAppInfo();
  }, [])

  useEffect(()=> {
    refresh();
  }, [page, pageSize, filteredInfo]);

  return (
    <div>
      <div className='margin-bottom-standard test'>
        <Button className='margin-right-standard' type='primary' style={{ width: '88px' }} onClick={showDrawer}>创建</Button>
        <Button onClick={()=> {
          setEvaluteFlag(true);
        }} 
        disabled={selectedList.length? false: true}
        >应用评估</Button>
      </div>
      <Table
        dataSource={data}
        columns={columns}
        rowSelection={{
          type: 'checkbox',
          columnWidth: 60,
          onChange: (k, r, d) => {
            selectChange(k, r, d)
          },
        }}
        virtual
        scroll={{ y: 800 }}
        pagination={false}
      />
      <Pagination total = {total} current={page} onChange={paginationChange} pageSize={pageSize}/>
      <CreateSet visible={open} createCallback={callback} />
      {detailOpen && <SetDetail visible={detailOpen} params={detailInfo} detailCallback={detailCallback} />}
      {evaluteFlag && <AppEvalute visible={evaluteFlag}  callback={evalCallback} />}
    </div>
  )
}

export default TestSet;
