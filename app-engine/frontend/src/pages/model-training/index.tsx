import { Button, Flex, Progress, Table } from 'antd';
import type { PaginationProps, TableColumnsType } from 'antd';
import React from 'react';
import TableTextSearch from '../../components/table-text-search';
import { useNavigate } from 'react-router';

const showTotal: PaginationProps['showTotal'] = (total) => `Total: ${total}`;

const ModelTraining = () => {

  const navigate = useNavigate();

  const data = {
    data: [
      {
        id: '123',
        name: 'Qwen2-14B-Chat',
        type: 'lora',
        policy: 'TP4PP2',
        dataSet: '数据集123',
        startTime: '2024-6-13 10:21:23',
        progress: 0.5,
        curIters: 200,
        totalIters: 400
      },
    ],
    total: 1
  };

  const typeOptions = [
    {
      text: '全参训练',
      value: 'all',
    },
    {
      text: 'LoRA微调',
      value: 'lora'
    }
  ]

  const columns: TableColumnsType = [
    {
      key: 'id',
      dataIndex: 'id',
      title: '任务ID',
    },
    {
      key: 'name',
      dataIndex: 'name',
      title: '模型名称',
      ...TableTextSearch('name', true),
    },
    {
      key: 'type',
      dataIndex: 'type',
      title: '训练类型',
      filters: typeOptions,
      render: (value) => {
        return <>
          {typeOptions.find(item => item.value === value)?.text ?? '--'}
        </>;
      },
    },
    {
      key: 'policy',
      dataIndex: 'policy',
      title: '训练策略',
      ...TableTextSearch('policy', true),
    },
    {
      key: 'dataSet',
      dataIndex: 'dataSet',
      title: '数据集名称',
      ...TableTextSearch('dataSet', true),
    },
    {
      key: 'startTime',
      dataIndex: 'startTime',
      title: '启动时间',
      sorter: true
    },
    {
      key: 'progress',
      dataIndex: 'progress',
      title: '训练进度',
      sorter: true,
      width: 250,
      ellipsis: true,
      render: (val, record) => {
        return (
          <Flex gap={4}>
            <Progress style={{ width: 80 }} showInfo={false} percent={val * 100} size='small' />
            <span>{val * 100}%({record.curIters}/{record.totalIters})</span>
          </Flex>
        );
      },
    },
    {
      key: 'operate',
      dataIndex: 'operate',
      title: '操作',
    },
  ];

  return (
    <div className='aui-fullpage'>
      <div
        className='aui-header-1'
        style={{
          display: 'flex',
          gap: '1000px',
        }}
      >
        <div className='aui-title-1'>模型训练</div>
      </div>
      <div className='aui-block'>
        <div className='content-header'>
          <Button
            type='primary'
            style={{
              background: '#2673E5',
              minWidth: '96px',
              height: '32px',
              fontSize: '14px',
              borderRadius: '4px',
              letterSpacing: '0',
              margin: '0 0 16px'
            }}
            onClick={() => { navigate('/model-training/create') }}>
            创建任务
          </Button>
        </div>
        <Table
          dataSource={data?.data}
          columns={columns}
          scroll={{ y: '800px' }}
          pagination={{
            size: 'small',
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: showTotal,
          }}
        />
      </div>
    </div>
  );
};
export default ModelTraining;
