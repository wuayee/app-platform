import React, { useEffect, useState } from "react";
import { Alert, Button, Drawer, Input, message, Modal, Space, Table } from "antd";
import type { PaginationProps, TableColumnsType } from "antd";
import { getCheckpointList, saveCheckpoints } from "../../shared/http/model-train";

const showTotal: PaginationProps['showTotal'] = (total) => `Total: ${total}`;

const ArchiveCheckpoint = ({ taskId, open, closeCallback }: any) => {

  const [data, setData] = useState([]);
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

  useEffect(() => {
    if (taskId) {
      setSelectedKeys([]);
      getCheckpoint();
    }
  }, [taskId]);

  const getCheckpoint = () => {
    getCheckpointList(taskId).then(res => {
      setData(res?.result.map((item) => ({
        ...item,
        key: item?.checkpointId,
        description: ''
      })));
    })
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
      dataIndex: 'validationLoss'
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
            disabled={!selectedKeys?.some(id => id === record?.checkpointId)}
            onChange={(e) => descriptionChange(e, record)}
          />
        )
      },
    }
  ];

  const descriptionChange = (e, record) => {
    let items = [...data];
    items.forEach(item => {
      if (item.checkpointId === record?.checkpointId) {
        item.description = e.target?.value;
      }
    });
    setData(items);
  }

  const rowSelection = {
    onChange: (selectedKeys: string[]) => {
      setSelectedKeys(selectedKeys);
    }
  }

  const confirmArchive = () => {
    if (selectedKeys?.length === 0) {
      Modal.info({
        title: '请选择Checkpoint',
      })
    } else {
      //TODO归档确认
      Modal.confirm({
        title: '确认',
        content: `已选择 ${selectedKeys.length} 个Checkpoint，确认归档？`,
        onOk: () => {
          const archiveParams = data.filter(item => selectedKeys.some(v => v === item.checkpointId))
            .map(item => ({ checkpointId: item.checkpointId, desc: item.description }));
          saveCheckpoints(taskId, { archiveParams }).then(res => {
            message.success('归档成功');
            closeCallback(true);
          });
        }
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
          }}>共 {data?.length} 个Checkpoint</span>
        <span> 已选择 {selectedKeys?.length} 个</span>
      </div>
      <Table
        rowSelection={{
          type: 'checkbox',
          selectedRowKeys: selectedKeys,
          ...rowSelection,
        }}
        columns={columns}
        dataSource={data}
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
