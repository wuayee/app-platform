import React, { useState, useEffect } from 'react';
import { Button, message, Modal, Table } from 'antd';
import { deleteExternalModel, getExternalModelList } from '../../shared/http/model';
import CreateModel from './components/external-create';
import GlobalConfig from './components/global-config';

const ExternalModel = () => {

  const [total, setTotal] = useState(0);
  const [modelList, setModelList] = useState([]);
  const [openCreate, setOpenCreate] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [deleteName, setDeleteName] = useState('');
  const [openConfig, setOpenConfig] = useState(false);

  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    getList();
  }, []);

  const getList = () => {
    getExternalModelList().then(res => {
      setTotal(res?.services?.length);
      setModelList(res.services);
    })
  }

  const createCallback = (type: string) => {
    setOpenCreate(false);
    if (type === 'submit') {
      getList();
    }
  }

  const configCallback = (type: string) => {
    setOpenConfig(false);
    if (type === 'submit') {
      getList();
    }
  }

  const handleOk = async () => {
    await deleteExternalModel(deleteName);
    messageApi.open({
      type: 'success',
      content: '删除成功',
    });
    setDeleteOpen(false);
    await getList();
    
  }

  const handleCancel = () => {
    setDeleteName('');
    setDeleteOpen(false);
  }

  const columns = [
    {
      key: 'name',
      dataIndex: 'name',
      title: '名字',
      ellipsis: true,
    },
    {
      key: 'url',
      dataIndex: 'url',
      title: 'URL',
      ellipsis: true,
    },
    {
      key: 'api_key',
      dataIndex: 'api_key',
      title: 'API Key',
      ellipsis: true,
    },
    {
      key: 'http_proxy',
      dataIndex: 'http_proxy',
      title: 'HTTP代理',
      ellipsis: true,
    },
    {
      key: 'https_proxy',
      dataIndex: 'https_proxy',
      title: 'HTTPS代理',
      ellipsis: true,
    },
    {
      key: 'action',
      title: '操作',
      render(_: any, record: any) {
        const deleteData = async () => {
          try {
            setDeleteOpen(true);
            setDeleteName(record?.name);
          } catch (error) {

          }
        }
        return (
          <a onClick={deleteData}>删除</a>
        )
      },
    }
  ]

  return (
    <>
      {contextHolder}
      <Button
        type='primary'
        style={{
          background: '#2673E5',
          width: '96px',
          height: '32px',
          fontSize: '14px',
          borderRadius: '4px',
          letterSpacing: '0',
          marginBottom: '16px',
        }}
        onClick={() => {
          setOpenCreate(true);
        }}
      >
        创建
      </Button>
      <Button
        style={{
          width: '96px',
          height: '32px',
          fontSize: '14px',
          borderRadius: '4px',
          letterSpacing: '0',
          marginBottom: '16px',
        }}
        onClick={() => {
          setOpenConfig(true);
        }}
      >
        全局配置
      </Button>
      <Table
        style={{ marginBottom: '16px' }}
        columns={columns}
        dataSource={modelList}
        virtual
        scroll={{ y: 800 }}
        pagination={{
          size: 'small'
        }}
      />
      <CreateModel visible={openCreate} createCallback={createCallback} />
      <Modal title='删除' open={deleteOpen} onOk={handleOk} onCancel={handleCancel}>
        <p>确定要删除该条记录？</p>
      </Modal>
      <GlobalConfig visible={openConfig} configCallback={configCallback} />
    </>
  )
}

export default ExternalModel;
