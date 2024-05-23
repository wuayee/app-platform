import React, { useEffect, useRef, useState } from 'react';
import { Button, Space, DatePicker, Progress, notification } from 'antd';
import TableHW, { getColumnSearchProps } from '../../../../components/table';
import { AppIcons } from '../../../../components/icons/app';
import EvaluationDrawer from './evaluation';
import './index.scss';

const taskStatusMap = {
  complete: (
    <div>
      <AppIcons.CompleteIcon /> 已完成
    </div>
  ),
  uncomplete: (
    <div>
      <AppIcons.PowerOffIcon /> 未完成
    </div>
  ),
};

type NotificationType = 'success' | 'info' | 'warning' | 'error';

const EvaluateTask = () => {
  const [openSignal, setOpenSignal] = useState(-1);
  const [data, setData] = useState([]);
  const [searchParams, setSearchParams] = useState({});
  const currentRow = useRef(null);
  const [api, contextHolder] = notification.useNotification();
  const openNotificationWithIcon = (type: NotificationType) => {
    api[type]({
      message: '复制成功',
    });
  };

  const refreshData = () => {
    const dataSource = new Array(100).fill(1).map((_, i) => ({
      id: i,
      question: '这是一个很长长长长长长长长长长长长长长长长长长长长的问题',
      answer: '回答',
      time: '2024-03-04 14:33:23',
      speed: '20ms',
      execute: i % 3 === 1 ? 'complete' : 'uncomplete',
      department: '部门',
      feedback: i % 3,
    }));
    setData(dataSource);
  };
  useEffect(() => {
    refreshData();
  }, [searchParams]);
  const handleChange: void = (pagination, filters, sorter) => {
    setSearchParams({ ...searchParams, pagination, filters, sorter });
    console.log('Various parameters', pagination, filters, sorter);
  };
  const columns = [
    {
      title: '应用版本',
      dataIndex: 'question',
      key: 'question',
      ellipsis: true,
      ...getColumnSearchProps('question'),
    },
    {
      title: '评估测试集',
      dataIndex: 'answer',
      key: 'answer',
      ellipsis: true,
    },
    {
      title: '评估人员',
      dataIndex: 'time',
      key: 'time',
      width: 200,
      ...getColumnSearchProps('time'),
    },
    {
      title: '时间',
      dataIndex: 'speed',
      key: 'speed',
      width:100,
      sorter: (a, b) => a.speed - b.speed,
    },
    {
      title: '是否完成',
      dataIndex: 'execute',
      key: 'execute',
      width:150,
      filters: [
        {
          text: '已完成',
          value: 'done',
        },
        {
          text: '未完成',
          value: 'unfinished',
        },
      ],
      render: (value, record) => taskStatusMap[value],
    },
    {
      title: '结果预览',
      dataIndex: 'department',
      key: 'department',
      render: (value, record) => <Progress percent={30} size='small' />,
    },
    {
      title: '操作',
      dataIndex: 'operate',
      key: 'operate',
      width:180,
      render: (value, record) => (
        <Space size='middle'>
          <a
            onClick={() => {
              currentRow.current = record;
              openNotificationWithIcon('success');
            }}
          >
            复制任务
          </a>

          <a
            onClick={(e) => {
              currentRow.current = record;
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
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 14 }}>
        <DatePicker.RangePicker
          showTime
          onChange={(_date, dateString) => {
            setSearchParams({ ...searchParams, date: dateString });
            console.log(dateString);
          }}
        />
        <Button type='primary'>导出</Button>
      </div>
      <TableHW
        dataSource={data}
        columns={columns}
        onChange={handleChange}
        scroll={{ y: 'calc(100vh - 340px)' }}
      />
      <EvaluationDrawer openSignal={openSignal} />
    </div>
  );
};

export default EvaluateTask;
