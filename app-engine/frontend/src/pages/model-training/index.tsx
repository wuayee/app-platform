import { Button, Flex, Progress, Space, Table } from 'antd';
import type { PaginationProps, TableColumnsType } from 'antd';
import React, { useState } from 'react';
import TableTextSearch from '../../components/table-text-search';
import { useNavigate } from 'react-router';
import { AppIcons } from '../../components/icons/app';
import ArchiveCheckpoint from './archive';

const showTotal: PaginationProps['showTotal'] = (total) => `Total: ${total}`;

const ModelTraining = () => {

  const [isCheckpointOpen, setIsCheckpointOpen] = useState(false);
  const [archiveTaskId, setArchiveTaskId] = useState({});

  const navigate = useNavigate();

  const data = {
    data: [
      {
        taskId: '123',
        modelName: 'Qwen2-14B-Chat',
        mode: 'lora',
        strategy: 'TP4PP2',
        datasetName: '数据集123',
        startTime: '2024-6-13 10:21:23',
        percent: 0.5,
        curIter: 200,
        totalIter: 400,
        taskStatus: 'running',
        archiveStatus: 'undo'
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
  ];

  const taskStatusOptions = [
    {
      text: '成功',
      value: 'success',
    },
    {
      text: '失败',
      value: 'fail'
    },
    {
      text: '训练中',
      value: 'running'
    },
  ];

  const archiveStatusOptions = [
    {
      text: '已归档',
      value: 'archived',
    },
    {
      text: '未归档',
      value: 'undo'
    },
    {
      text: '归档中',
      value: 'archiving'
    },
  ];

  const taskStatusCell = (status: string) => {
    switch (status) {
      case 'success':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.NormalIcon />
            成功
          </Flex>
        );
      case 'fail':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.AbnormalIcon />
            失败
          </Flex>
        );
      case 'running':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.RunningIcon />
            训练中
          </Flex>
        );
      default:
        return (
          <>未知</>
        )
    }
  }

  const archiveStatusCell = (status: string) => {
    switch (status) {
      case 'archived':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.NormalIcon />
            已归档
          </Flex>
        );
      case 'undo':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.UndoIcon />
            未归档
          </Flex>
        );
      case 'archiving':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.RunningIcon />
            归档中
          </Flex>
        );
      default:
        return (
          <>未知</>
        )
    }
  }

  const archiveCallback = () => {
    setIsCheckpointOpen(false);
  }

  const columns: TableColumnsType = [
    {
      key: 'taskId',
      dataIndex: 'taskId',
      title: '任务ID',
      ellipsis: true
    },
    {
      key: 'modelName',
      dataIndex: 'modelName',
      title: '模型名称',
      ...TableTextSearch('modelName', true),
      ellipsis: true
    },
    {
      key: 'mode',
      dataIndex: 'mode',
      title: '训练类型',
      filters: typeOptions,
      render: (value) => {
        return <>
          {typeOptions.find(item => item.value === value)?.text ?? '--'}
        </>;
      },
    },
    {
      key: 'strategy',
      dataIndex: 'strategy',
      title: '训练策略',
      ...TableTextSearch('strategy', true),
    },
    {
      key: 'datasetName',
      dataIndex: 'datasetName',
      title: '数据集名称',
      ...TableTextSearch('dataSet', true),
    },
    {
      key: 'startTime',
      dataIndex: 'startTime',
      title: '启动时间',
      sorter: true,
      ellipsis: true
    },
    {
      key: 'percent',
      dataIndex: 'percent',
      title: '训练进度',
      sorter: true,
      width: 250,
      ellipsis: true,
      render: (val, record) => {
        return (
          <Flex gap={4}>
            <Progress style={{ width: 80 }} showInfo={false} percent={val * 100} size='small' />
            <span>{val * 100}%({record.curIter}/{record.totalIter})</span>
          </Flex>
        );
      },
    },
    {
      key: 'taskStatus',
      dataIndex: 'taskStatus',
      title: '训练状态',
      filters: taskStatusOptions,
      render: (val) => {
        return taskStatusCell(val);
      }
    },
    {
      key: 'archiveStatus',
      dataIndex: 'archiveStatus',
      title: '归档状态',
      filters: archiveStatusOptions,
      render: (val) => {
        return archiveStatusCell(val);
      }
    },
    {
      key: 'operate',
      dataIndex: 'operate',
      title: '操作',
      render(_, record) {
        const openArchive = () => {
          setArchiveTaskId(record?.taskId);
          setIsCheckpointOpen(true);
        }
        return (
          <Space size='small'>
            <a>查看详情</a>
            <a onClick={openArchive}>Checkpoint归档</a>
          </Space>
        )
      },
      width: 200
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
      <ArchiveCheckpoint taskId={archiveTaskId} open={isCheckpointOpen} closeCallback={archiveCallback} />
    </div>
  );
};
export default ModelTraining;
