import { Button, Flex, Progress, Space, Table } from 'antd';
import type { PaginationProps, TableColumnsType } from 'antd';
import React, { useEffect, useState } from 'react';
import TableTextSearch from '../../components/table-text-search';
import { useNavigate } from 'react-router';
import { AppIcons } from '../../components/icons/app';
import ArchiveCheckpoint from './archive';
import { queryModelTaskList } from '../../shared/http/model-train';

const showTotal: PaginationProps['showTotal'] = (num) => `Total: ${num}`;

const ModelTraining = () => {
  const [isCheckpointOpen, setIsCheckpointOpen] = useState(false);
  const [archiveTaskId, setArchiveTaskId] = useState('');
  const [data, setData] = useState({ data: [], total: 0 });
  //单独设置一个，用于归档后回调时刷新页面时使用
  const [queryBody, setQueryBody] = useState({ page: 0, limit: 10 });

  const navigate = useNavigate();

  useEffect(() => {
    getModelTaskList(queryBody);
  }, []);

  const getModelTaskList = (queryBody: any) => {
    queryModelTaskList(queryBody).then((res) => {
      setData({
        data: res?.result,
        total: res?.count,
      });
    });
  };

  const typeOptions = [
    {
      text: '全参训练',
      value: 'all',
    },
    {
      text: 'LoRA微调',
      value: 'lora',
    },
  ];

  const taskStatusOptions = [
    {
      text: '成功',
      value: 'FINISHED',
    },
    {
      text: '失败',
      value: 'FAILED',
    },
    {
      text: '训练中',
      value: 'PROCESSING',
    },
  ];

  const archiveStatusOptions = [
    {
      text: '已归档',
      value: 'ARCHIVED',
    },
    {
      text: '未归档',
      value: 'NOT_ARCHIVED',
    },
    {
      text: '归档中',
      value: 'ARCHIVING',
    },
    {
      text: '归档失败',
      value: 'FAILED',
    },
  ];

  const taskStatusCell = (status: string) => {
    switch (status.toUpperCase()) {
      case 'FINISHED':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.NormalIcon />
            成功
          </Flex>
        );
      case 'FAILED':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.AbnormalIcon />
            失败
          </Flex>
        );
      case 'PROCESSING':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.RunningIcon />
            训练中
          </Flex>
        );
      default:
        return <>未知</>;
    }
  };

  const archiveStatusCell = (status: string) => {
    switch (status.toUpperCase()) {
      case 'ARCHIVED':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.NormalIcon />
            已归档
          </Flex>
        );
      case 'NOT_ARCHIVED':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.UndoIcon />
            未归档
          </Flex>
        );
      case 'ARCHIVING':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.RunningIcon />
            归档中
          </Flex>
        );
      case 'FAILED':
        return (
          <Flex gap={4} align='center'>
            <AppIcons.AbnormalIcon />
            归档失败
          </Flex>
        );
      default:
        return <>未知</>;
    }
  };

  const archiveCallback = (refresh = false) => {
    if (refresh) {
      getModelTaskList(queryBody);
    }
    setIsCheckpointOpen(false);
  };

  const columns: TableColumnsType = [
    {
      key: 'taskId',
      dataIndex: 'taskId',
      title: '任务ID',
      ellipsis: true,
    },
    {
      key: 'modelName',
      dataIndex: 'modelName',
      title: '模型名称',
      ...TableTextSearch('modelName', true),
      ellipsis: true,
    },
    {
      key: 'modeType',
      dataIndex: 'modeType',
      title: '训练类型',
      filters: typeOptions,
      render: (value) => {
        return <>{typeOptions.find((item) => item.value === value)?.text ?? '--'}</>;
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
      ellipsis: true,
    },
    {
      key: 'percent',
      dataIndex: 'percent',
      title: '训练进度',
      sorter: true,
      width: 250,
      ellipsis: true,
      render: (val, record) => {
        if (val === -1) {
          return '--';
        }
        return (
          <Flex gap={4}>
            <Progress style={{ width: 80 }} showInfo={false} percent={val} size='small' />
            <span>
              {val}%({record?.curIter}/{record?.totalIter})
            </span>
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
      },
    },
    {
      key: 'archiveStatus',
      dataIndex: 'archiveStatus',
      title: '归档状态',
      filters: archiveStatusOptions,
      render: (val) => {
        return archiveStatusCell(val);
      },
    },
    {
      key: 'operate',
      dataIndex: 'operate',
      title: '操作',
      render(_, record) {
        const openArchive = () => {
          setArchiveTaskId(record?.taskId);
          setIsCheckpointOpen(true);
        };
        return (
          <Space size='small'>
            <a
              hidden={record?.tensorboardUrl === ''}
              onClick={() => {
                window.open(record?.tensorboardUrl);
              }}
            >
              查看详情
            </a>
            <a hidden={record?.taskStatus !== 'FINISHED'} onClick={openArchive}>
              Checkpoint归档
            </a>
          </Space>
        );
      },
      width: 200,
    },
  ];

  //筛选和排序项变更时的回调方法，触发数据调用方法
  const fetchData = (pagination, filters, sorter) => {
    console.log(filters);
    let params: any = {
      page: pagination?.current - 1,
      limit: pagination?.pageSize,
    };
    if (filters?.modelName && filters.modelName.length > 0) {
      params.modelName = filters.modelName[0];
    }
    if (filters?.modelType) {
      params.modelType = filters.modelType;
    }
    if (filters?.datasetName && filters.datasetName.length > 0) {
      params.datasetName = filters.datasetName[0];
    }
    if (filters?.strategy && filters.strategy.length > 0) {
      params.strategy = filters.strategy[0];
    }
    if (filters?.archiveStatus) {
      params.archiveStatus = filters.archiveStatus;
    }
    if (filters?.taskStatus) {
      params.taskStatus = filters.taskStatus;
    }
    if (sorter?.order) {
      params.sort = [sorter?.field];
      params.direction = [sorter?.order.slice(0, -3)];
    }
    setQueryBody(params);
    getModelTaskList(params);
  };

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
              margin: '0 0 16px',
            }}
            onClick={() => {
              navigate('/model-training/create');
            }}
          >
            创建任务
          </Button>
        </div>
        <Table
          dataSource={data?.data}
          columns={columns}
          scroll={{ y: '800px' }}
          pagination={{
            total: data?.total,
            size: 'small',
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: showTotal,
          }}
          onChange={fetchData}
        />
      </div>
      <ArchiveCheckpoint
        taskId={archiveTaskId}
        open={isCheckpointOpen}
        closeCallback={archiveCallback}
      />
    </div>
  );
};
export default ModelTraining;
