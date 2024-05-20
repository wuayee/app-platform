import React, { useRef } from 'react';
import { SearchOutlined } from '@ant-design/icons';
import type { InputRef, TableColumnsType, TableColumnType } from 'antd';
import { Button, Input, Pagination, Space, Table } from 'antd';
import type { FilterDropdownProps } from 'antd/es/table/interface';
import './style.scoped.scss';
import LiveUiTable, { getColumnSearchProps } from '../../../components/table';
import UIPagination from '../../../components/pagination';
import LiveUiPagination from '../../../components/pagination';
import Ribbon from 'antd/es/badge/Ribbon';

const dataSource = [
  {
    id: 1,
    question: '这是一个很长长长长长长长长长长长长长长长长长长长长的问题',
    answer: '回答',
    time: '2024-03-04 14:33:23',
    speed: '20ms',
    user: '用户',
    department: '部门',
    feedback: '1',
  },
];

const FeedBack = () => {
  const searchInput1 = useRef<InputRef>(null);
  const searchInput2 = useRef<InputRef>(null);
  const handleChange: OnChange = (pagination, filters, sorter) => {
    console.log('Various parameters', pagination, filters, sorter);
    setFilteredInfo(filters);
    setSortedInfo(sorter as Sorts);
  };
  const columns = [
    {
      title: '用户提问',
      dataIndex: 'question',
      key: 'question',
      width: 300,
      ellipsis: true,
      ...getColumnSearchProps('question', searchInput1),
    },
    {
      title: '应用问答',
      dataIndex: 'answer',
      key: 'answer',
      width: 300,
      ellipsis: true,
      ...getColumnSearchProps('answer', searchInput2),
    },
    {
      title: '时间',
      dataIndex: 'time',
      key: 'time',
      width: 200,
    },
    {
      title: '相应速度',
      dataIndex: 'speed',
      key: 'speed',
      sorter: (a, b) => a.speed - b.speed,
    },
    {
      title: '用户',
      dataIndex: 'user',
      key: 'user',
    },
    {
      title: '部门',
      dataIndex: 'department',
      key: 'department',
    },
    {
      title: '用户反馈',
      dataIndex: 'feedback',
      key: 'feedback',
    },
    {
      title: '操作',
      dataIndex: 'operate',
      key: 'operate',
    },
  ];
  return (
    <div>
      <Button style={{ float: 'right', marginBottom: 10 }} type='primary'>
        导出
      </Button>
      <LiveUiTable dataSource={dataSource} columns={columns} onChange={handleChange} />
    </div>
  );
};

export default FeedBack;
