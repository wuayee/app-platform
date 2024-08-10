
import React, { useState, useEffect } from 'react';
import { Table, Input } from 'antd';
import { Icons } from '@/components/icons';
import './index.scss';

const DeployMent = () => {
  const [tableData, setTableData] = useState([]);
  const [name, setName] = useState(undefined);
  const paramsColumns = [
    {
      title: '插件名称',
      dataIndex: 'key',
      key: 'key',
    },
    {
      title: '详情',
      dataIndex: 'type',
      key: 'type',
    },
    {
      title: '部署状态',
      dataIndex: 'description',
      key: 'description'
    },
    {
      title: '操作',
      dataIndex: 'type',
      key: 'type',
    }
  ];
  const filterByName = (value: string) => {
    if (value !== name) {
      setName(value);
    }
  }
  return <>{(
    <div className='engine-deployment'>
      <div className='upload-info-head'>
        <img src='/src/assets/images/ai/info-upload.png' />
        <span>部署可能需要一定时长，请关注部署状态，部署成功后插件内的工具将可以被使用</span>
      </div>
      <div className='search'>
        <Input
          showCount
          maxLength={20}
          placeholder='搜索'
          className='search-input'
          style={{ width: '200px'}}
          onPressEnter={(e) => filterByName(e.target.value)}
          prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
          defaultValue={name}
        />
        <span>部署插件个数</span>
        <span className='tag installed'>10</span>
        <span>卸载插件个数</span>
        <span className='tag uninstalling'>20</span>
      </div>
      <Table dataSource={tableData} columns={paramsColumns} pagination={false} />
    </div>
  )}</>
};


export default DeployMent;
