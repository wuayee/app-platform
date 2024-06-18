import React, { useEffect, useState } from "react";
import { Alert, Button, Drawer, Input, Modal, Space, Table } from "antd";
import type { PaginationProps, TableColumnsType } from "antd";

const showTotal: PaginationProps['showTotal'] = (total) => `Total: ${total}`;

const ArchiveCheckpoint = ({ taskId, open, closeCallback }: any) => {

  const data = [
    {
      checkpointId: 1,
      curIteration: 100,
      testLoss: 0.01,
      generateTime: '2024-6-21 10:45:20',
      description: '',
      key: 1,
    },
    {
      checkpointId: 2,
      curIteration: 300,
      testLoss: 0.01,
      generateTime: '2024-6-21 10:45:20',
      description: '',
      key: 2,
    }
  ];

  const [selectedCheckpoint, setSelectedCheckpoint] = useState<any[]>([]);
  const [tableItems, setTableItems] = useState(data);

  useEffect(() => {
    getCheckpoint();
  }, [taskId]);

  const getCheckpoint = () => {
    //TODO通过taskId获取checkpoint列表
    console.log('获取checkpoint接口：', taskId);
  }

  const columns: TableColumnsType = [
    {
      title: 'Checkpoint',
      dataIndex: 'checkpointId'
    },
    {
      title: '迭代轮次',
      dataIndex: 'curIteration'
    },
    {
      title: '测试集损失',
      dataIndex: 'testLoss'
    },
    {
      title: '生成时间',
      dataIndex: 'generateTime'
    },
    {
      title: '描述',
      render(_, record) {
        return (
          <Input
            value={record?.description}
            disabled={!selectedCheckpoint?.some(id => id === record?.checkpointId)}
            onChange={(e) => descriptionChange(e, record)}
          />
        )
      },
    }
  ];

  const descriptionChange = (e, record) => {
    let items = [...tableItems];
    items.forEach(item => {
      if (item.checkpointId === record?.checkpointId) {
        item.description = e.target?.value;
      }
    });
    setTableItems(items);
  }

  const rowSelection = {
    onChange: (_, selectedRows) => {
      setSelectedCheckpoint(selectedRows.map((item) => item.checkpointId));
    }
  }

  const confirmArchive = () => {
    if (selectedCheckpoint?.length === 0) {
      Modal.info({
        title: '请选择Checkpoint',
      })
    } else {
      //TODO归档确认
      Modal.confirm({
        title: '确认',
        content: `已选择 ${selectedCheckpoint.length} 个Checkpoint，确认归档？`,
      })
    }
  }

  return (
    <Drawer
      open={open}
      title='Checkpoint归档'
      width={900}
      onClose={closeCallback}
      footer={
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Space>
            <Button style={{ minWidth: 96 }} onClick={closeCallback}>取消</Button>
            <Button type='primary' style={{ minWidth: 96 }} onClick={confirmArchive}>归档</Button>
          </Space>
        </div>
      }
    >
      <Alert
        message="由于权重文件会占用大量存储资源，为了保证存储空间的有效利用，选择归档的Checkpoint会被保存，未选择的会被删除。"
        type="info"
        showIcon
        style={{ marginBottom: 16 }}
      />
      <div style={{ marginBottom: 16 }}>
        <span
          style={{
            fontSize: 14,
            color: '#808080',
            marginRight: 16
          }}>共 {tableItems?.length} 个Checkpoint</span>
        <span> 已选择 {selectedCheckpoint?.length} 个</span>
      </div>
      <Table
        rowSelection={{
          type: 'checkbox',

          ...rowSelection,
        }}
        columns={columns}
        dataSource={tableItems}
        pagination={{
          size: 'small',
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: showTotal,
        }}
      />
    </Drawer>
  )
}

export default ArchiveCheckpoint;
