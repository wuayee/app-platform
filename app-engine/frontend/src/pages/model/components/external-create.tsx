
import { Button, Form, Input, Radio, Space, Drawer, message } from 'antd';
import React, { useEffect, useState } from 'react';
import { createExternalModel } from '../../../shared/http/model';

interface props {
  visible: boolean;
  createCallback: Function;
}

const CreateModel = ({ visible, createCallback }: props) => {

  const [createOpen, setCreateOpen] = useState(false);

  const [form] = Form.useForm();

  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    setCreateOpen(visible);
  })

  const cancel = () => {
    form.resetFields();
    createCallback('cancel');
  }

  const onFinish = (value: any) => {
    console.log(value);
    createExternalModel(value).then(_ => {
      messageApi.open({
        type: 'success',
        content: '创建成功',
      });
      createCallback('submit');
    })
  }

  // 点击确认按钮
  const clickSubmit = async () => {
    form.submit();
  }

  return (
    <>
      {contextHolder}
      <Drawer
        title={'新建'}
        width={500}
        open={createOpen}
        maskClosable={false}
        destroyOnClose={true}
        footer={
          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Space>
              <Button style={{ minWidth: 96 }} onClick={cancel}>取消</Button>
              <Button type='primary' style={{ minWidth: 96 }} onClick={clickSubmit}>确定</Button>
            </Space>
          </div>
        }
      >
        <Form form={form} layout='vertical' onFinish={onFinish}>
          <Form.Item
            label='名称'
            name='name'
            required
            validateFirst
            rules={[
              { required: true, message: '输入不能为空' },
              {
                validator: (_, value) => {
                  if (/^[a-zA-Z0-9](([a-zA-Z0-9-_])*[a-zA-Z0-9])*$/.test(value)) {
                    return Promise.resolve();
                  } else {
                    return Promise.reject('名称以字母和数字开头或结尾，仅可包含字母、数字、“_”和“-”');
                  }
                }
              }
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label='URL'
            name='url'
            required
            validateFirst
            rules={[
              { required: true, message: '输入不能为空' },
              {
                validator: (_, value) => {
                  if (/^https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)$/.test(value)) {
                    return Promise.resolve();
                  } else {
                    return Promise.reject('请输入正确的url地址');
                  }
                }
              }
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item label='API Key' name='api_key' required rules={[{ required: true, message: '输入不能为空' }]}>
            <Input />
          </Form.Item>
        </Form>
      </Drawer>
    </>

  )
}

export default CreateModel;
