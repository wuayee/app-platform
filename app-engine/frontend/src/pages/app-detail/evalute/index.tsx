import { Button, Tabs } from 'antd';
import React, { useState } from 'react';
import './style.scss';

const Evaluate: React.FC = () => {

  const dataSource = [
    {
      key: '1',
      name: '胡彦斌',
      age: 32,
      address: '西湖区湖底公园1号',
    },
    {
      key: '2',
      name: '胡彦祖',
      age: 42,
      address: '西湖区湖底公园1号',
    },
  ];

  const columns = [
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '年龄',
      dataIndex: 'age',
      key: 'age',
    },
    {
      title: '住址',
      dataIndex: 'address',
      key: 'address',
    },
  ];



  return (
    <div>
      <Button className='margin-right-standard' type='primary'>创建</Button>
      <Button>应用评估</Button>
    </div>
  )
}


export default Evaluate;
