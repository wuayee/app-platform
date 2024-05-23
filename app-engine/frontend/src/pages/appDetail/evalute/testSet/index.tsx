import { Button } from 'antd';
import React, { useState } from 'react';
import type { PaginationProps } from 'antd';
import { Table, Space } from 'antd';
import { formatDateTime } from '../../../../shared/utils/function';

const showTotal: PaginationProps['showTotal'] = (total) => `共 ${total} 条`;

const TestSet: React.FC = () => {
  const [open, setOpen] = useState(false);

  const dataSource = Array.from({ length: 30 })
    .fill(null)
    .map((_, index) => ({
      key: index,
      id: index,
      name: `数据集${index}`,
      desc: `描述${index}`,
      creator: 'admin',
      createTime: formatDateTime(new Date()),
      modifyTime: formatDateTime(new Date()),
    }));

  const columns = [
    {
      key: 'id',
      dataIndex: 'id',
      title: 'ID',
    },
    {
      key: 'name',
      dataIndex: 'name',
      title: '测试集名称',
    },
    {
      key: 'desc',
      dataIndex: 'desc',
      title: '测试集描述',
    },
    {
      key: 'creator',
      dataIndex: 'creator',
      title: '创建人',
    },
    {
      key: 'createTime',
      dataIndex: 'createTime',
      title: '创建时间',
    },
    {
      key: 'modifyTime',
      dataIndex: 'modifyTime',
      title: '修改时间',
    },
    {
      key: 'action',
      title: 'Action',
      render: () => (
        <Space size='middle'>
          <a>查看</a>
          <a>删除</a>
        </Space>
      ),
    },
  ];

  const showDrawer = () => {
    setOpen(true);
  };

  return (
    <div className='tab-content'>
      <div className='margin-bottom-standard test'>
        <Button className='margin-right-standard' type='primary' style={{ width: '88px' }}>
          创建
        </Button>
        <Button>应用评估</Button>
      </div>
      <Table
        dataSource={dataSource}
        columns={columns}
        virtual
        scroll={{ y: 800 }}
        pagination={{
          size: 'small',
          total: 50,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal,
        }}
      />
    </div>
  );
};

export default TestSet;
