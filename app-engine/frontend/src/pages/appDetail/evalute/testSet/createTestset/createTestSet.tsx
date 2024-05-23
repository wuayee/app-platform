
import { Button, Form, Input, Radio, Space, Table, Drawer } from 'antd';
import { RadioChangeEvent } from 'antd/lib';
import React, { useEffect, useState } from 'react';
import LiveUpload from '../../../../../components/upload';
import CreateItem from './createItem';
import { CallbackMethod, CreateType } from './model';

interface props {
  visible: boolean;
  createCallback: Function;
}

const CreateSet = ({ visible, createCallback }: props) => {

  const defaultItemData = {
    input: '',
    output: '',
    index: -1
  }

  const [params, setParams] = useState(defaultItemData);
  const [createOpen, setCreateOpen] = useState(false);
  const [type, setType] = useState(CreateType.MANUAL);
  const [manualData, setManualData] = useState<any[]>([]);
  const [uploadData, setUploadData] = useState<any[]>([]);
  const [itemOpen, setItemOpen] = useState(false);

  const [form] = Form.useForm();

  useEffect(() => {
    setCreateOpen(visible);
  })


  const changeType = (e: RadioChangeEvent) => {
    setType(e.target.value);
  }

  const showItemDrawer = () => {
    const tempParams = {
      index: -1,
      input: '',
      output: ''
    }
    setParams(tempParams);
    setItemOpen(true);
  };

  const editItemDrawer = (data: any, index: number) => {
    if (data) {
      const tempParams = {
        input: data?.input,
        output: data?.output,
        index
      }
      setParams(tempParams);
    }
    setItemOpen(true);
  };

  const callback = (operate: string, data: any, index: number) => {
    switch (operate) {
      case CallbackMethod.SUBMIT:
        const updateData = [...manualData];
        if (index >= 0) {
          updateData[index] = data;
        } else {
          updateData.push(data);
        }
        setManualData(updateData);
    }
    setItemOpen(false);
  }

  const closeDrawer = () => {
    createCallback('cancel', {});
  }

  const submit = () => {
    form.submit();
  }

  const onFinish = (value: any) => {
    createCallback('submit', value);
  }

  const columns = [
    {
      key: 'input',
      dataIndex: 'input',
      title: '输入',
      ellipsis: true
    },
    {
      key: 'output',
      dataIndex: 'output',
      title: '输出',
      ellipsis: true
    },
    {
      key: 'action',
      title: '操作',
      render(_: any, record: any, index: any) {
        const edit = () => {
          editItemDrawer(record, index);
        }
        const delItem = () => {
          const updateData = [...manualData];
          updateData.splice(index, 1);
          setManualData(updateData);
        }
        return (
          <Space size='middle'>
            <a onClick={edit}>编辑</a>
            <a onClick={delItem}>删除</a>
          </Space>
        )
      },
      hidden: type !== CreateType.MANUAL,
      width: 120
    },
  ];

  return (
    <Drawer
      title='新建测试集'
      width={800}
      open={createOpen}
      onClose={closeDrawer}
      maskClosable={false}
      destroyOnClose={true}
      footer={
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Space>
            <Button style={{ minWidth: 96 }} onClick={closeDrawer}>取消</Button>
            <Button type='primary' style={{ minWidth: 96 }} onClick={submit}>确定</Button>
          </Space>
        </div>
      }
    >
      <Form form={form} layout='vertical' onFinish={onFinish}>
        <Form.Item label='新建方式' required>
          <Radio.Group value={type} onChange={changeType}>
            <Space size='large'>
              <Radio value='upload'>上传</Radio>
              <Radio value='manual'>手动</Radio>
            </Space>
          </Radio.Group>
        </Form.Item>
        <Form.Item label='测试集名称' name='name' required rules={[{ required: true, message: '输入不能为空' }]}>
          <Input />
        </Form.Item>
        <Form.Item label='测试集描述' name='desc' required rules={[{ required: true, message: '输入不能为空' }]}>
          <Input />
        </Form.Item>
        {(type === CreateType.UPLOAD) ?
          <Form.Item label='上传' required>
            <LiveUpload />
          </Form.Item>
          :
          <Button
            type='primary'
            style={{ minWidth: '96px', margin: '8px 0 16px' }}
            onClick={showItemDrawer}
          >创建</Button>
        }
        <Table
          dataSource={type === CreateType.MANUAL ? manualData : uploadData}
          columns={columns}
          pagination={{
            simple: true,
            size: 'small'
          }}
        />
        <CreateItem params={params} visible={itemOpen} callback={callback} />
      </Form>
    </Drawer>
  )
}

export default CreateSet;
