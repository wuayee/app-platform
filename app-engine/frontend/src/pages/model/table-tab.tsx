import React, { useEffect, useState } from 'react';
import { Button, Table, message, Space } from 'antd';
import type { TableProps } from 'antd';
import { useNavigate } from 'react-router-dom';

import { ModelItem } from './cards-tab';
import { deleteModelByName, getModelList } from '../../shared/http/model';

interface TableTabProps {
  modelList: Array<ModelItem>;
  setOpen: (val: boolean) => void;
  setModels: (val: Array<any>) => void;
  openModify: Function,
}

const TableTab: React.FC<TableTabProps> = ({ modelList, setOpen, setModels,openModify }) => {
  const navigate = useNavigate();
  const toModelDetail = (id: string) => {
    navigate('/model/detail', { state: { modelId: id } });
  };
  const typeFilters: any[] = [];
  const typeList: any[] = [];

  modelList.forEach((item) => {
    if (!typeList.includes(item.type)) {
      typeList.push(item.type);
      const typeItem = { value: '', text: '' };
      typeItem.value = item.type;
      typeItem.text = item.type;
      typeFilters.push(typeItem);
    }
  });

  const deleteModel = (name: string) => {
    const deleteParams = {
      data: {
        name,
      },
    };
    deleteModelByName(deleteParams).then((res) => {
      if (res && res.code === 200) {
        message.success('模型删除成功');
        getModelList().then((res) => {
          if (res) {
            setModels(res.llms);
          }
        });
      } else {
        message.error('模型删除失败');
      }
    });
  };

  const modifyModel = (record: any) => {
    openModify(record);
  }

  const columns: TableProps<ModelItem>['columns'] = [
    {
      title: '模型',
      dataIndex: 'name',
      key: 'name',
      render: (value, record) => (
        <a
          onClick={() => {
            toModelDetail(record.id);
          }}
        >
          {record.name}
        </a>
      ),
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: 500,
      sorter: (a: any, b: any) => a.description.length - b.description.length,
    },
    {
      title: '机构',
      dataIndex: 'orgnization',
      key: 'orgnization',
      sorter: (a: any, b: any) => a.orgnization.length - b.orgnization.length,
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      sorter: (a: any, b: any) => a.type.length - b.type.length,
      filters: typeFilters,
      onFilter: (value, record) => record.type.indexOf(value as string) === 0,
    },
    {
      title: '健康状态',
      dataIndex: 'status',
      key: 'status',
      width: 150,
      sorter: (a: any, b: any) => a.status.length - b.status.length,
      render: (value) => (
        <div
          style={{
            display: 'flex',
          }}
        >
          <div>
            {value === 'healthy' && <img src='/src/assets/images/model/healthy.svg' />}
            {value === 'unhealthy' && <img src='/src/assets/images/model/unhealthy.svg' />}
            {value === 'undeployed' && <img src='/src/assets/images/model/undeployed.svg' />}
          </div>
          <div
            style={{
              marginTop: -2,
            }}
          >
            {value}
          </div>
        </div>
      ),
    },
    {
      title: '请求数',
      dataIndex: 'requests',
      key: 'requests',
      sorter: (a: any, b: any) => a.requests - b.requests,
    },
    {
      title: '回答数',
      dataIndex: 'responses',
      key: 'responses',
      sorter: (a: any, b: any) => a.responses - b.responses,
    },
    {
      title: '异常数',
      dataIndex: 'exceptions',
      key: 'exceptions',
      sorter: (a: any, b: any) => a.exceptions - b.exceptions,
    },
    {
      title: '吞吐量',
      dataIndex: 'throughput',
      key: 'throughput',
      sorter: (a: any, b: any) => a.throughput - b.throughput,
    },
    {
      title: '输入token',
      dataIndex: 'total_input_tokens',
      key: 'total_input_tokens',
      sorter: (a: any, b: any) => a.total_input_tokens - b.total_input_tokens,
    },
    {
      title: '输出token',
      dataIndex: 'total_output_tokens',
      key: 'total_output_tokens',
      sorter: (a: any, b: any) => a.total_output_tokens - b.total_output_tokens,
    },
    {
      title: '时延',
      dataIndex: 'latency',
      key: 'latency',
      sorter: (a: any, b: any) => a.latency - b.latency,
      render: (value) => <div>{value}s</div>,
    },
    {
      title: '速度',
      dataIndex: 'speed',
      key: 'speed',
      sorter: (a: any, b: any) => a.speed - b.speed,
      render: (value) => <div>{value} token/s</div>,
    },
    {
      title: '操作',
      dataIndex: 'operator',
      key: 'operator',
      render(value, record) {
        return (
          <Space>
            <Button
              type='link'
              style={{ padding: 0 }}
              onClick={() => modifyModel(record)}
              disabled={record?.status === 'undeployed'}
            >
              修改
            </Button>
            <Button
              type='link'
              style={{ padding: 0 }}
              onClick={() => {
                deleteModel(record.name);
              }}
              disabled={record?.status === 'undeployed'}
            >
              删除
            </Button>
          </Space>
        );
      },
    },
  ];
  return (
    <div
      className='aui-block'
      style={{
        display: 'flex',
        flexDirection: 'column',
        gap: 8,
      }}
    >
      <div>
        <Table
          columns={columns}
          dataSource={modelList}
          size='small'
          pagination={false}
          scroll={{ y: 800 }}
        />
      </div>
      <div />
    </div>
  );
};
export default TableTab;
