import React, { useEffect, useRef, useState } from 'react';
import { Button, Space, Progress, notification, Table } from 'antd';
import { AppIcons } from '../../../../components/icons/app';
import EvaluationDrawer from './evaluation';
import './index.scss';
import TableTextSearch from '../../../../components/table-text-search';
import { copyEvalTask, getEvalReport, getEvalTaskList } from '../../../../shared/http/apps';
import { TaskStatusE, traceColumns, type evalTaskI, type getEvalTaskListParamsI } from './model';
import TableCalendarSearch from '../../../../components/table-calendar-search';

const taskStatusMap = {
  [TaskStatusE.FINISH]: (
    <div>
      <AppIcons.CompleteIcon /> 已完成
    </div>
  ),
  [TaskStatusE.NOT_START]: (
    <div>
      <AppIcons.PowerOffIcon /> 未开始
    </div>
  ),
  [TaskStatusE.FAILURE]: (
    <div>
      <AppIcons.PowerOffIcon />  失败
    </div>
  ),
  [TaskStatusE.IN_PROGRESS]: (
    <div>
      <AppIcons.PowerOffIcon />  执行中
    </div>
  ),
};

type NotificationType = 'success' | 'info' | 'warning' | 'error';

const EvaluateTask = () => {
  const [openSignal, setOpenSignal] = useState(-1);
  const [data, setData] = useState<Array<evalTaskI>>([]);
  const [total, setTotal] = useState(0);
  const [searchParams, setSearchParams] = useState<getEvalTaskListParamsI>({
    pageIndex: 1,
    appId: '84b3dfd4d0814618a46680f3b38ac2fb',
    pageSize: 10,
  });
  const currentRow = useRef(null);
  const [api, contextHolder] = notification.useNotification();
  const openNotificationWithIcon = (type: NotificationType) => {
    api[type]({
      message: '复制成功',
    });
  };

  const onCopyTask = async (record) => {
    await copyEvalTask(record?.id, record?.author);
    openNotificationWithIcon('success');
    refreshData();
  };

  const refreshData = async () => {
    const dataSource = await getEvalTaskList(searchParams);
    setTotal(dataSource?.total);
    setData(dataSource?.data);
  };
  useEffect(() => {
    refreshData();
  }, [searchParams]);

  const handleChange: void = (pagination, filters, sorter) => {
    const paramBody = {
      pageIndex: pagination.current,
      createTimeTo: filters?.createTime?.[1] !== '' ? filters?.createTime?.[1] : undefined,
      author: filters?.author?.[0],
      createTimeFrom: filters?.createTime?.[0] !== '' ? filters?.createTime?.[0] : undefined,
      appId: searchParams.appId,
      pageSize: pagination.pageSize,
      version: filters?.version?.[0],
      statusList:filters?.status,
    };

    setSearchParams(paramBody);
  };
  const columns = [
    {
      title: '应用版本',
      dataIndex: 'version',
      key: 'version',
      ellipsis: true,
      ...TableTextSearch('version'),
    },
    {
      title: '评估测试集',
      dataIndex: 'datasets',
      key: 'datasets',
      ellipsis: true,
      render: (value: [], record) => {
        const str = value?.map((item: string) => item.slice(0, item.indexOf('('))).join(',');
        return str;
      },
    },
    {
      title: '评估人员',
      dataIndex: 'author',
      key: 'author',
      ...TableTextSearch('author'),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      ...TableCalendarSearch('createTime'),
    },
    {
      title: '是否完成',
      dataIndex: 'status',
      key: 'status',
      filters: [
        {
          text: '已完成',
          value: TaskStatusE.FINISH,
        },
        {
          text: '执行中',
          value: TaskStatusE.IN_PROGRESS,
        },
        {
          text: '失败',
          value: TaskStatusE.FAILURE,
        },
        {
          text: '未开始',
          value: TaskStatusE.NOT_START,
        },
        
      ],
      render: (value, record) => taskStatusMap[value],
    },
    {
      title: '任务进度',
      dataIndex: 'passRate',
      key: 'passRate',
      render: (value, record) => <Progress percent={value} size='small' />,
    },
    {
      title: '操作',
      dataIndex: 'operate',
      key: 'operate',
      render: (value, record) => (
        <Space size='middle'>
          <a
            onClick={() => {
              onCopyTask(record);
            }}
          >
            复制任务
          </a>

          <a
            onClick={(e) => {
              currentRow.current=record;
              setOpenSignal(e.timeStamp);
            }}
          >
            查看报告
          </a>
        </Space>
      ),
    },
  ];

  return (
    <div>
      {contextHolder}
      <Table
        dataSource={data}
        columns={columns}
        onChange={handleChange}
        virtual
        scroll={{ y: 'calc(100vh - 340px)' }}
        pagination={{
          total,
          position: ['bottomRight'],
          size: 'small',
          showQuickJumper: true,
          defaultCurrent: 1,
          showSizeChanger: true,
          showTotal: () => <div>共{total}条</div>,
          onChange: (pageNo, pageSize) => {},
        }}
      />
      <EvaluationDrawer openSignal={openSignal} taskRecord={currentRow.current}/>
    </div>
  );
};

export default EvaluateTask;
