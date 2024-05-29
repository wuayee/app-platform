import React, { useEffect, useRef, useState } from 'react';
import { Button, Space, DatePicker, Drawer, Table } from 'antd';
import './style.scoped.scss';
import { CloseOutlined } from '@ant-design/icons';
import { getColumnSearchProps } from '../../../components/table';
import { feedbackType } from './model';
import { AppIcons } from '../../../components/icons/app';
import TableTextSearch from '../../../components/table-text-search';
import { exportFeedBackData, getFeedBackData } from '../../../shared/http/apps';
import Pagination from '../../../components/pagination';
import { useParams } from 'react-router-dom';

const feedbackIcon = {
  0: <AppIcons.UnFeedbackIcon style={{ verticalAlign: 'text-bottom' }} />,
  1: <AppIcons.LikeIcon style={{ verticalAlign: 'text-bottom' }} />,
  2: <AppIcons.DisLikeIcon style={{ verticalAlign: 'text-bottom' }} />,
};

const basicInfoCols = [
  {
    key: 'createTime',
    label: '创建时间',
  },
  {
    key: 'responseTime',
    label: '响应速度',
  },
  {
    key: 'createUser',
    label: '用户',
  },
  // {
  //   key: 'department',
  //   label: '部门',
  // },
  {
    key: 'userFeedback',
    label: '用户反馈',
    render: (value) => (
      <div>
        <span>{feedbackType[value]}</span> {feedbackIcon[value]}
      </div>
    ),
  },
];

const FeedBack = () => {
  const [open, setOpen] = useState(false);
  const [data, setData] = useState<any[]>([]);
  const [searchParams,setSearchParams]=useState({});

  const { tenantId, appId} = useParams();
  const currentRow = useRef(null);
  const refreshData = () => {
    
  };
  useEffect(() => {
    refreshData();
  }, [searchParams]);

  // 搜索数据
  const buildSearchParms = (filters: any, sorter) => {
    const filterData: any = {};
    Object.keys(filters).forEach(item=> {
      filterData[item] = filters[item]?.[0] ?? ''
    });

    return filterData;
  }
  const handleChange: void = (pagination, filters, sorter) => {
    const serach = buildSearchParms(filters, sorter)
    setSearchParams({...serach, startTime:searchParams?.startTime || null, endTime: searchParams?.endTime || null});
    setPage(1);
  };
  const columns = [
    {
      title: '用户提问',
      dataIndex: 'question',
      key: 'question',
      width: 300,
      ellipsis: true,
      ...TableTextSearch('question'),
    },
    {
      title: '应用问答',
      dataIndex: 'answer',
      key: 'answer',
      width: 300,
      ellipsis: true,
      ...TableTextSearch('answer'),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 200,
    },
    {
      title: '响应速度',
      dataIndex: 'responseTime',
      key: 'responseTime',
      render: (value) => <>
        {value}ms
      </>
    },
    {
      title: '用户',
      dataIndex: 'createUser',
      key: 'createUser',
    },
    // {
    //   title: '部门',
    //   dataIndex: 'department',
    //   key: 'department',
    // },
    {
      title: '用户反馈',
      dataIndex: 'userFeedback',
      key: 'userFeedback',
      render: (value, record) => (
        <div>
          {feedbackIcon[value]} <span>{feedbackType[value]}</span>
        </div>
      ),
    },
    {
      title: '操作',
      dataIndex: 'operate',
      key: 'operate',
      render: (value, record) => (
        <a
          onClick={() => {
            currentRow.current = record;
            setOpen(true);
          }}
        >
          详情
        </a>
      ),
    },
  ];

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

  useEffect(()=> {
    getFeedBack()
  }, [page, pageSize, searchParams])

  // 查询反馈数据
  const getFeedBack = async () => {
    try {
      const res = await getFeedBackData({
        pageIndex: page,
        pageSize: pageSize,
        appId: appId,
        ...searchParams
      });
      setTotal(res?.total || 0);

      const resdata: any[] = (res?.data || []).map((item: any)=> ({
        ...item,
        id: item.id,
        question: item.question,
        answer: item.answer,
        responseTime: item.responseTime,
        createTime: item.createTime,
        createUser: item.createUser,
        userFeedback: item.userFeedback
      }));
      setData([...resdata]);
    } catch (error) {
      
    }
  }

  // 导出数据
  const exportData = async () => {
    try {
      await exportFeedBackData({
        pageIndex: page,
        pageSize: pageSize,
        appId: appId,
        ...searchParams
      });
      
    } catch (error) {
      
    }
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 14 }}>
        <DatePicker.RangePicker
          showTime
          onChange={(_date, dateString) => {
            setSearchParams({...searchParams,startTime:dateString[0], endTime: dateString[1]})
            setPage(1);
          }}
        />
        <Button type='primary' onClick={exportData}>导出</Button>
      </div>
      <Table
        dataSource={data}
        columns={columns}
        onChange={handleChange}
        virtual
        scroll={{ y: 'calc(100vh - 320px)' }}
        pagination={false}
      />
      <Pagination total = {total} current={page} onChange={paginationChange} pageSize={pageSize}/>
      <Drawer
        title='反馈详情'
        placement='right'
        size='large'
        closeIcon={false}
        onClose={false}
        open={open}
        extra={
          <CloseOutlined
            onClick={() => {
              setOpen(false);
            }}
          />
        }
        footer={
          <Button
            style={{ float: 'right', width: 90 }}
            onClick={() => {
              setOpen(false);
            }}
          >
            关闭
          </Button>
        }
      >
        <div className='drawer-title'>基本信息</div>
        <div
          style={{
            display: 'flex',
            flexWrap: 'wrap',
            whiteSpace: 'normal',
            wordBreak: 'break-all',
            marginTop: '10px',
          }}
        >
          {basicInfoCols.map((item) => (
            <div style={{ width: 'calc((100%) / 3)', marginBottom: 10 }}>
              <div style={{ color: '#4d4d4d', fontSize: 12 }}>{item.label}</div>
              <div style={{ marginTop: '5px', fontSize: '14px' }}>
                {item.render?.(currentRow.current?.[item.key]) || currentRow.current?.[item.key]}
              </div>
            </div>
          ))}
        </div>
        <div className='drawer-title'>问答详情</div>
        <div className='drawer-sub-title'>用户提问</div>
        <div className='question-card '>{currentRow.current?.question}</div>
        <div className='drawer-sub-title'>用户回答</div>
        <div className='answer-card'>
          {currentRow.current?.answer}
        </div>
        <div className='drawer-title'>用户反馈</div>
        <div className='question-card '>{currentRow.current?.userFeedbackText}</div>
      </Drawer>
    </div>
  );
};

export default FeedBack;
